package io.github.potatustudio.mythicrelics.client.entity;

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

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5f, -2.5f, -6.0f, 5, 5, 12)
				// Collar de oro sobre el pecho.
				.texOffs(34, 0).addBox(-3.0f, -3.0f, -6.5f, 6, 6, 1),
				PartPose.offset(0.0f, 14.5f, 0.0f));
		root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(34, 7).addBox(-2.5f, -3.0f, -4.0f, 5, 5, 4)
				.texOffs(48, 0).addBox(-1.5f, -0.5f, -8.0f, 3, 2, 4)
				.texOffs(52, 7).addBox(-2.5f, -7.0f, -2.0f, 2, 4, 1)
				.texOffs(52, 7).mirror().addBox(0.5f, -7.0f, -2.0f, 2, 4, 1),
				PartPose.offset(0.0f, 12.5f, -6.5f));
		root.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(8, 17).addBox(-1.0f, 0.0f, -1.0f, 2, 7, 2),
				PartPose.offsetAndRotation(0.0f, 12.5f, 6.0f, TAIL_DROP, 0.0f, 0.0f));
		CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 17).addBox(-1.0f, 0.0f, -1.0f, 2, 7, 2);
		CubeListBuilder mirroredLeg = CubeListBuilder.create().texOffs(0, 17).mirror().addBox(-1.0f, 0.0f, -1.0f, 2, 7, 2);
		root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(1.5f, 17.0f, -4.5f));
		root.addOrReplaceChild("right_front_leg", mirroredLeg, PartPose.offset(-1.5f, 17.0f, -4.5f));
		root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(1.5f, 17.0f, 4.5f));
		root.addOrReplaceChild("right_hind_leg", mirroredLeg, PartPose.offset(-1.5f, 17.0f, 4.5f));
		return LayerDefinition.create(mesh, 64, 32);
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
