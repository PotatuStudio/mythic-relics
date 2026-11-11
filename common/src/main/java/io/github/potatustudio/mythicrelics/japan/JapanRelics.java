package io.github.potatustudio.mythicrelics.japan;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.registry.ModItems;
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
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
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
	public static final float KUSANAGI_SLASH_DAMAGE = 6.0f;
	public static final int KUSANAGI_SLASH_RANGE = 5;
	public static final int KUSANAGI_COOLDOWN = 160;

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
	public static final int TIDE_WIDTH = 3;
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

	/** Golpes seguidos con el tambor de Raijin: [cantidad, tick del último golpe]. */
	private static final Map<UUID, long[]> DRUM = new HashMap<>();
	/** Jugador agrandado con el mazo -> tick en que vuelve a su tamaño. */
	private static final Map<UUID, Long> GROWN = new HashMap<>();

	/** Pasillo seco de kanju: se vacía el agua de la caja cada pocos ticks hasta que vence. */
	private record Corridor(ServerLevel level, BlockPos from, BlockPos to, long until) {
	}

	private static final java.util.List<Corridor> CORRIDORS = new java.util.ArrayList<>();

	private JapanRelics() {
	}

	public static void init() {
		TickEvent.SERVER_POST.register(JapanRelics::tick);
		EntityEvent.LIVING_HURT.register(JapanRelics::onHurt);
	}

	public static boolean isDay(Level level) {
		return level.isBrightOutside();
	}

	/** Set de los Tres Tesoros: Kusanagi en una mano, el espejo en la otra y la magatama puesta. */
	public static boolean hasRegalia(Player player) {
		boolean sword = player.getMainHandItem().is(ModItems.KUSANAGI.get()) || player.getOffhandItem().is(ModItems.KUSANAGI.get());
		boolean mirror = player.getMainHandItem().is(ModItems.YATA_MIRROR.get()) || player.getOffhandItem().is(ModItems.YATA_MIRROR.get());
		return sword && mirror && EgyptRelics.isWearing(player, ModItems.YASAKANI_MAGATAMA);
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
		if (source.is(DamageTypeTags.IS_LIGHTNING) && EgyptRelics.isWearing(self, ModItems.RAIJIN_DRUM)) {
			return 0;
		}
		if (source.getEntity() instanceof Player attacker && source.getDirectEntity() == attacker) {
			if (hasRegalia(attacker) && isDay(attacker.level())) {
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
				|| !EgyptRelics.isWearing(player, ModItems.RAIJIN_DRUM) || !(target.level() instanceof ServerLevel level)) {
			return EventResult.pass();
		}
		long now = level.getGameTime();
		long[] drum = DRUM.computeIfAbsent(player.getUUID(), k -> new long[] {0, 0});
		drum[0] = now - drum[1] <= RAIJIN_WINDOW ? drum[0] + 1 : 1;
		drum[1] = now;
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.PLAYERS,
				0.8f, 0.5f + drum[0] * 0.15f);
		if (drum[0] >= RAIJIN_HITS) {
			drum[0] = 0;
			LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
			if (bolt != null) {
				bolt.snapTo(target.getX(), target.getY(), target.getZ());
				bolt.setCause(player);
				level.addFreshEntity(bolt);
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
		player.heal(MALLET_HEALTH);
	}

	private static void shrink(ServerPlayer player) {
		setModifier(player, Attributes.SCALE, MALLET_SCALE_ID, 0);
		setModifier(player, Attributes.MAX_HEALTH, MALLET_HEALTH_ID, 0);
		setModifier(player, Attributes.ENTITY_INTERACTION_RANGE, MALLET_REACH_ID, 0);
		setModifier(player, Attributes.BLOCK_INTERACTION_RANGE, MALLET_BLOCK_REACH_ID, 0);
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
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
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
			if (player.tickCount % MAGATAMA_INTERVAL == 0 && day && EgyptRelics.isWearing(player, ModItems.YASAKANI_MAGATAMA)) {
				player.heal(1.0f);
			}
			if (player.tickCount % 40 == 0 && isDay(level) && hasRegalia(player)) {
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
			case "tide_jewels" -> new Object[] {TIDE_LENGTH, TIDE_TICKS / 20, TIDE_WAVE_RANGE, TIDE_COOLDOWN / 20};
			case "lucky_mallet" -> new Object[] {fmt(1 + MALLET_SCALE), MALLET_TICKS / 20, MALLET_HEALTH / 2, MALLET_COOLDOWN / 20 / 60,
					MALLET_GROW_RADIUS};
			case "kibi_dango" -> new Object[] {KIBI_STRENGTH_TICKS / 20, KIBI_MAX};
			case "urashima_rod" -> new Object[] {TURTLE_LEAVE_TICKS / 20, TURTLE_COOLDOWN / 20 / 60};
			default -> new Object[0];
		};
	}

	private static String fmt(double v) {
		return v == Math.floor(v) ? Integer.toString((int) v) : Double.toString(Math.round(v * 10) / 10.0);
	}
}
