package io.github.potatustudio.mythicrelics.relic;

import dev.architectury.registry.registries.RegistrySupplier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

	/** Respuestas del tick en curso por entidad (servidor y cliente por separado: cada uno en su hilo). */
	private static final Map<UUID, Tick> SERVER_CACHE = new HashMap<>();
	private static final Map<UUID, Tick> CLIENT_CACHE = new HashMap<>();

	private static final class Tick {
		long time;
		final Map<Item, Boolean> answers = new IdentityHashMap<>();
	}

	/**
	 * Si la entidad lleva puesto el ítem. Consultar Trinkets/Curios cuesta (capability, recorrer casillas) y muchos
	 * sistemas preguntan lo mismo varias veces por tick (los mobs, en cada getTarget), así que la respuesta se guarda por
	 * entidad, ítem y tick del juego.
	 */
	public static boolean isWearing(LivingEntity entity, Item item) {
		if (provider == null) {
			throw new IllegalStateException("Mythic Relics: falta instalar el proveedor de accesorios del loader");
		}
		Map<UUID, Tick> cache = entity.level().isClientSide() ? CLIENT_CACHE : SERVER_CACHE;
		long now = entity.level().getGameTime();
		Tick tick = cache.get(entity.getUUID());
		if (tick == null) {
			if (cache.size() > 128) {
				cache.values().removeIf(t -> t.time != now);
			}
			tick = new Tick();
			cache.put(entity.getUUID(), tick);
		}
		if (tick.time != now) {
			tick.time = now;
			tick.answers.clear();
		}
		Boolean known = tick.answers.get(item);
		if (known == null) {
			known = provider.isWearing(entity, item);
			tick.answers.put(item, known);
		}
		return known;
	}

	public static boolean isWearing(LivingEntity entity, RegistrySupplier<Item> item) {
		return isWearing(entity, item.get());
	}

	/** Si el jugador tiene el ítem en la mano principal o en la otra. */
	public static boolean holding(Player player, Item item) {
		return WornRelics.holding(player, item);
	}

	static {
		ServerState.clearOnStop(SERVER_CACHE);
		// El del cliente se poda solo (se descarta todo lo que no es del tick en curso).
	}
}
