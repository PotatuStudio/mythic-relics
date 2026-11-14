package io.github.potatustudio.mythicrelics.japan;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.greece.GreeceLegends;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.ServerState;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Segunda tanda japonesa (diseño aprobado el 2026-11-10): la bolsa de vientos de Fūjin (y el set con el tambor de
 * Raijin), la máscara de tengu, el sake de Yashiori, el plato del kappa, la hoja del tanuki, el hacha de Kintarō, la
 * muñeca de zashiki-warashi, la lanza Ame-no-nuboko y la escama de Orochi.
 * <ul>
 *   <li>Ítems: {@link FujinWindBagItem}, {@link YashioriSakeItem}, {@link TanukiLeafItem}, {@link AmeNoNubokoItem},
 *       {@link OrochiScaleItem} y el bloque de la muñeca ({@link ZashikiWarashiBlock}).</li>
 *   <li>Aquí: los críticos del tengu (el doble salto lo da el cliente, como las sandalias de Hermes), el plato del kappa
 *       y su sequía, la borrachera del sake, el disfraz del tanuki, la tala del hacha y la prosperidad de la muñeca.</li>
 * </ul>
 */
public final class JapanGods {
	/** Con Caída lenta desde el primer tick: sube unos 10 bloques en 2 s. */
	public static final double WIND_LIFT = 0.6;
	public static final int WIND_GLIDE_TICKS = 120;
	public static final int WIND_GALE_RADIUS = 8;
	public static final int WIND_COOLDOWN = 400;
	public static final int SET_RAIJIN_HITS = 3;
	public static final int SET_CHAIN_TARGETS = 2;

	public static final float TENGU_CRIT = 0.5f;

	public static final int SAKE_RADIUS = 4;
	public static final int SAKE_RANGE = 12;
	public static final int SAKE_TICKS = 200;
	public static final int SAKE_COOLDOWN = 1200;

	public static final float KAPPA_DAMAGE = 0.3f;
	public static final int KAPPA_DRY_TICKS = 1200;

	public static final int TANUKI_TICKS = 600;
	public static final int TANUKI_COOLDOWN = 2400;

	public static final int KINTARO_MAX_LOGS = 64;
	public static final float KINTARO_BEAST_DAMAGE = 0.5f;

	public static final int ZASHIKI_RADIUS = 16;

	public static final int NUBOKO_RANGE = 16;
	public static final int NUBOKO_RADIUS = 3;
	public static final int NUBOKO_COOLDOWN = 600;

	public static final int OROCHI_TICKS = 900;
	public static final int OROCHI_COOLDOWN = 6000;
	public static final int OROCHI_REACH = 6;
	public static final float OROCHI_BITE = 4.0f;
	public static final int OROCHI_BITE_INTERVAL = 30;

	/** Hostil borracho -> tick en que se le pasa. */
	private static final Map<UUID, Long> DRUNK = new HashMap<>();
	/** Jugador transformado con la hoja del tanuki -> tick en que vuelve a su forma. */
	private static final Map<UUID, Long> DISGUISED = new HashMap<>();
	/** Ticks seguidos al sol fuera del agua con el plato del kappa. */
	private static final Map<UUID, Integer> DRYING = new HashMap<>();
	private static final Set<UUID> DRY = new HashSet<>();

	private JapanGods() {
	}

	public static void init() {
		TickEvent.SERVER_POST.register(JapanGods::tick);
		EntityEvent.LIVING_HURT.register(JapanGods::onHurt);
		BlockEvent.BREAK.register(JapanGods::onBreak);
	}

	public static boolean kappaWet(Player player) {
		return WornRelics.isWearing(player, ModItems.KAPPA_DISH) && !DRY.contains(player.getUUID());
	}

	public static boolean isDisguised(Player player) {
		return DISGUISED.containsKey(player.getUUID());
	}

	static void disguise(ServerPlayer player) {
		DISGUISED.put(player.getUUID(), player.level().getGameTime() + TANUKI_TICKS);
	}

	static void makeDrunk(Mob mob, long until) {
		DRUNK.put(mob.getUUID(), until);
	}

