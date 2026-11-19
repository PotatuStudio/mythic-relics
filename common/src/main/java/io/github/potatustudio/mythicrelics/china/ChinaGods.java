package io.github.potatustudio.mythicrelics.china;

import com.mojang.math.Transformation;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.mixin.DisplayAccessor;
import io.github.potatustudio.mythicrelics.mixin.ItemDisplayAccessor;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.RelicFx;
import io.github.potatustudio.mythicrelics.relic.ServerState;
import io.github.potatustudio.mythicrelics.relic.TimedMobStatus;
import io.github.potatustudio.mythicrelics.relic.TooltipArgs;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Segunda tanda china (diseño aprobado el 2026-11-10): los talismanes fu y el saco de xirang.
 * <ul>
 *   <li>Ítems: {@link FuTalismansItem} y el saco de xirang (solo pasiva).</li>
 *   <li>Aquí: los talismanes que vencen y el rescate del xirang.</li>
 * </ul>
 */
public final class ChinaGods {
	public static final int TALISMAN_TICKS = 160;
	public static final int TALISMAN_COOLDOWN = 300;

	/** Saco de xirang: solo la pasiva (te salva de la lava o del vacío), con recarga de 5 min. */
	public static final int XIRANG_RESCUE_COOLDOWN = 6000;

	public static final String TALISMAN_TAG = MythicRelics.MOD_ID + ".talisman";
	/** No-muerto con un talismán pegado (sin IA): el papel sigue a la cabeza; al despegarse, ceniza. */
	private static final TimedMobStatus SEALED = new TimedMobStatus(TALISMAN_TAG, new TimedMobStatus.Hooks() {
		@Override
		public void active(ServerLevel level, Mob mob, long now, long until) {
			UUID id = TALISMAN_PAPER.get(mob.getUUID());
			if (id != null && level.getEntity(id) instanceof Display.ItemDisplay paper) {
				placePaper(paper, mob);
			}
			if (now % 10 == 0) {
				level.sendParticles(ParticleTypes.WAX_ON, mob.getX(), mob.getEyeY() + 0.2, mob.getZ(), 1, 0.1, 0.1, 0.1, 0.0);
			}
		}

		@Override
		public void released(ServerLevel level, Mob mob) {
			level.sendParticles(ParticleTypes.ASH, mob.getX(), mob.getEyeY(), mob.getZ(), 10, 0.2, 0.2, 0.2, 0.02);
		}
	});
	/** El papel pegado en la frente de cada no-muerto sellado (una entidad de visualización de ítem). */
	private static final Map<UUID, UUID> TALISMAN_PAPER = new HashMap<>();
	private static final String PAPER_TAG = MythicRelics.MOD_ID + ".talisman_paper";

	private ChinaGods() {
	}

	public static void init() {
		TickEvent.SERVER_POST.register(ChinaGods::tick);
		EntityEvent.ADD.register(ChinaGods::onEntityAdded);
	}

	// ---- Talismanes -------------------------------------------------------------------------------------------------

