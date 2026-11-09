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
 * Cerbero como lo describe Apolodoro: tres cabezas de perro, cola de serpiente y serpientes que le salen del lomo. Bestia
 * de lomo encorvado (pecho alto y ancho, cadera más baja), patas gruesas con garras y fauces que se abren y gruñen a
 * destiempo. Las cabezas de los lados miran hacia afuera.
 */
public class CerberusModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("cerberus"), "main");

	/** Giro hacia afuera de las cabezas de los lados. */
	private static final float SIDE_TURN = 0.5f;
	/** Serpientes del lomo: x, y, z de la base e inclinación hacia atrás. */
	private static final float[][] BACK_SNAKES = {
			{-1.5f, 5.0f, -6.0f, -0.35f}, {1.5f, 5.0f, -3.5f, -0.45f}, {-1.5f, 5.0f, -1.0f, -0.55f},
			{1.5f, 7.0f, 1.5f, -0.6f}, {-1.5f, 7.0f, 4.0f, -0.7f}};

	private final ModelPart[] heads = new ModelPart[3];
	private final ModelPart[] jaws = new ModelPart[3];
	private final ModelPart[] snakes = new ModelPart[BACK_SNAKES.length];
	private final ModelPart tail;
	private final ModelPart tailMid;
	private final ModelPart tailHead;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightHindLeg;

	public CerberusModel(ModelPart root) {
		super(root);
		String[] names = {"head", "left_head", "right_head"};
		for (int i = 0; i < 3; i++) {
			heads[i] = root.getChild(names[i]);
			jaws[i] = heads[i].getChild("jaw");
		}
		for (int i = 0; i < snakes.length; i++) {
			snakes[i] = root.getChild("snake_" + i);
		}
		tail = root.getChild("tail");
		tailMid = tail.getChild("tail_mid");
		tailHead = tailMid.getChild("tail_head");
		leftFrontLeg = root.getChild("left_front_leg");
		rightFrontLeg = root.getChild("right_front_leg");
		leftHindLeg = root.getChild("left_hind_leg");
		rightHindLeg = root.getChild("right_hind_leg");
	}

	/** Cráneo, hocico (mandíbula de arriba con los colmillos), orejas en punta y la mandíbula de abajo, que se abre. */
	private static void dogHead(PartDefinition root, String name, PartPose pose) {
		PartDefinition head = root.addOrReplaceChild(name, CubeListBuilder.create()
				.texOffs(36, 0).addBox(-3.0f, -3.0f, -5.0f, 6, 6, 5)
				.texOffs(36, 11).addBox(-2.0f, -1.0f, -10.0f, 4, 2, 5)
				.texOffs(54, 11).addBox(-3.0f, -6.0f, -2.0f, 2, 3, 1)
				.texOffs(54, 11).mirror().addBox(1.0f, -6.0f, -2.0f, 2, 3, 1), pose);
		head.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(36, 18).addBox(-2.0f, 0.0f, -5.0f, 4, 1, 5), PartPose.offset(0.0f, 1.0f, -5.0f));
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		// Pecho alto y ancho y cadera más baja: lomo encorvado de bestia.
		root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0f, -5.0f, -8.0f, 10, 9, 8)
				.texOffs(0, 17).addBox(-4.0f, -3.0f, 0.0f, 8, 7, 8),
				PartPose.offset(0.0f, 10.0f, 0.0f));
		dogHead(root, "head", PartPose.offset(0.0f, 7.0f, -8.0f));
		dogHead(root, "left_head", PartPose.offsetAndRotation(5.0f, 9.0f, -6.5f, 0.0f, -SIDE_TURN, 0.0f));
		dogHead(root, "right_head", PartPose.offsetAndRotation(-5.0f, 9.0f, -6.5f, 0.0f, SIDE_TURN, 0.0f));
		// Serpientes del lomo: cuello fino y cabeza.
		for (int i = 0; i < BACK_SNAKES.length; i++) {
			float[] s = BACK_SNAKES[i];
			root.addOrReplaceChild("snake_" + i, CubeListBuilder.create()
					.texOffs(40, 32).addBox(-0.5f, -4.0f, -0.5f, 1, 4, 1)
					.texOffs(44, 32).addBox(-1.0f, -5.0f, -1.5f, 2, 1, 2),
					PartPose.offsetAndRotation(s[0], s[1], s[2], s[3], 0.0f, 0.0f));
		}
		// Cola de serpiente en tres tramos, con su propia cabeza.
		PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(32, 24).addBox(-1.0f, -1.0f, 0.0f, 2, 2, 6), PartPose.offsetAndRotation(0.0f, 9.0f, 8.0f, 0.5f, 0.0f, 0.0f));
		PartDefinition mid = tail.addOrReplaceChild("tail_mid", CubeListBuilder.create()
				.texOffs(48, 24).addBox(-1.0f, -1.0f, 0.0f, 2, 2, 5), PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, -0.9f, 0.0f, 0.0f));
		mid.addOrReplaceChild("tail_head", CubeListBuilder.create()
				.texOffs(28, 32).addBox(-1.5f, -1.0f, 0.0f, 3, 2, 3), PartPose.offsetAndRotation(0.0f, 0.0f, 5.0f, -0.4f, 0.0f, 0.0f));
		// Patas gruesas con zarpas anchas y garras.
		CubeListBuilder leg = CubeListBuilder.create()
				.texOffs(0, 32).addBox(-1.5f, 0.0f, -1.5f, 3, 10, 3)
				.texOffs(12, 32).addBox(-2.0f, 8.0f, -2.5f, 4, 2, 4);
		CubeListBuilder mirroredLeg = CubeListBuilder.create().mirror()
				.texOffs(0, 32).addBox(-1.5f, 0.0f, -1.5f, 3, 10, 3)
				.texOffs(12, 32).addBox(-2.0f, 8.0f, -2.5f, 4, 2, 4);
		root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(3.5f, 14.0f, -4.0f));
		root.addOrReplaceChild("right_front_leg", mirroredLeg, PartPose.offset(-3.5f, 14.0f, -4.0f));
		root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(3.0f, 14.0f, 5.0f));
		root.addOrReplaceChild("right_hind_leg", mirroredLeg, PartPose.offset(-3.0f, 14.0f, 5.0f));
		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float pos = state.walkAnimationPos;
		float speed = state.walkAnimationSpeed;
		float age = state.ageInTicks;
		rightHindLeg.xRot = Mth.cos(pos * 0.6662f) * 1.2f * speed;
		leftHindLeg.xRot = Mth.cos(pos * 0.6662f + Mth.PI) * 1.2f * speed;
		rightFrontLeg.xRot = Mth.cos(pos * 0.6662f + Mth.PI) * 1.2f * speed;
		leftFrontLeg.xRot = Mth.cos(pos * 0.6662f) * 1.2f * speed;
		float pitch = state.xRot * Mth.DEG_TO_RAD;
		float yaw = state.yRot * Mth.DEG_TO_RAD;
		heads[0].xRot = pitch;
		heads[0].yRot = yaw;
		float sway = Mth.sin(age * 0.15f) * 0.12f;
		heads[1].xRot = pitch * 0.5f + sway;
		heads[1].yRot = -SIDE_TURN + yaw * 0.5f;
		heads[2].xRot = pitch * 0.5f - sway;
		heads[2].yRot = SIDE_TURN + yaw * 0.5f;
		// Las fauces gruñen a destiempo; al caminar se abren más.
		for (int i = 0; i < 3; i++) {
			jaws[i].xRot = 0.15f + Math.abs(Mth.sin(age * 0.2f + i * 2.1f)) * (0.25f + 0.3f * speed);
		}
		// La cola de serpiente se menea y su cabeza busca; las del lomo se mecen.
		tail.yRot = Mth.sin(age * 0.12f) * 0.4f;
		tailMid.yRot = Mth.sin(age * 0.12f - 1.0f) * 0.5f;
		tailHead.yRot = Mth.sin(age * 0.12f - 2.0f) * 0.6f;
		for (int i = 0; i < snakes.length; i++) {
			snakes[i].zRot = Mth.sin(age * 0.18f + i * 1.3f) * 0.3f;
			snakes[i].xRot = BACK_SNAKES[i][3] + Mth.cos(age * 0.14f + i) * 0.12f;
		}
	}
}
