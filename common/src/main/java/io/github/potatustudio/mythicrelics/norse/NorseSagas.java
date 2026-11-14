package io.github.potatustudio.mythicrelics.norse;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.entity.FreyrSword;
import io.github.potatustudio.mythicrelics.greece.GreeceLegends;
import io.github.potatustudio.mythicrelics.network.FalconPayload;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Tercera tanda nórdica: las espadas de las sagas, la espada de Freyr, el manto de halcón de Freyja y el dardo de
 * muérdago.
 * <ul>
 *   <li><b>Gram</b> ({@link GramItem}): estocada cargada «desde el foso», el doble contra mobs grandes, con aviso.</li>
 *   <li><b>Tyrfing</b>: todos los golpes son críticos y brilla como el fuego; maldición: debe matar o te hiere.</li>
 *   <li><b>Dáinsleif</b>: el golpeado no se cura y sangra.</li>
 *   <li><b>Espada de Freyr</b> (cinturón): en combate sale de la vaina y pelea sola ({@link FreyrSword}).</li>
 *   <li><b>Manto de plumas de halcón</b> (espalda): doble toque de salto en el aire y vuelas como halcón.</li>
 *   <li><b>Dardo de muérdago</b>: se lanza; ignora armadura, Resistencia y protección, doble contra jefes.</li>
 * </ul>
 */
public final class NorseSagas {
	// Gram
	public static final int GRAM_CHARGE_TICKS = 20;
	public static final float GRAM_THRUST_DAMAGE = 20.0f;
	public static final float GRAM_BIG_HEALTH = 100.0f;
	public static final double GRAM_REACH = 4.5;
	public static final int GRAM_COOLDOWN = 240;
	// Tyrfing
	public static final float TYRFING_CRIT = 0.5f;
	public static final int TYRFING_CURSE_TICKS = 1200;
	public static final float TYRFING_CURSE_DAMAGE = 8.0f;
	// Dáinsleif
	public static final int DAINSLEIF_NO_HEAL_TICKS = 200;
	public static final int DAINSLEIF_BLEED_TICKS = 80;
	public static final float DAINSLEIF_BLEED_DAMAGE = 1.0f;
	// Espada de Freyr
	public static final float FREYR_DAMAGE = 5.0f;
	public static final float FREYR_BIG_BONUS = 0.5f;
	public static final int FREYR_WISE_LEVEL = 30;
	public static final float FREYR_WISE_BONUS = 0.5f;
	public static final int FREYR_RADIUS = 12;
	public static final int FREYR_IDLE_TICKS = 100;
	// Manto de halcón
	public static final int FALCON_TICKS = 400;
	public static final int FALCON_COOLDOWN = 1800;
	public static final float FALCON_FLY_SPEED = 0.07f;
	public static final int FALCON_SLOW_FALL_TICKS = 80;
	// Dardo de muérdago
	public static final float DART_DAMAGE = 12.0f;
	public static final float DART_BOSS_MULTIPLIER = 2.0f;
	public static final int DART_COOLDOWN = 400;

	private static final DustParticleOptions FIRE_GOLD = new DustParticleOptions(0xFFB040, 0.8f);

	private record Wound(long noHealUntil, long bleedUntil) {
	}

	private static final Map<UUID, Integer> TYRFING_HELD = new HashMap<>();
	private static final Map<LivingEntity, Wound> WOUNDS = new WeakHashMap<>();
	private static final Map<UUID, Long> COMBAT_UNTIL = new HashMap<>();
	private static final Map<UUID, FreyrSword> SWORDS = new HashMap<>();
	private static final Map<UUID, Long> FALCON_UNTIL = new HashMap<>();
	/**
	 * La forma de halcón encoge al jugador con el atributo vanilla {@code scale} (caja de golpe, cámara y pasos), y ese
	 * modificador es también la marca que ve el cliente para dibujar el halcón en lugar del jugador.
	 */
	public static final net.minecraft.resources.Identifier FALCON_SCALE_ID = io.github.potatustudio.mythicrelics.MythicRelics.id("falcon_form");
	public static final double FALCON_SCALE = -0.65;
	private static final Map<UUID, UUID> GRAM_WARNED = new HashMap<>();

