package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Kitsune de nueve colas (geometría en {@link MythicModelLayers#kitsune}, igual que el visor de diseño): zorro blanco de
 * Inari con babero rojo, nueve colas alzadas que se mecen y dos kitsunebi que giran a su alrededor. Se dibuja a 1,5
 * veces ({@link MythicRenderers.KitsuneRenderer}).
 */
public class KitsuneModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("kitsune"), "main");
	private static final int TAILS = 9;

	private final ModelPart head;
	private final ModelPart legFr;
	private final ModelPart legFl;
	private final ModelPart legHr;
	private final ModelPart legHl;
	private final ModelPart foxfire;
	private final ModelPart foxfireA;
	private final ModelPart foxfireB;
	private final ModelPart[] tails = new ModelPart[TAILS];
	private final ModelPart[] tips = new ModelPart[TAILS];

	public KitsuneModel(ModelPart root) {
		super(root);
		head = root.getChild("head");
		legFr = root.getChild("leg_fr");
		legFl = root.getChild("leg_fl");
		legHr = root.getChild("leg_hr");
		legHl = root.getChild("leg_hl");
		foxfire = root.getChild("foxfire");
		foxfireA = foxfire.getChild("foxfire_a");
		foxfireB = foxfire.getChild("foxfire_b");
		ModelPart body = root.getChild("body");
		for (int i = 0; i < TAILS; i++) {
			tails[i] = body.getChild("tail" + i);
			tips[i] = tails[i].getChild("tail" + i + "_tip");
		}
	}

	public static LayerDefinition createBodyLayer() {
		return MythicModelLayers.kitsune();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks / 20.0f;
		float a = state.walkAnimationPos * 0.6662f;
		float w = 1.3f * Math.min(1.0f, state.walkAnimationSpeed);
		legFr.xRot += Mth.cos(a) * w;
		legHl.xRot += Mth.cos(a) * w;
		legFl.xRot -= Mth.cos(a) * w;
		legHr.xRot -= Mth.cos(a) * w;
		head.yRot = state.yRot * Mth.DEG_TO_RAD;
		head.xRot += state.xRot * Mth.DEG_TO_RAD;
		for (int i = 0; i < TAILS; i++) {
			tails[i].xRot += Mth.sin(t * 1.5f + i * 0.7f) * 0.08f;
			tails[i].yRot += Mth.sin(t * 1.2f + i * 0.5f) * 0.12f;
			tips[i].xRot += Mth.sin(t * 1.5f + i * 0.7f - 0.6f) * 0.15f;
		}
		foxfire.yRot = t * 1.6f;
		foxfireA.y = Mth.sin(t * 2.0f) * 1.5f;
		foxfireB.y = -2.0f + Mth.cos(t * 2.0f) * 1.5f;
	}
}
