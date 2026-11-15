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
		PartDefinition p0 = root.addOrReplaceChild("seg0", CubeListBuilder.create()
				.texOffs(116, 57).addBox(-4.5f, -4.5f, 0.0f, 9.0f, 9.0f, 10.0f)
				.texOffs(220, 110).addBox(-3.0f, 4.5f, 0.5f, 6.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(0.0f, 8.5f, -16.0f, -0.12f, 0.0f, 0.0f));
		PartDefinition p1 = p0.addOrReplaceChild("crest0", CubeListBuilder.create()
				.texOffs(100, 96).addBox(0.0f, -4.0f, 0.5f, 0.0f, 4.0f, 9.0f),
				PartPose.offset(0.0f, -4.5f, 0.0f));
		PartDefinition p2 = p0.addOrReplaceChild("seg1", CubeListBuilder.create()
				.texOffs(54, 30).addBox(-6.0f, -6.0f, -2.0f, 12.0f, 12.0f, 14.0f)
				.texOffs(138, 96).addBox(-4.5f, 6.0f, 0.5f, 9.0f, 1.0f, 11.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 10.0f, 0.12f, 0.0f, 0.0f));
		PartDefinition p3 = p2.addOrReplaceChild("crest1", CubeListBuilder.create()
				.texOffs(66, 79).addBox(0.0f, -5.0f, 0.5f, 0.0f, 5.0f, 11.0f),
				PartPose.offset(0.0f, -6.0f, 0.0f));
		PartDefinition p4 = p2.addOrReplaceChild("seg2", CubeListBuilder.create()
				.texOffs(160, 0).addBox(-6.5f, -6.5f, -2.0f, 13.0f, 13.0f, 14.0f)
				.texOffs(178, 96).addBox(-5.0f, 6.5f, 0.5f, 10.0f, 1.0f, 11.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 12.0f, 0.12f, 0.12f, 0.0f));
		PartDefinition p5 = p4.addOrReplaceChild("crest2", CubeListBuilder.create()
				.texOffs(0, 79).addBox(0.0f, -6.0f, 0.5f, 0.0f, 6.0f, 11.0f),
				PartPose.offset(0.0f, -6.5f, 0.0f));
		PartDefinition p6 = p4.addOrReplaceChild("seg3", CubeListBuilder.create()
				.texOffs(104, 0).addBox(-7.0f, -7.0f, -2.0f, 14.0f, 14.0f, 14.0f)
				.texOffs(0, 110).addBox(-5.5f, 7.0f, 0.5f, 11.0f, 1.0f, 11.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 12.0f, 0.0f, 0.08f, 0.0f));
		PartDefinition p7 = p6.addOrReplaceChild("crest3", CubeListBuilder.create()
				.texOffs(22, 79).addBox(0.0f, -6.0f, 0.5f, 0.0f, 6.0f, 11.0f),
				PartPose.offset(0.0f, -7.0f, 0.0f));
		PartDefinition p8 = p6.addOrReplaceChild("seg4", CubeListBuilder.create()
				.texOffs(0, 30).addBox(-6.5f, -6.5f, -2.0f, 13.0f, 13.0f, 14.0f)
				.texOffs(44, 110).addBox(-5.0f, 6.5f, 0.5f, 10.0f, 1.0f, 11.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 12.0f, -0.18f, -0.14f, 0.0f));
		PartDefinition p9 = p8.addOrReplaceChild("crest4", CubeListBuilder.create()
				.texOffs(44, 79).addBox(0.0f, -6.0f, 0.5f, 0.0f, 6.0f, 11.0f),
				PartPose.offset(0.0f, -6.5f, 0.0f));
		PartDefinition p10 = p8.addOrReplaceChild("seg5", CubeListBuilder.create()
				.texOffs(106, 30).addBox(-5.5f, -5.5f, -2.0f, 11.0f, 11.0f, 14.0f)
				.texOffs(86, 110).addBox(-4.0f, 5.5f, 0.5f, 8.0f, 1.0f, 11.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 12.0f, -0.14f, -0.18f, 0.0f));
		PartDefinition p11 = p10.addOrReplaceChild("crest5", CubeListBuilder.create()
				.texOffs(88, 79).addBox(0.0f, -5.0f, 0.5f, 0.0f, 5.0f, 11.0f),
				PartPose.offset(0.0f, -5.5f, 0.0f));
		PartDefinition p12 = p10.addOrReplaceChild("seg6", CubeListBuilder.create()
				.texOffs(0, 57).addBox(-4.5f, -4.5f, -2.0f, 9.0f, 9.0f, 13.0f)
				.texOffs(124, 110).addBox(-3.0f, 4.5f, 0.5f, 6.0f, 1.0f, 10.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 12.0f, 0.1f, 0.12f, 0.0f));
		PartDefinition p13 = p12.addOrReplaceChild("crest6", CubeListBuilder.create()
				.texOffs(0, 96).addBox(0.0f, -4.0f, 0.5f, 0.0f, 4.0f, 10.0f),
				PartPose.offset(0.0f, -4.5f, 0.0f));
		PartDefinition p14 = p12.addOrReplaceChild("seg7", CubeListBuilder.create()
				.texOffs(154, 57).addBox(-3.5f, -3.5f, -1.0f, 7.0f, 7.0f, 12.0f)
				.texOffs(156, 110).addBox(-2.0f, 3.5f, 0.5f, 4.0f, 1.0f, 10.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 11.0f, 0.22f, 0.22f, 0.0f));
		PartDefinition p15 = p14.addOrReplaceChild("crest7", CubeListBuilder.create()
				.texOffs(118, 96).addBox(0.0f, -3.0f, 0.5f, 0.0f, 3.0f, 10.0f),
				PartPose.offset(0.0f, -3.5f, 0.0f));
		PartDefinition p16 = p14.addOrReplaceChild("seg8", CubeListBuilder.create()
				.texOffs(110, 79).addBox(-2.5f, -2.5f, -1.0f, 5.0f, 5.0f, 11.0f)
				.texOffs(0, 122).addBox(-1.0f, 2.5f, 0.5f, 2.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 11.0f, 0.2f, 0.12f, 0.0f));
		PartDefinition p17 = p16.addOrReplaceChild("crest8", CubeListBuilder.create()
				.texOffs(184, 110).addBox(0.0f, -2.0f, 0.5f, 0.0f, 2.0f, 9.0f),
				PartPose.offset(0.0f, -2.5f, 0.0f));
		PartDefinition p18 = p16.addOrReplaceChild("seg9", CubeListBuilder.create()
				.texOffs(20, 96).addBox(-1.5f, -1.5f, -1.0f, 3.0f, 3.0f, 11.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 10.0f, 0.1f, -0.2f, 0.0f));
		PartDefinition p19 = p18.addOrReplaceChild("crest9", CubeListBuilder.create()
				.texOffs(202, 110).addBox(0.0f, -2.0f, 0.5f, 0.0f, 2.0f, 9.0f),
				PartPose.offset(0.0f, -1.5f, 0.0f));
		PartDefinition p20 = p18.addOrReplaceChild("tuft", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0f, -7.0f, 0.0f, 0.0f, 14.0f, 16.0f)
				.texOffs(48, 96).addBox(-6.0f, 0.0f, 0.0f, 12.0f, 0.0f, 14.0f),
				PartPose.offset(0.0f, 0.0f, 9.0f));
		PartDefinition p21 = p2.addOrReplaceChild("seg1_leg_r", CubeListBuilder.create()
				.texOffs(22, 122).addBox(-2.0f, -1.0f, -2.0f, 4.0f, 6.0f, 4.0f),
				PartPose.offsetAndRotation(-5.5f, 3.0f, 6.0f, 1.25f, 0.0f, 0.6f));
		PartDefinition p22 = p21.addOrReplaceChild("seg1_shin_r", CubeListBuilder.create()
				.texOffs(222, 122).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 5.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 4.5f, 0.0f, -0.55f, 0.0f, 0.0f));
		PartDefinition p23 = p22.addOrReplaceChild("seg1_elbow_r", CubeListBuilder.create()
				.texOffs(142, 79).addBox(0.0f, -2.0f, 0.0f, 0.0f, 7.0f, 9.0f),
				PartPose.offsetAndRotation(-1.5f, 0.5f, 1.0f, 0.6f, -0.5f, 0.0f));
		PartDefinition p24 = p22.addOrReplaceChild("seg1_foot_r", CubeListBuilder.create()
				.texOffs(190, 132).addBox(-2.5f, 0.0f, -3.0f, 5.0f, 2.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, -0.45f, 0.0f, 0.0f));
		PartDefinition p25 = p24.addOrReplaceChild("seg1_claw-4_r", CubeListBuilder.create()
				.texOffs(94, 140).addBox(-2.0f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(210, 140).addBox(-2.0f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p26 = p24.addOrReplaceChild("seg1_claw-1_r", CubeListBuilder.create()
				.texOffs(100, 140).addBox(-0.5f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(214, 140).addBox(-0.5f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p27 = p24.addOrReplaceChild("seg1_claw2_r", CubeListBuilder.create()
				.texOffs(106, 140).addBox(1.0f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(218, 140).addBox(1.0f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p28 = p24.addOrReplaceChild("seg1_spur_r", CubeListBuilder.create()
				.texOffs(112, 140).addBox(-0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(222, 140).addBox(-0.5f, 1.5f, 2.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p29 = p2.addOrReplaceChild("seg1_leg_l", CubeListBuilder.create()
				.texOffs(38, 122).addBox(-2.0f, -1.0f, -2.0f, 4.0f, 6.0f, 4.0f),
				PartPose.offsetAndRotation(5.5f, 3.0f, 6.0f, 1.25f, 0.0f, -0.6f));
		PartDefinition p30 = p29.addOrReplaceChild("seg1_shin_l", CubeListBuilder.create()
				.texOffs(234, 122).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 5.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 4.5f, 0.0f, -0.55f, 0.0f, 0.0f));
		PartDefinition p31 = p30.addOrReplaceChild("seg1_elbow_l", CubeListBuilder.create()
				.texOffs(160, 79).addBox(0.0f, -2.0f, 0.0f, 0.0f, 7.0f, 9.0f),
				PartPose.offsetAndRotation(1.5f, 0.5f, 1.0f, 0.6f, 0.5f, 0.0f));
		PartDefinition p32 = p30.addOrReplaceChild("seg1_foot_l", CubeListBuilder.create()
				.texOffs(208, 132).addBox(-2.5f, 0.0f, -3.0f, 5.0f, 2.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, -0.45f, 0.0f, 0.0f));
		PartDefinition p33 = p32.addOrReplaceChild("seg1_claw-4_l", CubeListBuilder.create()
				.texOffs(118, 140).addBox(-2.0f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(226, 140).addBox(-2.0f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p34 = p32.addOrReplaceChild("seg1_claw-1_l", CubeListBuilder.create()
				.texOffs(124, 140).addBox(-0.5f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(230, 140).addBox(-0.5f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p35 = p32.addOrReplaceChild("seg1_claw2_l", CubeListBuilder.create()
				.texOffs(130, 140).addBox(1.0f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(234, 140).addBox(1.0f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p36 = p32.addOrReplaceChild("seg1_spur_l", CubeListBuilder.create()
				.texOffs(136, 140).addBox(-0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(238, 140).addBox(-0.5f, 1.5f, 2.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p37 = p8.addOrReplaceChild("seg4_leg_r", CubeListBuilder.create()
				.texOffs(54, 122).addBox(-2.0f, -1.0f, -2.0f, 4.0f, 6.0f, 4.0f),
				PartPose.offsetAndRotation(-6.0f, 3.5f, 6.0f, 1.25f, 0.0f, 0.6f));
		PartDefinition p38 = p37.addOrReplaceChild("seg4_shin_r", CubeListBuilder.create()
				.texOffs(0, 132).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 5.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 4.5f, 0.0f, -0.55f, 0.0f, 0.0f));
		PartDefinition p39 = p38.addOrReplaceChild("seg4_elbow_r", CubeListBuilder.create()
				.texOffs(178, 79).addBox(0.0f, -2.0f, 0.0f, 0.0f, 7.0f, 9.0f),
				PartPose.offsetAndRotation(-1.5f, 0.5f, 1.0f, 0.6f, -0.5f, 0.0f));
		PartDefinition p40 = p38.addOrReplaceChild("seg4_foot_r", CubeListBuilder.create()
				.texOffs(226, 132).addBox(-2.5f, 0.0f, -3.0f, 5.0f, 2.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, -0.45f, 0.0f, 0.0f));
		PartDefinition p41 = p40.addOrReplaceChild("seg4_claw-4_r", CubeListBuilder.create()
				.texOffs(142, 140).addBox(-2.0f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(242, 140).addBox(-2.0f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p42 = p40.addOrReplaceChild("seg4_claw-1_r", CubeListBuilder.create()
				.texOffs(148, 140).addBox(-0.5f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(246, 140).addBox(-0.5f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p43 = p40.addOrReplaceChild("seg4_claw2_r", CubeListBuilder.create()
				.texOffs(154, 140).addBox(1.0f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(250, 140).addBox(1.0f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p44 = p40.addOrReplaceChild("seg4_spur_r", CubeListBuilder.create()
				.texOffs(160, 140).addBox(-0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(0, 146).addBox(-0.5f, 1.5f, 2.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p45 = p8.addOrReplaceChild("seg4_leg_l", CubeListBuilder.create()
				.texOffs(70, 122).addBox(-2.0f, -1.0f, -2.0f, 4.0f, 6.0f, 4.0f),
				PartPose.offsetAndRotation(6.0f, 3.5f, 6.0f, 1.25f, 0.0f, -0.6f));
		PartDefinition p46 = p45.addOrReplaceChild("seg4_shin_l", CubeListBuilder.create()
				.texOffs(12, 132).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 5.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 4.5f, 0.0f, -0.55f, 0.0f, 0.0f));
		PartDefinition p47 = p46.addOrReplaceChild("seg4_elbow_l", CubeListBuilder.create()
				.texOffs(196, 79).addBox(0.0f, -2.0f, 0.0f, 0.0f, 7.0f, 9.0f),
				PartPose.offsetAndRotation(1.5f, 0.5f, 1.0f, 0.6f, 0.5f, 0.0f));
		PartDefinition p48 = p46.addOrReplaceChild("seg4_foot_l", CubeListBuilder.create()
				.texOffs(0, 140).addBox(-2.5f, 0.0f, -3.0f, 5.0f, 2.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 5.0f, 0.0f, -0.45f, 0.0f, 0.0f));
		PartDefinition p49 = p48.addOrReplaceChild("seg4_claw-4_l", CubeListBuilder.create()
				.texOffs(166, 140).addBox(-2.0f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(4, 146).addBox(-2.0f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p50 = p48.addOrReplaceChild("seg4_claw-1_l", CubeListBuilder.create()
				.texOffs(172, 140).addBox(-0.5f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(8, 146).addBox(-0.5f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p51 = p48.addOrReplaceChild("seg4_claw2_l", CubeListBuilder.create()
				.texOffs(178, 140).addBox(1.0f, 0.5f, -5.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(12, 146).addBox(1.0f, 1.5f, -5.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p52 = p48.addOrReplaceChild("seg4_spur_l", CubeListBuilder.create()
				.texOffs(184, 140).addBox(-0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 2.0f)
				.texOffs(16, 146).addBox(-0.5f, 1.5f, 2.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p53 = p0.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(192, 57).addBox(-6.0f, -7.0f, -10.0f, 12.0f, 9.0f, 10.0f)
				.texOffs(24, 132).addBox(-4.0f, -9.0f, -9.0f, 8.0f, 2.0f, 6.0f)
				.texOffs(214, 79).addBox(-4.5f, -5.0f, -19.0f, 9.0f, 6.0f, 9.0f)
				.texOffs(132, 132).addBox(-3.5f, -7.0f, -20.0f, 7.0f, 3.0f, 4.0f)
				.texOffs(154, 132).addBox(-6.5f, -8.0f, -11.0f, 4.0f, 2.0f, 5.0f)
				.texOffs(172, 132).addBox(2.5f, -8.0f, -11.0f, 4.0f, 2.0f, 5.0f)
				.texOffs(18, 140).addBox(-6.5f, -6.0f, -10.5f, 2.0f, 2.0f, 3.0f)
				.texOffs(28, 140).addBox(4.5f, -6.0f, -10.5f, 2.0f, 2.0f, 3.0f)
				.texOffs(190, 140).addBox(-4.0f, 1.0f, -18.5f, 1.0f, 2.0f, 1.0f)
				.texOffs(194, 140).addBox(3.0f, 1.0f, -18.5f, 1.0f, 2.0f, 1.0f)
				.texOffs(198, 122).addBox(-6.5f, -3.0f, -6.0f, 1.0f, 4.0f, 5.0f)
				.texOffs(210, 122).addBox(5.5f, -3.0f, -6.0f, 1.0f, 4.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, -1.0f, 0.0f, 0.05f, 0.0f, 0.0f));
		PartDefinition p54 = p53.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(86, 122).addBox(-4.0f, 0.0f, -8.0f, 8.0f, 2.0f, 8.0f)
				.texOffs(20, 146).addBox(-3.5f, -1.0f, -7.5f, 1.0f, 1.0f, 1.0f)
				.texOffs(24, 146).addBox(2.5f, -1.0f, -7.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, 1.0f, -10.0f, 0.3f, 0.0f, 0.0f));
		PartDefinition p55 = p54.addOrReplaceChild("beard", CubeListBuilder.create()
				.texOffs(156, 30).addBox(0.0f, 0.0f, -6.0f, 0.0f, 12.0f, 12.0f),
				PartPose.offset(0.0f, 2.0f, -3.0f));
		PartDefinition p56 = p54.addOrReplaceChild("beard_r", CubeListBuilder.create()
				.texOffs(76, 57).addBox(0.0f, 0.0f, -5.0f, 0.0f, 10.0f, 10.0f),
				PartPose.offsetAndRotation(-3.0f, 2.0f, -3.0f, 0.0f, -0.35f, 0.0f));
		PartDefinition p57 = p53.addOrReplaceChild("ear_r", CubeListBuilder.create()
				.texOffs(52, 132).addBox(-0.5f, -1.5f, 0.0f, 1.0f, 3.0f, 5.0f),
				PartPose.offsetAndRotation(-6.0f, -6.0f, -3.0f, 0.2f, 0.7f, 0.5f));
		PartDefinition p58 = p53.addOrReplaceChild("antler_r", CubeListBuilder.create()
				.texOffs(64, 132).addBox(-1.0f, -6.0f, -1.0f, 2.0f, 6.0f, 2.0f)
				.texOffs(38, 140).addBox(-0.5f, -4.0f, -3.5f, 1.0f, 1.0f, 3.0f),
				PartPose.offsetAndRotation(-3.0f, -8.0f, -5.0f, -0.45f, 0.0f, -0.35f));
		PartDefinition p59 = p58.addOrReplaceChild("antler2_r", CubeListBuilder.create()
				.texOffs(72, 132).addBox(-0.5f, -7.0f, -0.5f, 1.0f, 7.0f, 1.0f)
				.texOffs(46, 140).addBox(-0.5f, -3.0f, -3.0f, 1.0f, 1.0f, 3.0f)
				.texOffs(54, 140).addBox(-0.5f, -6.0f, 0.5f, 1.0f, 1.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -6.0f, 0.0f, -0.5f, 0.0f, -0.25f));
		PartDefinition p60 = p59.addOrReplaceChild("antler3_r", CubeListBuilder.create()
				.texOffs(62, 140).addBox(-0.5f, -3.0f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(198, 140).addBox(-0.5f, -2.0f, -2.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -7.0f, 0.0f, 0.6f, 0.0f, 0.0f));
		PartDefinition p61 = p53.addOrReplaceChild("whisker_r", CubeListBuilder.create()
				.texOffs(118, 122).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(-4.0f, -2.0f, -17.0f, -0.2f, -0.85f, 0.0f));
		PartDefinition p62 = p61.addOrReplaceChild("whisker2_r", CubeListBuilder.create()
				.texOffs(138, 122).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 9.0f, -0.55f, 0.0f, 0.0f));
		PartDefinition p63 = p62.addOrReplaceChild("whisker3_r", CubeListBuilder.create()
				.texOffs(76, 132).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 7.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 9.0f, -0.6f, 0.3f, 0.0f));
		PartDefinition p64 = p53.addOrReplaceChild("mane_r", CubeListBuilder.create()
				.texOffs(32, 0).addBox(0.0f, 0.0f, 0.0f, 0.0f, 11.0f, 18.0f),
				PartPose.offsetAndRotation(-6.0f, -6.0f, -5.0f, 0.1f, -0.55f, 0.0f));
		PartDefinition p65 = p53.addOrReplaceChild("mane2_r", CubeListBuilder.create()
				.texOffs(180, 30).addBox(0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 15.0f),
				PartPose.offsetAndRotation(-5.0f, -8.0f, -3.0f, -0.35f, -0.3f, 0.0f));
		PartDefinition p66 = p54.addOrReplaceChild("beard_l", CubeListBuilder.create()
				.texOffs(96, 57).addBox(0.0f, 0.0f, -5.0f, 0.0f, 10.0f, 10.0f),
				PartPose.offsetAndRotation(3.0f, 2.0f, -3.0f, 0.0f, 0.35f, 0.0f));
		PartDefinition p67 = p53.addOrReplaceChild("ear_l", CubeListBuilder.create()
				.texOffs(92, 132).addBox(-0.5f, -1.5f, 0.0f, 1.0f, 3.0f, 5.0f),
				PartPose.offsetAndRotation(6.0f, -6.0f, -3.0f, 0.2f, -0.7f, -0.5f));
		PartDefinition p68 = p53.addOrReplaceChild("antler_l", CubeListBuilder.create()
				.texOffs(104, 132).addBox(-1.0f, -6.0f, -1.0f, 2.0f, 6.0f, 2.0f)
				.texOffs(66, 140).addBox(-0.5f, -4.0f, -3.5f, 1.0f, 1.0f, 3.0f),
				PartPose.offsetAndRotation(3.0f, -8.0f, -5.0f, -0.45f, 0.0f, 0.35f));
		PartDefinition p69 = p68.addOrReplaceChild("antler2_l", CubeListBuilder.create()
				.texOffs(112, 132).addBox(-0.5f, -7.0f, -0.5f, 1.0f, 7.0f, 1.0f)
				.texOffs(74, 140).addBox(-0.5f, -3.0f, -3.0f, 1.0f, 1.0f, 3.0f)
				.texOffs(82, 140).addBox(-0.5f, -6.0f, 0.5f, 1.0f, 1.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, -6.0f, 0.0f, -0.5f, 0.0f, 0.25f));
		PartDefinition p70 = p69.addOrReplaceChild("antler3_l", CubeListBuilder.create()
				.texOffs(90, 140).addBox(-0.5f, -3.0f, -0.5f, 1.0f, 3.0f, 1.0f)
				.texOffs(204, 140).addBox(-0.5f, -2.0f, -2.5f, 1.0f, 1.0f, 2.0f),
				PartPose.offsetAndRotation(0.0f, -7.0f, 0.0f, 0.6f, 0.0f, 0.0f));
		PartDefinition p71 = p53.addOrReplaceChild("whisker_l", CubeListBuilder.create()
				.texOffs(158, 122).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(4.0f, -2.0f, -17.0f, -0.2f, 0.85f, 0.0f));
		PartDefinition p72 = p71.addOrReplaceChild("whisker2_l", CubeListBuilder.create()
				.texOffs(178, 122).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 9.0f, -0.55f, 0.0f, 0.0f));
		PartDefinition p73 = p72.addOrReplaceChild("whisker3_l", CubeListBuilder.create()
				.texOffs(116, 132).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 7.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 9.0f, -0.6f, -0.3f, 0.0f));
		PartDefinition p74 = p53.addOrReplaceChild("mane_l", CubeListBuilder.create()
				.texOffs(68, 0).addBox(0.0f, 0.0f, 0.0f, 0.0f, 11.0f, 18.0f),
				PartPose.offsetAndRotation(6.0f, -6.0f, -5.0f, 0.1f, 0.55f, 0.0f));
		PartDefinition p75 = p53.addOrReplaceChild("mane2_l", CubeListBuilder.create()
				.texOffs(210, 30).addBox(0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 15.0f),
				PartPose.offsetAndRotation(5.0f, -8.0f, -3.0f, -0.35f, 0.3f, 0.0f));
		PartDefinition p76 = p53.addOrReplaceChild("mane_top", CubeListBuilder.create()
				.texOffs(44, 57).addBox(0.0f, -5.0f, 0.0f, 0.0f, 6.0f, 16.0f),
				PartPose.offsetAndRotation(0.0f, -9.0f, -4.0f, -0.15f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
