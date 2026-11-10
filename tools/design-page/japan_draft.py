"""Borrador de la tanda 1 japonesa: íconos 16x16 y modelos puestos (magatama al cuello, tambor de Raijin a la espalda).
No toca el mod. Uso: python japan_draft.py <salida.json> [<vista.png>]"""
import json
import math
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import china_draft as CD  # noqa: E402
from china_draft import PAL, SHINY, blank, box, glint, grid, outline, png_b64  # noqa: E402,F401
from china2_draft import line  # noqa: E402


def kusanagi():
    """Kusanagi: espada recta de doble filo (tsurugi antigua, no katana), guarda de oro, empuñadura oscura y pomo de
    anillo."""
    c = blank()
    line(c, 5, 10, 14, 1, 'W')
    line(c, 6, 10, 14, 2, 'w', only_empty=True)
    line(c, 5, 9, 13, 1, 'L', only_empty=True)
    c[1][14] = 'M'
    for x, y, ch in [(3, 9, 'g'), (4, 10, 'G'), (5, 11, 'g'), (6, 12, 'd'), (4, 9, 'd')]:
        c[y][x] = ch
    line(c, 1, 14, 4, 11, 'o')
    for x, y in [(2, 13), (3, 12)]:
        c[y][x] = 'i'
    for x, y, ch in [(0, 15, 'g'), (1, 15, 'd'), (0, 14, 'G')]:
        c[y][x] = ch
    return outline(c)


def yata_mirror():
    """Yata no Kagami: espejo redondo de bronce con borde labrado, la cara brillante con un destello de sol, y la perilla
    de arriba con un cordón rojo."""
    c = blank()
    cx, cy = 7.5, 8.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d <= 6.2:
                if d > 5.0:
                    c[y][x] = 'b' if (x + y) % 2 else 'n'
                else:
                    light = -((x - cx) + (y - cy)) / 8
                    c[y][x] = 'W' if light > 0.25 else '6' if light > -0.15 else '7'
    for x, y in [(5, 6), (6, 5), (5, 5)]:
        c[y][x] = 'M'
    for x, y, ch in [(7, 1, 'r'), (8, 1, 'r'), (7, 2, 'n'), (8, 2, 'b'), (6, 0, 'x'), (9, 0, 'x')]:
        c[y][x] = ch
    return outline(c)


# Yasakani no Magatama: la joya curva de jade en forma de coma (cabeza redonda con su agujero arriba a la izquierda y la
# cola que se curva hacia abajo), colgada de un cordón de oro que pasa por el agujero.
MAGATAMA = grid([
    "......gg........",
    ".....g..g.......",
    "......gg........",
    ".....3333.......",
    "...3M3333333....",
    "..3333..33333...",
    "..333....33333..",
    "..3333..3333334.",
    "...33333333334..",
    "....333333334...",
    ".........33344..",
    ".........33444..",
    "........33444...",
    "......33444.....",
    ".....4444.......",
    "................",
])


def magatama():
    return outline(MAGATAMA)


def raijin_drum():
    """Tambor de Raijin: un taiko rojo laqueado con tachuelas de oro y el parche blanco con el mitsudomoe (tres comas)
    del trueno, y un rayo amarillo."""
    c = blank()
    cx, cy = 7.5, 8.0
    for y in range(16):
        for x in range(16):
            d = math.hypot((x - cx) / 1.0, (y - cy) / 1.0)
            if d <= 6.4:
                if d > 5.4:
                    c[y][x] = 'g' if (round(math.atan2(y - cy, x - cx) * 3) % 2) else 'r'
                elif d > 4.4:
                    c[y][x] = 'x'
                else:
                    c[y][x] = 'W' if d < 4.0 else 'w'
    for k in range(3):
        a = 2 * math.pi * k / 3
        for t in range(6):
            r = 0.6 + t * 0.55
            ang = a + t * 0.45
            x, y = round(cx + r * math.cos(ang)), round(cy + r * math.sin(ang))
            c[y][x] = 'r' if t < 5 else 'x'
    for x, y in [(13, 0), (12, 1), (13, 2), (12, 3)]:
        c[y][x] = 'Y'
    return outline(c)


