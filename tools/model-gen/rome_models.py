"""Modelos de Roma que no son reliquias puestas: el Ancile (escudo 3D) y el Paladio (bloque).

Ancile: mismo lugar y mismas transformaciones que el escudo vanilla. El escudo vanilla es un modelo especial
(ShieldModel: placa box(-6,-11,-2, 12,22,1) y asa box(-1,-3,-1, 2,6,6), con la transformación scale(1,-1,-1) de
items/shield.json y el corrimiento de -0,5 del renderizador). Pasado a coordenadas de modelo de bloque eso da la placa
en x 2..14, y -3..19, z 9..10 y el asa en x 7..9, y 5..11, z 3..9; con esas coordenadas y los "display" de
models/item/shield.json y shield_blocking.json, el Ancile se sostiene y bloquea igual que un escudo normal. En el
inventario, en el suelo y en marcos se usa el ícono plano.

Paladio: estatuilla con base, túnica, cabeza, yelmo con cimera, escudo y lanza, con una paleta propia de bloque.
Uso: python rome_models.py
"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from mclib import H, png  # noqa: E402
from moddata import ASSETS, ROOT  # noqa: E402

NS = "mythicrelics"
DATA = os.path.join(ROOT, "common", "src", "main", "resources", "data")

# Paleta de 2x2 por color (como worn_palette), para el escudo (como textura de ítem) y el bloque.
COLORS = {"bronze_l": "#ffd29a", "bronze": "#e0954a", "bronze_d": "#a85f26", "gold_l": "#fff3a8", "gold": "#f5c842",
          "gold_d": "#c98f1c", "leather": "#8a5a30", "leather_d": "#5e3a1a", "wood_l": "#d8a868", "wood": "#a8743a",
          "wood_d": "#6e4a22", "silver": "#e8e8ec", "silver_d": "#a8a8b0", "stone": "#bdb6a8", "stone_d": "#8a8478"}
NAMES = list(COLORS)
SHADE = {"bronze": ("bronze_l", "bronze_d"), "gold": ("gold_l", "gold_d"), "leather": ("leather", "leather_d"),
         "wood": ("wood_l", "wood_d"), "silver": ("silver", "silver_d"), "stone": ("stone", "stone_d")}


def uv(color):
    i = NAMES.index(color)
    u, v = (i % 8) * 2, (i // 8) * 2
    return [u + 0.5, v + 0.5, u + 1.5, v + 1.5]


def box(frm, to, color, texture="#p", name=""):
    top, bottom = SHADE.get(color, (color, color))
    faces = {s: {"uv": uv(color), "texture": texture} for s in ("north", "south", "east", "west")}
    faces["up"] = {"uv": uv(top), "texture": texture}
    faces["down"] = {"uv": uv(bottom), "texture": texture}
    return {"name": name, "from": list(frm), "to": list(to), "faces": faces}


def palette():
    img = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for i, name in enumerate(NAMES):
        u, v = (i % 8) * 2, (i // 8) * 2
        for y in range(2):
            for x in range(2):
                img[v + y][u + x] = H(COLORS[name])
    return img


# ---- Ancile -------------------------------------------------------------------------------------------------------
# Ancho de la placa por franja de 1 px de alto (y de -3 a 19): dos lóbulos y la cintura entre ambos.
def ancile_widths():
    out = []
    for y in range(-3, 19):
        cy, r = (2.5, 5.6) if y < 8 else (14.0, 5.2)
        dy = abs(y + 0.5 - cy)
        half = (r * r - dy * dy) ** 0.5 if dy < r else 0
        if 7 <= y <= 8:
            half = 2.5   # la cintura del ocho
        out.append((y, round(min(half, 6.0) * 2) / 2))
    return out


def ancile_elements():
    e = []
    for y, half in ancile_widths():
        if half <= 0:
            continue
        x0, x1 = 8 - half, 8 + half
        # Placa de bronce con borde de oro a los costados. La cara de adelante es z = 10 (el asa queda atrás).
        e.append(box([x0, y, 9.0], [x1, y + 1, 10.0], "bronze", name="placa"))
        e.append(box([x0 - 0.01, y, 8.9], [x0 + 0.5, y + 1, 10.1], "gold", name="borde"))
        e.append(box([x1 - 0.5, y, 8.9], [x1 + 0.01, y + 1, 10.1], "gold", name="borde"))
    e.append(box([7.5, -2.5, 10.0], [8.5, 18.5, 10.3], "bronze", name="nervio"))
    e.append(box([6.5, 6.5, 10.0], [9.5, 9.5, 10.6], "gold", name="umbo"))
    e.append(box([7.0, 5.0, 3.0], [9.0, 11.0, 9.0], "leather", name="asa"))
    return e


def shield_display(blocking):
    """Los "display" del escudo vanilla (models/item/shield.json y shield_blocking.json)."""
    if blocking:
        return {
            "thirdperson_righthand": {"rotation": [45, 155, 0], "translation": [-3.49, 11, -2], "scale": [1, 1, 1]},
            "thirdperson_lefthand": {"rotation": [45, 155, 0], "translation": [11.51, 7, 2.5], "scale": [1, 1, 1]},
            "firstperson_righthand": {"rotation": [0, 180, -5], "translation": [-15, 3.25, -11], "scale": [1.25, 1.25, 1.25]},
            "firstperson_lefthand": {"rotation": [0, 180, -5], "translation": [5, 5, -11], "scale": [1.25, 1.25, 1.25]},
        }
    return {
        "thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [10, 6, -4], "scale": [1, 1, 1]},
        "thirdperson_lefthand": {"rotation": [0, 90, 0], "translation": [10, 6, 12], "scale": [1, 1, 1]},
        "firstperson_righthand": {"rotation": [0, 180, 5], "translation": [-10, 1.75, -10], "scale": [1.25, 1.25, 1.25]},
        "firstperson_lefthand": {"rotation": [0, 180, 5], "translation": [10, 0, -10], "scale": [1.25, 1.25, 1.25]},
    }


# ---- Paladio ------------------------------------------------------------------------------------------------------
def palladium_elements():
    """Estatuilla de Palas Atenea sobre un pedestal de dos escalones: peplo con pliegues, égida dorada con la gorgona en el
    pecho, yelmo con cimera alta, escudo redondo en la izquierda y la lanza en alto en la derecha, apoyada sobre el
    escalón (no lo atraviesa). La cara mira al frente (norte, z bajo)."""
    t = "#p"
    return [
        box([3, 0, 3], [13, 1.5, 13], "stone", t, "pedestal"),
        box([4, 1.5, 4], [12.8, 2.5, 12], "stone", t, "escalón"),
        box([6, 2.5, 6.2], [10, 8, 10], "wood", t, "peplo"),
        box([6.6, 2.5, 6.0], [7.0, 7.6, 6.2], "wood_d", t, "pliegue"),
        box([7.8, 2.5, 6.0], [8.2, 7.6, 6.2], "wood_d", t, "pliegue"),
        box([9.0, 2.5, 6.0], [9.4, 7.6, 6.2], "wood_d", t, "pliegue"),
        box([5.8, 7.4, 6.0], [10.2, 8.0, 10.2], "gold", t, "cinturón"),
        box([6.3, 8, 6.5], [9.7, 10.5, 9.7], "wood", t, "torso"),
        box([6.6, 8.5, 6.3], [9.4, 10.3, 6.5], "gold", t, "égida"),
        box([7.6, 9.0, 6.1], [8.4, 9.8, 6.3], "bronze", t, "gorgona"),
        box([5.6, 9.8, 6.8], [10.4, 10.6, 9.4], "wood", t, "hombros"),
        box([6.8, 10.6, 6.8], [9.2, 12.6, 9.2], "wood_l", t, "cara"),
        box([6.6, 11.6, 6.6], [9.4, 13.0, 9.4], "bronze", t, "yelmo"),
        box([7.6, 13.0, 6.2], [8.4, 15.2, 10.2], "gold", t, "cimera"),
        box([7.6, 14.6, 10.2], [8.4, 15.2, 11.2], "gold", t, "penacho"),
        box([5.0, 7.6, 7.4], [5.6, 10.0, 8.6], "wood", t, "brazo izq."),
        box([3.4, 4.6, 6.0], [4.6, 9.8, 10.0], "bronze", t, "escudo"),
        box([3.1, 6.6, 7.4], [3.4, 7.8, 8.6], "gold", t, "umbo"),
        box([10.4, 8.0, 7.4], [11.2, 10.6, 8.4], "wood", t, "brazo der."),
        box([11.0, 10.2, 7.4], [11.7, 11.0, 8.4], "wood_l", t, "mano"),
        box([11.7, 2.5, 7.6], [12.3, 14.6, 8.2], "wood_d", t, "lanza"),
        box([11.6, 14.6, 7.5], [12.4, 16.4, 8.3], "silver", t, "punta"),
    ]


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


if __name__ == "__main__":
    png(os.path.join(ASSETS, "textures", "item", "rome_palette.png"), palette())
    os.makedirs(os.path.join(ASSETS, "textures", "block"), exist_ok=True)
    png(os.path.join(ASSETS, "textures", "block", "palladium.png"), palette())
    models = os.path.join(ASSETS, "models")
    # Ancile: modelo base con la geometría y dos hijos con los "display" de sostener y de bloquear.
    write(os.path.join(models, "item", "ancile_shield.json"), {
        "gui_light": "front", "textures": {"p": f"{NS}:item/rome_palette", "particle": f"{NS}:item/rome_palette"},
        "elements": ancile_elements(), "display": shield_display(False)})
    write(os.path.join(models, "item", "ancile_blocking.json"), {
        "parent": f"{NS}:item/ancile_shield", "display": shield_display(True)})
    icon = {"type": "minecraft:model", "model": f"{NS}:item/ancile"}
    write(os.path.join(ASSETS, "items", "ancile.json"), {"model": {
        "type": "minecraft:select", "property": "minecraft:display_context",
        "cases": [{"when": ["gui", "ground", "fixed", "on_shelf"], "model": icon}],
        "fallback": {"type": "minecraft:condition", "property": "minecraft:using_item",
                     "on_true": {"type": "minecraft:model", "model": f"{NS}:item/ancile_blocking"},
                     "on_false": {"type": "minecraft:model", "model": f"{NS}:item/ancile_shield"}}}})
    # Paladio: bloque orientable, ícono plano en el inventario, se suelta a sí mismo y se rompe con hacha.
    write(os.path.join(models, "block", "palladium.json"), {
        "textures": {"p": f"{NS}:block/palladium", "particle": f"{NS}:block/palladium"}, "elements": palladium_elements()})
    write(os.path.join(ASSETS, "blockstates", "palladium.json"), {"variants": {
        f"facing={f}": {"model": f"{NS}:block/palladium", **({"y": y} if y else {})}
        for f, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270))}})
    write(os.path.join(ASSETS, "items", "palladium.json"), {"model": {"type": "minecraft:model", "model": f"{NS}:item/palladium"}})
    write(os.path.join(DATA, NS, "loot_table", "blocks", "palladium.json"), {
        "type": "minecraft:block", "random_sequence": f"{NS}:blocks/palladium",
        "pools": [{"rolls": 1, "bonus_rolls": 0, "entries": [{"type": "minecraft:item", "name": f"{NS}:palladium"}],
                   "conditions": [{"condition": "minecraft:survives_explosion"}]}]})
    write(os.path.join(DATA, "minecraft", "tags", "block", "mineable", "axe.json"), {"replace": False, "values": [f"{NS}:palladium"]})
    print("ok: ancile, palladium")
