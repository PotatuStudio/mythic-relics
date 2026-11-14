"""Íconos 16x16 de la tanda 3 griega (aprobados el 2026-11-14), sus modelos de ítem y el Escudo de Aquiles en 3D.
Los dibujos son los de la página de diseño (tools/design-page/greece3_draft.py; el rayo, el de greece3_bolts.py).
El rayo y la harpe se sostienen como herramientas. La piel de Nemea tiene su modelo puesto en worn_models.py.

Escudo de Aquiles: como el Ancile (rome_models.py), mismo lugar y mismas transformaciones que el escudo vanilla, pero
redondo, con los anillos del mundo grabado (Ilíada XVIII): borde de océano, bronce, banda de oro, bronce y el centro de
plata. En el inventario se usa el ícono plano.
Uso: python greece3_icons.py
"""
import json
import math
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "..", "design-page"))
import greece3_draft as CD  # noqa: E402
from greece_icons import save  # noqa: E402
from mclib import H, png  # noqa: E402
from moddata import ASSETS  # noqa: E402
from rome_models import shield_display  # noqa: E402

NS = "mythicrelics"
HANDHELD = {"zeus_thunderbolt", "harpe"}
WORN = {"nemean_lion_skin"}
SHIELD = {"achilles_shield"}

# Paleta de 2x2 por color para el escudo.
COLORS = {"ocean_l": "#8fe6dc", "ocean": "#2f9ba0", "ocean_d": "#175a6e", "bronze_l": "#ffd29a", "bronze": "#e0954a",
          "bronze_d": "#a85f26", "gold_l": "#fff3a8", "gold": "#f5c842", "gold_d": "#c98f1c", "silver_l": "#ffffff",
          "silver": "#dcdcdc", "silver_d": "#a8a8b0", "leather": "#8a5a30", "leather_d": "#5e3a1a"}
NAMES = list(COLORS)
SHADE = {"ocean": ("ocean_l", "ocean_d"), "bronze": ("bronze_l", "bronze_d"), "gold": ("gold_l", "gold_d"),
         "silver": ("silver_l", "silver_d"), "leather": ("leather", "leather_d")}


def uv(color):
    i = NAMES.index(color)
    u, v = (i % 8) * 2, (i // 8) * 2
    return [u + 0.5, v + 0.5, u + 1.5, v + 1.5]


def box(frm, to, color, name=""):
    top, bottom = SHADE.get(color, (color, color))
    faces = {s: {"uv": uv(color), "texture": "#p"} for s in ("north", "south", "east", "west")}
    faces["up"] = {"uv": uv(top), "texture": "#p"}
    faces["down"] = {"uv": uv(bottom), "texture": "#p"}
    return {"name": name, "from": [round(v, 3) for v in frm], "to": [round(v, 3) for v in to], "faces": faces}


def palette():
    img = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for i, name in enumerate(NAMES):
        u, v = (i % 8) * 2, (i // 8) * 2
        for y in range(2):
            for x in range(2):
                img[v + y][u + x] = H(COLORS[name])
    return img


def shield_elements():
    """Placa redonda de 15 px de diámetro (centro x 8, y 8) en franjas de 1 px; cada anillo es una franja más adelante
    (z 10 es el frente), así los anillos quedan en relieve sin caras coplanares. Asa de cuero atrás como el vanilla."""
    e = []
    cx, cy, R = 8.0, 8.0, 7.5
    rings = [(7.5, "ocean", 9.0, 10.0), (6.4, "bronze", 10.0, 10.25), (5.2, "gold", 10.25, 10.45),
             (3.8, "bronze", 10.45, 10.6), (2.4, "silver", 10.6, 10.8), (1.0, "gold", 10.8, 11.0)]
    for r, color, z0, z1 in rings:
        for y in range(int(cy - r), int(math.ceil(cy + r))):
            dy = abs(y + 0.5 - cy)
            if dy >= r:
                continue
            half = round(math.sqrt(r * r - dy * dy) * 2) / 2
            if half <= 0:
                continue
            e.append(box([cx - half, y, z0], [cx + half, y + 1, z1], color, f"anillo {color}"))
    e.append(box([7.0, 5.0, 3.0], [9.0, 11.0, 9.0], "leather", "asa"))
    return e


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1)
        f.write("\n")


if __name__ == "__main__":
    textures = os.path.join(ASSETS, "textures", "item")
    for name, make in CD.ICONS.items():
        save(textures, name, make())
        parent = "minecraft:item/handheld" if name in HANDHELD else "minecraft:item/generated"
        write(os.path.join(ASSETS, "models", "item", name + ".json"), {"parent": parent, "textures": {"layer0": f"{NS}:item/{name}"}})
        if name in WORN or name in SHIELD:
            continue
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": {"type": "minecraft:model", "model": f"{NS}:item/{name}"}})
    # Escudo de Aquiles: modelo 3D para sostener y bloquear, ícono plano en el inventario.
    png(os.path.join(textures, "greece3_palette.png"), palette())
    models = os.path.join(ASSETS, "models", "item")
    write(os.path.join(models, "achilles_shield_3d.json"), {
        "gui_light": "front", "textures": {"p": f"{NS}:item/greece3_palette", "particle": f"{NS}:item/greece3_palette"},
        "elements": shield_elements(), "display": shield_display(False)})
    write(os.path.join(models, "achilles_shield_blocking.json"), {
        "parent": f"{NS}:item/achilles_shield_3d", "display": shield_display(True)})
    icon = {"type": "minecraft:model", "model": f"{NS}:item/achilles_shield"}
    write(os.path.join(ASSETS, "items", "achilles_shield.json"), {"model": {
        "type": "minecraft:select", "property": "minecraft:display_context",
        "cases": [{"when": ["gui", "ground", "fixed", "on_shelf"], "model": icon}],
        "fallback": {"type": "minecraft:condition", "property": "minecraft:using_item",
                     "on_true": {"type": "minecraft:model", "model": f"{NS}:item/achilles_shield_blocking"},
                     "on_false": {"type": "minecraft:model", "model": f"{NS}:item/achilles_shield_3d"}}}})
    print("ok:", ", ".join(CD.ICONS))
