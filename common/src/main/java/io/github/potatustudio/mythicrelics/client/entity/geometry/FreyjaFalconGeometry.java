package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad freyja_falcon. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class FreyjaFalconGeometry {
	private FreyjaFalconGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5f, -2.5f, -4.5f, 5.0f, 5.0f, 9.0f),
				PartPose.offset(0.0f, 16.0f, 0.0f));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(28, 0).addBox(-2.0f, -3.5f, -3.5f, 4.0f, 4.0f, 4.0f),
				PartPose.offset(0.0f, 14.0f, -4.5f));
		PartDefinition p_beak = p_head.addOrReplaceChild("beak", CubeListBuilder.create()
				.texOffs(58, 22).addBox(-0.5f, -0.5f, -2.0f, 1.0f, 2.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, -3.5f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_wing_r = p_body.addOrReplaceChild("wing_r", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-12.0f, -0.5f, 0.0f, 12.0f, 1.0f, 7.0f),
				PartPose.offset(-2.5f, -2.0f, -3.0f));
		PartDefinition p_wing_l = p_body.addOrReplaceChild("wing_l", CubeListBuilder.create()
				.texOffs(0, 22).addBox(0.0f, -0.5f, 0.0f, 12.0f, 1.0f, 7.0f),
				PartPose.offset(2.5f, -2.0f, -3.0f));
		PartDefinition p_tail = p_body.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(38, 22).addBox(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, -0.15f, 0.0f, 0.0f));
		PartDefinition p_leg_r = p_body.addOrReplaceChild("leg_r", CubeListBuilder.create()
				.texOffs(0, 30).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(-1.2f, 2.5f, 1.0f, 0.9f, 0.0f, 0.0f));
		PartDefinition p_leg_l = p_body.addOrReplaceChild("leg_l", CubeListBuilder.create()
				.texOffs(4, 30).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(1.2f, 2.5f, 1.0f, 0.9f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
