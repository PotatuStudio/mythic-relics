package io.github.potatustudio.mythicrelics.china;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.cubemob.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Primera tanda china (diseño aprobado el 2026-11-10). Mitos antiguos, Viaje al Oeste y La investidura de los dioses:
 * <ul>
 *   <li><b>Ruyi Jingu Bang</b> ({@link RuyiJinguBangItem}): arma pesada de mucho alcance que se estira en línea.</li>
 *   <li><b>Pelos del Rey Mono</b> ({@link MonkeyHairsItem}): cuatro monos guerreros
 *       ({@link io.github.potatustudio.mythicrelics.entity.MonkeyWarrior}).</li>
 *   <li><b>Calabaza roja de oro púrpura</b> ({@link PurpleGoldGourdItem}): atrapa un mob, lo suelta o lo disuelve.</li>
 *   <li><b>Abanico de hoja de plátano</b> ({@link BananaLeafFanItem}): arma de viento con tres aleteos.</li>
 *   <li><b>Perla del dragón</b> ({@link DragonPearlItem}): llama al long que se monta
 *       ({@link io.github.potatustudio.mythicrelics.entity.LongDragon}).</li>
 *   <li><b>Ruedas de viento y fuego</b> (pies): velocidad, estela de llamas y cruzar la lava corriendo.</li>
 *   <li><b>Arco de Hou Yi</b> ({@link HouYiBowItem}): derriba «soles» (mobs de fuego y voladores).</li>
 *   <li><b>Elixir de Chang'e</b> ({@link ChangeElixirItem}): ascenso lunar según la fase de la luna.</li>
 *   <li><b>Piedra de cinco colores</b> (cinturón): remienda el equipo y los rayos no te dañan.</li>
 * </ul>
 */
public final class ChinaRelics {
	public static final float RUYI_REACH = 1.5f;
	public static final int RUYI_LENGTH = 10;
	public static final float RUYI_STRETCH_DAMAGE = 18.0f;
	public static final int RUYI_COOLDOWN = 300;
	public static final int RUYI_EXTENDED_TICKS = 200;
	public static final float RUYI_EXTENDED_REACH = 4.0f;
	public static final float RUYI_SWEEP_RADIUS = 2.5f;

	public static final int HAIRS_MONKEYS = 4;
	public static final int HAIRS_TICKS = 1200;
	public static final int HAIRS_COOLDOWN = 4800;
	public static final int MONKEY_HEALTH = 20;
	public static final int MONKEY_DAMAGE = 5;

	public static final int GOURD_MAX_HEALTH = 40;
	public static final int GOURD_DISSOLVE_TICKS = 6000;
	public static final int GOURD_COOLDOWN = 60;

	public static final int FAN_BLADE_RANGE = 12;
	public static final float FAN_BLADE_DAMAGE = 6.0f;
	public static final float FAN_DASH_DAMAGE = 12.0f;
	public static final int FAN_DASH_BLOCKS = 10;
	public static final int FAN_CHARGES = 3;
	public static final int FAN_RECHARGE_TICKS = 120;
	public static final int FAN_THIRD_WINDOW = 80;
	public static final int FAN_RAIN_TICKS = 300;
	public static final int FAN_RAIN_RADIUS = 10;

	public static final int DRAGON_LEAVE_TICKS = 60;
	public static final int DRAGON_COOLDOWN = 6000;

	public static final float WHEELS_SPEED = 0.4f;
	public static final int WHEELS_BURN_SECONDS = 3;

	public static final float HOU_YI_DAMAGE = 0.5f;
	public static final int HOU_YI_SUN_FACTOR = 3;
	public static final String HOU_YI_TAG = MythicRelics.MOD_ID + ".hou_yi";

	public static final int ELIXIR_FULL_SECONDS = 30;
	public static final int ELIXIR_QUARTER_SECONDS = 20;
	public static final int ELIXIR_NEW_SECONDS = 10;
	public static final float ELIXIR_AIR_DAMAGE = 0.5f;
	public static final int ELIXIR_COOLDOWN = 3600;

