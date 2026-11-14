package io.github.potatustudio.mythicrelics.norse;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import io.github.potatustudio.mythicrelics.MythicRelics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;

/**
 * Cabeza de Mímir en la mano secundaria:
 * <ul>
 *   <li>Al preguntarle, los minerales valiosos cercanos se marcan unos segundos con un contorno brillante (una copia
 *       del bloque que brilla y se ve a través de las paredes), del color del mineral.</li>
 *   <li>Se le pregunta (clic derecho): dice una vez, sobre la barra, cuál es el tesoro más valioso cerca (debris,
 *       diamante o cofre sin abrir, esmeralda, oro, lapislázuli), marca los minerales y te guía con una estela unos
 *       segundos; al llegar avisa y calla. Recarga corta entre preguntas. Nada va al chat.</li>
 * </ul>
 */
public final class MimirHead {
	private static final int OUTLINE_TICKS = 100;
	private static final int MAX_OUTLINES = 24;
	private static final int WHISPER_RADIUS = 20;
	private static final String TAG = MythicRelics.MOD_ID + ".mimir";
	private static final Map<Block, Integer> COLORS = Map.ofEntries(
			Map.entry(Blocks.DIAMOND_ORE, 0x5FF0FF), Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, 0x5FF0FF),
			Map.entry(Blocks.EMERALD_ORE, 0x3FE06A), Map.entry(Blocks.DEEPSLATE_EMERALD_ORE, 0x3FE06A),
			Map.entry(Blocks.ANCIENT_DEBRIS, 0xB0603A), Map.entry(Blocks.GOLD_ORE, 0xFFD23F),
			Map.entry(Blocks.DEEPSLATE_GOLD_ORE, 0xFFD23F), Map.entry(Blocks.NETHER_GOLD_ORE, 0xFFD23F),
			Map.entry(Blocks.LAPIS_ORE, 0x3F5FFF), Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, 0x3F5FFF));

	private record Outline(ServerLevel level, Entity display, long until) {
	}

	private static final List<Outline> OUTLINES = new ArrayList<>();
	/** Mientras se agrega un contorno nuevo (el evento ADD no debe borrarlo: todavía no está en OUTLINES). */
	private static boolean spawning;

	private MimirHead() {
	}

	static void init() {
		// Si el mundo se cerró con contornos puestos, al volver a cargarse se quitan.
		EntityEvent.ADD.register((entity, level) -> !spawning && !level.isClientSide() && entity.entityTags().contains(TAG)
				&& OUTLINES.stream().noneMatch(o -> o.display() == entity) ? EventResult.interruptFalse() : EventResult.pass());
	}

	static void tick(MinecraftServer server) {
		long now = server.overworld().getGameTime();
		Iterator<Outline> it = OUTLINES.iterator();
		while (it.hasNext()) {
			Outline o = it.next();
			if (o.until() <= now || o.display().isRemoved()) {
				o.display().discard();
				it.remove();
			}
		}
	}

	private static boolean valuable(BlockState state) {
		return COLORS.containsKey(state.getBlock()) || state.is(BlockTags.GOLD_ORES);
	}

	/** Marca los minerales valiosos a {@link NorseGods#MIMIR_RADIUS} bloques con un contorno brillante. */
	static void outlineOres(ServerLevel level, ServerPlayer player) {
		BlockPos center = player.blockPosition();
		int r = NorseGods.MIMIR_RADIUS;
		List<BlockPos> found = new ArrayList<>();
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -r, -r), center.offset(r, r, r))) {
			if (valuable(level.getBlockState(pos))) {
				found.add(pos.immutable());
			}
		}
		found.sort(Comparator.comparingDouble(p -> p.distSqr(center)));
		long until = level.getGameTime() + OUTLINE_TICKS;
		for (BlockPos pos : found.subList(0, Math.min(MAX_OUTLINES, found.size()))) {
			BlockState state = level.getBlockState(pos);
			Entity display = outline(level, pos, state, COLORS.getOrDefault(state.getBlock(), 0xFFD23F));
			if (display != null) {
				OUTLINES.add(new Outline(level, display, until));
			}
		}
		if (!found.isEmpty()) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.6f, 0.7f);
		}
	}

	private static Entity outline(ServerLevel level, BlockPos pos, BlockState state, int color) {
		CompoundTag tag = new CompoundTag();
		tag.putString("id", "minecraft:block_display");
		tag.put("block_state", NbtUtils.writeBlockState(state));
		tag.putBoolean("Glowing", true);
		tag.putInt("glow_color_override", color);
		CompoundTag transform = new CompoundTag();
		transform.put("left_rotation", floats(0, 0, 0, 1));
		transform.put("right_rotation", floats(0, 0, 0, 1));
		transform.put("translation", floats(0.02f, 0.02f, 0.02f));
		transform.put("scale", floats(0.96f, 0.96f, 0.96f));
		tag.put("transformation", transform);
		ListTag tags = new ListTag();
		tags.add(StringTag.valueOf(TAG));
		tag.put("Tags", tags);
		Entity display = EntityType.loadEntityRecursive(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag),
				level, EntitySpawnReason.TRIGGERED, e -> {
					e.snapTo(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
					return e;
				});
		if (display == null) {
			return null;
		}
		spawning = true;
		try {
			return level.addFreshEntity(display) ? display : null;
		} finally {
			spawning = false;
		}
	}

	private static ListTag floats(float... values) {
		ListTag list = new ListTag();
		for (float v : values) {
			list.add(FloatTag.valueOf(v));
		}
		return list;
	}

	// ---- Guía: Mímir te lleva al tesoro más valioso cercano ------------------------------------------------------------

	/** Radio en que Mímir busca tesoros (horizontal; la mitad hacia arriba y abajo). */
	public static final int GUIDE_RADIUS = 24;
	/** Cada cuánto se dibuja la estela y se actualiza el aviso sobre la barra. */
	private static final int TRAIL_INTERVAL = 30;
	private static final double ARRIVED = 2.5;
	/** Un cofre sin abrir vale como un diamante. */
	private static final int CHEST_VALUE = 5;

	/** Lo que busca cada jugador con Mímir en la mano. */
	private static final class Guide {
		BlockPos target;
		Component name;
		long nextScan;
	}

	private static final Map<java.util.UUID, Guide> GUIDES = new java.util.HashMap<>();

	/** Cuánto vale cada mineral (más alto, más prioridad; empate: el más cercano). 0 = no es tesoro. */
	private static int value(BlockState state) {
		Block b = state.getBlock();
		if (b == Blocks.ANCIENT_DEBRIS) {
			return 6;
		}
		if (b == Blocks.DIAMOND_ORE || b == Blocks.DEEPSLATE_DIAMOND_ORE) {
			return 5;
		}
		if (b == Blocks.EMERALD_ORE || b == Blocks.DEEPSLATE_EMERALD_ORE) {
			return 3;
		}
		if (state.is(BlockTags.GOLD_ORES)) {
			return 2;
		}
		if (b == Blocks.LAPIS_ORE || b == Blocks.DEEPSLATE_LAPIS_ORE) {
			return 1;
		}
		return 0;
	}

	/** Cuánto dura la guía después de preguntarle. */
	public static final int GUIDE_TICKS = 600;
	/** Recarga entre preguntas. */
	public static final int ASK_COOLDOWN = 100;

	/**
	 * Clic derecho con Mímir en la mano: le preguntas. Busca el tesoro más valioso, te lo dice una sola vez (sobre la
	 * barra), marca los minerales cercanos y te guía con una estela durante {@link #GUIDE_TICKS} ticks. Sin tesoro, lo dice
	 * y no gasta la recarga.
	 */
	static boolean ask(ServerLevel level, ServerPlayer player) {
		Guide g = new Guide();
		scan(level, player, g);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 0.7f, 0.6f);
		if (g.target == null) {
			GUIDES.remove(player.getUUID());
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.mimir_nothing").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC));
			return false;
		}
		g.nextScan = level.getGameTime() + GUIDE_TICKS;   // aquí: hasta cuándo guía
		GUIDES.put(player.getUUID(), g);
		BlockPos here = player.blockPosition();
		player.sendOverlayMessage(Component.translatable("message.mythicrelics.mimir_guide", g.name,
				(int) Math.round(Math.sqrt(g.target.distSqr(here))), where(here, g.target)).withStyle(ChatFormatting.DARK_AQUA));
		outlineOres(level, player);
		trail(level, player, g.target);
		return true;
	}

	/** Cada tick por jugador: mientras dura la guía, la estela cada {@link #TRAIL_INTERVAL} ticks; al llegar, avisa y calla. */
	static void tickGuide(ServerLevel level, ServerPlayer player) {
		Guide g = GUIDES.get(player.getUUID());
		if (g == null || g.target == null) {
			return;
		}
		boolean gone = value(level.getBlockState(g.target)) == 0 && !isChest(level, g.target);
		if (level.getGameTime() > g.nextScan || player.level() != level) {
			GUIDES.remove(player.getUUID());
			return;
		}
		if (gone || player.getEyePosition().distanceTo(Vec3.atCenterOf(g.target)) < ARRIVED) {
			GUIDES.remove(player.getUUID());
			player.sendOverlayMessage(Component.translatable("message.mythicrelics.mimir_arrived").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC));
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8f, 0.8f);
			return;
		}
		if (player.tickCount % TRAIL_INTERVAL == 0) {
			trail(level, player, g.target);
		}
	}

	private static boolean isChest(ServerLevel level, BlockPos pos) {
		return level.getBlockEntity(pos) instanceof RandomizableContainerBlockEntity box && box.getLootTable() != null;
	}

	/** Busca el mejor tesoro: minerales valiosos y cofres sin abrir. */
	private static void scan(ServerLevel level, ServerPlayer player, Guide g) {
		BlockPos center = player.blockPosition();
		int r = GUIDE_RADIUS;
		BlockPos best = null;
		int bestValue = 0;
		double bestDist = Double.MAX_VALUE;
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -r / 2, -r), center.offset(r, r / 2, r))) {
			int v = value(level.getBlockState(pos));
			if (v == 0) {
				continue;
			}
			double d = pos.distSqr(center);
			if (v > bestValue || v == bestValue && d < bestDist) {
				best = pos.immutable();
				bestValue = v;
				bestDist = d;
			}
		}
		for (int cx = -1; cx <= 1; cx++) {
			for (int cz = -1; cz <= 1; cz++) {
				LevelChunk chunk = level.getChunkSource().getChunkNow((center.getX() >> 4) + cx, (center.getZ() >> 4) + cz);
				if (chunk == null) {
					continue;
				}
				for (BlockEntity be : chunk.getBlockEntities().values()) {
					if (be instanceof RandomizableContainerBlockEntity box && box.getLootTable() != null) {
						double d = be.getBlockPos().distSqr(center);
						if (CHEST_VALUE > bestValue || CHEST_VALUE == bestValue && d < bestDist) {
							best = be.getBlockPos().immutable();
							bestValue = CHEST_VALUE;
							bestDist = d;
						}
					}
				}
			}
		}
		g.target = best;
		g.name = best == null ? null : isChest(level, best) ? Component.translatable("message.mythicrelics.mimir_chest_name")
				: level.getBlockState(best).getBlock().getName();
	}

	/** Estela de luz, solo para el que lleva a Mímir: unos 8 bloques desde el pecho hacia el tesoro. */
	private static void trail(ServerLevel level, ServerPlayer player, BlockPos target) {
		Vec3 from = player.position().add(0, 1.2, 0);
		Vec3 dir = Vec3.atCenterOf(target).subtract(from);
		double len = Math.min(8.0, dir.length());
		dir = dir.normalize();
		for (double d = 0.8; d < len; d += 0.35) {
			Vec3 p = from.add(dir.scale(d));
			level.sendParticles(player, ParticleTypes.SOUL_FIRE_FLAME, true, false, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
		}
		Vec3 end = from.add(dir.scale(len));
		level.sendParticles(player, ParticleTypes.END_ROD, true, false, end.x, end.y, end.z, 4, 0.1, 0.1, 0.1, 0.01);
	}

	/** «arriba, al noreste», «abajo, al sur»… */
	private static Component where(BlockPos from, BlockPos to) {
		int dx = to.getX() - from.getX();
		int dz = to.getZ() - from.getZ();
		int dy = to.getY() - from.getY();
		double angle = Math.toDegrees(Math.atan2(-dx, dz));
		String[] dirs = {"south", "southwest", "west", "northwest", "north", "northeast", "east", "southeast"};
		Component dir = Component.translatable("direction.mythicrelics." + dirs[(int) Math.floorMod(Math.round(angle / 45.0), 8)]);
		String level = dy > 3 ? "up" : dy < -3 ? "down" : "level";
		return Component.translatable("message.mythicrelics.mimir_where." + level, dir);
	}
}
