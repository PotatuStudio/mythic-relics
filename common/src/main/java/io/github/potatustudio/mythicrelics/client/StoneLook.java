package io.github.potatustudio.mythicrelics.client;

/** Campos agregados por mixin a {@code LivingEntityRenderState}: el mob se dibuja de piedra; el jugador es un halcón. */
public interface StoneLook {
	boolean mythicrelics$isStone();

	void mythicrelics$setStone(boolean stone);

	boolean mythicrelics$isFalcon();

	void mythicrelics$setFalcon(boolean falcon);
}
