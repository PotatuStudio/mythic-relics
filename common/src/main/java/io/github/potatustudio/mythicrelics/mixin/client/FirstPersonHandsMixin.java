package io.github.potatustudio.mythicrelics.mixin.client;

import io.github.potatustudio.mythicrelics.norse.NorseSagas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** En forma de halcón no hay manos ni objetos en primera persona. */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FirstPersonHandsMixin {
	@Inject(method = "submitHandsWithItems", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$noHandsAsFalcon(CallbackInfo ci) {
		var player = Minecraft.getInstance().player;
		if (player != null && NorseSagas.isFalconForm(player)) {
			ci.cancel();
		}
	}
}
