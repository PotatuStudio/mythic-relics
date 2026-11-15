"""Propuesta: escarabajo sagrado (Scarabaeus sacer), la imagen de Jepri, el sol que renace cada mañana: así como el
escarabajo empuja su bola, Jepri empuja el disco del sol por el cielo. Del tamaño de casi un bloque: élitros abovedados
con surcos y brillo de joya (turquesa, lapislázuli y oro, como los escarabeos de los ajuares), pronoto ancho, cabeza en
pala con seis dientes, antenas en abanico, seis patas de dos tramos (las delanteras son palas dentadas para cavar) y,
sobre la cabeza, el pequeño disco del sol, que brilla."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from china_entities import C, Model, cube, mix, pick  # noqa: E402

TEX_W = 64
SCALE = 1.0
ROOT_OFFSET = (0, 0, 0)
SEED = 9

GOLD_L, GOLD, GOLD_M, GOLD_D = C("#ffe478"), C("#e8b634"), C("#c69222"), C("#92621a")
OUTL = C("#56380e")
TURQ_L, TURQ, TURQ_D = C("#6ee4d0"), C("#28aca0"), C("#167070")
LAPIS_L, LAPIS, LAPIS_D = C("#5078dc"), C("#2a48ac"), C("#1a2c76")
CHITIN = [C("#2a2018"), C("#32261c"), C("#241a12")]
CHITIN_HI = C("#5a4630")
SUN, SUN_L, SUN_D = C("#ff8a1e"), C("#ffd860"), C("#e0401a")


def elytron(face, x, y, w, hh):
    """Élitro: surcos a lo largo (estrías), del turquesa del lomo al lapislázuli de los costados, borde de oro."""
    if face == "bottom":
        return LAPIS_D, None
    if face == "top":
        # En la textura, x crece hacia afuera o hacia adentro según el lado; el brillo va en la columna del centro.
        t = abs(x - (w - 1) / 2) / max(1, w / 2)
        base = mix(TURQ, LAPIS, min(1.0, t * 0.8))
        if x % 2 == 1:
            base = mix(base, LAPIS_D, 0.3)          # surco
        if t < 0.25 and 1 <= y <= hh - 2 and y % 4 != 3:
            base = mix(base, TURQ_L, 0.45)          # reflejo
        return base, None
    if face in ("west", "east"):
        return (mix(LAPIS, TURQ, 0.3) if y == 0 else (LAPIS if x % 2 else mix(LAPIS, LAPIS_D, 0.5))), None
    # frente y atrás
    return (LAPIS if x % 2 else mix(LAPIS, TURQ, 0.4)), None


def elytron_rim(face, x, y, w, hh):
    """Capa de abajo del élitro: igual, con el reborde de oro abajo."""
    if face in ("west", "east", "front", "back") and y == hh - 1:
        return (GOLD if x % 3 else GOLD_L), None
    return elytron(face, x, y, w, hh)


def elytron_edge(face, x, y, w, hh):
    """Reborde de oro de los élitros y la sutura del medio."""
    if face == "top":
        return (GOLD_L if (x + y) % 3 == 0 else GOLD), None
    return (GOLD if y == 0 else GOLD_M), None


def pronotum(face, x, y, w, hh):
    """Pronoto: oro bruñido con el reflejo arriba y una línea de lapislázuli en el borde."""
    if face == "top":
        cx = abs(x - (w - 1) / 2) / max(1, w / 2)
        if cx < 0.25 and y < hh - 1:
            return GOLD_L, None
        return (GOLD if cx < 0.7 else GOLD_M), None
    if face == "bottom":
        return GOLD_D, None
    if y == hh - 1 and hh > 1:
        return LAPIS, None
    return (GOLD_M if y else GOLD), None


def head(face, x, y, w, hh):
    """Cabeza en pala: oro, con los ojos negros a los lados."""
    if face in ("west", "east") and y == 0 and x == 1:
        return C("#0c0a08"), None
    if face == "bottom":
        return GOLD_D, None
    if face == "top":
        return (GOLD_L if (x + y) % 4 == 0 else GOLD), None
    return GOLD_M, None


def teeth(face, x, y, w, hh):
    """Plano de los seis dientes del clípeo: se recorta en dientes hacia adelante."""
    if face not in ("top", "bottom"):
        return None, None
    # y = 0 es la punta (lado -z) en la cara de arriba.
    tip = y if face == "top" else hh - 1 - y
    if tip == 0 and x % 2 == 1:
        return None, None
    return (GOLD if tip else GOLD_L), None


def chitin(face, x, y, w, hh):
    if face == "top" and x % 3 == 0:
        return CHITIN_HI, None
    return pick(CHITIN), None


def spines(face, x, y, w, hh):
    """Plano de espinas de las tibias: dientes hacia afuera."""
    if face not in ("top", "bottom"):
        return None, None
    if y == 0 and x % 2 == 1:
        return None, None
    return (CHITIN_HI if y == 0 else CHITIN[0]), None


def antenna(face, x, y, w, hh):
    return CHITIN[1], None


def club(face, x, y, w, hh):
    """Maza de láminas en abanico (rojiza, con las láminas marcadas)."""
    return (C("#7a4a20") if x % 2 == 0 else C("#a8662a")), None


def sun_disk(face, x, y, w, hh):
    """Disco del sol: rojo-naranja con el centro claro; se recorta en círculo y brilla."""
    if face in ("front", "back"):
        cx, cy = (w - 1) / 2, (hh - 1) / 2
        d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
        if d > w / 2 - 0.05:
            return None, None
        c = SUN_L if d < 1.0 else SUN if d < w / 2 - 1.1 else SUN_D
        return c, c
    # cantos: solo donde el círculo toca el borde de la caja
    span = w if face in ("top", "bottom") else hh
    along = x if face in ("top", "bottom") else y
    if abs(along - (span - 1) / 2) > span / 2 - 1.2:
        return None, None
    return SUN_D, SUN_D


def belly(face, x, y, w, hh):
    if face == "bottom":
        return (CHITIN[0] if y % 2 else CHITIN_HI), None
    return pick(CHITIN), None


def build():
    m = Model()
    # Cuerpo de abajo (abdomen y tórax), oscuro.
    m.part("shell", None, [0, 21, 2], [0, 0, 0], [
        cube(-4.5, -1.5, -5.5, 9, 3, 11, belly),
    ])
    # Élitros en tres capas que forman la cúpula, con reborde y redondeo atrás. Cada uno es su propia parte para poder
    # abrirlos al volar.
    for name, sx in (("left_elytron", 1), ("right_elytron", -1)):
        def X(x0, w):
            return x0 if sx > 0 else -x0 - w
        m.part(name, "shell", [0, 0, 0], [0, 0, 0], [
            cube(X(0, 6), -3.5, -6, 6, 3, 11, elytron_rim),
            cube(X(0, 5), -4.5, -5, 5, 1, 10, elytron),
            cube(X(0, 3), -5.5, -3.5, 3, 1, 7, elytron),
            cube(X(0, 5), -3.2, 5, 5, 3, 1, elytron_rim),        # redondeo de atrás
            cube(X(0, 3), -2.8, 6, 3, 2, 1, elytron_rim),
        ])
    # Sutura de oro entre los dos élitros, apenas en relieve.
    m.part("suture", "shell", [0, 0, 0], [0, 0, 0], [cube(-0.5, -6, -3, 1, 1, 6, elytron_edge)])
    # Pronoto: escudo ancho de oro delante de los élitros.
    m.part("pronotum", None, [0, 21, -4.5], [0, 0, 0], [
        cube(-5, -4, -3, 10, 4, 4, pronotum),
        cube(-4, -5, -2.5, 8, 1, 3, pronotum),
    ])
    # Cabeza en pala con los dientes, las antenas en abanico y el disco del sol encima.
    m.part("head", None, [0, 21, -7.5], [0, 0, 0], [
        cube(-3.5, -2, -3, 7, 2, 3, head),
        cube(-4, -1.2, -3.8, 8, 0, 1, teeth),
    ])
    for name, sx in (("left_antenna", 1), ("right_antenna", -1)):
        m.part(name, "head", [sx * 2.5, -1, -2.5], [-0.35, -sx * 0.6, 0], [
            cube(-0.5, -0.5, -2, 1, 1, 2, antenna)])
        # La maza: tres láminas que se abren en abanico.
        for j, ang in enumerate((-0.45, 0.0, 0.45)):
            m.part(f"{name}_lamella{j}", name, [0, 0, -2], [ang, 0, 0], [cube(-0.5, -0.5, -2, 1, 1, 2, club)])
    m.part("sun", "head", [0, -2, -1], [0, 0, 0], [cube(-3, -6, 0, 6, 6, 1, sun_disk)])
    # Seis patas de dos tramos: fémur ancho hacia afuera y tibia que baja al suelo; las de adelante, palas dentadas.
    legs = [(-5.5, 0.55, 2.6), (-1.0, 0.0, 3.0), (3.5, -0.55, 3.2)]
    for i, (z, spread, x0) in enumerate(legs):
        for side, sx in (("left", 1), ("right", -1)):
            name = f"{side}_leg{i}"
            m.part(name, None, [sx * x0, 21.3 - (0.32 if i != 1 else 0), z], [0, sx * spread, sx * -0.25], [
                cube(0 if sx > 0 else -5, -0.5, -1, 5, 1, 2, chitin)])
            m.part(name + "_tibia", name, [sx * 5, 0, 0], [0, 0, sx * 1.05], [
                cube(0 if sx > 0 else -5, -0.5, -0.5, 5, 1, 1, chitin),
                cube(0 if sx > 0 else -5, 0, -1.5 if i == 0 else 0.5, 5, 0, 1, spines)])
    return m


NOTES = "\n".join([
    "Mucho más grande: de 0,8 × 0,35 × 0,75 a 1,5 × 0,7 × 1,4 bloques con las patas abiertas (el caparazón mide casi un bloque); 10 → 43 cubos.",
    "Élitros en cúpula de tres capas con surcos, reborde y sutura de oro; cada élitro es su propia pieza (se pueden abrir al volar).",
    "Pronoto ancho de oro bruñido, cabeza en pala con los seis dientes del clípeo y ojos a los lados.",
    "Antenas con la maza de láminas en abanico; seis patas de dos tramos (fémur y tibia) con espinas; las delanteras, palas dentadas para cavar.",
    "Pequeño disco del sol sobre la cabeza que brilla.",
    "Textura de joya: turquesa y lapislázuli con reflejos y oro, como los escarabeos de los ajuares egipcios.",
    "Fiel al mito: el escarabajo pelotero es Jepri, el sol que renace; su imagen sostiene el disco solar (escarabeos, Libro de los Muertos).",
    "Licencia de juego: el disco en la cabeza en vez de una bola entre las patas; colores de joya en un escarabajo de verdad negro.",
    "Cultura pop: nada.",
    "Animación propuesta: marcha en trípode con la tibia que se dobla, antenas que abren el abanico, élitros que se levantan al saltar o atacar y el disco que late con luz.",
])
