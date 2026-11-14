package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
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

/** Clavo de bronce de Talos: el gigante de bronce de Creta aparece a tu lado y pelea por ti un buen rato. */
public class TalosNailItem extends RelicItem {
	public TalosNailItem(Properties properties) {
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
		if (!RelicCooldowns.tryStart(serverPlayer, "talos_nail", GreeceLegends.TALOS_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		double x = player.getX() - Math.sin(Math.toRadians(player.getYRot())) * 2.5;
		double z = player.getZ() + Math.cos(Math.toRadians(player.getYRot())) * 2.5;
		Minions.summon(server, player, ModEntities.TALOS.get(), x, player.getY(), z, GreeceLegends.TALOS_TICKS,
				Component.translatable("entity.mythicrelics.talos"));
		server.sendParticles(ParticleTypes.LAVA, x, player.getY() + 1.5, z, 20, 0.8, 1.2, 0.8, 0.0);
		server.sendParticles(ParticleTypes.LARGE_SMOKE, x, player.getY() + 1.5, z, 40, 0.8, 1.2, 0.8, 0.02);
		server.playSound(null, x, player.getY(), z, SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 1.2f, 0.5f);
		server.playSound(null, x, player.getY(), z, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1.2f, 0.6f);
		return InteractionResult.SUCCESS;
	}
}
