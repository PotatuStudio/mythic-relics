package io.github.potatustudio.mythicrelics.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.client.entity.FreyjaFalconModel;
import io.github.potatustudio.mythicrelics.norse.NorseSagas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Forma de halcón del manto de Freyja: en lugar del jugador (y de todo lo que lleva encima: capa, armadura, reliquias,
 * objetos en la mano) se dibuja el halcón, con el mismo modelo y textura que tenía el halcón que acompañaba. El jugador
 * ya está encogido por el atributo de tamaño, así que la caja de golpe y la cámara coinciden con el ave.
 */
public final class FalconForm {
	/** Tamaño del ave (la geometría del halcón, a 1,6). */
	private static final float SCALE = 1.3f;
	private static final RenderType BODY = RenderTypes.entityCutout(MythicRelics.id("textures/entity/freyja_falcon.png"));
	private static final RenderType GLOW = RenderTypes.eyes(MythicRelics.id("textures/entity/freyja_falcon_glow.png"));

	private static @Nullable FreyjaFalconModel model;

	/**
	 * Cómo vuela el halcón, para la animación: {@code dive} (picada, 0 a 1), {@code ground} (apoyado en el suelo) y
	 * {@code climb} (subiendo: bate siempre, sin planear). Los tres van suavizados para que las alas no salten.
	 */
	public record Flight(float dive, float ground, float climb) {
	}

	/** Velocidad vertical (bloques por tick) desde la que empieza la picada y en la que es completa. */
	private static final float DIVE_START = 0.12f;
	private static final float DIVE_FULL = 0.28f;
	/** Ticks que tardan los valores en acercarse al objetivo (curva exponencial). */
	private static final float EASE_TICKS = 4.0f;
	/** Valores suavizados por jugador: {picada, suelo, subida, último instante}. */
	private static final Map<UUID, float[]> EASED = new HashMap<>();
	/**
	 * Vuelo de cada estado dibujado en este cuadro: el modelo lo lee en setupAnim, que puede correr más tarde (al
	 * vaciar la cola de dibujo) y una vez por estado aunque haya varios halcones a la vez.
	 */
	private static final Map<LivingEntityRenderState, Flight> FLIGHTS = new WeakHashMap<>();

	private FalconForm() {
	}

	static {
		// Al salir del mundo se olvidan los halcones de esa partida.
		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> EASED.clear());
	}

	/** Lo llama el renderer del jugador al crearse (ahí está el contexto para armar el modelo). */
	public static void bake(EntityRendererProvider.Context context) {
		model = new FreyjaFalconModel(context.bakeLayer(FreyjaFalconModel.LAYER));
	}

	/** Dibuja el halcón con la pose del jugador. Devuelve false si el modelo aún no existe (y se dibuja al jugador). */
	public static boolean submit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
		FreyjaFalconModel m = model;
		if (m == null) {
			return false;
		}
		FLIGHTS.put(state, measure(state));
		poseStack.pushPose();
		// Como LivingEntityRenderer: gira con el cuerpo, voltea el modelo y lo apoya en los pies.
		poseStack.rotateDegrees(Axis.YP, 180.0f - state.bodyRot);
		poseStack.scale(-SCALE, -SCALE, SCALE);
		poseStack.translate(0.0f, -1.501f, 0.0f);
		m.setupAnim(state);
		collector.submitModel(m, state, poseStack, BODY, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		collector.submitModel(m, state, poseStack, GLOW, 0xF000F0, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
		return true;
	}

	/** El vuelo calculado para este estado (null si no es un halcón dibujado por {@link #submit}). */
	public static @Nullable Flight flight(LivingEntityRenderState state) {
		return FLIGHTS.get(state);
	}

	/**
	 * Busca al jugador halcón que corresponde al estado (el más cercano a su posición) y suaviza su movimiento: bajar
	 * rápido es picada, subir es batir sin planear y estar en el suelo pliega las alas.
	 */
	private static Flight measure(LivingEntityRenderState state) {
		var level = Minecraft.getInstance().level;
		if (level == null) {
			return new Flight(0.0f, 0.0f, 0.0f);
		}
		Player found = null;
		double best = 4.0;
		for (Player player : level.players()) {
			double d = player.distanceToSqr(state.x, state.y, state.z);
			if (d < best && NorseSagas.isFalconForm(player)) {
				best = d;
				found = player;
			}
		}
		if (found == null) {
			return new Flight(0.0f, 0.0f, 0.0f);
		}
		float vy = (float) (found.getY() - found.yo);
		float diveTarget = Mth.clamp((-vy - DIVE_START) / (DIVE_FULL - DIVE_START), 0.0f, 1.0f);
		float groundTarget = found.onGround() ? 1.0f : 0.0f;
		float climbTarget = Mth.clamp(vy / 0.1f, 0.0f, 1.0f);
		float now = state.ageInTicks;
		float[] eased = EASED.computeIfAbsent(found.getUUID(), id -> new float[] {diveTarget, groundTarget, climbTarget, now});
		float dt = Mth.clamp(now - eased[3], 0.0f, 20.0f);
		float k = 1.0f - (float) Math.exp(-dt / EASE_TICKS);
		eased[0] += (diveTarget - eased[0]) * k;
		eased[1] += (groundTarget - eased[1]) * k;
		eased[2] += (climbTarget - eased[2]) * k;
		eased[3] = now;
		return new Flight(eased[0], eased[1], eased[2]);
	}
}
