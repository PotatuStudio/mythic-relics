package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.Sleipnir;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.resources.Identifier;

/** Sleipnir: gris tormenta, crin y cola blancas, cascos oscuros y silla con adornos de oro. */
public class SleipnirRenderer extends AbstractHorseRenderer<Sleipnir, EquineRenderState, SleipnirModel> {
	private static final Identifier TEXTURE = MythicRelics.id("textures/entity/sleipnir.png");

	public SleipnirRenderer(EntityRendererProvider.Context context) {
		super(context, new SleipnirModel(context.bakeLayer(SleipnirModel.LAYER)), new SleipnirModel(context.bakeLayer(SleipnirModel.LAYER)));
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
