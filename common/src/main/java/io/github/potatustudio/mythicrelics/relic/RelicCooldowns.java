package io.github.potatustudio.mythicrelics.relic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.PlayerEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

/**
 * Recargas de las reliquias, guardadas con el mundo por jugador. Así no se reinician al salir y volver a entrar (la
 * recarga de ítems de vanilla no se guarda) ni al reiniciar el servidor. Se miden con el tiempo de juego del Overworld.
 *
 * <p>Las que tienen un grupo de recarga de ítem además se muestran en el ícono, y al volver a entrar se restaura lo que
 * quedaba.
 */
public final class RelicCooldowns extends SavedData {
	/** Una recarga en curso: hasta qué tick del juego dura, y el grupo de ítem que la muestra (si hay). */
	private record Entry(UUID player, String key, long until, Identifier display) {
		static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
				UUIDUtil.CODEC.fieldOf("player").forGetter(Entry::player),
				Codec.STRING.fieldOf("key").forGetter(Entry::key),
				Codec.LONG.fieldOf("until").forGetter(Entry::until),
				Identifier.CODEC.optionalFieldOf("display", MythicRelics.id("none")).forGetter(Entry::display)
		).apply(i, Entry::new));
	}

	private static final Identifier NO_DISPLAY = MythicRelics.id("none");
	private static final Codec<RelicCooldowns> CODEC = Entry.CODEC.listOf().xmap(RelicCooldowns::new, RelicCooldowns::entries);
	// Los datos son propios del mod: el tipo de corrección de vanilla no los toca (misma versión, sin reglas para ellos).
	private static final SavedDataType<RelicCooldowns> TYPE = new SavedDataType<>(MythicRelics.id("cooldowns"), RelicCooldowns::new,
			CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

	private final Map<UUID, Map<String, Entry>> byPlayer = new HashMap<>();

	private RelicCooldowns() {
	}

	private RelicCooldowns(List<Entry> list) {
		for (Entry e : list) {
			byPlayer.computeIfAbsent(e.player(), k -> new HashMap<>()).put(e.key(), e);
		}
	}

	private List<Entry> entries() {
		List<Entry> out = new ArrayList<>();
		byPlayer.values().forEach(m -> out.addAll(m.values()));
		return out;
	}

	public static void init() {
		// Al entrar, el ícono vuelve a mostrar la recarga que quedaba.
		PlayerEvent.PLAYER_JOIN.register(player -> {
			long now = now(player.level().getServer());
			Map<String, Entry> mine = get(player.level().getServer()).byPlayer.get(player.getUUID());
			if (mine == null) {
				return;
			}
			for (Entry e : mine.values()) {
				if (e.until() > now && !e.display().equals(NO_DISPLAY)) {
					player.getCooldowns().addCooldown(e.display(), (int) Math.min(Integer.MAX_VALUE, e.until() - now));
				}
			}
		});
	}

	private static RelicCooldowns get(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(TYPE);
	}

	private static long now(MinecraftServer server) {
		return server.overworld().getGameTime();
	}

	/** ¿Se puede usar ya? */
	public static boolean ready(ServerPlayer player, String key) {
		MinecraftServer server = player.level().getServer();
		Map<String, Entry> mine = get(server).byPlayer.get(player.getUUID());
		Entry e = mine == null ? null : mine.get(key);
		return e == null || e.until() <= now(server);
	}

	/** Empieza una recarga; si {@code display} no es nulo, también se muestra en el ícono de ese grupo de ítem. */
	public static void start(ServerPlayer player, String key, int ticks, @Nullable Identifier display) {
		MinecraftServer server = player.level().getServer();
		RelicCooldowns data = get(server);
		long now = now(server);
		Map<String, Entry> mine = data.byPlayer.computeIfAbsent(player.getUUID(), k -> new HashMap<>());
		mine.values().removeIf(e -> e.until() <= now);
		mine.put(key, new Entry(player.getUUID(), key, now + ticks, display == null ? NO_DISPLAY : display));
		data.setDirty();
		if (display != null) {
			player.getCooldowns().addCooldown(display, ticks);
		}
	}

	/** Si no está en recarga, la empieza y devuelve true; si lo está, devuelve false. */
	public static boolean tryStart(ServerPlayer player, String key, int ticks, @Nullable Identifier display) {
		if (!ready(player, key)) {
			return false;
		}
		start(player, key, ticks, display);
		return true;
	}
}
