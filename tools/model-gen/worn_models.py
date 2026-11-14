"""Modelos 3D de las reliquias puestas (Trinkets Updated, render "model") y su paleta.

Cómo los ubica Trinkets 4.2.1 (leído de sus fuentes): el modelo va pegado a la parte del cuerpo (se mueve con ella),
centrado en el centro de esa parte más offset × medio tamaño (+X = izquierda de quien lo lleva, +Y arriba, +Z frente).
Con escala 1 y "centered", el píxel (x, y, z) del modelo queda en (x-8, y-8, z-8) píxeles del jugador desde ese punto.
Así, con offset [0,0,0]:
  - cuerpo (body):   x 4..12, y 2..14 (14 = cuello), z 6..10 (10 = pecho)
  - cabeza (head):   x, y, z 4..12 (frente en z 12)
  - brazo (arm):     x 6..10, y 2..14 (2 = extremo de la mano), z 6..10
  - pierna (leg):    x 6..10, y 2..14 (2 = la planta del pie), z 6..10
Para que se vea sobre la capa de la skin hay que sobresalir al menos 0,5 px (0,75 en la cabeza). Dos reliquias que
se pueden llevar a la vez en la misma parte no deben compartir planos (parpadean): por eso el anillo va más afuera que
las vendas.

Escribe (common) models/item/<reliquia>_worn.json, textures/item/worn_palette.png e items/<reliquia>.json, y (fabric)
trinkets/<reliquia>.json. La definición del ítem usa el modelo puesto en el contexto de display "none", el que usan
Trinkets (render "model"/"item") y el renderizador de Curios en NeoForge; en el resto (inventario, mano, suelo) usa el
ícono. Así NeoForge no necesita registrar modelos extra.
Uso: python worn_models.py
"""
import json
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from mclib import H, png  # noqa: E402
from moddata import ASSETS, FABRIC_ASSETS  # noqa: E402

NS = "mythicrelics"
# Paleta de 8x8 casillas de 2x2 px (una por color). Cada cara de un elemento toma el centro de su casilla.
COLORS = {
    "gold_l": "#ffe27a", "gold": "#f0c040", "gold_d": "#c8901e", "gold_dd": "#8a5a10",
    "lapis_l": "#5f86e6", "lapis": "#2f52b4", "turq_l": "#8af0dc", "turq": "#34bfae",
    "linen_l": "#fbf5e2", "linen": "#e2d6b4", "linen_d": "#bfae86", "linen_dd": "#8f7d58",
    "onyx_hi": "#3e383c", "onyx": "#1c1719", "onyx_d": "#0e0b0c", "white": "#f4f1e6",
    "sun_l": "#ff9a5a", "sun": "#e0502a", "sun_d": "#a8301a",
    "cobra_l": "#8ad874", "cobra": "#3f9a3a", "cobra_d": "#2a6a28",
    "fur_l": "#f0c070", "fur": "#c8892e", "fur_d": "#8a5a20", "ivory": "#f4ead0", "ivory_d": "#cfc2a0",
    # Grecia
    "black_l": "#4a4446", "black": "#1c1719", "black_d": "#0e0b0c", "crest_l": "#ff6a5a", "crest": "#c8302a",
    "crest_d": "#7a1a14", "wing_l": "#f4f8ff", "wing": "#cfe6ff", "wing_d": "#7fb0e0", "leather_l": "#c09060",
    "leather": "#a07848", "leather_d": "#6e4e2c", "stone": "#c9cfc4", "stone_d": "#8f978a", "snake": "#4f8f3a",
    "wax_l": "#fff2c2", "wax": "#e6c66a", "silver": "#e8e8ec", "silver_d": "#a8a8b0", "bronze": "#e0954a",
    "bronze_d": "#a85f26", "ember": "#ff8a3a",
    "iron_l": "#cfd3dc", "iron": "#8a8f9c", "iron_d": "#4a4e58", "amber": "#ffb030", "ruby": "#d8392b",
    # Grecia 3 y nórdicos 3: melena del león y plumas de halcón
    "mane": "#6e4216", "falcon_l": "#c4955e", "falcon": "#7a4f2c", "falcon_d": "#4e3018", "feather_w": "#efe6d6",
}
SWATCHES = list(COLORS)
# Cara de arriba más clara y de abajo más oscura: da volumen aunque cada elemento sea de un color.
SHADE = {"gold": ("gold_l", "gold_d"), "gold_l": ("gold_l", "gold"), "gold_d": ("gold", "gold_dd"),
         "gold_dd": ("gold_d", "gold_dd"), "lapis": ("lapis_l", "lapis"), "lapis_l": ("lapis_l", "lapis"),
         "turq": ("turq_l", "turq"), "turq_l": ("turq_l", "turq"), "linen": ("linen_l", "linen_d"),
         "linen_l": ("linen_l", "linen"), "linen_d": ("linen", "linen_dd"), "linen_dd": ("linen_d", "linen_dd"),
         "onyx": ("onyx_hi", "onyx_d"), "onyx_hi": ("onyx_hi", "onyx"), "onyx_d": ("onyx", "onyx_d"),
         "white": ("white", "white"), "sun": ("sun_l", "sun_d"), "cobra": ("cobra_l", "cobra_d"),
         "fur": ("fur_l", "fur_d"), "fur_l": ("fur_l", "fur"), "ivory": ("ivory", "ivory_d"),
         "black": ("black_l", "black_d"), "crest": ("crest_l", "crest_d"), "wing": ("wing_l", "wing_d"),
         "leather": ("leather_l", "leather_d"), "stone": ("stone", "stone_d"), "wax": ("wax_l", "wax"),
         "silver": ("silver", "silver_d"), "bronze": ("bronze", "bronze_d"), "iron": ("iron_l", "iron_d"),
         "falcon": ("falcon_l", "falcon_d"), "mane": ("fur_d", "mane")}


