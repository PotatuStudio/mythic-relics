package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad weapon/gungnir. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class GungnirGeometry {
	private GungnirGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_spear = root.addOrReplaceChild("spear", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5f, -8.0f, -0.5f, 1.0f, 24.0f, 1.0f)
				.texOffs(18, 0).addBox(-1.0f, -10.0f, -1.0f, 2.0f, 2.0f, 2.0f)
				.texOffs(4, 0).addBox(-1.5f, -16.0f, -0.5f, 3.0f, 6.0f, 1.0f)
				.texOffs(26, 0).addBox(-2.5f, -14.0f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(0, 25).addBox(1.5f, -14.0f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(12, 0).addBox(-1.0f, -20.0f, -0.5f, 2.0f, 4.0f, 1.0f)
				.texOffs(12, 25).addBox(-0.5f, -22.0f, -0.5f, 1.0f, 2.0f, 1.0f)
				.texOffs(4, 25).addBox(-1.0f, 16.0f, -1.0f, 2.0f, 2.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 32, 32);
	}
}
