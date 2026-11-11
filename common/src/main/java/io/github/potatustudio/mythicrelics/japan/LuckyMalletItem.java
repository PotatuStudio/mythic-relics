package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mazo de la suerte (uchide no kozuchi): con él, el diminuto Issun-bōshi creció hasta ser un hombre. Clic derecho te
 * agranda un rato ({@link JapanRelics#grow}); agachado, los cultivos y las crías cercanas crecen al instante.
 */
public class LuckyMalletItem extends RelicItem {
	public LuckyMalletItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.SUCCESS;
		}
		if (player.isShiftKeyDown()) {
			prosper(server, player);
			player.getCooldowns().addCooldown(stack, JapanRelics.MALLET_GROW_COOLDOWN);
			return InteractionResult.SUCCESS;
		}
		if (!RelicCooldowns.tryStart(serverPlayer, "lucky_mallet", JapanRelics.MALLET_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		JapanRelics.grow(serverPlayer);
		server.sendParticles(ParticleTypes.WAX_ON, player.getX(), player.getY() + 1.5, player.getZ(), 30, 0.6, 0.8, 0.6, 0.1);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.4f, 1.6f);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 0.6f);
		return InteractionResult.SUCCESS;
	}

	/** Prosperidad: los cultivos maduran y las crías crecen. */
	private static void prosper(ServerLevel level, Player player) {
		int r = JapanRelics.MALLET_GROW_RADIUS;
		BlockPos center = player.blockPosition();
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -2, -r), center.offset(r, 2, r))) {
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state)) {
				level.setBlock(pos, crop.getStateForAge(crop.getMaxAge()), 2);
				level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 3, 0.3, 0.2, 0.3, 0.0);
			}
		}
		for (AgeableMob baby : level.getEntitiesOfClass(AgeableMob.class, player.getBoundingBox().inflate(r), m -> m.getAge() < 0)) {
			baby.setAge(0);
			level.sendParticles(ParticleTypes.HAPPY_VILLAGER, baby.getX(), baby.getY() + 0.6, baby.getZ(), 5, 0.3, 0.3, 0.3, 0.0);
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
	}
}
