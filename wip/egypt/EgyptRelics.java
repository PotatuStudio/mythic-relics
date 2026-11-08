package io.github.potatustudio.mythicrelics.egypt;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.network.EyeRevealPayload;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Primera tanda de Egipto (Trinkets salvo el báculo):
 * <ul>
 *   <li><b>Escarabajo dorado</b> (cinturón): al bajar de 3 corazones, absorción y dos escarabajos aliados. Recarga.</li>
 *   <li><b>Ojo de Horus</b> (cara): con una tecla ves el contorno de las criaturas y columnas doradas sobre los cofres
 *       cercanos; solo tú. Recarga.</li>
 *   <li><b>Anj</b> (collar): regeneración lenta constante y los no-muertos te hacen menos daño (mixin).</li>
 *   <li><b>Báculo de Anubis</b> (arma): los no-muertos que matas con él a veces vuelven como aliados un rato.</li>
 *   <li><b>Vendas de momia</b> (guantes): tus golpes dan hambre y lentitud; los husks no te atacan.</li>
 * </ul>
 */
public final class EgyptRelics {
	public static final float SCARAB_THRESHOLD = 6.0f;
	public static final int SCARAB_COOLDOWN = 1200;
	public static final int SCARAB_ABSORPTION = 1;
	public static final int SCARAB_MINION_TICKS = 300;

	public static final double EYE_RADIUS = 32.0;
	public static final int EYE_CHEST_CHUNKS = 2;
	public static final int EYE_TICKS = 160;
	public static final int EYE_COOLDOWN = 600;
	public static final Identifier EYE_COOLDOWN_GROUP = MythicRelics.id("eye_of_horus");

	public static final int ANKH_HEAL_EVERY = 50;
	public static final float ANKH_UNDEAD_REDUCTION = 0.25f;

	public static final float ANUBIS_RAISE_CHANCE = 0.35f;
	public static final int ANUBIS_MAX_MINIONS = 3;
	public static final int ANUBIS_MINION_TICKS = 400;

	public static final int WRAPS_HUNGER_TICKS = 100;
	public static final int WRAPS_SLOW_TICKS = 60;

	private static final DustParticleOptions GOLD = new DustParticleOptions(0xFFD84A, 1.4f);

	private static final Map<UUID, Long> SCARAB_LAST = new HashMap<>();

	/** Columnas doradas sobre cofres que está viendo un jugador con el Ojo de Horus: jugador -> [cofres, ticks]. */
	private record Reveal(List<BlockPos> chests, int[] ticksLeft) {
	}

	private static final Map<UUID, Reveal> REVEALS = new HashMap<>();

	private EgyptRelics() {
	}

