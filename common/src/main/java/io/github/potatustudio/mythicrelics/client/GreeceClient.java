package io.github.potatustudio.mythicrelics.client;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import io.github.potatustudio.mythicrelics.greece.AriadneThreadItem;
import io.github.potatustudio.mythicrelics.greece.GreeceHeroes;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
import io.github.potatustudio.mythicrelics.network.HermesJumpPayload;
import io.github.potatustudio.mythicrelics.norse.NorseGods;
import io.github.potatustudio.mythicrelics.network.IcarusPayload;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Cliente de Grecia: el doble salto de las sandalias de Hermes y el planeo y los aleteos de las alas de Ícaro (el
 * movimiento lo decide el cliente, como en vanilla; el servidor solo reinicia la caída y muestra las alas) y el hilo rojo
 * de Ariadna hacia el punto de inicio.
 */
public final class GreeceClient {
	private static final DustParticleOptions THREAD = new DustParticleOptions(0xC8302A, 0.6f);
	private static final double THREAD_LENGTH = 12.0;

	private static boolean jumpWasDown;
	private static boolean doubleJumpUsed;
	/** Se soltó el salto ya en el aire: el doble salto pide una segunda pulsación, no mantener la del primer salto. */
	private static boolean releasedInAir;
	private static int icarusFlaps;
	private static int glideTicks;

	/** Caída máxima al planear (bloques por tick) y velocidad hacia donde miras. */
	private static final double GLIDE_FALL = -0.12;
	private static final double GLIDE_SPEED = 0.55;

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
		boolean pressed = jumpDown && !jumpWasDown;
		boolean grounded = player.onGround() || player.isInWater() || player.getAbilities().flying || player.isPassenger();
		if (grounded) {
			doubleJumpUsed = false;
			releasedInAir = false;
			icarusFlaps = GreeceHeroes.ICARUS_FLAPS;
		} else if (!jumpDown) {
			releasedInAir = true;
		} else if (pressed && releasedInAir && !doubleJumpUsed && (WornRelics.isWearing(player, ModItems.HERMES_SANDALS.get())
				|| WornRelics.isWearing(player, ModItems.TENGU_MASK.get()))) {
			Vec3 motion = player.getDeltaMovement();
			player.setDeltaMovement(motion.x, GreeceRelics.HERMES_JUMP, motion.z);
			doubleJumpUsed = true;
			pressed = false;
			if (NetworkManager.canServerReceive(HermesJumpPayload.TYPE)) {
				NetworkManager.sendToServer(HermesJumpPayload.INSTANCE);
			}
		}
		if (!grounded && !player.isFallFlying() && WornRelics.isWearing(player, ModItems.ICARUS_WINGS.get())
				&& !GreeceHeroes.waxMelted(player)) {
			icarus(player, pressed, jumpDown);
		}
		jumpWasDown = jumpDown;
		skidbladnirWind(player);

		if (player.tickCount % 2 == 0) {
			ItemStack thread = player.getMainHandItem().is(ModItems.ARIADNE_THREAD.get()) ? player.getMainHandItem()
					: player.getOffhandItem().is(ModItems.ARIADNE_THREAD.get()) ? player.getOffhandItem() : ItemStack.EMPTY;
			GlobalPos anchor = thread.isEmpty() ? null : AriadneThreadItem.anchor(thread);
			if (anchor != null && anchor.dimension() == client.level.dimension()) {
				drawThread(client, player, Vec3.atCenterOf(anchor.pos()));
			}
		}
	}

	/** Skíðblaðnir: con el barco plegable encima, el bote que conduces acelera hasta un 60 % más (siempre viento a favor). */
	private static void skidbladnirWind(LocalPlayer player) {
		if (!NorseGods.favorableWind(player) || player.getVehicle() == null || !player.getVehicle().isInWater()) {
			return;
		}
		var boat = player.getVehicle();
		Vec3 motion = boat.getDeltaMovement();
		double speed = Math.hypot(motion.x, motion.z);
		if (speed > 0.05 && speed < WIND_MAX_SPEED) {
			boat.setDeltaMovement(motion.x * 1.03, motion.y, motion.z * 1.03);
		}
	}

	/** Velocidad máxima de un bote con viento a favor (la del bote vanilla en agua es unos 0,4 bloques por tick). */
	private static final double WIND_MAX_SPEED = 0.4 * (1.0 + NorseGods.SKIDBLADNIR_BOOST);

	/** Alas de Ícaro: un aleteo por pulsación (hasta {@link GreeceHeroes#ICARUS_FLAPS}) o planear con salto mantenido. */
	private static void icarus(LocalPlayer player, boolean pressed, boolean jumpDown) {
		Vec3 motion = player.getDeltaMovement();
		if (pressed && icarusFlaps > 0) {
			icarusFlaps--;
			player.setDeltaMovement(motion.x, GreeceHeroes.ICARUS_FLAP, motion.z);
			send(true);
			return;
		}
		if (!jumpDown || motion.y >= 0) {
			glideTicks = 0;
			return;
		}
		Vec3 look = player.getLookAngle().multiply(1, 0, 1);
		Vec3 target = look.lengthSqr() < 1.0E-4 ? Vec3.ZERO : look.normalize().scale(GLIDE_SPEED);
		player.setDeltaMovement(motion.x + (target.x - motion.x) * 0.1, Math.max(motion.y, GLIDE_FALL),
				motion.z + (target.z - motion.z) * 0.1);
		if (glideTicks++ % 5 == 0) {
			send(false);
		}
	}

	private static void send(boolean flap) {
		if (NetworkManager.canServerReceive(IcarusPayload.TYPE)) {
			NetworkManager.sendToServer(new IcarusPayload(flap));
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
