"""Modelos 3D de Mjölnir y Gungnir (como el tridente vanilla): en la mano y lanzados se ven en 3D; en el inventario, en
el suelo y en marcos se usa el ícono plano. Paleta propia de 2x2 por color (como rome_palette).

- Mjölnir: cabeza corta y ancha de acero oscuro, con bandas de plata y nudos dorados en las caras, mango corto (el
  defecto que le dejó Loki) envuelto en cuero, y pomo con anilla.
- Gungnir: lanza larga de fresno con regatón de oro, casquillo dorado y punta de hoja de acero con runas de oro.

Uso: python norse_models.py  (escribe los modelos, la paleta y items/*.json en el mod)
"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from mclib import H, png  # noqa: E402
from moddata import ASSETS  # noqa: E402

NS = "mythicrelics"
COLORS = {"steel_l": "#a9b0bc", "steel": "#6e7480", "steel_d": "#3e434c", "silver": "#e8e8ec", "silver_d": "#a8a8b0",
          "gold_l": "#fff3a8", "gold": "#f5c842", "gold_d": "#c98f1c", "leather_l": "#9a6a3c", "leather": "#7a4e28",
          "leather_d": "#4e3018", "ash_l": "#e6cf9a", "ash": "#c8a868", "ash_d": "#8a6a3a", "rune": "#7fd4ff",
          "dark": "#1c1a20"}
NAMES = list(COLORS)
SHADE = {"steel": ("steel_l", "steel_d"), "silver": ("silver", "silver_d"), "gold": ("gold_l", "gold_d"),
         "leather": ("leather_l", "leather_d"), "ash": ("ash_l", "ash_d")}


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


def mjolnir():
    e = [box([4, 10, 5.5], [12, 15, 10.5], "steel", "cabeza")]
    # Bandas de plata un poco más grandes que la cabeza (no comparten planos con ella).
    for x0 in (4.6, 10.4):
        e.append(box([x0, 9.8, 5.3], [x0 + 1.0, 15.2, 10.7], "silver", "banda"))
    # Nudo dorado en las dos caras grandes (frente y atrás).
    e.append(box([6.6, 11.2, 5.2], [9.4, 13.8, 5.5], "gold", "nudo frente"))
    e.append(box([6.6, 11.2, 10.5], [9.4, 13.8, 10.8], "gold", "nudo atrás"))
    e.append(box([7.4, 12.0, 5.0], [8.6, 13.0, 5.2], "rune", "runa frente"))
    e.append(box([7.4, 12.0, 10.8], [8.6, 13.0, 11.0], "rune", "runa atrás"))
    # Mango corto envuelto en cuero, con anillos del envoltorio.
    e.append(box([7.25, 2, 7.25], [8.75, 10, 8.75], "leather", "mango"))
    for y0 in (3.0, 5.0, 7.0):
        e.append(box([7.1, y0, 7.1], [8.9, y0 + 0.6, 8.9], "leather_d", "vuelta"))
    e.append(box([7.0, 1.0, 7.0], [9.0, 2.0, 9.0], "gold", "pomo"))
    e.append(box([7.6, -0.4, 7.9], [8.4, 1.0, 8.1], "leather_d", "anilla"))
    return e


def gungnir():
    e = [box([7.5, -8, 7.5], [8.5, 24, 8.5], "ash", "asta"),
         box([7.3, -9, 7.3], [8.7, -7, 8.7], "gold", "regatón"),
         box([7.2, 24, 7.2], [8.8, 26, 8.8], "gold", "casquillo"),
         box([7.35, 10, 7.35], [8.65, 13, 8.65], "leather", "empuñadura")]
    # Punta de hoja: se ensancha y se afila; plana en z.
    for y0, y1, x0, x1 in ((26, 27.5, 6.6, 9.4), (27.5, 29.5, 6.0, 10.0), (29.5, 30.8, 6.6, 9.4), (30.8, 31.8, 7.3, 8.7)):
        e.append(box([x0, y0, 7.7], [x1, y1, 8.3], "steel", "hoja"))
    e.append(box([7.75, 26.6, 7.55], [8.25, 30.4, 8.45], "gold", "runas"))
    e.append(box([7.82, 27.4, 7.5], [8.18, 29.4, 8.5], "rune", "brillo de runas"))
    return e


# Cómo se sostienen: el martillo como un hacha; la lanza en alto, como el tridente.
MJOLNIR_DISPLAY = {
    "thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 2, 0.5], "scale": [0.85, 0.85, 0.85]},
    "thirdperson_lefthand": {"rotation": [0, -90, 0], "translation": [0, 2, 0.5], "scale": [0.85, 0.85, 0.85]},
    "firstperson_righthand": {"rotation": [0, 90, 20], "translation": [1.5, 3, 0], "scale": [0.68, 0.68, 0.68]},
    "firstperson_lefthand": {"rotation": [0, -90, -20], "translation": [1.5, 3, 0], "scale": [0.68, 0.68, 0.68]},
    "head": {"translation": [0, 13, 0]},
}
GUNGNIR_DISPLAY = {
    "thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, -2, 1], "scale": [0.85, 0.85, 0.85]},
    "thirdperson_lefthand": {"rotation": [0, -90, 0], "translation": [0, -2, 1], "scale": [0.85, 0.85, 0.85]},
    "firstperson_righthand": {"rotation": [0, 90, 15], "translation": [1, -2, 0], "scale": [0.68, 0.68, 0.68]},
    "firstperson_lefthand": {"rotation": [0, -90, -15], "translation": [1, -2, 0], "scale": [0.68, 0.68, 0.68]},
}
MODELS = {"mjolnir": (mjolnir, MJOLNIR_DISPLAY), "gungnir": (gungnir, GUNGNIR_DISPLAY)}


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


def model_json(name):
    make, display = MODELS[name]
    return {"gui_light": "front", "textures": {"p": f"{NS}:item/norse_palette", "particle": f"{NS}:item/norse_palette"},
            "elements": make(), "display": display}


if __name__ == "__main__":
    png(os.path.join(ASSETS, "textures", "item", "norse_palette.png"), palette())
    for name in MODELS:
        write(os.path.join(ASSETS, "models", "item", name + "_3d.json"), model_json(name))
        icon = {"type": "minecraft:model", "model": f"{NS}:item/{name}"}
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": {
            "type": "minecraft:select", "property": "minecraft:display_context",
            "cases": [{"when": ["gui", "ground", "fixed", "on_shelf"], "model": icon}],
            "fallback": {"type": "minecraft:model", "model": f"{NS}:item/{name}_3d"}}})
    print("ok:", ", ".join(MODELS))