	public static final int STONE_INTERVAL = 200;

	private static final Identifier RUYI_REACH_ID = MythicRelics.id("ruyi_reach");
	private static final Identifier WHEELS_SPEED_ID = MythicRelics.id("wind_fire_wheels_speed");
	private static final Identifier WHEELS_STEP_ID = MythicRelics.id("wind_fire_wheels_step");
	private static final EquipmentSlot[] REPAIRED = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
			EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

	/** Jugador en ascenso lunar -> tick del juego en que termina. */
	private static final Map<UUID, Long> LUNAR = new HashMap<>();
	/** Jugador en pleno aleteo del abanico -> ticks que le quedan (empuja a los mobs que cruza y no recibe daño de caída). */
	private static final Map<UUID, Integer> DASHING = new HashMap<>();
	/** Mobs ya golpeados por el dash en curso de cada jugador. */
	private static final Map<UUID, java.util.Set<UUID>> DASH_HIT = new HashMap<>();
	/** Evita que el barrido del Ruyi se barra a sí mismo. */
	private static final ThreadLocal<Boolean> RUYI_SWEEPING = ThreadLocal.withInitial(() -> false);
	/** Últimos aleteos de cada jugador (para el tercer aleteo seguido). */
	private static final Map<UUID, Deque<Long>> FLAPS = new HashMap<>();

	/** Nube de lluvia del tercer aleteo: apaga el fuego a su alrededor mientras dura. */
	private record RainCloud(ServerLevel level, Vec3 center, int[] ticksLeft) {
	}

	private static final List<RainCloud> CLOUDS = new ArrayList<>();

	private ChinaRelics() {
	}

	public static void init() {
		TickEvent.SERVER_POST.register(ChinaRelics::tick);
		EntityEvent.LIVING_DEATH.register(ChinaRelics::onDeath);
	}

	// ---- Daño -------------------------------------------------------------------------------------------------------

	/** Soles de Hou Yi: los mobs de fuego y los voladores. */
	public static boolean isSun(Entity entity) {
		return entity instanceof Blaze || entity instanceof Ghast || entity instanceof MagmaCube || entity instanceof Phantom
				|| entity instanceof Vex;
	}

	private static boolean fromHouYi(DamageSource source) {
		return source.getDirectEntity() != null && source.getDirectEntity().entityTags().contains(HOU_YI_TAG);
	}

