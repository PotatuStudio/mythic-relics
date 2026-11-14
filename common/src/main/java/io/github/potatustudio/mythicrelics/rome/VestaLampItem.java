package io.github.potatustudio.mythicrelics.rome;

import io.github.potatustudio.mythicrelics.egypt.RelicItem;
import io.github.potatustudio.mythicrelics.relic.RelicCooldowns;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;

/**
 * Lámpara de Vesta. Clic derecho sobre un bloque: enciende el fuego del hogar (una fogata) que se apaga sola a los
 * {@link RomeRelics#VESTA_FIRE_TICKS} ticks; así la Regeneración de la lámpara sirve en cualquier lugar. El resto de sus
 * efectos (hambre a la mitad, no congelarse) funcionan siempre en la mano secundaria.
 */
public class VestaLampItem extends RelicItem {
	private record Hearth(ServerLevel level, BlockPos pos, long until) {
	}

	private static final List<Hearth> HEARTHS = new ArrayList<>();

	public VestaLampItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!(context.getLevel() instanceof ServerLevel level) || !(context.getPlayer() instanceof ServerPlayer player)) {
			return InteractionResult.SUCCESS;
		}
		BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
		if (!level.getBlockState(pos).canBeReplaced() || !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(),
				net.minecraft.core.Direction.UP)) {
			return InteractionResult.FAIL;
		}
		if (!RelicCooldowns.tryStart(player, "vesta_lamp", RomeRelics.VESTA_FIRE_COOLDOWN, BuiltInRegistries.ITEM.getKey(this))) {
			return InteractionResult.PASS;
		}
		level.setBlockAndUpdate(pos, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, true));
		HEARTHS.add(new Hearth(level, pos.immutable(), level.getGameTime() + RomeRelics.VESTA_FIRE_TICKS));
		level.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.3, 0.3, 0.3, 0.02);
		level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8f, 0.8f);
		return InteractionResult.SUCCESS;
	}

	/** Apaga los fuegos del hogar que ya cumplieron su tiempo (si siguen ahí). */
	static void tick(MinecraftServer server) {
		long now = server.overworld().getGameTime();
		Iterator<Hearth> it = HEARTHS.iterator();
		while (it.hasNext()) {
			Hearth h = it.next();
			if (h.until() > now) {
				continue;
			}
			if (h.level().getBlockState(h.pos()).is(Blocks.CAMPFIRE)) {
				h.level().removeBlock(h.pos(), false);
				h.level().sendParticles(ParticleTypes.LARGE_SMOKE, h.pos().getX() + 0.5, h.pos().getY() + 0.4, h.pos().getZ() + 0.5, 10,
						0.2, 0.2, 0.2, 0.01);
			}
			it.remove();
		}
	}

	// Ciclo de vida: qué se olvida al desconectarse un jugador y al cerrar el mundo (relic/ServerState).
	static {
		io.github.potatustudio.mythicrelics.relic.ServerState.onStopping(server -> {
			for (Hearth h : HEARTHS) {
				if (h.level().getBlockState(h.pos()).is(Blocks.CAMPFIRE)) {
					h.level().removeBlock(h.pos(), false);
				}
			}
			HEARTHS.clear();
		});
	}
}
