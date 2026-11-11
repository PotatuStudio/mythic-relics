package io.github.potatustudio.mythicrelics.japan;

import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Sake de Yashiori, el que Susanoo destiló ocho veces para emborrachar a Orochi: la jarra salpica donde miras y los mobs
 * mojados quedan borrachos (lentos y mareados); los hostiles borrachos se pelean entre ellos. La jarra se rellena sola.
 */
public class YashioriSakeItem extends RelicItem {
	public YashioriSakeItem(Properties properties) {
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
		player.getCooldowns().addCooldown(stack, JapanGods.SAKE_COOLDOWN);
		HitResult hit = player.pick(JapanGods.SAKE_RANGE, 1.0f, false);
		Vec3 at = hit.getType() == HitResult.Type.MISS
				? player.getEyePosition().add(player.getLookAngle().scale(JapanGods.SAKE_RANGE))
				: hit.getLocation();
		int r = JapanGods.SAKE_RADIUS;
		long until = server.getGameTime() + JapanGods.SAKE_TICKS;
		for (LivingEntity e : server.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(r),
				e -> e != player && e.isAlive() && !ChinaRelics.isFriend(player, e) && e.position().distanceToSqr(at) <= r * r)) {
			e.addEffect(new MobEffectInstance(MobEffects.NAUSEA, JapanGods.SAKE_TICKS, 0));
			e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, JapanGods.SAKE_TICKS, 1));
			if (e instanceof Mob mob && e instanceof Enemy) {
				JapanGods.makeDrunk(mob, until);
			}
		}
		server.sendParticles(ParticleTypes.SPLASH, at.x, at.y + 0.5, at.z, 60, r * 0.4, 0.4, r * 0.4, 0.2);
		server.sendParticles(ParticleTypes.FALLING_WATER, at.x, at.y + 1.5, at.z, 30, r * 0.4, 0.3, r * 0.4, 0.0);
		server.playSound(null, at.x, at.y, at.z, SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 1.0f, 0.8f);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1.0f, 0.7f);
		return InteractionResult.SUCCESS;
	}
}
