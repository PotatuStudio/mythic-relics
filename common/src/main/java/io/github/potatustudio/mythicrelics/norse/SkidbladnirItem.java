package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Skíðblaðnir: clic derecho mirando al agua despliega un bote y te sube a él; al bajarte se pliega (lo hace
 * {@link NorseGods}). El ítem no se gasta. El viento a favor lo da el cliente al conducir con el ítem encima.
 */
public class SkidbladnirItem extends RelicItem {
	public SkidbladnirItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.isPassenger()) {
			return InteractionResult.PASS;
		}
		BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (hit.getType() != HitResult.Type.BLOCK || !level.getFluidState(hit.getBlockPos()).is(FluidTags.WATER)) {
			return InteractionResult.PASS;
		}
		if (level instanceof ServerLevel server) {
			AbstractBoat boat = io.github.potatustudio.mythicrelics.registry.ModEntities.SKIDBLADNIR_BOAT.get().create(server, EntitySpawnReason.SPAWN_ITEM_USE);
			if (boat == null) {
				return InteractionResult.PASS;
			}
			boat.snapTo(hit.getLocation().x, hit.getBlockPos().getY() + 1.0, hit.getLocation().z, player.getYRot(), 0.0f);
			server.addFreshEntity(boat);
			NorseGods.launched(boat);
			server.sendParticles(ParticleTypes.CLOUD, boat.getX(), boat.getY() + 0.5, boat.getZ(), 12, 0.5, 0.2, 0.5, 0.02);
			server.playSound(null, boat.getX(), boat.getY(), boat.getZ(), SoundEvents.BOAT_PADDLE_WATER, SoundSource.PLAYERS, 1.0f, 1.0f);
			player.startRiding(boat);
		}
		return InteractionResult.SUCCESS;
	}
}
