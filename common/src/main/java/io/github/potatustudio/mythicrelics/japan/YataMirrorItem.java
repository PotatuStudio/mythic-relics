package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Yata no Kagami, el espejo que devolvió la luz al mundo cuando Amaterasu se vio reflejada. En la mano secundaria
 * devuelve los proyectiles ({@link JapanRelics#modifyIncomingDamage}); con clic derecho, un destello de sol ciega a los
 * hostiles cercanos.
 */
public class YataMirrorItem extends RelicItem {
	public YataMirrorItem(Properties properties) {
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
		RelicCooldowns.start(player, stack, JapanRelics.MIRROR_COOLDOWN);
		int r = JapanRelics.MIRROR_FLASH_RADIUS;
		for (LivingEntity foe : server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(r),
				e -> e instanceof Enemy && e.isAlive() && e.distanceToSqr(player) <= r * r)) {
			foe.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, JapanRelics.MIRROR_BLIND_TICKS, 0));
			foe.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, JapanRelics.MIRROR_BLIND_TICKS, 1));
		}
		server.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.4, player.getZ(), 40, 0.5, 0.5, 0.5, 0.25);
		RelicFx.sound(server, player, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5f, 1.6f);
		return InteractionResult.SUCCESS;
	}
}
