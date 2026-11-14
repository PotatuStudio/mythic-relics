package io.github.potatustudio.mythicrelics.entity;

import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * El halcón en que te convierte el manto de plumas de Freyja: solo es la figura (el jugador vuela invisible dentro de
 * ella). Sigue al jugador en cada tick, mira hacia donde mira él y bate las alas. No se puede herir ni tocar y no se
 * guarda con el mundo; desaparece con la transformación ({@code NorseSagas}).
 */
public class FreyjaFalcon extends Mob {
	private @Nullable UUID owner;

	public FreyjaFalcon(EntityType<? extends FreyjaFalcon> type, Level level) {
		super(type, level);
		setNoGravity(true);
		setNoAi(true);
		setSilent(true);
		noPhysics = true;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0);
	}

	public void follow(Player player) {
		owner = player.getUUID();
		snapTo(player.getX(), player.getY() + 0.2, player.getZ(), player.getYRot(), player.getXRot());
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
		setPos(player.getX(), player.getY() + 0.2, player.getZ());
		setYRot(player.getYRot());
		setXRot(player.getXRot() * 0.5f);
		yHeadRot = yBodyRot = getYRot();
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
	public boolean shouldBeSaved() {
		return false;
	}
}
