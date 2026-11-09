package io.github.potatustudio.mythicrelics.mixin;

import io.github.potatustudio.mythicrelics.rome.JanusLocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Llave de Jano: los zombis no rompen las puertas cerradas con llave. */
@Mixin(BreakDoorGoal.class)
public abstract class BreakDoorGoalMixin {
	@Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
	private void mythicrelics$janusLock(CallbackInfoReturnable<Boolean> cir) {
		DoorInteractGoalAccessor goal = (DoorInteractGoalAccessor) this;
		if (cir.getReturnValueZ() && goal.mythicrelics$mob().level() instanceof ServerLevel level
				&& JanusLocks.isLocked(level, goal.mythicrelics$doorPos())) {
			cir.setReturnValue(false);
		}
	}
}
