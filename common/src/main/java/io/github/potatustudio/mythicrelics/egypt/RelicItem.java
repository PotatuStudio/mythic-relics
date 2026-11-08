package io.github.potatustudio.mythicrelics.egypt;

import io.github.potatustudio.mythicrelics.registry.ModLoot;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Reliquia. El tooltip sale de {@code item.mythicrelics.<id>.tooltip.1}, {@code .2}… (tantas líneas como haya en la
 * traducción; Minecraft no corta las líneas largas): la primera en dorado con el efecto principal, el resto en gris, con
 * los números de {@link EgyptRelics#tooltipArgs}. Las piezas del set de Anubis agregan el bono como los atributos vanilla
 * y al final va dónde se encuentra, según {@link ModLoot}.
 */
public class RelicItem extends Item {
	private static final int MAX_LINES = 8;

	public RelicItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip,
			TooltipFlag flag) {
		String id = BuiltInRegistries.ITEM.getKey(this).getPath();
		Object[] args = EgyptRelics.tooltipArgs(id);
		Language language = Language.getInstance();
		for (int i = 1; i <= MAX_LINES; i++) {
			String key = "item.mythicrelics." + id + ".tooltip." + i;
			if (!language.has(key)) {
				break;
			}
			tooltip.accept(Component.translatable(key, args).withStyle(i == 1 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
		}
		if (EgyptRelics.isAnubisSetPiece(id)) {
			tooltip.accept(Component.empty());
			tooltip.accept(Component.translatable("tooltip.mythicrelics.anubis_set.1").withStyle(ChatFormatting.GRAY));
			tooltip.accept(Component.translatable("tooltip.mythicrelics.anubis_set.2").withStyle(ChatFormatting.BLUE));
			tooltip.accept(Component.translatable("tooltip.mythicrelics.anubis_set.3").withStyle(ChatFormatting.BLUE));
		}
		ModLoot.appendWhere(this, tooltip);
	}
}
