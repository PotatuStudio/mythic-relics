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
import net.minecraft.util.Mth;

import java.util.Map;
import java.util.WeakHashMap;

/** Cerbero: pelaje de sombra, ojos de brasa, fauces rojas y cola de serpiente. Se dibuja a 2 veces: más grande que un caballo. */
public class CerberusRenderer extends MobRenderer<Cerberus, LivingEntityRenderState, CerberusModel> {
	public static final float SCALE = 2.0f;
	private static final Identifier TEXTURE = MythicRelics.id("textures/entity/cerberus.png");
	/** Lo que brilla en la oscuridad (ojos, disco solar, fauces de brasa): textures/entity/cerberus_glow.png. */
	private static final RenderType GLOW = RenderTypes.eyes(MythicRelics.id("textures/entity/cerberus_glow.png"));

	/**
	 * Furia suavizada de cada Cerbero ({valor, último instante}). El estado de render se reutiliza entre entidades, así
	 * que lo que necesita memoria (para pasar de calmo a furioso sin saltos) se guarda aquí, por entidad.
	 */
	private final Map<Cerberus, float[]> fury = new WeakHashMap<>();

	/** Estado de render con lo que pide la animación: furia (persigue a un objetivo) y mordida (golpe en curso). */
	public static class State extends LivingEntityRenderState {
		/** 0 = calmo, 1 = furioso; sube y baja suave en unos pocos ticks. */
		public float fury;
		/** Avance del golpe, de 0 a 1 (0 si no está mordiendo). */
		public float bite;
	}

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
		return new State();
	}

	@Override
	public void extractRenderState(Cerberus cerberus, LivingEntityRenderState state, float partialTick) {
		super.extractRenderState(cerberus, state, partialTick);
		if (state instanceof State s) {
			float time = cerberus.tickCount + partialTick;
			s.fury = approach(fury, cerberus, cerberus.isAggressive() ? 1.0f : 0.0f, time, 0.15f);
			s.bite = cerberus.getSwingAnimation(partialTick);
		}
	}

	/** Acerca el valor guardado de la entidad al objetivo con un amortiguado exponencial (sin saltos ni dependencia de los FPS). */
	static <E> float approach(Map<E, float[]> memory, E entity, float target, float time, float rate) {
		float[] m = memory.computeIfAbsent(entity, e -> new float[] {target, time});
		float dt = Mth.clamp(time - m[1], 0.0f, 10.0f);
		m[1] = time;
		m[0] += (target - m[0]) * (1.0f - (float) Math.exp(-rate * dt));
		return m[0];
	}
}
