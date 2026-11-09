package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Cabeza de Medusa: clic derecho petrifica a los mobs que te miran. Con el set de Perseo recarga a la mitad. */
public class MedusaHeadItem extends RelicItem {
	public MedusaHeadItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (player instanceof ServerPlayer serverPlayer) {
			int cooldown = GreeceRelics.hasPerseusSet(player) ? GreeceRelics.MEDUSA_COOLDOWN / 2 : GreeceRelics.MEDUSA_COOLDOWN;
			if (!RelicCooldowns.tryStart(serverPlayer, "medusa_head", cooldown, BuiltInRegistries.ITEM.getKey(this))) {
				return InteractionResult.PASS;
			}
			GreeceRelics.petrify(serverPlayer);
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS,
					0.5f, 1.5f);
		}
		return InteractionResult.SUCCESS;
	}
}
