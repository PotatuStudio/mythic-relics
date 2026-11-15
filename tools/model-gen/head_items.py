"""Cabezas de Medusa y de Mímir como objetos 3D (en la mano, en el inventario, en el suelo y en marcos), con la misma
geometría base y las mismas posiciones de mano que las cabezas de mob vanilla (template_skull: el cráneo en x 4..12,
y 0..8, z 4..12, con la cara hacia el sur, +z), y textura pintada por cara (worn_texture.py).

- Medusa: piel verde grisácea, ceño marcado, ojos amarillos con la pupila en ranura, nariz, boca oscura con colmillos,
  el cuello cortado y una cabellera de serpientes (cuerpo de dos tramos que se curva hacia afuera, cabeza y lengua).
  Variante "mirada" (mientras se alza): los ojos se encienden (emiten luz) y las serpientes se retuercen (8 cuadros que
  pasan con la propiedad use_cycle del ítem, así se ve igual en primera y en tercera persona).
- Mímir: anciano curtido, calvo arriba y con el pelo gris a los lados, cejas blancas pobladas, nariz grande, ojos de una
  luz celeste tenue (su sabiduría), bigote y la barba larga trenzada con un anillo de oro, una banda de bronce con runas
  alrededor de la cabeza y las hierbas con las que Odín la conservó, en el cuello.
Uso: python head_items.py
"""
import json
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import worn_texture  # noqa: E402
from moddata import ASSETS  # noqa: E402
from worn_models import COLORS  # noqa: E402

NS = "mythicrelics"
EXTRA = {"skin": "#9cb38a", "skin_d": "#7b9268", "eye": "#e8d36a", "eye_glow": "#fff27a", "mouth": "#3a1a1a",
         "blood": "#6a1010", "skin_old": "#c9a27e", "skin_old_d": "#a88260", "beard": "#e9e6dc", "beard_d": "#bdb8aa",
         "hair_grey": "#9a958c", "eye_mimir": "#9fe8ff", "herb": "#5b8a3a", "herb_l": "#86b85a", "rune": "#7ff7ff"}
PAL = {**COLORS, **EXTRA}
worn_texture.FUR |= {"beard", "beard_d", "hair_grey"}
worn_texture.SCALES |= {"skin", "skin_d"}
worn_texture.FEATHER |= {"herb", "herb_l"}

# Las de la cabeza de mob vanilla (models/item/template_skull.json), salvo la tercera persona (ver abajo).
SKULL_DISPLAY = {
    "gui": {"rotation": [30, 45, 0], "translation": [0, 3, 0], "scale": [1, 1, 1]},
    "fixed": {"rotation": [0, 180, 0], "translation": [0, 4, 0], "scale": [1, 1, 1]},
    "on_shelf": {"rotation": [0, 0, 0], "translation": [0, 8, 0], "scale": [2, 2, 2]},
    "ground": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.5, 0.5, 0.5]},
    # En tercera persona la cara quedaba mirando al jugador: media vuelta en Y respecto de la vanilla.
    "thirdperson_righthand": {"rotation": [45, 225, 0], "translation": [0, 3, 0], "scale": [0.5, 0.5, 0.5]},
}


# Alzada (mirada): se sostiene al frente como un escudo, con la cara hacia donde mira el jugador. Con la de reposo, la
# cara quedaba de costado en tercera persona.
GAZE_DISPLAY = {**SKULL_DISPLAY,
                "thirdperson_righthand": {"rotation": [10, 135, 0], "translation": [0, 3, 1], "scale": [0.6, 0.6, 0.6]}}


def box(frm, to, color, name="", light=0):
    e = {"name": name, "from": [round(v, 3) for v in frm], "to": [round(v, 3) for v in to],
         "faces": {f: {} for f in ("north", "south", "east", "west", "up", "down")}, "__color": color}
    if light:
        e["light_emission"] = light
    return e


