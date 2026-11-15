package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.greece.GreeceLegends;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.Allies;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
 * Pegaso, el caballo alado que llama la Brida de oro de Atenea: blanco, con alas de plumas, solo lo monta su dueño. En el
 * suelo camina como un caballo; mantener el salto lo eleva y al soltarlo planea bajando despacio. Mirando hacia abajo
 * baja en picada y embiste a los mobs que toca (Belerofonte contra la Quimera). Si sube por encima del Olimpo, el tábano
 * de Zeus lo pica y tira al jinete (que cae lento). Al quedarse sin jinete se va; si muere, la brida tarda en poder
 * llamarlo otra vez. No se guarda con el mundo.
 */
public class Pegasus extends PathfinderMob {
	private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(Pegasus.class, EntityDataSerializers.BOOLEAN);
	private static final double FLY_SPEED = 0.42;
	private static final double DIVE_SPEED = -0.3;
	private static final int DIVE_HIT_COOLDOWN = 20;

	private @Nullable UUID owner;
	private int riderless;
	private final Map<UUID, Long> diveHits = new HashMap<>();
	/** Solo en el cliente: cuánto está «en el aire» el modelo (0 suelo, 1 vuelo), suavizado para que no cambie de golpe. */
	public float airAnim;
	/** Ticks seguidos en el aire (solo en el servidor) y los que hacen falta para contar como vuelo. */
	private int airTicks;
	private static final int AIR_TICKS_TO_FLY = 6;
	public float airAnimO;

