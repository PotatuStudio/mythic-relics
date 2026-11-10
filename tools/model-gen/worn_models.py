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
         "silver": ("silver", "silver_d"), "bronze": ("bronze", "bronze_d"), "iron": ("iron_l", "iron_d")}


def uv(color):
    i = SWATCHES.index(color)
    u, v = (i % 8) * 2, (i // 8) * 2
    return [u + 0.5, v + 0.5, u + 1.5, v + 1.5]


def box(frm, to, color, name=""):
    top, bottom = SHADE.get(color, (color, color))
    faces = {side: {"uv": uv(color), "texture": "#p"} for side in ("north", "south", "east", "west")}
    faces["up"] = {"uv": uv(top), "texture": "#p"}
    faces["down"] = {"uv": uv(bottom), "texture": "#p"}
    return {"name": name, "from": [round(v, 3) for v in frm], "to": [round(v, 3) for v in to], "faces": faces}


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
    """En cada pierna (centro en y 8; el pie llega a y 2): suela, tiras y un ala blanca en el talón, hacia atrás."""
    e = [box([5.5, 1.5, 5.5], [10.5, 2.25, 11.0], "leather", "suela")]
    e += ring(5.6, 10.4, 2.25, 3.0, 5.6, 10.4, 0.5, "leather", "tira del pie")
    e += ring(5.6, 10.4, 4.5, 5.25, 5.6, 10.4, 0.5, "leather", "tira del tobillo")
    for side, x0, x1 in (("ext.", 4.6, 5.5), ("int.", 10.5, 11.4)):
        e += [box([x0, 3.4, 4.6], [x1, 5.0, 7.2], "wing", f"ala {side}"),
              box([x0, 4.6, 3.0], [x1, 6.4, 4.6], "wing", f"ala {side}"),
              box([x0, 6.0, 1.6], [x1, 7.6, 3.0], "wing_d", f"punta ala {side}")]
    return e


def helm():
    """Yelmo corintio: casco negro alrededor de la cabeza, protectores de mejillas y la cimera roja.

    Va un poco más afuera (2,95..13,05) que las piezas de cara y la corona de Ra, para no compartir planos con ellas."""
    e = ring(2.95, 13.05, 7.1, 12.85, 2.95, 13.05, 0.7, "black", "casco")
    e += [box([2.95, 12.85, 2.95], [13.05, 13.55, 13.05], "black", "tapa"),
          box([2.95, 3.1, 12.35], [6.4, 7.1, 13.05], "black", "mejilla der."),
          box([9.6, 3.1, 12.35], [13.05, 7.1, 13.05], "black", "mejilla izq."),
          box([7.35, 5.1, 12.35], [8.65, 9.6, 13.05], "black", "nasal"),
          box([2.95, 3.1, 2.95], [13.05, 7.1, 3.65], "black", "nuca"),
          box([7.05, 13.55, 2.45], [8.95, 16.1, 13.45], "crest", "cimera"),
          box([7.25, 16.1, 3.45], [8.75, 16.7, 12.05], "crest", "crin")]
    return e


def aegis_worn():
    """Égida: piel dorada a la espalda (sobre los hombros) con el gorgoneion al centro. Apartada de la cadena de los
    collares (Anj, Bastet) para no compartir planos con ella."""
    e = [box([3.5, 5.0, 4.6], [12.5, 14.0, 5.35], "gold", "piel"),
         box([3.2, 14.0, 4.6], [12.8, 14.7, 10.35], "gold", "hombros"),
         box([6.5, 8.0, 4.1], [9.5, 11.0, 4.6], "stone", "gorgoneion"),
         box([6.2, 10.8, 3.95], [9.8, 11.5, 4.6], "snake", "serpientes")]
    for i, x in enumerate((4.0, 6.0, 8.0, 10.0)):
        e.append(box([x, 4.2, 4.75], [x + 1.2, 5.0, 5.25], "gold_d", f"fleco {i}"))
    return e


def mirror(e):
    """Copia un elemento al otro lado del cuerpo (x -> 16 - x)."""
    m = json.loads(json.dumps(e))
    m["from"][0], m["to"][0] = 16 - e["to"][0], 16 - e["from"][0]
    return m


def icarus_wings():
    """Arnés de cera en la espalda y dos alas abiertas en escalera: hueso, cobertoras y plumas largas. Van por detrás
    del vellocino (z < 5,4) para poder llevarlos juntos."""
    e = [box([6.0, 8.5, 4.75], [10.0, 13.0, 5.35], "wax", "cera")]
    side = [box([10.0, 12.6, 4.6], [19.0, 14.0, 5.3], "wing", "hueso"),
            box([10.0, 10.0, 4.7], [20.0, 12.6, 5.2], "wing", "cobertoras")]
    for i, (x, low) in enumerate(((10.5, 6.0), (13.0, 5.0), (15.5, 4.0), (18.0, 3.2))):
        side.append(box([x, low, 4.8], [x + 2.4, 10.0, 5.1], "wing_d", f"pluma {i}"))
    side.append(box([11.0, 13.0, 4.4], [12.0, 13.6, 4.6], "wax", "gota de cera"))
    return e + side + [mirror(p) for p in side]


def golden_fleece():
    """Piel dorada sobre la espalda y los hombros, con dos patas al frente que se anudan en el pecho. Los hombros quedan
    dentro de los de la égida (y la cadena de los collares, entre la piel y el cuerpo) para no compartir planos."""
    e = [box([3.6, 6.5, 5.4], [12.4, 13.95, 5.95], "gold", "lomo"),
         box([3.6, 13.92, 5.4], [12.4, 14.55, 10.45], "gold", "hombros"),
         box([3.6, 11.0, 10.05], [6.2, 14.05, 10.45], "gold", "pata der."),
         box([9.8, 11.0, 10.05], [12.4, 14.05, 10.45], "gold", "pata izq."),
         box([6.2, 11.6, 10.1], [9.8, 12.3, 10.35], "gold_d", "nudo")]
    for i, x in enumerate((4.2, 6.4, 8.6, 10.8)):
        e.append(box([x, 5.6, 5.5], [x + 1.4, 6.5, 5.9], "gold_d", f"rizo {i}"))
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
    """Collar de cuero negro con púas de plata y un medallón de bronce con tres pares de ojos de brasa."""
    e = ring(3.55, 12.45, 13.2, 14.2, 5.6, 10.4, 0.6, "black", "cuero")
    for x in (4.6, 7.6, 10.6):
        e.append(box([x, 13.45, 10.4], [x + 0.8, 13.95, 11.2], "silver", "púa frente"))
    for z in (6.8, 8.8):
        e.append(box([2.75, 13.45, z], [3.55, 13.95, z + 0.8], "silver", "púa der."))
        e.append(box([12.45, 13.45, z], [13.25, 13.95, z + 0.8], "silver", "púa izq."))
    e.append(box([6.6, 11.0, 10.4], [9.4, 13.2, 10.9], "bronze", "medallón"))
    for x in (6.9, 7.8, 8.7):
        e.append(box([x, 12.2, 10.9], [x + 0.4, 12.5, 11.0], "ember", "ojos"))
    return e


# --- China (2026-11-10) ---
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


if __name__ == "__main__":
    png(os.path.join(ASSETS, "textures", "item", "worn_palette.png"), palette())
    for name, (make, render) in WORN.items():
        m = make()
        for el in m["elements"]:
            assert all(-16 <= c <= 32 for c in el["from"] + el["to"]), (name, el["name"])
            assert all(a < b for a, b in zip(el["from"], el["to"])), (name, el["name"])
        write(os.path.join(ASSETS, "models", "item", name + "_worn.json"), m)
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": {
            "type": "minecraft:select",
            "property": "minecraft:display_context",
            "cases": [{"when": "none", "model": {"type": "minecraft:model", "model": f"{NS}:item/{name}_worn"}}],
            "fallback": {"type": "minecraft:model", "model": f"{NS}:item/{name}"},
        }})
        write(os.path.join(FABRIC_ASSETS, "trinkets", name + ".json"), {"target": [f"{NS}:{name}"], "render": render})
        print(f"{name}: {len(m['elements'])} elementos")
