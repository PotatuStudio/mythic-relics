"""Propuesta: Sleipnir, el corcel gris de Odín, el mejor de los caballos (Gylfaginning 15 y 42), de ocho patas.

Mismas partes que anima SleipnirModel (body, tail, tail_tip, head_parts, head, mane, upper_mouth, left_ear, right_ear y
las ocho patas con sus nombres); cada pata suma una parte hija «*_lower» (caña y casco) para la articulación.
Silla y arreos de la era vikinga (manta tejida, perilla y borrén, pretal con colgantes de bronce dorado, cabezada con
apliques); runas en los dientes como dice el Sigrdrífumál (15: «á Sleipnis tǫnnum»), que brillan."""
import math
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from china_entities import C, Model, cube, front_x, mix, pick  # noqa: E402

TEX_W = 256
SCALE = 1.25
ROOT_OFFSET = (0, 0, 0)
SEED = 23

# ---------------------------------------------------------------- colores
COAT = [C("#949aa6"), C("#8a909c"), C("#9ca2ae"), C("#868c98")]
DAPPLE = [C("#c3c8d2"), C("#b9bfca")]
SHADE = C("#686e7c")
LEG_D = C("#4c505c")
HOOF = [C("#2c2a32"), C("#34323a")]
SHOE = C("#8e94a0")
MANE = [C("#eef1f6"), C("#dfe4ec"), C("#f8f9fc")]
MANE_D = C("#a9b2c2")
MUZ = [C("#5a5f6c"), C("#525764")]
EYE = C("#1c1a22")
GOLD = [C("#e8b84a"), C("#f2c75a"), C("#d9a63c")]
GOLD_L, GOLD_D = C("#ffe08a"), C("#9c6a1e")
LEATHER = [C("#6e4224"), C("#76482a"), C("#663c20")]
LEATHER_D = C("#40240f")
WOOL = [C("#2f4f7a"), C("#2b4870")]
WOOL_RED = C("#9a2e22")
IVORY = C("#e9e1c8")
RUNE = C("#8fe4ff")

# Runas de 3x5 (futhark): ᛏ (Týr), ᚱ, ᛉ, ᚠ, ᛟ
RUNES = [["XXX", ".X.", ".X.", ".X.", ".X."], ["XX.", "X.X", "XX.", "X.X", "X.X"],
         ["X.X", "X.X", ".X.", ".X.", ".X."], ["X..", "XX.", "X.X", "X..", "X.."],
         [".X.", "X.X", ".X.", "X.X", "X.X"]]


