"""Borrador de las entidades de la tanda 1 japonesa, con la misma geometría que tendrán en el juego (partes de ModelPart
con UV de caja; la y crece hacia abajo, el frente es -z y el suelo está en y = 24):

- Kitsune de nueve colas (Esfera estelar del kitsune): zorro blanco de Inari con marcas rojas en la cara, babero rojo
  como el de sus estatuas en los santuarios, ojos dorados y nueve colas en abanico con la punta que brilla; dos
  kitsunebi (fuegos fatuos azules) giran a su alrededor.
- Tortuga de Urashima: tortuga marina gigante, la minogame de mil años del arte japonés: caparazón verde oscuro con
  escudos de borde ámbar, aletas grandes, piel oliva con manchas claras y algas que cuelgan del caparazón como una cola.

Usa las piezas de china_entities.py; cada modelo pinta con su propia semilla.
Uso: python japan_entities.py <salida.json> [<carpeta_png>]"""
import json
import math
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import china_entities as CE  # noqa: E402
from china_entities import C, Model, cube, mix, pick, front_x  # noqa: E402

# ---------------------------------------------------------------- Kitsune
WHITE = [C("#f4f2ec"), C("#ebe8e0"), C("#faf8f2"), C("#e2ded4")]
WHITE_D = C("#c9c4b8")
VERMILION = C("#d8392b")
GOLD_EYE = C("#ffd23a")
FOXFIRE = [C("#9fd8ff"), C("#c8ecff"), C("#6fb8ff")]


def white_fur(face, x, y, w, h):
    if face == "bottom":
        return WHITE_D, None
    return (WHITE_D if random.random() < 0.08 else pick(WHITE)), None


