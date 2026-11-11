package io.github.potatustudio.mythicrelics.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.CelestialDog;
import io.github.potatustudio.mythicrelics.entity.Kitsune;
import io.github.potatustudio.mythicrelics.entity.LongDragon;
import io.github.potatustudio.mythicrelics.entity.MonkeyWarrior;
import io.github.potatustudio.mythicrelics.entity.Qilin;
import io.github.potatustudio.mythicrelics.entity.UrashimaTurtle;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

/**
 * Renderizadores de las entidades de China y Japón. Todas tienen una segunda textura que brilla en la oscuridad (como los ojos
 * de la araña): los ojos de fuego del mono; los ojos del long y su perla llameante; los ojos del perro; las llamas del
 * qilin.
 */
public final class ChinaRenderers {
	private ChinaRenderers() {
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

	/** Perro Celestial. */
	public static class Dog extends Glowing<CelestialDog, CelestialDogModel> {
		public Dog(EntityRendererProvider.Context context) {
			super(context, new CelestialDogModel(context.bakeLayer(CelestialDogModel.LAYER)), 0.5f, "celestial_dog");
		}
	}

	/** Qilin: se dibuja más grande que su geometría, del tamaño de un caballo. */
	public static class QilinRenderer extends Glowing<Qilin, QilinModel> {
		public static final float SCALE = 1.35f;

		public QilinRenderer(EntityRendererProvider.Context context) {
			super(context, new QilinModel(context.bakeLayer(QilinModel.LAYER)), 0.9f, "qilin");
		}

		@Override
		protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
			poseStack.scale(SCALE, SCALE, SCALE);
		}
	}

	/** Kitsune de nueve colas: del tamaño de un lobo grande. */
	public static class KitsuneRenderer extends Glowing<Kitsune, KitsuneModel> {
		public static final float SCALE = 1.5f;

		public KitsuneRenderer(EntityRendererProvider.Context context) {
			super(context, new KitsuneModel(context.bakeLayer(KitsuneModel.LAYER)), 0.6f, "kitsune");
		}

		@Override
		protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
			poseStack.scale(SCALE, SCALE, SCALE);
		}
	}

	/** Tortuga de Urashima: tortuga marina gigante. */
	public static class TurtleRenderer extends Glowing<UrashimaTurtle, UrashimaTurtleModel> {
		public static final float SCALE = 1.7f;

		public TurtleRenderer(EntityRendererProvider.Context context) {
			super(context, new UrashimaTurtleModel(context.bakeLayer(UrashimaTurtleModel.LAYER)), 1.2f, "urashima_turtle");
		}

		@Override
		protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
			poseStack.scale(SCALE, SCALE, SCALE);
		}
	}
}
