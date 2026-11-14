package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Rayo de Zeus: arma a distancia de uso continuo. Se carga un instante (mantener clic derecho y soltar) y cae un rayo
 * limpio, sin fuego, donde apuntas; después salta a los hostiles cercanos. El rayo es solo el efecto visual del vanilla
 * (no quema) y el daño se aplica aparte; el propio rayo no te daña. Con tormenta salta a más enemigos.
 */
public class ZeusThunderboltItem extends RelicItem {
	public ZeusThunderboltItem(Properties properties) {
		super(properties);
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
		return ItemUseAnimation.SPEAR;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (!(entity instanceof Player player) || getUseDuration(stack, entity) - timeLeft < GreeceLegends.ZEUS_CHARGE_TICKS) {
			return false;
		}
		if (level instanceof ServerLevel server) {
			strike(server, player);
			player.getCooldowns().addCooldown(stack, GreeceLegends.ZEUS_COOLDOWN);
		}
		return true;
	}

	private static void strike(ServerLevel level, Player player) {
		Vec3 eyes = player.getEyePosition();
		Vec3 end = eyes.add(player.getLookAngle().scale(GreeceLegends.ZEUS_RANGE));
		var block = level.clip(new ClipContext(eyes, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
		if (block.getType() != HitResult.Type.MISS) {
			end = block.getLocation();
		}
		// El primer ser vivo en la línea de la mirada; si no hay, el más cercano al punto donde cae.
		LivingEntity first = null;
		double best = Double.MAX_VALUE;
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().expandTowards(end.subtract(eyes)).inflate(1.0),
				e -> e != player && e.isAlive() && !ChinaRelics.isFriend(player, e))) {
			var hit = e.getBoundingBox().inflate(0.4).clip(eyes, end);
			if (hit.isPresent() && hit.get().distanceToSqr(eyes) < best) {
				best = hit.get().distanceToSqr(eyes);
				first = e;
			}
		}
		Vec3 point = first != null ? first.position() : end;
		if (first == null) {
			Vec3 at = point;
			first = level.getEntitiesOfClass(LivingEntity.class, new net.minecraft.world.phys.AABB(at, at).inflate(1.5),
					e -> e != player && e.isAlive() && !ChinaRelics.isFriend(player, e)).stream()
					.min(Comparator.comparingDouble(e -> e.distanceToSqr(at))).orElse(null);
		}
		bolt(level, point, player);
		level.playSound(null, point.x, point.y, point.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.2f, 1.3f);
		if (first == null) {
			return;
		}
		first.hurtServer(level, level.damageSources().playerAttack(player), GreeceLegends.ZEUS_DAMAGE);
		chain(level, player, first);
	}

	/** El rayo salta del primer golpeado a los hostiles más cercanos (con tormenta, a más). */
	private static void chain(ServerLevel level, Player player, LivingEntity from) {
		int count = level.isThundering() ? GreeceLegends.ZEUS_STORM_CHAIN_TARGETS : GreeceLegends.ZEUS_CHAIN_TARGETS;
		List<LivingEntity> near = new ArrayList<>(level.getEntitiesOfClass(LivingEntity.class,
				from.getBoundingBox().inflate(GreeceLegends.ZEUS_CHAIN_RADIUS),
				e -> e != from && e != player && e.isAlive() && e instanceof Enemy && !ChinaRelics.isFriend(player, e)));
		near.sort(Comparator.comparingDouble(e -> e.distanceToSqr(from)));
		Vec3 last = from.position().add(0, from.getBbHeight() * 0.6, 0);
		for (LivingEntity e : near.subList(0, Math.min(count, near.size()))) {
			Vec3 to = e.position().add(0, e.getBbHeight() * 0.6, 0);
			arc(level, last, to);
			e.hurtServer(level, level.damageSources().playerAttack(player), GreeceLegends.ZEUS_CHAIN_DAMAGE);
			last = to;
		}
	}

	/** Rayo vanilla solo visual: se ve y suena, no quema ni hace daño por su cuenta. */
	private static void bolt(ServerLevel level, Vec3 at, @Nullable Player cause) {
		LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
		if (bolt == null) {
			return;
		}
		bolt.snapTo(at.x, at.y, at.z);
		bolt.setVisualOnly(true);
		level.addFreshEntity(bolt);
	}

	/** Arco eléctrico entre dos enemigos: una línea de chispas. */
	private static void arc(ServerLevel level, Vec3 from, Vec3 to) {
		Vec3 step = to.subtract(from);
		int n = (int) Math.max(4, step.length() * 4);
		for (int i = 0; i <= n; i++) {
			Vec3 p = from.add(step.scale(i / (double) n));
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.0);
		}
		level.sendParticles(ParticleTypes.ELECTRIC_SPARK, to.x, to.y, to.z, 12, 0.2, 0.3, 0.2, 0.3);
	}
}
