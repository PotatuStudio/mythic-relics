package io.github.potatustudio.mythicrelics.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.LongDragon;
import io.github.potatustudio.mythicrelics.entity.MonkeyWarrior;
import io.github.potatustudio.mythicrelics.entity.OdinRaven;
import io.github.potatustudio.mythicrelics.entity.Pegasus;
import io.github.potatustudio.mythicrelics.entity.Talos;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

/**
 * Renderizadores de las entidades de todas las culturas (monos, long, cuervos, Pegaso, Talos…). Varias tienen una
 * segunda textura que brilla en la oscuridad (como los ojos de la araña): los ojos de fuego del mono; los ojos del long y
 * su perla llameante.
 */
public final class MythicRenderers {
	private MythicRenderers() {
	}

	private static <M extends EntityModel<LivingEntityRenderState>> EyesLayer<LivingEntityRenderState, M> glow(
			RenderLayerParent<LivingEntityRenderState, M> parent, String name) {
		RenderType type = RenderTypes.eyes(MythicRelics.id("textures/entity/" + name + "_glow.png"));
		return new EyesLayer<>(parent) {
			@Override
			public RenderType renderType() {
				return type;
			}
		};
	}

	/** Base común: textura propia y capa que brilla. */
	private abstract static class Glowing<T extends Mob, M extends EntityModel<LivingEntityRenderState>>
			extends MobRenderer<T, LivingEntityRenderState, M> {
		private final Identifier texture;

		Glowing(EntityRendererProvider.Context context, M model, float shadow, String name) {
			super(context, model, shadow);
			texture = MythicRelics.id("textures/entity/" + name + ".png");
			addLayer(glow(this, name));
		}

		@Override
		public Identifier getTextureLocation(LivingEntityRenderState state) {
			return texture;
		}

		@Override
		public LivingEntityRenderState createRenderState() {
			return new LivingEntityRenderState();
		}
	}

	/** Mono guerrero. */
	public static class Monkey extends Glowing<MonkeyWarrior, MonkeyWarriorModel> {
		public Monkey(EntityRendererProvider.Context context) {
			super(context, new MonkeyWarriorModel(context.bakeLayer(MonkeyWarriorModel.LAYER)), 0.5f, "monkey_warrior");
		}
	}

	/** El long: mide unos ocho bloques, así que no se oculta cuando su caja (la del jinete) queda fuera de la vista. */
	public static class Dragon extends Glowing<LongDragon, LongDragonModel> {
		public Dragon(EntityRendererProvider.Context context) {
			super(context, new LongDragonModel(context.bakeLayer(LongDragonModel.LAYER)), 1.2f, "long_dragon");
		}

		@Override
		protected boolean affectedByCulling(LongDragon entity) {
			return false;
		}
	}

	/** Pegaso: la brida de oro brilla un poco; el paso de suelo a vuelo se suaviza. */
	public static class PegasusRenderer extends Glowing<Pegasus, PegasusModel> {
		public static class State extends LivingEntityRenderState {
			public float air;
		}

		public PegasusRenderer(EntityRendererProvider.Context context) {
			super(context, new PegasusModel(context.bakeLayer(PegasusModel.LAYER)), 1.0f, "pegasus");
		}

		@Override
		public LivingEntityRenderState createRenderState() {
			return new State();
		}

		@Override
		public void extractRenderState(Pegasus pegasus, LivingEntityRenderState state, float partialTick) {
			super.extractRenderState(pegasus, state, partialTick);
			if (state instanceof State s) {
				s.air = Mth.lerp(partialTick, pegasus.airAnimO, pegasus.airAnim);
			}
		}
	}

	/** Talos: hombre de bronce de unos 3 bloques (geometría a 1,5); ojos y vena de ícor que brillan. */
	public static class TalosRenderer extends Glowing<Talos, TalosModel> {
		public static final float SCALE = 1.5f;

		public static class State extends LivingEntityRenderState {
			public float sinceThrow = 100.0f;
		}

		public TalosRenderer(EntityRendererProvider.Context context) {
			super(context, new TalosModel(context.bakeLayer(TalosModel.LAYER)), 1.0f, "talos");
		}

		@Override
		public LivingEntityRenderState createRenderState() {
			return new State();
		}

		@Override
		public void extractRenderState(Talos talos, LivingEntityRenderState state, float partialTick) {
			super.extractRenderState(talos, state, partialTick);
			if (state instanceof State s) {
				s.sinceThrow = talos.ticksSinceThrow() + partialTick;
			}
		}

		@Override
		protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
			poseStack.scale(SCALE, SCALE, SCALE);
		}
	}

	/** Huginn y Muninn: cuervos de Odín (los ojos brillan). */
	public static class RavenRenderer extends Glowing<OdinRaven, OdinRavenModel> {
		public RavenRenderer(EntityRendererProvider.Context context) {
			super(context, new OdinRavenModel(context.bakeLayer(OdinRavenModel.LAYER)), 0.3f, "odin_raven");
		}

		@Override
		public void extractRenderState(OdinRaven raven, LivingEntityRenderState state, float partialTick) {
			super.extractRenderState(raven, state, partialTick);
			// Posado: alas plegadas (el modelo lo lee de la velocidad de "caminar").
			state.walkAnimationSpeed = raven.isPerched() ? 0.0f : 1.0f;
		}
	}
}
