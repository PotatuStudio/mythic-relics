package io.github.potatustudio.mythicrelics.mixin;

import io.github.potatustudio.mythicrelics.norse.NorseGods;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Andvaranaut: con el anillo cerca, los piglins dan el doble en los trueques. */
@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {
	@Inject(method = "getBarterResponseItems", at = @At("RETURN"), cancellable = true)
	private static void mythicrelics$andvaranaut(Piglin piglin, CallbackInfoReturnable<List<ItemStack>> cir) {
		if (NorseGods.doubleBarter(piglin)) {
			List<ItemStack> doubled = new ArrayList<>();
			for (ItemStack stack : cir.getReturnValue()) {
				doubled.add(stack);
				doubled.add(stack.copy());
			}
			cir.setReturnValue(doubled);
		}
	}
}
