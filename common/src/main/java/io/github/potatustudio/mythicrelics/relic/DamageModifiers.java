package io.github.potatustudio.mythicrelics.relic;

import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.greece.GreeceHeroes;
import io.github.potatustudio.mythicrelics.greece.GreeceLegends;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
import io.github.potatustudio.mythicrelics.norse.NorseRelics;
import io.github.potatustudio.mythicrelics.norse.NorseSagas;
import io.github.potatustudio.mythicrelics.rome.RomeRelics;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/** Daño que recibe una entidad: cada cultura lo ajusta en su {@code modifyIncomingDamage} (lo llama el mixin). */
public final class DamageModifiers {
	@FunctionalInterface
	public interface Modifier {
		float apply(LivingEntity self, DamageSource source, float amount);
	}

	/** En este orden: algunos suman o ponen topes, así que el orden cambia el resultado. */
	private static final List<Modifier> INCOMING = List.of(
			EgyptRelics::modifyIncomingDamage,
			EgyptGods::modifyIncomingDamage,
			GreeceRelics::modifyIncomingDamage,
			GreeceHeroes::modifyIncomingDamage,
			RomeRelics::modifyIncomingDamage,
			NorseRelics::modifyIncomingDamage,
			ChinaRelics::modifyIncomingDamage,
			GreeceLegends::modifyIncomingDamage,
			NorseSagas::modifyIncomingDamage,
			SetWeapons::modifyIncomingDamage);

	private DamageModifiers() {
	}

	public static float incoming(LivingEntity self, DamageSource source, float amount) {
		HitEffects.begin(self);
		for (Modifier m : INCOMING) {
			amount = m.apply(self, source, amount);
		}
		return amount;
	}
}
