package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad qilin. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class QilinGeometry {
	private QilinGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0f, -4.0f, -10.0f, 8.0f, 8.0f, 18.0f)
				.texOffs(70, 26).addBox(-4.5f, -5.0f, -5.0f, 9.0f, 1.0f, 7.0f)
				.texOffs(16, 39).addBox(-5.0f, -4.0f, -3.0f, 1.0f, 4.0f, 3.0f)
				.texOffs(24, 39).addBox(4.0f, -4.0f, -3.0f, 1.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 11.0f, 2.0f));
		PartDefinition p_spine_flames = p_body.addOrReplaceChild("spine_flames", CubeListBuilder.create()
				.texOffs(48, 26).addBox(0.0f, -3.0f, 0.0f, 0.0f, 3.0f, 6.0f),
				PartPose.offset(0.0f, -4.0f, 2.0f));
		PartDefinition p_neck = root.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(52, 0).addBox(-2.5f, -10.0f, -3.0f, 5.0f, 11.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, 9.0f, -7.0f, 0.55f, 0.0f, 0.0f));
		PartDefinition p_mane = p_neck.addOrReplaceChild("mane", CubeListBuilder.create()
				.texOffs(72, 0).addBox(0.0f, 0.0f, 0.0f, 0.0f, 11.0f, 5.0f),
				PartPose.offset(0.0f, -10.0f, 1.5f));
		PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(24, 26).addBox(-3.0f, -5.0f, -4.0f, 6.0f, 5.0f, 6.0f)
				.texOffs(102, 26).addBox(-2.0f, -4.0f, -9.0f, 4.0f, 3.0f, 5.0f)
				.texOffs(76, 39).addBox(-3.5f, -6.0f, -2.0f, 2.0f, 2.0f, 2.0f)
				.texOffs(84, 39).addBox(1.5f, -6.0f, -2.0f, 2.0f, 2.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -9.5f, -1.0f, -0.55f, 0.0f, 0.0f));
		PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(56, 39).addBox(-1.5f, 0.0f, -4.5f, 3.0f, 1.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, -1.0f, -4.0f, 0.12f, 0.0f, 0.0f));
		PartDefinition p_beard = p_head.addOrReplaceChild("beard", CubeListBuilder.create()
				.texOffs(60, 26).addBox(0.0f, 0.0f, -2.0f, 0.0f, 4.0f, 5.0f),
				PartPose.offset(0.0f, 0.0f, -6.0f));
		PartDefinition p_horn = p_head.addOrReplaceChild("horn", CubeListBuilder.create()
				.texOffs(72, 39).addBox(-0.5f, -5.0f, -0.5f, 1.0f, 5.0f, 1.0f)
				.texOffs(96, 39).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -5.0f, -1.5f, -0.45f, 0.0f, 0.0f));
		PartDefinition p_horn_tip = p_horn.addOrReplaceChild("horn_tip", CubeListBuilder.create()
				.texOffs(92, 39).addBox(-0.5f, -3.0f, -0.5f, 1.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -5.0f, 0.0f, -0.4f, 0.0f, 0.0f));
		PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create()
				.texOffs(82, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
				PartPose.offset(-2.5f, 14.0f, -6.0f));
		PartDefinition p_leg_fr_flame = p_leg_fr.addOrReplaceChild("leg_fr_flame", CubeListBuilder.create()
				.texOffs(32, 39).addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 6.0f, 1.5f));
		PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create()
				.texOffs(94, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
				PartPose.offset(2.5f, 14.0f, -6.0f));
		PartDefinition p_leg_fl_flame = p_leg_fl.addOrReplaceChild("leg_fl_flame", CubeListBuilder.create()
				.texOffs(38, 39).addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 6.0f, 1.5f));
		PartDefinition p_leg_hr = root.addOrReplaceChild("leg_hr", CubeListBuilder.create()
				.texOffs(106, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
				PartPose.offset(-2.5f, 14.0f, 8.0f));
		PartDefinition p_leg_hr_flame = p_leg_hr.addOrReplaceChild("leg_hr_flame", CubeListBuilder.create()
				.texOffs(44, 39).addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 6.0f, 1.5f));
		PartDefinition p_leg_hl = root.addOrReplaceChild("leg_hl", CubeListBuilder.create()
				.texOffs(0, 26).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
				PartPose.offset(2.5f, 14.0f, 8.0f));
		PartDefinition p_leg_hl_flame = p_leg_hl.addOrReplaceChild("leg_hl_flame", CubeListBuilder.create()
				.texOffs(50, 39).addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 6.0f, 1.5f));
		PartDefinition p_tail = p_body.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(0, 39).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 7.0f),
				PartPose.offsetAndRotation(0.0f, -3.0f, 8.0f, -0.9f, 0.0f, 0.0f));
		PartDefinition p_tail_tuft = p_tail.addOrReplaceChild("tail_tuft", CubeListBuilder.create()
				.texOffs(12, 26).addBox(0.0f, -3.0f, 0.0f, 0.0f, 6.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 7.0f, 0.5f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 128, 64);
	}
}
