package io.github.potatustudio.mythicrelics.greece;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.network.HermesJumpPayload;
import io.github.potatustudio.mythicrelics.network.PetrifyPayload;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Primera tanda de Grecia (diseño aprobado el 2026-11-09):
 * <ul>
 *   <li><b>Sandalias aladas de Hermes</b> (pies): más velocidad, doble salto (lo pide el cliente con
 *       {@link HermesJumpPayload}) y la arena y la tierra de almas no frenan.</li>
 *   <li><b>Yelmo de Hades</b> (sombrero): agachado y quieto te vuelves invisible; moverse rápido o atacar lo rompe.</li>
 *   <li><b>Égida de Atenea</b> (espalda): al bloquear con escudo un golpe cuerpo a cuerpo, el atacante queda aturdido.</li>
 *   <li><b>Set de Perseo</b> (las tres): la cabeza de Medusa recarga a la mitad y lo petrificado recibe más daño.</li>
 *   <li><b>Cabeza de Medusa</b>, <b>Hilo de Ariadna</b> y <b>Lira de Orfeo</b>: ítems que se usan
 *       ({@link MedusaHeadItem}, {@link AriadneThreadItem}, {@link OrpheusLyreItem}).</li>
 *   <li><b>Óbolo de Caronte</b>: al morir conservas inventario y experiencia ({@link CharonStash}).</li>
 * </ul>
 */
public final class GreeceRelics {
	public static final float HERMES_SPEED = 0.20f;
	public static final double HERMES_JUMP = 0.55;
	private static final Identifier HERMES_SPEED_ID = MythicRelics.id("hermes_speed");
	private static final Identifier HERMES_SOUL_ID = MythicRelics.id("hermes_soul");

	public static final int HADES_STILL_TICKS = 40;
	private static final double HADES_MAX_SPEED = 0.08;

	public static final int AEGIS_STUN_TICKS = 40;

	public static final int MEDUSA_TICKS = 100;
	public static final double MEDUSA_RADIUS = 16.0;
	public static final int MEDUSA_COOLDOWN = 1200;
	public static final float PERSEUS_PETRIFIED_BONUS = 0.5f;
	/** Coseno del medio ángulo del cono de la mirada de Medusa (50°). */
	public static final double MEDUSA_CONE = 0.64;
	/** Probabilidad de que lo que muere petrificado deje su estatua de piedra. */
	public static final float MEDUSA_STATUE_CHANCE = 0.15f;
	public static final String PETRIFIED_TAG = MythicRelics.MOD_ID + ".petrified";

	public static final int ARIADNE_MAX_DISTANCE = 256;
	public static final int ARIADNE_COOLDOWN = 6000;

	public static final double ORPHEUS_ANIMALS = 16.0;
	public static final double ORPHEUS_HOSTILES = 8.0;

	/** Ticks que lleva cada jugador agachado y quieto con el yelmo, y si ya lo volvimos invisible. */
	private static final Map<UUID, int[]> HADES_STILL = new HashMap<>();
	private static final Map<UUID, Boolean> HADES_INVISIBLE = new HashMap<>();
	/** Mob petrificado -> tick del juego en que se libera. */
	private static final Map<UUID, Long> PETRIFIED = new HashMap<>();

	private GreeceRelics() {
	}

	public static void init() {
		NetworkManager.registerReceiver(NetworkManager.c2s(), HermesJumpPayload.TYPE, HermesJumpPayload.CODEC,
				(payload, context) -> context.queue(() -> {
					if (context.getPlayer() instanceof ServerPlayer player) {
						hermesJumped(player);
					}
				}));
		if (dev.architectury.platform.Platform.getEnvironment() == dev.architectury.utils.Env.SERVER) {
			NetworkManager.registerS2CPayloadType(PetrifyPayload.TYPE, PetrifyPayload.CODEC);
		}
		EntityEvent.ADD.register(GreeceRelics::onEntityAdded);
		EntityEvent.LIVING_DEATH.register((victim, source) -> {
			if (victim instanceof Mob mob && mob.entityTags().contains(PETRIFIED_TAG) && mob.level() instanceof ServerLevel level) {
				crumble(level, mob, source);
			}
			return EventResult.pass();
		});
		EntityEvent.LIVING_DAMAGE_POST.register((entity, source, baseDamage, damageTaken, blocked) -> {
			// Atacar rompe la invisibilidad del yelmo.
			if (source.getEntity() instanceof ServerPlayer attacker) {
				breakInvisibility(attacker);
			}
		});
		TickEvent.SERVER_POST.register(GreeceRelics::tick);
		CharonStash.init();
	}

