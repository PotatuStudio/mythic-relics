package io.github.potatustudio.mythicrelics.norse;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Segunda tanda nórdica (diseño aprobado el 2026-11-09):
 * <ul>
 *   <li><b>Herradura de Sleipnir</b>: invoca a Sleipnir ({@link SleipnirHorseshoeItem},
 *       {@link io.github.potatustudio.mythicrelics.entity.Sleipnir}).</li>
 *   <li><b>Huginn y Muninn</b>, <b>Skíðblaðnir</b> y <b>Óðrerir</b>: ítems que se usan.</li>
 *   <li><b>Brísingamen</b> (collar): cultivos y crías crecen más rápido; matar a un hostil cura.</li>
 *   <li><b>Cabeza de Mímir</b> (mano secundaria): señala los minerales valiosos cercanos y avisa de los creepers.</li>
 *   <li><b>Andvaranaut</b> (anillo): doble oro (mineral y trueques con piglins) con su maldición: menos vida y los
 *       hostiles te prefieren.</li>
 * </ul>
 */
public final class NorseGods {
	public static final int SLEIPNIR_LEAVE_TICKS = 60;
	public static final int SLEIPNIR_COOLDOWN = 6000;

	public static final int RAVENS_RADIUS = 32;

	public static final int BRISINGAMEN_RADIUS = 8;
	public static final int BRISINGAMEN_GROWTH_TICKS = 8;
	public static final float BRISINGAMEN_HEAL = 2.0f;

	public static final float SKIDBLADNIR_BOOST = 0.6f;

	public static final int MIMIR_RADIUS = 8;

	public static final float ANDVARANAUT_HEALTH = 4.0f;
	public static final int ANDVARANAUT_LURE_RADIUS = 16;

	public static final int ODRERIR_LEVELS = 5;
	public static final int ODRERIR_HASTE_TICKS = 6000;
	public static final int ODRERIR_COOLDOWN = 24000;

	public static final String SKIDBLADNIR_TAG = MythicRelics.MOD_ID + ".skidbladnir";
	private static final Identifier CURSE_ID = MythicRelics.id("andvaranaut_curse");
	/** Botes de Skíðblaðnir desplegados (se pliegan al quedar vacíos). */
	private static final Set<UUID> SHIPS = new HashSet<>();
	/** Último aviso de creeper por jugador (para no repetirlo cada tick). */
	private static final Map<UUID, Long> CREEPER_WARNED = new HashMap<>();

	private NorseGods() {
	}

	public static void init() {
		EntityEvent.LIVING_DEATH.register(NorseGods::onDeath);
		BlockEvent.BREAK.register(NorseGods::onBreak);
		TickEvent.SERVER_POST.register(NorseGods::tick);
		TickEvent.SERVER_POST.register(MimirHead::tick);
		TickEvent.SERVER_POST.register(OdinRavensItem::tick);
		MimirHead.init();
	}

	static void launched(Entity boat) {
		boat.addTag(SKIDBLADNIR_TAG);
		SHIPS.add(boat.getUUID());
	}

	/** Cliente: ¿el jugador navega con Skíðblaðnir en el inventario? (viento a favor). */
	public static boolean favorableWind(Player player) {
		return player.getVehicle() instanceof AbstractBoat && player.getInventory().contains(s -> s.is(ModItems.SKIDBLADNIR.get()));
	}

	private static EventResult onDeath(LivingEntity entity, DamageSource source) {
		// Brísingamen: Freyja se queda con la mitad de los caídos; matar a un hostil te devuelve vida.
		if (entity instanceof Enemy && source.getEntity() instanceof ServerPlayer player && WornRelics.isWearing(player, ModItems.BRISINGAMEN)) {
			player.heal(BRISINGAMEN_HEAL);
		}
		return EventResult.pass();
	}

