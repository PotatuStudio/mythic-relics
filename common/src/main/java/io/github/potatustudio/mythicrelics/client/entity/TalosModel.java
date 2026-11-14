package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Talos (geometría en {@link MythicModelLayers#talos}): camina pesado como un gólem, balancea los brazos y, al lanzar,
 * levanta la roca por detrás de la cabeza y la arroja (la roca desaparece de la mano un instante y vuelve). Se dibuja a
 * 1,5 ({@link ChinaRenderers.TalosRenderer}).
 */
public class TalosModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("talos"), "main");
	private static final float THROW_TICKS = 14.0f;

	private final ModelPart head;
	private final ModelPart armR;
	private final ModelPart rock;
	private final ModelPart armL;
	private final ModelPart legR;
	private final ModelPart legL;

	public TalosModel(ModelPart root) {
		super(root);
		head = root.getChild("head");
		armR = root.getChild("arm_r");
		rock = armR.getChild("rock");
		armL = root.getChild("arm_l");
		legR = root.getChild("leg_r");
		legL = root.getChild("leg_l");
	}

	public static LayerDefinition createBodyLayer() {
		return MythicModelLayers.talos();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float pos = state.walkAnimationPos;
		float speed = Math.min(1.0f, state.walkAnimationSpeed);
		float swing = Mth.cos(pos * 0.6662f) * 0.7f * speed;
		legR.xRot += swing;
		legL.xRot -= swing;
		armL.xRot -= swing * 0.8f;
		armR.xRot += swing * 0.8f;
		head.yRot += state.yRot * Mth.DEG_TO_RAD;
		head.xRot += state.xRot * Mth.DEG_TO_RAD;
		float since = state instanceof ChinaRenderers.TalosRenderer.State s ? s.sinceThrow : 100.0f;
		float u = since / THROW_TICKS;
		rock.visible = !(u > 0.45f && u < 1.4f);
		if (u < 1.0f) {
			// 0–0,45: levanta la roca por detrás; 0,45–1: la arroja y el brazo baja.
			float raise = u < 0.45f ? u / 0.45f : 1.0f - (u - 0.45f) / 0.55f;
			float k = raise * raise * (3 - 2 * raise);
			armR.xRot += -2.8f * k;
			armR.zRot += 0.2f * k;
		}
	}
}
