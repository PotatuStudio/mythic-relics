package io.github.potatustudio.mythicrelics.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.Sleipnir;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/**
 * Sleipnir: pelaje gris tormenta moteado, crin y cola plateadas, bardas de hierro con oro. Los ojos y las runas de las
 * bardas brillan en la oscuridad con una segunda textura (como los ojos de la araña).
 */
public class SleipnirRenderer extends AbstractHorseRenderer<Sleipnir, EquineRenderState, SleipnirModel> {
	private static final Identifier TEXTURE = MythicRelics.id("textures/entity/sleipnir.png");
	private static final RenderType GLOW = RenderTypes.eyes(MythicRelics.id("textures/entity/sleipnir_glow.png"));

	public SleipnirRenderer(EntityRendererProvider.Context context) {
		super(context, new SleipnirModel(context.bakeLayer(SleipnirModel.LAYER)), new SleipnirModel(context.bakeLayer(SleipnirModel.LAYER)));
		addLayer(new EyesLayer<>(this) {
			@Override
			public RenderType renderType() {
				return GLOW;
			}
		});
	}

	/** Un 25 % más grande que un caballo: el corcel de Odín (proposals/sleipnir.py). */
	public static final float SCALE = 1.25f;

	@Override
	protected void scale(EquineRenderState state, PoseStack poseStack) {
		super.scale(state, poseStack);
		poseStack.scale(SCALE, SCALE, SCALE);
	}

	@Override
	public Identifier getTextureLocation(EquineRenderState state) {
		return TEXTURE;
	}

	@Override
	public EquineRenderState createRenderState() {
		return new EquineRenderState();
	}
}
