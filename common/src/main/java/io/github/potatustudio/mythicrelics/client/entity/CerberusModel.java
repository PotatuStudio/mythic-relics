package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.client.entity.geometry.CerberusGeometry;
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
 * Cerbero: tres cabezas de perro y cola de serpiente, como en Apolodoro (sin las serpientes del lomo, a pedido del
 * usuario). Bestia de lomo encorvado (pecho alto y ancho, cadera más baja), patas gruesas con garras y fauces que se abren y gruñen a
 * destiempo. Las cabezas de los lados miran hacia afuera.
 */
public class CerberusModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("cerberus"), "main");

	/** Giro hacia afuera de las cabezas de los lados. */
	private static final float SIDE_TURN = 0.5f;
	/** Curva de la cola de serpiente: sale de la cadera, baja y se levanta en la punta. */
	private static final float TAIL_BASE = 0.7f;
	private static final float TAIL_MID = -0.35f;
	private static final float TAIL_TIP = -0.45f;

	private final ModelPart[] heads = new ModelPart[3];
	private final ModelPart[] jaws = new ModelPart[3];
	private final ModelPart tail;
	private final ModelPart tailMid;
	private final ModelPart tailTip;
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
		tail = root.getChild("body").getChild("tail");
		tailMid = tail.getChild("tail_mid");
		tailTip = tailMid.getChild("tail_tip");
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

	/** Geometría generada por tools/model-gen/china_entities.py --mod desde proposals/cerberus.py (aprobada el 2026-11-15) */
	public static LayerDefinition createBodyLayer() {
		return CerberusGeometry.create();
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
		// La cola de serpiente se menea en ola, más en la punta.
		tail.yRot = Mth.sin(age * 0.12f) * 0.3f;
		tailMid.yRot = Mth.sin(age * 0.12f - 1.0f) * 0.35f;
		tailTip.yRot = Mth.sin(age * 0.12f - 2.0f) * 0.5f;
		tailTip.xRot = TAIL_TIP + Mth.sin(age * 0.09f) * 0.15f;
	}
}
