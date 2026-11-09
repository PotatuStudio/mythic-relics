package io.github.potatustudio.mythicrelics.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Acceso a la puerta y al mob de los objetivos de puertas (para los candados de la llave de Jano). */
@Mixin(DoorInteractGoal.class)
public interface DoorInteractGoalAccessor {
	@Accessor("doorPos")
	BlockPos mythicrelics$doorPos();

	@Accessor("mob")
	Mob mythicrelics$mob();
}
