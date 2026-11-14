package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad urashima_turtle. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class UrashimaTurtleGeometry {
	private UrashimaTurtleGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-9.0f, -3.0f, -10.0f, 18.0f, 4.0f, 20.0f)
				.texOffs(0, 24).addBox(-7.0f, -6.0f, -8.0f, 14.0f, 3.0f, 16.0f)
				.texOffs(24, 43).addBox(-4.0f, -8.0f, -5.0f, 8.0f, 2.0f, 10.0f),
				PartPose.offset(0.0f, 18.0f, 0.0f));
		PartDefinition p_seaweed0 = p_body.addOrReplaceChild("seaweed0", CubeListBuilder.create()
				.texOffs(60, 24).addBox(0.0f, -1.0f, 0.0f, 0.0f, 5.0f, 12.0f),
				PartPose.offsetAndRotation(-4.0f, -2.0f, 10.0f, -0.15f, 0.0f, 0.0f));
		PartDefinition p_seaweed1 = p_body.addOrReplaceChild("seaweed1", CubeListBuilder.create()
				.texOffs(84, 24).addBox(0.0f, -1.0f, 0.0f, 0.0f, 5.0f, 12.0f),
				PartPose.offsetAndRotation(0.0f, -2.0f, 10.0f, -0.15f, 0.0f, 0.0f));
		PartDefinition p_seaweed2 = p_body.addOrReplaceChild("seaweed2", CubeListBuilder.create()
				.texOffs(0, 43).addBox(0.0f, -1.0f, 0.0f, 0.0f, 5.0f, 12.0f),
				PartPose.offsetAndRotation(4.0f, -2.0f, 10.0f, -0.15f, 0.0f, 0.0f));
		PartDefinition p_neck = root.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(80, 43).addBox(-2.0f, -2.0f, -3.0f, 4.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 18.0f, -10.0f));
		PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(60, 43).addBox(-2.5f, -2.5f, -5.0f, 5.0f, 5.0f, 5.0f),
				PartPose.offset(0.0f, -0.5f, -3.0f));
		PartDefinition p_flipper_fr = root.addOrReplaceChild("flipper_fr", CubeListBuilder.create()
				.texOffs(94, 43).addBox(-12.0f, -0.5f, -2.5f, 12.0f, 1.0f, 5.0f),
				PartPose.offsetAndRotation(-8.0f, 19.0f, -6.0f, 0.0f, 0.3f, 0.2f));
		PartDefinition p_flipper_fl = root.addOrReplaceChild("flipper_fl", CubeListBuilder.create()
				.texOffs(0, 60).addBox(0.0f, -0.5f, -2.5f, 12.0f, 1.0f, 5.0f),
				PartPose.offsetAndRotation(8.0f, 19.0f, -6.0f, 0.0f, -0.3f, -0.2f));
		PartDefinition p_flipper_hr = root.addOrReplaceChild("flipper_hr", CubeListBuilder.create()
				.texOffs(34, 60).addBox(-6.0f, -0.5f, -2.0f, 6.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(-7.0f, 19.0f, 8.0f, 0.0f, -0.4f, 0.1f));
		PartDefinition p_flipper_hl = root.addOrReplaceChild("flipper_hl", CubeListBuilder.create()
				.texOffs(54, 60).addBox(0.0f, -0.5f, -2.0f, 6.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(7.0f, 19.0f, 8.0f, 0.0f, 0.4f, -0.1f));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
