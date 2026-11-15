package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.geometry.TalosGeometry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Talos (geometría en {@link TalosGeometry}): camina pesado como un gólem (el torso baja en cada pisada y carga el peso
 * de lado), balancea los brazos contra las piernas y, al lanzar, levanta la roca por detrás de la cabeza y la arroja (la
 * roca desaparece de la mano un instante y vuelve). Al abrazar cierra los dos brazos al frente; el bronce se pone al rojo
 * con una capa del renderizador ({@link MythicRenderers.TalosRenderer}). Se dibuja a 2,2.
 */
public class TalosModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("talos"), "main");
	private static final float THROW_TICKS = 14.0f;

	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart armR;
	private final ModelPart rock;
	private final ModelPart armL;
	private final ModelPart legR;
	private final ModelPart legL;

	public TalosModel(ModelPart root) {
		super(root);
		body = root.getChild("body");
		head = root.getChild("head");
		armR = root.getChild("arm_r");
		rock = armR.getChild("rock");
		armL = root.getChild("arm_l");
		legR = root.getChild("leg_r");
		legL = root.getChild("leg_l");
	}

	public static LayerDefinition createBodyLayer() {
		return TalosGeometry.create();
	}

	/** Curva de suavizado (0 → 1 con velocidad nula en los dos extremos). */
	private static float smooth(float v) {
		v = Mth.clamp(v, 0.0f, 1.0f);
		return v * v * (3.0f - 2.0f * v);
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		// resetPose deja cada parte en su pose de reposo; todo lo de abajo se suma encima.
		super.setupAnim(state);
		MythicRenderers.TalosRenderer.State s = state instanceof MythicRenderers.TalosRenderer.State st ? st : null;
		float since = s != null ? s.sinceThrow : 100.0f;
		float hug = s != null ? s.hug : 0.0f;
		float pos = state.walkAnimationPos;
		float speed = Math.min(1.0f, state.walkAnimationSpeed);
		float wp = pos * 0.6662f;

		// Paso pesado: las piernas van en oposición y los brazos contra la pierna del mismo lado, un poco atrasados (el
		// bronce pesa y llega tarde). Al andar los brazos se abren apenas para no rozar el faldellín.
		float swing = Mth.cos(wp) * 0.7f * speed;
		float armSwing = Mth.cos(wp - 0.35f) * 0.55f * speed * (1.0f - hug);
		legR.xRot += swing;
		legL.xRot -= swing;
		armR.xRot -= armSwing;
		armL.xRot += armSwing;
		armR.zRot += 0.06f * speed;
		armL.zRot -= 0.06f * speed;
		// En cada pisada (piernas en los extremos) el torso, la cabeza y los hombros bajan y vuelven a subir, y el cuerpo
		// carga el peso sobre la pierna que acaba de apoyar.
		float cosStep = Mth.cos(wp);
		float drop = 0.8f * speed * cosStep * cosStep;
		float roll = 0.04f * speed * cosStep;
		body.y += drop;
		head.y += drop;
		armR.y += drop;
		armL.y += drop;
		body.zRot += roll;
		head.zRot += roll * 0.6f;

		head.yRot += state.yRot * Mth.DEG_TO_RAD;
		head.xRot += state.xRot * Mth.DEG_TO_RAD;

		// Lanzamiento (u = 0 al soltar el evento): 0–0,45 levanta la roca por detrás de la cabeza mirando arriba y adelanta
		// el brazo izquierdo como contrapeso; 0,45–0,75 la arroja por encima y el brazo sigue de largo hasta el frente;
		// 0,75–1,3 vuelve al reposo. Cada tramo arranca y termina suave, sin saltos entre uno y otro.
		float u = since / THROW_TICKS;
		rock.visible = !(u > 0.45f && u < 1.4f);
		float throwX = 0.0f;
		float throwZ = 0.0f;
		float lookUp = 0.0f;
		float counter = 0.0f;
		if (u < 0.45f) {
			float k = smooth(u / 0.45f);
			throwX = -2.8f * k;
			throwZ = 0.2f * k;
			lookUp = -0.2f * k;
			counter = -0.5f * k;
		} else if (u < 0.75f) {
			float k = smooth((u - 0.45f) / 0.3f);
			throwX = Mth.lerp(k, -2.8f, -0.7f);
			throwZ = Mth.lerp(k, 0.2f, 0.05f);
			lookUp = Mth.lerp(k, -0.2f, 0.1f);
			counter = Mth.lerp(k, -0.5f, -0.2f);
		} else if (u < 1.3f) {
			float k = smooth((u - 0.75f) / 0.55f);
			throwX = Mth.lerp(k, -0.7f, 0.0f);
			throwZ = Mth.lerp(k, 0.05f, 0.0f);
			lookUp = Mth.lerp(k, 0.1f, 0.0f);
			counter = Mth.lerp(k, -0.2f, 0.0f);
		}
		armR.xRot += throwX;
		armR.zRot += throwZ;
		armL.xRot += counter;
		head.xRot += lookUp;

		// Abrazo al rojo (hug sube y baja suave con el golpe): los dos brazos se alzan al frente y se cierran hacia el
		// centro, con los puños casi juntos delante del pecho (sin cruzarse ni tocar la roca); la cabeza baja hacia la
		// presa.
		armR.xRot += -1.3f * hug;
		armL.xRot += -1.3f * hug;
		armR.yRot += -0.35f * hug;
		armL.yRot += 0.35f * hug;
		head.xRot += 0.15f * hug;
	}
}
