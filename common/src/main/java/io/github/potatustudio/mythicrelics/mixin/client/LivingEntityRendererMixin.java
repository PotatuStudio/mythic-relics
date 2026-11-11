package io.github.potatustudio.mythicrelics.mixin.client;

import io.github.potatustudio.mythicrelics.client.PetrifiedClient;
import io.github.potatustudio.mythicrelics.client.StoneLook;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Lo petrificado por Medusa y las estatuas se dibujan con la textura de la piedra sobre el modelo del mob. */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
			at = @At("TAIL"))
	private void mythicrelics$extractStone(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
		((StoneLook) state).mythicrelics$setStone(PetrifiedClient.isStone(entity));
	}

	@Inject(method = "getRenderType", at = @At("RETURN"), cancellable = true)
	private void mythicrelics$stoneTexture(LivingEntityRenderState state, boolean bodyVisible, boolean translucent, boolean glowing,
			CallbackInfoReturnable<RenderType> cir) {
		if (cir.getReturnValue() != null && ((StoneLook) state).mythicrelics$isStone()) {
			cir.setReturnValue(RenderTypes.entityCutout(PetrifiedClient.STONE));
		}
	}
}
