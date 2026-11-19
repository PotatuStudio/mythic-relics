package io.github.potatustudio.mythicrelics.relic;

/** Cuentas de experiencia de vanilla (la paleta de Thot). Sin dependencias del juego, para poder probarlas solas. */
public final class XpMath {
	private XpMath() {
	}

	/** Puntos de experiencia de un nivel (de {@code level} a {@code level + 1}), la misma fórmula de vanilla. */
	public static int pointsForLevel(int level) {
		return level >= 30 ? 112 + (level - 30) * 9 : level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
	}

	/** Nivel al que llegarías desde cero con esos puntos (para mostrar lo escrito en la paleta). */
	public static int levelForPoints(int points) {
		int level = 0;
		while (points >= pointsForLevel(level)) {
			points -= pointsForLevel(level);
			level++;
		}
		return level;
	}
}
