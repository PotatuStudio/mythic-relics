package io.github.potatustudio.mythicrelics.japan;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.Allies;
import io.github.potatustudio.mythicrelics.relic.RelicSets;
import io.github.potatustudio.mythicrelics.relic.TooltipNumbers;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import io.github.potatustudio.mythicrelics.rome.RomeRelics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Primera tanda japonesa (diseño aprobado el 2026-11-10): los Tres Tesoros Imperiales (Kusanagi, Yata no Kagami,
 * Yasakani no Magatama) y su set, el tambor de Raijin, el kanabō del oni, la esfera del kitsune, las joyas de la marea,
 * el mazo de la suerte, los kibi dango de Momotarō y la caña de Urashima.
 * <ul>
 *   <li>Ítems: {@link KusanagiItem}, {@link YataMirrorItem}, {@link KitsuneStarBallItem}, {@link TideJewelsItem},
 *       {@link LuckyMalletItem}, {@link KibiDangoItem} y {@link UrashimaRodItem}; el kanabō es un arma con su efecto aquí.</li>
 *   <li>Aquí: el reflejo del espejo, la magatama y el set, el redoble de Raijin, el golpe de oni, el pasillo seco de
 *       kanju y el tamaño del mazo.</li>
 * </ul>
 */
public final class JapanRelics {
	public static final float KUSANAGI_SLASH_DAMAGE = 16.0f;
	public static final int KUSANAGI_SLASH_RANGE = 5;
	public static final int KUSANAGI_COOLDOWN = 100;

	public static final int MIRROR_FLASH_RADIUS = 12;
	public static final int MIRROR_BLIND_TICKS = 100;
	public static final int MIRROR_COOLDOWN = 600;

	public static final int MAGATAMA_INTERVAL = 100;
	public static final float SET_DAMAGE = 0.2f;

	public static final int RAIJIN_HITS = 4;
	public static final int RAIJIN_WINDOW = 60;

	public static final float KANABO_ARMOR_IGNORED = 0.5f;

	public static final int KITSUNE_TICKS = 1200;
	public static final int KITSUNE_COOLDOWN = 4800;
	public static final int KITSUNE_HEALTH = 30;
	public static final int KITSUNE_DAMAGE = 6;
	public static final int KITSUNEBI_INTERVAL = 60;
	public static final int KITSUNEBI_BURN_SECONDS = 2;

	public static final int TIDE_LENGTH = 12;
	public static final int TIDE_TICKS = 400;
	public static final int TIDE_WAVE_RANGE = 8;
	public static final int TIDE_COOLDOWN = 1200;

	public static final float MALLET_SCALE = 0.6f;
	public static final int MALLET_TICKS = 600;
	public static final int MALLET_HEALTH = 8;
	public static final int MALLET_COOLDOWN = 3600;
	public static final int MALLET_GROW_RADIUS = 4;
	public static final int MALLET_GROW_COOLDOWN = 200;

	public static final int KIBI_MAX = 3;
	public static final int KIBI_STRENGTH_TICKS = 1200;

	public static final int TURTLE_LEAVE_TICKS = 60;
	public static final int TURTLE_COOLDOWN = 6000;

	private static final Identifier MALLET_SCALE_ID = MythicRelics.id("lucky_mallet_scale");
	private static final Identifier MALLET_HEALTH_ID = MythicRelics.id("lucky_mallet_health");
	private static final Identifier MALLET_REACH_ID = MythicRelics.id("lucky_mallet_reach");
	private static final Identifier MALLET_BLOCK_REACH_ID = MythicRelics.id("lucky_mallet_block_reach");
	private static final Identifier MALLET_DAMAGE_ID = MythicRelics.id("lucky_mallet_damage");
	private static final Identifier MALLET_JUMP_ID = MythicRelics.id("lucky_mallet_jump");
	private static final Identifier MALLET_STEP_ID = MythicRelics.id("lucky_mallet_step");
	private static final Identifier MALLET_FALL_ID = MythicRelics.id("lucky_mallet_fall");
	private static final Identifier MALLET_KNOCKBACK_ID = MythicRelics.id("lucky_mallet_knockback");
	public static final float MALLET_DAMAGE = 4.0f;

	/** Golpes seguidos con el tambor de Raijin: [cantidad, tick del último golpe]. */
	private static final Map<UUID, long[]> DRUM = new HashMap<>();
	/** Jugador agrandado con el mazo -> tick en que vuelve a su tamaño. */
	private static final Map<UUID, Long> GROWN = new HashMap<>();

