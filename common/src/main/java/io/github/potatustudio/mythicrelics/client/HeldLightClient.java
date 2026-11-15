package io.github.potatustudio.mythicrelics.client;

import dev.architectury.event.events.client.ClientTickEvent;
import io.github.potatustudio.mythicrelics.registry.ModItems;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

/**
 * Luz en la mano del hinojo de Prometeo, del lado del cliente: cada jugador cercano que lo sostiene lleva un bloque de
 * luz invisible (nivel 15) a la altura de los ojos, puesto solo en el mundo del cliente. Así la luz sigue al jugador en
 * el mismo cuadro, sin esperar al servidor (antes iba por el servidor y se notaba a saltos). En el agua va anegado; si a
 * la altura de los ojos hay algas u otra cosa, prueba los bloques de al lado. La luz nueva se pone antes de devolver el
 * bloque anterior, para que no haya un instante a oscuras. Es solo visual: no cambia dónde aparecen los monstruos.
 */
public final class HeldLightClient {
	private static final double RANGE = 64.0;

	/** Dónde quedó la luz de cada jugador y qué bloque había ahí antes. */
	private record Placed(BlockPos pos, BlockState original) {
	}

	private static final Map<UUID, Placed> PLACED = new HashMap<>();
	private static ClientLevel lastLevel;

	private HeldLightClient() {
	}

	public static void init() {
		ClientTickEvent.CLIENT_POST.register(HeldLightClient::tick);
	}

	private static boolean holdsLight(Player player) {
		return player.isAlive() && !player.isSpectator()
				&& (player.getMainHandItem().is(ModItems.PROMETHEUS_FENNEL.get()) || player.getOffhandItem().is(ModItems.PROMETHEUS_FENNEL.get()));
	}

	private static void tick(Minecraft mc) {
		ClientLevel level = mc.level;
		if (level != lastLevel) {
			PLACED.clear();   // otro mundo o dimensión: lo puesto en el anterior ya no existe
			lastLevel = level;
		}
		if (level == null || mc.player == null) {
			return;
		}
		// Jugadores que ya no lo sostienen o se fueron: se les devuelve el bloque.
		Iterator<Map.Entry<UUID, Placed>> it = PLACED.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Placed> e = it.next();
			Player p = level.getPlayerByUUID(e.getKey());
			if (p == null || !holdsLight(p) || p.distanceToSqr(mc.player) > RANGE * RANGE) {
				restore(level, e.getValue());
				it.remove();
			}
		}
		for (Player p : level.players()) {
			if (holdsLight(p) && p.distanceToSqr(mc.player) <= RANGE * RANGE) {
				follow(level, p);
			}
		}
	}

	private static void follow(ClientLevel level, Player player) {
		Placed last = PLACED.get(player.getUUID());
		BlockPos eyes = BlockPos.containing(player.getEyePosition());
		BlockPos feet = player.blockPosition();
		BlockPos[] candidates = {eyes, feet.equals(eyes) ? eyes.below() : feet, eyes.above(), eyes.north(), eyes.south(),
				eyes.east(), eyes.west(), feet.north(), feet.south(), feet.east(), feet.west()};
		for (BlockPos pos : candidates) {
			if (last != null && last.pos().equals(pos) && level.getBlockState(pos).is(Blocks.LIGHT)) {
				return;   // ya está en el mejor lugar
			}
			BlockState here = level.getBlockState(pos);
			boolean water = here.is(Blocks.WATER) && level.getFluidState(pos).isSourceOfType(Fluids.WATER);
			if (!here.isAir() && !water) {
				continue;
			}
			level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15)
					.setValue(BlockStateProperties.WATERLOGGED, water), Block.UPDATE_ALL_IMMEDIATE);
			PLACED.put(player.getUUID(), new Placed(pos.immutable(), here));
			if (last != null && !last.pos().equals(pos)) {
				restore(level, last);
			}
			return;
		}
	}

	/** Devuelve el bloque que había, si la luz sigue ahí (el servidor pudo haber cambiado ese bloque mientras tanto). */
	private static void restore(ClientLevel level, Placed placed) {
		if (level.getBlockState(placed.pos()).is(Blocks.LIGHT)) {
			level.setBlock(placed.pos(), placed.original(), Block.UPDATE_ALL_IMMEDIATE);
		}
	}
}
