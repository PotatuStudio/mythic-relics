package io.github.potatustudio.mythicrelics.japan;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.potatustudio.mythicrelics.MythicRelics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/** Dónde hay muñecas de zashiki-warashi colocadas (por dimensión), guardado con el mundo. */
public final class ZashikiWarashis extends SavedData {
	private record Entry(ResourceKey<Level> dimension, BlockPos pos) {
		static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
				Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(Entry::dimension),
				BlockPos.CODEC.fieldOf("pos").forGetter(Entry::pos)
		).apply(i, Entry::new));
	}

	private static final Codec<ZashikiWarashis> CODEC = Entry.CODEC.listOf().xmap(ZashikiWarashis::new, ZashikiWarashis::entries);
	private static final SavedDataType<ZashikiWarashis> TYPE = new SavedDataType<>(MythicRelics.id("zashiki_warashis"),
			ZashikiWarashis::new, CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

	private final Set<GlobalPos> placed = new HashSet<>();

	private ZashikiWarashis() {
	}

	private ZashikiWarashis(List<Entry> list) {
		list.forEach(e -> placed.add(GlobalPos.of(e.dimension(), e.pos())));
	}

	private List<Entry> entries() {
		List<Entry> out = new ArrayList<>();
		placed.forEach(p -> out.add(new Entry(p.dimension(), p.pos())));
		return out;
	}

	private static ZashikiWarashis get(ServerLevel level) {
		return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
	}

	static void add(ServerLevel level, BlockPos pos) {
		ZashikiWarashis data = get(level);
		if (data.placed.add(GlobalPos.of(level.dimension(), pos.immutable()))) {
			data.setDirty();
		}
	}

	static void remove(ServerLevel level, BlockPos pos) {
		ZashikiWarashis data = get(level);
		if (data.placed.remove(GlobalPos.of(level.dimension(), pos))) {
			data.setDirty();
		}
	}

	/** ¿Hay una muñeca a {@code radius} bloques o menos de esa posición? */
	static boolean near(ServerLevel level, BlockPos pos, int radius) {
		double r2 = (double) radius * radius;
		for (GlobalPos p : get(level).placed) {
			if (p.dimension() == level.dimension() && p.pos().distSqr(pos) <= r2) {
				return true;
			}
		}
		return false;
	}
}
