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

	private static final Map<ResourceKey<LootTable>, List<Drop>> DROPS = Map.ofEntries(
			// ---- Egipto ----
			// Pirámide del desierto (4 cofres): tanda 1 e insignias de Osiris.
			Map.entry(BuiltInLootTables.DESERT_PYRAMID, List.of(
					new Drop(0.040f, ModItems.MUMMY_WRAPS),
					new Drop(0.035f, ModItems.GOLDEN_SCARAB),
					new Drop(0.030f, ModItems.ANKH),
					new Drop(0.030f, ModItems.JACKAL_MASK),
					new Drop(0.025f, ModItems.JACKAL_RING),
					new Drop(0.025f, ModItems.EYE_OF_HORUS),
					new Drop(0.020f, ModItems.ANUBIS_SCEPTER),
					new Drop(0.020f, ModItems.OSIRIS_CROOK),
					new Drop(0.020f, ModItems.OSIRIS_FLAIL))),
			Map.entry(BuiltInLootTables.VILLAGE_DESERT_HOUSE, List.of(
					new Drop(0.030f, ModItems.ANKH),
					new Drop(0.040f, ModItems.BASTET_AMULET))),
			// Arqueología (tanda 2): probabilidad por bloque de arena sospechosa cepillado.
			Map.entry(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY, List.of(
					new Drop(0.030f, ModItems.THOTH_PALETTE),
					new Drop(0.015f, ModItems.RA_DISK),
					new Drop(0.015f, ModItems.SEKHMET_CLAWS))),
			Map.entry(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, List.of(
					new Drop(0.050f, ModItems.BASTET_AMULET))),
			// ---- Grecia ----
			// Ruinas de los senderos: las ruinas antiguas (arqueología, por bloque cepillado).
			Map.entry(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE, List.of(
					new Drop(0.020f, ModItems.HERMES_SANDALS),
					new Drop(0.030f, ModItems.CHARON_OBOL))),
			// Ruinas oceánicas cálidas: templos hundidos.
			Map.entry(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY, List.of(
					new Drop(0.015f, ModItems.MEDUSA_HEAD))),
			// Fortaleza del Nether: el reino de Hades.
			Map.entry(BuiltInLootTables.NETHER_BRIDGE, List.of(
					new Drop(0.040f, ModItems.HADES_HELM),
					new Drop(0.050f, ModItems.CHARON_OBOL))),
			// Fortaleza: el laberinto.
			Map.entry(BuiltInLootTables.STRONGHOLD_CORRIDOR, List.of(
					new Drop(0.060f, ModItems.ARIADNE_THREAD))),
			Map.entry(BuiltInLootTables.STRONGHOLD_LIBRARY, List.of(
					new Drop(0.060f, ModItems.ARIADNE_THREAD))),
			// Cámaras de desafío: las pruebas del héroe.
			Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_REWARD, List.of(
					new Drop(0.030f, ModItems.ATHENA_AEGIS))),
			// Ciudad antigua: el descenso de Orfeo.
			Map.entry(BuiltInLootTables.ANCIENT_CITY, List.of(
					new Drop(0.030f, ModItems.ORPHEUS_LYRE))));

	/** Dónde aparece una reliquia: tabla de botín y probabilidad por cofre (o por bloque cepillado, en arqueología). */
	public record Source(ResourceKey<LootTable> table, float chance) {
		/** Arena o grava sospechosa: la probabilidad es por bloque cepillado, no por cofre. */
		public boolean archaeology() {
			return table.identifier().getPath().startsWith("archaeology/");
		}

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
