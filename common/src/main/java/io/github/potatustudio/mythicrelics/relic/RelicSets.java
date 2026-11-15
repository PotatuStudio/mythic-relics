package io.github.potatustudio.mythicrelics.relic;

import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

/**
 * Los sets de reliquias: qué piezas llevan, dónde cuenta cada una y cuándo están completos. La misma regla sirve para el
 * bono y para el tooltip de cada pieza ({@code client/RelicTooltips}). Avisan al jugador al completarse (mensaje, sonido
 * y destello) y al romperse.
 */
public enum RelicSets {
	ANUBIS("anubis_set", worn("jackal_mask"), worn("jackal_ring"), inventory("anubis_scepter")),
	OSIRIS("osiris_set", hand("osiris_crook"), hand("osiris_flail")),
	PERSEUS("perseus_set", GreeceRelics.PERSEUS_SET_PIECES, worn("hermes_sandals"), worn("hades_helm"), inventory("athena_aegis"), inventory("harpe")),
	THOR("thor_set", bearer("mjolnir"), worn("jarngreipr"), worn("megingjord"));

	/** Dónde tiene que estar una pieza para contar. */
	public enum Where {
		/** Puesta en una casilla de accesorio (Trinkets/Curios). */
		WORN,
		/** En cualquier parte del inventario. */
		INVENTORY,
		/** En la mano principal o en la otra. */
		HAND,
		/** En la mano principal. */
		MAIN_HAND;

		/** Clave del texto que le dice al jugador dónde ponerla («póntela», «en una mano»…). */
		public String hintKey() {
			return "tooltip.mythicrelics.set_where." + name().toLowerCase(Locale.ROOT);
		}
	}

	/** Cómo está una pieza para el jugador: donde cuenta, la lleva pero en otro lugar, o le falta. */
	public enum State {
		PLACED, ELSEWHERE, MISSING
	}

	/** Una pieza del set. Las que no son {@code required} reciben el bono pero no hacen falta para activarlo. */
	public static final class Piece {
		private final String id;
		private final Where where;
		private final boolean required;
		private @Nullable Item item;

		private Piece(String id, Where where, boolean required) {
			this.id = id;
			this.where = where;
			this.required = required;
		}

		public String id() {
			return id;
		}

		public Where where() {
			return where;
		}

		public Item item() {
			if (item == null) {
				item = BuiltInRegistries.ITEM.getValue(MythicRelics.id(id));
			}
			return item;
		}

		/** Si la pieza está donde cuenta. Un mob no tiene inventario: para él cuenta lo que lleva en las manos. */
		public boolean placed(LivingEntity entity) {
			Item it = item();
			return switch (where) {
				case WORN -> WornRelics.isWearing(entity, it);
				case INVENTORY -> entity instanceof Player player ? player.getInventory().contains(s -> s.is(it))
						: entity.getMainHandItem().is(it);
				case HAND -> entity.getMainHandItem().is(it) || entity.getOffhandItem().is(it);
				case MAIN_HAND -> entity.getMainHandItem().is(it);
			};
		}

		public State state(Player player) {
			if (placed(player)) {
				return State.PLACED;
			}
			Item it = item();
			return player.getInventory().contains(s -> s.is(it)) || WornRelics.isWearing(player, it) ? State.ELSEWHERE : State.MISSING;
		}
	}

	private static Piece worn(String id) {
		return new Piece(id, Where.WORN, true);
	}

	private static Piece inventory(String id) {
		return new Piece(id, Where.INVENTORY, true);
	}

	private static Piece hand(String id) {
		return new Piece(id, Where.HAND, true);
	}

	private static Piece mainHand(String id) {
		return new Piece(id, Where.MAIN_HAND, true);
	}

	/** La reliquia que recibe el bono (el martillo de Thor): cuenta en el inventario, pero el set se activa sin ella. */
	private static Piece bearer(String id) {
		return new Piece(id, Where.INVENTORY, false);
	}

	private final String key;
	private final int needed;
	private final List<Piece> pieces;

	/** Set que se activa con todas sus piezas obligatorias. */
	RelicSets(String key, Piece... pieces) {
		this(key, -1, pieces);
	}

	/** Set que se activa con {@code needed} de sus piezas obligatorias (-1: todas). */
	RelicSets(String key, int needed, Piece... pieces) {
		this.key = key;
		this.pieces = List.of(pieces);
		this.needed = needed < 0 ? (int) this.pieces.stream().filter(p -> p.required).count() : needed;
	}

	public String key() {
		return key;
	}

	public List<Piece> pieces() {
		return pieces;
	}

	/** Activo: al menos {@code needed} de las piezas obligatorias están donde cuentan. */
	public boolean isComplete(Entity entity) {
		if (!(entity instanceof LivingEntity living)) {
			return false;
		}
		int have = 0;
		for (Piece p : pieces) {
			if (p.required && p.placed(living) && ++have >= needed) {
				return true;
			}
		}
		return false;
	}

	public Component displayName() {
		return Component.translatable("set.mythicrelics." + key);
	}

	/** Números del texto del bono ({@code set.mythicrelics.<set>.bonus.N}). */
	public Object[] bonusArgs() {
		return switch (this) {
			case OSIRIS -> new Object[] {EgyptGods.OSIRIS_REBIRTH_COOLDOWN / 20 / 60};
			default -> new Object[0];
		};
	}

	/** Set al que pertenece una reliquia (por su id), o null. */
	public static @Nullable RelicSets of(String id) {
		for (RelicSets set : values()) {
			for (Piece p : set.pieces) {
				if (p.id.equals(id)) {
					return set;
				}
			}
		}
		return null;
	}

	/** Daño extra del arma de un set completo (cetro, mayal, harpe, Mjölnir y su rayo). */
	public static final float WEAPON_BONUS = 0.3f;

	/** Sets activos de cada jugador en la última revisión (bit por set). */
	private static final Map<UUID, Integer> ACTIVE = new HashMap<>();

	public static void init() {
		TickEvent.SERVER_POST.register(RelicSets::tick);
	}

	private static void tick(MinecraftServer server) {
		if (server.getTickCount() % 10 != 0) {
			return;
		}
		ACTIVE.keySet().removeIf(id -> server.getPlayerList().getPlayer(id) == null);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			int now = 0;
			for (RelicSets set : values()) {
				if (set.isComplete(player)) {
					now |= 1 << set.ordinal();
				}
			}
			Integer before = ACTIVE.put(player.getUUID(), now);
			if (before == null || before == now) {
				continue;
			}
			for (RelicSets set : values()) {
				int bit = 1 << set.ordinal();
				if ((now & bit) != 0 && (before & bit) == 0) {
					player.sendOverlayMessage(Component.translatable("message.mythicrelics.set_on", set.displayName())
							.withStyle(ChatFormatting.GOLD));
					RelicFx.sound(player.level(), player, SoundEvents.BEACON_ACTIVATE, 0.6f, 1.6f);
					player.level().sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1, player.getZ(),
							25, 0.4, 0.6, 0.4, 0.15);
				} else if ((now & bit) == 0 && (before & bit) != 0) {
					player.sendOverlayMessage(Component.translatable("message.mythicrelics.set_off", set.displayName())
							.withStyle(ChatFormatting.GRAY));
					RelicFx.sound(player.level(), player, SoundEvents.BEACON_DEACTIVATE, 0.4f, 1.6f);
				}
			}
		}
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		ServerState.perPlayer(ACTIVE);
	}
}
