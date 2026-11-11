package io.github.potatustudio.mythicrelics.neoforge;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.greece.CharonStash;
import io.github.potatustudio.mythicrelics.neoforge.client.MythicRelicsNeoForgeClient;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.common.DropRule;
import top.theillusivec4.curios.api.event.DropRulesEvent;

@Mod(MythicRelics.MOD_ID)
public final class MythicRelicsNeoForge {
	public MythicRelicsNeoForge(IEventBus modBus) {
		WornRelics.install(new CuriosWornRelics());
		MythicRelics.init();
		// Óbolo de Caronte: si pagó el pasaje, los accesorios puestos se quedan puestos.
		NeoForge.EVENT_BUS.addListener((DropRulesEvent event) -> {
			if (CharonStash.paidPassage(event.getEntity())) {
				event.addOverride(stack -> true, DropRule.ALWAYS_KEEP);
			}
		});
		if (FMLEnvironment.getDist() == Dist.CLIENT) {
			MythicRelicsNeoForgeClient.init(modBus);
		}
	}
}
