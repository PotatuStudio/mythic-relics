"""Íconos 16x16 de la tanda 2 japonesa (aprobados el 2026-11-10), sus modelos de ítem y el bloque de la muñeca de
zashiki-warashi. Los dibujos son los de la página de diseño (tools/design-page/japan2_draft.py). El hacha de Kintarō y
la lanza se sostienen como herramientas. La máscara de tengu y el plato del kappa tienen su modelo puesto en
worn_models.py. Uso: python japan2_icons.py
"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "..", "design-page"))
import japan2_draft as CD  # noqa: E402
from greece_icons import save  # noqa: E402
from mclib import H, png  # noqa: E402
from moddata import ASSETS, ROOT  # noqa: E402

NS = "mythicrelics"
DATA = os.path.join(ROOT, "common", "src", "main", "resources", "data")
HANDHELD = {"kintaro_axe", "ame_no_nuboko"}
WORN = {"tengu_mask", "kappa_dish"}

# Muñeca: paleta de bloque de 2x2 por color, como el Paladio.
COLORS = {"red_l": "#e8524a", "red": "#c42a20", "red_d": "#7e1712", "gold_l": "#ffe27a", "gold": "#f0c040",
          "gold_d": "#c8901e", "skin_l": "#fff8f0", "skin": "#f6e6d6", "skin_d": "#d8c2ac", "hair_l": "#3e383c",
          "hair": "#1c1719", "hair_d": "#0e0b0c", "wood_l": "#d8a868", "wood": "#a8743a", "wood_d": "#6e4a22",
          "white": "#f4f1e6"}
NAMES = list(COLORS)
SHADE = {"red": ("red_l", "red_d"), "gold": ("gold_l", "gold_d"), "skin": ("skin_l", "skin_d"),
         "hair": ("hair_l", "hair_d"), "wood": ("wood_l", "wood_d")}


def uv(color):
    i = NAMES.index(color)
    u, v = (i % 8) * 2, (i // 8) * 2
    return [u + 0.5, v + 0.5, u + 1.5, v + 1.5]


def box(frm, to, color, name=""):
    top, bottom = SHADE.get(color, (color, color))
    faces = {s: {"uv": uv(color), "texture": "#p"} for s in ("north", "south", "east", "west")}
    faces["up"] = {"uv": uv(top), "texture": "#p"}
    faces["down"] = {"uv": uv(bottom), "texture": "#p"}
    return {"name": name, "from": list(frm), "to": list(to), "faces": faces}


def palette():
    img = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for i, name in enumerate(NAMES):
        u, v = (i % 8) * 2, (i // 8) * 2
        for y in range(2):
            for x in range(2):
                img[v + y][u + x] = H(COLORS[name])
    return img


def doll_elements():
    """Muñeca de niña con kimono rojo, obi dorado, cara blanca y pelo negro con flequillo; la cara mira al norte (z bajo),
    que el estado del bloque gira hacia quien la coloca."""
    return [
        box([5, 0, 5], [11, 1, 11], "wood", "base"),
        box([5.5, 1, 6], [10.5, 7, 10], "red", "kimono"),
        box([5.4, 4.4, 5.9], [10.6, 5.8, 10.1], "gold", "obi"),
        box([4.5, 4, 6.5], [5.5, 7, 9.5], "red", "manga der."),
        box([10.5, 4, 6.5], [11.5, 7, 9.5], "red", "manga izq."),
        box([7.2, 6.2, 5.9], [8.8, 7, 6.0], "white", "cuello"),
        box([6, 7, 6], [10, 11, 10], "skin", "cabeza"),
        box([5.8, 9.6, 5.8], [10.2, 11.6, 10.2], "hair", "flequillo"),
        box([5.8, 7.4, 8.4], [10.2, 9.6, 10.2], "hair", "pelo atrás"),
        box([6.8, 8.6, 5.8], [7.4, 9.0, 6.0], "hair", "ojo der."),
        box([8.6, 8.6, 5.8], [9.2, 9.0, 6.0], "hair", "ojo izq."),
        box([7.7, 7.7, 5.8], [8.3, 7.9, 6.0], "red", "boca"),
    ]


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


if __name__ == "__main__":
    textures = os.path.join(ASSETS, "textures", "item")
    for name, make in CD.ICONS.items():
        save(textures, name, make())
        parent = "minecraft:item/handheld" if name in HANDHELD else "minecraft:item/generated"
        write(os.path.join(ASSETS, "models", "item", name + ".json"), {"parent": parent, "textures": {"layer0": f"{NS}:item/{name}"}})
        if name in WORN:
            continue
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": {"type": "minecraft:model", "model": f"{NS}:item/{name}"}})
    # Muñeca de zashiki-warashi: bloque orientable con ícono plano; se suelta a sí misma.
    os.makedirs(os.path.join(ASSETS, "textures", "block"), exist_ok=True)
    png(os.path.join(ASSETS, "textures", "block", "zashiki_warashi.png"), palette())
    write(os.path.join(ASSETS, "models", "block", "zashiki_warashi.json"), {
        "textures": {"p": f"{NS}:block/zashiki_warashi", "particle": f"{NS}:block/zashiki_warashi"}, "elements": doll_elements()})
    write(os.path.join(ASSETS, "blockstates", "zashiki_warashi.json"), {"variants": {
        f"facing={f}": {"model": f"{NS}:block/zashiki_warashi", **({"y": y} if y else {})}
        for f, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270))}})
    write(os.path.join(DATA, NS, "loot_table", "blocks", "zashiki_warashi.json"), {
        "type": "minecraft:block", "random_sequence": f"{NS}:blocks/zashiki_warashi",
        "pools": [{"rolls": 1, "bonus_rolls": 0, "entries": [{"type": "minecraft:item", "name": f"{NS}:zashiki_warashi"}],
                   "conditions": [{"condition": "minecraft:survives_explosion"}]}]})
    print("ok:", ", ".join(CD.ICONS))
