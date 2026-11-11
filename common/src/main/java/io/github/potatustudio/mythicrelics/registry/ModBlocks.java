package io.github.potatustudio.mythicrelics.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.japan.ZashikiWarashiBlock;
import io.github.potatustudio.mythicrelics.rome.PalladiumBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/** Bloques del mod. */
public final class ModBlocks {
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MythicRelics.MOD_ID, Registries.BLOCK);

	/** Paladio: estatuilla de madera de Palas (como la madera, pero no se quema). */
	public static final RegistrySupplier<Block> PALLADIUM = BLOCKS.register("palladium", () -> new PalladiumBlock(
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0f, 6.0f).sound(SoundType.WOOD).noOcclusion()
					.setId(ResourceKey.create(Registries.BLOCK, MythicRelics.id("palladium")))));

	/** Muñeca de zashiki-warashi: muñeca de tela y madera. */
	public static final RegistrySupplier<Block> ZASHIKI_WARASHI = BLOCKS.register("zashiki_warashi", () -> new ZashikiWarashiBlock(
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(1.0f, 6.0f).sound(SoundType.WOOL).noOcclusion()
					.setId(ResourceKey.create(Registries.BLOCK, MythicRelics.id("zashiki_warashi")))));

	private ModBlocks() {
	}

	public static void init() {
		BLOCKS.register();
	}
}
