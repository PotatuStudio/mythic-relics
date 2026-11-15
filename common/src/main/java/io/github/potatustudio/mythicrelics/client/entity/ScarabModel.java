package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.client.entity.geometry.ScarabGeometry;
import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Escarabajo sagrado (Jepri): élitros de joya en cúpula, pronoto de oro, cabeza en pala, antenas en abanico, seis patas
 * de dos tramos y el disco del sol sobre la cabeza.
 *
 * <p>Animación (todo se suma a la pose de reposo de la geometría generada: {@code super.setupAnim} deja cada parte en
 * su {@code getInitialPose()} y aquí solo se agregan desfases):
 * <ul>
 * <li>Marcha en trípode, como los insectos reales: delantera y trasera de un lado con la del medio del otro. El fémur
 * avanza y se alza; la tibia se dobla al levantar la pata, un poco después.</li>
 * <li>Antenas que se mecen y abren el abanico de láminas (más al estar alerta o al andar).</li>
 * <li>Élitros que se levantan al saltar o atacar, girando sobre su borde de afuera y delantero para no meterse en el
 * cuerpo.</li>
 * <li>Disco del sol que late despacio.</li>
 * </ul>
 */
public class ScarabModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("scarab"), "main");

	/** Frecuencia del paso: los insectos dan pasos cortos y rápidos. */
	private static final float STRIDE = 1.3f;
	/** Bisagra de cada élitro (en el espacio de "shell"): borde de afuera, abajo y adelante del élitro izquierdo. */
	private static final float HINGE_X = 6.0f;
	private static final float HINGE_Y = -0.5f;
	private static final float HINGE_Z = -6.0f;

	private final ModelPart head;
	private final ModelPart sun;
	private final ModelPart suture;
	private final ModelPart leftElytron;
	private final ModelPart rightElytron;
	private final ModelPart[] antennas = new ModelPart[2];
	/** Láminas de la maza de cada antena: [antena][0 = de arriba, 1 = del medio, 2 = de abajo]. */
	private final ModelPart[][] lamellae = new ModelPart[2][3];
	private final ModelPart[] leftLegs = new ModelPart[3];
	private final ModelPart[] leftTibiae = new ModelPart[3];
	private final ModelPart[] rightLegs = new ModelPart[3];
	private final ModelPart[] rightTibiae = new ModelPart[3];

	public ScarabModel(ModelPart root) {
		super(root);
		ModelPart shell = root.getChild("shell");
		leftElytron = shell.getChild("left_elytron");
		rightElytron = shell.getChild("right_elytron");
		suture = shell.getChild("suture");
		head = root.getChild("head");
		sun = head.getChild("sun");
		String[] sides = {"left_antenna", "right_antenna"};
		for (int a = 0; a < 2; a++) {
			antennas[a] = head.getChild(sides[a]);
			for (int j = 0; j < 3; j++) {
				lamellae[a][j] = antennas[a].getChild(sides[a] + "_lamella" + j);
			}
		}
		for (int i = 0; i < 3; i++) {
			leftLegs[i] = root.getChild("left_leg" + i);
			leftTibiae[i] = leftLegs[i].getChild("left_leg" + i + "_tibia");
			rightLegs[i] = root.getChild("right_leg" + i);
			rightTibiae[i] = rightLegs[i].getChild("right_leg" + i + "_tibia");
		}
	}

	/** Geometría generada por tools/model-gen/china_entities.py --mod desde proposals/scarab.py (aprobada el 2026-11-15) */
	public static LayerDefinition createBodyLayer() {
		return ScarabGeometry.create();
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
		float speed = Math.min(state.walkAnimationSpeed, 1.0f);
		float age = state.ageInTicks;
		float open = 0.0f;
		float alert = 0.0f;
		if (state instanceof ScarabRenderer.State s) {
			open = s.elytra;
			alert = s.alert;
		}
		float w = state.walkAnimationPos * STRIDE;

		// --- Patas: marcha en trípode. Trípode A = izquierda 0, derecha 1, izquierda 2; trípode B, las otras tres. ---
		for (int i = 0; i < 3; i++) {
			float phaseLeft = w + (i % 2 == 0 ? 0.0f : Mth.PI);
			stepLeg(leftLegs[i], leftTibiae[i], phaseLeft, 1.0f, speed);
			stepLeg(rightLegs[i], rightTibiae[i], phaseLeft + Mth.PI, -1.0f, speed);
		}

		// --- Cabeza: sigue un poco la mirada (es una pala pegada al pronoto, apenas gira). ---
		head.yRot += Mth.clamp(state.yRot * Mth.DEG_TO_RAD, -0.6f, 0.6f) * 0.4f;
		head.xRot += Mth.clamp(state.xRot * Mth.DEG_TO_RAD, -0.6f, 0.6f) * 0.3f;

		// --- Antenas: se mecen despacio y abren el abanico de láminas (respira solo, y se abre más al estar alerta
		// o al andar). Las láminas siguen el vaivén de la antena un poco atrasadas. ---
		float breath = 0.5f - 0.5f * Mth.cos(age * 0.06f);
		float fan = Math.min(1.0f, 0.2f + breath * 0.35f + alert * 0.5f + speed * 0.15f);
		for (int a = 0; a < 2; a++) {
			float side = a == 0 ? 1.0f : -1.0f;
			float t = age * 0.1f + a * 1.9f;
			antennas[a].xRot += Mth.sin(t) * 0.08f - alert * 0.12f;
			antennas[a].yRot += side * Mth.sin(t * 0.8f + 0.5f) * 0.1f;
			lamellae[a][0].xRot -= fan * 0.45f;
			lamellae[a][2].xRot += fan * 0.45f;
			for (int j = 0; j < 3; j++) {
				lamellae[a][j].yRot += side * Mth.sin(t * 0.8f - 0.7f - j * 0.15f) * 0.08f;
			}
		}

		// --- Élitros: se levantan al saltar o atacar. Giran sobre una bisagra en su borde de afuera, abajo y
		// adelante: así el borde del medio sube y se separa del otro élitro y la parte de atrás se alza, sin que nada
		// baje hacia el cuerpo. ---
		if (open > 0.001f) {
			float flutter = Mth.sin(age * 0.9f) * 0.04f * open;
			hinge(leftElytron, 1.0f, open * 0.8f + flutter, open * 0.3f);
			hinge(rightElytron, -1.0f, -(open * 0.8f + flutter), open * 0.3f);
		}
		// La sutura de oro va entre los dos élitros: se achica hasta desaparecer apenas se separan.
		float seam = Mth.clamp(1.0f - open * 3.0f, 0.0f, 1.0f);
		suture.xScale *= seam;
		suture.yScale *= seam;
		suture.zScale *= seam;

		// --- Disco del sol: late despacio (crece y vuelve, desde su base sobre la cabeza). ---
		float beat = 0.5f + 0.5f * Mth.sin(age * 0.12f);
		float pulse = 1.0f + beat * beat * 0.07f;
		sun.xScale *= pulse;
		sun.yScale *= pulse;
	}

	/**
	 * Un paso de una pata: el fémur avanza y retrocede (giro en Y) y se alza mientras avanza en el aire; la tibia se
	 * dobla hacia abajo un poco después, para que la punta no arrastre. {@code side} = 1 izquierda, -1 derecha.
	 */
	private static void stepLeg(ModelPart femur, ModelPart tibia, float phase, float side, float speed) {
		// La pata avanza (yRot creciente en la izquierda) cuando sin(phase) < 0: ahí se levanta.
		femur.yRot += side * Mth.cos(phase) * 0.35f * speed;
		femur.zRot -= side * lift(0.3f - phase) * 0.3f * speed;
		tibia.zRot += side * lift(0.6f - phase) * 0.35f * speed;
	}

	/**
	 * Gira un élitro sobre su bisagra (borde de afuera, abajo y adelante) en vez de sobre el centro del caparazón: se
	 * mueve el punto de giro de la parte para que la bisagra quede quieta. {@code side} = 1 izquierdo, -1 derecho.
	 */
	private static void hinge(ModelPart elytron, float side, float zRot, float xRot) {
		float hx = side * HINGE_X;
		float hy = HINGE_Y;
		float hz = HINGE_Z;
		// R·H con R = Rz·Rx (el orden en que ModelPart aplica los giros cuando yRot = 0).
		float cx = Mth.cos(xRot);
		float sx = Mth.sin(xRot);
		float y1 = hy * cx - hz * sx;
		float z1 = hy * sx + hz * cx;
		float cz = Mth.cos(zRot);
		float sz = Mth.sin(zRot);
		float x2 = hx * cz - y1 * sz;
		float y2 = hx * sz + y1 * cz;
		// Nueva posición = H − R·H (la pose de reposo del élitro está en el origen de "shell"); se suma a la de reposo.
		elytron.x += hx - x2;
		elytron.y += hy - y2;
		elytron.z += hz - z1;
		elytron.zRot += zRot;
		elytron.xRot += xRot;
	}
}
