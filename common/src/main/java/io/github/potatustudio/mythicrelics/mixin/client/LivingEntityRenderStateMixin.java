package io.github.potatustudio.mythicrelics.mixin.client;

import io.github.potatustudio.mythicrelics.client.StoneLook;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/** Agrega a cada estado de render de un mob si se dibuja de piedra ({@link StoneLook}). */
@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements StoneLook {
	@Unique
	private boolean mythicrelics$stone;

	@Unique
	private boolean mythicrelics$falcon;
	@Unique
	private boolean mythicrelics$hidden;
	@Unique
	private int mythicrelics$stoneStage;

	@Override
	public int mythicrelics$stoneStage() {
		return mythicrelics$stoneStage;
	}

	@Override
	public void mythicrelics$setStoneStage(int stage) {
		mythicrelics$stoneStage = stage;
	}

	@Override
	public boolean mythicrelics$isFalcon() {
		return mythicrelics$falcon;
	}

	@Override
	public void mythicrelics$setFalcon(boolean falcon) {
		mythicrelics$falcon = falcon;
	}

	@Override
	public boolean mythicrelics$isHidden() {
		return mythicrelics$hidden;
	}

	@Override
	public void mythicrelics$setHidden(boolean hidden) {
		mythicrelics$hidden = hidden;
	}

	@Override
	public boolean mythicrelics$isStone() {
		return mythicrelics$stone;
	}

	@Override
	public void mythicrelics$setStone(boolean stone) {
		mythicrelics$stone = stone;
	}
}