	/**
	 * Daño recibido: las ruedas anulan el fuego (y la lava) mientras corres; la piedra, los rayos; el arco de Hou Yi
	 * triplica contra los soles; el ascenso lunar da más daño a los golpes en el aire.
	 */
	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		if (source.is(DamageTypeTags.IS_FIRE) && self.isSprinting() && EgyptRelics.isWearing(self, ModItems.WIND_FIRE_WHEELS)) {
			return 0;
		}
		if (source.is(DamageTypeTags.IS_LIGHTNING) && EgyptRelics.isWearing(self, ModItems.FIVE_COLOR_STONE)) {
			return 0;
		}
		if (source.getEntity() instanceof Player attacker && source.getDirectEntity() == attacker && !RUYI_SWEEPING.get()
				&& RuyiJinguBangItem.isExtended(attacker.getMainHandItem(), attacker.level().getGameTime())) {
			// Ruyi estirado: golpes al doble que barren a los de alrededor.
			amount *= 2.0f;
			if (attacker.level() instanceof ServerLevel level) {
				RUYI_SWEEPING.set(true);
				try {
					for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, self.getBoundingBox().inflate(RUYI_SWEEP_RADIUS),
							e -> e != self && e != attacker && e.isAlive() && !isFriend(attacker, e))) {
						near.hurtServer(level, level.damageSources().playerAttack(attacker), amount * 0.5f);
					}
				} finally {
					RUYI_SWEEPING.set(false);
				}
				level.sendParticles(ParticleTypes.SWEEP_ATTACK, self.getX(), self.getY(0.5), self.getZ(), 3, 0.8, 0.2, 0.8, 0.0);
			}
		}
		if (fromHouYi(source) && isSun(self)) {
			amount *= HOU_YI_SUN_FACTOR;
		}
		if (source.getEntity() instanceof Player attacker && source.getDirectEntity() == attacker && !attacker.onGround()
				&& isLunar(attacker)) {
			amount *= 1.0f + ELIXIR_AIR_DAMAGE;
		}
		return amount;
	}

	/** Las ruedas, corriendo: la lava se pisa como suelo. */
	public static boolean wheelsOnLava(LivingEntity entity) {
		return entity.isSprinting() && EgyptRelics.isWearing(entity, ModItems.WIND_FIRE_WHEELS);
	}

	/** Lo que mata el arco de Hou Yi a un sol cae como el cuervo dorado de tres patas y suelta el doble de botín. */
	private static EventResult onDeath(LivingEntity victim, DamageSource source) {
		if (fromHouYi(source) && isSun(victim) && victim.level() instanceof ServerLevel level) {
			ResourceKey<LootTable> table = victim.getType().getDefaultLootTable().orElse(null);
			if (table != null) {
				victim.dropFromLootTable(level, source, true, table);
			}
			level.sendParticles(ParticleTypes.FLAME, victim.getX(), victim.getY() + victim.getBbHeight() * 0.5, victim.getZ(), 30,
					0.4, 0.5, 0.4, 0.05);
			level.sendParticles(ParticleTypes.WAX_ON, victim.getX(), victim.getY() + victim.getBbHeight() * 0.5, victim.getZ(), 20,
					0.5, 0.5, 0.5, 0.1);
			level.playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.8f, 0.6f);
		}
		return EventResult.pass();
	}

	// ---- Elixir de Chang'e ------------------------------------------------------------------------------------------

	/** Segundos de ascenso lunar según la fase de la luna: más con luna llena, menos con luna nueva. */
	public static int lunarSeconds(MoonPhase phase) {
		return switch (phase) {
			case FULL_MOON -> ELIXIR_FULL_SECONDS;
			case WANING_GIBBOUS, WAXING_GIBBOUS -> (ELIXIR_FULL_SECONDS + ELIXIR_QUARTER_SECONDS) / 2;
			case FIRST_QUARTER, THIRD_QUARTER -> ELIXIR_QUARTER_SECONDS;
			case WANING_CRESCENT, WAXING_CRESCENT -> (ELIXIR_QUARTER_SECONDS + ELIXIR_NEW_SECONDS) / 2;
			case NEW_MOON -> ELIXIR_NEW_SECONDS;
		};
	}

	static void startLunar(ServerPlayer player, int ticks) {
		LUNAR.put(player.getUUID(), player.level().getGameTime() + ticks);
	}

	public static boolean isLunar(Player player) {
		Long until = LUNAR.get(player.getUUID());
		return until != null && until > player.level().getGameTime();
	}

	// ---- Abanico ----------------------------------------------------------------------------------------------------

	/** Registra un aleteo: el dash empuja a los mobs que cruza; si son tres en poco tiempo, llueve. */
	static void flap(ServerPlayer player, int dashTicks) {
		DASHING.put(player.getUUID(), dashTicks);
		DASH_HIT.put(player.getUUID(), new java.util.HashSet<>());
	}

	private static void rain(RainCloud cloud) {
		ServerLevel level = cloud.level();
		Vec3 c = cloud.center();
		int r = FAN_RAIN_RADIUS;
		level.sendParticles(ParticleTypes.CLOUD, c.x, c.y + 6, c.z, 12, r * 0.4, 0.4, r * 0.4, 0.0);
		level.sendParticles(ParticleTypes.FALLING_WATER, c.x, c.y + 5, c.z, 60, r * 0.5, 0.5, r * 0.5, 0.0);
		if (cloud.ticksLeft()[0] % 20 != 0) {
			return;
		}
		BlockPos center = BlockPos.containing(c);
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -4, -r), center.offset(r, 6, r))) {
			if (level.getBlockState(pos).is(BlockTags.FIRE)) {
				level.removeBlock(pos, false);
				level.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0.0);
			}
		}
		for (Entity e : level.getEntities((Entity) null, new AABB(c, c).inflate(r, 6, r), e -> e.isOnFire())) {
			e.clearFire();
		}
	}

	private static void dashPush(ServerPlayer player) {
		player.resetFallDistance();
		ServerLevel level = player.level();
		Vec3 push = player.getDeltaMovement().multiply(1, 0, 1).normalize().scale(2.2).add(0, 0.7, 0);
		java.util.Set<UUID> hit = DASH_HIT.computeIfAbsent(player.getUUID(), k -> new java.util.HashSet<>());
		for (LivingEntity mob : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(2.0),
				e -> e != player && e.isAlive() && !isFriend(player, e))) {
			if (hit.add(mob.getUUID())) {
				mob.hurtServer(level, level.damageSources().playerAttack(player), FAN_DASH_DAMAGE);
				level.sendParticles(ParticleTypes.SWEEP_ATTACK, mob.getX(), mob.getY(0.5), mob.getZ(), 1, 0, 0, 0, 0);
			}
			mob.push(push.x, push.y, push.z);
			mob.needsSync = true;
		}
		level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.5, player.getZ(), 4, 0.3, 0.2, 0.3, 0.02);
		level.sendParticles(ParticleTypes.GUST, player.getX(), player.getY() + 0.8, player.getZ(), 1, 0.2, 0.2, 0.2, 0.0);
	}

	/** Aliados del jugador: sus monos, sus mascotas y otros jugadores de su equipo. */
	public static boolean isFriend(Player player, Entity e) {
		if (player.getUUID().equals(Minions.ownerOf(e))) {
			return true;
		}
		if (e instanceof OwnableEntity ownable && ownable.getOwnerReference() != null && ownable.getOwnerReference().matches(player)) {
			return true;
		}
		return e instanceof Player && e.isAlliedTo(player);
	}

	// ---- Cada tick ---------------------------------------------------------------------------------------------------

	private static void tick(MinecraftServer server) {
		Iterator<RainCloud> clouds = CLOUDS.iterator();
		while (clouds.hasNext()) {
			RainCloud cloud = clouds.next();
			rain(cloud);
			if (--cloud.ticksLeft()[0] <= 0) {
				clouds.remove();
			}
		}
		long now = server.overworld().getGameTime();
		LUNAR.values().removeIf(until -> until <= now);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ServerLevel level = player.level();
			ItemStack main = player.getMainHandItem();
			setModifier(player, Attributes.ENTITY_INTERACTION_RANGE, RUYI_REACH_ID,
					!main.is(ModItems.RUYI_JINGU_BANG.get()) ? 0
							: RuyiJinguBangItem.isExtended(main, now) ? RUYI_REACH + RUYI_EXTENDED_REACH : RUYI_REACH,
					AttributeModifier.Operation.ADD_VALUE);
			boolean wheels = EgyptRelics.isWearing(player, ModItems.WIND_FIRE_WHEELS);
			setModifier(player, Attributes.MOVEMENT_SPEED, WHEELS_SPEED_ID, wheels ? WHEELS_SPEED : 0,
					AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
			setModifier(player, Attributes.STEP_HEIGHT, WHEELS_STEP_ID, wheels ? 0.4 : 0, AttributeModifier.Operation.ADD_VALUE);
			if (wheels && player.isSprinting()) {
				wheelsTrail(level, player);
			}
			Integer dash = DASHING.get(player.getUUID());
			if (dash != null) {
				dashPush(player);
				if (dash <= 1) {
					DASHING.remove(player.getUUID());
				} else {
					DASHING.put(player.getUUID(), dash - 1);
				}
			}
			if (isLunar(player) && player.tickCount % 4 == 0) {
				level.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 0.3, player.getZ(), 1, 0.3, 0.1, 0.3, 0.01);
			}
			if (player.tickCount % STONE_INTERVAL == 0 && EgyptRelics.isWearing(player, ModItems.FIVE_COLOR_STONE)) {
				mend(player);
			}
		}
	}

	/** Ruedas de viento y fuego: corriendo no te quemas y las llamas de la estela prenden a los hostiles que tocas. */
	private static void wheelsTrail(ServerLevel level, ServerPlayer player) {
		player.clearFire();
		if (player.tickCount % 2 == 0) {
			level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.1, player.getZ(), 2, 0.2, 0.05, 0.2, 0.01);
			level.sendParticles(ParticleTypes.SMALL_GUST, player.getX(), player.getY() + 0.2, player.getZ(), 1, 0.2, 0.05, 0.2, 0.0);
		}
		if (player.tickCount % 5 == 0) {
			for (LivingEntity mob : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(1.2, 0.2, 1.2),
					e -> e instanceof Enemy && e.isAlive() && !isFriend(player, e))) {
				mob.igniteForSeconds(WHEELS_BURN_SECONDS);
			}
		}
	}

	/** Piedra de cinco colores: un punto de durabilidad a cada pieza de armadura puesta y a lo que tienes en las manos. */
	private static void mend(ServerPlayer player) {
		for (EquipmentSlot slot : REPAIRED) {
			ItemStack stack = player.getItemBySlot(slot);
			if (stack.isDamaged()) {
				stack.setDamageValue(stack.getDamageValue() - 1);
			}
		}
	}

	private static void setModifier(Player player, Holder<Attribute> attribute, Identifier id, double amount,
			AttributeModifier.Operation operation) {
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
			instance.addTransientModifier(new AttributeModifier(id, amount, operation));
		}
	}

	/** Números de los tooltips chinos (%1$s, %2$s… de sus líneas de traducción, en este orden). */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "ruyi_jingu_bang" -> new Object[] {fmt(RUYI_REACH), RUYI_LENGTH, (int) RUYI_STRETCH_DAMAGE, RUYI_COOLDOWN / 20,
					RUYI_EXTENDED_TICKS / 20, (int) RUYI_EXTENDED_REACH};
			case "monkey_king_hairs" -> new Object[] {HAIRS_MONKEYS, HAIRS_TICKS / 20, MONKEY_HEALTH, MONKEY_DAMAGE, HAIRS_COOLDOWN / 20 / 60};
			case "purple_gold_gourd" -> new Object[] {GOURD_MAX_HEALTH, GOURD_DISSOLVE_TICKS / 20 / 60, GOURD_COOLDOWN / 20};
			case "banana_leaf_fan" -> new Object[] {FAN_BLADE_RANGE, (int) FAN_BLADE_DAMAGE, FAN_DASH_BLOCKS, FAN_CHARGES,
					FAN_RECHARGE_TICKS / 20, (int) FAN_DASH_DAMAGE};
			case "dragon_pearl" -> new Object[] {DRAGON_LEAVE_TICKS / 20, DRAGON_COOLDOWN / 20 / 60};
			case "wind_fire_wheels" -> new Object[] {Math.round(WHEELS_SPEED * 100)};
			case "hou_yi_bow" -> new Object[] {Math.round(HOU_YI_DAMAGE * 100), HOU_YI_SUN_FACTOR};
			case "change_elixir" -> new Object[] {Math.round(ELIXIR_AIR_DAMAGE * 100), ELIXIR_FULL_SECONDS, ELIXIR_QUARTER_SECONDS,
					ELIXIR_NEW_SECONDS, ELIXIR_COOLDOWN / 20 / 60};
			case "five_color_stone" -> new Object[] {STONE_INTERVAL / 20};
			default -> ChinaGods.tooltipArgs(id);
		};
	}

	private static String fmt(double value) {
		return value == Math.floor(value) ? Integer.toString((int) value) : Double.toString(value);
	}
}
