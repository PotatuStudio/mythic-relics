"""Lectura de los datos del mod que comparten el visor (make_viewer.py) y el generador del libro y la wiki
(make_codex.py): íconos, traducciones, números de EgyptRelics.java, botín de ModLoot.java y rareza de ModItems.java.

Los números de los tooltips replican EgyptRelics, EgyptGods y GreeceRelics#tooltipArgs: si cambia una fórmula,
actualizar tooltip_args aquí.
"""
import base64
import json
import os
import re

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
COMMON = os.path.join(ROOT, "common", "src", "main")
JAVA = os.path.join(COMMON, "java", "io", "github", "potatustudio", "mythicrelics")
ASSETS = os.path.join(COMMON, "resources", "assets", "mythicrelics")
# Configuración de render de Trinkets (solo Fabric; en NeoForge la ubicación está en CuriosRelicRenderer).
FABRIC_ASSETS = os.path.join(ROOT, "fabric", "src", "main", "resources", "assets", "mythicrelics")
ORDER = ["golden_scarab", "eye_of_horus", "ankh", "mummy_wraps", "anubis_scepter", "jackal_mask", "jackal_ring",
         "ra_disk", "bastet_amulet", "thoth_palette", "osiris_crook", "osiris_flail", "sekhmet_claws",
         "hermes_sandals", "hades_helm", "athena_aegis", "medusa_head", "ariadne_thread", "charon_obol", "orpheus_lyre",
         "icarus_wings", "golden_fleece", "cerberus_collar", "pandora_jar", "styx_water", "prometheus_fennel",
         "zeus_thunderbolt", "nemean_lion_skin", "achilles_shield", "harpe", "golden_bridle", "apple_of_discord", "talos_nail",
         "wolf_bulla", "ancile", "palladium", "janus_key", "vesta_lamp", "sibylline_books", "augur_lituus",
         "mjolnir", "jarngreipr", "megingjord", "gungnir", "draupnir", "gjallarhorn", "gleipnir", "idunn_apples",
         "sleipnir_horseshoe", "odin_ravens", "brisingamen", "skidbladnir", "mimir_head", "andvaranaut", "odrerir",
         "gram", "tyrfing", "dainsleif", "freyr_sword", "falcon_cloak", "mistletoe_dart",
         "ruyi_jingu_bang", "monkey_king_hairs", "purple_gold_gourd", "dragon_pearl", "hou_yi_bow", "change_elixir",
         "five_color_stone", "fu_talismans", "xirang_sack"]
# Cultura de cada reliquia (categoría del libro y página de la wiki), en el orden en que se muestran.
CULTURES = {"egypt": ORDER[:13], "greece": ORDER[13:33], "rome": ORDER[33:40], "norse": ORDER[40:61], "china": ORDER[61:]}
# Sets: clave de traducción del bono (tooltip.mythicrelics.<set>.N) -> piezas.
def _sets():
    """Los sets y sus piezas, leídos de relic/RelicSets.java (la fuente de verdad), para no copiarlos a mano."""
    import re as _re
    src = open(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "common", "src", "main", "java", "io",
                            "github", "potatustudio", "mythicrelics", "relic", "RelicSets.java"), encoding="utf-8").read()
    out = {}
    # ANUBIS("anubis_set", worn("jackal_mask"), …) o PERSEUS("perseus_set", NECESARIAS, worn(…), …): cada pieza es dónde("id").
    for m in _re.finditer(r'^\s*[A-Z_]+\("(\w+)", (?:[\w.]+, )?((?:\w+\("\w+"\)(?:, )?)+)\)[,;]', src, _re.M):
        out[m.group(1)] = _re.findall(r'"(\w+)"', m.group(2))
    return out


