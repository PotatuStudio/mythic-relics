package io.github.potatustudio.mythicrelics.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

/**
 * Modelo 3D de un arma que vuela (geometría en {@code client/entity/geometry}, generada por tools/model-gen/weapon_entities.py).
 * Como el tridente vanilla: en el inventario y en la mano se ve el ícono, pero en el aire se dibuja este modelo, sin los
 * destellos de adorno del ícono. La punta mira a -y (como la del tridente) y el origen queda cerca del centro de la
 * pieza. Cada arma tiene su textura y una capa que brilla.
 */
public class WeaponModel extends Model<Unit> {
	public static final ModelLayerLocation FREYR_SWORD = layer("freyr_sword");
	public static final ModelLayerLocation MJOLNIR = layer("mjolnir");
	public static final ModelLayerLocation GUNGNIR = layer("gungnir");
	public static final ModelLayerLocation MISTLETOE_DART = layer("mistletoe_dart");

	private final RenderType solid;
	private final RenderType glow;

	public WeaponModel(ModelPart root, String name) {
		super(root, RenderTypes::entityCutout);
		Identifier base = MythicRelics.id("textures/entity/weapon/" + name + ".png");
		solid = RenderTypes.entityCutout(base);
		glow = RenderTypes.eyes(MythicRelics.id("textures/entity/weapon/" + name + "_glow.png"));
	}

	private static ModelLayerLocation layer(String name) {
		return new ModelLayerLocation(MythicRelics.id("weapon/" + name), "main");
	}

	/** Dibuja el arma con su luz y, encima, lo que brilla (a luz plena). */
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, int outlineColor) {
		collector.submitModel(this, Unit.INSTANCE, poseStack, solid, light, OverlayTexture.NO_OVERLAY, outlineColor);
		collector.submitModel(this, Unit.INSTANCE, poseStack, glow, 0xF000F0, OverlayTexture.NO_OVERLAY, 0);
	}
}
