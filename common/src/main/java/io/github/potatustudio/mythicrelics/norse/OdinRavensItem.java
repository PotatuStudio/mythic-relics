package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.entity.OdinRaven;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.relic.Compass;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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

	public OdinRavensItem(Properties properties) {
		super(properties);
	}

	/** Cuervos afuera por dueño: solo para devolverlos al llamarlos (el efecto no depende de ellos). */
	private static final java.util.Map<java.util.UUID, java.util.List<OdinRaven>> OUT = new java.util.concurrent.ConcurrentHashMap<>();
	/**
	 * Dueños con los cuervos enviados: Huginn le cuenta a Odín lo que ve, así que el efecto es alrededor del jugador
	 * aunque los cuervos se alejen, se posen lejos o desaparezcan. Se apaga al volver a usar el ítem.
	 */
	private static final java.util.Set<java.util.UUID> ACTIVE = java.util.concurrent.ConcurrentHashMap.newKeySet();

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		RelicCooldowns.start(player, player.getItemInHand(hand), 20);
		if (player.isShiftKeyDown()) {
			memory(serverPlayer);
			RelicFx.sound(server, player, SoundEvents.PARROT_AMBIENT, 1.0f, 0.5f);
			return InteractionResult.SUCCESS;
		}
		java.util.List<OdinRaven> out = OUT.remove(player.getUUID());
		if (ACTIVE.remove(player.getUUID())) {
			// Vuelven con Odín.
			for (OdinRaven r : out != null ? out : java.util.List.<OdinRaven>of()) {
				server.sendParticles(ParticleTypes.SMOKE, r.getX(), r.getY() + 0.3, r.getZ(), 10, 0.3, 0.3, 0.3, 0.03);
				r.discard();
			}
			RelicFx.notify(serverPlayer, "ravens_away", ChatFormatting.DARK_GRAY);
			RelicFx.sound(server, player, SoundEvents.PARROT_FLY, 1.0f, 0.6f);
			return InteractionResult.SUCCESS;
		}
		java.util.List<OdinRaven> ravens = new java.util.ArrayList<>();
		String[] names = {"huginn", "muninn"};
		for (int i = 0; i < 2; i++) {
			OdinRaven raven = ModEntities.ODIN_RAVEN.get().create(server, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
			if (raven == null) {
				continue;
			}
			raven.bind(player, i);
			raven.snapTo(player.getX(), player.getY() + 1.8, player.getZ(), player.getYRot(), 0);
			raven.setCustomName(Component.translatable("entity.mythicrelics." + names[i]));
			server.addFreshEntity(raven);
			ravens.add(raven);
		}
		OUT.put(player.getUUID(), ravens);
		ACTIVE.add(player.getUUID());
		server.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 2.0, player.getZ(), 16, 0.6, 0.3, 0.6, 0.05);
		RelicFx.sound(server, player, SoundEvents.PARROT_FLY, 1.0f, 0.6f);
		RelicFx.notify(serverPlayer, "huginn", ChatFormatting.DARK_GRAY);
		return InteractionResult.SUCCESS;
	}

	/** Mientras los cuervos están enviados, los hostiles alrededor del dueño brillan sin parar (estén o no los cuervos cerca). */
	static void tick(net.minecraft.server.MinecraftServer server) {
		if (server.getTickCount() % 20 != 0) {
			return;
		}
		OUT.entrySet().removeIf(e -> e.getValue().stream().allMatch(net.minecraft.world.entity.Entity::isRemoved));
		ACTIVE.forEach(id -> {
			ServerPlayer player = server.getPlayerList().getPlayer(id);
			if (player == null || !player.isAlive()) {
				return;
			}
			for (LivingEntity foe : player.level().getEntitiesOfClass(LivingEntity.class,
					player.getBoundingBox().inflate(NorseGods.RAVENS_RADIUS), e -> e instanceof Enemy && e.isAlive())) {
				foe.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
			}
		});
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
		return Component.translatable("message.mythicrelics." + key, Compass.direction(dx, dz), distance).withStyle(ChatFormatting.GRAY);
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		io.github.potatustudio.mythicrelics.relic.ServerState.onQuit(p -> {
			ACTIVE.remove(p.getUUID());
			java.util.List<OdinRaven> out = OUT.remove(p.getUUID());
			if (out != null) {
				out.forEach(net.minecraft.world.entity.Entity::discard);
			}
		});
		io.github.potatustudio.mythicrelics.relic.ServerState.clearOnStop(OUT, ACTIVE);
	}
}
