package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Ame-no-nuboko, la lanza enjoyada con que Izanagi e Izanami revolvieron el mar: mirando al agua, de la punta caen gotas
 * y brota una islita de arena con pasto en el centro.
 */
public class AmeNoNubokoItem extends RelicItem {
	private static final int DEPTH = 4;

	public AmeNoNubokoItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		BlockHitResult hit = server.clip(new ClipContext(player.getEyePosition(),
				player.getEyePosition().add(player.getLookAngle().scale(JapanGods.NUBOKO_RANGE)), ClipContext.Block.OUTLINE,
				ClipContext.Fluid.SOURCE_ONLY, player));
		if (hit.getType() != HitResult.Type.BLOCK || !server.getFluidState(hit.getBlockPos()).is(FluidTags.WATER)) {
			return InteractionResult.FAIL;
		}
		BlockPos surface = hit.getBlockPos();
		while (server.getFluidState(surface.above()).is(FluidTags.WATER)) {
			surface = surface.above();
		}
		int r = JapanGods.NUBOKO_RADIUS;
		for (int dx = -r; dx <= r; dx++) {
			for (int dz = -r; dz <= r; dz++) {
				double d = Math.sqrt(dx * dx + dz * dz);
				if (d > r + 0.5) {
					continue;
				}
				int top = d < r - 0.5 ? 1 : 0;
				for (int dy = -DEPTH; dy <= top; dy++) {
					BlockPos p = surface.offset(dx, dy, dz);
					BlockState old = server.getBlockState(p);
					if (!old.canBeReplaced() && !old.getFluidState().is(FluidTags.WATER)) {
						continue;
					}
					BlockState put = dy == top && d < r - 1.0 ? Blocks.GRASS_BLOCK.defaultBlockState()
							: dy < -1 ? Blocks.DIRT.defaultBlockState() : Blocks.SAND.defaultBlockState();
					server.setBlockAndUpdate(p, put);
				}
			}
		}
		player.getCooldowns().addCooldown(stack, JapanGods.NUBOKO_COOLDOWN);
		double cx = surface.getX() + 0.5, cy = surface.getY() + 1.2, cz = surface.getZ() + 0.5;
		server.sendParticles(ParticleTypes.SPLASH, cx, cy, cz, 80, r, 0.4, r, 0.2);
		server.sendParticles(ParticleTypes.WAX_ON, cx, cy + 1, cz, 20, r * 0.5, 0.6, r * 0.5, 0.05);
		server.playSound(null, cx, cy, cz, SoundEvents.GRAVEL_PLACE, SoundSource.PLAYERS, 1.5f, 0.6f);
		RelicFx.sound(server, player, SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 0.8f);
		return InteractionResult.SUCCESS;
	}
}
