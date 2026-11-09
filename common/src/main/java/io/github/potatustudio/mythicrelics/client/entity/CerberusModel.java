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
 * Cerbero cachorro: cuerpo de perro robusto con collar de púas y tres cabezas. La del centro mira al frente; las de los
 * lados, un poco más bajas y atrás, miran hacia afuera y se mueven a destiempo. Camina como el lobo.
 */
public class CerberusModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("cerberus"), "main");

	private static final float TAIL_DROP = Mth.PI / 4;
	/** Giro hacia afuera de las cabezas de los lados. */
	private static final float SIDE_TURN = 0.45f;

	private final ModelPart head;
	private final ModelPart leftHead;
	private final ModelPart rightHead;
	private final ModelPart tail;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightHindLeg;

	public CerberusModel(ModelPart root) {
		super(root);
		head = root.getChild("head");
		leftHead = root.getChild("left_head");
		rightHead = root.getChild("right_head");
		tail = root.getChild("tail");
		leftFrontLeg = root.getChild("left_front_leg");
		rightFrontLeg = root.getChild("right_front_leg");
		leftHindLeg = root.getChild("left_hind_leg");
		rightHindLeg = root.getChild("right_hind_leg");
	}

	private static CubeListBuilder dogHead() {
		return CubeListBuilder.create()
				.texOffs(36, 8).addBox(-2.5f, -3.0f, -4.0f, 5, 5, 4)
				.texOffs(52, 17).addBox(-1.5f, -0.5f, -7.0f, 3, 2, 3)
				.texOffs(56, 8).addBox(-2.5f, -5.0f, -2.0f, 2, 2, 1)
				.texOffs(56, 8).mirror().addBox(0.5f, -5.0f, -2.0f, 2, 2, 1);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.0f, -3.0f, -6.0f, 6, 6, 12)
				// Collar de cuero con púas, sobre el pecho.
				.texOffs(36, 0).addBox(-3.5f, -3.5f, -6.5f, 7, 7, 1),
				PartPose.offset(0.0f, 14.0f, 0.0f));
		root.addOrReplaceChild("head", dogHead(), PartPose.offset(0.0f, 12.0f, -6.5f));
		root.addOrReplaceChild("left_head", dogHead(), PartPose.offsetAndRotation(3.2f, 13.0f, -5.5f, 0.0f, -SIDE_TURN, 0.0f));
		root.addOrReplaceChild("right_head", dogHead(), PartPose.offsetAndRotation(-3.2f, 13.0f, -5.5f, 0.0f, SIDE_TURN, 0.0f));
		root.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(8, 18).addBox(-1.0f, 0.0f, -1.0f, 2, 7, 2),
				PartPose.offsetAndRotation(0.0f, 12.0f, 6.0f, TAIL_DROP, 0.0f, 0.0f));
		CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 18).addBox(-1.0f, 0.0f, -1.0f, 2, 7, 2);
		CubeListBuilder mirroredLeg = CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-1.0f, 0.0f, -1.0f, 2, 7, 2);
		root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(1.8f, 17.0f, -4.5f));
		root.addOrReplaceChild("right_front_leg", mirroredLeg, PartPose.offset(-1.8f, 17.0f, -4.5f));
		root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(1.8f, 17.0f, 4.5f));
		root.addOrReplaceChild("right_hind_leg", mirroredLeg, PartPose.offset(-1.8f, 17.0f, 4.5f));
		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float pos = state.walkAnimationPos;
		float speed = state.walkAnimationSpeed;
		rightHindLeg.xRot = Mth.cos(pos * 0.6662f) * 1.4f * speed;
		leftHindLeg.xRot = Mth.cos(pos * 0.6662f + Mth.PI) * 1.4f * speed;
		rightFrontLeg.xRot = Mth.cos(pos * 0.6662f + Mth.PI) * 1.4f * speed;
		leftFrontLeg.xRot = Mth.cos(pos * 0.6662f) * 1.4f * speed;
		tail.yRot = Mth.cos(pos * 0.6662f) * 1.4f * speed;
		float pitch = state.xRot * Mth.DEG_TO_RAD;
		float yaw = state.yRot * Mth.DEG_TO_RAD;
		head.xRot = pitch;
		head.yRot = yaw;
		// Las de los lados siguen la mirada a medias y cabecean a destiempo.
		float sway = Mth.sin(state.ageInTicks * 0.15f) * 0.12f;
		leftHead.xRot = pitch * 0.5f + sway;
		leftHead.yRot = -SIDE_TURN + yaw * 0.5f;
		rightHead.xRot = pitch * 0.5f - sway;
		rightHead.yRot = SIDE_TURN + yaw * 0.5f;
	}
}
