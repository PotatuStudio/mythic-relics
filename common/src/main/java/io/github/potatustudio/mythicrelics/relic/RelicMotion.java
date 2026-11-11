package io.github.potatustudio.mythicrelics.relic;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Impulsos al jugador desde el servidor (dash del abanico, corriente de Fūjin). El movimiento del jugador lo manda su
 * cliente: si el servidor solo cambia la velocidad, el cliente no se entera; hay que enviársela.
 */
public final class RelicMotion {
	private RelicMotion() {
	}

	public static void launch(ServerPlayer player, Vec3 velocity) {
		player.setDeltaMovement(velocity);
		player.resetFallDistance();
		player.connection.send(new ClientboundSetEntityMotionPacket(player));
	}
}