	public static void init() {
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
			if (blocked || damageTaken <= 0 || !(entity.level() instanceof ServerLevel level)) {
				return;
			}
			if (entity instanceof ServerPlayer player && isWearing(player, ModItems.GOLDEN_SCARAB)
					&& player.getHealth() <= SCARAB_THRESHOLD) {
				scarab(level, player);
			}
			if (source.getEntity() instanceof ServerPlayer attacker && source.getDirectEntity() == attacker
					&& attacker != entity && isWearing(attacker, ModItems.MUMMY_WRAPS)) {
				entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, WRAPS_HUNGER_TICKS, 1), attacker);
				entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, WRAPS_SLOW_TICKS, 0), attacker);
			}
		});
		ServerLivingEntityEvents.AFTER_DEATH.register((victim, source) -> {
			if (source.getEntity() instanceof ServerPlayer player && victim.getType().is(EntityTypeTags.UNDEAD)
					&& player.getMainHandItem().is(ModItems.ANUBIS_STAFF) && victim.level() instanceof ServerLevel level) {
				anubis(level, player, victim);
			}
		});
		ServerTickEvents.END_SERVER_TICK.register(EgyptRelics::tick);
	}

	public static boolean isWearing(LivingEntity entity, Item item) {
		return TrinketsApi.getAttachment(entity).findFirst(item).map(TrinketSlotAccess::get).isPresent();
	}

	private static void scarab(ServerLevel level, ServerPlayer player) {
		long now = level.getGameTime();
		if (now - SCARAB_LAST.getOrDefault(player.getUUID(), -SCARAB_COOLDOWN * 2L) < SCARAB_COOLDOWN) {
			return;
		}
		SCARAB_LAST.put(player.getUUID(), now);
		player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, SCARAB_ABSORPTION));
		for (int i = 0; i < 2; i++) {
			Minions.summon(level, player, EntityTypes.SILVERFISH, player.getX() + (i == 0 ? 1 : -1), player.getY(), player.getZ(),
					SCARAB_MINION_TICKS, Component.translatable("entity.mythicrelics.scarab"));
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.5f, 1.6f);
		level.sendParticles(GOLD, player.getX(), player.getY() + 1, player.getZ(), 30, 0.5, 0.7, 0.5, 0);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.scarab").withStyle(ChatFormatting.GOLD));
	}

	private static void anubis(ServerLevel level, ServerPlayer player, LivingEntity victim) {
		if (player.getRandom().nextFloat() >= ANUBIS_RAISE_CHANCE || Minions.countOf(player) >= ANUBIS_MAX_MINIONS
				|| Minions.ownerOf(victim) != null) {
			return;
		}
		@SuppressWarnings("unchecked")
		EntityType<? extends Mob> type = victim instanceof Mob ? (EntityType<? extends Mob>) victim.getType() : null;
		if (type == null) {
			return;
		}
		Minions.summon(level, player, type, victim.getX(), victim.getY(), victim.getZ(), ANUBIS_MINION_TICKS,
				Component.translatable("entity.mythicrelics.raised", victim.getType().getDescription()));
		level.playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 1.0f, 0.7f);
	}

	/** Tecla del Ojo de Horus: contorno de criaturas (solo en tu pantalla) y columnas doradas sobre los cofres. */
	public static void eyeOfHorus(ServerPlayer player) {
		var eye = TrinketsApi.getAttachment(player).findFirst(ModItems.EYE_OF_HORUS).map(TrinketSlotAccess::get);
		if (eye.isEmpty()) {
			return;
		}
		if (player.getCooldowns().isOnCooldown(eye.get())) {
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.eye_cooldown"));
			return;
		}
		ServerLevel level = player.level();
		List<Integer> found = new ArrayList<>();
		for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(EYE_RADIUS),
				e -> e != player && e.isAlive() && e.distanceToSqr(player) <= EYE_RADIUS * EYE_RADIUS)) {
			found.add(e.getId());
		}
		ServerPlayNetworking.send(player, new EyeRevealPayload(found, EYE_TICKS));
		List<BlockPos> chests = new ArrayList<>();
		int cx = player.blockPosition().getX() >> 4;
		int cz = player.blockPosition().getZ() >> 4;
		for (int dx = -EYE_CHEST_CHUNKS; dx <= EYE_CHEST_CHUNKS; dx++) {
			for (int dz = -EYE_CHEST_CHUNKS; dz <= EYE_CHEST_CHUNKS; dz++) {
				LevelChunk chunk = level.getChunkSource().getChunkNow(cx + dx, cz + dz);
				if (chunk == null) {
					continue;
				}
				for (BlockEntity be : chunk.getBlockEntities().values()) {
					if (be instanceof Container) {
						chests.add(be.getBlockPos());
					}
				}
			}
		}
		REVEALS.put(player.getUUID(), new Reveal(chests, new int[] {EYE_TICKS}));
		player.getCooldowns().addCooldown(EYE_COOLDOWN_GROUP, EYE_COOLDOWN);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0f, 0.6f);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.eye", found.size(), chests.size())
				.withStyle(ChatFormatting.GOLD));
	}

	private static void tick(MinecraftServer server) {
		Iterator<Map.Entry<UUID, Reveal>> it = REVEALS.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Reveal> e = it.next();
			ServerPlayer player = server.getPlayerList().getPlayer(e.getKey());
			Reveal r = e.getValue();
			if (player == null || --r.ticksLeft()[0] <= 0) {
				it.remove();
				continue;
			}
			if (r.ticksLeft()[0] % 10 == 0) {
				ServerLevel level = player.level();
				for (BlockPos p : r.chests()) {
					for (int k = 0; k < 6; k++) {
						level.sendParticles(player, GOLD, true, true, p.getX() + 0.5, p.getY() + 1.0 + k * 0.5, p.getZ() + 0.5,
								1, 0.05, 0.05, 0.05, 0);
					}
				}
			}
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ServerLevel level = player.level();
			if (player.tickCount % EgyptRelics.ANKH_HEAL_EVERY == 0 && player.getHealth() < player.getMaxHealth()
					&& isWearing(player, ModItems.ANKH)) {
				player.heal(1.0f);
				level.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.8, player.getZ(), 2, 0.2, 0.1, 0.2, 0);
			}
			if (player.tickCount % 10 == 0 && isWearing(player, ModItems.MUMMY_WRAPS)) {
				for (Husk husk : level.getEntitiesOfClass(Husk.class, player.getBoundingBox().inflate(24),
						h -> h.getTarget() == player)) {
					husk.setTarget(null);
				}
			}
		}
	}
}
