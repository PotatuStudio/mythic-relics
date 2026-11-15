package io.github.potatustudio.mythicrelics;

import io.github.potatustudio.mythicrelics.china.ChinaGods;
import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.greece.GreeceHeroes;
import io.github.potatustudio.mythicrelics.greece.GreeceLegends;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.norse.NorseGods;
import io.github.potatustudio.mythicrelics.norse.NorseRelics;
import io.github.potatustudio.mythicrelics.norse.NorseSagas;
import io.github.potatustudio.mythicrelics.registry.ModBlocks;
import io.github.potatustudio.mythicrelics.registry.ModComponents;
import io.github.potatustudio.mythicrelics.registry.ModEffects;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.registry.ModLoot;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import io.github.potatustudio.mythicrelics.relic.HeldLight;
import io.github.potatustudio.mythicrelics.relic.SafeLanding;
import io.github.potatustudio.mythicrelics.relic.RelicSets;
import io.github.potatustudio.mythicrelics.relic.ServerState;
import io.github.potatustudio.mythicrelics.relic.TooltipArgs;
import io.github.potatustudio.mythicrelics.rome.RomeRelics;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Punto de entrada común (lo llaman los inicializadores de Fabric y de NeoForge). */
public final class MythicRelics {
	public static final String MOD_ID = "mythicrelics";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private MythicRelics() {
	}

	public static void init() {
		ModComponents.init();
		ModEffects.init();
		ModBlocks.init();
		ModItems.init();
		ModEntities.init();
		ModLoot.init();
		ServerState.init();
		Minions.init();
		RelicCooldowns.init();
		SafeLanding.init();
		HeldLight.init();
		RelicSets.init();
		TooltipArgs.init();
		EgyptRelics.init();
		EgyptGods.init();
		GreeceRelics.init();
		GreeceHeroes.init();
		RomeRelics.init();
		NorseRelics.init();
		NorseGods.init();
		ChinaRelics.init();
		ChinaGods.init();
		GreeceLegends.init();
		NorseSagas.init();
		LOGGER.info("Mythic Relics cargado");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