def uv(color):
    i = SWATCHES.index(color)
    u, v = (i % 8) * 2, (i // 8) * 2
    return [u + 0.5, v + 0.5, u + 1.5, v + 1.5]


def box(frm, to, color, name=""):
    top, bottom = SHADE.get(color, (color, color))
    faces = {side: {"uv": uv(color), "texture": "#p"} for side in ("north", "south", "east", "west")}
    faces["up"] = {"uv": uv(top), "texture": "#p"}
    faces["down"] = {"uv": uv(bottom), "texture": "#p"}
    return {"name": name, "from": [round(v, 3) for v in frm], "to": [round(v, 3) for v in to], "faces": faces, "__color": color}


def ring(x0, x1, y0, y1, z0, z1, t, color, name):
    """Aro rectangular de grosor t (cuatro cajas) alrededor de una parte del cuerpo."""
    return [
        box([x0, y0, z1 - t], [x1, y1, z1], color, name + " frente"),
        box([x0, y0, z0], [x1, y1, z0 + t], color, name + " atrás"),
        box([x0, y0, z0 + t], [x0 + t, y1, z1 - t], color, name + " der."),
        box([x1 - t, y0, z0 + t], [x1, y1, z1 - t], color, name + " izq."),
    ]


def model(elements):
    return {"textures": {"p": f"{NS}:item/worn_palette", "particle": f"{NS}:item/worn_palette"}, "elements": elements}


def ankh_necklace():
    """Cadena de oro alrededor del cuello que baja en V al pecho, con el Anj colgando en relieve."""
    e = []
    # Vuelta de la cadena arriba del torso (bajo la cabeza): atrás y sobre los hombros.
    e.append(box([3.5, 13.3, 5.5], [12.5, 13.8, 6.0], "gold_d", "cadena atrás"))
    e.append(box([3.5, 13.3, 6.0], [4.0, 13.8, 10.5], "gold", "cadena der."))
    e.append(box([12.0, 13.3, 6.0], [12.5, 13.8, 10.5], "gold", "cadena izq."))
    # Las dos hebras del frente bajan escalonadas hasta el colgante.
    steps = 6
    for i in range(steps):
        x_left = 4.0 + i * 0.6
        y = 13.3 - i * 0.22
        e.append(box([x_left, y - 0.5, 10.5], [x_left + 0.6, y, 11.0], "gold", f"hebra der. {i}"))
        e.append(box([12.0 - (i + 1) * 0.6, y - 0.5, 10.5], [12.0 - i * 0.6, y, 11.0], "gold", f"hebra izq. {i}"))
    # Anj: lazo, travesaño y asta, 1 px de relieve sobre el pecho, con la gema turquesa en el cruce.
    z0, z1 = 10.75, 11.75
    e += [
        box([7.0, 11.6, z0], [9.0, 12.1, z1], "gold", "anj lazo arriba"),
        box([7.0, 10.2, z0], [7.5, 11.6, z1], "gold", "anj lazo der."),
        box([8.5, 10.2, z0], [9.0, 11.6, z1], "gold", "anj lazo izq."),
        box([6.6, 9.6, z0], [9.4, 10.2, z1], "gold", "anj travesaño"),
        box([7.6, 7.4, z0], [8.4, 9.6, z1], "gold", "anj asta"),
        box([7.7, 9.65, z1], [8.3, 10.15, z1 + 0.2], "turq", "anj gema"),
    ]
    return model(e)


def scarab_belt():
    """Faja de lino alrededor de la cintura con tachas de oro y un escarabajo chico como hebilla."""
    e = ring(3.5, 12.5, 3.0, 4.5, 5.5, 10.5, 0.5, "linen", "faja")
    e += [box([3.4, 3.35, 7.6], [3.5, 4.15, 8.4], "gold", "tacha der."),
          box([12.5, 3.35, 7.6], [12.6, 4.15, 8.4], "gold", "tacha izq.")]
    for x in (5.0, 10.4):
        e.append(box([x, 3.4, 10.5], [x + 0.6, 4.1, 10.7], "gold", "tacha frente"))
    # Escarabajo: élitros, pronoto y cabeza en pala; línea de lapislázuli entre los élitros.
    e += [
        box([7.0, 2.7, 10.5], [9.0, 4.5, 11.4], "gold", "escarabajo élitros"),
        box([7.85, 2.8, 11.4], [8.15, 4.4, 11.5], "lapis", "escarabajo línea"),
        box([7.3, 4.5, 10.5], [8.7, 5.0, 11.2], "gold_d", "escarabajo pronoto"),
        box([7.6, 5.0, 10.5], [8.4, 5.3, 11.0], "gold_dd", "escarabajo cabeza"),
    ]
    return model(e)


def eye_diadem():
    """Diadema de oro alrededor de la cabeza con el wedjat al frente."""
    e = ring(3.15, 12.85, 9.5, 10.5, 3.15, 12.85, 0.75, "gold", "diadema")
    z = 12.85
    e += [
        # La placa sobresale hasta 13,3: con la máscara puesta (cara en z 12,5..13,25) no comparten plano.
        box([6.4, 9.0, z], [9.6, 11.6, z + 0.45], "lapis", "placa"),
        box([6.7, 9.9, z + 0.45], [9.3, 10.6, z + 0.6], "white", "ojo"),
        box([7.7, 9.9, z + 0.6], [8.4, 10.6, z + 0.7], "lapis_l", "pupila"),
        box([6.7, 10.9, z + 0.45], [9.3, 11.25, z + 0.6], "gold", "ceja"),
        box([6.9, 9.15, z + 0.45], [7.25, 9.9, z + 0.6], "gold", "lágrima"),
        box([8.8, 9.15, z + 0.45], [9.15, 9.9, z + 0.6], "gold", "espiral"),
    ]
    return model(e)


def jackal_mask():
    """Cabeza de chacal sobre la cara: placa negra, ojos dorados, hocico que sobresale, orejas altas."""
    e = [
        box([3.5, 4.0, 12.5], [12.5, 12.0, 13.25], "onyx", "cara"),
        box([3.25, 4.0, 6.0], [3.5, 12.0, 13.25], "onyx", "costado der."),
        box([12.5, 4.0, 6.0], [12.75, 12.0, 13.25], "onyx", "costado izq."),
        box([3.5, 12.0, 6.0], [12.5, 12.25, 13.25], "onyx", "coronilla"),
        box([4.8, 8.6, 13.25], [6.6, 9.4, 13.4], "gold_l", "ojo der."),
        box([9.4, 8.6, 13.25], [11.2, 9.4, 13.4], "gold_l", "ojo izq."),
        box([6.0, 4.5, 13.25], [10.0, 7.5, 16.0], "onyx", "hocico"),
        box([5.8, 5.4, 14.2], [10.2, 6.1, 14.9], "gold", "banda del hocico"),
        box([7.2, 6.7, 16.0], [8.8, 7.5, 16.3], "onyx_d", "nariz"),
        box([4.0, 12.25, 10.5], [6.0, 15.0, 12.25], "onyx", "oreja der."),
        box([4.5, 15.0, 11.0], [5.5, 16.5, 12.0], "onyx", "punta der."),
        box([10.0, 12.25, 10.5], [12.0, 15.0, 12.25], "onyx", "oreja izq."),
        box([10.5, 15.0, 11.0], [11.5, 16.5, 12.0], "onyx", "punta izq."),
        box([4.5, 12.6, 12.25], [5.5, 14.8, 12.4], "gold", "oreja der. por dentro"),
        box([10.5, 12.6, 12.25], [11.5, 14.8, 12.4], "gold", "oreja izq. por dentro"),
    ]
    return model(e)


def mummy_wraps():
    """Vendaje de lino en la mano y el antebrazo: franjas alternadas y la punta de la mano cubierta."""
    e = [box([5.5, 1.75, 5.5], [10.5, 2.25, 10.5], "linen", "punta de la mano")]
    colors = ["linen_l", "linen", "linen_l", "linen_d", "linen_l"]
    for i, c in enumerate(colors):
        y0 = 2.25 + i
        # Cada vuelta se corre un poco: parece una venda enrollada en diagonal.
        grow = 0.1 if i % 2 else 0.0
        e += ring(5.5 - grow, 10.5 + grow, y0, y0 + 1.0, 5.5 - grow, 10.5 + grow, 0.5, c, f"vuelta {i}")
    e.append(box([6.2, 6.6, 10.5], [9.8, 7.4, 10.8], "linen_d", "nudo"))
    return model(e)


def jackal_ring():
    """Aro de oro en el extremo del brazo con el ónix de orejitas al frente.

    Va un poco más afuera (5,15..10,85) que las vendas (5,4..10,6): si los dos se llevan en la misma mano, ninguna cara
    queda en el mismo plano (eso hace parpadear las superficies)."""
    e = ring(5.15, 10.85, 8.6, 9.6, 5.15, 10.85, 0.5, "gold", "aro")
    z = 10.85
    e += [
        box([7.3, 8.4, z], [8.7, 9.8, z + 0.7], "onyx", "ónix"),
        box([7.4, 9.8, z + 0.1], [7.8, 10.3, z + 0.5], "onyx", "oreja der."),
        box([8.2, 9.8, z + 0.1], [8.6, 10.3, z + 0.5], "onyx", "oreja izq."),
        box([7.55, 9.2, z + 0.7], [7.8, 9.45, z + 0.8], "gold_l", "ojo der."),
        box([8.2, 9.2, z + 0.7], [8.45, 9.45, z + 0.8], "gold_l", "ojo izq."),
    ]
    return model(e)


def ra_headdress():
    """Corona baja de oro y, sobre la cabeza, el disco solar con su aro de oro y el ureo al frente.

    Se puede llevar a la vez que el Ojo de Horus o la máscara (sombrero + cara): la corona va más afuera (3..13) y la base
    del disco sobre la coronilla de la máscara, sin compartir planos con ellos."""
    e = ring(3.0, 13.0, 11.0, 12.1, 3.0, 13.0, 0.75, "gold", "corona")
    cx, cy, r = 8.0, 17.0, 4.0
    for k in range(-4, 4):
        y0 = cy + k
        half = math.sqrt(max(0.0, r * r - (k + 0.5) ** 2))
        e.append(box([cx - half, y0, 7.6], [cx + half, y0 + 1, 8.6], "sun", "disco"))
        e.append(box([cx - half - 0.4, y0, 7.75], [cx - half, y0 + 1, 8.45], "gold", "aro"))
        e.append(box([cx + half, y0, 7.75], [cx + half + 0.4, y0 + 1, 8.45], "gold", "aro"))
    e += [box([7.0, 12.4, 6.0], [9.0, 13.0, 10.0], "gold", "base del disco"),
          box([7.5, 12.1, 13.1], [8.5, 14.5, 13.75], "cobra", "ureo cuerpo"),
          box([7.0, 14.5, 12.95], [9.0, 15.6, 13.65], "cobra", "ureo capucha"),
          box([7.7, 15.0, 13.65], [8.3, 15.3, 13.75], "gold_l", "ureo ojos")]
    return e


def bastet_pendant():
    """Cadena como la del Anj y una gata sentada de bronce oscuro con collar de oro, en relieve sobre el pecho."""
    e = [box([3.5, 13.3, 5.5], [12.5, 13.8, 6.0], "gold_d", "cadena"),
         box([3.5, 13.3, 6.0], [4.0, 13.8, 10.5], "gold", "cadena"),
         box([12.0, 13.3, 6.0], [12.5, 13.8, 10.5], "gold", "cadena")]
    for i in range(6):
        x, y = 4.0 + i * 0.6, 13.3 - i * 0.22
        e.append(box([x, y - 0.5, 10.5], [x + 0.6, y, 11.0], "gold", "hebra"))
        e.append(box([12.0 - (i + 1) * 0.6, y - 0.5, 10.5], [12.0 - i * 0.6, y, 11.0], "gold", "hebra"))
    z0 = 10.75
    e += [box([6.9, 7.6, z0], [9.1, 10.2, z0 + 1.3], "onyx", "cuerpo"),
          box([7.2, 10.2, z0 + 0.2], [8.8, 11.6, z0 + 1.2], "onyx", "cabeza"),
          box([7.2, 11.6, z0 + 0.5], [7.6, 12.1, z0 + 0.9], "onyx", "oreja"),
          box([8.4, 11.6, z0 + 0.5], [8.8, 12.1, z0 + 0.9], "onyx", "oreja"),
          box([6.9, 9.9, z0 - 0.05], [9.1, 10.3, z0 + 1.35], "gold", "collar"),
          box([7.5, 10.9, z0 + 1.2], [7.75, 11.15, z0 + 1.3], "gold_l", "ojo"),
          box([8.25, 10.9, z0 + 1.2], [8.5, 11.15, z0 + 1.3], "gold_l", "ojo"),
          box([9.1, 7.6, z0 + 0.3], [9.6, 8.1, z0 + 1.0], "onyx", "cola")]
    return e


def sekhmet_claws_worn():
    """Guantelete de piel de leona con muñequera de oro y cuatro garras de marfil que asoman en la punta."""
    e = ring(5.5, 10.5, 2.0, 6.0, 5.5, 10.5, 0.5, "fur", "piel")
    e += ring(5.35, 10.65, 6.0, 7.0, 5.35, 10.65, 0.5, "gold", "muñequera")
    e += [box([5.5, 1.5, 5.5], [10.5, 2.0, 10.5], "fur", "punta")]
    for i, x in enumerate((5.9, 7.1, 8.3, 9.5)):
        e.append(box([x, 0.2, 9.3], [x + 0.6, 1.5, 10.1], "ivory", f"garra {i}"))
    return e


def sandal():
    """En cada pierna (x 6..10, z 6..10, frente z 10; el pie llega a y 2): suela con puntera, tiras de cuero cruzadas que
    suben por la canilla (en X por delante, rectas por detrás), aro de oro en el tobillo y, a cada lado del tobillo, un
    ala de tres plumas escalonadas (blanca, celeste y punta azul) que se abre hacia atrás y arriba desde una base de oro."""
    e = [box([5.5, 1.45, 5.5], [10.5, 2.2, 11.05], "leather_d", "suela"),
         box([5.8, 1.6, 11.05], [10.2, 2.1, 11.35], "leather", "puntera")]
    e += ring(5.6, 10.4, 2.2, 2.8, 5.6, 10.4, 0.45, "leather", "tira del pie")
    e += ring(5.55, 10.45, 4.3, 4.95, 5.55, 10.45, 0.5, "gold", "aro del tobillo")
    # Tiras cruzadas: por delante en X (escalones alternos), por detrás rectas.
    for k, y in enumerate((3.0, 5.4, 6.6, 7.8)):
        left = k % 2 == 0
        e.append(box([5.65 if left else 7.9, y, 10.42], [8.1 if left else 10.35, y + 0.55, 10.62], "leather", f"tira cruzada {k}"))
        e.append(box([7.9 if left else 5.65, y + 0.55, 10.42], [10.35 if left else 8.1, y + 1.1, 10.62], "leather_d", f"tira cruzada {k}b"))
    e += ring(5.62, 10.38, 8.9, 9.4, 5.62, 10.38, 0.4, "leather", "lazo de la canilla")
    e.append(box([7.4, 8.6, 10.38], [8.6, 9.6, 10.72], "gold", "nudo de la canilla"))
    # Alas en los dos lados del tobillo (la misma pieza va en las dos piernas).
    for side, x0, x1 in (("ext.", 4.5, 5.15), ("int.", 10.85, 11.5)):
        e.append(box([x0 - 0.1, 4.2, 6.6], [x1 + 0.1, 5.2, 7.6], "gold", f"base del ala {side}"))
        e += [box([x0, 4.5, 4.4], [x1, 5.4, 6.6], "white", f"pluma corta {side}"),
              box([x0 + 0.05, 5.4, 3.4], [x1 - 0.05, 6.3, 6.4], "wing_l", f"pluma media {side}"),
              box([x0 + 0.1, 6.3, 2.4], [x1 - 0.1, 7.1, 6.1], "wing", f"pluma larga {side}"),
              box([x0 + 0.12, 7.1, 1.8], [x1 - 0.12, 7.6, 3.4], "wing_d", f"punta del ala {side}")]
    return e


def helm():
    """Yelmo corintio de Hades (el kunée que vuelve invisible): hierro oscuro con bordes de plata, abertura en T al
    frente (ojos y boca) con la nasal al medio y el interior negro (da profundidad), mejillas largas con una espiral de
    plata, remaches, guardanuca abierta y una cimera de crin roja oscura sobre un soporte de oro que sube desde la frente,
    se curva en lo alto y cae larga por la nuca.

    Va un poco más afuera (2,95..13,05) que las piezas de cara y la corona de Ra, para no compartir planos con ellas."""
    e = []
    # Casco: costados y nuca (la cabeza va de 4 a 12; el frente es z 12).
    e += [box([2.95, 3.1, 2.95], [3.65, 12.85, 12.35], "iron", "costado der."),
          box([12.35, 3.1, 2.95], [13.05, 12.85, 12.35], "iron", "costado izq."),
          box([3.65, 4.6, 2.95], [12.35, 12.85, 3.65], "iron", "nuca"),
          box([2.95, 12.85, 2.95], [13.05, 13.55, 13.05], "iron", "cúpula"),
          box([3.6, 13.55, 3.6], [12.45, 14.05, 12.45], "iron_d", "cúpula alta")]
    e.append(box([3.3, 3.1, 2.45], [12.7, 4.6, 2.95], "iron_d", "guardanuca"))
    e.append(box([3.1, 2.6, 2.2], [12.9, 3.1, 2.75], "silver", "borde de la guardanuca"))
    # Frente con abertura en T: frente, cejas de plata, nasal, mejillas y el interior negro.
    e += [box([3.65, 9.7, 12.35], [12.35, 12.85, 13.05], "iron", "frente"),
          box([3.4, 9.55, 13.05], [12.6, 10.15, 13.27], "silver", "ceja"),
          box([4.4, 10.15, 13.05], [11.6, 10.6, 13.18], "iron_d", "arco de las cejas"),
          box([7.35, 5.6, 12.35], [8.65, 9.7, 13.2], "iron", "nasal"),
          box([7.5, 5.3, 12.4], [8.5, 5.6, 13.22], "silver", "punta de la nasal"),
          box([3.65, 3.1, 12.35], [6.9, 7.6, 13.05], "iron", "mejilla der."),
          box([9.1, 3.1, 12.35], [12.35, 7.6, 13.05], "iron", "mejilla izq."),
          box([3.65, 7.6, 12.35], [4.5, 9.7, 13.05], "iron", "sien der."),
          box([11.5, 7.6, 12.35], [12.35, 9.7, 13.05], "iron", "sien izq."),
          box([4.5, 7.6, 12.18], [7.35, 9.7, 12.3], "black", "hueco del ojo der."),
          box([8.65, 7.6, 12.18], [11.5, 9.7, 12.3], "black", "hueco del ojo izq."),
          box([6.9, 3.1, 12.18], [9.1, 5.6, 12.3], "black", "hueco de la boca")]
    e += [box([3.4, 2.8, 12.25], [7.1, 3.1, 13.25], "silver", "borde mejilla der."),
          box([8.9, 2.8, 12.25], [12.6, 3.1, 13.25], "silver", "borde mejilla izq.")]
    # Espiral de plata en cada mejilla y remaches.
    for side, x0 in (("der.", 4.3), ("izq.", 9.7)):
        e += [box([x0, 4.05, 13.05], [x0 + 2.0, 4.4, 13.2], "silver", f"espiral {side} 1"),
              box([x0 + 1.6, 4.4, 13.05], [x0 + 2.0, 6.0, 13.2], "silver", f"espiral {side} 2"),
              box([x0 + 0.4, 5.6, 13.05], [x0 + 1.6, 6.0, 13.2], "silver", f"espiral {side} 3"),
              box([x0 + 0.4, 4.8, 13.05], [x0 + 0.8, 5.6, 13.2], "silver", f"espiral {side} 4")]
    for k, z in enumerate((4.2, 6.6, 9.0, 11.2)):
        e.append(box([2.8, 11.6, z], [2.95, 12.0, z + 0.4], "silver", f"remache der. {k}"))
        e.append(box([13.05, 11.6, z], [13.2, 12.0, z + 0.4], "silver", f"remache izq. {k}"))
    # Cimera: soporte de oro y crin en perfil curvo (sube desde la frente, se arquea y cae por la nuca).
    e.append(box([7.3, 14.05, 3.0], [8.7, 14.75, 13.0], "gold", "soporte"))
    profile = [(12.4, 13.6, 16.2), (10.4, 12.4, 17.4), (8.0, 10.4, 18.0), (5.6, 8.0, 17.8), (3.4, 5.6, 17.0), (2.0, 3.4, 16.0)]
    for k, (z0, z1, top) in enumerate(profile):
        e.append(box([7.05, 14.75, z0], [8.95, top, z1], "crest" if k % 2 == 0 else "crest_d", f"crin {k}"))
    e.append(box([7.25, 15.6, 6.0], [8.75, 18.5, 10.0], "crest_l", "crin alta"))
    e += [box([7.05, 10.8, 1.2], [8.95, 16.0, 2.0], "crest_d", "cola de crin"),
          box([7.15, 7.6, 0.7], [8.85, 10.8, 1.5], "crest", "punta de la cola")]
    return e


def aegis_worn():
    """Égida de Atenea: la piel de cabra dorada de escamas (damero de oro claro y oscuro) que cubre la espalda y los
    hombros, con un fleco de serpientes verdes con cabeza al borde de abajo y a los costados, y el gorgoneion al centro:
    cara de piedra con ojos abiertos, la lengua afuera y la cabellera de serpientes alrededor. Va por detrás de la capa
    (z < 5,35) y apartada de la cadena de los collares."""
    e = [box([3.5, 5.0, 4.95], [12.5, 14.0, 5.35], "gold", "piel"),
         box([3.2, 14.0, 4.6], [12.8, 14.7, 10.35], "gold", "hombros")]
    # Escamas en damero sobre la piel.
    for row, y in enumerate([5.6, 6.8, 8.0, 9.2, 10.4, 11.6, 12.8]):
        for col, x in enumerate([3.8, 5.0, 6.2, 7.4, 8.6, 9.8, 11.0]):
            if (row + col) % 2 == 0 and not (6.0 <= x <= 9.6 and 7.4 <= y <= 12.2):
                e.append(box([x, y, 4.8], [x + 1.1, y + 1.1, 4.95], "gold_l" if row % 2 else "gold_d", f"escama {row} {col}"))
    # Fleco de serpientes: cuerpo verde y cabeza más oscura, abajo y en los costados.
    for k, x in enumerate((3.7, 5.0, 6.3, 7.6, 8.9, 10.2, 11.5)):
        low = 3.2 if k % 2 == 0 else 3.8
        e.append(box([x, low, 4.85], [x + 0.6, 5.0, 5.25], "cobra", f"serpiente {k}"))
        e.append(box([x - 0.1, low - 0.6, 4.75], [x + 0.7, low, 5.3], "cobra_d", f"cabeza de serpiente {k}"))
    for side, x0, x1 in (("der.", 2.9, 3.5), ("izq.", 12.5, 13.1)):
        for k, y in enumerate((6.4, 9.0, 11.6)):
            e.append(box([x0, y, 4.7], [x1, y + 1.6, 5.2], "cobra", f"serpiente del costado {side} {k}"))
    # Gorgoneion, como en las vasijas: rostro ancho de piedra, ceño fruncido en V, ojos ámbar fijos con la pupila en
    # ranura, la boca ancha en mueca con colmillos de jabalí arriba y abajo y la lengua apenas afuera, y una cabellera de
    # serpientes que se retuercen alrededor (con cabeza oscura en la punta).
    e += [box([5.8, 7.2, 4.3], [10.2, 11.6, 4.75], "stone", "cara de la gorgona"),
          box([5.5, 8.0, 4.4], [5.8, 10.8, 4.75], "stone_d", "mejilla der."),
          box([10.2, 8.0, 4.4], [10.5, 10.8, 4.75], "stone_d", "mejilla izq."),
          box([6.1, 10.7, 4.18], [7.8, 11.05, 4.3], "black", "ceja der."),
          box([8.2, 10.7, 4.18], [9.9, 11.05, 4.3], "black", "ceja izq."),
          box([7.6, 10.4, 4.18], [8.4, 10.8, 4.3], "black", "entrecejo"),
          box([6.3, 9.7, 4.2], [7.6, 10.5, 4.3], "amber", "ojo der."),
          box([8.4, 9.7, 4.2], [9.7, 10.5, 4.3], "amber", "ojo izq."),
          box([6.85, 9.75, 4.12], [7.05, 10.45, 4.2], "black", "pupila der."),
          box([8.95, 9.75, 4.12], [9.15, 10.45, 4.2], "black", "pupila izq."),
          box([7.65, 8.9, 4.18], [8.35, 9.7, 4.3], "stone_d", "nariz"),
          box([6.4, 7.7, 4.2], [9.6, 8.7, 4.3], "black", "boca"),
          box([6.5, 7.5, 4.14], [7.0, 8.9, 4.22], "ivory", "colmillo der."),
          box([9.0, 7.5, 4.14], [9.5, 8.9, 4.22], "ivory", "colmillo izq."),
          box([7.2, 8.35, 4.15], [7.5, 8.7, 4.2], "white", "diente 1"),
          box([8.5, 8.35, 4.15], [8.8, 8.7, 4.2], "white", "diente 2"),
          box([7.7, 7.3, 4.12], [8.3, 7.9, 4.2], "crest_d", "lengua")]
    for k, (x0, y0, x1, y1, hx, hy) in enumerate([(5.0, 11.6, 5.6, 13.0, 4.6, 12.8), (6.4, 11.6, 7.0, 13.2, 6.0, 13.0),
                                                   (9.0, 11.6, 9.6, 13.2, 9.6, 13.0), (10.4, 11.6, 11.0, 13.0, 11.0, 12.8),
                                                   (4.8, 9.0, 5.5, 11.6, 4.3, 9.0), (10.5, 9.0, 11.2, 11.6, 11.2, 9.0),
                                                   (4.9, 7.0, 5.5, 8.6, 4.4, 6.8), (10.5, 7.0, 11.1, 8.6, 11.1, 6.8)]):
        e.append(box([x0, y0, 4.4], [x1, y1, 4.7], "cobra" if k % 2 else "cobra_l", f"serpiente del pelo {k}"))
        e.append(box([hx, hy, 4.35], [hx + 0.7, hy + 0.5, 4.68], "cobra_d", f"cabeza de serpiente del pelo {k}"))
    return e


def mirror(e):
    """Copia un elemento al otro lado del cuerpo (x -> 16 - x)."""
    m = json.loads(json.dumps(e))
    m["from"][0], m["to"][0] = 16 - e["to"][0], 16 - e["from"][0]
    return m


def icarus_wings():
    """Alas de Ícaro: arnés de cuero cruzado en la espalda con la placa de cera, y dos alas abiertas que suben un poco:
    hueso de madera, cobertoras cortas, plumas medias y primarias largas (cada hilera más larga que la anterior y
    escalonada, sin caras coplanares), con goterones de cera en las uniones. Van por detrás del vellocino (z < 5,35)."""
    e = [box([6.0, 8.5, 4.75], [10.0, 13.0, 5.3], "wax", "cera"),
         box([6.6, 9.2, 4.6], [9.4, 12.2, 4.75], "wax_l", "cera clara")]
    for k, (x0, y0, x1, y1) in enumerate([(4.0, 12.6, 6.0, 13.6), (10.0, 12.6, 12.0, 13.6), (4.4, 6.2, 6.4, 7.2), (9.6, 6.2, 11.6, 7.2)]):
        e.append(box([x0, y0, 4.85], [x1, y1, 5.32], "leather", f"correa {k}"))
    side = [box([10.0, 12.4, 4.55], [21.0, 13.6, 5.2], "leather_d", "hueso"),
            box([20.8, 12.0, 4.6], [22.0, 13.4, 5.15], "leather_d", "punta del hueso")]
    # Cobertoras (cortas, blancas), plumas medias y primarias (largas, de punta azul), escalonadas en z.
    for i, x in enumerate([10.4, 12.2, 14.0, 15.8, 17.6, 19.4]):
        side.append(box([x, 10.6, 4.62], [x + 1.6, 12.4, 5.1], "white", f"cobertora {i}"))
    for i, (x, low) in enumerate([(10.6, 7.6), (12.4, 7.0), (14.2, 6.4), (16.0, 5.8), (17.8, 5.4), (19.6, 5.2)]):
        side.append(box([x, low, 4.68], [x + 1.6, 10.6, 5.02], "wing_l", f"pluma media {i}"))
    for i, (x, low) in enumerate([(11.0, 5.4), (12.8, 4.4), (14.6, 3.4), (16.4, 2.6), (18.2, 2.0), (20.0, 1.8)]):
        side.append(box([x, low, 4.74], [x + 1.4, 7.6 - i * 0.3, 4.96], "wing", f"primaria {i}"))
        side.append(box([x + 0.2, low - 0.6, 4.76], [x + 1.2, low, 4.94], "wing_d", f"punta de primaria {i}"))
    for k, x in enumerate([11.6, 15.0, 18.4]):
        side.append(box([x, 12.0, 4.4], [x + 0.8, 12.6, 4.55], "wax", f"gota de cera {k}"))
        side.append(box([x + 0.2, 11.4, 4.42], [x + 0.6, 12.0, 4.54], "wax_l", f"chorrito de cera {k}"))
    return e + side + [mirror(p) for p in side]


def golden_fleece():
    """El vellocino de oro: la piel del carnero alado, de lana dorada en rizos (relieve en damero de oro claro y oscuro)
    sobre la espalda y los hombros; las patas traseras cuelgan en las esquinas y las delanteras se anudan en el pecho; la
    cabeza del carnero, con los cuernos en espiral, cuelga detrás de la nuca como la del león (no atraviesa la cabeza al
    girar). Comparte la franja de la capa con la piel de Nemea (no se llevan juntas)."""
    e = [box([3.6, 6.5, 5.52], [12.4, 13.95, 5.95], "gold", "lomo"),
         box([3.6, 13.92, 5.4], [12.4, 14.55, 10.45], "gold", "hombros")]
    # Rizos de lana en damero.
    for row, y in enumerate([6.8, 8.0, 9.2, 10.4, 11.6, 12.55]):
        for col, x in enumerate([3.7, 4.9, 6.1, 7.3, 8.5, 9.7, 10.9]):
            if (row + col) % 2 == 0:
                e.append(box([x, y, 5.41], [x + 1.4, y + 1.2, 5.52], "gold_l" if row % 3 else "gold_d", f"rizo {row} {col}"))
    # Borde de abajo en mechones y patas traseras con pezuña.
    for k, x in enumerate((4.2, 6.4, 8.6, 10.8)):
        e.append(box([x, 5.7, 5.55], [x + 1.4, 6.5, 5.9], "gold_d", f"mechón {k}"))
    for side, x0 in (("der.", 3.7), ("izq.", 10.9)):
        e.append(box([x0, 6.0, 5.42], [x0 + 1.4, 7.5, 5.92], "gold", f"pata trasera {side}"))
        e.append(box([x0 + 0.2, 5.4, 5.48], [x0 + 1.2, 6.0, 5.86], "black", f"pezuña trasera {side}"))
    # Patas delanteras anudadas en el pecho.
    for side, sign in (("der.", 1), ("izq.", -1)):
        for k, (a, b, y0, y1) in enumerate([(3.7, 5.5, 12.9, 14.05), (4.6, 6.4, 12.0, 12.9), (5.5, 7.3, 11.1, 12.0)]):
            x0, x1 = (a, b) if sign == 1 else (16 - b, 16 - a)
            e.append(box([x0, y0, 10.05], [x1, y1, 10.45], "gold" if k != 1 else "gold_l", f"pata delantera {side} {k}"))
        px0, px1 = (6.1, 7.3) if sign == 1 else (8.7, 9.9)
        e.append(box([px0, 10.4, 10.1], [px1, 11.1, 10.48], "black", f"pezuña delantera {side}"))
    e.append(box([7.2, 11.2, 10.12], [8.8, 12.1, 10.48], "gold_d", "nudo"))
    # Cabeza del carnero detrás de la nuca, mirando hacia atrás, con los cuernos en espiral.
    e += [box([4.6, 13.4, 1.6], [11.4, 19.2, 3.55], "gold", "cabeza del carnero"),
          box([5.6, 13.0, 0.6], [10.4, 16.4, 1.6], "gold_l", "hocico del carnero"),
          box([7.0, 13.4, 0.35], [9.0, 14.4, 0.6], "black", "nariz del carnero"),
          box([5.2, 17.0, 1.4], [6.6, 17.8, 1.6], "black", "ojo der. del carnero"),
          box([9.4, 17.0, 1.4], [10.8, 17.8, 1.6], "black", "ojo izq. del carnero"),
          box([5.4, 19.2, 2.0], [10.6, 20.0, 3.4], "gold_l", "testuz")]
    for side, sign in (("der.", 1), ("izq.", -1)):
        horn = [(2.6, 4.6, 17.6, 20.0, 1.8, 3.8), (1.4, 2.6, 15.6, 19.0, 1.9, 3.7), (1.6, 3.4, 14.2, 15.6, 2.0, 3.6),
                (3.4, 4.6, 14.6, 16.2, 2.1, 3.5)]
        for k, (a, b, y0, y1, z0, z1) in enumerate(horn):
            x0, x1 = (a, b) if sign == 1 else (16 - b, 16 - a)
            e.append(box([x0, y0, z0], [x1, y1, z1], "ivory" if k % 2 == 0 else "ivory_d", f"cuerno {side} {k}"))
    return e


def jarngreipr():
    """Guantelete de hierro hasta la muñeca, puño de oro y remaches al frente. Por dentro de los anillos (5,15..10,85)."""
    e = ring(5.45, 10.55, 2.0, 7.0, 5.45, 10.55, 0.55, "iron", "guantelete")
    e += ring(5.3, 10.7, 7.0, 8.2, 5.3, 10.7, 0.5, "gold", "puño")
    e += [box([5.45, 1.5, 5.45], [10.55, 2.0, 10.55], "iron", "nudillos")]
    for x in (6.0, 7.4, 8.8):
        e.append(box([x, 4.4, 10.55], [x + 0.9, 5.2, 10.8], "iron", "remache"))
    return e


def megingjord():
    """Cinturón de cuero a la cintura con hebilla de oro trenzada y la punta colgando."""
    e = ring(3.45, 12.55, 2.6, 4.4, 5.45, 10.55, 0.55, "leather", "cinturón")
    e += [box([6.6, 2.2, 10.55], [9.4, 4.8, 11.25], "gold", "hebilla"),
          box([7.4, 3.0, 11.25], [8.6, 4.0, 11.45], "gold_d", "nudo"),
          box([9.6, 1.0, 10.6], [10.4, 2.6, 10.95], "leather", "punta")]
    return e


def draupnir_ring():
    """Anillo de oro macizo con un sello liso, en el mismo lugar que el anillo de los chacales (no se llevan juntos)."""
    e = ring(5.15, 10.85, 8.6, 9.6, 5.15, 10.85, 0.5, "gold", "aro")
    e += [box([7.2, 8.4, 10.85], [8.8, 9.8, 11.4], "gold", "sello"),
          box([7.6, 8.8, 11.4], [8.4, 9.4, 11.5], "gold_l", "brillo")]
    return e


def brisingamen():
    """Cadena de oro como la del Anj y cinco piezas en V sobre el pecho, alternando ámbar y oro (los cuatro enanos)."""
    e = [box([3.5, 13.3, 5.5], [12.5, 13.8, 6.0], "gold_d", "cadena atrás"),
         box([3.5, 13.3, 6.0], [4.0, 13.8, 10.5], "gold", "cadena der."),
         box([12.0, 13.3, 6.0], [12.5, 13.8, 10.5], "gold", "cadena izq.")]
    for i, (x, y) in enumerate([(4.4, 12.2), (5.7, 11.2), (7.3, 10.6), (8.9, 11.2), (10.2, 12.2)]):
        e.append(box([x, y, 10.5], [x + 1.4, y + 1.2, 11.2], "amber" if i % 2 == 0 else "gold", "pieza"))
    return e


def andvaranaut():
    """Anillo de oro con la gema roja de la maldición, en el lugar de los demás anillos."""
    e = ring(5.15, 10.85, 8.6, 9.6, 5.15, 10.85, 0.5, "gold", "aro")
    e += [box([7.3, 8.4, 10.85], [8.7, 9.8, 11.5], "ruby", "gema")]
    return e


def wolf_bulla():
    """Cordón de cuero como la cadena del Anj y el relicario redondo de oro con la loba en relieve sobre el pecho."""
    e = [box([3.5, 13.3, 5.5], [12.5, 13.8, 6.0], "leather_d", "cordón atrás"),
         box([3.5, 13.3, 6.0], [4.0, 13.8, 10.5], "leather_d", "cordón der."),
         box([12.0, 13.3, 6.0], [12.5, 13.8, 10.5], "leather_d", "cordón izq.")]
    for i in range(6):
        x, y = 4.0 + i * 0.6, 13.3 - i * 0.22
        e.append(box([x, y - 0.5, 10.5], [x + 0.6, y, 11.0], "leather_d", "cordón"))
        e.append(box([12.0 - (i + 1) * 0.6, y - 0.5, 10.5], [12.0 - i * 0.6, y, 11.0], "leather_d", "cordón"))
    e += [box([6.8, 9.2, 10.75], [9.2, 11.6, 11.6], "gold", "relicario"),
          box([7.2, 8.8, 10.75], [8.8, 12.0, 11.6], "gold", "relicario"),
          box([7.4, 9.8, 11.6], [8.6, 10.8, 11.75], "gold_d", "loba")]
    return e


def cerberus_collar():
    """Collar de Cerbero: cuero negro tachonado con púas de plata al frente y a los costados (atrás no: ahí van la égida
    y el tambor, y la capa lo tapa), y al frente una placa de bronce
    con las tres cabezas de perro (negras, con ojos de brasa y orejas en punta) y un trozo de la cadena rota de hierro
    que cuelga, la que Heracles usó para sacarlo del Hades."""
    e = ring(3.55, 12.45, 13.2, 14.2, 5.6, 10.4, 0.6, "black", "cuero")
    for x in (4.2, 5.8, 9.4, 11.0):
        e.append(box([x, 13.45, 10.4], [x + 0.8, 13.95, 11.2], "silver", "púa frente"))
    for z in (6.4, 7.8, 9.2):
        e.append(box([2.75, 13.45, z], [3.55, 13.95, z + 0.8], "silver", "púa der."))
        e.append(box([12.45, 13.45, z], [13.25, 13.95, z + 0.8], "silver", "púa izq."))
    # Placa de bronce con las tres cabezas.
    e.append(box([5.9, 10.8, 10.4], [10.1, 13.2, 10.85], "bronze", "placa"))
    e.append(box([5.7, 10.6, 10.45], [10.3, 10.8, 10.8], "bronze_d", "borde de la placa"))
    for k, x in enumerate((6.2, 7.5, 8.8)):
        e.append(box([x, 11.2, 10.85], [x + 1.0, 12.4, 11.15], "black", f"cabeza de perro {k}"))
        e.append(box([x + 0.25, 10.95, 11.15], [x + 0.75, 11.45, 11.4], "black", f"hocico {k}"))
        e.append(box([x, 12.4, 10.9], [x + 0.3, 12.9, 11.1], "black", f"oreja der. {k}"))
        e.append(box([x + 0.7, 12.4, 10.9], [x + 1.0, 12.9, 11.1], "black", f"oreja izq. {k}"))
        e.append(box([x + 0.15, 11.85, 11.15], [x + 0.35, 12.05, 11.22], "ember", f"ojo der. {k}"))
        e.append(box([x + 0.65, 11.85, 11.15], [x + 0.85, 12.05, 11.22], "ember", f"ojo izq. {k}"))
    # Cadena rota que cuelga de la placa.
    for k, y in enumerate((9.9, 9.0, 8.1)):
        wide = k % 2 == 0
        e.append(box([7.5 if wide else 7.75, y, 10.55 if wide else 10.45], [8.5 if wide else 8.25, y + 0.8, 10.75 if wide else 10.85],
                     "iron", f"eslabón {k}"))
    return e


def wind_fire_wheels():
    """En cada pierna, a la altura del tobillo (el pie llega a y 2): un aro de oro con cuatro rayos hacia afuera y
    lenguas de fuego detrás (hacia atrás es z bajo)."""
    y0, y1 = 2.2, 3.2
    e = ring(4.6, 11.4, y0, y1, 4.6, 11.4, 0.8, "gold", "aro")
    e += [box([5.6, y0 - 0.2, 4.0], [7.2, y1 + 0.2, 4.6], "gold_d", "rayo atrás der."),
          box([8.8, y0 - 0.2, 4.0], [10.4, y1 + 0.2, 4.6], "gold_d", "rayo atrás izq."),
          box([4.0, y0 - 0.2, 7.4], [4.6, y1 + 0.2, 8.6], "gold_d", "rayo der."),
          box([11.4, y0 - 0.2, 7.4], [12.0, y1 + 0.2, 8.6], "gold_d", "rayo izq.")]
    for i, (x0, h) in enumerate([(5.0, 2.4), (6.6, 3.4), (8.2, 2.8), (9.8, 3.6)]):
        e.append(box([x0, y1, 3.5], [x0 + 1.2, y1 + h, 4.3], "sun" if i % 2 else "ember", "llama"))
        e.append(box([x0 + 0.3, y1 + h, 3.6], [x0 + 0.9, y1 + h + 0.8, 4.2], "amber", "punta de llama"))
    return e


def five_color_stone():
    """Cordón de seda roja a la cintura con la piedra de Nüwa al frente: verde azulada con vetas de los otros cuatro
    colores (amarillo, blanco, rojo y negro)."""
    e = ring(3.45, 12.55, 2.6, 3.4, 5.45, 10.55, 0.5, "crest", "cordón")
    e += [box([6.8, 1.0, 10.55], [9.2, 3.6, 11.35], "turq", "piedra"),
          box([7.1, 1.4, 11.35], [7.8, 2.2, 11.55], "amber", "veta amarilla"),
          box([8.1, 2.4, 11.35], [8.9, 3.2, 11.55], "white", "veta blanca"),
          box([7.2, 2.6, 11.35], [7.8, 3.2, 11.55], "ruby", "veta roja"),
          box([8.2, 1.4, 11.35], [8.9, 2.0, 11.55], "onyx", "veta negra"),
          box([7.6, 3.6, 10.6], [8.4, 4.2, 11.1], "gold", "engarce")]
    return e


def erlang_eye():
    """Cabeza (4..12, frente en z 12): diadema de oro alrededor de la frente y el tercer ojo vertical al frente, con iris
    de oro y pupila oscura (Erlang Shen)."""
    e = ring(3.25, 12.75, 9.65, 10.65, 3.25, 12.75, 0.6, "gold", "diadema")
    e += [box([7.3, 9.05, 12.75], [8.7, 11.85, 12.95], "white", "ojo"),
          box([7.6, 9.65, 12.95], [8.4, 11.25, 13.1], "gold_l", "iris"),
          box([7.85, 10.05, 13.1], [8.15, 10.85, 13.2], "onyx", "pupila")]
    return e


def pixiu_bracelet():
    """Muñeca (la mano llega a y 2): cuentas de jade alrededor del brazo y el pixiu de oro arriba. Más afuera y más
    arriba que los guantes, para no compartir planos con ellos."""
    y0, y1 = 3.6, 4.6
    e = ring(5.35, 10.65, y0, y1, 5.35, 10.65, 0.6, "turq", "cuentas")
    e.append(box([7.0, y0 - 0.2, 10.65], [9.0, y1 + 0.4, 11.35], "gold", "pixiu"))
    return e


def yasakani_magatama():
    """Cuello (y 14) y pecho (z 10): cordón de oro y la magatama de jade al frente, con la cola curva."""
    # Mismo cordón que Brísingamen (los collares no se llevan juntos y ya no choca con el vellocino).
    e = [box([3.5, 13.2, 5.45], [12.5, 13.9, 6.05], "gold", "cordón atrás"),
         box([3.5, 13.2, 6.05], [4.05, 13.9, 10.55], "gold", "cordón der."),
         box([11.95, 13.2, 6.05], [12.5, 13.9, 10.55], "gold", "cordón izq.")]
    e += [box([7.0, 10.4, 10.55], [9.2, 12.4, 11.25], "turq", "magatama"),
          box([8.2, 9.6, 10.6], [9.2, 10.4, 11.2], "turq", "cola"),
          box([7.4, 12.4, 10.6], [8.2, 13.2, 11.05], "gold", "anilla")]
    return e


def raijin_drum():
    """Espalda (z 6): un taiko rojo laqueado con parche blanco, aros de oro y un palillo, colgado sobre la espalda."""
    return [box([4.5, 6.55, 3.4], [11.5, 13.45, 5.3], "sun_d", "tambor"),
            box([5.0, 7.0, 3.2], [11.0, 13.0, 3.4], "white", "parche"),
            box([4.3, 6.2, 3.6], [11.7, 6.5, 5.3], "gold", "aro abajo"),
            box([4.3, 13.5, 3.6], [11.7, 13.8, 5.3], "gold", "aro arriba"),
            box([3.6, 13.8, 4.1], [12.4, 14.3, 4.6], "onyx", "palillo")]


def tengu_mask():
    """Cara (frente en z 12): máscara roja con la nariz larga hacia adelante, cejas y bigote blancos."""
    return [box([4.2, 5.0, 12.85], [11.8, 12.0, 13.35], "ruby", "máscara"),
            box([7.2, 7.4, 13.35], [8.8, 9.0, 16.3], "crest", "nariz"),
            box([4.8, 10.4, 13.35], [7.2, 11.0, 13.55], "white", "ceja der."),
            box([8.8, 10.4, 13.35], [11.2, 11.0, 13.55], "white", "ceja izq."),
            box([5.6, 5.6, 13.35], [10.4, 6.4, 13.55], "white", "bigote")]


def kappa_dish():
    """Cabeza (arriba en y 12): el plato hondo verde lleno de agua sobre la coronilla."""
    return [box([5.0, 12.95, 5.0], [11.0, 13.55, 11.0], "turq", "plato"),
            box([5.6, 13.55, 5.6], [10.4, 13.75, 10.4], "wing", "agua")]


def nemean_lion_skin():
    """La piel del león de Nemea, como la lleva Heracles en las vasijas (capa, compite con el vellocino y usa su misma
    franja: espalda en z 5,4..5,95, por fuera de las reliquias de espalda; pecho en z 10,05..10,5).

    - Espalda: la piel entera en franjas verticales de tres tonos, unas más salientes que otras (relieve de pelaje), la
      columna más oscura, los bordes del vientre más claros y el borde de abajo desflecado; las patas traseras caen en
      las esquinas con almohadillas y garras de marfil, y la cola con su borla oscura cuelga al centro.
    - Melena: espesa sobre los hombros, con mechones que sobresalen a los lados del cuello y un fleco que baja por la
      parte alta de la espalda.
    - Pecho: las patas delanteras bajan en diagonal desde los hombros y se anudan al centro, con las garras de marfil.
    - Cabeza: cuelga detrás de la nuca como una capucha bajada (así no atraviesa la cabeza del jugador cuando mira a los
      lados), mirando hacia atrás: marco de melena con mechones, frente y ceño marcados, ojos ámbar con pupila, hocico
      claro con la nariz oscura, las fauces abiertas (boca roja oscura) con colmillos arriba y abajo, y orejas redondas."""
    e = []
    # ---- Espalda. Arriba de los cinturones (y > 5,9) la piel va en z 5,4..5,93 en franjas con relieve; a la altura de
    # los cinturones (que dan la vuelta por atrás en z 5,45..6) baja como una capa fina por fuera, z 5,32..5,42, sin
    # tocar las reliquias de espalda (z <= 5,3). Garras y cola cuelgan por debajo de los cinturones (y < 1).
    tones = ["fur", "fur_l", "fur", "fur_d", "fur", "fur_l", "fur", "fur_d"]
    hems = [2.9, 2.1, 2.7, 1.7, 2.3, 1.9, 2.8, 2.2]
    width = (12.4 - 3.6) / len(tones)
    for i, (tone, hem) in enumerate(zip(tones, hems)):
        x0 = 3.6 + i * width
        z0 = 5.4 if i % 2 == 0 else 5.48
        e.append(box([x0, 5.9, z0], [x0 + width, 12.0, 5.93], tone, f"piel franja {i}"))
        e.append(box([x0, hem, 5.32], [x0 + width, 5.9, 5.42], tone, f"piel baja franja {i}"))
    e.append(box([7.55, 5.9, 5.37], [8.45, 12.0, 5.47], "fur_d", "columna"))
    e.append(box([3.52, 5.9, 5.42], [3.62, 11.8, 5.92], "fur_l", "vientre der."))
    e.append(box([12.38, 5.9, 5.42], [12.48, 11.8, 5.92], "fur_l", "vientre izq."))
    # Fleco de melena que baja por la parte alta de la espalda, con puntas de distinto largo.
    for i, bottom in enumerate([11.0, 10.2, 10.8, 9.9, 10.6, 10.1, 10.9, 10.3]):
        x0 = 3.6 + i * width
        e.append(box([x0, bottom, 5.38], [x0 + width, 13.88, 5.91], "mane" if i % 2 == 0 else "fur_d", f"fleco {i}"))
    # Patas traseras en las esquinas de abajo (en la capa fina), con almohadilla y tres garras colgando.
    for side, x0 in (("der.", 3.6), ("izq.", 10.8)):
        e.append(box([x0, 0.3, 5.3], [x0 + 1.6, 1.7, 5.43], "fur_d", f"pata trasera {side}"))
        e.append(box([x0 + 0.1, -0.1, 5.33], [x0 + 1.5, 0.3, 5.41], "black", f"almohadilla trasera {side}"))
        for k in range(3):
            cx = x0 + 0.2 + k * 0.5
            e.append(box([cx, -0.55, 5.34], [cx + 0.25, -0.1, 5.4], "ivory", f"garra trasera {side} {k}"))
    # Cola al centro, bajo los cinturones, y su borla.
    e.append(box([7.65, -2.4, 5.34], [8.35, 0.9, 5.7], "fur", "cola"))
    e.append(box([7.45, -3.6, 5.31], [8.55, -2.4, 5.74], "mane", "borla de la cola"))
    # ---- Melena sobre los hombros y alrededor del cuello.
    e.append(box([3.6, 13.92, 5.4], [12.4, 14.55, 10.45], "mane", "melena en los hombros"))
    for side, (x0, x1) in (("der.", (3.0, 3.95)), ("izq.", (12.05, 13.0))):
        e.append(box([x0, 12.9, 5.62], [x1, 15.2, 9.4], "mane", f"melena del cuello {side}"))
        for k, (y1, z0) in enumerate([(15.7, 5.8), (15.4, 6.9), (15.9, 8.0)]):
            e.append(box([x0 + 0.1, 15.2, z0], [x1 - 0.1, y1, z0 + 0.9], "fur_d" if k % 2 else "mane", f"mechón {side} {k}"))
    # ---- Pecho: patas delanteras en diagonal (escalones) que se anudan, con garras.
    for side, sign in (("der.", 1), ("izq.", -1)):
        steps = [(3.7, 5.5, 12.9, 14.05), (4.6, 6.4, 12.0, 12.9), (5.5, 7.3, 11.1, 12.0)]
        for k, (a, b, y0, y1) in enumerate(steps):
            x0, x1 = (a, b) if sign == 1 else (16 - b, 16 - a)
            e.append(box([x0, y0, 10.05], [x1, y1, 10.45], "fur" if k != 1 else "fur_l", f"pata delantera {side} {k}"))
        px0, px1 = (6.1, 7.4) if sign == 1 else (8.6, 9.9)
        e.append(box([px0, 10.3, 10.1], [px1, 11.1, 10.48], "fur_d", f"zarpa {side}"))
        for k in range(3):
            cx = px0 + 0.15 + k * 0.42
            e.append(box([cx, 9.85, 10.2], [cx + 0.22, 10.3, 10.4], "ivory", f"garra delantera {side} {k}"))
    e.append(box([7.2, 11.2, 10.12], [8.8, 12.1, 10.48], "mane", "nudo"))
    e.append(box([7.55, 10.6, 10.15], [8.45, 11.2, 10.42], "fur_d", "cabo del nudo"))
    # ---- Cabeza del león detrás de la nuca, mirando hacia atrás (-z).
    e.append(box([2.8, 13.2, 2.4], [13.2, 21.0, 3.9], "mane", "melena de la cabeza"))
    for k, (x0, y0, x1, y1) in enumerate([(2.2, 15.0, 2.8, 19.4), (13.2, 15.0, 13.8, 19.4), (4.0, 21.0, 6.6, 21.7),
                                          (9.4, 21.0, 12.0, 21.7), (6.8, 21.0, 9.2, 21.4), (3.4, 12.6, 5.6, 13.2),
                                          (10.4, 12.6, 12.6, 13.2)]):
        e.append(box([x0, y0, 2.6], [x1, y1, 3.7], "fur_d", f"mechón de la melena {k}"))
    e.append(box([4.4, 14.2, 1.4], [11.6, 19.6, 2.4], "fur", "cara"))
    e.append(box([4.8, 18.4, 0.9], [11.2, 19.2, 1.4], "fur_d", "ceño"))
    for side, x0 in (("der.", 5.3), ("izq.", 9.3)):
        e.append(box([x0, 17.3, 1.15], [x0 + 1.4, 18.1, 1.4], "amber", f"ojo {side}"))
        e.append(box([x0 + 0.45, 17.4, 1.05], [x0 + 0.95, 18.0, 1.15], "black", f"pupila {side}"))
    e.append(box([5.8, 14.6, 0.4], [10.2, 17.0, 1.4], "fur_l", "hocico"))
    e.append(box([7.2, 16.9, 0.8], [8.8, 18.4, 1.4], "fur", "caña de la nariz"))
    e.append(box([7.0, 16.3, 0.15], [9.0, 17.0, 0.4], "black", "nariz"))
    e.append(box([6.2, 13.8, 0.55], [9.8, 14.6, 1.4], "crest_d", "fauces"))
    e.append(box([6.0, 12.9, 0.75], [10.0, 13.8, 1.8], "fur_l", "mandíbula"))
    for side, x0 in (("der.", 6.35), ("izq.", 9.05)):
        e.append(box([x0, 13.75, 0.45], [x0 + 0.6, 14.65, 0.85], "ivory", f"colmillo de arriba {side}"))
    for side, x0 in (("der.", 7.1), ("izq.", 8.5)):
        e.append(box([x0, 13.8, 0.6], [x0 + 0.4, 14.25, 0.95], "ivory", f"colmillo de abajo {side}"))
    for side, x0 in (("der.", 3.9), ("izq.", 10.5)):
        e.append(box([x0, 19.7, 1.8], [x0 + 1.6, 21.1, 2.6], "fur_d", f"oreja {side}"))
        e.append(box([x0 + 0.4, 20.0, 1.65], [x0 + 1.2, 20.8, 1.8], "fur_l", f"oreja por dentro {side}"))
    return e


def freyr_sword():
    """Espada de Freyr envainada (cinturón, compite con el escarabajo, Megingjörð y la piedra: usa los planos de
    Megingjörð). Cinturón de cuero y la vaina al frente de la cadera izquierda, por fuera del brazo, con la guarda y el
    pomo de oro arriba."""
    e = ring(3.45, 12.55, 2.6, 3.6, 5.45, 10.55, 0.55, "leather", "cinturón")
    e += [box([10.4, -6.0, 10.6], [11.6, 2.4, 11.6], "leather", "vaina"),
          box([10.3, -6.6, 10.5], [11.7, -6.0, 11.7], "gold", "contera"),
          box([10.3, 1.8, 10.5], [11.7, 2.4, 11.7], "gold", "boca de la vaina"),
          box([9.8, 2.4, 10.4], [12.2, 3.0, 11.8], "gold", "guarda"),
          box([10.6, 3.0, 10.8], [11.4, 5.0, 11.4], "leather_d", "puño"),
          box([10.4, 5.0, 10.6], [11.6, 5.8, 11.6], "gold", "pomo")]
    return e


def falcon_cloak():
    """Manto de plumas de halcón de Freyja (espalda, compite con la égida, las alas de Ícaro y el tambor: va por detrás de
    la capa, z < 5,35, como las alas). Cae por la espalda y se abre abajo, con las barras claras del plumaje y un borde de
    puntas de plumas; una esclavina de plumas por encima de los hombros de la capa (sin bajar por el pecho, donde van los collares),
    cuello claro y broche de oro."""
    e = [box([3.2, 3.0, 4.6], [12.8, 13.6, 5.3], "falcon", "manto"),
         box([2.6, 0.4, 4.3], [13.4, 3.0, 5.1], "falcon", "manto abierto abajo")]
    for i, y in enumerate((4.2, 6.6, 9.0, 11.4)):
        e.append(box([3.1, y, 4.4], [12.9, y + 0.6, 4.55], "falcon_l", f"barra {i}"))
    e.append(box([2.5, 1.4, 4.1], [13.5, 2.0, 4.25], "falcon_l", "barra de abajo"))
    x = 2.6
    while x < 13.2:
        e.append(box([x, -1.0, 4.45], [x + 0.9, 0.4, 5.0], "falcon_d", "punta de pluma"))
        x += 1.6
    e += [box([2.9, 14.6, 4.4], [13.1, 15.1, 10.7], "falcon", "esclavina"),
          box([3.4, 15.1, 4.7], [12.6, 15.6, 10.4], "feather_w", "cuello"),
          box([7.0, 14.7, 10.7], [9.0, 15.3, 11.0], "gold", "broche")]
    return e


def attach(part, name, offset=(0, 0, 0)):
    return {"type": "model", "model_part": part, "model": f"{NS}:item/{name}_worn", "offset": list(offset), "centered": True}


def both_hands(slot, name, offset=(0, 0, 0)):
    return [{"type": "minecraft:if_slot", "slot": f"hand/{slot}", "then": attach(":main_hand", name, offset)},
            {"type": "minecraft:if_slot", "slot": f"offhand/{slot}", "then": attach(":off_hand", name, offset)}]


WORN = {
    "ankh": (ankh_necklace, [attach("body", "ankh")]),
    "golden_scarab": (scarab_belt, [attach("body", "golden_scarab")]),
    "eye_of_horus": (eye_diadem, [attach("head", "eye_of_horus")]),
    "jackal_mask": (jackal_mask, [attach("head", "jackal_mask")]),
    "mummy_wraps": (mummy_wraps, both_hands("glove", "mummy_wraps")),
    "jackal_ring": (jackal_ring, both_hands("ring", "jackal_ring", (0, -1, 0))),
    # Tanda 2
    "ra_disk": (lambda: model(ra_headdress()), [attach("head", "ra_disk")]),
    "bastet_amulet": (lambda: model(bastet_pendant()), [attach("body", "bastet_amulet")]),
    "sekhmet_claws": (lambda: model(sekhmet_claws_worn()), both_hands("glove", "sekhmet_claws")),
    # Grecia: las sandalias van en las dos piernas.
    "hermes_sandals": (lambda: model(sandal()), [attach("right_leg", "hermes_sandals"), attach("left_leg", "hermes_sandals")]),
    "hades_helm": (lambda: model(helm()), [attach("head", "hades_helm")]),
    "athena_aegis": (lambda: model(aegis_worn()), [attach("body", "athena_aegis")]),
    # Grecia, tanda 2
    "icarus_wings": (lambda: model(icarus_wings()), [attach("body", "icarus_wings")]),
    "golden_fleece": (lambda: model(golden_fleece()), [attach("body", "golden_fleece")]),
    "cerberus_collar": (lambda: model(cerberus_collar()), [attach("body", "cerberus_collar")]),
    # Roma
    "wolf_bulla": (lambda: model(wolf_bulla()), [attach("body", "wolf_bulla")]),
    # Nórdicos
    "jarngreipr": (lambda: model(jarngreipr()), both_hands("glove", "jarngreipr")),
    "megingjord": (lambda: model(megingjord()), [attach("body", "megingjord")]),
    "draupnir": (lambda: model(draupnir_ring()), both_hands("ring", "draupnir", (0, -1, 0))),
    "brisingamen": (lambda: model(brisingamen()), [attach("body", "brisingamen")]),
    "andvaranaut": (lambda: model(andvaranaut()), both_hands("ring", "andvaranaut", (0, -1, 0))),
    # China: las ruedas van en las dos piernas.
    "wind_fire_wheels": (lambda: model(wind_fire_wheels()),
                         [attach("right_leg", "wind_fire_wheels"), attach("left_leg", "wind_fire_wheels")]),
    "five_color_stone": (lambda: model(five_color_stone()), [attach("body", "five_color_stone")]),
    # China, tanda 2
    "erlang_eye": (lambda: model(erlang_eye()), [attach("head", "erlang_eye")]),
    "pixiu_bracelet": (lambda: model(pixiu_bracelet()), both_hands("ring", "pixiu_bracelet", (0, -1, 0))),
    # Japón
    "yasakani_magatama": (lambda: model(yasakani_magatama()), [attach("body", "yasakani_magatama")]),
    "raijin_drum": (lambda: model(raijin_drum()), [attach("body", "raijin_drum")]),
    # Japón, tanda 2
    "tengu_mask": (lambda: model(tengu_mask()), [attach("head", "tengu_mask")]),
    "kappa_dish": (lambda: model(kappa_dish()), [attach("head", "kappa_dish")]),
    # Grecia, tanda 3
    "nemean_lion_skin": (lambda: model(nemean_lion_skin()), [attach("body", "nemean_lion_skin")]),
    # Nórdicos, tanda 3
    "freyr_sword": (lambda: model(freyr_sword()), [attach("body", "freyr_sword")]),
    "falcon_cloak": (lambda: model(falcon_cloak()), [attach("body", "falcon_cloak")]),
}


def palette():
    img = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for i, name in enumerate(SWATCHES):
        u, v = (i % 8) * 2, (i // 8) * 2
        for y in range(2):
            for x in range(2):
                img[v + y][u + x] = H(COLORS[name])
    return img


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


# Reliquias con textura pintada por cara (luz, sombras, ruido y patrón del material; worn_texture.py) en lugar de los
# colores planos de la paleta.
TEXTURED = {"nemean_lion_skin", "hermes_sandals", "hades_helm", "athena_aegis", "icarus_wings", "golden_fleece", "cerberus_collar"}


if __name__ == "__main__":
    import worn_texture  # noqa: E402
    png(os.path.join(ASSETS, "textures", "item", "worn_palette.png"), palette())
    os.makedirs(os.path.join(ASSETS, "textures", "item", "worn"), exist_ok=True)
    for name, (make, render) in WORN.items():
        m = make()
        for el in m["elements"]:
            assert all(-16 <= c <= 32 for c in el["from"] + el["to"]), (name, el["name"])
            assert all(a < b for a, b in zip(el["from"], el["to"])), (name, el["name"])
        if name in TEXTURED:
            m, img = worn_texture.texture(name, m["elements"], COLORS, NS)
            img.save(os.path.join(ASSETS, "textures", "item", "worn", name + ".png"))
        for el in m["elements"]:
            el.pop("__color", None)
        write(os.path.join(ASSETS, "models", "item", name + "_worn.json"), m)
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": {
            "type": "minecraft:select",
            "property": "minecraft:display_context",
            "cases": [{"when": "none", "model": {"type": "minecraft:model", "model": f"{NS}:item/{name}_worn"}}],
            "fallback": {"type": "minecraft:model", "model": f"{NS}:item/{name}"},
        }})
        write(os.path.join(FABRIC_ASSETS, "trinkets", name + ".json"), {"target": [f"{NS}:{name}"], "render": render})
        print(f"{name}: {len(m['elements'])} elementos")
