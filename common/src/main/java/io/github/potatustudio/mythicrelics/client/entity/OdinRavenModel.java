package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.geometry.OdinRavenGeometry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Cuervo de Odín (geometría en {@link OdinRavenGeometry}, igual que la página de revisión). Alas en dos tramos (brazo
 * y mano con cinco primarias sueltas) y cola en cuña de cinco plumas.
 *
 * <p>En vuelo bate las alas con la mano un poco atrasada (latigazo) y abre las primarias al bajar. Posado, el brazo se
 * pliega hacia la cola por el costado, la mano se recoge encima del brazo con las primarias juntas y la cola se cierra.
 * La cabeza se ladea de a saltos, como miran los córvidos. El paso entre vuelo y posado llega suavizado desde el
 * renderer ({@code fold}). Todo se suma a la pose de reposo ({@code super.setupAnim} la restaura en cada cuadro).
 */
public class OdinRavenModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("odin_raven"), "main");

	/**
	 * Ala plegada: el brazo apunta hacia la cola (giro en y) y la placa cae por el flanco, apenas abierta hacia afuera
	 * (giro en z: con el 0,12 de reposo queda a 90° + 0,2). El ala del cuervo es más ancha (9 px) que el cuerpo (5 px):
	 * tendida sobre el lomo asomaría por el otro lado, así que va de canto sobre el costado y cubre el muslo.
	 */
	private static final float FOLD_YAW = Mth.HALF_PI;
	private static final float FOLD_ROLL = 1.65f;
	/** Al plegarse el hombro se corre hacia afuera y hacia arriba para que la placa quede por fuera del cuerpo. */
	private static final float FOLD_OUT = 0.8f;
	private static final float FOLD_LIFT = 1.0f;
	/** Cuánto entra la muñeca hacia el hombro al plegarse (de 7 px a 2,5 px del hombro): la mano queda sobre el brazo. */
	private static final float HAND_TUCK = 4.5f;
	/** Cuánto se apoya la mano por fuera del brazo para no hundirse en él (la cara de abajo del brazo queda afuera). */
	private static final float HAND_LIFT = 0.7f;
	/** Duración de cada pose de la cabeza, en ticks, y cuánto tarda el salto de una pose a la siguiente. */
	private static final float HEAD_HOLD = 22.0f;
	private static final float HEAD_SNAP = 3.0f;
	private static final String[] SIDES = {"r", "l"};

	private final ModelPart head;
	private final ModelPart[] wings = new ModelPart[2];
	private final ModelPart[] hands = new ModelPart[2];
	private final ModelPart[][] primaries = new ModelPart[2][5];
	private final float[][] primaryRest = new float[2][5];
	private final ModelPart tail;
	private final ModelPart[] tailFeathers = new ModelPart[5];
	private final float[] tailRest = new float[5];

	public OdinRavenModel(ModelPart root) {
		// Con descarte de caras traseras: las plumas son planos de grosor 0 y sin él sus dos caras parpadean.
		super(root, RenderTypes::entityCutout);
		head = root.getChild("head");
		ModelPart body = root.getChild("body");
		for (int s = 0; s < 2; s++) {
			String side = SIDES[s];
			wings[s] = body.getChild("wing_" + side);
			hands[s] = wings[s].getChild("wing_" + side + "_tip");
			for (int i = 0; i < 5; i++) {
				primaries[s][i] = hands[s].getChild("wing_" + side + "_p" + i);
				primaryRest[s][i] = primaries[s][i].getInitialPose().yRot();
			}
		}
		tail = body.getChild("tail");
		for (int i = 0; i < 5; i++) {
			tailFeathers[i] = tail.getChild("tail_f" + i);
			tailRest[i] = tailFeathers[i].getInitialPose().yRot();
		}
	}

	public static LayerDefinition createBodyLayer() {
		return OdinRavenGeometry.create();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks;
		float fold = state instanceof MythicRenderers.RavenRenderer.State s ? s.fold
				: state.walkAnimationSpeed > 0.5f ? 0.0f : 1.0f;
		// Curva suave para que el plegado arranque y termine despacio.
		float f = fold * fold * (3.0f - 2.0f * fold);
		float fly = 1.0f - f;
		// Los dos tiempos del plegado: alzar (primera mitad) y barrer hacia atrás (desde el 30 %); al despegar es al revés.
		float raise = smooth(fold / 0.5f);
		float sweep = smooth((fold - 0.3f) / 0.7f);

		// Aleteo: el brazo sube y baja; la mano lo sigue con retraso (latigazo). Al bajar (downstroke) las primarias se
		// separan en abanico, y al subir se juntan para dejar pasar el aire.
		float phase = t * 0.45f;
		float beat = Mth.sin(phase) * 0.55f * fly;
		float whip = Mth.sin(phase - 0.7f) * 0.45f * fly;
		float down = 0.5f * (1.0f - Mth.cos(phase - 0.4f)) * fly;

		for (int s = 0; s < 2; s++) {
			float sign = s == 0 ? 1.0f : -1.0f;
			ModelPart wing = wings[s];
			ModelPart hand = hands[s];
			wing.zRot += sign * beat;
			hand.zRot += sign * whip;

			// Plegado (posado), en dos tiempos como un ave que se acomoda: primero alza las alas (giro en z) y después
			// las barre hacia la cola (giro en y). Así el ala pasa por encima del lomo y nunca cruza el cuerpo; al final
			// queda como una placa sobre el flanco, con el borde de ataque arriba, junto al lomo.
			wing.zRot += sign * FOLD_ROLL * raise;
			wing.yRot += sign * FOLD_YAW * sweep;
			wing.x -= sign * FOLD_OUT * sweep;
			wing.y -= FOLD_LIFT * sweep;
			// La mano: la muñeca entra hacia el hombro y la mano, ya recta, se apoya por fuera del brazo, de modo que
			// las primarias quedan sobre el flanco apuntando a la cola, sin pasar la punta de la cola.
			hand.x += sign * HAND_TUCK * sweep;
			hand.y += HAND_LIFT * sweep;
			hand.yRot -= hand.getInitialPose().yRot() * sweep;

			// Primarias: abiertas al bajar en vuelo; juntas una sobre otra al plegar.
			for (int i = 0; i < 5; i++) {
				float rest = primaryRest[s][i];
				primaries[s][i].yRot += (rest * 0.6f + sign * i * 0.04f) * down - rest * 0.85f * f;
			}
		}

		// Cola: en vuelo se abre un poco y cabecea despacio; posado se cierra en cuña estrecha.
		tail.xRot += Mth.sin(t * 0.1f) * 0.1f * (0.4f + 0.6f * fly);
		for (int i = 0; i < 5; i++) {
			tailFeathers[i].yRot += tailRest[i] * (0.25f * fly - 0.75f * f);
		}

		// Cabeza: sigue la mirada y se ladea de a saltos (mantiene una pose y cambia rápido, pero con una curva suave
		// de pocos ticks en lugar de un salto seco). Posado ladea más, como el cuervo que mira de reojo.
		head.xRot += state.xRot * Mth.DEG_TO_RAD * 0.5f;
		float slot = t / HEAD_HOLD;
		int n = Mth.floor(slot);
		float into = (slot - n) * HEAD_HOLD / HEAD_SNAP;
		float e = Mth.clamp(into, 0.0f, 1.0f);
		e = e * e * (3.0f - 2.0f * e);
		float amount = 0.12f + 0.25f * f;
		head.zRot += Mth.lerp(e, cocked(n - 1), cocked(n)) * amount;
		head.yRot += Mth.lerp(e, cocked(n + 99), cocked(n + 100)) * amount * 0.8f;
	}

	/** Curva suave entre 0 y 1 (smoothstep), recortada fuera de ese rango. */
	private static float smooth(float x) {
		float c = Mth.clamp(x, 0.0f, 1.0f);
		return c * c * (3.0f - 2.0f * c);
	}

	/** Pose de cabeza pseudoaleatoria (entre -1 y 1) para el tramo {@code n}; siempre la misma para el mismo tramo. */
	private static float cocked(int n) {
		return Mth.frac(Mth.sin(n * 12.9898f) * 43758.547f) * 2.0f - 1.0f;
	}
}
