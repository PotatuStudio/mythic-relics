"""Borrador de la tanda 1 china: íconos 16x16 y modelos puestos (ruedas en los pies, piedra en el cinturón).
No toca el mod. Uso: python china_draft.py <salida.json> [<vista.png>]"""
import base64
import json
import math
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, r"C:\dev\mods\mythic-relics\tools\model-gen")
import greece_icons as GI  # noqa: E402
from glint import glint  # noqa: E402
from mclib import H, T, png  # noqa: E402

grid, outline, PAL = GI.grid, GI.outline, GI.PAL
SHINY = set(GI.SHINY)


def blank():
    return [['.'] * 16 for _ in range(16)]


def ruyi():
    """Ruyi Jingu Bang: barra de hierro negro con aros de oro en los dos extremos (no roja: eso es de Dragon Ball)."""
    c = blank()
    for x in range(1, 15):
        y = 15 - x
        for xx, yy, ch in [(x, y, 'i'), (x, y - 1, 'I'), (x + 1, y, 'o')]:
            if 0 <= xx < 16 and 0 <= yy < 16:
                gold = xx <= 3 or xx >= 12
                c[yy][xx] = {'i': 'g', 'I': 'G', 'o': 'd'}[ch] if gold else ch
    return outline(c)


# Pelos del Rey Mono: un mechón de pelo dorado que, al soplarlo, se vuelve nubecitas con monitos.
HAIRS = grid([
    "................",
    "..........WWW...",
    ".........WbbbW..",
    "........WbKKbW..",
    "........WKjKjW..",
    ".........WKKW...",
    "....WW....WW....",
    "...WbbW.........",
    "...WKKW.........",
    "....WW..........",
    "...b..b.........",
    "..bn.bn.........",
    ".bnnbnb.........",
    ".bnnnnb.........",
    "..gGgd..........",
    "..nnn...........",
])


# Calabaza púrpura y dorada (zijin hong hulu): calabaza roja con cintura de oro, tapón y borla.
GOURD = grid([
    "................",
    ".......uu.......",
    "......gGGg......",
    "......rRRr......",
    ".....rRRrrx.....",
    ".....rRrrrx.....",
    "......rrrx...2..",
    "......gGgd..21..",
    ".....rRRrrx.2...",
    "....rRRrrrrx2...",
    "...rRRrrrrrrx...",
    "...rRrrrrrrrx...",
    "...rrrrrrrrrx...",
    "....rrrrrrrx....",
    ".....xxxxxx.....",
    "................",
])


def fan():
    """Abanico de hoja de plátano: hoja grande ovalada con nervio central y mango de madera, con viento."""
    c = blank()
    cx, cy = 9.0, 5.5
    for y in range(16):
        for x in range(16):
            u, v = (x - cx), (y - cy)
            ru, rv = (u + v) / math.sqrt(2), (u - v) / math.sqrt(2)
            if (ru / 6.2) ** 2 + (rv / 3.6) ** 2 <= 1:
                c[y][x] = 'V' if rv > 1.2 else 'v' if rv > -1.5 else 'y'
    for t in range(-4, 6):
        x, y = int(round(cx + t * 0.7)), int(round(cy - t * 0.7))
        if 0 <= x < 16 and 0 <= y < 16 and c[y][x] != '.':
            c[y][x] = 'S'
            # nervaduras laterales de la hoja
            if t % 2 == 0:
                for k in (1, 2):
                    for xx, yy in ((x + k, y + k), (x - k, y - k)):
                        if 0 <= xx < 16 and 0 <= yy < 16 and c[yy][xx] in 'Vvy':
                            c[yy][xx] = 'y' if c[yy][xx] != 'y' else 'v'
    for x, y in [(4, 11), (3, 12), (2, 13), (1, 14)]:
        c[y][x] = 'P'
        c[y][x + 1] = 'p'
    c[10][5] = 'g'
    c[11][5] = 'd'
    return outline(c)


