"""Borrador de la tanda 2 japonesa: íconos 16x16 y modelos puestos (máscara de tengu en la cara, plato del kappa en
la cabeza). No toca el mod. Uso: python japan2_draft.py <salida.json> [<vista.png>]"""
import json
import math
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import china_draft as CD  # noqa: E402
from china_draft import PAL, SHINY, blank, box, glint, grid, outline, png_b64  # noqa: E402,F401
from china2_draft import line  # noqa: E402


def wind_bag():
    """Bolsa de vientos de Fūjin: un saco de tela blanca inflado, atado arriba con un cordón verde, del que escapan
    remolinos de viento."""
    c = blank()
    cx, cy = 7.5, 9.5
    for y in range(16):
        for x in range(16):
            u, v = (x - cx) / 5.6, (y - cy) / 4.8
            if u * u + v * v <= 1 and y >= 5:
                light = -(u + v) / 2
                c[y][x] = 'W' if light > 0.2 else 'w' if light > -0.25 else 'L'
    for x in range(6, 10):
        c[4][x] = 'v'
    for x, y in [(6, 3), (7, 2), (8, 2), (9, 3), (7, 3), (8, 3)]:
        c[y][x] = 'w'
    c = outline(c)
    for x, y in [(11, 1), (12, 1), (13, 2), (12, 3), (11, 3), (2, 2), (3, 1), (4, 1), (1, 3), (14, 4)]:
        if c[y][x] == '.':
            c[y][x] = '7'
    return c


# Máscara de tengu: cara roja de nariz larga, cejas blancas y bigote blanco.
TENGU = grid([
    "................",
    "....WWW..WWW....",
    "...RrrrrrrrrR...",
    "..RrWWr..rWWrR..",
    "..rrkWrrrrWkrr..",
    "..rrrrrRRrrrrr..",
    "..rrrrrRRRrrrr..",
    "..rrrrrRRRRrrr..",
    "..rrrrrrRRRRrr..",
    "...rrWWrrRRRRR..",
    "...rWWWWrrRRRRR.",
    "....rWWWWr..RRr.",
    ".....rrrr.......",
    "................",
    "................",
    "................",
])

# Sake de Yashiori: jarra de cerámica crema (tokkuri) con dibujo azul, tapón de madera y una gota.
SAKE = grid([
    "................",
    "......uu........",
    "......PP........",
    ".....9889.......",
    "......88........",
    "......89........",
    ".....8899.......",
    "....88l899......",
    "...88ll899T.....",
    "...8l888l9T.....",
    "...88lll89T.....",
    "...988889TT.....",
    "....9999TT....6.",
    ".....TTTT....676",
    ".............666",
    "................",
])


def kappa_dish():
    """Plato del kappa: plato hondo de cerámica verde lleno de agua que brilla."""
    c = blank()
    cx, cy = 7.5, 8.5
    for y in range(16):
        for x in range(16):
            u, v = (x - cx) / 6.6, (y - cy) / 3.6
            d = u * u + v * v
            if d <= 1:
                if d < 0.55 and y <= cy + 0.5:
                    c[y][x] = '6' if (x + y) % 5 == 0 else '3' if y < cy - 1 else '4'
                else:
                    c[y][x] = 'V' if y < cy else 'v' if y < cy + 2 else 'y'
    c[7][5] = 'W'
    c[7][6] = 'W'
    return outline(c)


# Hoja del tanuki: una hoja verde con su nervio, y una carita de tanuki asomando abajo.
LEAF = grid([
    "...........yv...",
    ".........yvVV...",
    "........yvVVV...",
    ".......yvVVVv...",
    "......yvVVVv....",
    ".....yvSVVv.....",
    ".....yvSVv......",
    "....yvSVv.......",
    "....yvSv........",
    "...uySv.........",
    "..nNnn..........",
    ".nkWnWkn........",
    ".nnNkNnn........",
    "..nnnnn.........",
    "................",
    "................",
])


