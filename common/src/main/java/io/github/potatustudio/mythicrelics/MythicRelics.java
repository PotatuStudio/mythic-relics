package io.github.potatustudio.mythicrelics;

import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.minion.Minions;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import io.github.potatustudio.mythicrelics.registry.ModLoot;
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
		ModItems.init();
		ModLoot.init();
		Minions.init();
		EgyptRelics.init();
		LOGGER.info("Mythic Relics cargado");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
