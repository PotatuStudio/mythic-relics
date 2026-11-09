package io.github.potatustudio.mythicrelics.rome;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.registry.ModLoot;
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

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip,
			TooltipFlag flag) {
		RelicItem.effectLines(this, tooltip);
		ModLoot.appendWhere(this, tooltip);
	}
}
