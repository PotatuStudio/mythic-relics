package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad celestial_dog. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class CelestialDogGeometry {
	private CelestialDogGeometry() {
	}

	public static LayerDefinition create() {
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
}
