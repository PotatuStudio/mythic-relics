"""Íconos 16x16 de Roma, con la paleta y el contorno por material de greece_icons.py.

Uso: python rome_icons.py <carpeta_texturas_item>
"""
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from greece_icons import grid, outline, save  # noqa: E402


def bulla():
    """Relicario redondo de oro colgado de un cordón, con la cabeza de la loba de perfil en relieve."""
    c = [['.'] * 16 for _ in range(16)]
    for i in range(4):
        c[1 + i][2 + i] = 'u'
        c[1 + i][13 - i] = 'u'
    c[4][7], c[4][8] = 'd', 'd'
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - 7.5, y - 9.5)
            if d <= 4.7:
                light = -(x - 7.5 + y - 9.5) / 6
                c[y][x] = 'd' if d > 3.9 else ('G' if light > 0.45 else 'g' if light > -0.4 else 'd')
    for x, y in [(8, 7), (10, 7), (7, 8), (8, 8), (9, 8), (10, 8), (5, 9), (6, 9), (7, 9), (8, 9), (9, 9), (10, 9),
                 (7, 10), (8, 10), (9, 10), (10, 10), (8, 11), (10, 11)]:
        c[y][x] = 'D'
    c[8][9] = 'G'   # el ojo
    return outline(c)


def ancile():
    """Escudo en ocho con borde de oro, nervio central y umbo."""
    c = [['.'] * 16 for _ in range(16)]
    inside = lambda x, y: ((math.hypot(x - 7.5, y - 4.3) <= 4.3 or math.hypot(x - 7.5, y - 11.0) <= 4.5)
                           and not (7 <= y <= 8 and abs(x - 7.5) > 2.6))
    for y in range(16):
        for x in range(16):
            if not inside(x, y):
                continue
            edge = not all(inside(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
            light = -(x - 7.5) / 5 - (y - 7.5) / 9
            if edge:
                c[y][x] = 'g' if light > 0 else 'd'
            else:
                c[y][x] = 'B' if light > 0.35 else 'b' if light > -0.35 else 'n'
    for y in range(2, 14):
        if c[y][8] in 'Bbn':
            c[y][8] = 'n'
        if c[y][7] in 'Bbn':
            c[y][7] = 'B'
    for x, y, ch in ((7, 7, 'G'), (8, 7, 'g'), (7, 8, 'g'), (8, 8, 'd')):
        c[y][x] = ch
    return outline(c)


# Paladio: estatuilla de madera de Palas con yelmo dorado, escudo de bronce y lanza.
PALLADIUM = grid([
    "................",
    "......gg........",
    ".....pPPp...M...",
    ".....pPPp...a...",
    "......pp....a...",
    "....pPPPPp..a...",
    "..bbpPPPPPppa...",
    ".bBbbPPPPp..a...",
    ".bBbbPPPPp..a...",
    "..bbpPPPPp..a...",
    "....pPPPPp..a...",
    "....pPPPPp..a...",
    "...pPPPPPPp.a...",
    "...uuuuuuuu.....",
    "................",
    "................",
])


# Llave de Jano: el anillo son dos caras de perfil (una mira a cada lado); paletón abajo.
JANUS = grid([
    "................",
    "....bBBBBBBb....",
    "...bBnBBbBnBb...",
    "..bBBBBBbBBBBb..",
    "...bBBnBbBnBb...",
    "....bBBBbBBb....",
    ".....bbbbbb.....",
    ".......Bb.......",
    ".......Bb.......",
    ".......Bb.......",
    ".......Bbbn.....",
    ".......Bb.......",
    ".......Bbbn.....",
    ".......Bb.......",
    ".......nn.......",
    "................",
])


# Lámpara de Vesta: lucerna de terracota con asa y la llama que no se apaga.
VESTA = grid([
    "................",
    "................",
    "............Y...",
    "...........YYr..",
    "...........RYr..",
    "...........rRr..",
    "............x...",
    "..hh..hjjh..ch..",
    ".h..KCCCCCCCCCh.",
    ".h.KCCCCCCCCCCCh",
    "..hcCCCCCCCCCch.",
    "....ccCCCCCcch..",
    ".....hhhhhhh....",
    "................",
    "................",
    "................",
])


# Libros sibilinos: tres rollos atados con cordón rojo; el de abajo, quemado (la sibila quemaba los que no compraban).
SIBYL = grid([
    "................",
    "................",
    "..pwWWWWWWWWWp..",
    "..pWWWWrWWWWwp..",
    "..pwwwwrwwwwwp..",
    "................",
    ".pwWWWWWWWWWp...",
    ".pWWWWWrWWWwp...",
    ".pwwwwwrwwwxp...",
    "........1.2.....",
    "...pwWWWWxXX....",
    "...pWWWrWxX.....",
    "...pwwwrwx......",
    "................",
    "................",
    "................",
])


def lituus():
    """Lítuo del augur: bastón de oro con la punta enroscada en espiral."""
    c = [['.'] * 16 for _ in range(16)]
    for x, y, ch in [(8, 1, 'G'), (9, 1, 'G'), (10, 1, 'g'), (11, 1, 'g'), (7, 2, 'g'), (7, 3, 'g'), (12, 2, 'g'),
                     (12, 3, 'g'), (12, 4, 'd'), (8, 4, 'd'), (9, 4, 'd'), (10, 3, 'd')]:
        c[y][x] = ch
    for i in range(10):
        x, y = 11 - i, 5 + i
        c[y][x] = 'g'
        if x + 1 <= 12:
            c[y][x + 1] = 'd'
    return outline(c)


ICONS = {"wolf_bulla": bulla, "ancile": ancile, "palladium": lambda: outline(PALLADIUM),
         "janus_key": lambda: outline(JANUS), "vesta_lamp": lambda: outline(VESTA),
         "sibylline_books": lambda: outline(SIBYL), "augur_lituus": lituus}

if __name__ == "__main__":
    out = sys.argv[1]
    for name, make in ICONS.items():
        save(out, name, make())
    print("ok:", ", ".join(ICONS))
