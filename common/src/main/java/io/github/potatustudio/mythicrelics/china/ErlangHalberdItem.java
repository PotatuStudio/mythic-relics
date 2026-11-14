package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.Allies;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Alabarda de tres puntas y doble filo (sanjian liangren dao) de Erlang Shen: arma de mucho alcance (lo da
 * {@link ChinaGods} mientras está en la mano) cuyos golpes barren a los mobs cercanos al objetivo. Con el Ojo de Erlang
 * forma el set que trae al Perro Celestial.
 */
public class ErlangHalberdItem extends RelicItem {
	public ErlangHalberdItem(Properties properties) {
		super(properties);
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		super.hurtEnemy(stack, target, attacker);
		if (!(attacker instanceof Player player) || !(attacker.level() instanceof ServerLevel server)) {
			return;
		}
		for (LivingEntity near : server.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(ChinaGods.HALBERD_SWEEP_RADIUS),
				e -> e != player && e != target && e.isAlive() && !Allies.isFriend(player, e))) {
			near.hurtServer(server, server.damageSources().playerAttack(player), ChinaGods.HALBERD_SWEEP_DAMAGE);
		}
		server.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), 2,
				0.4, 0.1, 0.4, 0.0);
	}
}
