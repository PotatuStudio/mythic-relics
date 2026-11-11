package io.github.potatustudio.mythicrelics.rome;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;

/**
 * Llave de Jano: agachado y clic derecho en una puerta, trampilla, valla o contenedor lo cierra con llave; otra vez, lo
 * abre. (Agachado, porque sin agacharse el clic abre la puerta antes de que la llave actúe.)
 */
public class JanusKeyItem extends RelicItem {
	public JanusKeyItem(Properties properties) {
		super(properties);
	}

	static boolean lockable(ServerLevel level, BlockPos pos) {
		Block block = level.getBlockState(pos).getBlock();
		return block instanceof DoorBlock || block instanceof TrapDoorBlock || block instanceof FenceGateBlock
				|| level.getBlockEntity(pos) instanceof Container;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null || !player.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		if (!(context.getLevel() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		BlockPos pos = context.getClickedPos();
		if (!lockable(level, pos)) {
			return InteractionResult.PASS;
		}
		UUID owner = JanusLocks.owner(level, pos);
		if (owner == null) {
			JanusLocks.lock(level, pos, player, context.getClickedFace(), context.getClickLocation());
			tell(player, "janus_lock", ChatFormatting.GOLD);
			level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.8f, 1.4f);
		} else if (owner.equals(player.getUUID())) {
			JanusLocks.unlock(level, pos);
			tell(player, "janus_unlock", ChatFormatting.GRAY);
			level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0.8f, 1.4f);
		} else {
			player.sendOverlayMessage(RomeRelics.lockedBy(level, owner));
		}
		return InteractionResult.SUCCESS;
	}

	private static void tell(Player player, String key, ChatFormatting color) {
		player.sendOverlayMessage(Component.translatable("message.mythicrelics." + key).withStyle(color));
	}
}
