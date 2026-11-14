package io.github.potatustudio.mythicrelics.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.events.client.ClientPlayerEvent;
import io.github.potatustudio.mythicrelics.client.PetrifiedClient;
import io.github.potatustudio.mythicrelics.entity.StoneStatue;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * Estatua de piedra: dibuja el modelo del mob que guarda, quieto y con textura de piedra. Usa una copia del mob que no está
 * en el mundo (una por tipo), puesta en la posición y el giro de la estatua.
 */
public class StoneStatueRenderer extends EntityRenderer<StoneStatue, StoneStatueRenderer.State> {
	/** Estado de la estatua: el estado del mob que se dibuja. */
	public static class State extends EntityRenderState {
		@Nullable EntityRenderState mob;
	}

	/**
	 * Un modelo por tipo de mob, compartido por todas las estatuas (y por los renderers que se crean al recargar los
	 * recursos). Se vacía al desconectarse, para no retener el mundo del cliente que se cerró.
	 */
	private static final Map<EntityType<?>, Entity> models = new HashMap<>();
	private static @Nullable Level modelsLevel;

	static {
		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
			models.clear();
			modelsLevel = null;
		});
	}

	public StoneStatueRenderer(EntityRendererProvider.Context context) {
		super(context);
		shadowRadius = 0.4f;
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	private static int nextModelId = -100_000;

	private @Nullable Entity model(StoneStatue statue) {
		EntityType<?> type = statue.getMob();
		if (type == null) {
			return null;
		}
		if (modelsLevel != statue.level()) {
			models.clear();
			modelsLevel = statue.level();
		}
		return models.computeIfAbsent(type, t -> {
			Entity e = t.create(statue.level(), EntitySpawnReason.LOAD);
			if (e != null) {
				// No entra al mundo, así que no tiene ID y el render vanilla lo pide: uno negativo propio, sin choques.
				e.setId(nextModelId--);
				PetrifiedClient.markStatueModel(e);
			}
			return e;
		});
	}

	@Override
	public void extractRenderState(StoneStatue statue, State state, float partialTick) {
		super.extractRenderState(statue, state, partialTick);
		Entity mob = model(statue);
		state.mob = null;
		if (mob == null) {
			return;
		}
		mob.setPos(statue.getX(), statue.getY(), statue.getZ());
		mob.setOldPosAndRot();
		float yaw = statue.getYRot();
		mob.setYRot(yaw);
		mob.yRotO = yaw;
		mob.setXRot(0);
		if (mob instanceof LivingEntity living) {
			living.yBodyRot = yaw;
			living.yBodyRotO = yaw;
			living.yHeadRot = yaw;
			living.yHeadRotO = yaw;
		}
		state.mob = Minecraft.getInstance().getEntityRenderDispatcher().extractEntity(mob, 1.0f);
		state.mob.lightCoords = state.lightCoords;
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.mob != null) {
			Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(state.mob).submit(state.mob, poseStack, collector, camera);
		}
	}
}
