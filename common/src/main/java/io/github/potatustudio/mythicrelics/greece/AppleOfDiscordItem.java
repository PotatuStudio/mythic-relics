package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Manzana de la Discordia: «para la más bella». Clic derecho apuntando a un mob (hasta 16 bloques): queda marcado y
 * brilla, y los hostiles cercanos se pelean por él y lo atacan (no sirve con jefes).
 */
public class AppleOfDiscordItem extends RelicItem {
	public AppleOfDiscordItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		Mob target = aimed(server, player);
		if (target == null) {
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.apple_no_target").withStyle(ChatFormatting.GRAY));
			return InteractionResult.PASS;
		}
		GreeceLegends.markFairest(target, server.getGameTime() + GreeceLegends.APPLE_TICKS);
		server.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, target.getX(), target.getY(0.7), target.getZ(), 30, 0.4, 0.5, 0.4, 0.2);
		server.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.5f, 0.8f);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.8f, 0.7f);
		player.getCooldowns().addCooldown(stack, GreeceLegends.APPLE_COOLDOWN);
		return InteractionResult.SUCCESS;
	}

	private static Mob aimed(ServerLevel level, Player player) {
		Vec3 eyes = player.getEyePosition();
		Vec3 end = eyes.add(player.getLookAngle().scale(GreeceLegends.APPLE_RANGE));
		Mob best = null;
		double bestDist = Double.MAX_VALUE;
		for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().expandTowards(end.subtract(eyes)).inflate(1.0),
				m -> m.isAlive() && !GreeceLegends.isBoss(m) && !ChinaRelics.isFriend(player, m) && player.hasLineOfSight(m))) {
			var hit = mob.getBoundingBox().inflate(0.5).clip(eyes, end);
			if (hit.isPresent() && hit.get().distanceToSqr(eyes) < bestDist) {
				bestDist = hit.get().distanceToSqr(eyes);
				best = mob;
			}
		}
		return best;
	}
}
