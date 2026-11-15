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
		PartDefinition p0 = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5f, -2.5f, -4.0f, 5.0f, 5.0f, 8.0f)
				.texOffs(0, 27).addBox(-2.0f, -0.5f, -5.0f, 4.0f, 4.0f, 2.0f)
				.texOffs(32, 33).addBox(-2.0f, -2.0f, 3.8f, 4.0f, 3.0f, 2.0f),
				PartPose.offset(0.0f, 16.0f, 0.0f));
		PartDefinition p1 = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(26, 0).addBox(-2.0f, -3.5f, -3.5f, 4.0f, 4.0f, 4.0f)
				.texOffs(12, 27).addBox(-1.5f, -1.2f, -1.0f, 3.0f, 3.0f, 3.0f)
				.texOffs(28, 39).addBox(-1.5f, -3.8f, -3.2f, 3.0f, 1.0f, 3.0f)
				.texOffs(44, 33).addBox(-2.0f, 0.6f, -1.6f, 4.0f, 1.0f, 4.0f),
				PartPose.offset(0.0f, 13.5f, -5.0f));
		PartDefinition p2 = p1.addOrReplaceChild("beak", CubeListBuilder.create()
				.texOffs(40, 39).addBox(-1.0f, -0.8f, -1.6f, 2.0f, 2.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, -3.5f, 0.35f, 0.0f, 0.0f));
		PartDefinition p3 = p2.addOrReplaceChild("beak_tip", CubeListBuilder.create()
				.texOffs(24, 44).addBox(-0.5f, 0.0f, -1.0f, 1.0f, 2.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -0.6f, -1.6f, 0.5f, 0.0f, 0.0f));
		PartDefinition p4 = p0.addOrReplaceChild("wing_r", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-6.0f, -0.5f, 0.0f, 6.0f, 1.0f, 6.0f)
				.texOffs(28, 44).addBox(-6.0f, 0.0f, 5.0f, 6.0f, 0.0f, 3.0f),
				PartPose.offsetAndRotation(-2.3f, -2.0f, -3.0f, 0.0f, 0.0f, 0.06f));
		PartDefinition p5 = p4.addOrReplaceChild("wing_r_mid", CubeListBuilder.create()
				.texOffs(24, 27).addBox(-5.0f, -0.5f, 0.0f, 5.0f, 1.0f, 5.0f)
				.texOffs(46, 44).addBox(-5.0f, 0.12f, 4.0f, 5.0f, 0.0f, 3.0f),
				PartPose.offsetAndRotation(-6.0f, 0.0f, 0.0f, 0.0f, 0.12f, 0.0f));
		PartDefinition p6 = p5.addOrReplaceChild("wing_r_tip", CubeListBuilder.create()
				.texOffs(0, 39).addBox(-3.0f, -0.4f, 0.0f, 3.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(-5.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.0f));
		PartDefinition p7 = p6.addOrReplaceChild("wing_r_p0", CubeListBuilder.create()
				.texOffs(0, 51).addBox(-9.0f, 0.0f, -1.0f, 9.0f, 0.0f, 2.0f),
				PartPose.offset(-2.0f, -0.3f, 0.8f));
		PartDefinition p8 = p6.addOrReplaceChild("wing_r_p1", CubeListBuilder.create()
				.texOffs(22, 51).addBox(-8.0f, 0.0f, -1.0f, 8.0f, 0.0f, 2.0f),
				PartPose.offsetAndRotation(-2.0f, -0.1f, 1.7f, 0.0f, 0.1f, 0.0f));
		PartDefinition p9 = p6.addOrReplaceChild("wing_r_p2", CubeListBuilder.create()
				.texOffs(42, 51).addBox(-7.0f, 0.0f, -1.0f, 7.0f, 0.0f, 2.0f),
				PartPose.offsetAndRotation(-2.0f, 0.1f, 2.6f, 0.0f, 0.2f, 0.0f));
		PartDefinition p10 = p6.addOrReplaceChild("wing_r_p3", CubeListBuilder.create()
				.texOffs(0, 53).addBox(-6.0f, 0.0f, -1.0f, 6.0f, 0.0f, 2.0f),
				PartPose.offsetAndRotation(-2.0f, 0.3f, 3.5f, 0.0f, 0.32f, 0.0f));
		PartDefinition p11 = p0.addOrReplaceChild("wing_l", CubeListBuilder.create()
				.texOffs(24, 13).addBox(0.0f, -0.5f, 0.0f, 6.0f, 1.0f, 6.0f)
				.texOffs(0, 48).addBox(0.0f, 0.0f, 5.0f, 6.0f, 0.0f, 3.0f),
				PartPose.offsetAndRotation(2.3f, -2.0f, -3.0f, 0.0f, 0.0f, -0.06f));
		PartDefinition p12 = p11.addOrReplaceChild("wing_l_mid", CubeListBuilder.create()
				.texOffs(44, 27).addBox(0.0f, -0.5f, 0.0f, 5.0f, 1.0f, 5.0f)
				.texOffs(18, 48).addBox(0.0f, 0.12f, 4.0f, 5.0f, 0.0f, 3.0f),
				PartPose.offsetAndRotation(6.0f, 0.0f, 0.0f, 0.0f, -0.12f, 0.0f));
		PartDefinition p13 = p12.addOrReplaceChild("wing_l_tip", CubeListBuilder.create()
				.texOffs(14, 39).addBox(0.0f, -0.4f, 0.0f, 3.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(5.0f, 0.0f, 0.0f, 0.0f, -0.2f, 0.0f));
		PartDefinition p14 = p13.addOrReplaceChild("wing_l_p0", CubeListBuilder.create()
				.texOffs(16, 53).addBox(0.0f, 0.0f, -1.0f, 9.0f, 0.0f, 2.0f),
				PartPose.offset(2.0f, -0.3f, 0.8f));
		PartDefinition p15 = p13.addOrReplaceChild("wing_l_p1", CubeListBuilder.create()
				.texOffs(38, 53).addBox(0.0f, 0.0f, -1.0f, 8.0f, 0.0f, 2.0f),
				PartPose.offsetAndRotation(2.0f, -0.1f, 1.7f, 0.0f, -0.1f, 0.0f));
		PartDefinition p16 = p13.addOrReplaceChild("wing_l_p2", CubeListBuilder.create()
				.texOffs(0, 55).addBox(0.0f, 0.0f, -1.0f, 7.0f, 0.0f, 2.0f),
				PartPose.offsetAndRotation(2.0f, 0.1f, 2.6f, 0.0f, -0.2f, 0.0f));
		PartDefinition p17 = p13.addOrReplaceChild("wing_l_p3", CubeListBuilder.create()
				.texOffs(18, 55).addBox(0.0f, 0.0f, -1.0f, 6.0f, 0.0f, 2.0f),
				PartPose.offsetAndRotation(2.0f, 0.3f, 3.5f, 0.0f, -0.32f, 0.0f));
		PartDefinition p18 = p0.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(48, 39).addBox(-1.5f, -0.5f, -0.5f, 3.0f, 1.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 5.5f, -0.12f, 0.0f, 0.0f));
		PartDefinition p19 = p18.addOrReplaceChild("tail_f0", CubeListBuilder.create()
				.texOffs(0, 33).addBox(-1.0f, 0.0f, 0.0f, 2.0f, 0.0f, 6.0f),
				PartPose.offsetAndRotation(-1.0f, -0.4f, 0.5f, 0.0f, -0.2f, 0.0f));
		PartDefinition p20 = p18.addOrReplaceChild("tail_f1", CubeListBuilder.create()
				.texOffs(0, 20).addBox(-1.0f, 0.0f, 0.0f, 2.0f, 0.0f, 7.0f),
				PartPose.offsetAndRotation(-0.5f, -0.2f, 0.5f, 0.0f, -0.1f, 0.0f));
		PartDefinition p21 = p18.addOrReplaceChild("tail_f2", CubeListBuilder.create()
				.texOffs(18, 20).addBox(-1.0f, 0.0f, 0.0f, 2.0f, 0.0f, 7.0f),
				PartPose.offset(0.0f, 0.0f, 0.5f));
		PartDefinition p22 = p18.addOrReplaceChild("tail_f3", CubeListBuilder.create()
				.texOffs(36, 20).addBox(-1.0f, 0.0f, 0.0f, 2.0f, 0.0f, 7.0f),
				PartPose.offsetAndRotation(0.5f, 0.2f, 0.5f, 0.0f, 0.1f, 0.0f));
		PartDefinition p23 = p18.addOrReplaceChild("tail_f4", CubeListBuilder.create()
				.texOffs(16, 33).addBox(-1.0f, 0.0f, 0.0f, 2.0f, 0.0f, 6.0f),
				PartPose.offsetAndRotation(1.0f, 0.4f, 0.5f, 0.0f, 0.2f, 0.0f));
		PartDefinition p24 = p0.addOrReplaceChild("leg_r", CubeListBuilder.create()
				.texOffs(0, 44).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f)
				.texOffs(8, 44).addBox(-0.5f, 0.8f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(34, 48).addBox(-1.0f, 3.7f, -2.0f, 2.0f, 0.0f, 3.0f),
				PartPose.offsetAndRotation(-1.2f, 2.5f, 1.0f, 0.9f, 0.0f, 0.0f));
		PartDefinition p25 = p0.addOrReplaceChild("leg_l", CubeListBuilder.create()
				.texOffs(12, 44).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f)
				.texOffs(20, 44).addBox(-0.5f, 0.8f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(44, 48).addBox(-1.0f, 3.7f, -2.0f, 2.0f, 0.0f, 3.0f),
				PartPose.offsetAndRotation(1.2f, 2.5f, 1.0f, 0.9f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
