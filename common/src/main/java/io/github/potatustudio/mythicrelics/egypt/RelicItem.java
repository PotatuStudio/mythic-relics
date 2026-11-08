package io.github.potatustudio.mythicrelics.egypt;

import io.github.potatustudio.mythicrelics.registry.ModLoot;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Reliquia: el tooltip sale de {@code tooltip.mythicrelics.<id>} (con los números de {@link EgyptRelics}) y termina
 * con dónde se encuentra, según {@link ModLoot}.
 */
public class RelicItem extends Item {
	public RelicItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip,
			TooltipFlag flag) {
		String id = BuiltInRegistries.ITEM.getKey(this).getPath();
		tooltip.accept(Component.translatable("tooltip.mythicrelics." + id, EgyptRelics.tooltipArgs(id))
				.withStyle(ChatFormatting.GRAY));
		if (id.equals("anubis_scepter") || id.equals("jackal_mask") || id.equals("jackal_ring")) {
			tooltip.accept(Component.translatable("tooltip.mythicrelics.anubis_set").withStyle(ChatFormatting.GOLD));
		}
		ModLoot.appendWhere(this, tooltip);
	}
}
