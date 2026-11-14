package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.entity.Pegasus;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Brida de oro de Atenea: llama a Pegaso y te sube a su lomo. Si ya hay uno tuyo cerca, te monta en ese. */
public class GoldenBridleItem extends RelicItem {
	public GoldenBridleItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand)) || player.isPassenger()) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		for (Pegasus mine : server.getEntitiesOfClass(Pegasus.class, player.getBoundingBox().inflate(32), p -> p.isOwnedBy(player))) {
			player.startRiding(mine);
			return InteractionResult.SUCCESS;
		}
		Pegasus pegasus = ModEntities.PEGASUS.get().create(server, EntitySpawnReason.MOB_SUMMONED);
		if (pegasus == null) {
			return InteractionResult.PASS;
		}
		pegasus.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0f);
		pegasus.setOwner(player);
		server.addFreshEntity(pegasus);
		server.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 1.5, player.getZ(), 40, 1.2, 0.6, 1.2, 0.03);
		server.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.5, player.getZ(), 30, 1.0, 0.8, 1.0, 0.05);
		RelicFx.sound(server, player, SoundEvents.HORSE_AMBIENT, 1.2f, 1.2f);
		RelicFx.sound(server, player, SoundEvents.PHANTOM_FLAP, 1.2f, 1.4f);
		player.startRiding(pegasus);
		return InteractionResult.SUCCESS;
	}
}
