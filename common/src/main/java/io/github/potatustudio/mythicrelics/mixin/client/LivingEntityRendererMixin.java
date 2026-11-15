package io.github.potatustudio.mythicrelics.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.potatustudio.mythicrelics.client.FalconForm;
import io.github.potatustudio.mythicrelics.client.PetrifiedClient;
import io.github.potatustudio.mythicrelics.client.StoneLook;
import io.github.potatustudio.mythicrelics.norse.NorseSagas;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lo petrificado por Medusa y las estatuas se dibujan con la textura de la piedra sobre el modelo del mob; el jugador
 * transformado por el manto de halcón se dibuja como el halcón.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
			at = @At("TAIL"))
	private void mythicrelics$extractStone(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
		((StoneLook) state).mythicrelics$setStone(PetrifiedClient.isStone(entity));
		((StoneLook) state).mythicrelics$setStoneStage(PetrifiedClient.stage(entity));
		((StoneLook) state).mythicrelics$setFalcon(entity instanceof Player player
				&& NorseSagas.isFalconForm(player));
		// Yelmo de Hades: invisible del todo, también la armadura, las reliquias puestas y lo que lleva en las manos.
		((StoneLook) state).mythicrelics$setHidden(entity instanceof Player player && player.isInvisible()
				&& WornRelics.isWearing(player, ModItems.HADES_HELM));
	}

	/** Manto de halcón: el jugador transformado se dibuja como el halcón (sin capa, armadura ni objetos). */
	@Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
			at = @At("HEAD"), cancellable = true)
	private void mythicrelics$falconForm(LivingEntityRenderState state, PoseStack poseStack,
			SubmitNodeCollector collector, CameraRenderState camera,
			CallbackInfo ci) {
		if (((StoneLook) state).mythicrelics$isHidden()
				|| ((StoneLook) state).mythicrelics$isFalcon() && FalconForm.submit(state, poseStack, collector)) {
			ci.cancel();
		}
	}

	@Inject(method = "getRenderType", at = @At("RETURN"), cancellable = true)
	private void mythicrelics$stoneTexture(LivingEntityRenderState state, boolean bodyVisible, boolean translucent, boolean glowing,
			CallbackInfoReturnable<RenderType> cir) {
		if (cir.getReturnValue() != null && ((StoneLook) state).mythicrelics$isStone()) {
			cir.setReturnValue(RenderTypes.entityCutout(PetrifiedClient.texture(((StoneLook) state).mythicrelics$stoneStage())));
		}
	}
}
