package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.FalconForm;
import io.github.potatustudio.mythicrelics.client.entity.geometry.FreyjaFalconGeometry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * El halcón del manto de Freyja (geometría en {@link FreyjaFalconGeometry}): alas de tres tramos (brazo, antebrazo y
 * mano con cuatro primarias), cola de cinco plumas y patas recogidas.
 *
 * <p>Aleteo rápido con el antebrazo y la mano en latigazo (cada tramo atrasado respecto del anterior) y las primarias
 * que se abren al bajar; cada tanto planea con las alas en arco (brazo alzado, mano caída) y las primarias apenas
 * abiertas. En picada pliega las alas en punta, barridas hacia la cola y pegadas al lomo, con la cola cerrada. En el
 * suelo también las pliega y baja las patas. El vuelo (picada, subida, suelo) lo calcula {@link FalconForm} a partir
 * del movimiento del jugador. Todo se suma a la pose de reposo ({@code super.setupAnim} la restaura en cada cuadro).
 */
public class FreyjaFalconModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("freyja_falcon"), "main");
	private static final String[] SIDES = {"r", "l"};

	/**
	 * Picada: barrido hacia atrás de brazo, antebrazo y mano. Con estos ángulos las puntas se juntan detrás de la cola,
	 * cerca de la línea del lomo, sin cruzarse (la silueta en punta del peregrino).
	 */
	private static final float DIVE_ARM = 0.8f;
	private static final float DIVE_MID = 0.6f;
	private static final float DIVE_TIP = 0.3f;
	/** En picada el ala pasa por encima del lomo: se alza el hombro (un poco distinto en cada lado para no solaparse). */
	private static final float DIVE_LIFT_R = 1.2f;
	private static final float DIVE_LIFT_L = 1.35f;

	private final ModelPart head;
	private final ModelPart[] wings = new ModelPart[2];
	private final ModelPart[] mids = new ModelPart[2];
	private final ModelPart[] tips = new ModelPart[2];
	private final ModelPart[][] primaries = new ModelPart[2][4];
	private final float[][] primaryRest = new float[2][4];
	private final ModelPart tail;
	private final ModelPart[] tailFeathers = new ModelPart[5];
	private final float[] tailRest = new float[5];
	private final ModelPart[] legs = new ModelPart[2];

	public FreyjaFalconModel(ModelPart root) {
		super(root);
		head = root.getChild("head");
		ModelPart body = root.getChild("body");
		for (int s = 0; s < 2; s++) {
			String side = SIDES[s];
			wings[s] = body.getChild("wing_" + side);
			mids[s] = wings[s].getChild("wing_" + side + "_mid");
			tips[s] = mids[s].getChild("wing_" + side + "_tip");
			for (int i = 0; i < 4; i++) {
				primaries[s][i] = tips[s].getChild("wing_" + side + "_p" + i);
				primaryRest[s][i] = primaries[s][i].getInitialPose().yRot();
			}
			legs[s] = body.getChild("leg_" + side);
		}
		tail = body.getChild("tail");
		for (int i = 0; i < 5; i++) {
			tailFeathers[i] = tail.getChild("tail_f" + i);
			tailRest[i] = tailFeathers[i].getInitialPose().yRot();
		}
	}

	public static LayerDefinition createBodyLayer() {
		return FreyjaFalconGeometry.create();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks;
		FalconForm.Flight flight = FalconForm.flight(state);
		float dive = flight != null ? flight.dive() : 0.0f;
		float ground = flight != null ? flight.ground() : 0.0f;
		float climb = flight != null ? flight.climb() : 0.0f;

		// Plegado: en picada o en el suelo (curva suave para que no arranque de golpe).
		float fold = smooth(Math.max(dive, ground));
		float open = 1.0f - fold;
		// Planeo: cada tanto deja de batir y planea; al subir siempre bate.
		float glide = Mth.clamp(Mth.sin(t * 0.05f) * 2.0f, 0.0f, 1.0f) * (1.0f - climb) * open;
		float flapping = (1.0f - glide) * open;

		// Aleteo rápido: el brazo marca el golpe y el antebrazo y la mano lo siguen atrasados (latigazo). Al bajar las
		// primarias se separan apenas; al subir se juntan.
		float phase = t * 0.9f;
		float armBeat = Mth.sin(phase) * 0.7f * flapping;
		float midBeat = Mth.sin(phase - 0.5f) * 0.35f * flapping;
		float tipBeat = Mth.sin(phase - 1.0f) * 0.3f * flapping;
		float down = 0.5f * (1.0f - Mth.cos(phase - 0.4f)) * flapping;
		// Planeo en arco: brazo alzado, antebrazo casi recto y mano caída, con un vaivén lento de corrección.
		float drift = Mth.sin(t * 0.1f) * 0.05f * glide;

		for (int s = 0; s < 2; s++) {
			float sign = s == 0 ? 1.0f : -1.0f;
			wings[s].zRot += sign * (armBeat + 0.14f * glide + drift);
			mids[s].zRot += sign * (midBeat - 0.06f * glide);
			tips[s].zRot += sign * (tipBeat - 0.18f * glide);

			// Picada: alas barridas hacia la cola, en punta, por encima del lomo.
			wings[s].yRot += sign * DIVE_ARM * fold;
			wings[s].y -= (s == 0 ? DIVE_LIFT_R : DIVE_LIFT_L) * fold;
			mids[s].yRot += sign * DIVE_MID * fold;
			tips[s].yRot += sign * DIVE_TIP * fold;

			// Primarias: abiertas al bajar, apenas abiertas al planear, juntas en la picada.
			for (int i = 0; i < 4; i++) {
				float rest = primaryRest[s][i];
				float spread = rest * 0.5f + sign * i * 0.03f;
				primaries[s][i].yRot += spread * down + spread * 0.5f * glide - rest * fold;
			}

			// Patas: recogidas en vuelo; en el suelo bajan hasta quedar casi verticales.
			legs[s].xRot -= 0.75f * smooth(ground);
		}

		// Cola: se abre al planear, se cierra en la picada y cabecea despacio.
		tail.xRot += Mth.sin(t * 0.12f) * 0.08f * open;
		for (int i = 0; i < 5; i++) {
			tailFeathers[i].yRot += tailRest[i] * (0.4f * glide - 0.8f * fold);
		}
		head.xRot += state.xRot * Mth.DEG_TO_RAD * 0.5f;
	}

	/** Curva suave entre 0 y 1 (smoothstep), recortada fuera de ese rango. */
	private static float smooth(float x) {
		float c = Mth.clamp(x, 0.0f, 1.0f);
		return c * c * (3.0f - 2.0f * c);
	}
}
