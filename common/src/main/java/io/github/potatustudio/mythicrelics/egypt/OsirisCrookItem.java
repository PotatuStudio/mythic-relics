package io.github.potatustudio.mythicrelics.egypt;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Cayado de Osiris: clic derecho en una cría la vuelve adulta al instante (la crecida del Nilo). Que los animales de
 * granja lo sigan lo agrega {@link EgyptGods} a cada animal.
 */
public class OsirisCrookItem extends RelicItem {
	public OsirisCrookItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		if (!(target instanceof AgeableMob animal) || !animal.isBaby() || player.getCooldowns().isOnCooldown(stack)) {
			return InteractionResult.PASS;
		}
		if (player.level() instanceof ServerLevel level) {
			animal.setAge(0);
			player.getCooldowns().addCooldown(stack, EgyptGods.CROOK_GROW_COOLDOWN);
			level.sendParticles(ParticleTypes.HAPPY_VILLAGER, animal.getX(), animal.getY() + animal.getBbHeight() / 2, animal.getZ(),
					12, 0.4, 0.4, 0.4, 0);
			level.playSound(null, animal.getX(), animal.getY(), animal.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
		}
		return InteractionResult.SUCCESS;
	}
}
