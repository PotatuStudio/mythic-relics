package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad pegasus. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class PegasusGeometry {
	private PegasusGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p0 = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0f, -5.0f, -10.0f, 10.0f, 10.0f, 20.0f)
				.texOffs(144, 0).addBox(-5.5f, -3.5f, -12.0f, 11.0f, 8.0f, 6.0f)
				.texOffs(84, 0).addBox(-5.5f, -5.5f, 4.0f, 11.0f, 9.0f, 8.0f)
				.texOffs(148, 44).addBox(-4.0f, -6.5f, 5.0f, 8.0f, 1.0f, 6.0f)
				.texOffs(132, 30).addBox(-4.5f, 4.5f, -6.0f, 9.0f, 1.0f, 10.0f)
				.texOffs(176, 44).addBox(-2.0f, -6.5f, -10.0f, 4.0f, 2.0f, 5.0f)
				.texOffs(170, 30).addBox(-5.5f, -5.4f, -3.0f, 11.0f, 4.0f, 7.0f),
				PartPose.offset(0.0f, 9.0f, 0.0f));
		PartDefinition p1 = root.addOrReplaceChild("leg_fr", CubeListBuilder.create()
				.texOffs(238, 30).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 6.0f, 4.0f),
				PartPose.offset(-2.8f, 13.0f, -9.2f));
		PartDefinition p2 = p1.addOrReplaceChild("leg_fr_lower", CubeListBuilder.create()
				.texOffs(148, 54).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(160, 54).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(80, 61).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(60, 54).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f),
				PartPose.offset(0.0f, 4.0f, 0.0f));
		PartDefinition p3 = root.addOrReplaceChild("leg_fl", CubeListBuilder.create()
				.texOffs(0, 44).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 6.0f, 4.0f),
				PartPose.offset(2.8f, 13.0f, -9.2f));
		PartDefinition p4 = p3.addOrReplaceChild("leg_fl_lower", CubeListBuilder.create()
				.texOffs(170, 54).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(182, 54).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(92, 61).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(76, 54).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f),
				PartPose.offset(0.0f, 4.0f, 0.0f));
		PartDefinition p5 = root.addOrReplaceChild("leg_br", CubeListBuilder.create()
				.texOffs(16, 44).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 6.0f, 4.0f),
				PartPose.offset(-2.8f, 13.0f, 7.8f));
		PartDefinition p6 = p5.addOrReplaceChild("leg_br_lower", CubeListBuilder.create()
				.texOffs(192, 54).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(204, 54).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(104, 61).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(92, 54).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f)
				.texOffs(214, 54).addBox(-1.0f, -1.0f, 1.0f, 2.0f, 3.0f, 2.0f),
				PartPose.offset(0.0f, 4.0f, 0.0f));
		PartDefinition p7 = root.addOrReplaceChild("leg_bl", CubeListBuilder.create()
				.texOffs(32, 44).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 6.0f, 4.0f),
				PartPose.offset(2.8f, 13.0f, 7.8f));
		PartDefinition p8 = p7.addOrReplaceChild("leg_bl_lower", CubeListBuilder.create()
				.texOffs(222, 54).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(234, 54).addBox(-1.5f, 1.5f, -1.0f, 3.0f, 3.0f, 2.0f)
				.texOffs(116, 61).addBox(-1.5f, 4.5f, -1.5f, 3.0f, 1.0f, 3.0f)
				.texOffs(108, 54).addBox(-2.0f, 5.0f, -2.6f, 4.0f, 2.0f, 4.0f)
				.texOffs(244, 54).addBox(-1.0f, -1.0f, 1.0f, 2.0f, 3.0f, 2.0f),
				PartPose.offset(0.0f, 4.0f, 0.0f));
		PartDefinition p9 = p0.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(60, 0).addBox(-2.5f, -12.0f, -3.0f, 5.0f, 13.0f, 7.0f)
				.texOffs(178, 0).addBox(-3.0f, -4.0f, -3.5f, 6.0f, 6.0f, 8.0f),
				PartPose.offsetAndRotation(0.0f, -3.0f, -8.0f, 0.45f, 0.0f, 0.0f));
		PartDefinition p10 = p9.addOrReplaceChild("mane", CubeListBuilder.create()
				.texOffs(128, 61).addBox(-1.0f, -13.0f, 3.5f, 2.0f, 2.0f, 2.0f)
				.texOffs(136, 61).addBox(-1.5f, -11.4f, 3.7f, 3.0f, 2.0f, 2.0f)
				.texOffs(146, 61).addBox(-1.0f, -9.8f, 3.5f, 2.0f, 2.0f, 2.0f)
				.texOffs(154, 61).addBox(-1.5f, -8.2f, 3.7f, 3.0f, 2.0f, 2.0f)
				.texOffs(164, 61).addBox(-1.0f, -6.6f, 3.5f, 2.0f, 2.0f, 2.0f)
				.texOffs(172, 61).addBox(-1.5f, -5.0f, 3.7f, 3.0f, 2.0f, 2.0f)
				.texOffs(182, 61).addBox(-1.0f, -3.4f, 3.5f, 2.0f, 2.0f, 2.0f)
				.texOffs(190, 61).addBox(-1.5f, -1.8f, 3.7f, 3.0f, 2.0f, 2.0f)
				.texOffs(122, 0).addBox(-0.5f, -13.8f, 3.8f, 1.0f, 13.0f, 2.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p11 = p9.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(26, 30).addBox(-3.0f, -4.5f, -5.0f, 6.0f, 6.0f, 7.0f)
				.texOffs(194, 44).addBox(-3.5f, -1.2f, -1.0f, 7.0f, 3.0f, 4.0f)
				.texOffs(48, 44).addBox(-2.0f, -4.0f, -10.5f, 4.0f, 4.0f, 6.0f)
				.texOffs(0, 61).addBox(-1.5f, -0.2f, -10.0f, 3.0f, 1.0f, 4.0f)
				.texOffs(200, 61).addBox(-1.0f, -6.5f, -2.5f, 2.0f, 2.0f, 2.0f)
				.texOffs(172, 74).addBox(-1.5f, -5.6f, -2.0f, 3.0f, 1.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, -1.0f, 0.25f, 0.0f, 0.0f));
		PartDefinition p12 = p11.addOrReplaceChild("ear_l", CubeListBuilder.create()
				.texOffs(208, 61).addBox(-1.0f, -3.0f, -0.5f, 2.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(1.75f, -4.5f, 0.5f, 0.2f, 0.0f, 0.25f));
		PartDefinition p13 = p12.addOrReplaceChild("ear_l_tip", CubeListBuilder.create()
				.texOffs(180, 74).addBox(-0.5f, -1.0f, -0.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, -3.0f, 0.0f));
		PartDefinition p14 = p11.addOrReplaceChild("ear_r", CubeListBuilder.create()
				.texOffs(214, 61).addBox(-1.0f, -3.0f, -0.5f, 2.0f, 3.0f, 1.0f),
				PartPose.offsetAndRotation(-1.75f, -4.5f, 0.5f, 0.2f, 0.0f, -0.25f));
		PartDefinition p15 = p14.addOrReplaceChild("ear_r_tip", CubeListBuilder.create()
				.texOffs(184, 74).addBox(-0.5f, -1.0f, -0.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, -3.0f, 0.0f));
		PartDefinition p16 = p11.addOrReplaceChild("bridle", CubeListBuilder.create()
				.texOffs(220, 61).addBox(-3.5f, -4.8f, 0.2f, 7.0f, 3.0f, 1.0f)
				.texOffs(188, 74).addBox(-3.4f, -4.8f, -3.6f, 7.0f, 1.0f, 1.0f)
				.texOffs(124, 54).addBox(-2.5f, -4.3f, -8.0f, 5.0f, 5.0f, 1.0f)
				.texOffs(216, 44).addBox(-3.6f, -2.0f, -6.0f, 0.0f, 1.0f, 6.0f)
				.texOffs(228, 44).addBox(3.6f, -2.0f, -6.0f, 0.0f, 1.0f, 6.0f)
				.texOffs(136, 54).addBox(-3.7f, -3.0f, -3.5f, 0.0f, 3.0f, 3.0f)
				.texOffs(142, 54).addBox(3.7f, -3.0f, -3.5f, 0.0f, 3.0f, 3.0f)
				.texOffs(204, 74).addBox(-3.0f, -1.5f, -9.5f, 1.0f, 1.0f, 1.0f)
				.texOffs(208, 74).addBox(2.0f, -1.5f, -9.5f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p17 = p11.addOrReplaceChild("rein_r", CubeListBuilder.create()
				.texOffs(206, 0).addBox(0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 13.0f),
				PartPose.offsetAndRotation(-3.6f, -1.0f, -9.0f, -0.15f, 0.0f, 0.0f));
		PartDefinition p18 = p11.addOrReplaceChild("rein_l", CubeListBuilder.create()
				.texOffs(0, 30).addBox(0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 13.0f),
				PartPose.offsetAndRotation(3.6f, -1.0f, -9.0f, -0.15f, 0.0f, 0.0f));
		PartDefinition p19 = p0.addOrReplaceChild("wing_r", CubeListBuilder.create()
				.texOffs(0, 66).addBox(-9.0f, -1.5f, -0.5f, 9.0f, 2.0f, 2.0f)
				.texOffs(0, 54).addBox(-9.0f, -0.5f, 1.5f, 9.0f, 1.0f, 6.0f)
				.texOffs(68, 44).addBox(-8.5f, -0.1f, 5.0f, 8.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(-3.5f, -5.0f, -6.0f, 0.0f, 0.2f, 0.4f));
		PartDefinition p20 = p19.addOrReplaceChild("wing_r2", CubeListBuilder.create()
				.texOffs(22, 66).addBox(-9.0f, -1.5f, -0.5f, 9.0f, 2.0f, 2.0f)
				.texOffs(14, 61).addBox(-8.5f, -0.6f, 1.5f, 8.0f, 1.0f, 4.0f)
				.texOffs(52, 30).addBox(-9.0f, -0.2f, 4.0f, 9.0f, 1.0f, 11.0f),
				PartPose.offsetAndRotation(-9.0f, 0.0f, 0.0f, 0.0f, 0.1f, 0.15f));
		PartDefinition p21 = p20.addOrReplaceChild("wing_r3", CubeListBuilder.create()
				.texOffs(136, 74).addBox(-7.0f, -1.0f, -0.5f, 7.0f, 1.0f, 2.0f)
				.texOffs(44, 66).addBox(-7.0f, -0.6f, 1.5f, 7.0f, 1.0f, 3.0f),
				PartPose.offsetAndRotation(-9.0f, 0.0f, 0.0f, 0.0f, 0.25f, 0.45f));
		PartDefinition p22 = p21.addOrReplaceChild("wing_r_primary0", CubeListBuilder.create()
				.texOffs(64, 66).addBox(-16.0f, 0.0f, -2.0f, 16.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(-7.0f, 0.1f, 1.0f, 0.0f, 0.08f, 0.0f));
		PartDefinition p23 = p21.addOrReplaceChild("wing_r_primary1", CubeListBuilder.create()
				.texOffs(104, 66).addBox(-15.0f, 0.0f, -2.0f, 15.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(-6.0f, 0.21f, 1.0f, 0.0f, 0.22f, 0.0f));
		PartDefinition p24 = p21.addOrReplaceChild("wing_r_primary2", CubeListBuilder.create()
				.texOffs(142, 66).addBox(-15.0f, 0.0f, -2.0f, 15.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(-5.0f, 0.32f, 1.0f, 0.0f, 0.36f, 0.0f));
		PartDefinition p25 = p21.addOrReplaceChild("wing_r_primary3", CubeListBuilder.create()
				.texOffs(180, 66).addBox(-14.0f, 0.0f, -2.0f, 14.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(-4.0f, 0.43f, 1.0f, 0.0f, 0.5f, 0.0f));
		PartDefinition p26 = p21.addOrReplaceChild("wing_r_primary4", CubeListBuilder.create()
				.texOffs(216, 66).addBox(-13.0f, 0.0f, -2.0f, 13.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(-3.0f, 0.54f, 1.0f, 0.0f, 0.64f, 0.0f));
		PartDefinition p27 = p21.addOrReplaceChild("wing_r_primary5", CubeListBuilder.create()
				.texOffs(0, 70).addBox(-13.0f, 0.0f, -2.0f, 13.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(-2.0f, 0.65f, 1.0f, 0.0f, 0.78f, 0.0f));
		PartDefinition p28 = p21.addOrReplaceChild("wing_r_primary6", CubeListBuilder.create()
				.texOffs(34, 70).addBox(-12.0f, 0.0f, -2.0f, 12.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(-1.0f, 0.76f, 1.0f, 0.0f, 0.92f, 0.0f));
		PartDefinition p29 = p0.addOrReplaceChild("wing_l", CubeListBuilder.create()
				.texOffs(66, 70).addBox(0.0f, -1.5f, -0.5f, 9.0f, 2.0f, 2.0f)
				.texOffs(30, 54).addBox(0.0f, -0.5f, 1.5f, 9.0f, 1.0f, 6.0f)
				.texOffs(102, 44).addBox(0.5f, -0.1f, 5.0f, 8.0f, 1.0f, 9.0f),
				PartPose.offsetAndRotation(3.5f, -5.0f, -6.0f, 0.0f, -0.2f, -0.4f));
		PartDefinition p30 = p29.addOrReplaceChild("wing_l2", CubeListBuilder.create()
				.texOffs(88, 70).addBox(0.0f, -1.5f, -0.5f, 9.0f, 2.0f, 2.0f)
				.texOffs(38, 61).addBox(0.5f, -0.6f, 1.5f, 8.0f, 1.0f, 4.0f)
				.texOffs(92, 30).addBox(0.0f, -0.2f, 4.0f, 9.0f, 1.0f, 11.0f),
				PartPose.offsetAndRotation(9.0f, 0.0f, 0.0f, 0.0f, -0.1f, -0.15f));
		PartDefinition p31 = p30.addOrReplaceChild("wing_l3", CubeListBuilder.create()
				.texOffs(154, 74).addBox(0.0f, -1.0f, -0.5f, 7.0f, 1.0f, 2.0f)
				.texOffs(110, 70).addBox(0.0f, -0.6f, 1.5f, 7.0f, 1.0f, 3.0f),
				PartPose.offsetAndRotation(9.0f, 0.0f, 0.0f, 0.0f, -0.25f, -0.45f));
		PartDefinition p32 = p31.addOrReplaceChild("wing_l_primary0", CubeListBuilder.create()
				.texOffs(130, 70).addBox(0.0f, 0.0f, -2.0f, 16.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(7.0f, 0.1f, 1.0f, 0.0f, -0.08f, 0.0f));
		PartDefinition p33 = p31.addOrReplaceChild("wing_l_primary1", CubeListBuilder.create()
				.texOffs(170, 70).addBox(0.0f, 0.0f, -2.0f, 15.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(6.0f, 0.21f, 1.0f, 0.0f, -0.22f, 0.0f));
		PartDefinition p34 = p31.addOrReplaceChild("wing_l_primary2", CubeListBuilder.create()
				.texOffs(208, 70).addBox(0.0f, 0.0f, -2.0f, 15.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(5.0f, 0.32f, 1.0f, 0.0f, -0.36f, 0.0f));
		PartDefinition p35 = p31.addOrReplaceChild("wing_l_primary3", CubeListBuilder.create()
				.texOffs(0, 74).addBox(0.0f, 0.0f, -2.0f, 14.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(4.0f, 0.43f, 1.0f, 0.0f, -0.5f, 0.0f));
		PartDefinition p36 = p31.addOrReplaceChild("wing_l_primary4", CubeListBuilder.create()
				.texOffs(36, 74).addBox(0.0f, 0.0f, -2.0f, 13.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(3.0f, 0.54f, 1.0f, 0.0f, -0.64f, 0.0f));
		PartDefinition p37 = p31.addOrReplaceChild("wing_l_primary5", CubeListBuilder.create()
				.texOffs(70, 74).addBox(0.0f, 0.0f, -2.0f, 13.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(2.0f, 0.65f, 1.0f, 0.0f, -0.78f, 0.0f));
		PartDefinition p38 = p31.addOrReplaceChild("wing_l_primary6", CubeListBuilder.create()
				.texOffs(104, 74).addBox(0.0f, 0.0f, -2.0f, 12.0f, 0.0f, 4.0f),
				PartPose.offsetAndRotation(1.0f, 0.76f, 1.0f, 0.0f, -0.92f, 0.0f));
		PartDefinition p39 = p0.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(136, 44).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 5.0f, 3.0f)
				.texOffs(62, 61).addBox(-2.5f, 3.3f, -2.0f, 5.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, -4.5f, 11.5f, 0.4f, 0.0f, 0.0f));
		PartDefinition p40 = p39.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(206, 30).addBox(-2.0f, 0.0f, -1.5f, 4.0f, 8.0f, 3.0f)
				.texOffs(220, 30).addBox(-2.5f, 6.0f, -2.0f, 5.0f, 7.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 4.5f, 0.0f, -0.1f, 0.0f, 0.0f));
		PartDefinition p41 = p40.addOrReplaceChild("tail_lock_r", CubeListBuilder.create()
				.texOffs(128, 0).addBox(0.0f, 0.0f, -1.5f, 0.0f, 11.0f, 4.0f),
				PartPose.offsetAndRotation(-2.6f, 4.0f, 0.0f, 0.1f, 0.0f, -0.12f));
		PartDefinition p42 = p40.addOrReplaceChild("tail_lock_l", CubeListBuilder.create()
				.texOffs(136, 0).addBox(0.0f, 0.0f, -1.5f, 0.0f, 11.0f, 4.0f),
				PartPose.offsetAndRotation(2.6f, 4.0f, 0.0f, 0.1f, 0.0f, 0.12f));
		return LayerDefinition.create(mesh, 256, 128);
	}
}
