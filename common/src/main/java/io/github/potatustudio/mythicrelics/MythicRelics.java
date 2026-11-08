package io.github.potatustudio.mythicrelics;

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
		LOGGER.info("Mythic Relics cargado");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
