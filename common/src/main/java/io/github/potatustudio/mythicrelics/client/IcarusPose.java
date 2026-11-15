package io.github.potatustudio.mythicrelics.client;

import com.mojang.serialization.MapCodec;
import io.github.potatustudio.mythicrelics.MythicRelics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Propiedad de modelo {@code mythicrelics:icarus_pose}: qué pose tienen las alas de Ícaro puestas, según cómo se mueve
 * quien las lleva (sirve igual para el jugador local y para los demás, porque solo mira su posición tick a tick).
 * <ul>
 *   <li>0: plegadas (en el suelo, en el agua o trepando);</li>
 *   <li>1 a 4: aleteo, un cuadro cada 2 ticks, mientras sube (al saltar o aletear);</li>
 *   <li>5: abiertas, planeando (bajando despacio);</li>
 *   <li>6: echadas atrás, en picada (cayendo rápido).</li>
 * </ul>
 * items/icarus_wings.json elige el modelo con un range_dispatch (worn_models.py, ICARUS_POSES). Cada loader la registra
 * (Fabric en el ID_MAPPER del juego, NeoForge con su evento).
 */
public record IcarusPose() implements RangeSelectItemModelProperty {
	public static final Identifier ID = MythicRelics.id("icarus_pose");
	public static final MapCodec<IcarusPose> MAP_CODEC = MapCodec.unit(new IcarusPose());

	@Override
	public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		LivingEntity entity = owner == null ? null : owner.asLivingEntity();
		if (entity == null || entity.onGround() || entity.isInWater() || entity.onClimbable() || entity.isPassenger()) {
			return 0;
		}
		double dy = entity.getY() - entity.yo;
		if (dy > 0.05) {
			return 1 + (entity.tickCount / 2) % 4;
		}
		return dy > -0.35 ? 5 : 6;
	}

	@Override
	public MapCodec<IcarusPose> type() {
		return MAP_CODEC;
	}
}
