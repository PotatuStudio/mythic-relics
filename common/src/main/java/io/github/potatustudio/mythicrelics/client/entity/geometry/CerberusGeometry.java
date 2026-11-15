package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad cerberus. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class CerberusGeometry {
	private CerberusGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p0 = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.5f, -6.0f, -9.0f, 11.0f, 10.0f, 9.0f)
				.texOffs(22, 19).addBox(-4.5f, -4.0f, -0.5f, 9.0f, 7.0f, 6.0f)
				.texOffs(52, 19).addBox(-5.0f, -4.5f, 5.0f, 10.0f, 8.0f, 5.0f)
				.texOffs(68, 0).addBox(-6.5f, -7.5f, -10.5f, 13.0f, 10.0f, 5.0f)
				.texOffs(0, 76).addBox(-4.5f, -7.0f, -4.0f, 9.0f, 2.0f, 5.0f),
				PartPose.offset(0.0f, 10.0f, 0.0f));
		PartDefinition p1 = p0.addOrReplaceChild("mane", CubeListBuilder.create()
				.texOffs(40, 0).addBox(0.0f, -4.0f, 0.0f, 0.0f, 4.0f, 14.0f),
				PartPose.offset(0.0f, -7.0f, -4.0f));
		PartDefinition p2 = p0.addOrReplaceChild("ruff_tuft_l", CubeListBuilder.create()
				.texOffs(70, 46).addBox(0.0f, -4.0f, 0.0f, 0.0f, 4.0f, 6.0f),
				PartPose.offsetAndRotation(6.5f, -4.0f, -8.0f, 0.0f, 0.0f, -0.5f));
		PartDefinition p3 = p0.addOrReplaceChild("ruff_tuft_r", CubeListBuilder.create()
				.texOffs(82, 46).addBox(0.0f, -4.0f, 0.0f, 0.0f, 4.0f, 6.0f),
				PartPose.offsetAndRotation(-6.5f, -4.0f, -8.0f, 0.0f, 0.0f, 0.5f));
		PartDefinition p4 = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(82, 19).addBox(-3.5f, -4.0f, -6.0f, 7.0f, 7.0f, 6.0f)
				.texOffs(22, 89).addBox(-4.0f, -4.5f, -6.5f, 8.0f, 2.0f, 2.0f)
				.texOffs(94, 46).addBox(-2.0f, -2.0f, -12.0f, 4.0f, 3.0f, 7.0f)
				.texOffs(12, 83).addBox(-2.5f, -1.5f, -7.0f, 5.0f, 3.0f, 2.0f)
				.texOffs(82, 89).addBox(-1.5f, 1.0f, -11.5f, 1.0f, 2.0f, 1.0f)
				.texOffs(86, 89).addBox(0.5f, 1.0f, -11.5f, 1.0f, 2.0f, 1.0f)
				.texOffs(88, 33).addBox(-3.0f, -3.5f, -1.0f, 6.0f, 6.0f, 6.0f)
				.texOffs(44, 58).addBox(-3.5f, -4.0f, 1.5f, 7.0f, 7.0f, 2.0f)
				.texOffs(90, 89).addBox(-0.5f, -6.0f, 2.0f, 1.0f, 2.0f, 1.0f)
				.texOffs(118, 89).addBox(-5.5f, -1.0f, 2.0f, 2.0f, 1.0f, 1.0f)
				.texOffs(0, 94).addBox(3.5f, -1.0f, 2.0f, 2.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 4.5f, -9.8f));
		PartDefinition p5 = p4.addOrReplaceChild("head_ear_l", CubeListBuilder.create()
				.texOffs(26, 83).addBox(-1.5f, -4.0f, -0.5f, 3.0f, 4.0f, 1.0f),
				PartPose.offsetAndRotation(2.2f, -3.5f, -2.5f, -0.85f, 0.0f, 0.3f));
		PartDefinition p6 = p4.addOrReplaceChild("head_ear_r", CubeListBuilder.create()
				.texOffs(34, 83).addBox(-1.5f, -4.0f, -0.5f, 3.0f, 4.0f, 1.0f),
				PartPose.offsetAndRotation(-2.2f, -3.5f, -2.5f, -0.85f, 0.0f, -0.3f));
		PartDefinition p7 = p4.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(0, 68).addBox(-2.0f, 0.0f, -5.5f, 4.0f, 2.0f, 6.0f)
				.texOffs(42, 83).addBox(-1.0f, -0.5f, -4.5f, 2.0f, 1.0f, 4.0f)
				.texOffs(6, 94).addBox(-1.8f, -1.0f, -5.2f, 1.0f, 1.0f, 1.0f)
				.texOffs(10, 94).addBox(0.8f, -1.0f, -5.2f, 1.0f, 1.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, 1.0f, -6.0f, 0.32f, 0.0f, 0.0f));
		PartDefinition p8 = root.addOrReplaceChild("left_head", CubeListBuilder.create()
				.texOffs(0, 33).addBox(-3.5f, -4.0f, -6.0f, 7.0f, 7.0f, 6.0f)
				.texOffs(42, 89).addBox(-4.0f, -4.5f, -6.5f, 8.0f, 2.0f, 2.0f)
				.texOffs(0, 58).addBox(-2.0f, -2.0f, -12.0f, 4.0f, 3.0f, 7.0f)
				.texOffs(54, 83).addBox(-2.5f, -1.5f, -7.0f, 5.0f, 3.0f, 2.0f)
				.texOffs(94, 89).addBox(-1.5f, 1.0f, -11.5f, 1.0f, 2.0f, 1.0f)
				.texOffs(98, 89).addBox(0.5f, 1.0f, -11.5f, 1.0f, 2.0f, 1.0f)
				.texOffs(0, 46).addBox(-3.0f, -3.5f, -1.0f, 6.0f, 6.0f, 6.0f)
				.texOffs(62, 58).addBox(-3.5f, -4.0f, 1.5f, 7.0f, 7.0f, 2.0f)
				.texOffs(102, 89).addBox(-0.5f, -6.0f, 2.0f, 1.0f, 2.0f, 1.0f)
				.texOffs(14, 94).addBox(-5.5f, -1.0f, 2.0f, 2.0f, 1.0f, 1.0f)
				.texOffs(20, 94).addBox(3.5f, -1.0f, 2.0f, 2.0f, 1.0f, 1.0f),
				PartPose.offsetAndRotation(6.0f, 6.2f, -9.0f, 0.0f, -0.55f, -0.12f));
		PartDefinition p9 = p8.addOrReplaceChild("left_head_ear_l", CubeListBuilder.create()
				.texOffs(68, 83).addBox(-1.5f, -4.0f, -0.5f, 3.0f, 4.0f, 1.0f),
				PartPose.offsetAndRotation(2.2f, -3.5f, -2.5f, -0.85f, 0.0f, 0.3f));
		PartDefinition p10 = p8.addOrReplaceChild("left_head_ear_r", CubeListBuilder.create()
				.texOffs(76, 83).addBox(-1.5f, -4.0f, -0.5f, 3.0f, 4.0f, 1.0f),
				PartPose.offsetAndRotation(-2.2f, -3.5f, -2.5f, -0.85f, 0.0f, -0.3f));
		PartDefinition p11 = p8.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(20, 68).addBox(-2.0f, 0.0f, -5.5f, 4.0f, 2.0f, 6.0f)
				.texOffs(84, 83).addBox(-1.0f, -0.5f, -4.5f, 2.0f, 1.0f, 4.0f)
				.texOffs(26, 94).addBox(-1.8f, -1.0f, -5.2f, 1.0f, 1.0f, 1.0f)
				.texOffs(30, 94).addBox(0.8f, -1.0f, -5.2f, 1.0f, 1.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, 1.0f, -6.0f, 0.32f, 0.0f, 0.0f));
		PartDefinition p12 = root.addOrReplaceChild("right_head", CubeListBuilder.create()
				.texOffs(26, 33).addBox(-3.5f, -4.0f, -6.0f, 7.0f, 7.0f, 6.0f)
				.texOffs(62, 89).addBox(-4.0f, -4.5f, -6.5f, 8.0f, 2.0f, 2.0f)
				.texOffs(22, 58).addBox(-2.0f, -2.0f, -12.0f, 4.0f, 3.0f, 7.0f)
				.texOffs(96, 83).addBox(-2.5f, -1.5f, -7.0f, 5.0f, 3.0f, 2.0f)
				.texOffs(106, 89).addBox(-1.5f, 1.0f, -11.5f, 1.0f, 2.0f, 1.0f)
				.texOffs(110, 89).addBox(0.5f, 1.0f, -11.5f, 1.0f, 2.0f, 1.0f)
				.texOffs(24, 46).addBox(-3.0f, -3.5f, -1.0f, 6.0f, 6.0f, 6.0f)
				.texOffs(80, 58).addBox(-3.5f, -4.0f, 1.5f, 7.0f, 7.0f, 2.0f)
				.texOffs(114, 89).addBox(-0.5f, -6.0f, 2.0f, 1.0f, 2.0f, 1.0f)
				.texOffs(34, 94).addBox(-5.5f, -1.0f, 2.0f, 2.0f, 1.0f, 1.0f)
				.texOffs(40, 94).addBox(3.5f, -1.0f, 2.0f, 2.0f, 1.0f, 1.0f),
				PartPose.offsetAndRotation(-6.0f, 6.2f, -9.0f, 0.0f, 0.55f, 0.12f));
		PartDefinition p13 = p12.addOrReplaceChild("right_head_ear_l", CubeListBuilder.create()
				.texOffs(110, 83).addBox(-1.5f, -4.0f, -0.5f, 3.0f, 4.0f, 1.0f),
				PartPose.offsetAndRotation(2.2f, -3.5f, -2.5f, -0.85f, 0.0f, 0.3f));
		PartDefinition p14 = p12.addOrReplaceChild("right_head_ear_r", CubeListBuilder.create()
				.texOffs(118, 83).addBox(-1.5f, -4.0f, -0.5f, 3.0f, 4.0f, 1.0f),
				PartPose.offsetAndRotation(-2.2f, -3.5f, -2.5f, -0.85f, 0.0f, -0.3f));
		PartDefinition p15 = p12.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(40, 68).addBox(-2.0f, 0.0f, -5.5f, 4.0f, 2.0f, 6.0f)
				.texOffs(0, 89).addBox(-1.0f, -0.5f, -4.5f, 2.0f, 1.0f, 4.0f)
				.texOffs(46, 94).addBox(-1.8f, -1.0f, -5.2f, 1.0f, 1.0f, 1.0f)
				.texOffs(50, 94).addBox(0.8f, -1.0f, -5.2f, 1.0f, 1.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, 1.0f, -6.0f, 0.32f, 0.0f, 0.0f));
		PartDefinition p16 = p0.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(48, 46).addBox(-2.0f, -2.0f, -1.0f, 4.0f, 4.0f, 7.0f),
				PartPose.offsetAndRotation(0.0f, -2.0f, 9.5f, -0.3f, 0.0f, 0.0f));
		PartDefinition p17 = p16.addOrReplaceChild("tail_mid", CubeListBuilder.create()
				.texOffs(98, 58).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 5.5f, 0.7f, 0.0f, 0.0f));
		PartDefinition p18 = p17.addOrReplaceChild("tail_tip", CubeListBuilder.create()
				.texOffs(28, 76).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 5.5f, 0.55f, 0.0f, 0.0f));
		PartDefinition p19 = p18.addOrReplaceChild("tail_end", CubeListBuilder.create()
				.texOffs(12, 89).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 4.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 4.5f, -0.5f, 0.0f, 0.0f));
		PartDefinition p20 = root.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
				.texOffs(52, 33).addBox(-2.0f, -2.0f, -2.5f, 4.0f, 8.0f, 5.0f),
				PartPose.offset(3.8f, 12.0f, -5.0f));
		PartDefinition p21 = p20.addOrReplaceChild("left_front_leg_lower", CubeListBuilder.create()
				.texOffs(60, 68).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 5.0f, 3.0f)
				.texOffs(42, 76).addBox(-2.0f, 4.0f, -3.0f, 4.0f, 2.0f, 5.0f)
				.texOffs(54, 94).addBox(-1.9f, 5.0f, -4.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(58, 94).addBox(-0.5f, 5.0f, -4.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(62, 94).addBox(0.9f, 5.0f, -4.0f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 6.0f, 0.0f));
		PartDefinition p22 = root.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
				.texOffs(70, 33).addBox(-2.0f, -2.0f, -2.5f, 4.0f, 8.0f, 5.0f),
				PartPose.offset(-3.8f, 12.0f, -5.0f));
		PartDefinition p23 = p22.addOrReplaceChild("right_front_leg_lower", CubeListBuilder.create()
				.texOffs(72, 68).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 5.0f, 3.0f)
				.texOffs(60, 76).addBox(-2.0f, 4.0f, -3.0f, 4.0f, 2.0f, 5.0f)
				.texOffs(66, 94).addBox(-1.9f, 5.0f, -4.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(70, 94).addBox(-0.5f, 5.0f, -4.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(74, 94).addBox(0.9f, 5.0f, -4.0f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, 6.0f, 0.0f));
		PartDefinition p24 = root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
				.texOffs(104, 0).addBox(-2.5f, -2.0f, -3.0f, 5.0f, 8.0f, 6.0f),
				PartPose.offsetAndRotation(3.6f, 11.77f, 7.0f, -0.3f, 0.0f, 0.0f));
		PartDefinition p25 = p24.addOrReplaceChild("left_hind_leg_lower", CubeListBuilder.create()
				.texOffs(84, 68).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 5.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 4.5f, 0.5f, 0.75f, 0.0f, 0.0f));
		PartDefinition p26 = p25.addOrReplaceChild("left_hind_leg_foot", CubeListBuilder.create()
				.texOffs(114, 76).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 3.0f, 3.0f)
				.texOffs(78, 76).addBox(-2.0f, 2.0f, -3.0f, 4.0f, 2.0f, 5.0f)
				.texOffs(78, 94).addBox(-1.9f, 3.0f, -4.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(82, 94).addBox(-0.5f, 3.0f, -4.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(86, 94).addBox(0.9f, 3.0f, -4.0f, 1.0f, 1.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, 4.2f, 0.0f, -0.45f, 0.0f, 0.0f));
		PartDefinition p27 = root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
				.texOffs(0, 19).addBox(-2.5f, -2.0f, -3.0f, 5.0f, 8.0f, 6.0f),
				PartPose.offsetAndRotation(-3.6f, 11.77f, 7.0f, -0.3f, 0.0f, 0.0f));
		PartDefinition p28 = p27.addOrReplaceChild("right_hind_leg_lower", CubeListBuilder.create()
				.texOffs(96, 68).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 5.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 4.5f, 0.5f, 0.75f, 0.0f, 0.0f));
		PartDefinition p29 = p28.addOrReplaceChild("right_hind_leg_foot", CubeListBuilder.create()
				.texOffs(0, 83).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 3.0f, 3.0f)
				.texOffs(96, 76).addBox(-2.0f, 2.0f, -3.0f, 4.0f, 2.0f, 5.0f)
				.texOffs(90, 94).addBox(-1.9f, 3.0f, -4.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(94, 94).addBox(-0.5f, 3.0f, -4.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(98, 94).addBox(0.9f, 3.0f, -4.0f, 1.0f, 1.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, 4.2f, 0.0f, -0.45f, 0.0f, 0.0f));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
