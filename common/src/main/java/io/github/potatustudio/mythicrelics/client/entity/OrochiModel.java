package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.geometry.OrochiShadowGeometry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Sombra de Yamata no Orochi (geometría en {@link OrochiShadowGeometry}, igual que el visor de diseño): cuerpo
 * de escamas con musgo y cipreses en el lomo, ocho cuellos que se mecen con cabezas que muerden y ocho colas por el
 * suelo. Se dibuja al doble ({@link MythicRenderers.OrochiRenderer}).
 */
public class OrochiModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("orochi_shadow"), "main");
	private static final int HEADS = 8;
	/** Orden en que entran las cabezas a morder: repartidas alrededor del cuerpo aunque haya pocos hostiles. */
	private static final int[] BITE_ORDER = {0, 4, 2, 6, 1, 5, 3, 7};
	/** Duración de una mordida en ticks: se echa atrás, se lanza con la boca abierta y cierra de golpe. */
	private static final float BITE_TICKS = 12.0f;

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
		return OrochiShadowGeometry.create();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks / 20.0f;
		float[] strike = new float[HEADS];
		if (state instanceof MythicRenderers.OrochiRenderer.State s) {
			for (int k = 0; k < Math.min(s.bitingHeads, HEADS); k++) {
				// cada cabeza entra un tick después de la anterior, para que no se vea como un solo bloque
				strike[BITE_ORDER[k]] = (s.sinceBite - k * 0.7f) / BITE_TICKS;
			}
		}
		for (int i = 0; i < HEADS; i++) {
			necks[i].xRot += Mth.sin(t * 1.1f + i * 0.8f) * 0.12f;
			upper[i].xRot += Mth.sin(t * 1.3f + i * 0.8f - 0.5f) * 0.18f;
			upper[i].yRot += Mth.sin(t * 0.9f + i) * 0.15f;
			heads[i].xRot += Mth.sin(t * 1.3f + i * 0.8f - 1.0f) * 0.1f;
			float p = strike[i];
			if (p > 0.0f && p < 1.0f) {
				// 0–0,35: se echa atrás abriendo la boca; 0,35–0,6: se lanza; 0,6–1: cierra y vuelve
				float back = p < 0.35f ? p / 0.35f : p < 0.6f ? 1.0f - (p - 0.35f) / 0.25f * 2.0f : -1.0f + (p - 0.6f) / 0.4f;
				upper[i].xRot -= back * 0.35f;
				heads[i].xRot -= back * 0.2f;
				float open = p < 0.45f ? p / 0.45f : p < 0.6f ? 1.0f : Math.max(0.0f, 1.0f - (p - 0.6f) / 0.08f);
				jaws[i].xRot += open * 0.9f;
			}
			tails[i].yRot += Mth.sin(t * 0.8f + i * 0.9f) * 0.2f;
		}
	}
}
