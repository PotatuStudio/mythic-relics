package io.github.potatustudio.mythicrelics.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fox.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Kibi dango: el zorro que las come pasa a confiar en ti (como el que se cría con bayas dulces). */
@Mixin(Fox.class)
public interface FoxAccessor {
	@Invoker("addTrustedEntity")
	void mythicrelics$addTrustedEntity(LivingEntity entity);
}