	static void seal(ServerLevel level, Mob mob) {
		SEALED.apply(level, mob, TALISMAN_TICKS);
		Display.ItemDisplay paper = EntityTypes.ITEM_DISPLAY.create(level, EntitySpawnReason.TRIGGERED);
		if (paper != null) {
			ItemStack stack = new ItemStack(ModItems.FU_TALISMANS.get());
			stack.set(DataComponents.ITEM_MODEL, MythicRelics.id("talisman_seal"));
			((ItemDisplayAccessor) paper).mythicrelics$setItemStack(stack);
			((ItemDisplayAccessor) paper).mythicrelics$setItemTransform(ItemDisplayContext.FIXED);
			float size = 0.55f * Math.clamp(mob.getBbWidth() / 0.6f, 0.6f, 2.0f);
			((DisplayAccessor) paper).mythicrelics$setTransformation(new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(size, size, size), new Quaternionf()));
			((DisplayAccessor) paper).mythicrelics$setPosRotInterpolationDuration(2);
			paper.addTag(PAPER_TAG);
			placePaper(paper, mob);
			level.addFreshEntity(paper);
			TALISMAN_PAPER.put(mob.getUUID(), paper.getUUID());
		}
	}

	/** El papel en la frente: delante de la cara, a la altura de los ojos, mirando hacia donde mira la cabeza. */
	private static void placePaper(Display.ItemDisplay paper, Mob mob) {
		Vec3 look = Vec3.directionFromRotation(0, mob.getYHeadRot());
		Vec3 at = new Vec3(mob.getX(), mob.getEyeY() + 0.12, mob.getZ()).add(look.scale(mob.getBbWidth() * 0.5 + 0.04));
		paper.snapTo(at.x, at.y, at.z, mob.getYHeadRot(), 0.0f);
	}

	/**
	 * Quita el papel de un sello que terminó. La entrada se borra siempre; si el papel está en un chunk descargado, se
	 * descarta al cargarse (no queda en el mapa, ver {@link #onEntityAdded}).
	 */
	private static void removePaper(MinecraftServer server, UUID mob) {
		UUID id = TALISMAN_PAPER.remove(mob);
		if (id == null) {
			return;
		}
		for (ServerLevel level : server.getAllLevels()) {
			if (level.getEntity(id) instanceof Display.ItemDisplay paper) {
				paper.discard();
				return;
			}
		}
	}

	private static EventResult onEntityAdded(Entity entity, Level level) {
		// Papeles que quedaron de una sesión anterior (el talismán ya no está): se quitan.
		// (un mob con el talismán vencido lo suelta TimedMobStatus al cargarse).
		if (!level.isClientSide() && entity instanceof Display.ItemDisplay paper && paper.entityTags().contains(PAPER_TAG)
				&& !TALISMAN_PAPER.containsValue(paper.getUUID())) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	// ---- Cada tick ---------------------------------------------------------------------------------------------------

	/** Último suelo firme de cada jugador (a donde lo devuelve el xirang si cae al vacío). */
	private static final Map<UUID, GlobalPos> SAFE_GROUND = new HashMap<>();

	/** Saco de xirang: la tierra crece bajo tus pies y te saca de la lava o del vacío. */
	private static void xirangRescue(ServerPlayer player) {
		if (!RelicCooldowns.tryStart(player, "xirang_rescue", XIRANG_RESCUE_COOLDOWN,
				BuiltInRegistries.ITEM.getKey(ModItems.XIRANG_SACK.get()))) {
			return;
		}
		ServerLevel level = player.level();
		BlockPos feet;
		if (player.isInLava()) {
			// Sube hasta la superficie de la lava.
			feet = player.blockPosition();
			while (level.getFluidState(feet).is(FluidTags.LAVA) && feet.getY() < level.getMaxY()) {
				feet = feet.above();
			}
		} else {
			GlobalPos safe = SAFE_GROUND.get(player.getUUID());
			feet = (safe != null && safe.dimension() == level.dimension() ? safe.pos() : level.getRespawnData().pos()).above();
		}
		for (BlockPos p : BlockPos.betweenClosed(feet.offset(-1, -1, -1), feet.offset(1, -1, 1))) {
			if (level.getBlockState(p).canBeReplaced() || level.getFluidState(p).is(FluidTags.LAVA)) {
				level.setBlockAndUpdate(p, Blocks.DIRT.defaultBlockState());
			}
		}
		for (BlockPos p : BlockPos.betweenClosed(feet, feet.above())) {
			if (level.getFluidState(p).is(FluidTags.LAVA)) {
				level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
			}
		}
		player.teleportTo(feet.getX() + 0.5, feet.getY(), feet.getZ() + 0.5);
		player.setDeltaMovement(Vec3.ZERO);
		player.connection.send(new ClientboundSetEntityMotionPacket(player));
		player.resetFallDistance();
		player.clearFire();
		player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0));
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,
				Blocks.DIRT.defaultBlockState()), player.getX(), player.getY(), player.getZ(), 40, 1.0, 0.3, 1.0, 0.1);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ROOTED_DIRT_PLACE,
				SoundSource.PLAYERS, 1.2f, 0.7f);
		RelicFx.notify(player, "xirang_rescue", ChatFormatting.GOLD);
	}

	private static void tick(MinecraftServer server) {
		// Papeles de sellos que terminaron (también con el mob en un chunk descargado).
		if (!TALISMAN_PAPER.isEmpty()) {
			for (UUID mob : List.copyOf(TALISMAN_PAPER.keySet())) {
				if (!SEALED.isActive(mob)) {
					removePaper(server, mob);
				}
			}
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.onGround() && !player.isInLava()) {
				SAFE_GROUND.put(player.getUUID(), GlobalPos.of(player.level().dimension(), player.blockPosition()));
			}
			if ((player.isInLava() || player.getY() < player.level().getMinY() - 8) && !player.isSpectator()
					&& player.getInventory().contains(s -> s.is(ModItems.XIRANG_SACK.get()))) {
				xirangRescue(player);
			}
		}
	}

	/** Números de los tooltips de la segunda tanda china. */
	public static void registerTooltipArgs() {
		TooltipArgs.register("fu_talismans", TALISMAN_TICKS / 20, TALISMAN_COOLDOWN / 20);
		TooltipArgs.register("xirang_sack", XIRANG_RESCUE_COOLDOWN / 20 / 60);
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		ServerState.perPlayer(SAFE_GROUND);
		
		ServerState.clearOnStop(TALISMAN_PAPER);
	}
}
