package io.github.potatustudio.mythicrelics.registry;

import dev.architectury.event.events.common.LootEvent;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

/**
 * Dónde se encuentran las reliquias: no se fabrican, aparecen en estructuras de su cultura. Cada entrada agrega a la
 * tabla vanilla un pool propio con la probabilidad indicada por cofre. Es la única fuente de esos datos: de aquí salen
 * el botín y la línea "Se encuentra en" del tooltip.
 */
public final class ModLoot {
	private record Drop(float chance, RegistrySupplier<Item> item) {
	}

	private static final Map<ResourceKey<LootTable>, List<Drop>> DROPS = Map.of(
			// Pirámide del desierto (4 cofres): ~57 % de las pirámides trae al menos una reliquia.
			BuiltInLootTables.DESERT_PYRAMID, List.of(
					new Drop(0.040f, ModItems.MUMMY_WRAPS),
					new Drop(0.035f, ModItems.GOLDEN_SCARAB),
					new Drop(0.030f, ModItems.ANKH),
					new Drop(0.030f, ModItems.JACKAL_MASK),
					new Drop(0.025f, ModItems.JACKAL_RING),
					new Drop(0.025f, ModItems.EYE_OF_HORUS),
					new Drop(0.020f, ModItems.ANUBIS_SCEPTER)),
			BuiltInLootTables.VILLAGE_DESERT_HOUSE, List.of(
					new Drop(0.030f, ModItems.ANKH)));

	/** Dónde aparece una reliquia: tabla de botín y probabilidad por cofre. */
	public record Source(ResourceKey<LootTable> table, float chance) {
		/** Clave de traducción del lugar, p. ej. loot.mythicrelics.chests.desert_pyramid. */
		public String translationKey() {
			return "loot.mythicrelics." + table.identifier().getPath().replace('/', '.');
		}
	}

	private ModLoot() {
	}

	/** Todos los lugares donde se encuentra una reliquia, de más a menos probable. */
	public static List<Source> sources(Item item) {
		List<Source> found = new ArrayList<>();
		DROPS.forEach((table, drops) -> {
			for (Drop drop : drops) {
				if (drop.item().get() == item) {
					found.add(new Source(table, drop.chance()));
				}
			}
		});
		found.sort((a, b) -> Float.compare(b.chance(), a.chance()));
		return found;
	}

	/** Línea discreta para el tooltip: "Se encuentra en: pirámides del desierto, casas de aldea del desierto". */
	public static void appendWhere(Item item, Consumer<Component> tooltip) {
		List<Source> found = sources(item);
		if (found.isEmpty()) {
			return;
		}
		MutableComponent places = Component.empty();
		for (int i = 0; i < found.size(); i++) {
			if (i > 0) {
				places.append(", ");
			}
			places.append(Component.translatable(found.get(i).translationKey()));
		}
		tooltip.accept(Component.translatable("tooltip.mythicrelics.found_in", places)
				.withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
	}

	public static void init() {
		LootEvent.MODIFY_LOOT_TABLE.register((lookup, key, context, builtin) -> {
			List<Drop> drops = DROPS.get(key);
			if (drops == null || !builtin) {
				return;
			}
			for (Drop drop : drops) {
				context.addPool(LootPool.lootPool()
						.when(LootItemRandomChanceCondition.randomChance(drop.chance()))
						.add(LootItem.lootTableItem(drop.item().get())));
			}
		});
	}
}
