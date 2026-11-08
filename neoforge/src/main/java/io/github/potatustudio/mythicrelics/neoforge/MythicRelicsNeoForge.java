package io.github.potatustudio.mythicrelics.neoforge;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.neoforge.client.MythicRelicsNeoForgeClient;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(MythicRelics.MOD_ID)
public final class MythicRelicsNeoForge {
	public MythicRelicsNeoForge(IEventBus modBus) {
		WornRelics.install(new CuriosWornRelics());
		MythicRelics.init();
		if (FMLEnvironment.getDist() == Dist.CLIENT) {
			MythicRelicsNeoForgeClient.init(modBus);
		}
	}
}
