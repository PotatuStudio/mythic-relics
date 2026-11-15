"""Propuesta: Skíðblaðnir, el barco de Freyr que hicieron los hijos de Ivaldi (Gylfaginning 43, Skáldskaparmál 35;
Grímnismál 43): cabe toda la hueste de los Æsir, tiene viento a favor apenas se iza la vela y se pliega como un pañuelo.

Versión cercana al bote vanilla: el mismo casco (fondo de 28 × 16 y costados de 6 de alto) con bloques grandes y
píxeles del tamaño vanilla, alargado con una proa y una popa en punta que suben un escalón, la roda que sube otro y
termina en una cabeza de dragón simple con cuernos y la boca abierta, el codaste enroscado hacia adentro, tres escudos por banda, y el mástil
con una vela cuadrada plana a franjas con el sol de Freyr y la runa Ingwaz, que brillan.

Orientación (la misma que el bote vanilla, para que el BoatRenderer lo dibuje igual): proa en +x, estribor (derecha) en
-z, arriba es -y. El renderer sube el modelo 0,375, lo escala (-1, -1, 1) y lo gira 90° en y. Conserva las partes que el
juego busca por nombre: bottom, back, front, right, left, left_paddle y right_paddle (las palas se animan solas)."""
import math

from china_entities import C, Model, cube, pick

TEX_W = 128
SCALE = 1.0
ROOT_OFFSET = (0, 0, 0)
SEED = 44

ASH = [C("#e6cf9a"), C("#dcc38a"), C("#e2c890")]
ASH_L = C("#f2dcaa")
ASH_D = C("#a88a52")
ASH_DD = C("#7c6236")
GOLD = [C("#f5c842"), C("#f0bd35")]
GOLD_L = C("#ffe27a")
GOLD_D = C("#b0791a")
RUNE = C("#4fb8ff")
SAIL_W = C("#f4f1e6")
SAIL_G = C("#e8b83a")
SUN = C("#ffcf40")
SUN_HOT = C("#fff3b0")
RAY = C("#ffa62a")
RAY_D = C("#e8761a")
SHIELD_Y = C("#f2c230")
SHIELD_K = C("#2a2420")
RIM = C("#5a3a22")
BOSS = C("#c9c4b8")
EYE = C("#ff5a2a")
MAST = [C("#8a5a30"), C("#7a4e28")]


# ---------------------------------------------------------------- pintores (píxeles grandes, como el bote vanilla)
def planks(face, x, y, w, h):
    """Tablas de fresno claro con una junta oscura cada 3 px, como las tablas del bote vanilla."""
    if face in ("top", "bottom"):
        return (ASH_D if face == "bottom" else pick(ASH)), None
    return (ASH_D if y % 3 == 2 else pick(ASH)), None


def hull_side(face, x, y, w, h):
    """Costado: borda dorada arriba y, en la cara de afuera y la de adentro, una fila de runas azules que brillan."""
    if face == "top":
        return pick(GOLD), None
    if face in ("front", "back"):
        if y == 0:
            return pick(GOLD), None
        if y == 2 and x % 4 in (1, 2) and 2 <= x < w - 2:
            return RUNE, RUNE
        return (ASH_D if y == h - 1 else pick(ASH)), None
    return planks(face, x, y, w, h)


def bow_board(face, x, y, w, h):
    """Tablas de proa y popa: borda dorada arriba y tablas abajo (sin runas)."""
    if face == "top" or (face in ("front", "back") and y == 0):
        return pick(GOLD), None
    return planks(face, x, y, w, h)


def floor(face, x, y, w, h):
    if face == "top":
        return (ASH_D if y % 4 == 3 else pick(ASH)), None
    return (ASH_DD if face == "bottom" else ASH_D), None


def deck(face, x, y, w, h):
    if face == "top":
        return (ASH_D if x % 4 == 3 else ASH_L), None
    return pick(GOLD), None


def keel(face, x, y, w, h):
    return (ASH_DD if face == "bottom" else ASH_D), None


def gold(face, x, y, w, h):
    return (GOLD_D if face == "bottom" else pick(GOLD)), None


def dragon_head(face, x, y, w, h):
    """Cabeza: oro, con un ojo rojo que brilla a cada lado (cerca del hocico) y una ceja clara."""
    if face in ("front", "back"):
        snout_x = w - 2 if face == "back" else 1      # la cara de +z se ve espejada respecto de la de -z
        if y == 1 and x == snout_x:
            return EYE, EYE
        if y == 0 and x == snout_x:
            return GOLD_L, None
    return gold(face, x, y, w, h)


def snout(face, x, y, w, h):
    """Hocico: fosas oscuras arriba y dientes blancos abajo."""
    if face == "east" and y == 0 and x in (0, w - 1):
        return GOLD_D, None
    if face == "bottom":
        return C("#b8322a"), None                     # paladar rojo
    if face in ("front", "back", "east") and y == h - 1 and x % 2 == 0:
        return C("#fff8e0"), None
    return gold(face, x, y, w, h)


