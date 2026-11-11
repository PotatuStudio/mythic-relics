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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Esfera estelar (hoshi no tama) del kitsune: llama al zorro de nueve colas, que pelea a tu lado un buen rato. */
public class KitsuneStarBallItem extends RelicItem {
	public KitsuneStarBallItem(Properties properties) {
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
		if (!RelicCooldowns.tryStart(serverPlayer, "kitsune_star_ball", JapanRelics.KITSUNE_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		double x = player.getX() - Math.sin(Math.toRadians(player.getYRot())) * 2;
		double z = player.getZ() + Math.cos(Math.toRadians(player.getYRot())) * 2;
		Mob fox = Minions.summon(server, player, ModEntities.KITSUNE.get(), x, player.getY(), z, JapanRelics.KITSUNE_TICKS,
				Component.translatable("entity.mythicrelics.kitsune"));
		if (fox != null) {
			server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, player.getY() + 0.8, z, 30, 0.5, 0.5, 0.5, 0.03);
			server.sendParticles(ParticleTypes.END_ROD, x, player.getY() + 1.0, z, 12, 0.4, 0.4, 0.4, 0.05);
		}
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FOX_SCREECH, SoundSource.PLAYERS, 0.8f, 1.2f);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 0.7f);
		return InteractionResult.SUCCESS;
	}
}
