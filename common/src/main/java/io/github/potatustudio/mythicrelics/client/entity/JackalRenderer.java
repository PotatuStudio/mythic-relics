package io.github.potatustudio.mythicrelics.client.entity;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.Jackal;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/** Chacal negro de ojos dorados con collar de oro y lapislázuli. */
public class JackalRenderer extends MobRenderer<Jackal, LivingEntityRenderState, JackalModel> {
	private static final Identifier TEXTURE = MythicRelics.id("textures/entity/jackal.png");
	/** Lo que brilla en la oscuridad (ojos, disco solar, fauces de brasa): textures/entity/jackal_glow.png. */
	private static final RenderType GLOW = RenderTypes.eyes(MythicRelics.id("textures/entity/jackal_glow.png"));

	public JackalRenderer(EntityRendererProvider.Context context) {
		super(context, new JackalModel(context.bakeLayer(JackalModel.LAYER)), 0.4f);
		addLayer(new EyesLayer<>(this) {
			@Override
			public RenderType renderType() {
				return GLOW;
			}
		});
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
