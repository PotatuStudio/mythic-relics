package io.github.potatustudio.mythicrelics.entity;

import io.github.potatustudio.mythicrelics.norse.NorseSagas;
import java.util.UUID;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * La espada de Freyr, que pelea sola. La llevas envainada en la espalda; al entrar en combate sale de la vaina girando
 * con un destello dorado, embiste a un enemigo dejando una estela de luz, da tajos en arco o estocadas, vuelve a tu lado
 * entre ataque y ataque y, a los 5 s sin combate, vuelve girando a la vaina y desaparece. No se puede herir.
 *
 * <p>Movimiento suave: cada paso del guion fija un <em>objetivo</em> (posición y orientación) y la espada lo alcanza con
 * una curva amortiguada, así las transiciones entre movimientos no saltan. La espada se mueve con {@code setPos} (sin
 * física) como los cuervos de Odín.
 */
public class FreyrSword extends Mob {
	public static final byte GUARD = 0;
	public static final byte DASH = 1;
	public static final byte SLASH = 2;
	public static final byte THRUST = 3;
	public static final byte SHEATHE = 4;
	private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(FreyrSword.class, EntityDataSerializers.BYTE);
	/** Id de entidad del dueño (-1 sin dueño): el cliente lo usa para dejar la vaina vacía en su espalda. */
	private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(FreyrSword.class, EntityDataSerializers.INT);
	private static final DustParticleOptions GOLD = new DustParticleOptions(0xFFD36A, 1.0f);
	private static final DustParticleOptions GOLD_SMALL = new DustParticleOptions(0xFFF0B0, 0.6f);
	private static final int DASH_TICKS = 7;
	private static final int SLASH_TICKS = 11;
	private static final int THRUST_TICKS = 10;
	private static final int GUARD_TICKS = 8;
	private static final int SHEATHE_TICKS = 14;

	private @Nullable UUID owner;
	private @Nullable LivingEntity target;
	private int stateTicks;
	private int attacks;
	private int idle;
	private Vec3 from = Vec3.ZERO;
	private double orbit;

	public FreyrSword(EntityType<? extends FreyrSword> type, Level level) {
		super(type, level);
		setNoGravity(true);
		setNoAi(true);
		setSilent(true);
		noPhysics = true;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(STATE, GUARD);
		builder.define(OWNER_ID, -1);
	}

	public byte state() {
		return entityData.get(STATE);
	}

	/** Id de entidad del dueño, o -1. */
	public int ownerId() {
		return entityData.get(OWNER_ID);
	}

	/** Sale de la vaina, a la altura de la cadera izquierda del dueño. */
	public void draw(Player player) {
		owner = player.getUUID();
		entityData.set(OWNER_ID, player.getId());
		Vec3 hip = hip(player);
		snapTo(hip.x, hip.y, hip.z, player.getYRot(), -80.0f);
		setState(GUARD);
		if (level() instanceof ServerLevel level) {
			level.sendParticles(GOLD, hip.x, hip.y + 0.4, hip.z, 30, 0.2, 0.4, 0.2, 0.0);
			level.sendParticles(ParticleTypes.END_ROD, hip.x, hip.y + 0.4, hip.z, 12, 0.2, 0.3, 0.2, 0.08);
			level.playSound(null, hip.x, hip.y, hip.z, SoundEvents.ARMOR_EQUIP_NETHERITE.value(), SoundSource.PLAYERS, 1.0f, 1.4f);
			level.playSound(null, hip.x, hip.y, hip.z, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0f, 1.6f);
		}
	}

	/**
	 * La boca de la vaina: la vaina cruza la espalda y la empuñadura asoma sobre el hombro derecho
	 * (freyr_sword_worn.json), así que la espada sale y vuelve por ahí. directionFromRotation(0, yRot - 90) es la
	 * izquierda del jugador.
	 */
	/** Objetivo en guardia (se recalcula cada 5 ticks). */
	private @Nullable LivingEntity guardTarget;

