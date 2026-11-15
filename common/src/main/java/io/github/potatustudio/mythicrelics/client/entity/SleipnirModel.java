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
	// Cañas (rodilla adelante, corvejón atrás), una por pata.
	private final ModelPart leftFrontLower;
	private final ModelPart rightFrontLower;
	private final ModelPart leftFrontMiddleLower;
	private final ModelPart rightFrontMiddleLower;
	private final ModelPart leftHindMiddleLower;
	private final ModelPart rightHindMiddleLower;
	private final ModelPart leftHindLower;
	private final ModelPart rightHindLower;
	private final ModelPart tailRoot;
	private final ModelPart tailTip;
	private final ModelPart mane;

	public SleipnirModel(ModelPart root) {
		super(root);
		leftFrontMiddleLeg = root.getChild("left_front_middle_leg");
		rightFrontMiddleLeg = root.getChild("right_front_middle_leg");
		leftHindMiddleLeg = root.getChild("left_hind_middle_leg");
		rightHindMiddleLeg = root.getChild("right_hind_middle_leg");
		leftFrontLower = root.getChild("left_front_leg").getChild("left_front_leg_lower");
		rightFrontLower = root.getChild("right_front_leg").getChild("right_front_leg_lower");
		leftFrontMiddleLower = leftFrontMiddleLeg.getChild("left_front_middle_leg_lower");
		rightFrontMiddleLower = rightFrontMiddleLeg.getChild("right_front_middle_leg_lower");
		leftHindMiddleLower = leftHindMiddleLeg.getChild("left_hind_middle_leg_lower");
		rightHindMiddleLower = rightHindMiddleLeg.getChild("right_hind_middle_leg_lower");
		leftHindLower = root.getChild("left_hind_leg").getChild("left_hind_leg_lower");
		rightHindLower = root.getChild("right_hind_leg").getChild("right_hind_leg_lower");
		tailRoot = root.getChild("body").getChild("tail");
		tailTip = tailRoot.getChild("tail_tip");
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

	/** Curva de suavizado (0 → 1 con velocidad nula en los dos extremos). */
	private static float smooth(float v) {
		v = Mth.clamp(v, 0.0f, 1.0f);
		return v * v * (3.0f - 2.0f * v);
	}

	/**
	 * Cuánto se levanta un pie: la pata oscila como {@code cos(fase)} y avanza (xRot baja) mientras {@code sin(fase)} es
	 * positivo. El cuadrado de la media onda da una curva que sube y baja sin quiebres.
	 */
	private static float lift(float phase) {
		float s = 0.5f + 0.5f * Mth.sin(phase);
		return s * s;
	}

	@Override
	public void setupAnim(EquineRenderState state) {
		// La clase padre ya mueve cuerpo, cabeza, las cuatro patas de los extremos y la cola (paso, encabritarse, comer).
		// Todo lo de abajo se suma encima: cada parte parte de su pose de reposo (resetPose) y nada se fija a un ángulo.
		super.setupAnim(state);
		float stand = state.standAnimation;
		float speed = state.walkAnimationSpeed * (1.0f - stand);
		float wp = state.walkAnimationPos * (state.isInWater ? 0.2f : 1.0f) * 0.6662f;
		float age = state.ageInTicks;

		// Patas del medio: siguen el mismo paso con un desfase que crece al galopar, como un ciempiés de cascos (una onda
		// que corre de adelante hacia atrás: delantera, delantera del medio... y trasera del medio antes que la trasera).
		float gallop = smooth((state.walkAnimationSpeed - 0.3f) / 0.5f);
		float lag = Mth.HALF_PI + 0.5f * gallop;
		float pLF = wp + Mth.PI;            // fases de las patas de los extremos (las mismas que usa la clase padre)
		float pRF = wp;
		float pLH = wp;
		float pRH = wp + Mth.PI;
		float pLFM = pLF - lag;
		float pRFM = pRF - lag;
		float pLHM = pLH + lag;
		float pRHM = pRH + lag;
		// Al encabritarse, las del medio copian a la pata de su extremo (se pliegan con ella).
		leftFrontMiddleLeg.xRot += leftFrontLeg.xRot * stand + Mth.cos(pLFM) * 0.8f * speed;
		rightFrontMiddleLeg.xRot += rightFrontLeg.xRot * stand + Mth.cos(pRFM) * 0.8f * speed;
		leftHindMiddleLeg.xRot += leftHindLeg.xRot * stand + Mth.cos(pLHM) * 0.6f * speed;
		rightHindMiddleLeg.xRot += rightHindLeg.xRot * stand + Mth.cos(pRHM) * 0.6f * speed;
		leftFrontMiddleLeg.y = leftFrontLeg.y;
		rightFrontMiddleLeg.y = rightFrontLeg.y;

		// Cañas: al levantar el pie la rodilla delantera dobla el casco hacia atrás y el corvejón trasero lo lleva hacia
		// adelante; con el pie en el suelo la pata queda recta. Al encabritarse, las manos se recogen bajo el pecho.
		float knee = 0.9f * speed;
		float hock = -0.7f * speed;
		leftFrontLower.xRot += knee * lift(pLF) + 1.1f * stand;
		rightFrontLower.xRot += knee * lift(pRF) + 1.1f * stand;
		leftFrontMiddleLower.xRot += knee * lift(pLFM) + 0.9f * stand;
		rightFrontMiddleLower.xRot += knee * lift(pRFM) + 0.9f * stand;
		leftHindMiddleLower.xRot += hock * lift(pLHM);
		rightHindMiddleLower.xRot += hock * lift(pRHM);
		leftHindLower.xRot += hock * lift(pLH);
		rightHindLower.xRot += hock * lift(pRH);

		// Cola trenzada: la raíz se mece despacio a los lados y la punta la sigue atrasada (zRot = de lado, sin girar sobre
		// su largo); al galopar la punta se estira hacia atrás; cuando el caballo espanta moscas (animateTail), la punta
		// repite el latigazo de la raíz un poco después.
		tailRoot.zRot += Mth.sin(age * 0.12f) * 0.08f;
		tailTip.zRot += Mth.sin(age * 0.12f - 0.8f) * 0.16f + (state.animateTail ? Mth.cos(age * 0.7f - 0.9f) * 0.35f : 0.0f);
		tailTip.xRot += speed * 0.5f + Mth.sin(age * 0.12f - 0.6f) * 0.04f;

		// Crin: se mece de lado al respirar y flamea más al galopar.
		mane.zRot += Mth.sin(age * 0.1f) * (0.03f + 0.03f * speed) + Mth.sin(wp * 2.0f - 0.5f) * 0.02f * speed;
	}
}
