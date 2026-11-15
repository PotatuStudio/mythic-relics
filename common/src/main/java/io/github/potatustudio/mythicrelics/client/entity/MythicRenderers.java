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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.Map;
import java.util.WeakHashMap;

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
		/** Estado con el avance del golpe (0 a 1), para encorvarse y hacer girar el bastón. */
		public static class State extends LivingEntityRenderState {
			public float attack;
		}

		public Monkey(EntityRendererProvider.Context context) {
			super(context, new MonkeyWarriorModel(context.bakeLayer(MonkeyWarriorModel.LAYER)), 0.5f, "monkey_warrior");
		}

		@Override
		public LivingEntityRenderState createRenderState() {
			return new State();
		}

		@Override
		public void extractRenderState(MonkeyWarrior monkey, LivingEntityRenderState state, float partialTick) {
			super.extractRenderState(monkey, state, partialTick);
			if (state instanceof State s) {
				s.attack = monkey.getSwingAnimation(partialTick);
			}
		}
	}

	/** El long: mide unos ocho bloques, así que no se oculta cuando su caja (la del jinete) queda fuera de la vista. */
	public static class Dragon extends Glowing<LongDragon, LongDragonModel> {
		/** Altura (en bloques) desde la que las patas empiezan a bajar y las garras a abrirse para posarse. */
		private static final double LANDING_REACH = 3.0;

		/** Estado con lo cerca que está del suelo: 0 en el aire, 1 apoyado (sube suave al acercarse). */
		public static class State extends LivingEntityRenderState {
			public float landing;
		}

		public Dragon(EntityRendererProvider.Context context) {
			super(context, new LongDragonModel(context.bakeLayer(LongDragonModel.LAYER)), 1.5f, "long_dragon");
		}

		@Override
		public LivingEntityRenderState createRenderState() {
			return new State();
		}

		@Override
		public void extractRenderState(LongDragon dragon, LivingEntityRenderState state, float partialTick) {
			super.extractRenderState(dragon, state, partialTick);
			if (state instanceof State s) {
				// Rayo hacia abajo desde la posición interpolada: la distancia al suelo cambia de a poco, así que las garras
				// se abren sin saltos a medida que baja.
				Vec3 from = dragon.getPosition(partialTick);
				Vec3 to = from.subtract(0.0, LANDING_REACH, 0.0);
				var hit = dragon.level().clip(new ClipContext(from.add(0.0, 0.1, 0.0), to, ClipContext.Block.COLLIDER,
						ClipContext.Fluid.NONE, dragon));
				double distance = hit.getType() == HitResult.Type.MISS ? LANDING_REACH : from.y - hit.getLocation().y;
				s.landing = (float) Mth.clamp(1.0 - distance / LANDING_REACH, 0.0, 1.0);
			}
		}

		/** Un 30 % más grande que el modelo (proposals/dragon.py). */
		public static final float SCALE = 1.3f;

		@Override
		protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
			poseStack.scale(SCALE, SCALE, SCALE);
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
		public static final float SCALE = 2.2f;

		/** El bronce al rojo: la textura del cuerpo, sin sombra y teñida de naranja encendido, encima del modelo. */
		private static final RenderType RED_HOT = RenderTypes.eyes(MythicRelics.id("textures/entity/talos.png"));
		/** Segunda pasada de la vena de ícor y los ojos, para que se vean por encima del rojo y brillen más. */
		private static final RenderType ICHOR = RenderTypes.eyes(MythicRelics.id("textures/entity/talos_glow.png"));

		public static class State extends LivingEntityRenderState {
			public float sinceThrow = 100.0f;
			/** Abrazo: 0 en reposo, sube y baja suave (seno al cuadrado) a lo largo del golpe cuerpo a cuerpo. */
			public float hug;
		}

		public TalosRenderer(EntityRendererProvider.Context context) {
			super(context, new TalosModel(context.bakeLayer(TalosModel.LAYER)), 1.0f, "talos");
			// Al abrazar, todo el bronce se pone al rojo (más intenso cuanto más cerrado el abrazo) y la vena y los ojos
			// vuelven a dibujarse encima, así brillan más que el resto.
			addLayer(new net.minecraft.client.renderer.entity.layers.RenderLayer<>(this) {
				@Override
				public void submit(PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector collector, int light,
						LivingEntityRenderState state, float yRot, float xRot) {
					if (!(state instanceof State s) || s.hug < 0.01f) {
						return;
					}
					int hot = net.minecraft.util.ARGB.color(Math.round(170 * s.hug), 255, 96, 36);
					int ichor = net.minecraft.util.ARGB.color(Math.round(255 * s.hug), 255, 236, 170);
					int none = net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;
					collector.order(2).submitModel(getParentModel(), state, poseStack, RED_HOT, light, none, hot, null, state.outlineColor);
					collector.order(3).submitModel(getParentModel(), state, poseStack, ICHOR, light, none, ichor, null, state.outlineColor);
				}
			});
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
				// El abrazo usa el golpe de MeleeAttackGoal (swingForAttack, que llega al cliente): 0 → 1 → 0 sin saltos.
				float swing = Mth.sin(talos.getSwingAnimation(partialTick) * Mth.PI);
				s.hug = swing * swing;
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

		/** El modelo nuevo es más largo (alas en dos tramos): se dibuja al 80 % (proposals/raven.py). */
		public static final float SCALE = 0.8f;

		@Override
		protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
			poseStack.scale(SCALE, SCALE, SCALE);
		}

		/** Ticks que tarda más o menos en plegar o abrir las alas al posarse o despegar. */
		private static final float FOLD_TICKS = 6.0f;

		/** Estado con el plegado de las alas: 0 en vuelo, 1 posado, suavizado entre medio. */
		public static class State extends LivingEntityRenderState {
			public float fold;
		}

		/** Plegado suavizado por cuervo: {valor, último instante}. Se olvida solo cuando el cuervo desaparece. */
		private final Map<OdinRaven, float[]> folds = new WeakHashMap<>();

		@Override
		public LivingEntityRenderState createRenderState() {
			return new State();
		}

		@Override
		public void extractRenderState(OdinRaven raven, LivingEntityRenderState state, float partialTick) {
			super.extractRenderState(raven, state, partialTick);
			// Posado: alas plegadas (el modelo lo lee de la velocidad de "caminar").
			state.walkAnimationSpeed = raven.isPerched() ? 0.0f : 1.0f;
			if (state instanceof State s) {
				// El plegado se acerca al objetivo con una curva exponencial: al posarse la mano se recoge de a poco, sin
				// saltar de golpe de la pose de vuelo a la de posado.
				float now = raven.tickCount + partialTick;
				float target = raven.isPerched() ? 1.0f : 0.0f;
				float[] fold = folds.computeIfAbsent(raven, r -> new float[] {target, now});
				float dt = Mth.clamp(now - fold[1], 0.0f, 20.0f);
				fold[0] += (target - fold[0]) * (1.0f - (float) Math.exp(-dt / FOLD_TICKS));
				fold[1] = now;
				s.fold = fold[0];
			}
		}
	}
}
