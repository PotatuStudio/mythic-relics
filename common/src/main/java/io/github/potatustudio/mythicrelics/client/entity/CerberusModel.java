package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.client.entity.geometry.CerberusGeometry;
import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Cerbero: tres cabezas de perro y cola de serpiente, como en Apolodoro (sin las serpientes del lomo, a pedido del
 * usuario). Bestia de lomo encorvado (pecho alto y ancho, cadera más baja), patas de dos tramos con garras y fauces de
 * brasa. Las cabezas de los lados miran hacia afuera.
 *
 * <p>Animación (todo se suma a la pose de reposo de la geometría generada: {@code super.setupAnim} deja cada parte en
 * su {@code getInitialPose()} y aquí solo se agregan desfases):
 * <ul>
 * <li>Trote en diagonal: hombro y anca oscilan; el codo y el corvejón se doblan al levantar la pata, un poco después.</li>
 * <li>Las tres quijadas gruñen a destiempo; muerden escalonadas al atacar.</li>
 * <li>Cabezas laterales que se balancean despacio, cada una a su ritmo, y cabeceo leve al trotar.</li>
 * <li>Orejas que tiemblan en reposo y se aplastan hacia atrás con la furia o al morder.</li>
 * <li>Cola de serpiente en cuatro tramos que ondea de lado a lado, cada tramo atrasado y más amplio que el anterior.</li>
 * <li>Cresta y mechones de la gorguera que se erizan con la furia.</li>
 * </ul>
 */
