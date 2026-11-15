package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad sleipnir. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class SleipnirGeometry {
	private SleipnirGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p0 = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0f, -5.0f, -12.0f, 10.0f, 10.0f, 24.0f)
				.texOffs(176, 0).addBox(-5.5f, -3.5f, -14.0f, 11.0f, 8.0f, 6.0f)
				.texOffs(106, 0).addBox(-5.5f, -5.5f, 4.0f, 11.0f, 9.0f, 9.0f)
				.texOffs(60, 47).addBox(-4.0f, -6.5f, 5.0f, 8.0f, 1.0f, 7.0f)
				.texOffs(0, 34).addBox(-4.5f, 4.5f, -8.0f, 9.0f, 1.0f, 12.0f)
				.texOffs(118, 47).addBox(-2.0f, -6.5f, -12.0f, 4.0f, 2.0f, 5.0f),
				PartPose.offset(0.0f, 8.0f, 0.0f));
		PartDefinition p1 = p0.addOrReplaceChild("saddle", CubeListBuilder.create()
				.texOffs(210, 0).addBox(-5.5f, -5.3f, -7.0f, 11.0f, 4.0f, 10.0f)
				.texOffs(0, 47).addBox(-3.5f, -6.3f, -6.0f, 7.0f, 1.0f, 8.0f)
				.texOffs(0, 67).addBox(-2.0f, -8.3f, -6.8f, 4.0f, 2.0f, 1.0f)
				.texOffs(10, 67).addBox(-3.0f, -8.3f, 1.5f, 6.0f, 2.0f, 1.0f)
				.texOffs(92, 67).addBox(-2.5f, -9.3f, 1.5f, 5.0f, 1.0f, 1.0f)
				.texOffs(30, 47).addBox(-5.4f, -1.3f, -3.5f, 11.0f, 7.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p2 = p0.addOrReplaceChild("stirrup_l", CubeListBuilder.create()
				.texOffs(56, 47).addBox(0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 1.0f)
				.texOffs(114, 62).addBox(-0.5f, 8.0f, -1.0f, 1.0f, 1.0f, 3.0f),
				PartPose.offset(5.7f, -2.0f, -2.5f));
		PartDefinition p3 = p0.addOrReplaceChild("stirrup_r", CubeListBuilder.create()
				.texOffs(58, 47).addBox(0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 1.0f)
				.texOffs(122, 62).addBox(-0.5f, 8.0f, -1.0f, 1.0f, 1.0f, 3.0f),
				PartPose.offset(-5.7f, -2.0f, -2.5f));
		PartDefinition p4 = p0.addOrReplaceChild("breast_collar", CubeListBuilder.create()
				.texOffs(24, 67).addBox(-6.0f, -1.0f, -14.5f, 12.0f, 2.0f, 1.0f)
				.texOffs(158, 47).addBox(-1.5f, 1.0f, -14.6f, 3.0f, 5.0f, 1.0f)
				.texOffs(50, 67).addBox(-4.0f, 1.0f, -14.6f, 2.0f, 2.0f, 1.0f)
				.texOffs(56, 67).addBox(3.0f, 1.0f, -14.6f, 2.0f, 2.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p5 = root.addOrReplaceChild("head_parts", CubeListBuilder.create()
				.texOffs(80, 0).addBox(-2.5f, -7.0f, -2.0f, 5.0f, 13.0f, 8.0f)
				.texOffs(146, 0).addBox(-3.0f, 0.5f, -2.5f, 6.0f, 6.0f, 9.0f),
				PartPose.offsetAndRotation(0.0f, 4.0f, -11.0f, 0.5236f, 0.0f, 0.0f));
		PartDefinition p6 = p5.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(42, 34).addBox(-3.0f, -11.5f, -2.5f, 6.0f, 6.0f, 7.0f)
				.texOffs(136, 47).addBox(-3.5f, -8.2f, 1.0f, 7.0f, 3.0f, 4.0f)
				.texOffs(90, 47).addBox(-3.5f, -12.2f, 0.0f, 7.0f, 7.0f, 1.0f)
				.texOffs(166, 47).addBox(-2.5f, -11.4f, -6.0f, 5.0f, 5.0f, 1.0f)
				.texOffs(178, 47).addBox(-1.5f, -12.0f, -4.5f, 3.0f, 1.0f, 5.0f)
				.texOffs(104, 67).addBox(-3.0f, -8.0f, -6.5f, 1.0f, 1.0f, 1.0f)
				.texOffs(108, 67).addBox(2.0f, -8.0f, -6.5f, 1.0f, 1.0f, 1.0f)
				.texOffs(130, 62).addBox(-0.5f, -12.6f, -1.2f, 1.0f, 2.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p7 = p5.addOrReplaceChild("upper_mouth", CubeListBuilder.create()
				.texOffs(214, 34).addBox(-2.0f, -11.0f, -7.5f, 4.0f, 4.0f, 6.0f)
				.texOffs(96, 56).addBox(-1.5f, -7.5f, -7.0f, 3.0f, 1.0f, 4.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p8 = p6.addOrReplaceChild("left_ear", CubeListBuilder.create()
				.texOffs(136, 62).addBox(-1.0f, -3.0f, -0.5f, 2.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(1.75f, -11.5f, 3.0f, 0.2f, 0.0f, 0.2f));
		PartDefinition p9 = p8.addOrReplaceChild("left_ear_tip", CubeListBuilder.create()
				.texOffs(112, 67).addBox(-0.5f, -1.0f, -0.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, -3.0f, 0.0f));
		PartDefinition p10 = p6.addOrReplaceChild("right_ear", CubeListBuilder.create()
				.texOffs(142, 62).addBox(-1.0f, -3.0f, -0.5f, 2.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(-1.75f, -11.5f, 3.0f, 0.2f, 0.0f, -0.2f));
		PartDefinition p11 = p10.addOrReplaceChild("right_ear_tip", CubeListBuilder.create()
				.texOffs(116, 67).addBox(-0.5f, -1.0f, -0.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, -3.0f, 0.0f));
		PartDefinition p12 = p5.addOrReplaceChild("mane", CubeListBuilder.create()
				.texOffs(110, 56).addBox(-1.5f, -12.5f, 5.5f, 3.0f, 3.0f, 2.0f)
				.texOffs(194, 47).addBox(-1.0f, -9.5f, 5.5f, 2.0f, 3.0f, 3.0f)
				.texOffs(62, 67).addBox(-1.5f, -8.5f, 6.0f, 3.0f, 1.0f, 2.0f)
				.texOffs(120, 56).addBox(-1.5f, -6.5f, 5.5f, 3.0f, 3.0f, 2.0f)
				.texOffs(204, 47).addBox(-1.0f, -3.5f, 5.5f, 2.0f, 3.0f, 3.0f)
				.texOffs(72, 67).addBox(-1.5f, -2.5f, 6.0f, 3.0f, 1.0f, 2.0f)
				.texOffs(130, 56).addBox(-1.5f, -0.5f, 5.5f, 3.0f, 3.0f, 2.0f)
				.texOffs(214, 47).addBox(-1.0f, 2.5f, 5.5f, 2.0f, 3.0f, 3.0f)
				.texOffs(82, 67).addBox(-1.5f, 3.5f, 6.0f, 3.0f, 1.0f, 2.0f)
				.texOffs(68, 0).addBox(-2.6f, -10.5f, 0.5f, 0.0f, 18.0f, 6.0f)
				.texOffs(82, 34).addBox(2.6f, -10.5f, 4.5f, 0.0f, 9.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p13 = root.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
				.texOffs(86, 34).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 7.0f, 4.0f),
				PartPose.offset(2.3f, 12.0f, -10.8f));
		PartDefinition p14 = p13.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create()
				.texOffs(140, 56).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(152, 56).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(148, 62).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(224, 47).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f),
				PartPose.offset(0.0f, 5.0f, 0.0f));
		PartDefinition p15 = root.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
				.texOffs(102, 34).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 7.0f, 4.0f),
				PartPose.offset(-2.3f, 12.0f, -10.8f));
		PartDefinition p16 = p15.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create()
				.texOffs(162, 56).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(174, 56).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(160, 62).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(240, 47).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f),
				PartPose.offset(0.0f, 5.0f, 0.0f));
		PartDefinition p17 = root.addOrReplaceChild("left_front_middle_leg", CubeListBuilder.create()
				.texOffs(118, 34).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 7.0f, 4.0f),
				PartPose.offset(3.1f, 12.0f, -5.8f));
		PartDefinition p18 = p17.addOrReplaceChild("left_front_middle_leg_lower", CubeListBuilder.create()
				.texOffs(184, 56).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(196, 56).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(172, 62).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(0, 56).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f),
				PartPose.offset(0.0f, 5.0f, 0.0f));
		PartDefinition p19 = root.addOrReplaceChild("right_front_middle_leg", CubeListBuilder.create()
				.texOffs(134, 34).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 7.0f, 4.0f),
				PartPose.offset(-3.1f, 12.0f, -5.8f));
		PartDefinition p20 = p19.addOrReplaceChild("right_front_middle_leg_lower", CubeListBuilder.create()
				.texOffs(206, 56).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(218, 56).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(184, 62).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(16, 56).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f),
				PartPose.offset(0.0f, 5.0f, 0.0f));
		PartDefinition p21 = root.addOrReplaceChild("left_hind_middle_leg", CubeListBuilder.create()
				.texOffs(150, 34).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 7.0f, 4.0f),
				PartPose.offset(3.1f, 12.0f, 4.5f));
		PartDefinition p22 = p21.addOrReplaceChild("left_hind_middle_leg_lower", CubeListBuilder.create()
				.texOffs(228, 56).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(240, 56).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(196, 62).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(32, 56).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f)
				.texOffs(0, 62).addBox(-1.0f, -1.0f, 1.0f, 2.0f, 3.0f, 2.0f),
				PartPose.offset(0.0f, 5.0f, 0.0f));
		PartDefinition p23 = root.addOrReplaceChild("right_hind_middle_leg", CubeListBuilder.create()
				.texOffs(166, 34).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 7.0f, 4.0f),
				PartPose.offset(-3.1f, 12.0f, 4.5f));
		PartDefinition p24 = p23.addOrReplaceChild("right_hind_middle_leg_lower", CubeListBuilder.create()
				.texOffs(8, 62).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(20, 62).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(208, 62).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(48, 56).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f)
				.texOffs(30, 62).addBox(-1.0f, -1.0f, 1.0f, 2.0f, 3.0f, 2.0f),
				PartPose.offset(0.0f, 5.0f, 0.0f));
		PartDefinition p25 = root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
				.texOffs(182, 34).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 7.0f, 4.0f),
				PartPose.offset(2.3f, 12.0f, 9.5f));
		PartDefinition p26 = p25.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create()
				.texOffs(38, 62).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(50, 62).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(220, 62).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(64, 56).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f)
				.texOffs(60, 62).addBox(-1.0f, -1.0f, 1.0f, 2.0f, 3.0f, 2.0f),
				PartPose.offset(0.0f, 5.0f, 0.0f));
		PartDefinition p27 = root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
				.texOffs(198, 34).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 7.0f, 4.0f),
				PartPose.offset(-2.3f, 12.0f, 9.5f));
		PartDefinition p28 = p27.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create()
				.texOffs(68, 62).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(80, 62).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(232, 62).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(80, 56).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f)
				.texOffs(90, 62).addBox(-1.0f, -1.0f, 1.0f, 2.0f, 3.0f, 2.0f),
				PartPose.offset(0.0f, 5.0f, 0.0f));
		PartDefinition p29 = p0.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(106, 47).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 5.0f, 3.0f)
				.texOffs(98, 62).addBox(-2.0f, 3.0f, -2.0f, 4.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, -5.0f, 12.5f, 0.5236f, 0.0f, 0.0f));
		PartDefinition p30 = p29.addOrReplaceChild("tail_tip", CubeListBuilder.create()
				.texOffs(68, 34).addBox(-2.0f, 0.0f, -1.5f, 4.0f, 9.0f, 3.0f)
				.texOffs(234, 34).addBox(-2.5f, 7.0f, -2.0f, 5.0f, 6.0f, 4.0f)
				.texOffs(244, 62).addBox(-1.0f, 12.5f, -1.0f, 2.0f, 2.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 4.5f, 0.0f, -0.25f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 256, 128);
	}
}
