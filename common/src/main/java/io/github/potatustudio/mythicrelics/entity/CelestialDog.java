package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.china.ChinaGods;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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

/**
 * El Perro Celestial (Xiaotian Quan) de Erlang Shen: un lebrel negro muy veloz. Su mordida deja inmóvil al objetivo un
 * instante, como cuando le mordió la pierna al Rey Mono. Como los demás aliados, lo dirige {@code Minions}.
 */
public class CelestialDog extends PathfinderMob {
	public CelestialDog(EntityType<? extends CelestialDog> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, ChinaGods.DOG_HEALTH)
				.add(Attributes.MOVEMENT_SPEED, 0.42)
				.add(Attributes.ATTACK_DAMAGE, ChinaGods.DOG_DAMAGE)
				.add(Attributes.FOLLOW_RANGE, 32.0)
				.add(Attributes.SAFE_FALL_DISTANCE, 12.0);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.45f));
		goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.35, true));
		goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	@Override
	public boolean doHurtTarget(ServerLevel level, Entity target) {
		boolean hit = super.doHurtTarget(level, target);
		if (hit && target instanceof LivingEntity living) {
			// La mordida a la pierna: lo deja clavado un momento.
			living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20, 9, false, false));
		}
		return hit;
	}

	/** Voz grave, de perro grande (los sonidos del lobo cambian por variante en 26.3; se usan los del zorro más graves). */
	@Override
	public float getVoicePitch() {
		return super.getVoicePitch() * 0.6f;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return getTarget() != null ? SoundEvents.FOX_AGGRO : SoundEvents.FOX_SNIFF;
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