	private NorseSagas() {
	}

	public static void init() {
		TickEvent.SERVER_POST.register(NorseSagas::tick);
		EntityEvent.LIVING_HURT.register(NorseSagas::onHurt);
		EntityEvent.LIVING_DEATH.register(NorseSagas::onDeath);
		NetworkManager.registerReceiver(NetworkManager.c2s(), FalconPayload.TYPE, FalconPayload.CODEC,
				(payload, context) -> context.queue(() -> {
					if (context.getPlayer() instanceof ServerPlayer player) {
						if (isFalcon(player)) {
							endFalcon(player, false);   // la misma tecla te devuelve a la forma humana
						} else {
							startFalcon(player);
						}
					}
				}));
		// Siendo halcón solo se vuela: no se ataca, no se usan ni se recogen objetos, no se rompe, no se pone ni se
		// interactúa con nada (en los dos lados, para que el cliente tampoco lo intente).
		InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> blockFalcon(player));
		InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> blockFalcon(player));
		InteractionEvent.LEFT_CLICK_BLOCK.register((player, hand, pos, face) -> blockFalcon(player));
		InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> blockFalcon(player));
		PlayerEvent.ATTACK_ENTITY.register((player, level, target, hand, hit) -> blockFalcon(player));
		PlayerEvent.PICKUP_ITEM_PRE.register((player, item, stack) -> blockFalcon(player));
		BlockEvent.BREAK.register((level, pos, state, player) -> blockFalcon(player));
		BlockEvent.PLACE.register((level, pos, state, placer) -> placer instanceof Player player ? blockFalcon(player) : EventResult.pass());
	}

	private static EventResult blockFalcon(@Nullable Player player) {
		return player != null && isFalconForm(player) ? EventResult.interruptFalse() : EventResult.pass();
	}

	/**
	 * Transformado en halcón, en los dos lados. El servidor lo sabe por su registro; el cliente, por el modificador de
	 * tamaño (los atributos se sincronizan solos), con el que también dibuja el halcón en lugar del jugador.
	 */
	public static boolean isFalconForm(Player player) {
		if (!player.level().isClientSide()) {
			return isFalcon(player);
		}
		var scale = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE);
		return scale != null && scale.hasModifier(FALCON_SCALE_ID);
	}

	// ---- Daño ---------------------------------------------------------------------------------------------------------

	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		if (source.getEntity() instanceof Player attacker && source.getDirectEntity() == attacker) {
			// En forma de halcón no se puede atacar.
			if (isFalcon(attacker)) {
				return 0.0f;
			}
			// Tyrfing nunca falla: todos sus golpes son críticos.
			if (attacker.getMainHandItem().is(ModItems.TYRFING.get()) && self.level() instanceof ServerLevel level) {
				level.sendParticles(ParticleTypes.CRIT, self.getX(), self.getY(0.6), self.getZ(), 10, 0.3, 0.3, 0.3, 0.2);
				level.sendParticles(ParticleTypes.FLAME, self.getX(), self.getY(0.6), self.getZ(), 4, 0.2, 0.2, 0.2, 0.02);
				amount *= 1.0f + TYRFING_CRIT;
			}
		}
		return amount;
	}

	/** Dáinsleif: el herido no se cura (lo consulta el mixin de {@code heal}). */
	public static boolean healBlocked(LivingEntity e) {
		Wound w = WOUNDS.get(e);
		return w != null && e.level().getGameTime() < w.noHealUntil();
	}

	private static EventResult onHurt(LivingEntity target, DamageSource source, float amount) {
		if (!(target.level() instanceof ServerLevel level)) {
			return EventResult.pass();
		}
		long now = level.getGameTime();
		if (target instanceof Player player) {
			COMBAT_UNTIL.put(player.getUUID(), now + FREYR_IDLE_TICKS);
			if (isFalcon(player) && player instanceof ServerPlayer sp) {
				endFalcon(sp, true);
			}
		}
		if (source.getEntity() instanceof Player attacker) {
			COMBAT_UNTIL.put(attacker.getUUID(), now + FREYR_IDLE_TICKS);
			if (source.getDirectEntity() == attacker && attacker.getMainHandItem().is(ModItems.DAINSLEIF.get()) && target != attacker) {
				WOUNDS.put(target, new Wound(now + DAINSLEIF_NO_HEAL_TICKS, now + DAINSLEIF_BLEED_TICKS));
				level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.6), target.getZ(), 6, 0.3, 0.3, 0.3, 0.1);
			}
		}
		return EventResult.pass();
	}

	private static EventResult onDeath(LivingEntity victim, DamageSource source) {
		if (source.getEntity() instanceof Player killer && killer.getMainHandItem().is(ModItems.TYRFING.get())) {
			TYRFING_HELD.put(killer.getUUID(), 0);
		}
		WOUNDS.remove(victim);
		return EventResult.pass();
	}

	// ---- Espada de Freyr ------------------------------------------------------------------------------------------------

	/** En combate: golpeó o lo golpearon hace poco, o un hostil lo persigue a menos de 12 bloques. */
	public static boolean inCombat(Player player) {
		Long until = COMBAT_UNTIL.get(player.getUUID());
		if (until != null && player.level().getGameTime() < until) {
			return true;
		}
		return !player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(FREYR_RADIUS),
				m -> m instanceof Enemy && m.isAlive() && m.getTarget() == player).isEmpty();
	}

	/** El enemigo más cercano a quien pelear: el que lo golpeó, el que golpeó él, o el hostil más cercano que lo persigue. */
	public static @Nullable LivingEntity freyrTarget(Player player) {
		LivingEntity best = null;
		double bestDist = FREYR_RADIUS * FREYR_RADIUS;
		for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(FREYR_RADIUS),
				e -> e != player && e.isAlive() && !ChinaRelics.isFriend(player, e)
						&& (e instanceof Enemy || e == player.getLastHurtMob() || e == player.getLastHurtByMob()))) {
			double d = e.distanceToSqr(player);
			if (d < bestDist) {
				bestDist = d;
				best = e;
			}
		}
		return best;
	}

	/** Daño de un tajo de la espada: +50 % contra mobs grandes y +50 % si su portador es sabio (30 niveles). */
	public static float freyrDamage(Player owner, LivingEntity target) {
		float damage = FREYR_DAMAGE;
		if (target.getMaxHealth() >= GRAM_BIG_HEALTH) {
			damage *= 1.0f + FREYR_BIG_BONUS;
		}
		if (owner.experienceLevel >= FREYR_WISE_LEVEL) {
			damage *= 1.0f + FREYR_WISE_BONUS;
		}
		return damage;
	}

	private static void freyr(ServerPlayer player) {
		FreyrSword sword = SWORDS.get(player.getUUID());
		boolean wearing = EgyptRelics.isWearing(player, ModItems.FREYR_SWORD);
		if (sword != null && (sword.isRemoved() || !wearing)) {
			if (!sword.isRemoved()) {
				sword.discard();
			}
			SWORDS.remove(player.getUUID());
			sword = null;
		}
		if (!wearing || sword != null || player.isSpectator() || !inCombat(player) || freyrTarget(player) == null) {
			return;
		}
		ServerLevel level = player.level();
		FreyrSword drawn = ModEntities.FREYR_SWORD.get().create(level, EntitySpawnReason.TRIGGERED);
		if (drawn == null) {
			return;
		}
		drawn.draw(player);
		level.addFreshEntity(drawn);
		SWORDS.put(player.getUUID(), drawn);
	}

	// ---- Manto de halcón ------------------------------------------------------------------------------------------------

	public static boolean isFalcon(Player player) {
		return FALCON_UNTIL.containsKey(player.getUUID());
	}

	private static void startFalcon(ServerPlayer player) {
		if (isFalcon(player) || !EgyptRelics.isWearing(player, ModItems.FALCON_CLOAK) || player.isPassenger()
				|| player.isSpectator() || !RelicCooldowns.tryStart(player, "falcon_cloak", FALCON_COOLDOWN,
				BuiltInRegistries.ITEM.getKey(ModItems.FALCON_CLOAK.get()))) {
			return;
		}
		ServerLevel level = player.level();
		FALCON_UNTIL.put(player.getUUID(), level.getGameTime() + FALCON_TICKS);
		player.getAbilities().mayfly = true;
		player.getAbilities().flying = true;
		player.getAbilities().setFlyingSpeed(FALCON_FLY_SPEED);
		player.onUpdateAbilities();
		setFalconScale(player, true);
		level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 1, player.getZ(), 30, 0.5, 0.6, 0.5, 0.05);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PARROT_FLY, SoundSource.PLAYERS, 1.5f, 0.7f);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.6f, 1.6f);
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.falcon_start", FALCON_TICKS / 20).withStyle(ChatFormatting.GOLD));
	}

	private static void endFalcon(ServerPlayer player, boolean struck) {
		if (FALCON_UNTIL.remove(player.getUUID()) == null) {
			return;
		}
		if (!player.isCreative() && !player.isSpectator()) {
			player.getAbilities().mayfly = false;
			player.getAbilities().flying = false;
		}
		player.getAbilities().setFlyingSpeed(0.05f);
		player.onUpdateAbilities();
		setFalconScale(player, false);
		player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, FALCON_SLOW_FALL_TICKS, 0, false, true));
		ServerLevel level = player.level();
		level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 1, player.getZ(), 30, 0.5, 0.6, 0.5, 0.05);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PARROT_HURT, SoundSource.PLAYERS, 1.0f, 0.6f);
		player.sendOverlayMessage(Component.translatable(struck ? "message.mythicrelics.falcon_struck" : "message.mythicrelics.falcon_end")
				.withStyle(ChatFormatting.GRAY));
	}

	private static void falcon(ServerPlayer player) {
		Long until = FALCON_UNTIL.get(player.getUUID());
		if (until == null) {
			return;
		}
		if (player.level().getGameTime() >= until || !EgyptRelics.isWearing(player, ModItems.FALCON_CLOAK) || player.isDeadOrDying()) {
			endFalcon(player, false);
			return;
		}
		// Es un halcón (tamaño y modelo de halcón): no usa objetos (los eventos bloquean el resto) y siempre puede volar.
		setFalconScale(player, true);
		if (player.isUsingItem()) {
			player.stopUsingItem();
		}
		if (!player.getAbilities().mayfly) {
			player.getAbilities().mayfly = true;
			player.onUpdateAbilities();
		}
		player.resetFallDistance();
	}

	/** Pone o quita el tamaño de halcón (el modificador es transitorio: no se guarda con el jugador). */
	private static void setFalconScale(ServerPlayer player, boolean on) {
		var scale = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE);
		if (scale == null) {
			return;
		}
		if (on && !scale.hasModifier(FALCON_SCALE_ID)) {
			scale.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(FALCON_SCALE_ID, FALCON_SCALE,
					net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		} else if (!on && scale.hasModifier(FALCON_SCALE_ID)) {
			scale.removeModifier(FALCON_SCALE_ID);
		}
	}

	// ---- Tick ---------------------------------------------------------------------------------------------------------------

	private static void tick(MinecraftServer server) {
		long now = server.overworld().getGameTime();
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			tyrfing(player);
			if (now % 5 == 0) {
				gramWarning(player);
			}
			freyr(player);
			falcon(player);
		}
		if (now % 20 == 0) {
			bleed();
		}
	}

	/** Tyrfing: brilla como el fuego y, si pasa un minuto en tu mano sin matar, te hiere. */
	private static void tyrfing(ServerPlayer player) {
		if (!player.getMainHandItem().is(ModItems.TYRFING.get())) {
			return;
		}
		ServerLevel level = player.level();
		if (player.tickCount % 4 == 0) {
			Vec3 hand = player.getEyePosition().add(player.getLookAngle().scale(0.6)).add(0, -0.5, 0);
			level.sendParticles(FIRE_GOLD, hand.x, hand.y, hand.z, 1, 0.1, 0.15, 0.1, 0.0);
			if (player.tickCount % 12 == 0) {
				level.sendParticles(ParticleTypes.SMALL_FLAME, hand.x, hand.y, hand.z, 1, 0.08, 0.1, 0.08, 0.0);
			}
		}
		int held = TYRFING_HELD.merge(player.getUUID(), 1, Integer::sum);
		if (held == TYRFING_CURSE_TICKS - 200) {
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.tyrfing_hungers").withStyle(ChatFormatting.RED));
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0f, 1.0f);
		}
		if (held >= TYRFING_CURSE_TICKS) {
			TYRFING_HELD.put(player.getUUID(), 0);
			player.hurtServer(level, level.damageSources().magic(), TYRFING_CURSE_DAMAGE);
			level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY(0.6), player.getZ(), 30, 0.3, 0.5, 0.3, 0.05);
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 0.5f);
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.tyrfing_curse").withStyle(ChatFormatting.DARK_RED));
		}
	}

	/** Gram: con la estocada lista, el mob grande al que apuntas brilla y suena un aviso una vez. */
	private static void gramWarning(ServerPlayer player) {
		ItemStack held = player.getMainHandItem();
		if (!held.is(ModItems.GRAM.get()) || player.getCooldowns().isOnCooldown(held)) {
			GRAM_WARNED.remove(player.getUUID());
			return;
		}
		LivingEntity aimed = GramItem.aimed(player, 8.0);
		if (aimed == null || aimed.getMaxHealth() < GRAM_BIG_HEALTH) {
			return;
		}
		aimed.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false));
		if (!aimed.getUUID().equals(GRAM_WARNED.put(player.getUUID(), aimed.getUUID()))) {
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6f, 0.5f);
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.gram_ready").withStyle(ChatFormatting.GOLD));
		}
	}

	/** Dáinsleif: los heridos sangran medio corazón por segundo. */
	private static void bleed() {
		WOUNDS.entrySet().removeIf(e -> {
			LivingEntity target = e.getKey();
			long now = target.level().getGameTime();
			if (!target.isAlive() || now >= e.getValue().noHealUntil()) {
				return true;
			}
			if (now < e.getValue().bleedUntil() && target.level() instanceof ServerLevel level) {
				target.hurtServer(level, level.damageSources().magic(), DAINSLEIF_BLEED_DAMAGE);
				level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.6), target.getZ(), 3, 0.2, 0.3, 0.2, 0.05);
			}
			return false;
		});
	}

	/** Números de los tooltips de la tercera tanda nórdica. */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "gram" -> new Object[] {GRAM_CHARGE_TICKS / 20, (int) GRAM_THRUST_DAMAGE, (int) GRAM_BIG_HEALTH, GRAM_COOLDOWN / 20};
			case "tyrfing" -> new Object[] {Math.round(TYRFING_CRIT * 100), TYRFING_CURSE_TICKS / 20, (int) (TYRFING_CURSE_DAMAGE / 2)};
			case "dainsleif" -> new Object[] {DAINSLEIF_NO_HEAL_TICKS / 20, DAINSLEIF_BLEED_TICKS / 20};
			case "freyr_sword" -> new Object[] {FREYR_RADIUS, (int) FREYR_DAMAGE, Math.round(FREYR_BIG_BONUS * 100), FREYR_WISE_LEVEL,
					Math.round(FREYR_WISE_BONUS * 100), FREYR_IDLE_TICKS / 20};
			case "falcon_cloak" -> new Object[] {FALCON_TICKS / 20, FALCON_COOLDOWN / 20};
			case "mistletoe_dart" -> new Object[] {(int) DART_DAMAGE, DART_COOLDOWN / 20};
			default -> new Object[0];
		};
	}

	/** Para el resto del código: el jefe según la harpe. */
	static boolean isBoss(LivingEntity e) {
		return GreeceLegends.isBoss(e);
	}
}
