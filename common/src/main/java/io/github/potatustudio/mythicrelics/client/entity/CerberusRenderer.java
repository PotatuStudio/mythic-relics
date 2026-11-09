package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.Cerberus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/** Cerbero: pelaje de sombra, ojos de brasa, fauces rojas y cola de serpiente. */
public class CerberusRenderer extends MobRenderer<Cerberus, LivingEntityRenderState, CerberusModel> {
	private static final Identifier TEXTURE = MythicRelics.id("textures/entity/cerberus.png");

	public CerberusRenderer(EntityRendererProvider.Context context) {
		super(context, new CerberusModel(context.bakeLayer(CerberusModel.LAYER)), 0.7f);
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
