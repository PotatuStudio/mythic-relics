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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/** Óðrerir: beber el hidromiel de la poesía da experiencia y Prisa. No se gasta: una vez por día de Minecraft. */
public class OdrerirItem extends RelicItem {
	private static final int DRINK_TICKS = 32;

	public OdrerirItem(Properties properties) {
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
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return DRINK_TICKS;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.DRINK;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (entity instanceof ServerPlayer player && level instanceof ServerLevel server
				&& RelicCooldowns.tryStart(player, "odrerir", NorseGods.ODRERIR_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			player.giveExperienceLevels(NorseGods.ODRERIR_LEVELS);
			player.addEffect(new MobEffectInstance(MobEffects.HASTE, NorseGods.ODRERIR_HASTE_TICKS, 0));
			server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK.value(), SoundSource.PLAYERS, 0.8f, 0.9f);
			server.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.5, player.getZ(), 30, 0.4, 0.5, 0.4, 0.5);
		}
		return stack;
	}
}
