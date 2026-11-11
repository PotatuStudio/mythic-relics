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
			setBlock(lines, set, player);
		}
	}

	/**
	 * Bloque del set, igual en todas sus piezas: el nombre en violeta con cuántas piezas tienes, cada pieza marcada (✔ la
	 * tienes contigo, ✘ te falta), el bono en azul y si está activo. Va justo antes de «Se encuentra en».
	 */
	private static void setBlock(List<Component> lines, RelicSets set, Player player) {
		int at = lines.size();
		for (int i = lines.size() - 1; i >= 0; i--) {
			if (lines.get(i).getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents t
					&& t.getKey().equals("tooltip.mythicrelics.found_in")) {
				at = i;
				break;
			}
		}
		List<Component> block = new java.util.ArrayList<>();
		int have = 0;
		net.minecraft.network.chat.MutableComponent pieces = Component.literal(" ");
		boolean first = true;
		for (String id : set.pieces()) {
			net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.getValue(MythicRelics.id(id));
			boolean has = player.getInventory().contains(s -> s.is(item))
					|| io.github.potatustudio.mythicrelics.relic.WornRelics.isWearing(player, item);
			if (has) {
				have++;
			}
			if (!first) {
				pieces.append(Component.literal(", ").withStyle(ChatFormatting.DARK_GRAY));
			}
			first = false;
			pieces.append(Component.literal(has ? "✔ " : "✘ ").append(new net.minecraft.world.item.ItemStack(item).getHoverName())
					.withStyle(has ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
		}
		block.add(Component.empty());
		block.add(Component.literal("◈ ").append(set.displayName()).append(Component.literal(" · " + have + "/" + set.pieces().size()))
				.withStyle(ChatFormatting.LIGHT_PURPLE));
		block.add(pieces);
		for (int i = 1; i <= 3; i++) {
			String k = "set.mythicrelics." + set.key() + ".bonus." + i;
			if (net.minecraft.locale.Language.getInstance().has(k)) {
				block.add(Component.literal(" ").append(Component.translatable(k,
						io.github.potatustudio.mythicrelics.egypt.EgyptGods.rebirthMinutes())).withStyle(ChatFormatting.BLUE));
			}
		}
		boolean on = set.isComplete(player);
		block.add(Component.literal(" ").append(Component.translatable(on ? "tooltip.mythicrelics.set_active" : "tooltip.mythicrelics.set_inactive",
				set.displayName())).withStyle(on ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
		lines.addAll(at, block);
	}
}
