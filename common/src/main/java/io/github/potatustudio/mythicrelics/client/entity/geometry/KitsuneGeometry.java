package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad kitsune. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class KitsuneGeometry {
	private KitsuneGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.0f, -3.0f, -5.0f, 6.0f, 6.0f, 10.0f),
				PartPose.offset(0.0f, 15.0f, 0.0f));
		PartDefinition p_bib = p_body.addOrReplaceChild("bib", CubeListBuilder.create()
				.texOffs(0, 54).addBox(-2.5f, 0.0f, -0.6f, 5.0f, 4.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -1.0f, -5.0f, 0.25f, 0.0f, 0.0f));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(32, 0).addBox(-3.0f, -4.0f, -4.0f, 6.0f, 5.0f, 5.0f)
				.texOffs(12, 54).addBox(-1.5f, -1.0f, -7.0f, 3.0f, 2.0f, 3.0f),
				PartPose.offset(0.0f, 13.0f, -5.0f));
		PartDefinition p_ear_r = p_head.addOrReplaceChild("ear_r", CubeListBuilder.create()
				.texOffs(24, 54).addBox(-1.0f, -3.0f, -0.5f, 2.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(-2.0f, -4.0f, -2.0f, 0.0f, 0.0f, -0.2f));
		PartDefinition p_ear_l = p_head.addOrReplaceChild("ear_l", CubeListBuilder.create()
				.texOffs(30, 54).addBox(-1.0f, -3.0f, -0.5f, 2.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(2.0f, -4.0f, -2.0f, 0.0f, 0.0f, 0.2f));
		PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create()
				.texOffs(54, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
				PartPose.offset(-1.8f, 18.0f, -3.5f));
		PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create()
				.texOffs(0, 16).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
				PartPose.offset(1.8f, 18.0f, -3.5f));
		PartDefinition p_leg_hr = root.addOrReplaceChild("leg_hr", CubeListBuilder.create()
				.texOffs(8, 16).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
				PartPose.offset(-1.8f, 18.0f, 3.5f));
		PartDefinition p_leg_hl = root.addOrReplaceChild("leg_hl", CubeListBuilder.create()
				.texOffs(16, 16).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
				PartPose.offset(1.8f, 18.0f, 3.5f));
		PartDefinition p_tail0 = p_body.addOrReplaceChild("tail0", CubeListBuilder.create()
				.texOffs(24, 16).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 0.85f, -0.88f, 0.0f));
		PartDefinition p_tail0_tip = p_tail0.addOrReplaceChild("tail0_tip", CubeListBuilder.create()
				.texOffs(48, 32).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail1 = p_body.addOrReplaceChild("tail1", CubeListBuilder.create()
				.texOffs(40, 16).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 0.9375f, -0.66f, 0.0f));
		PartDefinition p_tail1_tip = p_tail1.addOrReplaceChild("tail1_tip", CubeListBuilder.create()
				.texOffs(0, 40).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail2 = p_body.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(0, 24).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 1.025f, -0.44f, 0.0f));
		PartDefinition p_tail2_tip = p_tail2.addOrReplaceChild("tail2_tip", CubeListBuilder.create()
				.texOffs(14, 40).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail3 = p_body.addOrReplaceChild("tail3", CubeListBuilder.create()
				.texOffs(16, 24).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 1.112f, -0.22f, 0.0f));
		PartDefinition p_tail3_tip = p_tail3.addOrReplaceChild("tail3_tip", CubeListBuilder.create()
				.texOffs(28, 40).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail4 = p_body.addOrReplaceChild("tail4", CubeListBuilder.create()
				.texOffs(32, 24).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 1.2f, 0.0f, 0.0f));
		PartDefinition p_tail4_tip = p_tail4.addOrReplaceChild("tail4_tip", CubeListBuilder.create()
				.texOffs(42, 40).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail5 = p_body.addOrReplaceChild("tail5", CubeListBuilder.create()
				.texOffs(48, 24).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 1.112f, 0.22f, 0.0f));
		PartDefinition p_tail5_tip = p_tail5.addOrReplaceChild("tail5_tip", CubeListBuilder.create()
				.texOffs(0, 47).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail6 = p_body.addOrReplaceChild("tail6", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 1.025f, 0.44f, 0.0f));
		PartDefinition p_tail6_tip = p_tail6.addOrReplaceChild("tail6_tip", CubeListBuilder.create()
				.texOffs(14, 47).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail7 = p_body.addOrReplaceChild("tail7", CubeListBuilder.create()
				.texOffs(16, 32).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 0.9375f, 0.66f, 0.0f));
		PartDefinition p_tail7_tip = p_tail7.addOrReplaceChild("tail7_tip", CubeListBuilder.create()
				.texOffs(28, 47).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail8 = p_body.addOrReplaceChild("tail8", CubeListBuilder.create()
				.texOffs(32, 32).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 0.85f, 0.88f, 0.0f));
		PartDefinition p_tail8_tip = p_tail8.addOrReplaceChild("tail8_tip", CubeListBuilder.create()
				.texOffs(42, 47).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_foxfire = root.addOrReplaceChild("foxfire", CubeListBuilder.create(),
				PartPose.offset(0.0f, 12.0f, 0.0f));
		PartDefinition p_foxfire_a = p_foxfire.addOrReplaceChild("foxfire_a", CubeListBuilder.create()
				.texOffs(36, 54).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f),
				PartPose.offset(9.0f, 0.0f, 0.0f));
		PartDefinition p_foxfire_b = p_foxfire.addOrReplaceChild("foxfire_b", CubeListBuilder.create()
				.texOffs(44, 54).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f),
				PartPose.offset(-9.0f, -2.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
