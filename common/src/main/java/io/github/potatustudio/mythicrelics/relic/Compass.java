package io.github.potatustudio.mythicrelics.relic;

import net.minecraft.network.chat.Component;

/** Rumbo de la rosa de 8 vientos («al noreste») para los avisos que señalan hacia algo. */
public final class Compass {
	private static final String[] DIRECTIONS = {"south", "southwest", "west", "northwest", "north", "northeast", "east", "southeast"};

	private Compass() {
	}

	/** Rumbo hacia un punto a {@code dx}, {@code dz} bloques. El ángulo parte del sur (+Z) y gira al oeste, como el yaw. */
	public static Component direction(double dx, double dz) {
		int sector = (int) Math.floorMod(Math.round(Math.toDegrees(Math.atan2(-dx, dz)) / 45.0), 8);
		return Component.translatable("direction.mythicrelics." + DIRECTIONS[sector]);
	}
}
