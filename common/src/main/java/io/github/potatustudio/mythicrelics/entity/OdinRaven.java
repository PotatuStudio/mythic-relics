package io.github.potatustudio.mythicrelics.entity;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Huginn o Muninn, los cuervos de Odín: mascota pasiva. Se posa en algún lugar alto a media distancia de su dueño (unos 4 a
 * 7 bloques: el suelo, un árbol, un muro) y desde ahí lo mira. Cuando el dueño se aleja demasiado, vuela a otro sitio
 * cerca de él. Grazna de vez en cuando. No ataca, no se puede herir, atraviesa los bloques al volar y no se guarda con el
 * mundo; si el dueño se va, se va con él. Mientras están afuera, los hostiles cercanos brillan ({@code OdinRavensItem}).
 */
public class OdinRaven extends Mob {
	private static final EntityDataAccessor<Boolean> PERCHED = SynchedEntityData.defineId(OdinRaven.class, EntityDataSerializers.BOOLEAN);
	/** Se posan lejos, a la vista pero sin estar encima del dueño, y cambian de percha cuando él se aleja. */
	private static final double PERCH_MIN = 8.0;
	private static final double PERCH_SPREAD = 6.0;
	private static final double LEAVE_DISTANCE = 18.0;
	private static final double FLY_SPEED = 0.45;
	/** Si se quedan muy atrás, vuelan más rápido; si se pierden (más de 40 bloques), reaparecen cerca del dueño. */
	private static final double CATCH_UP_DISTANCE = 14.0;
	private static final double LOST_DISTANCE = 40.0;

	private @Nullable UUID owner;
	private int index;
	private @Nullable Vec3 perch;

	public OdinRaven(EntityType<? extends OdinRaven> type, Level level) {
		super(type, level);
		setNoGravity(true);
		setNoAi(true);
		noPhysics = true;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.FLYING_SPEED, 0.6);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(PERCHED, false);
	}

	public boolean isPerched() {
		return entityData.get(PERCHED);
	}

	public void bind(Player player, int index) {
		owner = player.getUUID();
		this.index = index;
	}

	/** Un lugar donde posarse cerca del dueño: lo más alto que haya en esa columna (copa de árbol, muro o suelo). */
	private Vec3 findPerch(ServerLevel level, Player player) {
		double a = random.nextDouble() * Math.PI * 2 + index * Math.PI;
		double r = PERCH_MIN + random.nextDouble() * PERCH_SPREAD;
		int x = Mth.floor(player.getX() + Math.cos(a) * r);
		int z = Mth.floor(player.getZ() + Math.sin(a) * r);
		int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
		// Si la columna está muy por encima o por debajo (un acantilado, una cueva), se queda a la altura del dueño.
		if (Math.abs(top - player.getY()) > 8) {
			BlockPos feet = BlockPos.containing(x, player.getY(), z);
			while (feet.getY() > player.getY() - 4 && level.getBlockState(feet.below()).isAir()) {
				feet = feet.below();
			}
			top = feet.getY();
		}
		return new Vec3(x + 0.5, top, z + 0.5);
	}

	@Override
	public void tick() {
		super.tick();
		noPhysics = true;
		if (!(level() instanceof ServerLevel level)) {
			return;
		}
		Player player = owner == null ? null : level.getPlayerByUUID(owner);
		if (player == null || !player.isAlive() || player.level() != level) {
			discard();
			return;
		}
		// Se perdieron (el dueño corrió, voló o se teletransportó): vuelven a aparecer a su alrededor, en una percha nueva.
		if (player.distanceToSqr(this) > LOST_DISTANCE * LOST_DISTANCE) {
			perch = findPerch(level, player);
			level.sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, getX(), getY() + 0.3, getZ(), 6, 0.2, 0.2, 0.2, 0.01);
			setPos(perch.x, perch.y + 3.0, perch.z);
			level.sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, getX(), getY() + 0.3, getZ(), 6, 0.2, 0.2, 0.2, 0.01);
		}
		if (perch == null || perch.distanceToSqr(player.position()) > LEAVE_DISTANCE * LEAVE_DISTANCE) {
			perch = findPerch(level, player);
			entityData.set(PERCHED, false);
		}
		Vec3 to = perch.subtract(position());
		double dist = to.length();
		if (dist > 0.15) {
			// Vuela en arco: sube un poco a mitad de camino.
			double speed = dist > CATCH_UP_DISTANCE ? FLY_SPEED * 2.2 : FLY_SPEED;
			Vec3 step = to.normalize().scale(Math.min(speed, dist)).add(0, dist > 2 ? 0.08 : 0, 0);
			setDeltaMovement(Vec3.ZERO);
			setPos(getX() + step.x, getY() + step.y, getZ() + step.z);
			face(step.x, step.z);
			entityData.set(PERCHED, false);
		} else {
			setDeltaMovement(Vec3.ZERO);
			setPos(perch.x, perch.y, perch.z);
			entityData.set(PERCHED, true);
			// Posado, mira a su dueño.
			face(player.getX() - getX(), player.getZ() - getZ());
			double dy = player.getEyeY() - getEyeY();
			setXRot((float) (-Mth.atan2(dy, Math.sqrt(distanceToSqr(player.getX(), getY(), player.getZ()))) * Mth.RAD_TO_DEG));
		}
	}

	private void face(double dx, double dz) {
		float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0f;
		setYRot(yaw);
		setYBodyRot(yaw);
		setYHeadRot(yaw);
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}

	/** Un graznido cada medio minuto, más o menos (el sonido del loro, grave, se parece al de un cuervo). */
	@Override
	public int getAmbientSoundInterval() {
		return 600;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.PARROT_AMBIENT;
	}

	@Override
	protected float getSoundVolume() {
		return 0.6f;
	}

	@Override
	public float getVoicePitch() {
		return 0.45f + random.nextFloat() * 0.1f;
	}
}
