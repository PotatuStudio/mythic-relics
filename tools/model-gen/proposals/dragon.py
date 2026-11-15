"""Propuesta del long (dragón chino de la Perla del dragón): más grueso al medio y fino en la cola, cabeza de camello con
los «nueve parecidos» de Wang Fu bien a la vista, vientre de placas, cresta dentada, llamas en los codos, melena y barba
en varios planos y bigotes largos de tres tramos. Sin perla (pedido del usuario)."""
import math
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from china_entities import C, Model, cube, front_x, mix, pick  # noqa: E402

TEX_W = 256
SCALE = 1.3
ROOT_OFFSET = (0, 8.5, -16)
SEED = 11

# ---------------------------------------------------------------- colores
SC_TOP = C("#145068")      # lomo: azul verdoso profundo
SC_MID = C("#2a8f96")      # flanco
SC_LOW = C("#5fd0c0")      # flanco bajo, más claro
SC_DARK = C("#0c3446")
SC_RIM = C("#a6f0e0")
GOLD = [C("#f5c842"), C("#f0bd35"), C("#f8d055")]
GOLD_L, GOLD_D = C("#fff3a8"), C("#b0791a")
BELLY = [C("#f2d98a"), C("#ecd07a"), C("#f6e09a")]
BELLY_D = C("#b8903c")
FL_R, FL_O, FL_Y = C("#d8322a"), C("#ff8a2a"), C("#ffe14a")
BONE = [C("#e6d4a8"), C("#d8c49a"), C("#ecdcb4")]
BONE_D = C("#9c8460")


def noise(c, amt=10):
    k = random.randint(-amt, amt)
    return tuple(max(0, min(255, v + k)) for v in c)


def scale_cell(x, y, base):
    """Escama de carpa (arco con el borde claro abajo y la juntura oscura) en celdas de 4 x 3 en tresbolillo."""
    row = y // 3
    cx = (x + (row % 2) * 2) % 4
    r = y % 3
    if r == 2 and cx in (1, 2):
        return mix(base, SC_RIM, 0.55)
    if r == 2 or cx == 0:
        return mix(base, SC_DARK, 0.55)
    if r == 0 and cx in (1, 2):
        return mix(base, SC_DARK, 0.15)
    return noise(base, 6)


