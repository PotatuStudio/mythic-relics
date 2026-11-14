package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.greece.GreeceLegends;
import io.github.potatustudio.mythicrelics.minion.Minions;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Talos, el hombre de bronce que Hefesto hizo para guardar Creta, invocado con su clavo. Da vueltas alrededor de su
 * dueño como alrededor de la isla, lanza rocas a los hostiles y abraza al rojo al que se le acerca (lo quema). Su punto
 * débil es el tobillo: por la espalda recibe el doble ({@link GreeceLegends#modifyIncomingDamage}). Lo dirige
 * {@code Minions}; no se guarda con el mundo.
 */
public class Talos extends PathfinderMob {
	/** Evento para el brazo que lanza la roca (el mismo número que usa el gólem de hierro al golpear). */
	public static final byte THROW_EVENT = 4;
	private static final double CIRCLE_RADIUS = 2.6;

	private int rockCooldown = 20;
	private double circleAngle;
	/** Solo en el cliente: tick del último lanzamiento. */
	private int lastThrowTick = -100;

	public Talos(EntityType<? extends Talos> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 100.0)
				.add(Attributes.MOVEMENT_SPEED, 0.28)
				.add(Attributes.ATTACK_DAMAGE, GreeceLegends.TALOS_HUG_DAMAGE)
				.add(Attributes.ARMOR, 10.0)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
				.add(Attributes.FOLLOW_RANGE, 24.0)
				.add(Attributes.STEP_HEIGHT, 1.1);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1, true));
		goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	/** El abrazo al rojo: además del golpe, prende fuego al abrazado. */
	@Override
	public boolean doHurtTarget(ServerLevel level, Entity target) {
		boolean hit = super.doHurtTarget(level, target);
		if (hit) {
			target.igniteForSeconds(GreeceLegends.TALOS_HUG_FIRE_SECONDS);
			level.sendParticles(ParticleTypes.FLAME, target.getX(), target.getY(0.6), target.getZ(), 20, 0.3, 0.4, 0.3, 0.03);
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 0.8f, 0.6f);
		}
		return hit;
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		LivingEntity target = getTarget();
		if (rockCooldown > 0) {
			rockCooldown--;
		}
		if (target != null && target.isAlive() && rockCooldown == 0 && distanceToSqr(target) > 9
				&& distanceToSqr(target) < GreeceLegends.TALOS_ROCK_RANGE * GreeceLegends.TALOS_ROCK_RANGE && hasLineOfSight(target)) {
			throwRock(level, target);
			rockCooldown = GreeceLegends.TALOS_ROCK_INTERVAL;
		}
		// Sin enemigo: da vueltas alrededor de su dueño, como alrededor de Creta.
		if (target == null && tickCount % 10 == 0) {
			UUID owner = Minions.ownerOf(this);
			Player player = owner == null ? null : level.getPlayerByUUID(owner);
			if (player != null) {
				circleAngle += 0.6;
				Vec3 p = player.position().add(Math.cos(circleAngle) * CIRCLE_RADIUS, 0, Math.sin(circleAngle) * CIRCLE_RADIUS);
				getNavigation().moveTo(p.x, p.y, p.z, 0.9);
			}
		}
	}

	private void throwRock(ServerLevel level, LivingEntity target) {
		lookAt(target, 60.0f, 60.0f);
		Snowball rock = new Snowball(level, this, new ItemStack(Items.COBBLESTONE));
		rock.addTag(GreeceLegends.TALOS_ROCK_TAG);
		rock.setPos(getX(), getY() + getBbHeight() * 0.85, getZ());
		Vec3 to = target.getEyePosition().subtract(rock.position());
		rock.shoot(to.x, to.y + to.horizontalDistance() * 0.12, to.z, 1.4f, 2.0f);
		level.addFreshEntity(rock);
		level.broadcastEntityEvent(this, THROW_EVENT);
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.NEUTRAL, 1.2f, 0.7f);
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == THROW_EVENT) {
			lastThrowTick = tickCount;
		} else {
			super.handleEntityEvent(id);
		}
	}

	/** Ticks desde el último lanzamiento (para el brazo en el modelo). */
	public int ticksSinceThrow() {
		return tickCount - lastThrowTick;
	}

	@Override
	public void tick() {
		super.tick();
		// Calor del bronce: chispas en los pies y el ícor que brilla en el tobillo.
		if (level() instanceof ServerLevel level && tickCount % 6 == 0) {
			level.sendParticles(ParticleTypes.SMALL_FLAME, getRandomX(0.6), getY() + 0.1, getRandomZ(0.6), 1, 0.0, 0.02, 0.0, 0.0);
		}
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.IRON_GOLEM_REPAIR;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.IRON_GOLEM_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.IRON_GOLEM_DEATH;
	}

	@Override
	public float getVoicePitch() {
		return 0.7f;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.IRON_GOLEM_STEP, 1.0f, 0.8f);
	}
}
