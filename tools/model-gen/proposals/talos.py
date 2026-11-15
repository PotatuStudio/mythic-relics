"""Propuesta: Talos, el gigante de bronce de Creta hecho por Hefesto (Apolonio de Rodas, Argonáuticas IV; Apolodoro,
Biblioteca I.9.26; la crátera de Ruvo, c. 400 a. C.).

Un hombre de bronce de unos 5 bloques, con cuerpo de estatua: placas remachadas, pectorales y abdominales en relieve,
rodillas y codos marcados, rizos de pelo y barba como un bronce griego, ojos y boca de horno, la única vena de ícor que
baja del cuello al tobillo derecho (brilla) y el clavo de bronce que la cierra en el tobillo. Pátina verde en lo bajo.
Mantiene las partes que ya se animan (head, body, arm_r con rock, arm_l, leg_r con nail, leg_l)."""
import random

from china_entities import C, Model, cube, pick, mix

TEX_W = 128
SCALE = 2.2
ROOT_OFFSET = (0, 0, 0)
SEED = 32

BRONZE = [C("#c08a3e"), C("#b47e34"), C("#cc9648"), C("#a8742e")]
BRONZE_L = C("#ecc070")
BRONZE_H = C("#f6d68e")
BRONZE_D = C("#7a4e1e")
BRONZE_DD = C("#5a3814")
BRONZE_MID_L = C("#d39c4c")
BRONZE_MID_D = C("#a06c2c")
PATINA = [C("#4f9a7c"), C("#5fae8c"), C("#3f8a6c")]
ICHOR = C("#ffd24a")
ICHOR_HOT = C("#fff2a0")
FURNACE = C("#ff8a2a")
FURNACE_HOT = C("#ffd23a")
ROCK = [C("#8a8a8a"), C("#7a7a7a"), C("#9a9a9a"), C("#6c6c6c")]
ROCK_D = C("#55524e")
ROCK_MOSS = C("#6f7f52")

SIDES = ("front", "back", "west", "east")


