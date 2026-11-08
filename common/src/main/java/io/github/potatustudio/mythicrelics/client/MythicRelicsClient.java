package io.github.potatustudio.mythicrelics.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import io.github.potatustudio.mythicrelics.client.entity.ScarabModel;
import io.github.potatustudio.mythicrelics.client.entity.ScarabRenderer;
import io.github.potatustudio.mythicrelics.registry.ModEntities;

/** Inicialización común del cliente (la llaman ambos loaders): modelos y renderizadores de las entidades. */
public final class MythicRelicsClient {
	private MythicRelicsClient() {
	}

	public static void init() {
		EntityModelLayerRegistry.register(ScarabModel.LAYER, ScarabModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.SCARAB, ScarabRenderer::new);
	}
}
