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

import java.util.Map;
import java.util.WeakHashMap;

/** Chacal negro de ojos dorados con collar de oro y lapislázuli. */
public class JackalRenderer extends MobRenderer<Jackal, LivingEntityRenderState, JackalModel> {
	private static final Identifier TEXTURE = MythicRelics.id("textures/entity/jackal.png");
	/** Lo que brilla en la oscuridad (ojos, disco solar, fauces de brasa): textures/entity/jackal_glow.png. */
	private static final RenderType GLOW = RenderTypes.eyes(MythicRelics.id("textures/entity/jackal_glow.png"));

	/** Furia suavizada de cada chacal (el estado de render se comparte entre entidades, la memoria va aquí). */
	private final Map<Jackal, float[]> fury = new WeakHashMap<>();

	/** Estado de render con la furia (persigue a un objetivo) y el avance de la mordida. */
	public static class State extends LivingEntityRenderState {
		/** 0 = tranquilo, 1 = al ataque; cambia suave en unos pocos ticks. */
		public float fury;
		/** Avance del golpe, de 0 a 1 (0 si no muerde). */
		public float bite;
	}

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
		return new State();
	}

	@Override
	public void extractRenderState(Jackal jackal, LivingEntityRenderState state, float partialTick) {
		super.extractRenderState(jackal, state, partialTick);
		if (state instanceof State s) {
			float time = jackal.tickCount + partialTick;
			s.fury = CerberusRenderer.approach(fury, jackal, jackal.isAggressive() ? 1.0f : 0.0f, time, 0.15f);
			s.bite = jackal.getSwingAnimation(partialTick);
		}
	}
}
