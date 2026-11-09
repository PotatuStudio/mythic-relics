package io.github.potatustudio.mythicrelics.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

/** Componentes de datos de los ítems del mod. */
public final class ModComponents {
	private static final DeferredRegister<DataComponentType<?>> COMPONENTS =
			DeferredRegister.create(MythicRelics.MOD_ID, Registries.DATA_COMPONENT_TYPE);

	/** Niveles de experiencia escritos en la paleta de escriba de Thot. Se guardan con el ítem y viajan al cliente. */
	public static final RegistrySupplier<DataComponentType<Integer>> WRITTEN_LEVELS = COMPONENTS.register("written_levels",
			() -> DataComponentType.<Integer>builder().persistent(Codec.intRange(0, 1000)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

	private ModComponents() {
	}

	public static void init() {
		COMPONENTS.register();
	}
}
