package io.github.potatustudio.mythicrelics.neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * Dibuja una reliquia puesta en un espacio de Curios con su modelo 3D ({@code models/item/<reliquia>_worn.json}), en el
 * mismo lugar que la dibuja Trinkets en Fabric, para que se vea igual en los dos loaders:
 * <ol>
 *   <li>pegada a la parte del cuerpo (se mueve con ella);</li>
 *   <li>en el centro de los cubos de esa parte, desplazada {@code offsetY} medios tamaños hacia arriba;</li>
 *   <li>con el eje Y hacia arriba y Z hacia el frente ({@code scale(1, -1, -1)});</li>
 *   <li>centrada: el ítem se dibuja en el contexto NONE, que la definición del ítem usa para el modelo puesto y que
 *       Minecraft centra con {@code translate(-0.5, -0.5, -0.5)}.</li>
 * </ol>
 */
public final class CuriosRelicRenderer implements ICurioRenderer {
	/** En qué parte del cuerpo va la reliquia. */
	public enum Part {
		HEAD, BODY, MAIN_ARM, BOTH_ARMS, BOTH_LEGS
	}

	private final Part part;
	private final float offsetY;
	private final ItemStackRenderState item = new ItemStackRenderState();

	public CuriosRelicRenderer(Part part, float offsetY) {
		this.part = part;
		this.offsetY = offsetY;
	}

	@Override
	public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(ItemStack stack, SlotContext slotContext,
			PoseStack poseStack, SubmitNodeCollector collector, int light, S renderState, RenderLayerParent<S, M> parent,
			EntityRendererProvider.Context context, float yRotation, float xRotation) {
		if (!(parent.getModel() instanceof HumanoidModel<?> model)) {
			return;
		}
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(item, stack, ItemDisplayContext.NONE,
				slotContext.entity().level(), slotContext.entity(), slotContext.entity().getId());
		HumanoidArm mainArm = renderState instanceof HumanoidRenderState humanoid ? humanoid.mainArm : HumanoidArm.RIGHT;
		ModelPart mainHand = mainArm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
		switch (part) {
			case HEAD -> draw(model, model.head, poseStack, collector, light);
			case BODY -> draw(model, model.body, poseStack, collector, light);
			case MAIN_ARM -> draw(model, mainHand, poseStack, collector, light);
			case BOTH_ARMS -> {
				draw(model, model.rightArm, poseStack, collector, light);
				draw(model, model.leftArm, poseStack, collector, light);
			}
			case BOTH_LEGS -> {
				draw(model, model.rightLeg, poseStack, collector, light);
				draw(model, model.leftLeg, poseStack, collector, light);
			}
		}
	}

	private void draw(HumanoidModel<?> model, ModelPart bodyPart, PoseStack poseStack, SubmitNodeCollector collector, int light) {
		float[] box = ownCubeBounds(bodyPart);
		float cx = (box[0] + box[3]) / 32f;
		float cy = (box[1] + box[4]) / 32f;
		float cz = (box[2] + box[5]) / 32f;
		float halfY = (box[4] - box[1]) / 32f;
		poseStack.pushPose();
		model.root().translateAndRotate(poseStack);
		bodyPart.translateAndRotate(poseStack);
		// En el espacio del modelo Y va hacia abajo: "arriba" resta.
		poseStack.translate(cx, cy - halfY * offsetY, cz);
		poseStack.scale(1f, -1f, -1f);
		item.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}

	/** Caja (en píxeles, espacio local de la parte) de los cubos propios de la parte, sin las capas de la skin. */
	private static float[] ownCubeBounds(ModelPart bodyPart) {
		float[] box = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
		bodyPart.visit(new PoseStack(), (pose, path, index, cube) -> {
			if (!path.isEmpty()) {
				return;
			}
			box[0] = Math.min(box[0], cube.minX);
			box[1] = Math.min(box[1], cube.minY);
			box[2] = Math.min(box[2], cube.minZ);
			box[3] = Math.max(box[3], cube.maxX);
			box[4] = Math.max(box[4], cube.maxY);
			box[5] = Math.max(box[5], cube.maxZ);
		});
		if (box[0] == Float.MAX_VALUE) {
			return new float[6];
		}
		return box;
	}
}
