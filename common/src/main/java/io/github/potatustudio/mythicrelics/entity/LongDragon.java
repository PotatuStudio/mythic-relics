package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * El long, el dragón chino que llama la Perla del dragón: largo y serpentino, sin alas, vuela entre las nubes y nada
 * bajo el agua igual de rápido. Solo lo monta su dueño. Se maneja como el ghast feliz: hacia donde miras (también arriba
 * y abajo), con salto para subir. Montado, respiras bajo el agua y no recibes daño de caída. Al quedarse sin jinete
 * vuelve a las nubes a los pocos segundos; si muere, la perla tarda en poder llamarlo otra vez. No se guarda con el
 * mundo.
 */
public class LongDragon extends PathfinderMob {
	private @Nullable UUID owner;
	private int riderless;

	public LongDragon(EntityType<? extends LongDragon> type, Level level) {
		super(type, level);
		setNoGravity(true);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 60.0)
				.add(Attributes.MOVEMENT_SPEED, 0.3)
				.add(Attributes.FLYING_SPEED, 0.11)
				.add(Attributes.ARMOR, 6.0)
				.add(Attributes.SAFE_FALL_DISTANCE, 1024.0);
	}

	public void setOwner(Player player) {
		owner = player.getUUID();
	}

	public boolean isOwnedBy(Player player) {
		return player.getUUID().equals(owner);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (!isOwnedBy(player)) {
			return InteractionResult.PASS;
		}
		if (!isVehicle() && !level().isClientSide()) {
			player.startRiding(this);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public @Nullable LivingEntity getControllingPassenger() {
		return getFirstPassenger() instanceof Player player ? player : super.getControllingPassenger();
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return getPassengers().isEmpty();
	}

	@Override
	public boolean isFlyingVehicle() {
		return true;
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
		return new Vec3(0.0, dimensions.height() * 0.78, 0.0);
	}

	/** Como el ghast feliz: avanza hacia donde mira el jinete (subiendo o bajando con la mirada) y sube con el salto. */
	@Override
	protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
		float strafe = player.xxa * 0.5f;
		float forward = 0.0f;
		float up = 0.0f;
		if (player.zza != 0.0f) {
			float pitch = player.getXRot() * Mth.DEG_TO_RAD;
			forward = Mth.cos(pitch);
			up = -Mth.sin(pitch);
			if (player.zza < 0.0f) {
				forward *= -0.5f;
				up *= -0.5f;
			}
		}
		if (player.isJumping()) {
			up += 0.5f;
		}
		return new Vec3(strafe, up, forward).scale(3.9 * getAttributeValue(Attributes.FLYING_SPEED));
	}

	@Override
	protected void tickRidden(Player player, Vec3 travelVector) {
		super.tickRidden(player, travelVector);
		Vec2 look = new Vec2(player.getXRot() * 0.5f, player.getYRot());
		float yaw = getYRot();
		yaw += Mth.wrapDegrees(look.y - yaw) * 0.15f;
		setRot(yaw, look.x);
		yRotO = yBodyRot = yHeadRot = yaw;
	}

	@Override
	protected float getRiddenSpeed(Player player) {
		return (float) getAttributeValue(Attributes.FLYING_SPEED) * 5.0f / 3.0f;
	}

	@Override
	public void travel(Vec3 travelVector) {
		float speed = (float) getAttributeValue(Attributes.FLYING_SPEED) * 5.0f / 3.0f;
		travelFlying(travelVector, speed, speed, speed);
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			trail();
			return;
		}
		if (!(level() instanceof ServerLevel level)) {
			return;
		}
		if (getFirstPassenger() instanceof Player rider) {
			riderless = 0;
			rider.resetFallDistance();
			rider.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 40, 0, true, false));
			return;
		}
		// Sin jinete: se queda flotando y vuelve a las nubes.
		setDeltaMovement(getDeltaMovement().scale(0.8));
		if (++riderless >= ChinaRelics.DRAGON_LEAVE_TICKS) {
			level.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 0.8, getZ(), 50, 1.5, 0.6, 1.5, 0.03);
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.PHANTOM_FLAP, getSoundSource(), 1.5f, 0.6f);
			discard();
		}
	}

	/** Cliente: al volar deja jirones de nube; bajo el agua, burbujas. */
	private void trail() {
		if (getDeltaMovement().lengthSqr() < 0.01 || random.nextFloat() > 0.6f) {
			return;
		}
		Vec3 back = Vec3.directionFromRotation(0, getYRot()).scale(-1.5);
		level().addParticle(isInWater() ? ParticleTypes.BUBBLE : ParticleTypes.CLOUD, getX() + back.x + (random.nextDouble() - 0.5),
				getY() + 0.5 + (random.nextDouble() - 0.5) * 0.4, getZ() + back.z + (random.nextDouble() - 0.5), 0.0, 0.0, 0.0);
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		// Ni su jinete ni sus propios golpes lo lastiman.
		if (source.getEntity() != null && hasPassenger(source.getEntity())) {
			return false;
		}
		return super.hurtServer(level, source, amount);
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		if (level() instanceof ServerLevel level && owner != null && level.getPlayerByUUID(owner) instanceof ServerPlayer player) {
			RelicCooldowns.start(player, "dragon_pearl", ChinaRelics.DRAGON_COOLDOWN, BuiltInRegistries.ITEM.getKey(ModItems.DRAGON_PEARL.get()));
		}
	}

	@Override
	public boolean causeFallDamage(double distance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	@Override
	public boolean canBeLeashed() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENDER_DRAGON_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENDER_DRAGON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENDER_DRAGON_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4f;
	}

	@Override
	public float getVoicePitch() {
		return 1.6f;
	}

	@Override
	public int getAmbientSoundInterval() {
		return 400;
	}
}
