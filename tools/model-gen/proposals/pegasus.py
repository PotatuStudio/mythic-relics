"""Propuesta: Pegaso, el caballo alado blanco nacido de la sangre de Medusa, que Belerofonte domó con la brida de oro
que le dio Atenea (Píndaro, Olímpica 13).

Mismas partes que anima PegasusModel (body, neck, head, wing_r, wing_r2, wing_l, wing_l2, tail, tail2 y las cuatro
patas); se suman partes hijas: el tercer tramo del ala («wing_*3», la mano), las plumas primarias sueltas, los mechones
de la cola y la caña de cada pata («leg_*_lower»). Las alas suben desde la cruz y curvan la punta hacia arriba, como en
los estáteros de Corinto; la crin es corta y erguida, con el copete atado en un moño, como en los caballos del friso
del Partenón y los vasos áticos."""
import math
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from china_entities import C, Model, cube, front_x, mix, pick  # noqa: E402

TEX_W = 256
SCALE = 1.0
ROOT_OFFSET = (0, 0, 0)
SEED = 31

# ---------------------------------------------------------------- colores
COAT = [C("#f7f6f2"), C("#f2f0ea"), C("#fbfaf7"), C("#efece5")]
COAT_SH = C("#d9d4ca")
COAT_SH2 = C("#c9c3b8")
SKIN = [C("#b9b2b4"), C("#aea7aa")]          # piel rosada grisácea del hocico de los caballos blancos
HOOF = [C("#8a8478"), C("#7d776c")]
HOOF_D = C("#5e584e")
SILVER = [C("#eef1f6"), C("#e2e7ee"), C("#f8f9fb")]
SILVER_D = C("#b8c2d0")
FEATHER = [C("#ffffff"), C("#f7f8fb"), C("#eef2f7")]
VANE_D = C("#d3dbe6")
TIP = C("#aebbd0")
RACHIS = C("#e6dcc2")
COVERT_EDGE = C("#d8cfb4")
GOLD = [C("#f5c842"), C("#ffd968"), C("#efbb32")]
GOLD_L, GOLD_D = C("#fff1a8"), C("#b47a16")
RED = [C("#a8322a"), C("#9c2c24")]
EYE = C("#2a2430")


# ---------------------------------------------------------------- pintores
def coat(face, x, y, w, h):
    """Pelaje blanco con sombra suave abajo (se lee como volumen y no como un bloque plano)."""
    if face == "bottom":
        return COAT_SH, None
    if face in ("west", "east", "front", "back") and h > 4:
        if y >= h - 1:
            return COAT_SH2, None
        if y >= h - 3:
            return COAT_SH, None
    return (COAT_SH if random.random() < 0.03 else pick(COAT)), None


def leg_upper(face, x, y, w, h):
    if face in ("top", "bottom"):
        return COAT_SH, None
    if face == "back" or y == h - 1:
        return COAT_SH, None
    return pick(COAT), None


def leg_lower(face, x, y, w, h):
    if face in ("top", "bottom"):
        return COAT_SH, None
    return (COAT_SH if face == "back" else pick(COAT)), None


def hoof(face, x, y, w, h):
    """Casco gris cuerno con la corona blanca arriba (con este casco abrió la fuente Hipocrene)."""
    if face == "top":
        return COAT_SH, None
    if face == "bottom" or y == h - 1:
        return HOOF_D, None
    if y == 0:
        return C("#e9e4da"), None
    return pick(HOOF), None


def head_paint(face, x, y, w, h):
    """Cráneo: ojo grande y oscuro a cada lado con un punto de luz y la sombra de la cuenca."""
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y == 2 and fx in (2, 3):
            return EYE, None
        if y == 1 and fx == 2:
            return C("#ffffff"), None
        if y == 1 and fx == 3:
            return COAT_SH2, None
    return coat(face, x, y, w, h)


def jowl(face, x, y, w, h):
    c, _ = coat(face, x, y, w, h)
    return mix(c, COAT_SH, 0.35), None


def muzzle(face, x, y, w, h):
    """Hocico que pasa del blanco al gris rosado, con los ollares y la boca."""
    if face == "front" and y == 1 and x in (0, w - 1):
        return C("#4a4248"), None
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y == 1 and fx == 0:
            return C("#4a4248"), None
        if y == h - 1 and fx <= 3:
            return C("#6e6468"), None
        t = max(0.0, 1 - fx / 4)
        return mix(pick(COAT), pick(SKIN), t), None
    if face in ("front", "bottom"):
        return pick(SKIN), None
    return mix(pick(COAT), pick(SKIN), 0.4 if face == "top" and y < 2 else 0.0), None


def lip(face, x, y, w, h):
    return pick(SKIN), None


def ear(face, x, y, w, h):
    if face == "front" and 0 < x < w - 1 and y > 0:
        return C("#c9b8b8"), None
    return pick(COAT), None


