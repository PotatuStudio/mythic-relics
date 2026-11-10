package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Ruyi Jingu Bang, el bastón del Rey Mono: barra de hierro negro con aros de oro que crece y se encoge a voluntad. Es un
 * arma pesada y de mucho alcance (el alcance lo da {@link ChinaRelics} mientras está en la mano); con clic derecho se
 * estira en línea recta y golpea a todo lo que toca.
 */
public class RuyiJinguBangItem extends RelicItem {
	public RuyiJinguBangItem(Properties properties) {
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
		player.getCooldowns().addCooldown(stack, ChinaRelics.RUYI_COOLDOWN);
		Vec3 from = player.getEyePosition();
		Vec3 dir = player.getLookAngle();
		Vec3 to = from.add(dir.scale(ChinaRelics.RUYI_LENGTH));
		AABB reach = new AABB(from, to).inflate(1.0);
		for (LivingEntity target : server.getEntitiesOfClass(LivingEntity.class, reach,
				e -> e != player && e.isAlive() && !ChinaRelics.isFriend(player, e))) {
			// Solo lo que queda sobre la línea del bastón (con un poco de grosor).
			if (target.getBoundingBox().inflate(0.4).clip(from, to).isEmpty() && !target.getBoundingBox().contains(from)) {
				continue;
			}
			if (target.hurtServer(server, server.damageSources().playerAttack(player), ChinaRelics.RUYI_STRETCH_DAMAGE)) {
				target.push(dir.x * 1.2, 0.4, dir.z * 1.2);
				target.needsSync = true;
			}
		}
		for (int i = 1; i <= ChinaRelics.RUYI_LENGTH * 2; i++) {
			Vec3 p = from.add(dir.scale(i * 0.5));
			server.sendParticles(i % 4 == 0 ? ParticleTypes.WAX_ON : ParticleTypes.CRIT, p.x, p.y - 0.2, p.z, 1, 0.02, 0.02, 0.02, 0.0);
		}
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.35f, 1.6f);
		server.playSound(null, to.x, to.y, to.z, SoundEvents.IRON_GOLEM_ATTACK, SoundSource.PLAYERS, 1.0f, 0.8f);
		return InteractionResult.SUCCESS;
	}
}
