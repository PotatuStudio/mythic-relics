"""Propuesta: el halcón del manto de plumas de Freyja (fjaðrhamr / valshamr), la forma que toma el jugador.

Halcón peregrino: cabeza con capucha oscura, bigotera negra bajo el ojo y mejilla clara, anillo del ojo y cera
amarillos, pico corto y ganchudo; lomo pardo pizarra con barras (del mismo tono que el manto que se lleva en la
espalda), pecho claro moteado y vientre y calzones barrados; alas largas y puntiagudas en tres tramos, cola barrada con
la banda oscura y la punta clara, patas amarillas recogidas. De Freyja: el borde de ataque de las alas y la punta de
la cola con un brillo dorado, y Brísingamen al cuello."""
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from china_entities import C, Model, cube, front_x, pick  # noqa: E402

TEX_W = 64
SCALE = 1.3
ROOT_OFFSET = (0, 0, 0)
SEED = 33

BACK = [C("#5c4a3c"), C("#665244"), C("#544336"), C("#6a5848")]
BACK_EDGE = C("#8a735c")
BAR = C("#352a22")
CREAM = [C("#efe6d6"), C("#e8dcc6"), C("#f4ede0")]
BUFF = [C("#e6d2b0"), C("#dcc6a0")]
STREAK = C("#4e3e32")
HOOD = [C("#2e2826"), C("#36302c"), C("#2a2422")]
YELLOW = C("#f2c230")
YELLOW_D = C("#c89418")
DARK = C("#1c1716")
BEAK = [C("#3a4250"), C("#2e3440")]
PRIM = [C("#3a2e26"), C("#43362c"), C("#33281f")]
GOLD = C("#ffd35a")
GOLD_D = C("#e0a52a")
AMBER = C("#ff9f2a")
CLAW = C("#141010")


def barred_cream(x, y, step=2, off=0):
    return (STREAK if (y + off) % step == 0 and (x + y) % 3 != 2 else pick(CREAM)), None