SETS = _sets()
SET_PIECES = {piece for pieces in SETS.values() for piece in pieces}
SLOTS = {"golden_scarab": "legs/belt", "eye_of_horus": "head/face", "ankh": "chest/necklace",
         "mummy_wraps": "hand/glove", "anubis_scepter": "mainhand", "jackal_mask": "head/face",
         "jackal_ring": "hand/ring", "ra_disk": "head/hat", "bastet_amulet": "chest/necklace",
         "thoth_palette": "offhand", "osiris_crook": "offhand", "osiris_flail": "mainhand", "sekhmet_claws": "hand/glove",
         "hermes_sandals": "feet/shoes", "hades_helm": "head/hat", "athena_aegis": "offhand", "medusa_head": "offhand",
         "ariadne_thread": "hand", "charon_obol": "inventory", "orpheus_lyre": "hand",
         "icarus_wings": "chest/back", "golden_fleece": "chest/cape", "cerberus_collar": "chest/necklace",
         "pandora_jar": "hand", "styx_water": "hand", "prometheus_fennel": "hand",
         "wolf_bulla": "chest/necklace", "ancile": "offhand", "palladium": "block", "janus_key": "hand",
         "vesta_lamp": "offhand", "sibylline_books": "hand", "augur_lituus": "hand",
         "mjolnir": "mainhand", "jarngreipr": "hand/glove", "megingjord": "legs/belt", "gungnir": "mainhand",
         "draupnir": "hand/ring", "gjallarhorn": "hand", "gleipnir": "hand", "idunn_apples": "hand",
         "sleipnir_horseshoe": "hand", "odin_ravens": "hand", "brisingamen": "chest/necklace", "skidbladnir": "hand",
         "mimir_head": "offhand", "andvaranaut": "hand/ring", "odrerir": "hand",
         "ruyi_jingu_bang": "mainhand", "monkey_king_hairs": "hand", "purple_gold_gourd": "hand",
         "dragon_pearl": "hand", "hou_yi_bow": "mainhand", "change_elixir": "hand",
         "five_color_stone": "legs/belt", "fu_talismans": "hand", "xirang_sack": "hand",
         # Grecia 3 y nórdicos 3
         "zeus_thunderbolt": "mainhand", "nemean_lion_skin": "chest/cape", "achilles_shield": "offhand", "harpe": "mainhand",
         "golden_bridle": "hand", "apple_of_discord": "hand", "talos_nail": "hand",
         "gram": "mainhand", "tyrfing": "mainhand", "dainsleif": "mainhand", "freyr_sword": "chest/back",
         "falcon_cloak": "chest/back", "mistletoe_dart": "mainhand"}


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def b64(path):
    with open(path, "rb") as f:
        return base64.b64encode(f.read()).decode()


def constants():
    out = {}
    for name in ("egypt/EgyptRelics.java", "egypt/EgyptGods.java", "greece/GreeceRelics.java", "greece/GreeceHeroes.java",
                 "rome/RomeRelics.java", "norse/NorseRelics.java",
                 "norse/NorseGods.java", "china/ChinaRelics.java", "china/ChinaGods.java",
                 "greece/GreeceLegends.java", "norse/NorseSagas.java",
                 "norse/MimirHead.java"):
        src = read(os.path.join(JAVA, *name.split("/")))
        out.update({k: float(v) for k, v in re.findall(r"public static final (?:float|int|double) (\w+) = ([\d.]+)f?;", src)})
    return out


def fmt(v):
    return str(int(v)) if v == int(v) else str(v)


ROMAN = {1: "I", 2: "II", 3: "III", 4: "IV", 5: "V"}


