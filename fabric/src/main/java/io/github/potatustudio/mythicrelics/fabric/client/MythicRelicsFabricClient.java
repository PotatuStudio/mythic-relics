package io.github.potatustudio.mythicrelics.fabric.client;

import io.github.potatustudio.mythicrelics.client.MythicRelicsClient;
import net.fabricmc.api.ClientModInitializer;

public final class MythicRelicsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MythicRelicsClient.init();
	}
}
