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
		CULTURE.put("ruyi_jingu_bang", "china");
		CULTURE.put("monkey_king_hairs", "china");
		CULTURE.put("purple_gold_gourd", "china");
		CULTURE.put("banana_leaf_fan", "china");
		CULTURE.put("dragon_pearl", "china");
		CULTURE.put("wind_fire_wheels", "china");
		CULTURE.put("hou_yi_bow", "china");
		CULTURE.put("change_elixir", "china");
		CULTURE.put("five_color_stone", "china");
		CULTURE.put("fire_tipped_spear", "china");
		CULTURE.put("universe_ring", "china");
		CULTURE.put("erlang_eye", "china");
		CULTURE.put("erlang_halberd", "china");
		CULTURE.put("qilin_horn", "china");
		CULTURE.put("pixiu_bracelet", "china");
		CULTURE.put("fu_talismans", "china");
		CULTURE.put("xirang_sack", "china");
		CULTURE.put("immortal_peach_branch", "china");
		CULTURE.put("kusanagi", "japan");
		CULTURE.put("yata_mirror", "japan");
		CULTURE.put("yasakani_magatama", "japan");
		CULTURE.put("raijin_drum", "japan");
		CULTURE.put("oni_kanabo", "japan");
		CULTURE.put("kitsune_star_ball", "japan");
		CULTURE.put("tide_jewels", "japan");
		CULTURE.put("lucky_mallet", "japan");
		CULTURE.put("kibi_dango", "japan");
		CULTURE.put("urashima_rod", "japan");
		CULTURE.put("fujin_wind_bag", "japan");
		CULTURE.put("tengu_mask", "japan");
		CULTURE.put("yashiori_sake", "japan");
		CULTURE.put("kappa_dish", "japan");
		CULTURE.put("tanuki_leaf", "japan");
		CULTURE.put("kintaro_axe", "japan");
		CULTURE.put("zashiki_warashi", "japan");
		CULTURE.put("ame_no_nuboko", "japan");
		CULTURE.put("orochi_scale", "japan");
	}

	private RelicCultures() {
	}

	/** egypt, greece, rome, norse, china o japan; null si no es una reliquia. */
	public static @Nullable String of(String id) {
		return CULTURE.get(id);
	}
}
