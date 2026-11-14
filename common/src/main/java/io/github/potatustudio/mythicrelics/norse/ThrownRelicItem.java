package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.entity.ThrownRelic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Arma que se lanza y vuelve (Mjölnir, Gungnir): se carga como un tridente (mantener clic derecho y soltar). Con
 * {@code homing}, al soltar se elige el mob más cercano a donde apuntas y la lanza lo persigue.
 */
public class ThrownRelicItem extends RelicItem {
	private static final int MIN_CHARGE = 10;
	private static final float THROW_SPEED = 2.5f;

	private final boolean homing;

	public ThrownRelicItem(Properties properties, boolean homing) {
		super(properties);
		this.homing = homing;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.TRIDENT;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (!(entity instanceof Player player) || getUseDuration(stack, entity) - timeLeft < MIN_CHARGE) {
			return false;
		}
		if (level instanceof ServerLevel server) {
			ThrownRelic thrown = new ThrownRelic(server, player, stack);
			thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, THROW_SPEED, 1.0f);
			thrown.pickup = player.hasInfiniteMaterials() ? AbstractArrow.Pickup.CREATIVE_ONLY : AbstractArrow.Pickup.ALLOWED;
			if (homing) {
				thrown.setTarget(aim(server, player));
			}
			server.addFreshEntity(thrown);
			server.playSound(null, thrown, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0f, homing ? 1.2f : 0.7f);
			if (stack.is(io.github.potatustudio.mythicrelics.registry.ModItems.MISTLETOE_DART.get())) {
				player.getCooldowns().addCooldown(stack, NorseSagas.DART_COOLDOWN);
			}
			if (!player.hasInfiniteMaterials()) {
				player.getInventory().removeItem(stack);
			}
		}
		return true;
	}

	/** El mob vivo más cercano a la línea de la mirada (cono de {@link NorseRelics#GUNGNIR_CONE_DEGREES}°). */
	private static @Nullable Mob aim(ServerLevel level, Player player) {
		Vec3 eyes = player.getEyePosition();
		Vec3 look = player.getLookAngle();
		double cos = Math.cos(Math.toRadians(NorseRelics.GUNGNIR_CONE_DEGREES));
		Mob best = null;
		double bestDot = cos;
		for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(NorseRelics.GUNGNIR_RANGE),
				m -> m.isAlive() && !m.isAlliedTo(player))) {
			Vec3 to = mob.getBoundingBox().getCenter().subtract(eyes);
			if (to.length() > NorseRelics.GUNGNIR_RANGE) {
				continue;
			}
			double dot = look.dot(to.normalize());
			if (dot > bestDot && player.hasLineOfSight(mob)) {
				bestDot = dot;
				best = mob;
			}
		}
		return best;
	}
}
