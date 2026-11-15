"""Égida de Atenea como escudo de mano (cambio del 2026-11-15: antes se ponía en la espalda).

Mismo lugar y mismas transformaciones que el escudo vanilla, como el Escudo de Aquiles (greece3_icons.py): placa redonda
centrada en el origen, frente hacia +z, asa de cuero atrás. Borde de oro y placa de bronce; al centro, el gorgoneion: la
MISMA cabeza de Medusa que la reliquia (head_items.medusa, sin el cuello cortado), achatada en relieve sobre la placa,
para que las dos se vean como la misma Gorgona. Textura pintada por cara con la paleta de las cabezas (worn_texture).
En el inventario se usa el ícono plano de siempre (greece_icons.py).
Uso: python aegis_shield.py"""
import json
import math
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import head_items  # noqa: E402
import worn_texture  # noqa: E402
from head_items import PAL, box  # noqa: E402
from moddata import ASSETS  # noqa: E402
from rome_models import shield_display  # noqa: E402

NS = "mythicrelics"
NAME = "athena_aegis_3d"
# Medusa sobre la placa: escala de la cara y profundidad del relieve; el cráneo (x 4..12, y 0..8, z 4..12) se centra en
# el escudo y su nuca queda apoyada en el frente de la placa.
FACE_SCALE = 0.6
DEPTH_SCALE = 0.3
HEAD_CENTER = (8.0, 4.6)
PLATE_FRONT = 2.2


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


def gorgoneion():
    """La cabeza de Medusa de la reliquia, achatada y centrada sobre la placa."""
    cx, cy = HEAD_CENTER
    z_back = min(min(el["from"][2], el["to"][2]) for el in head_items.medusa(neck=False))
    out = []
    for el in head_items.medusa(neck=False):
        el = dict(el)
        f, t = el["from"], el["to"]
        el["from"] = [round((f[0] - cx) * FACE_SCALE, 3), round((f[1] - cy) * FACE_SCALE, 3),
                      round(PLATE_FRONT + (f[2] - z_back) * DEPTH_SCALE, 3)]
        el["to"] = [round((t[0] - cx) * FACE_SCALE, 3), round((t[1] - cy) * FACE_SCALE, 3),
                    round(PLATE_FRONT + (t[2] - z_back) * DEPTH_SCALE, 3)]
        out.append(el)
    return out


def shield_elements():
    e = []
    e += disc(8.5, "gold", 1.0, 2.0, "borde de oro")
    e += disc(7.4, "bronze", 2.0, PLATE_FRONT, "placa de bronce")
    e += gorgoneion()
    e.append(box([-1.0, -3.0, -5.0], [1.0, 3.0, 1.0], "leather", "asa"))
    return e


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


if __name__ == "__main__":
    m, img = worn_texture.texture(NAME, shield_elements(), PAL, NS)
    for el in m["elements"]:
        el.pop("__color", None)
        for face in el["faces"].values():
            face["texture"] = "#t"
    m["textures"] = {"t": f"{NS}:item/painted/{NAME}", "particle": f"{NS}:item/painted/{NAME}"}
    m["display"] = shield_display(False, 3.75)
    m["gui_light"] = "front"
    img.save(os.path.join(ASSETS, "textures", "item", "painted", NAME + ".png"))
    models = os.path.join(ASSETS, "models", "item")
    write(os.path.join(models, NAME + ".json"), m)
    write(os.path.join(models, "athena_aegis_blocking.json"), {
        "parent": f"{NS}:item/{NAME}", "display": shield_display(True, 3.75)})
    icon = {"type": "minecraft:model", "model": f"{NS}:item/athena_aegis"}
    write(os.path.join(ASSETS, "items", "athena_aegis.json"), {"model": {
        "type": "minecraft:select", "property": "minecraft:display_context",
        "cases": [{"when": ["gui", "ground", "fixed", "on_shelf"], "model": icon}],
        "fallback": {"type": "minecraft:condition", "property": "minecraft:using_item",
                     "on_true": {"type": "minecraft:model", "model": f"{NS}:item/athena_aegis_blocking"},
                     "on_false": {"type": "minecraft:model", "model": f"{NS}:item/{NAME}"}}}})
    old = os.path.join(ASSETS, "textures", "item", "aegis_palette.png")
    if os.path.exists(old):
        os.remove(old)
    print("ok:", NAME, len(m["elements"]), "piezas")
