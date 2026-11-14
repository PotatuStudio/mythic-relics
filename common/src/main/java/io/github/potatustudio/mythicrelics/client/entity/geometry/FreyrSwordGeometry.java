package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad weapon/freyr_sword. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class FreyrSwordGeometry {
	private FreyrSwordGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_sword = root.addOrReplaceChild("sword", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1.5f, -13.0f, -0.5f, 3.0f, 17.0f, 1.0f)
				.texOffs(0, 23).addBox(-1.0f, -15.0f, -0.5f, 2.0f, 2.0f, 1.0f)
				.texOffs(6, 23).addBox(-0.5f, -16.0f, -0.5f, 1.0f, 1.0f, 1.0f)
				.texOffs(12, 18).addBox(-4.0f, 4.0f, -1.0f, 8.0f, 2.0f, 2.0f)
				.texOffs(16, 0).addBox(-5.0f, 2.0f, -1.0f, 1.0f, 3.0f, 2.0f)
				.texOffs(22, 0).addBox(4.0f, 2.0f, -1.0f, 1.0f, 3.0f, 2.0f)
				.texOffs(8, 0).addBox(-1.0f, 6.0f, -1.0f, 2.0f, 5.0f, 2.0f)
				.texOffs(0, 18).addBox(-1.5f, 11.0f, -1.5f, 3.0f, 2.0f, 3.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 32, 32);
	}
}