public class CerberusModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("cerberus"), "main");

	/** Frecuencia del paso, como el lobo vanilla. */
	private static final float STEP = 0.6662f;

	private final ModelPart[] heads = new ModelPart[3];
	private final ModelPart[] jaws = new ModelPart[3];
	private final ModelPart[] leftEars = new ModelPart[3];
	private final ModelPart[] rightEars = new ModelPart[3];
	private final ModelPart mane;
	private final ModelPart leftTuft;
	private final ModelPart rightTuft;
	/** Cola: tail > tail_mid > tail_tip > tail_end (cuelga de body). */
	private final ModelPart[] tail = new ModelPart[4];
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

	public CerberusModel(ModelPart root) {
		super(root);
		// Las orejas llevan el nombre de su cabeza (head_ear_l, left_head_ear_l...); las quijadas se llaman todas "jaw".
		String[] names = {"head", "left_head", "right_head"};
		for (int i = 0; i < 3; i++) {
			heads[i] = root.getChild(names[i]);
			jaws[i] = heads[i].getChild("jaw");
			leftEars[i] = heads[i].getChild(names[i] + "_ear_l");
			rightEars[i] = heads[i].getChild(names[i] + "_ear_r");
		}
		ModelPart body = root.getChild("body");
		mane = body.getChild("mane");
		leftTuft = body.getChild("ruff_tuft_l");
		rightTuft = body.getChild("ruff_tuft_r");
		tail[0] = body.getChild("tail");
		tail[1] = tail[0].getChild("tail_mid");
		tail[2] = tail[1].getChild("tail_tip");
		tail[3] = tail[2].getChild("tail_end");
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

	/** Geometría generada por tools/model-gen/china_entities.py --mod desde proposals/cerberus.py (aprobada el 2026-11-15) */
	public static LayerDefinition createBodyLayer() {
		return CerberusGeometry.create();
	}

	/** Curva de levantar la pata: 0 mientras apoya y una loma suave (seno al cuadrado, sin quiebres) mientras avanza. */
	private static float lift(float phase) {
		float s = Mth.sin(phase);
		return s > 0.0f ? s * s : 0.0f;
	}

	/** Loma suave de 0 a 1 y de vuelta a 0 cuando t recorre [from, from + span]; fuera de ese tramo vale 0. */
	private static float bump(float t, float from, float span) {
		float u = Mth.clamp((t - from) / span, 0.0f, 1.0f);
		float s = Mth.sin(u * Mth.PI);
		return s * s;
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
		if (state instanceof CerberusRenderer.State s) {
			fury = s.fury;
			swing = s.bite;
		}
		float w = pos * STEP;

		// --- Patas: trote en diagonal (delantera izquierda con trasera derecha). ---
		// Hombro y anca oscilan; el tramo de abajo se dobla solo mientras la pata avanza en el aire, un poco atrasado.
		trotFront(leftFrontLeg, leftFrontLower, w, speed);
		trotFront(rightFrontLeg, rightFrontLower, w + Mth.PI, speed);
		trotHind(leftHindLeg, leftHindLower, leftHindFoot, w + Mth.PI, speed);
		trotHind(rightHindLeg, rightHindLower, rightHindFoot, w, speed);

		// --- Cabezas ---
		float pitch = state.xRot * Mth.DEG_TO_RAD;
		float yaw = state.yRot * Mth.DEG_TO_RAD;
		// Cabeceo leve al trotar (dos veces por paso, como el peso que cae en cada apoyo).
		float nod = Mth.cos(w * 2.0f) * 0.04f * speed;
		for (int i = 0; i < 3; i++) {
			// Cada cabeza muerde con un pequeño desfase: primero la del centro, luego las de los lados.
			float lunge = bump(swing, i * 0.12f, 0.7f);
			ModelPart head = heads[i];
			if (i == 0) {
				// La del centro sigue la mirada.
				head.xRot += pitch + nod + lunge * 0.3f;
				head.yRot += yaw;
			} else {
				// Las laterales siguen la mirada a medias y se balancean despacio, cada una a su ritmo, sin cruzarse con la del centro.
				float side = i == 1 ? 1.0f : -1.0f;
				float t = age * (i == 1 ? 0.071f : 0.083f) + i * 1.7f;
				head.xRot += pitch * 0.5f + Mth.sin(t) * 0.08f + nod + lunge * 0.3f;
				head.yRot += yaw * 0.4f + Mth.sin(t * 0.8f + 0.6f) * 0.1f - side * lunge * 0.12f;
				head.zRot += Mth.sin(t * 0.6f) * 0.05f;
			}

			// Quijada: gruñido lento a destiempo (se cierra a medias y se vuelve a abrir), más abierta con la furia,
			// con un temblor fino mientras gruñe y bien abierta al morder.
			float growl = 0.5f - 0.5f * Mth.cos(age * 0.19f + i * 2.1f);
			float tremble = Mth.sin(age * 1.1f + i * 1.3f) * 0.025f * growl;
			jaws[i].xRot += -0.18f + growl * 0.3f + tremble + fury * 0.15f + lunge * 0.55f;

			// Orejas: tiemblan en reposo y se aplastan hacia atrás (y un poco hacia afuera) con la furia o al morder.
			float flat = Math.min(0.4f, fury * 0.3f + lunge * 0.2f);
			float twitchL = Mth.sin(age * 0.13f + i * 2.3f) * 0.06f;
			float twitchR = Mth.sin(age * 0.11f + i * 1.7f + 1.0f) * 0.06f;
			leftEars[i].xRot += -flat + twitchL;
			leftEars[i].zRot += flat * 0.4f;
			rightEars[i].xRot += -flat + twitchR;
			rightEars[i].zRot -= flat * 0.4f;
		}

		// --- Cola de serpiente: ola de lado a lado que avanza hacia la punta. ---
		// Cada tramo va atrasado respecto del anterior y con más amplitud; al andar ondea más rápido y con la furia se alza.
		float tailTime = age * (0.11f + 0.08f * speed);
		float[] amp = {0.16f, 0.22f, 0.28f, 0.34f};
		for (int i = 0; i < 4; i++) {
			float phase = tailTime - i * 0.9f;
			tail[i].yRot += Mth.sin(phase) * amp[i];
			tail[i].xRot += Mth.sin(phase * 0.7f + 0.5f) * 0.05f;
		}
		tail[0].xRot += fury * 0.25f;
		tail[3].xRot -= fury * 0.2f;

		// --- Pelo erizado: la cresta crece hacia arriba y los mechones de la gorguera se abren con la furia. ---
		mane.yScale += fury * 0.3f;
		leftTuft.zRot -= fury * 0.25f;
		rightTuft.zRot += fury * 0.25f;
	}

	/** Pata delantera: hombro que oscila y codo (tramo de abajo) que se dobla hacia atrás al levantar la zarpa. */
	private static void trotFront(ModelPart upper, ModelPart lower, float phase, float speed) {
		upper.xRot += Mth.cos(phase) * 0.8f * speed;
		lower.xRot += lift(phase - 0.4f) * 0.75f * speed;
	}

	/** Pata trasera: anca que oscila, pierna que se recoge al levantar y pie que se pliega en el corvejón, cada uno más atrasado. */
	private static void trotHind(ModelPart thigh, ModelPart lower, ModelPart foot, float phase, float speed) {
		thigh.xRot += Mth.cos(phase) * 0.7f * speed;
		lower.xRot += lift(phase - 0.3f) * 0.45f * speed;
		foot.xRot += lift(phase - 0.6f) * 0.4f * speed;
	}
}