def mane(face, x, y, w, h):
    """Crin corta y erguida, recortada a peine como en el friso del Partenón: mechones de hebras plateadas."""
    if face in ("west", "east"):
        if y == 0 and front_x(face, x, w) % 2 == 1:
            return None, None          # puntas dentadas de los mechones
        return (SILVER_D if x % 2 == 0 and y > 1 else pick(SILVER)), None
    if face == "top":
        return pick(SILVER), None
    return (SILVER_D if x % 2 else pick(SILVER)), None


def tail_hair(face, x, y, w, h):
    if face == "top":
        return pick(SILVER), None
    if face == "bottom":
        return SILVER_D, None
    return (SILVER_D if x % 2 == 0 and random.random() < 0.45 else pick(SILVER)), None


def lock(face, x, y, w, h):
    """Mechón suelto de la cola (plano): se afina hacia la punta."""
    if face not in ("west", "east"):
        return None, None
    fx = front_x(face, x, w)
    if y > h - 1 - fx // 2:
        return None, None
    return (SILVER_D if (fx + y) % 3 == 0 else pick(SILVER)), None


def gold(face, x, y, w, h):
    if face == "top":
        return GOLD_L, None
    if face == "bottom":
        return GOLD_D, None
    return (GOLD_D if (x + y) % 4 == 0 else pick(GOLD)), None


def bridle(face, x, y, w, h):
    """La brida de oro de Atenea: correas de oro labrado; un punto de cada tres brilla, como un regalo divino."""
    c = GOLD_D if (x + y) % 3 == 0 else pick(GOLD)
    glow = (x * 7 + y * 3) % 5 == 0
    return c, (mix(c, GOLD_L, 0.5) if glow else None)


def rosette(face, x, y, w, h):
    """Falera de la carrillera: roseta de oro con el centro que brilla."""
    if face in ("west", "east"):
        cx, cy = (w - 1) / 2, (h - 1) / 2
        if abs(x - cx) < 0.6 and abs(y - cy) < 0.6:
            return GOLD_L, GOLD_L
        return (GOLD_D if (x + y) % 2 else GOLD[0]), None
    return gold(face, x, y, w, h)


def cloth(face, x, y, w, h):
    """Manta roja (licencia: Belerofonte lo montaba a pelo) con el meandro de oro en el borde."""
    if face in ("west", "east", "front", "back") and y >= h - 2:
        meander = (x % 4 in (0, 1)) if y == h - 2 else (x % 4 in (1, 2))
        return (GOLD[0] if meander else GOLD_D), None
    if face == "top" and (x in (0, w - 1) or y in (0, h - 1)):
        return GOLD_D, None
    return pick(RED), None


def bone(face, x, y, w, h):
    """Borde de ataque del ala: plumitas pequeñas, blanco apenas dorado."""
    return (COVERT_EDGE if (x + y) % 5 == 0 else pick(FEATHER)), None


def coverts(face, x, y, w, h):
    """Cobertoras: plumas cortas en escamas, en filas, con el borde marcado."""
    if face in ("top", "bottom"):
        row = y // 2
        sx = (x + 2 * (row % 2)) % 4
        if y % 2 == 1 and sx in (0, 3):
            return VANE_D, None
        return pick(FEATHER), None
    return pick(FEATHER), None


def secondaries(face, x, y, w, h):
    """Secundarias: plumas largas paralelas hacia atrás con la punta redonda y el borde de atrás recortado."""
    if face in ("top", "bottom"):
        k = x % 3
        notch = y == h - 1 and k != 1
        if notch:
            return None, None
        if y >= h - 2:
            return TIP, None
        if k == 2:
            return VANE_D, None
        if k == 1 and y < h - 3:
            return RACHIS, None
        return pick(FEATHER), None
    return pick(FEATHER), None


def primary(face, x, y, w, h):
    """Primaria suelta (plano): pluma larga con el raquis al centro, que se afina en punta; la punta, gris azulada."""
    if face not in ("top", "bottom"):
        return None, None
    # el largo va en x (hacia afuera, x = 0 es la punta en el ala derecha) y el ancho en y
    along = x / max(1, w - 1)          # 0 = punta, 1 = base
    half = (h - 1) / 2
    width = half * min(1.0, 0.55 + along * 1.5)
    if abs(y - half) > width + 0.01:
        return None, None
    if abs(y - half) < 0.6:
        return RACHIS, None
    if along < 0.22:
        return TIP, None
    return (VANE_D if y > half else pick(FEATHER)), None


def primary_l(face, x, y, w, h):
    return primary(face, w - 1 - x, y, w, h)


