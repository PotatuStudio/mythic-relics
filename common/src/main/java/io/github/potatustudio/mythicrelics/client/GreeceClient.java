package io.github.potatustudio.mythicrelics.client;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import io.github.potatustudio.mythicrelics.greece.AriadneThreadItem;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
import io.github.potatustudio.mythicrelics.network.HermesJumpPayload;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Cliente de Grecia: el doble salto de las sandalias de Hermes (el movimiento lo decide el cliente, como en vanilla; el
 * servidor solo reinicia la caída y muestra las alas) y el hilo rojo de Ariadna hacia el punto de inicio.
 */
public final class GreeceClient {
	private static final DustParticleOptions THREAD = new DustParticleOptions(0xC8302A, 0.6f);
	private static final double THREAD_LENGTH = 12.0;

	private static boolean jumpWasDown;
	private static boolean doubleJumpUsed;

	private GreeceClient() {
	}

	public static void init() {
		ClientTickEvent.CLIENT_POST.register(GreeceClient::tick);
	}

	private static void tick(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null || client.level == null) {
			return;
		}
		boolean jumpDown = client.options.keyJump.isDown();
		if (player.onGround() || player.isInWater() || player.getAbilities().flying || player.isPassenger()) {
			doubleJumpUsed = false;
		} else if (jumpDown && !jumpWasDown && !doubleJumpUsed
				&& WornRelics.isWearing(player, ModItems.HERMES_SANDALS.get())) {
			Vec3 motion = player.getDeltaMovement();
			player.setDeltaMovement(motion.x, GreeceRelics.HERMES_JUMP, motion.z);
			doubleJumpUsed = true;
			if (NetworkManager.canServerReceive(HermesJumpPayload.TYPE)) {
				NetworkManager.sendToServer(HermesJumpPayload.INSTANCE);
			}
		}
		jumpWasDown = jumpDown;

		if (player.tickCount % 2 == 0) {
			ItemStack thread = player.getMainHandItem().is(ModItems.ARIADNE_THREAD.get()) ? player.getMainHandItem()
					: player.getOffhandItem().is(ModItems.ARIADNE_THREAD.get()) ? player.getOffhandItem() : ItemStack.EMPTY;
			GlobalPos anchor = thread.isEmpty() ? null : AriadneThreadItem.anchor(thread);
			if (anchor != null && anchor.dimension() == client.level.dimension()) {
				drawThread(client, player, Vec3.atCenterOf(anchor.pos()));
			}
		}
	}

	/** Un tramo del hilo, desde el jugador hacia el inicio (como mucho {@link #THREAD_LENGTH} bloques). */
	private static void drawThread(Minecraft client, LocalPlayer player, Vec3 target) {
		Vec3 from = player.position().add(0, 0.9, 0);
		Vec3 delta = target.subtract(from);
		double length = Math.min(delta.length(), THREAD_LENGTH);
		if (length < 0.5) {
			return;
		}
		Vec3 step = delta.normalize().scale(0.5);
		Vec3 p = from;
		for (double d = 0; d < length; d += 0.5) {
			p = p.add(step);
			client.level.addParticle(THREAD, p.x, p.y, p.z, 0, 0, 0);
		}
	}
}