def metal(face, x, y, w, h, patina=None):
    """Bronce pulido: cara de arriba clara, la de abajo oscura, bordes algo más oscuros (se ve redondeado), un brillo
    vertical en el tercio izquierdo y pátina verde que sube desde abajo (patina = fracción de alto donde empieza)."""
    if face == "top":
        return (BRONZE_H if random.random() < 0.25 else BRONZE_L), None
    if face == "bottom":
        if patina is not None and random.random() < 0.6:
            return pick(PATINA), None
        return BRONZE_D, None
    if patina is not None and h > 1:
        t = y / (h - 1)
        if t >= patina and random.random() < 0.25 + 0.6 * (t - patina) / max(0.01, 1 - patina):
            return pick(PATINA), None
    # bronce liso: un degradé suave de arriba (claro) a abajo (oscuro) con muy poco ruido
    t = y / max(1, h - 1)
    base = mix(BRONZE_MID_L, BRONZE_MID_D, t)
    if random.random() < 0.12:
        base = mix(base, BRONZE_D, 0.25)
    if w > 2 and (x == 0 or x == w - 1):
        return mix(base, BRONZE_D, 0.4), None
    if w > 3 and x == max(1, w // 3):
        return mix(base, BRONZE_H, 0.5), None
    return base, None


def vein():
    """Un píxel de la vena de ícor que brilla (a ratos más caliente)."""
    return ICHOR if random.random() < 0.8 else ICHOR_HOT


def with_vein(base, col):
    def f(face, x, y, w, h):
        if face == "front" and x == col:
            g = vein()
            return g, g
        return base(face, x, y, w, h)
    return f


def chest(face, x, y, w, h):
    """Pecho de placas: costura con remaches abajo y a los lados; la columna del esternón oscura."""
    if face in SIDES:
        if y == h - 1:
            return (BRONZE_L if x % 2 == 0 else BRONZE_DD), None        # costura con remaches
        if face in ("west", "east") and x == w // 2:
            return (BRONZE_L if y % 2 == 0 else BRONZE_DD), None
        if face == "back" and x == w // 2:
            return BRONZE_D, None                                       # espina
        if face == "back" and y in (1, 2) and x in (2, 3, w - 3, w - 4):
            return BRONZE_D, None                                       # omóplatos
        if face == "front" and x == w // 2:
            return BRONZE_D, None
    return metal(face, x, y, w, h)


def pec(face, x, y, w, h):
    """Pectoral en relieve: borde de arriba brillante y sombra abajo."""
    if face == "front":
        if y == 0:
            return BRONZE_H, None
        if y == h - 1:
            return BRONZE_D, None
    return metal(face, x, y, w, h)


def abs_(face, x, y, w, h):
    """Abdomen: seis placas separadas por líneas oscuras."""
    if face == "front":
        if x == w // 2 or y == 1 or (y == 3 and 0 < x < w - 1):
            return mix(BRONZE_MID_D, BRONZE_D, 0.5), None
    if face == "back" and x == w // 2:
        return BRONZE_D, None
    return metal(face, x, y, w, h)


def belt(face, x, y, w, h):
    """Cinto de bronce claro con tachones y una greca."""
    if face in SIDES:
        if y == 1 and x % 2 == 0:
            return BRONZE_H, None
        if y in (0, h - 1):
            return BRONZE_D, None
        return mix(pick(BRONZE), BRONZE_L, 0.4), None
    return metal(face, x, y, w, h)


def strip(face, x, y, w, h):
    """Lámina del faldellín (pteryges): borde oscuro y un remache abajo."""
    if face in SIDES:
        if face in ("front", "back") and y == h - 2 and x == w // 2:
            return BRONZE_H, None
        if face in ("front", "back") and x == w - 1:
            return BRONZE_D, None
        if y == h - 1:
            return BRONZE_D, None
    return metal(face, x, y, w, h, patina=0.75)


def curls(face, x, y, w, h):
    """Rizos de caracol, como los de los bronces griegos: filas de bolitas (brillo arriba, sombra abajo), cada fila
    corrida medio rizo respecto de la anterior."""
    if face == "bottom":
        return BRONZE_DD, None
    row = y // 2
    on = (x + row) % 2 == 0
    if y % 2 == 0:
        return (BRONZE_H if on else BRONZE_MID_L), None
    return (BRONZE_DD if on else BRONZE_MID_D), None


def fringe(face, x, y, w, h):
    return (BRONZE_H if face in ("front", "top") else BRONZE_D), None


def head(face, x, y, w, h):
    """Cara: ojos de horno (brillan), patillas de rizos a los lados y la nuca de rizos."""
    if face == "front":
        if y == 3 and x in (1, 2, 5, 6):
            g = FURNACE_HOT if x in (2, 5) else FURNACE
            return g, g
        if y == 4 and x in (1, 2, 5, 6):
            return BRONZE_D, None                                       # párpado de abajo
        if y == 2:
            return BRONZE_D, None
        return metal(face, x, y, w, h)
    if face in ("west", "east"):
        fx = w - 1 - x if face == "west" else x
        if fx >= 4 or (fx == 3 and y >= 4):
            return curls(face, x, y, w, h)
    if face == "back":
        return curls(face, x, y, w, h)
    return metal(face, x, y, w, h)


def brow(face, x, y, w, h):
    if face == "bottom":
        return BRONZE_DD, None
    return (BRONZE_L if face in ("front", "top") else BRONZE_D), None


def nose(face, x, y, w, h):
    if face == "bottom":
        return BRONZE_DD, None
    return (BRONZE_L if face == "front" else pick(BRONZE)), None


def beard(face, x, y, w, h):
    """Barba de rizos; arriba, en el centro, la boca que brilla como la boca de un horno."""
    if face == "front" and y == 0 and w // 2 - 1 <= x <= w // 2 + 1 and w > 5:
        g = FURNACE if x != w // 2 else FURNACE_HOT
        return g, g
    return curls(face, x, y, w, h)


def arm_upper(face, x, y, w, h):
    return metal(face, x, y, w, h)


def shoulder(face, x, y, w, h):
    """Hombro redondo con una fila de remaches donde se une al pecho."""
    if face in SIDES and y == h - 1:
        return (BRONZE_H if x % 2 == 0 else BRONZE_DD), None
    return metal(face, x, y, w, h)


def elbow(face, x, y, w, h):
    return (BRONZE_D if face in ("west", "east", "bottom") else BRONZE_L), None


def forearm(face, x, y, w, h):
    return metal(face, x, y, w, h, patina=0.7)


def bracer(face, x, y, w, h):
    """Brazalete: banda clara con remaches."""
    if face in SIDES:
        if y == 0 and x % 2 == 1:
            return BRONZE_H, None
        return mix(pick(BRONZE), BRONZE_L, 0.35), None
    return metal(face, x, y, w, h)


def hand(face, x, y, w, h):
    """Puño: nudillos marcados en el frente."""
    if face == "front" and y == 0:
        return (BRONZE_L if x % 2 == 0 else BRONZE_D), None
    return metal(face, x, y, w, h, patina=0.4)


def thigh(face, x, y, w, h):
    return metal(face, x, y, w, h)


def knee(face, x, y, w, h):
    if face == "front":
        return (BRONZE_H if y == 0 else BRONZE_L if y == 1 else BRONZE_D), None
    return metal(face, x, y, w, h)


def shin(face, x, y, w, h):
    return metal(face, x, y, w, h, patina=0.35)


def calf(face, x, y, w, h):
    return metal(face, x, y, w, h, patina=0.5)


def foot(face, x, y, w, h):
    """Pie con dedos marcados en el frente y mucha pátina."""
    if face == "front" and x % 2 == 1:
        return BRONZE_DD, None
    if face == "bottom":
        return BRONZE_DD, None
    return metal(face, x, y, w, h, patina=0.0)


def nail(face, x, y, w, h):
    """El clavo de bronce que cierra la vena: cabeza oscura con el centro claro y un aro de ícor que brilla."""
    if face == "front":
        if x == w // 2 and y == h // 2:
            return BRONZE_H, None
        if x in (0, w - 1) or y in (0, h - 1):
            return ICHOR, ICHOR
        return BRONZE_D, None
    if face == "top":
        return ICHOR, ICHOR
    return BRONZE_D, None


def rock(face, x, y, w, h):
    """Roca gris con grietas oscuras y algo de musgo arriba."""
    if face == "top" and random.random() < 0.3:
        return ROCK_MOSS, None
    if random.random() < 0.12:
        return ROCK_D, None
    return pick(ROCK), None


def mirror(cubes):
    """Los cubos de un brazo o pierna derecha, reflejados para el lado izquierdo (x -> -x)."""
    out = []
    for c in cubes:
        (x, y, z), (w, h, d) = c["pos"], c["size"]
        out.append(cube(-(x + w), y, z, w, h, d, c["paint"]))
    return out


def arm_cubes(right):
    cubes = [cube(-4.5, -2, -3, 5, 4, 6, shoulder),
             cube(-4, 1, -2, 4, 7, 4, arm_upper),
             cube(-3, 7, 2, 2, 2, 1, elbow),
             cube(-4, 8, -2, 4, 5, 4, forearm),
             cube(-4.5, 11, -2.5, 5, 2, 5, bracer),
             cube(-3.5, 13, -2, 3, 3, 4, hand)]
    return cubes if right else mirror(cubes)


def leg_cubes(right):
    v = (lambda p, col: with_vein(p, col)) if right else (lambda p, col: p)
    cubes = [cube(-2.5, 0, -2.5, 5, 7, 5, v(thigh, 2)),
             cube(-1.5, 6, -3.5, 3, 2, 1, v(knee, 1)),
             cube(-2.5, 7, -2, 5, 5, 4, v(shin, 2)),
             cube(-2, 7.5, 2, 4, 3, 1, calf),
             cube(-2.5, 12, -4.5, 5, 2, 7, foot)]
    return cubes if right else mirror(cubes)


def build():
    m = Model()
    # Torso de estatua (13 de ancho): trapecios, pecho de placas, pectorales, abdomen, cinto y faldellín de láminas.
    # La vena de ícor baja por la columna x -3,5..-2,5 (lado derecho) desde el cuello hasta el tobillo.
    torso = [cube(-5, -4, -2.5, 10, 1, 5, chest),
             cube(-6.5, -3, -3.5, 13, 7, 7, with_vein(chest, 3)),
             cube(-5.5, -2, -4, 5, 4, 1, with_vein(pec, 2)),
             cube(0.5, -2, -4, 5, 4, 1, pec),
             cube(-5.5, 4, -3, 11, 4, 6, with_vein(abs_, 2)),
             cube(-1, -2, 3.5, 2, 9, 1, strip),                               # espina de bronce remachada
             cube(-6.5, 7, -3.5, 13, 3, 7, with_vein(belt, 3))]
    for i, x in enumerate([-6.5, -4.5, -2.5, -0.5, 1.5, 3.5]):
        h = 4 if i % 2 == 0 else 3
        paint = with_vein(strip, 1) if x == -4.5 else strip
        torso.append(cube(x, 9, -4, 2, h, 1, paint))
        torso.append(cube(x, 9, 3, 2, h, 1, strip))
    m.part("body", None, [0, 0, 0], [0, 0, 0], torso)

    # Cabeza: rizos de caracol, flequillo de rizos, ceja marcada, nariz recta, barba de rizos y boca de horno.
    hd = [cube(-4, -8, -4, 8, 8, 8, head),
          cube(-4.5, -9, -4.5, 9, 3, 9, curls),
          cube(-4.5, -6, 2, 9, 6, 3, curls),
          cube(-3.5, -6, -4.5, 7, 1, 1, brow),
          cube(-0.5, -5, -5, 1, 2, 1, nose),
          cube(-3.5, -3, -5, 7, 3, 1, beard),
          cube(-2.5, 0, -5, 5, 1, 1, beard)]
    for i in range(8):
        hd.append(cube(-4 + i, -7 + (0.4 if i % 2 else 0), -5, 1, 1, 1, fringe))
    m.part("head", None, [0, -3, 0], [0, 0, 0], hd)

    ar = m.part("arm_r", None, [-6.5, -2, 0], [0, 0, 0], arm_cubes(True))
    m.part("rock", ar, [-2, 15.5, -0.5], [0, 0, 0], [cube(-3, 0, -3, 6, 5, 6, rock),
                                                    cube(-2, 4.5, -2, 4, 1, 4, rock),
                                                    cube(-3.5, 1, -2, 1, 3, 3, rock),
                                                    cube(2.5, 0.5, -1, 1, 3, 3, rock),
                                                    cube(-1.5, 1, -3.5, 3, 3, 1, rock)])
    m.part("arm_l", None, [6.5, -2, 0], [0, 0, 0], arm_cubes(False))

    lr = m.part("leg_r", None, [-3, 10, 0], [0, 0, 0], leg_cubes(True))
    m.part("nail", lr, [0, 0, 0], [0, 0, 0], [cube(-1.5, 8.5, -3, 3, 3, 1, nail)])
    m.part("leg_l", None, [3, 10, 0], [0, 0, 0], leg_cubes(False))
    return m


NOTES = """Pasa de 11 a 62 cubos y de escala 1,5 (3 bloques de alto) a 2,2: casi 5 bloques de alto y 3 de ancho con la roca.
Cuerpo de estatua de bronce: trapecios, pecho de placas remachadas, pectorales y abdominales en relieve, espina, cinto con tachones y faldellín de seis láminas.
Brazos con hombro redondo, codo, antebrazo, brazalete y puño; piernas con muslo, rodilla, espinilla, pantorrilla y pie con dedos.
Cabeza con rizos de caracol, flequillo, ceja, nariz, barba de rizos, ojos y boca de horno que brillan.
La vena de ícor es una sola línea que brilla del cuello al tobillo derecho, y termina en el clavo de bronce con un aro de ícor.
Pátina verde que sube desde los pies; la roca de la mano derecha es más irregular, con musgo.
Fiel al mito: gigante de bronce de Hefesto, una sola vena del cuello al tobillo cerrada por un clavo de bronce, rizos y barba como en la crátera de Ruvo, lanza rocas.
Licencia de juego: el cinto y el faldellín de láminas (en la vasija está desnudo), la boca de horno y la pátina.
Cultura pop: nada (ni el Talos de Jasón y los argonautas de 1963 ni el de God of War).
Animación propuesta: la de hoy (paso pesado, brazos, lanzar la roca por detrás de la cabeza) y, al abrazar, los dos brazos se cierran al frente y la vena y los ojos brillan más (todo el bronce se pone al rojo).
En el juego habría que agrandar la caja de choque (hoy 1,5 × 2,9) a unos 2,2 × 4,9."""
