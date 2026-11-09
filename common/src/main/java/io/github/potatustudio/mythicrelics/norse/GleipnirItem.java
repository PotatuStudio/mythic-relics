package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Gleipnir: clic derecho a un mob lo ata unos segundos (no se mueve ni ataca). No ata a los jefes. */
public class GleipnirItem extends RelicItem {
	public GleipnirItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		if (!(target instanceof Mob mob) || GreeceRelics.isBoss(mob) || player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (player instanceof ServerPlayer serverPlayer && player.level() instanceof ServerLevel level) {
			if (!RelicCooldowns.tryStart(serverPlayer, "gleipnir", NorseRelics.GLEIPNIR_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
				return InteractionResult.PASS;
			}
			NorseRelics.bind(level, mob);
			level.sendParticles(ParticleTypes.END_ROD, mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(), 20,
					mob.getBbWidth() * 0.6, mob.getBbHeight() * 0.4, mob.getBbWidth() * 0.6, 0.02);
			level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.LEAD_TIED, SoundSource.PLAYERS, 1.0f, 0.8f);
		}
		return InteractionResult.SUCCESS;
	}
}
