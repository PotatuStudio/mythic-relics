package io.github.potatustudio.mythicrelics.client.entity;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.Cerberus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/** Cerbero: pelaje de sombra, ojos de brasa, fauces rojas y cola de serpiente. Se dibuja a 1,7 veces: del tamaño de un caballo. */
public class CerberusRenderer extends MobRenderer<Cerberus, LivingEntityRenderState, CerberusModel> {
	public static final float SCALE = 2.0f;
	private static final Identifier TEXTURE = MythicRelics.id("textures/entity/cerberus.png");
	/** Lo que brilla en la oscuridad (ojos, disco solar, fauces de brasa): textures/entity/cerberus_glow.png. */
	private static final RenderType GLOW = RenderTypes.eyes(MythicRelics.id("textures/entity/cerberus_glow.png"));

	public CerberusRenderer(EntityRendererProvider.Context context) {
		super(context, new CerberusModel(context.bakeLayer(CerberusModel.LAYER)), 1.1f);
		addLayer(new EyesLayer<>(this) {
			@Override
			public RenderType renderType() {
				return GLOW;
			}
		});
	}

	@Override
	protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
		poseStack.scale(SCALE, SCALE, SCALE);
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return TEXTURE;
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}
}
