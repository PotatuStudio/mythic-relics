"""Égida de Atenea como escudo de mano (cambio del 2026-11-15: antes se ponía en la espalda).

Mismo lugar y mismas transformaciones que el escudo vanilla, como el Escudo de Aquiles (greece3_icons.py): placa redonda
centrada en el origen, frente hacia +z, asa de cuero atrás. Borde de oro, bronce, y al centro el gorgoneion en relieve:
corona de serpientes verdes con cabezas hacia afuera y la cara de piedra con los ojos abiertos, colmillos y la lengua
afuera. En el inventario se usa el ícono plano de siempre (greece_icons.py).
Uso: python aegis_shield.py"""
import json
import math
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from mclib import H, png  # noqa: E402
from moddata import ASSETS  # noqa: E402
from rome_models import shield_display  # noqa: E402

NS = "mythicrelics"
COLORS = {"gold_l": "#fff3a8", "gold": "#f5c842", "gold_d": "#c98f1c", "bronze_l": "#ffd29a", "bronze": "#e0954a",
          "bronze_d": "#a85f26", "snake_l": "#b6e27a", "snake": "#5fa13a", "snake_d": "#2f6420", "stone_l": "#dfe6d6",
          "stone": "#a9b5a0", "stone_d": "#6f7d68", "eye": "#ffe14a", "dark": "#1a0f08", "tongue": "#d8392b",
          "fang": "#ffffff", "leather": "#8a5a30", "leather_d": "#5e3a1a"}
NAMES = list(COLORS)
SHADE = {"gold": ("gold_l", "gold_d"), "bronze": ("bronze_l", "bronze_d"), "snake": ("snake_l", "snake_d"),
         "stone": ("stone_l", "stone_d"), "leather": ("leather", "leather_d")}


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


def disc(r, color, z0, z1, name):
    """Disco de radio r en franjas de 1 px, centrado en el origen."""
    e = []
    for y in range(int(-r), int(math.ceil(r))):
        dy = abs(y + 0.5)
        if dy >= r:
            continue
        half = round(math.sqrt(r * r - dy * dy) * 2) / 2
        if half > 0:
            e.append(box([-half, y, z0], [half, y + 1, z1], color, name))
    return e


def shield_elements():
    e = []
    e += disc(8.5, "gold", 1.0, 2.0, "borde de oro")
    e += disc(7.4, "bronze", 2.0, 2.2, "placa de bronce")
    e += disc(4.8, "snake", 2.2, 2.5, "corona de serpientes")
    # Cabezas de serpiente hacia afuera, con ojo amarillo, alrededor de la cara.
    for k in range(8):
        a = math.radians(22.5 + k * 45)
        x, y = math.cos(a) * 5.1, math.sin(a) * 5.1
        e.append(box([x - 0.6, y - 0.6, 2.2], [x + 0.6, y + 0.6, 2.7], "snake", "cabeza de serpiente"))
        ex, ey = math.cos(a) * 5.4, math.sin(a) * 5.4
        e.append(box([ex - 0.2, ey - 0.2, 2.7], [ex + 0.2, ey + 0.2, 2.8], "eye", "ojo de serpiente"))
    e += disc(3.4, "stone", 2.5, 2.9, "cara de piedra")
    # Rasgos de la Gorgona, en relieve sobre la cara (y hacia arriba).
    for sx in (-1, 1):
        e.append(box([sx * 1.4 - 0.7, 0.4, 2.9], [sx * 1.4 + 0.7, 1.6, 3.05], "eye", "ojo abierto"))
        e.append(box([sx * 1.4 - 0.3, 0.6, 3.05], [sx * 1.4 + 0.3, 1.4, 3.15], "dark", "pupila"))
        e.append(box([sx * 1.4 - 0.9, 1.7, 2.9], [sx * 1.4 + 0.9, 2.1, 3.0], "stone_d", "ceja"))
    e.append(box([-0.4, -0.6, 2.9], [0.4, 0.4, 3.0], "stone_d", "nariz"))
    e.append(box([-1.5, -1.9, 2.9], [1.5, -1.1, 3.0], "dark", "boca"))
    e.append(box([-1.3, -1.5, 3.0], [-0.8, -1.1, 3.1], "fang", "colmillo"))
    e.append(box([0.8, -1.5, 3.0], [1.3, -1.1, 3.1], "fang", "colmillo"))
    e.append(box([-0.5, -2.9, 2.9], [0.5, -1.5, 3.1], "tongue", "lengua"))
    e.append(box([-1.0, -3.0, -5.0], [1.0, 3.0, 1.0], "leather", "asa"))
    return e


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


if __name__ == "__main__":
    png(os.path.join(ASSETS, "textures", "item", "aegis_palette.png"), palette())
    models = os.path.join(ASSETS, "models", "item")
    write(os.path.join(models, "athena_aegis_3d.json"), {
        "gui_light": "front", "textures": {"p": f"{NS}:item/aegis_palette", "particle": f"{NS}:item/aegis_palette"},
        "elements": shield_elements(), "display": shield_display(False, 3.75)})
    write(os.path.join(models, "athena_aegis_blocking.json"), {
        "parent": f"{NS}:item/athena_aegis_3d", "display": shield_display(True, 3.75)})
    icon = {"type": "minecraft:model", "model": f"{NS}:item/athena_aegis"}
    write(os.path.join(ASSETS, "items", "athena_aegis.json"), {"model": {
        "type": "minecraft:select", "property": "minecraft:display_context",
        "cases": [{"when": ["gui", "ground", "fixed", "on_shelf"], "model": icon}],
        "fallback": {"type": "minecraft:condition", "property": "minecraft:using_item",
                     "on_true": {"type": "minecraft:model", "model": f"{NS}:item/athena_aegis_blocking"},
                     "on_false": {"type": "minecraft:model", "model": f"{NS}:item/athena_aegis_3d"}}}})
    print("ok: athena_aegis_3d")
    import paint_models  # noqa: E402  (textura pintada en lugar de la paleta plana)
    paint_models.run(['item/athena_aegis_3d'])
