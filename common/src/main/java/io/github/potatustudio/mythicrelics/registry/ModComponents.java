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

	/** Puntos de experiencia escritos en la paleta de escriba de Thot. Se guardan con el ítem y viajan al cliente. */
	public static final RegistrySupplier<DataComponentType<Integer>> WRITTEN_POINTS = COMPONENTS.register("written_points",
			() -> DataComponentType.<Integer>builder().persistent(Codec.intRange(0, 100000)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

	private ModComponents() {
	}

	public static void init() {
		COMPONENTS.register();
	}
}