def kitsune_head(face, x, y, w, h):
    """Cara: ojos dorados que brillan con las marcas rojas de Inari encima y a los lados."""
    if face == "front":
        if y == 2 and x in (1, w - 2):
            return GOLD_EYE, GOLD_EYE
        if y == 1 and x in (0, 1, w - 2, w - 1):
            return VERMILION, None
        if y == 0 and x in (w // 2 - 1, w // 2):
            return VERMILION, None
    return white_fur(face, x, y, w, h)


def kitsune_snout(face, x, y, w, h):
    if face == "front" and y == 0 and x == w // 2:
        return C("#1a1418"), None
    return white_fur(face, x, y, w, h)


def ear(face, x, y, w, h):
    if face == "front" and y >= 1:
        return C("#f2b8b0"), None
    if y == 0:
        return VERMILION, None
    return white_fur(face, x, y, w, h)


def bib(face, x, y, w, h):
    if y == h - 1:
        return C("#8c1d16"), None
    return pick([C("#d8392b"), C("#c42a20")]), None


def tail_base(face, x, y, w, h):
    return white_fur(face, x, y, w, h)


def tail_tip(face, x, y, w, h):
    """Punta de la cola: blanca que pasa a llama celeste (brilla) en el extremo de atrás."""
    if face == "back":
        glow = True
    elif face in ("west", "east"):
        glow = front_x(face, x, w) >= w - 2
    elif face in ("top", "bottom"):
        glow = y >= h - 2
    else:
        glow = False
    if glow:
        c = pick(FOXFIRE)
        return c, c
    return white_fur(face, x, y, w, h)


def foxfire(face, x, y, w, h):
    c = pick(FOXFIRE)
    return c, c


def kitsune():
    m = Model()
    m.part("body", None, [0, 15, 0], [0, 0, 0], [cube(-3, -3, -5, 6, 6, 10, white_fur)])
    m.part("bib", "body", [0, -1, -5], [0.25, 0, 0], [cube(-2.5, 0, -0.6, 5, 4, 1, bib)])
    head = m.part("head", None, [0, 13, -5], [0, 0, 0], [
        cube(-3, -4, -4, 6, 5, 5, kitsune_head),
        cube(-1.5, -1, -7, 3, 2, 3, kitsune_snout),
    ])
    m.part("ear_r", head, [-2, -4, -2], [0, 0, -0.2], [cube(-1, -3, -0.5, 2, 3, 1, ear)])
    m.part("ear_l", head, [2, -4, -2], [0, 0, 0.2], [cube(-1, -3, -0.5, 2, 3, 1, ear)])
    for name, x, z in (("leg_fr", -1.8, -3.5), ("leg_fl", 1.8, -3.5), ("leg_hr", -1.8, 3.5), ("leg_hl", 1.8, 3.5)):
        m.part(name, None, [x, 18, z], [0, 0, 0], [cube(-1, 0, -1, 2, 6, 2, white_fur)])
    # Nueve colas en abanico, alzadas detrás del cuerpo.
    for i in range(9):
        yaw = (i - 4) * 0.22
        pitch = 0.85 + 0.35 * (1 - abs(i - 4) / 4)
        t = m.part(f"tail{i}", "body", [0, -1.5, 4.5], [pitch, yaw, 0], [cube(-1, -1, 0, 2, 2, 6, tail_base)])
        m.part(f"tail{i}_tip", t, [0, 0, 6], [0.35, 0, 0], [cube(-1.5, -1.5, 0, 3, 3, 4, tail_tip)])
    # Kitsunebi: dos fuegos fatuos que giran alrededor (se animan en el modelo).
    orbit = m.part("foxfire", None, [0, 12, 0], [0, 0, 0], [])
    m.part("foxfire_a", orbit, [9, 0, 0], [0, 0, 0], [cube(-1, -1, -1, 2, 2, 2, foxfire)])
    m.part("foxfire_b", orbit, [-9, -2, 0], [0, 0, 0], [cube(-1, -1, -1, 2, 2, 2, foxfire)])
    return m


# ---------------------------------------------------------------- Tortuga de Urashima
SHELL = [C("#2f4a2a"), C("#355230"), C("#2a4325")]
SCUTE = C("#c8902e")
SKIN = [C("#7a8a4a"), C("#6e7e40"), C("#859652")]
SKIN_SPOT = C("#c8d29a")
BELLY = [C("#e6d8a8"), C("#dccd98")]
WEED = [C("#3f8a3a"), C("#2f6a2a"), C("#5aa04a")]


def shell(face, x, y, w, h):
    """Caparazón: escudos hexagonales verde oscuro con el borde ámbar; abajo, el plastrón crema."""
    if face == "bottom":
        return (C("#b8a878") if y % 4 == 0 or x % 5 == 0 else pick(BELLY)), None
    cx, cy = x % 6, (y + (x // 6) % 2 * 3) % 6
    if cx == 0 or cy == 0:
        return SCUTE, None
    return mix(pick(SHELL), C("#4a6a3a"), 0.3 if (cx in (2, 3) and cy in (2, 3)) else 0.0), None


def skin(face, x, y, w, h):
    if random.random() < 0.12:
        return SKIN_SPOT, None
    if face == "bottom":
        return pick(BELLY), None
    return pick(SKIN), None


def turtle_head(face, x, y, w, h):
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y == 1 and fx == 1:
            return C("#1a1a10"), None
    if face == "front" and y == h - 1:
        return C("#4a4a2a"), None
    return skin(face, x, y, w, h)


def seaweed(face, x, y, w, h):
    """Algas (minogame): mechones verdes que cuelgan hacia atrás, con huecos."""
    if face not in ("west", "east", "top", "bottom"):
        return None, None
    fx = front_x(face, x, w) if face in ("west", "east") else y
    lane = (x if face in ("top", "bottom") else y) % 3
    if lane == 2 or fx > w * (0.55 + 0.45 * ((x * 7 + y * 3) % 5) / 4):
        return None, None
    return pick(WEED), None


def turtle():
    m = Model()
    body = m.part("body", None, [0, 18, 0], [0, 0, 0], [
        cube(-9, -3, -10, 18, 4, 20, shell),
        cube(-7, -6, -8, 14, 3, 16, shell),
        cube(-4, -8, -5, 8, 2, 10, shell),
    ])
    for k, x in enumerate((-4, 0, 4)):
        m.part(f"seaweed{k}", body, [x, -2, 10], [-0.15, 0, 0], [cube(0, -1, 0, 0, 5, 12, seaweed)])
    neck = m.part("neck", None, [0, 18, -10], [0, 0, 0], [cube(-2, -2, -3, 4, 4, 3, skin)])
    m.part("head", neck, [0, -0.5, -3], [0, 0, 0], [cube(-2.5, -2.5, -5, 5, 5, 5, turtle_head)])
    m.part("flipper_fr", None, [-8, 19, -6], [0, 0.3, 0.2], [cube(-12, -0.5, -2.5, 12, 1, 5, skin)])
    m.part("flipper_fl", None, [8, 19, -6], [0, -0.3, -0.2], [cube(0, -0.5, -2.5, 12, 1, 5, skin)])
    m.part("flipper_hr", None, [-7, 19, 8], [0, -0.4, 0.1], [cube(-6, -0.5, -2, 6, 1, 4, skin)])
    m.part("flipper_hl", None, [7, 19, 8], [0, 0.4, -0.1], [cube(0, -0.5, -2, 6, 1, 4, skin)])
    return m


MODELS = {"kitsune": (kitsune, 64, 21), "turtle": (turtle, 128, 22)}
SCALE = {"kitsune": 1.5, "turtle": 1.7}


def bake_one(key):
    make, width, seed = MODELS[key]
    random.seed(seed)
    return CE.bake(make(), width)


if __name__ == "__main__":
    out = {}
    for key in MODELS:
        parts, img, glow, w, h = bake_one(key)
        out[key] = {"parts": parts, "texW": w, "texH": h, "tex": CE.b64(img), "glow": CE.b64(glow), "scale": SCALE[key]}
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8"))
    print("ok", {k: (v["texW"], v["texH"]) for k, v in out.items()})
