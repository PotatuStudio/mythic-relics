package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.norse.NorseRelics;
import io.github.potatustudio.mythicrelics.norse.NorseSagas;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.Bosses;
import io.github.potatustudio.mythicrelics.relic.RelicLightning;
import io.github.potatustudio.mythicrelics.relic.RelicSets;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Mjölnir o Gungnir lanzados: vuelan, golpean y vuelven a quien los lanzó, como un tridente con Lealtad (la vuelta está
 * copiada de ThrownTrident). Mjölnir no pasa de {@link NorseRelics#MJOLNIR_RANGE} bloques (el mango corto) y llama un
 * rayo en tormenta o con el set de Thor. Gungnir persigue al mob al que se apuntó (lo elige {@code ThrownRelicItem}).
 */
public class ThrownRelic extends AbstractArrow implements ItemSupplier {
	private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(ThrownRelic.class, EntityDataSerializers.ITEM_STACK);
	private static final int RETURN_SPEED = 3;

	private boolean dealtDamage;
	private @Nullable Vec3 origin;
	private int targetId = -1;
	private int returnTicks;

	public ThrownRelic(EntityType<? extends ThrownRelic> type, Level level) {
		super(type, level);
	}

	public ThrownRelic(Level level, LivingEntity owner, ItemStack stack) {
		super(ModEntities.THROWN_RELIC.get(), owner, level, stack, null);
		entityData.set(ITEM, stack.copyWithCount(1));
		origin = position();
	}

	public void setTarget(@Nullable Entity target) {
		targetId = target == null ? -1 : target.getId();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(ITEM, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItem() {
		return entityData.get(ITEM);
	}

	private boolean isMjolnir() {
		return getItem().is(ModItems.MJOLNIR.get());
	}

	private boolean isDart() {
		return getItem().is(ModItems.MISTLETOE_DART.get());
	}

	/** Muérdago: nada juró protegerlo, así que ignora la armadura, la Resistencia y los encantamientos de protección. */
	public static final ResourceKey<DamageType> MISTLETOE =
			ResourceKey.create(Registries.DAMAGE_TYPE,
					MythicRelics.id("mistletoe"));

	@Override
	protected ItemStack getDefaultPickupItem() {
		return new ItemStack(ModItems.MJOLNIR.get());
	}

	@Override
	public void tick() {
		if (inGroundTime > 4) {
			dealtDamage = true;
		}
		int range = isMjolnir() ? NorseRelics.MJOLNIR_RANGE : NorseRelics.GUNGNIR_RANGE;
		if (!dealtDamage && origin != null && position().distanceToSqr(origin) > (double) range * range) {
			dealtDamage = true;
		}
		// Gungnir nunca falla: mientras vuela, corrige el rumbo hacia su blanco.
		if (!dealtDamage && !isNoPhysics() && targetId >= 0 && level().getEntity(targetId) instanceof LivingEntity target && target.isAlive()) {
			Vec3 motion = getDeltaMovement();
			double speed = Math.max(1.5, motion.length());
			Vec3 wanted = target.getBoundingBox().getCenter().subtract(position()).normalize().scale(speed);
			setDeltaMovement(motion.lerp(wanted, 0.3));
		}
		Entity owner = getOwner();
		// El dardo de muérdago no vuelve: queda clavado donde cae hasta que lo recojas (como una flecha).
		if (!isDart() && (dealtDamage || isNoPhysics()) && owner != null) {
			if (!owner.isAlive() || owner instanceof ServerPlayer player && player.isSpectator()) {
				if (level() instanceof ServerLevel level && pickup == Pickup.ALLOWED) {
					spawnAtLocation(level, getPickupItem(), 0.1f);
				}
				discard();
			} else {
				setNoPhysics(true);
				Vec3 back = owner.getEyePosition().subtract(position());
				setPosRaw(getX(), getY() + back.y * 0.015 * RETURN_SPEED, getZ());
				setDeltaMovement(getDeltaMovement().scale(0.95).add(back.normalize().scale(0.05 * RETURN_SPEED)));
				if (returnTicks++ == 0) {
					playSound(SoundEvents.TRIDENT_RETURN, 10.0f, isMjolnir() ? 0.7f : 1.0f);
				}
			}
		}
		super.tick();
	}

	@Override
	protected @Nullable EntityHitResult findHitEntity(Vec3 from, Vec3 to) {
		return dealtDamage ? null : super.findHitEntity(from, to);
	}

	@Override
	protected void onHitEntity(EntityHitResult hit) {
		Entity target = hit.getEntity();
		Entity owner = getOwner();
		DamageSource source = damageSources().trident(this, owner == null ? this : owner);
		dealtDamage = true;
		float damage = isMjolnir() ? NorseRelics.MJOLNIR_DAMAGE : NorseRelics.GUNGNIR_DAMAGE;
		if (isDart()) {
			source = new DamageSource(level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
					.getOrThrow(MISTLETOE), this, owner == null ? this : owner);
			damage = NorseSagas.DART_DAMAGE;
			if (target instanceof LivingEntity living && Bosses.is(living)) {
				damage *= NorseSagas.DART_BOSS_MULTIPLIER;
			}
			if (level() instanceof ServerLevel level) {
				level.sendParticles(ParticleTypes.HAPPY_VILLAGER, target.getX(), target.getY(0.6), target.getZ(),
						12, 0.3, 0.3, 0.3, 0.0);
			}
		}
		if (level() instanceof ServerLevel level && target.hurtServer(level, source, damage)) {
			if (isMjolnir() && (level.isThundering() || RelicSets.THOR.isComplete(owner))) {
				// Rayo sin fuego: no quema el terreno.
				RelicLightning.strike(level, target.getX(), target.getY(), target.getZ(),
						owner instanceof ServerPlayer player ? player : null);
			}
		}
		setDeltaMovement(getDeltaMovement().multiply(-0.01, -0.1, -0.01));
		playSound(SoundEvents.TRIDENT_HIT, 1.0f, isMjolnir() ? 0.6f : 1.0f);
	}

	@Override
	protected boolean tryPickup(Player player) {
		return super.tryPickup(player) || isNoPhysics() && ownedBy(player) && player.getInventory().add(getPickupItem());
	}

	@Override
	public void playerTouch(Player player) {
		if (ownedBy(player) || getOwner() == null) {
			super.playerTouch(player);
		}
	}

	@Override
	public void tickDespawn() {
		// Mjölnir y Gungnir vuelven siempre a su dueño y el dardo espera a que lo recojan: no se borran solos.
		if (pickup != Pickup.ALLOWED) {
			super.tickDespawn();
		}
	}

	@Override
	protected float getWaterInertia() {
		return 0.99f;
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		dealtDamage = input.getBooleanOr("dealt_damage", false);
		entityData.set(ITEM, getPickupItemStackOrigin().copyWithCount(1));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putBoolean("dealt_damage", dealtDamage);
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		return true;
	}
}
