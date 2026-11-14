"""Entidades nórdicas nuevas (2026-11-10), con la misma geometría que tendrán en el juego (partes de ModelPart con UV de
caja; la y crece hacia abajo y el frente es -z):

- Cuervo de Odín (Huginn y Muninn): cuervo grande negro azabache con brillo azulado, pico negro y grueso, plumas
  erizadas en la garganta y ojos dorados que brillan. Vuela con las alas abiertas (se baten) y la cola en abanico.
- Skíðblaðnir: el barco de Freyr, que se pliega como un pañuelo. Casco de madera clara casi dorada con borde de oro,
  una línea de runas azules, proa con cabeza de dragón, mástil y vela a franjas blancas y doradas con el sol de Freyr.
  El casco sigue las medidas del bote vanilla (mismos nombres de partes) para que flote y se maneje igual.

Usa las piezas de china_entities.py; cada modelo pinta con su propia semilla.
Uso: python norse_entities.py <salida.json>"""
import json
import math
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import china_entities as CE  # noqa: E402
from china_entities import C, Model, cube, pick  # noqa: E402

# ---------------------------------------------------------------- Cuervo
BLACK = [C("#16141c"), C("#1c1a24"), C("#121018"), C("#201d2a")]
SHEEN = [C("#2c3a5c"), C("#34305a"), C("#283c58")]
BEAK = [C("#0c0b10"), C("#141218")]
RAVEN_EYE = C("#ffc93a")
TALON = C("#3a3638")


def feathers(face, x, y, w, h):
    """Plumas negras con reflejos azul violáceo (más arriba que abajo)."""
    if face == "bottom":
        return pick(BLACK), None
    return (pick(SHEEN) if random.random() < (0.22 if face == "top" else 0.1) else pick(BLACK)), None


def raven_head(face, x, y, w, h):
    if face in ("west", "east") and y == 1 and front_x_dist(face, x, w) == 1:
        return RAVEN_EYE, RAVEN_EYE
    return feathers(face, x, y, w, h)


def front_x_dist(face, x, w):
    return w - 1 - x if face == "west" else x


def beak(face, x, y, w, h):
    return pick(BEAK), None


def wing(face, x, y, w, h):
    """Ala: plumas largas con el borde de las primarias más claro."""
    if face in ("top", "bottom") and x in (0, w - 1):
        return pick(SHEEN), None
    if face in ("top", "bottom") and (y % 3 == 0):
        return C("#0e0c12"), None
    return feathers(face, x, y, w, h)


def talon(face, x, y, w, h):
    return TALON, None


def raven():
    m = Model()
    body = m.part("body", None, [0, 18, 0], [0, 0, 0], [cube(-2.5, -2.5, -4, 5, 5, 8, feathers)])
    head = m.part("head", None, [0, 16, -4], [0, 0, 0], [cube(-2, -3, -3.5, 4, 4, 4, raven_head),
                                                        cube(-1.5, 0, -3, 3, 1, 3, feathers)])
    m.part("beak", head, [0, -1, -3.5], [0.12, 0, 0], [cube(-1, -1, -3, 2, 2, 3, beak)])
    # Alas abiertas: nacen en el lomo y se extienden a los lados; se baten girando en z.
    m.part("wing_r", body, [-2.5, -2, -2], [0, 0, 0], [cube(-9, -0.5, 0, 9, 1, 6, wing)])
    m.part("wing_l", body, [2.5, -2, -2], [0, 0, 0], [cube(0, -0.5, 0, 9, 1, 6, wing)])
    m.part("tail", body, [0, -1, 4], [-0.25, 0, 0], [cube(-2.5, 0, 0, 5, 1, 5, wing)])
    m.part("leg_r", body, [-1.2, 2.5, 0], [0.6, 0, 0], [cube(-0.5, 0, 0, 1, 3, 1, talon)])
    m.part("leg_l", body, [1.2, 2.5, 0], [0.6, 0, 0], [cube(-0.5, 0, 0, 1, 3, 1, talon)])
    return m


