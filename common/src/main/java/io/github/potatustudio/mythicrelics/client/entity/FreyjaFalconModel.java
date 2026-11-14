package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * El halcón del manto de Freyja (geometría en {@link MythicModelLayers#freyjaFalcon}): bate las alas largas y
 * puntiagudas con un aleteo rápido y planeos entre medio, la cola se abre y las patas van recogidas.
 */
public class FreyjaFalconModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("freyja_falcon"), "main");

	private final ModelPart head;
	private final ModelPart wingR;
	private final ModelPart wingL;
	private final ModelPart tail;

	public FreyjaFalconModel(ModelPart root) {
		super(root);
		head = root.getChild("head");
		ModelPart body = root.getChild("body");
		wingR = body.getChild("wing_r");
		wingL = body.getChild("wing_l");
		tail = body.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		return MythicModelLayers.freyjaFalcon();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks;
		// Aleteo rápido que cada tanto se detiene en un planeo (las alas quedan abiertas y apenas se mueven).
		float glide = Mth.clamp(Mth.sin(t * 0.05f) * 2.0f, 0.0f, 1.0f);
		float flap = Mth.sin(t * 0.9f) * 0.8f * (1.0f - glide) + Mth.sin(t * 0.1f) * 0.05f * glide;
		wingR.zRot += flap;
		wingL.zRot -= flap;
		tail.xRot += Mth.sin(t * 0.12f) * 0.08f;
		head.xRot += state.xRot * Mth.DEG_TO_RAD * 0.5f;
	}
}
