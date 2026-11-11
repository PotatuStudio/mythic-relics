package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Cuervo de Odín (geometría en {@link MythicModelLayers#odinRaven}, igual que la página de revisión): vuela con las alas
 * abiertas que se baten, la cola que se abre y la cabeza hacia su dueño.
 */
public class OdinRavenModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("odin_raven"), "main");

	private final ModelPart head;
	private final ModelPart wingR;
	private final ModelPart wingL;
	private final ModelPart tail;

	public OdinRavenModel(ModelPart root) {
		super(root);
		head = root.getChild("head");
		ModelPart body = root.getChild("body");
		wingR = body.getChild("wing_r");
		wingL = body.getChild("wing_l");
		tail = body.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		return MythicModelLayers.odinRaven();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks;
		if (state.walkAnimationSpeed > 0.5f) {
			// En vuelo: alas abiertas que se baten.
			float flap = Mth.sin(t * 0.45f) * 0.55f;
			wingR.zRot += flap;
			wingL.zRot -= flap;
		} else {
			// Posado: alas plegadas a los costados.
			wingR.zRot -= 1.45f;
			wingL.zRot += 1.45f;
		}
		tail.xRot += Mth.sin(t * 0.1f) * 0.1f;
		head.xRot += state.xRot * Mth.DEG_TO_RAD * 0.5f;
	}
}
