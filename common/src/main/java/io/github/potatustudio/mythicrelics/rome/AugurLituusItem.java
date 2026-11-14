package io.github.potatustudio.mythicrelics.rome;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Lítuo del augur: tomar los auspicios. Dos de cada tres veces son favorables (Suerte y Prisa); si no, Mala suerte. Bajo
 * el cielo abierto con aves cerca (loros o pollos) siempre son favorables: Rómulo vio sus doce buitres.
 */
public class AugurLituusItem extends RelicItem {
	public AugurLituusItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		if (!RelicCooldowns.tryStart(serverPlayer, "augur_lituus", RomeRelics.LITUUS_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		boolean birds = server.canSeeSky(BlockPos.containing(player.getEyePosition()))
				&& !server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(RomeRelics.LITUUS_BIRD_RADIUS),
						e -> e.getType() == EntityTypes.PARROT || e.getType() == EntityTypes.CHICKEN).isEmpty();
		boolean favorable = birds || server.getRandom().nextInt(3) < 2;
		if (favorable) {
			player.addEffect(new MobEffectInstance(MobEffects.LUCK, RomeRelics.LITUUS_FAVOR_TICKS, 1));
			player.addEffect(new MobEffectInstance(MobEffects.HASTE, RomeRelics.LITUUS_FAVOR_TICKS, 0));
			server.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 2.2, player.getZ(), 16, 0.8, 0.3, 0.8, 0.0);
			RelicFx.sound(server, player, SoundEvents.PARROT_AMBIENT, 1.0f, 1.0f);
			RelicFx.notify(serverPlayer, "augury_good", ChatFormatting.GOLD);
		} else {
			player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, RomeRelics.LITUUS_BAD_TICKS, 0));
			server.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 2.2, player.getZ(), 16, 0.8, 0.3, 0.8, 0.0);
			RelicFx.sound(server, player, SoundEvents.PHANTOM_AMBIENT, 0.6f, 1.2f);
			RelicFx.notify(serverPlayer, "augury_bad", ChatFormatting.DARK_GRAY);
		}
		return InteractionResult.SUCCESS;
	}
}