# ---------------------------------------------------------------- Skíðblaðnir
ASH = [C("#e6cf9a"), C("#dcc38a"), C("#efd8a6"), C("#d6bb80")]
ASH_D = C("#a88a52")
TRIM = [C("#f5c842"), C("#f0bd35"), C("#ffd860")]
RUNE = C("#4fb8ff")
SAIL_W = [C("#f4f1e6"), C("#ebe6d6")]
SAIL_G = [C("#e8b83a"), C("#f0c040")]
MAST = [C("#8a5a30"), C("#7a4e28")]


def planks(face, x, y, w, h):
    """Tablas de fresno claro, con una veta oscura cada 4 px."""
    if y % 4 == 3 and face not in ("top", "bottom"):
        return ASH_D, None
    return pick(ASH), None


def hull_side(face, x, y, w, h):
    """Costado del casco: borde de oro arriba y una línea de runas azules que brillan."""
    if face in ("front", "back"):
        if y == 0:
            return pick(TRIM), None
        if y == 3 and x % 3 != 1:
            return RUNE, RUNE
    if face == "top":
        return pick(TRIM), None
    return planks(face, x, y, w, h)


def sail(face, x, y, w, h):
    """Vela a franjas verticales blancas y doradas, con el sol de Freyr en el centro."""
    if face in ("west", "east"):
        cx, cy = w / 2 - 0.5, h / 2 - 0.5
        if (x - cx) ** 2 + (y - cy) ** 2 <= 6.5:
            return C("#ffcf40"), C("#ffcf40")
        return (pick(SAIL_G) if (x // 3) % 2 else pick(SAIL_W)), None
    return pick(SAIL_W), None


def mast(face, x, y, w, h):
    return pick(MAST), None


def dragon_prow(face, x, y, w, h):
    if face in ("front", "back") and y == 2 and x in (1, w - 2):
        return C("#ff6a3a"), C("#ff6a3a")
    return (pick(TRIM) if random.random() < 0.7 else C("#c8901e")), None


def paddle(face, x, y, w, h):
    return pick(ASH), None


def skidbladnir():
    m = Model()
    # Casco: mismas partes y medidas que el bote vanilla (BoatModel), con la textura propia.
    m.part("bottom", None, [0, 3, 1], [math.pi / 2, 0, 0], [cube(-14, -9, -3, 28, 16, 3, planks)])
    m.part("back", None, [-15, 4, 4], [0, math.pi * 1.5, 0], [cube(-13, -7, -1, 18, 6, 2, hull_side)])
    m.part("front", None, [15, 4, 0], [0, math.pi / 2, 0], [cube(-8, -7, -1, 16, 6, 2, hull_side)])
    m.part("right", None, [0, 4, -9], [0, math.pi, 0], [cube(-14, -7, -1, 28, 6, 2, hull_side)])
    m.part("left", None, [0, 4, 9], [0, 0, 0], [cube(-14, -7, -1, 28, 6, 2, hull_side)])
    m.part("left_paddle", None, [3, -5, 9], [0, 0, math.pi / 16], [cube(-1, 0, -5, 2, 2, 18, paddle),
                                                                     cube(-1.001, -3, 8, 1, 6, 7, paddle)])
    m.part("right_paddle", None, [3, -5, -9], [0, math.pi, math.pi / 16], [cube(-1, 0, -5, 2, 2, 18, paddle),
                                                                             cube(0.001, -3, 8, 1, 6, 7, paddle)])
    # Lo que lo distingue: proa de dragón, mástil y vela.
    m.part("prow", None, [16, -2, 0], [0, 0, 0], [cube(-1, -9, -1.5, 3, 10, 3, dragon_prow),
                                                  cube(1, -11, -1.5, 5, 3, 3, dragon_prow)])
    # Mástil a popa, detrás del jinete (que va al centro mirando a la proa): así no tapa la vista.
    m.part("mast", None, [-10, 1, 0], [0, 0, 0], [cube(-1, -30, -1, 2, 30, 2, mast)])
    m.part("sail", "mast", [0, -27, 0], [0, 0, 0], [cube(-0.5, 0, -10, 1, 16, 20, sail)])
    return m


# ---------------------------------------------------------------- Halcón (manto de plumas de Freyja)
FALCON_BACK = [C("#5c4a3a"), C("#665240"), C("#544232"), C("#6e5a46")]
FALCON_BAR = C("#3a2c22")
FALCON_CHEST = [C("#efe6d6"), C("#e6dcc8"), C("#f4eee2")]
FALCON_STREAK = C("#5a4636")
FALCON_YELLOW = C("#f2c230")
FALCON_DARK = C("#2a2220")


def falcon_back(face, x, y, w, h):
    """Lomo pardo pizarra con barras oscuras; vientre claro con motas (el halcón peregrino)."""
    if face == "bottom" or (face in ("front",) and y >= 1):
        return (FALCON_STREAK if random.random() < 0.18 else pick(FALCON_CHEST)), None
    if face in ("west", "east") and y >= h - 2:
        return (FALCON_STREAK if random.random() < 0.18 else pick(FALCON_CHEST)), None
    return (FALCON_BAR if (x + 2 * y) % 5 == 0 else pick(FALCON_BACK)), None


def falcon_head(face, x, y, w, h):
    """Cabeza: capucha oscura, mejillas claras con la bigotera negra bajo el ojo, ojo oscuro con anillo amarillo."""
    if face in ("west", "east"):
        fx = front_x_dist(face, x, w)
        if y == 1 and fx == 1:
            return FALCON_DARK, None                         # ojo
        if y == 1 and fx == 0:
            return FALCON_YELLOW, None                       # anillo
        if y == 2 and fx == 1:
            return FALCON_DARK, None                         # bigotera
        if y >= 2:
            return pick(FALCON_CHEST), None
    if face == "front" and y >= 2:
        return pick(FALCON_CHEST), None
    return pick([C("#3a3230"), C("#443a36")]), None


def falcon_beak(face, x, y, w, h):
    return (FALCON_YELLOW if y == 0 and face != "front" else C("#2e3440")), None   # cera amarilla y pico gris azulado


def falcon_wing(face, x, y, w, h):
    """Alas largas y puntiagudas: pardas con barras; por debajo, claras con barras finas."""
    if face == "bottom":
        return (FALCON_STREAK if (x + y) % 3 == 0 else pick(FALCON_CHEST)), None
    if x >= w - 3:
        return FALCON_DARK, None                             # puntas oscuras de las primarias
    return (FALCON_BAR if y % 2 == 0 and x % 2 == 0 else pick(FALCON_BACK)), None


def falcon_talon(face, x, y, w, h):
    return FALCON_YELLOW, None


def falcon():
    """Halcón peregrino del tamaño de un jugador agachado (se dibuja a 1,6): cuerpo, cabeza con pico ganchudo, alas largas
    y puntiagudas que se baten, cola y patas amarillas recogidas."""
    m = Model()
    body = m.part("body", None, [0, 16, 0], [0, 0, 0], [cube(-2.5, -2.5, -4.5, 5, 5, 9, falcon_back)])
    head = m.part("head", None, [0, 14, -4.5], [0, 0, 0], [cube(-2, -3.5, -3.5, 4, 4, 4, falcon_head)])
    m.part("beak", head, [0, -1.5, -3.5], [0.35, 0, 0], [cube(-0.5, -0.5, -2, 1, 2, 2, falcon_beak)])
    m.part("wing_r", body, [-2.5, -2, -3], [0, 0, 0], [cube(-12, -0.5, 0, 12, 1, 7, falcon_wing)])
    m.part("wing_l", body, [2.5, -2, -3], [0, 0, 0], [cube(0, -0.5, 0, 12, 1, 7, falcon_wing)])
    m.part("tail", body, [0, -1.5, 4.5], [-0.15, 0, 0], [cube(-2, 0, 0, 4, 1, 6, falcon_wing)])
    m.part("leg_r", body, [-1.2, 2.5, 1], [0.9, 0, 0], [cube(-0.5, 0, -0.5, 1, 3, 1, falcon_talon)])
    m.part("leg_l", body, [1.2, 2.5, 1], [0.9, 0, 0], [cube(-0.5, 0, -0.5, 1, 3, 1, falcon_talon)])
    return m


MODELS = {"raven": (raven, 64, 31), "skidbladnir": (skidbladnir, 128, 32), "falcon": (falcon, 64, 33)}
SCALE = {"raven": 1.0, "skidbladnir": 1.0, "falcon": 1.6}


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
