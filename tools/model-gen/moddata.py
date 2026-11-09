"""Lectura de los datos del mod que comparten el visor (make_viewer.py) y el generador del libro y la wiki
(make_codex.py): íconos, traducciones, números de EgyptRelics.java, botín de ModLoot.java y rareza de ModItems.java.

Los números de los tooltips replican EgyptRelics#tooltipArgs y EgyptGods#tooltipArgs: si cambia una fórmula,
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
         "ra_disk", "bastet_amulet", "thoth_palette", "osiris_crook", "osiris_flail", "sekhmet_claws"]
# Sets: clave de traducción del bono (tooltip.mythicrelics.<set>.N) -> piezas.
SETS = {"anubis_set": ["anubis_scepter", "jackal_mask", "jackal_ring"], "osiris_set": ["osiris_crook", "osiris_flail"]}
SET_PIECES = {piece for pieces in SETS.values() for piece in pieces}
SLOTS = {"golden_scarab": "legs/belt", "eye_of_horus": "head/face", "ankh": "chest/necklace",
         "mummy_wraps": "hand/glove", "anubis_scepter": "mainhand", "jackal_mask": "head/face",
         "jackal_ring": "hand/ring", "ra_disk": "head/hat", "bastet_amulet": "chest/necklace",
         "thoth_palette": "offhand", "osiris_crook": "offhand", "osiris_flail": "mainhand", "sekhmet_claws": "hand/glove"}


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def b64(path):
    with open(path, "rb") as f:
        return base64.b64encode(f.read()).decode()


def constants():
    out = {}
    for name in ("EgyptRelics.java", "EgyptGods.java"):
        src = read(os.path.join(JAVA, "egypt", name))
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
        "bastet_amulet": [round(c["BASTET_SAFE_FALL"]) + 3, round(c["BASTET_FALL_REDUCTION"] * 100)],
        "thoth_palette": [int(c["THOTH_MAX_LEVELS"]), round(c["THOTH_XP_BONUS"] * 100)],
        "osiris_crook": [int(c["CROOK_GROW_COOLDOWN"] // 20)],
        "osiris_flail": [fmt(c["FLAIL_SWEEP_RADIUS"]), round(c["FLAIL_SWEEP_DAMAGE"] * 100)],
        "sekhmet_claws": [int(c["SEKHMET_WINDOW"] // 20), int(c["SEKHMET_MAX"]), round(c["SEKHMET_PER_LEVEL"] * 100),
                          int(c["SEKHMET_BURN_SECONDS"]), round(c["SEKHMET_CALM_HEAL"] / 2)],
    }


def set_args(c):
    """Argumentos de las líneas de bono de cada set (el de Osiris muestra la recarga en minutos)."""
    return {"anubis_set": [], "osiris_set": [int(c["OSIRIS_REBIRTH_COOLDOWN"] // 20 // 60)]}


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
    for name in ORDER:
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
                   "desert_well_archaeology": "loot.mythicrelics.archaeology.desert_well"}
# Tablas de arqueología: la probabilidad es por bloque cepillado, no por cofre.
ARCHAEOLOGY = {"desert_pyramid_archaeology", "desert_well_archaeology"}



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
