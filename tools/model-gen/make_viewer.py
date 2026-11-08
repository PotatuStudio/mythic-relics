"""Visor HTML (three.js) para revisar Mythic Relics sin abrir el juego.

Lee los mismos datos que usa el mod (íconos, traducciones, números de EgyptRelics.java, botín de ModLoot.java, rareza
de ModItems.java y la textura del escarabajo) y arma una sola página con:
  - la pestaña creativa con los tooltips como se ven en el juego (colores, líneas, rareza);
  - la página de JEI de "dónde encontrarlo";
  - el escarabajo y el chacal en 3D con sus modelos y su marcha, y un jugador con las reliquias puestas.

No usa texturas de Mojang. Los números de los tooltips replican EgyptRelics#tooltipArgs: si cambia esa fórmula,
actualizar TOOLTIP_ARGS aquí.

Uso: python make_viewer.py <salida.html>
"""
import base64
import json
import os
import re
import sys

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
COMMON = os.path.join(ROOT, "common", "src", "main")
JAVA = os.path.join(COMMON, "java", "io", "github", "potatustudio", "mythicrelics")
ASSETS = os.path.join(COMMON, "resources", "assets", "mythicrelics")
ORDER = ["golden_scarab", "eye_of_horus", "ankh", "mummy_wraps", "anubis_scepter", "jackal_mask", "jackal_ring"]
SET_PIECES = {"anubis_scepter", "jackal_mask", "jackal_ring"}
SLOTS = {"golden_scarab": "legs/belt", "eye_of_horus": "head/face", "ankh": "chest/necklace",
         "mummy_wraps": "hand/glove", "anubis_scepter": "mainhand", "jackal_mask": "head/face",
         "jackal_ring": "hand/ring"}


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def b64(path):
    with open(path, "rb") as f:
        return base64.b64encode(f.read()).decode()


def constants():
    src = read(os.path.join(JAVA, "egypt", "EgyptRelics.java"))
    return {name: float(value) for name, value in
            re.findall(r"public static final (?:float|int) (\w+) = ([\d.]+)f?;", src)}


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
    }


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
    return {item: r.lower() for item, r in re.findall(r'relic\("(\w+)", p -> p\.rarity\(Rarity\.(\w+)\)', src)}


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
                   "village_desert_house": "loot.mythicrelics.chests.village.village_desert_house"}

if __name__ == "__main__":
    out = sys.argv[1]
    data = {
        "order": ORDER,
        "setPieces": sorted(SET_PIECES),
        "slots": SLOTS,
        "args": tooltip_args(constants()),
        "loot": loot(),
        "lootKeys": LOOT_TABLE_KEYS,
        "rarity": rarity(),
        "icons": icons(),
        "lang": lang(),
        "modIcon": b64(os.path.join(ASSETS, "icon.png")),
        "scarabTexture": b64(os.path.join(ASSETS, "textures", "entity", "scarab.png")),
        "jackalTexture": b64(os.path.join(ASSETS, "textures", "entity", "jackal.png")),
        "trinkets": {name: json.loads(read(os.path.join(ASSETS, "trinkets", name + ".json")))
                     for name in ORDER if os.path.exists(os.path.join(ASSETS, "trinkets", name + ".json"))},
    }
    template = read(os.path.join(os.path.dirname(os.path.abspath(__file__)), "viewer_template.html"))
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        f.write(template.replace("__DATA__", json.dumps(data, ensure_ascii=False)))
    print(out)
