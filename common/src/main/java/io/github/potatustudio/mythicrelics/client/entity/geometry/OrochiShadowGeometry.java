package io.github.potatustudio.mythicrelics.client.entity.geometry;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometría de la entidad orochi_shadow. GENERADO por tools/model-gen/china_entities.py --mod a partir del mismo diseño que el visor: no
 * editar a mano.
 */
@SuppressWarnings("unused")
public final class OrochiShadowGeometry {
	private OrochiShadowGeometry() {
	}

	public static LayerDefinition create() {
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
}
