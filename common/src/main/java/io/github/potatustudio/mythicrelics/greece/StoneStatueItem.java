package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.entity.StoneStatue;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import org.jspecify.annotations.Nullable;

/** Estatua de piedra de un mob (la suelta a veces lo que muere petrificado por Medusa): se coloca como decoración. */
public class StoneStatueItem extends Item {
	private static final String KEY = "mythicrelics_statue";

	public StoneStatueItem(Properties properties) {
		super(properties);
	}

	public static ItemStack of(EntityType<?> type) {
		ItemStack stack = new ItemStack(ModItems.STONE_STATUE.get());
		CompoundTag tag = new CompoundTag();
		tag.putString(KEY, BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		return stack;
	}

	public static @Nullable EntityType<?> mobOf(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data == null) {
			return null;
		}
		Identifier id = data.copyTag().getString(KEY).map(Identifier::tryParse).orElse(null);
		return id == null ? null : BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
	}

	@Override
	public Component getName(ItemStack stack) {
		EntityType<?> mob = mobOf(stack);
		return mob == null ? super.getName(stack) : Component.translatable("item.mythicrelics.stone_statue.of", mob.getDescription());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip,
			TooltipFlag flag) {
		tooltip.accept(Component.translatable("item.mythicrelics.stone_statue.tooltip").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		EntityType<?> mob = mobOf(context.getItemInHand());
		if (mob == null) {
			return InteractionResult.FAIL;
		}
		if (!(context.getLevel() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
		StoneStatue statue = ModEntities.STONE_STATUE.get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
		if (statue == null) {
			return InteractionResult.FAIL;
		}
		statue.setMob(mob);
		float yaw = context.getPlayer() == null ? 0 : context.getPlayer().getYRot() + 180.0f;
		statue.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, Math.round(yaw / 45.0f) * 45.0f, 0);
		level.addFreshEntity(statue);
		level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0f, 0.9f);
		context.getItemInHand().consume(1, context.getPlayer());
		return InteractionResult.SUCCESS;
	}
}