def body_paint(face, x, y, w, h):
    """Lomo pardo pizarra con barras en V; flancos y vientre claros con barras finas."""
    if face == "bottom":
        return barred_cream(x, y)
    if face in ("west", "east"):
        if y >= h - 2:
            return barred_cream(x, y, off=1)
        if y == h - 3:
            return BACK_EDGE, None
    if face == "front":
        return (pick(CREAM), None) if y < 2 else barred_cream(x, y, 3)
    if (y + abs(x - (w - 1) / 2) // 1) % 3 == 0:
        return BAR, None
    return (BACK_EDGE if random.random() < 0.08 else pick(BACK)), None


def breast(face, x, y, w, h):
    """Pecho: blanco crema arriba, con gotas oscuras más abajo."""
    if face in ("top",):
        return pick(BACK), None
    if y >= 1 and (x * 3 + y * 2) % 5 == 0:
        return STREAK, None
    return pick(CREAM), None


def head_paint(face, x, y, w, h):
    """Capucha oscura, ojo grande con anillo amarillo, bigotera negra y mejilla clara detrás."""
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y == 1 and fx == 1:
            return DARK, None                                # ojo grande y oscuro
        if y == 1 and fx in (0, 2):
            return YELLOW, None                              # anillo del ojo, delante y detrás
        if y >= 2 and fx == 1:
            return DARK, None                                # bigotera
        if y >= 2 and fx >= 2:
            return pick(CREAM), None                         # mejilla clara
        if y >= 2:
            return pick(HOOD), None
        return pick(HOOD), None
    if face == "front":
        return (pick(CREAM), None) if y >= 2 else (pick(HOOD), None)
    if face == "bottom":
        return pick(CREAM), None
    return pick(HOOD), None


def hood(face, x, y, w, h):
    return pick(HOOD), None


def neck_paint(face, x, y, w, h):
    if face in ("front", "bottom"):
        return pick(CREAM), None
    return pick(HOOD), None


def beak_paint(face, x, y, w, h):
    """Cera amarilla atrás, pico gris azulado con la punta oscura."""
    if face in ("west", "east") and front_x(face, x, w) >= w - 1:
        return YELLOW, None
    if face == "top" and y >= h - 1:
        return YELLOW, None
    return pick(BEAK), None


def beak_tip(face, x, y, w, h):
    return (DARK if y >= h - 1 else pick(BEAK)), None


def coverts(face, x, y, w, h):
    """Cobijas: pardo pizarra con barras y el borde claro de cada pluma; por debajo, claras y barradas. El borde de
    ataque (cara delantera) es una línea dorada que brilla: el manto de la diosa."""
    if face == "front":
        return (GOLD, GOLD) if x % 2 == 0 else (GOLD_D, None)
    if face == "bottom":
        return barred_cream(x, y)
    if face == "top":
        if y % 3 == 2:
            return BACK_EDGE, None
        return (BAR if (x + y) % 4 == 0 else pick(BACK)), None
    return pick(BACK), None


def secondaries(face, x, y, w, h):
    """Plano de secundarias: plumas de 2 px, barradas, con la punta clara en dientes."""
    if face not in ("top", "bottom"):
        return pick(BACK), None
    if y in (0, h - 1) and x % 2 == 1:
        return None, None
    if y in (0, h - 1):
        return pick(CREAM), None
    return (BAR if y % 2 == 0 else pick(BACK)), None


def primary(face, x, y, w, h):
    """Primaria larga y afilada: oscura con barras tenues; la punta en una sola columna."""
    if face not in ("top", "bottom"):
        return pick(PRIM), None
    tip = min(x, w - 1 - x)
    if tip == 0 and y == 1:
        return None, None
    return ((BAR, None) if x % 3 == 0 and tip > 1 else (pick(PRIM), None))


def tail_feather(face, x, y, w, h):
    """Pluma de la cola (la punta es la fila 0, hacia +z): barras oscuras, banda negra cerca de la punta y la punta
    dorada que brilla. Igual por las dos caras (es un plano)."""
    if face not in ("top", "bottom"):
        return pick(BACK), None
    if y == 0:
        return GOLD, GOLD
    if y == 1:
        return DARK, None
    return (BAR if y % 2 == 0 else pick(BACK)), None


def tail_coverts(face, x, y, w, h):
    if face == "bottom":
        return barred_cream(x, y)
    return (BAR if (x + y) % 3 == 0 else pick(BACK)), None


def trousers(face, x, y, w, h):
    """Calzones de plumas: crema con barras."""
    if face == "top":
        return pick(BACK), None
    return barred_cream(x, y)


def talon(face, x, y, w, h):
    return (YELLOW if (x + y) % 3 else YELLOW_D), None


def toes(face, x, y, w, h):
    if face not in ("top", "bottom"):
        return YELLOW, None
    if y == 0:
        return CLAW, None
    if y == h - 1:
        return (CLAW, None) if x == 1 else (None, None)
    return (YELLOW if (x + y) % 2 else YELLOW_D), None


def brisingamen(face, x, y, w, h):
    """Brísingamen: collar de oro con cuentas de ámbar que brillan (adelante)."""
    if face == "front" and x % 2 == 1:
        return AMBER, AMBER
    if face in ("top", "bottom"):
        return GOLD_D, None
    return (GOLD if (x + y) % 2 else GOLD_D), (GOLD if face == "front" else None)


def build():
    m = Model()
    body = m.part("body", None, [0, 16, 0], [0, 0, 0], [
        cube(-2.5, -2.5, -4, 5, 5, 8, body_paint),           # tronco
        cube(-2, -0.5, -5, 4, 4, 2, breast),                   # pecho ancho
        cube(-2, -2, 3.8, 4, 3, 2, body_paint),              # rabadilla
    ])
    head = m.part("head", None, [0, 13.5, -5], [0, 0, 0], [
        cube(-2, -3.5, -3.5, 4, 4, 4, head_paint),            # cabeza redonda con capucha
        cube(-1.5, -1.2, -1, 3, 3, 3, neck_paint),            # cuello
        cube(-1.5, -3.8, -3.2, 3, 1, 3, hood),                # coronilla
        cube(-2, 0.6, -1.6, 4, 1, 4, brisingamen),            # Brísingamen
    ])
    bk = m.part("beak", head, [0, -1.5, -3.5], [0.35, 0, 0], [cube(-1, -0.8, -1.6, 2, 2, 2, beak_paint)])
    m.part("beak_tip", bk, [0, -0.6, -1.6], [0.5, 0, 0], [cube(-0.5, 0, -1, 1, 2, 1, beak_tip)])

    # Alas largas y puntiagudas en tres tramos: brazo, antebrazo y mano con cuatro primarias juntas en punta.
    for side, s in (("r", -1), ("l", 1)):
        def X(x0, w):
            return x0 if s > 0 else -x0 - w
        wing = m.part(f"wing_{side}", body, [2.3 * s, -2, -3], [0, 0, -0.06 * s], [
            cube(X(0, 6), -0.5, 0, 6, 1, 6, coverts),
            cube(X(0, 6), 0, 5, 6, 0, 3, secondaries),
        ])
        mid = m.part(f"wing_{side}_mid", wing, [6 * s, 0, 0], [0, -0.12 * s, 0], [
            cube(X(0, 5), -0.5, 0, 5, 1, 5, coverts),
            cube(X(0, 5), 0.12, 4, 5, 0, 3, secondaries),
        ])
        tip = m.part(f"wing_{side}_tip", mid, [5 * s, 0, 0], [0, -0.2 * s, 0], [cube(X(0, 3), -0.4, 0, 3, 1, 4, coverts)])
        for i, (length, yaw) in enumerate(zip((9, 8, 7, 6), (0.0, 0.1, 0.2, 0.32))):
            m.part(f"wing_{side}_p{i}", tip, [2 * s, -0.3 + 0.2 * i, 0.8 + 0.9 * i], [0, -yaw * s, 0],
                   [cube(X(0, length), 0, -1, length, 0, 2, primary)])

    # Cola barrada, casi cerrada, con la punta dorada.
    tail = m.part("tail", body, [0, -1.5, 5.5], [-0.12, 0, 0], [cube(-1.5, -0.5, -0.5, 3, 1, 3, tail_coverts)])
    for i, (length, yaw) in enumerate(zip((6, 7, 7, 7, 6), (-0.2, -0.1, 0.0, 0.1, 0.2))):
        m.part(f"tail_f{i}", tail, [(i - 2) * 0.5, -0.4 + 0.2 * i, 0.5], [0, yaw, 0],
               [cube(-1, 0, 0, 2, 0, length, tail_feather)])

    # Patas recogidas: calzones barrados, tarso y dedos amarillos con uñas negras.
    for side, s in (("r", -1), ("l", 1)):
        m.part(f"leg_{side}", body, [1.2 * s, 2.5, 1], [0.9, 0, 0], [
            cube(-1, -1, -1, 2, 2, 2, trousers),
            cube(-0.5, 0.8, -0.5, 1, 3, 1, talon),
            cube(-1, 3.7, -2, 2, 0, 3, toes),
        ])
    return m


NOTES = """Hoy: 8 cubos, alas de una sola placa y cola recta. Propuesta: 39 cubos dibujados a 1,3 en vez de 1,6: el mismo largo (31 px), pero alas más largas y finas (envergadura de 46 a 62 px).
Alas en tres tramos (brazo, antebrazo y mano) con secundarias de borde dentado y cuatro primarias juntas que forman la punta afilada del halcón.
Cabeza de peregrino: capucha oscura con coronilla, ojo grande con anillo amarillo, bigotera negra y mejilla clara; pico corto ganchudo con la cera amarilla.
Pecho ancho crema con gotas, flancos, vientre y calzones barrados; lomo pardo pizarra del mismo tono que el manto de la espalda.
Cola de cinco plumas barradas con la banda oscura; patas amarillas recogidas con uñas negras.
Fiel al mito: Freyja tiene un manto de plumas de halcón que presta a Loki para volar (Þrymskviða, Skáldskaparmál); el halcón es un peregrino.
Licencia de juego: el borde de las alas y la punta de la cola con brillo dorado (la diosa dentro del halcón) y Brísingamen al cuello como collar de oro con cuentas de ámbar.
Cultura pop: nada.
Animación propuesta: aleteo rápido con el antebrazo y la mano en latigazo, planeo con las alas en arco y las primarias apenas abiertas; en picada, alas plegadas en punta pegadas al cuerpo y la cola cerrada."""
