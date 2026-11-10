package io.github.potatustudio.mythicrelics.china;

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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Aro del Universo (qiankun quan) de Nezha: se lanza hacia donde miras, golpea a un enemigo, rebota al siguiente más
 * cercano (hasta tres) y vuelve a tu mano. El vuelo se ve como una estela de oro de golpe en golpe; el aro no sale del
 * inventario, así que nunca se pierde.
 */
public class UniverseRingItem extends RelicItem {
	private static final double AIM_COS = Math.cos(Math.toRadians(20));
	private static final double BOUNCE_RANGE = 6.0;

	public UniverseRingItem(Properties properties) {
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
		LivingEntity first = aim(server, player);
		player.getCooldowns().addCooldown(stack, ChinaGods.RING_COOLDOWN);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0f, 1.4f);
		Vec3 from = player.getEyePosition().add(0, -0.3, 0);
		if (first == null) {
			trail(server, from, from.add(player.getLookAngle().scale(ChinaGods.RING_RANGE)));
			return InteractionResult.SUCCESS;
		}
		List<LivingEntity> hit = new ArrayList<>();
		LivingEntity current = first;
		while (current != null && hit.size() < ChinaGods.RING_TARGETS) {
			Vec3 at = current.getBoundingBox().getCenter();
			trail(server, from, at);
			current.hurtServer(server, server.damageSources().playerAttack(player), ChinaGods.RING_DAMAGE);
			current.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, ChinaGods.RING_STUN_TICKS, 9, false, false));
			server.playSound(null, at.x, at.y, at.z, SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.3f, 2.0f);
			hit.add(current);
			from = at;
			LivingEntity last = current;
			current = server.getEntitiesOfClass(LivingEntity.class, last.getBoundingBox().inflate(BOUNCE_RANGE),
							e -> e != player && e.isAlive() && !hit.contains(e) && !ChinaRelics.isFriend(player, e)
									&& e.getType().getCategory() == net.minecraft.world.entity.MobCategory.MONSTER)
					.stream().min(Comparator.comparingDouble(e -> e.distanceToSqr(last))).orElse(null);
		}
		// Vuelve a la mano.
		trail(server, from, player.getEyePosition().add(0, -0.3, 0));
		return InteractionResult.SUCCESS;
	}

	/** El primer blanco: el ser vivo más cercano dentro de un cono angosto frente a ti. */
	private static @Nullable LivingEntity aim(ServerLevel level, Player player) {
		Vec3 eye = player.getEyePosition();
		Vec3 look = player.getLookAngle();
		return level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(ChinaGods.RING_RANGE),
						e -> e != player && e.isAlive() && !ChinaRelics.isFriend(player, e) && player.hasLineOfSight(e))
				.stream()
				.filter(e -> {
					Vec3 to = e.getBoundingBox().getCenter().subtract(eye);
					return to.length() <= ChinaGods.RING_RANGE && to.normalize().dot(look) >= AIM_COS;
				})
				.min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
				.orElse(null);
	}

	private static void trail(ServerLevel level, Vec3 from, Vec3 to) {
		Vec3 step = to.subtract(from);
		int n = Math.max(2, (int) (step.length() * 3));
		for (int i = 0; i <= n; i++) {
			Vec3 p = from.add(step.scale(i / (double) n));
			level.sendParticles(ParticleTypes.WAX_ON, p.x, p.y, p.z, 1, 0.03, 0.03, 0.03, 0.0);
		}
	}
}
