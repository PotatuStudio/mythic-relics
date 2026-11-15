package io.github.potatustudio.mythicrelics.relic;

import dev.architectury.event.events.common.TickEvent;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

/**
 * Luz en la mano (hinojo de Prometeo): mientras lo sostienes, un bloque de luz invisible (el {@code minecraft:light} de
 * vanilla, nivel 15) te sigue a la altura de los ojos; en el agua va anegado, así que alumbra también sumergido. Solo se
 * pone en aire o en agua quieta: si a la altura de los ojos hay pasto marino, algas u otra cosa, prueba los bloques de al
 * lado, para no apagarse nunca. Se mueve cada tick y la luz nueva se pone ANTES de quitar la anterior, para que no haya un
 * instante a oscuras. Al soltarlo, desconectarte o cerrar el mundo se quita y deja lo que había.
 */
public final class HeldLight {
	private static final Map<UUID, GlobalPos> PLACED = new HashMap<>();

	private HeldLight() {
	}

	public static void init() {
		TickEvent.SERVER_POST.register(HeldLight::tick);
		ServerState.onQuit(player -> remove(player.level().getServer(), PLACED.remove(player.getUUID())));
		ServerState.onStopping(server -> {
			PLACED.values().forEach(pos -> remove(server, pos));
			PLACED.clear();
		});
	}

	private static boolean holdsLight(ServerPlayer player) {
		return player.isAlive() && !player.isSpectator()
				&& (player.getMainHandItem().is(ModItems.PROMETHEUS_FENNEL.get()) || player.getOffhandItem().is(ModItems.PROMETHEUS_FENNEL.get()));
	}

	/** Dónde probar poner la luz, en orden: los ojos, los pies, sobre la cabeza y los costados de los ojos. */
	private static BlockPos[] candidates(ServerPlayer player) {
		BlockPos eyes = BlockPos.containing(player.getEyePosition());
		BlockPos feet = player.blockPosition();
		return new BlockPos[] {eyes, feet.equals(eyes) ? eyes.below() : feet, eyes.above(), eyes.north(), eyes.south(), eyes.east(),
				eyes.west(), feet.north(), feet.south(), feet.east(), feet.west()};
	}

	private static void tick(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			GlobalPos last = PLACED.get(player.getUUID());
			if (!holdsLight(player)) {
				if (last != null) {
					remove(server, PLACED.remove(player.getUUID()));
				}
				continue;
			}
			ServerLevel level = player.level();
			boolean sameLevel = last != null && last.dimension() == level.dimension();
			for (BlockPos pos : candidates(player)) {
				// La luz ya está en el mejor lugar posible: no se toca.
				if (sameLevel && last.pos().equals(pos) && level.getBlockState(pos).is(Blocks.LIGHT)) {
					break;
				}
				if (place(level, pos)) {
					PLACED.put(player.getUUID(), GlobalPos.of(level.dimension(), pos.immutable()));
					if (!(sameLevel && last.pos().equals(pos))) {
						remove(server, last);   // después de poner la nueva: sin parpadeo
					}
					break;
				}
			}
		}
	}

	private static boolean place(ServerLevel level, BlockPos pos) {
		BlockState here = level.getBlockState(pos);
		boolean water = here.is(Blocks.WATER) && level.getFluidState(pos).isSourceOfType(Fluids.WATER);
		if (!here.isAir() && !water) {
			return false;
		}
		level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15)
				.setValue(BlockStateProperties.WATERLOGGED, water), Block.UPDATE_ALL);
		return true;
	}

	private static void remove(@Nullable MinecraftServer server, @Nullable GlobalPos pos) {
		if (server == null || pos == null) {
			return;
		}
		ServerLevel level = server.getLevel(pos.dimension());
		if (level == null || !level.isLoaded(pos.pos())) {
			return;
		}
		BlockState state = level.getBlockState(pos.pos());
		if (state.is(Blocks.LIGHT)) {
			level.setBlock(pos.pos(), state.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState()
					: Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
		}
	}

	// Ciclo de vida: lo que queda puesto se quita al desconectarse y al cerrar (ver init).
	static {
		ServerState.clearOnStop(PLACED);
	}
}
