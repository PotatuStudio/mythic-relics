package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;

/**
 * Elixir de Chang'e: el elixir de la inmortalidad que la volvió ligera y la llevó flotando hasta la Luna. Beberlo da el
 * ascenso lunar: saltas muy alto, caes despacio sin daño y tus golpes en el aire hacen más daño ({@link ChinaRelics}).
 * Dura más con luna llena y menos con luna nueva. No se gasta.
 */
public class ChangeElixirItem extends RelicItem {
	private static final int DRINK_TICKS = 24;

	public ChangeElixirItem(Properties properties) {
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
		if (!(entity instanceof ServerPlayer player) || !(level instanceof ServerLevel server)
				|| !RelicCooldowns.tryStart(player, "change_elixir", ChinaRelics.ELIXIR_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return stack;
		}
		MoonPhase phase = server.environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, player.position());
		int ticks = ChinaRelics.lunarSeconds(phase) * 20;
		player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, ticks, 2));
		player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, ticks, 0));
		ChinaRelics.startLunar(player, ticks);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.5f, 1.4f);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK.value(), SoundSource.PLAYERS, 0.8f, 1.2f);
		server.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.4, 0.6, 0.4, 0.05);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.change_elixir",
				Component.translatable("moon_phase.mythicrelics." + phase.getSerializedName()), ticks / 20).withStyle(ChatFormatting.AQUA));
		return stack;
	}
}
