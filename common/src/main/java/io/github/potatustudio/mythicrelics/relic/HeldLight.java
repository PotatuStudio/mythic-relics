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
 * pone en aire o en agua quieta, y al moverte, soltarlo, desconectarte o cerrar el mundo se quita y deja lo que había.
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

	private static void tick(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if ((player.tickCount + player.getId()) % 2 != 0) {
				continue;
			}
			GlobalPos last = PLACED.get(player.getUUID());
			if (!holdsLight(player)) {
				if (last != null) {
					remove(server, PLACED.remove(player.getUUID()));
				}
				continue;
			}
			ServerLevel level = player.level();
			BlockPos eyes = BlockPos.containing(player.getEyePosition());
			if (last != null && last.dimension() == level.dimension() && last.pos().equals(eyes)
					&& level.getBlockState(eyes).is(Blocks.LIGHT)) {
				continue;
			}
			remove(server, last);
			PLACED.remove(player.getUUID());
			for (BlockPos pos : new BlockPos[] {eyes, player.blockPosition()}) {
				if (place(level, pos)) {
					PLACED.put(player.getUUID(), GlobalPos.of(level.dimension(), pos.immutable()));
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
