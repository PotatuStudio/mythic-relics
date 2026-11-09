package io.github.potatustudio.mythicrelics.norse;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.potatustudio.mythicrelics.MythicRelics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * La cuenta de noches de Draupnir por jugador, guardada con el mundo: cuántos amaneceres llevó el anillo puesto y el
 * último día visto. La cuenta no se pierde al quitárselo; solo cuentan los amaneceres con el anillo puesto.
 */
public final class DraupnirNights extends SavedData {
	private record Entry(UUID player, int nights, long lastDay) {
		static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
				UUIDUtil.CODEC.fieldOf("player").forGetter(Entry::player),
				Codec.INT.fieldOf("nights").forGetter(Entry::nights),
				Codec.LONG.fieldOf("last_day").forGetter(Entry::lastDay)
		).apply(i, Entry::new));
	}

	private static final Codec<DraupnirNights> CODEC = Entry.CODEC.listOf().xmap(DraupnirNights::new, DraupnirNights::entries);
	private static final SavedDataType<DraupnirNights> TYPE = new SavedDataType<>(MythicRelics.id("draupnir_nights"),
			DraupnirNights::new, CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

	private final Map<UUID, Entry> byPlayer = new HashMap<>();

	private DraupnirNights() {
	}

	private DraupnirNights(List<Entry> list) {
		list.forEach(e -> byPlayer.put(e.player(), e));
	}

	private List<Entry> entries() {
		return new ArrayList<>(byPlayer.values());
	}

	/** Avanza la cuenta si amaneció; devuelve true cuando llega a la novena noche (y la reinicia). */
	static boolean dawn(ServerPlayer player, long day, boolean wearing) {
		DraupnirNights data = player.level().getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
		Entry e = data.byPlayer.get(player.getUUID());
		if (e == null) {
			data.byPlayer.put(player.getUUID(), new Entry(player.getUUID(), 0, day));
			data.setDirty();
			return false;
		}
		if (day <= e.lastDay()) {
			return false;
		}
		int nights = e.nights() + (wearing ? 1 : 0);
		boolean drip = nights >= NorseRelics.DRAUPNIR_NIGHTS;
		data.byPlayer.put(player.getUUID(), new Entry(player.getUUID(), drip ? 0 : nights, day));
		data.setDirty();
		return drip;
	}
}
