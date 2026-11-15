package io.github.potatustudio.mythicrelics.client.entity;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.Scarab;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/** Escarabajo dorado con incrustaciones turquesa, como los amuletos egipcios. */
public class ScarabRenderer extends MobRenderer<Scarab, LivingEntityRenderState, ScarabModel> {
	private static final Identifier TEXTURE = MythicRelics.id("textures/entity/scarab.png");
	/** Lo que brilla en la oscuridad (ojos, disco solar, fauces de brasa): textures/entity/scarab_glow.png. */
	private static final RenderType GLOW = RenderTypes.eyes(MythicRelics.id("textures/entity/scarab_glow.png"));

	public ScarabRenderer(EntityRendererProvider.Context context) {
		super(context, new ScarabModel(context.bakeLayer(ScarabModel.LAYER)), 0.3f);
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
