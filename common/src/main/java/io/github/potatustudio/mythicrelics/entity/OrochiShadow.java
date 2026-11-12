package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.japan.JapanGods;
import io.github.potatustudio.mythicrelics.minion.Minions;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

/**
 * Sombra de Yamata no Orochi, llamada con la escama: no se mueve; cada poco sus ocho cabezas muerden a la vez a los
 * hostiles que tiene a {@link JapanGods#OROCHI_REACH} bloques (una cabeza por hostil, hasta ocho). Lo dirige {@code Minions}.
 */
public class OrochiShadow extends PathfinderMob {
	private static final int HEADS = 8;
	/** Evento que avisa a los clientes de una mordida (100 + cuántas cabezas muerden; vanilla no usa ese rango). */
	private static final byte BITE_EVENT_BASE = 100;
	private int bite;
	/** Solo en el cliente: tick de la última mordida y cuántas cabezas mordieron. */
	private int lastBiteTick = -100;
	private int bitingHeads;

	public OrochiShadow(EntityType<? extends OrochiShadow> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 80.0)
				.add(Attributes.MOVEMENT_SPEED, 0.0)
				.add(Attributes.ARMOR, 10.0)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
				.add(Attributes.ATTACK_DAMAGE, JapanGods.OROCHI_BITE)
				.add(Attributes.FOLLOW_RANGE, 16.0);
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		if (++bite < JapanGods.OROCHI_BITE_INTERVAL) {
			return;
		}
		UUID owner = Minions.ownerOf(this);
		int r = JapanGods.OROCHI_REACH;
		List<LivingEntity> prey = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(r),
						e -> e != this && e.isAlive() && (e instanceof Enemy || e == getTarget()) && Minions.ownerOf(e) == null
								&& (owner == null || !e.getUUID().equals(owner)) && e.distanceToSqr(this) <= r * r)
				.stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(this))).limit(HEADS).toList();
		if (prey.isEmpty()) {
			return;
		}
		bite = 0;
		getLookControl().setLookAt(prey.get(0));
		level.broadcastEntityEvent(this, (byte) (BITE_EVENT_BASE + prey.size()));
		DamageSource source = damageSources().mobAttack(this);
		for (LivingEntity e : prey) {
			e.hurtServer(level, source, JapanGods.OROCHI_BITE);
			level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, e.getX(), e.getY(0.6), e.getZ(), 3, 0.2, 0.2, 0.2, 0.0);
			level.sendParticles(ParticleTypes.SMOKE, e.getX(), e.getY(0.6), e.getZ(), 6, 0.2, 0.3, 0.2, 0.02);
		}
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.EVOKER_FANGS_ATTACK, SoundSource.NEUTRAL, 1.2f, 0.6f);
	}

	@Override
	public void tick() {
		super.tick();
		if (level() instanceof ServerLevel level && tickCount % 4 == 0) {
			level.sendParticles(ParticleTypes.LARGE_SMOKE, getRandomX(1.0), getY() + 0.3, getRandomZ(1.0), 1, 0.0, 0.05, 0.0, 0.0);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id > BITE_EVENT_BASE && id <= BITE_EVENT_BASE + HEADS) {
			lastBiteTick = tickCount;
			bitingHeads = id - BITE_EVENT_BASE;
		} else {
			super.handleEntityEvent(id);
		}
	}

	/** Ticks desde la última mordida (para la animación). */
	public int ticksSinceBite() {
		return tickCount - lastBiteTick;
	}

	/** Cuántas cabezas mordieron la última vez (una por hostil, hasta ocho). */
	public int bitingHeads() {
		return bitingHeads;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.RAVAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.RAVAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.RAVAGER_DEATH;
	}

	@Override
	public float getVoicePitch() {
		return 0.6f;
	}
}
