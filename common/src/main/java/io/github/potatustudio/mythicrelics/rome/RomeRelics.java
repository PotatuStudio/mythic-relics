package io.github.potatustudio.mythicrelics.rome;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Primera tanda de Roma (diseño aprobado el 2026-11-09): mito y leyenda propios de Roma.
 * <ul>
 *   <li><b>Bulla de la Loba</b> (collar): tus lobos se fortalecen, los salvajes no te atacan y, en apuros, aúllas y
 *       dos lobos salvajes se vuelven tuyos.</li>
 *   <li><b>Ancile</b> (escudo): bloquea sin romperse (componente {@code blocks_attacks} sin durabilidad), devuelve los
 *       proyectiles y protege a los aldeanos en una invasión.</li>
 *   <li><b>Paladio</b> (bloque, {@link PalladiumBlock}): no aparecen monstruos a su alrededor ({@link Palladiums}).</li>
 *   <li><b>Llave de Jano</b> ({@link JanusKeyItem}): cierra con llave puertas y cofres ({@link JanusLocks}); los mobs
 *       que te persiguen por detrás brillan.</li>
 *   <li><b>Lámpara de Vesta</b> (mano secundaria): regenera junto a una fogata, menos hambre y no te congelas.</li>
 *   <li><b>Libros sibilinos</b> y <b>Lítuo del augur</b>: ítems que se usan ({@link SibyllineBooksItem},
 *       {@link AugurLituusItem}).</li>
 * </ul>
 */
public final class RomeRelics {
	public static final int WOLF_RADIUS = 16;
	public static final int WOLF_HOWL_HEALTH = 6;
	public static final int WOLF_HOWL_TAMES = 2;
	public static final int WOLF_HOWL_COOLDOWN = 6000;

	public static final int ANCILE_RAID_RADIUS = 32;
	public static final float ANCILE_RAID_FACTOR = 0.5f;

	public static final int PALLADIUM_RADIUS = 48;

	public static final int VESTA_RADIUS = 5;
	public static final float VESTA_HUNGER_FACTOR = 0.5f;

	public static final int SIBYL_LEVELS = 3;
	public static final int SIBYL_RADIUS = 48;
	public static final int SIBYL_GLOW_TICKS = 300;
	public static final int SIBYL_COOLDOWN = 2400;

	public static final int LITUUS_FAVOR_TICKS = 6000;
	public static final int LITUUS_BAD_TICKS = 1200;
	public static final int LITUUS_COOLDOWN = 6000;
	public static final int LITUUS_BIRD_RADIUS = 16;

	/** Proyectiles que el Ancile bloqueó: se devuelven en el tick siguiente (la flecha termina su rebote primero). */
	private static final List<Object[]> REFLECT = new ArrayList<>();

	private RomeRelics() {
	}

	public static void init() {
		EntityEvent.LIVING_CHECK_SPAWN.register(RomeRelics::checkSpawn);
		InteractionEvent.RIGHT_CLICK_BLOCK.register(RomeRelics::onUseBlock);
		BlockEvent.BREAK.register(RomeRelics::onBreak);
		TickEvent.SERVER_POST.register(RomeRelics::tick);
	}

	static boolean holding(Player player, net.minecraft.world.item.Item item) {
		return player.getMainHandItem().is(item) || player.getOffhandItem().is(item);
	}

	// ---- Ancile (lo llama el mixin de LivingEntity) ------------------------------------------------------------------

	/** Después de bloquear: si fue un proyectil y el escudo es el Ancile, se lo devuelve a quien lo lanzó. */
	public static void afterBlocking(LivingEntity self, DamageSource source, float blocked) {
		if (blocked <= 0 || !(self.level() instanceof ServerLevel) || !self.getUseItem().is(ModItems.ANCILE.get())) {
			return;
		}
		if (source.getDirectEntity() instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity shooter
				&& shooter != self) {
			synchronized (REFLECT) {
				REFLECT.add(new Object[] {projectile, shooter, self});
			}
		}
	}

	/** Devuelve un proyectil a quien lo lanzó en el próximo tick (lo usan el Ancile y el espejo Yata). */
	public static void reflectLater(Projectile projectile, LivingEntity shooter, LivingEntity defender) {
		synchronized (REFLECT) {
			REFLECT.add(new Object[] {projectile, shooter, defender});
		}
	}

