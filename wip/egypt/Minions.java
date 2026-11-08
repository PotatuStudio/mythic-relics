package io.github.potatustudio.mythicrelics.minion;

import io.github.potatustudio.mythicrelics.registry.ModAttachments;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

/**
 * Aliados temporales (escarabajos del escarabajo dorado, no-muertos alzados por el báculo de Anubis): pelean contra lo
 * que su dueño golpea o lo que golpea a su dueño, nunca contra él ni sus aliados, y se deshacen al acabar su tiempo.
 * No sobreviven a una recarga del mundo (al cargarse, desaparecen).
 */
public final class Minions {
	private static final int TARGET_MEMORY = 120;
	private static final double SEARCH_RANGE = 16.0;

	private record Minion(UUID owner, int[] ticksLeft) {
	}

	private static final Map<Mob, Minion> MINIONS = Collections.synchronizedMap(new IdentityHashMap<>());

	private Minions() {
	}

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(Minions::tick);
		// Un aliado que se guardó con el mundo no vuelve: se deshace al cargarse.
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (entity.hasAttached(ModAttachments.MINION_OWNER) && !MINIONS.containsKey(entity)) {
				entity.discard();
			}
		});
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((target, source, amount) -> {
			Entity attacker = source.getEntity();
			UUID owner = attacker == null ? null : ownerOf(attacker);
			if (owner == null) {
				return true;
			}
			return !(target.getUUID().equals(owner) || ownerOf(target) != null || isAlly(target, owner));
		});
	}

	public static @Nullable UUID ownerOf(Entity entity) {
		return entity.getAttached(ModAttachments.MINION_OWNER);
	}

	public static int countOf(Player owner) {
		synchronized (MINIONS) {
			return (int) MINIONS.values().stream().filter(m -> m.owner().equals(owner.getUUID())).count();
		}
	}

	/** Invoca un aliado del tipo indicado junto a una posición, por la cantidad de ticks indicada. */
	public static @Nullable Mob summon(ServerLevel level, Player owner, EntityType<? extends Mob> type, double x, double y, double z,
			int ticks, Component name) {
		Mob mob = type.create(level, EntitySpawnReason.MOB_SUMMONED);
		if (mob == null) {
			return null;
		}
		mob.snapTo(x, y, z, level.getRandom().nextFloat() * 360f, 0f);
		mob.setAttached(ModAttachments.MINION_OWNER, owner.getUUID());
		mob.setCustomName(name);
		mob.setPersistenceRequired();
		MINIONS.put(mob, new Minion(owner.getUUID(), new int[] {ticks}));
		level.addFreshEntity(mob);
		level.sendParticles(ParticleTypes.SOUL, x, y + 0.5, z, 12, 0.3, 0.5, 0.3, 0.02);
		return mob;
	}

	private static boolean isAlly(LivingEntity target, UUID owner) {
		if (target instanceof OwnableEntity ownable && ownable.getOwnerReference() != null
				&& owner.equals(ownable.getOwnerReference().getUUID())) {
			return true;
		}
		return target instanceof Player && target.getTeam() != null && target.level() instanceof ServerLevel level
				&& level.getPlayerByUUID(owner) instanceof Player p && target.isAlliedTo(p);
	}

	private static void tick(MinecraftServer server) {
		synchronized (MINIONS) {
			Iterator<Map.Entry<Mob, Minion>> it = MINIONS.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Mob, Minion> e = it.next();
				Mob mob = e.getKey();
				Minion minion = e.getValue();
				if (mob.isRemoved() || !mob.isAlive() || !(mob.level() instanceof ServerLevel level)) {
					it.remove();
					continue;
				}
				if (--minion.ticksLeft()[0] <= 0) {
					level.sendParticles(ParticleTypes.POOF, mob.getX(), mob.getY() + 0.5, mob.getZ(), 10, 0.3, 0.4, 0.3, 0.02);
					mob.discard();
					it.remove();
					continue;
				}
				mob.clearFire();
				Player owner = level.getPlayerByUUID(minion.owner());
				LivingEntity current = mob.getTarget();
				if (current != null && (current == owner || ownerOf(current) != null || isAlly(current, minion.owner()))) {
					mob.setTarget(null);
					current = null;
				}
				if (owner != null && (current == null || !current.isAlive())) {
					LivingEntity next = pick(owner, mob);
					if (next != null) {
						mob.setTarget(next);
					} else if (mob.distanceToSqr(owner) > 9) {
						mob.getNavigation().moveTo(owner, 1.1);
					}
				}
			}
		}
	}

	private static @Nullable LivingEntity pick(Player owner, Mob mob) {
		LivingEntity hit = owner.getLastHurtMob();
		if (valid(owner, mob, hit, owner.getLastHurtMobTimestamp())) {
			return hit;
		}
		LivingEntity attacker = owner.getLastHurtByMob();
		return valid(owner, mob, attacker, owner.getLastHurtByMobTimestamp()) ? attacker : null;
	}

	private static boolean valid(Player owner, Mob mob, @Nullable LivingEntity t, int timestamp) {
		return t != null && t.isAlive() && t != mob && t != owner && owner.tickCount - timestamp < TARGET_MEMORY
				&& ownerOf(t) == null && !isAlly(t, owner.getUUID()) && mob.distanceToSqr(t) < SEARCH_RANGE * SEARCH_RANGE;
	}
}
