package io.github.potatustudio.mythicrelics.relic;

import io.github.potatustudio.mythicrelics.registry.ModItems;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

/**
 * Armas de set: con el set completo, el arma del set pega {@link RelicSets#WEAPON_BONUS} más en cuerpo a cuerpo (y en lo
 * que hace con el arma en la mano: barrido del mayal). Mjölnir lanzado y los rayos de
 * Thor se suben donde se calculan (ThrownRelic y RelicLightning).
 */
public final class SetWeapons {
	private static final Map<RelicSets, Supplier<Item>> MELEE = Map.of(
			RelicSets.ANUBIS, () -> ModItems.ANUBIS_SCEPTER.get(),
			RelicSets.OSIRIS, () -> ModItems.OSIRIS_FLAIL.get(),
			RelicSets.PERSEUS, () -> ModItems.HARPE.get());

	private SetWeapons() {
	}

	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		if (!(source.getEntity() instanceof Player attacker) || source.getDirectEntity() != attacker) {
			return amount;
		}
		for (Map.Entry<RelicSets, Supplier<Item>> e : MELEE.entrySet()) {
			if (attacker.getMainHandItem().is(e.getValue().get()) && e.getKey().isComplete(attacker)) {
				return amount * (1.0f + RelicSets.WEAPON_BONUS);
			}
		}
		return amount;
	}
}
