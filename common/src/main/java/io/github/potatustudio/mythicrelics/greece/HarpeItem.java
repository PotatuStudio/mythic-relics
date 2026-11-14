package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Harpe: la hoz de adamante de Cronos y de Perseo. Además del golpe y de las cabezas de trofeo
 * ({@link GreeceLegends}), con clic derecho decapita al instante a un mob malherido (menos del 25 % de vida, no jefes);
 * cuando hay uno así y la recarga está lista, brilla y suena un aviso, y decapitarlo siempre deja su cabeza.
 */
public class HarpeItem extends RelicItem {
	/** Mobs que se están decapitando (para que suelten la cabeza siempre). */
	private static final Set<LivingEntity> BEHEADING = Collections.newSetFromMap(new WeakHashMap<>());

	public HarpeItem(Properties properties) {
		super(properties);
	}

	static boolean isBeheading(LivingEntity e) {
		return BEHEADING.contains(e);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(stack) || !GreeceLegends.canBehead(target) || ChinaRelics.isFriend(player, target)) {
			return InteractionResult.PASS;
		}
		if (!(player.level() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		BEHEADING.add(target);
		target.hurtServer(level, level.damageSources().playerAttack(player), target.getHealth() + 100.0f);
		BEHEADING.remove(target);
		level.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY(0.8), target.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
		level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.8), target.getZ(), 20, 0.3, 0.3, 0.3, 0.4);
		level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.2f, 0.6f);
		level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0f, 0.5f);
		player.getCooldowns().addCooldown(stack, GreeceLegends.HARPE_COOLDOWN);
		return InteractionResult.SUCCESS;
	}
}
