package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.greece.GreeceHeroes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
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
 * Cerbero cachorro del collar de Cerbero: un solo perro de tres cabezas. Cada ataque son tres mordidas seguidas, una por
 * cabeza (la primera es el golpe normal; las otras dos llegan a los pocos ticks). En el Nether, sus mordidas prenden fuego.
 * Como el chacal, no elige objetivos solo ({@code Minions} lo dirige) y no le afecta el fuego.
 */
public class Cerberus extends PathfinderMob {
	private static final int TICKS_BETWEEN_BITES = 4;

	private LivingEntity biting;
	private int bitesLeft;
	private int nextBite;

	public Cerberus(EntityType<? extends Cerberus> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0)
				.add(Attributes.MOVEMENT_SPEED, 0.34)
				.add(Attributes.ATTACK_DAMAGE, 2.5);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4f));
		goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.25, true));
		goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	@Override
	public boolean doHurtTarget(ServerLevel level, Entity target) {
		boolean hit = super.doHurtTarget(level, target);
		if (hit && target instanceof LivingEntity living) {
			burn(level, living);
			biting = living;
			bitesLeft = GreeceHeroes.CERBERUS_BITES - 1;
			nextBite = TICKS_BETWEEN_BITES;
		}
		return hit;
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		if (bitesLeft <= 0 || biting == null) {
			return;
		}
		if (!biting.isAlive() || distanceToSqr(biting) > 9.0) {
			bitesLeft = 0;
			biting = null;
			return;
		}
		if (--nextBite > 0) {
			return;
		}
		// Las otras cabezas: se salta la invulnerabilidad del golpe anterior para que cada mordida cuente.
		biting.setInvulnerableTime(0);
		if (biting.hurtServer(level, damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE))) {
			burn(level, biting);
		}
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.ZOGLIN_ATTACK, SoundSource.NEUTRAL, 0.6f, 1.3f + bitesLeft * 0.15f);
		bitesLeft--;
		nextBite = TICKS_BETWEEN_BITES;
	}

	/** Guardián del Hades: en el Nether, la mordida prende fuego. */
	private void burn(ServerLevel level, LivingEntity target) {
		if (level.dimension() == Level.NETHER) {
			target.igniteForSeconds(GreeceHeroes.CERBERUS_BURN_SECONDS);
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return getTarget() != null ? SoundEvents.ZOGLIN_ANGRY : SoundEvents.ZOGLIN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ZOGLIN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ZOGLIN_DEATH;
	}

	@Override
	public float getVoicePitch() {
		return super.getVoicePitch() * 1.4f;
	}
}