def kintaro_axe():
    """Hacha de Kintarō (masakari): hoja ancha de acero con el kanji... no: con una banda roja, mango de madera."""
    c = blank()
    line(c, 2, 14, 10, 6, 'P')
    line(c, 3, 14, 10, 7, 'p', only_empty=True)
    for y in range(16):
        for x in range(16):
            u, v = x - 11.0, y - 4.5
            if (u * u) / 9 + (v * v) / 16 <= 1 and x >= 9:
                c[y][x] = 'W' if x > 12 else 'w' if x > 10 else 'a'
    for y in range(1, 9):
        if c[y][9] != '.':
            c[y][9] = 'r'
    return outline(c)


# Muñeca de zashiki-warashi: niña con flequillo negro y kimono rojo.
DOLL = grid([
    "................",
    "......OOOO......",
    ".....OOOOOO.....",
    ".....OOOOOO.....",
    ".....OKKKKO.....",
    ".....OKkKkO.....",
    ".....OKKKKO.....",
    "......RRRR......",
    ".....RrgrRR.....",
    "....RRrgrRrR....",
    "....RRrgrrRR....",
    "....rRggggRr....",
    "....rRRrrRRr....",
    ".....xxxxxx.....",
    "......kk.kk.....",
    "................",
])


def nuboko():
    """Ame-no-nuboko: lanza de punta ancha enjoyada, con gotas de agua que caen de la punta (las que formaron la
    primera isla)."""
    c = blank()
    line(c, 1, 14, 10, 5, 'P')
    line(c, 2, 14, 10, 6, 'p', only_empty=True)
    for x, y, ch in [(9, 5, 'g'), (10, 4, 'G'), (8, 6, 'd')]:
        c[y][x] = ch
    for x, y, ch in [(11, 3, 'W'), (12, 2, 'W'), (13, 1, 'M'), (11, 2, 'w'), (12, 3, 'w'), (10, 2, 'w'), (12, 4, 'w')]:
        c[y][x] = ch
    for x, y, ch in [(10, 3, '3'), (11, 4, 'r')]:
        c[y][x] = ch
    c = outline(c)
    for x, y, ch in [(14, 4, '6'), (14, 5, '7'), (14, 7, '6'), (13, 8, '7'), (15, 9, '6')]:
        if c[y][x] == '.':
            c[y][x] = ch
    return c


def orochi_scale():
    """Escama de Orochi: escama grande verde musgo y oscura, con el borde rojo y una veta como ojo rojo."""
    c = blank()
    cx, cy = 7.5, 7.0
    for y in range(16):
        for x in range(16):
            u, v = (x - cx) / 6.0, (y - cy) / 7.0
            if u * u + v * v <= 1 and y < 14 - abs(x - cx) * 0.4:
                light = -(u + v) / 2
                c[y][x] = 'V' if light > 0.35 else 'v' if light > -0.1 else 'y' if light > -0.45 else 'Z'
    for x, y in [(6, 6), (7, 6), (8, 6), (7, 7)]:
        c[y][x] = 'R'
    c[6][7] = 'Y'
    return outline(c)


ICONS = {"fujin_wind_bag": wind_bag, "tengu_mask": lambda: outline(TENGU), "yashiori_sake": lambda: outline(SAKE),
         "kappa_dish": kappa_dish, "tanuki_leaf": lambda: outline(LEAF), "kintaro_axe": kintaro_axe,
         "zashiki_warashi": lambda: outline(DOLL), "ame_no_nuboko": nuboko, "orochi_scale": orochi_scale}


def tengu_worn():
    """Cara (frente en z 12): máscara roja con la nariz larga hacia adelante y cejas blancas."""
    return [box([4.2, 5.0, 12.55], [11.8, 12.0, 13.05], "fire_d", "máscara"),
            box([7.2, 7.4, 13.05], [8.8, 9.0, 16.0], "red", "nariz"),
            box([4.8, 10.4, 13.05], [7.2, 11.0, 13.25], "white", "ceja der."),
            box([8.8, 10.4, 13.05], [11.2, 11.0, 13.25], "white", "ceja izq.")]


def kappa_worn():
    """Cabeza (arriba en y 12): el plato hondo verde lleno de agua sobre la coronilla."""
    return [box([5.0, 12.75, 5.0], [11.0, 13.35, 11.0], "jade", "plato"),
            box([5.6, 13.35, 5.6], [10.4, 13.55, 10.4], "white", "agua")]


WORN = {"tengu_mask": ("head", tengu_worn), "kappa_dish": ("head", kappa_worn)}

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
