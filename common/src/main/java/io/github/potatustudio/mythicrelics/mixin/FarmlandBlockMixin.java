package io.github.potatustudio.mythicrelics.mixin;

import io.github.potatustudio.mythicrelics.entity.Qilin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** El qilin camina sin aplastar la hierba ni dañar a ningún ser vivo: no pisotea los cultivos al caer sobre ellos. */
@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {
	@Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
	private void mythicrelics$qilinTreadsLightly(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
		if (entity instanceof Qilin || entity.getVehicle() instanceof Qilin) {
			ci.cancel();
		}
	}
}
