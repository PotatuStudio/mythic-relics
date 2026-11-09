package io.github.potatustudio.mythicrelics.mixin;

import io.github.potatustudio.mythicrelics.MythicRelics;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Arqueología: al cepillar, Minecraft se queda solo con el primer ítem que da la tabla. Las reliquias se agregan como un
 * pool aparte (van al final), así que cuando sale una se mueve al principio; si no, nunca se vería.
 */
@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin {
	@ModifyVariable(method = "unpackLootTable", at = @At("STORE"))
	private ObjectArrayList<ItemStack> mythicrelics$relicFirst(ObjectArrayList<ItemStack> loot) {
		for (int i = 1; i < loot.size(); i++) {
			if (BuiltInRegistries.ITEM.getKey(loot.get(i).getItem()).getNamespace().equals(MythicRelics.MOD_ID)) {
				ItemStack relic = loot.remove(i);
				loot.add(0, relic);
				break;
			}
		}
		return loot;
	}
}
