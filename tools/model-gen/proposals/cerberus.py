"""Propuesta: Cerbero más bestia. Tres cabezas grandes de quijada pesada con colmillos, lengua y fauces de brasa, gorguera
de pelo erizado, cresta de cerdas en el lomo, patas de dos tramos con zarpas y garras, y la cola de serpiente
entera (con cabeza, ojos y colmillos), como en Apolodoro (Biblioteca II 5.12).

Las serpientes del lomo que también menciona Apolodoro quedan apagadas (BACK_SNAKES = False): el usuario pidió quitarlas
(commit «Cerbero sin las serpientes del lomo»). Con True se ven como una hilera de cabezas sobre la cruz."""
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from china_entities import C, Model, cube, front_x, mix, pick  # noqa: E402

TEX_W = 128
SCALE = 2.0
ROOT_OFFSET = (0, 0, 0)
SEED = 11
BACK_SNAKES = False

FUR = [C("#1e1a24"), C("#241f2b"), C("#19151e"), C("#2b2533")]
FUR_HI = C("#3e3648")
FUR_TIP = C("#55485f")
FUR_SH = C("#0e0c11")
RIDGE = C("#0a080c")
EMBER = C("#ff6e28")
EMBER_CORE = C("#ffd660")
MOUTH = C("#6e1216")
MOUTH_D = C("#3c080c")
MOUTH_GLOW = C("#8a2a10")
FANG = C("#f0e8d0")
FANG_D = C("#c8bc9c")
CLAW = [C("#d6ccb4"), C("#c4b89c")]
CLAW_TIP = C("#2a2420")
SCALE_C = [C("#28392a"), C("#304630"), C("#22301f")]
SCALE_RIM = C("#4c6a3c")
SCALE_BELLY = C("#68784a")
SNAKE_EYE = C("#ffd23a")
TONGUE = C("#c82828")
BRONZE = [C("#4a3418"), C("#553c1c"), C("#3e2c14")]
BRONZE_HI = C("#9a7238")
SPIKE = C("#9a9488")


def h(*v):
    """Ruido fijo por píxel (para que los mechones no cambien con el orden de pintura)."""
    s = 0
    for i, a in enumerate(v):
        s = (s * 131 + int(a) * (i + 7) + 17) % 9973
    return (s * 2654435761 % 1000) / 1000.0


# ---------------------------------------------------------------- pintores
def fur(face, x, y, w, hh):
    """Pelaje de sombra: mechones verticales más claros en las puntas, lomo más oscuro, panza en sombra."""
    if face == "bottom":
        return FUR_SH, None
    if face == "top":
        return (RIDGE if h(x, y) < 0.25 else pick(FUR)), None
    streak = h(x, 3) < 0.3
    if streak and y % 3 == 1:
        return FUR_HI, None
    if h(x, y, 5) < 0.08:
        return FUR_TIP, None
    if y >= hh - 1:
        return FUR_SH, None
    return pick(FUR), None


