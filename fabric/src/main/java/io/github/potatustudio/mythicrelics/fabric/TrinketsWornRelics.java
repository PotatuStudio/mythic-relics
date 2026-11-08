package io.github.potatustudio.mythicrelics.fabric;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

/** Accesorios en Fabric: Trinkets Updated. Los espacios se asignan en data/trinkets y el render en assets/.../trinkets. */
public final class TrinketsWornRelics implements WornRelics.Provider {
	@Override
	public boolean isWearing(LivingEntity entity, Item item) {
		return TrinketsApi.getAttachment(entity).findFirst(item).map(TrinketSlotAccess::get).isPresent();
	}
}
