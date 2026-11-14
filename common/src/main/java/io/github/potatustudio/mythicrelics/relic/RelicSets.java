package io.github.potatustudio.mythicrelics.relic;

import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.china.ChinaGods;
import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
import io.github.potatustudio.mythicrelics.japan.JapanGods;
import io.github.potatustudio.mythicrelics.japan.JapanRelics;
import io.github.potatustudio.mythicrelics.norse.NorseRelics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

/**
 * Los sets de reliquias, con qué piezas llevan y cuándo están completos. Avisan al jugador al completarse (mensaje,
 * sonido y destello) y al romperse; el tooltip de cada pieza dice si el set está activo ({@code client/RelicTooltips}).
 */
public enum RelicSets {
	ANUBIS("anubis_set", EgyptRelics::hasAnubisSet, "jackal_mask", "jackal_ring", "anubis_scepter"),
	OSIRIS("osiris_set", EgyptGods::hasOsirisSet, "osiris_crook", "osiris_flail"),
	PERSEUS("perseus_set", GreeceRelics::hasPerseusSet, "hermes_sandals", "hades_helm", "athena_aegis", "harpe"),
	THOR("thor_set", NorseRelics::hasThorSet, "mjolnir", "jarngreipr", "megingjord"),
	ERLANG("erlang_set", ChinaGods::hasErlangSet, "erlang_eye", "erlang_halberd"),
	REGALIA("regalia_set", JapanRelics::hasRegalia, "kusanagi", "yata_mirror", "yasakani_magatama"),
	FUJIN_RAIJIN("fujin_raijin_set", JapanGods::hasWindAndThunder, "fujin_wind_bag", "raijin_drum");

	private final String key;
	private final Predicate<Player> complete;
	private final List<String> pieces;

	RelicSets(String key, Predicate<Player> complete, String... pieces) {
		this.key = key;
		this.complete = complete;
		this.pieces = List.of(pieces);
	}

	public String key() {
		return key;
	}

	public List<String> pieces() {
		return pieces;
	}

	public boolean isComplete(Player player) {
		return complete.test(player);
	}

	public Component displayName() {
		return Component.translatable("set.mythicrelics." + key);
	}

	/** Set al que pertenece una reliquia (por su id), o null. */
	public static @Nullable RelicSets of(String id) {
		for (RelicSets set : values()) {
			if (set.pieces.contains(id)) {
				return set;
			}
		}
		return null;
	}

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
					player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE,
							SoundSource.PLAYERS, 0.6f, 1.6f);
					player.level().sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1, player.getZ(),
							25, 0.4, 0.6, 0.4, 0.15);
				} else if ((now & bit) == 0 && (before & bit) != 0) {
					player.sendOverlayMessage(Component.translatable("message.mythicrelics.set_off", set.displayName())
							.withStyle(ChatFormatting.GRAY));
					player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_DEACTIVATE,
							SoundSource.PLAYERS, 0.4f, 1.6f);
				}
			}
		}
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		io.github.potatustudio.mythicrelics.relic.ServerState.perPlayer(ACTIVE);
	}
}
