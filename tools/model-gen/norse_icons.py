"""Íconos 16x16 nórdicos (tandas 1 y 2), con
la paleta y el contorno por material de greece_icons.py.

Uso: python norse_icons.py <carpeta_texturas_item>
"""
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from greece_icons import grid, outline, save  # noqa: E402


def blank():
    return [['.'] * 16 for _ in range(16)]


def mjolnir():
    """Mjölnir en diagonal como las herramientas vanilla (mango abajo a la izquierda, cabeza arriba a la derecha),
    porque en la mano se sostiene como un hacha: cabeza de hierro con banda de nudos de oro y mango corto de cuero."""
    c = [['.'] * 16 for _ in range(16)]
    cx, cy = 10.0, 5.0
    du = (1 / math.sqrt(2), -1 / math.sqrt(2))   # eje del mango (hacia arriba a la derecha)
    dv = (1 / math.sqrt(2), 1 / math.sqrt(2))    # eje largo de la cabeza
    for y in range(16):
        for x in range(16):
            px, py = x + 0.5 - cx, y + 0.5 - cy
            u = px * du[0] + py * du[1]
            v = px * dv[0] + py * dv[1]
            if abs(u) <= 2.3 and abs(v) <= 4.6:
                light = -(px + py) / 6
                ch = 'I' if light > 0.5 else 'i' if light > -0.4 else 'o'
                if abs(u) < 0.75 and abs(v) < 3.6:
                    ch = 'g' if (x + y) % 2 else 'd'
                c[y][x] = ch
    for i in range(6):
        x, y = 2 + i, 13 - i
        c[y][x] = 'P' if i % 2 else 'p'
        if c[y][x + 1] == '.':
            c[y][x + 1] = 'u'
    c[14][1], c[14][2], c[13][1] = 'g', 'd', 'd'
    return outline(c)


# Járngreipr: guantelete de hierro con puño de oro.
JARNGREIPR = grid([
    "................",
    "................",
    ".....I.I.I......",
    "....IiIiIiI.....",
    "....IiIiIiI.....",
    "..I.IiiiiiI.....",
    "..IiIiiiiiI.....",
    "...IiiiiiiI.....",
    "....IiiiiiO.....",
    "....ooooooO.....",
    "...gooooooOg....",
    "...oOOOOOOOo....",
    "...oooooooo.....",
    "................",
    "................",
    "................",
])

# Megingjörð: cinturón de cuero con hebilla de oro trenzada.
MEGINGJORD = grid([
    "................",
    "................",
    "................",
    "................",
    "................",
    ".PPPPPgGGgPPPPP.",
    ".pPpPpgdDgpPpPp.",
    ".uuuuugGGguuuuu.",
    "..........uP....",
    "..........Pu....",
    "..........uP....",
    "................",
    "................",
    "................",
    "................",
    "................",
])


def gungnir():
    """Lanza de Odín en diagonal: asta de fresno, cuello con runas de oro y la punta de plata en hoja."""
    c = blank()
    for i in range(9):
        x, y = 2 + i, 14 - i
        c[y][x] = 'P' if i % 2 else 'p'
    for x, y, ch in [(11, 5, 'g'), (10, 5, 'd'), (11, 4, 'M'), (12, 4, 'm'), (12, 3, 'M'), (13, 3, 'm'), (13, 2, 'M'),
                     (14, 1, 'M'), (12, 5, 'a'), (11, 3, 'a'), (14, 2, 'a')]:
        c[y][x] = ch
    return outline(c)


def draupnir():
    """Anillo de oro y, debajo, las gotas de los ocho anillos que caen cada novena noche."""
    c = blank()
    cx, cy = 7.5, 6.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if 3.0 <= d <= 5.2:
                light = -(x - cx + y - cy) / 7
                c[y][x] = 'G' if light > 0.4 else 'g' if light > -0.4 else 'd'
    for x, y in [(4, 13), (7, 14), (11, 13)]:
        c[y][x] = 'g'
        c[y - 1][x] = 'G'
    return outline(c)


def gjallarhorn():
    """Cuerno curvo de Heimdall: boca ancha abajo a la izquierda, punta arriba a la derecha y bandas de oro."""
    c = blank()
    for k in range(40):
        a = math.pi + k / 39 * (math.pi / 2)
        cx, cy = 13.0, 13.0
        r = 9.5
        x, y = cx + r * math.cos(a), cy + r * math.sin(a)
        thick = 2.6 - 1.8 * k / 39
        for yy in range(16):
            for xx in range(16):
                if math.hypot(xx - x, yy - y) <= thick / 2 + 0.3:
                    band = k in (8, 9, 20, 21, 31)
                    c[yy][xx] = 'g' if band else ('W' if (xx + yy) % 5 == 0 else 'w' if yy < y else 'L')
    return outline(c)


def gleipnir():
    """Gleipnir: cinta de seda fina, suave como un listón, enroscada en ocho."""
    c = blank()
    for k in range(60):
        t = k / 59 * 2 * math.pi
        x = 7.5 + 5.5 * math.sin(t)
        y = 7.5 + 5.5 * math.sin(t) * math.cos(t)
        xi, yi = int(round(x)), int(round(y))
        c[yi][xi] = '1' if k % 3 else 'W'
        if yi + 1 < 16:
            c[yi + 1][xi] = '2'
    return outline(c)


IDUNN = grid([
    "................",
    "....PPPPPPP.....",
    "...P.......P....",
    "....V....V......",
    "...gGgd.gGgd....",
    "...gggd.gggd....",
    "..uPPPPPPPPPPu..",
    "..PpPpPpPpPpPp..",
    "...pPpPpPpPpP...",
    "...PpPpPpPpPp...",
    "....uuuuuuuu....",
    "................",
    "................",
    "................",
    "................",
    "................",
])


