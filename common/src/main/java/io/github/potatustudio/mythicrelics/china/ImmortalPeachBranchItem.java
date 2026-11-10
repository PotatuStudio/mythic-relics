package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.registry.ModComponents;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * Rama del melocotonero celestial: da un melocotón de la inmortalidad, que tarda en madurar. Comerlo da muchos corazones
 * extra un buen rato, regenera y quita los efectos negativos. Mientras crece el siguiente, el ítem lleva el componente
 * {@link ModComponents#PEACH_GROWING} (el ícono muestra la rama sin fruto).
 */
public class ImmortalPeachBranchItem extends RelicItem {
	private static final int EAT_TICKS = 32;

	public ImmortalPeachBranchItem(Properties properties) {
		super(properties);
	}

	public static boolean ripe(ItemStack stack) {
		return !stack.has(ModComponents.PEACH_GROWING.get());
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		Long eaten = stack.get(ModComponents.PEACH_GROWING.get());
		if (eaten != null && level.getGameTime() - eaten >= ChinaGods.PEACH_GROW_TICKS) {
			stack.remove(ModComponents.PEACH_GROWING.get());
			if (entity instanceof Player player) {
				player.sendOverlayMessage(Component.translatable("message.mythicrelics.peach_ripe").withStyle(ChatFormatting.LIGHT_PURPLE));
			}
		}
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (!ripe(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.EAT;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return EAT_TICKS;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!ripe(stack) || !(level instanceof ServerLevel server)) {
			return stack;
		}
		stack.set(ModComponents.PEACH_GROWING.get(), server.getGameTime());
		List<Holder<MobEffect>> harmful = new ArrayList<>();
		for (MobEffectInstance effect : entity.getActiveEffects()) {
			if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
				harmful.add(effect.getEffect());
			}
		}
		harmful.forEach(entity::removeEffect);
		entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, ChinaGods.PEACH_ABSORPTION_TICKS, 3));
		entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, ChinaGods.PEACH_REGEN_TICKS, 1));
		server.sendParticles(ParticleTypes.HEART, entity.getX(), entity.getY() + 1.8, entity.getZ(), 6, 0.4, 0.3, 0.4, 0.0);
		server.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5f, 1.3f);
		server.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);
		return stack;
	}

	@Override
	protected void appendExtraLines(ItemStack stack, Consumer<Component> tooltip) {
		tooltip.accept(Component.translatable(ripe(stack) ? "tooltip.mythicrelics.peach_ripe" : "tooltip.mythicrelics.peach_growing")
				.withStyle(ripe(stack) ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.DARK_GRAY));
	}
}
