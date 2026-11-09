package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.registry.ModComponents;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * Manzanas de Iðunn: la cesta guarda hasta {@link NorseRelics#IDUNN_MAX} manzanas y crece una por cada día de Minecraft.
 * Comer una cura y quita los males del envejecimiento. Las manzanas que quedan y el último día se guardan en el ítem.
 */
public class IdunnApplesItem extends RelicItem {
	private static final int EAT_TICKS = 32;
	private static final List<Holder<MobEffect>> AGING = List.of(MobEffects.POISON, MobEffects.WITHER, MobEffects.SLOWNESS,
			MobEffects.WEAKNESS, MobEffects.HUNGER);

	public IdunnApplesItem(Properties properties) {
		super(properties);
	}

	public static int apples(ItemStack stack) {
		return stack.getOrDefault(ModComponents.IDUNN_APPLES.get(), NorseRelics.IDUNN_MAX);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		long day = level.getServer().overworld().getOverworldClockTime() / 24000L;
		Long last = stack.get(ModComponents.IDUNN_DAY.get());
		if (last == null) {
			stack.set(ModComponents.IDUNN_DAY.get(), day);
		} else if (day > last) {
			stack.set(ModComponents.IDUNN_APPLES.get(), (int) Math.min(NorseRelics.IDUNN_MAX, apples(stack) + (day - last)));
			stack.set(ModComponents.IDUNN_DAY.get(), day);
		}
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (apples(player.getItemInHand(hand)) <= 0) {
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
		int left = apples(stack);
		if (left <= 0 || !(level instanceof ServerLevel server)) {
			return stack;
		}
		stack.set(ModComponents.IDUNN_APPLES.get(), left - 1);
		List<Holder<MobEffect>> remove = new ArrayList<>();
		for (MobEffectInstance effect : entity.getActiveEffects()) {
			if (AGING.contains(effect.getEffect())) {
				remove.add(effect.getEffect());
			}
		}
		remove.forEach(entity::removeEffect);
		entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, NorseRelics.IDUNN_REGEN_TICKS, 1));
		entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, NorseRelics.IDUNN_ABSORPTION_TICKS, 0));
		server.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5f, 1.2f);
		return stack;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return apples(stack) < NorseRelics.IDUNN_MAX;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(13.0f * apples(stack) / NorseRelics.IDUNN_MAX);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0xF5C842;
	}

	@Override
	protected void appendExtraLines(ItemStack stack, Consumer<Component> tooltip) {
		tooltip.accept(Component.translatable("tooltip.mythicrelics.idunn_apples.left", apples(stack), NorseRelics.IDUNN_MAX)
				.withStyle(ChatFormatting.YELLOW));
	}
}
