package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.geometry.PegasusGeometry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Pegaso (geometría en {@link PegasusGeometry}, igual que la página de diseño): en el suelo camina como un caballo,
 * doblando rodillas y corvejones, con las alas plegadas hacia atrás contra los flancos y las primarias cerradas; en el
 * aire bate las alas en tres tramos (brazo, antebrazo y mano, cada uno un poco atrasado), abre las primarias, recoge las
 * patas y la cola ondea. El paso de un estado a otro es gradual ({@code air}, de 0 a 1, lo suaviza el renderizador).
 */
public class PegasusModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("pegasus"), "main");
	private static final int PRIMARIES = 7;

	// Plegado de las alas en el suelo, como un ave posada (comprobado contra las cajas del cuerpo, las patas y la cola):
	// el brazo baja pegado al flanco con las plumas hacia atrás, el antebrazo se dobla encima por fuera (la Z del ala de
	// ave) y la mano gira hacia la grupa, con las primarias juntas apuntando atrás, un poco hacia dentro y hacia arriba,
	// para que las puntas queden sobre la cola. El hombro sale y sube apenas lo justo para no meterse en el cuerpo.
	private static final float FOLD_SHOULDER_X = 3.0f;
	private static final float FOLD_SHOULDER_Y = -1.5f;
	private static final float FOLD_ARM_Z = -1.87f;
	private static final float FOLD_ARM_Y = -0.12f;
	private static final float FOLD_FOREARM = 2.85f;
	private static final float FOLD_FOREARM_OUT = -0.6f;
	private static final float FOLD_HAND_X = 1.196f;
	private static final float FOLD_HAND_Y = 1.184f;
	private static final float FOLD_HAND_Z = 0.786f;

	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart wingR;
	private final ModelPart wingR2;
	private final ModelPart wingR3;
	private final ModelPart[] primR = new ModelPart[PRIMARIES];
	private final ModelPart wingL;
	private final ModelPart wingL2;
	private final ModelPart wingL3;
	private final ModelPart[] primL = new ModelPart[PRIMARIES];
	private final ModelPart tail;
	private final ModelPart tail2;
	private final ModelPart lockR;
	private final ModelPart lockL;
	private final ModelPart legFr;
	private final ModelPart legFl;
	private final ModelPart legBr;
	private final ModelPart legBl;
	private final ModelPart lowerFr;
	private final ModelPart lowerFl;
	private final ModelPart lowerBr;
	private final ModelPart lowerBl;

	public PegasusModel(ModelPart root) {
		super(root);
		ModelPart body = root.getChild("body");
		neck = body.getChild("neck");
		head = neck.getChild("head");
		wingR = body.getChild("wing_r");
		wingR2 = wingR.getChild("wing_r2");
		wingR3 = wingR2.getChild("wing_r3");
		wingL = body.getChild("wing_l");
		wingL2 = wingL.getChild("wing_l2");
		wingL3 = wingL2.getChild("wing_l3");
		for (int k = 0; k < PRIMARIES; k++) {
			primR[k] = wingR3.getChild("wing_r_primary" + k);
			primL[k] = wingL3.getChild("wing_l_primary" + k);
		}
		tail = body.getChild("tail");
		tail2 = tail.getChild("tail2");
		lockR = tail2.getChild("tail_lock_r");
		lockL = tail2.getChild("tail_lock_l");
		legFr = root.getChild("leg_fr");
		legFl = root.getChild("leg_fl");
		legBr = root.getChild("leg_br");
		legBl = root.getChild("leg_bl");
		lowerFr = legFr.getChild("leg_fr_lower");
		lowerFl = legFl.getChild("leg_fl_lower");
		lowerBr = legBr.getChild("leg_br_lower");
		lowerBl = legBl.getChild("leg_bl_lower");
	}

	public static LayerDefinition createBodyLayer() {
		return PegasusGeometry.create();
	}

	/** Curva de suavizado (0 → 1 con velocidad nula en los dos extremos). */
	private static float smooth(float v) {
		v = Mth.clamp(v, 0.0f, 1.0f);
		return v * v * (3.0f - 2.0f * v);
	}

	/** Cuánto se levanta el pie de una pata que oscila como {@code cos(fase)}: sube mientras avanza, sin quiebres. */
	private static float lift(float phase) {
		float s = 0.5f + 0.5f * Mth.sin(phase);
		return s * s;
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		// resetPose deja cada parte en su pose de reposo; todo lo de abajo se suma encima.
		super.setupAnim(state);
		float t = state.ageInTicks;
		float air = state instanceof MythicRenderers.PegasusRenderer.State s ? s.air : 0.0f;
		float ground = 1.0f - air;

		// Paso (en el suelo): patas en diagonal, como el caballo vanilla. La caña dobla al levantar el pie (rodilla hacia
		// atrás, corvejón hacia adelante). En el aire, manos recogidas bajo el pecho y patas traseras estiradas atrás.
		float pos = state.walkAnimationPos;
		float speed = Math.min(1.0f, state.walkAnimationSpeed) * ground;
		float wp = pos * 0.6662f;
		float swing = Mth.cos(wp) * 0.9f * speed;
		legFr.xRot += swing - 0.9f * air;
		legBl.xRot += swing + 0.8f * air;
		legFl.xRot += -swing - 0.9f * air;
		legBr.xRot += -swing + 0.8f * air;
		lowerFr.xRot += 0.8f * speed * lift(wp) + 1.4f * air;
		lowerFl.xRot += 0.8f * speed * lift(wp + Mth.PI) + 1.4f * air;
		lowerBl.xRot += -0.6f * speed * lift(wp) - 0.5f * air;
		lowerBr.xRot += -0.6f * speed * lift(wp + Mth.PI) - 0.5f * air;

		// Aleteo (en el aire): brazo, antebrazo y mano con la misma onda, cada tramo más atrasado, así la mano llega
		// tarde y el ala se curva como un látigo. El brazo baja más de lo que sube para que las puntas no se crucen
		// sobre el lomo.
		float beat = t * 0.36f;   // unos 1,15 aletazos por segundo (el usuario lo vio muy rápido a 0,55)
		float arm = (-0.1f + 0.6f * Mth.sin(beat)) * air;
		float forearm = 0.35f * Mth.sin(beat - 0.6f) * air;
		float hand = 0.3f * Mth.sin(beat - 1.2f) * air;
		wingR.zRot += arm;
		wingL.zRot -= arm;
		wingR2.zRot += forearm;
		wingL2.zRot -= forearm;
		wingR3.zRot += hand;
		wingL3.zRot -= hand;

		// Plegado (en el suelo): por etapas para que nada cruce el cuerpo en la transición. El hombro sale primero; la
		// mano gira hacia la grupa mientras el ala sigue arriba; luego el antebrazo se dobla sobre el brazo (barre por
		// fuera) y, al final, el brazo baja contra el flanco ya plegado. Al despegar se deshace en orden inverso.
		float shoulder = smooth(ground / 0.45f);
		float handFold = smooth(ground / 0.55f);
		float forearmFold = smooth((ground - 0.1f) / 0.6f);
		float armDown = smooth((ground - 0.35f) / 0.65f);
		float breathe = (0.5f + 0.5f * Mth.sin(t * 0.06f)) * 0.03f * ground;   // en reposo el ala respira, siempre hacia afuera
		wingR.x -= FOLD_SHOULDER_X * shoulder;
		wingL.x += FOLD_SHOULDER_X * shoulder;
		wingR.y += FOLD_SHOULDER_Y * shoulder;
		wingL.y += FOLD_SHOULDER_Y * shoulder;
		wingR.yRot += FOLD_ARM_Y * armDown;
		wingL.yRot -= FOLD_ARM_Y * armDown;
		wingR.zRot += FOLD_ARM_Z * armDown + breathe;
		wingL.zRot -= FOLD_ARM_Z * armDown + breathe;
		wingR2.y += FOLD_FOREARM_OUT * forearmFold;
		wingL2.y += FOLD_FOREARM_OUT * forearmFold;
		wingR2.zRot += FOLD_FOREARM * forearmFold;
		wingL2.zRot -= FOLD_FOREARM * forearmFold;
		wingR3.xRot += FOLD_HAND_X * handFold;
		wingL3.xRot += FOLD_HAND_X * handFold;
		wingR3.yRot += FOLD_HAND_Y * handFold;
		wingL3.yRot -= FOLD_HAND_Y * handFold;
		wingR3.zRot += FOLD_HAND_Z * handFold;
		wingL3.zRot -= FOLD_HAND_Z * handFold;

		// Primarias: en el suelo el abanico se cierra (las plumas quedan juntas sobre la mano); en vuelo se abre un poco
		// más que en reposo y respira con el aleteo, cada pluma un poco más atrasada; las puntas se curvan con la onda.
		// El abanico escala el ángulo de reposo de cada pluma, así nunca se pisa su pose.
		float close = 0.9f * smooth(ground * 1.5f);
		for (int k = 0; k < PRIMARIES; k++) {
			float wave = Mth.sin(beat - 1.6f - 0.12f * k);
			float spread = 1.0f - close + air * (0.15f + 0.1f * wave);
			float curl = 0.12f * wave * air;
			primR[k].yRot = primR[k].getInitialPose().yRot() * spread;
			primL[k].yRot = primL[k].getInitialPose().yRot() * spread;
			primR[k].zRot += curl;
			primL[k].zRot -= curl;
		}

		// Cuello y cabeza: cabeceo al caminar (la cabeza va atrasada respecto del cuello); en el aire, estirados hacia
		// adelante y con un leve vaivén contrario al aleteo.
		neck.xRot += Mth.sin(t * 0.08f) * 0.04f + Mth.cos(wp) * 0.05f * speed + 0.25f * air - Mth.sin(beat) * 0.04f * air;
		head.xRot += Mth.sin(t * 0.08f - 0.6f) * 0.05f + Mth.cos(wp - 0.6f) * 0.04f * speed - 0.15f * air;

		// Cola: se mece despacio en el suelo; en el aire flota hacia atrás y ondea con cada aletazo. Cada tramo (raíz,
		// tramo largo y mechones sueltos) va atrasado respecto del anterior.
		tail.xRot += Mth.sin(t * 0.07f) * 0.08f + 0.6f * air;
		tail.yRot += Mth.sin(t * 0.05f) * 0.15f;
		tail2.xRot += Mth.sin(t * 0.07f - 0.6f) * 0.12f - 0.2f * air + Mth.sin(beat - 1.0f) * 0.12f * air;
		tail2.zRot += Mth.sin(t * 0.05f - 0.7f) * 0.1f;
		float lockSway = Mth.sin(t * 0.05f - 1.4f) * 0.06f;
		float lockWave = Mth.sin(beat - 1.6f) * 0.15f * air;
		lockR.xRot += lockWave + Mth.sin(t * 0.07f - 1.2f) * 0.06f;
		lockL.xRot += lockWave + Mth.sin(t * 0.07f - 1.4f) * 0.06f;
		lockR.zRot += lockSway;
		lockL.zRot += lockSway;
	}
}
