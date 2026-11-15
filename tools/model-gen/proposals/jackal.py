"""Propuesta: chacal del anillo, como el de Anubis echado sobre su santuario o el Wepwawet de los estandartes: negro de
pies a cabeza, delgado y alto, cuello largo, hocico fino, orejas enormes en punta, cola tupida y caída, y al cuello el
usekh (collar ancho de cuentas de oro, lapislázuli, turquesa y cornalina). Ojos dorados que brillan, con la raya negra del
kohl como en las pinturas."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from china_entities import C, Model, cube, front_x, mix, pick  # noqa: E402

TEX_W = 64
SCALE = 1.0
ROOT_OFFSET = (0, 0, 0)
SEED = 5

FUR = [C("#1a161c"), C("#201b22"), C("#161318"), C("#251f28")]
FUR_HI = C("#3a3345")      # brillo azulado del pelo negro
FUR_SH = C("#0c0a0d")
NOSE = C("#050405")
GOLD_L, GOLD, GOLD_D = C("#ffe478"), C("#e8b634"), C("#a06c18")
LAPIS, LAPIS_L = C("#2a48ac"), C("#5078dc")
TURQ = C("#2aaa9c")
CARNELIAN = C("#b8402a")
EYE, EYE_CORE = C("#f0b020"), C("#fff0a0")
INNER_EAR = C("#a87e30")
SASH = [C("#a8261e"), C("#b83024")]


def h(*v):
    s = 0
    for i, a in enumerate(v):
        s = (s * 131 + int(a) * (i + 7) + 17) % 9973
    return (s * 2654435761 % 1000) / 1000.0


def fur(face, x, y, w, hh):
    """Pelo negro, corto y liso, con vetas de brillo azulado a lo largo (del lomo hacia abajo)."""
    if face == "bottom":
        return FUR_SH, None
    if face == "top":
        return (FUR_HI if h(x, 1) < 0.15 else pick(FUR)), None
    if h(x, y // 2, 3) < 0.1:
        return FUR_HI, None
    return (FUR_SH if y == hh - 1 and hh > 2 else pick(FUR)), None


def skull(face, x, y, w, hh):
    """Cráneo: ojos dorados que brillan en los costados, alargados hacia el frente con la raya del kohl."""
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y == 1 and fx in (0, 1):
            c = EYE_CORE if fx == 1 else EYE
            return c, c
        if y == 2 and fx in (0, 1, 2):
            return NOSE, None          # raya del kohl bajo el ojo
    if face == "front" and y == 1 and x in (0, w - 1):
        return EYE, EYE
    return fur(face, x, y, w, hh)


def snout(face, x, y, w, hh):
    if face == "front":
        return (NOSE if y == 0 else FUR_SH), None
    if face in ("west", "east") and y == hh - 1:
        return FUR_SH, None
    return fur(face, x, y, w, hh)


def jaw(face, x, y, w, hh):
    if face == "top":
        return C("#5a1418"), None
    return fur(face, x, y, w, hh)


def nose(face, x, y, w, hh):
    return NOSE, None


def ear(face, x, y, w, hh):
    """Oreja en punta: triángulo; por delante, el interior dorado."""
    half = (w - 1) / 2
    inside = abs(x - half) <= (y + 0.6) * (w / 2) / hh
    if face in ("front", "back"):
        if not inside:
            return None, None
        if face == "front" and abs(x - half) < 0.6 and 0 < y < hh - 1:
            return INNER_EAR, None
        return pick(FUR), None
    if face == "top":
        return None, None
    if face in ("west", "east") and y < hh * 0.5:
        return None, None
    return pick(FUR), None


BANDS = [GOLD_L, LAPIS, GOLD, TURQ, GOLD, CARNELIAN, GOLD_D]


def usekh(face, x, y, w, hh, first=0):
    """Collar ancho: hileras de cuentas (oro, lapislázuli, oro, turquesa, cornalina) y gotas de oro en el borde."""
    if face in ("front", "back", "west", "east"):
        c = BANDS[min(y + first, len(BANDS) - 1)]
        if c in (LAPIS, TURQ, CARNELIAN) and x % 2 == 0:
            c = mix(c, C("#ffffff"), 0.18)
        if y == hh - 1 and x % 2 == 1:
            return GOLD_L, None
        return c, None
    if face == "top":
        return (GOLD if (x + y) % 2 else LAPIS), None
    return GOLD_D, None


def usekh_low(face, x, y, w, hh):
    return usekh(face, x, y, w, hh, first=3)


def sash(face, x, y, w, hh):
    if face in ("front", "back") and y == hh - 1 and x % 2 == 1:
        return None, None
    return pick(SASH), None


def tail_fur(face, x, y, w, hh):
    """Cola tupida: mechones con puntas de brillo; la punta de la cola es más oscura."""
    if (x + y) % 4 == 0:
        return FUR_HI, None
    return pick(FUR), None


def paw(face, x, y, w, hh):
    if face == "front" and y == hh - 1:
        return C("#3a3430"), None   # uñas
    return fur(face, x, y, w, hh)


def build():
    m = Model()
    m.part("body", None, [0, 11, 0], [0, 0, 0], [
        cube(-2.5, -3, -7, 5, 7, 6, fur),        # pecho hondo
        cube(-2, -2.5, -1.5, 4, 4, 7, fur),      # cintura recogida
        cube(-2.5, -2.8, 4.5, 5, 5, 4, fur),     # cadera
    ])
    # Cuello largo que sube; el usekh y la cinta roja lo rodean.
    m.part("neck", "body", [0, -2, -6], [-0.6, 0, 0], [cube(-1.5, -1.5, -5.5, 3, 3, 6, fur)])
    m.part("sash", "neck", [0, 0, -1.5], [0, 0, 0], [
        cube(-2, -2, -1, 4, 4, 1, sash),            # cinta roja al cuello
    ])
    # Usekh: collar ancho que cubre los hombros y el pecho.
    m.part("collar", "body", [0, 0, 0], [0, 0, 0], [
        cube(-3, -3.5, -7.5, 6, 3, 3, usekh),      # sobre los hombros
        cube(-2, -0.5, -7.6, 4, 3, 1, usekh_low),     # el peto que cae sobre el pecho
    ])
    m.part("head", None, [0, 6.2, -10.2], [0, 0, 0], [
        cube(-2, -2.5, -3, 4, 4, 4, skull),
        cube(-1, -0.5, -8, 2, 2, 5, snout),
        cube(-1.5, -1.2, -4, 3, 3, 1, fur),      # base del hocico (de la frente al morro)
        cube(-0.5, -0.8, -8.4, 1, 1, 1, nose),
    ])
    m.part("jaw", "head", [0, 1.5, -3.5], [0.06, 0, 0], [cube(-1, 0, -4, 2, 1, 4, jaw)])
    for name, sx in (("left_ear", 1), ("right_ear", -1)):
        m.part(name, "head", [sx * 1.2, -2.5, -0.5], [-0.1, 0, sx * 0.12], [cube(-1.5, -6, -0.5, 3, 6, 1, ear)])
    # Cola tupida en tres tramos, caída hacia atrás.
    m.part("tail", None, [0, 9, 8.2], [0.55, 0, 0], [cube(-1, 0, -1, 2, 3, 2, tail_fur)])
    m.part("tail_mid", "tail", [0, 2.7, 0], [0.15, 0, 0], [cube(-1.5, 0, -1.5, 3, 4, 3, tail_fur)])
    m.part("tail_tip", "tail_mid", [0, 3.8, 0], [0.1, 0, 0], [cube(-1, 0, -1, 2, 3, 2, tail_fur)])
    # Patas: dos tramos adelante; atrás, muslo, pierna y metatarso (el corvejón alto de los cánidos).
    for name, sx in (("left_front_leg", 1), ("right_front_leg", -1)):
        m.part(name, None, [sx * 1.4, 14, -4.5], [0, 0, 0], [cube(-1, -1, -1, 2, 6, 2, fur)])
        m.part(name + "_lower", name, [0, 5, 0], [0, 0, 0], [
            cube(-1, 0, -1, 2, 4, 2, fur), cube(-1, 4, -2, 2, 1, 3, paw)])
    for name, sx in (("left_hind_leg", 1), ("right_hind_leg", -1)):
        m.part(name, None, [sx * 1.5, 12.88, 6.2], [-0.25, 0, 0], [cube(-1.5, -2, -2, 3, 6, 4, fur)])
        m.part(name + "_lower", name, [0, 3.8, 0.4], [0.75, 0, 0], [cube(-1, 0, -1, 2, 4, 2, fur)])
        m.part(name + "_foot", name + "_lower", [0, 3.8, 0], [-0.5, 0, 0], [
            cube(-1, 0, -1, 2, 3, 2, fur), cube(-1, 3, -2, 2, 1, 3, paw)])
    return m


NOTES = "\n".join([
    "Deja de ser un lobo recoloreado: chacal alto y fino, de pecho hondo y cintura recogida; de 1,2 a 1,65 bloques de alto con las orejas (11 → 31 cubos).",
    "Cuello largo que sube; cabeza chica con hocico fino y largo, nariz y quijada; orejas enormes en punta (6 px) con el interior dorado.",
    "Patas de dos tramos adelante y tres atrás (muslo, pierna y corvejón alto), con zarpas y uñas.",
    "Cola tupida de tres tramos, caída como la de los chacales sentados de las estatuas.",
    "Usekh al cuello: hileras de cuentas de oro, lapislázuli, turquesa y cornalina, con la cinta roja de Anubis.",
    "Ojos dorados que brillan, con la raya negra del kohl; pelo negro con vetas de brillo azulado.",
    "Fiel al mito: negro como Anubis (color del limo fértil y del renacer), orejas largas y cola caída del sab egipcio; usekh y cinta roja como el Anubis del santuario de Tutankamón.",
    "Licencia de juego: ojos que brillan.",
    "Cultura pop: nada.",
    "Animación propuesta: trote con las patas en diagonal y el corvejón que se dobla, orejas que giran hacia el ruido, cola que se mece al andar y se levanta al atacar, quijada que se abre al morder.",
])
