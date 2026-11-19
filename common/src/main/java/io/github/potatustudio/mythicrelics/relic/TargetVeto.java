package io.github.potatustudio.mythicrelics.relic;

import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.zombie.Husk;
import org.jspecify.annotations.Nullable;

/**
 * Objetivos que un mob no puede tomar mientras dure el efecto de una reliquia. Lo consulta {@code MobMixin} al fijar el
 * objetivo y al leerlo, así el mob ni siquiera llega a apuntar (antes se limpiaba cada pocos ticks y en el intervalo los
 * esqueletos alcanzaban a disparar).
 * <ul>
 *   <li>Set de Anubis: los no-muertos no atacan a quien lo lleva, salvo que los haya golpeado hace poco.</li>
 *   <li>Vendas de momia: los husks no atacan a quien las lleva.</li>
 *   <li>Lira de Orfeo: los hostiles dormidos por la música no atacan a nadie.</li>
 * </ul>
 */
public final class TargetVeto {
	/** Ticks tras un golpe del jugador en que el no-muerto sí puede devolverlo (set de Anubis). */
	public static final int ANUBIS_PROVOKED_TICKS = 200;
	/** Mob dormido por la lira -> tick del juego en que despierta. */
	private static final Map<UUID, Long> LULLED = new HashMap<>();

	private TargetVeto() {
	}

	/**
	 * Los mobs con cerebro (zoglin, hoglin, piglin…) no usan {@code getTarget}: guardan el objetivo en la memoria
	 * {@code ATTACK_TARGET}. Cada 2 ticks, cerca de quien tiene un veto (o si hay mobs dormidos por la lira), se les
	 * borra el objetivo negado.
	 */
	public static void init() {
		TickEvent.SERVER_POST.register(server -> {
			if (server.getTickCount() % 2 != 0) {
				return;
			}
			boolean lulled = !LULLED.isEmpty();
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				if (!lulled && !WornRelics.isWearing(player, ModItems.MUMMY_WRAPS) && !RelicSets.ANUBIS.isComplete(player)) {
					continue;
				}
				for (Mob mob : player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(BRAIN_RADIUS),
						m -> m.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET))) {
					LivingEntity target = mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
					if (blocks(mob, target)) {
						mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
					}
				}
			}
		});
	}

	/** Radio en que se revisan los mobs con cerebro (el de seguimiento de los hostiles es menor). */
	private static final double BRAIN_RADIUS = 32.0;

	public static void lull(Mob mob, long until) {
		// Se poda de vez en cuando: los mobs que mueren o se descargan no vuelven a consultar y dejaban su entrada.
		if (LULLED.size() > 256) {
			long now = mob.level().getGameTime();
			LULLED.values().removeIf(u -> u <= now);
		}
		LULLED.put(mob.getUUID(), until);
	}

	/** Solo del servidor (blocks() no consulta en el cliente). */
	public static boolean isLulled(Mob mob) {
		Long until = LULLED.get(mob.getUUID());
		if (until == null) {
			return false;
		}
		if (until <= mob.level().getGameTime()) {
			LULLED.remove(mob.getUUID());
			return false;
		}
		return true;
	}

	/** ¿Se le niega a {@code mob} tomar a {@code target} como objetivo? Solo en el servidor. */
	public static boolean blocks(Mob mob, @Nullable LivingEntity target) {
		if (target == null || mob.level().isClientSide()) {
			return false;
		}
		if (mob instanceof Enemy && isLulled(mob)) {
			return true;
		}
		if (!(target instanceof ServerPlayer player)) {
			return false;
		}
		if (mob instanceof Husk && WornRelics.isWearing(player, ModItems.MUMMY_WRAPS)) {
			return true;
		}
		return mob.typeHolder().is(EntityTypeTags.UNDEAD) && RelicSets.ANUBIS.isComplete(player)
				&& !(mob.getLastHurtByMob() == player && mob.tickCount - mob.getLastHurtByMobTimestamp() < ANUBIS_PROVOKED_TICKS);
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		ServerState.clearOnStop(LULLED);
	}
}
