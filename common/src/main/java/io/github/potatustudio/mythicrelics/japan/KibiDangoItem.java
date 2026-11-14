package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.mixin.FoxAccessor;
import io.github.potatustudio.mythicrelics.registry.ModComponents;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * Kibi dango de Momotarō: las bolitas de mijo con que se ganó al perro, al mono y al faisán. Dar una a un lobo, zorro o
 * loro salvaje lo vuelve tu compañero al instante; comer una da Fuerza. La bolsa tiene tres y se rellena una por día.
 */
public class KibiDangoItem extends RelicItem {
	private static final int EAT_TICKS = 24;

	public KibiDangoItem(Properties properties) {
		super(properties);
	}

	public static int left(ItemStack stack) {
		return stack.getOrDefault(ModComponents.KIBI_DANGO.get(), JapanRelics.KIBI_MAX);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		long day = level.getServer().overworld().getOverworldClockTime() / 24000L;
		Long last = stack.get(ModComponents.KIBI_DAY.get());
		if (last == null) {
			stack.set(ModComponents.KIBI_DAY.get(), day);
		} else if (day > last) {
			stack.set(ModComponents.KIBI_DANGO.get(), (int) Math.min(JapanRelics.KIBI_MAX, left(stack) + (day - last)));
			stack.set(ModComponents.KIBI_DAY.get(), day);
		}
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack ignored, Player player, LivingEntity target, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (left(stack) <= 0) {
			return InteractionResult.PASS;
		}
		boolean wild = target instanceof Wolf wolf && !wolf.isTame() || target instanceof Parrot parrot && !parrot.isTame()
				|| target instanceof Fox;
		if (!wild) {
			return InteractionResult.PASS;
		}
		if (!(player.level() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		if (target instanceof Wolf wolf) {
			wolf.tame(player);
			wolf.setOrderedToSit(false);
		} else if (target instanceof Parrot parrot) {
			parrot.tame(player);
		} else if (target instanceof Fox fox) {
			((FoxAccessor) fox).mythicrelics$addTrustedEntity(player);
		}
		stack.set(ModComponents.KIBI_DANGO.get(), left(stack) - 1);
		level.sendParticles(ParticleTypes.HEART, target.getX(), target.getY() + target.getBbHeight() + 0.3, target.getZ(), 6, 0.3, 0.2, 0.3, 0.0);
		level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GENERIC_EAT.value(), SoundSource.NEUTRAL, 0.8f, 1.2f);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.kibi_dango", target.getDisplayName()).withStyle(ChatFormatting.GOLD));
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (left(player.getItemInHand(hand)) <= 0) {
			return InteractionResult.PASS;
		}
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	/** Mordiscos (o sorbos) y migas mientras se usa, como la comida vanilla. */
	@Override
	public void onUseTick(net.minecraft.world.level.Level level, net.minecraft.world.entity.LivingEntity entity, ItemStack stack, int remaining) {
		io.github.potatustudio.mythicrelics.relic.ConsumeFeedback.tick(level, entity, stack, remaining, getUseDuration(stack, entity), false);
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
		int left = left(stack);
		if (left <= 0 || !(level instanceof ServerLevel server)) {
			return stack;
		}
		stack.set(ModComponents.KIBI_DANGO.get(), left - 1);
		entity.addEffect(new MobEffectInstance(MobEffects.STRENGTH, JapanRelics.KIBI_STRENGTH_TICKS, 0));
		RelicFx.sound(server, entity, SoundEvents.PLAYER_BURP, 0.5f, 1.3f);
		return stack;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return left(stack) < JapanRelics.KIBI_MAX;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(13.0f * left(stack) / JapanRelics.KIBI_MAX);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0xE6C66A;
	}

	@Override
	protected void appendExtraLines(ItemStack stack, Consumer<Component> tooltip) {
		tooltip.accept(Component.translatable("tooltip.mythicrelics.kibi_dango.left", left(stack), JapanRelics.KIBI_MAX)
				.withStyle(ChatFormatting.YELLOW));
	}
}
