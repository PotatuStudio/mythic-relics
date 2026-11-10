package io.github.potatustudio.mythicrelics.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.china.BananaLeafFanItem;
import io.github.potatustudio.mythicrelics.china.ChangeElixirItem;
import io.github.potatustudio.mythicrelics.china.DragonPearlItem;
import io.github.potatustudio.mythicrelics.china.HouYiBowItem;
import io.github.potatustudio.mythicrelics.china.MonkeyHairsItem;
import io.github.potatustudio.mythicrelics.china.PurpleGoldGourdItem;
import io.github.potatustudio.mythicrelics.china.RuyiJinguBangItem;
import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.egypt.OsirisCrookItem;
import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.egypt.ThothPaletteItem;
import io.github.potatustudio.mythicrelics.greece.AriadneThreadItem;
import io.github.potatustudio.mythicrelics.greece.MedusaHeadItem;
import io.github.potatustudio.mythicrelics.greece.OrpheusLyreItem;
import io.github.potatustudio.mythicrelics.greece.PandoraJarItem;
import io.github.potatustudio.mythicrelics.greece.PrometheusFennelItem;
import io.github.potatustudio.mythicrelics.greece.StyxWaterItem;
import io.github.potatustudio.mythicrelics.norse.GjallarhornItem;
import io.github.potatustudio.mythicrelics.norse.GleipnirItem;
import io.github.potatustudio.mythicrelics.norse.IdunnApplesItem;
import io.github.potatustudio.mythicrelics.norse.OdinRavensItem;
import io.github.potatustudio.mythicrelics.norse.OdrerirItem;
import io.github.potatustudio.mythicrelics.norse.SkidbladnirItem;
import io.github.potatustudio.mythicrelics.norse.SleipnirHorseshoeItem;
import io.github.potatustudio.mythicrelics.norse.ThrownRelicItem;
import io.github.potatustudio.mythicrelics.rome.AugurLituusItem;
import io.github.potatustudio.mythicrelics.rome.JanusKeyItem;
import io.github.potatustudio.mythicrelics.rome.PalladiumItem;
import io.github.potatustudio.mythicrelics.rome.SibyllineBooksItem;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.UseCooldown;

/**
 * Ítems y pestaña creativa del mod. Los espacios de accesorio se asignan con tags en cada loader: data/trinkets en Fabric
 * y data/curios en NeoForge.
 */
