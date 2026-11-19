package io.github.potatustudio.mythicrelics.relic;

import io.github.potatustudio.mythicrelics.registry.ModLoot;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Reliquia que es un escudo (escudo de Aquiles, Ancile). Tiene que ser un {@link ShieldItem}: al bloquear en primera
 * persona, el juego gira como una espada todo lo que bloquea y no es {@code ShieldItem}, y el escudo se salía de la
 * pantalla. El tooltip es el mismo de {@link RelicItem}.
 */
public class RelicShieldItem extends ShieldItem {
	public RelicShieldItem(Properties properties) {
		super(properties);
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
