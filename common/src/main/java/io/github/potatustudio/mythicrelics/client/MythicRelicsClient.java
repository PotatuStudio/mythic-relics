package io.github.potatustudio.mythicrelics.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import io.github.potatustudio.mythicrelics.client.entity.CelestialDogModel;
import io.github.potatustudio.mythicrelics.client.entity.CerberusModel;
import io.github.potatustudio.mythicrelics.client.entity.ChinaRenderers;
import io.github.potatustudio.mythicrelics.client.entity.LongDragonModel;
import io.github.potatustudio.mythicrelics.client.entity.MonkeyWarriorModel;
import io.github.potatustudio.mythicrelics.client.entity.QilinModel;
import io.github.potatustudio.mythicrelics.client.entity.CerberusRenderer;
import io.github.potatustudio.mythicrelics.client.entity.JackalModel;
import io.github.potatustudio.mythicrelics.client.entity.KitsuneModel;
import io.github.potatustudio.mythicrelics.client.entity.OrochiModel;
import io.github.potatustudio.mythicrelics.client.entity.SleipnirModel;
import io.github.potatustudio.mythicrelics.client.entity.UrashimaTurtleModel;
import io.github.potatustudio.mythicrelics.client.entity.SleipnirRenderer;
import io.github.potatustudio.mythicrelics.client.entity.JackalRenderer;
import io.github.potatustudio.mythicrelics.client.entity.ScarabModel;
import io.github.potatustudio.mythicrelics.client.entity.ScarabRenderer;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

/** Inicialización común del cliente (la llaman ambos loaders): modelos y renderizadores de las entidades. */
public final class MythicRelicsClient {
	private MythicRelicsClient() {
	}

	public static void init() {
		EntityModelLayerRegistry.register(ScarabModel.LAYER, ScarabModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.SCARAB, ScarabRenderer::new);
		EntityModelLayerRegistry.register(JackalModel.LAYER, JackalModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.JACKAL, JackalRenderer::new);
		EntityModelLayerRegistry.register(CerberusModel.LAYER, CerberusModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.CERBERUS, CerberusRenderer::new);
		EntityModelLayerRegistry.register(SleipnirModel.LAYER, SleipnirModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.SLEIPNIR, SleipnirRenderer::new);
		EntityModelLayerRegistry.register(MonkeyWarriorModel.LAYER, MonkeyWarriorModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.MONKEY_WARRIOR, ChinaRenderers.Monkey::new);
		EntityModelLayerRegistry.register(LongDragonModel.LAYER, LongDragonModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.LONG_DRAGON, ChinaRenderers.Dragon::new);
		EntityModelLayerRegistry.register(CelestialDogModel.LAYER, CelestialDogModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.CELESTIAL_DOG, ChinaRenderers.Dog::new);
		EntityModelLayerRegistry.register(QilinModel.LAYER, QilinModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.QILIN, ChinaRenderers.QilinRenderer::new);
		EntityModelLayerRegistry.register(KitsuneModel.LAYER, KitsuneModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.KITSUNE, ChinaRenderers.KitsuneRenderer::new);
		EntityModelLayerRegistry.register(UrashimaTurtleModel.LAYER, UrashimaTurtleModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.URASHIMA_TURTLE, ChinaRenderers.TurtleRenderer::new);
		EntityModelLayerRegistry.register(OrochiModel.LAYER, OrochiModel::createBodyLayer);
		EntityModelLayerRegistry.register(io.github.potatustudio.mythicrelics.client.entity.OdinRavenModel.LAYER,
				io.github.potatustudio.mythicrelics.client.entity.OdinRavenModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.ODIN_RAVEN, ChinaRenderers.RavenRenderer::new);
		// Skíðblaðnir: el renderizador de botes vanilla con el modelo y la textura propios (textures/entity/boat/skidbladnir.png).
		net.minecraft.client.model.geom.ModelLayerLocation ship = new net.minecraft.client.model.geom.ModelLayerLocation(
				io.github.potatustudio.mythicrelics.MythicRelics.id("boat/skidbladnir"), "main");
		EntityModelLayerRegistry.register(ship, io.github.potatustudio.mythicrelics.client.entity.MythicModelLayers::skidbladnir);
		EntityRendererRegistry.register(ModEntities.SKIDBLADNIR_BOAT, context -> new net.minecraft.client.renderer.entity.BoatRenderer(context, ship));
		EntityRendererRegistry.register(ModEntities.OROCHI_SHADOW, ChinaRenderers.OrochiRenderer::new);
		// Mjölnir y Gungnir en vuelo: el ítem, un poco más grande que un objeto lanzado normal.
		EntityRendererRegistry.register(ModEntities.THROWN_RELIC, io.github.potatustudio.mythicrelics.client.entity.ThrownRelicRenderer::new);
		// Grecia 3 y nórdicos 3.
		EntityModelLayerRegistry.register(io.github.potatustudio.mythicrelics.client.entity.PegasusModel.LAYER,
				io.github.potatustudio.mythicrelics.client.entity.PegasusModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.PEGASUS, ChinaRenderers.PegasusRenderer::new);
		EntityModelLayerRegistry.register(io.github.potatustudio.mythicrelics.client.entity.TalosModel.LAYER,
				io.github.potatustudio.mythicrelics.client.entity.TalosModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.TALOS, ChinaRenderers.TalosRenderer::new);
		EntityModelLayerRegistry.register(io.github.potatustudio.mythicrelics.client.entity.FreyjaFalconModel.LAYER,
				io.github.potatustudio.mythicrelics.client.entity.FreyjaFalconModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.FREYJA_FALCON, ChinaRenderers.FalconRenderer::new);
		// Armas en vuelo: modelo 3D propio (el ícono queda para el inventario y la mano).
		EntityModelLayerRegistry.register(io.github.potatustudio.mythicrelics.client.entity.WeaponModel.FREYR_SWORD,
				io.github.potatustudio.mythicrelics.client.entity.MythicModelLayers::freyrSword);
		EntityModelLayerRegistry.register(io.github.potatustudio.mythicrelics.client.entity.WeaponModel.MJOLNIR,
				io.github.potatustudio.mythicrelics.client.entity.MythicModelLayers::mjolnir);
		EntityModelLayerRegistry.register(io.github.potatustudio.mythicrelics.client.entity.WeaponModel.GUNGNIR,
				io.github.potatustudio.mythicrelics.client.entity.MythicModelLayers::gungnir);
		EntityModelLayerRegistry.register(io.github.potatustudio.mythicrelics.client.entity.WeaponModel.MISTLETOE_DART,
				io.github.potatustudio.mythicrelics.client.entity.MythicModelLayers::mistletoeDart);
		EntityRendererRegistry.register(ModEntities.FREYR_SWORD, io.github.potatustudio.mythicrelics.client.entity.FreyrSwordRenderer::new);
		GreeceClient.init();
		ChargeFeedback.init();
		RelicTooltips.init();
		PetrifiedClient.init();
		EntityRendererRegistry.register(ModEntities.STONE_STATUE, io.github.potatustudio.mythicrelics.client.entity.StoneStatueRenderer::new);
	}
}
