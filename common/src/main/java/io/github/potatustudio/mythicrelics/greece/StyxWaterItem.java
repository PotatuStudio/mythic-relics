package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.registry.ModEffects;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/** Agua del Estigia: se bebe y da el Baño del Estigia. El frasco no se gasta: tiene recarga. */
public class StyxWaterItem extends RelicItem {
	private static final int DRINK_TICKS = 32;

	public StyxWaterItem(Properties properties) {
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

	/** Sorbos mientras se bebe, como una poción. */
	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
		if (remaining % 4 == 0) {
			entity.playSound(SoundEvents.GENERIC_DRINK.value(), 0.5f, level.getRandom().nextFloat() * 0.1f + 0.9f);
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (entity instanceof ServerPlayer player && level instanceof ServerLevel server
				&& RelicCooldowns.tryStart(player, "styx_water", GreeceHeroes.STYX_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			player.addEffect(new MobEffectInstance(ModEffects.STYX_BATH.asHolder(), GreeceHeroes.STYX_TICKS, 0));
			RelicFx.sound(server, player, SoundEvents.GENERIC_DRINK.value(), 0.8f, 0.7f);
			server.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1.0, player.getZ(), 12, 0.3, 0.5, 0.3, 0.02);
		}
		return stack;
	}
}
