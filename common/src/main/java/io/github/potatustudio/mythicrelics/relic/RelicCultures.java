package io.github.potatustudio.mythicrelics.relic;

import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/** Cultura de cada reliquia. GENERADO por tools/model-gen/make_codex.py desde moddata.CULTURES: no editar a mano. */
public final class RelicCultures {
	private static final Map<String, String> CULTURE = new HashMap<>();

	static {
		CULTURE.put("golden_scarab", "egypt");
		CULTURE.put("eye_of_horus", "egypt");
		CULTURE.put("ankh", "egypt");
		CULTURE.put("mummy_wraps", "egypt");
		CULTURE.put("anubis_scepter", "egypt");
		CULTURE.put("jackal_mask", "egypt");
		CULTURE.put("jackal_ring", "egypt");
		CULTURE.put("ra_disk", "egypt");
		CULTURE.put("bastet_amulet", "egypt");
		CULTURE.put("thoth_palette", "egypt");
		CULTURE.put("osiris_crook", "egypt");
		CULTURE.put("osiris_flail", "egypt");
		CULTURE.put("sekhmet_claws", "egypt");
		CULTURE.put("hermes_sandals", "greece");
		CULTURE.put("hades_helm", "greece");
		CULTURE.put("athena_aegis", "greece");
		CULTURE.put("medusa_head", "greece");
		CULTURE.put("ariadne_thread", "greece");
		CULTURE.put("charon_obol", "greece");
		CULTURE.put("orpheus_lyre", "greece");
		CULTURE.put("icarus_wings", "greece");
		CULTURE.put("golden_fleece", "greece");
		CULTURE.put("cerberus_collar", "greece");
		CULTURE.put("pandora_jar", "greece");
		CULTURE.put("styx_water", "greece");
		CULTURE.put("prometheus_fennel", "greece");
		CULTURE.put("zeus_thunderbolt", "greece");
		CULTURE.put("nemean_lion_skin", "greece");
		CULTURE.put("achilles_shield", "greece");
		CULTURE.put("harpe", "greece");
		CULTURE.put("golden_bridle", "greece");
		CULTURE.put("apple_of_discord", "greece");
		CULTURE.put("talos_nail", "greece");
		CULTURE.put("wolf_bulla", "rome");
		CULTURE.put("ancile", "rome");
		CULTURE.put("palladium", "rome");
		CULTURE.put("janus_key", "rome");
		CULTURE.put("vesta_lamp", "rome");
		CULTURE.put("sibylline_books", "rome");
		CULTURE.put("augur_lituus", "rome");
		CULTURE.put("mjolnir", "norse");
		CULTURE.put("jarngreipr", "norse");
		CULTURE.put("megingjord", "norse");
		CULTURE.put("gungnir", "norse");
		CULTURE.put("draupnir", "norse");
		CULTURE.put("gjallarhorn", "norse");
		CULTURE.put("gleipnir", "norse");
		CULTURE.put("idunn_apples", "norse");
		CULTURE.put("sleipnir_horseshoe", "norse");
		CULTURE.put("odin_ravens", "norse");
		CULTURE.put("brisingamen", "norse");
		CULTURE.put("skidbladnir", "norse");
		CULTURE.put("mimir_head", "norse");
		CULTURE.put("andvaranaut", "norse");
		CULTURE.put("odrerir", "norse");
		CULTURE.put("gram", "norse");
		CULTURE.put("tyrfing", "norse");
		CULTURE.put("dainsleif", "norse");
		CULTURE.put("freyr_sword", "norse");
		CULTURE.put("falcon_cloak", "norse");
		CULTURE.put("mistletoe_dart", "norse");
		CULTURE.put("ruyi_jingu_bang", "china");
		CULTURE.put("monkey_king_hairs", "china");
		CULTURE.put("purple_gold_gourd", "china");
		CULTURE.put("dragon_pearl", "china");
		CULTURE.put("hou_yi_bow", "china");
		CULTURE.put("change_elixir", "china");
		CULTURE.put("five_color_stone", "china");
		CULTURE.put("fu_talismans", "china");
		CULTURE.put("xirang_sack", "china");
	}

	private RelicCultures() {
	}

	/** egypt, greece, rome, norse o china; null si no es una reliquia. */
	public static @Nullable String of(String id) {
		return CULTURE.get(id);
	}
}