	private static Vec3 hip(Player player) {
		Vec3 left = Vec3.directionFromRotation(0, player.getYRot() - 90.0f);
		Vec3 forward = Vec3.directionFromRotation(0, player.getYRot());
		// Empuñadura sobre el hombro derecho y detrás de la espalda (centro del cuerpo x 8, z 8; y 14 = hombros, 1,5 bloques).
		return player.position().add(left.scale(-0.22)).add(forward.scale(-0.2)).add(0, 1.62, 0);
	}

	private Vec3 guardPoint(Player player) {
		Vec3 left = Vec3.directionFromRotation(0, player.getYRot() - 90.0f);
		double bob = Math.sin(tickCount * 0.15) * 0.08;
		return player.position().add(left.scale(-0.9)).add(0, 1.6 + bob, 0);
	}

	private void setState(byte s) {
		entityData.set(STATE, s);
		stateTicks = 0;
		from = position();
	}

	@Override
	public void tick() {
		super.tick();
		noPhysics = true;
		if (!(level() instanceof ServerLevel level)) {
			return;
		}
		Player player = owner == null ? null : level.getPlayerByUUID(owner);
		if (player == null || !player.isAlive() || player.level() != level || player.distanceToSqr(this) > 48 * 48) {
			discard();
			return;
		}
		stateTicks++;
		Vec3 goal;
		Vec3 aim;
		byte s = state();
		if (target != null && (!target.isAlive() || target.distanceToSqr(player) > (NorseSagas.FREYR_RADIUS + 4) * (NorseSagas.FREYR_RADIUS + 4))) {
			target = null;
		}
		switch (s) {
			case DASH -> {
				if (target == null) {
					setState(GUARD);
					return;
				}
				double k = ease(Math.min(1.0, stateTicks / (double) DASH_TICKS));
				Vec3 to = front(target);
				Vec3 ctrl = from.add(to).scale(0.5).add(0, 0.8, 0);
				goal = from.lerp(ctrl, k).lerp(ctrl.lerp(to, k), k);
				aim = center(target);
				trail(level, 2);
				if (stateTicks >= DASH_TICKS) {
					orbit = Math.atan2(position().z - target.getZ(), position().x - target.getX());
					setState(attacks % 2 == 0 ? SLASH : THRUST);
				}
			}
			case SLASH -> {
				if (target == null) {
					setState(GUARD);
					return;
				}
				double u = ease(Math.min(1.0, stateTicks / (double) SLASH_TICKS));
				double a = orbit + u * Math.PI * 2;
				Vec3 c = center(target);
				goal = c.add(Math.cos(a) * 1.0, 0.1 + Math.sin(u * Math.PI) * 0.35, Math.sin(a) * 1.0);
				aim = goal.add(-Math.sin(a), 0, Math.cos(a));   // la punta va por delante en el arco
				trail(level, 3);
				if (stateTicks == SLASH_TICKS / 2) {
					strike(level, player, false);
				}
				if (stateTicks >= SLASH_TICKS) {
					finishAttack();
				}
			}
			case THRUST -> {
				if (target == null) {
					setState(GUARD);
					return;
				}
				Vec3 c = center(target);
				Vec3 back = c.add(c.subtract(player.position()).multiply(1, 0, 1).normalize().scale(-1.8)).add(0, 0.5, 0);
				double u = stateTicks / (double) THRUST_TICKS;
				goal = u < 0.55 ? from.lerp(back, ease(u / 0.55)) : back.lerp(c, ease((u - 0.55) / 0.45) * 0.85);
				aim = c;
				if (u > 0.55) {
					trail(level, 3);
				}
				if (stateTicks == THRUST_TICKS - 1) {
					strike(level, player, true);
				}
				if (stateTicks >= THRUST_TICKS) {
					finishAttack();
				}
			}
			case SHEATHE -> {
				double k = ease(Math.min(1.0, stateTicks / (double) SHEATHE_TICKS));
				Vec3 hip = hip(player);
				goal = from.lerp(hip, k).add(0, Math.sin(k * Math.PI) * 0.5, 0);
				aim = goal.add(0, -1, 0);
				if (stateTicks >= SHEATHE_TICKS) {
					level.sendParticles(GOLD, hip.x, hip.y + 0.3, hip.z, 16, 0.15, 0.2, 0.15, 0.0);
					level.playSound(null, hip.x, hip.y, hip.z, SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.PLAYERS, 1.0f, 0.9f);
					level.playSound(null, hip.x, hip.y, hip.z, SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.8f, 1.6f);
					discard();
					return;
				}
			}
			default -> {
				// En guardia: flota a tu lado con la punta hacia el enemigo más cercano.
				Vec3 guard = guardPoint(player);
				double k = ease(Math.min(1.0, stateTicks / (double) GUARD_TICKS));
				goal = from.lerp(guard, k);
				if (tickCount % 5 == 0 || guardTarget != null && !guardTarget.isAlive()) {
					guardTarget = NorseSagas.freyrTarget(player);
				}
				LivingEntity next = guardTarget;
				aim = next != null ? center(next) : goal.add(Vec3.directionFromRotation(-30, player.getYRot()));
				if (next == null && (tickCount % 10 != 0 || !NorseSagas.inCombat(player))) {
					if (++idle >= NorseSagas.FREYR_IDLE_TICKS) {
						setState(SHEATHE);
					}
				} else {
					idle = 0;
				}
				if (next != null && stateTicks >= GUARD_TICKS) {
					target = next;
					setState(DASH);
				}
			}
		}
		// Seguimiento amortiguado: la espada alcanza el objetivo sin saltos.
		Vec3 pos = position().lerp(goal, 0.55);
		Vec3 dir = aim.subtract(pos);
		float yaw = (float) (Mth.atan2(dir.z, dir.x) * Mth.RAD_TO_DEG) - 90.0f;
		float pitch = (float) (-(Mth.atan2(dir.y, dir.horizontalDistance()) * Mth.RAD_TO_DEG));
		setPos(pos.x, pos.y, pos.z);
		setYRot(Mth.approachDegrees(getYRot(), yaw, 35.0f));
		setXRot(Mth.approachDegrees(getXRot(), pitch, 35.0f));
		yHeadRot = yBodyRot = getYRot();
	}

