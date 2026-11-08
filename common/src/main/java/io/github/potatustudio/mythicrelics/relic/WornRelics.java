package io.github.potatustudio.mythicrelics.relic;

import java.util.Objects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

/**
 * Consulta de accesorios puestos, independiente del loader: en Fabric la responde Trinkets y en NeoForge, Curios (cada
 * loader usa su biblioteca de accesorios estándar). El inicializador de cada loader instala su implementación antes de
 * {@code MythicRelics.init()}.
 */
public final class WornRelics {
	/** Implementación de cada loader. */
	public interface Provider {
		boolean isWearing(LivingEntity entity, Item item);
	}

	private static Provider provider;

	private WornRelics() {
	}

	public static void install(Provider implementation) {
		provider = Objects.requireNonNull(implementation);
	}

	public static boolean isWearing(LivingEntity entity, Item item) {
		if (provider == null) {
			throw new IllegalStateException("Mythic Relics: falta instalar el proveedor de accesorios del loader");
		}
		return provider.isWearing(entity, item);
	}
}
