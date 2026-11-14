package io.github.potatustudio.mythicrelics.relic;

/** Formato de los números que muestran los tooltips (%1$s, %2$s…). */
public final class TooltipNumbers {
	private TooltipNumbers() {
	}

	/** Número sin ".0" cuando es entero (3 en vez de 3.0); con decimales usa punto en todos los idiomas. */
	public static String fmt(double value) {
		return value == Math.floor(value) ? Integer.toString((int) value) : Double.toString(value);
	}

	/** Como {@link #fmt(double)}, pero redondeado a un decimal. */
	public static String fmtTenths(double value) {
		return value == Math.floor(value) ? Integer.toString((int) value) : Double.toString(Math.round(value * 10) / 10.0);
	}

	/** Nivel de efecto como en vanilla (Absorción II). */
	public static String roman(int level) {
		return switch (level) {
			case 1 -> "I";
			case 2 -> "II";
			case 3 -> "III";
			case 4 -> "IV";
			case 5 -> "V";
			default -> Integer.toString(level);
		};
	}
}
