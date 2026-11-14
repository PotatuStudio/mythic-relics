package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad weapon/mistletoe_dart. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class MistletoeDartGeometry {
	private MistletoeDartGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_dart = root.addOrReplaceChild("dart", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5f, -8.0f, -0.5f, 1.0f, 15.0f, 1.0f)
				.texOffs(12, 0).addBox(-0.5f, -11.0f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(4, 0).addBox(-1.0f, 5.0f, -1.0f, 2.0f, 3.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_leaf_r0 = p_dart.addOrReplaceChild("leaf_r0", CubeListBuilder.create()
				.texOffs(16, 0).addBox(-3.0f, -0.5f, -1.0f, 3.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(-0.5f, -1.0f, 0.0f, 0.0f, 0.0f, -0.9f));
		PartDefinition p_leaf_l0 = p_dart.addOrReplaceChild("leaf_l0", CubeListBuilder.create()
				.texOffs(0, 16).addBox(0.0f, -0.5f, -1.0f, 3.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.9f));
		PartDefinition p_leaf_r1 = p_dart.addOrReplaceChild("leaf_r1", CubeListBuilder.create()
				.texOffs(10, 16).addBox(-3.0f, -0.5f, -1.0f, 3.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(-0.5f, 3.0f, 0.0f, 0.0f, 0.0f, -0.9f));
		PartDefinition p_leaf_l1 = p_dart.addOrReplaceChild("leaf_l1", CubeListBuilder.create()
				.texOffs(20, 16).addBox(0.0f, -0.5f, -1.0f, 3.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.5f, 3.0f, 0.0f, 0.0f, 0.0f, 0.9f));
		PartDefinition p_berries = p_dart.addOrReplaceChild("berries", CubeListBuilder.create()
				.texOffs(0, 19).addBox(-1.0f, -2.0f, -1.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(4, 19).addBox(0.0f, 2.0f, 0.0f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 32, 32);
	}
}
