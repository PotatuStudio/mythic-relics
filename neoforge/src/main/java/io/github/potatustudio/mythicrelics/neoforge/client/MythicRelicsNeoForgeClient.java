package io.github.potatustudio.mythicrelics.neoforge.client;

import io.github.potatustudio.mythicrelics.client.MythicRelicsClient;
import io.github.potatustudio.mythicrelics.neoforge.client.CuriosRelicRenderer.Part;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/** Cliente de NeoForge: inicialización común y renderizadores de Curios para las reliquias puestas. */
public final class MythicRelicsNeoForgeClient {
	private MythicRelicsNeoForgeClient() {
	}

	public static void init(IEventBus modBus) {
		MythicRelicsClient.init();
		modBus.addListener(FMLClientSetupEvent.class, event -> registerCurioRenderers());
	}

	/** Misma ubicación que assets/mythicrelics/trinkets/*.json en Fabric. */
	private static void registerCurioRenderers() {
		ICurioRenderer.register(ModItems.ANKH.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
		ICurioRenderer.register(ModItems.GOLDEN_SCARAB.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
		ICurioRenderer.register(ModItems.EYE_OF_HORUS.get(), () -> new CuriosRelicRenderer(Part.HEAD, 0f));
		ICurioRenderer.register(ModItems.JACKAL_MASK.get(), () -> new CuriosRelicRenderer(Part.HEAD, 0f));
		// Curios tiene un solo espacio de manos: las vendas son un par y se ven en los dos brazos.
		ICurioRenderer.register(ModItems.MUMMY_WRAPS.get(), () -> new CuriosRelicRenderer(Part.BOTH_ARMS, 0f));
		ICurioRenderer.register(ModItems.JACKAL_RING.get(), () -> new CuriosRelicRenderer(Part.MAIN_ARM, -1f));
		ICurioRenderer.register(ModItems.RA_DISK.get(), () -> new CuriosRelicRenderer(Part.HEAD, 0f));
		ICurioRenderer.register(ModItems.BASTET_AMULET.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
		ICurioRenderer.register(ModItems.SEKHMET_CLAWS.get(), () -> new CuriosRelicRenderer(Part.BOTH_ARMS, 0f));
		ICurioRenderer.register(ModItems.HERMES_SANDALS.get(), () -> new CuriosRelicRenderer(Part.BOTH_LEGS, 0f));
		ICurioRenderer.register(ModItems.HADES_HELM.get(), () -> new CuriosRelicRenderer(Part.HEAD, 0f));
		ICurioRenderer.register(ModItems.ATHENA_AEGIS.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
		ICurioRenderer.register(ModItems.ICARUS_WINGS.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
		ICurioRenderer.register(ModItems.GOLDEN_FLEECE.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
		ICurioRenderer.register(ModItems.CERBERUS_COLLAR.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
		ICurioRenderer.register(ModItems.WOLF_BULLA.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
		ICurioRenderer.register(ModItems.JARNGREIPR.get(), () -> new CuriosRelicRenderer(Part.BOTH_ARMS, 0f));
		ICurioRenderer.register(ModItems.MEGINGJORD.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
		ICurioRenderer.register(ModItems.DRAUPNIR.get(), () -> new CuriosRelicRenderer(Part.MAIN_ARM, -1f));
		ICurioRenderer.register(ModItems.BRISINGAMEN.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
		ICurioRenderer.register(ModItems.ANDVARANAUT.get(), () -> new CuriosRelicRenderer(Part.MAIN_ARM, -1f));
		ICurioRenderer.register(ModItems.WIND_FIRE_WHEELS.get(), () -> new CuriosRelicRenderer(Part.BOTH_LEGS, 0f));
		ICurioRenderer.register(ModItems.FIVE_COLOR_STONE.get(), () -> new CuriosRelicRenderer(Part.BODY, 0f));
	}
}
