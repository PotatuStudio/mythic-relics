package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.relic.Bosses;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
		if (!(target instanceof Mob mob) || player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (player instanceof ServerPlayer serverPlayer && player.level() instanceof ServerLevel level) {
			if (!RelicCooldowns.tryStart(serverPlayer, "gleipnir", NorseRelics.GLEIPNIR_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
				return InteractionResult.PASS;
			}
			if (Bosses.is(mob)) {
				// A los jefes no los ata: los frena mucho un rato.
				mob.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, NorseRelics.GLEIPNIR_BOSS_TICKS, 3));
			} else {
				NorseRelics.bind(level, mob);
			}
			level.sendParticles(ParticleTypes.END_ROD, mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(), 20,
					mob.getBbWidth() * 0.6, mob.getBbHeight() * 0.4, mob.getBbWidth() * 0.6, 0.02);
			RelicFx.sound(level, mob, SoundEvents.LEAD_TIED, 1.0f, 0.8f);
		}
		return InteractionResult.SUCCESS;
	}
}
