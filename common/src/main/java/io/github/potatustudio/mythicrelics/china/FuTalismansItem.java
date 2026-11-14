package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Talismanes de papel (fu): clic derecho a un no-muerto le pega un talismán en la frente y lo deja inmóvil un rato, como
 * al jiangshi del folclore. El fajo vuelve a escribirse solo (recarga).
 */
public class FuTalismansItem extends RelicItem {
	public FuTalismansItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack ignored, Player player, LivingEntity target, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (!(target instanceof Mob mob) || !target.typeHolder().is(EntityTypeTags.UNDEAD)) {
			if (!player.level().isClientSide()) {
				RelicFx.notify(player, "talisman_not_undead", ChatFormatting.GRAY);
			}
			return InteractionResult.FAIL;
		}
		if (!(player.level() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		ChinaGods.seal(level, mob);
		RelicCooldowns.start(player, stack, ChinaGods.TALISMAN_COOLDOWN);
		level.sendParticles(ParticleTypes.WAX_ON, mob.getX(), mob.getEyeY(), mob.getZ(), 12, 0.2, 0.2, 0.2, 0.05);
		RelicFx.sound(level, mob, SoundEvents.BOOK_PAGE_TURN, 1.2f, 0.8f);
		return InteractionResult.SUCCESS;
	}
}
