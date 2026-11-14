package io.github.potatustudio.mythicrelics.client.entity;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de las entidades del mod (China, Japón, nórdicos y Grecia). GENERADO por tools/model-gen/china_entities.py
 * --mod a partir del mismo diseño que el visor: no editar a mano.
 */
@SuppressWarnings("unused")
public final class MythicModelLayers {
	private MythicModelLayers() {
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

	public static LayerDefinition celestialDog() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_chest = root.addOrReplaceChild("chest", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.0f, -4.0f, -3.0f, 6.0f, 7.0f, 6.0f),
				PartPose.offset(0.0f, 13.0f, -3.0f));
		PartDefinition p_waist = p_chest.addOrReplaceChild("waist", CubeListBuilder.create()
				.texOffs(24, 0).addBox(-2.0f, -3.0f, 0.0f, 4.0f, 5.0f, 7.0f),
				PartPose.offsetAndRotation(0.0f, -0.5f, 3.0f, -0.08f, 0.0f, 0.0f));
		PartDefinition p_collar = p_chest.addOrReplaceChild("collar", CubeListBuilder.create()
				.texOffs(12, 24).addBox(-3.5f, -1.0f, -0.5f, 7.0f, 2.0f, 1.0f)
				.texOffs(36, 24).addBox(-0.5f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, -3.0f, -3.0f));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(16, 13).addBox(-2.5f, -3.0f, -4.0f, 5.0f, 5.0f, 5.0f)
				.texOffs(36, 13).addBox(-1.5f, -1.0f, -8.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 10.5f, -6.0f, 0.1f, 0.0f, 0.0f));
		PartDefinition p_ear_r = p_head.addOrReplaceChild("ear_r", CubeListBuilder.create()
				.texOffs(28, 24).addBox(-0.5f, -2.0f, -0.5f, 1.0f, 2.0f, 1.0f),
				PartPose.offsetAndRotation(-1.7f, -3.0f, -1.0f, -0.2f, 0.0f, -0.12f));
		PartDefinition p_ear_l = p_head.addOrReplaceChild("ear_l", CubeListBuilder.create()
				.texOffs(32, 24).addBox(-0.5f, -2.0f, -0.5f, 1.0f, 2.0f, 1.0f),
				PartPose.offsetAndRotation(1.7f, -3.0f, -1.0f, -0.2f, 0.0f, 0.12f));
		PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create()
				.texOffs(46, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 9.0f, 2.0f),
				PartPose.offset(-1.6f, 15.0f, -5.0f));
		PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create()
				.texOffs(54, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 9.0f, 2.0f),
				PartPose.offset(1.6f, 15.0f, -5.0f));
		PartDefinition p_leg_hr = root.addOrReplaceChild("leg_hr", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 9.0f, 2.0f),
				PartPose.offset(-1.4f, 15.0f, 5.0f));
		PartDefinition p_leg_hl = root.addOrReplaceChild("leg_hl", CubeListBuilder.create()
				.texOffs(8, 13).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 9.0f, 2.0f),
				PartPose.offset(1.4f, 15.0f, 5.0f));
		PartDefinition p_tail = p_waist.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(50, 13).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -2.0f, 6.5f, -0.9f, 0.0f, 0.0f));
		PartDefinition p_tail_tip = p_tail.addOrReplaceChild("tail_tip", CubeListBuilder.create()
				.texOffs(0, 24).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.7f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 32);
	}

	public static LayerDefinition qilin() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0f, -4.0f, -10.0f, 8.0f, 8.0f, 18.0f)
				.texOffs(70, 26).addBox(-4.5f, -5.0f, -5.0f, 9.0f, 1.0f, 7.0f)
				.texOffs(16, 39).addBox(-5.0f, -4.0f, -3.0f, 1.0f, 4.0f, 3.0f)
				.texOffs(24, 39).addBox(4.0f, -4.0f, -3.0f, 1.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 11.0f, 2.0f));
		PartDefinition p_spine_flames = p_body.addOrReplaceChild("spine_flames", CubeListBuilder.create()
				.texOffs(48, 26).addBox(0.0f, -3.0f, 0.0f, 0.0f, 3.0f, 6.0f),
				PartPose.offset(0.0f, -4.0f, 2.0f));
		PartDefinition p_neck = root.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(52, 0).addBox(-2.5f, -10.0f, -3.0f, 5.0f, 11.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, 9.0f, -7.0f, 0.55f, 0.0f, 0.0f));
		PartDefinition p_mane = p_neck.addOrReplaceChild("mane", CubeListBuilder.create()
				.texOffs(72, 0).addBox(0.0f, 0.0f, 0.0f, 0.0f, 11.0f, 5.0f),
				PartPose.offset(0.0f, -10.0f, 1.5f));
		PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(24, 26).addBox(-3.0f, -5.0f, -4.0f, 6.0f, 5.0f, 6.0f)
				.texOffs(102, 26).addBox(-2.0f, -4.0f, -9.0f, 4.0f, 3.0f, 5.0f)
				.texOffs(76, 39).addBox(-3.5f, -6.0f, -2.0f, 2.0f, 2.0f, 2.0f)
				.texOffs(84, 39).addBox(1.5f, -6.0f, -2.0f, 2.0f, 2.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -9.5f, -1.0f, -0.55f, 0.0f, 0.0f));
		PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(56, 39).addBox(-1.5f, 0.0f, -4.5f, 3.0f, 1.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, -1.0f, -4.0f, 0.12f, 0.0f, 0.0f));
		PartDefinition p_beard = p_head.addOrReplaceChild("beard", CubeListBuilder.create()
				.texOffs(60, 26).addBox(0.0f, 0.0f, -2.0f, 0.0f, 4.0f, 5.0f),
				PartPose.offset(0.0f, 0.0f, -6.0f));
		PartDefinition p_horn = p_head.addOrReplaceChild("horn", CubeListBuilder.create()
				.texOffs(72, 39).addBox(-0.5f, -5.0f, -0.5f, 1.0f, 5.0f, 1.0f)
				.texOffs(96, 39).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -5.0f, -1.5f, -0.45f, 0.0f, 0.0f));
		PartDefinition p_horn_tip = p_horn.addOrReplaceChild("horn_tip", CubeListBuilder.create()
				.texOffs(92, 39).addBox(-0.5f, -3.0f, -0.5f, 1.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -5.0f, 0.0f, -0.4f, 0.0f, 0.0f));
		PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create()
				.texOffs(82, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
				PartPose.offset(-2.5f, 14.0f, -6.0f));
		PartDefinition p_leg_fr_flame = p_leg_fr.addOrReplaceChild("leg_fr_flame", CubeListBuilder.create()
				.texOffs(32, 39).addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 6.0f, 1.5f));
		PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create()
				.texOffs(94, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
				PartPose.offset(2.5f, 14.0f, -6.0f));
		PartDefinition p_leg_fl_flame = p_leg_fl.addOrReplaceChild("leg_fl_flame", CubeListBuilder.create()
				.texOffs(38, 39).addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 6.0f, 1.5f));
		PartDefinition p_leg_hr = root.addOrReplaceChild("leg_hr", CubeListBuilder.create()
				.texOffs(106, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
				PartPose.offset(-2.5f, 14.0f, 8.0f));
		PartDefinition p_leg_hr_flame = p_leg_hr.addOrReplaceChild("leg_hr_flame", CubeListBuilder.create()
				.texOffs(44, 39).addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 6.0f, 1.5f));
		PartDefinition p_leg_hl = root.addOrReplaceChild("leg_hl", CubeListBuilder.create()
				.texOffs(0, 26).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
				PartPose.offset(2.5f, 14.0f, 8.0f));
		PartDefinition p_leg_hl_flame = p_leg_hl.addOrReplaceChild("leg_hl_flame", CubeListBuilder.create()
				.texOffs(50, 39).addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 6.0f, 1.5f));
		PartDefinition p_tail = p_body.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(0, 39).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 7.0f),
				PartPose.offsetAndRotation(0.0f, -3.0f, 8.0f, -0.9f, 0.0f, 0.0f));
		PartDefinition p_tail_tuft = p_tail.addOrReplaceChild("tail_tuft", CubeListBuilder.create()
				.texOffs(12, 26).addBox(0.0f, -3.0f, 0.0f, 0.0f, 6.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 7.0f, 0.5f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 128, 64);
	}

	public static LayerDefinition kitsune() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.0f, -3.0f, -5.0f, 6.0f, 6.0f, 10.0f),
				PartPose.offset(0.0f, 15.0f, 0.0f));
		PartDefinition p_bib = p_body.addOrReplaceChild("bib", CubeListBuilder.create()
				.texOffs(0, 54).addBox(-2.5f, 0.0f, -0.6f, 5.0f, 4.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -1.0f, -5.0f, 0.25f, 0.0f, 0.0f));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(32, 0).addBox(-3.0f, -4.0f, -4.0f, 6.0f, 5.0f, 5.0f)
				.texOffs(12, 54).addBox(-1.5f, -1.0f, -7.0f, 3.0f, 2.0f, 3.0f),
				PartPose.offset(0.0f, 13.0f, -5.0f));
		PartDefinition p_ear_r = p_head.addOrReplaceChild("ear_r", CubeListBuilder.create()
				.texOffs(24, 54).addBox(-1.0f, -3.0f, -0.5f, 2.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(-2.0f, -4.0f, -2.0f, 0.0f, 0.0f, -0.2f));
		PartDefinition p_ear_l = p_head.addOrReplaceChild("ear_l", CubeListBuilder.create()
				.texOffs(30, 54).addBox(-1.0f, -3.0f, -0.5f, 2.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(2.0f, -4.0f, -2.0f, 0.0f, 0.0f, 0.2f));
		PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create()
				.texOffs(54, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
				PartPose.offset(-1.8f, 18.0f, -3.5f));
		PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create()
				.texOffs(0, 16).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
				PartPose.offset(1.8f, 18.0f, -3.5f));
		PartDefinition p_leg_hr = root.addOrReplaceChild("leg_hr", CubeListBuilder.create()
				.texOffs(8, 16).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
				PartPose.offset(-1.8f, 18.0f, 3.5f));
		PartDefinition p_leg_hl = root.addOrReplaceChild("leg_hl", CubeListBuilder.create()
				.texOffs(16, 16).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
				PartPose.offset(1.8f, 18.0f, 3.5f));
		PartDefinition p_tail0 = p_body.addOrReplaceChild("tail0", CubeListBuilder.create()
				.texOffs(24, 16).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 0.85f, -0.88f, 0.0f));
		PartDefinition p_tail0_tip = p_tail0.addOrReplaceChild("tail0_tip", CubeListBuilder.create()
				.texOffs(48, 32).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail1 = p_body.addOrReplaceChild("tail1", CubeListBuilder.create()
				.texOffs(40, 16).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 0.9375f, -0.66f, 0.0f));
		PartDefinition p_tail1_tip = p_tail1.addOrReplaceChild("tail1_tip", CubeListBuilder.create()
				.texOffs(0, 40).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail2 = p_body.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(0, 24).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 1.025f, -0.44f, 0.0f));
		PartDefinition p_tail2_tip = p_tail2.addOrReplaceChild("tail2_tip", CubeListBuilder.create()
				.texOffs(14, 40).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail3 = p_body.addOrReplaceChild("tail3", CubeListBuilder.create()
				.texOffs(16, 24).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 1.112f, -0.22f, 0.0f));
		PartDefinition p_tail3_tip = p_tail3.addOrReplaceChild("tail3_tip", CubeListBuilder.create()
				.texOffs(28, 40).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail4 = p_body.addOrReplaceChild("tail4", CubeListBuilder.create()
				.texOffs(32, 24).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 1.2f, 0.0f, 0.0f));
		PartDefinition p_tail4_tip = p_tail4.addOrReplaceChild("tail4_tip", CubeListBuilder.create()
				.texOffs(42, 40).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail5 = p_body.addOrReplaceChild("tail5", CubeListBuilder.create()
				.texOffs(48, 24).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 1.112f, 0.22f, 0.0f));
		PartDefinition p_tail5_tip = p_tail5.addOrReplaceChild("tail5_tip", CubeListBuilder.create()
				.texOffs(0, 47).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail6 = p_body.addOrReplaceChild("tail6", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 1.025f, 0.44f, 0.0f));
		PartDefinition p_tail6_tip = p_tail6.addOrReplaceChild("tail6_tip", CubeListBuilder.create()
				.texOffs(14, 47).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail7 = p_body.addOrReplaceChild("tail7", CubeListBuilder.create()
				.texOffs(16, 32).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 0.9375f, 0.66f, 0.0f));
		PartDefinition p_tail7_tip = p_tail7.addOrReplaceChild("tail7_tip", CubeListBuilder.create()
				.texOffs(28, 47).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_tail8 = p_body.addOrReplaceChild("tail8", CubeListBuilder.create()
				.texOffs(32, 32).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, 0.85f, 0.88f, 0.0f));
		PartDefinition p_tail8_tip = p_tail8.addOrReplaceChild("tail8_tip", CubeListBuilder.create()
				.texOffs(42, 47).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_foxfire = root.addOrReplaceChild("foxfire", CubeListBuilder.create(),
				PartPose.offset(0.0f, 12.0f, 0.0f));
		PartDefinition p_foxfire_a = p_foxfire.addOrReplaceChild("foxfire_a", CubeListBuilder.create()
				.texOffs(36, 54).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f),
				PartPose.offset(9.0f, 0.0f, 0.0f));
		PartDefinition p_foxfire_b = p_foxfire.addOrReplaceChild("foxfire_b", CubeListBuilder.create()
				.texOffs(44, 54).addBox(-1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f),
				PartPose.offset(-9.0f, -2.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 64);
	}

	public static LayerDefinition urashimaTurtle() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-9.0f, -3.0f, -10.0f, 18.0f, 4.0f, 20.0f)
				.texOffs(0, 24).addBox(-7.0f, -6.0f, -8.0f, 14.0f, 3.0f, 16.0f)
				.texOffs(24, 43).addBox(-4.0f, -8.0f, -5.0f, 8.0f, 2.0f, 10.0f),
				PartPose.offset(0.0f, 18.0f, 0.0f));
		PartDefinition p_seaweed0 = p_body.addOrReplaceChild("seaweed0", CubeListBuilder.create()
				.texOffs(60, 24).addBox(0.0f, -1.0f, 0.0f, 0.0f, 5.0f, 12.0f),
				PartPose.offsetAndRotation(-4.0f, -2.0f, 10.0f, -0.15f, 0.0f, 0.0f));
		PartDefinition p_seaweed1 = p_body.addOrReplaceChild("seaweed1", CubeListBuilder.create()
				.texOffs(84, 24).addBox(0.0f, -1.0f, 0.0f, 0.0f, 5.0f, 12.0f),
				PartPose.offsetAndRotation(0.0f, -2.0f, 10.0f, -0.15f, 0.0f, 0.0f));
		PartDefinition p_seaweed2 = p_body.addOrReplaceChild("seaweed2", CubeListBuilder.create()
				.texOffs(0, 43).addBox(0.0f, -1.0f, 0.0f, 0.0f, 5.0f, 12.0f),
				PartPose.offsetAndRotation(4.0f, -2.0f, 10.0f, -0.15f, 0.0f, 0.0f));
		PartDefinition p_neck = root.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(80, 43).addBox(-2.0f, -2.0f, -3.0f, 4.0f, 4.0f, 3.0f),
				PartPose.offset(0.0f, 18.0f, -10.0f));
		PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(60, 43).addBox(-2.5f, -2.5f, -5.0f, 5.0f, 5.0f, 5.0f),
				PartPose.offset(0.0f, -0.5f, -3.0f));
		PartDefinition p_flipper_fr = root.addOrReplaceChild("flipper_fr", CubeListBuilder.create()
				.texOffs(94, 43).addBox(-12.0f, -0.5f, -2.5f, 12.0f, 1.0f, 5.0f),
				PartPose.offsetAndRotation(-8.0f, 19.0f, -6.0f, 0.0f, 0.3f, 0.2f));
		PartDefinition p_flipper_fl = root.addOrReplaceChild("flipper_fl", CubeListBuilder.create()
				.texOffs(0, 60).addBox(0.0f, -0.5f, -2.5f, 12.0f, 1.0f, 5.0f),
				PartPose.offsetAndRotation(8.0f, 19.0f, -6.0f, 0.0f, -0.3f, -0.2f));
		PartDefinition p_flipper_hr = root.addOrReplaceChild("flipper_hr", CubeListBuilder.create()
				.texOffs(34, 60).addBox(-6.0f, -0.5f, -2.0f, 6.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(-7.0f, 19.0f, 8.0f, 0.0f, -0.4f, 0.1f));
		PartDefinition p_flipper_hl = root.addOrReplaceChild("flipper_hl", CubeListBuilder.create()
				.texOffs(54, 60).addBox(0.0f, -0.5f, -2.0f, 6.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(7.0f, 19.0f, 8.0f, 0.0f, 0.4f, -0.1f));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition orochiShadow() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-12.0f, -8.0f, -12.0f, 24.0f, 8.0f, 24.0f),
				PartPose.offset(0.0f, 24.0f, 0.0f));
		PartDefinition p_tree0 = p_body.addOrReplaceChild("tree0", CubeListBuilder.create()
				.texOffs(104, 81).addBox(-0.5f, -3.0f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(80, 55).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 5.0f, 3.0f),
				PartPose.offset(-6.0f, -8.0f, -3.0f));
		PartDefinition p_tree1 = p_body.addOrReplaceChild("tree1", CubeListBuilder.create()
				.texOffs(108, 81).addBox(-0.5f, -3.0f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(92, 55).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 5.0f, 3.0f),
				PartPose.offset(5.0f, -8.0f, 4.0f));
		PartDefinition p_tree2 = p_body.addOrReplaceChild("tree2", CubeListBuilder.create()
				.texOffs(112, 81).addBox(-0.5f, -3.0f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(104, 55).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 5.0f, 3.0f),
				PartPose.offset(0.0f, -8.0f, 7.0f));
		PartDefinition p_neck0 = p_body.addOrReplaceChild("neck0", CubeListBuilder.create()
				.texOffs(96, 0).addBox(-1.5f, -9.0f, -1.5f, 3.0f, 9.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -7.0f, -8.5f, 0.45f, 0.0f, 0.0f));
		PartDefinition p_neck0b = p_neck0.addOrReplaceChild("neck0b", CubeListBuilder.create()
				.texOffs(72, 32).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 8.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -9.0f, 0.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p_head0 = p_neck0b.addOrReplaceChild("head0", CubeListBuilder.create()
				.texOffs(0, 65).addBox(-2.0f, -2.5f, -5.0f, 4.0f, 3.0f, 5.0f)
				.texOffs(18, 73).addBox(-1.5f, -1.5f, -8.0f, 3.0f, 2.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -8.0f, 0.0f, -0.75f, 0.0f, 0.0f));
		PartDefinition p_jaw0 = p_head0.addOrReplaceChild("jaw0", CubeListBuilder.create()
				.texOffs(30, 73).addBox(-1.5f, 0.0f, -3.5f, 3.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.5f, -4.5f, 0.15f, 0.0f, 0.0f));
		PartDefinition p_tail0 = p_body.addOrReplaceChild("tail0", CubeListBuilder.create()
				.texOffs(48, 44).addBox(-1.0f, -1.0f, -8.0f, 2.0f, 2.0f, 8.0f),
				PartPose.offsetAndRotation(-4.21f, -1.5f, -10.16f, 0.0f, 0.3927f, 0.0f));
		PartDefinition p_neck1 = p_body.addOrReplaceChild("neck1", CubeListBuilder.create()
				.texOffs(108, 0).addBox(-1.5f, -9.0f, -1.5f, 3.0f, 9.0f, 3.0f),
				PartPose.offsetAndRotation(-6.01f, -7.0f, -6.01f, 0.45f, 0.7854f, 0.0f));
		PartDefinition p_neck1b = p_neck1.addOrReplaceChild("neck1b", CubeListBuilder.create()
				.texOffs(84, 32).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 8.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -9.0f, 0.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p_head1 = p_neck1b.addOrReplaceChild("head1", CubeListBuilder.create()
				.texOffs(18, 65).addBox(-2.0f, -2.5f, -5.0f, 4.0f, 3.0f, 5.0f)
				.texOffs(44, 73).addBox(-1.5f, -1.5f, -8.0f, 3.0f, 2.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -8.0f, 0.0f, -0.75f, 0.0f, 0.0f));
		PartDefinition p_jaw1 = p_head1.addOrReplaceChild("jaw1", CubeListBuilder.create()
				.texOffs(56, 73).addBox(-1.5f, 0.0f, -3.5f, 3.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.5f, -4.5f, 0.15f, 0.0f, 0.0f));
		PartDefinition p_tail1 = p_body.addOrReplaceChild("tail1", CubeListBuilder.create()
				.texOffs(68, 44).addBox(-1.0f, -1.0f, -8.0f, 2.0f, 2.0f, 8.0f),
				PartPose.offsetAndRotation(-10.16f, -1.5f, -4.21f, 0.0f, 1.178f, 0.0f));
		PartDefinition p_neck2 = p_body.addOrReplaceChild("neck2", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-1.5f, -9.0f, -1.5f, 3.0f, 9.0f, 3.0f),
				PartPose.offsetAndRotation(-8.5f, -7.0f, 0.0f, 0.45f, 1.571f, 0.0f));
		PartDefinition p_neck2b = p_neck2.addOrReplaceChild("neck2b", CubeListBuilder.create()
				.texOffs(96, 32).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 8.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -9.0f, 0.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p_head2 = p_neck2b.addOrReplaceChild("head2", CubeListBuilder.create()
				.texOffs(36, 65).addBox(-2.0f, -2.5f, -5.0f, 4.0f, 3.0f, 5.0f)
				.texOffs(70, 73).addBox(-1.5f, -1.5f, -8.0f, 3.0f, 2.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -8.0f, 0.0f, -0.75f, 0.0f, 0.0f));
		PartDefinition p_jaw2 = p_head2.addOrReplaceChild("jaw2", CubeListBuilder.create()
				.texOffs(82, 73).addBox(-1.5f, 0.0f, -3.5f, 3.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.5f, -4.5f, 0.15f, 0.0f, 0.0f));
		PartDefinition p_tail2 = p_body.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(88, 44).addBox(-1.0f, -1.0f, -8.0f, 2.0f, 2.0f, 8.0f),
				PartPose.offsetAndRotation(-10.16f, -1.5f, 4.21f, 0.0f, 1.964f, 0.0f));
		PartDefinition p_neck3 = p_body.addOrReplaceChild("neck3", CubeListBuilder.create()
				.texOffs(12, 32).addBox(-1.5f, -9.0f, -1.5f, 3.0f, 9.0f, 3.0f),
				PartPose.offsetAndRotation(-6.01f, -7.0f, 6.01f, 0.45f, 2.356f, 0.0f));
		PartDefinition p_neck3b = p_neck3.addOrReplaceChild("neck3b", CubeListBuilder.create()
				.texOffs(108, 32).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 8.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -9.0f, 0.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p_head3 = p_neck3b.addOrReplaceChild("head3", CubeListBuilder.create()
				.texOffs(54, 65).addBox(-2.0f, -2.5f, -5.0f, 4.0f, 3.0f, 5.0f)
				.texOffs(96, 73).addBox(-1.5f, -1.5f, -8.0f, 3.0f, 2.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -8.0f, 0.0f, -0.75f, 0.0f, 0.0f));
		PartDefinition p_jaw3 = p_head3.addOrReplaceChild("jaw3", CubeListBuilder.create()
				.texOffs(108, 73).addBox(-1.5f, 0.0f, -3.5f, 3.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.5f, -4.5f, 0.15f, 0.0f, 0.0f));
		PartDefinition p_tail3 = p_body.addOrReplaceChild("tail3", CubeListBuilder.create()
				.texOffs(108, 44).addBox(-1.0f, -1.0f, -8.0f, 2.0f, 2.0f, 8.0f),
				PartPose.offsetAndRotation(-4.21f, -1.5f, 10.16f, 0.0f, 2.749f, 0.0f));
		PartDefinition p_kusanagi_glint = p_tail3.addOrReplaceChild("kusanagi_glint", CubeListBuilder.create()
				.texOffs(116, 81).addBox(-0.5f, -1.0f, -0.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, -1.0f, -6.0f));
		PartDefinition p_neck4 = p_body.addOrReplaceChild("neck4", CubeListBuilder.create()
				.texOffs(24, 32).addBox(-1.5f, -9.0f, -1.5f, 3.0f, 9.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -7.0f, 8.5f, 0.45f, 3.142f, 0.0f));
		PartDefinition p_neck4b = p_neck4.addOrReplaceChild("neck4b", CubeListBuilder.create()
				.texOffs(0, 44).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 8.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -9.0f, 0.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p_head4 = p_neck4b.addOrReplaceChild("head4", CubeListBuilder.create()
				.texOffs(72, 65).addBox(-2.0f, -2.5f, -5.0f, 4.0f, 3.0f, 5.0f)
				.texOffs(0, 81).addBox(-1.5f, -1.5f, -8.0f, 3.0f, 2.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -8.0f, 0.0f, -0.75f, 0.0f, 0.0f));
		PartDefinition p_jaw4 = p_head4.addOrReplaceChild("jaw4", CubeListBuilder.create()
				.texOffs(12, 81).addBox(-1.5f, 0.0f, -3.5f, 3.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.5f, -4.5f, 0.15f, 0.0f, 0.0f));
		PartDefinition p_tail4 = p_body.addOrReplaceChild("tail4", CubeListBuilder.create()
				.texOffs(0, 55).addBox(-1.0f, -1.0f, -8.0f, 2.0f, 2.0f, 8.0f),
				PartPose.offsetAndRotation(4.21f, -1.5f, 10.16f, 0.0f, 3.534f, 0.0f));
		PartDefinition p_neck5 = p_body.addOrReplaceChild("neck5", CubeListBuilder.create()
				.texOffs(36, 32).addBox(-1.5f, -9.0f, -1.5f, 3.0f, 9.0f, 3.0f),
				PartPose.offsetAndRotation(6.01f, -7.0f, 6.01f, 0.45f, 3.927f, 0.0f));
		PartDefinition p_neck5b = p_neck5.addOrReplaceChild("neck5b", CubeListBuilder.create()
				.texOffs(12, 44).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 8.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -9.0f, 0.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p_head5 = p_neck5b.addOrReplaceChild("head5", CubeListBuilder.create()
				.texOffs(90, 65).addBox(-2.0f, -2.5f, -5.0f, 4.0f, 3.0f, 5.0f)
				.texOffs(26, 81).addBox(-1.5f, -1.5f, -8.0f, 3.0f, 2.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -8.0f, 0.0f, -0.75f, 0.0f, 0.0f));
		PartDefinition p_jaw5 = p_head5.addOrReplaceChild("jaw5", CubeListBuilder.create()
				.texOffs(38, 81).addBox(-1.5f, 0.0f, -3.5f, 3.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.5f, -4.5f, 0.15f, 0.0f, 0.0f));
		PartDefinition p_tail5 = p_body.addOrReplaceChild("tail5", CubeListBuilder.create()
				.texOffs(20, 55).addBox(-1.0f, -1.0f, -8.0f, 2.0f, 2.0f, 8.0f),
				PartPose.offsetAndRotation(10.16f, -1.5f, 4.21f, 0.0f, 4.32f, 0.0f));
		PartDefinition p_neck6 = p_body.addOrReplaceChild("neck6", CubeListBuilder.create()
				.texOffs(48, 32).addBox(-1.5f, -9.0f, -1.5f, 3.0f, 9.0f, 3.0f),
				PartPose.offsetAndRotation(8.5f, -7.0f, 0.0f, 0.45f, 4.712f, 0.0f));
		PartDefinition p_neck6b = p_neck6.addOrReplaceChild("neck6b", CubeListBuilder.create()
				.texOffs(24, 44).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 8.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -9.0f, 0.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p_head6 = p_neck6b.addOrReplaceChild("head6", CubeListBuilder.create()
				.texOffs(108, 65).addBox(-2.0f, -2.5f, -5.0f, 4.0f, 3.0f, 5.0f)
				.texOffs(52, 81).addBox(-1.5f, -1.5f, -8.0f, 3.0f, 2.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -8.0f, 0.0f, -0.75f, 0.0f, 0.0f));
		PartDefinition p_jaw6 = p_head6.addOrReplaceChild("jaw6", CubeListBuilder.create()
				.texOffs(64, 81).addBox(-1.5f, 0.0f, -3.5f, 3.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.5f, -4.5f, 0.15f, 0.0f, 0.0f));
		PartDefinition p_tail6 = p_body.addOrReplaceChild("tail6", CubeListBuilder.create()
				.texOffs(40, 55).addBox(-1.0f, -1.0f, -8.0f, 2.0f, 2.0f, 8.0f),
				PartPose.offsetAndRotation(10.16f, -1.5f, -4.21f, 0.0f, 5.105f, 0.0f));
		PartDefinition p_neck7 = p_body.addOrReplaceChild("neck7", CubeListBuilder.create()
				.texOffs(60, 32).addBox(-1.5f, -9.0f, -1.5f, 3.0f, 9.0f, 3.0f),
				PartPose.offsetAndRotation(6.01f, -7.0f, -6.01f, 0.45f, 5.498f, 0.0f));
		PartDefinition p_neck7b = p_neck7.addOrReplaceChild("neck7b", CubeListBuilder.create()
				.texOffs(36, 44).addBox(-1.5f, -8.0f, -1.5f, 3.0f, 8.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -9.0f, 0.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p_head7 = p_neck7b.addOrReplaceChild("head7", CubeListBuilder.create()
				.texOffs(0, 73).addBox(-2.0f, -2.5f, -5.0f, 4.0f, 3.0f, 5.0f)
				.texOffs(78, 81).addBox(-1.5f, -1.5f, -8.0f, 3.0f, 2.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -8.0f, 0.0f, -0.75f, 0.0f, 0.0f));
		PartDefinition p_jaw7 = p_head7.addOrReplaceChild("jaw7", CubeListBuilder.create()
				.texOffs(90, 81).addBox(-1.5f, 0.0f, -3.5f, 3.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.5f, -4.5f, 0.15f, 0.0f, 0.0f));
		PartDefinition p_tail7 = p_body.addOrReplaceChild("tail7", CubeListBuilder.create()
				.texOffs(60, 55).addBox(-1.0f, -1.0f, -8.0f, 2.0f, 2.0f, 8.0f),
				PartPose.offsetAndRotation(4.21f, -1.5f, -10.16f, 0.0f, 5.891f, 0.0f));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition odinRaven() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5f, -2.5f, -4.0f, 5.0f, 5.0f, 8.0f),
				PartPose.offset(0.0f, 18.0f, 0.0f));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(26, 0).addBox(-2.0f, -3.0f, -3.5f, 4.0f, 4.0f, 4.0f)
				.texOffs(30, 20).addBox(-1.5f, 0.0f, -3.0f, 3.0f, 1.0f, 3.0f),
				PartPose.offset(0.0f, 16.0f, -4.0f));
		PartDefinition p_beak = p_head.addOrReplaceChild("beak", CubeListBuilder.create()
				.texOffs(20, 20).addBox(-1.0f, -1.0f, -3.0f, 2.0f, 2.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -1.0f, -3.5f, 0.12f, 0.0f, 0.0f));
		PartDefinition p_wing_r = p_body.addOrReplaceChild("wing_r", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-9.0f, -0.5f, 0.0f, 9.0f, 1.0f, 6.0f),
				PartPose.offset(-2.5f, -2.0f, -2.0f));
		PartDefinition p_wing_l = p_body.addOrReplaceChild("wing_l", CubeListBuilder.create()
				.texOffs(30, 13).addBox(0.0f, -0.5f, 0.0f, 9.0f, 1.0f, 6.0f),
				PartPose.offset(2.5f, -2.0f, -2.0f));
		PartDefinition p_tail = p_body.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(0, 20).addBox(-2.5f, 0.0f, 0.0f, 5.0f, 1.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, -1.0f, 4.0f, -0.25f, 0.0f, 0.0f));
		PartDefinition p_leg_r = p_body.addOrReplaceChild("leg_r", CubeListBuilder.create()
				.texOffs(42, 20).addBox(-0.5f, 0.0f, 0.0f, 1.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(-1.2f, 2.5f, 0.0f, 0.6f, 0.0f, 0.0f));
		PartDefinition p_leg_l = p_body.addOrReplaceChild("leg_l", CubeListBuilder.create()
				.texOffs(46, 20).addBox(-0.5f, 0.0f, 0.0f, 1.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(1.2f, 2.5f, 0.0f, 0.6f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 32);
	}

	public static LayerDefinition skidbladnir() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_bottom = root.addOrReplaceChild("bottom", CubeListBuilder.create()
				.texOffs(40, 36).addBox(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 3.0f, 1.0f, 1.571f, 0.0f, 0.0f));
		PartDefinition p_back = root.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(28, 56).addBox(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f),
				PartPose.offsetAndRotation(-15.0f, 4.0f, 4.0f, 0.0f, 4.712f, 0.0f));
		PartDefinition p_front = root.addOrReplaceChild("front", CubeListBuilder.create()
				.texOffs(68, 56).addBox(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f),
				PartPose.offsetAndRotation(15.0f, 4.0f, 0.0f, 0.0f, 1.571f, 0.0f));
		PartDefinition p_right = root.addOrReplaceChild("right", CubeListBuilder.create()
				.texOffs(0, 69).addBox(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, 4.0f, -9.0f, 0.0f, 3.142f, 0.0f));
		PartDefinition p_left = root.addOrReplaceChild("left", CubeListBuilder.create()
				.texOffs(60, 69).addBox(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f),
				PartPose.offset(0.0f, 4.0f, 9.0f));
		PartDefinition p_left_paddle = root.addOrReplaceChild("left_paddle", CubeListBuilder.create()
				.texOffs(50, 0).addBox(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f)
				.texOffs(102, 36).addBox(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f),
				PartPose.offsetAndRotation(3.0f, -5.0f, 9.0f, 0.0f, 0.0f, 0.1963f));
		PartDefinition p_right_paddle = root.addOrReplaceChild("right_paddle", CubeListBuilder.create()
				.texOffs(0, 36).addBox(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f)
				.texOffs(0, 56).addBox(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f),
				PartPose.offsetAndRotation(3.0f, -5.0f, -9.0f, 0.0f, 3.142f, 0.1963f));
		PartDefinition p_prow = root.addOrReplaceChild("prow", CubeListBuilder.create()
				.texOffs(16, 56).addBox(-1.0f, -9.0f, -1.5f, 3.0f, 10.0f, 3.0f)
				.texOffs(0, 77).addBox(1.0f, -11.0f, -1.5f, 5.0f, 3.0f, 3.0f),
				PartPose.offset(16.0f, -2.0f, 0.0f));
		PartDefinition p_mast = root.addOrReplaceChild("mast", CubeListBuilder.create()
				.texOffs(42, 0).addBox(-1.0f, -30.0f, -1.0f, 2.0f, 30.0f, 2.0f),
				PartPose.offset(-10.0f, 1.0f, 0.0f));
		PartDefinition p_sail = p_mast.addOrReplaceChild("sail", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5f, 0.0f, -10.0f, 1.0f, 16.0f, 20.0f),
				PartPose.offset(0.0f, -27.0f, 0.0f));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition freyjaFalcon() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5f, -2.5f, -4.5f, 5.0f, 5.0f, 9.0f),
				PartPose.offset(0.0f, 16.0f, 0.0f));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(28, 0).addBox(-2.0f, -3.5f, -3.5f, 4.0f, 4.0f, 4.0f),
				PartPose.offset(0.0f, 14.0f, -4.5f));
		PartDefinition p_beak = p_head.addOrReplaceChild("beak", CubeListBuilder.create()
				.texOffs(58, 22).addBox(-0.5f, -0.5f, -2.0f, 1.0f, 2.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, -3.5f, 0.35f, 0.0f, 0.0f));
		PartDefinition p_wing_r = p_body.addOrReplaceChild("wing_r", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-12.0f, -0.5f, 0.0f, 12.0f, 1.0f, 7.0f),
				PartPose.offset(-2.5f, -2.0f, -3.0f));
		PartDefinition p_wing_l = p_body.addOrReplaceChild("wing_l", CubeListBuilder.create()
				.texOffs(0, 22).addBox(0.0f, -0.5f, 0.0f, 12.0f, 1.0f, 7.0f),
				PartPose.offset(2.5f, -2.0f, -3.0f));
		PartDefinition p_tail = p_body.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(38, 22).addBox(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 4.5f, -0.15f, 0.0f, 0.0f));
		PartDefinition p_leg_r = p_body.addOrReplaceChild("leg_r", CubeListBuilder.create()
				.texOffs(0, 30).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(-1.2f, 2.5f, 1.0f, 0.9f, 0.0f, 0.0f));
		PartDefinition p_leg_l = p_body.addOrReplaceChild("leg_l", CubeListBuilder.create()
				.texOffs(4, 30).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(1.2f, 2.5f, 1.0f, 0.9f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 64);
	}

	public static LayerDefinition pegasus() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0f, -5.0f, -11.0f, 10.0f, 10.0f, 22.0f),
				PartPose.offset(0.0f, 9.0f, 0.0f));
		PartDefinition p_blanket = p_body.addOrReplaceChild("blanket", CubeListBuilder.create()
				.texOffs(32, 32).addBox(-5.5f, -5.5f, -4.0f, 11.0f, 5.0f, 8.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create()
				.texOffs(92, 0).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(-3.0f, 14.0f, -8.0f));
		PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create()
				.texOffs(108, 0).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(3.0f, 14.0f, -8.0f));
		PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(-3.0f, 14.0f, 8.0f));
		PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create()
				.texOffs(16, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
				PartPose.offset(3.0f, 14.0f, 8.0f));
		PartDefinition p_neck = p_body.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(64, 0).addBox(-2.0f, -12.0f, -3.0f, 4.0f, 13.0f, 6.0f)
				.texOffs(84, 0).addBox(-1.0f, -13.0f, 3.0f, 2.0f, 14.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -3.0f, -9.0f, 0.5f, 0.0f, 0.0f));
		PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(70, 32).addBox(-3.0f, -4.0f, -5.0f, 6.0f, 6.0f, 7.0f)
				.texOffs(76, 59).addBox(-2.0f, -3.0f, -10.0f, 4.0f, 5.0f, 5.0f)
				.texOffs(12, 70).addBox(-2.5f, -7.0f, 0.0f, 2.0f, 3.0f, 1.0f)
				.texOffs(18, 70).addBox(0.5f, -7.0f, 0.0f, 2.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, -1.0f, 0.2f, 0.0f, 0.0f));
		PartDefinition p_bridle = p_head.addOrReplaceChild("bridle", CubeListBuilder.create()
				.texOffs(0, 70).addBox(-2.5f, -3.5f, -8.0f, 5.0f, 6.0f, 1.0f)
				.texOffs(106, 59).addBox(-3.5f, -4.5f, -2.0f, 7.0f, 7.0f, 1.0f)
				.texOffs(24, 70).addBox(-3.0f, 0.0f, -9.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(28, 70).addBox(2.0f, 0.0f, -9.0f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_wing_r = p_body.addOrReplaceChild("wing_r", CubeListBuilder.create()
				.texOffs(0, 59).addBox(-10.0f, -1.0f, 0.0f, 10.0f, 2.0f, 9.0f),
				PartPose.offsetAndRotation(-5.0f, -4.0f, -6.0f, 0.0f, 0.2f, 0.6f));
		PartDefinition p_wing_r2 = p_wing_r.addOrReplaceChild("wing_r2", CubeListBuilder.create()
				.texOffs(0, 46).addBox(-12.0f, -0.5f, 0.0f, 12.0f, 1.0f, 12.0f),
				PartPose.offsetAndRotation(-10.0f, 0.0f, 0.0f, 0.0f, 0.1f, 0.3f));
		PartDefinition p_wing_l = p_body.addOrReplaceChild("wing_l", CubeListBuilder.create()
				.texOffs(38, 59).addBox(0.0f, -1.0f, 0.0f, 10.0f, 2.0f, 9.0f),
				PartPose.offsetAndRotation(5.0f, -4.0f, -6.0f, 0.0f, -0.2f, -0.6f));
		PartDefinition p_wing_l2 = p_wing_l.addOrReplaceChild("wing_l2", CubeListBuilder.create()
				.texOffs(48, 46).addBox(0.0f, -0.5f, 0.0f, 12.0f, 1.0f, 12.0f),
				PartPose.offsetAndRotation(10.0f, 0.0f, 0.0f, 0.0f, -0.1f, -0.3f));
		PartDefinition p_tail = p_body.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(96, 46).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -4.0f, 11.0f, 0.6f, 0.0f, 0.0f));
		PartDefinition p_tail2 = p_tail.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(94, 59).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 7.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 10.0f, 0.0f, 0.3f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition talos() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 19).addBox(-6.0f, 0.0f, -3.0f, 12.0f, 12.0f, 6.0f)
				.texOffs(0, 66).addBox(-6.5f, 9.0f, -3.5f, 13.0f, 4.0f, 7.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(20, 37).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
				.texOffs(0, 54).addBox(-4.5f, -8.5f, -4.5f, 9.0f, 3.0f, 9.0f)
				.texOffs(0, 77).addBox(-3.0f, -2.0f, -5.0f, 6.0f, 4.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_arm_r = root.addOrReplaceChild("arm_r", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0f, -1.0f, -2.5f, 4.0f, 14.0f, 5.0f),
				PartPose.offset(-6.0f, 1.0f, 0.0f));
		PartDefinition p_rock = p_arm_r.addOrReplaceChild("rock", CubeListBuilder.create()
				.texOffs(40, 66).addBox(-3.0f, -1.0f, -4.0f, 6.0f, 5.0f, 6.0f),
				PartPose.offset(-2.0f, 13.0f, -1.0f));
		PartDefinition p_arm_l = root.addOrReplaceChild("arm_l", CubeListBuilder.create()
				.texOffs(18, 0).addBox(0.0f, -1.0f, -2.5f, 4.0f, 14.0f, 5.0f),
				PartPose.offset(6.0f, 1.0f, 0.0f));
		PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
				.texOffs(36, 19).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 12.0f, 5.0f),
				PartPose.offset(-3.0f, 12.0f, 0.0f));
		PartDefinition p_nail = p_leg_r.addOrReplaceChild("nail", CubeListBuilder.create()
				.texOffs(16, 77).addBox(-4.0f, 9.0f, -0.5f, 2.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
				.texOffs(0, 37).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 12.0f, 5.0f),
				PartPose.offset(3.0f, 12.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 128);
	}

	public static LayerDefinition freyrSword() {
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

	public static LayerDefinition mjolnir() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_hammer = root.addOrReplaceChild("hammer", CubeListBuilder.create()
				.texOffs(8, 0).addBox(-4.5f, -9.0f, -2.5f, 9.0f, 5.0f, 5.0f)
				.texOffs(0, 0).addBox(-1.0f, -4.0f, -1.0f, 2.0f, 9.0f, 2.0f)
				.texOffs(36, 0).addBox(-1.5f, 5.0f, -1.5f, 3.0f, 2.0f, 3.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 64, 16);
	}

	public static LayerDefinition gungnir() {
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

	public static LayerDefinition mistletoeDart() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p_dart = root.addOrReplaceChild("dart", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5f, -8.0f, -0.5f, 1.0f, 15.0f, 1.0f)
				.texOffs(12, 0).addBox(-0.5f, -11.0f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(4, 0).addBox(-1.0f, 5.0f, -1.0f, 2.0f, 3.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p_leaf_r0 = p_dart.addOrReplaceChild("leaf_r0", CubeListBuilder.create()
				.texOffs(16, 0).addBox(-3.0f, -0.5f, -1.0f, 3.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(-0.5f, -1.0f, 0.0f, 0.0f, 0.0f, -0.9f));
		PartDefinition p_leaf_l0 = p_dart.addOrReplaceChild("leaf_l0", CubeListBuilder.create()
				.texOffs(0, 16).addBox(0.0f, -0.5f, -1.0f, 3.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.9f));
		PartDefinition p_leaf_r1 = p_dart.addOrReplaceChild("leaf_r1", CubeListBuilder.create()
				.texOffs(10, 16).addBox(-3.0f, -0.5f, -1.0f, 3.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(-0.5f, 3.0f, 0.0f, 0.0f, 0.0f, -0.9f));
		PartDefinition p_leaf_l1 = p_dart.addOrReplaceChild("leaf_l1", CubeListBuilder.create()
				.texOffs(20, 16).addBox(0.0f, -0.5f, -1.0f, 3.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.5f, 3.0f, 0.0f, 0.0f, 0.0f, 0.9f));
		PartDefinition p_berries = p_dart.addOrReplaceChild("berries", CubeListBuilder.create()
				.texOffs(0, 19).addBox(-1.0f, -2.0f, -1.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(4, 19).addBox(0.0f, 2.0f, 0.0f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 32, 32);
	}
}
