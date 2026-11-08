"""Modelos 3D de las reliquias puestas (Trinkets Updated, render "model") y su paleta.

Cómo los ubica Trinkets 4.2.1 (leído de sus fuentes): el modelo va pegado a la parte del cuerpo (se mueve con ella),
centrado en el centro de esa parte más offset × medio tamaño (+X = izquierda de quien lo lleva, +Y arriba, +Z frente).
Con escala 1 y "centered", el píxel (x, y, z) del modelo queda en (x-8, y-8, z-8) píxeles del jugador desde ese punto.
Así, con offset [0,0,0]:
  - cuerpo (body):   x 4..12, y 2..14 (14 = cuello), z 6..10 (10 = pecho)
  - cabeza (head):   x, y, z 4..12 (frente en z 12)
  - brazo (arm):     x 6..10, y 2..14 (2 = extremo de la mano), z 6..10
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
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from mclib import H, png  # noqa: E402
from moddata import ASSETS, FABRIC_ASSETS  # noqa: E402

NS = "mythicrelics"
# Paleta de 4x4 casillas de 4x4 px (una por color). Cada cara de un elemento toma una casilla entera.
SWATCHES = [
    "gold_l", "gold", "gold_d", "gold_dd",
    "lapis_l", "lapis", "turq_l", "turq",
    "linen_l", "linen", "linen_d", "linen_dd",
    "onyx_hi", "onyx", "onyx_d", "white",
]
COLORS = {
    "gold_l": "#ffe27a", "gold": "#f0c040", "gold_d": "#c8901e", "gold_dd": "#8a5a10",
    "lapis_l": "#5f86e6", "lapis": "#2f52b4", "turq_l": "#8af0dc", "turq": "#34bfae",
    "linen_l": "#fbf5e2", "linen": "#e2d6b4", "linen_d": "#bfae86", "linen_dd": "#8f7d58",
    "onyx_hi": "#3e383c", "onyx": "#1c1719", "onyx_d": "#0e0b0c", "white": "#f4f1e6",
}
# Cara de arriba más clara y de abajo más oscura: da volumen aunque cada elemento sea de un color.
SHADE = {"gold": ("gold_l", "gold_d"), "gold_l": ("gold_l", "gold"), "gold_d": ("gold", "gold_dd"),
         "lapis": ("lapis_l", "lapis"), "turq": ("turq_l", "turq"), "linen": ("linen_l", "linen_d"),
         "linen_l": ("linen_l", "linen"), "linen_d": ("linen", "linen_dd"), "onyx": ("onyx_hi", "onyx_d"),
         "onyx_hi": ("onyx_hi", "onyx"), "white": ("white", "white"), "lapis_l": ("lapis_l", "lapis"),
         "turq_l": ("turq_l", "turq"), "onyx_d": ("onyx", "onyx_d"), "linen_dd": ("linen_d", "linen_dd"),
         "gold_dd": ("gold_d", "gold_dd")}


def uv(color):
    i = SWATCHES.index(color)
    u, v = (i % 4) * 4, (i // 4) * 4
    return [u + 0.5, v + 0.5, u + 3.5, v + 3.5]


def box(frm, to, color, name=""):
    top, bottom = SHADE[color]
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
    e = ring(3.25, 12.75, 9.5, 10.5, 3.25, 12.75, 0.75, "gold", "diadema")
    z = 12.75
    e += [
        box([6.4, 9.0, z], [9.6, 11.6, z + 0.4], "lapis", "placa"),
        box([6.7, 9.9, z + 0.4], [9.3, 10.6, z + 0.55], "white", "ojo"),
        box([7.7, 9.9, z + 0.55], [8.4, 10.6, z + 0.65], "lapis_l", "pupila"),
        box([6.7, 10.9, z + 0.4], [9.3, 11.25, z + 0.55], "gold", "ceja"),
        box([6.9, 9.15, z + 0.4], [7.25, 9.9, z + 0.55], "gold", "lágrima"),
        box([8.8, 9.15, z + 0.4], [9.15, 9.9, z + 0.55], "gold", "espiral"),
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
}


def palette():
    img = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for i, name in enumerate(SWATCHES):
        u, v = (i % 4) * 4, (i // 4) * 4
        for y in range(4):
            for x in range(4):
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