	public static boolean hasPerseusSet(LivingEntity entity) {
		return EgyptRelics.isWearing(entity, ModItems.HERMES_SANDALS) && EgyptRelics.isWearing(entity, ModItems.HADES_HELM)
				&& EgyptRelics.isWearing(entity, ModItems.ATHENA_AEGIS);
	}

	// ---- Hermes ------------------------------------------------------------------------------------------------------

	/** El cliente ya aplicó el impulso del doble salto; aquí se valida, se reinicia la caída y se muestran las alas. */
	private static void hermesJumped(ServerPlayer player) {
		if (!EgyptRelics.isWearing(player, ModItems.HERMES_SANDALS) && !EgyptRelics.isWearing(player, ModItems.TENGU_MASK)
				|| player.onGround()) {
			return;
		}
		player.resetFallDistance();
		ServerLevel level = player.level();
		level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 8, 0.3, 0.05, 0.3, 0.02);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.3f, 1.8f);
	}

	private static void setModifier(Player player, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
			Identifier id, double amount, AttributeModifier.Operation operation) {
		AttributeInstance instance = player.getAttribute(attribute);
		if (instance == null) {
			return;
		}
		boolean has = instance.hasModifier(id);
		if (amount == 0 && has) {
			instance.removeModifier(id);
		} else if (amount != 0 && !has) {
			instance.addTransientModifier(new AttributeModifier(id, amount, operation));
		}
	}

	// ---- Hades -------------------------------------------------------------------------------------------------------

	private static void hades(ServerPlayer player) {
		if (!EgyptRelics.isWearing(player, ModItems.HADES_HELM)) {
			breakInvisibility(player);
			HADES_STILL.remove(player.getUUID());
			return;
		}
		Vec3 motion = player.getDeltaMovement();
		boolean invisible = HADES_INVISIBLE.getOrDefault(player.getUUID(), false);
		double speed = Math.hypot(motion.x, motion.z);
		// Para volverse invisible hay que estar quieto; ya invisible, se puede avanzar agachado (no rápido).
		boolean calm = player.isShiftKeyDown() && !player.isSprinting() && speed < (invisible ? HADES_MAX_SPEED : 0.01);
		if (!calm) {
			breakInvisibility(player);
			HADES_STILL.remove(player.getUUID());
			return;
		}
		int[] still = HADES_STILL.computeIfAbsent(player.getUUID(), k -> new int[1]);
		still[0]++;
		if (still[0] >= HADES_STILL_TICKS) {
			if (!invisible) {
				HADES_INVISIBLE.put(player.getUUID(), true);
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE,
						SoundSource.PLAYERS, 0.6f, 0.7f);
			}
			if (still[0] % 20 == 0 || !player.hasEffect(MobEffects.INVISIBILITY)) {
				player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40, 0, false, false, true));
			}
			if (still[0] % 10 == 0) {
				// Los mobs pierden tu rastro.
				for (Mob mob : player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(24), m -> m.getTarget() == player)) {
					mob.setTarget(null);
				}
			}
		}
	}

	private static void breakInvisibility(ServerPlayer player) {
		if (HADES_INVISIBLE.remove(player.getUUID()) != null) {
			player.removeEffect(MobEffects.INVISIBILITY);
		}
		HADES_STILL.remove(player.getUUID());
	}

	// ---- Égida (lo llama el mixin de LivingEntity al bloquear) -----------------------------------------------------

	public static void aegisBlocked(LivingEntity defender, LivingEntity attacker) {
		if (!(defender.level() instanceof ServerLevel level) || !EgyptRelics.isWearing(defender, ModItems.ATHENA_AEGIS) || isBoss(attacker)) {
			return;
		}
		attacker.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, AEGIS_STUN_TICKS, 5), defender);
		attacker.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, AEGIS_STUN_TICKS, 3), defender);
		Vec3 away = attacker.position().subtract(defender.position()).multiply(1, 0, 1).normalize().scale(0.8);
		attacker.push(away.x, 0.25, away.z);
		attacker.needsSync = true;
		level.sendParticles(ParticleTypes.CRIT, attacker.getX(), attacker.getY() + attacker.getBbHeight(), attacker.getZ(), 10, 0.3, 0.2, 0.3, 0.1);
		level.playSound(null, defender.getX(), defender.getY(), defender.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 0.6f, 1.4f);
	}

	// ---- Medusa ------------------------------------------------------------------------------------------------------

	public static boolean isBoss(Entity entity) {
		return entity instanceof WitherBoss || entity instanceof EnderDragon || entity instanceof Warden || entity instanceof ElderGuardian;
	}

	/**
	 * Petrifica a los hostiles que tienes delante (en un cono de 100° hasta {@link #MEDUSA_RADIUS} bloques, con línea de
	 * vista); devuelve cuántos. Se quedan quietos y se ven de piedra; si mueren así, se derrumban ({@link #crumble}).
	 */
	public static int petrify(ServerPlayer player) {
		ServerLevel level = player.level();
		Vec3 eyes = player.getEyePosition();
		Vec3 look = player.getLookAngle();
		int count = 0;
		for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(MEDUSA_RADIUS),
				m -> m.isAlive() && m instanceof net.minecraft.world.entity.monster.Enemy && !isBoss(m)
						&& m.distanceToSqr(player) <= MEDUSA_RADIUS * MEDUSA_RADIUS)) {
			Vec3 toMob = mob.getBoundingBox().getCenter().subtract(eyes).normalize();
			if (look.dot(toMob) < MEDUSA_CONE || !player.hasLineOfSight(mob)) {
				continue;
			}
			mob.setNoAi(true);
			mob.setDeltaMovement(Vec3.ZERO);
			mob.addTag(PETRIFIED_TAG);
			PETRIFIED.put(mob.getUUID(), level.getGameTime() + MEDUSA_TICKS);
			sync(level, mob, true);
			level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
					mob.getX(), mob.getY() + mob.getBbHeight() / 2, mob.getZ(), 30, 0.3, 0.5, 0.3, 0.1);
			level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.STONE_PLACE, SoundSource.HOSTILE, 1.0f, 0.6f);
			count++;
		}
		return count;
	}

	private static void release(Mob mob) {
		mob.setNoAi(false);
		mob.removeTag(PETRIFIED_TAG);
		if (mob.level() instanceof ServerLevel level) {
			sync(level, mob, false);
			level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GRAVEL.defaultBlockState()),
					mob.getX(), mob.getY() + mob.getBbHeight() / 2, mob.getZ(), 15, 0.3, 0.5, 0.3, 0.1);
		}
	}

	/** Avisa a los jugadores cercanos para que el mob se dibuje de piedra (o vuelva a su color). */
	private static void sync(ServerLevel level, Mob mob, boolean on) {
		java.util.List<ServerPlayer> near = level.players().stream().filter(p -> p.distanceToSqr(mob) < 128 * 128).toList();
		NetworkManager.sendToPlayers(near, new PetrifyPayload(mob.getId(), on));
	}

	/** Lo que muere petrificado se derrumba: polvo de piedra, el doble de botín y a veces su estatua. */
	private static void crumble(ServerLevel level, Mob mob, DamageSource source) {
		PETRIFIED.remove(mob.getUUID());
		sync(level, mob, false);
		double cy = mob.getY() + mob.getBbHeight() / 2;
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
				mob.getX(), cy, mob.getZ(), 60, mob.getBbWidth() * 0.4, mob.getBbHeight() * 0.4, mob.getBbWidth() * 0.4, 0.15);
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GRAVEL.defaultBlockState()),
				mob.getX(), cy, mob.getZ(), 30, mob.getBbWidth() * 0.4, mob.getBbHeight() * 0.4, mob.getBbWidth() * 0.4, 0.1);
		level.sendParticles(ParticleTypes.CLOUD, mob.getX(), mob.getY() + 0.2, mob.getZ(), 12, 0.4, 0.1, 0.4, 0.02);
		level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.STONE_BREAK, SoundSource.HOSTILE, 1.4f, 0.6f);
		level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.DEEPSLATE_BRICKS_BREAK, SoundSource.HOSTILE, 1.0f, 0.5f);
		net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> table =
				mob.getType().getDefaultLootTable().orElse(null);
		if (table != null) {
			mob.dropFromLootTable(level, source, true, table);
		}
		if (level.getRandom().nextFloat() < MEDUSA_STATUE_CHANCE) {
			net.minecraft.world.entity.item.ItemEntity drop = new net.minecraft.world.entity.item.ItemEntity(level, mob.getX(),
					mob.getY() + 0.5, mob.getZ(), StoneStatueItem.of(mob.getType()));
			level.addFreshEntity(drop);
		}
	}

	/** Daño recibido: lo petrificado recibe +50 % si el atacante lleva el set de Perseo. */
	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		if (self.entityTags().contains(PETRIFIED_TAG) && source.getEntity() instanceof LivingEntity attacker && hasPerseusSet(attacker)) {
			return amount * (1.0f + PERSEUS_PETRIFIED_BONUS);
		}
		return amount;
	}

	private static EventResult onEntityAdded(Entity entity, Level level) {
		// Si el servidor se cerró con un mob petrificado, al cargarlo se libera (si no, quedaría sin IA para siempre).
		if (!level.isClientSide() && entity instanceof Mob mob && mob.entityTags().contains(PETRIFIED_TAG)
				&& !PETRIFIED.containsKey(mob.getUUID())) {
			release(mob);
		}
		return EventResult.pass();
	}

	// ---- Cada tick ---------------------------------------------------------------------------------------------------

	private static void tick(MinecraftServer server) {
		long now = server.overworld().getGameTime();
		Iterator<Map.Entry<UUID, Long>> it = PETRIFIED.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Long> e = it.next();
			if (e.getValue() > now) {
				if (now % 10 == 0) {
					for (ServerLevel level : server.getAllLevels()) {
						if (level.getEntity(e.getKey()) instanceof Mob mob) {
							level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
									mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(), 3, 0.25, 0.4, 0.25, 0.0);
							break;
						}
					}
				}
				continue;
			}
			for (ServerLevel level : server.getAllLevels()) {
				if (level.getEntity(e.getKey()) instanceof Mob mob) {
					release(mob);
					break;
				}
			}
			it.remove();
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			boolean sandals = EgyptRelics.isWearing(player, ModItems.HERMES_SANDALS);
			setModifier(player, Attributes.MOVEMENT_SPEED, HERMES_SPEED_ID, sandals ? HERMES_SPEED : 0,
					AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
			boolean onSoul = sandals && player.level().getBlockState(BlockPos.containing(player.getX(), player.getY() - 0.2, player.getZ()))
					.is(BlockTags.SOUL_SPEED_BLOCKS);
			// Hermes guía las almas: la arena y la tierra de almas no te frenan.
			setModifier(player, Attributes.MOVEMENT_EFFICIENCY, HERMES_SOUL_ID, onSoul ? 1.0 : 0, AttributeModifier.Operation.ADD_VALUE);
			hades(player);
		}
	}

	/** Números de los tooltips de Grecia (%1$s, %2$s… de sus líneas de traducción, en este orden). */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "hermes_sandals" -> new Object[] {Math.round(HERMES_SPEED * 100)};
			case "hades_helm" -> new Object[] {HADES_STILL_TICKS / 20};
			case "athena_aegis" -> new Object[] {AEGIS_STUN_TICKS / 20};
			case "medusa_head" -> new Object[] {MEDUSA_TICKS / 20, (int) MEDUSA_RADIUS, MEDUSA_COOLDOWN / 20};
			case "ariadne_thread" -> new Object[] {ARIADNE_MAX_DISTANCE, ARIADNE_COOLDOWN / 20 / 60};
			case "orpheus_lyre" -> new Object[] {(int) ORPHEUS_ANIMALS, (int) ORPHEUS_HOSTILES};
			default -> GreeceHeroes.tooltipArgs(id);
		};
	}
}
