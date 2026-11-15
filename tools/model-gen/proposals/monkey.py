"""Propuesta del mono guerrero (los clones que Sun Wukong saca de sus pelos): cuerpo de mono encorvado, brazos largos y
piernas cortas, cara de mono con el corazón de piel y los ojos de fuego dorados, gorro púrpura de alas de fénix con dos
plumas de faisán largas, diadema de oro, cota de oro, faldellín de tigre, botas de nube y el bastón de hierro negro con
un aro de oro en cada punta, grueso y largo."""
import math
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from china_entities import C, Model, cube, front_x, mix, pick  # noqa: E402

TEX_W = 128
SCALE = 1.0
ROOT_OFFSET = (0, 0, 0)
SEED = 23

FUR = [C("#b8782f"), C("#a96a26"), C("#c4863a"), C("#b07028")]
FUR_L = C("#d9a35a")
FUR_D = C("#6e4214")
SKIN = [C("#eec4a0"), C("#e8b892"), C("#f2cba8")]
SKIN_D = C("#c08a68")
GOLD = [C("#f5c842"), C("#f0bd35"), C("#f8d055")]
GOLD_L, GOLD_D = C("#fff3a8"), C("#a86e14")
PURPLE = [C("#7a3fa0"), C("#6a338e"), C("#8a4cb0")]
PURPLE_D = C("#45215e")
RED, RED_D = C("#c42a20"), C("#7c1812")
IRON = [C("#2b2932"), C("#34313c"), C("#26242c")]
IRON_L = C("#56525f")


def noise(c, amt=8):
    k = random.randint(-amt, amt)
    return tuple(max(0, min(255, v + k)) for v in c)


