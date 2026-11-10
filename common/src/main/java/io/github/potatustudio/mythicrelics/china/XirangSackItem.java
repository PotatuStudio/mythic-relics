package io.github.potatustudio.mythicrelics.china;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Saco de Xirang, la tierra que crece sola con la que Yu el Grande domó el Diluvio: clic derecho lanza un puñado y
 * frente a ti crece un muro de tierra que corta el agua y frena a los enemigos. Las cargas van en los datos del ítem.
 */
public class XirangSackItem extends RelicItem {
	private static final String CHARGES = "xirang_charges";
	private static final String SINCE = "xirang_since";
	private static final int DISTANCE = 2;

	public XirangSackItem(Properties properties) {
		super(properties);
	}

	private static int charges(ItemStack stack, long now) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		if (custom == null) {
			return ChinaGods.XIRANG_CHARGES;
		}
		CompoundTag tag = custom.copyTag();
		int stored = tag.getIntOr(CHARGES, ChinaGods.XIRANG_CHARGES);
		long since = tag.getLongOr(SINCE, now);
		return (int) Math.min(ChinaGods.XIRANG_CHARGES, stored + Math.max(0, now - since) / ChinaGods.XIRANG_RECHARGE_TICKS);
	}

	private static void store(ItemStack stack, int charges, long now) {
		long since = now;
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		if (custom != null && charges < ChinaGods.XIRANG_CHARGES) {
			CompoundTag tag = custom.copyTag();
			if (tag.getIntOr(CHARGES, ChinaGods.XIRANG_CHARGES) < ChinaGods.XIRANG_CHARGES) {
				long old = tag.getLongOr(SINCE, now);
				since = old + Math.max(0, now - old) / ChinaGods.XIRANG_RECHARGE_TICKS * ChinaGods.XIRANG_RECHARGE_TICKS;
			}
		}
		long from = since;
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.putInt(CHARGES, charges);
			tag.putLong(SINCE, from);
		});
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		long now = server.getGameTime();
		int left = charges(stack, now);
		if (left <= 0) {
			return InteractionResult.FAIL;
		}
		Direction facing = player.getDirection();
		Direction side = facing.getClockWise();
		BlockPos base = player.blockPosition().relative(facing, DISTANCE);
		int placed = 0;
		for (int w = -ChinaGods.XIRANG_WIDTH / 2; w <= ChinaGods.XIRANG_WIDTH / 2; w++) {
			BlockPos column = ground(server, base.relative(side, w));
			if (column == null) {
				continue;
			}
			for (int h = 0; h < ChinaGods.XIRANG_HEIGHT; h++) {
				BlockPos pos = column.above(h);
				BlockState state = server.getBlockState(pos);
				if (state.canBeReplaced() || !state.getFluidState().isEmpty() && state.getBlock() == Blocks.WATER) {
					server.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
					server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
							pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6, 0.3, 0.3, 0.3, 0.05);
					placed++;
				}
			}
		}
		if (placed == 0) {
			return InteractionResult.FAIL;
		}
		store(stack, left - 1, now);
		server.playSound(null, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, SoundEvents.ROOTED_DIRT_PLACE, SoundSource.PLAYERS, 1.5f, 0.6f);
		server.playSound(null, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, SoundEvents.GRAVEL_BREAK, SoundSource.PLAYERS, 1.0f, 0.5f);
		return InteractionResult.SUCCESS;
	}

	/** El primer bloque libre sobre el suelo en esa columna (busca un poco arriba y abajo). */
	private static @Nullable BlockPos ground(ServerLevel level, BlockPos start) {
		for (int dy = 2; dy >= -3; dy--) {
			BlockPos pos = start.above(dy);
			BlockState below = level.getBlockState(pos.below());
			BlockState here = level.getBlockState(pos);
			if ((here.canBeReplaced() || here.getBlock() == Blocks.WATER) && !below.canBeReplaced() && below.getBlock() != Blocks.WATER) {
				return pos;
			}
		}
		return null;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity holder, @Nullable EquipmentSlot slot) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		if (holder.tickCount % 20 != 0 || custom == null) {
			return;
		}
		long now = level.getGameTime();
		int left = charges(stack, now);
		if (custom.copyTag().getIntOr(CHARGES, ChinaGods.XIRANG_CHARGES) != left) {
			store(stack, left, now);
		}
	}

	private static int storedCharges(ItemStack stack) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		return custom == null ? ChinaGods.XIRANG_CHARGES : custom.copyTag().getIntOr(CHARGES, ChinaGods.XIRANG_CHARGES);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return storedCharges(stack) < ChinaGods.XIRANG_CHARGES;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Mth.clamp(Math.round(13.0f * storedCharges(stack) / ChinaGods.XIRANG_CHARGES), 0, 13);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0x8A6230;
	}

	@Override
	protected void appendExtraLines(ItemStack stack, Consumer<Component> tooltip) {
		if (stack.get(DataComponents.CUSTOM_DATA) != null) {
			tooltip.accept(Component.translatable("tooltip.mythicrelics.xirang_charges", storedCharges(stack), ChinaGods.XIRANG_CHARGES)
					.withStyle(ChatFormatting.YELLOW));
		}
	}
}