def tooltip_args(c):
    return {
        "golden_scarab": [fmt(c["SCARAB_THRESHOLD"] / 2), ROMAN[int(c["SCARAB_ABSORPTION"]) + 1],
                          int(c["SCARAB_MINION_TICKS"] // 20), int(c["SCARAB_COOLDOWN"] // 20)],
        "eye_of_horus": [fmt(c["EYE_BIG_HIT"] / 2), round(c["EYE_HEAL_FRACTION"] * 100), int(c["EYE_HEAL_TICKS"] // 20),
                         int(c["EYE_COOLDOWN"] // 20)],
        "ankh": [fmt(c["ANKH_HEAL_EVERY"] / 20), round(c["ANKH_UNDEAD_REDUCTION"] * 100)],
        "mummy_wraps": [int(c["WRAPS_HUNGER_TICKS"] // 20), int(c["WRAPS_SLOW_TICKS"] // 20)],
        "anubis_scepter": [round(c["SCEPTER_JUDGMENT_HEALTH"] * 100), int(c["SCEPTER_XP_MULTIPLIER"])],
        "jackal_mask": [fmt(c["MASK_SOUL_HEAL"] / 2), int(c["MASK_SOUL_TICKS"] // 20)],
        "jackal_ring": [int(c["RING_JACKALS"]), int(c["RING_JACKAL_TICKS"] // 20), int(c["RING_COOLDOWN"] // 20)],
        # Tanda 2 (EgyptGods#tooltipArgs)
        "ra_disk": [round(c["RA_UNDEAD_BONUS"] * 100), int(c["RA_BURN_SECONDS"]), int(c["URAEUS_BURN_SECONDS"]),
                    int(c["URAEUS_COOLDOWN"] // 20)],
        "bastet_amulet": [],
        "thoth_palette": [round(c["THOTH_XP_BONUS"] * 100)],
        "osiris_crook": [int(c["CROOK_GROW_COOLDOWN"] // 20), int(c["CROOK_RULE_TICKS"] // 20)],
        "osiris_flail": [fmt(c["FLAIL_SWEEP_RADIUS"]), round(c["FLAIL_SWEEP_DAMAGE"] * 100)],
        "sekhmet_claws": [int(c["SEKHMET_WINDOW"] // 20), int(c["SEKHMET_MAX"]), round(c["SEKHMET_PER_LEVEL"] * 100),
                          int(c["SEKHMET_BURN_SECONDS"]), round(c["SEKHMET_CALM_HEAL"] / 2)],
        # Grecia (GreeceRelics#tooltipArgs)
        "hermes_sandals": [round(c["HERMES_SPEED"] * 100)],
        "hades_helm": [int(c["HADES_STILL_TICKS"] // 20), round(c["HADES_SNEAK_SPEED"] * 100 / 0.3)],
        "athena_aegis": [int(c["AEGIS_STUN_TICKS"] // 20)],
        "medusa_head": [int(c["MEDUSA_TICKS"] // 20), int(c["MEDUSA_RADIUS"]), int(c["MEDUSA_GAZE_TICKS"] // 20)],
        "ariadne_thread": [int(c["ARIADNE_COOLDOWN"] // 20 // 60)],
        "charon_obol": [],
        "orpheus_lyre": [int(c["ORPHEUS_ANIMALS"]), int(c["ORPHEUS_HOSTILES"])],
        # Grecia, tanda 2 (GreeceHeroes#tooltipArgs)
        "icarus_wings": [int(c["ICARUS_FLAPS"]), int(c["ICARUS_MELT_HEIGHT"]), int(c["ICARUS_MELT_TICKS"] // 20)],
        "golden_fleece": [round(c["FLEECE_NUGGET_CHANCE"] * 100)],
        "cerberus_collar": [int(c["CERBERUS_TICKS"] // 20), int(c["CERBERUS_BITES"]), int(c["CERBERUS_BURN_SECONDS"]),
                            int(c["CERBERUS_COOLDOWN"] // 20)],
        "pandora_jar": [int(c["PANDORA_COOLDOWN"] // 20), int(c["PANDORA_EVIL_CHANCE"]), int(c["PANDORA_EVIL_TICKS"] // 20),
                        int(c["PANDORA_VEX_CHANCE"]), int(c["PANDORA_VEXES"]), int(c["PANDORA_VEX_TICKS"] // 20),
                        int(c["PANDORA_TREASURE_CHANCE"]), int(c["PANDORA_HOPE_CHANCE"]),
                        int(c["PANDORA_HOPE_ABSORPTION_TICKS"] // 20 // 60)],
        "styx_water": [int(c["STYX_TICKS"] // 20 // 60), round(c["STYX_FRONT_FACTOR"] * 100), int(c["STYX_COOLDOWN"] // 20 // 60)],
        "prometheus_fennel": [int(c["PROMETHEUS_BURN_SECONDS"]), int(c["PROMETHEUS_COOLDOWN_PER_ITEM"] // 20),
                              int(c["PROMETHEUS_COOLDOWN_MIN"] // 20)],
        # Roma (RomeRelics#tooltipArgs)
        "wolf_bulla": [int(c["WOLF_RADIUS"]), int(c["WOLF_HOWL_HEALTH"] // 2), int(c["WOLF_HOWL_WOLVES"]),
                       int(c["WOLF_HOWL_TICKS"] // 20), int(c["WOLF_HOWL_COOLDOWN"] // 20 // 60)],
        "ancile": [int(c["ANCILE_RAID_RADIUS"]), round(c["ANCILE_RAID_FACTOR"] * 100)],
        "palladium": [int(c["PALLADIUM_RADIUS"])],
        "janus_key": [],
        "vesta_lamp": [int(c["VESTA_RADIUS"]), round(c["VESTA_HUNGER_FACTOR"] * 100), int(c["VESTA_FIRE_TICKS"] // 20 // 60),
                       int(c["VESTA_FIRE_COOLDOWN"] // 20 // 60)],
        "sibylline_books": [int(c["SIBYL_LEVELS"]), int(c["SIBYL_RADIUS"]), int(c["SIBYL_GLOW_TICKS"] // 20),
                            int(c["SIBYL_COOLDOWN"] // 20 // 60)],
        "augur_lituus": [int(c["LITUUS_FAVOR_TICKS"] // 20 // 60), int(c["LITUUS_BAD_TICKS"] // 20 // 60),
                         int(c["LITUUS_BIRD_RADIUS"]), int(c["LITUUS_COOLDOWN"] // 20 // 60)],
        # Nórdicos (NorseRelics#tooltipArgs)
        "mjolnir": [int(c["MJOLNIR_RANGE"])],
        "jarngreipr": [int(c["JARNGREIPR_FIST"])],
        "megingjord": [round(c["MEGINGJORD_DAMAGE"] * 100), round(c["MEGINGJORD_KNOCKBACK"] * 100)],
        "gungnir": [int(c["GUNGNIR_RANGE"])],
        "draupnir": [int(c["DRAUPNIR_NIGHTS"]), int(c["DRAUPNIR_INGOTS"])],
        "gjallarhorn": [int(c["HORN_USE_TICKS"] // 20), int(c["HORN_ALLY_RADIUS"]), int(c["HORN_BUFF_TICKS"] // 20),
                        int(c["HORN_SIGHT_RADIUS"]), int(c["HORN_GLOW_TICKS"] // 20), int(c["HORN_COOLDOWN"] // 20 // 60)],
        "gleipnir": [int(c["GLEIPNIR_TICKS"] // 20), int(c["GLEIPNIR_COOLDOWN"] // 20), round(c["GLEIPNIR_DAMAGE_TAKEN"] * 100),
                     int(c["GLEIPNIR_BOSS_TICKS"] // 20)],
        "idunn_apples": [int(c["IDUNN_REGEN_TICKS"] // 20), int(c["IDUNN_ABSORPTION_TICKS"] // 20 // 60), int(c["IDUNN_MAX"])],
        # Nórdicos, tanda 2 (NorseGods#tooltipArgs)
        "sleipnir_horseshoe": [int(c["SLEIPNIR_LEAVE_TICKS"] // 20), int(c["SLEIPNIR_COOLDOWN"] // 20 // 60)],
        "odin_ravens": [int(c["RAVENS_RADIUS"])],
        "brisingamen": [int(c["BRISINGAMEN_RADIUS"]), int(c["BRISINGAMEN_HEAL"] // 2)],
        "skidbladnir": [round(c["SKIDBLADNIR_BOOST"] * 100)],
        "mimir_head": [int(c["MIMIR_RADIUS"]), int(c["GUIDE_TICKS"] // 20), int(c["GUIDE_RADIUS"]), int(c["ASK_COOLDOWN"] // 20)],
        "andvaranaut": [int(c["ANDVARANAUT_HEALTH"] // 2)],
        "odrerir": [int(c["ODRERIR_LEVELS"]), int(c["ODRERIR_HASTE_TICKS"] // 20 // 60)],
        # China (ChinaRelics#tooltipArgs)
        "ruyi_jingu_bang": [fmt(c["RUYI_REACH"]), int(c["RUYI_LENGTH"]), int(c["RUYI_STRETCH_DAMAGE"]), int(c["RUYI_COOLDOWN"] // 20),
                            int(c["RUYI_EXTENDED_TICKS"] // 20), int(c["RUYI_EXTENDED_REACH"])],
        "monkey_king_hairs": [int(c["HAIRS_MONKEYS"]), int(c["HAIRS_TICKS"] // 20), int(c["MONKEY_HEALTH"]), int(c["MONKEY_DAMAGE"]),
                              int(c["HAIRS_COOLDOWN"] // 20 // 60)],
        "purple_gold_gourd": [int(c["GOURD_MAX_HEALTH"]), int(c["GOURD_DISSOLVE_TICKS"] // 20 // 60), int(c["GOURD_COOLDOWN"] // 20)],
        "dragon_pearl": [int(c["DRAGON_LEAVE_TICKS"] // 20), int(c["DRAGON_COOLDOWN"] // 20 // 60)],
        "hou_yi_bow": [round(c["HOU_YI_DAMAGE"] * 100), int(c["HOU_YI_SUN_FACTOR"])],
        "change_elixir": [round(c["ELIXIR_AIR_DAMAGE"] * 100), int(c["ELIXIR_FULL_SECONDS"]), int(c["ELIXIR_QUARTER_SECONDS"]),
                          int(c["ELIXIR_NEW_SECONDS"]), int(c["ELIXIR_COOLDOWN"] // 20 // 60)],
        "five_color_stone": [int(c["STONE_INTERVAL"] // 20)],
        # China, tanda 2 (ChinaGods#tooltipArgs)
        "fu_talismans": [int(c["TALISMAN_TICKS"] // 20), int(c["TALISMAN_COOLDOWN"] // 20)],
        "xirang_sack": [int(c["XIRANG_RESCUE_COOLDOWN"] // 20 // 60)],
        # Grecia 3 (GreeceLegends.tooltipArgs)
        "zeus_thunderbolt": [int(c["ZEUS_RANGE"]), int(c["ZEUS_DAMAGE"]), int(c["ZEUS_CHAIN_TARGETS"]), int(c["ZEUS_CHAIN_DAMAGE"]),
                             int(c["ZEUS_STORM_CHAIN_TARGETS"]), int(c["ZEUS_COOLDOWN"] // 20),
                           int(c["ZEUS_STRONG_TICKS"] // 20), int(c["ZEUS_STRONG_BOLTS"]), int(c["ZEUS_STRONG_COOLDOWN"] // 20)],
        "nemean_lion_skin": [round(c["NEMEA_REDUCTION"] * 100), int(c["NEMEA_FIST_DAMAGE"])],
        "achilles_shield": [int(c["WRATH_TICKS"] // 20), round(c["WRATH_BONUS"] * 100)],
        "harpe": [round(c["HARPE_HEAD_CHANCE"] * 100), round(c["HARPE_WITHER_SKULL_CHANCE"] * 100),
                  round(c["HARPE_EXECUTE_HEALTH"] * 100), int(c["HARPE_COOLDOWN"] // 20)],
        "golden_bridle": [int(c["PEGASUS_DIVE_DAMAGE"]), int(c["PEGASUS_OLYMPUS_Y"]), int(c["PEGASUS_COOLDOWN"] // 20 // 60)],
        "apple_of_discord": [int(c["APPLE_RANGE"]), int(c["APPLE_TICKS"] // 20), int(c["APPLE_RADIUS"]), int(c["APPLE_COOLDOWN"] // 20)],
        "talos_nail": [int(c["TALOS_TICKS"] // 20), int(c["TALOS_ROCK_DAMAGE"]), int(c["TALOS_ROCK_INTERVAL"] // 20),
                       int(c["TALOS_ROCK_RANGE"]), int(c["TALOS_HUG_DAMAGE"]), int(c["TALOS_HUG_FIRE_SECONDS"]),
                       int(c["TALOS_COOLDOWN"] // 20 // 60)],
        # Nórdicos 3 (NorseSagas.tooltipArgs)
        "gram": [int(c["GRAM_HITS_PER_THRUST"]), round(c["GRAM_THRUST_BONUS"] * 100), int(c["GRAM_BIG_HEALTH"])],
        "tyrfing": [round(c["TYRFING_CRIT"] * 100)],
        "dainsleif": [int(c["DAINSLEIF_NO_HEAL_TICKS"] // 20), int(c["DAINSLEIF_BLEED_TICKS"] // 20)],
        "freyr_sword": [int(c["FREYR_RADIUS"]), int(c["FREYR_DAMAGE"]), round(c["FREYR_BIG_BONUS"] * 100), int(c["FREYR_WISE_LEVEL"]),
                        round(c["FREYR_WISE_BONUS"] * 100), int(c["FREYR_IDLE_TICKS"] // 20)],
        "falcon_cloak": [int(c["FALCON_TICKS"] // 20), int(c["FALCON_COOLDOWN"] // 20)],
        "mistletoe_dart": [int(c["DART_DAMAGE"])],
    }


def set_args(c):
    """Argumentos de las líneas de bono de cada set (el de Osiris muestra la recarga en minutos)."""
    return {"anubis_set": [], "osiris_set": [int(c["OSIRIS_REBIRTH_COOLDOWN"] // 20 // 60)], "perseus_set": [],
            "thor_set": []}


def loot():
    src = read(os.path.join(JAVA, "registry", "ModLoot.java"))
    out = {}
    for table, body in re.findall(r"BuiltInLootTables\.(\w+), List\.of\((.*?)\)\)", src, re.S):
        for chance, item in re.findall(r"new Drop\(([\d.]+)f, ModItems\.(\w+)", body):
            out.setdefault(item.lower(), []).append({"table": table.lower(), "chance": float(chance)})
    for v in out.values():
        v.sort(key=lambda s: -s["chance"])
    return out


def rarity():
    src = read(os.path.join(JAVA, "registry", "ModItems.java"))
    return {item: r.lower() for item, r in
            re.findall(r'(?:relic|register)\("(\w+)",[^;]*?p -> p\.rarity\(Rarity\.(\w+)\)', src)}


def icons():
    out = {}
    for name in ORDER + ["emblem"]:
        png = os.path.join(ASSETS, "textures", "item", name + ".png")
        meta_path = png + ".mcmeta"
        frames = json.loads(read(meta_path))["animation"]["frames"] if os.path.exists(meta_path) else None
        out[name] = {"png": b64(png), "frames": frames}
    return out


def lang():
    return {code: json.loads(read(os.path.join(ASSETS, "lang", code + ".json"))) for code in ("es_es", "en_us")}


LOOT_TABLE_KEYS = {"desert_pyramid": "loot.mythicrelics.chests.desert_pyramid",
                   "village_desert_house": "loot.mythicrelics.chests.village.village_desert_house",
                   "desert_pyramid_archaeology": "loot.mythicrelics.archaeology.desert_pyramid",
                   "desert_well_archaeology": "loot.mythicrelics.archaeology.desert_well",
                   "trail_ruins_archaeology_rare": "loot.mythicrelics.archaeology.trail_ruins_rare",
                   "ocean_ruin_warm_archaeology": "loot.mythicrelics.archaeology.ocean_ruin_warm",
                   "nether_bridge": "loot.mythicrelics.chests.nether_bridge",
                   "stronghold_corridor": "loot.mythicrelics.chests.stronghold_corridor",
                   "stronghold_library": "loot.mythicrelics.chests.stronghold_library",
                   "trial_chambers_reward": "loot.mythicrelics.chests.trial_chambers.reward",
                   "ancient_city": "loot.mythicrelics.chests.ancient_city",
                   "village_weaponsmith": "loot.mythicrelics.chests.village.village_weaponsmith",
                   "ocean_ruin_cold_archaeology": "loot.mythicrelics.archaeology.ocean_ruin_cold",
                   "village_shepherd": "loot.mythicrelics.chests.village.village_shepherd",
                   "shipwreck_treasure": "loot.mythicrelics.chests.shipwreck_treasure",
                   "bastion_treasure": "loot.mythicrelics.chests.bastion_treasure",
                   "bastion_other": "loot.mythicrelics.chests.bastion_other",
                   "bastion_bridge": "loot.mythicrelics.chests.bastion_bridge",
                   "bastion_hoglin_stable": "loot.mythicrelics.chests.bastion_hoglin_stable",
                   "woodland_mansion": "loot.mythicrelics.chests.woodland_mansion",
                   "ruined_portal": "loot.mythicrelics.chests.ruined_portal",
                   "village_taiga_house": "loot.mythicrelics.chests.village.village_taiga_house",
                   "pillager_outpost": "loot.mythicrelics.chests.pillager_outpost",
                   "village_temple": "loot.mythicrelics.chests.village.village_temple",
                   "village_plains_house": "loot.mythicrelics.chests.village.village_plains_house",
                   "jungle_temple": "loot.mythicrelics.chests.jungle_temple",
                   "trial_chambers_reward_ominous": "loot.mythicrelics.chests.trial_chambers.reward_ominous",
                   "abandoned_mineshaft": "loot.mythicrelics.chests.abandoned_mineshaft",
                   "igloo_chest": "loot.mythicrelics.chests.igloo_chest",
                   "end_city_treasure": "loot.mythicrelics.chests.end_city_treasure",
                   "buried_treasure": "loot.mythicrelics.chests.buried_treasure",
                   "village_snowy_house": "loot.mythicrelics.chests.village.village_snowy_house",
                   "village_savanna_house": "loot.mythicrelics.chests.village.village_savanna_house",
                   "shipwreck_supply": "loot.mythicrelics.chests.shipwreck_supply",
                   "stronghold_crossing": "loot.mythicrelics.chests.stronghold_crossing",
                   "underwater_ruin_big": "loot.mythicrelics.chests.underwater_ruin_big",
                   "underwater_ruin_small": "loot.mythicrelics.chests.underwater_ruin_small",
                   "simple_dungeon": "loot.mythicrelics.chests.simple_dungeon",
                   "trail_ruins_archaeology_common": "loot.mythicrelics.archaeology.trail_ruins_common"}
# Tablas de arqueología: la probabilidad es por bloque cepillado, no por cofre.
ARCHAEOLOGY = {"desert_pyramid_archaeology", "desert_well_archaeology", "trail_ruins_archaeology_rare",
               "ocean_ruin_warm_archaeology", "ocean_ruin_cold_archaeology", "trail_ruins_archaeology_common"}



def substitute(text, args):
    """Formato de Minecraft: %1$s / %s con argumentos y %% como signo de porcentaje."""
    seq = iter(range(100))

    def repl(m):
        i = int(m.group(1)[:-1]) - 1 if m.group(1) else next(seq)
        return str(args[i]) if i < len(args) else m.group(0)

    return re.sub(r"%(\d+\$)?s", repl, text).replace("%%", "%")


def tooltip_lines(lang_map, item, args):
    lines = []
    for i in range(1, 9):
        key = f"item.mythicrelics.{item}.tooltip.{i}"
        if key not in lang_map:
            break
        lines.append(substitute(lang_map[key], args))
    return lines


def pct(chance):
    p = chance * 100
    return str(round(p)) if abs(p - round(p)) < 1e-6 else f"{p:.1f}"