def pearl():
    """Perla del dragón: la perla llameante de la pintura china, blanca azulada envuelta en lenguas de fuego."""
    c = blank()
    cx, cy = 7.5, 9.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            ang = math.atan2(y - cy, x - cx)
            if d <= 3.6:
                light = -(x - cx + y - cy) / 4.5
                c[y][x] = '6' if light > 0.6 else 'W' if light > 0.0 else '7' if light > -0.6 else 'E'
            elif d <= 4.6:
                c[y][x] = 'Y'
            elif y < cy + 2:
                # lenguas de fuego que suben y se curvan
                tongue = 4.6 + 2.6 * max(0.0, math.cos(ang * 3 + 0.4)) + (1.2 if y < cy - 3 else 0)
                if d <= tongue:
                    c[y][x] = 'R' if d < tongue - 1 else 'r'
    c[8][6] = 'M'
    c[7][7] = 'M'
    return outline(c)


def wheel():
    """Ruedas de viento y fuego (Nezha): aro de oro con rayos y llamas por debajo."""
    c = blank()
    cx, cy = 7.5, 7.0
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            ang = math.atan2(y - cy, x - cx)
            if 4.3 <= d <= 5.6:
                light = -(x - cx + y - cy) / 6
                c[y][x] = 'G' if light > 0.4 else 'g' if light > -0.4 else 'd'
            elif d <= 1.3:
                c[y][x] = 'r'
            elif d < 4.3 and min(abs(math.sin(ang * 3)), 1) < 0.25:
                c[y][x] = 'd'
    for x, h in [(3, 3), (5, 2), (7, 4), (9, 2), (11, 3), (13, 2), (2, 1), (12, 1)]:
        for k in range(h):
            y = 13 + 2 - k
            if 0 <= y < 16 and c[y][x] == '.':
                c[y][x] = 'Y' if k == 0 else 'R' if k == 1 else 'r'
    return outline(c)


# Arco de Hou Yi: arco compuesto chino (con las puntas vueltas hacia afuera), lacado en rojo y oro, con la cuerda.
BOW = grid([
    "................",
    "...........dG...",
    ".........rrrg...",
    ".......rRrr.L...",
    "......rRr..L....",
    ".....rRr..L.....",
    "....rRr..L......",
    "....rr..L.......",
    "....gg.L........",
    "....rrL.........",
    "...rRL..........",
    "...rr...........",
    "..rRr...........",
    "..rr............",
    ".gd.............",
    ".G..............",
])


# Elixir de Chang'e: frasco de jade con tapón rojo y la luna creciente.
ELIXIR = grid([
    "............YY..",
    "...........Y8...",
    "...........Y8...",
    "............YY..",
    "......xx........",
    "......rR........",
    ".....4334.......",
    "......33........",
    ".....3333.......",
    "....333333......",
    "...33W33334.....",
    "...3W333344.....",
    "...33333444.....",
    "....334444......",
    ".....5555.......",
    "................",
])


def stone():
    """Piedra de cinco colores de Nüwa: un canto rodado con vetas de los cinco colores (rojo, amarillo, verde azulado,
    blanco y negro), las piedras que fundió para remendar el cielo."""
    c = blank()
    cx, cy = 7.5, 8.5
    cols = ['R', 'Y', '3', 'W', 'O']
    for y in range(16):
        for x in range(16):
            u, v = (x - cx) / 6.4, (y - cy) / 5.2
            if u * u + v * v <= 1:
                band = int((x * 0.55 + y * 0.9) / 2.2) % 5
                light = -(u + v)
                ch = cols[band]
                if light < -0.7:
                    ch = {'R': 'r', 'Y': 'g', '3': '4', 'W': 'm', 'O': 'o'}[ch]
                c[y][x] = ch
    return outline(c)


ICONS = {"ruyi_jingu_bang": ruyi, "monkey_king_hairs": lambda: outline(HAIRS), "purple_gold_gourd": lambda: outline(GOURD),
         "banana_leaf_fan": fan, "dragon_pearl": pearl, "wind_fire_wheels": wheel, "hou_yi_bow": lambda: outline(BOW),
         "change_elixir": lambda: outline(ELIXIR), "five_color_stone": stone}

