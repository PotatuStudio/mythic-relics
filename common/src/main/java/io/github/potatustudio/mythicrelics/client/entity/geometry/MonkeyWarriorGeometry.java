package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad monkey_warrior. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class MonkeyWarriorGeometry {
	private MonkeyWarriorGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p0 = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(98, 35).addBox(-4.0f, -5.0f, -2.5f, 8.0f, 5.0f, 5.0f)
				.texOffs(66, 35).addBox(-5.0f, -10.0f, -3.0f, 10.0f, 5.0f, 6.0f)
				.texOffs(50, 47).addBox(-4.5f, -2.0f, -3.0f, 9.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, 15.0f, 0.5f, 0.32f, 0.0f, 0.0f));
		PartDefinition p1 = p0.addOrReplaceChild("kilt", CubeListBuilder.create()
				.texOffs(0, 35).addBox(-5.0f, 0.0f, -3.5f, 10.0f, 5.0f, 7.0f)
				.texOffs(110, 65).addBox(-2.0f, 4.0f, -4.0f, 4.0f, 2.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -0.5f, 0.0f, -0.32f, 0.0f, 0.0f));
		PartDefinition p2 = p0.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(4, 47).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 7.0f),
				PartPose.offsetAndRotation(0.0f, -1.5f, 2.5f, -0.95f, 0.0f, 0.0f));
		PartDefinition p3 = p2.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(80, 47).addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 6.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 6.5f, 0.9f, 0.0f, 0.0f));
		PartDefinition p4 = p3.addOrReplaceChild("tail3", CubeListBuilder.create()
				.texOffs(24, 65).addBox(-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 5.0f),
				PartPose.offsetAndRotation(0.0f, 0.0f, 5.5f, 1.1f, 0.0f, 0.0f));
		PartDefinition p5 = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(44, 0).addBox(-4.0f, -7.0f, -4.0f, 8.0f, 7.0f, 8.0f)
				.texOffs(36, 65).addBox(-2.5f, -3.0f, -6.0f, 5.0f, 3.0f, 2.0f)
				.texOffs(16, 72).addBox(-3.5f, -5.0f, -4.5f, 7.0f, 1.0f, 1.0f)
				.texOffs(40, 57).addBox(-5.0f, -4.0f, -2.5f, 1.0f, 4.0f, 3.0f)
				.texOffs(48, 57).addBox(4.0f, -4.0f, -2.5f, 1.0f, 4.0f, 3.0f)
				.texOffs(78, 65).addBox(-5.5f, -6.0f, 0.5f, 1.0f, 2.0f, 2.0f)
				.texOffs(84, 65).addBox(4.5f, -6.0f, 0.5f, 1.0f, 2.0f, 2.0f)
				.texOffs(32, 72).addBox(-4.5f, -6.0f, -4.5f, 9.0f, 1.0f, 1.0f)
				.texOffs(52, 72).addBox(-4.5f, -6.0f, 3.5f, 9.0f, 1.0f, 1.0f)
				.texOffs(96, 47).addBox(-4.5f, -6.0f, -3.5f, 1.0f, 1.0f, 7.0f)
				.texOffs(112, 47).addBox(3.5f, -6.0f, -3.5f, 1.0f, 1.0f, 7.0f)
				.texOffs(22, 47).addBox(-3.5f, -9.0f, -3.5f, 7.0f, 2.0f, 7.0f)
				.texOffs(50, 65).addBox(-1.5f, -11.0f, -1.5f, 3.0f, 2.0f, 3.0f)
				.texOffs(120, 65).addBox(-1.0f, -10.0f, -4.5f, 2.0f, 2.0f, 1.0f),
				PartPose.offset(0.0f, 6.5f, -3.5f));
		PartDefinition p6 = p5.addOrReplaceChild("wing_r", CubeListBuilder.create()
				.texOffs(8, 0).addBox(0.0f, -7.0f, -3.0f, 0.0f, 7.0f, 9.0f),
				PartPose.offsetAndRotation(-3.5f, -8.5f, 0.0f, 0.0f, 0.4f, -0.5f));
		PartDefinition p7 = p5.addOrReplaceChild("feather_r", CubeListBuilder.create()
				.texOffs(100, 0).addBox(-0.5f, -12.0f, -0.5f, 1.0f, 12.0f, 1.0f),
				PartPose.offsetAndRotation(-1.5f, -10.5f, -0.5f, -0.3f, 0.0f, -0.2f));
		PartDefinition p8 = p7.addOrReplaceChild("feather_r2", CubeListBuilder.create()
				.texOffs(104, 0).addBox(-0.5f, -12.0f, -0.5f, 1.0f, 12.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, 0.0f, -0.45f, 0.0f, -0.12f));
		PartDefinition p9 = p8.addOrReplaceChild("feather_r3", CubeListBuilder.create()
				.texOffs(124, 35).addBox(-0.5f, -9.0f, -0.5f, 1.0f, 9.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, 0.0f, -0.5f, 0.0f, -0.1f));
		PartDefinition p10 = p5.addOrReplaceChild("wing_l", CubeListBuilder.create()
				.texOffs(26, 0).addBox(0.0f, -7.0f, -3.0f, 0.0f, 7.0f, 9.0f),
				PartPose.offsetAndRotation(3.5f, -8.5f, 0.0f, 0.0f, -0.4f, 0.5f));
		PartDefinition p11 = p5.addOrReplaceChild("feather_l", CubeListBuilder.create()
				.texOffs(108, 0).addBox(-0.5f, -12.0f, -0.5f, 1.0f, 12.0f, 1.0f),
				PartPose.offsetAndRotation(1.5f, -10.5f, -0.5f, -0.3f, 0.0f, 0.2f));
		PartDefinition p12 = p11.addOrReplaceChild("feather_l2", CubeListBuilder.create()
				.texOffs(112, 0).addBox(-0.5f, -12.0f, -0.5f, 1.0f, 12.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, 0.0f, -0.45f, 0.0f, 0.12f));
		PartDefinition p13 = p12.addOrReplaceChild("feather_l3", CubeListBuilder.create()
				.texOffs(0, 47).addBox(-0.5f, -9.0f, -0.5f, 1.0f, 9.0f, 1.0f),
				PartPose.offsetAndRotation(0.0f, -12.0f, 0.0f, -0.5f, 0.0f, 0.1f));
		PartDefinition p14 = root.addOrReplaceChild("arm_r", CubeListBuilder.create()
				.texOffs(76, 0).addBox(-1.5f, -1.0f, -1.5f, 3.0f, 12.0f, 3.0f)
				.texOffs(0, 57).addBox(-2.5f, -2.0f, -2.5f, 5.0f, 3.0f, 5.0f)
				.texOffs(56, 57).addBox(-2.0f, 7.0f, -2.0f, 4.0f, 3.0f, 4.0f)
				.texOffs(72, 57).addBox(-2.0f, 10.5f, -2.0f, 4.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(-6.5f, 7.5f, -3.0f, 0.0f, 0.0f, 0.08f));
		PartDefinition p15 = root.addOrReplaceChild("arm_l", CubeListBuilder.create()
				.texOffs(88, 0).addBox(-1.5f, -1.0f, -1.5f, 3.0f, 12.0f, 3.0f)
				.texOffs(20, 57).addBox(-2.5f, -2.0f, -2.5f, 5.0f, 3.0f, 5.0f)
				.texOffs(88, 57).addBox(-2.0f, 7.0f, -2.0f, 4.0f, 3.0f, 4.0f)
				.texOffs(104, 57).addBox(-2.0f, 10.5f, -2.0f, 4.0f, 3.0f, 4.0f),
				PartPose.offsetAndRotation(6.5f, 7.5f, -3.0f, 0.0f, 0.0f, -0.08f));
		PartDefinition p16 = p14.addOrReplaceChild("staff", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1.0f, -30.0f, -1.0f, 2.0f, 33.0f, 2.0f)
				.texOffs(0, 65).addBox(-1.5f, -31.0f, -1.5f, 3.0f, 4.0f, 3.0f)
				.texOffs(12, 65).addBox(-1.5f, -0.5f, -1.5f, 3.0f, 4.0f, 3.0f),
				PartPose.offsetAndRotation(0.0f, 12.0f, -0.5f, 0.22f, 0.0f, 0.0f));
		PartDefinition p17 = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
				.texOffs(34, 35).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 8.0f, 4.0f)
				.texOffs(90, 65).addBox(-1.5f, 6.0f, -3.5f, 3.0f, 2.0f, 2.0f)
				.texOffs(72, 72).addBox(-1.0f, 5.0f, -4.5f, 2.0f, 1.0f, 1.0f)
				.texOffs(62, 65).addBox(-3.0f, 6.0f, -1.5f, 1.0f, 2.0f, 3.0f)
				.texOffs(0, 72).addBox(-1.5f, 6.0f, 2.0f, 3.0f, 2.0f, 1.0f),
				PartPose.offset(-2.5f, 16.0f, 0.5f));
		PartDefinition p18 = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
				.texOffs(50, 35).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 8.0f, 4.0f)
				.texOffs(100, 65).addBox(-1.5f, 6.0f, -3.5f, 3.0f, 2.0f, 2.0f)
				.texOffs(78, 72).addBox(-1.0f, 5.0f, -4.5f, 2.0f, 1.0f, 1.0f)
				.texOffs(70, 65).addBox(2.0f, 6.0f, -1.5f, 1.0f, 2.0f, 3.0f)
				.texOffs(8, 72).addBox(-1.5f, 6.0f, 2.0f, 3.0f, 2.0f, 1.0f),
				PartPose.offset(2.5f, 16.0f, 0.5f));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
