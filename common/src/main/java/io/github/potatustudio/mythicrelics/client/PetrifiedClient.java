package io.github.potatustudio.mythicrelics.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.github.potatustudio.mythicrelics.network.PetrifyPayload;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

/**
 * Qué mobs se dibujan de piedra: los petrificados por la cabeza de Medusa (los avisa el servidor) y los modelos que usan
 * las estatuas ({@code StoneStatueRenderer}). Lo lee {@code LivingEntityRendererMixin} para cambiar la textura por piedra.
 */
public final class PetrifiedClient {
	public static final Identifier STONE = Identifier.withDefaultNamespace("textures/block/stone.png");
	private static final IntSet PETRIFIED = new IntOpenHashSet();
	private static final Set<Entity> STATUE_MODELS = Collections.newSetFromMap(new WeakHashMap<>());

	private PetrifiedClient() {
	}

	public static void init() {
		NetworkManager.registerReceiver(NetworkManager.s2c(), PetrifyPayload.TYPE, PetrifyPayload.CODEC,
				(payload, context) -> context.queue(() -> {
					if (payload.on()) {
						PETRIFIED.add(payload.entityId());
					} else {
						PETRIFIED.remove(payload.entityId());
					}
				}));
		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> PETRIFIED.clear());
	}

	public static void markStatueModel(Entity entity) {
		STATUE_MODELS.add(entity);
	}

	public static boolean isStone(Entity entity) {
		return PETRIFIED.contains(entity.getId()) || STATUE_MODELS.contains(entity);
	}
}
