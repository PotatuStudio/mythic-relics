package io.github.potatustudio.mythicrelics.greece;

import io.github.potatustudio.mythicrelics.relic.HitEffects;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.entity.Talos;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.Bosses;
import io.github.potatustudio.mythicrelics.relic.ExecuteWarning;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.ServerState;
import io.github.potatustudio.mythicrelics.relic.TooltipArgs;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/**
 * Tercera tanda griega: lo más conocido de Grecia que sí es mito.
 * <ul>
 *   <li><b>Rayo de Zeus</b> ({@link ZeusThunderboltItem}): arma a distancia que salta entre enemigos.</li>
 *   <li><b>Piel del león de Nemea</b> (capa): la mitad del daño de armas y proyectiles; sin arma, más daño.</li>
 *   <li><b>Escudo de Aquiles</b> (escudo propio): cada golpe bloqueado suma cólera para el siguiente golpe.</li>
 *   <li><b>Harpe</b> ({@link HarpeItem}): cabezas de trofeo, decapita a los mobs malheridos y avisa cuándo se puede;
 *       entra en el set de Perseo.</li>
 *   <li><b>Brida de oro de Atenea</b> ({@link GoldenBridleItem}): Pegaso, montura voladora propia.</li>
 *   <li><b>Manzana de la Discordia</b> ({@link AppleOfDiscordItem}): los hostiles atacan al mob marcado.</li>
 *   <li><b>Clavo de bronce de Talos</b> ({@link TalosNailItem}): el gigante de bronce de Creta pelea a tu lado.</li>
 * </ul>
 */
public final class GreeceLegends {
	// Rayo de Zeus
	public static final int ZEUS_RANGE = 32;
	public static final int ZEUS_CHARGE_TICKS = 10;
	public static final float ZEUS_DAMAGE = 14.0f;
	public static final float ZEUS_CHAIN_DAMAGE = 7.0f;
	public static final int ZEUS_CHAIN_TARGETS = 3;
	public static final int ZEUS_STORM_CHAIN_TARGETS = 5;
	public static final int ZEUS_CHAIN_RADIUS = 6;
	public static final int ZEUS_COOLDOWN = 100;
	/** Ataque fuerte: mantener la carga hasta acá (2 s) suelta varios rayos, cada uno con su daño y su salto. */
	public static final int ZEUS_STRONG_TICKS = 40;
	public static final int ZEUS_STRONG_BOLTS = 3;
	public static final int ZEUS_STRONG_COOLDOWN = 200;
	// Piel del león de Nemea
	public static final float NEMEA_REDUCTION = 0.5f;
	public static final float NEMEA_FIST_DAMAGE = 3.0f;
	// Escudo de Aquiles
	/** Daño extra del golpe que sigue a un bloqueo con el escudo de Aquiles (una sola carga). */
	public static final float WRATH_BONUS = 1.0f;
	public static final int WRATH_TICKS = 200;
	public static final float WRATH_KNOCKBACK = 1.6f;
	// Harpe
	/** Pasiva: el golpe a un mob malherido le corta la cabeza; la cabeza cae el 75 % de las veces (cráneo de wither 25 %). */
	public static final float HARPE_HEAD_CHANCE = 0.75f;
	public static final float HARPE_WITHER_SKULL_CHANCE = 0.25f;
	/** Muertes con la harpe sin decapitar (golpes que no cumplían el umbral). */
	public static final float HARPE_KILL_HEAD_CHANCE = 0.15f;
	public static final float HARPE_EXECUTE_HEALTH = 0.25f;
	public static final int HARPE_COOLDOWN = 20;
	public static final int HARPE_WARN_RADIUS = 12;
	// Pegaso
	public static final int PEGASUS_LEAVE_TICKS = 60;
	public static final int PEGASUS_COOLDOWN = 6000;
	public static final float PEGASUS_DIVE_DAMAGE = 12.0f;
	public static final int PEGASUS_OLYMPUS_Y = 200;
	public static final int PEGASUS_FALL_TICKS = 100;
	// Manzana de la Discordia
	public static final int APPLE_RANGE = 16;
	public static final int APPLE_RADIUS = 12;
	public static final int APPLE_TICKS = 300;
	public static final int APPLE_COOLDOWN = 1200;
	// Talos
	public static final int TALOS_TICKS = 1800;
	public static final int TALOS_COOLDOWN = 6000;
	public static final float TALOS_ROCK_DAMAGE = 10.0f;
	public static final int TALOS_ROCK_INTERVAL = 60;
	public static final int TALOS_ROCK_RANGE = 16;
	public static final float TALOS_HUG_DAMAGE = 8.0f;
	public static final int TALOS_HUG_FIRE_SECONDS = 4;
	public static final float TALOS_ANKLE_MULTIPLIER = 2.0f;

