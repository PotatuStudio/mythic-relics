package io.github.potatustudio.mythicrelics.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.core.GlobalPos;
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

	/** Punto de inicio atado con el hilo de Ariadna (dimensión y bloque). */
	public static final RegistrySupplier<DataComponentType<GlobalPos>> ARIADNE_ANCHOR = COMPONENTS.register("ariadne_anchor",
			() -> DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build());

	/** Manzanas que quedan en la cesta de Iðunn (si falta, la cesta está llena). */
	public static final RegistrySupplier<DataComponentType<Integer>> IDUNN_APPLES = COMPONENTS.register("idunn_apples",
			() -> DataComponentType.<Integer>builder().persistent(Codec.intRange(0, 64)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

	/** Último día de Minecraft en que creció una manzana de Iðunn. */
	public static final RegistrySupplier<DataComponentType<Long>> IDUNN_DAY = COMPONENTS.register("idunn_day",
			() -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

	/** Rama del melocotonero: tick del juego en que se comió el último melocotón (si falta, hay uno maduro). */
	public static final RegistrySupplier<DataComponentType<Long>> PEACH_GROWING = COMPONENTS.register("peach_growing",
			() -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

	/** Kibi dango que quedan en la bolsa de Momotarō (si falta, está llena). */
	public static final RegistrySupplier<DataComponentType<Integer>> KIBI_DANGO = COMPONENTS.register("kibi_dango",
			() -> DataComponentType.<Integer>builder().persistent(Codec.intRange(0, 64)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

	/** Último día de Minecraft en que se rellenó la bolsa de kibi dango. */
	public static final RegistrySupplier<DataComponentType<Long>> KIBI_DAY = COMPONENTS.register("kibi_day",
			() -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());

	private ModComponents() {
	}

	public static void init() {
		COMPONENTS.register();
	}
}
