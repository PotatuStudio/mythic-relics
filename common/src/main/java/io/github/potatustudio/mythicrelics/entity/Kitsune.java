package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.japan.JapanRelics;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Kitsune de nueve colas, llamado por la esfera estelar: muerde y, cada pocos segundos, lanza un kitsunebi (fuego fatuo
 * azul) a su objetivo, que lo quema un instante y lo deja confundido. No le afecta el fuego. Lo dirige {@code Minions}.
 */
public class Kitsune extends PathfinderMob {
	private int foxfire;

	public Kitsune(EntityType<? extends Kitsune> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, JapanRelics.KITSUNE_HEALTH)
				.add(Attributes.MOVEMENT_SPEED, 0.38)
				.add(Attributes.ATTACK_DAMAGE, JapanRelics.KITSUNE_DAMAGE)
				.add(Attributes.FOLLOW_RANGE, 32.0)
				.add(Attributes.SAFE_FALL_DISTANCE, 12.0);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4f));
		goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.3, true));
		goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		LivingEntity target = getTarget();
		if (target == null || !target.isAlive() || ++foxfire < JapanRelics.KITSUNEBI_INTERVAL || distanceToSqr(target) > 16 * 16
				|| !hasLineOfSight(target)) {
			return;
		}
		foxfire = 0;
		Vec3 from = getEyePosition();
		Vec3 to = target.getEyePosition();
		Vec3 step = to.subtract(from);
		int n = Math.max(2, (int) (step.length() * 3));
		for (int i = 0; i <= n; i++) {
			Vec3 p = from.add(step.scale(i / (double) n));
			level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, p.x, p.y, p.z, 1, 0.03, 0.03, 0.03, 0.0);
		}
		target.igniteForSeconds(JapanRelics.KITSUNEBI_BURN_SECONDS);
		target.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 0));
		target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0));
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.NEUTRAL, 1.0f, 1.4f);
	}

	@Override
	public boolean fireImmune() {
		return true;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return getTarget() != null ? SoundEvents.FOX_AGGRO : SoundEvents.FOX_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.FOX_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.FOX_DEATH;
	}
}
