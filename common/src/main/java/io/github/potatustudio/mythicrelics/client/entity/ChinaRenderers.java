package io.github.potatustudio.mythicrelics.client.entity;

import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.entity.LongDragon;
import io.github.potatustudio.mythicrelics.entity.MonkeyWarrior;
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
 * Renderizadores de las entidades chinas. Las dos tienen una segunda textura que brilla en la oscuridad (como los ojos
 * de la araña): los ojos de fuego del mono; los ojos del long y su perla llameante.
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
}
