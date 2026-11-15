package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.client.entity.geometry.JackalGeometry;
import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Chacal de Anubis: negro, alto y fino, cuello largo, orejas enormes, cola tupida de tres tramos y usekh al cuello.
 *
 * <p>Animación (todo se suma a la pose de reposo de la geometría generada: {@code super.setupAnim} deja cada parte en
 * su {@code getInitialPose()} y aquí solo se agregan desfases):
 * <ul>
 * <li>Trote con las patas en diagonal: adelante, hombro y codo; atrás, muslo, pierna y corvejón que se pliegan al
 * levantar la pata, cada tramo un poco atrasado.</li>
 * <li>Orejas que giran cada una por su lado, como buscando un ruido, y se echan atrás al atacar.</li>
 * <li>Cola que se mece de lado a lado al andar (en ola, de la base a la punta) y se levanta al atacar.</li>
 * <li>Cabeza que sigue la mirada, cabecea al trotar y se lanza al morder; quijada que se abre al morder y jadea al
 * correr.</li>
 * </ul>
 */
public class JackalModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("jackal"), "main");

	/** Frecuencia del paso, como el lobo vanilla. */
	private static final float STEP = 0.6662f;

	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart leftEar;
	private final ModelPart rightEar;
	/** Cola: tail > tail_mid > tail_tip. */
	private final ModelPart[] tail = new ModelPart[3];
	private final ModelPart leftFrontLeg;
	private final ModelPart leftFrontLower;
	private final ModelPart rightFrontLeg;
	private final ModelPart rightFrontLower;
	private final ModelPart leftHindLeg;
	private final ModelPart leftHindLower;
	private final ModelPart leftHindFoot;
	private final ModelPart rightHindLeg;
	private final ModelPart rightHindLower;
	private final ModelPart rightHindFoot;

	public JackalModel(ModelPart root) {
		super(root);
		head = root.getChild("head");
		jaw = head.getChild("jaw");
		leftEar = head.getChild("left_ear");
		rightEar = head.getChild("right_ear");
		tail[0] = root.getChild("tail");
		tail[1] = tail[0].getChild("tail_mid");
		tail[2] = tail[1].getChild("tail_tip");
		leftFrontLeg = root.getChild("left_front_leg");
		leftFrontLower = leftFrontLeg.getChild("left_front_leg_lower");
		rightFrontLeg = root.getChild("right_front_leg");
		rightFrontLower = rightFrontLeg.getChild("right_front_leg_lower");
		leftHindLeg = root.getChild("left_hind_leg");
		leftHindLower = leftHindLeg.getChild("left_hind_leg_lower");
		leftHindFoot = leftHindLower.getChild("left_hind_leg_foot");
		rightHindLeg = root.getChild("right_hind_leg");
		rightHindLower = rightHindLeg.getChild("right_hind_leg_lower");
		rightHindFoot = rightHindLower.getChild("right_hind_leg_foot");
	}

	/** Geometría generada por tools/model-gen/china_entities.py --mod desde proposals/jackal.py (aprobada el 2026-11-15) */
	public static LayerDefinition createBodyLayer() {
		return JackalGeometry.create();
	}

	/** Curva de levantar la pata: 0 mientras apoya y una loma suave (seno al cuadrado, sin quiebres) mientras avanza. */
	private static float lift(float phase) {
		float s = Mth.sin(phase);
		return s > 0.0f ? s * s : 0.0f;
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		// Devuelve todas las partes a su pose de reposo; desde aquí solo se suman desfases.
		super.setupAnim(state);
		float pos = state.walkAnimationPos;
		float speed = Math.min(state.walkAnimationSpeed, 1.0f);
		float age = state.ageInTicks;
		float fury = 0.0f;
		float swing = 0.0f;
		if (state instanceof JackalRenderer.State s) {
			fury = s.fury;
			swing = s.bite;
		}
		// Mordida: loma suave de 0 a 1 y de vuelta durante el golpe.
		float biteSin = Mth.sin(swing * Mth.PI);
		float bite = biteSin * biteSin;
		float w = pos * STEP;

		// --- Patas: trote en diagonal (delantera izquierda con trasera derecha). ---
		// Adelante: el hombro oscila y el codo se dobla hacia atrás al levantar la zarpa.
		leftFrontLeg.xRot += Mth.cos(w) * 0.9f * speed;
		leftFrontLower.xRot += lift(w - 0.4f) * 0.8f * speed;
		rightFrontLeg.xRot += Mth.cos(w + Mth.PI) * 0.9f * speed;
		rightFrontLower.xRot += lift(w + Mth.PI - 0.4f) * 0.8f * speed;
		// Atrás: el muslo oscila, la pierna se recoge y el corvejón pliega el metatarso, cada uno más atrasado.
		hind(leftHindLeg, leftHindLower, leftHindFoot, w + Mth.PI, speed);
		hind(rightHindLeg, rightHindLower, rightHindFoot, w, speed);

		// --- Cabeza: sigue la mirada, cabecea dos veces por paso y se lanza hacia abajo y adelante al morder. ---
		head.xRot += state.xRot * Mth.DEG_TO_RAD + Mth.cos(w * 2.0f) * 0.05f * speed + bite * 0.35f;
		head.yRot += state.yRot * Mth.DEG_TO_RAD;

		// --- Quijada: jadeo suave al correr, entreabierta con la furia y bien abierta al morder. ---
		float pant = (0.5f + 0.5f * Mth.sin(age * 0.55f)) * 0.12f * speed;
		jaw.xRot += pant + fury * 0.1f + bite * 0.7f;

		// --- Orejas: cada una gira por su lado de tanto en tanto, como hacia un ruido (producto de dos senos lentos:
		// casi quietas un rato y luego un giro suave); con la furia o al morder se echan hacia atrás. ---
		float listenL = Mth.sin(age * 0.045f) * Mth.sin(age * 0.031f + 1.3f);
		float listenR = Mth.sin(age * 0.041f + 2.2f) * Mth.sin(age * 0.027f + 0.4f);
		float back = Math.min(0.6f, fury * 0.45f + bite * 0.25f);
		float calm = 1.0f - Math.min(1.0f, fury);
		leftEar.yRot += listenL * 0.45f * calm;
		leftEar.xRot -= back;
		leftEar.zRot += back * 0.3f;
		rightEar.yRot -= listenR * 0.45f * calm;
		rightEar.xRot -= back;
		rightEar.zRot -= back * 0.3f;

		// --- Cola: se mece de lado a lado al andar (ola que va de la base a la punta) y apenas en reposo;
		// al atacar se levanta hacia atrás. ---
		float wag = speed * 0.22f + 0.05f;
		float wagTime = w + age * 0.06f;
		float raise = fury * 0.6f + bite * 0.2f;
		for (int i = 0; i < 3; i++) {
			float a = Mth.sin(wagTime - i * 0.8f) * wag * (1.0f + i * 0.35f);
			if (i == 0) {
				// La base cuelga hacia abajo y atrás: el vaivén de lado mezcla giro en Z y en Y para que se vea lateral
				// tanto caída como levantada.
				tail[i].zRot += a;
				tail[i].yRot -= a;
				tail[i].xRot += raise;
			} else {
				tail[i].zRot += a;
				tail[i].xRot += raise * 0.15f;
			}
		}
	}

	/** Pata trasera de tres tramos: muslo que oscila, pierna que se recoge y metatarso que se pliega en el corvejón. */
	private static void hind(ModelPart thigh, ModelPart lower, ModelPart foot, float phase, float speed) {
		thigh.xRot += Mth.cos(phase) * 0.8f * speed;
		lower.xRot += lift(phase - 0.3f) * 0.5f * speed;
		foot.xRot += lift(phase - 0.6f) * 0.45f * speed;
	}
}