	private static EventResult onBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player) {
		// Andvaranaut: el mineral de oro suelta oro de más.
		if (level instanceof ServerLevel server && state.is(BlockTags.GOLD_ORES) && !player.isCreative()
				&& WornRelics.isWearing(player, ModItems.ANDVARANAUT)) {
			ItemEntity gold = new ItemEntity(server, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.RAW_GOLD));
			gold.setDefaultPickUpDelay();
			server.addFreshEntity(gold);
		}
		return EventResult.pass();
	}

	/** Andvaranaut en los trueques: si un jugador con el anillo está cerca del piglin, el trueque se duplica. */
	public static boolean doubleBarter(Mob piglin) {
		Player near = piglin.level().getNearestPlayer(piglin, 16.0);
		return near != null && WornRelics.isWearing(near, ModItems.ANDVARANAUT);
	}

	private static void tick(MinecraftServer server) {
		foldShips(server);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ServerLevel level = player.level();
			boolean ring = WornRelics.isWearing(player, ModItems.ANDVARANAUT);
			AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
			if (health != null) {
				if (ring && !health.hasModifier(CURSE_ID)) {
					health.addTransientModifier(new AttributeModifier(CURSE_ID, -ANDVARANAUT_HEALTH, AttributeModifier.Operation.ADD_VALUE));
				} else if (!ring && health.hasModifier(CURSE_ID)) {
					health.removeModifier(CURSE_ID);
				}
				if (player.getHealth() > player.getMaxHealth()) {
					player.setHealth(player.getMaxHealth());
				}
			}
			if (ring && player.tickCount % 20 == 0) {
				lure(level, player);
			}
			if (player.tickCount % 20 == 0 && WornRelics.isWearing(player, ModItems.BRISINGAMEN)) {
				fertility(level, player);
			}
			MimirHead.tickGuide(level, player);
			if (WornRelics.holding(player, ModItems.MIMIR_HEAD.get())) {
				if (player.tickCount % 5 == 0) {
					creeperWarning(level, player);
				}
			}
		}
	}

	private static void foldShips(MinecraftServer server) {
		Iterator<UUID> it = SHIPS.iterator();
		while (it.hasNext()) {
			UUID id = it.next();
			Entity boat = null;
			for (ServerLevel level : server.getAllLevels()) {
				boat = level.getEntity(id);
				if (boat != null) {
					break;
				}
			}
			if (boat == null || boat.isRemoved()) {
				it.remove();
			} else if (!boat.isVehicle()) {
				if (boat.level() instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.CLOUD, boat.getX(), boat.getY() + 0.5, boat.getZ(), 12, 0.5, 0.2, 0.5, 0.02);
				}
				boat.discard();
				it.remove();
			}
		}
	}

	/** Maldición de Andvaranaut: los hostiles que persiguen a otros cerca de ti te prefieren a ti. */
	private static void lure(ServerLevel level, ServerPlayer player) {
		for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(ANDVARANAUT_LURE_RADIUS),
				m -> m instanceof Enemy && m.getTarget() != null && m.getTarget() != player)) {
			mob.setTarget(player);
		}
	}

	/** Brísingamen: ticks de crecimiento de más para los cultivos y las crías cercanas. */
	private static void fertility(ServerLevel level, ServerPlayer player) {
		RandomSource random = level.getRandom();
		BlockPos center = player.blockPosition();
		for (int i = 0; i < BRISINGAMEN_GROWTH_TICKS; i++) {
			BlockPos pos = center.offset(random.nextInt(BRISINGAMEN_RADIUS * 2 + 1) - BRISINGAMEN_RADIUS, random.nextInt(5) - 2,
					random.nextInt(BRISINGAMEN_RADIUS * 2 + 1) - BRISINGAMEN_RADIUS);
			BlockState state = level.getBlockState(pos);
			if (state.is(BlockTags.CROPS) && state.isRandomlyTicking()) {
				state.randomTick(level, pos, random);
			}
		}
		for (AgeableMob baby : level.getEntitiesOfClass(AgeableMob.class, player.getBoundingBox().inflate(BRISINGAMEN_RADIUS),
				m -> m.getAge() < 0)) {
			baby.setAge(Math.min(0, baby.getAge() + 20));
		}
	}

	/** Cabeza de Mímir: avisa cuando un creeper cercano empieza a explotar. */
	private static void creeperWarning(ServerLevel level, ServerPlayer player) {
		long now = level.getGameTime();
		if (now - CREEPER_WARNED.getOrDefault(player.getUUID(), -100L) < 40) {
			return;
		}
		for (Creeper creeper : level.getEntitiesOfClass(Creeper.class, player.getBoundingBox().inflate(16), c -> c.getSwellDir() > 0)) {
			creeper.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
			RelicFx.notify(player, "mimir_creeper", ChatFormatting.RED);
			CREEPER_WARNED.put(player.getUUID(), now);
			return;
		}
	}

	/** Números de los tooltips de la segunda tanda nórdica. */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "sleipnir_horseshoe" -> new Object[] {SLEIPNIR_LEAVE_TICKS / 20, SLEIPNIR_COOLDOWN / 20 / 60};
			case "odin_ravens" -> new Object[] {RAVENS_RADIUS};
			case "brisingamen" -> new Object[] {BRISINGAMEN_RADIUS, (int) (BRISINGAMEN_HEAL / 2)};
			case "skidbladnir" -> new Object[] {Math.round(SKIDBLADNIR_BOOST * 100)};
			case "mimir_head" -> new Object[] {MIMIR_RADIUS, MimirHead.GUIDE_TICKS / 20, MimirHead.GUIDE_RADIUS, MimirHead.ASK_COOLDOWN / 20};
			case "andvaranaut" -> new Object[] {(int) (ANDVARANAUT_HEALTH / 2)};
			case "odrerir" -> new Object[] {ODRERIR_LEVELS, ODRERIR_HASTE_TICKS / 20 / 60};
			default -> io.github.potatustudio.mythicrelics.china.ChinaRelics.tooltipArgs(id);
		};
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		io.github.potatustudio.mythicrelics.relic.ServerState.perPlayer(CREEPER_WARNED);
		io.github.potatustudio.mythicrelics.relic.ServerState.clearOnStop(SHIPS);
	}
}
