package io.github.potatustudio.mythicrelics.client;

import dev.architectury.event.events.client.ClientTooltipEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.relic.RelicCultures;
import io.github.potatustudio.mythicrelics.relic.RelicSets;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Líneas del tooltip que dependen del jugador o se agregan a todas las reliquias: la mitología de la que viene (bajo el
 * nombre, en el color de su cultura), si el set de la pieza está activo y cuánto le falta a la recarga.
 */
public final class RelicTooltips {
	private static final Map<String, Integer> COLORS = Map.of("egypt", 0xE8C15A, "greece", 0x7FB7E6, "rome", 0xD0564A,
			"norse", 0x9FD3E8, "china", 0xE8603C, "japan", 0xF2A2B8);

	private RelicTooltips() {
	}

	public static void init() {
		ClientTooltipEvent.ITEM.register(RelicTooltips::append);
	}

	private static void append(ItemStack stack, List<Component> lines, net.minecraft.world.item.Item.TooltipContext context,
			net.minecraft.world.item.TooltipFlag flag) {
		Identifier key = BuiltInRegistries.ITEM.getKey(stack.getItem());
		if (!key.getNamespace().equals(MythicRelics.MOD_ID)) {
			return;
		}
		String culture = RelicCultures.of(key.getPath());
		if (culture == null) {
			return;
		}
		int at = Math.min(1, lines.size());
		lines.add(at, Component.literal("◆ ").append(Component.translatable("culture.mythicrelics." + culture))
				.withStyle(Style.EMPTY.withColor(COLORS.get(culture)).withItalic(true)));
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}
		if (player.getCooldowns().isOnCooldown(stack)) {
			int pct = Math.round(player.getCooldowns().getCooldownPercent(stack, 0f) * 100);
			lines.add(at + 1, Component.translatable("tooltip.mythicrelics.cooldown", pct).withStyle(ChatFormatting.RED));
		}
		RelicSets set = RelicSets.of(key.getPath());
		if (set != null) {
			boolean on = set.isComplete(player);
			lines.add(Component.translatable(on ? "tooltip.mythicrelics.set_active" : "tooltip.mythicrelics.set_inactive",
					set.displayName()).withStyle(on ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
		}
	}
}
