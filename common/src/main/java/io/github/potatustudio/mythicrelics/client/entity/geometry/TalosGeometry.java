package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad talos. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class TalosGeometry {
	private TalosGeometry() {
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition p0 = root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(48, 47).addBox(-5.0f, -4.0f, -2.5f, 10.0f, 1.0f, 5.0f)
				.texOffs(32, 0).addBox(-6.5f, -3.0f, -3.5f, 13.0f, 7.0f, 7.0f)
				.texOffs(94, 47).addBox(-5.5f, -2.0f, -4.0f, 5.0f, 4.0f, 1.0f)
				.texOffs(106, 47).addBox(0.5f, -2.0f, -4.0f, 5.0f, 4.0f, 1.0f)
				.texOffs(76, 16).addBox(-5.5f, 4.0f, -3.0f, 11.0f, 4.0f, 6.0f)
				.texOffs(110, 16).addBox(-1.0f, -2.0f, 3.5f, 2.0f, 9.0f, 1.0f)
				.texOffs(0, 28).addBox(-6.5f, 7.0f, -3.5f, 13.0f, 3.0f, 7.0f)
				.texOffs(118, 47).addBox(-6.5f, 9.0f, -4.0f, 2.0f, 4.0f, 1.0f)
				.texOffs(0, 54).addBox(-6.5f, 9.0f, 3.0f, 2.0f, 4.0f, 1.0f)
				.texOffs(46, 54).addBox(-4.5f, 9.0f, -4.0f, 2.0f, 3.0f, 1.0f)
				.texOffs(52, 54).addBox(-4.5f, 9.0f, 3.0f, 2.0f, 3.0f, 1.0f)
				.texOffs(6, 54).addBox(-2.5f, 9.0f, -4.0f, 2.0f, 4.0f, 1.0f)
				.texOffs(12, 54).addBox(-2.5f, 9.0f, 3.0f, 2.0f, 4.0f, 1.0f)
				.texOffs(58, 54).addBox(-0.5f, 9.0f, -4.0f, 2.0f, 3.0f, 1.0f)
				.texOffs(64, 54).addBox(-0.5f, 9.0f, 3.0f, 2.0f, 3.0f, 1.0f)
				.texOffs(18, 54).addBox(1.5f, 9.0f, -4.0f, 2.0f, 4.0f, 1.0f)
				.texOffs(24, 54).addBox(1.5f, 9.0f, 3.0f, 2.0f, 4.0f, 1.0f)
				.texOffs(70, 54).addBox(3.5f, 9.0f, -4.0f, 2.0f, 3.0f, 1.0f)
				.texOffs(76, 54).addBox(3.5f, 9.0f, 3.0f, 2.0f, 3.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p1 = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
				.texOffs(72, 0).addBox(-4.5f, -9.0f, -4.5f, 9.0f, 3.0f, 9.0f)
				.texOffs(84, 28).addBox(-4.5f, -6.0f, 2.0f, 9.0f, 6.0f, 3.0f)
				.texOffs(42, 59).addBox(-3.5f, -6.0f, -4.5f, 7.0f, 1.0f, 1.0f)
				.texOffs(10, 59).addBox(-0.5f, -5.0f, -5.0f, 1.0f, 2.0f, 1.0f)
				.texOffs(82, 54).addBox(-3.5f, -3.0f, -5.0f, 7.0f, 3.0f, 1.0f)
				.texOffs(58, 59).addBox(-2.5f, 0.0f, -5.0f, 5.0f, 1.0f, 1.0f)
				.texOffs(70, 59).addBox(-4.0f, -7.0f, -5.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(74, 59).addBox(-3.0f, -6.6f, -5.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(78, 59).addBox(-2.0f, -7.0f, -5.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(82, 59).addBox(-1.0f, -6.6f, -5.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(86, 59).addBox(0.0f, -7.0f, -5.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(90, 59).addBox(1.0f, -6.6f, -5.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(94, 59).addBox(2.0f, -7.0f, -5.0f, 1.0f, 1.0f, 1.0f)
				.texOffs(98, 59).addBox(3.0f, -6.6f, -5.0f, 1.0f, 1.0f, 1.0f),
				PartPose.offset(0.0f, -3.0f, 0.0f));
		PartDefinition p2 = root.addOrReplaceChild("arm_r", CubeListBuilder.create()
				.texOffs(40, 28).addBox(-4.5f, -2.0f, -3.0f, 5.0f, 4.0f, 6.0f)
				.texOffs(20, 16).addBox(-4.0f, 1.0f, -2.0f, 4.0f, 7.0f, 4.0f)
				.texOffs(14, 59).addBox(-3.0f, 7.0f, 2.0f, 2.0f, 2.0f, 1.0f)
				.texOffs(108, 28).addBox(-4.0f, 8.0f, -2.0f, 4.0f, 5.0f, 4.0f)
				.texOffs(100, 38).addBox(-4.5f, 11.0f, -2.5f, 5.0f, 2.0f, 5.0f)
				.texOffs(0, 47).addBox(-3.5f, 13.0f, -2.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offset(-6.5f, -2.0f, 0.0f));
		PartDefinition p3 = p2.addOrReplaceChild("rock", CubeListBuilder.create()
				.texOffs(36, 16).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 5.0f, 6.0f)
				.texOffs(30, 54).addBox(-2.0f, 4.5f, -2.0f, 4.0f, 1.0f, 4.0f)
				.texOffs(78, 47).addBox(-3.5f, 1.0f, -2.0f, 1.0f, 3.0f, 3.0f)
				.texOffs(86, 47).addBox(2.5f, 0.5f, -1.0f, 1.0f, 3.0f, 3.0f)
				.texOffs(98, 54).addBox(-1.5f, 1.0f, -3.5f, 3.0f, 3.0f, 1.0f),
				PartPose.offset(-2.0f, 15.5f, -0.5f));
		PartDefinition p4 = root.addOrReplaceChild("arm_l", CubeListBuilder.create()
				.texOffs(62, 28).addBox(-0.5f, -2.0f, -3.0f, 5.0f, 4.0f, 6.0f)
				.texOffs(60, 16).addBox(0.0f, 1.0f, -2.0f, 4.0f, 7.0f, 4.0f)
				.texOffs(20, 59).addBox(1.0f, 7.0f, 2.0f, 2.0f, 2.0f, 1.0f)
				.texOffs(0, 38).addBox(0.0f, 8.0f, -2.0f, 4.0f, 5.0f, 4.0f)
				.texOffs(14, 47).addBox(-0.5f, 11.0f, -2.5f, 5.0f, 2.0f, 5.0f)
				.texOffs(34, 47).addBox(0.5f, 13.0f, -2.0f, 3.0f, 3.0f, 4.0f),
				PartPose.offset(6.5f, -2.0f, 0.0f));
		PartDefinition p5 = root.addOrReplaceChild("leg_r", CubeListBuilder.create()
				.texOffs(108, 0).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 7.0f, 5.0f)
				.texOffs(26, 59).addBox(-1.5f, 6.0f, -3.5f, 3.0f, 2.0f, 1.0f)
				.texOffs(16, 38).addBox(-2.5f, 7.0f, -2.0f, 5.0f, 5.0f, 4.0f)
				.texOffs(106, 54).addBox(-2.0f, 7.5f, 2.0f, 4.0f, 3.0f, 1.0f)
				.texOffs(34, 38).addBox(-2.5f, 12.0f, -4.5f, 5.0f, 2.0f, 7.0f),
				PartPose.offset(-3.0f, 10.0f, 0.0f));
		PartDefinition p6 = p5.addOrReplaceChild("nail", CubeListBuilder.create()
				.texOffs(116, 54).addBox(-1.5f, 8.5f, -3.0f, 3.0f, 3.0f, 1.0f),
				PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition p7 = root.addOrReplaceChild("leg_l", CubeListBuilder.create()
				.texOffs(0, 16).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 7.0f, 5.0f)
				.texOffs(34, 59).addBox(-1.5f, 6.0f, -3.5f, 3.0f, 2.0f, 1.0f)
				.texOffs(58, 38).addBox(-2.5f, 7.0f, -2.0f, 5.0f, 5.0f, 4.0f)
				.texOffs(0, 59).addBox(-2.0f, 7.5f, 2.0f, 4.0f, 3.0f, 1.0f)
				.texOffs(76, 38).addBox(-2.5f, 12.0f, -4.5f, 5.0f, 2.0f, 7.0f),
				PartPose.offset(3.0f, 10.0f, 0.0f));
		return LayerDefinition.create(mesh, 128, 64);
	}
}
