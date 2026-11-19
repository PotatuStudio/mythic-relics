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
from handheld3d import GRIP_Y, diagonal, handheld_display  # noqa: E402
from moddata import ASSETS, palette_path  # noqa: E402

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


def tri(lo, hi):
    """Del espacio del tridente vanilla (y hacia arriba, el asta bajo el origen) al del modelo: mitad de tamaño + 4, para
    caber en -16..32; el display lo dobla (scale 2), así se sostiene y se lanza exactamente como el tridente."""
    return [v / 2 + 4 for v in lo], [v / 2 + 4 for v in hi]


def gungnir():
    """Gungnir con las medidas del tridente vanilla: asta de fresno de 25 px bajo el casquillo, punta de hoja arriba."""
    parts = [((-0.5, -27, -0.5), (0.5, -2, 0.5), "ash", "asta"),
             ((-0.75, -29, -0.75), (0.75, -27, 0.75), "gold", "regatón"),
             ((-0.7, -15, -0.7), (0.7, -11, 0.7), "leather", "empuñadura"),
             ((-1.0, -2, -0.75), (1.0, 0, 0.75), "gold", "casquillo"),
             ((-1.2, 0, -0.35), (1.2, 1.2, 0.35), "steel", "hoja"),
             ((-1.8, 1.2, -0.35), (1.8, 3.4, 0.35), "steel", "hoja"),
             ((-1.2, 3.4, -0.35), (1.2, 4.8, 0.35), "steel", "hoja"),
             ((-0.6, 4.8, -0.35), (0.6, 6.2, 0.35), "steel", "punta"),
             ((-0.25, 0.4, -0.45), (0.25, 4.4, 0.45), "gold", "runas"),
             ((-0.15, 1.4, -0.5), (0.15, 3.2, 0.5), "rune", "brillo de runas")]
    return [box(*tri(lo, hi), color, name) for lo, hi, color, name in parts]


# Cómo se sostienen: el martillo como un hacha; la lanza en alto, como el tridente.
def mjolnir_held():
    """Mjölnir en la mano: el mismo martillo, bajado para que el agarre (justo sobre el pomo) quede en GRIP_Y y girado a
    la diagonal de la espada vanilla, con sus posiciones de mano (handheld3d.py)."""
    dy = GRIP_Y - 1.6
    moved = [{**el, "from": [el["from"][0], el["from"][1] + dy, el["from"][2]], "to": [el["to"][0], el["to"][1] + dy, el["to"][2]]}
             for el in mjolnir()]
    return diagonal(moved)


MJOLNIR_DISPLAY = handheld_display()
# Las mismas transformaciones que trident_in_hand y trident_throwing de vanilla, con scale 2 (ver tri()).
def _x2(d):
    return {k: {**v, "scale": [s * 2 for s in v.get("scale", [1, 1, 1])]} for k, v in d.items()}


GUNGNIR_DISPLAY = _x2({
    "thirdperson_righthand": {"rotation": [0, 60, 0], "translation": [11, 17, -2]},
    "thirdperson_lefthand": {"rotation": [0, 60, 0], "translation": [3, 17, 12]},
    "firstperson_righthand": {"rotation": [0, -90, 25], "translation": [-3, 17, 1]},
    "firstperson_lefthand": {"rotation": [0, 90, -25], "translation": [13, 17, 1]},
})
GUNGNIR_THROWING = _x2({
    "thirdperson_righthand": {"rotation": [0, 90, 180], "translation": [8, -17, 9]},
    "thirdperson_lefthand": {"rotation": [0, 90, 180], "translation": [8, -17, -7]},
    "firstperson_righthand": {"rotation": [0, -90, 25], "translation": [-3, 17, 1]},
    "firstperson_lefthand": {"rotation": [0, 90, -25], "translation": [13, 17, 1]},
})
MODELS = {"mjolnir": (mjolnir_held, MJOLNIR_DISPLAY), "gungnir": (gungnir, GUNGNIR_DISPLAY)}


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
    png(palette_path("norse_palette"), palette())
    for name in MODELS:
        write(os.path.join(ASSETS, "models", "item", name + "_3d.json"), model_json(name))
        icon = {"type": "minecraft:model", "model": f"{NS}:item/{name}"}
        held = {"type": "minecraft:model", "model": f"{NS}:item/{name}_3d"}
        if name == "gungnir":
            # Al cargar el lanzamiento, la pose del tridente al lanzar.
            write(os.path.join(ASSETS, "models", "item", "gungnir_throwing.json"),
                  {"parent": f"{NS}:item/gungnir_3d", "display": GUNGNIR_THROWING})
            held = {"type": "minecraft:condition", "property": "minecraft:using_item", "on_false": held,
                    "on_true": {"type": "minecraft:model", "model": f"{NS}:item/gungnir_throwing"}}
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": {
            "type": "minecraft:select", "property": "minecraft:display_context",
            "cases": [{"when": ["gui", "ground", "fixed", "on_shelf"], "model": icon}],
            "fallback": held}})
    print("ok:", ", ".join(MODELS))
    import paint_models  # noqa: E402  (textura pintada en lugar de la paleta plana)
    paint_models.run(['item/mjolnir_3d', 'item/gungnir_3d'])