	public static final String TALOS_ROCK_TAG = "mythicrelics.talos_rock";
	private static final Identifier WRATH_KNOCKBACK_ID = Identifier.fromNamespaceAndPath("mythicrelics", "achilles_wrath");

	/** Cólera de Aquiles: jugadores con la carga lista y hasta cuándo dura. */
	private static final Map<UUID, Long> WRATH_UNTIL = new HashMap<>();
	/** Manzana de la Discordia: mob marcado y hasta cuándo. */
	private static final Map<Mob, Long> FAIREST = new WeakHashMap<>();
	/** Harpe: mobs que ya avisaron (para que el aviso suene una vez por mob). */

	private GreeceLegends() {
	}

	public static void init() {
		TickEvent.SERVER_POST.register(GreeceLegends::tick);
		EntityEvent.LIVING_DEATH.register(GreeceLegends::onDeath);
	}

	// ---- Daño -----------------------------------------------------------------------------------------------------

	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		Entity direct = source.getDirectEntity();
		// Roca de Talos: una bola de nieve con forma de roca que pega como una roca.
		if (direct != null && direct.entityTags().contains(TALOS_ROCK_TAG)) {
			amount = Math.max(amount, TALOS_ROCK_DAMAGE);
		}
		// Piel de Nemea: ningún arma la atraviesa (mobs con arma en la mano y proyectiles), sí garras y fuego.
		if (WornRelics.isWearing(self, ModItems.NEMEAN_LION_SKIN)) {
			boolean projectile = source.is(DamageTypeTags.IS_PROJECTILE);
			boolean weapon = source.getEntity() instanceof LivingEntity attacker && direct == attacker && !attacker.getMainHandItem().isEmpty()
					&& !source.is(DamageTypeTags.IS_FIRE) && !source.is(DamageTypeTags.IS_EXPLOSION);
			if (projectile || weapon) {
				amount *= 1.0f - NEMEA_REDUCTION;
			}
		}
		if (source.getEntity() instanceof Player attacker && direct == attacker) {
			// Como Heracles: sin arma, con la piel puesta, el golpe pesa más.
			if (attacker.getMainHandItem().isEmpty() && WornRelics.isWearing(attacker, ModItems.NEMEAN_LION_SKIN)) {
				amount += NEMEA_FIST_DAMAGE;
			}
			amount = releaseWrath(attacker, self, amount);
			// Harpe (pasiva): el golpe a un mob malherido le corta la cabeza.
			amount = HarpeItem.behead(attacker, self, amount);
		}
		// Talos: por la espalda (el tobillo) recibe el doble.
		if (self instanceof Talos && source.getSourcePosition() != null) {
			Vec3 facing = Vec3.directionFromRotation(0, self.getYRot());
			Vec3 from = source.getSourcePosition().subtract(self.position()).multiply(1, 0, 1).normalize();
			if (facing.dot(from) < -0.4) {
				amount *= TALOS_ANKLE_MULTIPLIER;
			}
		}
		return amount;
	}

	// ---- Escudo de Aquiles: la cólera -------------------------------------------------------------------------------

	/** Después de bloquear con el escudo de Aquiles: el siguiente golpe queda cargado de cólera (una sola carga). */
	public static void afterBlocking(LivingEntity self, DamageSource source, float blocked) {
		if (!(self instanceof ServerPlayer player) || blocked <= 0 || !player.getUseItem().is(ModItems.ACHILLES_SHIELD.get())) {
			return;
		}
		boolean fresh = !WRATH_UNTIL.containsKey(player.getUUID());
		WRATH_UNTIL.put(player.getUUID(), player.level().getGameTime() + WRATH_TICKS);
		if (!fresh) {
			return;
		}
		ServerLevel level = player.level();
		level.sendParticles(ParticleTypes.ANGRY_VILLAGER, player.getX(), player.getY() + 2.1, player.getZ(), 3, 0.3, 0.1, 0.3, 0.0);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.3f, 1.3f);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.achilles_wrath").withStyle(ChatFormatting.RED));
	}

	/** Si tiene la cólera cargada, el golpe hace +{@link #WRATH_BONUS} y manda lejos al golpeado. */
	private static float releaseWrath(Player attacker, LivingEntity target, float amount) {
		if (!WRATH_UNTIL.containsKey(attacker.getUUID()) || !(attacker.level() instanceof ServerLevel level)) {
			return amount;
		}
		// La cólera se gasta solo si el golpe entra (ver HitEffects).
		HitEffects.onLanded(target, () -> {
			WRATH_UNTIL.remove(attacker.getUUID());
			level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.6), target.getZ(), 30, 0.4, 0.4, 0.4, 0.3);
			Vec3 push = target.position().subtract(attacker.position()).multiply(1, 0, 1).normalize();
			target.push(push.x * WRATH_KNOCKBACK, 0.45, push.z * WRATH_KNOCKBACK);
			target.needsSync = true;
			RelicFx.sound(level, target, SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.2f, 0.7f);
		});
		return amount * (1.0f + WRATH_BONUS);
	}

	// ---- Harpe ------------------------------------------------------------------------------------------------------

	/** Se puede decapitar: vivo, no jefe y con menos del 25 % de la vida. */
	public static boolean canBehead(LivingEntity e) {
		return canBehead(e, 0.0f);
	}

	/** Se puede decapitar con un golpe de {@code damage}: la vida que le quedaría está bajo el 25 % (o el golpe lo mata). */
	public static boolean canBehead(LivingEntity e, float damage) {
		return e.isAlive() && !(e instanceof Player) && !Bosses.is(e)
				&& e.getHealth() - damage <= e.getMaxHealth() * HARPE_EXECUTE_HEALTH;
	}

	/** La cabeza de trofeo de cada mob, o vacío si no tiene. */
	public static ItemStack headOf(LivingEntity e) {
		EntityType<?> t = e.getType();
		if (t == EntityTypes.ZOMBIE || t == EntityTypes.HUSK || t == EntityTypes.DROWNED) {
			return new ItemStack(Items.ZOMBIE_HEAD);
		}
		if (t == EntityTypes.SKELETON || t == EntityTypes.STRAY || t == EntityTypes.BOGGED) {
			return new ItemStack(Items.SKELETON_SKULL);
		}
		if (t == EntityTypes.WITHER_SKELETON) {
			return new ItemStack(Items.WITHER_SKELETON_SKULL);
		}
		if (t == EntityTypes.CREEPER) {
			return new ItemStack(Items.CREEPER_HEAD);
		}
		if (t == EntityTypes.PIGLIN || t == EntityTypes.PIGLIN_BRUTE) {
			return new ItemStack(Items.PIGLIN_HEAD);
		}
		return ItemStack.EMPTY;
	}

	private static EventResult onDeath(LivingEntity victim, DamageSource source) {
		if (!(victim.level() instanceof ServerLevel level) || !(source.getEntity() instanceof Player killer)
				|| source.getDirectEntity() != killer || !killer.getMainHandItem().is(ModItems.HARPE.get())) {
			return EventResult.pass();
		}
		ItemStack head = headOf(victim);
		float chance = !HarpeItem.isBeheading(victim) ? HARPE_KILL_HEAD_CHANCE
				: head.is(Items.WITHER_SKELETON_SKULL) ? HARPE_WITHER_SKULL_CHANCE : HARPE_HEAD_CHANCE;
		HarpeItem.forget(victim);
		if (!head.isEmpty() && level.getRandom().nextFloat() < chance) {
			victim.spawnAtLocation(level, head);
		}
		return EventResult.pass();
	}

	/** Aviso de la harpe: con la recarga lista, los mobs que ya se pueden decapitar brillan y suena un aviso una vez. */
	private static void harpeWarning(ServerPlayer player) {
		ItemStack held = player.getMainHandItem();
		ExecuteWarning.tick(player, "harpe",
				held.is(ModItems.HARPE.get()) && !player.getCooldowns().isOnCooldown(held), HARPE_WARN_RADIUS,
				GreeceLegends::canBehead, "message.mythicrelics.harpe_ready");
	}

	// ---- Manzana de la Discordia --------------------------------------------------------------------------------------

	static void markFairest(Mob mob, long until) {
		FAIREST.put(mob, until);
		mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, APPLE_TICKS, 0, false, false));
	}

	private static void discord(MinecraftServer server) {
		FAIREST.entrySet().removeIf(e -> !e.getKey().isAlive() || e.getKey().level().getGameTime() > e.getValue());
		for (Mob fairest : FAIREST.keySet()) {
			if (!(fairest.level() instanceof ServerLevel level)) {
				continue;
			}
			for (Mob mob : level.getEntitiesOfClass(Mob.class, fairest.getBoundingBox().inflate(APPLE_RADIUS),
					m -> m != fairest && m instanceof Enemy && m.isAlive())) {
				mob.setTarget(fairest);
			}
			level.sendParticles(ParticleTypes.HAPPY_VILLAGER, fairest.getX(), fairest.getY() + fairest.getBbHeight() + 0.4, fairest.getZ(),
					2, 0.3, 0.1, 0.3, 0.0);
		}
	}

	// ---- Tick ---------------------------------------------------------------------------------------------------------

	private static void tick(MinecraftServer server) {
		long tick = server.overworld().getGameTime();
		if (tick % 10 == 0) {
			discord(server);
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			// La cólera se pierde a los 10 s sin usarla.
			Long until = WRATH_UNTIL.get(player.getUUID());
			if (until != null && player.level().getGameTime() > until) {
				WRATH_UNTIL.remove(player.getUUID());
			}
			// Bloqueando con el escudo de Aquiles no te empujan.
			boolean blocking = player.isBlocking() && player.getUseItem().is(ModItems.ACHILLES_SHIELD.get());
			var kb = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
			if (kb != null) {
				if (blocking && !kb.hasModifier(WRATH_KNOCKBACK_ID)) {
					kb.addTransientModifier(new AttributeModifier(WRATH_KNOCKBACK_ID, 1.0, AttributeModifier.Operation.ADD_VALUE));
				} else if (!blocking && kb.hasModifier(WRATH_KNOCKBACK_ID)) {
					kb.removeModifier(WRATH_KNOCKBACK_ID);
				}
			}
			if (tick % 10 == 0) {
				harpeWarning(player);
			}
		}
	}

	/** Números de los tooltips de la tercera tanda griega. */
	public static void registerTooltipArgs() {
		TooltipArgs.register("zeus_thunderbolt", ZEUS_RANGE, (int) ZEUS_DAMAGE, ZEUS_CHAIN_TARGETS, (int) ZEUS_CHAIN_DAMAGE,
				ZEUS_STORM_CHAIN_TARGETS, ZEUS_COOLDOWN / 20, ZEUS_STRONG_TICKS / 20, ZEUS_STRONG_BOLTS, ZEUS_STRONG_COOLDOWN / 20);
		TooltipArgs.register("nemean_lion_skin", Math.round(NEMEA_REDUCTION * 100), (int) NEMEA_FIST_DAMAGE);
		TooltipArgs.register("achilles_shield", WRATH_TICKS / 20, Math.round(WRATH_BONUS * 100));
		TooltipArgs.register("harpe", Math.round(HARPE_HEAD_CHANCE * 100), Math.round(HARPE_WITHER_SKULL_CHANCE * 100),
				Math.round(HARPE_EXECUTE_HEALTH * 100), HARPE_COOLDOWN / 20);
		TooltipArgs.register("golden_bridle", (int) PEGASUS_DIVE_DAMAGE, PEGASUS_OLYMPUS_Y, PEGASUS_COOLDOWN / 20 / 60);
		TooltipArgs.register("apple_of_discord", APPLE_RANGE, APPLE_TICKS / 20, APPLE_RADIUS, APPLE_COOLDOWN / 20);
		TooltipArgs.register("talos_nail", TALOS_TICKS / 20, (int) TALOS_ROCK_DAMAGE, TALOS_ROCK_INTERVAL / 20, TALOS_ROCK_RANGE,
				(int) TALOS_HUG_DAMAGE, TALOS_HUG_FIRE_SECONDS, TALOS_COOLDOWN / 20 / 60);
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		ServerState.perPlayer(WRATH_UNTIL);
		ServerState.clearOnStop(FAIREST);
	}
}
