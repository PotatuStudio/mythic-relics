"""Borrador de las entidades de la tanda 3 griega, con la misma geometría que tendrán en el juego (partes de ModelPart
con UV de caja; la y crece hacia abajo, el frente es -z y el suelo está en y = 24):

- Pegaso (Brida de oro de Atenea): caballo blanco con grandes alas de plumas que nacen en la cruz, crin y cola
  plateadas, y la brida de oro de Atenea en la cabeza (brilla un poco: es un regalo divino). Una manta roja con greca de
  oro hace de silla; Belerofonte lo montaba a pelo, la manta es licencia para que se vea montable.
- Talos (Clavo de bronce de Talos): hombre de bronce de unos 3 bloques, como en la vasija de Ruvo (c. 400 a. C.): pelo
  y barba de rizos, cuerpo de estatua con pátina verde en las partes bajas, ojos de horno que brillan, la única vena de
  ícor dorado que baja del cuello al tobillo derecho (brilla) y el clavo de bronce en el tobillo. Lleva una roca en la
  mano derecha para lanzar. El faldellín de bronce es licencia (en la vasija está desnudo).

Usa las piezas de china_entities.py; cada modelo pinta con su propia semilla.
Uso: python greece_entities.py <salida.json>"""
import json
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import china_entities as CE  # noqa: E402
from china_entities import C, Model, cube, pick, front_x  # noqa: E402

# ---------------------------------------------------------------- Pegaso
COAT = [C("#f7f6f2"), C("#f2f0ea"), C("#fbfaf7"), C("#eeebe4")]
COAT_D = C("#dedad1")
SILVER = [C("#e4e8ef"), C("#d2d8e2"), C("#f2f4f8")]
FEATHER = [C("#ffffff"), C("#f4f6fa"), C("#e8edf4")]
FEATHER_EDGE = C("#c5cfdc")
HOOF = C("#6b6660")
GOLD = [C("#f5c842"), C("#ffe27a")]
GOLD_D = C("#c98f1c")
RED = [C("#b8322a"), C("#a82a22")]


def coat(face, x, y, w, h):
    if face == "bottom":
        return COAT_D, None
    return (COAT_D if random.random() < 0.02 else pick(COAT)), None


def leg(face, x, y, w, h):
    """Pata blanca con el casco gris en los dos píxeles de abajo."""
    if face == "bottom" or (face not in ("top",) and y >= h - 2):
        return HOOF, None
    return coat(face, x, y, w, h)


def head(face, x, y, w, h):
    """Cabeza: ojos oscuros a los lados (caballo: los ojos miran a los costados)."""
    if face in ("west", "east") and y == 1 and front_x(face, x, w) == 1:
        return C("#2a2430"), None
    return coat(face, x, y, w, h)


def muzzle(face, x, y, w, h):
    if face == "front" and y == h - 2 and x in (0, w - 1):
        return C("#5a5450"), None                       # ollares
    return (C("#e8e2d8") if y >= h - 2 else pick(COAT)), None


def mane(face, x, y, w, h):
    return pick(SILVER), None


def feathers(face, x, y, w, h):
    """Plumas largas: franjas blancas a lo largo del ala, separadas por una línea gris azulada muy suave, y la punta
    de cada pluma marcada en el borde de atrás."""
    if face in ("top", "bottom"):
        if y >= h - 1 and x % 3 != 1:
            return FEATHER_EDGE, None
        return (C("#e3e9f1") if x % 3 == 2 else pick(FEATHER)), None
    return pick(FEATHER), None


def bridle(face, x, y, w, h):
    """La brida de oro de Atenea: brilla un poco."""
    col = GOLD_D if (x + y) % 3 == 0 else pick(GOLD)
    return col, col


def blanket(face, x, y, w, h):
    """Manta roja con greca de oro en el borde de abajo."""
    if face in ("west", "east", "front", "back") and y >= h - 2:
        meander = (x % 4 in (0, 1)) if y == h - 2 else (x % 4 in (1, 2))
        return (GOLD[0] if meander else GOLD_D), None
    return pick(RED), None


def pegasus():
    m = Model()
    body = m.part("body", None, [0, 9, 0], [0, 0, 0], [cube(-5, -5, -11, 10, 10, 22, coat)])
    m.part("blanket", body, [0, 0, 0], [0, 0, 0], [cube(-5.5, -5.5, -4, 11, 5, 8, blanket)])
    for name, x, z in (("leg_fr", -3, -8), ("leg_fl", 3, -8), ("leg_br", -3, 8), ("leg_bl", 3, 8)):
        m.part(name, None, [x, 14, z], [0, 0, 0], [cube(-2, 0, -2, 4, 10, 4, leg)])
    neck = m.part("neck", body, [0, -3, -9], [0.5, 0, 0], [cube(-2, -12, -3, 4, 13, 6, coat),
                                                          cube(-1, -13, 3, 2, 14, 2, mane)])
    hd = m.part("head", neck, [0, -12, -1], [0.2, 0, 0], [cube(-3, -4, -5, 6, 6, 7, head),
                                                       cube(-2, -3, -10, 4, 5, 5, muzzle),
                                                       cube(-2.5, -7, 0, 2, 3, 1, coat),
                                                       cube(0.5, -7, 0, 2, 3, 1, coat)])
    m.part("bridle", hd, [0, 0, 0], [0, 0, 0], [cube(-2.5, -3.5, -8, 5, 6, 1, bridle),
                                                cube(-3.5, -4.5, -2, 7, 7, 1, bridle),
                                                cube(-3, 0, -9, 1, 1, 1, bridle),
                                                cube(2, 0, -9, 1, 1, 1, bridle)])
    # Alas: nacen en la cruz, cobertoras y luego las plumas largas (primarias), levantadas como en las monedas de Corinto.
    wr = m.part("wing_r", body, [-5, -4, -6], [0, 0.2, 0.6], [cube(-10, -1, 0, 10, 2, 9, feathers)])
    m.part("wing_r2", wr, [-10, 0, 0], [0, 0.1, 0.3], [cube(-12, -0.5, 0, 12, 1, 12, feathers)])
    wl = m.part("wing_l", body, [5, -4, -6], [0, -0.2, -0.6], [cube(0, -1, 0, 10, 2, 9, feathers)])
    m.part("wing_l2", wl, [10, 0, 0], [0, -0.1, -0.3], [cube(0, -0.5, 0, 12, 1, 12, feathers)])
    tail = m.part("tail", body, [0, -4, 11], [0.6, 0, 0], [cube(-1.5, 0, -1.5, 3, 10, 3, mane)])
    m.part("tail2", tail, [0, 10, 0], [0.3, 0, 0], [cube(-1.5, 0, -1.5, 3, 7, 3, mane)])
    return m


