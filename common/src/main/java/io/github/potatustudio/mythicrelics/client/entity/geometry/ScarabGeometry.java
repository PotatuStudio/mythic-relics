package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad scarab. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class ScarabGeometry {
	private ScarabGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p0 = root.addOrReplaceChild("shell", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.5f, -1.5f, -5.5f, 9.0f, 3.0f, 11.0f),
				PartPose.offset(0.0f, 21.0f, 2.0f));
		PartDefinition p1 = p0.addOrReplaceChild("left_elytron", CubeListBuilder.create()
				.texOffs(0, 14).addBox(0.0f, -3.5f, -6.0f, 6.0f, 3.0f, 11.0f)
				.texOffs(34, 28).addBox(0.0f, -4.5f, -5.0f, 5.0f, 1.0f, 10.0f)
				.texOffs(30, 42).addBox(0.0f, -5.5f, -3.5f, 3.0f, 1.0f, 7.0f)
				.texOffs(34, 61).addBox(0.0f, -3.2f, 5.0f, 5.0f, 3.0f, 1.0f)
				.texOffs(22, 68).addBox(0.0f, -2.8f, 6.0f, 3.0f, 2.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p2 = p0.addOrReplaceChild("right_elytron", CubeListBuilder.create()
				.texOffs(0, 28).addBox(-6.0f, -3.5f, -6.0f, 6.0f, 3.0f, 11.0f)
				.texOffs(0, 42).addBox(-5.0f, -4.5f, -5.0f, 5.0f, 1.0f, 10.0f)
				.texOffs(0, 53).addBox(-3.0f, -5.5f, -3.5f, 3.0f, 1.0f, 7.0f)
				.texOffs(46, 61).addBox(-5.0f, -3.2f, 5.0f, 5.0f, 3.0f, 1.0f)
				.texOffs(30, 68).addBox(-3.0f, -2.8f, 6.0f, 3.0f, 2.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p3 = p0.addOrReplaceChild("suture", CubeListBuilder.create()
				.texOffs(48, 53).addBox(-0.5f, -6.0f, -3.0f, 1.0f, 1.0f, 6.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p4 = root.addOrReplaceChild("pronotum", CubeListBuilder.create()
				.texOffs(20, 53).addBox(-5.0f, -4.0f, -3.0f, 10.0f, 4.0f, 4.0f)
				.texOffs(0, 68).addBox(-4.0f, -5.0f, -2.5f, 8.0f, 1.0f, 3.0f),
				PartPose.offset(0.0f, 21.0f, -4.5f));
		PartDefinition p5 = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(14, 61).addBox(-3.5f, -2.0f, -3.0f, 7.0f, 2.0f, 3.0f)
				.texOffs(12, 80).addBox(-4.0f, -1.2f, -3.8f, 8.0f, 0.0f, 1.0f),
				PartPose.offset(0.0f, 21.0f, -7.5f));
		PartDefinition p6 = p5.addOrReplaceChild("left_antenna", CubeListBuilder.create()
				.texOffs(38, 68).addBox(-0.5f, -0.5f, -2.0f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(2.5f, -1.0f, -2.5f, -0.35f, -0.6f, 0.0f));
		PartDefinition p7 = p6.addOrReplaceChild("left_antenna_lamella0", CubeListBuilder.create()
				.texOffs(44, 68).addBox(-0.5f, -0.5f, -2.0f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, -2.0f, -0.45f, 0.0f, 0.0f));
		PartDefinition p8 = p6.addOrReplaceChild("left_antenna_lamella1", CubeListBuilder.create()
				.texOffs(50, 68).addBox(-0.5f, -0.5f, -2.0f, 1.0f, 1.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, -2.0f));
		PartDefinition p9 = p6.addOrReplaceChild("left_antenna_lamella2", CubeListBuilder.create()
				.texOffs(56, 68).addBox(-0.5f, -0.5f, -2.0f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, -2.0f, 0.45f, 0.0f, 0.0f));
		PartDefinition p10 = p5.addOrReplaceChild("right_antenna", CubeListBuilder.create()
				.texOffs(0, 72).addBox(-0.5f, -0.5f, -2.0f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(-2.5f, -1.0f, -2.5f, -0.35f, 0.6f, 0.0f));
		PartDefinition p11 = p10.addOrReplaceChild("right_antenna_lamella0", CubeListBuilder.create()
				.texOffs(6, 72).addBox(-0.5f, -0.5f, -2.0f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, -2.0f, -0.45f, 0.0f, 0.0f));
		PartDefinition p12 = p10.addOrReplaceChild("right_antenna_lamella1", CubeListBuilder.create()
				.texOffs(12, 72).addBox(-0.5f, -0.5f, -2.0f, 1.0f, 1.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, -2.0f));
		PartDefinition p13 = p10.addOrReplaceChild("right_antenna_lamella2", CubeListBuilder.create()
				.texOffs(18, 72).addBox(-0.5f, -0.5f, -2.0f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, -2.0f, 0.45f, 0.0f, 0.0f));
		PartDefinition p14 = p5.addOrReplaceChild("sun", CubeListBuilder.create()
				.texOffs(0, 61).addBox(-3.0f, -6.0f, 0.0f, 6.0f, 6.0f, 1.0f),
				PartPose.offset(0.0f, -2.0f, -1.0f));
		PartDefinition p15 = root.addOrReplaceChild("left_leg0", CubeListBuilder.create()
				.texOffs(24, 72).addBox(0.0f, -0.5f, -1.0f, 5.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(2.6f, 20.98f, -5.5f, 0.0f, 0.55f, -0.25f));
		PartDefinition p16 = p15.addOrReplaceChild("left_leg0_tibia", CubeListBuilder.create()
				.texOffs(0, 78).addBox(0.0f, -0.5f, -0.5f, 5.0f, 1.0f, 1.0f)
				.texOffs(30, 80).addBox(0.0f, 0.0f, -1.5f, 5.0f, 0.0f, 1.0f),
				PartPose.offsetAndRotation(5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.05f));
		PartDefinition p17 = root.addOrReplaceChild("right_leg0", CubeListBuilder.create()
				.texOffs(38, 72).addBox(-5.0f, -0.5f, -1.0f, 5.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(-2.6f, 20.98f, -5.5f, 0.0f, -0.55f, 0.25f));
		PartDefinition p18 = p17.addOrReplaceChild("right_leg0_tibia", CubeListBuilder.create()
				.texOffs(12, 78).addBox(-5.0f, -0.5f, -0.5f, 5.0f, 1.0f, 1.0f)
				.texOffs(42, 80).addBox(-5.0f, 0.0f, -1.5f, 5.0f, 0.0f, 1.0f),
				PartPose.offsetAndRotation(-5.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.05f));
		PartDefinition p19 = root.addOrReplaceChild("left_leg1", CubeListBuilder.create()
				.texOffs(0, 75).addBox(0.0f, -0.5f, -1.0f, 5.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(3.0f, 21.3f, -1.0f, 0.0f, 0.0f, -0.25f));
		PartDefinition p20 = p19.addOrReplaceChild("left_leg1_tibia", CubeListBuilder.create()
				.texOffs(24, 78).addBox(0.0f, -0.5f, -0.5f, 5.0f, 1.0f, 1.0f)
				.texOffs(0, 82).addBox(0.0f, 0.0f, 0.5f, 5.0f, 0.0f, 1.0f),
				PartPose.offsetAndRotation(5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.05f));
		PartDefinition p21 = root.addOrReplaceChild("right_leg1", CubeListBuilder.create()
				.texOffs(14, 75).addBox(-5.0f, -0.5f, -1.0f, 5.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(-3.0f, 21.3f, -1.0f, 0.0f, 0.0f, 0.25f));
		PartDefinition p22 = p21.addOrReplaceChild("right_leg1_tibia", CubeListBuilder.create()
				.texOffs(36, 78).addBox(-5.0f, -0.5f, -0.5f, 5.0f, 1.0f, 1.0f)
				.texOffs(12, 82).addBox(-5.0f, 0.0f, 0.5f, 5.0f, 0.0f, 1.0f),
				PartPose.offsetAndRotation(-5.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.05f));
		PartDefinition p23 = root.addOrReplaceChild("left_leg2", CubeListBuilder.create()
				.texOffs(28, 75).addBox(0.0f, -0.5f, -1.0f, 5.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(3.2f, 20.98f, 3.5f, 0.0f, -0.55f, -0.25f));
		PartDefinition p24 = p23.addOrReplaceChild("left_leg2_tibia", CubeListBuilder.create()
				.texOffs(48, 78).addBox(0.0f, -0.5f, -0.5f, 5.0f, 1.0f, 1.0f)
				.texOffs(24, 82).addBox(0.0f, 0.0f, 0.5f, 5.0f, 0.0f, 1.0f),
				PartPose.offsetAndRotation(5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.05f));
		PartDefinition p25 = root.addOrReplaceChild("right_leg2", CubeListBuilder.create()
				.texOffs(42, 75).addBox(-5.0f, -0.5f, -1.0f, 5.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(-3.2f, 20.98f, 3.5f, 0.0f, 0.55f, 0.25f));
		PartDefinition p26 = p25.addOrReplaceChild("right_leg2_tibia", CubeListBuilder.create()
				.texOffs(0, 80).addBox(-5.0f, -0.5f, -0.5f, 5.0f, 1.0f, 1.0f)
				.texOffs(36, 82).addBox(-5.0f, 0.0f, 0.5f, 5.0f, 0.0f, 1.0f),
				PartPose.offsetAndRotation(-5.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.05f));
		return LayerDefinition.create(mesh, 64, 128);
	}
}
