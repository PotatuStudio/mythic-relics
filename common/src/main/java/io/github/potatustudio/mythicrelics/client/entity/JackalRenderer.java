package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.Jackal;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/** Chacal negro de ojos dorados con collar de oro y lapislázuli. */
public class JackalRenderer extends MobRenderer<Jackal, LivingEntityRenderState, JackalModel> {
	private static final Identifier TEXTURE = MythicRelics.id("textures/entity/jackal.png");

	public JackalRenderer(EntityRendererProvider.Context context) {
		super(context, new JackalModel(context.bakeLayer(JackalModel.LAYER)), 0.4f);
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
