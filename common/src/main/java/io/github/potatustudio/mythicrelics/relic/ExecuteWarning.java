package io.github.potatustudio.mythicrelics.relic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;

/**
 * Aviso de las armas que ejecutan a los malheridos (harpe, cetro de Anubis): con el arma en la mano y lista, los mobs que
 * ya se pueden rematar brillan y tienen chispas encima, y la primera vez que aparece cada uno suena un aviso y se lee una
 * línea sobre la barra. Llamar cada 10 ticks (el brillo dura 15).
 */
public final class ExecuteWarning {
	private static final Map<String, Map<UUID, Set<UUID>>> WARNED = new HashMap<>();

	private ExecuteWarning() {
	}

	/**
	 * @param key      identifica el arma (cada una recuerda sus propios avisos)
	 * @param ready    si el arma está en la mano y sin recarga
	 * @param canKill  qué mobs se pueden rematar ahora
	 * @param message  clave de la línea sobre la barra
	 */
	public static void tick(ServerPlayer player, String key, boolean ready, double radius, Predicate<Mob> canKill, String message) {
		Set<UUID> warned = WARNED.computeIfAbsent(key, k -> new HashMap<>()).computeIfAbsent(player.getUUID(), k -> new HashSet<>());
		if (!ready) {
			warned.clear();
			return;
		}
		ServerLevel level = player.level();
		Set<UUID> now = new HashSet<>();
		for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(radius),
				m -> m.isAlive() && canKill.test(m) && !Allies.isFriend(player, m))) {
			now.add(mob.getUUID());
			mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 15, 0, false, false));
			level.sendParticles(ParticleTypes.CRIT, mob.getX(), mob.getY() + mob.getBbHeight() + 0.3, mob.getZ(), 3, 0.2, 0.1, 0.2, 0.0);
			if (!warned.contains(mob.getUUID())) {
				RelicFx.sound(level, player, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.6f, 0.6f);
				player.sendOverlayMessage(Component.translatable(message).withStyle(ChatFormatting.GOLD));
			}
		}
		warned.retainAll(now);
		warned.addAll(now);
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		io.github.potatustudio.mythicrelics.relic.ServerState.onQuit(p -> WARNED.values().forEach(m -> m.remove(p.getUUID())));
		io.github.potatustudio.mythicrelics.relic.ServerState.clearOnStop(WARNED);
	}
}
