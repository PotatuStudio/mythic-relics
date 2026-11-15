"""Propuesta: Skíðblaðnir, el barco de Freyr que hicieron los hijos de Ivaldi (Gylfaginning 43, Skáldskaparmál 35;
Grímnismál 43): cabe toda la hueste de los Æsir, tiene viento a favor apenas se iza la vela y se pliega como un pañuelo.

Un barco largo propio en lugar del bote vanilla: casco esbelto de tablas en tingladillo (cada tabla monta sobre la de
abajo, con remaches), quilla, roda y codaste altos, la roda rematada en una cabeza de dragón tallada y dorada, el codaste
enroscado en espiral como en el barco de Oseberg, escudos redondos amarillos y negros en la borda (como en el de
Gokstad), espadilla de gobierno a estribor, mástil con veleta dorada, obenques y una vela cuadrada a franjas, inflada
hacia proa, con el sol de Freyr y la runa Ingwaz (Yngvi-Freyr) que brillan.

Orientación (la misma que el bote vanilla, para que el BoatRenderer lo dibuje igual): proa en +x, estribor (derecha) en
-z, arriba es -y. El renderer sube el modelo 0,375, lo escala (-1, -1, 1) y lo gira 90° en y. Conserva las partes que el
juego busca por nombre: bottom, back, front, right, left, left_paddle y right_paddle (las palas se animan solas)."""
import math
import random

from china_entities import C, Model, cube, pick, mix

TEX_W = 256
SCALE = 1.0
ROOT_OFFSET = (0, 0, 0)
SEED = 43

ASH = [C("#e6cf9a"), C("#dcc38a"), C("#efd8a6"), C("#d6bb80")]
ASH_L = C("#f6e4b8")
ASH_D = C("#a88a52")
ASH_DD = C("#7c6236")
IRON = C("#3a3530")
GOLD = [C("#f5c842"), C("#f0bd35"), C("#ffd860")]
GOLD_L = C("#fff1a0")
GOLD_D = C("#b0791a")
RUNE = C("#4fb8ff")
RUNE_HOT = C("#bfe8ff")
SAIL_W = C("#f4f1e6")
SAIL_WD = C("#e2dccb")
SAIL_G = C("#e8b83a")
SAIL_GD = C("#cf9f2a")
SUN = C("#ffcf40")
SUN_HOT = C("#fff3b0")
SHIELD_Y = C("#f2c230")
SHIELD_K = C("#24201c")
ROPE = C("#c8b58a")
EYE = C("#ff5a2a")
MAST = [C("#8a5a30"), C("#7a4e28"), C("#94643a")]


def h2(*v):
    """Ruido determinista (las caras de los planos se pintan igual por los dos lados)."""
    n = 0
    for i in v:
        n = (n * 73856093) ^ int(i * 7 + 1009)
    return (n % 1000) / 1000


# ---------------------------------------------------------------- pintores
GLYPHS = [  # runas de 3 × 3 (fila de arriba primero)
    ["#..", "##.", "#.."],     # ᚠ fé
    [".#.", "#.#", ".#."],     # ᛜ Ingwaz (Yngvi-Freyr)
    ["#.#", ".#.", ".#."],     # ᛉ algiz
    ["###", ".#.", ".#."],     # ᛏ tiwaz
    ["##.", "#.#", "#.."],     # ᚱ raido (el viaje)
]


