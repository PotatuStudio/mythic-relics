package io.github.potatustudio.mythicrelics.fabric;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.fabricmc.api.ModInitializer;

public final class MythicRelicsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		MythicRelics.init();
	}
}
