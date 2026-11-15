package io.github.potatustudio.mythicrelics.client;

/**
 * Campos agregados por mixin a {@code LivingEntityRenderState}: el mob se dibuja de piedra; el jugador es un halcón; el
 * jugador está oculto del todo por el yelmo de Hades.
 */
public interface StoneLook {
	boolean mythicrelics$isStone();

	void mythicrelics$setStone(boolean stone);

	int mythicrelics$stoneStage();

	void mythicrelics$setStoneStage(int stage);

	boolean mythicrelics$isFalcon();

	void mythicrelics$setFalcon(boolean falcon);

	boolean mythicrelics$isHidden();

	void mythicrelics$setHidden(boolean hidden);
}
