package io.github.potatustudio.mythicrelics.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.github.potatustudio.mythicrelics.network.PetrifyPayload;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Collections;
import java.util.Set;
import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

/**
 * Qué mobs se dibujan de piedra: los petrificados por la cabeza de Medusa (los avisa el servidor) y los modelos que usan
 * las estatuas ({@code StoneStatueRenderer}). Lo lee {@code LivingEntityRendererMixin} para cambiar la textura por piedra.
 */
public final class PetrifiedClient {
	private static final IntSet PETRIFIED = new IntOpenHashSet();
	/** Mob petrificado -> {tick del cliente en que se petrificó, duración}: con eso se dibuja cada vez más agrietado. */
	private static final it.unimi.dsi.fastutil.ints.Int2ObjectMap<long[]> TIMES = new it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<>();
	/** Piedra lisa, grietas finas, grietas grandes y a punto de romperse (textures/entity/petrified/stone_N.png). */
	private static final Identifier[] STAGES = {MythicRelics.id("textures/entity/petrified/stone_0.png"),
			MythicRelics.id("textures/entity/petrified/stone_1.png"), MythicRelics.id("textures/entity/petrified/stone_2.png"),
			MythicRelics.id("textures/entity/petrified/stone_3.png")};
	/**
	 * Modelos de las estatuas: entidades que nunca entran al mundo y no tienen ID (Entity.hashCode lo pide y se cae), así
	 * que se guardan por identidad, con referencias débiles.
	 */
	private static final Set<Entity> STATUE_MODELS = Collections.newSetFromMap(new com.google.common.collect.MapMaker().weakKeys().makeMap());

	private PetrifiedClient() {
	}

	public static void init() {
		NetworkManager.registerReceiver(NetworkManager.s2c(), PetrifyPayload.TYPE, PetrifyPayload.CODEC,
				(payload, context) -> context.queue(() -> {
					if (payload.on()) {
						PETRIFIED.add(payload.entityId());
						var level = net.minecraft.client.Minecraft.getInstance().level;
						TIMES.put(payload.entityId(), new long[] {level != null ? level.getGameTime() : 0L, Math.max(1, payload.ticks())});
					} else {
						PETRIFIED.remove(payload.entityId());
						TIMES.remove(payload.entityId());
					}
				}));
		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
			PETRIFIED.clear();
			TIMES.clear();
		});
	}

	public static void markStatueModel(Entity entity) {
		STATUE_MODELS.add(entity);
	}

	/** Grado de agrietamiento (0 liso .. 3 a punto de romperse); las estatuas, siempre lisas. */
	public static int stage(Entity entity) {
		if (STATUE_MODELS.contains(entity)) {
			return 0;
		}
		long[] t = TIMES.get(entity.getId());
		if (t == null) {
			return 0;
		}
		double progress = (entity.level().getGameTime() - t[0]) / (double) t[1];
		return progress < 0.35 ? 0 : progress < 0.6 ? 1 : progress < 0.85 ? 2 : 3;
	}

	public static Identifier texture(int stage) {
		return STAGES[Math.max(0, Math.min(3, stage))];
	}

	public static boolean isStone(Entity entity) {
		// Primero los modelos de estatua: no tienen ID.
		return STATUE_MODELS.contains(entity) || PETRIFIED.contains(entity.getId());
	}
}
