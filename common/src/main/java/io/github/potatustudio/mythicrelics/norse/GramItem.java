package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.Allies;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Gram, la espada de Sigurd. Además del golpe, se carga (mantener clic derecho 1 s y soltar) para la estocada «desde el
 * foso» con la que Sigurd mató a Fáfnir: mucho daño al que apuntas, el doble contra los mobs grandes. Con la estocada
 * lista, el mob grande al que apuntas brilla y suena un aviso ({@link NorseSagas}).
 */
public class GramItem extends RelicItem {
	public GramItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.SPEAR;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (!(entity instanceof Player player) || getUseDuration(stack, entity) - timeLeft < NorseSagas.GRAM_CHARGE_TICKS) {
			return false;
		}
		if (!(level instanceof ServerLevel server)) {
			return true;
		}
		LivingEntity target = aimed(player, NorseSagas.GRAM_REACH);
		Vec3 look = player.getLookAngle();
		Vec3 tip = player.getEyePosition().add(look.scale(2.0));
		server.sendParticles(ParticleTypes.SWEEP_ATTACK, tip.x, tip.y, tip.z, 1, 0.0, 0.0, 0.0, 0.0);
		RelicFx.sound(server, player, SoundEvents.TRIDENT_THROW.value(), 1.0f, 0.6f);
		if (target != null) {
			float damage = NorseSagas.GRAM_THRUST_DAMAGE * (target.getMaxHealth() >= NorseSagas.GRAM_BIG_HEALTH ? 2.0f : 1.0f);
			target.hurtServer(server, server.damageSources().playerAttack(player), damage);
			target.push(look.x * 0.6, 0.25, look.z * 0.6);
			server.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.5), target.getZ(), 25, 0.3, 0.4, 0.3, 0.4);
			server.sendParticles(ParticleTypes.ENCHANTED_HIT, target.getX(), target.getY(0.5), target.getZ(), 15, 0.3, 0.4, 0.3, 0.3);
			RelicFx.sound(server, target, SoundEvents.ANVIL_LAND, 0.7f, 1.4f);
		}
		player.getCooldowns().addCooldown(stack, NorseSagas.GRAM_COOLDOWN);
		return true;
	}

	/** El ser vivo en la línea de la mirada, hasta {@code reach} bloques. */
	static @Nullable LivingEntity aimed(Player player, double reach) {
		Vec3 eyes = player.getEyePosition();
		Vec3 end = eyes.add(player.getLookAngle().scale(reach));
		LivingEntity best = null;
		double bestDist = Double.MAX_VALUE;
		for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class,
				player.getBoundingBox().expandTowards(end.subtract(eyes)).inflate(1.0),
				e -> e != player && e.isAlive() && !Allies.isFriend(player, e))) {
			var hit = e.getBoundingBox().inflate(0.3).clip(eyes, end);
			if (hit.isPresent() && hit.get().distanceToSqr(eyes) < bestDist) {
				bestDist = hit.get().distanceToSqr(eyes);
				best = e;
			}
		}
		return best;
	}
}