def jaw(face, x, y, w, h):
    if face == "top":
        return C("#b8322a"), None                     # boca roja
    if face in ("front", "back", "east") and y == 0:
        return C("#fff8e0"), None                     # dientes
    return gold(face, x, y, w, h)


def mast(face, x, y, w, h):
    return pick(MAST), None


def sail(face, x, y, w, h):
    """Vela plana (las dos caras grandes): paño blanco al centro con dos franjas doradas por lado, y el sol de Freyr
    con la runa Ingwaz (un rombo) en el centro; el sol y sus rayos brillan."""
    if face not in ("west", "east"):
        return SAIL_W, None
    cz, cy = w / 2 - 0.5, h / 2 - 0.5
    dz, dy = x - cz, y - cy
    r = math.hypot(dz, dy)
    if abs(dz) + abs(dy) == 2:
        return SUN_HOT, SUN_HOT                       # Ingwaz
    if r <= 2.9:
        return SUN, SUN
    if r <= 3.6:
        return RAY_D, RAY_D                           # borde del disco
    if r <= 5.6 and (abs(dz) < 0.6 or abs(dy) < 0.6 or abs(abs(dz) - abs(dy)) < 0.6):
        return RAY, RAY                               # ocho rayos
    if y == 0:
        return GOLD_D, None                           # relinga
    a = abs(dz)                                       # paño blanco al centro y dos franjas doradas por lado
    return (SAIL_G if 5 < a < 7 or a > 9 else SAIL_W), None


def shield(col):
    """Escudo redondo de 6 × 6 (plano): borde de cuero, el color y el umbo de hierro al centro."""
    def f(face, x, y, w, h):
        if face not in ("front", "back"):
            return None, None
        cx, cy = w / 2 - 0.5, h / 2 - 0.5
        r = math.hypot(x - cx, y - cy)
        if r > 2.9:
            return None, None
        if r > 2.0:
            return RIM, None
        if r < 0.8:
            return BOSS, None
        return col, None
    return f


def paddle(face, x, y, w, h):
    return pick(ASH), None


def paddle_blade(face, x, y, w, h):
    return pick(GOLD), None


# ---------------------------------------------------------------- piezas
def board(m, name, parent, p_from, p_to, y0, h):
    """Tabla recta de 2 de grueso que va de p_from a p_to (x, z) en planta, de y0 hacia abajo h px; se arma hacia +x
    (girada solo en y) y se alarga 1 px por cada punta para cerrar las juntas."""
    (x0, z0), (x1, z1) = sorted([p_from, p_to])
    dx, dz = x1 - x0, z1 - z0
    n = math.hypot(dx, dz)
    m.part(name, parent, [x0, 0, z0], [0, math.atan2(-dz, dx), 0], [cube(-1, y0, -1, round(n + 2), h, 2, bow_board)])


