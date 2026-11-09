package io.github.potatustudio.mythicrelics.egypt;

import io.github.potatustudio.mythicrelics.registry.ModComponents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Paleta de escriba de Thot: clic derecho escribe en la paleta los puntos de un nivel de tu experiencia; agachado y clic
 * derecho los lee de vuelta, de a un nivel. Guarda <b>puntos</b>, no niveles: un nivel alto vale muchos más puntos que uno
 * bajo, y guardar niveles permitiría escribir barato y leer caro. Cabe lo de los niveles 0 a 30 ({@link
 * EgyptGods#THOTH_MAX_POINTS}). Lo escrito queda en el ítem, así que no se pierde al morir: cae con la paleta.
 */
public class ThothPaletteItem extends RelicItem {
	public ThothPaletteItem(Properties properties) {
		super(properties);
	}

	public static int written(ItemStack stack) {
		return stack.getOrDefault(ModComponents.WRITTEN_POINTS.get(), 0);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		int written = written(stack);
		boolean read = player.isShiftKeyDown();
		// Escribir: los puntos del último nivel que tienes. Leer: los de un nivel a tu nivel actual (o lo que quede).
		int points = read ? Math.min(written, EgyptGods.pointsForLevel(player.experienceLevel))
				: Math.min(EgyptGods.pointsForLevel(Math.max(0, player.experienceLevel - 1)), EgyptGods.THOTH_MAX_POINTS - written);
		if (points <= 0 || !read && player.experienceLevel <= 0) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide()) {
			if (read) {
				EgyptGods.giveWithoutThothBonus(player, points);
			} else {
				player.giveExperiencePoints(-points);
			}
			stack.set(ModComponents.WRITTEN_POINTS.get(), written + (read ? -points : points));
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
					read ? SoundEvents.EXPERIENCE_ORB_PICKUP : SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.8f, read ? 1.2f : 0.9f);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return written(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(13.0f * written(stack) / EgyptGods.THOTH_MAX_POINTS);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0x7CD8FF;
	}

	@Override
	protected void appendExtraLines(ItemStack stack, Consumer<Component> tooltip) {
		tooltip.accept(Component.translatable("tooltip.mythicrelics.thoth_palette.written", written(stack), EgyptGods.THOTH_MAX_POINTS)
				.withStyle(ChatFormatting.AQUA));
	}
}
