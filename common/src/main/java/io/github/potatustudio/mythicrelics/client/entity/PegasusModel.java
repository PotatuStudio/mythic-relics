package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Pegaso (geometría en {@link MythicModelLayers#pegasus}, igual que la página de diseño): en el suelo camina como un
 * caballo y mece las alas; en el aire bate las alas, recoge las patas y la cola flota hacia atrás. El paso de un estado a
 * otro es gradual ({@code air}, de 0 a 1, lo suaviza el renderizador).
 */
public class PegasusModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("pegasus"), "main");

	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart wingR;
	private final ModelPart wingR2;
	private final ModelPart wingL;
	private final ModelPart wingL2;
	private final ModelPart tail;
	private final ModelPart tail2;
	private final ModelPart legFr;
	private final ModelPart legFl;
	private final ModelPart legBr;
	private final ModelPart legBl;

	public PegasusModel(ModelPart root) {
		super(root);
		ModelPart body = root.getChild("body");
		neck = body.getChild("neck");
		head = neck.getChild("head");
		wingR = body.getChild("wing_r");
		wingR2 = wingR.getChild("wing_r2");
		wingL = body.getChild("wing_l");
		wingL2 = wingL.getChild("wing_l2");
		tail = body.getChild("tail");
		tail2 = tail.getChild("tail2");
		legFr = root.getChild("leg_fr");
		legFl = root.getChild("leg_fl");
		legBr = root.getChild("leg_br");
		legBl = root.getChild("leg_bl");
	}

	public static LayerDefinition createBodyLayer() {
		return MythicModelLayers.pegasus();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks;
		float air = state instanceof ChinaRenderers.PegasusRenderer.State s ? s.air : 0.0f;
		float ground = 1.0f - air;
		// Paso (en el suelo): patas en diagonal, como el caballo vanilla.
		float pos = state.walkAnimationPos;
		float speed = Math.min(1.0f, state.walkAnimationSpeed) * ground;
		float swing = Mth.cos(pos * 0.6662f) * 0.9f * speed;
		legFr.xRot += swing - 0.9f * air;
		legBl.xRot += swing + 0.8f * air;
		legFl.xRot += -swing - 0.9f * air;
		legBr.xRot += -swing + 0.8f * air;
		// Alas: en el suelo se mecen; en el aire aletean con la punta un poco atrasada.
		float flap = Mth.sin(t * 0.55f) * 0.75f * air + Mth.sin(t * 0.06f) * 0.06f * ground;
		float tip = Mth.sin(t * 0.55f - 0.7f) * 0.45f * air;
		wingR.zRot += flap;
		wingL.zRot -= flap;
		wingR2.zRot += tip;
		wingL2.zRot -= tip;
		// Cuello y cabeza: cabeceo al caminar; en el aire, estirados hacia adelante.
		neck.xRot += Mth.sin(t * 0.08f) * 0.04f + Mth.cos(pos * 0.6662f) * 0.05f * speed + 0.25f * air;
		head.xRot += Mth.sin(t * 0.08f - 0.6f) * 0.05f - 0.15f * air;
		tail.xRot += Mth.sin(t * 0.07f) * 0.08f + 0.6f * air;
		tail.yRot += Mth.sin(t * 0.05f) * 0.15f;
		tail2.xRot += Mth.sin(t * 0.07f - 0.6f) * 0.12f - 0.2f * air;
	}
}
