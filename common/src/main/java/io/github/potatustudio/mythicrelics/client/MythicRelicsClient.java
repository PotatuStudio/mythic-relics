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
		// Mjölnir y Gungnir en vuelo: el ítem, un poco más grande que un objeto lanzado normal.
		EntityRendererRegistry.register(ModEntities.THROWN_RELIC, context -> new ThrownItemRenderer<>(context, 1.5f, false));
		GreeceClient.init();
	}
}