# ---------------------------------------------------------------- pintores
def dapple_at(x, y, salt=0):
    """Moteado del tordo: anillos claros redondeados en una grilla desfasada."""
    cx = (x + (y // 6) * 3 + salt) % 6 - 2.5
    cy = y % 6 - 2.5
    return math.hypot(cx, cy) < 1.7 and ((x + salt) // 6 + y // 6) % 2 == 0


def coat(face, x, y, w, h):
    if face == "bottom":
        return SHADE, None
    if face in ("west", "east", "front", "back") and y >= h - 1 and h > 4:
        return mix(pick(COAT), SHADE, 0.6), None
    if dapple_at(x, y, 1 if face in ("west", "front") else 3):
        return pick(DAPPLE), None
    return pick(COAT), None


def neck_coat(face, x, y, w, h):
    """Cuello: más claro arriba (los tordos aclaran con la edad) y moteado."""
    c, g = coat(face, x, y, w, h)
    if face in ("west", "east") and front_x(face, x, w) == 0:
        return mix(c, SHADE, 0.3), g
    return c, g


def leg_upper(face, x, y, w, h):
    """Antebrazo / muslo: gris moteado que oscurece hacia la rodilla."""
    if face in ("top", "bottom"):
        return SHADE, None
    t = y / max(1, h - 1)
    c, _ = coat(face, x, y, w, h)
    return mix(c, LEG_D, max(0.0, t - 0.4)), None


def leg_lower(face, x, y, w, h):
    """Caña: gris oscuro, casi pizarra, con un brillo en el tendón de atrás."""
    if face in ("top", "bottom"):
        return LEG_D, None
    if face == "back":
        return mix(LEG_D, SHADE, 0.6), None
    return mix(pick(COAT), LEG_D, 0.75), None


def hoof(face, x, y, w, h):
    """Casco negro con la herradura de hierro en la fila de abajo y la corona clara arriba."""
    if face == "bottom":
        return (SHOE if x in (0, w - 1) or y in (0, h - 1) else HOOF[0]), None
    if face == "top":
        return LEG_D, None
    if y == h - 1:
        return SHOE, None
    if y == 0:
        return C("#5e626e"), None
    return pick(HOOF), None


def mane(face, x, y, w, h):
    """Crin plateada en mechones: hebras verticales, más oscuras entre trenza y trenza."""
    if face in ("top", "bottom"):
        return pick(MANE), None
    if y % 4 == 3:
        return MANE_D, None
    return (MANE_D if x % 2 == 1 and random.random() < 0.35 else pick(MANE)), None


def braid(face, x, y, w, h):
    """Trenza: cruces en zigzag."""
    if face in ("top", "bottom"):
        return pick(MANE), None
    return (MANE_D if (x + y) % 2 == 0 else pick(MANE)), None


def mane_fall(face, x, y, w, h):
    """Melena que cae a un costado (plano): mechones de largo distinto con puntas libres."""
    if face not in ("west", "east"):
        return None, None
    fx = front_x(face, x, w)
    reach = h - 1 - ((fx * 5 + 3) % 4)
    if y > reach:
        return None, None
    return (MANE_D if fx % 2 == 1 and y % 3 == 0 else pick(MANE)), None


def tail_hair(face, x, y, w, h):
    if face in ("top",):
        return pick(MANE), None
    if face == "bottom":
        return MANE_D, None
    return (MANE_D if (x % 2 == 0 and random.random() < 0.4) else pick(MANE)), None


def gold(face, x, y, w, h):
    """Bronce dorado de los apliques vikingos (estilo Borre): un nudo en relieve claro."""
    if face == "top":
        return GOLD_L, None
    if face == "bottom":
        return GOLD_D, None
    return (GOLD_L if (x + y) % 3 == 0 else pick(GOLD)), None


def leather(face, x, y, w, h):
    if face in ("west", "east", "front", "back") and y in (0, h - 1) and h > 2:
        return LEATHER_D, None
    return pick(LEATHER), None


def strap(face, x, y, w, h):
    """Correa de cuero con remaches de bronce cada tres píxeles."""
    if (x + y) % 4 == 0 and face not in ("top", "bottom"):
        return GOLD[0], None
    return pick(LEATHER), None


def blanket(face, x, y, w, h):
    """Manta tejida azul con la orla en sarga roja y oro, como los tejidos de Oseberg."""
    if face in ("west", "east", "front", "back"):
        if y == h - 1:
            return GOLD_D, None
        if y == h - 2:
            return (GOLD[0] if x % 2 == 0 else WOOL_RED), None
        if y == h - 3:
            return WOOL_RED, None
    if face == "top" and (x in (0, w - 1) or y in (0, h - 1)):
        return WOOL_RED, None
    return (WOOL[1] if (x + y) % 3 == 0 else WOOL[0]), None


def head_paint(face, x, y, w, h):
    """Cráneo: ojos a los costados, oscuros con un brillo de luz, y el gris más oscuro de la cara."""
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y == 2 and fx in (1, 2):
            return EYE, None
        if y == 1 and fx == 1:
            return C("#e8eef8"), None
        if y == 3 and fx == 2:
            return C("#3a3c46"), None
    c, g = coat(face, x, y, w, h)
    return mix(c, SHADE, 0.25), g


def jowl(face, x, y, w, h):
    c, g = coat(face, x, y, w, h)
    return mix(c, SHADE, 0.3), g


def muzzle(face, x, y, w, h):
    """Hocico pizarra con ollares y la fila de dientes con runas (Sigrdrífumál 15: runas en los dientes de Sleipnir)."""
    if face == "front":
        if y == h - 1:
            rune = x % 2 == 0
            return (RUNE if rune else IVORY), (RUNE if rune else None)
        if y == 1 and x in (0, w - 1):
            return C("#1c1c24"), None
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y == h - 1 and fx <= 3:
            rune = fx % 2 == 1
            return (RUNE if rune else IVORY), (RUNE if rune else None)
        if y == 1 and fx == 0:
            return C("#1c1c24"), None
    return pick(MUZ), None


def chin(face, x, y, w, h):
    return pick(MUZ), None


def ear(face, x, y, w, h):
    if face in ("front",) and 0 < x < w - 1 and y > 0:
        return C("#3a3e4a"), None
    return mix(pick(COAT), SHADE, 0.3), None


def rune_plate(face, x, y, w, h):
    """Aplique de la frente: placa de oro con una runa ᛏ que brilla en la cara de arriba."""
    if face == "top":
        g = RUNES[0]
        rx, ry = x - (w - 3) // 2, y - (h - 5) // 2
        if 0 <= ry < 5 and 0 <= rx < 3 and g[ry][rx] == "X":
            return RUNE, RUNE
        return (GOLD_D if x in (0, w - 1) or y in (0, h - 1) else pick(GOLD)), None
    return gold(face, x, y, w, h)


def pendant(face, x, y, w, h):
    c = GOLD_L if y == 0 else pick(GOLD)
    return c, None


def rune_pendant(i):
    """Colgante del pretal con una runa distinta que brilla de frente."""
    g = RUNES[i % len(RUNES)]

    def f(face, x, y, w, h):
        if face == "front":
            rx, ry = x - (w - 3) // 2, y - (h - 5) // 2
            if 0 <= ry < 5 and 0 <= rx < 3 and g[ry][rx] == "X":
                return RUNE, RUNE
            return (GOLD_D if x in (0, w - 1) or y in (0, h - 1) else pick(GOLD)), None
        return gold(face, x, y, w, h)
    return f


def ring(face, x, y, w, h):
    return (GOLD[0] if y == 0 else GOLD_D), None


# ---------------------------------------------------------------- modelo
def leg(m, name, x, z, hind, side):
    """Pata de dos tramos: antebrazo (o muslo) con codo, y la parte «*_lower» con rodilla (o corvejón), caña, menudillo
    y casco. El pivote de arriba es el mismo y = 12 para todas; el casco apoya en y = 24."""
    sx = 1 if side == "l" else -1
    up = m.part(name, None, [x, 12, z], [0, 0, 0], [
        cube(-2, -2, -2, 4, 7, 4, leg_upper),             # antebrazo / muslo (y 10..17 del mundo)
    ])
    lo_cubes = [
        cube(-1.5, -0.5, -1.5, 3, 2, 3, leg_upper),       # rodilla / corvejón
        cube(-1.5, 1.5, -1, 3, 3, 2, leg_lower),          # caña
        cube(-1.5, 4.5, -1.5, 3, 1, 3, leg_lower),        # menudillo
        cube(-2, 5, -2.6, 4, 2, 4, hoof),                 # casco (apoya en y = 24)
    ]
    if hind:
        lo_cubes.append(cube(-1, -1, 1, 2, 3, 2, leg_upper))   # punta del corvejón, hacia atrás
    m.part(name + "_lower", up, [0, 5, 0], [0, 0, 0], lo_cubes)
    return up


def build():
    m = Model()
    body = m.part("body", None, [0, 8, 0], [0, 0, 0], [
        cube(-5, -5, -12, 10, 10, 24, coat),               # tonel (y 3..13, z -12..12)
        cube(-5.5, -3.5, -14, 11, 8, 6, coat),             # pecho y paletas
        cube(-5.5, -5.5, 4, 11, 9, 9, coat),               # grupa y ancas (más alta: caballo de tiro)
        cube(-4, -6.5, 5, 8, 1, 7, coat),                  # lomo redondeado de la grupa
        cube(-4.5, 4.5, -8, 9, 1, 12, coat),               # vientre
        cube(-2, -6.5, -12, 4, 2, 5, coat),                # cruz
    ])
    # Silla vikinga: manta tejida, asiento de cuero, perilla y borrén de madera con bronce, cincha y estribos.
    m.part("saddle", body, [0, 0, 0], [0, 0, 0], [
        cube(-5.5, -5.3, -7, 11, 4, 10, blanket),
        cube(-3.5, -6.3, -6, 7, 1, 8, leather),
        cube(-2, -8.3, -6.8, 4, 2, 1, gold),               # perilla
        cube(-3, -8.3, 1.5, 6, 2, 1, leather),             # borrén
        cube(-2.5, -9.3, 1.5, 5, 1, 1, gold),
        cube(-5.4, -1.3, -3.5, 11, 7, 2, strap),           # cincha
    ])
    for side, sx in (("l", 1), ("r", -1)):
        m.part(f"stirrup_{side}", body, [sx * 5.7, -2, -2.5], [0, 0, 0], [
            cube(0, 0, 0, 0, 8, 1, strap),
            cube(-0.5, 8, -1, 1, 1, 3, gold),
        ])
    # Pretal con colgantes de bronce dorado (como los de los ajuares de Borre y Gokstad), cada uno con una runa.
    m.part("breast_collar", body, [0, 0, 0], [0, 0, 0],
           [cube(-6, -1, -14.5, 12, 2, 1, strap)]
           + [cube(-1.5, 1, -14.6, 3, 5, 1, rune_pendant(0))]
           + [cube(px - 1, 1, -14.6, 2, 2, 1, pendant) for px in (-3, 4)])

    # Cuello y cabeza: «head_parts» como en el caballo vanilla (el juego lo baja al pastar y lo mueve al andar).
    hp = m.part("head_parts", None, [0, 4, -11], [math.pi / 6, 0, 0], [
        cube(-2.5, -7, -2, 5, 13, 8, neck_coat),           # cuello largo y ancho
        cube(-3, 0.5, -2.5, 6, 6, 9, neck_coat),             # base del cuello, más gruesa
    ])
    hd = m.part("head", hp, [0, 0, 0], [0, 0, 0], [
        cube(-3, -11.5, -2.5, 6, 6, 7, head_paint),        # cráneo
        cube(-3.5, -8.2, 1, 7, 3, 4, jowl),          # quijadas anchas
        # cabezada: testera, ahogadero y muserola de cuero con apliques dorados
        cube(-3.5, -12.2, 0, 7, 7, 1, strap),
        cube(-2.5, -11.4, -6, 5, 5, 1, strap),
        cube(-1.5, -12, -4.5, 3, 1, 5, rune_plate),
        cube(-3, -8, -6.5, 1, 1, 1, ring), cube(2, -8, -6.5, 1, 1, 1, ring),   # anillas del bocado
        cube(-0.5, -12.6, -1.2, 1, 2, 2, braid),           # copete trenzado
    ])
    m.part("upper_mouth", hp, [0, 0, 0], [0, 0, 0], [
        cube(-2, -11, -7.5, 4, 4, 6, muzzle),               # hocico (el juego lo abre al relinchar)
        cube(-1.5, -7.5, -7, 3, 1, 4, chin),               # belfo de abajo
    ])
    for side, sx in (("left_ear", 1), ("right_ear", -1)):
        e = m.part(side, hd, [sx * 1.75, -11.5, 3], [0.2, 0, sx * 0.2], [cube(-1, -3, -0.5, 2, 3, 1, ear)])
        m.part(side + "_tip", e, [0, -3, 0], [0, 0, 0], [cube(-0.5, -1, -0.5, 1, 1, 1, ear)])
    # Crin: cresta en mechones que alternan ancho (trenzas atadas con anillas de oro) y una melena que cae a la
    # derecha, como la de los caballos islandeses.
    mane_cubes = []
    for i in range(6):
        wide = i % 2 == 0
        y0 = -12.5 + i * 3
        mane_cubes.append(cube(-1.5 if wide else -1, y0, 5.5, 3 if wide else 2, 3, 2 if wide else 3, mane))
        if not wide:
            mane_cubes.append(cube(-1.5, y0 + 1, 6, 3, 1, 2, ring))
    mane_cubes.append(cube(-2.6, -10.5, 0.5, 0, 18, 6, mane_fall))
    mane_cubes.append(cube(2.6, -10.5, 4.5, 0, 9, 2, mane_fall))          # las puntas que asoman del otro lado
    m.part("mane", hp, [0, 0, 0], [0, 0, 0], mane_cubes)

    # Ocho patas con los nombres que ya anima SleipnirModel; las del medio, un poco más abiertas para que de frente se
    # vean las ocho.
    for name, x, z, hind in (("left_front_leg", 2.3, -10.8, False), ("right_front_leg", -2.3, -10.8, False),
                             ("left_front_middle_leg", 3.1, -5.8, False), ("right_front_middle_leg", -3.1, -5.8, False),
                             ("left_hind_middle_leg", 3.1, 4.5, True), ("right_hind_middle_leg", -3.1, 4.5, True),
                             ("left_hind_leg", 2.3, 9.5, True), ("right_hind_leg", -2.3, 9.5, True)):
        leg(m, name, x, z, hind, "l" if x > 0 else "r")

    # Cola larga en dos tramos (tail y tail_tip, que ya se mecen) con una trenza atada en oro.
    tail = m.part("tail", body, [0, -5, 12.5], [math.pi / 6, 0, 0], [
        cube(-1.5, -0.5, -1.5, 3, 5, 3, tail_hair),
        cube(-2, 3, -2, 4, 1, 4, ring),
    ])
    m.part("tail_tip", tail, [0, 4.5, 0], [-0.25, 0, 0], [
        cube(-2, 0, -1.5, 4, 9, 3, tail_hair),
        cube(-2.5, 7, -2, 5, 6, 4, tail_hair),
        cube(-1, 12.5, -1, 2, 2, 2, braid),
    ])
    return m


NOTES = """Hoy: el caballo vanilla con 8 patas rectas de un solo cubo, crin de una tabla y bardas de hierro.
Cuerpo con forma: pecho y paletas, cruz, vientre, grupa alta y redondeada; un 25 % más grande que un caballo (escala 1,25).
Ocho patas de dos tramos (antebrazo, rodilla o corvejón, caña, menudillo y casco negro con herradura); las del medio más abiertas, para que de frente se cuenten las ocho.
Cabeza con cráneo, quijadas, hocico y belfo; ojos con punto de luz, orejas de dos piezas y copete trenzado.
Crin en mechones trenzados con anillas de oro y melena que cae a un lado; cola larga con trenza en la punta.
Silla vikinga (manta azul de orla roja, perilla y borrén, cincha, estribos) y pretal de cuero con colgantes de bronce y uno central con una runa.
Fiel al mito: gris (grár), ocho patas, el mejor de los caballos; runas en sus dientes (Sigrdrífumál 15), que brillan.
Licencia de juego: la silla, el pretal, la placa de la frente con la runa ᛏ y que las runas brillen (no se describen arreos de Sleipnir).
Cultura pop: nada.
Animación propuesta: las ocho patas con el paso que ya tiene y la caña doblando en la rodilla al levantar el pie; crin y cola se mecen; al galopar las patas del medio van desfasadas, como un ciempiés de cascos."""
