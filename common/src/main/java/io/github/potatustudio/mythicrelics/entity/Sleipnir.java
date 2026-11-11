package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.norse.NorseGods;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

/**
 * Sleipnir, el caballo de ocho patas de Odín, invocado con la herradura: siempre domado y ensillado, solo lo monta su
 * dueño, es muy rápido, salta alto, no recibe daño de caída y galopa sobre el agua. Al quedarse sin jinete vuelve a
 * Asgard a los pocos segundos; si muere, la herradura tarda en poder llamarlo otra vez. No se guarda con el mundo.
 */
public class Sleipnir extends AbstractHorse {
	private int riderless;

	public Sleipnir(EntityType<? extends Sleipnir> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createBaseHorseAttributes()
				.add(Attributes.MAX_HEALTH, 40.0)
				.add(Attributes.MOVEMENT_SPEED, 0.45)
				.add(Attributes.JUMP_STRENGTH, 1.1)
				.add(Attributes.SAFE_FALL_DISTANCE, 1024.0);
	}

	/** Lo deja listo para su dueño: domado y ensillado. */
	public void bindTo(Player owner) {
		setTamed(true);
		setOwner(owner);
		setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (getOwnerReference() == null || !getOwnerReference().matches(player)) {
			return InteractionResult.PASS;
		}
		if (!isVehicle()) {
			doPlayerRide(player);
		}
		return InteractionResult.SUCCESS;
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
		// Sin jinete, vuelve a Asgard.
		riderless = isVehicle() ? 0 : riderless + 1;
		if (riderless >= NorseGods.SLEIPNIR_LEAVE_TICKS) {
			level.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 1.0, getZ(), 30, 0.6, 0.6, 0.6, 0.05);
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.HORSE_GALLOP, getSoundSource(), 1.0f, 1.4f);
			discard();
		}
	}

	/** Cliente: al galopar, chispas de escarcha y niebla bajo los cascos; sobre el agua, salpicaduras. */
	private void trail() {
		double speed = getDeltaMovement().horizontalDistance();
		if (speed < 0.12 || random.nextFloat() > 0.8f) {
			return;
		}
		boolean onWater = level().getFluidState(blockPosition()).is(FluidTags.WATER)
				|| level().getFluidState(blockPosition().below()).is(FluidTags.WATER);
		for (int i = 0; i < 2; i++) {
			double x = getX() + (random.nextDouble() - 0.5) * getBbWidth();
			double z = getZ() + (random.nextDouble() - 0.5) * getBbWidth();
			level().addParticle(onWater ? ParticleTypes.SPLASH : ParticleTypes.SNOWFLAKE, x, getY() + 0.1, z, 0.0, 0.05, 0.0);
			if (random.nextFloat() < 0.4f) {
				level().addParticle(onWater ? ParticleTypes.BUBBLE_POP : ParticleTypes.ELECTRIC_SPARK, x, getY() + 0.2, z, 0.0, 0.02, 0.0);
			}
		}
		if (random.nextFloat() < 0.3f) {
			level().addParticle(ParticleTypes.CLOUD, getX(), getY() + 0.15, getZ(), -getDeltaMovement().x * 0.2, 0.01, -getDeltaMovement().z * 0.2);
		}
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		if (level() instanceof ServerLevel && getOwnerReference() != null
				&& getOwner() instanceof ServerPlayer owner) {
			RelicCooldowns.start(owner, "sleipnir_horseshoe", NorseGods.SLEIPNIR_COOLDOWN,
					BuiltInRegistries.ITEM.getKey(ModItems.SLEIPNIR_HORSESHOE.get()));
		}
	}

	@Override
	public boolean causeFallDamage(double distance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	public boolean canStandOnFluid(FluidState fluid) {
		return fluid.is(FluidTags.WATER);
	}

	@Override
	protected void dropEquipment(ServerLevel level) {
		// La montura es de Asgard: no suelta la silla.
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canMate(Animal other) {
		return false;
	}

	@Override
	public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return null;
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

	/** No se guarda con el mundo (no puede ser {@code noSave}: no se podría montar). */
	@Override
	public boolean shouldBeSaved() {
		return false;
	}
}
