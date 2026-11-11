package io.github.potatustudio.mythicrelics.fabric;

import eu.pb4.trinkets.api.TrinketDropRule;
import eu.pb4.trinkets.api.event.TrinketDropCallback;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.greece.CharonStash;
import io.github.potatustudio.mythicrelics.relic.WornRelics;
import net.fabricmc.api.ModInitializer;

public final class MythicRelicsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		WornRelics.install(new TrinketsWornRelics());
		MythicRelics.init();
		// Óbolo de Caronte: si pagó el pasaje, los accesorios puestos se quedan puestos.
		TrinketDropCallback.EVENT.register((rule, stack, slot, entity) -> CharonStash.paidPassage(entity) ? TrinketDropRule.KEEP : rule);
	}
}
