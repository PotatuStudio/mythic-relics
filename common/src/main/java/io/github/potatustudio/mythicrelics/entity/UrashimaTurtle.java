package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.japan.JapanRelics;
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
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * La tortuga gigante de Urashima, llamada con su caña: solo la monta su dueño. Bajo el agua nada muy rápido hacia donde
 * mira el jinete (también arriba y abajo, con salto para subir); en tierra camina despacio. Montada, el jinete tiene el
 * poder del conducto (respira y ve bajo el agua). Sin jinete vuelve al mar a los pocos segundos; si muere, la caña tarda
 * en llamarla otra vez. No se guarda con el mundo.
 */
public class UrashimaTurtle extends PathfinderMob {
	private static final double SWIM_SPEED = 0.11;
	private static final double LAND_SPEED = 0.05;

	private @Nullable UUID owner;
	private int riderless;

	public UrashimaTurtle(EntityType<? extends UrashimaTurtle> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 50.0)
				.add(Attributes.MOVEMENT_SPEED, 0.15)
				.add(Attributes.ARMOR, 8.0)
				.add(Attributes.SAFE_FALL_DISTANCE, 16.0);
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
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
		return new Vec3(0.0, dimensions.height() * 0.95, 0.0);
	}

	/** Bajo el agua: hacia donde mira el jinete, con salto para subir; en tierra solo adelante y atrás. */
	@Override
	protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
		float strafe = player.xxa * 0.4f;
		if (!isInWater()) {
			return new Vec3(strafe, 0.0, player.zza > 0 ? 1.0 : player.zza < 0 ? -0.4 : 0.0);
		}
		float forward = 0.0f;
		float up = 0.0f;
		if (player.zza != 0.0f) {
			float pitch = player.getXRot() * Mth.DEG_TO_RAD;
			forward = Mth.cos(pitch);
			up = -Mth.sin(pitch);
			if (player.zza < 0.0f) {
				forward *= -0.4f;
				up *= -0.4f;
			}
		}
		if (player.isJumping()) {
			up += 0.5f;
		}
		return new Vec3(strafe, up, forward);
	}

	@Override
	protected void tickRidden(Player player, Vec3 travelVector) {
		super.tickRidden(player, travelVector);
		float yaw = getYRot() + Mth.wrapDegrees(player.getYRot() - getYRot()) * 0.12f;
		setRot(yaw, isInWater() ? player.getXRot() * 0.5f : 0.0f);
		yRotO = yBodyRot = yHeadRot = yaw;
	}

	@Override
	protected float getRiddenSpeed(Player player) {
		return (float) (isInWater() ? SWIM_SPEED : LAND_SPEED);
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (isInWater() && getControllingPassenger() != null) {
			setNoGravity(true);
			moveRelative((float) SWIM_SPEED, travelVector);
			move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
			setDeltaMovement(getDeltaMovement().scale(0.85));
			return;
		}
		setNoGravity(false);
		super.travel(travelVector);
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			if (isInWater() && getDeltaMovement().lengthSqr() > 0.004 && random.nextFloat() < 0.5f) {
				level().addParticle(ParticleTypes.BUBBLE, getX() + (random.nextDouble() - 0.5), getY() + 0.5, getZ() + (random.nextDouble() - 0.5),
						0.0, 0.05, 0.0);
			}
			return;
		}
		if (!(level() instanceof ServerLevel level)) {
			return;
		}
		if (getFirstPassenger() instanceof Player rider) {
			riderless = 0;
			rider.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 220, 0, true, false));
			rider.resetFallDistance();
			return;
		}
		if (++riderless >= JapanRelics.TURTLE_LEAVE_TICKS) {
			level.sendParticles(ParticleTypes.SPLASH, getX(), getY() + 0.5, getZ(), 40, 1.0, 0.4, 1.0, 0.1);
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.TURTLE_SWIM, getSoundSource(), 1.0f, 0.7f);
			discard();
		}
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		if (source.getEntity() != null && hasPassenger(source.getEntity())) {
			return false;
		}
		return super.hurtServer(level, source, amount);
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		if (level() instanceof ServerLevel level && owner != null && level.getPlayerByUUID(owner) instanceof ServerPlayer player) {
			RelicCooldowns.start(player, "urashima_rod", JapanRelics.TURTLE_COOLDOWN, BuiltInRegistries.ITEM.getKey(ModItems.URASHIMA_ROD.get()));
		}
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
		return isInWater() ? SoundEvents.TURTLE_AMBIENT_LAND : SoundEvents.TURTLE_AMBIENT_LAND;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.TURTLE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.TURTLE_DEATH;
	}

	@Override
	public float getVoicePitch() {
		return 0.6f;
	}
}
