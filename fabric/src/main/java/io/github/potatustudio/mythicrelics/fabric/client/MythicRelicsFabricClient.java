package io.github.potatustudio.mythicrelics.fabric.client;

import io.github.potatustudio.mythicrelics.client.FreyrDrawn;
import io.github.potatustudio.mythicrelics.client.IcarusPose;
import io.github.potatustudio.mythicrelics.client.MythicRelicsClient;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;

public final class MythicRelicsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MythicRelicsClient.init();
		ConditionalItemModelProperties.ID_MAPPER.put(FreyrDrawn.ID, FreyrDrawn.MAP_CODEC);
		RangeSelectItemModelProperties.ID_MAPPER.put(IcarusPose.ID, IcarusPose.MAP_CODEC);
	}
}
