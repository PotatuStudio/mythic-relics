package io.github.potatustudio.mythicrelics.mixin;

import io.github.potatustudio.mythicrelics.relic.TargetVeto;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Objetivos negados por las reliquias ({@link TargetVeto}): el mob no los puede fijar ni seguir usando. */
@Mixin(Mob.class)
public abstract class MobMixin {
	@ModifyVariable(method = "setTarget", at = @At("HEAD"), argsOnly = true)
	private LivingEntity mythicrelics$vetoSetTarget(LivingEntity target) {
		return TargetVeto.blocks((Mob) (Object) this, target) ? null : target;
	}

	@Inject(method = "getTarget", at = @At("RETURN"), cancellable = true)
	private void mythicrelics$vetoGetTarget(CallbackInfoReturnable<LivingEntity> cir) {
		if (TargetVeto.blocks((Mob) (Object) this, cir.getReturnValue())) {
			cir.setReturnValue(null);
		}
	}
}
