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
 * que ondulan como una serpiente al volar, cabeza con cuernos de ciervo, bigotes que flotan, melena y barba de llamas,
 * cuatro patas con garras de águila que reman en el aire y la perla llameante que gira delante del hocico.
 */
public class LongDragonModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MythicRelics.id("long_dragon"), "main");
	private static final int SEGMENTS = 10;

	private final ModelPart[] segments = new ModelPart[SEGMENTS];
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart tuft;
	private final ModelPart[] whiskers = new ModelPart[2];
	private final ModelPart[] whiskerTips = new ModelPart[2];
	private final ModelPart[] frontLegs = new ModelPart[2];
	private final ModelPart[] hindLegs = new ModelPart[2];

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
		String[] sides = {"r", "l"};
		for (int s = 0; s < 2; s++) {
			whiskers[s] = head.getChild("whisker_" + sides[s]);
			whiskerTips[s] = whiskers[s].getChild("whisker2_" + sides[s]);
			frontLegs[s] = segments[1].getChild("seg1_leg_" + sides[s]);
			hindLegs[s] = segments[4].getChild("seg4_leg_" + sides[s]);
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
		float k = 0.35f + Math.min(1.0f, state.walkAnimationSpeed * 2.0f) * 0.65f;
		segments[0].yRot += Mth.sin(t * 0.9f) * 0.15f * k;
		for (int i = 1; i < SEGMENTS; i++) {
			segments[i].xRot += Mth.sin(t * 1.8f - i * 0.7f) * 0.16f * k;
			segments[i].yRot += Mth.sin(t * 1.3f - i * 0.6f) * 0.22f * k;
		}
		head.xRot += -Mth.sin(t * 1.8f + 0.7f) * 0.12f * k + state.xRot * Mth.DEG_TO_RAD * 0.5f;
		head.yRot += -Mth.sin(t * 0.9f) * 0.12f * k;
		jaw.xRot += Math.max(0.0f, Mth.sin(t * 0.8f)) * 0.25f;
		for (int s = 0; s < 2; s++) {
			whiskers[s].xRot += Mth.sin(t * 2.2f) * 0.2f;
			whiskerTips[s].xRot += Mth.sin(t * 2.2f - 0.8f) * 0.4f;
			frontLegs[s].xRot += Mth.sin(t * 2.0f + (s == 0 ? 0.0f : 1.6f)) * 0.35f * k;
			hindLegs[s].xRot += Mth.sin(t * 2.0f + (s == 0 ? 1.6f : 0.0f)) * 0.35f * k;
		}
		tuft.yRot += Mth.sin(t * 1.3f - 6.6f) * 0.3f * k;
	}
}
