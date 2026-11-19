package io.github.potatustudio.mythicrelics.relic;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

/**
 * Un estado que deja a un mob sin IA durante un tiempo: Gleipnir (atado), el talismán fu (sellado) y Medusa
 * (petrificado). Los tres comparten la misma mecánica:
 * <ul>
 *   <li>el mob lleva una etiqueta (se guarda con él) y un mapa en memoria dice hasta qué tick dura;</li>
 *   <li>al terminar se suelta; si al cargarse un mob tiene la etiqueta pero no está en el mapa (el servidor se cerró o
 *       el estado venció con el chunk descargado), se suelta en silencio;</li>
 *   <li>la IA se apaga con el primer estado y se devuelve solo cuando no queda ninguno, al valor que tenía antes (un
 *       mob con {@code NoAI:1b} de un mapa sigue sin IA). Así, petrificado y atado a la vez no camina al soltarse uno.</li>
 * </ul>
 * Solo del hilo del servidor.
 */
public final class TimedMobStatus {
	/** Lo que hace cada estado: mientras dura (partículas) y al soltarse con el mob presente. */
	public interface Hooks {
		default void active(ServerLevel level, Mob mob, long now, long until) {
		}

		default void released(ServerLevel level, Mob mob) {
		}
	}

	/** El mob ya no tenía IA antes del primer estado. */
	private static final String HAD_NO_AI = MythicRelics.MOD_ID + ".had_no_ai";
	private static final List<TimedMobStatus> ALL = new ArrayList<>();

	private final String tag;
	private final Hooks hooks;
	private final Map<UUID, Long> until = new HashMap<>();

	public TimedMobStatus(String tag, Hooks hooks) {
		this.tag = tag;
		this.hooks = hooks;
		ALL.add(this);
		TickEvent.SERVER_POST.register(this::tick);
		EntityEvent.ADD.register((entity, level) -> {
			if (!level.isClientSide() && entity instanceof Mob mob && mob.entityTags().contains(tag) && !until.containsKey(mob.getUUID())) {
				unfreeze(mob);
			}
			return EventResult.pass();
		});
		ServerState.clearOnStop(until);
	}

	public String tag() {
		return tag;
	}

	public boolean has(Mob mob) {
		return mob.entityTags().contains(tag);
	}

	/** Si el estado sigue vigente para ese mob (aunque no esté cargado). */
	public boolean isActive(UUID mob) {
		return until.containsKey(mob);
	}

	/** Aplica (o renueva) el estado por {@code ticks}. */
	public void apply(ServerLevel level, Mob mob, int ticks) {
		if (!frozenByAny(mob) && mob.isNoAi()) {
			mob.addTag(HAD_NO_AI);
		}
		mob.setNoAi(true);
		mob.addTag(tag);
		until.put(mob.getUUID(), level.getGameTime() + ticks);
	}

	/** Ticks que le quedan (0 si no lo tiene). */
	public long remaining(Mob mob, long now) {
		Long end = until.get(mob.getUUID());
		return end == null ? 0 : Math.max(0, end - now);
	}

	/** Quita el estado ya (sin los efectos de {@link Hooks#released}). */
	public void clear(Mob mob) {
		until.remove(mob.getUUID());
		unfreeze(mob);
	}

	private void unfreeze(Mob mob) {
		mob.removeTag(tag);
		if (!frozenByAny(mob)) {
			mob.setNoAi(mob.entityTags().contains(HAD_NO_AI));
			mob.removeTag(HAD_NO_AI);
		}
	}

	private static boolean frozenByAny(Mob mob) {
		for (TimedMobStatus status : ALL) {
			if (mob.entityTags().contains(status.tag)) {
				return true;
			}
		}
		return false;
	}

	private void tick(MinecraftServer server) {
		if (until.isEmpty()) {
			return;
		}
		long now = server.overworld().getGameTime();
		Iterator<Map.Entry<UUID, Long>> it = until.entrySet().iterator();
		List<Mob> released = new ArrayList<>();
		while (it.hasNext()) {
			Map.Entry<UUID, Long> e = it.next();
			Mob mob = find(server, e.getKey());
			boolean expired = e.getValue() <= now || (mob != null && !mob.isAlive());
			if (expired) {
				// Si el mob no está cargado, se suelta al cargarse (ver el evento ADD).
				it.remove();
				if (mob != null) {
					released.add(mob);
				}
			} else if (mob != null && mob.level() instanceof ServerLevel level) {
				hooks.active(level, mob, now, e.getValue());
			}
		}
		for (Mob mob : released) {
			unfreeze(mob);
			if (mob.level() instanceof ServerLevel level) {
				hooks.released(level, mob);
			}
		}
	}

	private static Mob find(MinecraftServer server, UUID id) {
		for (ServerLevel level : server.getAllLevels()) {
			if (level.getEntity(id) instanceof Mob mob) {
				return mob;
			}
		}
		return null;
	}
}
