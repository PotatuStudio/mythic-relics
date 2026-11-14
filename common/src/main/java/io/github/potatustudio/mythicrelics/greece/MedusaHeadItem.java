package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * Cabeza de Medusa: se sostiene como un escudo (clic derecho mantenido, sin recarga). Mientras la alzas, sus ojos
 * abren un cono de mirada y todo mob que se quede en él unos instantes se petrifica ({@link GreeceRelics#gaze}).
 */
public class MedusaHeadItem extends RelicItem {
	public MedusaHeadItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		if (!level.isClientSide()) {
			RelicFx.sound(level, player, SoundEvents.ELDER_GUARDIAN_CURSE, 0.35f, 1.6f);
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
		if (entity instanceof ServerPlayer player) {
			GreeceRelics.gaze(player);
			if (remaining % 30 == 0) {
				RelicFx.sound(level, player, SoundEvents.ELDER_GUARDIAN_AMBIENT, 0.25f, 1.8f);
			}
		}
	}
}