	/** Pasillo seco de kanju: se vacía el agua de la caja cada pocos ticks hasta que vence. */
	private record Corridor(ServerLevel level, BlockPos from, BlockPos to, long until) {
	}

	private static final java.util.List<Corridor> CORRIDORS = new java.util.ArrayList<>();
	/** Burbuja de kanju: jugador -> tick en que se acaba. */
	private static final Map<UUID, Long> BUBBLES = new HashMap<>();
	public static final int TIDE_BUBBLE_RADIUS = 3;

	static void startBubble(Player player) {
		BUBBLES.put(player.getUUID(), player.level().getGameTime() + TIDE_TICKS);
	}

	/** Seca una esfera alrededor del jugador (el agua vuelve sola al apartarse o al acabar la burbuja). */
	private static void bubble(ServerLevel level, Player player) {
		BlockPos c = BlockPos.containing(player.getX(), player.getY() + 1, player.getZ());
		int r = TIDE_BUBBLE_RADIUS;
		for (BlockPos pos : BlockPos.betweenClosed(c.offset(-r, -r, -r), c.offset(r, r, r))) {
			if (pos.distSqr(c) <= r * r + 1 && level.getBlockState(pos).is(Blocks.WATER)) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
			}
		}
		if (player.tickCount % 6 == 0) {
			for (int i = 0; i < 12; i++) {
				double a = Math.PI * 2 * i / 12 + player.tickCount * 0.05;
				level.sendParticles(ParticleTypes.BUBBLE_POP, c.getX() + 0.5 + Math.cos(a) * r, c.getY() + 0.5 + Math.sin(a * 2) * 1.5,
						c.getZ() + 0.5 + Math.sin(a) * r, 1, 0, 0, 0, 0);
			}
		}
	}

	private JapanRelics() {
	}

	public static void init() {
		TickEvent.SERVER_POST.register(JapanRelics::tick);
		EntityEvent.LIVING_HURT.register(JapanRelics::onHurt);
	}

	public static boolean isDay(Level level) {
		return level.isBrightOutside();
	}

	// ---- Daño -------------------------------------------------------------------------------------------------------

	/**
	 * Daño recibido: el espejo devuelve los proyectiles; Raijin anula los rayos; el set de los Tres Tesoros da más daño
	 * de día; el kanabō ignora parte de la armadura.
	 */
	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		if (self instanceof Player player && player.getOffhandItem().is(ModItems.YATA_MIRROR.get())
				&& source.getDirectEntity() instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity shooter
				&& shooter != self) {
			RomeRelics.reflectLater(projectile, shooter, self);
			return 0;
		}
		if (source.is(DamageTypeTags.IS_LIGHTNING) && WornRelics.isWearing(self, ModItems.RAIJIN_DRUM)) {
			return 0;
		}
		if (source.getEntity() instanceof Player attacker && source.getDirectEntity() == attacker) {
			if (RelicSets.REGALIA.isComplete(attacker) && isDay(attacker.level())) {
				amount *= 1.0f + SET_DAMAGE;
			}
			if (attacker.getMainHandItem().is(ModItems.ONI_KANABO.get())) {
				// La armadura quita armadura/25 del daño (hasta 80 %): se compensa la mitad de esa reducción.
				float reduction = Math.min(20, self.getArmorValue()) / 25.0f;
				amount *= (1.0f - reduction * (1.0f - KANABO_ARMOR_IGNORED)) / (1.0f - reduction);
			}
		}
		return amount;
	}

	/** Tambor de Raijin: cada cuatro golpes seguidos, un rayo sobre el objetivo. */
	private static EventResult onHurt(LivingEntity target, DamageSource source, float amount) {
		if (!(source.getEntity() instanceof ServerPlayer player) || source.getDirectEntity() != player || target == player
				|| !WornRelics.isWearing(player, ModItems.RAIJIN_DRUM) || !(target.level() instanceof ServerLevel level)) {
			return EventResult.pass();
		}
		long now = level.getGameTime();
		long[] drum = DRUM.computeIfAbsent(player.getUUID(), k -> new long[] {0, 0});
		drum[0] = now - drum[1] <= RAIJIN_WINDOW ? drum[0] + 1 : 1;
		drum[1] = now;
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.PLAYERS,
				0.8f, 0.5f + drum[0] * 0.15f);
		if (drum[0] >= (RelicSets.FUJIN_RAIJIN.isComplete(player) ? JapanGods.SET_RAIJIN_HITS : RAIJIN_HITS)) {
			drum[0] = 0;
			io.github.potatustudio.mythicrelics.relic.RelicLightning.strike(level, target.getX(), target.getY(), target.getZ(), player);
			if (RelicSets.FUJIN_RAIJIN.isComplete(player)) {
				// Set de Fūjin y Raijin: el viento lleva el rayo a los hostiles más cercanos.
				level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(8),
								e -> e != target && e != player && e.isAlive() && e instanceof net.minecraft.world.entity.monster.Enemy
										&& !Allies.isFriend(player, e))
						.stream().sorted(java.util.Comparator.comparingDouble(e -> e.distanceToSqr(target)))
						.limit(JapanGods.SET_CHAIN_TARGETS).forEach(e -> {
							io.github.potatustudio.mythicrelics.relic.RelicLightning.strike(level, e.getX(), e.getY(), e.getZ(), player);
						});
			}
		}
		return EventResult.pass();
	}

	// ---- Kanju: pasillo seco ------------------------------------------------------------------------------------------

	static void openCorridor(ServerLevel level, BlockPos from, BlockPos to) {
		CORRIDORS.add(new Corridor(level, from, to, level.getGameTime() + TIDE_TICKS));
		drain(level, from, to);
	}

	private static void drain(ServerLevel level, BlockPos from, BlockPos to) {
		for (BlockPos pos : BlockPos.betweenClosed(from, to)) {
			if (level.getBlockState(pos).is(Blocks.WATER)) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
			}
		}
	}

	// ---- Mazo de la suerte --------------------------------------------------------------------------------------------

	static void grow(ServerPlayer player) {
		GROWN.put(player.getUUID(), player.level().getGameTime() + MALLET_TICKS);
		setModifier(player, Attributes.SCALE, MALLET_SCALE_ID, MALLET_SCALE);
		setModifier(player, Attributes.MAX_HEALTH, MALLET_HEALTH_ID, MALLET_HEALTH);
		setModifier(player, Attributes.ENTITY_INTERACTION_RANGE, MALLET_REACH_ID, 1.5);
		setModifier(player, Attributes.BLOCK_INTERACTION_RANGE, MALLET_BLOCK_REACH_ID, 1.5);
		setModifier(player, Attributes.ATTACK_DAMAGE, MALLET_DAMAGE_ID, MALLET_DAMAGE);
		setModifier(player, Attributes.JUMP_STRENGTH, MALLET_JUMP_ID, 0.15);
		setModifier(player, Attributes.STEP_HEIGHT, MALLET_STEP_ID, 0.6);
		setModifier(player, Attributes.SAFE_FALL_DISTANCE, MALLET_FALL_ID, 4.0);
		setModifier(player, Attributes.KNOCKBACK_RESISTANCE, MALLET_KNOCKBACK_ID, 0.5);
		player.heal(MALLET_HEALTH);
	}

	private static void shrink(ServerPlayer player) {
		setModifier(player, Attributes.SCALE, MALLET_SCALE_ID, 0);
		setModifier(player, Attributes.MAX_HEALTH, MALLET_HEALTH_ID, 0);
		setModifier(player, Attributes.ENTITY_INTERACTION_RANGE, MALLET_REACH_ID, 0);
		setModifier(player, Attributes.BLOCK_INTERACTION_RANGE, MALLET_BLOCK_REACH_ID, 0);
		setModifier(player, Attributes.ATTACK_DAMAGE, MALLET_DAMAGE_ID, 0);
		setModifier(player, Attributes.JUMP_STRENGTH, MALLET_JUMP_ID, 0);
		setModifier(player, Attributes.STEP_HEIGHT, MALLET_STEP_ID, 0);
		setModifier(player, Attributes.SAFE_FALL_DISTANCE, MALLET_FALL_ID, 0);
		setModifier(player, Attributes.KNOCKBACK_RESISTANCE, MALLET_KNOCKBACK_ID, 0);
		if (player.getHealth() > player.getMaxHealth()) {
			player.setHealth(player.getMaxHealth());
		}
		player.level().sendParticles(ParticleTypes.POOF, player.getX(), player.getY() + 1, player.getZ(), 12, 0.4, 0.6, 0.4, 0.02);
	}

	// ---- Cada tick ----------------------------------------------------------------------------------------------------

	private static void tick(MinecraftServer server) {
		long now = server.overworld().getGameTime();
		Iterator<Corridor> corridors = CORRIDORS.iterator();
		while (corridors.hasNext()) {
			Corridor c = corridors.next();
			if (c.level().getGameTime() >= c.until()) {
				corridors.remove();
			} else if (now % 2 == 0) {
				drain(c.level(), c.from(), c.to());
			}
		}
		BUBBLES.values().removeIf(until -> until <= now);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (now % 2 == 0 && BUBBLES.containsKey(player.getUUID())) {
				bubble(player.level(), player);
			}
			Long until = GROWN.get(player.getUUID());
			if (until != null && until <= now) {
				GROWN.remove(player.getUUID());
				shrink(player);
			} else if (until == null && player.getAttribute(Attributes.SCALE) != null
					&& player.getAttribute(Attributes.SCALE).getModifier(MALLET_SCALE_ID) != null) {
				// Si el servidor se cerró con el jugador agrandado, vuelve a su tamaño.
				shrink(player);
			}
			ServerLevel level = player.level();
			boolean day = isDay(level) && level.canSeeSky(player.blockPosition().above());
			if (player.tickCount % MAGATAMA_INTERVAL == 0 && day && WornRelics.isWearing(player, ModItems.YASAKANI_MAGATAMA)) {
				player.heal(1.0f);
			}
			if (player.tickCount % 40 == 0 && isDay(level) && RelicSets.REGALIA.isComplete(player)) {
				player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 0, true, false));
			}
		}
	}

	static void setModifier(Player player, Holder<Attribute> attribute, Identifier id, double amount) {
		AttributeInstance instance = player.getAttribute(attribute);
		if (instance == null) {
			return;
		}
		AttributeModifier current = instance.getModifier(id);
		if (current != null && current.amount() == amount) {
			return;
		}
		instance.removeModifier(id);
		if (amount != 0) {
			instance.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
		}
	}

	static Vec3 flat(Vec3 v) {
		Vec3 f = new Vec3(v.x, 0, v.z);
		return f.lengthSqr() < 1.0E-6 ? new Vec3(0, 0, 1) : f.normalize();
	}

	/** Números de los tooltips japoneses. */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "kusanagi" -> new Object[] {KUSANAGI_SLASH_RANGE, (int) KUSANAGI_SLASH_DAMAGE, KUSANAGI_COOLDOWN / 20};
			case "yata_mirror" -> new Object[] {MIRROR_FLASH_RADIUS, MIRROR_BLIND_TICKS / 20, MIRROR_COOLDOWN / 20};
			case "yasakani_magatama" -> new Object[] {MAGATAMA_INTERVAL / 20, Math.round(SET_DAMAGE * 100)};
			case "raijin_drum" -> new Object[] {RAIJIN_HITS, RAIJIN_WINDOW / 20};
			case "oni_kanabo" -> new Object[] {Math.round(KANABO_ARMOR_IGNORED * 100)};
			case "kitsune_star_ball" -> new Object[] {KITSUNE_TICKS / 20, KITSUNE_HEALTH, KITSUNE_DAMAGE, KITSUNEBI_INTERVAL / 20,
					KITSUNEBI_BURN_SECONDS, KITSUNE_COOLDOWN / 20 / 60};
			case "tide_jewels" -> new Object[] {TIDE_BUBBLE_RADIUS, TIDE_TICKS / 20, TIDE_WAVE_RANGE, TIDE_COOLDOWN / 20};
			case "lucky_mallet" -> new Object[] {TooltipNumbers.fmtTenths(1 + MALLET_SCALE), MALLET_TICKS / 20, MALLET_HEALTH / 2, MALLET_COOLDOWN / 20 / 60,
					MALLET_GROW_RADIUS};
			case "kibi_dango" -> new Object[] {KIBI_STRENGTH_TICKS / 20, KIBI_MAX};
			case "urashima_rod" -> new Object[] {TURTLE_LEAVE_TICKS / 20, TURTLE_COOLDOWN / 20 / 60};
			default -> JapanGods.tooltipArgs(id);
		};
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		io.github.potatustudio.mythicrelics.relic.ServerState.perPlayer(DRUM, GROWN, BUBBLES);
		io.github.potatustudio.mythicrelics.relic.ServerState.clearOnStop(CORRIDORS);
	}
}