# ---------------------------------------------------------------- modelo
def leg(m, name, x, z, hind):
    up = m.part(name, None, [x, 13, z], [0, 0, 0], [cube(-2, -2, -2, 4, 6, 4, leg_upper)])
    lo = [
        cube(-1.5, -0.5, -1.5, 3, 2, 3, leg_upper),      # rodilla / corvejón
        cube(-1.5, 1.5, -1, 3, 3, 2, leg_lower),         # caña
        cube(-1.5, 4.5, -1.5, 3, 1, 3, leg_lower),       # menudillo
        cube(-2, 5, -2.6, 4, 2, 4, hoof),                # casco (apoya en y = 24)
    ]
    if hind:
        lo.append(cube(-1, -1, 1, 2, 3, 2, leg_upper))   # punta del corvejón
    m.part(name + "_lower", up, [0, 4, 0], [0, 0, 0], lo)


def wing(m, side, body):
    """Ala de tres tramos (brazo, antebrazo y mano) con cobertoras, secundarias y siete primarias sueltas en abanico.
    El ala derecha crece hacia -x; la izquierda es su espejo."""
    s = -1 if side == "r" else 1
    pf = primary if side == "r" else primary_l

    def X(x0, w):                      # caja que crece hacia afuera desde el pivote
        return x0 * s if s > 0 else -(x0 + w)

    w1 = m.part(f"wing_{side}", body, [s * 3.5, -5, -6], [0, s * -0.2, s * -0.4], [
        cube(X(0, 9), -1.5, -0.5, 9, 2, 2, bone),
        cube(X(0, 9), -0.5, 1.5, 9, 1, 6, coverts),
        cube(X(0.5, 8), -0.1, 5, 8, 1, 9, secondaries),   # terciarias, junto al cuerpo (un poco más cortas que las secundarias)
    ])
    w2 = m.part(f"wing_{side}2", w1, [s * 9, 0, 0], [0, s * -0.1, s * -0.15], [
        cube(X(0, 9), -1.5, -0.5, 9, 2, 2, bone),
        cube(X(0.5, 8), -0.6, 1.5, 8, 1, 4, coverts),
        cube(X(0, 9), -0.2, 4, 9, 1, 11, secondaries),
    ])
    w3 = m.part(f"wing_{side}3", w2, [s * 9, 0, 0], [0, s * -0.25, s * -0.45], [
        cube(X(0, 7), -1, -0.5, 7, 1, 2, bone),
        cube(X(0, 7), -0.6, 1.5, 7, 1, 3, coverts),
    ])
    # Primarias: nacen a lo largo de la mano y se abren en abanico hacia atrás. El largo crece parejo desde la muñeca
    # (12, como las secundarias) hasta la punta (16) y el abanico se abre en pasos iguales: el borde de fuga queda suave.
    for k in range(7):
        length = 16 - round(k * 4 / 6)
        ang = 0.08 + k * 0.14
        m.part(f"wing_{side}_primary{k}", w3, [s * (7 - k * 1.0), 0.1 + 0.11 * k, 1.0], [0, s * -ang, 0],
               [cube(X(0, length), 0, -2, length, 0, 4, pf)])
    return w1


