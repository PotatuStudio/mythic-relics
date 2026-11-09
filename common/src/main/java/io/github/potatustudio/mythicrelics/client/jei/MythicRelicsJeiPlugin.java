package io.github.potatustudio.mythicrelics.client.jei;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.registry.ModLoot;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Integración opcional con JEI: como las reliquias no se fabrican, cada una tiene una página de información con dónde
 * se encuentra y con qué probabilidad por cofre. Sale de {@link ModLoot#sources}, la misma tabla que usa el botín.
 * Cada loader la carga a su manera (entrypoint {@code jei_mod_plugin} en Fabric, {@code @JeiPlugin} en NeoForge);
 * sin JEI instalado, esta clase nunca se carga.
 */
public class MythicRelicsJeiPlugin implements IModPlugin {
	@Override
	public Identifier getPluginUid() {
		return MythicRelics.id("jei_plugin");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		int pages = 0;
		for (Item item : BuiltInRegistries.ITEM) {
			if (!BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(MythicRelics.MOD_ID) || item == ModItems.EMBLEM.get()) {
				continue;
			}
			List<ModLoot.Source> sources = ModLoot.sources(item);
			if (sources.isEmpty()) {
				continue;
			}
			List<Component> lines = new ArrayList<>();
			lines.add(Component.translatable("jei.mythicrelics.where.title").withStyle(ChatFormatting.BOLD));
			for (ModLoot.Source source : sources) {
				lines.add(Component.translatable(source.archaeology() ? "jei.mythicrelics.where.entry_brush" : "jei.mythicrelics.where.entry",
						Component.translatable(source.translationKey()), formatChance(source.chance())));
			}
			lines.add(Component.translatable("jei.mythicrelics.where.note").withStyle(ChatFormatting.GRAY));
			registration.addItemStackInfo(new ItemStack(item), lines.toArray(Component[]::new));
			pages++;
		}
		MythicRelics.LOGGER.info("JEI: {} páginas de \"dónde encontrarlo\" registradas", pages);
	}

	/** 3 -> "3", 2.5 -> "2.5". */
	private static String formatChance(float chance) {
		float percent = chance * 100f;
		return percent == Math.round(percent) ? String.valueOf(Math.round(percent)) : String.format(java.util.Locale.ROOT, "%.1f", percent);
	}
}
