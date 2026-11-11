package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Escama de Yamata no Orochi: surge a tu lado una sombra de la serpiente de ocho cabezas que muerde a los hostiles. */
public class OrochiScaleItem extends RelicItem {
	public OrochiScaleItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.SUCCESS;
		}
		if (!RelicCooldowns.tryStart(serverPlayer, "orochi_scale", JapanGods.OROCHI_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		double x = player.getX() - Math.sin(Math.toRadians(player.getYRot())) * 3;
		double z = player.getZ() + Math.cos(Math.toRadians(player.getYRot())) * 3;
		Minions.summon(server, player, ModEntities.OROCHI_SHADOW.get(), x, player.getY(), z, JapanGods.OROCHI_TICKS,
				Component.translatable("entity.mythicrelics.orochi_shadow"));
		server.sendParticles(ParticleTypes.LARGE_SMOKE, x, player.getY() + 1, z, 60, 1.5, 1.0, 1.5, 0.03);
		server.sendParticles(ParticleTypes.SOUL, x, player.getY() + 1, z, 20, 1.5, 1.0, 1.5, 0.03);
		server.playSound(null, x, player.getY(), z, SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 1.2f, 0.6f);
		return InteractionResult.SUCCESS;
	}
}
