package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Cabeza de Mímir: clic derecho (en cualquier mano) para preguntarle por el tesoro más valioso ({@link MimirHead#ask}). */
public class MimirHeadItem extends RelicItem {
	public MimirHeadItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		var stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (level instanceof ServerLevel server && player instanceof ServerPlayer serverPlayer && MimirHead.ask(server, serverPlayer)) {
			RelicCooldowns.start(player, stack, MimirHead.ASK_COOLDOWN);
		}
		return InteractionResult.SUCCESS;
	}
}
