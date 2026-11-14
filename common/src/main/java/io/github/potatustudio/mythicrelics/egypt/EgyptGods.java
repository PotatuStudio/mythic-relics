package io.github.potatustudio.mythicrelics.egypt;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.Allies;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.RelicSets;
import io.github.potatustudio.mythicrelics.relic.ServerState;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Segunda tanda de Egipto (diseño aprobado el 2026-11-09):
 * <ul>
 *   <li><b>Disco solar de Ra</b> (sombrero): bajo el sol, más daño a los no-muertos y los prende; el ureo escupe fuego a
 *       quien te golpea cuerpo a cuerpo. De noche solo queda el ureo.</li>
 *   <li><b>Amuleto de Bastet</b> (collar): creepers y phantoms te evitan; caes de pie.</li>
 *   <li><b>Paleta de escriba de Thot</b> (mano secundaria): guarda experiencia por escrito ({@link ThothPaletteItem}) y
 *       da más experiencia.</li>
 *   <li><b>Cayado y mayal de Osiris</b>: los animales siguen el cayado, que hace crecer a las crías
 *       ({@link OsirisCrookItem}); el mayal da un golpe giratorio y trilla. Con los dos en las manos, renaces.</li>
 *   <li><b>Garras de Sekhmet</b> (guantes): frenesí que se acumula con cada muerte, fuego al máximo y calma al final.</li>
 * </ul>
 * Lo que los eventos de Architectury no cubren (modificar el daño, la protección del tótem, la carga del ataque, la
 * experiencia y el botín de la arqueología) va en los mixins, que llaman a los métodos públicos de esta clase.
 */
public final class EgyptGods {
	public static final float RA_UNDEAD_BONUS = 0.30f;
	public static final int RA_BURN_SECONDS = 5;
	public static final int URAEUS_BURN_SECONDS = 3;
	public static final int URAEUS_COOLDOWN = 200;

	public static final double BASTET_RADIUS = 16.0;

	/** Lo que cabe en la paleta: los puntos de experiencia de los niveles 0 a 30 (1395 puntos). */
	public static final int THOTH_MAX_LEVELS = 30;
	public static final int THOTH_MAX_POINTS = pointsUpToLevel(THOTH_MAX_LEVELS);
	public static final float THOTH_XP_BONUS = 0.20f;

	public static final int CROOK_GROW_COOLDOWN = 600;
	public static final double FLAIL_SWEEP_RADIUS = 2.5;
	public static final float FLAIL_SWEEP_DAMAGE = 0.6f;
	public static final float OSIRIS_REBIRTH_HEALTH = 8.0f;
	public static final int OSIRIS_REBIRTH_REGEN_TICKS = 100;
	public static final int OSIRIS_REBIRTH_COOLDOWN = 24000;
	public static final Identifier OSIRIS_REBIRTH_GROUP = MythicRelics.id("osiris_rebirth");

	public static final int SEKHMET_WINDOW = 100;
	public static final int SEKHMET_MAX = 3;
	public static final float SEKHMET_PER_LEVEL = 0.10f;
	public static final int SEKHMET_BURN_SECONDS = 3;
	public static final float SEKHMET_CALM_HEAL = 4.0f;
	private static final Identifier SEKHMET_SPEED = MythicRelics.id("sekhmet_frenzy");

	/** Frenesí de Sekhmet de cada jugador: nivel y tick de la última muerte. */
	private record Frenzy(int level, long lastKill) {
	}

	private static final Map<UUID, Frenzy> FRENZY = new HashMap<>();

	private EgyptGods() {
	}

