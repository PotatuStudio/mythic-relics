package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad monkey_warrior. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class MonkeyWarriorGeometry {
	private MonkeyWarriorGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 31).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 11.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 3.0f, 0.0f, 0.12f, 0.0f, 0.0f));
		PartDefinition p_kilt = p_body.addOrReplaceChild("kilt", CubeListBuilder.create()
				.texOffs(8, 46).addBox(-4.5f, 0.0f, -2.5f, 9.0f, 5.0f, 5.0f),
				PartPose.offset(0.0f, 9.0f, 0.0f));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(4, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
				.texOffs(0, 73).addBox(-2.0f, -3.0f, -6.0f, 4.0f, 3.0f, 2.0f)
				.texOffs(12, 73).addBox(-5.0f, -5.0f, -1.0f, 1.0f, 2.0f, 2.0f)
				.texOffs(18, 73).addBox(4.0f, -5.0f, -1.0f, 1.0f, 2.0f, 2.0f)
				.texOffs(30, 73).addBox(-4.5f, -6.5f, -4.5f, 9.0f, 1.0f, 1.0f)
				.texOffs(0, 78).addBox(-4.5f, -6.5f, 3.5f, 9.0f, 1.0f, 1.0f)
				.texOffs(36, 46).addBox(-4.5f, -6.5f, -3.5f, 1.0f, 1.0f, 7.0f)
				.texOffs(0, 57).addBox(3.5f, -6.5f, -3.5f, 1.0f, 1.0f, 7.0f)
				.texOffs(16, 57).addBox(-3.0f, -10.0f, -3.0f, 6.0f, 2.0f, 6.0f)
				.texOffs(24, 73).addBox(-1.0f, -12.0f, -3.5f, 2.0f, 3.0f, 1.0f),
				PartPose.offset(0.0f, 3.0f, 0.0f));
		PartDefinition p_wing_r = p_head.addOrReplaceChild("wing_r", CubeListBuilder.create()
				.texOffs(40, 57).addBox(-1.0f, -3.0f, -2.0f, 1.0f, 4.0f, 4.0f),
				PartPose.offsetAndRotation(-3.0f, -9.0f, 0.0f, 0.0f, 0.0f, 0.5f));
		PartDefinition p_wing_l = p_head.addOrReplaceChild("wing_l", CubeListBuilder.create()
				.texOffs(50, 57).addBox(0.0f, -3.0f, -2.0f, 1.0f, 4.0f, 4.0f),
				PartPose.offsetAndRotation(3.0f, -9.0f, 0.0f, 0.0f, 0.0f, -0.5f));
		PartDefinition p_feather_r = p_head.addOrReplaceChild("feather_r", CubeListBuilder.create()
				.texOffs(56, 31).addBox(-0.5f, -12.0f, -0.5f, 1.0f, 12.0f, 1.0f),
				PartPose.offsetAndRotation(-1.5f, -10.0f, -1.0f, -0.35f, 0.0f, -0.18f));
		PartDefinition p_feather_r2 = p_feather_r.addOrReplaceChild("feather_r2", CubeListBuilder.create()
				.texOffs(0, 46).addBox(-0.5f, -10.0f, -0.5f, 1.0f, 10.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, 0.0f, -0.9f, 0.0f, 0.0f));
		PartDefinition p_feather_l = p_head.addOrReplaceChild("feather_l", CubeListBuilder.create()
				.texOffs(60, 31).addBox(-0.5f, -12.0f, -0.5f, 1.0f, 12.0f, 1.0f),
				PartPose.offsetAndRotation(1.5f, -10.0f, -1.0f, -0.35f, 0.0f, 0.18f));
		PartDefinition p_feather_l2 = p_feather_l.addOrReplaceChild("feather_l2", CubeListBuilder.create()
				.texOffs(4, 46).addBox(-0.5f, -10.0f, -0.5f, 1.0f, 10.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, 0.0f, -0.9f, 0.0f, 0.0f));
		PartDefinition p_arm_r = root.addOrReplaceChild("arm_r", CubeListBuilder.create()
				.texOffs(36, 0).addBox(-1.5f, -1.0f, -1.5f, 3.0f, 13.0f, 3.0f)
				.texOffs(30, 65).addBox(-2.5f, -2.0f, -2.0f, 4.0f, 2.0f, 4.0f),
				PartPose.offset(-5.5f, 4.0f, 0.0f));
		PartDefinition p_arm_l = root.addOrReplaceChild("arm_l", CubeListBuilder.create()
				.texOffs(48, 0).addBox(-1.5f, -1.0f, -1.5f, 3.0f, 13.0f, 3.0f)
				.texOffs(46, 65).addBox(-1.5f, -2.0f, -2.0f, 4.0f, 2.0f, 4.0f),
				PartPose.offset(5.5f, 4.0f, 0.0f));
		PartDefinition p_staff = p_arm_r.addOrReplaceChild("staff", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5f, -22.0f, -0.5f, 1.0f, 30.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, 11.0f, -0.5f, 0.25f, 0.0f, 0.0f));
		PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
				.texOffs(24, 31).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(-2.0f, 14.0f, 0.0f));
		PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
				.texOffs(40, 31).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(2.0f, 14.0f, 0.0f));
		PartDefinition p_tail = p_body.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(0, 65).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 7.0f),
				PartPose.offsetAndRotation(0.0f, 10.0f, 2.0f, -0.6f, 0.0f, 0.0f));
		PartDefinition p_tail2 = p_tail.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(16, 65).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 7.0f, -1.0f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 128);
	}
}
