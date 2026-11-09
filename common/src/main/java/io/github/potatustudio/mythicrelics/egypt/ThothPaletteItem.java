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
 * Paleta de escriba de Thot: clic derecho escribe un nivel de experiencia en la paleta (hasta {@link EgyptGods#THOTH_MAX_LEVELS});
 * agachado y clic derecho lo lee de vuelta. Lo escrito queda en el ítem, así que no se pierde al morir: cae con la
 * paleta. La barra del ítem muestra cuánto lleva escrito.
 */
public class ThothPaletteItem extends RelicItem {
	public ThothPaletteItem(Properties properties) {
		super(properties);
	}

	public static int written(ItemStack stack) {
		return stack.getOrDefault(ModComponents.WRITTEN_LEVELS.get(), 0);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		int written = written(stack);
		boolean read = player.isShiftKeyDown();
		if (read ? written <= 0 : (written >= EgyptGods.THOTH_MAX_LEVELS || player.experienceLevel <= 0)) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide()) {
			player.giveExperienceLevels(read ? 1 : -1);
			stack.set(ModComponents.WRITTEN_LEVELS.get(), written + (read ? -1 : 1));
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
		return Math.round(13.0f * written(stack) / EgyptGods.THOTH_MAX_LEVELS);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0x7CD8FF;
	}

	@Override
	protected void appendExtraLines(ItemStack stack, Consumer<Component> tooltip) {
		tooltip.accept(Component.translatable("tooltip.mythicrelics.thoth_palette.written", written(stack), EgyptGods.THOTH_MAX_LEVELS)
				.withStyle(ChatFormatting.AQUA));
	}
}
