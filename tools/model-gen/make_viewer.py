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
from moddata import (ASSETS, ORDER, SET_PIECES, SLOTS, LOOT_TABLE_KEYS, b64, constants, icons, lang, loot,  # noqa: E402
                     rarity, read, tooltip_args)

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
