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

import java.util.Map;
import java.util.WeakHashMap;

/** Escarabajo dorado con incrustaciones turquesa, como los amuletos egipcios. */
public class ScarabRenderer extends MobRenderer<Scarab, LivingEntityRenderState, ScarabModel> {
	private static final Identifier TEXTURE = MythicRelics.id("textures/entity/scarab.png");
	/** Lo que brilla en la oscuridad (ojos, disco solar, fauces de brasa): textures/entity/scarab_glow.png. */
	private static final RenderType GLOW = RenderTypes.eyes(MythicRelics.id("textures/entity/scarab_glow.png"));

	/** Apertura suavizada de los élitros y alerta de cada escarabajo (el estado de render se comparte, la memoria va aquí). */
	private final Map<Scarab, float[]> elytra = new WeakHashMap<>();
	private final Map<Scarab, float[]> alert = new WeakHashMap<>();

	/** Estado de render con la apertura de los élitros y la alerta (persigue a un objetivo). */
	public static class State extends LivingEntityRenderState {
		/** 0 = élitros cerrados, 1 = abiertos (en el aire o atacando); se abren rápido y se cierran despacio. */
		public float elytra;
		/** 0 = tranquilo, 1 = al ataque; abre el abanico de las antenas. */
		public float alert;
	}

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
		return new State();
	}

	@Override
	public void extractRenderState(Scarab scarab, LivingEntityRenderState state, float partialTick) {
		super.extractRenderState(scarab, state, partialTick);
		if (state instanceof State s) {
			float time = scarab.tickCount + partialTick;
			boolean open = (!scarab.onGround() && !scarab.isInWater() && !scarab.isPassenger()) || scarab.isSwinging();
			// Se abren en ~3 ticks y se cierran en ~8, para que no parpadeen entre salto y salto.
			float target = open ? 1.0f : 0.0f;
			float current = elytra.containsKey(scarab) ? elytra.get(scarab)[0] : 0.0f;
			s.elytra = CerberusRenderer.approach(elytra, scarab, target, time, target > current ? 0.5f : 0.2f);
			s.alert = CerberusRenderer.approach(alert, scarab, scarab.isAggressive() ? 1.0f : 0.0f, time, 0.15f);
		}
	}
}
