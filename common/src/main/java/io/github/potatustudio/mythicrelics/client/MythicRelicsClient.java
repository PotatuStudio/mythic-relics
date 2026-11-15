package io.github.potatustudio.mythicrelics.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.CerberusModel;
import io.github.potatustudio.mythicrelics.client.entity.FreyjaFalconModel;
import io.github.potatustudio.mythicrelics.client.entity.FreyrSwordRenderer;
import io.github.potatustudio.mythicrelics.client.entity.MythicRenderers;
import io.github.potatustudio.mythicrelics.client.entity.LongDragonModel;
import io.github.potatustudio.mythicrelics.client.entity.MonkeyWarriorModel;
import io.github.potatustudio.mythicrelics.client.entity.OdinRavenModel;
import io.github.potatustudio.mythicrelics.client.entity.PegasusModel;
import io.github.potatustudio.mythicrelics.client.entity.CerberusRenderer;
import io.github.potatustudio.mythicrelics.client.entity.JackalModel;
import io.github.potatustudio.mythicrelics.client.entity.SleipnirModel;
import io.github.potatustudio.mythicrelics.client.entity.StoneStatueRenderer;
import io.github.potatustudio.mythicrelics.client.entity.TalosModel;
import io.github.potatustudio.mythicrelics.client.entity.ThrownRelicRenderer;
import io.github.potatustudio.mythicrelics.client.entity.SleipnirRenderer;
import io.github.potatustudio.mythicrelics.client.entity.JackalRenderer;
import io.github.potatustudio.mythicrelics.client.entity.ScarabModel;
import io.github.potatustudio.mythicrelics.client.entity.ScarabRenderer;
import io.github.potatustudio.mythicrelics.client.entity.WeaponModel;
import io.github.potatustudio.mythicrelics.client.entity.geometry.FreyrSwordGeometry;
import io.github.potatustudio.mythicrelics.client.entity.geometry.GungnirGeometry;
import io.github.potatustudio.mythicrelics.client.entity.geometry.MistletoeDartGeometry;
import io.github.potatustudio.mythicrelics.client.entity.geometry.MjolnirGeometry;
import io.github.potatustudio.mythicrelics.client.entity.geometry.SkidbladnirGeometry;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;

/** Inicialización común del cliente (la llaman ambos loaders): modelos y renderizadores de las entidades. */
public final class MythicRelicsClient {
	private MythicRelicsClient() {
	}

	public static void init() {
		HeldLightClient.init();
		EntityModelLayerRegistry.register(ScarabModel.LAYER, ScarabModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.SCARAB, ScarabRenderer::new);
		EntityModelLayerRegistry.register(JackalModel.LAYER, JackalModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.JACKAL, JackalRenderer::new);
		EntityModelLayerRegistry.register(CerberusModel.LAYER, CerberusModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.CERBERUS, CerberusRenderer::new);
		EntityModelLayerRegistry.register(SleipnirModel.LAYER, SleipnirModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.SLEIPNIR, SleipnirRenderer::new);
		EntityModelLayerRegistry.register(MonkeyWarriorModel.LAYER, MonkeyWarriorModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.MONKEY_WARRIOR, MythicRenderers.Monkey::new);
		EntityModelLayerRegistry.register(LongDragonModel.LAYER, LongDragonModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.LONG_DRAGON, MythicRenderers.Dragon::new);
		EntityModelLayerRegistry.register(OdinRavenModel.LAYER,
				OdinRavenModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.ODIN_RAVEN, MythicRenderers.RavenRenderer::new);
		// Skíðblaðnir: el renderizador de botes vanilla con el modelo y la textura propios (textures/entity/boat/skidbladnir.png).
		ModelLayerLocation ship = new ModelLayerLocation(
				MythicRelics.id("boat/skidbladnir"), "main");
		EntityModelLayerRegistry.register(ship, SkidbladnirGeometry::create);
		EntityRendererRegistry.register(ModEntities.SKIDBLADNIR_BOAT, context -> new BoatRenderer(context, ship));
		// Mjölnir y Gungnir en vuelo: el ítem, un poco más grande que un objeto lanzado normal.
		EntityRendererRegistry.register(ModEntities.THROWN_RELIC, ThrownRelicRenderer::new);
		// Grecia 3 y nórdicos 3.
		EntityModelLayerRegistry.register(PegasusModel.LAYER,
				PegasusModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.PEGASUS, MythicRenderers.PegasusRenderer::new);
		EntityModelLayerRegistry.register(TalosModel.LAYER,
				TalosModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.TALOS, MythicRenderers.TalosRenderer::new);
		EntityModelLayerRegistry.register(FreyjaFalconModel.LAYER,
				FreyjaFalconModel::createBodyLayer);
		// Armas en vuelo: modelo 3D propio (el ícono queda para el inventario y la mano).
		EntityModelLayerRegistry.register(WeaponModel.FREYR_SWORD,
				FreyrSwordGeometry::create);
		EntityModelLayerRegistry.register(WeaponModel.MJOLNIR,
				MjolnirGeometry::create);
		EntityModelLayerRegistry.register(WeaponModel.GUNGNIR,
				GungnirGeometry::create);
		EntityModelLayerRegistry.register(WeaponModel.MISTLETOE_DART,
				MistletoeDartGeometry::create);
		EntityRendererRegistry.register(ModEntities.FREYR_SWORD, FreyrSwordRenderer::new);
		RelicClientTicks.init();
		ChargeFeedback.init();
		RelicTooltips.init();
		PetrifiedClient.init();
		EntityRendererRegistry.register(ModEntities.STONE_STATUE, StoneStatueRenderer::new);
	}
}
