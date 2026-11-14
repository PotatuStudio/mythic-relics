package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad weapon/mjolnir. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class MjolnirGeometry {
	private MjolnirGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_hammer = root.addOrReplaceChild("hammer", CubeListBuilder.create()
				.texOffs(8, 0).addBox(-4.5f, -9.0f, -2.5f, 9.0f, 5.0f, 5.0f)
				.texOffs(0, 0).addBox(-1.0f, -4.0f, -1.0f, 2.0f, 9.0f, 2.0f)
				.texOffs(36, 0).addBox(-1.5f, 5.0f, -1.5f, 3.0f, 2.0f, 3.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 16);
	}
}