def horseshoe():
    """Herradura de Sleipnir: herradura de hierro con clavos de oro y runas que brillan."""
    c = blank()
    cx, cy = 7.5, 6.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if 3.4 <= d <= 6.0 and (y <= 7 or abs(x - cx) > 3.0) and y <= 13:
                light = -(x - cx + y - cy) / 7
                c[y][x] = 'I' if light > 0.4 else 'i' if light > -0.4 else 'o'
    for x, y in [(3, 5), (12, 5), (2, 9), (13, 9), (7, 1), (9, 1)]:
        c[y][x] = 'g'
    return outline(c)


# Huginn y Muninn: Pensamiento (abajo, mira a la izquierda) y Memoria (arriba, mira a la derecha), posados.
RAVENS = grid([
    "..........OOO...",
    ".........OoYoOii",
    "........OooooO..",
    ".......OOooooO..",
    "......OOoooOO...",
    ".....OOoooOO....",
    "......O..d.d....",
    "...OOO..........",
    "iiOoYoO.........",
    "..OoooooO.......",
    "..OoooooOO......",
    "...OOoooooOO....",
    "....OOoooooOO...",
    ".....OOOoooOOO..",
    "......d.d.......",
    "................",
])


def brisingamen():
    """Brísingamen: collar de oro de Freyja con cuatro ámbares (los cuatro enanos que lo forjaron)."""
    c = blank()
    cx, cy = 7.5, 3.0
    for y in range(16):
        for x in range(16):
            d = math.hypot((x - cx) / 1.1, y - cy)
            if 6.0 <= d <= 7.0 and y >= 2:
                c[y][x] = 'g' if (x + y) % 2 else 'd'
    for x, y in [(3, 7), (5, 9), (10, 9), (12, 7)]:
        c[y][x] = 'Y'
        c[y + 1][x] = 'b'
    for x, y, ch in [(7, 10, 'G'), (8, 10, 'g'), (6, 11, 'g'), (7, 11, 'Y'), (8, 11, 'Y'), (9, 11, 'g'), (7, 12, 'b'),
                     (8, 12, 'n')]:
        c[y][x] = ch
    return outline(c)


# Skíðblaðnir: el barco de Freyr, con la vela recogida y la proa tallada.
SHIP = grid([
    "................",
    ".......W........",
    ".......Ww.......",
    ".......Www......",
    ".......Wwww.....",
    ".......WwwwL....",
    ".......P........",
    "..P....P.....P..",
    "..PP...P....PP..",
    "..pPPPPPPPPPPp..",
    "...pPrPrPrPrp...",
    "....pppppppp....",
    "................",
    "................",
    "................",
    "................",
])

# Cabeza de Mímir, fiel a la Saga de los Ynglingos: la cabeza cortada del sabio, untada con hierbas para que no se
# pudriera (hojas en el pelo y la barba) y con los encantamientos de Odín (runas de oro en la frente). Ojos abiertos:
# todavía habla. Barba y pelo grises de anciano.
MIMIR = grid([
    "................",
    "................",
    ".....wWWWWw.....",
    "....wWBBBBWw....",
    "...VBBBBBBBBw...",
    "...wBBgBBgBBv...",
    "...BBLlBBLlBb...",
    "...BBBBnBBBBb...",
    "...VWWBnnBWWv...",
    "...vWWWwwWWWV...",
    "...VwWWWWWWwv...",
    "....vwWWWWwV....",
    ".....VwWWwv.....",
    "......nbbn......",
    "................",
    "................",
])


def andvaranaut():
    """Andvaranaut: anillo de oro con la gema roja de la maldición."""
    c = blank()
    cx, cy = 7.5, 8.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if 3.2 <= d <= 5.0:
                light = -(x - cx + y - cy) / 6
                c[y][x] = 'G' if light > 0.4 else 'g' if light > -0.4 else 'd'
    for x, y, ch in [(6, 3, 'x'), (7, 3, 'r'), (8, 3, 'r'), (9, 3, 'x'), (6, 2, 'r'), (7, 2, 'R'), (8, 2, 'r'), (9, 2, 'x'),
                     (7, 1, 'x'), (8, 1, 'x')]:
        c[y][x] = ch
    return outline(c)


# Óðrerir: cuerno de beber con aro de oro, lleno de hidromiel.
ODRERIR = grid([
    "................",
    "................",
    "..........gG....",
    ".........gYYg...",
    "........gYYYg...",
    ".......wWYYg....",
    "......wWWwg.....",
    ".....wWWwL......",
    "....wWWwL.......",
    "...wWWwL........",
    "..wWwLL.........",
    "..WwL...........",
    "..L.............",
    "................",
    "................",
    "................",
])


ICONS = {"mjolnir": mjolnir, "jarngreipr": lambda: outline(JARNGREIPR), "megingjord": lambda: outline(MEGINGJORD),
         "gungnir": gungnir, "draupnir": draupnir, "gjallarhorn": gjallarhorn, "gleipnir": gleipnir,
         "idunn_apples": lambda: outline(IDUNN),
         "sleipnir_horseshoe": horseshoe, "odin_ravens": lambda: outline(RAVENS), "brisingamen": brisingamen,
         "skidbladnir": lambda: outline(SHIP), "mimir_head": lambda: outline(MIMIR), "andvaranaut": andvaranaut,
         "odrerir": lambda: outline(ODRERIR)}

if __name__ == "__main__":
    out = sys.argv[1]
    for name, make in ICONS.items():
        save(out, name, make())
    print("ok:", ", ".join(ICONS))
