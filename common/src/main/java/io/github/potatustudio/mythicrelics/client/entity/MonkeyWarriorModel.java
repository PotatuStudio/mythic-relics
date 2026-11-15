package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.geometry.MonkeyWarriorGeometry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Mono guerrero del Rey Mono (geometría en {@link MonkeyWarriorGeometry}, igual que el visor de diseño): cara
 * peluda con ojos de fuego que brillan, gorro de alas de fénix con dos plumas largas de faisán, diadema de oro, cota de
 * malla dorada, faldellín de tigre, botas de nube, cola de tres tramos y el bastón en la mano derecha.
 *
 * <p>Al andar balancea brazos y piernas; las plumas de tres tramos y la cola se mecen en cadena, cada tramo atrasado;
 * las alas del gorro vibran al correr; al golpear se encorva más, el brazo sube y baja y el bastón da una vuelta
 * completa en la mano. Todo se suma a la pose de reposo ({@code super.setupAnim} la restaura en cada cuadro).
 */
public class MonkeyWarriorModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("monkey_warrior"), "main");

	/** Cuánto más se encorva el torso en el punto alto del golpe (radianes). */
	private static final float HUNCH = 0.32f;

	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart armRight;
	private final ModelPart armLeft;
	private final ModelPart staff;
	private final ModelPart legRight;
	private final ModelPart legLeft;
	private final ModelPart tail;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart wingRight;
	private final ModelPart wingLeft;
	private final ModelPart[] feathersRight = new ModelPart[3];
	private final ModelPart[] feathersLeft = new ModelPart[3];

	public MonkeyWarriorModel(ModelPart root) {
		super(root);
		body = root.getChild("body");
		head = root.getChild("head");
		armRight = root.getChild("arm_r");
		armLeft = root.getChild("arm_l");
		staff = armRight.getChild("staff");
		legRight = root.getChild("leg_r");
		legLeft = root.getChild("leg_l");
		tail = body.getChild("tail");
		tail2 = tail.getChild("tail2");
		tail3 = tail2.getChild("tail3");
		wingRight = head.getChild("wing_r");
		wingLeft = head.getChild("wing_l");
		feathersRight[0] = head.getChild("feather_r");
		feathersRight[1] = feathersRight[0].getChild("feather_r2");
		feathersRight[2] = feathersRight[1].getChild("feather_r3");
		feathersLeft[0] = head.getChild("feather_l");
		feathersLeft[1] = feathersLeft[0].getChild("feather_l2");
		feathersLeft[2] = feathersLeft[1].getChild("feather_l3");
	}

	public static LayerDefinition createBodyLayer() {
		return MonkeyWarriorGeometry.create();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks;
		float pos = state.walkAnimationPos;
		float speed = Math.min(1.0f, state.walkAnimationSpeed);
		float attack = state instanceof MythicRenderers.Monkey.State s ? s.attack : 0.0f;

		// Golpe: una sola campana de seno (0 → 1 → 0) para que todo suba y vuelva sin saltos.
		float strike = attack > 0.0f ? Mth.sin(attack * Mth.PI) : 0.0f;

		// Torso: se encorva más al golpear; la cabeza y los brazos (que cuelgan de la raíz, no del torso) se mueven con
		// el pecho girando alrededor de la cintura, así no se separan del cuerpo.
		float hunch = strike * HUNCH;
		body.xRot += hunch;
		followChest(head, hunch);
		followChest(armRight, hunch);
		followChest(armLeft, hunch);

		// Cabeza: mira a donde mira el mono (y levanta un poco la vista al encorvarse para no perder al rival).
		head.yRot += state.yRot * Mth.DEG_TO_RAD;
		head.xRot += state.xRot * Mth.DEG_TO_RAD - hunch * 0.6f;

		// Andar: piernas y brazos en contrafase.
		float swing = Mth.cos(pos * 0.6662f) * 1.2f * speed;
		legRight.xRot += swing;
		legLeft.xRot -= swing;
		armRight.xRot += (-swing * 0.4f - 0.3f) * (1.0f - strike);
		armLeft.xRot += swing;

		// Golpe con el bastón: el brazo derecho sube por delante hasta arriba y baja; mientras, el bastón da una vuelta
		// entera en la mano, en el plano del costado (fuera del ancho del cuerpo y de la cabeza).
		armRight.xRot -= strike * 2.3f;
		float spin = Mth.clamp((attack - 0.05f) / 0.8f, 0.0f, 1.0f);
		staff.xRot -= Mth.TWO_PI * spin * spin * (3.0f - 2.0f * spin);

		// Cola de tres tramos: se mece arriba y abajo y de lado a lado; cada tramo va atrasado respecto del anterior y
		// con un poco más de vaivén, como un látigo lento.
		tail.xRot += Mth.sin(t * 0.1f) * 0.15f;
		tail.yRot += Mth.sin(t * 0.075f) * 0.4f;
		tail2.xRot += Mth.sin(t * 0.1f - 0.6f) * 0.2f;
		tail2.yRot += Mth.sin(t * 0.075f - 0.6f) * 0.15f;
		tail3.xRot += Mth.sin(t * 0.1f - 1.2f) * 0.25f;
		tail3.yRot += Mth.sin(t * 0.075f - 1.2f) * 0.2f;

		// Plumas de faisán de tres tramos: ondean con retraso creciente; al correr se van un poco hacia atrás.
		float sway = 0.08f + speed * 0.15f;
		for (int i = 0; i < 3; i++) {
			float amp = sway * (1.0f + i * 0.6f);
			feathersRight[i].xRot += Mth.sin(t * 0.15f - i * 0.6f) * amp - speed * 0.06f;
			feathersLeft[i].xRot += Mth.sin(t * 0.15f + 0.5f - i * 0.6f) * amp - speed * 0.06f;
		}

		// Alas de fénix del gorro: vibran rápido al correr (nada cuando está quieto).
		float run = Mth.clamp((speed - 0.3f) / 0.5f, 0.0f, 1.0f);
		float buzz = Mth.sin(t * 2.2f) * 0.1f * run;
		wingRight.zRot += buzz;
		wingLeft.zRot -= buzz;
	}

	/**
	 * Mueve una pieza colgada de la raíz como si fuera parte del pecho: gira su punto de unión alrededor de la cintura
	 * (el pivote del torso) el mismo ángulo extra que se encorva el torso.
	 */
	private void followChest(ModelPart part, float angle) {
		if (angle == 0.0f) {
			return;
		}
		PartPose waist = body.getInitialPose();
		PartPose rest = part.getInitialPose();
		float dy = rest.y() - waist.y();
		float dz = rest.z() - waist.z();
		float cos = Mth.cos(angle);
		float sin = Mth.sin(angle);
		part.y = waist.y() + dy * cos - dz * sin;
		part.z = waist.z() + dy * sin + dz * cos;
	}
}
