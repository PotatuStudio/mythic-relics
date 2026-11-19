package io.github.potatustudio.mythicrelics.relic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.network.chat.contents.TranslatableContents;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Lógica pura de relic/: se prueba sin arrancar Minecraft. */
class PureLogicTest {
	@Test
	void fmtSinDecimalesCuandoEsEntero() {
		assertEquals("3", TooltipNumbers.fmt(3.0));
		assertEquals("2.5", TooltipNumbers.fmt(2.5));
		assertEquals("0", TooltipNumbers.fmt(0));
	}

	@Test
	void romanComoVanilla() {
		assertEquals("I", TooltipNumbers.roman(1));
		assertEquals("II", TooltipNumbers.roman(2));
		assertEquals("V", TooltipNumbers.roman(5));
		assertEquals("6", TooltipNumbers.roman(6));
	}

	@ParameterizedTest(name = "dx={0}, dz={1} -> {2}")
	@CsvSource({
			"0, 10, south", "0, -10, north", "10, 0, east", "-10, 0, west",
			"10, 10, southeast", "-10, 10, southwest", "10, -10, northeast", "-10, -10, northwest"})
	void rumbosDeLaRosaDeLosVientos(double dx, double dz, String rumbo) {
		var contents = (TranslatableContents) Compass.direction(dx, dz).getContents();
		assertEquals("direction.mythicrelics." + rumbo, contents.getKey());
	}

	@Test
	void puntosDeNivelComoVanilla() {
		// De 0 a 1: 7 puntos; de 15 a 16: 37; de 30 a 31: 112 (wiki de Minecraft).
		assertEquals(7, XpMath.pointsForLevel(0));
		assertEquals(37, XpMath.pointsForLevel(15));
		assertEquals(112, XpMath.pointsForLevel(30));
	}

	@Test
	void nivelesYPuntosIdaYVuelta() {
		int total = 0;
		for (int level = 0; level <= 60; level++) {
			assertEquals(level, XpMath.levelForPoints(total), "nivel con " + total + " puntos");
			// Un punto menos que el siguiente nivel sigue siendo este nivel.
			assertEquals(level, XpMath.levelForPoints(total + XpMath.pointsForLevel(level) - 1));
			total += XpMath.pointsForLevel(level);
		}
	}
}
