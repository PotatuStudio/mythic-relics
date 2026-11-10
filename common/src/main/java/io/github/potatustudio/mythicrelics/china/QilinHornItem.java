package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.entity.Qilin;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Cuerno del qilin: llama al qilin entre llamas que no queman y te sube a su lomo. Si ya hay uno tuyo cerca, te monta
 * en ese. Si murió, hay que esperar la recarga.
 */
public class QilinHornItem extends RelicItem {
	public QilinHornItem(Properties properties) {
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
		for (Qilin mine : server.getEntitiesOfClass(Qilin.class, player.getBoundingBox().inflate(32),
				q -> q.getOwnerReference() != null && q.getOwnerReference().matches(player))) {
			player.startRiding(mine);
			return InteractionResult.SUCCESS;
		}
		Qilin qilin = ModEntities.QILIN.get().create(server, EntitySpawnReason.MOB_SUMMONED);
		if (qilin == null) {
			return InteractionResult.PASS;
		}
		qilin.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0f);
		qilin.bindTo(player);
		server.addFreshEntity(qilin);
		server.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.8, player.getZ(), 40, 0.8, 0.6, 0.8, 0.03);
		server.sendParticles(ParticleTypes.WAX_ON, player.getX(), player.getY() + 1.2, player.getZ(), 20, 0.8, 0.6, 0.8, 0.05);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.HORSE_AMBIENT, SoundSource.PLAYERS, 1.0f, 0.7f);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 0.8f);
		player.startRiding(qilin);
		return InteractionResult.SUCCESS;
	}
}
