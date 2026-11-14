package io.github.potatustudio.mythicrelics.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.potatustudio.mythicrelics.entity.FreyrSword;
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
import net.minecraft.world.item.ItemStack;

/**
 * La espada de Freyr peleando sola: el ícono extruido (como un ítem en el suelo), más grande, con la punta hacia donde
 * mira la entidad. En los tajos y al envainarse gira sobre su hoja; brilla siempre al máximo (es la espada del sol).
 */
public class FreyrSwordRenderer extends EntityRenderer<FreyrSword, FreyrSwordRenderer.State> {
	public static class State extends EntityRenderState {
		final ItemStackRenderState item = new ItemStackRenderState();
		float xRot;
		float yRot;
		byte phase;
	}

	private final ItemModelResolver items;

	public FreyrSwordRenderer(EntityRendererProvider.Context context) {
		super(context);
		items = context.getItemModelResolver();
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(FreyrSword entity, State state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		items.updateForNonLiving(state.item, new ItemStack(ModItems.FREYR_SWORD.get()), ItemDisplayContext.GROUND, entity);
		state.xRot = entity.getXRot(partialTick);
		state.yRot = entity.getYRot(partialTick);
		state.phase = entity.state();
		state.lightCoords = 0xF000F0;
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0.0f, 0.25f, 0.0f);
		// Orientación: la punta (+y del ícono girado) hacia donde mira la entidad.
		poseStack.rotateDegrees(Axis.YP, -state.yRot);
		poseStack.rotateDegrees(Axis.XP, state.xRot + 90.0f);
		// En el tajo y al envainarse gira sobre su hoja.
		if (state.phase == FreyrSword.SLASH || state.phase == FreyrSword.SHEATHE) {
			poseStack.rotateDegrees(Axis.YP, state.ageInTicks * 38.0f);
		}
		// El ícono va en diagonal (punta arriba a la derecha): 45° lo deja con la punta hacia +y.
		poseStack.rotateDegrees(Axis.ZP, 45.0f);
		poseStack.scale(2.6f, 2.6f, 2.6f);
		state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();
		super.submit(state, poseStack, collector, camera);
	}
}
