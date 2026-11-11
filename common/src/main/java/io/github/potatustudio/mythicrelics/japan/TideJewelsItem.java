package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Joyas de la marea de Ryūjin. Kanju (clic derecho) abre un pasillo seco frente a ti: el agua se aparta durante un rato
 * y luego vuelve sola. Manju (agachado y clic derecho) levanta una ola que arrastra a los mobs y apaga el fuego.
 */
public class TideJewelsItem extends RelicItem {
	private static final double WAVE_COS = Math.cos(Math.toRadians(45));

	public TideJewelsItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		player.getCooldowns().addCooldown(stack, JapanRelics.TIDE_COOLDOWN);
		if (player.isShiftKeyDown()) {
			wave(server, player);
		} else {
			ebb(server, player);
		}
		return InteractionResult.SUCCESS;
	}

	/** Kanju: pasillo seco de 3 de ancho, desde tus pies hasta 12 bloques adelante, de 3 abajo a 4 arriba. */
	private static void ebb(ServerLevel level, Player player) {
		Direction facing = player.getDirection();
		Direction side = facing.getClockWise();
		BlockPos base = player.blockPosition();
		int half = JapanRelics.TIDE_WIDTH / 2;
		BlockPos a = base.relative(side, -half).below(3);
		BlockPos b = base.relative(facing, JapanRelics.TIDE_LENGTH).relative(side, half).above(4);
		JapanRelics.openCorridor(level, a, b);
		for (int i = 1; i <= JapanRelics.TIDE_LENGTH; i++) {
			BlockPos p = base.relative(facing, i);
			level.sendParticles(ParticleTypes.SPLASH, p.getX() + 0.5, p.getY() + 1, p.getZ() + 0.5, 10, 1.0, 0.8, 1.0, 0.1);
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.PLAYERS, 1.2f, 0.8f);
	}

	/** Manju: una ola en cono frente a ti que arrastra a los mobs y apaga el fuego. */
	private static void wave(ServerLevel level, Player player) {
		Vec3 look = JapanRelics.flat(player.getLookAngle());
		int r = JapanRelics.TIDE_WAVE_RANGE;
		for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(r, 3, r),
				e -> e != player && e.isAlive() && !ChinaRelics.isFriend(player, e))) {
			Vec3 to = target.position().subtract(player.position());
			if (to.horizontalDistance() > r || JapanRelics.flat(to).dot(look) < WAVE_COS) {
				continue;
			}
			target.push(look.x * 1.8, 0.4, look.z * 1.8);
			target.needsSync = true;
			target.clearFire();
		}
		player.clearFire();
		for (int i = 1; i <= r; i++) {
			Vec3 p = player.position().add(look.scale(i));
			level.sendParticles(ParticleTypes.SPLASH, p.x, p.y + 0.5, p.z, 25, 1.2, 0.5, 1.2, 0.2);
			level.sendParticles(ParticleTypes.BUBBLE, p.x, p.y + 0.8, p.z, 8, 1.0, 0.5, 1.0, 0.05);
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.PLAYERS, 1.5f, 0.7f);
	}
}