public final class ModItems {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MythicRelics.MOD_ID, Registries.ITEM);
	private static final DeferredRegister<CreativeModeTab> TABS =
			DeferredRegister.create(MythicRelics.MOD_ID, Registries.CREATIVE_MODE_TAB);

	// Egipto, primera tanda.
	public static final RegistrySupplier<Item> GOLDEN_SCARAB = relic("golden_scarab", p -> p.rarity(Rarity.RARE));
	public static final RegistrySupplier<Item> EYE_OF_HORUS = relic("eye_of_horus", p -> p.rarity(Rarity.RARE)
			// El grupo de enfriamiento hace que el ícono muestre la recarga de la curación.
			.component(DataComponents.USE_COOLDOWN, new UseCooldown(0.05f, Optional.of(EgyptRelics.EYE_COOLDOWN_GROUP))));
	public static final RegistrySupplier<Item> ANKH = relic("ankh", p -> p.rarity(Rarity.UNCOMMON));
	public static final RegistrySupplier<Item> MUMMY_WRAPS = relic("mummy_wraps", p -> p.rarity(Rarity.UNCOMMON));
	// Set de Anubis.
	public static final RegistrySupplier<Item> ANUBIS_SCEPTER = relic("anubis_scepter", p -> p.rarity(Rarity.EPIC)
			.sword(ToolMaterial.GOLD, 3.0f, -2.4f));
	public static final RegistrySupplier<Item> JACKAL_MASK = relic("jackal_mask", p -> p.rarity(Rarity.RARE));
	public static final RegistrySupplier<Item> JACKAL_RING = relic("jackal_ring", p -> p.rarity(Rarity.RARE));

	// Egipto, segunda tanda.
	public static final RegistrySupplier<Item> RA_DISK = relic("ra_disk", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> BASTET_AMULET = relic("bastet_amulet", p -> p.rarity(Rarity.RARE));
	public static final RegistrySupplier<Item> THOTH_PALETTE = register("thoth_palette", ThothPaletteItem::new, p -> p.rarity(Rarity.RARE));
	public static final RegistrySupplier<Item> OSIRIS_CROOK = register("osiris_crook", OsirisCrookItem::new, p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> OSIRIS_FLAIL = relic("osiris_flail", p -> p.rarity(Rarity.EPIC)
			.sword(ToolMaterial.GOLD, 2.0f, -2.8f)
			// El grupo de enfriamiento hace que el ícono del mayal muestre la recarga del renacer.
			.component(DataComponents.USE_COOLDOWN, new UseCooldown(0.05f, Optional.of(EgyptGods.OSIRIS_REBIRTH_GROUP))));
	public static final RegistrySupplier<Item> SEKHMET_CLAWS = relic("sekhmet_claws", p -> p.rarity(Rarity.EPIC));

	// Grecia, primera tanda.
	public static final RegistrySupplier<Item> HERMES_SANDALS = relic("hermes_sandals", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> HADES_HELM = relic("hades_helm", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> ATHENA_AEGIS = relic("athena_aegis", p -> p.rarity(Rarity.RARE));
	public static final RegistrySupplier<Item> MEDUSA_HEAD = register("medusa_head", MedusaHeadItem::new, p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> ARIADNE_THREAD = register("ariadne_thread", AriadneThreadItem::new, p -> p.rarity(Rarity.RARE));
	public static final RegistrySupplier<Item> CHARON_OBOL = register("charon_obol", RelicItem::new, p -> p.rarity(Rarity.RARE).stacksTo(16));
	public static final RegistrySupplier<Item> ORPHEUS_LYRE = register("orpheus_lyre", OrpheusLyreItem::new, p -> p.rarity(Rarity.EPIC));

	// Grecia, segunda tanda.
	public static final RegistrySupplier<Item> ICARUS_WINGS = relic("icarus_wings", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> GOLDEN_FLEECE = relic("golden_fleece", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> CERBERUS_COLLAR = relic("cerberus_collar", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> PANDORA_JAR = register("pandora_jar", PandoraJarItem::new, p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> STYX_WATER = register("styx_water", StyxWaterItem::new, p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> PROMETHEUS_FENNEL = register("prometheus_fennel", PrometheusFennelItem::new,
			p -> p.rarity(Rarity.RARE));

	// Roma, primera tanda.
	public static final RegistrySupplier<Item> WOLF_BULLA = relic("wolf_bulla", p -> p.rarity(Rarity.RARE));
	// Escudo: bloquea como el vanilla (90°, 0,25 s) pero sin durabilidad, así que nunca se rompe.
	public static final RegistrySupplier<Item> ANCILE = relic("ancile", p -> p.rarity(Rarity.EPIC)
			.component(DataComponents.BLOCKS_ATTACKS, new BlocksAttacks(0.25f, 1.0f,
					List.of(new BlocksAttacks.DamageReduction(90.0f, Optional.empty(), 0.0f, 1.0f)),
					new BlocksAttacks.ItemDamageFunction(3.0f, 1.0f, 1.0f), Optional.empty(),
					Optional.of(SoundEvents.SHIELD_BLOCK), Optional.of(SoundEvents.SHIELD_BREAK))));
	public static final RegistrySupplier<Item> PALLADIUM = register("palladium",
			p -> new PalladiumItem(ModBlocks.PALLADIUM.get(), p), p -> p.rarity(Rarity.EPIC).useItemDescriptionPrefix());
	public static final RegistrySupplier<Item> JANUS_KEY = register("janus_key", JanusKeyItem::new, p -> p.rarity(Rarity.RARE));
	public static final RegistrySupplier<Item> VESTA_LAMP = relic("vesta_lamp", p -> p.rarity(Rarity.RARE));
	public static final RegistrySupplier<Item> SIBYLLINE_BOOKS = register("sibylline_books", SibyllineBooksItem::new,
			p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> AUGUR_LITUUS = register("augur_lituus", AugurLituusItem::new, p -> p.rarity(Rarity.RARE));

	// Nórdicos, primera tanda. Las armas lanzables no se rompen (vuelven siempre a la mano).
	public static final RegistrySupplier<Item> MJOLNIR = register("mjolnir", p -> new ThrownRelicItem(p, false),
			p -> p.rarity(Rarity.EPIC).axe(ToolMaterial.IRON, 6.0f, -3.1f).component(DataComponents.UNBREAKABLE, Unit.INSTANCE));
	public static final RegistrySupplier<Item> JARNGREIPR = relic("jarngreipr", p -> p.rarity(Rarity.RARE));
	public static final RegistrySupplier<Item> MEGINGJORD = relic("megingjord", p -> p.rarity(Rarity.RARE));
	public static final RegistrySupplier<Item> GUNGNIR = register("gungnir", p -> new ThrownRelicItem(p, true),
			p -> p.rarity(Rarity.EPIC).sword(ToolMaterial.IRON, 4.0f, -2.9f).component(DataComponents.UNBREAKABLE, Unit.INSTANCE));
	public static final RegistrySupplier<Item> DRAUPNIR = relic("draupnir", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> GJALLARHORN = register("gjallarhorn", GjallarhornItem::new, p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> GLEIPNIR = register("gleipnir", GleipnirItem::new, p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> IDUNN_APPLES = register("idunn_apples", IdunnApplesItem::new, p -> p.rarity(Rarity.EPIC));

	// Nórdicos, segunda tanda.
	public static final RegistrySupplier<Item> SLEIPNIR_HORSESHOE = register("sleipnir_horseshoe", SleipnirHorseshoeItem::new,
			p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> ODIN_RAVENS = register("odin_ravens", OdinRavensItem::new, p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> BRISINGAMEN = relic("brisingamen", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> SKIDBLADNIR = register("skidbladnir", SkidbladnirItem::new, p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> MIMIR_HEAD = relic("mimir_head", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> ANDVARANAUT = relic("andvaranaut", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> ODRERIR = register("odrerir", OdrerirItem::new, p -> p.rarity(Rarity.RARE));

	// China, primera tanda. Las armas no se rompen.
	public static final RegistrySupplier<Item> RUYI_JINGU_BANG = register("ruyi_jingu_bang", RuyiJinguBangItem::new,
			p -> p.rarity(Rarity.EPIC).sword(ToolMaterial.IRON, 6.0f, -3.0f).component(DataComponents.UNBREAKABLE, Unit.INSTANCE));
	public static final RegistrySupplier<Item> MONKEY_KING_HAIRS = register("monkey_king_hairs", MonkeyHairsItem::new,
			p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> PURPLE_GOLD_GOURD = register("purple_gold_gourd", PurpleGoldGourdItem::new,
			p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> BANANA_LEAF_FAN = register("banana_leaf_fan", BananaLeafFanItem::new,
			p -> p.rarity(Rarity.EPIC).sword(ToolMaterial.WOOD, 5.0f, -2.4f).component(DataComponents.UNBREAKABLE, Unit.INSTANCE));
	public static final RegistrySupplier<Item> DRAGON_PEARL = register("dragon_pearl", DragonPearlItem::new, p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> WIND_FIRE_WHEELS = relic("wind_fire_wheels", p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> HOU_YI_BOW = register("hou_yi_bow", HouYiBowItem::new,
			p -> p.rarity(Rarity.EPIC).durability(384).enchantable(1).component(DataComponents.UNBREAKABLE, Unit.INSTANCE));
	public static final RegistrySupplier<Item> CHANGE_ELIXIR = register("change_elixir", ChangeElixirItem::new, p -> p.rarity(Rarity.EPIC));
	public static final RegistrySupplier<Item> FIVE_COLOR_STONE = relic("five_color_stone", p -> p.rarity(Rarity.RARE));

	/** Emblema del mod (escudo con una cultura por cuarto): solo es el ícono de la pestaña creativa, no se consigue. */
	public static final RegistrySupplier<Item> EMBLEM = register("emblem", Item::new, p -> p);

	public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register("main", () -> CreativeTabRegistry.create(builder -> builder
			.title(Component.translatable("itemGroup.mythicrelics.main"))
			.icon(() -> new ItemStack(EMBLEM.get()))
			.displayItems((parameters, output) -> {
				for (RegistrySupplier<Item> item : ITEMS) {
					if (item != EMBLEM) {
						output.accept(item.get());
					}
				}
			})));

	private ModItems() {
	}

	private static RegistrySupplier<Item> relic(String path, UnaryOperator<Item.Properties> properties) {
		return register(path, RelicItem::new, properties);
	}

	private static RegistrySupplier<Item> register(String path, Function<Item.Properties, ? extends Item> factory,
			UnaryOperator<Item.Properties> properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, MythicRelics.id(path));
		return ITEMS.register(path, () -> factory.apply(properties.apply(new Item.Properties().stacksTo(1)).setId(key)));
	}

	public static void init() {
		ITEMS.register();
		TABS.register();
	}
}
