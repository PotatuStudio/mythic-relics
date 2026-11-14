package io.github.potatustudio.mythicrelics.rome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.potatustudio.mythicrelics.MythicRelics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Candados de la llave de Jano: bloque cerrado -> dueño, guardado con el mundo. Las puertas se guardan por su mitad de
 * abajo y los cofres dobles por sus dos mitades, así da igual qué parte se toque.
 */
public final class JanusLocks extends SavedData {
	private record Entry(ResourceKey<Level> dimension, BlockPos pos, UUID owner) {
		static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
				Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(Entry::dimension),
				BlockPos.CODEC.fieldOf("pos").forGetter(Entry::pos),
				UUIDUtil.CODEC.fieldOf("owner").forGetter(Entry::owner)
		).apply(i, Entry::new));
	}

	private static final Codec<JanusLocks> CODEC = Entry.CODEC.listOf().xmap(JanusLocks::new, JanusLocks::entries);
	private static final SavedDataType<JanusLocks> TYPE = new SavedDataType<>(MythicRelics.id("janus_locks"), JanusLocks::new,
			CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

	private final Map<GlobalPos, UUID> locks = new HashMap<>();

	private JanusLocks() {
	}

	private JanusLocks(List<Entry> list) {
		list.forEach(e -> locks.put(GlobalPos.of(e.dimension(), e.pos()), e.owner()));
	}

	private List<Entry> entries() {
		List<Entry> out = new ArrayList<>();
		locks.forEach((p, o) -> out.add(new Entry(p.dimension(), p.pos(), o)));
		return out;
	}

	private static JanusLocks get(ServerLevel level) {
		return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
	}

	/** Las posiciones que representan ese bloque: la mitad de abajo de una puerta; las dos mitades de un cofre doble. */
	private static List<BlockPos> keys(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		List<BlockPos> out = new ArrayList<>();
		if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
			out.add(pos.below().immutable());
		} else {
			out.add(pos.immutable());
		}
		if (state.getBlock() instanceof ChestBlock && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
			Direction other = ChestBlock.getConnectedDirection(state);
			out.add(pos.relative(other).immutable());
		}
		return out;
	}

	static @Nullable UUID owner(ServerLevel level, BlockPos pos) {
		JanusLocks data = get(level);
		for (BlockPos key : keys(level, pos)) {
			UUID owner = data.locks.get(GlobalPos.of(level.dimension(), key));
			if (owner != null) {
				return owner;
			}
		}
		return null;
	}

	/** ¿Está cerrado y no es de este jugador? */
	static boolean lockedAgainst(ServerLevel level, BlockPos pos, Player player) {
		UUID owner = owner(level, pos);
		return owner != null && !owner.equals(player.getUUID());
	}

	/** ¿Está cerrado? (para los mobs que rompen puertas). */
	public static boolean isLocked(ServerLevel level, BlockPos pos) {
		return owner(level, pos) != null;
	}

	private static final String PADLOCK_TAG = MythicRelics.MOD_ID + ".padlock";

	/** Candado dorado que cuelga de la cara del bloque cerrado: un ítem en exhibición (se guarda con el mundo). */
	private static void hangPadlock(ServerLevel level, Vec3 at, Direction face) {
		CompoundTag tag = new CompoundTag();
		tag.putString("id", "minecraft:item_display");
		CompoundTag item = new CompoundTag();
		item.putString("id", MythicRelics.id("padlock").toString());
		item.putInt("count", 1);
		tag.put("item", item);
		tag.putString("item_display", "fixed");
		CompoundTag transform = new CompoundTag();
		transform.put("left_rotation", floats(0, 0, 0, 1));
		transform.put("right_rotation", floats(0, 0, 0, 1));
		transform.put("translation", floats(0, 0, 0));
		transform.put("scale", floats(0.45f, 0.45f, 0.45f));
		tag.put("transformation", transform);
		ListTag tags = new ListTag();
		tags.add(StringTag.valueOf(PADLOCK_TAG));
		tag.put("Tags", tags);
		double x = at.x + face.getStepX() * 0.04;
		double y = at.y + face.getStepY() * 0.04;
		double z = at.z + face.getStepZ() * 0.04;
		float yaw = face.getAxis().isHorizontal() ? face.toYRot() : 0;
		float pitch = face == Direction.UP ? -90 : face == Direction.DOWN ? 90 : 0;
		Entity display = EntityType.loadEntityRecursive(
				TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag),
				level, EntitySpawnReason.TRIGGERED, e -> {
					e.snapTo(x, y, z, yaw, pitch);
					return e;
				});
		if (display != null) {
			level.addFreshEntity(display);
		}
	}

	private static ListTag floats(float... values) {
		ListTag list = new ListTag();
		for (float v : values) {
			list.add(FloatTag.valueOf(v));
		}
		return list;
	}

	/** Quita los candados que cuelgan de ese bloque (y de su otra mitad). */
	private static void removePadlocks(ServerLevel level, List<BlockPos> keys) {
		for (BlockPos key : keys) {
			for (Display.ItemDisplay d : level.getEntitiesOfClass(Display.ItemDisplay.class,
					new AABB(key).inflate(0.6, 1.6, 0.6), e -> e.entityTags().contains(PADLOCK_TAG))) {
				d.discard();
			}
		}
	}

	static void lock(ServerLevel level, BlockPos pos, Player player, Direction face, Vec3 at) {
		hangPadlock(level, at, face);
		JanusLocks data = get(level);
		for (BlockPos key : keys(level, pos)) {
			data.locks.put(GlobalPos.of(level.dimension(), key), player.getUUID());
		}
		data.setDirty();
	}

	static void unlock(ServerLevel level, BlockPos pos) {
		JanusLocks data = get(level);
		boolean changed = false;
		List<BlockPos> keys = keys(level, pos);
		removePadlocks(level, keys);
		for (BlockPos key : keys) {
			changed |= data.locks.remove(GlobalPos.of(level.dimension(), key)) != null;
		}
		if (changed) {
			data.setDirty();
		}
	}
}
