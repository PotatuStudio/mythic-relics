package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.geometry.LongDragonGeometry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * El long (geometría en {@link LongDragonGeometry}, igual que el visor de diseño): diez segmentos encadenados
 * que ondulan como una serpiente al volar sobre su pose en S, cabeza de camello con cuernos de ciervo (fijos), bigotes de
 * tres tramos que flotan, melena y barba de llamas que se mecen con retraso, cuatro patas que reman en el aire con las
 * llamas de los codos aleteando, y garras de águila que se abren y bajan al acercarse al suelo.
 *
 * <p>Todo se suma a la pose de reposo: {@code super.setupAnim} la restaura en cada cuadro y aquí solo se agregan
 * ángulos.
 */
public class LongDragonModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("long_dragon"), "main");
	private static final int SEGMENTS = 10;
	private static final String[] SIDES = {"r", "l"};

	private final ModelPart[] segments = new ModelPart[SEGMENTS];
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart tuft;
	private final ModelPart beard;
	private final ModelPart[] beardSides = new ModelPart[2];
	private final ModelPart[] manes = new ModelPart[2];
	private final ModelPart[] manes2 = new ModelPart[2];
	private final ModelPart maneTop;
	private final ModelPart[] whiskers = new ModelPart[2];
	private final ModelPart[] whiskerTips = new ModelPart[2];
	private final ModelPart[] whiskerEnds = new ModelPart[2];
	/** Patas: [0] delanteras (seg1), [1] traseras (seg4); en cada una, [lado]. */
	private final Leg[][] legs = new Leg[2][2];

	/** Una pata con sus tramos: muslo, canilla, llama del codo, planta y las garras. */
	private record Leg(ModelPart leg, ModelPart shin, ModelPart elbow, ModelPart foot, ModelPart outer, ModelPart middle,
			ModelPart inner, ModelPart spur) {
		static Leg of(ModelPart segment, String prefix, String side) {
			ModelPart leg = segment.getChild(prefix + "_leg_" + side);
			ModelPart shin = leg.getChild(prefix + "_shin_" + side);
			ModelPart foot = shin.getChild(prefix + "_foot_" + side);
			return new Leg(leg, shin, shin.getChild(prefix + "_elbow_" + side), foot,
					foot.getChild(prefix + "_claw-4_" + side), foot.getChild(prefix + "_claw-1_" + side),
					foot.getChild(prefix + "_claw2_" + side), foot.getChild(prefix + "_spur_" + side));
		}
	}

	public LongDragonModel(ModelPart root) {
		super(root);
		ModelPart part = root.getChild("seg0");
		segments[0] = part;
		for (int i = 1; i < SEGMENTS; i++) {
			part = part.getChild("seg" + i);
			segments[i] = part;
		}
		tuft = segments[SEGMENTS - 1].getChild("tuft");
		head = segments[0].getChild("head");
		jaw = head.getChild("jaw");
		beard = jaw.getChild("beard");
		maneTop = head.getChild("mane_top");
		for (int s = 0; s < 2; s++) {
			String side = SIDES[s];
			beardSides[s] = jaw.getChild("beard_" + side);
			manes[s] = head.getChild("mane_" + side);
			manes2[s] = head.getChild("mane2_" + side);
			whiskers[s] = head.getChild("whisker_" + side);
			whiskerTips[s] = whiskers[s].getChild("whisker2_" + side);
			whiskerEnds[s] = whiskerTips[s].getChild("whisker3_" + side);
			legs[0][s] = Leg.of(segments[1], "seg1", side);
			legs[1][s] = Leg.of(segments[4], "seg4", side);
		}
	}

	public static LayerDefinition createBodyLayer() {
		return LongDragonGeometry.create();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks / 20.0f;
		// Ondula más al moverse y apenas cuando flota quieto.
		float move = Math.min(1.0f, state.walkAnimationSpeed * 2.0f);
		float k = 0.35f + move * 0.65f;
		float landing = state instanceof MythicRenderers.Dragon.State s ? s.landing : 0.0f;
		// Curva suave (smoothstep) para que patas y garras no arranquen de golpe al entrar en el alcance del suelo.
		float land = landing * landing * (3.0f - 2.0f * landing);

		// Cuerpo: onda que viaja de la cabeza a la cola, cada segmento atrasado respecto del anterior, sobre la pose en S.
		segments[0].yRot += Mth.sin(t * 0.9f) * 0.15f * k;
		for (int i = 1; i < SEGMENTS; i++) {
			segments[i].xRot += Mth.sin(t * 1.8f - i * 0.7f) * 0.16f * k;
			segments[i].yRot += Mth.sin(t * 1.3f - i * 0.6f) * 0.22f * k;
		}
		// Cabeza: compensa la onda del cuello para mirar al frente y sigue la mirada del jinete. Los cuernos van fijos.
		head.xRot += -Mth.sin(t * 1.8f + 0.7f) * 0.12f * k + state.xRot * Mth.DEG_TO_RAD * 0.5f;
		head.yRot += -Mth.sin(t * 0.9f) * 0.12f * k;
		jaw.xRot += Math.max(0.0f, Mth.sin(t * 0.8f)) * 0.25f;

		// Melena de llamas: los planos se mecen hacia los lados siguiendo la onda del cuello, el segundo plano más
		// atrasado que el primero; nunca se cierran hasta tocar el cráneo (la apertura de reposo es mayor que el vaivén).
		for (int s = 0; s < 2; s++) {
			float sign = s == 0 ? -1.0f : 1.0f;
			manes[s].yRot += sign * Mth.sin(t * 1.3f - 0.9f) * 0.12f;
			manes[s].xRot += Mth.sin(t * 2.4f - 1.3f) * 0.07f + move * 0.08f;
			manes2[s].yRot += sign * Mth.sin(t * 1.3f - 1.5f) * 0.14f;
			manes2[s].xRot += Mth.sin(t * 2.4f - 1.9f) * 0.08f + move * 0.1f;
		}
		maneTop.yRot += Mth.sin(t * 1.3f - 1.1f) * 0.14f * k;
		maneTop.xRot += Mth.sin(t * 2.4f - 1.4f) * 0.06f + move * 0.06f;

		// Barba en tres planos: cuelgan de la quijada y se balancean con retraso; al volar rápido se van hacia atrás.
		beard.zRot += Mth.sin(t * 1.3f - 1.2f) * 0.1f;
		beard.xRot += Mth.sin(t * 1.8f - 1.0f) * 0.08f + move * 0.18f;
		for (int s = 0; s < 2; s++) {
			beardSides[s].zRot += Mth.sin(t * 1.3f - 1.6f - s * 0.4f) * 0.12f;
			beardSides[s].xRot += Mth.sin(t * 1.8f - 1.4f - s * 0.4f) * 0.09f + move * 0.2f;
		}

		// Bigotes de tres tramos: ondean como cintas, cada tramo atrasado y con más vaivén que el anterior.
		for (int s = 0; s < 2; s++) {
			whiskers[s].xRot += Mth.sin(t * 2.2f) * 0.2f;
			whiskerTips[s].xRot += Mth.sin(t * 2.2f - 0.8f) * 0.4f;
			whiskerEnds[s].xRot += Mth.sin(t * 2.2f - 1.6f) * 0.45f;
			whiskerEnds[s].yRot += Mth.sin(t * 1.6f - 1.6f) * 0.15f;
		}

		// Patas: reman en el aire (muslo, canilla y planta en cadena, cada una atrasada), las delanteras y traseras en
		// contrafase. Cerca del suelo dejan de remar, se estiran hacia abajo y la planta se aplana para posarse.
		float paddle = k * (1.0f - 0.7f * land);
		for (int pair = 0; pair < 2; pair++) {
			for (int s = 0; s < 2; s++) {
				Leg leg = legs[pair][s];
				float phase = t * 2.0f + ((pair == 0) == (s == 0) ? 0.0f : 1.6f);
				leg.leg.xRot += Mth.sin(phase) * 0.35f * paddle - 1.0f * land;
				leg.shin.xRot += Mth.sin(phase - 0.7f) * 0.25f * paddle + 0.35f * land;
				leg.foot.xRot += Mth.sin(phase - 1.4f) * 0.2f * paddle + 0.45f * land;
				// Llamas del codo: aletean rápido hacia afuera y hacia atrás, como fuego al viento.
				float sign = s == 0 ? -1.0f : 1.0f;
				leg.elbow.yRot += sign * Mth.sin(t * 5.5f - pair * 0.8f - s * 0.5f) * 0.18f;
				leg.elbow.xRot += Mth.sin(t * 4.2f - pair * 0.6f) * 0.1f;
				// Garras de águila: recogidas en vuelo; al posarse se abren en abanico, con las puntas alzadas
				// y el espolón de atrás separado (con un temblor mínimo para que no queden de piedra).
				float claws = land + Mth.sin(t * 1.1f + pair) * 0.04f;
				leg.outer.yRot += 0.32f * claws;
				leg.inner.yRot -= 0.32f * claws;
				leg.outer.xRot -= 0.25f * claws;
				leg.middle.xRot -= 0.3f * claws;
				leg.inner.xRot -= 0.25f * claws;
				leg.spur.xRot += 0.3f * claws;
			}
		}
		// Mechón de la cola: sigue la onda lateral con el retraso del último segmento.
		tuft.yRot += Mth.sin(t * 1.3f - 6.6f) * 0.3f * k;
	}
}
