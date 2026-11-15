package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad jackal. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class JackalGeometry {
	private JackalGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p0 = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5f, -3.0f, -7.0f, 5.0f, 7.0f, 6.0f)
				.texOffs(22, 0).addBox(-2.0f, -2.5f, -1.5f, 4.0f, 4.0f, 7.0f)
				.texOffs(14, 13).addBox(-2.5f, -2.8f, 4.5f, 5.0f, 5.0f, 4.0f),
				PartPose.offset(0.0f, 11.0f, 0.0f));
		PartDefinition p1 = p0.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(32, 13).addBox(-1.5f, -1.5f, -5.5f, 3.0f, 3.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -2.0f, -6.0f, -0.6f, 0.0f, 0.0f));
		PartDefinition p2 = p1.addOrReplaceChild("sash", CubeListBuilder.create()
				.texOffs(0, 38).addBox(-2.0f, -2.0f, -1.0f, 4.0f, 4.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, -1.5f));
		PartDefinition p3 = p0.addOrReplaceChild("collar", CubeListBuilder.create()
				.texOffs(12, 31).addBox(-3.0f, -3.5f, -7.5f, 6.0f, 3.0f, 3.0f)
				.texOffs(54, 38).addBox(-2.0f, -0.5f, -7.6f, 4.0f, 3.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p4 = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 23).addBox(-2.0f, -2.5f, -3.0f, 4.0f, 4.0f, 4.0f)
				.texOffs(32, 23).addBox(-1.0f, -0.5f, -8.0f, 2.0f, 2.0f, 5.0f)
				.texOffs(0, 43).addBox(-1.5f, -1.2f, -4.0f, 3.0f, 3.0f, 1.0f)
				.texOffs(48, 43).addBox(-0.5f, -0.8f, -8.4f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 6.2f, -10.2f));
		PartDefinition p5 = p4.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(10, 38).addBox(-1.0f, 0.0f, -4.0f, 2.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 1.5f, -3.5f, 0.06f, 0.0f, 0.0f));
		PartDefinition p6 = p4.addOrReplaceChild("left_ear", CubeListBuilder.create()
				.texOffs(46, 23).addBox(-1.5f, -6.0f, -0.5f, 3.0f, 6.0f, 1.0f),
				PartPose.offsetAndRotation(1.2f, -2.5f, -0.5f, -0.1f, 0.0f, 0.12f));
		PartDefinition p7 = p4.addOrReplaceChild("right_ear", CubeListBuilder.create()
				.texOffs(54, 23).addBox(-1.5f, -6.0f, -0.5f, 3.0f, 6.0f, 1.0f),
				PartPose.offsetAndRotation(-1.2f, -2.5f, -0.5f, -0.1f, 0.0f, -0.12f));
		PartDefinition p8 = root.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(22, 38).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 9.0f, 8.2f, 0.55f, 0.0f, 0.0f));
		PartDefinition p9 = p8.addOrReplaceChild("tail_mid", CubeListBuilder.create()
				.texOffs(0, 31).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 4.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 2.7f, 0.0f, 0.15f, 0.0f, 0.0f));
		PartDefinition p10 = p9.addOrReplaceChild("tail_tip", CubeListBuilder.create()
				.texOffs(30, 38).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 3.8f, 0.0f, 0.1f, 0.0f, 0.0f));
		PartDefinition p11 = root.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
				.texOffs(16, 23).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 6.0f, 2.0f),
				PartPose.offset(1.4f, 14.0f, -4.5f));
		PartDefinition p12 = p11.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create()
				.texOffs(30, 31).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 4.0f, 2.0f)
				.texOffs(8, 43).addBox(-1.0f, 4.0f, -2.0f, 2.0f, 1.0f, 3.0f),
				PartPose.offset(0.0f, 5.0f, 0.0f));
		PartDefinition p13 = root.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
				.texOffs(24, 23).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 6.0f, 2.0f),
				PartPose.offset(-1.4f, 14.0f, -4.5f));
		PartDefinition p14 = p13.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create()
				.texOffs(38, 31).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 4.0f, 2.0f)
				.texOffs(18, 43).addBox(-1.0f, 4.0f, -2.0f, 2.0f, 1.0f, 3.0f),
				PartPose.offset(0.0f, 5.0f, 0.0f));
		PartDefinition p15 = root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
				.texOffs(44, 0).addBox(-1.5f, -2.0f, -2.0f, 3.0f, 6.0f, 4.0f),
				PartPose.offsetAndRotation(1.5f, 12.88f, 6.2f, -0.25f, 0.0f, 0.0f));
		PartDefinition p16 = p15.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create()
				.texOffs(46, 31).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 4.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 3.8f, 0.4f, 0.75f, 0.0f, 0.0f));
		PartDefinition p17 = p16.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create()
				.texOffs(38, 38).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f)
				.texOffs(28, 43).addBox(-1.0f, 3.0f, -2.0f, 2.0f, 1.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 3.8f, 0.0f, -0.5f, 0.0f, 0.0f));
		PartDefinition p18 = root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-1.5f, -2.0f, -2.0f, 3.0f, 6.0f, 4.0f),
				PartPose.offsetAndRotation(-1.5f, 12.88f, 6.2f, -0.25f, 0.0f, 0.0f));
		PartDefinition p19 = p18.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create()
				.texOffs(54, 31).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 4.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 3.8f, 0.4f, 0.75f, 0.0f, 0.0f));
		PartDefinition p20 = p19.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create()
				.texOffs(46, 38).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f)
				.texOffs(38, 43).addBox(-1.0f, 3.0f, -2.0f, 2.0f, 1.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 3.8f, 0.0f, -0.5f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
