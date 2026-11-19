package io.github.potatustudio.mythicrelics.norse;

import io.github.potatustudio.mythicrelics.entity.OdinRaven;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.relic.Compass;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicItem;
import io.github.potatustudio.mythicrelics.relic.ServerState;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
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
	private static final Map<UUID, List<OdinRaven>> OUT = new HashMap<>();
	/**
	 * Dueños con los cuervos enviados: Huginn le cuenta a Odín lo que ve, así que el efecto es alrededor del jugador
	 * aunque los cuervos se alejen, se posen lejos o desaparezcan. Se apaga al volver a usar el ítem.
	 */
	private static final Set<UUID> ACTIVE = new HashSet<>();
	/** Muninn: el segundo recuerdo (la cama), que sale sobre la barra después del primero. Todo esto es del servidor. */
	private record Memory(Component text, int at) {
	}

	private static final Map<UUID, Memory> PENDING_MEMORY = new HashMap<>();
	/** Ticks entre los dos recuerdos de Muninn (para que se alcance a leer el primero). */
	private static final int MEMORY_GAP_TICKS = 60;

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
		List<OdinRaven> out = OUT.remove(player.getUUID());
		if (ACTIVE.remove(player.getUUID())) {
			// Vuelven con Odín.
			for (OdinRaven r : out != null ? out : List.<OdinRaven>of()) {
				server.sendParticles(ParticleTypes.SMOKE, r.getX(), r.getY() + 0.3, r.getZ(), 10, 0.3, 0.3, 0.3, 0.03);
				r.discard();
			}
			RelicFx.notify(serverPlayer, "ravens_away", ChatFormatting.DARK_GRAY);
			RelicFx.sound(server, player, SoundEvents.PARROT_FLY, 1.0f, 0.6f);
			return InteractionResult.SUCCESS;
		}
		List<OdinRaven> ravens = new ArrayList<>();
		String[] names = {"huginn", "muninn"};
		for (int i = 0; i < 2; i++) {
			OdinRaven raven = ModEntities.ODIN_RAVEN.get().create(server, EntitySpawnReason.MOB_SUMMONED);
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
	static void tick(MinecraftServer server) {
		if (server.getTickCount() % 20 != 0) {
			return;
		}
		PENDING_MEMORY.entrySet().removeIf(e -> {
			if (server.getTickCount() < e.getValue().at()) {
				return false;
			}
			ServerPlayer player = server.getPlayerList().getPlayer(e.getKey());
			if (player != null) {
				player.sendOverlayMessage(e.getValue().text());
			}
			return true;
		});
		OUT.entrySet().removeIf(e -> e.getValue().stream().allMatch(Entity::isRemoved));
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

	/**
	 * Muninn: la última muerte y la cama, con dirección y distancia (o en qué dimensión están). Sobre la barra, sin chat:
	 * primero la muerte y, unos segundos después, la cama.
	 */
	private static void memory(ServerPlayer player) {
		Optional<GlobalPos> death = player.getLastDeathLocation();
		player.sendOverlayMessage(death.isPresent() ? describe(player, "muninn_death", death.get())
				: Component.translatable("message.mythicrelics.muninn_no_death").withStyle(ChatFormatting.GRAY));
		ServerPlayer.RespawnConfig respawn = player.getRespawnConfig();
		Component bed = respawn != null ? describe(player, "muninn_bed", respawn.respawnData().globalPos())
				: Component.translatable("message.mythicrelics.muninn_no_bed").withStyle(ChatFormatting.GRAY);
		PENDING_MEMORY.put(player.getUUID(), new Memory(bed, player.level().getServer().getTickCount() + MEMORY_GAP_TICKS));
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
		ServerState.onQuit(p -> {
			ACTIVE.remove(p.getUUID());
			PENDING_MEMORY.remove(p.getUUID());
			List<OdinRaven> out = OUT.remove(p.getUUID());
			if (out != null) {
				out.forEach(Entity::discard);
			}
		});
		ServerState.clearOnStop(OUT, ACTIVE, PENDING_MEMORY);
	}
}