def body_scales(face, x, y, w, h):
    """Segmento del cuerpo: lomo oscuro con la línea dorada, flancos de escamas de carpa que aclaran hacia abajo y el
    borde del vientre crema; la cara de abajo es de placas (vientre de almeja)."""
    if face == "bottom":
        return (BELLY_D if y % 3 == 0 else noise(pick(BELLY), 5)), None
    if face == "top":
        mid = (w - 1) / 2
        if abs(x - mid) < 0.6 or (w % 2 == 0 and x in (w // 2 - 1, w // 2)):
            return (GOLD_L if y % 3 == 0 else GOLD_D), None
        side = abs(x - mid) / max(1, mid)
        return scale_cell(x, y, mix(SC_TOP, SC_MID, side * 0.6)), None
    if face in ("front", "back"):
        t = y / max(1, h - 1)
        if y >= h - 2:
            return pick(BELLY), None
        return noise(mix(SC_TOP, SC_MID, t), 6), None
    if y >= h - 2:
        return (BELLY_D if y == h - 1 else pick(BELLY)), None
    t = y / max(1, h - 3)
    base = mix(SC_TOP, SC_MID, min(1, t * 1.6)) if t < 0.6 else mix(SC_MID, SC_LOW, (t - 0.6) / 0.4)
    return scale_cell(x, y, base), None


def belly_plates(face, x, y, w, h):
    """Placas del vientre: bandas transversales crema con la juntura dorada oscura."""
    if face == "top":
        return None, None
    if face == "bottom":
        if y % 3 == 0:
            return BELLY_D, None
        return mix(pick(BELLY), C("#fff6d0"), 0.3 if abs(x - (w - 1) / 2) < w / 4 else 0), None
    if face in ("west", "east"):
        return (BELLY_D if x % 3 == 0 else pick(BELLY)), None
    return pick(BELLY), None


def fire_col(t):
    return FL_Y if t > 0.78 else FL_O if t > 0.42 else FL_R


def crest(face, x, y, w, h, tooth=4):
    """Cresta del lomo: dientes de sierra que se inclinan hacia atrás, rojo en la base y punta amarilla."""
    if face not in ("west", "east"):
        return None, None
    fx = front_x(face, x, w)
    k = fx % tooth
    lim = h * (1 - k / tooth) + 0.5
    depth = h - y
    if depth > lim:
        return None, None
    return fire_col(depth / h), None


def flame_back(face, x, y, w, h):
    """Llama que se abre hacia atrás (codos, mechones): lenguas de largo variable, con huecos."""
    if face not in ("west", "east"):
        return None, None
    fx = front_x(face, x, w)
    cy = (h - 1) / 2
    lane = (y * 0.618 + 0.3) % 1
    reach = w * (0.45 + 0.55 * lane) * (1 - 0.5 * abs(y - cy) / max(1, h / 2))
    if fx > reach:
        return None, None
    return fire_col(fx / max(1, reach)), None


def mane(face, x, y, w, h):
    """Melena: mechones largos hacia atrás, de largo distinto, con un hueco cada tres filas."""
    if face not in ("west", "east"):
        return None, None
    fx = front_x(face, x, w)
    lane = (y * 0.618 + 0.15) % 1
    reach = w * (0.35 + 0.65 * lane)
    if fx > reach or (y % 2 == 1 and fx > w * 0.15):
        return None, None
    t = fx / max(1, reach)
    return (FL_Y if t > 0.7 else FL_O if t > 0.25 else FL_R), None


def mane_top(face, x, y, w, h):
    """Melena del cogote, en el plano del medio: mechones que suben y se van hacia atrás."""
    if face not in ("west", "east"):
        return None, None
    fx = front_x(face, x, w)
    up = h - y
    reach = h * (0.55 + 0.45 * ((fx * 0.618) % 1)) * (1 - 0.4 * fx / w)
    if up > reach:
        return None, None
    return fire_col(up / max(1, reach)), None


def beard(face, x, y, w, h):
    """Barba: mechones que cuelgan, más largos atrás."""
    if face not in ("west", "east"):
        return None, None
    fx = front_x(face, x, w)
    reach = h * (0.45 + 0.55 * ((fx * 0.618) % 1)) * (0.6 + 0.4 * fx / w)
    if y > reach:
        return None, None
    return fire_col(y / max(1, reach)), None


def tail_flame(face, x, y, w, h):
    """Mechón de la cola en dos planos cruzados: llama ancha al medio que se abre en lenguas hacia atrás."""
    if face in ("west", "east"):
        fx, a, span = front_x(face, x, w), y, h
    elif face in ("top", "bottom"):
        fx, a, span = h - 1 - y, x, w   # en las caras de arriba/abajo, la y avanza a lo largo (z)
    else:
        return None, None
    c = (span - 1) / 2
    off = abs(a - c) / max(1, span / 2)
    lane = (a * 0.618 + 0.2) % 1
    reach = w if face in ("west", "east") else h
    lim = reach * (1 - off * 0.85) * (0.7 + 0.3 * lane)
    if fx > lim or ((fx + a) % 4 == 0 and fx > lim * 0.7):
        return None, None
    col = fire_col(fx / max(1, lim))
    return col, (col if fx > lim * 0.6 else None)


def head_scales(face, x, y, w, h):
    if face == "bottom":
        return pick(BELLY), None
    if face == "top":
        return scale_cell(x, y, mix(SC_TOP, SC_MID, 0.4)), None
    t = y / max(1, h - 1)
    return scale_cell(x, y, mix(SC_TOP, SC_MID, t)), None


def cranium(face, x, y, w, h):
    """Cráneo: escamas finas; en la frente, la marca dorada en forma de llama (hump del camello)."""
    if face == "front":
        if abs(x - (w - 1) / 2) < 1 and y < 3:
            return GOLD_L, None
        return noise(mix(SC_MID, SC_RIM, 0.15 + 0.1 * (y / h)), 8), None
    return head_scales(face, x, y, w, h)


def hump(face, x, y, w, h):
    """Frente abultada de camello, con la línea dorada al medio."""
    if face in ("top", "front") and abs(x - (w - 1) / 2) < 1:
        return pick(GOLD), None
    return head_scales(face, x, y, w, h)


def brow(face, x, y, w, h):
    """Cejas pobladas: pelo en llamas sobre los ojos."""
    if face == "bottom":
        return C("#7a1a14"), None
    return (FL_O if (x + y) % 3 else FL_R) if face != "top" else (FL_Y if x % 2 else FL_O), None


def eye(face, x, y, w, h):
    """Ojo de demonio saltón: amarillo fuego con la pupila rasgada roja oscura; brilla."""
    if face in ("bottom", "top", "back"):
        return SC_DARK, None
    if face == "front":
        c = C("#ffd23a") if y == 0 else C("#ff6a1e")
        return c, c
    fx = front_x(face, x, w)
    if fx == 1:
        return C("#3a0606"), C("#5a0a0a")
    c = C("#ffd23a") if y == 0 else C("#ff6a1e")
    return c, c


def snout(face, x, y, w, h):
    """Hocico largo de camello: escamas claras arriba, labio con los dientes en el borde de abajo."""
    if face == "front":
        if y == h - 1:
            return (C("#fbf7ea") if x % 2 == 0 else C("#7a1a14")), None
        return noise(mix(SC_MID, SC_RIM, 0.3), 6), None
    if face in ("west", "east"):
        if y == h - 1:
            return (C("#fbf7ea") if x % 2 == 0 else C("#7a1a14")), None
        if y == h - 2:
            return C("#c43a2e"), None   # labio rojo
    if face == "bottom":
        return C("#7a1a14"), None
    return head_scales(face, x, y, w, h)


def nose(face, x, y, w, h):
    """Nariz respingada, con los dos orificios grandes al frente."""
    if face == "front":
        if y == h - 1 and x in (1, w - 2):
            return C("#0a1e24"), None          # orificios
        if y == h - 1 or x in (0, w - 1):
            return mix(SC_MID, SC_RIM, 0.35), None
        return noise(mix(GOLD_D, GOLD[0], 0.5), 8), None   # almohadilla de la nariz
    if face == "top":
        return noise(mix(SC_MID, GOLD_L, 0.2), 6), None
    return noise(mix(SC_MID, SC_RIM, 0.35), 6), None


def jaw(face, x, y, w, h):
    if face == "top":
        return (C("#d84a5a") if abs(x - (w - 1) / 2) < 1.2 else C("#a8303a")), None
    if face in ("west", "east", "front") and y == 0:
        return (C("#fbf7ea") if x % 2 == 0 else C("#7a1a14")), None
    if face == "bottom":
        return (BELLY_D if y % 2 == 0 else pick(BELLY)), None
    return head_scales(face, x, y, w, h)


def fang(face, x, y, w, h):
    return (C("#fffaf0") if y < h - 1 else C("#e6dcc0")), None


def antler(face, x, y, w, h):
    """Cuerno de ciervo: hueso con anillos rugosos en la base y las puntas más claras."""
    if face == "top":
        return C("#fff6dc"), None
    if y == 0 and h > 1:
        return C("#fff6dc"), None
    if y >= h - 2 and h > 5:
        return (BONE_D if (x + y) % 2 else C("#b8a078")), None
    return noise(pick(BONE), 6), None


def ear(face, x, y, w, h):
    """Oreja de buey: por fuera escamas, por dentro rosada."""
    if face == "east" or face == "west":
        fx = x
        if 0 < y < h - 1 and 0 < fx < w - 1:
            return C("#d88a8a"), None
    return head_scales(face, x, y, w, h)


def whisker(face, x, y, w, h):
    return noise(pick(GOLD), 8), None


def leg_scales(face, x, y, w, h):
    if face == "bottom":
        return pick(BELLY), None
    if face == "top":
        return mix(SC_TOP, SC_MID, 0.5), None
    base = mix(SC_TOP, SC_MID, min(1, y / max(1, h - 1) + 0.3))
    if face in ("west", "east", "front", "back") and x == 0:
        return scale_cell(x + 1, y, base), None
    return scale_cell(x, y, base), None


def tiger_pad(face, x, y, w, h):
    """Pie de tigre: almohadilla gruesa, escamas arriba y la planta oscura."""
    if face == "bottom":
        return C("#3a2a22"), None
    if face == "top":
        return mix(SC_MID, SC_LOW, 0.3), None
    if y == h - 1:
        return C("#3a2a22"), None
    return noise(mix(SC_MID, SC_LOW, 0.3), 6), None


def talon(face, x, y, w, h):
    """Garra de águila: marfil, más oscura en la punta."""
    return noise(C("#f2ead2"), 6), None


def talon_tip(face, x, y, w, h):
    return C("#8a7a5a"), None


# ---------------------------------------------------------------- forma
# (ancho = alto de la sección, largo): crece hasta el pecho y se afina hacia la cola; tamaños vecinos distintos para que
# las juntas solapadas no tengan caras en el mismo plano.
SEGMENTS = [(9, 10), (12, 12), (13, 12), (14, 12), (13, 12), (11, 12), (9, 11), (7, 11), (5, 10), (3, 10)]
# Pose de reposo en S suave (la animación de hoy suma encima): el cuello sube hacia la cabeza, seg1 queda horizontal
# para el jinete y el resto ondula arriba-abajo y a los lados.
PITCH = [-0.12, 0.12, 0.12, 0.0, -0.18, -0.14, 0.1, 0.22, 0.2, 0.1]
YAW = [0.0, 0.0, 0.12, 0.08, -0.14, -0.18, 0.12, 0.22, 0.12, -0.2]


UPPER, SHIN, FOOT = 1.25, -0.55, -0.45


def build():
    random.seed(SEED)
    m = Model()
    parent = None
    for i, (s, length) in enumerate(SEGMENTS):
        name = f"seg{i}"
        off = [0, 0, 0] if parent is None else [0, 0, SEGMENTS[i - 1][1]]
        lap = 0 if i == 0 else 2 if s >= 9 else 1   # cada segmento entra en el anterior: sin rendijas al ondular
        cubes = [cube(-s / 2, -s / 2, -lap, s, s, length + lap, body_scales)]
        if s >= 5:
            bw = s - 3
            cubes.append(cube(-bw / 2, s / 2, 0.5, bw, 1, length - 1, belly_plates))
        m.part(name, parent, off, [PITCH[i], YAW[i], 0], cubes)
        crest_h = max(2, round(s * 0.45))
        m.part(f"crest{i}", name, [0, -s / 2, 0], [0, 0, 0],
               [cube(0, -crest_h, 0.5, 0, crest_h, length - 1, crest)])
        parent = name

    # cola: mechón de llamas en dos planos cruzados
    m.part("tuft", parent, [0, 0, SEGMENTS[-1][1] - 1], [0, 0, 0],
           [cube(0, -7, 0, 0, 14, 16, tail_flame), cube(-6, 0, 0, 12, 0, 14, tail_flame)])

    # patas: delanteras en seg1, traseras en seg4 (mismos nombres que hoy)
    for seg, z in (("seg1", 6), ("seg4", 6)):
        s = SEGMENTS[int(seg[3:])][0]
        for side, sx in (("r", -1), ("l", 1)):
            up = m.part(f"{seg}_leg_{side}", seg, [sx * (s / 2 - 0.5), s / 2 - 3, z], [UPPER, 0, sx * -0.6],
                        [cube(-2, -1, -2, 4, 6, 4, leg_scales)])
            lo = m.part(f"{seg}_shin_{side}", up, [0, 4.5, 0], [SHIN, 0, 0],
                        [cube(-1.5, 0, -1.5, 3, 5, 3, leg_scales)])
            # llamas del codo, hacia atrás
            m.part(f"{seg}_elbow_{side}", lo, [sx * 1.5, 0.5, 1], [0.6, sx * 0.5, 0], [cube(0, -2, 0, 0, 7, 9, flame_back)])
            foot = m.part(f"{seg}_foot_{side}", lo, [0, 5, 0], [FOOT, 0, 0],
                          [cube(-2.5, 0, -3, 5, 2, 4, tiger_pad)])
            # cuatro garras de águila: tres adelante y una atrás, cada una con su punta curva
            for cx in (-2, -0.5, 1):
                foot_claw = [cube(cx, 0.5, -5, 1, 1, 2, talon), cube(cx, 1.5, -5.5, 1, 1, 1, talon_tip)]
                m.part(f"{seg}_claw{int(cx * 2)}_{side}", foot, [0, 0, 0], [0, 0, 0], foot_claw)
            m.part(f"{seg}_spur_{side}", foot, [0, 0, 0], [0, 0, 0],
                   [cube(-0.5, 0.5, 1, 1, 1, 2, talon), cube(-0.5, 1.5, 2.5, 1, 1, 1, talon_tip)])

    # ---- cabeza de camello
    m.part("head", "seg0", [0, -1, 0], [0.05, 0, 0], [
        cube(-6, -7, -10, 12, 9, 10, cranium),        # cráneo
        cube(-4, -9, -9, 8, 2, 6, hump),               # frente abultada de camello
        cube(-4.5, -5, -19, 9, 6, 9, snout),           # hocico largo
        cube(-3.5, -7, -20, 7, 3, 4, nose),            # nariz respingada
        cube(-6.5, -8, -11, 4, 2, 5, brow), cube(2.5, -8, -11, 4, 2, 5, brow),   # cejas de llama
        cube(-6.5, -6, -10.5, 2, 2, 3, eye), cube(4.5, -6, -10.5, 2, 2, 3, eye),  # ojos de demonio
        cube(-4, 1, -18.5, 1, 2, 1, fang), cube(3, 1, -18.5, 1, 2, 1, fang),      # colmillos
        cube(-6.5, -3, -6, 1, 4, 5, head_scales), cube(5.5, -3, -6, 1, 4, 5, head_scales),  # mejillas
    ])
    m.part("jaw", "head", [0, 1, -10], [0.3, 0, 0], [
        cube(-4, 0, -8, 8, 2, 8, jaw),
        cube(-3.5, -1, -7.5, 1, 1, 1, fang), cube(2.5, -1, -7.5, 1, 1, 1, fang),
    ])
    m.part("beard", "jaw", [0, 2, -3], [0, 0, 0], [cube(0, 0, -6, 0, 12, 12, beard)])
    for side, sx in (("r", -1), ("l", 1)):
        m.part(f"beard_{side}", "jaw", [sx * 3, 2, -3], [0, sx * 0.35, 0], [cube(0, 0, -5, 0, 10, 10, beard)])
        # orejas de buey, abiertas hacia atrás
        m.part(f"ear_{side}", "head", [sx * 6, -6, -3], [0.2, sx * -0.7, sx * -0.5],
               [cube(-0.5, -1.5, 0, 1, 3, 5, ear)])
        # cuernos de ciervo: tronco grueso, rama alta hacia atrás y tres puntas
        a = m.part(f"antler_{side}", "head", [sx * 3, -8, -5], [-0.45, 0, sx * 0.35], [
            cube(-1, -6, -1, 2, 6, 2, antler),
            cube(-0.5, -4, -3.5, 1, 1, 3, antler),      # punta de adelante (frontal)
        ])
        a2 = m.part(f"antler2_{side}", a, [0, -6, 0], [-0.5, 0, sx * 0.25], [
            cube(-0.5, -7, -0.5, 1, 7, 1, antler),
            cube(-0.5, -3, -3, 1, 1, 3, antler),
            cube(-0.5, -6, 0.5, 1, 1, 3, antler),
        ])
        m.part(f"antler3_{side}", a2, [0, -7, 0], [0.6, 0, 0], [
            cube(-0.5, -3, -0.5, 1, 3, 1, antler), cube(-0.5, -2, -2.5, 1, 1, 2, antler),
        ])
        # bigotes largos de tres tramos, desde el labio de arriba
        w1 = m.part(f"whisker_{side}", "head", [sx * 4, -2, -17], [-0.2, sx * 0.85, 0],
                    [cube(-0.5, -0.5, 0, 1, 1, 9, whisker)])
        w2 = m.part(f"whisker2_{side}", w1, [0, 0, 9], [-0.55, 0, 0], [cube(-0.5, -0.5, 0, 1, 1, 9, whisker)])
        m.part(f"whisker3_{side}", w2, [0, 0, 9], [-0.6, sx * -0.3, 0], [cube(-0.5, -0.5, 0, 1, 1, 7, whisker)])
        # melena en dos planos por lado
        m.part(f"mane_{side}", "head", [sx * 6, -6, -5], [0.1, sx * 0.55, 0], [cube(0, 0, 0, 0, 11, 18, mane)])
        m.part(f"mane2_{side}", "head", [sx * 5, -8, -3], [-0.35, sx * 0.3, 0], [cube(0, 0, 0, 0, 8, 15, mane)])
    m.part("mane_top", "head", [0, -9, -4], [-0.15, 0, 0], [cube(0, -5, 0, 0, 6, 16, mane_top)])
    return m


NOTES = """Cuerpo: secciones de 9 a 14 px (hoy 3 a 11), más grueso en el pecho y afinado hasta 3 px en la cola; cada segmento entra 1-2 px en el anterior para que no se abran rendijas al ondular. Pose de reposo en S suave (seg1 queda horizontal para el jinete).
Vientre: una tira de placas crema bajo cada segmento; flancos de escamas de carpa que aclaran hacia abajo y lomo azul oscuro con la línea dorada; cresta dentada inclinada hacia atrás.
Cabeza de camello: cráneo de 12 px, frente abultada, hocico largo con nariz respingada, colmillos arriba y abajo, cejas de llama, ojos saltones que brillan, mejillas y orejas de buey (pieza nueva).
Cuernos de ciervo en tres tramos con cinco puntas, abiertos en V; bigotes de tres tramos (25 px); melena en dos planos por lado más una cresta en el cogote; barba en tres planos.
Patas con llamas en los codos, planta de tigre y cuatro garras de águila con la punta curva, recogidas hacia atrás como en vuelo. Mechón de la cola en dos planos cruzados con puntas que brillan.
Tamaño: escala 1,3 → 11,8 × 2,7 × 2,8 bloques (hoy 8,9 × 1,9 × 1,9); 74 → 124 cubos; textura 256×256.
ROOT_OFFSET pasa de (0, 16, -16) a (0, 8,5, -16): el jinete sigue sobre seg1, pero el cuerpo grueso y las patas obligan a subirlo; el punto de montura (hoy 0,78 del alto de 1 bloque) tendría que pasar a ~1,5 bloques y conviene agrandar la caja de choque.
Fiel al mito: los nueve parecidos de Wang Fu (cabeza de camello, cuernos de ciervo, ojos de demonio, cuello de serpiente, vientre de almeja, escamas de carpa, garras de águila, plantas de tigre, orejas de buey), sin alas, con bigotes y barba; cuatro garras como los long no imperiales.
Licencia de juego: melena, cejas, codos y cola en llamas (en la pintura china son mechones ondulantes, a menudo rojos); el azul verdoso (qinglong) y las puntas que brillan.
Cultura pop: nada. Sin perla.
Animación propuesta: la ondulación de hoy por segmento sumada a la pose en S; además, antler2/antler3 fijos, la melena (mane, mane2, mane_top) y los tres planos de la barba que se mecen con retraso, whisker3 que sigue a whisker2, las llamas de los codos que aletean y las garras que se abren al aterrizar."""