	public Pegasus(EntityType<? extends Pegasus> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 40.0)
				.add(Attributes.MOVEMENT_SPEED, 0.3)
				.add(Attributes.FLYING_SPEED, 0.11)
				.add(Attributes.ARMOR, 4.0)
				.add(Attributes.STEP_HEIGHT, 1.1)
				.add(Attributes.SAFE_FALL_DISTANCE, 1024.0);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(FLYING, false);
	}

	/** En el aire (para que el modelo bata las alas). */
	public boolean isFlying() {
		return entityData.get(FLYING);
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
		return !onGround();
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
		return new Vec3(0.0, dimensions.height() * 0.82, -0.1);
	}

	/**
	 * Como un caballo, pero con alas: adelante y atrás con las teclas; mantener el salto sube y, sin él, planea bajando
	 * despacio. Mirando hacia abajo mientras avanza, baja en picada.
	 */
	@Override
	protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
		float strafe = player.xxa * 0.5f;
		float forward = player.zza > 0 ? 1.0f : player.zza < 0 ? -0.4f : 0.0f;
		float up = 0.0f;
		if (player.isJumping()) {
			up = 0.7f;
		} else if (!onGround()) {
			up = -0.18f;
			float pitch = player.getXRot();
			if (pitch > 20.0f && forward > 0) {
				up -= Mth.sin(pitch * Mth.DEG_TO_RAD) * 1.2f;   // picada
			}
		}
		return new Vec3(strafe, up, forward).scale(FLY_SPEED);
	}

	@Override
	protected void tickRidden(Player player, Vec3 travelVector) {
		super.tickRidden(player, travelVector);
		float yaw = getYRot();
		yaw += Mth.wrapDegrees(player.getYRot() - yaw) * 0.25f;
		setRot(yaw, 0.0f);
		yRotO = yBodyRot = yHeadRot = yaw;
	}

	@Override
	protected float getRiddenSpeed(Player player) {
		return (float) FLY_SPEED;
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (getControllingPassenger() instanceof Player) {
			setNoGravity(true);
			travelFlying(travelVector, 0.12f, 0.12f, 0.12f);
		} else {
			setNoGravity(false);
			super.travel(travelVector);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!(level() instanceof ServerLevel level)) {
			airAnimO = airAnim;
			airAnim += ((isFlying() ? 1.0f : 0.0f) - airAnim) * 0.18f;
			if (isFlying() && random.nextFloat() < 0.3f) {
				level().addParticle(ParticleTypes.CLOUD, getRandomX(1.0), getY() + 0.6, getRandomZ(1.0), 0.0, -0.02, 0.0);
			}
			return;
		}
		// Cuenta como vuelo solo si lleva unos ticks en el aire: al caminar, un escalón o un saltito lo despegan un tick
		// del suelo y las alas se abrían y cerraban a medias (parecía que nadaba). Al tocar el suelo aterriza al instante.
		// Pegaso no entra al agua: al tocarla desmonta al jinete y vuelve al cielo (no se puede volar ni planear en el agua).
		if (isInWater()) {
			ejectPassengers();
			vanish(level);
			return;
		}
		airTicks = onGround() ? 0 : airTicks + 1;
		entityData.set(FLYING, airTicks > AIR_TICKS_TO_FLY);
		if (getFirstPassenger() instanceof Player rider) {
			riderless = 0;
			rider.resetFallDistance();
			resetFallDistance();
			dive(level, rider);
			if (getY() > GreeceLegends.PEGASUS_OLYMPUS_Y) {
				gadfly(level, rider);
			}
			return;
		}
		if (++riderless >= GreeceLegends.PEGASUS_LEAVE_TICKS) {
			vanish(level);
		}
	}

	/** Vuelve al cielo: nube, destellos y un aletazo. */
	private void vanish(ServerLevel level) {
		level.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 1.0, getZ(), 50, 1.0, 0.8, 1.0, 0.03);
		level.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1.0, getZ(), 20, 0.8, 0.6, 0.8, 0.05);
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.PHANTOM_FLAP, getSoundSource(), 1.5f, 1.4f);
		discard();
	}

	/** En picada (bajando rápido), embiste a los mobs que toca. */
	private void dive(ServerLevel level, Player rider) {
		if (getDeltaMovement().y > DIVE_SPEED) {
			return;
		}
		level.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 0.5, getZ(), 2, 0.4, 0.3, 0.4, 0.0);
		long now = level.getGameTime();
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.6),
				e -> e != this && e != rider && e.isAlive() && !Allies.isFriend(rider, e))) {
			Long last = diveHits.get(e.getUUID());
			if (last != null && now - last < DIVE_HIT_COOLDOWN) {
				continue;
			}
			diveHits.put(e.getUUID(), now);
			e.hurtServer(level, damageSources().mobAttack(this), GreeceLegends.PEGASUS_DIVE_DAMAGE);
			Vec3 push = e.position().subtract(position()).multiply(1, 0, 1).normalize();
			e.push(push.x * 0.8, 0.4, push.z * 0.8);
			level.sendParticles(ParticleTypes.CRIT, e.getX(), e.getY(0.6), e.getZ(), 15, 0.3, 0.3, 0.3, 0.3);
			level.playSound(null, e.getX(), e.getY(), e.getZ(), SoundEvents.HORSE_ANGRY, getSoundSource(), 1.0f, 1.1f);
		}
	}

	/** El tábano de Zeus: pica a Pegaso, que tira al jinete; el jinete cae lento. */
	private void gadfly(ServerLevel level, Player rider) {
		rider.stopRiding();
		rider.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, GreeceLegends.PEGASUS_FALL_TICKS, 0, false, true));
		RelicFx.notify(rider, "pegasus_gadfly", ChatFormatting.GOLD);
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.BEE_STING, getSoundSource(), 1.5f, 0.8f);
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.HORSE_ANGRY, getSoundSource(), 1.5f, 1.0f);
		setDeltaMovement(getDeltaMovement().add(0, 0.6, 0));
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
			RelicCooldowns.start(player, "golden_bridle", GreeceLegends.PEGASUS_COOLDOWN,
					BuiltInRegistries.ITEM.getKey(ModItems.GOLDEN_BRIDLE.get()));
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
		return SoundEvents.HORSE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.HORSE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.HORSE_DEATH;
	}

	@Override
	public int getAmbientSoundInterval() {
		return 300;
	}

	/** No se guarda con el mundo (no puede ser {@code noSave}: no se podría montar). */
	@Override
	public boolean shouldBeSaved() {
		return false;
	}
}