	/** Daño recibido: en una invasión, aldeanos y gólems cerca de quien sostiene el Ancile reciben la mitad. */
	public static float modifyIncomingDamage(LivingEntity self, DamageSource source, float amount) {
		if (!(self instanceof AbstractVillager || self instanceof IronGolem) || !(self.level() instanceof ServerLevel level)
				|| !level.isRaided(self.blockPosition())) {
			return amount;
		}
		for (ServerPlayer player : level.players()) {
			if (player.distanceToSqr(self) <= ANCILE_RAID_RADIUS * ANCILE_RAID_RADIUS && holding(player, ModItems.ANCILE.get())) {
				return amount * ANCILE_RAID_FACTOR;
			}
		}
		return amount;
	}

	// ---- Paladio -----------------------------------------------------------------------------------------------------

	private static EventResult checkSpawn(LivingEntity entity, LevelAccessor level, double x, double y, double z,
			EntitySpawnReason reason, BaseSpawner spawner) {
		if (spawner != null || !(entity instanceof Enemy) || !(level instanceof ServerLevel server)
				|| (reason != EntitySpawnReason.NATURAL && reason != EntitySpawnReason.CHUNK_GENERATION)) {
			return EventResult.pass();
		}
		return Palladiums.protects(server, x, y, z) ? EventResult.interruptFalse() : EventResult.pass();
	}

	// ---- Llave de Jano: candados ---------------------------------------------------------------------------------

