package io.github.potatustudio.mythicrelics.relic;

import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;

/**
 * Efectos de un golpe que solo cuentan si el golpe entra. Los modificadores de daño ({@link DamageModifiers}) corren al
 * principio de {@code hurtServer}, antes de que el juego descarte el golpe (frames de invulnerabilidad, escudo,
 * inmunidad): ahí solo se calcula el número, y lo que cuenta o gasta algo (golpes de Gram, la cólera, la recarga de la
 * harpe, el barrido del Ruyi) se deja aquí y se aplica en {@code LIVING_DAMAGE_POST} si el golpe hizo daño.
 * Solo del hilo del servidor.
 */
public final class HitEffects {
	private record Pending(Runnable landed, Runnable missed) {
	}

	private static final Map<LivingEntity, List<Pending>> PENDING = new IdentityHashMap<>();

	private HitEffects() {
	}

	public static void init() {
		EntityEvent.LIVING_DAMAGE_POST.register((entity, source, baseDamage, damageTaken, blocked) -> {
			List<Pending> list = PENDING.remove(entity);
			if (list != null) {
				// Entró si quitó vida, o si no lo bloqueó un escudo (la absorción puede dejar la vida igual).
				boolean landed = damageTaken > 0 || (!blocked && baseDamage > 0);
				list.forEach(p -> (landed ? p.landed() : p.missed()).run());
			}
		});
		// Si el juego descartó el golpe antes de aplicarlo, no hay evento: lo pendiente caduca al final del tick.
		TickEvent.SERVER_POST.register(server -> {
			if (!PENDING.isEmpty()) {
				List<List<Pending>> stale = new ArrayList<>(PENDING.values());
				PENDING.clear();
				stale.forEach(list -> list.forEach(p -> p.missed().run()));
			}
		});
		ServerState.clearOnStop(PENDING);
	}

	/** Al empezar a calcular un golpe nuevo sobre {@code target}: lo pendiente de un golpe anterior no entró. */
	static void begin(LivingEntity target) {
		List<Pending> stale = PENDING.remove(target);
		if (stale != null) {
			stale.forEach(p -> p.missed().run());
		}
	}

	/** {@code landed} corre si este golpe hace daño. */
	public static void onLanded(LivingEntity target, Runnable landed) {
		onLanded(target, landed, () -> {
		});
	}

	/** {@code landed} si el golpe hace daño; si no, {@code missed} (para deshacer una marca puesta de antemano). */
	public static void onLanded(LivingEntity target, Runnable landed, Runnable missed) {
		if (!target.level().isClientSide()) {
			PENDING.computeIfAbsent(target, t -> new ArrayList<>()).add(new Pending(landed, missed));
		}
	}
}
