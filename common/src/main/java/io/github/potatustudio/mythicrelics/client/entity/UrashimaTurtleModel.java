package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.geometry.UrashimaTurtleGeometry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Tortuga de Urashima (geometría en {@link UrashimaTurtleGeometry}, igual que el visor de diseño): caparazón
 * de escudos con borde ámbar, aletas que reman (más rápido al nadar) y algas que flotan detrás. Se dibuja a 1,7 veces
 * ({@link MythicRenderers.TurtleRenderer}).
 */
public class UrashimaTurtleModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("urashima_turtle"), "main");

	private final ModelPart neck;
	private final ModelPart flipperFr;
	private final ModelPart flipperFl;
	private final ModelPart flipperHr;
	private final ModelPart flipperHl;
	private final ModelPart[] seaweed = new ModelPart[3];

	public UrashimaTurtleModel(ModelPart root) {
		super(root);
		neck = root.getChild("neck");
		flipperFr = root.getChild("flipper_fr");
		flipperFl = root.getChild("flipper_fl");
		flipperHr = root.getChild("flipper_hr");
		flipperHl = root.getChild("flipper_hl");
		ModelPart body = root.getChild("body");
		for (int k = 0; k < 3; k++) {
			seaweed[k] = body.getChild("seaweed" + k);
		}
	}

	public static LayerDefinition createBodyLayer() {
		return UrashimaTurtleGeometry.create();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks / 20.0f;
		float k = 0.3f + Math.min(1.0f, state.walkAnimationSpeed * 3.0f) * 0.7f;
		float a = t * (state.isInWater ? 2.2f : 1.2f);
		flipperFr.yRot += Mth.sin(a) * 0.45f * k;
		flipperFr.zRot += Mth.cos(a) * 0.35f * k;
		flipperFl.yRot -= Mth.sin(a) * 0.45f * k;
		flipperFl.zRot -= Mth.cos(a) * 0.35f * k;
		flipperHr.yRot += Mth.sin(a + 1.6f) * 0.3f * k;
		flipperHl.yRot -= Mth.sin(a + 1.6f) * 0.3f * k;
		neck.yRot = state.yRot * Mth.DEG_TO_RAD * 0.5f;
		neck.xRot += Mth.sin(t * 0.9f) * 0.08f;
		for (int i = 0; i < 3; i++) {
			seaweed[i].xRot += Mth.sin(t * 1.3f + i) * 0.15f;
			seaweed[i].yRot += Mth.sin(t * 0.9f + i) * 0.2f;
		}
	}
}
