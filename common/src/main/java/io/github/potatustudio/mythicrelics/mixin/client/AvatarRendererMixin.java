package io.github.potatustudio.mythicrelics.mixin.client;

import io.github.potatustudio.mythicrelics.client.FalconForm;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Arma el modelo del halcón (forma de halcón del manto de Freyja) junto con el renderer del jugador. */
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void mythicrelics$bakeFalcon(EntityRendererProvider.Context context, boolean slim, CallbackInfo ci) {
		FalconForm.bake(context);
	}
}
