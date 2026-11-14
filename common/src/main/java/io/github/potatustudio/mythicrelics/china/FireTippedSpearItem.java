package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.relic.Allies;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Lanza de punta de fuego (huojian qiang) de Nezha: cada golpe prende fuego y con clic derecho lanza una llamarada en
 * cono. Con las ruedas de viento y fuego puestas, la llamarada recarga a la mitad ({@link ChinaGods#spearCooldown}).
 */
public class FireTippedSpearItem extends RelicItem {
	private static final double CONE_COS = Math.cos(Math.toRadians(30));

	public FireTippedSpearItem(Properties properties) {
		super(properties);
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		super.hurtEnemy(stack, target, attacker);
		target.igniteForSeconds(ChinaGods.SPEAR_BURN_SECONDS);
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
		RelicCooldowns.start(player, stack, ChinaGods.spearCooldown(player));
		Vec3 eye = player.getEyePosition();
		Vec3 look = player.getLookAngle();
		int range = ChinaGods.SPEAR_FLAME_RANGE;
		for (LivingEntity target : Allies.enemiesAround(player, player.getBoundingBox().inflate(range))) {
			Vec3 to = target.getBoundingBox().getCenter().subtract(eye);
			if (to.length() > range || to.normalize().dot(look) < CONE_COS || !player.hasLineOfSight(target)) {
				continue;
			}
			target.hurtServer(server, server.damageSources().playerAttack(player), ChinaGods.SPEAR_FLAME_DAMAGE);
			target.igniteForSeconds(ChinaGods.SPEAR_BURN_SECONDS);
		}
		for (int i = 1; i <= range * 2; i++) {
			Vec3 p = eye.add(look.scale(i * 0.5)).add(0, -0.2, 0);
			double spread = i * 0.08;
			server.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 4, spread, spread, spread, 0.02);
		}
		RelicFx.sound(server, player, SoundEvents.BLAZE_SHOOT, 1.0f, 0.8f);
		return InteractionResult.SUCCESS;
	}
}
