package io.github.potatustudio.mythicrelics.relic;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

/**
 * Daño que causa el jugador sin ser un golpe de su mano: tajos de la espada de Freyr, barridos, rayos. Lo cuenta como
 * del jugador (botín, experiencia, enojo del mob), pero la entidad directa no es el jugador, así que no activa lo que
 * pide «golpe con el arma en la mano» (maldición y crítico de Tyrfing, Gram, Dáinsleif, harpe, cólera de Aquiles, Nemea,
 * bono del arma de set). Esos efectos revisan {@code source.getDirectEntity() == attacker}.
 */
public final class RelicDamage {
	private RelicDamage() {
	}

	/** Ataque del jugador por medio de {@code direct} (la espada de Freyr) o sin entidad directa ({@code null}). */
	public static DamageSource byPlayer(ServerLevel level, @Nullable Entity direct, Player player) {
		return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.PLAYER_ATTACK),
				direct, player);
	}
}