	private void finishAttack() {
		attacks++;
		setState(GUARD);
	}

	private void strike(ServerLevel level, Player player, boolean thrust) {
		if (target == null || !target.isAlive()) {
			return;
		}
		float damage = NorseSagas.freyrDamage(player, target);
		target.hurtServer(level, level.damageSources().playerAttack(player), damage);
		Vec3 push = target.position().subtract(player.position()).multiply(1, 0, 1).normalize().scale(thrust ? 0.7 : 0.4);
		target.push(push.x, thrust ? 0.3 : 0.2, push.z);
		Vec3 c = center(target);
		level.sendParticles(GOLD, c.x, c.y, c.z, 14, 0.3, 0.3, 0.3, 0.0);
		level.sendParticles(ParticleTypes.CRIT, c.x, c.y, c.z, 8, 0.3, 0.3, 0.3, 0.25);
		if (!thrust) {
			level.sendParticles(ParticleTypes.SWEEP_ATTACK, c.x, c.y, c.z, 1, 0.0, 0.0, 0.0, 0.0);
		}
		level.playSound(null, c.x, c.y, c.z, thrust ? SoundEvents.TRIDENT_HIT : SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.9f,
				thrust ? 1.2f : 1.5f);
	}

	/** Estela dorada a lo largo de la hoja, desde la última posición. */
	private void trail(ServerLevel level, int n) {
		Vec3 p = position();
		for (int i = 0; i < n; i++) {
			level.sendParticles(i % 2 == 0 ? GOLD : GOLD_SMALL, p.x, p.y + 0.3, p.z, 1, 0.05, 0.05, 0.05, 0.0);
		}
	}

	private static double ease(double u) {
		return u * u * (3 - 2 * u);
	}

	private static Vec3 center(LivingEntity e) {
		return e.position().add(0, e.getBbHeight() * 0.6, 0);
	}

	/** Delante del enemigo, del lado del dueño. */
	private Vec3 front(LivingEntity e) {
		Vec3 c = center(e);
		Vec3 toSword = position().subtract(c).multiply(1, 0, 1);
		if (toSword.lengthSqr() < 1.0E-4) {
			toSword = new Vec3(0, 0, 1);
		}
		return c.add(toSword.normalize().scale(1.0));
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}
}