# ---------------------------------------------------------------- Talos
BRONZE = [C("#c08a3e"), C("#b47e34"), C("#cc9648"), C("#a8742e")]
BRONZE_L = C("#e6b464")
BRONZE_D = C("#7a4e1e")
PATINA = [C("#4f9a7c"), C("#5fae8c")]
ICHOR = C("#ffd24a")
FURNACE = C("#ff8a2a")


def bronze(face, x, y, w, h, patina_from=None):
    if face == "top":
        return BRONZE_L, None
    if face == "bottom":
        return BRONZE_D, None
    if patina_from is not None and y >= patina_from and random.random() < 0.3:
        return pick(PATINA), None
    return (BRONZE_L if random.random() < 0.08 else pick(BRONZE)), None


def talos_head(face, x, y, w, h):
    """Cara: ojos de horno que brillan y cejas marcadas, como un bronce griego."""
    if face == "front":
        if y == 3 and x in (2, w - 3):
            return FURNACE, FURNACE
        if y == 2 and x in (1, 2, w - 3, w - 2):
            return BRONZE_D, None
        if y == 5 and 3 <= x <= w - 4:
            return BRONZE_D, None                     # boca
    return bronze(face, x, y, w, h)


def curls(face, x, y, w, h):
    """Pelo y barba de rizos: bronce más oscuro en damero."""
    return (BRONZE_D if (x + y) % 2 else pick(BRONZE)), None


def torso(face, x, y, w, h):
    """Torso con pectorales y abdomen marcados; la vena de ícor baja por el lado derecho del frente (brilla)."""
    if face == "front":
        if x == 3:
            return ICHOR, ICHOR
        if y == 4 and 1 <= x <= w - 2 and x != w // 2:
            return BRONZE_D, None                     # línea de los pectorales
        if x == w // 2 and y >= 5:
            return BRONZE_D, None                     # línea alba
    return bronze(face, x, y, w, h)


def leg_r(face, x, y, w, h):
    """Pierna derecha: la vena sigue por el frente hasta el tobillo."""
    if face == "front" and x == 1 and y < h - 2:
        return ICHOR, ICHOR
    return bronze(face, x, y, w, h, patina_from=h - 5)


def leg_l(face, x, y, w, h):
    return bronze(face, x, y, w, h, patina_from=h - 5)


def arm(face, x, y, w, h):
    return bronze(face, x, y, w, h, patina_from=h - 3)


def kilt(face, x, y, w, h):
    """Faldellín de láminas de bronce."""
    if face in ("front", "back", "west", "east") and x % 2 == 0:
        return BRONZE_D, None
    return pick(BRONZE), None


def nail(face, x, y, w, h):
    """El clavo de bronce del tobillo, con ícor que brilla alrededor."""
    return ICHOR, ICHOR


def rock(face, x, y, w, h):
    return pick([C("#8a8a8a"), C("#7a7a7a"), C("#9a9a9a"), C("#6c6c6c")]), None


def talos():
    m = Model()
    body = m.part("body", None, [0, 0, 0], [0, 0, 0], [cube(-6, 0, -3, 12, 12, 6, torso),
                                                      cube(-6.5, 9, -3.5, 13, 4, 7, kilt)])
    hd = m.part("head", None, [0, 0, 0], [0, 0, 0], [cube(-4, -8, -4, 8, 8, 8, talos_head),
                                                    cube(-4.5, -8.5, -4.5, 9, 3, 9, curls),
                                                    cube(-3, -2, -5, 6, 4, 2, curls)])
    ar = m.part("arm_r", None, [-6, 1, 0], [0, 0, 0], [cube(-4, -1, -2.5, 4, 14, 5, arm)])
    m.part("rock", ar, [-2, 13, -1], [0, 0, 0], [cube(-3, -1, -4, 6, 5, 6, rock)])
    m.part("arm_l", None, [6, 1, 0], [0, 0, 0], [cube(0, -1, -2.5, 4, 14, 5, arm)])
    lr = m.part("leg_r", None, [-3, 12, 0], [0, 0, 0], [cube(-2.5, 0, -2.5, 5, 12, 5, leg_r)])
    m.part("nail", lr, [0, 0, 0], [0, 0, 0], [cube(-4, 9, -0.5, 2, 1, 1, nail)])
    m.part("leg_l", None, [3, 12, 0], [0, 0, 0], [cube(-2.5, 0, -2.5, 5, 12, 5, leg_l)])
    return m


MODELS = {"pegasus": (pegasus, 128, 31), "talos": (talos, 64, 32)}
SCALE = {"pegasus": 1.0, "talos": 1.5}


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