COLORS = {"gold_l": "#fff3a8", "gold": "#f5c842", "gold_d": "#c98f1c", "fire_y": "#ffe14a", "fire": "#ff7a2a",
          "fire_d": "#d8392b", "red": "#c42a20", "jade": "#4fc1a8", "white": "#f2f2ee", "black": "#2a2830",
          "yellow": "#ffd23a"}
NAMES = list(COLORS)
SHADE = {"gold": ("gold_l", "gold_d"), "fire": ("fire_y", "fire_d")}


def uv(color):
    i = NAMES.index(color)
    u, v = (i % 8) * 2, (i // 8) * 2
    return [u + 0.5, v + 0.5, u + 1.5, v + 1.5]


def box(frm, to, color, name=""):
    top, bottom = SHADE.get(color, (color, color))
    faces = {s: {"uv": uv(color), "texture": "#p"} for s in ("north", "south", "east", "west")}
    faces["up"] = {"uv": uv(top), "texture": "#p"}
    faces["down"] = {"uv": uv(bottom), "texture": "#p"}
    return {"name": name, "from": list(frm), "to": list(to), "faces": faces}


def wheels_worn():
    """Un aro de oro alrededor del tobillo con rayos hacia afuera y llamas detrás (en cada pierna)."""
    e = []
    y0, y1 = 2.2, 3.2
    e += [box([4.6, y0, 4.6], [11.4, y1, 5.4], "gold", "aro atrás"), box([4.6, y0, 10.6], [11.4, y1, 11.4], "gold", "aro adelante"),
          box([4.6, y0, 5.4], [5.4, y1, 10.6], "gold", "aro der."), box([10.6, y0, 5.4], [11.4, y1, 10.6], "gold", "aro izq.")]
    for x0, z0 in [(5.6, 4.0), (8.8, 4.0), (4.0, 7.4), (11.4, 7.4)]:
        e.append(box([x0, y0 - 0.2, z0], [x0 + 1.6 if z0 < 5 else x0 + 0.6, y1 + 0.2, z0 + 0.6 if z0 < 5 else z0 + 1.2], "gold", "rayo"))
    for x0, h in [(5.0, 2.4), (6.6, 3.4), (8.2, 2.8), (9.8, 3.6)]:
        e.append(box([x0, y1, 3.6], [x0 + 1.2, y1 + h, 4.4], "fire", "llama"))
    return e


def stone_worn():
    return [box([3.6, 2.4, 9.9], [12.4, 3.4, 10.5], "red", "cordón"),
            box([6.8, 1.0, 10.3], [9.2, 3.6, 11.1], "jade", "piedra"),
            box([7.2, 1.4, 11.1], [7.9, 2.2, 11.3], "yellow", "faceta"), box([8.0, 2.4, 11.1], [8.8, 3.2, 11.3], "white", "faceta"),
            box([7.4, 2.6, 11.1], [7.9, 3.2, 11.3], "fire_d", "faceta"), box([8.1, 1.4, 11.1], [8.8, 2.0, 11.3], "black", "faceta")]


WORN = {"wind_fire_wheels": ("legs", wheels_worn), "five_color_stone": ("body", stone_worn)}


def png_b64(img):
    path = os.path.join(HERE, "_tmp.png")
    png(path, img)
    data = base64.b64encode(open(path, "rb").read()).decode()
    os.remove(path)
    return data


def palette_b64():
    img = [[T] * 16 for _ in range(16)]
    for i, name in enumerate(NAMES):
        u, v = (i % 8) * 2, (i // 8) * 2
        for y in range(2):
            for x in range(2):
                img[v + y][u + x] = H(COLORS[name])
    return png_b64(img)


if __name__ == "__main__":
    out = {"icons": {}, "worn": {}, "palette": palette_b64()}
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
