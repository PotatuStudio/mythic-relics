package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.entity.LongDragon;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Perla del dragón: llama a tu long, que baja de las nubes y te sube a su lomo. Si ya hay uno tuyo cerca, te monta en
 * ese. Montado, usar la perla llama a la lluvia o la detiene: los dragones chinos gobiernan la lluvia.
 */
public class DragonPearlItem extends RelicItem {
	private static final int RAIN_TICKS = 6000;
	private static final int RAIN_TOGGLE_COOLDOWN = 100;

	public DragonPearlItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		if (player.getVehicle() instanceof LongDragon dragon && dragon.isOwnedBy(player)) {
			toggleRain(server, player);
			RelicCooldowns.start(player, player.getItemInHand(hand), RAIN_TOGGLE_COOLDOWN);
			return InteractionResult.SUCCESS;
		}
		if (player.isPassenger()) {
			return InteractionResult.PASS;
		}
		for (LongDragon mine : server.getEntitiesOfClass(LongDragon.class, player.getBoundingBox().inflate(32), d -> d.isOwnedBy(player))) {
			player.startRiding(mine);
			return InteractionResult.SUCCESS;
		}
		LongDragon dragon = ModEntities.LONG_DRAGON.get().create(server, EntitySpawnReason.MOB_SUMMONED);
		if (dragon == null) {
			return InteractionResult.PASS;
		}
		dragon.snapTo(player.getX(), player.getY() + 0.5, player.getZ(), player.getYRot(), 0.0f);
		dragon.setOwner(player);
		server.addFreshEntity(dragon);
		server.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 2.5, player.getZ(), 60, 2.0, 0.8, 2.0, 0.02);
		server.sendParticles(ParticleTypes.FALLING_WATER, player.getX(), player.getY() + 3.0, player.getZ(), 40, 2.0, 0.5, 2.0, 0.0);
		RelicFx.sound(server, player, SoundEvents.ENDER_DRAGON_GROWL, 0.6f, 1.5f);
		player.startRiding(dragon);
		return InteractionResult.SUCCESS;
	}

	/** Igual que /weather: llama a la lluvia o la detiene en todo el mundo. */
	private static void toggleRain(ServerLevel level, Player player) {
		boolean raining = level.getServer().overworld().isRaining();
		if (raining) {
			level.getServer().setWeatherParameters(RAIN_TICKS, 0, false, false);
		} else {
			level.getServer().setWeatherParameters(0, RAIN_TICKS, true, false);
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 1.0f, 0.8f);
		player.sendOverlayMessage(Component.translatable(raining ? "message.mythicrelics.dragon_rain_stop" : "message.mythicrelics.dragon_rain")
				.withStyle(ChatFormatting.AQUA));
	}
}