def snake(x, y, z, dx, dz, k, tall, phase=None):
    """Una serpiente del pelo: sale del cuero cabelludo hacia arriba, se curva hacia afuera (dx, dz) y termina en la
    cabeza (más oscura y ancha) con la lengua. Con {phase} (radianes) se retuerce: el cuerpo se estira y encoge, la
    cabeza se mece de lado y sube y baja, y la lengua sale y entra. Las piezas no cambian de tamaño (solo de lugar), así
    los cuadros de la animación comparten el mismo dibujo."""
    tone = ["cobra", "cobra_l", "cobra"][k % 3]
    sway = bob = reach = 0.0
    tongue = 0.6
    if phase is not None:
        a = phase + k * 1.7
        sway, bob, reach = math.sin(a) * 0.55, math.cos(a * 1.0) * 0.35, (math.sin(a + 1.2) + 1) * 0.35
        tongue = 0.6 if math.sin(a * 2 + k) > 0 else 0.15
    # Perpendicular a la dirección en que se curva (para el vaivén de lado).
    px, pz = -dz, dx
    e = [box([x, y, z], [x + 0.8, y + tall, z + 0.8], tone, f"serpiente {k}")]
    # El tramo curvo tiene siempre el mismo tamaño; al retorcerse solo se corre (la mitad que la cabeza).
    x2, z2 = x + dx * 0.9, z + dz * 0.9
    ox, oy, oz = dx * reach * 0.8 + px * sway * 0.5, bob * 0.5, dz * reach * 0.8 + pz * sway * 0.5
    e.append(box([min(x, x2) + ox, y + tall - 0.1 + oy, min(z, z2) + oz],
                 [max(x, x2) + 0.8 + ox, y + tall + 0.7 + oy, max(z, z2) + 0.8 + oz], tone, f"serpiente {k} curva"))
    hx, hz = x + dx * (1.7 + reach * 1.6) + px * sway, z + dz * (1.7 + reach * 1.6) + pz * sway
    hy = y + tall + 0.3 + bob
    e.append(box([hx - 0.1, hy, hz - 0.1], [hx + 1.0, hy + 0.7, hz + 1.0], "cobra_d", f"cabeza {k}"))
    tx, tz = hx + dx * tongue, hz + dz * tongue
    e.append(box([tx + 0.3, hy + 0.15, tz + 0.3], [tx + 0.55, hy + 0.25, tz + 0.55], "crest", f"lengua {k}"))
    return e


def medusa(gaze=False, phase=None, neck=True):
    eye = "eye_glow" if gaze else "eye"
    light = 15 if gaze else 0
    e = [box([4, 0.5, 4], [12, 8, 12], "skin", "cráneo"),
         box([5, 0, 6], [11, 0.5, 12], "skin_d", "mentón"),
         # Cara (sur): ceño, ojos con pupila en ranura, nariz, boca con colmillos y pómulos.
         box([4.6, 5.6, 12], [11.4, 6.3, 12.5], "skin_d", "ceño"),
         box([5.3, 4.4, 12], [7.3, 5.5, 12.25], eye, "ojo der.", light),
         box([8.7, 4.4, 12], [10.7, 5.5, 12.25], eye, "ojo izq.", light),
         box([6.1, 4.45, 12.25], [6.5, 5.45, 12.32], "black", "pupila der."),
         box([9.5, 4.45, 12.25], [9.9, 5.45, 12.32], "black", "pupila izq."),
         box([7.4, 3.0, 12], [8.6, 4.9, 12.6], "skin", "nariz"),
         box([5.8, 1.5, 12], [10.2, 2.6, 12.15], "mouth", "boca"),
         box([6.2, 1.1, 12.1], [6.7, 2.3, 12.35], "ivory", "colmillo der."),
         box([9.3, 1.1, 12.1], [9.8, 2.3, 12.35], "ivory", "colmillo izq."),
         box([4.4, 2.6, 11.4], [5.2, 4.2, 12.3], "skin_d", "pómulo der."),
         box([10.8, 2.6, 11.4], [11.6, 4.2, 12.3], "skin_d", "pómulo izq.")]
    if neck:
        e += [box([5.5, -1.5, 5.5], [10.5, 0.5, 10.5], "skin_d", "cuello"),
              box([5.7, -1.9, 5.7], [10.3, -1.5, 10.3], "blood", "corte")]
    # Cabellera: serpientes arriba, a los lados y atrás, curvándose hacia afuera.
    k = 0
    for x, z, dx, dz, tall in [(4.4, 5.0, -1, -1, 1.6), (6.4, 4.6, 0, -1, 2.2), (8.8, 4.6, 0, -1, 2.0), (10.8, 5.0, 1, -1, 1.6),
                               (4.6, 8.0, -1, 0, 2.0), (10.6, 8.0, 1, 0, 2.0), (6.0, 7.6, -1, 0.5, 2.6), (9.2, 7.6, 1, 0.5, 2.6),
                               (7.6, 6.2, 0, -1, 2.8), (4.6, 10.4, -1, 1, 1.4), (10.6, 10.4, 1, 1, 1.4)]:
        e += snake(x, 8, z, dx, dz, k, tall, phase)
        k += 1
    for y, side in ((6.0, -1), (3.6, -1), (6.0, 1), (3.6, 1)):
        x = 3.4 if side < 0 else 11.8
        w = 0.0 if phase is None else math.sin(phase + k * 1.3) * 0.4
        e.append(box([x, y, 6.0], [x + 0.8, y + 1.8, 9.6], "cobra", f"serpiente del costado {k}"))
        e.append(box([x + side * (0.6 + abs(w)), y - 0.6 + w, 9.0], [x + 0.8 + side * (0.6 + abs(w)), y + w, 10.2], "cobra_d",
                     f"cabeza del costado {k}"))
        k += 1
    # Nuca: mechones de serpientes que cuelgan, cada uno con su cabeza abajo.
    for i, (x, low) in enumerate([(4.4, 1.6), (5.8, 0.8), (7.2, 1.4), (8.6, 0.6), (10.0, 1.2), (11.0, 2.0)]):
        sw = 0.0 if phase is None else math.sin(phase + i * 1.1) * 0.35
        e.append(box([x, low + 0.6, 3.3 + (i % 2) * 0.25], [x + 0.9, 8.2, 4.0], "cobra" if i % 2 else "cobra_l", f"mechón de la nuca {i}"))
        e.append(box([x - 0.1 + sw, low, 3.1 + (i % 2) * 0.25 - abs(sw)], [x + 1.0 + sw, low + 0.7, 4.0 - abs(sw)], "cobra_d",
                     f"cabeza de la nuca {i}"))
    return e


