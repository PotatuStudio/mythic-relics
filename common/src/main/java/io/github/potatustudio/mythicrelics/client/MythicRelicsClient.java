package io.github.potatustudio.mythicrelics.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.CelestialDogModel;
import io.github.potatustudio.mythicrelics.client.entity.CerberusModel;
import io.github.potatustudio.mythicrelics.client.entity.FreyjaFalconModel;
import io.github.potatustudio.mythicrelics.client.entity.FreyrSwordRenderer;
import io.github.potatustudio.mythicrelics.client.entity.MythicModelLayers;
import io.github.potatustudio.mythicrelics.client.entity.MythicRenderers;
import io.github.potatustudio.mythicrelics.client.entity.LongDragonModel;
import io.github.potatustudio.mythicrelics.client.entity.MonkeyWarriorModel;
import io.github.potatustudio.mythicrelics.client.entity.OdinRavenModel;
import io.github.potatustudio.mythicrelics.client.entity.PegasusModel;
import io.github.potatustudio.mythicrelics.client.entity.QilinModel;
import io.github.potatustudio.mythicrelics.client.entity.CerberusRenderer;
import io.github.potatustudio.mythicrelics.client.entity.JackalModel;
import io.github.potatustudio.mythicrelics.client.entity.KitsuneModel;
import io.github.potatustudio.mythicrelics.client.entity.OrochiModel;
import io.github.potatustudio.mythicrelics.client.entity.SleipnirModel;
import io.github.potatustudio.mythicrelics.client.entity.StoneStatueRenderer;
import io.github.potatustudio.mythicrelics.client.entity.TalosModel;
import io.github.potatustudio.mythicrelics.client.entity.ThrownRelicRenderer;
import io.github.potatustudio.mythicrelics.client.entity.UrashimaTurtleModel;
import io.github.potatustudio.mythicrelics.client.entity.SleipnirRenderer;
import io.github.potatustudio.mythicrelics.client.entity.JackalRenderer;
import io.github.potatustudio.mythicrelics.client.entity.ScarabModel;
import io.github.potatustudio.mythicrelics.client.entity.ScarabRenderer;
import io.github.potatustudio.mythicrelics.client.entity.WeaponModel;
import io.github.potatustudio.mythicrelics.registry.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;

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
		EntityRendererRegistry.register(ModEntities.MONKEY_WARRIOR, MythicRenderers.Monkey::new);
		EntityModelLayerRegistry.register(LongDragonModel.LAYER, LongDragonModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.LONG_DRAGON, MythicRenderers.Dragon::new);
		EntityModelLayerRegistry.register(CelestialDogModel.LAYER, CelestialDogModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.CELESTIAL_DOG, MythicRenderers.Dog::new);
		EntityModelLayerRegistry.register(QilinModel.LAYER, QilinModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.QILIN, MythicRenderers.QilinRenderer::new);
		EntityModelLayerRegistry.register(KitsuneModel.LAYER, KitsuneModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.KITSUNE, MythicRenderers.KitsuneRenderer::new);
		EntityModelLayerRegistry.register(UrashimaTurtleModel.LAYER, UrashimaTurtleModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.URASHIMA_TURTLE, MythicRenderers.TurtleRenderer::new);
		EntityModelLayerRegistry.register(OrochiModel.LAYER, OrochiModel::createBodyLayer);
		EntityModelLayerRegistry.register(OdinRavenModel.LAYER,
				OdinRavenModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.ODIN_RAVEN, MythicRenderers.RavenRenderer::new);
		// Skíðblaðnir: el renderizador de botes vanilla con el modelo y la textura propios (textures/entity/boat/skidbladnir.png).
		ModelLayerLocation ship = new ModelLayerLocation(
				MythicRelics.id("boat/skidbladnir"), "main");
		EntityModelLayerRegistry.register(ship, MythicModelLayers::skidbladnir);
		EntityRendererRegistry.register(ModEntities.SKIDBLADNIR_BOAT, context -> new BoatRenderer(context, ship));
		EntityRendererRegistry.register(ModEntities.OROCHI_SHADOW, MythicRenderers.OrochiRenderer::new);
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
				MythicModelLayers::freyrSword);
		EntityModelLayerRegistry.register(WeaponModel.MJOLNIR,
				MythicModelLayers::mjolnir);
		EntityModelLayerRegistry.register(WeaponModel.GUNGNIR,
				MythicModelLayers::gungnir);
		EntityModelLayerRegistry.register(WeaponModel.MISTLETOE_DART,
				MythicModelLayers::mistletoeDart);
		EntityRendererRegistry.register(ModEntities.FREYR_SWORD, FreyrSwordRenderer::new);
		RelicClientTicks.init();
		ChargeFeedback.init();
		RelicTooltips.init();
		PetrifiedClient.init();
		EntityRendererRegistry.register(ModEntities.STONE_STATUE, StoneStatueRenderer::new);
	}
}
