package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Hoja del tanuki: te la pones en la cabeza y te transformas; los hostiles no te ven mientras no ataques. */
public class TanukiLeafItem extends RelicItem {
	public TanukiLeafItem(Properties properties) {
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
		if (!RelicCooldowns.tryStart(serverPlayer, "tanuki_leaf", JapanGods.TANUKI_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		JapanGods.disguise(serverPlayer);
		server.sendParticles(ParticleTypes.POOF, player.getX(), player.getY() + 1, player.getZ(), 30, 0.5, 0.8, 0.5, 0.05);
		server.sendParticles(ParticleTypes.COMPOSTER, player.getX(), player.getY() + 2, player.getZ(), 10, 0.3, 0.2, 0.3, 0.0);
		RelicFx.sound(server, player, SoundEvents.FOX_SNIFF, 1.0f, 1.5f);
		RelicFx.notify(player, "tanuki_leaf", ChatFormatting.GREEN);
		return InteractionResult.SUCCESS;
	}
}
