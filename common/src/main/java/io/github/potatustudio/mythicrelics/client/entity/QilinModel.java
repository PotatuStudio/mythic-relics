package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * El qilin (geometría en {@link MythicModelLayers#qilin}, igual que el visor de diseño): cabeza de dragón con barba y un
 * cuerno, cuerpo de ciervo escamado verde jade y oro, cascos, cola de buey y llamas que brillan. Se dibuja a 1,35 veces
 * ({@link ChinaRenderers.QilinRenderer}), del tamaño de un caballo.
 */
public class QilinModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("qilin"), "main");

	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart legFr;
	private final ModelPart legFl;
	private final ModelPart legHr;
	private final ModelPart legHl;
	private final ModelPart tail;
	private final ModelPart mane;

	public QilinModel(ModelPart root) {
		super(root);
		neck = root.getChild("neck");
		head = neck.getChild("head");
		jaw = head.getChild("jaw");
		mane = neck.getChild("mane");
		legFr = root.getChild("leg_fr");
		legFl = root.getChild("leg_fl");
		legHr = root.getChild("leg_hr");
		legHl = root.getChild("leg_hl");
		tail = root.getChild("body").getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		return MythicModelLayers.qilin();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks;
		float a = state.walkAnimationPos * 0.6662f;
		float w = 1.1f * Math.min(1.0f, state.walkAnimationSpeed);
		legFr.xRot += Mth.cos(a) * w;
		legHl.xRot += Mth.cos(a) * w;
		legFl.xRot -= Mth.cos(a) * w;
		legHr.xRot -= Mth.cos(a) * w;
		neck.xRot += Mth.sin(a) * 0.05f * w;
		head.yRot += state.yRot * Mth.DEG_TO_RAD * 0.5f;
		head.xRot += Mth.sin(t * 0.04f) * 0.06f;
		jaw.xRot += Math.max(0.0f, Mth.sin(t * 0.045f)) * 0.15f;
		tail.xRot += Mth.sin(t * 0.08f) * 0.15f;
		tail.yRot += Mth.sin(t * 0.055f) * 0.3f;
		mane.zRot += Mth.sin(t * 0.2f) * 0.04f;
	}
}
