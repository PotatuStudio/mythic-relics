package io.github.potatustudio.mythicrelics.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Skíðblaðnir desplegado: un bote con el casco, la proa de dragón, el mástil y la vela del barco de Freyr. No suelta
 * nada al romperse (la reliquia sigue en el inventario) y no se guarda con el mundo (se pliega al quedar vacío).
 */
public class SkidbladnirBoat extends Boat {
	public SkidbladnirBoat(EntityType<? extends Boat> type, Level level) {
		super(type, level, () -> Items.AIR);
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}
}
