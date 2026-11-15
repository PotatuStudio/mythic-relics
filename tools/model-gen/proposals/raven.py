"""Propuesta: Huginn y Muninn, los cuervos de Odín (pensamiento y memoria).

Cuervo grande (Corvus corax) negro azabache con reflejos azul violáceo: pico grueso y largo con la punta en gancho,
frente alta, plumas erizadas y en punta en la garganta (lo que distingue al cuervo de la corneja), alas largas en dos
tramos con cinco primarias separadas como dedos, cola en cuña (las plumas del centro más largas) y patas negras
fuertes. Los ojos brillan (licencia: son los ojos con que Odín ve el mundo). Una sola geometría para los dos; los
distingue la anilla de la pata y el color del brillo (ver NOTES)."""
import math
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from china_entities import C, Model, cube, front_x, mix, pick  # noqa: E402

TEX_W = 64
SCALE = 0.8
ROOT_OFFSET = (0, 0, 0)
SEED = 31

BLACK = [C("#16141c"), C("#1c1a24"), C("#121018"), C("#201d2a")]
DEEP = C("#0b0a0f")
SHEEN = [C("#2c3a5c"), C("#34305a"), C("#283c58"), C("#3a3468")]
SHEEN_L = C("#5a6aa0")
BEAK = [C("#0c0b10"), C("#141218"), C("#100e14")]
BEAK_L = C("#3a3846")
EYE = C("#ffc93a")          # Huginn: oro (Muninn: celeste #8fe0ff)
EYE_D = C("#b07a10")
TALON = [C("#2a2628"), C("#322e30")]
CLAW = C("#0a090c")
RING = [C("#f5c842"), C("#e0b030")]
RUNE = C("#ffe27a")


