package io.github.potatustudio.mythicrelics.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import java.util.Optional;
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
import net.minecraft.world.item.component.UseCooldown;

/** Ítems y pestaña creativa del mod. Los espacios de Trinkets se asignan con tags en data/trinkets. */
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

	public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register("main", () -> CreativeTabRegistry.create(builder -> builder
			.title(Component.translatable("itemGroup.mythicrelics.main"))
			.icon(() -> new ItemStack(ANKH.get()))
			.displayItems((parameters, output) -> {
				for (RegistrySupplier<Item> item : ITEMS) {
					output.accept(item.get());
				}
			})));

	private ModItems() {
	}

	private static RegistrySupplier<Item> relic(String path, UnaryOperator<Item.Properties> properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, MythicRelics.id(path));
		return ITEMS.register(path, () -> new RelicItem(properties.apply(new Item.Properties().stacksTo(1)).setId(key)));
	}

	public static void init() {
		ITEMS.register();
		TABS.register();
	}
}
