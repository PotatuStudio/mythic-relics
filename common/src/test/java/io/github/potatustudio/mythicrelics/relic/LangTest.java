package io.github.potatustudio.mythicrelics.relic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** Los dos idiomas del mod (los recursos de common/src/main/resources). */
class LangTest {
	private static final Pattern RELIC_NAME = Pattern.compile("item\\.mythicrelics\\.([a-z0-9_]+)");
	private static final Pattern ARG = Pattern.compile("%(\\d+)\\$s|%s");

	private static JsonObject lang(String code) throws IOException {
		try (InputStream in = LangTest.class.getResourceAsStream("/assets/mythicrelics/lang/" + code + ".json")) {
			assertTrue(in != null, "falta " + code + ".json");
			return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
		}
	}

	@Test
	void mismasClavesEnInglesYEspanol() throws IOException {
		Set<String> en = new TreeSet<>(lang("en_us").keySet());
		Set<String> es = new TreeSet<>(lang("es_es").keySet());
		Set<String> onlyEn = new TreeSet<>(en);
		onlyEn.removeAll(es);
		Set<String> onlyEs = new TreeSet<>(es);
		onlyEs.removeAll(en);
		assertTrue(onlyEn.isEmpty(), "solo en inglés: " + onlyEn);
		assertTrue(onlyEs.isEmpty(), "solo en español: " + onlyEs);
	}

	@Test
	void mismosArgumentosEnLosDosIdiomas() throws IOException {
		JsonObject en = lang("en_us");
		JsonObject es = lang("es_es");
		List<String> wrong = new ArrayList<>();
		for (String key : en.keySet()) {
			if (es.has(key) && !args(en.get(key).getAsString()).equals(args(es.get(key).getAsString()))) {
				wrong.add(key);
			}
		}
		assertTrue(wrong.isEmpty(), "argumentos distintos: " + wrong);
	}

	/** Cada reliquia con tooltip tiene su resumen corto (.summary), en los dos idiomas. */
	@Test
	void cadaReliquiaConTooltipTieneResumen() throws IOException {
		for (String code : List.of("en_us", "es_es")) {
			JsonObject lang = lang(code);
			List<String> missing = new ArrayList<>();
			for (String key : lang.keySet()) {
				Matcher m = RELIC_NAME.matcher(key);
				if (m.matches() && lang.has(key + ".tooltip.1") && !lang.has(key + ".summary")) {
					missing.add(m.group(1));
				}
			}
			assertEquals(List.of(), missing, "sin .summary en " + code);
		}
	}

	private static Set<String> args(String text) {
		Set<String> found = new TreeSet<>();
		Matcher m = ARG.matcher(text.replace("%%", ""));
		int sequential = 0;
		while (m.find()) {
			found.add(m.group(1) != null ? m.group(1) : Integer.toString(++sequential));
		}
		return found;
	}
}