def kanabo():
    """Kanabō del oni: maza larga de hierro negro, gruesa en la punta, con tachuelas, y el mango envuelto en tela roja."""
    c = blank()
    for i in range(40):
        t = i / 39
        x, y = 2 + 11 * t, 13 - 11 * t
        w = 0.6 + 1.6 * t
        for yy in range(16):
            for xx in range(16):
                if math.hypot(xx - x, yy - y) <= w:
                    c[yy][xx] = 'i' if (xx - x) - (yy - y) < 0 else 'o'
    for x, y in [(8, 6), (10, 4), (12, 2), (11, 6), (13, 4), (9, 3), (7, 8)]:
        if c[y][x] != '.':
            c[y][x] = 'I'
    for x, y, ch in [(1, 14, 'r'), (2, 13, 'R'), (3, 12, 'r'), (0, 15, 'x'), (2, 14, 'x'), (3, 13, 'x')]:
        c[y][x] = ch
    return outline(c)


def star_ball():
    """Hoshi no tama del kitsune: la esfera blanca con forma de cebolla y la llama de fuego fatuo azul en la punta."""
    c = blank()
    cx, cy = 7.5, 10.0
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, (y - cy) * 1.1)
            if d <= 4.6:
                light = -((x - cx) + (y - cy)) / 6
                c[y][x] = 'M' if light > 0.4 else 'W' if light > -0.1 else 'w' if light > -0.45 else '7'
    for y, xs in [(5, (6, 7, 8, 9)), (4, (7, 8)), (3, (8,))]:
        for x in xs:
            c[y][x] = 'w'
    c = outline(c)
    for x, y, ch in [(8, 2, '6'), (9, 1, '7'), (8, 1, '6'), (9, 0, 'L'), (7, 2, '7'), (10, 2, 'L'), (10, 1, '7')]:
        if c[y][x] == '.':
            c[y][x] = ch
    return c


def tide_jewels():
    """Joyas de la marea: kanju (la que baja el mar, azul profundo) y manju (la que lo sube, verde agua), con una ola."""
    c = blank()
    for cx, cy, cols in [(5.0, 6.0, ('6', 'l', 'E')), (10.5, 10.0, ('3', '4', '5'))]:
        for y in range(16):
            for x in range(16):
                d = math.hypot(x - cx, y - cy)
                if d <= 3.6:
                    light = -((x - cx) + (y - cy)) / 5
                    c[y][x] = cols[0] if light > 0.3 else cols[1] if light > -0.3 else cols[2]
        c[int(cy) - 2][int(cx) - 1] = 'M'
    for x, y, ch in [(1, 13, 'w'), (2, 12, 'W'), (3, 12, 'w'), (4, 13, 'L'), (5, 14, 'L'), (2, 14, 'L'), (3, 14, 'L')]:
        c[y][x] = ch
    return outline(c)


def lucky_mallet():
    """Mazo de la suerte (uchide no kozuchi): mazo de madera roja laqueada con bandas de oro y la joya del tesoro, y el
    mango con una borla."""
    c = blank()
    for y in range(16):
        for x in range(16):
            u, v = (x - 9.5), (y - 5.0)
            ru, rv = (u - v) / math.sqrt(2), (u + v) / math.sqrt(2)
            if abs(ru) <= 4.6 and abs(rv) <= 2.8:
                band = abs(abs(ru) - 3.4) < 0.7
                c[y][x] = 'g' if band else ('R' if rv < -0.8 else 'r' if rv < 1.0 else 'x')
    for x, y, ch in [(9, 5, 'Y'), (10, 4, 'Y'), (9, 4, 'M'), (10, 5, 'g')]:
        c[y][x] = ch
    line(c, 2, 13, 7, 8, 'P')
    line(c, 3, 13, 7, 9, 'p', only_empty=True)
    for x, y, ch in [(1, 14, 'r'), (1, 15, 'x'), (0, 15, 'R')]:
        c[y][x] = ch
    return outline(c)


# Kibi dango: bolsita de tela con un melocotón bordado y tres bolitas de mijo asomando.
KIBI = grid([
    "................",
    ".....99.99......",
    "....98898889....",
    "....9888898.....",
    "...t9t99t9t.....",
    "...tTTTTTTTt....",
    "..tTTTTTTTTTt...",
    "..TTTTKKTTTTt...",
    "..TTTKKCKTTTt...",
    "..TTTKCCCTTTt...",
    "..TTTTCvTTTTt...",
    "..tTTTTTTTTTt...",
    "...tTTTTTTTt....",
    "....tttttttt....",
    "................",
    "................",
])


