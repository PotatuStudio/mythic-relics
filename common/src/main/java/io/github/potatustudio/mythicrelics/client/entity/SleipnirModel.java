package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.client.entity.geometry.SleipnirGeometry;
import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.animal.equine.AbstractEquineModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.util.Mth;

/**
 * Sleipnir, corcel de guerra de Asgard: la geometría del caballo vanilla (para que montarlo y sus animaciones funcionen
 * igual) con ocho patas, bardas de hierro rúnico (testera, peto, faldones y silla con perilla), crin alta y trenzada y
 * una cola larga en dos tramos que se mece. Textura propia de 128x64; las runas y los ojos brillan con otra capa
 * ({@link SleipnirRenderer}).
 */
public class SleipnirModel extends AbstractEquineModel<EquineRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("sleipnir"), "main");

	private final ModelPart leftFrontMiddleLeg;
	private final ModelPart rightFrontMiddleLeg;
	private final ModelPart leftHindMiddleLeg;
	private final ModelPart rightHindMiddleLeg;
	private final ModelPart tailTip;
	private final ModelPart mane;

	public SleipnirModel(ModelPart root) {
		super(root);
		leftFrontMiddleLeg = root.getChild("left_front_middle_leg");
		rightFrontMiddleLeg = root.getChild("right_front_middle_leg");
		leftHindMiddleLeg = root.getChild("left_hind_middle_leg");
		rightHindMiddleLeg = root.getChild("right_hind_middle_leg");
		tailTip = root.getChild("body").getChild("tail").getChild("tail_tip");
		mane = root.getChild("head_parts").getChild("mane");
	}

	/** Geometría generada por tools/model-gen/china_entities.py --mod desde proposals/sleipnir.py (aprobada el 2026-11-15) */
	public static LayerDefinition createBodyLayer() {
		return SleipnirGeometry.create();
	}

	private static void leg(PartDefinition root, String name, boolean left, float x, float z, float boxZ) {
		CubeListBuilder cube = CubeListBuilder.create().texOffs(0, 32);
		if (left) {
			cube.mirror().addBox(-3.0f, -1.01f, boxZ, 4, 11, 4);
		} else {
			cube.addBox(-1.0f, -1.01f, boxZ, 4, 11, 4);
		}
		root.addOrReplaceChild(name, cube, PartPose.offset(x, 14.0f, z));
	}

	@Override
	public void setupAnim(EquineRenderState state) {
		super.setupAnim(state);
		// Las patas del medio siguen a las de sus extremos a medio paso, y se pliegan con ellas al encabritarse.
		float stand = state.standAnimation;
		float speed = state.walkAnimationSpeed * (1.0f - stand);
		float pos = state.walkAnimationPos * (state.isInWater ? 0.2f : 1.0f);
		float swing = Mth.cos(pos * 0.6662f + Mth.HALF_PI) * 0.8f * speed;
		leftFrontMiddleLeg.xRot = leftFrontLeg.xRot * stand - swing;
		rightFrontMiddleLeg.xRot = rightFrontLeg.xRot * stand + swing;
		leftHindMiddleLeg.xRot = leftHindLeg.xRot * stand + swing * 0.6f;
		rightHindMiddleLeg.xRot = rightHindLeg.xRot * stand - swing * 0.6f;
		leftFrontMiddleLeg.y = leftFrontLeg.y;
		rightFrontMiddleLeg.y = rightFrontLeg.y;
		// La punta de la cola y la crin se mecen; al galopar, la cola flamea hacia atrás.
		tailTip.yRot = Mth.sin(state.ageInTicks * 0.12f) * 0.25f;
		tailTip.xRot = -0.25f + speed * 0.5f;
		mane.zRot = Mth.sin(state.ageInTicks * 0.1f) * 0.03f;
	}
}
