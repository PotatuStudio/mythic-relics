package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.china.ChinaGods;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * El qilin que llama el cuerno: bestia benévola con cabeza de dragón, cuerpo de ciervo escamado y un solo cuerno.
 * Siempre domado y ensillado, solo lo monta su dueño. Muy rápido, salta alto, sin daño de caída, inmune al fuego y no
 * aplasta los cultivos (lo impide {@code FarmBlockMixin}). Mientras lo montan, regeneran el jinete y los animales y
 * aldeanos cercanos. Sin jinete se va a los pocos segundos; si muere, el cuerno tarda en llamarlo otra vez. No se
 * guarda con el mundo.
 */
public class Qilin extends AbstractHorse {
	private int riderless;

	public Qilin(EntityType<? extends Qilin> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createBaseHorseAttributes()
				.add(Attributes.MAX_HEALTH, 40.0)
				.add(Attributes.MOVEMENT_SPEED, 0.42)
				.add(Attributes.JUMP_STRENGTH, 1.0)
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
			if (random.nextFloat() < 0.3f) {
				// Las llamas que no queman.
				level().addParticle(ParticleTypes.SMALL_FLAME, getX() + (random.nextDouble() - 0.5) * getBbWidth(), getY() + 0.2 + random.nextDouble() * 0.6,
						getZ() + (random.nextDouble() - 0.5) * getBbWidth(), 0.0, 0.01, 0.0);
			}
			return;
		}
		if (!(level() instanceof ServerLevel level)) {
			return;
		}
		clearFire();
		if (getFirstPassenger() instanceof Player rider) {
			riderless = 0;
			if (tickCount % 40 == 0) {
				aura(level, rider);
			}
			return;
		}
		if (++riderless >= ChinaGods.QILIN_LEAVE_TICKS) {
			level.sendParticles(ParticleTypes.FLAME, getX(), getY() + 1.0, getZ(), 30, 0.6, 0.6, 0.6, 0.02);
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.HORSE_GALLOP, getSoundSource(), 1.0f, 1.2f);
			discard();
		}
	}

	/** Benevolencia: regeneran el jinete y los animales y aldeanos cercanos. */
	private void aura(ServerLevel level, Player rider) {
		rider.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(ChinaGods.QILIN_AURA_RADIUS),
				e -> e != this && (e instanceof Animal || e instanceof AbstractVillager))) {
			e.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
		}
		level.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 1.2, getZ(), 4, 1.5, 0.5, 1.5, 0.0);
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		if (level() instanceof ServerLevel && getOwner() instanceof ServerPlayer owner) {
			RelicCooldowns.start(owner, "qilin_horn", ChinaGods.QILIN_COOLDOWN, BuiltInRegistries.ITEM.getKey(ModItems.QILIN_HORN.get()));
		}
	}

	@Override
	public boolean causeFallDamage(double distance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	protected void dropEquipment(ServerLevel level) {
		// La silla es del cielo: no se suelta.
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

	@Override
	public float getVoicePitch() {
		return 0.8f;
	}

	/** No se guarda con el mundo (no puede ser {@code noSave}: no se podría montar). */
	@Override
	public boolean shouldBeSaved() {
		return false;
	}

	/** El jinete la controla siempre (no depende de que el cliente vea la silla). */
	@Override
	public @Nullable LivingEntity getControllingPassenger() {
		return getFirstPassenger() instanceof Player player ? player : super.getControllingPassenger();
	}

	/** Siempre va ensillado (la silla viene con él): así salta y responde al jinete aunque el cliente no la haya visto. */
	@Override
	public boolean isSaddled() {
		return true;
	}
}
