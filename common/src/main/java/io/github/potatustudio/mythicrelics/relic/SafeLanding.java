package io.github.potatustudio.mythicrelics.relic;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;

/**
 * Aterrizaje seguro: quien usó un salto en el aire de una reliquia (doble salto de Hermes, aleteo o planeo de Ícaro) no
 * recibe daño de caída hasta volver a tocar el suelo, caiga de donde caiga.
 */
public final class SafeLanding {
	private static final Set<UUID> AIRBORNE = new HashSet<>();

	private SafeLanding() {
	}

	public static void init() {
		EntityEvent.LIVING_HURT.register((target, source, amount) ->
				source.is(DamageTypeTags.IS_FALL) && AIRBORNE.contains(target.getUUID()) ? EventResult.interruptFalse() : EventResult.pass());
		TickEvent.SERVER_POST.register(SafeLanding::tick);
		ServerState.onQuit(p -> AIRBORNE.remove(p.getUUID()));
		ServerState.clearOnStop(AIRBORNE);
	}

	/** Marca al jugador hasta que aterrice. */
	public static void mark(ServerPlayer player) {
		player.resetFallDistance();
		AIRBORNE.add(player.getUUID());
	}

	/** El daño de caída se calcula al moverse (antes de este tick): aquí ya aterrizó y se olvida la marca. */
	private static void tick(MinecraftServer server) {
		if (AIRBORNE.isEmpty()) {
			return;
		}
		AIRBORNE.removeIf(id -> {
			ServerPlayer p = server.getPlayerList().getPlayer(id);
			return p == null || p.onGround() || p.isInWater() || p.onClimbable();
		});
	}
}
