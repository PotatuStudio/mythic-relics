package io.github.potatustudio.mythicrelics.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.potatustudio.mythicrelics.entity.FreyrSword;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/**
 * La espada de Freyr peleando sola, con su modelo 3D ({@link WeaponModel}): hoja clara con el canal dorado que brilla,
 * guarda de oro con gema ámbar. La punta va hacia donde mira la entidad; en los tajos y al envainarse gira sobre su
 * hoja. Brilla siempre al máximo (es la espada del sol).
 */
public class FreyrSwordRenderer extends EntityRenderer<FreyrSword, FreyrSwordRenderer.State> {
	/** Tamaño en el aire: la geometría mide unos 1,8 bloques de punta a pomo; así queda en ~1,45. */
	private static final float SCALE = 0.8f;

	public static class State extends EntityRenderState {
		float xRot;
		float yRot;
		byte phase;
	}

	private final WeaponModel model;

	public FreyrSwordRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new WeaponModel(context.bakeLayer(WeaponModel.FREYR_SWORD), "freyr_sword");
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(FreyrSword entity, State state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.xRot = entity.getXRot(partialTick);
		state.yRot = entity.getYRot(partialTick);
		state.phase = entity.state();
		state.lightCoords = 0xF000F0;
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0.0f, 0.25f, 0.0f);
		// Orientación: después de estos dos giros, +y apunta hacia donde mira la entidad.
		poseStack.rotateDegrees(Axis.YP, -state.yRot);
		poseStack.rotateDegrees(Axis.XP, state.xRot + 90.0f);
		// En el tajo y al envainarse gira sobre su hoja.
		if (state.phase == FreyrSword.SLASH || state.phase == FreyrSword.SHEATHE) {
			poseStack.rotateDegrees(Axis.YP, state.ageInTicks * 38.0f);
		}
		// La punta del modelo mira a -y: media vuelta la deja hacia +y.
		poseStack.rotateDegrees(Axis.XP, 180.0f);
		poseStack.scale(SCALE, SCALE, SCALE);
		model.submit(poseStack, collector, state.lightCoords, state.outlineColor);
		poseStack.popPose();
		super.submit(state, poseStack, collector, camera);
	}
}
