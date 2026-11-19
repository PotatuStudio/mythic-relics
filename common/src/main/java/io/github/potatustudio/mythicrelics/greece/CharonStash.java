package io.github.potatustudio.mythicrelics.greece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.ServerState;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Prediction;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Óbolo de Caronte: si mueres con él en el inventario, pagas el pasaje. Antes de que se suelten las cosas, el
 * inventario (menos un óbolo) y la experiencia se guardan aquí y se devuelven al reaparecer. Se guarda con el mundo,
 * así que no se pierde si te desconectas en la pantalla de muerte o se reinicia el servidor.
 *
 * <p>Los accesorios puestos (Trinkets/Curios) también se quedan: cada loader le pregunta a {@link #paidPassage} al morir.
 */
public final class CharonStash extends SavedData {
	private record Slot(int index, ItemStack stack) {
		static final Codec<Slot> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.INT.fieldOf("slot").forGetter(Slot::index),
				ItemStack.CODEC.fieldOf("item").forGetter(Slot::stack)
		).apply(i, Slot::new));
	}

	private record Entry(UUID player, List<Slot> slots, int level, float progress, int total) {
		static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
				UUIDUtil.CODEC.fieldOf("player").forGetter(Entry::player),
				Slot.CODEC.listOf().fieldOf("slots").forGetter(Entry::slots),
				Codec.INT.fieldOf("level").forGetter(Entry::level),
				Codec.FLOAT.fieldOf("progress").forGetter(Entry::progress),
				Codec.INT.fieldOf("total").forGetter(Entry::total)
		).apply(i, Entry::new));
	}

	private static final Codec<CharonStash> CODEC = Entry.CODEC.listOf().xmap(CharonStash::new, s -> new ArrayList<>(s.stash.values()));
	private static final SavedDataType<CharonStash> TYPE = new SavedDataType<>(MythicRelics.id("charon_stash"), CharonStash::new, CODEC,
			DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

	private final Map<UUID, Entry> stash = new HashMap<>();
	/** Jugadores que pagaron el pasaje en esta muerte: sus accesorios no se sueltan. */
	private static final Set<UUID> PAID = new HashSet<>();

	private CharonStash() {
	}

	private CharonStash(List<Entry> entries) {
		entries.forEach(e -> stash.put(e.player(), e));
	}

	private static CharonStash get(ServerPlayer player) {
		return player.level().getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
	}

	static void init() {
		EntityEvent.LIVING_DEATH.register((victim, source) -> {
			if (victim instanceof ServerPlayer player) {
				payPassage(player);
			}
			return EventResult.pass();
		});
		PlayerEvent.PLAYER_RESPAWN.register((player, conqueredEnd, reason) -> restore(player));
		// Por si quedó algo guardado y el jugador ya está vivo al entrar (p. ej. el servidor se cerró justo al reaparecer).
		PlayerEvent.PLAYER_JOIN.register(player -> {
			if (player.isAlive()) {
				restore(player);
			}
		});
	}

	private static void payPassage(ServerPlayer player) {
		if (player.level().getGameRules().get(GameRules.KEEP_INVENTORY) || player.isSpectator()) {
			return;
		}
		Inventory inventory = player.getInventory();
		int obol = -1;
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (inventory.getItem(i).is(ModItems.CHARON_OBOL.get())) {
				obol = i;
				break;
			}
		}
		if (obol < 0) {
			return;
		}
		inventory.getItem(obol).shrink(1);
		PAID.add(player.getUUID());
		List<Slot> slots = new ArrayList<>();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (!stack.isEmpty()) {
				slots.add(new Slot(i, stack.copy()));
				inventory.setItem(i, ItemStack.EMPTY);
			}
		}
		CharonStash data = get(player);
		data.stash.put(player.getUUID(), new Entry(player.getUUID(), slots, player.experienceLevel, player.experienceProgress,
				player.totalExperience));
		data.setDirty();
		// Sin experiencia que soltar: se devuelve entera al reaparecer.
		player.experienceLevel = 0;
		player.experienceProgress = 0;
		player.totalExperience = 0;
		player.level().sendParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1, player.getZ(), 20, 0.4, 0.6, 0.4, 0.02);
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 1.0f, 0.6f);
	}

	/** ¿Pagó el pasaje al morir? Entonces los accesorios puestos se quedan puestos. */
	public static boolean paidPassage(LivingEntity entity) {
		return PAID.contains(entity.getUUID());
	}

	private static void restore(ServerPlayer player) {
		PAID.remove(player.getUUID());
		CharonStash data = get(player);
		Entry entry = data.stash.remove(player.getUUID());
		if (entry == null) {
			return;
		}
		data.setDirty();
		Inventory inventory = player.getInventory();
		for (Slot slot : entry.slots()) {
			if (slot.index() < inventory.getContainerSize() && inventory.getItem(slot.index()).isEmpty()) {
				inventory.setItem(slot.index(), slot.stack());
			} else if (!inventory.add(slot.stack())) {
				player.drop(slot.stack(), false, Prediction.SERVER_ONLY);
			}
		}
		// Nivel -1: entrada antigua que no guardó la experiencia.
		if (entry.level() < 0) {
			return;
		}
		player.experienceLevel = entry.level();
		player.experienceProgress = entry.progress();
		player.totalExperience = entry.total();
		RelicFx.notify(player, "charon_paid", ChatFormatting.AQUA);
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		ServerState.clearOnStop(PAID);
	}
}
