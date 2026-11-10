package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * El Perro Celestial (geometría en {@link ChinaModelLayers#celestialDog}, igual que el visor de diseño): lebrel negro de
 * pecho hondo, collar de oro con campanita y ojos ámbar que brillan. Corre con las patas en diagonal y mueve la cola.
 */
public class CelestialDogModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("celestial_dog"), "main");

	private final ModelPart head;
	private final ModelPart legFr;
	private final ModelPart legFl;
	private final ModelPart legHr;
	private final ModelPart legHl;
	private final ModelPart tail;

	public CelestialDogModel(ModelPart root) {
		super(root);
		head = root.getChild("head");
		legFr = root.getChild("leg_fr");
		legFl = root.getChild("leg_fl");
		legHr = root.getChild("leg_hr");
		legHl = root.getChild("leg_hl");
		tail = root.getChild("chest").getChild("waist").getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		return ChinaModelLayers.celestialDog();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float a = state.walkAnimationPos * 0.6662f;
		float w = 1.3f * Math.min(1.0f, state.walkAnimationSpeed);
		legFr.xRot += Mth.cos(a) * w;
		legHl.xRot += Mth.cos(a) * w;
		legFl.xRot -= Mth.cos(a) * w;
		legHr.xRot -= Mth.cos(a) * w;
		head.yRot = state.yRot * Mth.DEG_TO_RAD;
		head.xRot += state.xRot * Mth.DEG_TO_RAD;
		tail.yRot += Mth.sin(state.ageInTicks * 0.3f) * 0.35f;
	}
}
