package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.animal.equine.AbstractEquineModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.util.Mth;

/**
 * Sleipnir: la misma geometría que el caballo vanilla (para que la montura y las animaciones del caballo funcionen
 * igual), con su propia textura de 128x64 y ocho patas: a las cuatro del caballo se suman dos pares en el medio, que se
 * mueven a destiempo para que el galope se vea de ocho patas. Lleva la silla dibujada como una pieza sobre el lomo.
 */
public class SleipnirModel extends AbstractEquineModel<EquineRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("sleipnir"), "main");

	private final ModelPart leftFrontMiddleLeg;
	private final ModelPart rightFrontMiddleLeg;
	private final ModelPart leftHindMiddleLeg;
	private final ModelPart rightHindMiddleLeg;

	public SleipnirModel(ModelPart root) {
		super(root);
		leftFrontMiddleLeg = root.getChild("left_front_middle_leg");
		rightFrontMiddleLeg = root.getChild("right_front_middle_leg");
		leftHindMiddleLeg = root.getChild("left_hind_middle_leg");
		rightHindMiddleLeg = root.getChild("right_hind_middle_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0f, -8.0f, -17.0f, 10, 10, 22)
				// Silla de montar sobre el lomo.
				.texOffs(32, 32).addBox(-5.5f, -8.6f, -11.0f, 11, 1, 8),
				PartPose.offset(0.0f, 11.0f, 5.0f));
		PartDefinition headParts = root.addOrReplaceChild("head_parts", CubeListBuilder.create()
				.texOffs(64, 0).addBox(-2.05f, -6.0f, -2.0f, 4, 12, 7),
				PartPose.offsetAndRotation(0.0f, 4.0f, -12.0f, (float) (Math.PI / 6), 0.0f, 0.0f));
		PartDefinition head = headParts.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(86, 0).addBox(-3.0f, -11.0f, -2.0f, 6, 5, 7), PartPose.ZERO);
		headParts.addOrReplaceChild("mane", CubeListBuilder.create()
				.texOffs(112, 0).addBox(-1.0f, -11.0f, 5.01f, 2, 16, 2), PartPose.ZERO);
		headParts.addOrReplaceChild("upper_mouth", CubeListBuilder.create()
				.texOffs(86, 12).addBox(-2.0f, -11.0f, -7.0f, 4, 5, 5), PartPose.ZERO);
		head.addOrReplaceChild("left_ear", CubeListBuilder.create()
				.texOffs(120, 0).addBox(0.55f, -13.0f, 4.0f, 2, 3, 1), PartPose.ZERO);
		head.addOrReplaceChild("right_ear", CubeListBuilder.create()
				.texOffs(120, 0).addBox(-2.55f, -13.0f, 4.0f, 2, 3, 1), PartPose.ZERO);
		body.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(16, 32).addBox(-1.5f, 0.0f, 0.0f, 3, 14, 4),
				PartPose.offsetAndRotation(0.0f, -5.0f, 2.0f, (float) (Math.PI / 6), 0.0f, 0.0f));
		leg(root, "left_front_leg", true, 4.0f, -10.0f, -1.9f);
		leg(root, "right_front_leg", false, -4.0f, -10.0f, -1.9f);
		leg(root, "left_front_middle_leg", true, 4.0f, -5.0f, -1.9f);
		leg(root, "right_front_middle_leg", false, -4.0f, -5.0f, -1.9f);
		leg(root, "left_hind_middle_leg", true, 4.0f, 2.0f, -1.0f);
		leg(root, "right_hind_middle_leg", false, -4.0f, 2.0f, -1.0f);
		leg(root, "left_hind_leg", true, 4.0f, 7.0f, -1.0f);
		leg(root, "right_hind_leg", false, -4.0f, 7.0f, -1.0f);
		return LayerDefinition.create(mesh, 128, 64);
	}

	private static void leg(PartDefinition root, String name, boolean left, float x, float z, float boxZ) {
		CubeListBuilder cube = CubeListBuilder.create().texOffs(0, 32);
		if (left) {
			cube.mirror().addBox(-3.0f, -1.01f, boxZ, 4, 11, 4);
		} else {
			cube.addBox(-1.0f, -1.01f, boxZ, 4, 11, 4);
		}
		root.addOrReplaceChild(name, cube, PartPose.offset(x, 14.0f, z));
	}

	@Override
	public void setupAnim(EquineRenderState state) {
		super.setupAnim(state);
		// Las patas del medio siguen a las de sus extremos a medio paso, y se pliegan con ellas al encabritarse.
		float stand = state.standAnimation;
		float speed = state.walkAnimationSpeed * (1.0f - stand);
		float pos = state.walkAnimationPos * (state.isInWater ? 0.2f : 1.0f);
		float swing = Mth.cos(pos * 0.6662f + Mth.HALF_PI) * 0.8f * speed;
		leftFrontMiddleLeg.xRot = leftFrontLeg.xRot * stand - swing;
		rightFrontMiddleLeg.xRot = rightFrontLeg.xRot * stand + swing;
		leftHindMiddleLeg.xRot = leftHindLeg.xRot * stand + swing * 0.6f;
		rightHindMiddleLeg.xRot = rightHindLeg.xRot * stand - swing * 0.6f;
		leftFrontMiddleLeg.y = leftFrontLeg.y;
		rightFrontMiddleLeg.y = rightFrontLeg.y;
	}
}
