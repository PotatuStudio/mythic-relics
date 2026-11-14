package io.github.potatustudio.mythicrelics.relic;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Ciclo de vida del estado del mod en memoria. Los mapas {@code static} sobreviven a cerrar un mundo y abrir otro en el
 * mismo juego, y muchos guardan "hasta qué tick" con el tiempo del mundo anterior. Cada sistema registra aquí:
 * <ul>
 *   <li>qué hacer cuando un jugador se desconecta ({@link #onQuit}): deshacer lo que se guarda con el jugador, como el
 *       vuelo del halcón, y olvidarlo;</li>
 *   <li>qué deshacer en el mundo antes de cerrarlo ({@link #onStopping}): fogatas, contornos, etc.;</li>
 *   <li>qué vaciar al cerrar el servidor ({@link #onStopped}).</li>
 * </ul>
 */
public final class ServerState {
	private static final List<Consumer<ServerPlayer>> QUIT = new ArrayList<>();
	private static final List<Consumer<MinecraftServer>> STOPPING = new ArrayList<>();
	private static final List<Runnable> STOPPED = new ArrayList<>();

	private ServerState() {
	}

	public static void init() {
		PlayerEvent.PLAYER_QUIT.register(player -> QUIT.forEach(c -> c.accept(player)));
		LifecycleEvent.SERVER_STOPPING.register(server -> STOPPING.forEach(c -> c.accept(server)));
		LifecycleEvent.SERVER_STOPPED.register(server -> STOPPED.forEach(Runnable::run));
	}

	public static void onQuit(Consumer<ServerPlayer> action) {
		QUIT.add(action);
	}

	public static void onStopping(Consumer<MinecraftServer> action) {
		STOPPING.add(action);
	}

	public static void onStopped(Runnable action) {
		STOPPED.add(action);
	}

	/** Atajo: mapas por UUID de jugador que se olvidan al desconectarse y se vacían al cerrar. */
	@SafeVarargs
	public static void perPlayer(Map<UUID, ?>... maps) {
		onQuit(p -> {
			for (Map<UUID, ?> m : maps) {
				m.remove(p.getUUID());
			}
		});
		onStopped(() -> {
			for (Map<UUID, ?> m : maps) {
				m.clear();
			}
		});
	}

	/** Atajo: colecciones que solo se vacían al cerrar el servidor. */
	public static void clearOnStop(Object... collections) {
		onStopped(() -> {
			for (Object c : collections) {
				if (c instanceof Map<?, ?> m) {
					m.clear();
				} else if (c instanceof Collection<?> col) {
					col.clear();
				}
			}
		});
	}
}