	private static EventResult onUseBlock(Player player, InteractionHand hand, BlockPos pos, net.minecraft.core.Direction face) {
		if (!(player.level() instanceof ServerLevel level) || player.isShiftKeyDown() && player.getItemInHand(hand).is(ModItems.JANUS_KEY.get())) {
			return EventResult.pass();
		}
		if (JanusLocks.lockedAgainst(level, pos, player)) {
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.janus_locked").withStyle(ChatFormatting.GRAY));
			level.playSound(null, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.8f, 1.0f);
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	private static EventResult onBreak(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, ServerPlayer player) {
		if (!(level instanceof ServerLevel server)) {
			return EventResult.pass();
		}
		if (JanusLocks.lockedAgainst(server, pos, player) && !player.isCreative()) {
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.janus_locked").withStyle(ChatFormatting.GRAY));
			return EventResult.interruptFalse();
		}
		JanusLocks.unlock(server, pos);
		return EventResult.pass();
	}

	// ---- Cada tick ---------------------------------------------------------------------------------------------------

	private static void tick(MinecraftServer server) {
		reflect();
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ServerLevel level = player.level();
			if (player.tickCount % 20 == 0 && EgyptRelics.isWearing(player, ModItems.WOLF_BULLA)) {
				wolves(level, player);
			}
			if (player.tickCount % 20 == 0 && player.getOffhandItem().is(ModItems.VESTA_LAMP.get()) && nearCampfire(level, player)) {
				player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, 1, true, true, true));
			}
			if (player.tickCount % 10 == 0 && holding(player, ModItems.JANUS_KEY.get())) {
				janusFaces(level, player);
			}
		}
	}

	private static void reflect() {
		List<Object[]> todo;
		synchronized (REFLECT) {
			if (REFLECT.isEmpty()) {
				return;
			}
			todo = new ArrayList<>(REFLECT);
			REFLECT.clear();
		}
		for (Object[] r : todo) {
			Projectile projectile = (Projectile) r[0];
			LivingEntity shooter = (LivingEntity) r[1];
			LivingEntity defender = (LivingEntity) r[2];
			if (projectile.isRemoved() || !shooter.isAlive()) {
				continue;
			}
			Vec3 to = shooter.getEyePosition().subtract(projectile.position());
			projectile.setOwner(defender);
			projectile.shoot(to.x, to.y + to.horizontalDistance() * 0.05, to.z, 1.6f, 0.0f);
			projectile.needsSync = true;
			if (projectile.level() instanceof ServerLevel level) {
				level.sendParticles(ParticleTypes.ELECTRIC_SPARK, projectile.getX(), projectile.getY(), projectile.getZ(), 6, 0.1, 0.1, 0.1, 0.05);
			}
		}
	}

	/** Bulla: refuerza a tus lobos, calma a los salvajes y, en apuros, aúllas y dos salvajes se vuelven tuyos. */
	private static void wolves(ServerLevel level, ServerPlayer player) {
		List<Wolf> wild = new ArrayList<>();
		for (Wolf wolf : level.getEntitiesOfClass(Wolf.class, player.getBoundingBox().inflate(WOLF_RADIUS))) {
			if (wolf.isTame() && wolf.isOwnedBy(player)) {
				wolf.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, 0, true, true));
				wolf.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 50, 0, true, true));
			} else if (!wolf.isTame()) {
				if (wolf.getTarget() == player) {
					wolf.setTarget(null);
					wolf.stopBeingAngry();
				}
				wild.add(wolf);
			}
		}
		if (player.getHealth() <= WOLF_HOWL_HEALTH && !wild.isEmpty()
				&& RelicCooldowns.tryStart(player, "wolf_bulla", WOLF_HOWL_COOLDOWN, null)) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FOX_SCREECH, SoundSource.PLAYERS, 1.0f, 0.5f);
			wild.sort((a, b) -> Double.compare(a.distanceToSqr(player), b.distanceToSqr(player)));
			for (int i = 0; i < Math.min(WOLF_HOWL_TAMES, wild.size()); i++) {
				Wolf wolf = wild.get(i);
				wolf.tame(player);
				wolf.setOrderedToSit(false);
				level.sendParticles(ParticleTypes.HEART, wolf.getX(), wolf.getY() + 0.8, wolf.getZ(), 5, 0.3, 0.3, 0.3, 0.0);
			}
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.wolf_howl").withStyle(ChatFormatting.GOLD));
		}
	}

	private static boolean nearCampfire(ServerLevel level, ServerPlayer player) {
		BlockPos center = player.blockPosition();
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-VESTA_RADIUS, -2, -VESTA_RADIUS), center.offset(VESTA_RADIUS, 2, VESTA_RADIUS))) {
			if (CampfireBlock.isLitCampfire(level.getBlockState(pos))) {
				return true;
			}
		}
		return false;
	}

	/** Dos caras: los mobs que te persiguen y vienen por detrás brillan. */
	private static void janusFaces(ServerLevel level, ServerPlayer player) {
		Vec3 look = player.getLookAngle().multiply(1, 0, 1).normalize();
		for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(24), m -> m.getTarget() == player)) {
			Vec3 to = mob.position().subtract(player.position()).multiply(1, 0, 1);
			if (to.lengthSqr() > 1.0E-4 && look.dot(to.normalize()) < -0.3) {
				mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 15, 0, true, false));
			}
		}
	}

	/** La lámpara de Vesta en la mano secundaria: el hambre baja a la mitad (lo llama el mixin de Player). */
	public static float modifyExhaustion(Player player, float amount) {
		return player.getOffhandItem().is(ModItems.VESTA_LAMP.get()) ? amount * VESTA_HUNGER_FACTOR : amount;
	}

	/** La lámpara de Vesta en la mano secundaria: no te congelas (lo llama el mixin de LivingEntity). */
	public static boolean warmedByVesta(LivingEntity entity) {
		return entity instanceof Player player && player.getOffhandItem().is(ModItems.VESTA_LAMP.get());
	}

	/** Números de los tooltips de Roma (%1$s, %2$s… de sus líneas de traducción, en este orden). */
	public static Object[] tooltipArgs(String id) {
		return switch (id) {
			case "wolf_bulla" -> new Object[] {WOLF_RADIUS, WOLF_HOWL_HEALTH / 2, WOLF_HOWL_TAMES, WOLF_HOWL_COOLDOWN / 20 / 60};
			case "ancile" -> new Object[] {ANCILE_RAID_RADIUS, Math.round(ANCILE_RAID_FACTOR * 100)};
			case "palladium" -> new Object[] {PALLADIUM_RADIUS};
			case "vesta_lamp" -> new Object[] {VESTA_RADIUS, Math.round(VESTA_HUNGER_FACTOR * 100)};
			case "sibylline_books" -> new Object[] {SIBYL_LEVELS, SIBYL_RADIUS, SIBYL_GLOW_TICKS / 20, SIBYL_COOLDOWN / 20 / 60};
			case "augur_lituus" -> new Object[] {LITUUS_FAVOR_TICKS / 20 / 60, LITUUS_BAD_TICKS / 20 / 60, LITUUS_BIRD_RADIUS,
					LITUUS_COOLDOWN / 20 / 60};
			default -> io.github.potatustudio.mythicrelics.norse.NorseRelics.tooltipArgs(id);
		};
	}
}