def mimir():
    e = [box([4, 0.5, 4], [12, 8, 12], "skin_old", "cráneo"),
         box([4.6, 8.0, 4.6], [11.4, 8.4, 11.4], "skin_old", "coronilla"),
         box([3.75, 3.2, 4.4], [4.0, 6.4, 10.6], "hair_grey", "pelo der."),
         box([12.0, 3.2, 4.4], [12.25, 6.4, 10.6], "hair_grey", "pelo izq."),
         box([5.5, -1.2, 5.5], [10.5, 0.5, 10.5], "skin_old_d", "cuello"),
         # Banda de bronce con runas.
         box([3.85, 6.4, 3.85], [12.15, 7.1, 12.15], "bronze", "banda")]
    for k, x in enumerate((5.0, 6.8, 8.6, 10.4)):
        e.append(box([x, 6.55, 12.15], [x + 0.6, 6.95, 12.25], "rune", f"runa {k}", 8))
    # Mechones de pelo gris a los lados y en la nuca, de distinto largo.
    for side, x0, x1 in (("der.", 3.45, 3.8), ("izq.", 12.2, 12.55)):
        for k, (z, low) in enumerate([(4.6, 1.8), (6.0, 2.4), (7.4, 2.0), (8.8, 2.8)]):
            e.append(box([x0, low, z], [x1, 6.6, z + 1.1], "hair_grey" if k % 2 else "beard_d", f"mechón {side} {k}"))
    for k, (x, low) in enumerate([(4.4, 1.4), (5.8, 0.9), (7.2, 1.2), (8.6, 0.8), (10.0, 1.3)]):
        e.append(box([x, low, 3.45 + (k % 2) * 0.2], [x + 1.3, 7.2, 4.0], "hair_grey" if k % 2 else "beard_d", f"mechón de la nuca {k}"))
    e += [box([4.5, 5.2, 12], [7.4, 5.9, 12.55], "beard", "ceja der."),
          box([8.6, 5.2, 12], [11.5, 5.9, 12.55], "beard", "ceja izq."),
          box([5.6, 4.3, 12], [6.9, 4.65, 12.2], "eye_mimir", "ojo der.", 8),
          box([9.1, 4.3, 12], [10.4, 4.65, 12.2], "eye_mimir", "ojo izq.", 8),
          box([5.3, 4.65, 12], [7.2, 5.2, 12.3], "skin_old_d", "párpado der."),
          box([8.8, 4.65, 12], [10.7, 5.2, 12.3], "skin_old_d", "párpado izq."),
          box([5.3, 3.8, 12], [7.2, 4.3, 12.25], "skin_old_d", "ojera der."),
          box([8.8, 3.8, 12], [10.7, 4.3, 12.25], "skin_old_d", "ojera izq."),
          box([7.3, 2.6, 12], [8.7, 5.0, 12.7], "skin_old", "nariz"),
          box([5.4, 2.2, 12.1], [10.6, 2.9, 12.8], "beard", "bigote"),
          box([4.6, -2.8, 10.4], [11.4, 2.3, 12.7], "beard", "barba"),
          box([5.4, -4.6, 10.8], [10.6, -2.8, 12.5], "beard_d", "barba baja"),
          box([7.2, -6.6, 11.1], [8.8, -4.6, 12.2], "beard", "trenza"),
          box([7.0, -5.4, 11.0], [9.0, -5.0, 12.3], "gold", "anillo de la trenza"),
          box([4.4, 0.6, 11.2], [5.0, 2.8, 12.5], "beard_d", "patilla der."),
          box([11.0, 0.6, 11.2], [11.6, 2.8, 12.5], "beard_d", "patilla izq.")]
    # Hierbas en el cuello.
    for k, (x0, z0, x1, z1) in enumerate([(5.2, 5.2, 7.2, 6.0), (8.8, 5.2, 10.8, 6.0), (5.2, 9.6, 6.6, 10.8), (9.4, 9.6, 10.8, 10.8),
                                          (4.8, 6.8, 5.6, 9.2), (10.4, 6.8, 11.2, 9.2)]):
        e.append(box([x0, -0.8, z0], [x1, 0.4, z1], "herb" if k % 2 else "herb_l", f"hierba {k}"))
    return e


