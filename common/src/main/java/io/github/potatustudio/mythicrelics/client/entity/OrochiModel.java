package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Sombra de Yamata no Orochi (geometría en {@link MythicModelLayers#orochiShadow}, igual que el visor de diseño): cuerpo
 * de escamas con musgo y cipreses en el lomo, ocho cuellos que se mecen con cabezas que muerden y ocho colas por el
 * suelo. Se dibuja al doble ({@link ChinaRenderers.OrochiRenderer}).
 */
public class OrochiModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("orochi_shadow"), "main");
	private static final int HEADS = 8;

	private final ModelPart[] necks = new ModelPart[HEADS];
	private final ModelPart[] upper = new ModelPart[HEADS];
	private final ModelPart[] heads = new ModelPart[HEADS];
	private final ModelPart[] jaws = new ModelPart[HEADS];
	private final ModelPart[] tails = new ModelPart[HEADS];

	public OrochiModel(ModelPart root) {
		super(root);
		ModelPart body = root.getChild("body");
		for (int i = 0; i < HEADS; i++) {
			necks[i] = body.getChild("neck" + i);
			upper[i] = necks[i].getChild("neck" + i + "b");
			heads[i] = upper[i].getChild("head" + i);
			jaws[i] = heads[i].getChild("jaw" + i);
			tails[i] = body.getChild("tail" + i);
		}
	}

	public static LayerDefinition createBodyLayer() {
		return MythicModelLayers.orochiShadow();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks / 20.0f;
		for (int i = 0; i < HEADS; i++) {
			necks[i].xRot += Mth.sin(t * 1.1f + i * 0.8f) * 0.12f;
			upper[i].xRot += Mth.sin(t * 1.3f + i * 0.8f - 0.5f) * 0.18f;
			upper[i].yRot += Mth.sin(t * 0.9f + i) * 0.15f;
			heads[i].xRot += Mth.sin(t * 1.3f + i * 0.8f - 1.0f) * 0.1f;
			jaws[i].xRot += Math.max(0.0f, Mth.sin(t * 2.4f + i * 1.7f)) * 0.6f;
			tails[i].yRot += Mth.sin(t * 0.8f + i * 0.9f) * 0.2f;
		}
	}
}
