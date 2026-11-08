package io.github.potatustudio.mythicrelics.neoforge;

import io.github.potatustudio.mythicrelics.relic.WornRelics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

/** Accesorios en NeoForge: Curios. Los espacios se asignan con data/curios/tags/item y data/mythicrelics/curios/entities. */
public final class CuriosWornRelics implements WornRelics.Provider {
	@Override
	public boolean isWearing(LivingEntity entity, Item item) {
		return CuriosApi.getCuriosInventory(entity).map(inventory -> inventory.isEquipped(item)).orElse(false);
	}
}
