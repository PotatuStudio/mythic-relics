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
					new Drop(0.015f, ModItems.HARPE),
					new Drop(0.020f, ModItems.HERMES_SANDALS),
					new Drop(0.030f, ModItems.CHARON_OBOL),
					new Drop(0.015f, ModItems.XIRANG_SACK))),
			// Ruinas oceánicas cálidas: templos hundidos.
			Map.entry(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY, List.of(
					new Drop(0.015f, ModItems.MEDUSA_HEAD),
					new Drop(0.010f, ModItems.DRAGON_PEARL))),
			// Fortaleza del Nether: el reino de Hades.
			Map.entry(BuiltInLootTables.NETHER_BRIDGE, List.of(
					new Drop(0.040f, ModItems.HADES_HELM),
					new Drop(0.050f, ModItems.CHARON_OBOL),
					new Drop(0.040f, ModItems.HOU_YI_BOW))),
			// Fortaleza: el laberinto.
			Map.entry(BuiltInLootTables.STRONGHOLD_CORRIDOR, List.of(
					new Drop(0.060f, ModItems.ARIADNE_THREAD))),
			Map.entry(BuiltInLootTables.STRONGHOLD_LIBRARY, List.of(
					new Drop(0.060f, ModItems.ARIADNE_THREAD),
					new Drop(0.040f, ModItems.SIBYLLINE_BOOKS))),
			// Cámaras de desafío: las pruebas del héroe.
			Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_REWARD, List.of(
					new Drop(0.030f, ModItems.NEMEAN_LION_SKIN),
					new Drop(0.030f, ModItems.ATHENA_AEGIS))),
			// Ciudad antigua: el descenso de Orfeo.
			// Ciudad antigua: el descenso de Orfeo y el río del inframundo.
			Map.entry(BuiltInLootTables.ANCIENT_CITY, List.of(
					new Drop(0.020f, ModItems.TALOS_NAIL),
					new Drop(0.030f, ModItems.DAINSLEIF),
					new Drop(0.030f, ModItems.ORPHEUS_LYRE),
					new Drop(0.020f, ModItems.STYX_WATER),
					new Drop(0.030f, ModItems.GLEIPNIR))),
			// Ruinas oceánicas frías: el mar donde cayó Ícaro (grava sospechosa).
			Map.entry(BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY, List.of(
					new Drop(0.015f, ModItems.ICARUS_WINGS))),
			// Casas de pastor y tesoros de naufragio (el Argo).
			Map.entry(BuiltInLootTables.VILLAGE_SHEPHERD, List.of(
					new Drop(0.040f, ModItems.GOLDEN_FLEECE))),
			Map.entry(BuiltInLootTables.SHIPWRECK_TREASURE, List.of(
					new Drop(0.030f, ModItems.ACHILLES_SHIELD),
					new Drop(0.020f, ModItems.GOLDEN_FLEECE))),
			// Bastiones: la puerta del Nether que guarda Cerbero.
			Map.entry(BuiltInLootTables.BASTION_TREASURE, List.of(
					new Drop(0.020f, ModItems.TYRFING),
					new Drop(0.030f, ModItems.CERBERUS_COLLAR),
					new Drop(0.030f, ModItems.ANDVARANAUT))),
			Map.entry(BuiltInLootTables.BASTION_OTHER, List.of(
					new Drop(0.030f, ModItems.CERBERUS_COLLAR))),
			Map.entry(BuiltInLootTables.BASTION_BRIDGE, List.of(
					new Drop(0.030f, ModItems.CERBERUS_COLLAR))),
			Map.entry(BuiltInLootTables.BASTION_HOGLIN_STABLE, List.of(
					new Drop(0.030f, ModItems.CERBERUS_COLLAR))),
			// Armeros de aldea: Gram, la espada que reforjó el herrero Regin.
			Map.entry(BuiltInLootTables.VILLAGE_WEAPONSMITH, List.of(
					new Drop(0.020f, ModItems.GRAM))),
			// Mansiones del bosque: muy rara.
			Map.entry(BuiltInLootTables.WOODLAND_MANSION, List.of(
					new Drop(0.030f, ModItems.FREYR_SWORD),
					new Drop(0.030f, ModItems.MISTLETOE_DART),
					new Drop(0.050f, ModItems.PANDORA_JAR))),
			// Portales en ruinas: el fuego robado.
			// Portales en ruinas: el fuego robado y las puertas de Jano.
			Map.entry(BuiltInLootTables.RUINED_PORTAL, List.of(
					new Drop(0.030f, ModItems.PROMETHEUS_FENNEL),
					new Drop(0.030f, ModItems.JANUS_KEY))),
			// ---- Roma ----
			// Casas de aldeas de taiga: los lobos.
			Map.entry(BuiltInLootTables.VILLAGE_TAIGA_HOUSE, List.of(
					new Drop(0.030f, ModItems.FALCON_CLOAK),
					new Drop(0.040f, ModItems.WOLF_BULLA),
					new Drop(0.030f, ModItems.ODRERIR))),
			// Puestos de saqueadores: la guerra de Marte.
			Map.entry(BuiltInLootTables.PILLAGER_OUTPOST, List.of(
					new Drop(0.030f, ModItems.APPLE_OF_DISCORD),
					new Drop(0.040f, ModItems.ANCILE),
					new Drop(0.040f, ModItems.GJALLARHORN))),
			// Templo de aldea (clérigo): la ciudad que no cae.
			Map.entry(BuiltInLootTables.VILLAGE_TEMPLE, List.of(
					new Drop(0.030f, ModItems.PALLADIUM))),
			// Casas de aldeas de llanura: el hogar de Vesta.
			Map.entry(BuiltInLootTables.VILLAGE_PLAINS_HOUSE, List.of(
					new Drop(0.030f, ModItems.VESTA_LAMP))),
			// Templos de la jungla: las aves de los augures.
			Map.entry(BuiltInLootTables.JUNGLE_TEMPLE, List.of(
					new Drop(0.030f, ModItems.GOLDEN_BRIDLE),
					new Drop(0.040f, ModItems.AUGUR_LITUUS),
					new Drop(0.040f, ModItems.MONKEY_KING_HAIRS))),
			// ---- Nórdicos ----
			// Bóvedas ominosas de las cámaras de desafío: el martillo de Thor.
			Map.entry(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS, List.of(
					new Drop(0.015f, ModItems.ZEUS_THUNDERBOLT),
					new Drop(0.020f, ModItems.MJOLNIR))),
			// Minas abandonadas: las forjas de los enanos.
			Map.entry(BuiltInLootTables.ABANDONED_MINESHAFT, List.of(
					new Drop(0.030f, ModItems.JARNGREIPR),
					new Drop(0.020f, ModItems.BRISINGAMEN))),
			// Iglús: el norte helado.
			Map.entry(BuiltInLootTables.IGLOO_CHEST, List.of(
					new Drop(0.060f, ModItems.MEGINGJORD))),
			// Ciudades del End: el reino de los dioses en lo alto.
			Map.entry(BuiltInLootTables.END_CITY_TREASURE, List.of(
					new Drop(0.030f, ModItems.GUNGNIR),
					new Drop(0.030f, ModItems.ODIN_RAVENS),
					new Drop(0.030f, ModItems.CHANGE_ELIXIR))),
			// Tesoros enterrados: el oro de los enanos.
			Map.entry(BuiltInLootTables.BURIED_TREASURE, List.of(
					new Drop(0.060f, ModItems.DRAUPNIR))),
			// Casas de aldeas nevadas: la cesta de Iðunn.
			Map.entry(BuiltInLootTables.VILLAGE_SNOWY_HOUSE, List.of(
					new Drop(0.040f, ModItems.IDUNN_APPLES))),
			// Casas de aldeas de sabana: los caballos.
			Map.entry(BuiltInLootTables.VILLAGE_SAVANNA_HOUSE, List.of(
					new Drop(0.040f, ModItems.SLEIPNIR_HORSESHOE))),
			// Provisiones de naufragios: el barco de Freyr.
			Map.entry(BuiltInLootTables.SHIPWRECK_SUPPLY, List.of(
					new Drop(0.040f, ModItems.SKIDBLADNIR))),
			// Cruces de la fortaleza: la cabeza que guarda lo oculto.
			Map.entry(BuiltInLootTables.STRONGHOLD_CROSSING, List.of(
					new Drop(0.040f, ModItems.MIMIR_HEAD))),
			// ---- China ----
			// Ruinas submarinas: el palacio del Rey Dragón del Mar del Este.
			Map.entry(BuiltInLootTables.UNDERWATER_RUIN_BIG, List.of(
					new Drop(0.040f, ModItems.RUYI_JINGU_BANG))),
			Map.entry(BuiltInLootTables.UNDERWATER_RUIN_SMALL, List.of(
					new Drop(0.030f, ModItems.DRAGON_PEARL))),
			// Mazmorras: las cuevas de los demonios Cuerno de Oro y Cuerno de Plata.
			Map.entry(BuiltInLootTables.SIMPLE_DUNGEON, List.of(
					new Drop(0.030f, ModItems.PURPLE_GOLD_GOURD),
					new Drop(0.040f, ModItems.FU_TALISMANS))),
			// Ruinas de los senderos (arqueología común): las piedras que sobraron del cielo remendado.
			Map.entry(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON, List.of(
					new Drop(0.010f, ModItems.FIVE_COLOR_STONE))));

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
