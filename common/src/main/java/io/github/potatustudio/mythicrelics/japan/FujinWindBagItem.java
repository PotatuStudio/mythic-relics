package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.Allies;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Bolsa de vientos de Fūjin: clic derecho suelta una corriente que te eleva y luego planeas; agachado, un ventarrón
 * empuja todo lo que tienes alrededor. En el inventario, junto al tambor de Raijin puesto, forma el set de Fūjin y Raijin.
 */
public class FujinWindBagItem extends RelicItem {
	public FujinWindBagItem(Properties properties) {
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
		player.getCooldowns().addCooldown(stack, JapanGods.WIND_COOLDOWN);
		if (player.isShiftKeyDown()) {
			int r = JapanGods.WIND_GALE_RADIUS;
			for (LivingEntity e : Allies.enemiesAround(player, player.getBoundingBox().inflate(r), e -> e.distanceToSqr(player) <= r * r)) {
				Vec3 d = e.position().subtract(player.position());
				Vec3 away = new Vec3(d.x, 0, d.z).lengthSqr() < 1.0E-4 ? new Vec3(1, 0, 0) : new Vec3(d.x, 0, d.z).normalize();
				e.push(away.x * 2.0, 0.5, away.z * 2.0);
				e.needsSync = true;
			}
			server.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, player.getX(), player.getY() + 1, player.getZ(), 1, 0, 0, 0, 0);
			server.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.5, player.getZ(), 40, 3.0, 0.5, 3.0, 0.15);
		} else {
			Vec3 v = player.getDeltaMovement();
			if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
				io.github.potatustudio.mythicrelics.relic.RelicMotion.launch(sp, new Vec3(v.x, JapanGods.WIND_LIFT, v.z));
			}
			player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, JapanGods.WIND_GLIDE_TICKS + 40, 0, true, false));
			server.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 30, 0.6, 0.2, 0.6, 0.08);
			server.sendParticles(ParticleTypes.GUST, player.getX(), player.getY() + 0.5, player.getZ(), 2, 0.3, 0.3, 0.3, 0);
		}
		RelicFx.sound(server, player, SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), 1.2f, 0.7f);
		return InteractionResult.SUCCESS;
	}
}
