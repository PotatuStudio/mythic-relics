"""Propuesta: la espada de Freyr, la que pelea sola contra los gigantes (Skírnismál, Gylfaginning 37) y que el mod hace
brillar como el sol.

Espada vikinga de época (tipología de Petersen): hoja recta de filos casi paralelos con el canal (fuller) que corre
por el centro y termina antes de la punta, acero claro con los filos en bandas apenas distintas; guarda inferior recta y
corta con los extremos apenas curvados hacia la hoja, puño de cuero con espiral de hilo de oro, guarda superior y
pomo escalonado de tres lóbulos, todo de oro con incrustaciones oscuras de niel. La punta mira a -y, como la de hoy."""
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from china_entities import C, Model, cube, pick  # noqa: E402

TEX_W = 32
SCALE = 0.8
ROOT_OFFSET = (0, 0, 0)
SEED = 41

STEEL_L = C("#f4f4f6")
STEEL = [C("#dcdde2"), C("#e6e7ec"), C("#d2d3da")]
STEEL_D = C("#9fa0aa")
FULLER = C("#ffe68a")
FULLER_D = C("#ffc93a")
GOLD = [C("#f5c842"), C("#ffd85a"), C("#f0bd35")]
GOLD_L = C("#fff3a8")
GOLD_D = C("#b07d1a")
NIELLO = C("#2a2420")
AMBER = C("#ff9f2a")
LEATHER = [C("#4a2a10"), C("#3f220b")]
WIRE = C("#e0aa2a")

def blade(face, x, y, w, h):
    """Hoja: filos claros en bandas suaves y el canal central dorado que brilla; el canal termina 3 px antes
    del final del tramo ancho (y = 0 es el lado de la punta)."""
    if face in ("front", "back"):
        if x == w // 2 and y >= 2:
            c = FULLER if y % 4 else FULLER_D
            return c, c
        return (STEEL_L if (y // 3) % 2 else C("#ececf0")), None
    if face in ("west", "east"):
        return STEEL_D, None                                   # el filo visto de canto
    return STEEL_L, None


def point(face, x, y, w, h):
    """Punta: acero claro; el canal se apaga (sin brillo) para que la punta lea limpia."""
    if face in ("west", "east"):
        return STEEL_D, None
    return (STEEL_L if x != w // 2 or w == 1 else pick(STEEL)), None


def gold_niello(face, x, y, w, h):
    """Oro con un dibujo de niel (rombos y líneas oscuras), como las empuñaduras de plata y niel de la época."""
    if face in ("top", "bottom"):
        return (GOLD_D if (x + y) % 2 else pick(GOLD)), None
    if face in ("front", "back") and y == h - 1 and x % 2 == 1:
        return NIELLO, None                                   # fila de rombos de niel
    if y == 0:
        return GOLD_L, None
    return pick(GOLD), None


def guard(face, x, y, w, h):
    """Guarda inferior: oro con niel y la gema de ámbar al centro, adelante y atrás (brilla)."""
    if face in ("front", "back") and x in (w // 2 - 1, w // 2):
        return AMBER, AMBER
    return gold_niello(face, x, y, w, h)


def guard_tip(face, x, y, w, h):
    return (GOLD_L if y == 0 else pick(GOLD)), None


def grip(face, x, y, w, h):
    """Puño: cuero oscuro con una espiral de hilo de oro (en diagonal, sigue al girar)."""
    if face in ("top", "bottom"):
        return pick(LEATHER), None
    k = {"front": 0, "east": 2, "back": 4, "west": 6}[face] if face in ("front", "east", "back", "west") else 0
    if (y - x + k) % 3 == 0:
        return WIRE, None
    return pick(LEATHER), None


def pommel(face, x, y, w, h):
    """Pomo: oro con las ranuras de niel entre los lóbulos y una gema de ámbar al centro, adelante."""
    if face == "front" and w == 4 and x in (1, 2):
        return AMBER, AMBER
    if face in ("front", "back", "west", "east") and x in (0, w - 1) and w >= 4:
        return NIELLO, None
    return (GOLD_L if face == "top" else pick(GOLD)), None


def build():
    m = Model()
    m.part("sword", None, [0, 0, 0], [0, 0, 0], [
        # Hoja: 16 px de filos casi paralelos + 4 px de punta = 20 px, como hoy (la vaina de la espalda mide lo mismo).
        cube(-1.5, -12, -0.5, 3, 16, 1, blade),                # y -12 .. 4
        cube(-1, -15, -0.5, 2, 3, 1, point),                   # y -15 .. -12, tramo que afina
        cube(-0.5, -16, -0.5, 1, 1, 1, point),                 # y -16 .. -15, la punta
    ])
    m.part("guard", "sword", [0, 4, 0], [0, 0, 0], [
        cube(-3, 0, -1, 6, 2, 2, guard),                       # guarda inferior recta (y 4 .. 6)
        cube(2.8, -0.4, -0.5, 1, 2, 1, guard_tip),             # extremos apenas levantados hacia la hoja
        cube(-3.8, -0.4, -0.5, 1, 2, 1, guard_tip),
    ])
    m.part("hilt", "sword", [0, 6, 0], [0, 0, 0], [
        cube(-1, 0, -1, 2, 4, 2, grip),                        # puño (y 6 .. 10)
        cube(-2.5, 4, -1, 5, 1, 2, gold_niello),               # guarda superior (10 .. 11)
        cube(-2, 5, -1, 4, 1, 2, pommel),                      # pomo, lóbulos de los lados (11 .. 12)
        cube(-1, 6, -1, 2, 1, 2, pommel),                      # pomo, lóbulo central (12 .. 13)
    ])
    return m


NOTES = """Hoy: 8 cubos, guarda con las puntas levantadas (más de fantasía) y pomo de caja. Propuesta: 10 cubos con proporción de espada vikinga.
Hoja de filos casi paralelos con el canal dorado que brilla y termina antes de la punta; filos claros en bandas suaves; punta que afina en tres tramos.
Guarda inferior recta y corta (extremos apenas curvados hacia la hoja) con la gema de ámbar; puño de cuero con espiral de hilo de oro.
Guarda superior y pomo escalonado de tres lóbulos (tipo «sombrero» de Petersen), de oro con niel oscuro.
Largo: igual que hoy, 29 px de punta a pomo (hoja + punta = 20 px, guarda 2, puño + pomo 7), así la vaina de la espalda (worn_models.freyr_sword, que mide 20·k) sigue calzando sin tocarla; solo cambia el dibujo de la empuñadura en la espalda (guarda recta y pomo de lóbulos).
Tamaño: escala 0,8 en vez de 0,7 (de ~1,27 a ~1,45 bloques). Para que envainada y desenvainada midan igual, FREYR_K en worn_models.py y FreyrSwordRenderer.SCALE pasan juntos a 0,8 (la vaina se recalcula sola); si la espalda queda muy cargada, se deja en 0,7 y la propuesta vale igual.
Fiel al mito: la espada de Freyr que pelea sola contra los gigantes y que él entrega a Skírnir (Skírnismál); forma de espada nórdica de época.
Licencia de juego: el brillo del canal (la espada «del sol»), la gema de ámbar y el oro macizo de la empuñadura.
Cultura pop: nada.
Animación propuesta: la de hoy (vuela con la punta adelante y gira sobre su hoja en el tajo); el canal podría latir más fuerte al golpear."""
