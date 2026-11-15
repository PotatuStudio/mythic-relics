package io.github.potatustudio.mythicrelics.relic;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

/**
 * Rayo de reliquia (Mjölnir con el set de Thor, el tambor de Raijin): se ve y suena como uno de verdad y hace su daño de
 * rayo, pero no prende fuego al suelo ni a los bloques. El rayo vanilla se lanza solo como efecto visual y el daño se
 * aplica aparte, sin quemar.
 */
public final class RelicLightning {
	public static final float DAMAGE = 5.0f;
	private static final double RADIUS = 3.0;

	private RelicLightning() {
	}

	public static void strike(ServerLevel level, double x, double y, double z, @Nullable ServerPlayer cause) {
		LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
		if (bolt == null) {
			return;
		}
		bolt.snapTo(x, y, z);
		bolt.setVisualOnly(true);
		if (cause != null) {
			bolt.setCause(cause);
		}
		level.addFreshEntity(bolt);
		// Con el set de Thor o el de Fūjin y Raijin, el rayo es el arma del set: pega más.
		boolean set = cause != null && (RelicSets.THOR.isComplete(cause) || RelicSets.FUJIN_RAIJIN.isComplete(cause));
		float damage = DAMAGE * (set ? 1.0f + RelicSets.WEAPON_BONUS : 1.0f);
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, bolt.getBoundingBox().inflate(RADIUS, 6, RADIUS),
				e -> e.isAlive() && e != cause && (cause == null || !Allies.isFriend(cause, e)))) {
			e.hurtServer(level, level.damageSources().lightningBolt(), damage);
		}
	}
}
