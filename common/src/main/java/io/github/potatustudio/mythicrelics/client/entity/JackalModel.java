package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.client.entity.geometry.JackalGeometry;
import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Chacal: más delgado que el lobo vanilla, con patas largas, hocico largo, orejas altas, cola tupida y un collar de
 * oro sobre el pecho. Camina como el lobo (patas en diagonal) y mueve la cola al andar.
 */
public class JackalModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("jackal"), "main");

	/** Cola caída hacia atrás, como la del lobo. */
	private static final float TAIL_DROP = Mth.PI / 5;

	private final ModelPart head;
	private final ModelPart tail;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightHindLeg;

	public JackalModel(ModelPart root) {
		super(root);
		head = root.getChild("head");
		tail = root.getChild("tail");
		leftFrontLeg = root.getChild("left_front_leg");
		rightFrontLeg = root.getChild("right_front_leg");
		leftHindLeg = root.getChild("left_hind_leg");
		rightHindLeg = root.getChild("right_hind_leg");
	}

	/** Geometría generada por tools/model-gen/china_entities.py --mod desde proposals/jackal.py (aprobada el 2026-11-15) */
	public static LayerDefinition createBodyLayer() {
		return JackalGeometry.create();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float pos = state.walkAnimationPos;
		float speed = state.walkAnimationSpeed;
		// Como el lobo vanilla: patas en diagonal en fase.
		rightHindLeg.xRot = Mth.cos(pos * 0.6662f) * 1.4f * speed;
		leftHindLeg.xRot = Mth.cos(pos * 0.6662f + Mth.PI) * 1.4f * speed;
		rightFrontLeg.xRot = Mth.cos(pos * 0.6662f + Mth.PI) * 1.4f * speed;
		leftFrontLeg.xRot = Mth.cos(pos * 0.6662f) * 1.4f * speed;
		tail.yRot = Mth.cos(pos * 0.6662f) * 1.4f * speed;
		head.xRot = state.xRot * Mth.DEG_TO_RAD;
		head.yRot = state.yRot * Mth.DEG_TO_RAD;
	}
}