	public static void init() {
		EntityEvent.ADD.register(EgyptGods::onEntityAdded);
		EntityEvent.LIVING_HURT.register((target, source, amount) ->
				// Bastet: caes de pie, las caídas no hacen daño (ni el golpe ni el sonido).
				source.is(DamageTypeTags.IS_FALL) && WornRelics.isWearing(target, ModItems.BASTET_AMULET)
						? EventResult.interruptFalse() : EventResult.pass());
		EntityEvent.LIVING_DAMAGE_POST.register((entity, source, baseDamage, damageTaken, blocked) -> {
			if (blocked || damageTaken <= 0 || !(entity.level() instanceof ServerLevel level)) {
				return;
			}
			if (entity instanceof ServerPlayer player && source.getEntity() instanceof LivingEntity attacker
					&& source.getDirectEntity() == attacker && attacker != player && WornRelics.isWearing(player, ModItems.RA_DISK)) {
				uraeus(level, player, attacker);
			}
			if (source.getEntity() instanceof ServerPlayer attacker && source.getDirectEntity() == attacker && attacker != entity
					&& entity.isAlive()) {
				if (entity.typeHolder().is(EntityTypeTags.UNDEAD) && WornRelics.isWearing(attacker, ModItems.RA_DISK) && underTheSun(attacker)) {
					entity.igniteForSeconds(RA_BURN_SECONDS);
				}
				Frenzy frenzy = FRENZY.get(attacker.getUUID());
				if (frenzy != null && frenzy.level() >= SEKHMET_MAX) {
					entity.igniteForSeconds(SEKHMET_BURN_SECONDS);
				}
			}
		});
		EntityEvent.LIVING_DEATH.register((victim, source) -> {
			if (source.getEntity() instanceof ServerPlayer player && player != victim && WornRelics.isWearing(player, ModItems.SEKHMET_CLAWS)) {
				frenzyKill(player);
			}
			return EventResult.pass();
		});
		BlockEvent.BREAK.register(EgyptGods::thresh);
		TickEvent.SERVER_POST.register(EgyptGods::tick);
	}

	// ---- Ra ----------------------------------------------------------------------------------------------------------

	/** De día, sin lluvia y con el cielo a la vista. */
	public static boolean underTheSun(LivingEntity entity) {
		Level level = entity.level();
		BlockPos head = BlockPos.containing(entity.getEyePosition());
		return level.isBrightOutside() && level.canSeeSky(head) && !level.isRainingAt(head);
	}

	private static void uraeus(ServerLevel level, ServerPlayer player, LivingEntity attacker) {
		if (!RelicCooldowns.tryStart(player, "uraeus", URAEUS_COOLDOWN, BuiltInRegistries.ITEM.getKey(ModItems.RA_DISK.get()))) {
			return;
		}
		attacker.igniteForSeconds(URAEUS_BURN_SECONDS);
		level.sendParticles(ParticleTypes.FLAME, attacker.getX(), attacker.getY() + attacker.getBbHeight() / 2, attacker.getZ(),
				14, 0.3, 0.4, 0.3, 0.02);
		RelicFx.sound(level, player, SoundEvents.FIRECHARGE_USE, 0.7f, 1.3f);
	}

	// ---- Daño (lo llama el mixin de LivingEntity) ------------------------------------------------------------------

