package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Mono guerrero del Rey Mono (geometría en {@link ChinaModelLayers#monkeyWarrior}, igual que el visor de diseño): cara
 * peluda con ojos de fuego que brillan, gorro de alas de fénix con dos plumas largas de faisán, diadema de oro, cota de
 * malla dorada, faldellín de tigre, botas de nube, cola y el bastón en la mano derecha. Al andar balancea brazos y
 * piernas; las plumas y la cola se mecen.
 */
public class MonkeyWarriorModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("monkey_warrior"), "main");

	private final ModelPart head;
	private final ModelPart armRight;
	private final ModelPart armLeft;
	private final ModelPart legRight;
	private final ModelPart legLeft;
	private final ModelPart tail;
	private final ModelPart tail2;
	private final ModelPart featherRight;
	private final ModelPart featherLeft;
	private final ModelPart featherRight2;
	private final ModelPart featherLeft2;

	public MonkeyWarriorModel(ModelPart root) {
		super(root);
		head = root.getChild("head");
		armRight = root.getChild("arm_r");
		armLeft = root.getChild("arm_l");
		legRight = root.getChild("leg_r");
		legLeft = root.getChild("leg_l");
		tail = root.getChild("body").getChild("tail");
		tail2 = tail.getChild("tail2");
		featherRight = head.getChild("feather_r");
		featherLeft = head.getChild("feather_l");
		featherRight2 = featherRight.getChild("feather_r2");
		featherLeft2 = featherLeft.getChild("feather_l2");
	}

	public static LayerDefinition createBodyLayer() {
		return ChinaModelLayers.monkeyWarrior();
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks;
		float pos = state.walkAnimationPos;
		float speed = Math.min(1.0f, state.walkAnimationSpeed);
		float swing = Mth.cos(pos * 0.6662f) * 1.2f * speed;
		head.yRot = state.yRot * Mth.DEG_TO_RAD;
		head.xRot = state.xRot * Mth.DEG_TO_RAD;
		legRight.xRot += swing;
		legLeft.xRot -= swing;
		armRight.xRot += -swing * 0.4f - 0.3f;
		armLeft.xRot += swing;
		tail.xRot += Mth.sin(t * 0.1f) * 0.15f;
		tail.yRot += Mth.sin(t * 0.075f) * 0.4f;
		tail2.xRot += Mth.sin(t * 0.12f) * 0.2f;
		float sway = 0.08f + speed * 0.15f;
		featherRight.xRot += Mth.sin(t * 0.15f) * sway;
		featherLeft.xRot += Mth.sin(t * 0.15f + 0.5f) * sway;
		featherRight2.xRot += Mth.sin(t * 0.15f - 0.6f) * sway * 2;
		featherLeft2.xRot += Mth.sin(t * 0.15f - 0.1f) * sway * 2;
	}
}
