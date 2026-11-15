package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad long_dragon. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class LongDragonGeometry {
	private LongDragonGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_seg0 = root.addOrReplaceChild("seg0", CubeListBuilder.create()
				.texOffs(120, 26).addBox(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 10.0f),
				PartPose.offset(0.0f, 16.0f, -16.0f));
		PartDefinition p_crest0 = p_seg0.addOrReplaceChild("crest0", CubeListBuilder.create()
				.texOffs(176, 47).addBox(0.0f, -4.0f, 1.0f, 0.0f, 4.0f, 8.0f),
				PartPose.offset(0.0f, -4.0f, 0.0f));
		PartDefinition p_seg1 = p_seg0.addOrReplaceChild("seg1", CubeListBuilder.create()
				.texOffs(110, 0).addBox(-5.0f, -5.0f, 0.0f, 10.0f, 10.0f, 12.0f),
				PartPose.offset(0.0f, -1.0f, 10.0f));
		PartDefinition p_crest1 = p_seg1.addOrReplaceChild("crest1", CubeListBuilder.create()
				.texOffs(230, 26).addBox(0.0f, -5.0f, 1.0f, 0.0f, 5.0f, 10.0f),
				PartPose.offset(0.0f, -5.0f, 0.0f));
		PartDefinition p_seg2 = p_seg1.addOrReplaceChild("seg2", CubeListBuilder.create()
				.texOffs(64, 0).addBox(-5.5f, -5.5f, 0.0f, 11.0f, 11.0f, 12.0f),
				PartPose.offset(0.0f, -0.5f, 12.0f));
		PartDefinition p_crest2 = p_seg2.addOrReplaceChild("crest2", CubeListBuilder.create()
				.texOffs(0, 47).addBox(0.0f, -5.0f, 1.0f, 0.0f, 5.0f, 10.0f),
				PartPose.offset(0.0f, -5.5f, 0.0f));
		PartDefinition p_seg3 = p_seg2.addOrReplaceChild("seg3", CubeListBuilder.create()
				.texOffs(154, 0).addBox(-5.0f, -5.0f, 0.0f, 10.0f, 10.0f, 12.0f),
				PartPose.offset(0.0f, 0.5f, 12.0f));
		PartDefinition p_crest3 = p_seg3.addOrReplaceChild("crest3", CubeListBuilder.create()
				.texOffs(20, 47).addBox(0.0f, -5.0f, 1.0f, 0.0f, 5.0f, 10.0f),
				PartPose.offset(0.0f, -5.0f, 0.0f));
		PartDefinition p_seg4 = p_seg3.addOrReplaceChild("seg4", CubeListBuilder.create()
				.texOffs(0, 26).addBox(-4.5f, -4.5f, 0.0f, 9.0f, 9.0f, 12.0f),
				PartPose.offset(0.0f, 0.5f, 12.0f));
		PartDefinition p_crest4 = p_seg4.addOrReplaceChild("crest4", CubeListBuilder.create()
				.texOffs(40, 47).addBox(0.0f, -4.0f, 1.0f, 0.0f, 4.0f, 10.0f),
				PartPose.offset(0.0f, -4.5f, 0.0f));
		PartDefinition p_seg5 = p_seg4.addOrReplaceChild("seg5", CubeListBuilder.create()
				.texOffs(42, 26).addBox(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 12.0f),
				PartPose.offset(0.0f, 0.5f, 12.0f));
		PartDefinition p_crest5 = p_seg5.addOrReplaceChild("crest5", CubeListBuilder.create()
				.texOffs(60, 47).addBox(0.0f, -4.0f, 1.0f, 0.0f, 4.0f, 10.0f),
				PartPose.offset(0.0f, -4.0f, 0.0f));
		PartDefinition p_seg6 = p_seg5.addOrReplaceChild("seg6", CubeListBuilder.create()
				.texOffs(82, 26).addBox(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 12.0f),
				PartPose.offset(0.0f, 0.5f, 12.0f));
		PartDefinition p_crest6 = p_seg6.addOrReplaceChild("crest6", CubeListBuilder.create()
				.texOffs(124, 47).addBox(0.0f, -3.0f, 1.0f, 0.0f, 3.0f, 10.0f),
				PartPose.offset(0.0f, -3.5f, 0.0f));
		PartDefinition p_seg7 = p_seg6.addOrReplaceChild("seg7", CubeListBuilder.create()
				.texOffs(196, 26).addBox(-3.0f, -3.0f, 0.0f, 6.0f, 6.0f, 11.0f),
				PartPose.offset(0.0f, 0.5f, 12.0f));
		PartDefinition p_crest7 = p_seg7.addOrReplaceChild("crest7", CubeListBuilder.create()
				.texOffs(192, 47).addBox(0.0f, -3.0f, 1.0f, 0.0f, 3.0f, 9.0f),
				PartPose.offset(0.0f, -3.0f, 0.0f));
		PartDefinition p_seg8 = p_seg7.addOrReplaceChild("seg8", CubeListBuilder.create()
				.texOffs(80, 47).addBox(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 10.0f),
				PartPose.offset(0.0f, 1.0f, 11.0f));
		PartDefinition p_crest8 = p_seg8.addOrReplaceChild("crest8", CubeListBuilder.create()
				.texOffs(0, 62).addBox(0.0f, -2.0f, 1.0f, 0.0f, 2.0f, 8.0f),
				PartPose.offset(0.0f, -2.0f, 0.0f));
		PartDefinition p_seg9 = p_seg8.addOrReplaceChild("seg9", CubeListBuilder.create()
				.texOffs(210, 47).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 9.0f),
				PartPose.offset(0.0f, 0.5f, 10.0f));
		PartDefinition p_crest9 = p_seg9.addOrReplaceChild("crest9", CubeListBuilder.create()
				.texOffs(126, 62).addBox(0.0f, -2.0f, 1.0f, 0.0f, 2.0f, 7.0f),
				PartPose.offset(0.0f, -1.5f, 0.0f));
		PartDefinition p_tuft = p_seg9.addOrReplaceChild("tuft", CubeListBuilder.create()
				.texOffs(198, 0).addBox(0.0f, -5.0f, 0.0f, 0.0f, 10.0f, 12.0f),
				PartPose.offset(0.0f, 0.0f, 9.0f));
		PartDefinition p_seg1_leg_r = p_seg1.addOrReplaceChild("seg1_leg_r", CubeListBuilder.create()
				.texOffs(140, 62).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f),
				PartPose.offsetAndRotation(-5.0f, 3.0f, 5.0f, 0.7f, 0.0f, 0.35f));
		PartDefinition p_seg1_shin_r = p_seg1_leg_r.addOrReplaceChild("seg1_shin_r", CubeListBuilder.create()
				.texOffs(188, 62).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f)
				.texOffs(196, 62).addBox(0.01f, 0.0f, 1.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 6.0f, 0.0f, -1.1f, 0.0f, 0.0f));
		PartDefinition p_seg1_foot_r = p_seg1_shin_r.addOrReplaceChild("seg1_foot_r", CubeListBuilder.create()
				.texOffs(52, 72).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 1.0f, 3.0f)
				.texOffs(172, 72).addBox(-2.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(66, 72).addBox(-0.5f, 0.0f, -5.0f, 1.0f, 1.0f, 3.0f)
				.texOffs(178, 72).addBox(1.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(184, 72).addBox(-2.5f, -0.5f, -0.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, 0.4f, 0.0f, 0.0f));
		PartDefinition p_seg1_leg_l = p_seg1.addOrReplaceChild("seg1_leg_l", CubeListBuilder.create()
				.texOffs(152, 62).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f),
				PartPose.offsetAndRotation(5.0f, 3.0f, 5.0f, 0.7f, 0.0f, -0.35f));
		PartDefinition p_seg1_shin_l = p_seg1_leg_l.addOrReplaceChild("seg1_shin_l", CubeListBuilder.create()
				.texOffs(202, 62).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f)
				.texOffs(210, 62).addBox(-0.01f, 0.0f, 1.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 6.0f, 0.0f, -1.1f, 0.0f, 0.0f));
		PartDefinition p_seg1_foot_l = p_seg1_shin_l.addOrReplaceChild("seg1_foot_l", CubeListBuilder.create()
				.texOffs(74, 72).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 1.0f, 3.0f)
				.texOffs(190, 72).addBox(-2.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(88, 72).addBox(-0.5f, 0.0f, -5.0f, 1.0f, 1.0f, 3.0f)
				.texOffs(196, 72).addBox(1.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(202, 72).addBox(-2.5f, -0.5f, -0.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, 0.4f, 0.0f, 0.0f));
		PartDefinition p_seg4_leg_r = p_seg4.addOrReplaceChild("seg4_leg_r", CubeListBuilder.create()
				.texOffs(164, 62).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f),
				PartPose.offsetAndRotation(-4.5f, 2.5f, 5.0f, 0.7f, 0.0f, 0.35f));
		PartDefinition p_seg4_shin_r = p_seg4_leg_r.addOrReplaceChild("seg4_shin_r", CubeListBuilder.create()
				.texOffs(216, 62).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f)
				.texOffs(224, 62).addBox(0.01f, 0.0f, 1.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 6.0f, 0.0f, -1.1f, 0.0f, 0.0f));
		PartDefinition p_seg4_foot_r = p_seg4_shin_r.addOrReplaceChild("seg4_foot_r", CubeListBuilder.create()
				.texOffs(96, 72).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 1.0f, 3.0f)
				.texOffs(208, 72).addBox(-2.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(110, 72).addBox(-0.5f, 0.0f, -5.0f, 1.0f, 1.0f, 3.0f)
				.texOffs(214, 72).addBox(1.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(220, 72).addBox(-2.5f, -0.5f, -0.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, 0.4f, 0.0f, 0.0f));
		PartDefinition p_seg4_leg_l = p_seg4.addOrReplaceChild("seg4_leg_l", CubeListBuilder.create()
				.texOffs(176, 62).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f),
				PartPose.offsetAndRotation(4.5f, 2.5f, 5.0f, 0.7f, 0.0f, -0.35f));
		PartDefinition p_seg4_shin_l = p_seg4_leg_l.addOrReplaceChild("seg4_shin_l", CubeListBuilder.create()
				.texOffs(230, 62).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f)
				.texOffs(238, 62).addBox(-0.01f, 0.0f, 1.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 6.0f, 0.0f, -1.1f, 0.0f, 0.0f));
		PartDefinition p_seg4_foot_l = p_seg4_shin_l.addOrReplaceChild("seg4_foot_l", CubeListBuilder.create()
				.texOffs(118, 72).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 1.0f, 3.0f)
				.texOffs(226, 72).addBox(-2.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(132, 72).addBox(-0.5f, 0.0f, -5.0f, 1.0f, 1.0f, 3.0f)
				.texOffs(232, 72).addBox(1.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(238, 72).addBox(-2.5f, -0.5f, -0.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, 0.4f, 0.0f, 0.0f));
		PartDefinition p_head = p_seg0.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(156, 26).addBox(-5.0f, -6.0f, -10.0f, 10.0f, 8.0f, 10.0f)
				.texOffs(144, 47).addBox(-4.0f, -3.0f, -18.0f, 8.0f, 5.0f, 8.0f)
				.texOffs(32, 72).addBox(-3.0f, -4.0f, -18.0f, 6.0f, 1.0f, 4.0f)
				.texOffs(0, 72).addBox(-5.5f, -7.0f, -9.0f, 3.0f, 2.0f, 5.0f)
				.texOffs(16, 72).addBox(2.5f, -7.0f, -9.0f, 3.0f, 2.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, -1.0f, 0.0f, 0.1f, 0.0f, 0.0f));
		PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(16, 62).addBox(-3.5f, 0.0f, -8.5f, 7.0f, 2.0f, 8.0f),
				PartPose.offsetAndRotation(0.0f, 2.0f, -9.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p_beard = p_jaw.addOrReplaceChild("beard", CubeListBuilder.create()
				.texOffs(108, 47).addBox(0.0f, 0.0f, -6.0f, 0.0f, 6.0f, 8.0f),
				PartPose.offset(0.0f, 2.0f, -2.0f));
		PartDefinition p_antler_r = p_head.addOrReplaceChild("antler_r", CubeListBuilder.create()
				.texOffs(234, 47).addBox(-0.5f, -11.0f, -0.5f, 1.0f, 11.0f, 1.0f)
				.texOffs(140, 72).addBox(-0.5f, -5.0f, -3.5f, 1.0f, 1.0f, 3.0f)
				.texOffs(148, 72).addBox(-0.5f, -9.0f, 0.5f, 1.0f, 1.0f, 3.0f)
				.texOffs(244, 72).addBox(-0.5f, -12.0f, -2.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(-3.0f, -6.0f, -4.0f, -0.55f, 0.0f, 0.35f));
		PartDefinition p_whisker_r = p_head.addOrReplaceChild("whisker_r", CubeListBuilder.create()
				.texOffs(46, 62).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(-4.0f, -1.0f, -16.0f, -0.25f, -0.9f, 0.0f));
		PartDefinition p_whisker2_r = p_whisker_r.addOrReplaceChild("whisker2_r", CubeListBuilder.create()
				.texOffs(66, 62).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 9.0f, -0.7f, 0.0f, 0.0f));
		PartDefinition p_mane_r = p_head.addOrReplaceChild("mane_r", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 0.0f, 10.0f, 16.0f),
				PartPose.offsetAndRotation(-5.0f, -6.0f, -5.0f, 0.15f, -0.6f, 0.0f));
		PartDefinition p_antler_l = p_head.addOrReplaceChild("antler_l", CubeListBuilder.create()
				.texOffs(238, 47).addBox(-0.5f, -11.0f, -0.5f, 1.0f, 11.0f, 1.0f)
				.texOffs(156, 72).addBox(-0.5f, -5.0f, -3.5f, 1.0f, 1.0f, 3.0f)
				.texOffs(164, 72).addBox(-0.5f, -9.0f, 0.5f, 1.0f, 1.0f, 3.0f)
				.texOffs(250, 72).addBox(-0.5f, -12.0f, -2.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(3.0f, -6.0f, -4.0f, -0.55f, 0.0f, -0.35f));
		PartDefinition p_whisker_l = p_head.addOrReplaceChild("whisker_l", CubeListBuilder.create()
				.texOffs(86, 62).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(4.0f, -1.0f, -16.0f, -0.25f, 0.9f, 0.0f));
		PartDefinition p_whisker2_l = p_whisker_l.addOrReplaceChild("whisker2_l", CubeListBuilder.create()
				.texOffs(106, 62).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 9.0f, -0.7f, 0.0f, 0.0f));
		PartDefinition p_mane_l = p_head.addOrReplaceChild("mane_l", CubeListBuilder.create()
				.texOffs(32, 0).addBox(0.0f, 0.0f, 0.0f, 0.0f, 10.0f, 16.0f),
				PartPose.offsetAndRotation(5.0f, -6.0f, -5.0f, 0.15f, 0.6f, 0.0f));
		return LayerDefinition.create(mesh, 256, 128);
	}
}