	/** Daño recibido por {@code self} después de aplicar Anj, Ra, Sekhmet y Bastet (que anula las caídas). */
	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		if (source.is(DamageTypeTags.IS_FALL) && WornRelics.isWearing(self, ModItems.BASTET_AMULET)) {
			return 0.0f;
		}
		if (!(source.getEntity() instanceof LivingEntity attacker)) {
			return amount;
		}
		boolean undead = self.typeHolder().is(EntityTypeTags.UNDEAD);
		if (undead && source.getDirectEntity() == attacker && WornRelics.isWearing(attacker, ModItems.RA_DISK) && underTheSun(attacker)) {
			amount *= 1.0f + RA_UNDEAD_BONUS;
		}
		Frenzy frenzy = FRENZY.get(attacker.getUUID());
		if (frenzy != null) {
			amount *= 1.0f + SEKHMET_PER_LEVEL * frenzy.level();
		}
		return amount;
	}

	// ---- Bastet ------------------------------------------------------------------------------------------------------

	private static EventResult onEntityAdded(Entity entity, Level level) {
		if (level.isClientSide()) {
			return EventResult.pass();
		}
		if (entity instanceof Creeper creeper) {
			// Como con los gatos en vanilla: el creeper se aleja de quien lleva el amuleto.
			creeper.getGoalSelector().addGoal(3, new AvoidEntityGoal<>(creeper, Player.class,
					p -> WornRelics.isWearing(p, ModItems.BASTET_AMULET), 6.0f, 1.0, 1.2, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
		}
		if (entity instanceof Animal animal) {
			// Osiris pastor: los animales de granja siguen el cayado como si fuera su comida.
			animal.getGoalSelector().addGoal(3, new TemptGoal(animal, 1.1, stack -> stack.is(ModItems.OSIRIS_CROOK.get()), false));
		}
		return EventResult.pass();
	}

	// ---- Thot (lo llama el mixin de Player) --------------------------------------------------------------------------

	/** Mientras la paleta devuelve lo escrito, no se aplica el +20 % (si no, leer daría experiencia de más). */
	private static boolean readingPalette;

	/** Puntos de experiencia de un nivel (de {@code level} a {@code level + 1}), la misma fórmula de vanilla. */
	public static int pointsForLevel(int level) {
		return level >= 30 ? 112 + (level - 30) * 9 : level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
	}

	public static int pointsUpToLevel(int level) {
		int total = 0;
		for (int l = 0; l < level; l++) {
			total += pointsForLevel(l);
		}
		return total;
	}

	/** Devuelve puntos al jugador sin el bono de la paleta. */
	public static void giveWithoutThothBonus(Player player, int points) {
		readingPalette = true;
		try {
			player.giveExperiencePoints(points);
		} finally {
			readingPalette = false;
		}
	}

	/** Experiencia ganada con la paleta en la mano secundaria: +20 %, con la fracción repartida al azar. */
	public static int thothExperience(Player player, int amount) {
		if (amount <= 0 || readingPalette || !player.getOffhandItem().is(ModItems.THOTH_PALETTE.get())) {
			return amount;
		}
		float bonus = amount * THOTH_XP_BONUS;
		int whole = (int) bonus;
		return amount + whole + (player.getRandom().nextFloat() < bonus - whole ? 1 : 0);
	}

	// ---- Osiris ------------------------------------------------------------------------------------------------------

	/** Golpe giratorio del mayal (lo llama el mixin al terminar un ataque del jugador con la carga que tenía). */
	public static void flailSweep(Player player, Entity target, float strength) {
		if (strength <= 0.9f || !(player.level() instanceof ServerLevel level) || !player.getMainHandItem().is(ModItems.OSIRIS_FLAIL.get())) {
			return;
		}
		float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * FLAIL_SWEEP_DAMAGE;
		double r2 = FLAIL_SWEEP_RADIUS * FLAIL_SWEEP_RADIUS;
		for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(FLAIL_SWEEP_RADIUS),
				e -> e != player && e != target && e.isAlive() && e.distanceToSqr(player) <= r2 && !Allies.isFriend(player, e))) {
			other.hurtServer(level, player.damageSources().playerAttack(player), damage);
		}
		level.sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX(), player.getY() + 1.0, player.getZ(), 3, 1.0, 0.2, 1.0, 0);
		RelicFx.sound(level, player, SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 0.8f);
	}

	/** Trilla: romper un cultivo maduro con el mayal cosecha y vuelve a plantar los maduros de un 3×3. */
	private static EventResult thresh(Level level, BlockPos pos, BlockState state, ServerPlayer player) {
		ItemStack flail = player.getMainHandItem();
		if (!flail.is(ModItems.OSIRIS_FLAIL.get()) || !(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)) {
			return EventResult.pass();
		}
		for (BlockPos p : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
			BlockState s = level.getBlockState(p);
			if (s.getBlock() instanceof CropBlock c && c.isMaxAge(s)) {
				Block.dropResources(s, level, p.immutable(), null, player, flail);
				level.setBlock(p.immutable(), c.getStateForAge(0), Block.UPDATE_ALL);
			}
		}
		level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
		return EventResult.interruptFalse();
	}

	/** Renacer de Osiris (lo llama el mixin en la misma comprobación que el tótem). Devuelve true si salvó al jugador. */
	public static boolean osirisRebirth(LivingEntity self, DamageSource source) {
		if (!(self instanceof ServerPlayer player) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || !RelicSets.OSIRIS.isComplete(player)
				|| !RelicCooldowns.ready(player, "osiris_rebirth")) {
			return false;
		}
		player.setHealth(OSIRIS_REBIRTH_HEALTH);
		player.removeAllEffects();
		player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, OSIRIS_REBIRTH_REGEN_TICKS, 1));
		RelicCooldowns.start(player, "osiris_rebirth", OSIRIS_REBIRTH_COOLDOWN, OSIRIS_REBIRTH_GROUP);
		ServerLevel level = player.level();
		level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 40, 0.5, 0.8, 0.5, 0.3);
		RelicFx.sound(level, player, SoundEvents.TOTEM_USE, 0.8f, 0.8f);
		RelicFx.notify(player, "osiris_rebirth", ChatFormatting.GOLD);
		return true;
	}

	// ---- Sekhmet -----------------------------------------------------------------------------------------------------

	private static void frenzyKill(ServerPlayer player) {
		long now = player.level().getGameTime();
		Frenzy old = FRENZY.get(player.getUUID());
		int level = old != null && now - old.lastKill() < SEKHMET_WINDOW ? Math.min(SEKHMET_MAX, old.level() + 1) : 1;
		FRENZY.put(player.getUUID(), new Frenzy(level, now));
		setFrenzySpeed(player, level);
		if (level == SEKHMET_MAX && (old == null || old.level() < SEKHMET_MAX)) {
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 0.5f, 1.4f);
			RelicFx.notify(player, "sekhmet_frenzy", ChatFormatting.RED);
		}
	}

	private static void setFrenzySpeed(ServerPlayer player, int level) {
		AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speed == null) {
			return;
		}
		speed.removeModifier(SEKHMET_SPEED);
		if (level > 0) {
			speed.addTransientModifier(new AttributeModifier(SEKHMET_SPEED, SEKHMET_PER_LEVEL * level,
					AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		}
	}

	// ---- Cada tick ---------------------------------------------------------------------------------------------------

	private static void tick(MinecraftServer server) {
		Iterator<Map.Entry<UUID, Frenzy>> it = FRENZY.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Frenzy> e = it.next();
			ServerPlayer player = server.getPlayerList().getPlayer(e.getKey());
			if (player == null) {
				it.remove();
				continue;
			}
			boolean wearing = WornRelics.isWearing(player, ModItems.SEKHMET_CLAWS);
			if (!wearing || player.level().getGameTime() - e.getValue().lastKill() >= SEKHMET_WINDOW) {
				// Calma: un frenesí que llegó al máximo termina curando.
				if (wearing && e.getValue().level() >= SEKHMET_MAX && player.isAlive()) {
					player.heal(SEKHMET_CALM_HEAL);
					player.level().sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 2.0, player.getZ(), 3, 0.3, 0.2, 0.3, 0);
				}
				setFrenzySpeed(player, 0);
				it.remove();
			}
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.tickCount % 10 != 0 || !WornRelics.isWearing(player, ModItems.BASTET_AMULET)) {
				continue;
			}
			// Bastet: creepers y phantoms dejan de perseguirte (como a un gato).
			for (Mob mob : player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(BASTET_RADIUS),
					m -> (m instanceof Creeper || m instanceof Phantom) && m.getTarget() == player)) {
				mob.setTarget(null);
			}
		}
	}

	/** Números de los tooltips de la tanda 2 (%1$s, %2$s… de sus líneas de traducción, en este orden). */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "ra_disk" -> new Object[] {Math.round(RA_UNDEAD_BONUS * 100), RA_BURN_SECONDS, URAEUS_BURN_SECONDS, URAEUS_COOLDOWN / 20};
			case "thoth_palette" -> new Object[] {THOTH_MAX_LEVELS, Math.round(THOTH_XP_BONUS * 100), THOTH_MAX_POINTS};
			case "osiris_crook" -> new Object[] {CROOK_GROW_COOLDOWN / 20};
			case "osiris_flail" -> new Object[] {FLAIL_SWEEP_RADIUS, Math.round(FLAIL_SWEEP_DAMAGE * 100)};
			case "sekhmet_claws" -> new Object[] {SEKHMET_WINDOW / 20, SEKHMET_MAX, Math.round(SEKHMET_PER_LEVEL * 100), SEKHMET_BURN_SECONDS,
					Math.round(SEKHMET_CALM_HEAL / 2)};
			default -> GreeceRelics.tooltipArgs(id);
		};
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		ServerState.perPlayer(FRENZY);
	}
}
