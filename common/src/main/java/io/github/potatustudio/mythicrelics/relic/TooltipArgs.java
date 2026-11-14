package io.github.potatustudio.mythicrelics.relic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.architectury.platform.Platform;
import io.github.potatustudio.mythicrelics.MythicRelics;
import io.github.potatustudio.mythicrelics.china.ChinaGods;
import io.github.potatustudio.mythicrelics.china.ChinaRelics;
import io.github.potatustudio.mythicrelics.egypt.EgyptGods;
import io.github.potatustudio.mythicrelics.egypt.EgyptRelics;
import io.github.potatustudio.mythicrelics.greece.GreeceHeroes;
import io.github.potatustudio.mythicrelics.greece.GreeceLegends;
import io.github.potatustudio.mythicrelics.greece.GreeceRelics;
import io.github.potatustudio.mythicrelics.japan.JapanGods;
import io.github.potatustudio.mythicrelics.japan.JapanRelics;
import io.github.potatustudio.mythicrelics.norse.NorseGods;
import io.github.potatustudio.mythicrelics.norse.NorseRelics;
import io.github.potatustudio.mythicrelics.norse.NorseSagas;
import io.github.potatustudio.mythicrelics.rome.RomeRelics;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Números que muestra el tooltip de cada reliquia: los %1$s, %2$s… de sus líneas {@code item.mythicrelics.<id>.tooltip.N},
 * en ese orden. Cada clase de cultura registra los de sus reliquias en {@code registerTooltipArgs()}, junto a las
 * constantes que usa. ({@code tools/model-gen/moddata.py} tiene una copia en Python para el libro y la wiki.)
 */
public final class TooltipArgs {
	private static final Object[] NONE = new Object[0];
	private static final Map<String, Object[]> ARGS = new HashMap<>();
	private static final Pattern POSITIONAL = Pattern.compile("%(\\d+)\\$s");
	private static final Pattern TOOLTIP_KEY = Pattern.compile("item\\.mythicrelics\\.(\\w+)\\.tooltip\\.\\d+");

	private TooltipArgs() {
	}

	public static void init() {
		EgyptRelics.registerTooltipArgs();
		EgyptGods.registerTooltipArgs();
		GreeceRelics.registerTooltipArgs();
		GreeceHeroes.registerTooltipArgs();
		GreeceLegends.registerTooltipArgs();
		RomeRelics.registerTooltipArgs();
		NorseRelics.registerTooltipArgs();
		NorseGods.registerTooltipArgs();
		NorseSagas.registerTooltipArgs();
		ChinaRelics.registerTooltipArgs();
		ChinaGods.registerTooltipArgs();
		JapanRelics.registerTooltipArgs();
		JapanGods.registerTooltipArgs();
		if (Platform.isDevelopmentEnvironment()) {
			check();
		}
	}

	public static void register(String id, Object... args) {
		if (ARGS.put(id, args) != null) {
			throw new IllegalStateException("Mythic Relics: tooltipArgs de " + id + " registrados dos veces");
		}
	}

	public static Object[] of(String id) {
		return ARGS.getOrDefault(id, NONE);
	}

	/** Solo en desarrollo: avisa si una línea de tooltip pide un %n$s que su reliquia no registra. */
	private static void check() {
		try (InputStream in = MythicRelics.class.getResourceAsStream("/assets/mythicrelics/lang/en_us.json")) {
			if (in == null) {
				return;
			}
			JsonObject lang = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
			for (Map.Entry<String, JsonElement> e : lang.entrySet()) {
				Matcher key = TOOLTIP_KEY.matcher(e.getKey());
				if (!key.matches()) {
					continue;
				}
				int have = of(key.group(1)).length;
				Matcher arg = POSITIONAL.matcher(e.getValue().getAsString());
				while (arg.find()) {
					if (Integer.parseInt(arg.group(1)) > have) {
						MythicRelics.LOGGER.warn("Mythic Relics: {} usa %{}$s pero {} solo registra {} argumentos", e.getKey(),
								arg.group(1), key.group(1), have);
					}
				}
			}
		} catch (Exception ex) {
			MythicRelics.LOGGER.warn("Mythic Relics: no pude revisar los argumentos de los tooltips", ex);
		}
	}
}
