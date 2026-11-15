package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad boat/skidbladnir. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class SkidbladnirGeometry {
	private SkidbladnirGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p0 = root.addOrReplaceChild("bottom", CubeListBuilder.create()
				.texOffs(40, 36).addBox(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 3.0f, 1.0f, 1.571f, 0.0f, 0.0f));
		PartDefinition p1 = root.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(28, 56).addBox(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f),
				PartPose.offsetAndRotation(-15.0f, 4.0f, 4.0f, 0.0f, 4.712f, 0.0f));
		PartDefinition p2 = root.addOrReplaceChild("front", CubeListBuilder.create()
				.texOffs(68, 56).addBox(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f),
				PartPose.offsetAndRotation(15.0f, 4.0f, 0.0f, 0.0f, 1.571f, 0.0f));
		PartDefinition p3 = root.addOrReplaceChild("right", CubeListBuilder.create()
				.texOffs(0, 69).addBox(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 4.0f, -9.0f, 0.0f, 3.142f, 0.0f));
		PartDefinition p4 = root.addOrReplaceChild("left", CubeListBuilder.create()
				.texOffs(60, 69).addBox(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f),
				PartPose.offset(0.0f, 4.0f, 9.0f));
		PartDefinition p5 = root.addOrReplaceChild("left_paddle", CubeListBuilder.create()
				.texOffs(50, 0).addBox(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f)
				.texOffs(102, 36).addBox(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f),
				PartPose.offsetAndRotation(3.0f, -5.0f, 9.0f, 0.0f, 0.0f, 0.1963f));
		PartDefinition p6 = root.addOrReplaceChild("right_paddle", CubeListBuilder.create()
				.texOffs(0, 36).addBox(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f)
				.texOffs(0, 56).addBox(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f),
				PartPose.offsetAndRotation(3.0f, -5.0f, -9.0f, 0.0f, 3.142f, 0.1963f));
		PartDefinition p7 = root.addOrReplaceChild("prow", CubeListBuilder.create()
				.texOffs(16, 56).addBox(-1.0f, -9.0f, -1.5f, 3.0f, 10.0f, 3.0f)
				.texOffs(0, 77).addBox(1.0f, -11.0f, -1.5f, 5.0f, 3.0f, 3.0f),
				PartPose.offset(16.0f, -2.0f, 0.0f));
		PartDefinition p8 = root.addOrReplaceChild("mast", CubeListBuilder.create()
				.texOffs(42, 0).addBox(-1.0f, -30.0f, -1.0f, 2.0f, 30.0f, 2.0f),
				PartPose.offset(-10.0f, 1.0f, 0.0f));
		PartDefinition p9 = p8.addOrReplaceChild("sail", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5f, 0.0f, -10.0f, 1.0f, 16.0f, 20.0f),
				PartPose.offset(0.0f, -27.0f, 0.0f));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
