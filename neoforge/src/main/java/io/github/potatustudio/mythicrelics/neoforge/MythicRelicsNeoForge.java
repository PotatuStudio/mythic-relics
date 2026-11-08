package io.github.potatustudio.mythicrelics.neoforge;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.MythicRelicsClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(MythicRelics.MOD_ID)
public final class MythicRelicsNeoForge {
	public MythicRelicsNeoForge() {
		MythicRelics.init();
		if (FMLEnvironment.getDist() == Dist.CLIENT) {
			MythicRelicsClient.init();
		}
	}
}