def fur(face, x, y, w, h):
    """Pelo dorado: mechones verticales, más oscuro abajo."""
    if face == "bottom":
        return FUR_D, None
    if face == "top":
        return noise(FUR_L if (x + y) % 3 == 0 else pick(FUR)), None
    strand = (x * 7 + (y // 2) * 3) % 5
    if strand == 0:
        return mix(pick(FUR), FUR_D, 0.4), None
    if strand == 3 and y % 3 == 0:
        return FUR_L, None
    return noise(pick(FUR), 5), None


def gold(face, x, y, w, h):
    if face == "top":
        return GOLD_L, None
    if face == "bottom" or (y == h - 1 and h > 1):
        return GOLD_D, None
    return (GOLD_L if y == 0 else noise(pick(GOLD), 6)), None


def mail(face, x, y, w, h):
    """Cota de oro en escamas (suozi huangjin jia): escamas en tresbolillo con el borde oscuro."""
    if face == "bottom":
        return FUR_D, None
    if face == "top":
        return gold(face, x, y, w, h)
    row = y // 2
    cx = (x + row % 2) % 2
    if y % 2 == 1 and cx == 0:
        return GOLD_D, None
    if y % 2 == 0 and cx == 1:
        return GOLD_L, None
    return noise(pick(GOLD), 6), None


def chest(face, x, y, w, h):
    """Pecho: cota de oro con el espejo protector del corazón (huxin jing) al frente."""
    if face == "front":
        cx, cy = (w - 1) / 2, h / 2
        d = math.hypot(x - cx, (y - cy) * 1.1)
        if d < 1.3:
            return C("#fffbe0"), None
        if d < 2.2:
            return C("#f3d27a"), None
        if d < 2.9:
            return GOLD_D, None
    return mail(face, x, y, w, h)


def belt(face, x, y, w, h):
    """Faja roja de seda con la hebilla de oro al frente."""
    if face == "front" and abs(x - (w - 1) / 2) < 1.2:
        return (GOLD_L if y == 0 else GOLD[0]), None
    if face in ("top", "bottom"):
        return RED_D, None
    return (RED if y == 0 else noise(mix(RED, RED_D, 0.4), 6)), None


def tiger(face, x, y, w, h):
    """Faldellín de piel de tigre (hupi qun): naranja con rayas negras en punta, borde de abajo dentado y claro."""
    if face == "top":
        return None, None
    if face == "bottom":
        return C("#3a2414"), None
    if y == h - 1:
        return (C("#f3e2c8") if x % 2 == 0 else None), None
    if y == h - 2 and x % 4 == 1:
        return C("#f3e2c8"), None
    stripe = math.sin(x * 1.25 + y * 0.45) > 0.7 and y < h - 2
    if stripe:
        return C("#1f1410"), None
    return noise(pick([C("#e88a2a"), C("#f09a36"), C("#dc7c22")]), 6), None


def leg(face, x, y, w, h):
    """Pierna corta: pelo arriba, la caña de oro y la bota de seda azul noche con nubes blancas."""
    if face == "bottom":
        return C("#1a1f33"), None
    if face == "top":
        return FUR_D, None
    if y < 3:
        return fur(face, x, y, w, h)
    if y == 3:
        return GOLD[0], None
    cloud = (y == h - 2 and x % 4 != 0) or (y == h - 3 and x % 4 in (1, 2)) or (y == 5 and x % 4 == 3)
    return (C("#f2f4fa") if cloud else noise(pick([C("#2a3150"), C("#252b47")]), 4)), None


def toe(face, x, y, w, h):
    if face == "bottom":
        return C("#1a1f33"), None
    return (GOLD[0] if face == "top" else noise(C("#2a3150"), 4)), None


def cloud(face, x, y, w, h):
    """Nube que acompaña al pie: blanca con la sombra celeste abajo."""
    if face == "bottom" or y == h - 1:
        return C("#b8c8e8"), None
    return (C("#ffffff") if (x + y) % 3 else C("#e4ecfa")), None


def arm(face, x, y, w, h):
    return fur(face, x, y, w, h)


def pauldron(face, x, y, w, h):
    """Hombrera de oro en láminas superpuestas."""
    if face == "top":
        return GOLD_L, None
    if face == "bottom":
        return GOLD_D, None
    return (GOLD_D if y % 2 == 1 else noise(pick(GOLD), 6)), None


def bracer(face, x, y, w, h):
    if face in ("top", "bottom"):
        return GOLD_D, None
    return (RED if y == 1 else noise(pick(GOLD), 6)), None


def hand(face, x, y, w, h):
    """Mano de mono: dedos de piel oscura con los nudillos de pelo."""
    if face == "top":
        return FUR_D, None
    if y == 0:
        return noise(pick(FUR), 5), None
    return (SKIN_D if (x + y) % 3 == 0 else mix(pick(SKIN), SKIN_D, 0.3)), None


def head(face, x, y, w, h):
    """Cabeza: pelo dorado; al frente, la cara de piel en forma de corazón, cejas oscuras y los ojos de fuego dorados
    (huoyan jinjing) que brillan."""
    if face == "front":
        if y == 3 and x in (1, 2, w - 3, w - 2):
            g = C("#ffd23a") if x in (2, w - 3) else C("#ff7a1e")
            return g, g
        if y == 2 and 1 <= x <= w - 2:
            return FUR_D, None   # cejas
        cx = (w - 1) / 2
        lobe = min(abs(x - (cx - 1.6)), abs(x - (cx + 1.6)))
        heart = (y == 2 and abs(x - cx) < 0.6) or (y >= 3 and (lobe < 2.0 if y <= 4 else abs(x - cx) < 3.4 - (y - 4) * 0.9))
        if heart:
            return noise(pick(SKIN), 4), None
        return fur(face, x, y, w, h)
    return fur(face, x, y, w, h)


def muzzle(face, x, y, w, h):
    """Hocico de «pico de dios del trueno» (leigong zui): piel, nariz chata con dos orificios y la boca."""
    if face == "front":
        if y == 0 and x in (w // 2 - 1, w // 2):
            return C("#5a3a22"), None
        if y == h - 1:
            return (C("#7a3a2a") if 0 < x < w - 1 else SKIN_D), None
    if face == "bottom":
        return SKIN_D, None
    return noise(pick(SKIN), 4), None


def ear(face, x, y, w, h):
    if face in ("west", "east"):
        return (C("#d8988a") if 0 < x < w - 1 and 0 < y < h - 1 else pick(SKIN)), None
    return pick(SKIN), None


def crown(face, x, y, w, h):
    """Gorro púrpura y oro (zijin guan) con el filo dorado arriba y abajo."""
    if face == "top":
        return (GOLD_L if (x + y) % 3 == 0 else pick(GOLD)), None
    if y == 0 or y == h - 1:
        return pick(GOLD), None
    if face == "front" and abs(x - (w - 1) / 2) < 1:
        return GOLD[0], None
    return noise(pick(PURPLE), 6), None


def jewel(face, x, y, w, h):
    if face == "front":
        return C("#d8262a"), None
    return pick(GOLD), None


def wing(face, x, y, w, h):
    """Ala de fénix del gorro: plumas de oro con el centro púrpura, recortadas en puntas hacia atrás y arriba."""
    if face not in ("west", "east"):
        return None, None
    fx = front_x(face, x, w)
    up = h - 1 - y
    k = fx % 2
    lim = 1 + fx * (h - 1) / max(1, w - 1)
    if up > lim - k * 0.6:
        return None, None
    if up >= lim - 1.4:
        return GOLD_L, None
    return (pick(PURPLE) if up < lim * 0.5 else pick(GOLD)), None


def feather(face, x, y, w, h):
    """Pluma de faisán (lingzi): franjas pardas y crema."""
    if face in ("top", "bottom"):
        return C("#7a4a24"), None
    return (C("#f0e2c0") if (y // 2) % 2 else C("#a2662e")), None


def feather_tip(face, x, y, w, h):
    if face in ("top", "bottom"):
        return C("#2a1a10"), None
    return (C("#3a2414") if y < 3 else (C("#f0e2c0") if (y // 2) % 2 else C("#a2662e"))), None


def iron(face, x, y, w, h):
    """Bastón de hierro negro (wutie): veta clara a lo largo."""
    if face in ("top", "bottom"):
        return IRON[0], None
    return (IRON_L if x == 0 and face in ("front", "west") else noise(pick(IRON), 4)), None


def staff_cap(face, x, y, w, h):
    """Aro de oro en la punta del bastón, con las líneas grabadas."""
    if face in ("top", "bottom"):
        return GOLD_D, None
    if y in (0, h - 1):
        return GOLD_D, None
    return (GOLD_L if y == 1 else noise(pick(GOLD), 6)), None


def build():
    random.seed(SEED)
    m = Model()
    # cuerpo encorvado: pivote en la cadera, el torso se inclina hacia adelante
    m.part("body", None, [0, 15, 0.5], [0.32, 0, 0], [
        cube(-4, -5, -2.5, 8, 5, 5, mail),          # cintura
        cube(-5, -10, -3, 10, 5, 6, chest),          # pecho ancho
        cube(-4.5, -2, -3, 9, 2, 6, belt),           # faja
    ])
    m.part("kilt", "body", [0, -0.5, 0], [-0.32, 0, 0], [
        cube(-5, 0, -3.5, 10, 5, 7, tiger),
        cube(-2, 4, -4, 4, 2, 1, tiger),             # faldón de adelante
    ])
    # cola larga que se enrosca hacia arriba
    t1 = m.part("tail", "body", [0, -1.5, 2.5], [-0.95, 0, 0], [cube(-1, -1, 0, 2, 2, 7, fur)])
    t2 = m.part("tail2", t1, [0, 0, 6.5], [0.9, 0, 0], [cube(-1, -1, 0, 2, 2, 6, fur)])
    m.part("tail3", t2, [0, 0, 5.5], [1.1, 0, 0], [cube(-0.5, -0.5, 0, 1, 1, 5, fur)])

    # cabeza baja y adelantada, sobre el pecho
    m.part("head", None, [0, 6.5, -3.5], [0, 0, 0], [
        cube(-4, -7, -4, 8, 7, 8, head),
        cube(-2.5, -3, -6, 5, 3, 2, muzzle),
        cube(-3.5, -5, -4.5, 7, 1, 1, fur),         # arco de las cejas
        cube(-5, -4, -2.5, 1, 4, 3, fur), cube(4, -4, -2.5, 1, 4, 3, fur),        # patillas
        cube(-5.5, -6, 0.5, 1, 2, 2, ear), cube(4.5, -6, 0.5, 1, 2, 2, ear),      # orejas
        # diadema de oro (jingu), con el remolino al frente
        cube(-4.5, -6, -4.5, 9, 1, 1, gold), cube(-4.5, -6, 3.5, 9, 1, 1, gold),
        cube(-4.5, -6, -3.5, 1, 1, 7, gold), cube(3.5, -6, -3.5, 1, 1, 7, gold),
        # gorro de púrpura y oro
        cube(-3.5, -9, -3.5, 7, 2, 7, crown), cube(-1.5, -11, -1.5, 3, 2, 3, gold),
        cube(-1, -10, -4.5, 2, 2, 1, jewel),
    ])
    for side, sx in (("r", -1), ("l", 1)):
        m.part(f"wing_{side}", "head", [sx * 3.5, -8.5, 0], [0, sx * -0.4, sx * 0.5],
               [cube(0, -7, -3, 0, 7, 9, wing)])
        f1 = m.part(f"feather_{side}", "head", [sx * 1.5, -10.5, -0.5], [-0.3, 0, sx * 0.2],
                    [cube(-0.5, -12, -0.5, 1, 12, 1, feather)])
        f2 = m.part(f"feather_{side}2", f1, [0, -12, 0], [-0.45, 0, sx * 0.12],
                    [cube(-0.5, -12, -0.5, 1, 12, 1, feather)])
        m.part(f"feather_{side}3", f2, [0, -12, 0], [-0.5, 0, sx * 0.1], [cube(-0.5, -9, -0.5, 1, 9, 1, feather_tip)])

    # brazos largos (hasta la rodilla), hombreras y brazales de oro
    for side, sx in (("r", -1), ("l", 1)):
        m.part(f"arm_{side}", None, [sx * 6.5, 7.5, -3], [0, 0, sx * -0.08], [
            cube(-1.5, -1, -1.5, 3, 12, 3, arm),
            cube(-2.5, -2, -2.5, 5, 3, 5, pauldron),
            cube(-2, 7, -2, 4, 3, 4, bracer),
            cube(-2, 10.5, -2, 4, 3, 4, hand),
        ])
    # bastón de hierro negro con un aro de oro en cada punta (en la mano derecha)
    m.part("staff", "arm_r", [0, 12, -0.5], [0.22, 0, 0], [
        cube(-1, -30, -1, 2, 33, 2, iron),
        cube(-1.5, -31, -1.5, 3, 4, 3, staff_cap),
        cube(-1.5, -0.5, -1.5, 3, 4, 3, staff_cap),
    ])
    # piernas cortas con botas de nube
    for side, sx in (("r", -1), ("l", 1)):
        m.part(f"leg_{side}", None, [sx * 2.5, 16, 0.5], [0, 0, 0], [
            cube(-2, 0, -2, 4, 8, 4, leg),
            cube(-1.5, 6, -3.5, 3, 2, 2, toe),
            cube(-1, 5, -4.5, 2, 1, 1, toe),              # punta respingada
            cube(2 if sx > 0 else -3, 6, -1.5, 1, 2, 3, cloud),   # nube por fuera
            cube(-1.5, 6, 2, 3, 2, 1, cloud),                      # nube atrás
        ])
    return m


NOTES = """Proporciones de mono: torso encorvado hacia adelante (pecho ancho de 10 px y cintura de 8), cabeza baja y adelantada, brazos largos hasta la rodilla y piernas cortas (8 px, hoy 10).
Cara de mono: el corazón de piel alrededor de los ojos y la boca, hocico saliente de «pico de dios del trueno», arco de cejas, patillas y orejas redondas; los ojos de fuego dorados siguen brillando.
Gorro púrpura y oro con joya roja, dos alas de fénix en plano y dos plumas de faisán de tres tramos (33 px, hoy 22) que se curvan hacia atrás.
Cota de escamas de oro con el espejo del corazón al pecho, hombreras de láminas, brazales con cinta roja, faja roja con hebilla y faldellín de tigre con faldón y borde dentado.
Bastón: 2 × 2 px y 33 de largo (hoy 1 × 1 × 30), hierro negro con un aro de oro de 3 × 3 × 4 en cada punta. Botas con la punta respingada y nubes blancas al costado y atrás. Cola de tres tramos que se enrosca hacia arriba.
Tamaño: 27 → 51 cubos; 1,2 × 3,2 × 1,8 bloques con plumas y bastón (hoy 1,0 × 2,9 × 1,2); el cuerpo mide casi lo mismo, pero es más ancho de hombros y la silueta (plumas, alas, bastón, cola) crece. Textura 128×128.
Fiel al mito: Viaje al Oeste (caps. 3 y 14) describe el gorro de púrpura y oro con alas de fénix, la cota de oro, las botas para andar sobre las nubes, el faldellín de piel de tigre, la diadema de oro, los ojos de fuego y el bastón «con un aro de oro en cada punta y hierro negro al medio»; las plumas de faisán vienen de la ópera de Pekín.
Licencia de juego: la diadema junto con el gorro (en la novela la diadema llega después), el espejo del pecho y la faja roja (de la iconografía, no del texto) y las nubes pegadas a las botas.
Cultura pop: nada (sin el bastón rojo ni la armadura de las películas o los videojuegos).
Animación propuesta: la de hoy (brazos y piernas al andar, plumas y cola que se mecen) más tail3 y feather_*3 con retraso, las alas del gorro que vibran al correr, el torso que se encorva más al atacar y el bastón que gira en la mano al golpear."""