def urashima_rod():
    """Caña de Urashima: caña de bambú con sus nudos, el sedal y el flotador rojo, como la caña de pescar de Minecraft
    (curva arriba a la izquierda, sedal colgando)."""
    c = blank()
    for i in range(50):
        t = i / 49
        x = 1 + 12 * t
        y = 14 - 12 * t + 2.5 * math.sin(t * math.pi) * 0.0
        xi, yi = round(x), round(y)
        c[yi][xi] = 'v' if int(t * 6) % 2 == 0 else 'V'
        if 0 <= yi + 1 < 16 and c[yi + 1][xi] == '.':
            c[yi + 1][xi] = 'y'
    for k in (2, 4, 6):
        t = k / 6.5
        xi, yi = round(1 + 12 * t), round(14 - 12 * t)
        c[yi][xi] = 'T'
    for y in range(3, 12):
        c[y][14] = 'm' if y < 11 else '.'
    for x, y, ch in [(14, 11, 'R'), (14, 12, 'W'), (13, 11, 'r'), (15, 11, 'r')]:
        c[y][x] = ch
    return outline(c)


ICONS = {"kusanagi": kusanagi, "yata_mirror": yata_mirror, "yasakani_magatama": magatama,
         "raijin_drum": raijin_drum, "oni_kanabo": kanabo, "kitsune_star_ball": star_ball, "tide_jewels": tide_jewels,
         "lucky_mallet": lucky_mallet, "kibi_dango": lambda: outline(KIBI), "urashima_rod": urashima_rod}


def magatama_worn():
    """Cuerpo (cuello en y 14, pecho en z 10): cordón de oro y la magatama verde al frente."""
    e = [box([3.5, 13.2, 5.45], [12.5, 13.9, 6.05], "gold", "cordón atrás"),
         box([3.5, 13.2, 6.05], [4.05, 13.9, 10.5], "gold", "cordón der."),
         box([11.95, 13.2, 6.05], [12.5, 13.9, 10.5], "gold", "cordón izq.")]
    e += [box([7.0, 10.4, 10.5], [9.2, 12.4, 11.2], "jade", "magatama"),
          box([8.2, 9.6, 10.55], [9.2, 10.4, 11.15], "jade", "cola"),
          box([7.4, 12.4, 10.55], [8.2, 13.3, 11.0], "gold", "anilla")]
    return e


def drum_worn():
    """Espalda (z 6 es la espalda): un taiko rojo con parche blanco, tachuelas de oro, en diagonal sobre el hombro."""
    e = [box([4.5, 6.5, 3.2], [11.5, 13.5, 5.5], "fire_d", "tambor"),
         box([5.0, 7.0, 3.0], [11.0, 13.0, 3.2], "white", "parche"),
         box([4.3, 6.3, 3.6], [11.7, 6.7, 5.1], "gold", "aro abajo"),
         box([4.3, 13.3, 3.6], [11.7, 13.7, 5.1], "gold", "aro arriba"),
         box([3.6, 13.7, 4.1], [12.4, 14.2, 4.6], "black", "palillo")]
    return e


WORN = {"yasakani_magatama": ("body", magatama_worn), "raijin_drum": ("body", drum_worn)}

if __name__ == "__main__":
    out = {"icons": {}, "worn": {}, "palette": CD.palette_b64()}
    sheet = []
    for name, make in ICONS.items():
        chars = make()
        img = [[PAL[ch] for ch in row] for row in chars]
        sheet.append(img)
        strip, meta = glint(img, lambda x, y: chars[y][x] in SHINY)
        out["icons"][name] = {"png": png_b64(strip), "frames": meta["animation"]["frames"] if meta else None}
    for name, (part, make) in WORN.items():
        out["worn"][name] = {"part": part, "elements": make()}
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8"))
    if len(sys.argv) > 2:
        from mclib import H, png
        S, bg = 12, H('#8b8b8b')
        prev = [[bg] * (len(sheet) * 17 * S) for _ in range(16 * S)]
        for i, img in enumerate(sheet):
            for y in range(16 * S):
                for x in range(16 * S):
                    p = img[y // S][x // S]
                    if p[3]:
                        prev[y][i * 17 * S + x] = p
        png(sys.argv[2], prev)
    print("ok")
