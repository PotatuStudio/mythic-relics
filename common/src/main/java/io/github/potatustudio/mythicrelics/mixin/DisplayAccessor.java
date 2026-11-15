package io.github.potatustudio.mythicrelics.mixin;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Talismanes fu: tamaño e interpolación del papel pegado en la frente. */
@Mixin(Display.class)
public interface DisplayAccessor {
	@Invoker("setTransformation")
	void mythicrelics$setTransformation(Transformation transformation);

	@Invoker("setPosRotInterpolationDuration")
	void mythicrelics$setPosRotInterpolationDuration(int duration);
}
