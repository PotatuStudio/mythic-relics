package io.github.potatustudio.mythicrelics.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.potatustudio.mythicrelics.entity.ThrownRelic;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Mjölnir y Gungnir en vuelo, con su modelo 3D (el del ítem fuera del inventario), orientados como el tridente: Gungnir
 * con la punta hacia adelante y Mjölnir girando de punta a punta.
 */
public class ThrownRelicRenderer extends EntityRenderer<ThrownRelic, ThrownRelicRenderer.State> {
	public static class State extends EntityRenderState {
		final ItemStackRenderState item = new ItemStackRenderState();
		float xRot;
		float yRot;
		boolean hammer;
		boolean dart;
	}

	private final ItemModelResolver items;

	public ThrownRelicRenderer(EntityRendererProvider.Context context) {
		super(context);
		items = context.getItemModelResolver();
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(ThrownRelic entity, State state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		items.updateForNonLiving(state.item, entity.getItem(), ItemDisplayContext.NONE, entity);
		state.xRot = entity.getXRot(partialTick);
		state.yRot = entity.getYRot(partialTick);
		state.hammer = entity.getItem().is(ModItems.MJOLNIR.get());
		state.dart = entity.getItem().is(ModItems.MISTLETOE_DART.get());
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.rotateDegrees(Axis.YP, state.yRot - 90.0f);
		poseStack.rotateDegrees(Axis.ZP, state.xRot + 90.0f);
		// El modelo del ítem tiene la punta hacia +y; el tridente, hacia -y.
		poseStack.rotateDegrees(Axis.ZP, 180.0f);
		if (state.dart) {
			// El dardo es el ícono plano (en diagonal): se gira 45° para que la punta vaya por delante.
			poseStack.rotateDegrees(Axis.ZP, 45.0f);
			poseStack.scale(1.6f, 1.6f, 1.6f);
		} else if (state.hammer) {
			poseStack.rotateDegrees(Axis.XP, state.ageInTicks * 30.0f);
			poseStack.scale(1.1f, 1.1f, 1.1f);
		} else {
			poseStack.translate(0.5f, 0.5f, 0.5f);
			poseStack.scale(2.0f, 2.0f, 2.0f);
		}
		state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();
		super.submit(state, poseStack, collector, camera);
	}
}
