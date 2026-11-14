package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad pegasus. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class PegasusGeometry {
	private PegasusGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0f, -5.0f, -11.0f, 10.0f, 10.0f, 22.0f),
				PartPose.offset(0.0f, 9.0f, 0.0f));
		PartDefinition p_blanket = p_body.addOrReplaceChild("blanket", CubeListBuilder.create()
				.texOffs(32, 32).addBox(-5.5f, -5.5f, -4.0f, 11.0f, 5.0f, 8.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create()
				.texOffs(92, 0).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(-3.0f, 14.0f, -8.0f));
		PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create()
				.texOffs(108, 0).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(3.0f, 14.0f, -8.0f));
		PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(-3.0f, 14.0f, 8.0f));
		PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create()
				.texOffs(16, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(3.0f, 14.0f, 8.0f));
		PartDefinition p_neck = p_body.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(64, 0).addBox(-2.0f, -12.0f, -3.0f, 4.0f, 13.0f, 6.0f)
				.texOffs(84, 0).addBox(-1.0f, -13.0f, 3.0f, 2.0f, 14.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -3.0f, -9.0f, 0.5f, 0.0f, 0.0f));
		PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(70, 32).addBox(-3.0f, -4.0f, -5.0f, 6.0f, 6.0f, 7.0f)
				.texOffs(76, 59).addBox(-2.0f, -3.0f, -10.0f, 4.0f, 5.0f, 5.0f)
				.texOffs(12, 70).addBox(-2.5f, -7.0f, 0.0f, 2.0f, 3.0f, 1.0f)
				.texOffs(18, 70).addBox(0.5f, -7.0f, 0.0f, 2.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, -1.0f, 0.2f, 0.0f, 0.0f));
		PartDefinition p_bridle = p_head.addOrReplaceChild("bridle", CubeListBuilder.create()
				.texOffs(0, 70).addBox(-2.5f, -3.5f, -8.0f, 5.0f, 6.0f, 1.0f)
				.texOffs(106, 59).addBox(-3.5f, -4.5f, -2.0f, 7.0f, 7.0f, 1.0f)
				.texOffs(24, 70).addBox(-3.0f, 0.0f, -9.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(28, 70).addBox(2.0f, 0.0f, -9.0f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_wing_r = p_body.addOrReplaceChild("wing_r", CubeListBuilder.create()
				.texOffs(0, 59).addBox(-10.0f, -1.0f, 0.0f, 10.0f, 2.0f, 9.0f),
				PartPose.offsetAndRotation(-5.0f, -4.0f, -6.0f, 0.0f, 0.2f, 0.6f));
		PartDefinition p_wing_r2 = p_wing_r.addOrReplaceChild("wing_r2", CubeListBuilder.create()
				.texOffs(0, 46).addBox(-12.0f, -0.5f, 0.0f, 12.0f, 1.0f, 12.0f),
				PartPose.offsetAndRotation(-10.0f, 0.0f, 0.0f, 0.0f, 0.1f, 0.3f));
		PartDefinition p_wing_l = p_body.addOrReplaceChild("wing_l", CubeListBuilder.create()
				.texOffs(38, 59).addBox(0.0f, -1.0f, 0.0f, 10.0f, 2.0f, 9.0f),
				PartPose.offsetAndRotation(5.0f, -4.0f, -6.0f, 0.0f, -0.2f, -0.6f));
		PartDefinition p_wing_l2 = p_wing_l.addOrReplaceChild("wing_l2", CubeListBuilder.create()
				.texOffs(48, 46).addBox(0.0f, -0.5f, 0.0f, 12.0f, 1.0f, 12.0f),
				PartPose.offsetAndRotation(10.0f, 0.0f, 0.0f, 0.0f, -0.1f, -0.3f));
		PartDefinition p_tail = p_body.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(96, 46).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -4.0f, 11.0f, 0.6f, 0.0f, 0.0f));
		PartDefinition p_tail2 = p_tail.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(94, 59).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 7.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 10.0f, 0.0f, 0.3f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
