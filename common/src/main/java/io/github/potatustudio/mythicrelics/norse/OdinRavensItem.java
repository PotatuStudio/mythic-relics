package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Huginn y Muninn: Pensamiento (clic derecho: los hostiles cercanos brillan) y Memoria (agachado: dónde moriste por
 * última vez y dónde está tu cama).
 */
public class OdinRavensItem extends RelicItem {
	private static final String[] DIRECTIONS = {"south", "southwest", "west", "northwest", "north", "northeast", "east", "southeast"};

	public OdinRavensItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		if (!RelicCooldowns.tryStart(serverPlayer, "odin_ravens", NorseGods.RAVENS_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		server.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 2.0, player.getZ(), 16, 0.6, 0.3, 0.6, 0.05);
		server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PARROT_FLY, SoundSource.PLAYERS, 1.0f, 0.6f);
		if (player.isShiftKeyDown()) {
			memory(serverPlayer);
		} else {
			for (LivingEntity foe : server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(NorseGods.RAVENS_RADIUS),
					e -> e instanceof Enemy && e.isAlive())) {
				foe.addEffect(new MobEffectInstance(MobEffects.GLOWING, NorseGods.RAVENS_GLOW_TICKS, 0, false, false));
			}
			serverPlayer.sendOverlayMessage(Component.translatable("message.mythicrelics.huginn").withStyle(ChatFormatting.DARK_GRAY));
		}
		return InteractionResult.SUCCESS;
	}

	/** Muninn: la última muerte y la cama, con dirección y distancia (o en qué dimensión están). */
	private static void memory(ServerPlayer player) {
		Optional<GlobalPos> death = player.getLastDeathLocation();
		player.sendSystemMessage(death.isPresent() ? describe(player, "muninn_death", death.get())
				: Component.translatable("message.mythicrelics.muninn_no_death").withStyle(ChatFormatting.GRAY));
		ServerPlayer.RespawnConfig respawn = player.getRespawnConfig();
		player.sendSystemMessage(respawn != null ? describe(player, "muninn_bed", respawn.respawnData().globalPos())
				: Component.translatable("message.mythicrelics.muninn_no_bed").withStyle(ChatFormatting.GRAY));
	}

	private static Component describe(ServerPlayer player, String key, GlobalPos where) {
		if (where.dimension() != player.level().dimension()) {
			return Component.translatable("message.mythicrelics." + key + "_elsewhere", where.dimension().identifier().getPath())
					.withStyle(ChatFormatting.GRAY);
		}
		BlockPos here = player.blockPosition();
		double dx = where.pos().getX() - here.getX();
		double dz = where.pos().getZ() - here.getZ();
		int distance = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
		int sector = (int) Math.floorMod(Math.round(Math.toDegrees(Math.atan2(-dx, dz)) / 45.0), 8);
		return Component.translatable("message.mythicrelics." + key, Component.translatable("direction.mythicrelics." + DIRECTIONS[sector]),
				distance).withStyle(ChatFormatting.GRAY);
	}
}