	// ---- Daño ---------------------------------------------------------------------------------------------------------

	/** Daño hecho: los críticos con la máscara de tengu, el plato del kappa bajo el agua y el hacha contra las bestias. */
	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		if (!(source.getEntity() instanceof Player attacker) || source.getDirectEntity() != attacker) {
			return amount;
		}
		if (WornRelics.isWearing(attacker, ModItems.TENGU_MASK) && attacker.fallDistance > 0 && !attacker.onGround()
				&& !attacker.isInWater() && !attacker.onClimbable() && !attacker.isPassenger()) {
			amount *= 1.0f + TENGU_CRIT;
		}
		if (attacker.isInWater() && kappaWet(attacker)) {
			amount *= 1.0f + KAPPA_DAMAGE;
		}
		if (attacker.getMainHandItem().is(ModItems.KINTARO_AXE.get())
				&& (self instanceof PolarBear || self instanceof Hoglin || self instanceof Ravager || self instanceof Spider)) {
			amount *= 1.0f + KINTARO_BEAST_DAMAGE;
		}
		return amount;
	}

	/** Atacar deshace el disfraz del tanuki. */
	private static EventResult onHurt(LivingEntity target, DamageSource source, float amount) {
		if (source.getEntity() instanceof ServerPlayer player && DISGUISED.remove(player.getUUID()) != null) {
			RelicFx.notify(player, "tanuki_revealed", ChatFormatting.GRAY);
			player.level().sendParticles(ParticleTypes.POOF, player.getX(), player.getY() + 1, player.getZ(), 16, 0.4, 0.6, 0.4, 0.02);
		}
		return EventResult.pass();
	}

	// ---- Hacha de Kintarō -------------------------------------------------------------------------------------------

	private static EventResult onBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player) {
		if (!(level instanceof ServerLevel server) || !player.isShiftKeyDown() || !state.is(BlockTags.LOGS)
				|| !player.getMainHandItem().is(ModItems.KINTARO_AXE.get())) {
			return EventResult.pass();
		}
		Deque<BlockPos> todo = new ArrayDeque<>();
		Set<BlockPos> seen = new HashSet<>();
		todo.add(pos);
		seen.add(pos);
		int felled = 0;
		while (!todo.isEmpty() && felled < KINTARO_MAX_LOGS) {
			BlockPos p = todo.poll();
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = 0; dy <= 1; dy++) {
					for (int dz = -1; dz <= 1; dz++) {
						BlockPos n = p.offset(dx, dy, dz);
						if (seen.add(n) && server.getBlockState(n).is(BlockTags.LOGS)) {
							todo.add(n);
							server.destroyBlock(n, true, player);
							if (++felled >= KINTARO_MAX_LOGS) {
								return EventResult.pass();
							}
						}
					}
				}
			}
		}
		return EventResult.pass();
	}

	// ---- Cada tick ----------------------------------------------------------------------------------------------------

	private static void tick(MinecraftServer server) {
		long now = server.overworld().getGameTime();
		DISGUISED.values().removeIf(until -> until <= now);
		Iterator<Map.Entry<UUID, Long>> drunk = DRUNK.entrySet().iterator();
		while (drunk.hasNext()) {
			Map.Entry<UUID, Long> e = drunk.next();
			if (e.getValue() <= now) {
				drunk.remove();
				continue;
			}
			if (now % 20 != 0) {
				continue;
			}
			for (ServerLevel level : server.getAllLevels()) {
				if (level.getEntity(e.getKey()) instanceof Mob mob) {
					// Borracho: busca pelea con el hostil más cercano.
					mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(8),
									o -> o != mob && o instanceof Enemy && o.isAlive())
							.stream().min((a, b) -> Double.compare(a.distanceToSqr(mob), b.distanceToSqr(mob)))
							.ifPresent(mob::setTarget);
					level.sendParticles(ParticleTypes.BUBBLE_POP, mob.getX(), mob.getEyeY() + 0.3, mob.getZ(), 3, 0.2, 0.1, 0.2, 0.0);
					break;
				}
			}
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ServerLevel level = player.level();
			if (DISGUISED.containsKey(player.getUUID()) && player.tickCount % 10 == 0) {
				// Los objetivos los niega TargetVeto; aquí solo se sueltan los que ya lo tenían fijado.
				for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(32), m -> m.getTargetUnchecked() == player)) {
					mob.setTarget(null);
				}
				level.sendParticles(ParticleTypes.COMPOSTER, player.getX(), player.getY() + 1.9, player.getZ(), 2, 0.2, 0.1, 0.2, 0.0);
			}
			if (WornRelics.isWearing(player, ModItems.KAPPA_DISH)) {
				kappa(level, player);
			}
			if (player.tickCount % 40 == 0 && ZashikiWarashis.near(level, player.blockPosition(), ZASHIKI_RADIUS)) {
				player.addEffect(new MobEffectInstance(MobEffects.LUCK, 60, 0, true, false));
				player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 60, 0, true, false));
			}
		}
	}

	/** Plato del kappa: bajo el agua, nado rápido; al sol fuera del agua, se seca hasta volver a mojarse. */
	private static void kappa(ServerLevel level, ServerPlayer player) {
		UUID id = player.getUUID();
		if (player.isInWaterOrRain()) {
			DRYING.remove(id);
			if (DRY.remove(id)) {
				RelicFx.notify(player, "kappa_wet", ChatFormatting.AQUA);
			}
			if (player.isInWater()) {
				refresh(player, MobEffects.DOLPHINS_GRACE, 40);
			}
			return;
		}
		if (JapanRelics.isDay(level) && level.canSeeSky(player.blockPosition().above()) && !DRY.contains(id)) {
			int t = DRYING.merge(id, 1, Integer::sum);
			if (t >= KAPPA_DRY_TICKS) {
				DRY.add(id);
				DRYING.remove(id);
				RelicFx.notify(player, "kappa_dry", ChatFormatting.GOLD);
			}
		}
	}

	/** Números de los tooltips de la segunda tanda japonesa. */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "fujin_wind_bag" -> new Object[] {WIND_GLIDE_TICKS / 20, WIND_GALE_RADIUS, WIND_COOLDOWN / 20, SET_RAIJIN_HITS, SET_CHAIN_TARGETS};
			case "tengu_mask" -> new Object[] {Math.round(TENGU_CRIT * 100)};
			case "yashiori_sake" -> new Object[] {SAKE_RADIUS, SAKE_RANGE, SAKE_TICKS / 20, SAKE_COOLDOWN / 20};
			case "kappa_dish" -> new Object[] {Math.round(KAPPA_DAMAGE * 100), KAPPA_DRY_TICKS / 20};
			case "tanuki_leaf" -> new Object[] {TANUKI_TICKS / 20, TANUKI_COOLDOWN / 20 / 60};
			case "kintaro_axe" -> new Object[] {KINTARO_MAX_LOGS, Math.round(KINTARO_BEAST_DAMAGE * 100)};
			case "zashiki_warashi" -> new Object[] {ZASHIKI_RADIUS};
			case "ame_no_nuboko" -> new Object[] {NUBOKO_RANGE, NUBOKO_RADIUS * 2 + 1, NUBOKO_COOLDOWN / 20};
			case "orochi_scale" -> new Object[] {OROCHI_TICKS / 20, OROCHI_REACH, (int) OROCHI_BITE, OROCHI_COOLDOWN / 20 / 60};
			default -> GreeceLegends.tooltipArgs(id);
		};
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		ServerState.perPlayer(DRYING, DISGUISED);
		ServerState.onQuit(p -> DRY.remove(p.getUUID()));
		ServerState.clearOnStop(DRY, DRUNK);
	}

	/** Pone el efecto solo si falta o le quedan menos de 20 ticks (cada addEffect manda un paquete al cliente). */
	private static void refresh(Player player, Holder<MobEffect> effect, int ticks) {
		MobEffectInstance current = player.getEffect(effect);
		if (current == null || current.getDuration() < 20) {
			player.addEffect(new MobEffectInstance(effect, ticks, 0, true, false));
		}
	}
}
