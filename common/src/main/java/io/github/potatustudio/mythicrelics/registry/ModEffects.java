package io.github.potatustudio.mythicrelics.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.greece.GreeceHeroes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Efectos del mod. El Baño del Estigia no hace nada por sí solo: el daño lo ajusta
 * {@link GreeceHeroes#modifyIncomingDamage}. Ser un efecto da el ícono, el
 * tiempo restante en el inventario y que se guarde con el jugador.
 */
public final class ModEffects {
	private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(MythicRelics.MOD_ID, Registries.MOB_EFFECT);

	public static final RegistrySupplier<MobEffect> STYX_BATH = EFFECTS.register("styx_bath",
			() -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x2F9BA0) {
			});

	private ModEffects() {
	}

	public static void init() {
		EFFECTS.register();
	}
}
