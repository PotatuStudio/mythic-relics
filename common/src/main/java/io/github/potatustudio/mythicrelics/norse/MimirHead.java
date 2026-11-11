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
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.TagValueInput;

/**
 * Cabeza de Mímir en la mano secundaria:
 * <ul>
 *   <li>Cada {@link NorseGods#MIMIR_INTERVAL} ticks, los minerales valiosos cercanos se marcan unos segundos con un
 *       contorno brillante (una copia del bloque que brilla y se ve a través de las paredes), del color del mineral.</li>
 *   <li>Cada {@link #WHISPER_INTERVAL} ticks, Mímir susurra una pista: el mineral valioso más cercano y su dirección,
 *       un cofre sin abrir o un generador de monstruos cerca, o cuántos hostiles te rodean.</li>
 * </ul>
 */
public final class MimirHead {
	public static final int WHISPER_INTERVAL = 600;
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

	private MimirHead() {
	}

	static void init() {
		// Si el mundo se cerró con contornos puestos, al volver a cargarse se quitan.
		EntityEvent.ADD.register((entity, level) -> !level.isClientSide() && entity.entityTags().contains(TAG)
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
		if (display != null) {
			level.addFreshEntity(display);
		}
		return display;
	}

	private static ListTag floats(float... values) {
		ListTag list = new ListTag();
		for (float v : values) {
			list.add(FloatTag.valueOf(v));
		}
		return list;
	}

	/** Mímir susurra una pista útil (si tiene algo que decir). */
	static void whisper(ServerLevel level, ServerPlayer player) {
		BlockPos center = player.blockPosition();
		List<Component> hints = new ArrayList<>();
		// El mineral valioso más cercano.
		BlockPos ore = null;
		int r = WHISPER_RADIUS;
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -r / 2, -r), center.offset(r, r / 2, r))) {
			if (valuable(level.getBlockState(pos)) && (ore == null || pos.distSqr(center) < ore.distSqr(center))) {
				ore = pos.immutable();
			}
		}
		if (ore != null) {
			hints.add(Component.translatable("message.mythicrelics.mimir_ore", level.getBlockState(ore).getBlock().getName(),
					(int) Math.sqrt(ore.distSqr(center)), where(center, ore)));
		}
		// Cofres sin abrir y generadores de monstruos en los chunks cercanos.
		BlockPos chest = null;
		BlockPos spawner = null;
		for (int cx = -1; cx <= 1; cx++) {
			for (int cz = -1; cz <= 1; cz++) {
				LevelChunk chunk = level.getChunkSource().getChunkNow((center.getX() >> 4) + cx, (center.getZ() >> 4) + cz);
				if (chunk == null) {
					continue;
				}
				for (BlockEntity be : chunk.getBlockEntities().values()) {
					BlockPos p = be.getBlockPos();
					if (be instanceof RandomizableContainerBlockEntity box && box.getLootTable() != null
							&& (chest == null || p.distSqr(center) < chest.distSqr(center))) {
						chest = p;
					} else if (be instanceof SpawnerBlockEntity && (spawner == null || p.distSqr(center) < spawner.distSqr(center))) {
						spawner = p;
					}
				}
			}
		}
		if (chest != null) {
			hints.add(Component.translatable("message.mythicrelics.mimir_chest", (int) Math.sqrt(chest.distSqr(center)), where(center, chest)));
		}
		if (spawner != null) {
			hints.add(Component.translatable("message.mythicrelics.mimir_spawner", (int) Math.sqrt(spawner.distSqr(center)),
					where(center, spawner)));
		}
		int hostiles = level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, player.getBoundingBox().inflate(24),
				e -> e instanceof Enemy && e.isAlive()).size();
		if (hostiles >= 4) {
			hints.add(Component.translatable("message.mythicrelics.mimir_danger", hostiles));
		}
		if (hints.isEmpty()) {
			return;
		}
		Component hint = hints.get(level.getRandom().nextInt(hints.size()));
		player.sendSystemMessage(Component.translatable("message.mythicrelics.mimir_whisper", hint)
				.withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC));
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 0.5f, 0.6f);
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
