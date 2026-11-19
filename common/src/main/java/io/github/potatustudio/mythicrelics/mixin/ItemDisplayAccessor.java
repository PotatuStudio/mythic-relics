package io.github.potatustudio.mythicrelics.mixin;

import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Talismanes fu: el papel pegado en la frente es una entidad de visualización de ítem armada desde el código. */
@Mixin(Display.ItemDisplay.class)
public interface ItemDisplayAccessor {
	@Invoker("setItemStack")
	void mythicrelics$setItemStack(ItemStack item);

	@Invoker("getItemStack")
	ItemStack mythicrelics$getItemStack();

	@Invoker("setItemTransform")
	void mythicrelics$setItemTransform(ItemDisplayContext transform);
}
