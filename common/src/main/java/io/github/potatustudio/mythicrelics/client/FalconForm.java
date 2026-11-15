package io.github.potatustudio.mythicrelics.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.FreyjaFalconModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jspecify.annotations.Nullable;

/**
 * Forma de halcón del manto de Freyja: en lugar del jugador (y de todo lo que lleva encima: capa, armadura, reliquias,
 * objetos en la mano) se dibuja el halcón, con el mismo modelo y textura que tenía el halcón que acompañaba. El jugador
 * ya está encogido por el atributo de tamaño, así que la caja de golpe y la cámara coinciden con el ave.
 */
public final class FalconForm {
	/** Tamaño del ave (la geometría del halcón, a 1,6). */
	private static final float SCALE = 1.3f;
	private static final RenderType BODY = RenderTypes.entityCutout(MythicRelics.id("textures/entity/freyja_falcon.png"));
	private static final RenderType GLOW = RenderTypes.eyes(MythicRelics.id("textures/entity/freyja_falcon_glow.png"));

	private static @Nullable FreyjaFalconModel model;

	private FalconForm() {
	}

	/** Lo llama el renderer del jugador al crearse (ahí está el contexto para armar el modelo). */
	public static void bake(EntityRendererProvider.Context context) {
		model = new FreyjaFalconModel(context.bakeLayer(FreyjaFalconModel.LAYER));
	}

	/** Dibuja el halcón con la pose del jugador. Devuelve false si el modelo aún no existe (y se dibuja al jugador). */
	public static boolean submit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
		FreyjaFalconModel m = model;
		if (m == null) {
			return false;
		}
		poseStack.pushPose();
		// Como LivingEntityRenderer: gira con el cuerpo, voltea el modelo y lo apoya en los pies.
		poseStack.rotateDegrees(Axis.YP, 180.0f - state.bodyRot);
		poseStack.scale(-SCALE, -SCALE, SCALE);
		poseStack.translate(0.0f, -1.501f, 0.0f);
		m.setupAnim(state);
		collector.submitModel(m, state, poseStack, BODY, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		collector.submitModel(m, state, poseStack, GLOW, 0xF000F0, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
		return true;
	}
}
