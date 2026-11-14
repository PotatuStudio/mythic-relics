package io.github.potatustudio.mythicrelics.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.potatustudio.mythicrelics.entity.ThrownRelic;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/**
 * Mjölnir, Gungnir y el dardo de muérdago en vuelo, con su modelo 3D ({@link WeaponModel}) orientado como el tridente:
 * Gungnir y el dardo con la punta hacia adelante, y Mjölnir girando de punta a punta.
 */
public class ThrownRelicRenderer extends EntityRenderer<ThrownRelic, ThrownRelicRenderer.State> {
	public static class State extends EntityRenderState {
		float xRot;
		float yRot;
		byte kind;
	}

	private static final byte GUNGNIR = 0;
	private static final byte HAMMER = 1;
	private static final byte DART = 2;

	private final WeaponModel gungnir;
	private final WeaponModel mjolnir;
	private final WeaponModel dart;

	public ThrownRelicRenderer(EntityRendererProvider.Context context) {
		super(context);
		gungnir = new WeaponModel(context.bakeLayer(WeaponModel.GUNGNIR), "gungnir");
		mjolnir = new WeaponModel(context.bakeLayer(WeaponModel.MJOLNIR), "mjolnir");
		dart = new WeaponModel(context.bakeLayer(WeaponModel.MISTLETOE_DART), "mistletoe_dart");
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(ThrownRelic entity, State state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.xRot = entity.getXRot(partialTick);
		state.yRot = entity.getYRot(partialTick);
		state.kind = entity.getItem().is(ModItems.MJOLNIR.get()) ? HAMMER
				: entity.getItem().is(ModItems.MISTLETOE_DART.get()) ? DART : GUNGNIR;
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		// Igual que ThrownTridentRenderer: la punta del modelo (-y) queda hacia donde vuela.
		poseStack.rotateDegrees(Axis.YP, state.yRot - 90.0f);
		poseStack.rotateDegrees(Axis.ZP, state.xRot + 90.0f);
		WeaponModel model = switch (state.kind) {
			case HAMMER -> {
				// Gira de punta a punta en el plano vertical del vuelo.
				poseStack.rotateDegrees(Axis.ZP, state.ageInTicks * 30.0f);
				yield mjolnir;
			}
			case DART -> dart;
			default -> gungnir;
		};
		model.submit(poseStack, collector, state.lightCoords, state.outlineColor);
		poseStack.popPose();
		super.submit(state, poseStack, collector, camera);
	}
}
