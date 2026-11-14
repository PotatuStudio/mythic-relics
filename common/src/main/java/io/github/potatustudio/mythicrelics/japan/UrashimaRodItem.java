package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.entity.UrashimaTurtle;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Caña de Urashima: desde el agua, llama a la tortuga gigante que llevó al pescador al palacio del Rey Dragón y te sube
 * a su caparazón. Si ya hay una tuya cerca, te monta en esa. Si murió, hay que esperar la recarga.
 */
public class UrashimaRodItem extends RelicItem {
	public UrashimaRodItem(Properties properties) {
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
		for (UrashimaTurtle mine : server.getEntitiesOfClass(UrashimaTurtle.class, player.getBoundingBox().inflate(32), t -> t.isOwnedBy(player))) {
			player.startRiding(mine);
			return InteractionResult.SUCCESS;
		}
		boolean water = player.isInWater() || server.getFluidState(player.blockPosition().below()).is(FluidTags.WATER);
		if (!water) {
			RelicFx.notify(player, "urashima_need_water", ChatFormatting.AQUA);
			return InteractionResult.FAIL;
		}
		UrashimaTurtle turtle = ModEntities.URASHIMA_TURTLE.get().create(server, EntitySpawnReason.MOB_SUMMONED);
		if (turtle == null) {
			return InteractionResult.PASS;
		}
		turtle.snapTo(player.getX(), player.getY() - 0.5, player.getZ(), player.getYRot(), 0.0f);
		turtle.setOwner(player);
		server.addFreshEntity(turtle);
		server.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, player.getX(), player.getY(), player.getZ(), 40, 1.0, 0.5, 1.0, 0.1);
		server.sendParticles(ParticleTypes.SPLASH, player.getX(), player.getY() + 0.5, player.getZ(), 30, 1.0, 0.3, 1.0, 0.1);
		RelicFx.sound(server, player, SoundEvents.TURTLE_AMBIENT_LAND, 1.0f, 0.6f);
		player.startRiding(turtle);
		return InteractionResult.SUCCESS;
	}
}
