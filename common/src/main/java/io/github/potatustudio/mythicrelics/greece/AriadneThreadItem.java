package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.registry.ModComponents;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * Hilo de Ariadna: clic derecho ata el hilo donde estás (lo guarda el ítem); agachado y clic derecho te devuelve ahí,
 * en la misma dimensión y hasta {@link GreeceRelics#ARIADNE_MAX_DISTANCE} bloques. El hilo rojo hacia el inicio lo
 * dibuja el cliente mientras lo llevas en la mano.
 */
public class AriadneThreadItem extends RelicItem {
	public AriadneThreadItem(Properties properties) {
		super(properties);
	}

	public static @Nullable GlobalPos anchor(ItemStack stack) {
		return stack.get(ModComponents.ARIADNE_ANCHOR.get());
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.SUCCESS;
		}
		if (!player.isShiftKeyDown()) {
			stack.set(ModComponents.ARIADNE_ANCHOR.get(), GlobalPos.of(level.dimension(), player.blockPosition()));
			RelicFx.sound(level, player, SoundEvents.WOOL_PLACE, 1.0f, 1.2f);
			RelicFx.notify(player, "ariadne_tied", ChatFormatting.RED);
			return InteractionResult.SUCCESS;
		}
		GlobalPos anchor = anchor(stack);
		if (anchor == null || anchor.dimension() != level.dimension()) {
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.ariadne_none"));
			return InteractionResult.FAIL;
		}
		if (anchor.pos().distSqr(player.blockPosition()) > (double) GreeceRelics.ARIADNE_MAX_DISTANCE * GreeceRelics.ARIADNE_MAX_DISTANCE) {
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.ariadne_far", GreeceRelics.ARIADNE_MAX_DISTANCE));
			return InteractionResult.FAIL;
		}
		if (!RelicCooldowns.tryStart(serverPlayer, "ariadne_thread", GreeceRelics.ARIADNE_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.FAIL;
		}
		serverLevel.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 30, 0.4, 0.8, 0.4, 0.1);
		serverPlayer.teleportTo(anchor.pos().getX() + 0.5, anchor.pos().getY(), anchor.pos().getZ() + 0.5);
		serverPlayer.resetFallDistance();
		RelicFx.sound(level, player, SoundEvents.ENDERMAN_TELEPORT, 0.8f, 1.3f);
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void appendExtraLines(ItemStack stack, Consumer<Component> tooltip) {
		GlobalPos anchor = anchor(stack);
		if (anchor != null) {
			tooltip.accept(Component.translatable("tooltip.mythicrelics.ariadne_thread.anchor",
					anchor.pos().getX(), anchor.pos().getY(), anchor.pos().getZ()).withStyle(ChatFormatting.RED));
		}
	}
}