def build():
    m = Model()
    body = m.part("body", None, [0, 9, 0], [0, 0, 0], [
        cube(-5, -5, -10, 10, 10, 20, coat),             # tonel (y 4..14)
        cube(-5.5, -3.5, -12, 11, 8, 6, coat),           # pecho y paletas
        cube(-5.5, -5.5, 4, 11, 9, 8, coat),             # grupa y ancas
        cube(-4, -6.5, 5, 8, 1, 6, coat),                # lomo redondeado de la grupa
        cube(-4.5, 4.5, -6, 9, 1, 10, coat),             # vientre
        cube(-2, -6.5, -10, 4, 2, 5, coat),              # cruz (de aquí nacen las alas)
        cube(-5.5, -5.4, -3, 11, 4, 7, cloth),           # manta
    ])
    for name, x, z, hind in (("leg_fr", -2.8, -9.2, False), ("leg_fl", 2.8, -9.2, False),
                             ("leg_br", -2.8, 7.8, True), ("leg_bl", 2.8, 7.8, True)):
        leg(m, name, x, z, hind)

    neck = m.part("neck", body, [0, -3, -8], [0.45, 0, 0], [
        cube(-2.5, -12, -3, 5, 13, 7, coat),             # cuello arqueado
        cube(-3, -4, -3.5, 6, 6, 8, coat),               # base del cuello
    ])
    # Crin corta y erguida en ocho mechones, del moño de la nuca a la cruz.
    m.part("mane", neck, [0, 0, 0], [0, 0, 0],
           [cube(-1.5, -13 + i * 1.6, 3.7, 3, 2, 2, mane) if i % 2 else cube(-1, -13 + i * 1.6, 3.5, 2, 2, 2, mane)
            for i in range(8)]
           + [cube(-0.5, -13.8, 3.8, 1, 13, 2, mane)])
    hd = m.part("head", neck, [0, -12, -1], [0.25, 0, 0], [
        cube(-3, -4.5, -5, 6, 6, 7, head_paint),         # cráneo
        cube(-3.5, -1.2, -1, 7, 3, 4, jowl),             # quijadas
        cube(-2, -4, -10.5, 4, 4, 6, muzzle),            # hocico fino (caballo de sangre caliente)
        cube(-1.5, -0.2, -10, 3, 1, 4, lip),             # belfo
        cube(-1, -6.5, -2.5, 2, 2, 2, mane),             # copete atado en moño
        cube(-1.5, -5.6, -2, 3, 1, 1, gold),             # la cinta de oro del moño
    ])
    for ear_name, sx in (("ear_l", 1), ("ear_r", -1)):
        e = m.part(ear_name, hd, [sx * 1.75, -4.5, 0.5], [0.2, 0, sx * 0.25], [cube(-1, -3, -0.5, 2, 3, 1, ear)])
        m.part(ear_name + "_tip", e, [0, -3, 0], [0, 0, 0], [cube(-0.5, -1, -0.5, 1, 1, 1, ear)])
    # La brida de oro de Atenea: testera, frontalera, muserola, carrilleras con faleras y las anillas del bocado.
    m.part("bridle", hd, [0, 0, 0], [0, 0, 0], [
        cube(-3.5, -4.8, 0.2, 7, 3, 1, bridle),          # testera detrás de las orejas
        cube(-3.4, -4.8, -3.6, 7, 1, 1, bridle),         # frontalera
        cube(-2.5, -4.3, -8, 5, 5, 1, bridle),           # muserola
        cube(-3.6, -2, -6, 0, 1, 6, bridle), cube(3.6, -2, -6, 0, 1, 6, bridle),   # carrilleras
        cube(-3.7, -3, -3.5, 0, 3, 3, rosette), cube(3.7, -3, -3.5, 0, 3, 3, rosette),
        cube(-3, -1.5, -9.5, 1, 1, 1, gold), cube(2, -1.5, -9.5, 1, 1, 1, gold),
    ])
    # Riendas de oro que bajan del bocado y vuelven sobre el cuello.
    for side, sx in (("r", -1), ("l", 1)):
        m.part(f"rein_{side}", hd, [sx * 3.6, -1, -9], [-0.15, 0, 0], [cube(0, 0, 0, 0, 1, 13, bridle)])

    wing(m, "r", body)
    wing(m, "l", body)

    tail = m.part("tail", body, [0, -4.5, 11.5], [0.4, 0, 0], [
        cube(-1.5, -0.5, -1.5, 3, 5, 3, tail_hair),
        cube(-2.5, 3.3, -2, 5, 1, 4, gold),               # cinta de oro (los griegos ataban la cola)
    ])
    t2 = m.part("tail2", tail, [0, 4.5, 0], [-0.1, 0, 0], [
        cube(-2, 0, -1.5, 4, 8, 3, tail_hair),
        cube(-2.5, 6, -2, 5, 7, 4, tail_hair),
    ])
    for side, sx, yr in (("r", -1, 0.25), ("l", 1, -0.25)):
        m.part(f"tail_lock_{side}", t2, [sx * 2.6, 4, 0], [0.1, 0, sx * 0.12], [cube(0, 0, -1.5, 0, 11, 4, lock)])
    return m


NOTES = """Hoy: caballo blanco de cajas rectas con dos tablas por ala, crin de una tabla y brida en tres cubos.
Alas de tres tramos (brazo, antebrazo y mano) con cobertoras en escamas, secundarias y 7 primarias sueltas en abanico, de largo parejo (de 12 en la muñeca a 16 en la punta); la punta sube como en los estáteros de Corinto. Unos 2 bloques por lado.
Cuerpo con forma: pecho, cruz, vientre, grupa redondeada; patas de dos tramos con rodilla, corvejón, caña y casco.
Cabeza fina con quijadas, hocico rosado, belfo, ojos con punto de luz y orejas de dos piezas.
Crin corta y erguida en mechones y copete atado en moño con cinta de oro (friso del Partenón, vasos áticos); cola atada con mechones sueltos.
Brida de oro completa (testera, frontalera, muserola, carrilleras con faleras y riendas) que brilla a puntos.
Fiel al mito: blanco, alado, nacido de Medusa; la brida de oro de Atenea con la que Belerofonte lo domó.
Licencia de juego: la manta roja con meandro (Belerofonte montaba a pelo) y el brillo de la brida.
Cultura pop: nada.
Animación propuesta: aleteo de los tres tramos con la mano atrasada; en el suelo las alas plegadas hacia atrás y las primarias cerradas; en vuelo las primarias se abren; cabeceo, cola que ondea y patas recogidas al volar."""