def bristles(face, x, y, w, hh, axis="y"):
    """Plano de cerdas erizadas: dientes de alto variable, oscuros en la base y grises en la punta."""
    if face in ("top", "bottom", "front", "back"):
        return None, None
    col = x
    tall = hh * (0.35 + 0.65 * h(col // 1, 9))
    depth = hh - y
    if depth > tall:
        return None, None
    t = depth / max(1, tall)
    return (FUR_TIP if t > 0.85 else FUR_HI if t > 0.6 else pick(FUR)), None


def ruff(face, x, y, w, hh):
    """Gorguera del cuello: pelo largo erizado con puntas grises, abajo con flecos."""
    if face == "bottom":
        return FUR_SH, None
    if face in ("front", "west", "east") and y >= hh - 2 and h(x, 4) < 0.5:
        return (None, None) if face != "front" else (FUR_SH, None)
    if (x + y * 2) % 5 == 0 or h(x, y, 2) < 0.12:
        return FUR_TIP, None
    if (x * 3 + y) % 7 == 0:
        return FUR_HI, None
    return pick(FUR), None


def skull(face, x, y, w, hh):
    """Cráneo: pelo corto; ojos de brasa al frente (a los lados del hocico) y en los costados, bajo la ceja."""
    if face == "front":
        if y in (2, 3) and x in (0, 1, w - 2, w - 1):
            c = EMBER_CORE if (y == 2 and x in (1, w - 2)) else EMBER
            return c, c
        if y == 1 and x in (0, 1, 2, w - 3, w - 2, w - 1):
            return RIDGE, None
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y in (2, 3) and fx in (0, 1):
            c = EMBER_CORE if (y == 2 and fx == 0) else EMBER
            return c, c
    if face == "bottom":
        return MOUTH_D, None
    return fur(face, x, y, w, hh)


def brow(face, x, y, w, hh):
    if face == "bottom" or (face == "front" and y == hh - 1):
        return RIDGE, None
    if face == "front":
        return (FUR_HI if (x + y) % 3 == 0 else pick(FUR)), None
    return fur(face, x, y, w, hh)


def snout(face, x, y, w, hh):
    """Hocico: nariz negra adelante y fila de dientes en el borde de abajo; por dentro, paladar de brasa."""
    if face == "bottom":
        if x in (0, w - 1):
            return FANG, None
        return MOUTH_GLOW, MOUTH_GLOW
    if face == "front":
        if y == 0 and 1 <= x <= w - 2:
            return C("#050405"), None
        if y == hh - 1:
            return (FANG if x % 2 == 0 else MOUTH_D), None
        return mix(pick(FUR), FUR_HI, 0.3), None
    if face in ("west", "east"):
        if y == hh - 1:
            return (FANG if x % 2 == 0 else MOUTH_D), None
        fx = front_x(face, x, w)
        if y == 0 and fx < 2:
            return C("#050405"), None
    if face == "top" and y == 0:
        return C("#050405"), None
    return fur(face, x, y, w, hh)


def jaw(face, x, y, w, hh):
    if face == "top":
        if x in (0, w - 1):
            return FANG, None
        return MOUTH_GLOW, MOUTH_GLOW
    if face in ("west", "east", "front") and y == 0:
        return (FANG if x % 2 == 0 else MOUTH_D), None
    if face == "bottom":
        return FUR_SH, None
    return fur(face, x, y, w, hh)


def tongue(face, x, y, w, hh):
    if face == "top" and x == w // 2:
        return C("#8a1818"), None
    return TONGUE, None


def fang(face, x, y, w, hh):
    return (FANG_D if y == 0 else FANG), None


def ear(face, x, y, w, hh):
    """Oreja en punta: se recorta en triángulo; por dentro, rojo oscuro."""
    if face in ("front", "back"):
        half = (w - 1) / 2
        if abs(x - half) > (y + 1) * (w / 2) / hh + 0.2:
            return None, None
        if face == "front" and y > 0 and abs(x - half) < 0.6:
            return MOUTH_D, None
        return pick(FUR), None
    if face == "top":
        return None, None
    return pick(FUR), None


def claw(face, x, y, w, hh):
    if face == "front" or (face in ("west", "east") and front_x(face, x, w) == 0):
        return CLAW_TIP if face == "front" else pick(CLAW), None
    return pick(CLAW), None


def paw(face, x, y, w, hh):
    if face == "bottom":
        return C("#141014"), None
    if face == "front" and y == hh - 1:
        return FUR_SH, None
    return fur(face, x, y, w, hh)


IRON = [C("#34302e"), C("#3c3734"), C("#2c2826")]


def collar(face, x, y, w, hh):
    """Collar de hierro negro con bordes y tachas de bronce."""
    if face in ("top", "bottom"):
        return pick(IRON), None
    if y in (0, hh - 1):
        return BRONZE[1], None
    if (x + 1) % 3 == 0 and y == hh // 2:
        return BRONZE_HI, None
    return pick(IRON), None


def spike(face, x, y, w, hh):
    return (C("#d8d2c4") if face == "front" else SPIKE), None


def scales(face, x, y, w, hh):
    """Escamas de serpiente verde oscuro con el borde claro; vientre de placas más pálido."""
    if face == "bottom":
        return (SCALE_BELLY if y % 2 == 0 else mix(SCALE_BELLY, SCALE_C[0], 0.4)), None
    if face == "top":
        if abs(x - (w - 1) / 2) < 0.6:
            return C("#141e14"), None
        return (SCALE_RIM if (x + y) % 3 == 0 else pick(SCALE_C)), None
    row = y // 2
    cx = (x + (row % 2)) % 3
    if y % 2 == 1 and cx == 1:
        return SCALE_RIM, None
    return pick(SCALE_C), None


def snake_head(face, x, y, w, hh):
    if face in ("west", "east"):
        fx = front_x(face, x, w)
        if y == 0 and fx in (1, 2):
            c = SNAKE_EYE if fx == 1 else C("#ff9a20")
            return c, c
    if face == "front" and y == 0 and x in (0, w - 1):
        return C("#0a0e0a"), None
    if face == "bottom":
        return MOUTH, None
    return scales(face, x, y, w, hh)


def snake_jaw(face, x, y, w, hh):
    if face == "top":
        return (FANG if x in (0, w - 1) else MOUTH), None
    if face == "bottom":
        return SCALE_BELLY, None
    return scales(face, x, y, w, hh)


# ---------------------------------------------------------------- modelo
def dog_head(m, name, offset, yaw, roll=0.0):
    """Cabeza con cuello: cráneo, ceja, hocico con dientes, colmillos, orejas, collar con púas y quijada abierta."""
    m.part(name, None, offset, [0.0, yaw, roll], [
        cube(-3.5, -4, -6, 7, 7, 6, skull),                 # cráneo
        cube(-4, -4.5, -6.5, 8, 2, 2, brow),                # ceja pesada
        cube(-2, -2, -12, 4, 3, 7, snout),                  # hocico
        cube(-2.5, -1.5, -7, 5, 3, 2, fur),                 # carrillos (ensanchan la base del hocico)
        cube(-1.5, 1, -11.5, 1, 2, 1, fang),                # colmillos de arriba
        cube(0.5, 1, -11.5, 1, 2, 1, fang),
        cube(-3, -3.5, -1, 6, 6, 6, ruff),                  # cuello (entra en la gorguera)
        cube(-3.5, -4, 1.5, 7, 7, 2, collar),               # collar de bronce
        cube(-0.5, -6, 2, 1, 2, 1, spike),                  # púas del collar: arriba y a los lados
        cube(-5.5, -1, 2, 2, 1, 1, spike),
        cube(3.5, -1, 2, 2, 1, 1, spike),
    ])
    for side, sx in (("l", 1), ("r", -1)):
        m.part(f"{name}_ear_{side}", name, [sx * 2.2, -3.5, -2.5], [-0.85, 0.0, sx * 0.3], [
            cube(-1.5, -4, -0.5, 3, 4, 1, ear)])
    m.part("jaw", name, [0, 1, -6], [0.32, 0.0, 0.0], [
        cube(-2, 0, -5.5, 4, 2, 6, jaw),
        cube(-1, -0.5, -4.5, 2, 1, 4, tongue),
        cube(-1.8, -1, -5.2, 1, 1, 1, fang),
        cube(0.8, -1, -5.2, 1, 1, 1, fang),
    ])


def leg(m, name, offset, front, mirror):
    """Pata de dos tramos: hombro o muslo musculoso, caña con codo o corvejón, zarpa ancha y tres garras."""
    if front:
        m.part(name, None, offset, [0, 0, 0], [
            cube(-2, -2, -2.5, 4, 8, 5, fur)])
        m.part(name + "_lower", name, [0, 6, 0], [0, 0, 0], [
            cube(-1.5, -0.5, -1.5, 3, 5, 3, fur),
            cube(-2, 4, -3, 4, 2, 5, paw)])
        foot = name + "_lower"
        fz = -3
    else:
        m.part(name, None, offset, [-0.3, 0, 0], [
            cube(-2.5, -2, -3, 5, 8, 6, fur)])          # anca: el muslo baja hacia adelante
        m.part(name + "_lower", name, [0, 4.5, 0.5], [0.75, 0, 0], [
            cube(-1.5, -0.5, -1.5, 3, 5, 3, fur)])      # pierna hacia atrás hasta el corvejón
        m.part(name + "_foot", name + "_lower", [0, 4.2, 0], [-0.45, 0, 0], [
            cube(-1.5, -0.5, -1.5, 3, 3, 3, fur),
            cube(-2, 2, -3, 4, 2, 5, paw)])
        foot = name + "_foot"
        fz = -3
    claws = [cube(cx, 5 if front else 3, fz - 1, 1, 1, 1, claw) for cx in (-1.9, -0.5, 0.9)]
    m.parts[-1]["cubes"].extend(claws)
    return foot


def build():
    m = Model()
    # Tronco: pecho alto y ancho, cintura recogida, cadera más baja (lomo encorvado de bestia).
    m.part("body", None, [0, 10, 0], [0, 0, 0], [
        cube(-5.5, -6, -9, 11, 10, 9, fur),                 # pecho
        cube(-4.5, -4, -0.5, 9, 7, 6, fur),                 # cintura
        cube(-5, -4.5, 5, 10, 8, 5, fur),                   # cadera
        cube(-6.5, -7.5, -10.5, 13, 10, 5, ruff),           # gorguera erizada alrededor de los cuellos
        cube(-4.5, -7, -4, 9, 2, 5, ruff),                  # joroba de la cruz
    ])
    # Cresta de cerdas a lo largo del lomo (plano) y mechones en los costados de la gorguera.
    m.part("mane", "body", [0, -7, -4], [0, 0, 0], [
        cube(0, -4, 0, 0, 4, 14, bristles)])
    for sx in (1, -1):
        m.part(f"ruff_tuft_{'l' if sx > 0 else 'r'}", "body", [sx * 6.5, -4, -8], [0, 0, sx * -0.5], [
            cube(0, -4, 0, 0, 4, 6, bristles)])
    dog_head(m, "head", [0, 4.5, -9.8], 0.0)
    dog_head(m, "left_head", [6, 6.2, -9], -0.55, -0.12)
    dog_head(m, "right_head", [-6, 6.2, -9], 0.55, 0.12)
    # Cola de serpiente (Apolodoro: «tenía cola de serpiente»): nace gruesa de la cadera como la cola de un perro y se
    # vuelve un cuerpo de serpiente escamado que se afina en cuatro tramos hasta una punta fina, sin cabeza.
    m.part("tail", "body", [0, -2, 9.5], [-0.3, 0, 0], [cube(-2, -2, -1, 4, 4, 7, scales)])
    m.part("tail_mid", "tail", [0, 0, 5.5], [0.7, 0, 0], [cube(-1.5, -1.5, 0, 3, 3, 6, scales)])
    m.part("tail_tip", "tail_mid", [0, 0, 5.5], [0.55, 0, 0], [cube(-1, -1, 0, 2, 2, 5, scales)])
    m.part("tail_end", "tail_tip", [0, 0, 4.5], [-0.5, 0, 0], [cube(-0.5, -0.5, 0, 1, 1, 4, scales)])
    if BACK_SNAKES:
        for i, (x, z) in enumerate(((-2.5, -6), (2.5, -6), (0, -3), (-2, 0), (2, 0))):
            m.part(f"back_snake{i}", "body", [x, -7, z], [-0.3, 0, x * 0.08], [
                cube(-0.5, -4, -0.5, 1, 4, 1, scales), cube(-1, -5, -2, 2, 1, 3, snake_head)])
    leg(m, "left_front_leg", [3.8, 12, -5], True, False)
    leg(m, "right_front_leg", [-3.8, 12, -5], True, True)
    leg(m, "left_hind_leg", [3.6, 11.77, 7], False, False)
    leg(m, "right_hind_leg", [-3.6, 11.77, 7], False, True)
    return m


NOTES = "\n".join([
    "Tamaño: escala 1,7 → 2,0 y cuerpo más grande: de 2,4 × 2,4 × 4,2 a 3,5 × 3,3 × 5,8 bloques (ancho × alto × largo); 28 → 91 cubos.",
    "Tres cabezas de cráneo ancho con ceja pesada, hocico largo, carrillos, colmillos arriba y abajo, lengua y orejas echadas atrás.",
    "Fauces de brasa: paladar y quijada brillan; ojos rojo-dorado al frente y a los lados.",
    "Cuellos largos dentro de una gorguera de pelo erizado, joroba en la cruz y cresta de cerdas en el lomo.",
    "Patas de dos tramos: hombro y codo adelante, anca y corvejón atrás, zarpas anchas con tres garras.",
    "La cola deja de ser un rabo de perro: es una cola de serpiente escamada que se afina hasta la punta (sin cabeza).",
    "Textura 128 px: pelo de sombra con mechones, escamas verdes con vientre de placas, collares de hierro con púas.",
    "Fiel al mito: tres cabezas de perro y cola de serpiente (Apolodoro); fiera del Hades, de fauces temibles (Hesíodo).",
    "Licencia de juego: fauces y ojos de brasa; collares de hierro con púas (por la reliquia, el «Collar de Cerbero»).",
    "Sin las serpientes del lomo de Apolodoro, porque el usuario pidió quitarlas (están listas con BACK_SNAKES).",
    "Cultura pop: nada.",
    "Animación propuesta: las tres quijadas gruñen a destiempo, cabezas laterales que se balancean, orejas que se aplastan al atacar, cola de serpiente que ondea, patas con codo y corvejón que se doblan al trotar.",
])
