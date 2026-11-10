package io.github.potatustudio.mythicrelics.client.entity;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de las entidades chinas (mono guerrero y long). GENERADO por tools/model-gen/china_entities.py
 * --mod a partir del mismo diseño que el visor: no editar a mano.
 */
@SuppressWarnings("unused")
public final class ChinaModelLayers {
	private ChinaModelLayers() {
	}

	public static LayerDefinition monkeyWarrior() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 31).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 11.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 3.0f, 0.0f, 0.12f, 0.0f, 0.0f));
		PartDefinition p_kilt = p_body.addOrReplaceChild("kilt", CubeListBuilder.create()
				.texOffs(8, 46).addBox(-4.5f, 0.0f, -2.5f, 9.0f, 5.0f, 5.0f),
				PartPose.offset(0.0f, 9.0f, 0.0f));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(4, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
				.texOffs(0, 73).addBox(-2.0f, -3.0f, -6.0f, 4.0f, 3.0f, 2.0f)
				.texOffs(12, 73).addBox(-5.0f, -5.0f, -1.0f, 1.0f, 2.0f, 2.0f)
				.texOffs(18, 73).addBox(4.0f, -5.0f, -1.0f, 1.0f, 2.0f, 2.0f)
				.texOffs(30, 73).addBox(-4.5f, -6.5f, -4.5f, 9.0f, 1.0f, 1.0f)
				.texOffs(0, 78).addBox(-4.5f, -6.5f, 3.5f, 9.0f, 1.0f, 1.0f)
				.texOffs(36, 46).addBox(-4.5f, -6.5f, -3.5f, 1.0f, 1.0f, 7.0f)
				.texOffs(0, 57).addBox(3.5f, -6.5f, -3.5f, 1.0f, 1.0f, 7.0f)
				.texOffs(16, 57).addBox(-3.0f, -10.0f, -3.0f, 6.0f, 2.0f, 6.0f)
				.texOffs(24, 73).addBox(-1.0f, -12.0f, -3.5f, 2.0f, 3.0f, 1.0f),
				PartPose.offset(0.0f, 3.0f, 0.0f));
		PartDefinition p_wing_r = p_head.addOrReplaceChild("wing_r", CubeListBuilder.create()
				.texOffs(40, 57).addBox(-1.0f, -3.0f, -2.0f, 1.0f, 4.0f, 4.0f),
				PartPose.offsetAndRotation(-3.0f, -9.0f, 0.0f, 0.0f, 0.0f, 0.5f));
		PartDefinition p_wing_l = p_head.addOrReplaceChild("wing_l", CubeListBuilder.create()
				.texOffs(50, 57).addBox(0.0f, -3.0f, -2.0f, 1.0f, 4.0f, 4.0f),
				PartPose.offsetAndRotation(3.0f, -9.0f, 0.0f, 0.0f, 0.0f, -0.5f));
		PartDefinition p_feather_r = p_head.addOrReplaceChild("feather_r", CubeListBuilder.create()
				.texOffs(56, 31).addBox(-0.5f, -12.0f, -0.5f, 1.0f, 12.0f, 1.0f),
				PartPose.offsetAndRotation(-1.5f, -10.0f, -1.0f, -0.35f, 0.0f, -0.18f));
		PartDefinition p_feather_r2 = p_feather_r.addOrReplaceChild("feather_r2", CubeListBuilder.create()
				.texOffs(0, 46).addBox(-0.5f, -10.0f, -0.5f, 1.0f, 10.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, 0.0f, -0.9f, 0.0f, 0.0f));
		PartDefinition p_feather_l = p_head.addOrReplaceChild("feather_l", CubeListBuilder.create()
				.texOffs(60, 31).addBox(-0.5f, -12.0f, -0.5f, 1.0f, 12.0f, 1.0f),
				PartPose.offsetAndRotation(1.5f, -10.0f, -1.0f, -0.35f, 0.0f, 0.18f));
		PartDefinition p_feather_l2 = p_feather_l.addOrReplaceChild("feather_l2", CubeListBuilder.create()
				.texOffs(4, 46).addBox(-0.5f, -10.0f, -0.5f, 1.0f, 10.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, 0.0f, -0.9f, 0.0f, 0.0f));
		PartDefinition p_arm_r = root.addOrReplaceChild("arm_r", CubeListBuilder.create()
				.texOffs(36, 0).addBox(-1.5f, -1.0f, -1.5f, 3.0f, 13.0f, 3.0f)
				.texOffs(30, 65).addBox(-2.5f, -2.0f, -2.0f, 4.0f, 2.0f, 4.0f),
				PartPose.offset(-5.5f, 4.0f, 0.0f));
		PartDefinition p_arm_l = root.addOrReplaceChild("arm_l", CubeListBuilder.create()
				.texOffs(48, 0).addBox(-1.5f, -1.0f, -1.5f, 3.0f, 13.0f, 3.0f)
				.texOffs(46, 65).addBox(-1.5f, -2.0f, -2.0f, 4.0f, 2.0f, 4.0f),
				PartPose.offset(5.5f, 4.0f, 0.0f));
		PartDefinition p_staff = p_arm_r.addOrReplaceChild("staff", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5f, -22.0f, -0.5f, 1.0f, 30.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, 11.0f, -0.5f, 0.25f, 0.0f, 0.0f));
		PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
				.texOffs(24, 31).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(-2.0f, 14.0f, 0.0f));
		PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
				.texOffs(40, 31).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(2.0f, 14.0f, 0.0f));
		PartDefinition p_tail = p_body.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(0, 65).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 7.0f),
				PartPose.offsetAndRotation(0.0f, 10.0f, 2.0f, -0.6f, 0.0f, 0.0f));
		PartDefinition p_tail2 = p_tail.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(16, 65).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 7.0f, -1.0f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 128);
	}

	public static LayerDefinition longDragon() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_seg0 = root.addOrReplaceChild("seg0", CubeListBuilder.create()
				.texOffs(140, 26).addBox(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 10.0f),
				PartPose.offset(0.0f, 16.0f, -16.0f));
		PartDefinition p_crest0 = p_seg0.addOrReplaceChild("crest0", CubeListBuilder.create()
				.texOffs(196, 47).addBox(0.0f, -4.0f, 1.0f, 0.0f, 4.0f, 8.0f),
				PartPose.offset(0.0f, -4.0f, 0.0f));
		PartDefinition p_seg1 = p_seg0.addOrReplaceChild("seg1", CubeListBuilder.create()
				.texOffs(110, 0).addBox(-5.0f, -5.0f, 0.0f, 10.0f, 10.0f, 12.0f),
				PartPose.offset(0.0f, -1.0f, 10.0f));
		PartDefinition p_crest1 = p_seg1.addOrReplaceChild("crest1", CubeListBuilder.create()
				.texOffs(0, 47).addBox(0.0f, -5.0f, 1.0f, 0.0f, 5.0f, 10.0f),
				PartPose.offset(0.0f, -5.0f, 0.0f));
		PartDefinition p_seg2 = p_seg1.addOrReplaceChild("seg2", CubeListBuilder.create()
				.texOffs(64, 0).addBox(-5.5f, -5.5f, 0.0f, 11.0f, 11.0f, 12.0f),
				PartPose.offset(0.0f, -0.5f, 12.0f));
		PartDefinition p_crest2 = p_seg2.addOrReplaceChild("crest2", CubeListBuilder.create()
				.texOffs(20, 47).addBox(0.0f, -5.0f, 1.0f, 0.0f, 5.0f, 10.0f),
				PartPose.offset(0.0f, -5.5f, 0.0f));
		PartDefinition p_seg3 = p_seg2.addOrReplaceChild("seg3", CubeListBuilder.create()
				.texOffs(154, 0).addBox(-5.0f, -5.0f, 0.0f, 10.0f, 10.0f, 12.0f),
				PartPose.offset(0.0f, 0.5f, 12.0f));
		PartDefinition p_crest3 = p_seg3.addOrReplaceChild("crest3", CubeListBuilder.create()
				.texOffs(40, 47).addBox(0.0f, -5.0f, 1.0f, 0.0f, 5.0f, 10.0f),
				PartPose.offset(0.0f, -5.0f, 0.0f));
		PartDefinition p_seg4 = p_seg3.addOrReplaceChild("seg4", CubeListBuilder.create()
				.texOffs(0, 26).addBox(-4.5f, -4.5f, 0.0f, 9.0f, 9.0f, 12.0f),
				PartPose.offset(0.0f, 0.5f, 12.0f));
		PartDefinition p_crest4 = p_seg4.addOrReplaceChild("crest4", CubeListBuilder.create()
				.texOffs(60, 47).addBox(0.0f, -4.0f, 1.0f, 0.0f, 4.0f, 10.0f),
				PartPose.offset(0.0f, -4.5f, 0.0f));
		PartDefinition p_seg5 = p_seg4.addOrReplaceChild("seg5", CubeListBuilder.create()
				.texOffs(42, 26).addBox(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 12.0f),
				PartPose.offset(0.0f, 0.5f, 12.0f));
		PartDefinition p_crest5 = p_seg5.addOrReplaceChild("crest5", CubeListBuilder.create()
				.texOffs(80, 47).addBox(0.0f, -4.0f, 1.0f, 0.0f, 4.0f, 10.0f),
				PartPose.offset(0.0f, -4.0f, 0.0f));
		PartDefinition p_seg6 = p_seg5.addOrReplaceChild("seg6", CubeListBuilder.create()
				.texOffs(102, 26).addBox(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 12.0f),
				PartPose.offset(0.0f, 0.5f, 12.0f));
		PartDefinition p_crest6 = p_seg6.addOrReplaceChild("crest6", CubeListBuilder.create()
				.texOffs(144, 47).addBox(0.0f, -3.0f, 1.0f, 0.0f, 3.0f, 10.0f),
				PartPose.offset(0.0f, -3.5f, 0.0f));
		PartDefinition p_seg7 = p_seg6.addOrReplaceChild("seg7", CubeListBuilder.create()
				.texOffs(216, 26).addBox(-3.0f, -3.0f, 0.0f, 6.0f, 6.0f, 11.0f),
				PartPose.offset(0.0f, 0.5f, 12.0f));
		PartDefinition p_crest7 = p_seg7.addOrReplaceChild("crest7", CubeListBuilder.create()
				.texOffs(212, 47).addBox(0.0f, -3.0f, 1.0f, 0.0f, 3.0f, 9.0f),
				PartPose.offset(0.0f, -3.0f, 0.0f));
		PartDefinition p_seg8 = p_seg7.addOrReplaceChild("seg8", CubeListBuilder.create()
				.texOffs(100, 47).addBox(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 10.0f),
				PartPose.offset(0.0f, 1.0f, 11.0f));
		PartDefinition p_crest8 = p_seg8.addOrReplaceChild("crest8", CubeListBuilder.create()
				.texOffs(8, 62).addBox(0.0f, -2.0f, 1.0f, 0.0f, 2.0f, 8.0f),
				PartPose.offset(0.0f, -2.0f, 0.0f));
		PartDefinition p_seg9 = p_seg8.addOrReplaceChild("seg9", CubeListBuilder.create()
				.texOffs(230, 47).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 9.0f),
				PartPose.offset(0.0f, 0.5f, 10.0f));
		PartDefinition p_crest9 = p_seg9.addOrReplaceChild("crest9", CubeListBuilder.create()
				.texOffs(154, 62).addBox(0.0f, -2.0f, 1.0f, 0.0f, 2.0f, 7.0f),
				PartPose.offset(0.0f, -1.5f, 0.0f));
		PartDefinition p_tuft = p_seg9.addOrReplaceChild("tuft", CubeListBuilder.create()
				.texOffs(198, 0).addBox(0.0f, -5.0f, 0.0f, 0.0f, 10.0f, 12.0f),
				PartPose.offset(0.0f, 0.0f, 9.0f));
		PartDefinition p_seg1_leg_r = p_seg1.addOrReplaceChild("seg1_leg_r", CubeListBuilder.create()
				.texOffs(168, 62).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f),
				PartPose.offsetAndRotation(-5.0f, 3.0f, 5.0f, 0.7f, 0.0f, 0.35f));
		PartDefinition p_seg1_shin_r = p_seg1_leg_r.addOrReplaceChild("seg1_shin_r", CubeListBuilder.create()
				.texOffs(232, 62).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f)
				.texOffs(240, 62).addBox(0.01f, 0.0f, 1.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 6.0f, 0.0f, -1.1f, 0.0f, 0.0f));
		PartDefinition p_seg1_foot_r = p_seg1_shin_r.addOrReplaceChild("seg1_foot_r", CubeListBuilder.create()
				.texOffs(86, 74).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 1.0f, 3.0f)
				.texOffs(206, 74).addBox(-2.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(100, 74).addBox(-0.5f, 0.0f, -5.0f, 1.0f, 1.0f, 3.0f)
				.texOffs(212, 74).addBox(1.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(218, 74).addBox(-2.5f, -0.5f, -0.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, 0.4f, 0.0f, 0.0f));
		PartDefinition p_seg1_leg_l = p_seg1.addOrReplaceChild("seg1_leg_l", CubeListBuilder.create()
				.texOffs(180, 62).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f),
				PartPose.offsetAndRotation(5.0f, 3.0f, 5.0f, 0.7f, 0.0f, -0.35f));
		PartDefinition p_seg1_shin_l = p_seg1_leg_l.addOrReplaceChild("seg1_shin_l", CubeListBuilder.create()
				.texOffs(246, 62).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f)
				.texOffs(0, 74).addBox(-0.01f, 0.0f, 1.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 6.0f, 0.0f, -1.1f, 0.0f, 0.0f));
		PartDefinition p_seg1_foot_l = p_seg1_shin_l.addOrReplaceChild("seg1_foot_l", CubeListBuilder.create()
				.texOffs(108, 74).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 1.0f, 3.0f)
				.texOffs(224, 74).addBox(-2.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(122, 74).addBox(-0.5f, 0.0f, -5.0f, 1.0f, 1.0f, 3.0f)
				.texOffs(230, 74).addBox(1.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(236, 74).addBox(-2.5f, -0.5f, -0.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, 0.4f, 0.0f, 0.0f));
		PartDefinition p_seg4_leg_r = p_seg4.addOrReplaceChild("seg4_leg_r", CubeListBuilder.create()
				.texOffs(192, 62).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f),
				PartPose.offsetAndRotation(-4.5f, 2.5f, 5.0f, 0.7f, 0.0f, 0.35f));
		PartDefinition p_seg4_shin_r = p_seg4_leg_r.addOrReplaceChild("seg4_shin_r", CubeListBuilder.create()
				.texOffs(6, 74).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f)
				.texOffs(14, 74).addBox(0.01f, 0.0f, 1.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 6.0f, 0.0f, -1.1f, 0.0f, 0.0f));
		PartDefinition p_seg4_foot_r = p_seg4_shin_r.addOrReplaceChild("seg4_foot_r", CubeListBuilder.create()
				.texOffs(130, 74).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 1.0f, 3.0f)
				.texOffs(242, 74).addBox(-2.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(144, 74).addBox(-0.5f, 0.0f, -5.0f, 1.0f, 1.0f, 3.0f)
				.texOffs(248, 74).addBox(1.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(0, 81).addBox(-2.5f, -0.5f, -0.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, 0.4f, 0.0f, 0.0f));
		PartDefinition p_seg4_leg_l = p_seg4.addOrReplaceChild("seg4_leg_l", CubeListBuilder.create()
				.texOffs(204, 62).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f),
				PartPose.offsetAndRotation(4.5f, 2.5f, 5.0f, 0.7f, 0.0f, -0.35f));
		PartDefinition p_seg4_shin_l = p_seg4_leg_l.addOrReplaceChild("seg4_shin_l", CubeListBuilder.create()
				.texOffs(20, 74).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f)
				.texOffs(28, 74).addBox(-0.01f, 0.0f, 1.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 6.0f, 0.0f, -1.1f, 0.0f, 0.0f));
		PartDefinition p_seg4_foot_l = p_seg4_shin_l.addOrReplaceChild("seg4_foot_l", CubeListBuilder.create()
				.texOffs(152, 74).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 1.0f, 3.0f)
				.texOffs(6, 81).addBox(-2.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(166, 74).addBox(-0.5f, 0.0f, -5.0f, 1.0f, 1.0f, 3.0f)
				.texOffs(12, 81).addBox(1.0f, 0.0f, -4.5f, 1.0f, 1.0f, 2.0f)
				.texOffs(18, 81).addBox(-2.5f, -0.5f, -0.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, 0.4f, 0.0f, 0.0f));
		PartDefinition p_head = p_seg0.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(176, 26).addBox(-5.0f, -6.0f, -10.0f, 10.0f, 8.0f, 10.0f)
				.texOffs(164, 47).addBox(-4.0f, -3.0f, -18.0f, 8.0f, 5.0f, 8.0f)
				.texOffs(66, 74).addBox(-3.0f, -4.0f, -18.0f, 6.0f, 1.0f, 4.0f)
				.texOffs(34, 74).addBox(-5.5f, -7.0f, -9.0f, 3.0f, 2.0f, 5.0f)
				.texOffs(50, 74).addBox(2.5f, -7.0f, -9.0f, 3.0f, 2.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, -1.0f, 0.0f, 0.1f, 0.0f, 0.0f));
		PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(24, 62).addBox(-3.5f, 0.0f, -8.5f, 7.0f, 2.0f, 8.0f),
				PartPose.offsetAndRotation(0.0f, 2.0f, -9.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p_beard = p_jaw.addOrReplaceChild("beard", CubeListBuilder.create()
				.texOffs(128, 47).addBox(0.0f, 0.0f, -6.0f, 0.0f, 6.0f, 8.0f),
				PartPose.offset(0.0f, 2.0f, -2.0f));
		PartDefinition p_antler_r = p_head.addOrReplaceChild("antler_r", CubeListBuilder.create()
				.texOffs(0, 62).addBox(-0.5f, -11.0f, -0.5f, 1.0f, 11.0f, 1.0f)
				.texOffs(174, 74).addBox(-0.5f, -5.0f, -3.5f, 1.0f, 1.0f, 3.0f)
				.texOffs(182, 74).addBox(-0.5f, -9.0f, 0.5f, 1.0f, 1.0f, 3.0f)
				.texOffs(24, 81).addBox(-0.5f, -12.0f, -2.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(-3.0f, -6.0f, -4.0f, -0.55f, 0.0f, 0.35f));
		PartDefinition p_whisker_r = p_head.addOrReplaceChild("whisker_r", CubeListBuilder.create()
				.texOffs(54, 62).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(-4.0f, -1.0f, -16.0f, -0.25f, -0.9f, 0.0f));
		PartDefinition p_whisker2_r = p_whisker_r.addOrReplaceChild("whisker2_r", CubeListBuilder.create()
				.texOffs(74, 62).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 9.0f, -0.7f, 0.0f, 0.0f));
		PartDefinition p_mane_r = p_head.addOrReplaceChild("mane_r", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 0.0f, 10.0f, 16.0f),
				PartPose.offsetAndRotation(-5.0f, -6.0f, -5.0f, 0.15f, -0.6f, 0.0f));
		PartDefinition p_antler_l = p_head.addOrReplaceChild("antler_l", CubeListBuilder.create()
				.texOffs(4, 62).addBox(-0.5f, -11.0f, -0.5f, 1.0f, 11.0f, 1.0f)
				.texOffs(190, 74).addBox(-0.5f, -5.0f, -3.5f, 1.0f, 1.0f, 3.0f)
				.texOffs(198, 74).addBox(-0.5f, -9.0f, 0.5f, 1.0f, 1.0f, 3.0f)
				.texOffs(30, 81).addBox(-0.5f, -12.0f, -2.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(3.0f, -6.0f, -4.0f, -0.55f, 0.0f, -0.35f));
		PartDefinition p_whisker_l = p_head.addOrReplaceChild("whisker_l", CubeListBuilder.create()
				.texOffs(94, 62).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(4.0f, -1.0f, -16.0f, -0.25f, 0.9f, 0.0f));
		PartDefinition p_whisker2_l = p_whisker_l.addOrReplaceChild("whisker2_l", CubeListBuilder.create()
				.texOffs(114, 62).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 9.0f, -0.7f, 0.0f, 0.0f));
		PartDefinition p_mane_l = p_head.addOrReplaceChild("mane_l", CubeListBuilder.create()
				.texOffs(32, 0).addBox(0.0f, 0.0f, 0.0f, 0.0f, 10.0f, 16.0f),
				PartPose.offsetAndRotation(5.0f, -6.0f, -5.0f, 0.15f, 0.6f, 0.0f));
		PartDefinition p_pearl = p_head.addOrReplaceChild("pearl", CubeListBuilder.create()
				.texOffs(216, 62).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 4.0f, 4.0f)
				.texOffs(82, 26).addBox(0.0f, -5.0f, -5.0f, 0.0f, 10.0f, 10.0f)
				.texOffs(134, 62).addBox(-5.0f, -5.0f, 0.0f, 10.0f, 10.0f, 0.0f),
				PartPose.offset(0.0f, 6.0f, -24.0f));
		return LayerDefinition.create(mesh, 256, 128);
	}
}
