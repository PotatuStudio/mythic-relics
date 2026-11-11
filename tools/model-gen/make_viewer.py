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
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import (ARCHAEOLOGY, ASSETS, CULTURES, FABRIC_ASSETS, ORDER, SETS, SLOTS, LOOT_TABLE_KEYS, b64, constants, icons, lang, loot,  # noqa: E402
                     rarity, read, set_args, tooltip_args)

if __name__ == "__main__":
    out = sys.argv[1]
    data = {
        "order": ORDER,
        "cultures": CULTURES,
        "sets": SETS,
        "setArgs": set_args(constants()),
        "archaeology": sorted(ARCHAEOLOGY),
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
        "cerberusTexture": b64(os.path.join(ASSETS, "textures", "entity", "cerberus.png")),
        "sleipnirTexture": b64(os.path.join(ASSETS, "textures", "entity", "sleipnir.png")),
        "sleipnirGlow": b64(os.path.join(ASSETS, "textures", "entity", "sleipnir_glow.png")),
        "trinkets": {name: json.loads(read(os.path.join(FABRIC_ASSETS, "trinkets", name + ".json")))
                     for name in ORDER if os.path.exists(os.path.join(FABRIC_ASSETS, "trinkets", name + ".json"))},
        "wornModels": {name: json.loads(read(os.path.join(ASSETS, "models", "item", name + "_worn.json")))
                       for name in ORDER if os.path.exists(os.path.join(ASSETS, "models", "item", name + "_worn.json"))},
        "wornPalette": b64(os.path.join(ASSETS, "textures", "item", "worn_palette.png")),
    }
    # Entidades chinas: la geometría sale del mismo diseño que ChinaModelLayers.java (china_entities.py) y las texturas,
    # de los archivos del mod.
    import china_entities as CE  # noqa: E402
    data["china"] = {}
    for key, make, width in (("monkey", CE.monkey, 64), ("dragon", CE.dragon, 256)):
        parts, _img, _glow, tex_w, tex_h = CE.bake(make(), width)
        name = CE.NAMES[key][1]
        data["china"][key] = {"parts": parts, "texW": tex_w, "texH": tex_h, "rootOffset": list(CE.ROOT_OFFSET[key]),
                              "tex": b64(os.path.join(ASSETS, "textures", "entity", name + ".png")),
                              "glow": b64(os.path.join(ASSETS, "textures", "entity", name + "_glow.png"))}
    import china2_entities as C2  # noqa: E402
    for key in C2.MODELS:
        parts, _img, _glow, tex_w, tex_h = C2.bake_one(key)
        name = CE.NAMES[key][1]
        data["china"][key] = {"parts": parts, "texW": tex_w, "texH": tex_h, "rootOffset": list(CE.ROOT_OFFSET[key]),
                              "scale": C2.SCALE[key],
                              "tex": b64(os.path.join(ASSETS, "textures", "entity", name + ".png")),
                              "glow": b64(os.path.join(ASSETS, "textures", "entity", name + "_glow.png"))}
    import japan_entities as JE  # noqa: E402
    for key in JE.MODELS:
        parts, _img, _glow, tex_w, tex_h = JE.bake_one(key)
        name = CE.NAMES[key][1]
        data["china"][key] = {"parts": parts, "texW": tex_w, "texH": tex_h, "rootOffset": list(CE.ROOT_OFFSET[key]),
                              "scale": JE.SCALE[key],
                              "tex": b64(os.path.join(ASSETS, "textures", "entity", name + ".png")),
                              "glow": b64(os.path.join(ASSETS, "textures", "entity", name + "_glow.png"))}
    template = read(os.path.join(os.path.dirname(os.path.abspath(__file__)), "viewer_template.html"))
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        f.write(template.replace("__DATA__", json.dumps(data, ensure_ascii=False)))
    print(out)
