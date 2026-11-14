package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.Allies;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Harpe: la hoz de adamante de Cronos y de Perseo. Pasiva: al golpear con ella a un mob malherido (menos del 25 % de
 * vida, no jefes) le corta la cabeza al instante, con {@link GreeceLegends#HARPE_COOLDOWN} ticks de recarga; la cabeza
 * cae el 75 % de las veces. Cuando la recarga está lista, los mobs que ya se pueden decapitar brillan
 * ({@link GreeceLegends}).
 */
public class HarpeItem extends RelicItem {
	/** Mobs que se están decapitando (para la probabilidad de la cabeza al morir). */
	private static final Set<LivingEntity> BEHEADING = Collections.newSetFromMap(new WeakHashMap<>());

	public HarpeItem(Properties properties) {
		super(properties);
	}

	static boolean isBeheading(LivingEntity e) {
		return BEHEADING.contains(e);
	}

	static void forget(LivingEntity e) {
		BEHEADING.remove(e);
	}

	/**
	 * Golpe cuerpo a cuerpo de {@code attacker} a {@code target}: si lleva la harpe en la mano, la recarga está lista y
	 * el mob está malherido, el golpe lo decapita (daño de sobra para matarlo). Devuelve el daño final.
	 */
	static float behead(Player attacker, LivingEntity target, float amount) {
		ItemStack stack = attacker.getMainHandItem();
		if (!stack.is(io.github.potatustudio.mythicrelics.registry.ModItems.HARPE.get()) || attacker.getCooldowns().isOnCooldown(stack)
				|| !GreeceLegends.canBehead(target) || Allies.isFriend(attacker, target)
				|| !(attacker.level() instanceof ServerLevel level)) {
			return amount;
		}
		BEHEADING.add(target);
		RelicCooldowns.start(attacker, stack, GreeceLegends.HARPE_COOLDOWN);
		level.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY(0.8), target.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
		level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.8), target.getZ(), 20, 0.3, 0.3, 0.3, 0.4);
		RelicFx.sound(level, target, SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, 0.6f);
		RelicFx.sound(level, target, SoundEvents.SHEEP_SHEAR, 1.0f, 0.5f);
		return Math.max(amount, target.getHealth() + 100.0f);
	}
}
