package io.github.potatustudio.mythicrelics.client;

/** Campos agregados por mixin a {@code LivingEntityRenderState}: el mob se dibuja de piedra; el jugador es un halcón. */
public interface StoneLook {
	boolean mythicrelics$isStone();

	void mythicrelics$setStone(boolean stone);

	int mythicrelics$stoneStage();

	void mythicrelics$setStoneStage(int stage);

	boolean mythicrelics$isFalcon();

	void mythicrelics$setFalcon(boolean falcon);
}
