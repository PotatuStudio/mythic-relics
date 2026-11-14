package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.entity.Sleipnir;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Herradura de Sleipnir: llama a Sleipnir entre rayos (solo visuales) y te sube a su lomo. Si ya hay uno tuyo cerca,
 * te monta en ese. Si murió, hay que esperar la recarga.
 */
public class SleipnirHorseshoeItem extends RelicItem {
	public SleipnirHorseshoeItem(Properties properties) {
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
		for (Sleipnir mine : server.getEntitiesOfClass(Sleipnir.class, player.getBoundingBox().inflate(32),
				s -> s.getOwnerReference() != null && s.getOwnerReference().matches(player))) {
			player.startRiding(mine);
			return InteractionResult.SUCCESS;
		}
		Sleipnir horse = ModEntities.SLEIPNIR.get().create(server, EntitySpawnReason.MOB_SUMMONED);
		if (horse == null) {
			return InteractionResult.PASS;
		}
		horse.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0f);
		horse.bindTo(player);
		server.addFreshEntity(horse);
		LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(server, EntitySpawnReason.TRIGGERED);
		if (bolt != null) {
			bolt.setVisualOnly(true);
			bolt.snapTo(player.getX() + 2.0, player.getY(), player.getZ());
			server.addFreshEntity(bolt);
		}
		RelicFx.sound(server, player, SoundEvents.HORSE_AMBIENT, 1.0f, 0.7f);
		player.startRiding(horse);
		return InteractionResult.SUCCESS;
	}
}
