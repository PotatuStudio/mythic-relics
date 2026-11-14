package io.github.potatustudio.mythicrelics.minion;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Aliados temporales (escarabajos del escarabajo dorado, chacales del anillo de los chacales): pelean contra lo
 * que su dueño golpea o lo que golpea a su dueño, nunca contra él ni sus aliados, y se deshacen al acabar su tiempo.
 * No sobreviven a una recarga del mundo: llevan la etiqueta {@link #TAG} y, si se cargan desde disco sin estar en el
 * registro en memoria, no se agregan al mundo. El dueño vive solo en memoria (no hace falta guardarlo).
 */
public final class Minions {
	/** Etiqueta de entidad (/tag) que marca a un aliado temporal. */
	public static final String TAG = MythicRelics.MOD_ID + ".minion";
	private static final int TARGET_MEMORY = 120;
	private static final double SEARCH_RANGE = 16.0;

	/** {@code burrows}: sale de la tierra al aparecer y se entierra al acabar su tiempo (escarabajos). */
	private record Minion(UUID owner, int[] ticksLeft, boolean burrows) {
	}

	private static final Map<Mob, Minion> MINIONS = Collections.synchronizedMap(new IdentityHashMap<>());

	private Minions() {
	}

	public static void init() {
		TickEvent.SERVER_POST.register(Minions::tick);
		// Un aliado que se guardó con el mundo no vuelve: al cargarse no se agrega.
		EntityEvent.ADD.register((entity, level) -> !level.isClientSide() && entity.entityTags().contains(TAG)
				&& !MINIONS.containsKey(entity) ? EventResult.interruptFalse() : EventResult.pass());
		// Los aliados no dañan a su dueño, a otros aliados ni a sus mascotas o compañeros de equipo.
		EntityEvent.LIVING_HURT.register((target, source, amount) -> {
			Entity attacker = source.getEntity();
			UUID owner = attacker == null ? null : ownerOf(attacker);
			if (owner == null) {
				return EventResult.pass();
			}
			return target.getUUID().equals(owner) || ownerOf(target) != null || isAlly(target, owner)
					? EventResult.interruptFalse() : EventResult.pass();
		});
	}

	public static @Nullable UUID ownerOf(Entity entity) {
		Minion minion = entity instanceof Mob mob ? MINIONS.get(mob) : null;
		return minion == null ? null : minion.owner();
	}

	public static int countOf(Player owner) {
		synchronized (MINIONS) {
			return (int) MINIONS.values().stream().filter(m -> m.owner().equals(owner.getUUID())).count();
		}
	}

	/** Invoca un aliado del tipo indicado junto a una posición, por la cantidad de ticks indicada. */
	public static @Nullable Mob summon(ServerLevel level, Player owner, EntityType<? extends Mob> type, double x, double y, double z,
			int ticks, Component name) {
		return summon(level, owner, type, x, y, z, ticks, name, false);
	}

	/** Igual que el anterior; con {@code burrows}, el aliado sale de la tierra y vuelve a enterrarse al terminar. */
	public static @Nullable Mob summon(ServerLevel level, Player owner, EntityType<? extends Mob> type, double x, double y, double z,
			int ticks, Component name, boolean burrows) {
		Mob mob = type.create(level, EntitySpawnReason.MOB_SUMMONED);
		if (mob == null) {
			return null;
		}
		mob.snapTo(x, y, z, level.getRandom().nextFloat() * 360f, 0f);
		mob.addTag(TAG);
		mob.setCustomName(name);
		mob.setPersistenceRequired();
		MINIONS.put(mob, new Minion(owner.getUUID(), new int[] {ticks}, burrows));
		level.addFreshEntity(mob);
		if (burrows) {
			dig(level, mob);
		} else {
			level.sendParticles(ParticleTypes.SOUL, x, y + 0.5, z, 12, 0.3, 0.5, 0.3, 0.02);
		}
		return mob;
	}

	/** Partículas y sonido del bloque que pisa el aliado, como si saliera de la tierra o se enterrara en ella. */
	private static void dig(ServerLevel level, Mob mob) {
		BlockPos below = mob.blockPosition().below();
		BlockState ground = level.getBlockState(below);
		if (ground.isAir()) {
			ground = Blocks.SAND.defaultBlockState();
		}
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), mob.getX(), mob.getY() + 0.1, mob.getZ(),
				24, 0.3, 0.1, 0.3, 0.1);
		SoundType sound = ground.getSoundType();
		level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), sound.getBreakSound(), SoundSource.NEUTRAL,
				sound.getVolume() * 0.8f, sound.getPitch() * 1.2f);
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
					if (minion.burrows()) {
						dig(level, mob);
					} else {
						level.sendParticles(ParticleTypes.POOF, mob.getX(), mob.getY() + 0.5, mob.getZ(), 10, 0.3, 0.4, 0.3, 0.02);
					}
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
				// Buscar objetivo cuesta (un rayo de vista por hostil): cada 10 ticks, escalonado entre aliados.
				if (owner != null && (current == null || !current.isAlive()) && (mob.tickCount + mob.getId()) % 10 == 0) {
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
		if (valid(owner, mob, attacker, owner.getLastHurtByMobTimestamp())) {
			return attacker;
		}
		// Si nadie pelea con su dueño, van por el hostil más cercano a la vista: se ordena por distancia y el rayo de vista
		// (lo caro) se prueba solo hasta encontrar el primero visible.
		java.util.List<LivingEntity> near = mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(SEARCH_RANGE),
				e -> e instanceof net.minecraft.world.entity.monster.Enemy && e.isAlive() && ownerOf(e) == null && !isAlly(e, owner.getUUID()));
		near.sort(java.util.Comparator.comparingDouble(e -> e.distanceToSqr(mob)));
		for (LivingEntity e : near) {
			if (mob.hasLineOfSight(e)) {
				return e;
			}
		}
		return null;
	}

	private static boolean valid(Player owner, Mob mob, @Nullable LivingEntity t, int timestamp) {
		return t != null && t.isAlive() && t != mob && t != owner && owner.tickCount - timestamp < TARGET_MEMORY
				&& ownerOf(t) == null && !isAlly(t, owner.getUUID()) && mob.distanceToSqr(t) < SEARCH_RANGE * SEARCH_RANGE;
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		io.github.potatustudio.mythicrelics.relic.ServerState.clearOnStop(MINIONS);
	}
}
