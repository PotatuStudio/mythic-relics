package io.github.potatustudio.mythicrelics.relic;

import io.github.potatustudio.mythicrelics.minion.Minions;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/** Quién está del lado del jugador: los golpes en área de las reliquias no dañan a sus aliados. */
public final class Allies {
	private Allies() {
	}

	/**
	 * Aliados del jugador: lo que invocó (monos, chacales, cuervos… de {@link Minions}), sus mascotas, y las entidades de
	 * su mismo equipo (otros jugadores o mobs con el mismo equipo del marcador).
	 */
	public static boolean isFriend(Player player, Entity e) {
		if (e.isAlliedTo(player) || player.getUUID().equals(Minions.ownerOf(e))) {
			return true;
		}
		return e instanceof OwnableEntity ownable && ownable.getOwnerReference() != null && ownable.getOwnerReference().matches(player);
	}

	/** Seres vivos dentro de la caja que no son el jugador ni sus aliados. */
	public static List<LivingEntity> enemiesAround(Player player, AABB box) {
		return enemiesAround(player, box, e -> true);
	}

	/** Como {@link #enemiesAround(Player, AABB)}, con una condición más. */
	public static List<LivingEntity> enemiesAround(Player player, AABB box, Predicate<LivingEntity> also) {
		return player.level().getEntitiesOfClass(LivingEntity.class, box,
				e -> e != player && e.isAlive() && !isFriend(player, e) && also.test(e));
	}
}
