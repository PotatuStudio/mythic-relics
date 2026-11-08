package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Escarabajo pelotero: caparazón ovalado con cúpula, pronoto, cabeza en forma de pala y seis patas. Camina en trípode
 * como los insectos reales: la delantera y la trasera de un lado se mueven con la del medio del otro.
 */
public class ScarabModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("scarab"), "main");

	private static final float[] LEG_Z = {-3.5f, -1.0f, 1.5f};
	/** Apertura de cada par de patas: las delanteras hacia adelante, las traseras hacia atrás. */
	private static final float[] LEG_SPREAD = {0.5f, 0.0f, -0.5f};
	/** Inclinación de las patas hacia el suelo. */
	private static final float LEG_DROP = 0.5f;
	private static final float STRIDE = 1.8f;

	private final ModelPart[] leftLegs = new ModelPart[3];
	private final ModelPart[] rightLegs = new ModelPart[3];

	public ScarabModel(ModelPart root) {
		super(root);
		for (int i = 0; i < 3; i++) {
			leftLegs[i] = root.getChild("left_leg" + i);
			rightLegs[i] = root.getChild("right_leg" + i);
		}
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		// Élitros: el cuerpo ovalado, con una cúpula encima para redondearlo.
		root.addOrReplaceChild("shell", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.0f, -3.0f, -3.5f, 6, 3, 7)
				.texOffs(0, 10).addBox(-2.0f, -4.0f, -2.5f, 4, 1, 5),
				PartPose.offset(0.0f, 23.0f, 1.5f));
		root.addOrReplaceChild("pronotum", CubeListBuilder.create()
				.texOffs(26, 0).addBox(-2.5f, -2.0f, -1.5f, 5, 2, 3),
				PartPose.offset(0.0f, 23.0f, -3.5f));
		root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(26, 5).addBox(-2.0f, -1.0f, -2.0f, 4, 1, 2),
				PartPose.offset(0.0f, 23.0f, -5.0f));
		for (int i = 0; i < 3; i++) {
			root.addOrReplaceChild("left_leg" + i, CubeListBuilder.create()
					.texOffs(42, 0).addBox(0.0f, -0.5f, -0.5f, 4, 1, 1),
					PartPose.offsetAndRotation(2.5f, 22.2f, LEG_Z[i], 0.0f, LEG_SPREAD[i], LEG_DROP));
			root.addOrReplaceChild("right_leg" + i, CubeListBuilder.create()
					.texOffs(42, 0).mirror().addBox(-4.0f, -0.5f, -0.5f, 4, 1, 1),
					PartPose.offsetAndRotation(-2.5f, 22.2f, LEG_Z[i], 0.0f, -LEG_SPREAD[i], -LEG_DROP));
		}
		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float speed = Math.min(state.walkAnimationSpeed, 1.0f);
		for (int i = 0; i < 3; i++) {
			// Trípode: izquierdas 0 y 2 con la derecha 1 en un tiempo; las otras tres en el tiempo opuesto.
			float left = state.walkAnimationPos * STRIDE + (i % 2 == 0 ? 0.0f : Mth.PI);
			float right = left + Mth.PI;
			leftLegs[i].yRot += Mth.cos(left) * 0.6f * speed;
			leftLegs[i].zRot -= Math.max(0.0f, Mth.sin(left)) * 0.4f * speed;
			rightLegs[i].yRot -= Mth.cos(right) * 0.6f * speed;
			rightLegs[i].zRot += Math.max(0.0f, Mth.sin(right)) * 0.4f * speed;
		}
	}
}