def plumage(face, x, y, w, h):
    """Plumaje del cuerpo: negro con reflejos azul violáceo, más en el lomo y la nuca; hileras de plumas marcadas."""
    if face == "bottom":
        return pick(BLACK), None
    p = 0.3 if face == "top" else 0.12
    if (x + (y // 2) * 2) % 4 == 0 and y % 2 == 1:
        return DEEP, None
    return (pick(SHEEN) if random.random() < p else pick(BLACK)), None


def raven_head(face, x, y, w, h):
    """Cabeza: ojo de 2 px que brilla con el borde oscuro, frente con más brillo."""
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y == 1 and fx in (1, 2):
            return (EYE, EYE) if fx == 1 else (EYE_D, EYE_D)
        if y == 0 and fx == 1:
            return DEEP, None            # ceja
    if face == "top" and random.random() < 0.35:
        return pick(SHEEN), None
    return plumage(face, x, y, w, h)


def hackles(face, x, y, w, h):
    """Plumas erizadas de la garganta: lanceoladas y sueltas, el borde de abajo en dientes."""
    if face in ("west", "east", "front", "back") and y == h - 1 and x % 2 == 0:
        return None, None
    if face in ("west", "east", "front") and y == h - 2 and x % 4 == 1:
        return None, None
    return (SHEEN_L if (face != "bottom" and x % 2 == 1 and y == 0) else pick(BLACK)), None


def beak(face, x, y, w, h):
    """Pico negro y grueso, lustroso: el lomo (culmen) y la comisura con un brillo gris que lo separa de la cabeza."""
    if face == "top" and x == w // 2:
        return BEAK_L, None
    if face in ("west", "east") and y == h - 1:
        return C("#26242e"), None
    if face in ("west", "east") and y == 0:
        return C("#2e2c38"), None
    return pick(BEAK), None


def bristles(face, x, y, w, h):
    """Cerdas nasales: cubren la base del pico, hacia adelante."""
    if face in ("top", "bottom"):
        if y >= h - 1 and x % 2 == 0:
            return None, None
        return (pick(SHEEN) if (x + y) % 2 else pick(BLACK)), None
    return pick(BLACK), None


def coverts(face, x, y, w, h):
    """Cobijas del ala: hileras de plumas como escamas, con el borde azulado."""
    if face == "top":
        if y % 3 == 2:
            return DEEP, None
        if (x + y) % 3 == 0:
            return pick(SHEEN), None
        return pick(BLACK), None
    if face == "bottom":
        return (DEEP if y % 2 else pick(BLACK)), None
    return pick(BLACK), None


def remiges(face, x, y, w, h, tips=True):
    """Plano de rémiges: plumas de 2 px separadas por el raquis oscuro y con las puntas en dientes a los dos lados."""
    if face not in ("top", "bottom"):
        return pick(BLACK), None
    if tips and (y in (0, h - 1)) and x % 2 == 1:
        return None, None
    if x % 2 == 1:
        return DEEP, None
    return (pick(SHEEN) if random.random() < 0.3 else pick(BLACK)), None


def primary(face, x, y, w, h):
    """Primaria suelta («dedo»): raquis al medio, brillo en el borde y la punta afilada."""
    if face not in ("top", "bottom"):
        return pick(BLACK), None
    tip = min(x, w - 1 - x)          # distancia a cualquiera de los extremos (la base queda tapada)
    if tip == 0 and y == 1:
        return None, None
    if y == 0 and tip <= 1:
        return SHEEN_L, None
    return (pick(SHEEN) if random.random() < 0.2 else pick(BLACK)), None


def tail_feather(face, x, y, w, h):
    """Pluma de la cola: punta redonda (cuña en conjunto), raquis oscuro."""
    if face not in ("top", "bottom"):
        return pick(BLACK), None
    if y == 0 and x == 1:
        return None, None              # punta redonda (la fila 0 queda hacia +z)
    if face == "top" and random.random() < 0.3:
        return pick(SHEEN), None
    return pick(BLACK), None


def leg(face, x, y, w, h):
    """Tarso negro con escamas; la anilla de oro con la runa que brilla (Huginn)."""
    if y == 1:
        if face in ("front", "back"):
            return RUNE, RUNE
        return pick(RING), None
    return pick(TALON), None


def toes(face, x, y, w, h):
    """Dedos: tres adelante y uno atrás, uñas negras en la punta."""
    if face not in ("top", "bottom"):
        return pick(TALON), None
    if y == 0:
        return CLAW, None
    if y == h - 1:
        return (CLAW, None) if x == 1 else (None, None)
    return pick(TALON), None


def build():
    m = Model()
    body = m.part("body", None, [0, 17, 0], [0, 0, 0], [
        cube(-2.5, -2.5, -4.5, 5, 5, 9, plumage),          # tronco
        cube(-2, -1, -5.5, 4, 4, 2, plumage),               # pecho abombado
        cube(-2, -2, 4, 4, 4, 3, plumage),                   # rabadilla que afina hacia la cola
    ])
    head = m.part("head", None, [0, 15, -5.5], [0, 0, 0], [
        cube(-2, -3.5, -4, 4, 4, 4, raven_head),             # cráneo de frente alta
        cube(-1, -1.2, -1.2, 2, 3, 3, plumage),               # cuello
        cube(-1.5, -3.2, -0.5, 3, 3, 1, plumage),            # nuca redonda
        cube(-1.5, 0, -3.7, 3, 2, 3, hackles),               # plumas erizadas de la garganta
    ])
    bk = m.part("beak", head, [0, -1.5, -4], [0.1, 0, 0], [
        cube(-1.5, -1, -2.5, 3, 2, 3, beak),                 # base ancha
        cube(-1, -0.8, -4.3, 2, 2, 2, beak),                 # tramo medio
        cube(-0.5, -0.6, -5.2, 1, 2, 1, beak),               # punta en gancho (baja más)
    ])
    m.part("bristles", bk, [0, -1.25, -1], [-0.15, 0, 0], [cube(-1.5, 0, -2, 3, 0, 3, bristles)])

    # Alas en dos tramos: brazo (cobijas + secundarias) y mano (cobijas + interiores + cinco primarias en abanico).
    for side, s in (("r", -1), ("l", 1)):
        def X(x0, w):
            return x0 if s > 0 else -x0 - w
        wing = m.part(f"wing_{side}", body, [2.3 * s, -2, -3], [0, 0, -0.12 * s], [
            cube(X(0, 7), -0.5, 0, 7, 1, 5, coverts),
            cube(X(0, 7), 0, 4, 7, 0, 5, remiges),
        ])
        tip = m.part(f"wing_{side}_tip", wing, [7 * s, 0, 0], [0, -0.18 * s, 0], [
            cube(X(0, 4), -0.5, 0, 4, 1, 4, coverts),
            cube(X(0, 4), 0.12, 3, 4, 0, 5, remiges),
        ])
        for i, (length, yaw) in enumerate(zip((6, 8, 8, 7, 6), (0.0, 0.14, 0.28, 0.42, 0.56))):
            m.part(f"wing_{side}_p{i}", tip, [3 * s, -0.3 + 0.12 * i, 0.7 + 0.75 * i], [0, -yaw * s, 0],
                   [cube(X(0, length), 0, -1, length, 0, 2, primary)])

    # Cola en cuña: cobijas y cinco plumas en abanico, las del centro más largas.
    tail = m.part("tail", body, [0, -1.5, 6.5], [-0.15, 0, 0], [cube(-1.5, -0.5, -0.5, 3, 1, 3, coverts)])
    for i, (length, yaw) in enumerate(zip((6, 7, 9, 7, 6), (-0.36, -0.18, 0.0, 0.18, 0.36))):
        m.part(f"tail_f{i}", tail, [(i - 2) * 0.5, -0.4 + 0.2 * i, 0.5], [0, yaw, 0],
               [cube(-1, 0, 0, 2, 0, length, tail_feather)])

    # Patas recogidas en vuelo: calzón de plumas, tarso con la anilla y los dedos con uñas.
    for side, s in (("r", -1), ("l", 1)):
        m.part(f"leg_{side}", body, [1.2 * s, 2.5, 0.5], [0.6, 0, 0], [
            cube(-1, -1, -1, 2, 2, 2, plumage),
            cube(-0.5, 0.8, -0.5, 1, 3, 1, leg),
            cube(-1, 3.7, -2, 2, 0, 3, toes),
        ])
    return m


NOTES = """Hoy: 9 cubos, alas y cola de una sola placa. Propuesta: 41 cubos dibujados a 0,8: algo más grande (largo de 19 a 25 px, envergadura de 23 a 32 px).
Cuerpo en tres volúmenes (pecho abombado, tronco y rabadilla), cuello y cráneo de frente alta.
Pico de cuervo grueso en tres tramos que afinan, con la punta en gancho y las cerdas nasales cubriendo la base.
Plumas erizadas de la garganta con el borde en dientes: el rasgo que separa al cuervo de la corneja.
Alas en dos tramos (brazo y mano) con secundarias de borde dentado y cinco primarias sueltas como dedos.
Cola en cuña de cinco plumas (las del centro más largas, como la del cuervo grande); patas con calzón, dedos y uñas.
Huginn y Muninn: misma geometría; Huginn (pensamiento) con ojos y runa ᚺ dorados en la anilla, Muninn (memoria) con ojos y anilla celeste plata (otra textura).
Fiel al mito: dos cuervos negros de Odín (Grímnismál), que vuelan sobre el mundo y vuelven a contarle lo que ven.
Licencia de juego: los ojos que brillan, la anilla rúnica y el reflejo azul exagerado.
Cultura pop: nada.
Animación propuesta: alas que se baten con la mano un poco atrasada (latigazo) y las primarias que se abren al bajar; posado, la mano se pliega sobre el brazo y la cola se cierra; la cabeza se ladea de a saltos."""
