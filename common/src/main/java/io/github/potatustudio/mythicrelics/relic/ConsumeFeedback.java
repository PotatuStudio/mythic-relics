package io.github.potatustudio.mythicrelics.relic;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Sonido y migas mientras se come o se bebe una reliquia, con el mismo ritmo que la comida vanilla (un mordisco o sorbo
 * cada 4 ticks, después de los primeros 7). Lo llaman los {@code onUseTick} de las reliquias que se comen o se beben
 * sin el componente de consumible.
 */
public final class ConsumeFeedback {
	private ConsumeFeedback() {
	}

	public static void tick(Level level, LivingEntity entity, ItemStack stack, int remaining, int duration, boolean drink) {
		if (!(level instanceof ServerLevel server) || duration - remaining < 7 || remaining % 4 != 0) {
			return;
		}
		var random = entity.getRandom();
		if (drink) {
			entity.playSound(SoundEvents.GENERIC_DRINK.value(), 0.5f, random.nextFloat() * 0.1f + 0.9f);
			return;
		}
		entity.playSound(SoundEvents.GENERIC_EAT.value(), 0.5f + 0.5f * random.nextInt(2), (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f);
		// Migas del ítem frente a la boca, como al comer una manzana.
		Vec3 look = entity.getLookAngle();
		Vec3 mouth = entity.getEyePosition().add(look.scale(0.4)).add(0.0, -0.15, 0.0);
		server.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack.getItem()), mouth.x, mouth.y, mouth.z, 5,
				0.08, 0.05, 0.08, 0.05);
	}
}
