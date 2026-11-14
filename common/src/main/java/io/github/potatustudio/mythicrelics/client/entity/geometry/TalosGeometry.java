package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad talos. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class TalosGeometry {
	private TalosGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 19).addBox(-6.0f, 0.0f, -3.0f, 12.0f, 12.0f, 6.0f)
				.texOffs(0, 66).addBox(-6.5f, 9.0f, -3.5f, 13.0f, 4.0f, 7.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(20, 37).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
				.texOffs(0, 54).addBox(-4.5f, -8.5f, -4.5f, 9.0f, 3.0f, 9.0f)
				.texOffs(0, 77).addBox(-3.0f, -2.0f, -5.0f, 6.0f, 4.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_arm_r = root.addOrReplaceChild("arm_r", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0f, -1.0f, -2.5f, 4.0f, 14.0f, 5.0f),
				PartPose.offset(-6.0f, 1.0f, 0.0f));
		PartDefinition p_rock = p_arm_r.addOrReplaceChild("rock", CubeListBuilder.create()
				.texOffs(40, 66).addBox(-3.0f, -1.0f, -4.0f, 6.0f, 5.0f, 6.0f),
				PartPose.offset(-2.0f, 13.0f, -1.0f));
		PartDefinition p_arm_l = root.addOrReplaceChild("arm_l", CubeListBuilder.create()
				.texOffs(18, 0).addBox(0.0f, -1.0f, -2.5f, 4.0f, 14.0f, 5.0f),
				PartPose.offset(6.0f, 1.0f, 0.0f));
		PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
				.texOffs(36, 19).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 12.0f, 5.0f),
				PartPose.offset(-3.0f, 12.0f, 0.0f));
		PartDefinition p_nail = p_leg_r.addOrReplaceChild("nail", CubeListBuilder.create()
				.texOffs(16, 77).addBox(-4.0f, 9.0f, -0.5f, 2.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
				.texOffs(0, 37).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 12.0f, 5.0f),
				PartPose.offset(3.0f, 12.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 128);
	}
}