def build():
    m = Model()
    # Fondo: la plancha del bote vanilla (28 × 16 × 3), dos escalones que se angostan bajo la proa y la popa, una quilla
    # de punta a punta y, dentro de las puntas, cubiertas cortas (tapan el agua fuera del parche de agua vanilla).
    m.part("bottom", None, [0, 0, 0], [0, 0, 0], [
        cube(-14, 3, -8, 28, 3, 16, floor),
        cube(14, 3, -6, 3, 2, 12, floor), cube(17, 3, -3, 3, 2, 6, floor),
        cube(-17, 3, -6, 3, 2, 12, floor), cube(-20, 3, -3, 3, 2, 6, floor),
        cube(-21, 5, -1, 42, 2, 2, keel),
        cube(14, -1, -6, 3, 1, 12, deck), cube(17, -1, -3, 3, 1, 6, deck),
        cube(-17, -1, -6, 3, 1, 12, deck), cube(-20, -1, -3, 3, 1, 6, deck),
    ])
    # Costados del bote vanilla (6 de alto) y, hacia las puntas, una tabla por banda que se cierra y sube un escalón
    # (2 px más alta).
    m.part("left", None, [0, 0, 9], [0, 0, 0], [cube(-14, -3, -1, 28, 6, 2, hull_side)])
    m.part("right", None, [0, 0, -9], [0, 0, 0], [cube(-14, -3, -1, 28, 6, 2, hull_side)])
    for s, side in ((1, "left"), (-1, "right")):
        board(m, f"{side}_bow", None, (14, s * 9), (21, s * 1.2), -5, 8)
        board(m, f"{side}_stern", None, (-14, s * 9), (-21, s * 1.2), -5, 8)
        # tres escudos redondos por banda, amarillos y negros alternados, asomados sobre la borda
        for j, xc in enumerate([-9, -3, 9]):
            col = SHIELD_Y if (j + (s > 0)) % 2 == 0 else SHIELD_K
            m.part(f"{side}_shield{j}", side, [xc, -3, s * 1.3], [0, 0, 0], [cube(-3, -3, 0, 6, 6, 0, shield(col))])

    # Roda: un poste que sube otro escalón y la cabeza de dragón (cuello, cabeza y hocico) mirando hacia adelante.
    m.part("front", None, [22, 0, 0], [0, 0, 0], [
        cube(-1.5, -9, -1.5, 3, 12, 3, gold),          # poste
        cube(-1, -13, -1.5, 3, 4, 3, gold),            # cuello
        cube(-1, -17, -2, 6, 4, 4, dragon_head),       # cabeza
        cube(5, -16, -1.5, 3, 2, 3, snout),            # hocico
        cube(5, -13, -1, 2, 1, 2, jaw),                # mandíbula, con la boca abierta
        cube(-1, -19, 0.5, 1, 2, 1, gold), cube(-1, -19, -1.5, 1, 2, 1, gold),   # cuernos
    ])
    # Codaste: sube, se echa atrás y se enrosca hacia adentro como una voluta.
    m.part("back", None, [-22, 0, 0], [0, 0, 0], [
        cube(-1.5, -9, -1.5, 3, 12, 3, gold),
        cube(-2.5, -14, -1.5, 3, 5, 3, gold),
        cube(-1, -16, -1.5, 4, 2, 3, gold),
        cube(2, -15, -1.5, 2, 2, 3, gold),
    ])

    # Palas vanilla (el juego las anima), de fresno con la pala dorada.
    m.part("left_paddle", None, [3, -5, 9], [0, 0, math.pi / 16], [cube(-1, 0, -5, 2, 2, 18, paddle),
                                                                     cube(-1.001, -3, 8, 1, 6, 7, paddle_blade)])
    m.part("right_paddle", None, [3, -5, -9], [0, math.pi, math.pi / 16], [cube(-1, 0, -5, 2, 2, 18, paddle),
                                                                             cube(0.001, -3, 8, 1, 6, 7, paddle_blade)])

    # Mástil a popa del jinete, con un remate de oro, la verga y la vela cuadrada plana.
    m.part("mast", None, [-10, 3, 0], [0, 0, 0], [cube(-1, -34, -1, 2, 34, 2, mast),
                                               cube(-1.5, -36, -1.5, 3, 2, 3, gold)])
    m.part("yard", "mast", [0, -30, 0], [0, 0, 0], [cube(-1, -1, -12, 2, 1, 24, mast)])
    m.part("sail", "yard", [1.5, 0, 0], [0, 0, 0], [cube(-0.5, 0, -11, 1, 16, 22, sail)])
    return m


NOTES = """Vuelve a ser casi el bote vanilla (de 13 a unos 40 cubos): el mismo fondo de 28 × 16 y los costados de 6 de alto, con píxeles del tamaño vanilla y sin detalle fino; 2,9 bloques de eslora de roda a codaste y 3,4 con la cabeza de dragón (hoy 2 de casco y 2,4 con la proa), y 2,5 de alto hasta el remate del mástil (hoy 2,2).
Proa y popa en punta: una tabla por banda que se cierra hacia la punta y sube un escalón (2 px), con cubiertas cortas por dentro y una quilla de punta a punta.
La roda sube otro escalón y termina en una cabeza de dragón dorada de pocos cubos (poste, cuello, cabeza con dos cuernos, hocico y mandíbula abierta con dientes) con ojos rojos que brillan; el codaste se enrosca hacia adentro en una voluta.
Tres escudos redondos por banda (amarillos y negros, con umbo) asomados sobre la borda dorada, que sigue con su fila de runas azules que brillan.
Mástil con remate de oro, verga y una vela cuadrada plana (una pieza de 22 × 16) blanca con franjas doradas a los lados y el sol de Freyr y la runa Ingwaz, que brillan.
Misma orientación que el bote vanilla para el BoatRenderer: proa en +x, estribor en -z, arriba -y; conserva bottom, back, front, right, left y las palas vanilla, que se siguen animando solas. La quilla baja 1 px más que el bote vanilla; la caja de choque no cambia.
Fiel al mito: el barco de Freyr hecho por los hijos de Ivaldi, el mejor de los barcos, que siempre tiene viento a favor; Ingwaz es la runa de Yngvi-Freyr y el sol, el de un dios de la luz del sol y las cosechas.
Licencia de juego: el dragón de proa, la voluta de popa y los escudos vienen de los barcos vikingos reales (Oseberg, Gokstad), no del mito; también el oro, las runas que brillan y el sol en la vela.
Cultura pop: nada.
Animación propuesta: las palas como el vanilla; la vela se mece un poco (giro leve de la verga) y el sol late; al subir o bajar, el barco se despliega o se pliega como un pañuelo (escala del casco y de la vela)."""
