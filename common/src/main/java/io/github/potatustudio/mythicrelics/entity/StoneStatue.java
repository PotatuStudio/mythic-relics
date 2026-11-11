package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.greece.StoneStatueItem;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Estatua de piedra de un mob vencido mientras estaba petrificado por la cabeza de Medusa: decorativa, se coloca con su
 * ítem ({@link StoneStatueItem}) y se dibuja con el modelo del mob en piedra. Un golpe la devuelve como ítem.
 */
public class StoneStatue extends Entity {
	private static final EntityDataAccessor<String> MOB = SynchedEntityData.defineId(StoneStatue.class, EntityDataSerializers.STRING);

	public StoneStatue(EntityType<? extends StoneStatue> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(MOB, "minecraft:zombie");
	}

	public void setMob(EntityType<?> type) {
		entityData.set(MOB, BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
	}

	public @Nullable EntityType<?> getMob() {
		Identifier id = Identifier.tryParse(entityData.get(MOB));
		return id == null ? null : BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		input.getString("mob").ifPresent(s -> entityData.set(MOB, s));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		output.putString("mob", entityData.get(MOB));
	}

	@Override
	public boolean isPickable() {
		return !isRemoved();
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		if (isRemoved()) {
			return false;
		}
		EntityType<?> mob = getMob();
		if (mob != null) {
			ItemEntity drop = new ItemEntity(level, getX(), getY() + 0.5, getZ(), StoneStatueItem.of(mob));
			drop.setDefaultPickUpDelay();
			level.addFreshEntity(drop);
		}
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()), getX(), getY() + 0.8,
				getZ(), 30, 0.3, 0.5, 0.3, 0.1);
		level.playSound(null, getX(), getY(), getZ(), SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0f, 0.9f);
		discard();
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide() && !onGround() && !isNoGravity()) {
			setDeltaMovement(getDeltaMovement().add(0, -0.04, 0));
			move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
			setDeltaMovement(getDeltaMovement().scale(0.98));
		}
	}
}
