package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * Gjallarhorn: tocarlo (mantener clic derecho) da Fuerza y Velocidad a ti y a tus aliados cercanos, y hace brillar a
 * los hostiles en un radio amplio: Heimdall todo lo ve.
 */
public class GjallarhornItem extends RelicItem {
	public GjallarhornItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.TOOT_HORN;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return NorseRelics.HORN_USE_TICKS;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!(entity instanceof ServerPlayer player) || !(level instanceof ServerLevel server)
				|| !RelicCooldowns.tryStart(player, "gjallarhorn", NorseRelics.HORN_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return stack;
		}
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RAID_HORN.value(), SoundSource.PLAYERS, 2.0f, 1.2f);
		server.sendParticles(ParticleTypes.NOTE, player.getX(), player.getY() + 2.2, player.getZ(), 12, 0.6, 0.3, 0.6, 1.0);
		double allies = NorseRelics.HORN_ALLY_RADIUS;
		for (LivingEntity ally : server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(allies),
				e -> e.isAlive() && e.distanceToSqr(player) <= allies * allies
						&& (e instanceof Player || e instanceof TamableAnimal pet && pet.isOwnedBy(player)))) {
			ally.addEffect(new MobEffectInstance(MobEffects.STRENGTH, NorseRelics.HORN_BUFF_TICKS, 0));
			ally.addEffect(new MobEffectInstance(MobEffects.SPEED, NorseRelics.HORN_BUFF_TICKS, 0));
		}
		for (LivingEntity foe : server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(NorseRelics.HORN_SIGHT_RADIUS),
				e -> e instanceof Enemy && e.isAlive())) {
			foe.addEffect(new MobEffectInstance(MobEffects.GLOWING, NorseRelics.HORN_GLOW_TICKS, 0, false, false));
		}
		return stack;
	}
}