def strake(runes=False, top=False):
    """Tabla del casco en tingladillo: borde de arriba claro, sombra del solape abajo, remaches de hierro y, en la
    tabla de las runas, una fila de runas que brillan."""
    def f(face, x, y, w, h):
        if face == "top":
            return (pick(GOLD) if top else ASH_L), None
        if face == "bottom":
            return ASH_DD, None
        if face in ("west", "east"):
            return ASH_D, None
        if y == h - 1:
            return ASH_D, None
        if y == h - 2 and x % 4 == 2:
            return IRON, None
        if runes and h >= 3 and w >= 8:
            gx = x % 5
            if 1 <= gx <= 3 and 0 <= y <= 2 and x % 10 < 5 and 1 < x < w - 2:
                g = GLYPHS[(x // 5) % len(GLYPHS)]
                if g[y][gx - 1] == "#":
                    return RUNE, RUNE
        if y == 0:
            return ASH_L, None
        return (ASH_D if random.random() < 0.06 else pick(ASH)), None
    return f


def gunwale(face, x, y, w, h):
    """Borda dorada."""
    if face == "bottom":
        return GOLD_D, None
    if face in ("front", "back") and x % 6 == 3:
        return GOLD_L, None
    return pick(GOLD), None


def keel(face, x, y, w, h):
    return (ASH_DD if face == "bottom" else ASH_D if random.random() < 0.5 else pick(ASH)), None


def floor(face, x, y, w, h):
    """Tablas del suelo, a lo largo."""
    if face == "top" and y % 3 == 2:
        return ASH_D, None
    return pick(ASH), None


def deck(face, x, y, w, h):
    if face == "top" and x % 3 == 2:
        return ASH_D, None
    if face in ("front", "back", "west", "east"):
        return pick(GOLD), None
    return pick(ASH), None


def carved(face, x, y, w, h):
    """Madera tallada y dorada: escamas en zigzag."""
    if face == "bottom":
        return GOLD_D, None
    if (x + 2 * y) % 4 == 0:
        return GOLD_D, None
    if (x + 2 * y) % 4 == 1:
        return GOLD_L, None
    return pick(GOLD), None


def dragon_head(face, x, y, w, h):
    """Cabeza del dragón: ojos rojos que brillan a los costados y fosas nasales oscuras."""
    if face in ("front", "back") and y == 1 and x == w - 2:
        return EYE, EYE
    if face in ("front", "back") and y == 1 and x == w - 3:
        return C("#ffb03a"), C("#ffb03a")
    return carved(face, x, y, w, h)


def snout(face, x, y, w, h):
    if face == "east" and y == 0 and x in (0, w - 1):
        return GOLD_D, None
    if face in ("front", "back") and y == h - 1 and x % 2 == 0:
        return C("#fff8e0"), None                 # dientes
    return carved(face, x, y, w, h)


def jaw(face, x, y, w, h):
    if face == "top":
        return C("#b8322a"), None                 # boca roja
    if face in ("front", "back") and y == 0 and x % 2 == 1:
        return C("#fff8e0"), None
    return carved(face, x, y, w, h)


def crest(face, x, y, w, h):
    """Cresta de púas (plano): dientes de sierra dorados."""
    if face not in ("front", "back"):
        return None, None
    tooth = 3
    if y < (tooth - 1 - (x % tooth)) * h / tooth * 0.9:
        return None, None
    return (GOLD_L if y < h / 2 else pick(GOLD)), None


def horn(face, x, y, w, h):
    return (GOLD_L if y < 2 else GOLD_D if face == "bottom" else pick(GOLD)), None


def mast(face, x, y, w, h):
    return (MAST[0] if face in ("top", "bottom") else pick(MAST)), None


def rope(face, x, y, w, h):
    return (ROPE if (y // 2) % 2 else mix(ROPE, ASH_DD, 0.3)), None


def vane(face, x, y, w, h):
    """Veleta dorada (veðrviti) en forma de cuarto de círculo con calados."""
    if face not in ("front", "back"):
        return None, None
    if face == "back":
        x = w - 1 - x
    if (x + 0.5) ** 2 + (h - y - 0.5) ** 2 > (w + 0.2) ** 2:
        return None, None
    if (x + y) % 3 == 0 and x > 0 and y < h - 1:
        return None, None
    return (GOLD_L if y == 0 or x == 0 else GOLD[0]), None


def shield(col):
    """Escudo redondo de un color, con el borde de cuero oscuro y una cruz de refuerzos claros."""
    def f(face, x, y, w, h):
        if face not in ("front", "back"):
            return None, None
        cx, cy = w / 2 - 0.5, h / 2 - 0.5
        r = math.hypot(x - cx, y - cy)
        if r > w / 2 + 0.1:
            return None, None
        if r > w / 2 - 0.7:
            return C("#5a3a22"), None
        if col == SHIELD_K and (abs(x - cx) < 0.6 or abs(y - cy) < 0.6):
            return C("#4a4038"), None
        return mix(col, C("#000000"), 0.12 * h2(x, y)), None
    return f


def boss(face, x, y, w, h):
    return (C("#c9c4b8") if face in ("front", "back", "top") else C("#8a857a")), None


def sail_panel(z0, y0, size, total_w, total_h):
    """Un paño de la vela (plano): franjas verticales blancas y doradas y el sol de Freyr con la runa Ingwaz en el
    centro, en coordenadas de toda la vela para que los paños calcen."""
    def f(face, x, y, w, h):
        if face not in ("west", "east"):
            return None, None
        gz = z0 + (x if face == "east" else w - 1 - x)
        gy = y0 + y
        cx, cy = total_w / 2 - 0.5, total_h * 0.45
        dz, dy = gz - cx, gy - cy
        r = math.hypot(dz, dy)
        # runa Ingwaz (rombo) en el centro del sol
        if abs(dz) + abs(dy) in (2.0, 2.5, 3.0) and r < 3.2:
            return SUN_HOT, SUN_HOT
        if r <= 4.6:
            return SUN, SUN
        ang = math.atan2(dy, dz)
        if r <= 7.0 and (math.floor((ang + math.pi) / (math.pi / 6)) % 2 == 0) and r <= 6.6 - 0.0:
            return C("#ffb23a"), C("#ffb23a")      # rayos
        if gy == 0:
            return C("#b08a3a"), None             # relinga de arriba
        stripe = (gz // 4) % 2
        shade = 0.12 * h2(gz, gy) + (0.1 if gy > total_h * 0.7 else 0)
        base = (SAIL_G, SAIL_GD) if stripe else (SAIL_W, SAIL_WD)
        return mix(base[0], base[1], shade * 2), None
    return f


# ---------------------------------------------------------------- casco
# Tablas de cada costado: (y de arriba en el centro, alto, z del centro de la tabla, cuánto sube en las puntas)
STRAKES = [(2, 3, 6, 3.0), (0, 3, 7, 4.2), (-2, 3, 8, 5.4), (-4, 3, 9, 6.6)]
MID = 6           # la parte recta del casco va de -6 a 6
BOW = 26          # roda
STERN = -25       # codaste
# Curva de las tablas hacia las puntas: x relativa (0 = fin de la parte recta, 1 = roda o codaste), cuánto se cierran
# (fracción del ancho) y cuánto suben (fracción del arrufo).
CURVE = [(0.0, 1.0, 0.0), (0.4, 0.93, 0.12), (0.75, 0.62, 0.42), (1.0, 0.0, 1.0)]


def plank_seg(m, name, parent, p0, p1, h, d, paint, lap=0.5):
    """Una tabla recta cuyo borde de arriba va de p0 a p1 (x, y, z); se arma siempre hacia +x para que no quede de
    cabeza, con la rotación ZYX que lleva su eje x a lo largo del segmento."""
    flip = p1[0] < p0[0]
    if flip:
        p0, p1 = p1, p0
    dx, dy, dz = (p1[i] - p0[i] for i in range(3))
    n = math.sqrt(dx * dx + dy * dy + dz * dz)
    a, b, c = dx / n, dy / n, dz / n
    yaw = math.atan2(-c, math.hypot(a, b))
    roll = math.atan2(b, a)
    # solape de 0,5 con el tramo vecino, salvo en la junta con la parte recta (lap = 0) para no dejar caras coplanares
    low, high = (0.5, lap) if flip else (lap, 0.5)
    width = math.ceil(n + low + high)
    x0 = n + high - width if flip else -low
    m.part(name, parent, list(p0), [0, yaw, roll], [cube(x0, 0, -d / 2, width, h, d, paint)])


def curve_points(x_end, z_mid, y_mid, rise, z_tip):
    sign = 1 if x_end > 0 else -1
    pts = []
    for t, f, g in CURVE:
        x = sign * (MID + t * (abs(x_end) - MID))
        z = z_tip + (z_mid - z_tip) * f
        pts.append((x, y_mid - rise * g, z))
    return pts


def side(m, s, name):
    """Un costado (s = 1 izquierdo, en +z; s = -1 derecho): tablas rectas en el centro y, hacia la roda y el codaste,
    tres tramos por tabla que se van cerrando y levantando (arrufo)."""
    cubes = []
    rows = [(y, h, z, rise, strake(runes=(i == 2)), 1) for i, (y, h, z, rise) in enumerate(STRAKES)]
    rows.append((-5, 1, 9.5, 7.6, gunwale, 2))
    for i, (y, h, z, rise, paint, d) in enumerate(rows):
        zc = s * z
        cubes.append(cube(-MID, y, zc - d / 2, 2 * MID, h, d, paint))
        for end, tag in ((BOW - 1, "bow"), (STERN + 1, "stern")):
            pts = curve_points(end, zc, y, rise, s * 0.7)
            for k in range(len(pts) - 1):
                plank_seg(m, f"{name}_{tag}{i}_{k}", name, pts[k], pts[k + 1], h, d, paint, lap=0.0 if k == 0 else 0.5)
    m.parts[[p["name"] for p in m.parts].index(name)]["cubes"] = cubes
    # escudos en la borda, solapados como en el barco de Gokstad (amarillos y negros alternados; cada uno un poco más
    # afuera que el anterior para que no compartan plano). Se deja libre el hueco de la pala, en x = 3.
    for j, xc in enumerate([-10, -5.5, -1, 8.5, 13]):
        col = SHIELD_Y if (j + (s > 0)) % 2 == 0 else SHIELD_K
        x_rel = max(0.0, (abs(xc) - MID) / (BOW - 1 - MID))
        zz = s * (10.55 + 0.2 * (j % 2) - 2.6 * x_rel)
        m.part(f"{name}_shield{j}", name, [xc, -4.5, zz], [0, -s * 0.3 * x_rel / 0.4 if x_rel else 0, 0],
               [cube(-3, -3, 0, 6, 6, 0, shield(col)),
                cube(-0.5, -0.5, -0.5 + s * 0.5, 1, 1, 1, boss)])


def build():
    m = Model()
    # Fondo: quilla de punta a punta, fondo y tablas del suelo; cubiertas cortas a proa y popa (más altas, para que el
    # agua no se vea dentro del casco fuera del parche de agua del bote vanilla).
    m.part("bottom", None, [0, 0, 0], [0, 0, 0], [
        cube(-16, 5, -1, 32, 2, 2, keel),
        cube(-MID, 4, -5.5, 2 * MID, 1, 11, keel),
        cube(-MID, 3, -6, 2 * MID, 1, 12, floor),
        cube(MID, 3, -5, 8, 1, 10, floor),
        cube(MID + 8, 2, -3.5, 6, 2, 7, keel),
        cube(MID + 14, 1, -1, 4, 2, 2, keel),
        cube(-MID - 8, 3, -5, 8, 1, 10, floor),
        cube(-MID - 14, 2, -3.5, 6, 2, 7, keel),
        cube(-MID - 18, 1, -1, 4, 2, 2, keel),
        cube(MID + 8, -2, -4.5, 6, 1, 9, deck),
        cube(MID + 14, -2.5, -2, 4, 1, 4, deck),
        cube(-MID - 14, -2, -4.5, 6, 1, 9, deck),
        cube(-MID - 18, -2.5, -2, 4, 1, 4, deck),
    ])
    plank_seg(m, "keel_bow", "bottom", (16, 5, 0), (BOW - 0.5, 0.5, 0), 2, 2, keel)
    plank_seg(m, "keel_stern", "bottom", (STERN + 0.5, 0.5, 0), (-16, 5, 0), 2, 2, keel)
    m.part("left", None, [0, 0, 0], [0, 0, 0], [])
    side(m, 1, "left")
    m.part("right", None, [0, 0, 0], [0, 0, 0], [])
    side(m, -1, "right")

    # Roda: tres tramos que suben y se adelantan, y la cabeza de dragón tallada mirando al frente.
    m.part("front", None, [BOW - 1.5, 2, 0], [0, 0, 0.12], [cube(-1, -10, -1, 2, 11, 2, carved)])
    m.part("stem2", "front", [0, -10, 0], [0, 0, 0.3], [cube(-1, -8, -1, 2, 8, 2, carved),
                                                         cube(-3, -8, 0, 2, 7, 0, crest)])
    m.part("stem3", "stem2", [0, -8, 0], [0, 0, 0.42], [cube(-1, -7, -1, 2, 7, 2, carved),
                                                         cube(-3, -7, 0, 2, 6, 0, crest)])
    m.part("dragon", "stem3", [0, -7, 0], [0, 0, -0.62], [cube(-2.5, -4, -2.5, 6, 5, 5, dragon_head),
                                                           cube(3.5, -3, -2, 5, 3, 4, snout),
                                                           cube(7.5, -3.5, -1.5, 1, 1, 1, horn),
                                                           cube(-4.5, -5, 0, 4, 4, 0, crest)])
    m.part("dragon_jaw", "dragon", [3.5, 0, 0], [0, 0, 0.35], [cube(-1, -0.5, -1.5, 5, 1, 3, jaw)])
    m.part("horn_l", "dragon", [-1.5, -4, 1.5], [0.3, 0, -0.8], [cube(-0.5, -5, -0.5, 1, 5, 1, horn)])
    m.part("horn_r", "dragon", [-1.5, -4, -1.5], [-0.3, 0, -0.8], [cube(-0.5, -5, -0.5, 1, 5, 1, horn)])

    # Codaste: sube, se echa hacia atrás y se enrosca en espiral sobre sí mismo.
    m.part("back", None, [STERN + 1.5, 2, 0], [0, 0, -0.12], [cube(-1, -10, -1, 2, 11, 2, carved)])
    prev = "back"
    for i, (ln, ang) in enumerate([(8, -0.3), (6, -0.25), (5, 0.55), (4, 0.75), (3, 0.85), (3, 0.95), (2, 1.0)]):
        top = -10 if i == 0 else -[8, 6, 5, 4, 3, 3, 2][i - 1]
        name = f"curl{i}"
        m.part(name, prev, [0, top, 0], [0, 0, ang], [cube(-1, -ln, -1, 2, ln, 2, carved)])
        prev = name

    # Espadilla de gobierno a estribor (lado derecho, -z), cerca de la popa.
    m.part("rudder", None, [STERN + 7, -5, -10.5], [0, 0, 0.35], [cube(-0.5, -2, -0.5, 1, 11, 1, mast),
                                                                 cube(-2.5, 6, -0.5, 4, 7, 1, deck),
                                                                 cube(-0.5, -2.5, 0, 1, 1, 4, mast)])

    # Palas vanilla (el juego las anima), de fresno con la pala dorada.
    m.part("left_paddle", None, [3, -5, 9], [0, 0, math.pi / 16], [cube(-1, 0, -5, 2, 2, 18, keel),
                                                                     cube(-1.001, -3, 8, 1, 6, 7, deck)])
    m.part("right_paddle", None, [3, -5, -9], [0, math.pi, math.pi / 16], [cube(-1, 0, -5, 2, 2, 18, keel),
                                                                             cube(0.001, -3, 8, 1, 6, 7, deck)])

    # Mástil a popa del jinete, con veleta dorada, verga, obenques, estay y la vela inflada hacia proa.
    mx = -10
    m.part("mast", None, [mx, 4, 0], [0, 0, 0], [cube(-1, -37, -1, 2, 37, 2, mast),
                                               cube(-1.5, -38, -1.5, 3, 1, 3, carved)])
    m.part("vane", "mast", [0, -38, 0], [0, 0, 0], [cube(-0.5, -4, -0.5, 1, 4, 1, carved),
                                                    cube(0.5, -4, 0, 5, 4, 0, vane)])
    m.part("yard", "mast", [1.5, -34, 0], [0, 0, 0], [cube(-0.5, -0.5, -14, 1, 1, 28, mast)])
    for nm, zt in (("shroud_l", 9.2), ("shroud_r", -9.2)):
        dy = 36 - 9
        m.part(nm, "mast", [0, -36, 0], [math.atan2(zt, dy), 0, 0],
               [cube(-0.5, 0, -0.5, 1, round(math.hypot(zt, dy)), 1, rope)])
    fx, fy = (BOW + 2) - mx, 36 - 15
    m.part("forestay", "mast", [0, -36, 0], [0, 0, -math.atan2(fx, fy)],
           [cube(-0.5, 0, -0.5, 1, round(math.hypot(fx, fy)), 1, rope)])

    # Vela cuadrada de 24 × 18 en paños de 6 × 6, curvada como un cojín (más inflada al centro y abajo).
    sw, sh, n_z, n_y, cell, bulge = 24, 18, 4, 3, 6, 3.4
    m.part("sail", "yard", [0.5, 0.5, 0], [0, 0, 0], [])

    def bx(z, y):
        u = z / (sw / 2)
        t = y / sh
        return bulge * (1 - u * u) * math.sin(math.pi * (0.15 + 0.7 * t))

    for i in range(n_y):
        for j in range(n_z):
            z0, y0 = -sw / 2 + j * cell, i * cell
            zc, yc = z0 + cell / 2, y0 + cell / 2
            xc = bx(zc, yc)
            ddz = (bx(zc + 0.5, yc) - bx(zc - 0.5, yc))
            ddy = (bx(zc, yc + 0.5) - bx(zc, yc - 0.5))
            yaw, roll = math.atan(ddz), -math.atan(ddy)
            m.part(f"sail_{i}_{j}", "sail", [xc, yc, zc], [0, yaw, roll],
                   [cube(0, -cell / 2, -cell / 2, 0, cell, cell, sail_panel(j * cell, i * cell, cell, sw, sh))])
    return m


NOTES = """Deja de ser el bote vanilla con vela (13 cubos) y pasa a un barco largo propio de unos 150: casco de 3,2 bloques de eslora (4,8 con el dragón y la espiral; antes 2,4) y 2,9 de alto con el mástil.
Casco esbelto de cuatro tablas por banda en tingladillo (cada una monta sobre la de abajo, con remaches), que se van cerrando y levantando en tres tramos hacia proa y popa; quilla que sube hasta la roda y el codaste.
Borda dorada, cubiertas cortas a proa y popa, cinco escudos por banda solapados (amarillos y negros, con umbo) y espadilla de gobierno a estribor.
Roda alta con cabeza de dragón tallada y dorada (mandíbula abierta, dientes, cuernos, cresta de púas, ojos que brillan); codaste enroscado en espiral.
Mástil con veleta dorada, verga, obenques y estay; vela de 12 paños inflada hacia proa, a franjas blancas y doradas, con el sol de Freyr y la runa Ingwaz que brillan; runas azules que brillan en una tabla del casco.
Misma orientación que el bote vanilla para el BoatRenderer: proa en +x, estribor en -z, arriba -y (el renderer sube 0,375, escala (-1,-1,1) y gira 90° en y); conserva bottom, back, front, right, left y las palas vanilla, que se siguen animando solas.
Ojo en el juego: el parche de agua vanilla tapa solo el centro (28 × 16), por eso las puntas llevan cubierta alta; la quilla baja 1 px más que el bote vanilla; la caja de choque no cambia.
Fiel al mito: el barco de Freyr hecho por los hijos de Ivaldi, que siempre tiene viento a favor (vela inflada) y es el mejor de los barcos; Ingwaz es la runa de Yngvi-Freyr.
Licencia de juego: el dragón de proa, la espiral de popa, los escudos y la veleta vienen de los barcos vikingos reales (Oseberg, Gokstad), no del mito; también el sol de la vela, el oro y las runas que brillan.
Cultura pop: nada.
Animación propuesta: la vela se hincha y ondea (los paños), la veleta gira con el viento, las palas como el vanilla y, al subir o bajar, el barco se despliega o se pliega como un pañuelo (escala y giro del casco y la vela)."""
