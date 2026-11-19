package io.github.potatustudio.mythicrelics.rome;

import io.github.potatustudio.mythicrelics.registry.ModLoot;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/** El Paladio en el inventario: un bloque con el mismo tooltip que las demás reliquias. */
public class PalladiumItem extends BlockItem {
	public PalladiumItem(Block block, Properties properties) {
		super(block, properties);
	}

	// Mojang lo marca @Deprecated en el sentido de «sobrescribir, no llamar»: en 26.3 sigue siendo el gancho del tooltip
	// del ítem (ItemStack lo llama). TooltipProvider solo sirve para componentes que vanilla ya conoce.
	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip,
			TooltipFlag flag) {
		RelicItem.effectLines(this, tooltip);
		ModLoot.appendWhere(this, tooltip);
	}
}