# Animación de la cabellera mientras se alza: cuadros y ticks por cuadro.
GAZE_FRAMES = 8
GAZE_TICKS = 2


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


def build(name, elements, display=None):
    m, img = worn_texture.texture(name, elements, PAL, NS)
    for el in m["elements"]:
        el.pop("__color", None)
        for face in el["faces"].values():
            face["texture"] = "#t"
    m["textures"] = {"t": f"{NS}:item/head/{name}", "particle": f"{NS}:item/head/{name}"}
    m["display"] = display or SKULL_DISPLAY
    m["gui_light"] = "front"
    img.save(os.path.join(ASSETS, "textures", "item", "head", name + ".png"))
    write(os.path.join(ASSETS, "models", "item", name + ".json"), m)
    return m


if __name__ == "__main__":
    os.makedirs(os.path.join(ASSETS, "textures", "item", "head"), exist_ok=True)
    build("medusa_head_3d", medusa())
    # Alzada: los ojos se encienden y las serpientes se retuercen, en GAZE_FRAMES cuadros que pasan con use_cycle.
    for i in range(GAZE_FRAMES):
        build(f"medusa_head_gaze_{i}", medusa(gaze=True, phase=2 * math.pi * i / GAZE_FRAMES), GAZE_DISPLAY)
    old = os.path.join(ASSETS, "models", "item", "medusa_head_gaze.json")
    for f in (old, os.path.join(ASSETS, "textures", "item", "head", "medusa_head_gaze.png")):
        if os.path.exists(f):
            os.remove(f)
    build("mimir_head_3d", mimir())
    model = lambda n: {"type": "minecraft:model", "model": f"{NS}:item/{n}"}  # noqa: E731
    write(os.path.join(ASSETS, "items", "medusa_head.json"), {"model": {
        "type": "minecraft:condition", "property": "minecraft:using_item",
        "on_true": {"type": "minecraft:range_dispatch", "property": "minecraft:use_cycle", "period": GAZE_FRAMES * GAZE_TICKS,
                    "entries": [{"threshold": i * GAZE_TICKS, "model": model(f"medusa_head_gaze_{i}")} for i in range(GAZE_FRAMES)],
                    "fallback": model("medusa_head_gaze_0")},
        "on_false": model("medusa_head_3d")}})
    write(os.path.join(ASSETS, "items", "mimir_head.json"), {"model": model("mimir_head_3d")})
    print("ok")
