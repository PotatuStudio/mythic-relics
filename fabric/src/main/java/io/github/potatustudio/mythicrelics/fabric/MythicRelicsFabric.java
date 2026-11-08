package io.github.potatustudio.mythicrelics.fabric;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import net.fabricmc.api.ModInitializer;

public final class MythicRelicsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		WornRelics.install(new TrinketsWornRelics());
		MythicRelics.init();
	}
}
