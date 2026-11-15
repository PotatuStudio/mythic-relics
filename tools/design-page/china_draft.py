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


def hairs():
    """Pelos del Rey Mono: tres pelos largos dorados, finos y ondulados, en diagonal y bien separados, con la sombra a
    un lado (sin contorno, para que se lean como pelos) y destellos (la magia de «¡Cambia!»)."""
    c = blank()
    for (x0, y0), (x1, y1), amp, ph in [((0, 10), (9, 1), 0.6, 0.0), ((2, 14), (13, 3), 0.7, 0.8), ((6, 15), (15, 6), 0.6, 1.6)]:
        steps = 60
        dx, dy = x1 - x0, y1 - y0
        n = math.hypot(dx, dy)
        px_, py_ = -dy / n, dx / n
        pts = []
        for i in range(steps + 1):
            t = i / steps
            w = amp * math.sin(t * math.pi * 1.6 + ph)
            xi, yi = int(round(x0 + dx * t + px_ * w)), int(round(y0 + dy * t + py_ * w))
            if 0 <= xi < 16 and 0 <= yi < 16 and (not pts or pts[-1][:2] != (xi, yi)):
                pts.append((xi, yi, t))
        for xi, yi, t in pts:
            c[yi][xi] = 'G' if t > 0.75 else 'g' if t > 0.25 else 'd'
        for xi, yi, t in pts:
            if yi + 1 < 16 and c[yi + 1][xi] == '.':
                c[yi + 1][xi] = 'D' if t < 0.25 else 'd'
    for x, y, ch in [(10, 0, 'W'), (14, 2, 'Y'), (15, 5, 'W')]:
        c[y][x] = ch
    return c


# Calabaza roja de oro púrpura (zijin hong hulu): calabaza roja con adornos de oro rojizo («oro púrpura») y borla roja.
GOURD = grid([
    "................",
    ".......uu.......",
    "......CKKC......",
    "......rRRr......",
    ".....rRRrrx.....",
    ".....rRrrrx.....",
    "......rrrx......",
    "......CKCc......",
    ".....rRRrrx.....",
    "....rRRrrrrx....",
    "...rRRrrrrrrx...",
    "...rRrrrrrrrx...",
    "...rrrrrrrrrx...",
    "....rrrrrrrx....",
    ".....xxxxxx.....",
    "................",
])


def pearl():
    """Perla del dragón: la «perla llameante» (huozhu) del arte chino. Una perla de nácar redonda (blanca con reflejos
    lila y celestes) y tres lenguas de fuego finas que suben desde el borde, sin contorno, para que no la tapen."""
    c = blank()
    cx, cy, r = 7.5, 9.5, 5.2
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d <= r:
                nx, ny = (x - cx) / r, (y - cy) / r
                light = -(nx * 0.65 + ny * 0.75) + (1 - d / r) * 0.5
                if light > 0.75:
                    ch = 'M'
                elif light > 0.4:
                    ch = 'W'
                elif light > 0.05:
                    ch = 'w'
                elif light > -0.3:
                    ch = '1' if (x * 3 + y) % 7 == 0 else '6'
                elif light > -0.6:
                    ch = '7'
                else:
                    ch = 'E'
                c[y][x] = ch
    c = outline(c)
    # lenguas de fuego finas: base amarilla pegada a la perla, cuerpo naranja, punta roja que se curva
    for pts in ([(4, 3, 'Y'), (3, 3, 'R'), (3, 2, 'Y'), (2, 2, 'R'), (2, 1, 'r'), (4, 2, 'R')],
                [(7, 3, 'Y'), (8, 3, 'Y'), (7, 2, 'R'), (8, 2, 'Y'), (9, 2, 'R'), (8, 1, 'R'), (9, 1, 'R'), (8, 0, 'r')],
                [(11, 3, 'Y'), (12, 3, 'R'), (12, 2, 'Y'), (13, 2, 'R'), (13, 1, 'r')]):
        for x, y, ch in pts:
            if c[y][x] == '.':
                c[y][x] = ch
    return c


def bow(pull=0):
    """Arco de Hou Yi: la silueta del arco de Minecraft (curva arriba y a la izquierda, cuerda en diagonal), lacado en
    rojo, con las puntas y la empuñadura de oro y la cuerda blanca de seda."""
    c = blank()
    T, K, B = (14.0, 1.0), (1.0, 1.0), (1.0, 14.0)
    for i in range(80):
        t = i / 79
        x = (1 - t) ** 2 * T[0] + 2 * (1 - t) * t * K[0] + t * t * B[0]
        y = (1 - t) ** 2 * T[1] + 2 * (1 - t) * t * K[1] + t * t * B[1]
        xi, yi = int(round(x)), int(round(y))
        gold = t < 0.1 or t > 0.9 or 0.45 < t < 0.55
        c[yi][xi] = 'G' if gold else 'R'
        ix, iy = int(round(x + 0.8)), int(round(y + 0.8))
        if c[iy][ix] == '.':
            c[iy][ix] = 'd' if gold else 'r'
    if pull == 0:
        for k in range(11):
            x, y = 13 - k, 2 + k
            if c[y][x] == '.':
                c[y][x] = 'm'
    else:
        # Tensado: la cuerda se abre hacia abajo a la derecha y aparece la flecha blanca (las de Hou Yi eran blancas)
        # con punta de hierro, que cruza el arco.
        px, py = 7 + pull, 8 + pull
        for i in range(0, 10):
            x, y = px - i, py - i
            if 0 <= x < 16 and 0 <= y < 16:
                c[y][x] = 'I' if i >= 8 else 'W' if i <= 1 else 'w'
        for x0, y0 in ((13, 2), (2, 13)):
            for i in range(25):
                t = i / 24
                x, y = round(x0 + (px - x0) * t), round(y0 + (py - y0) * t)
                if c[y][x] == '.':
                    c[y][x] = 'm'
    return outline(c)


# Elixir de Chang'e: frasco de porcelana blanca y azul con tapón rojo, y la luna creciente.
ELIXIR = grid([
    "...........YY...",
    "..........Y8....",
    "..........Y8....",
    "...........YY...",
    "......xx........",
    "......rR........",
    ".....LWWL.......",
    "......Ww........",
    ".....WlWw.......",
    "....WWllWL......",
    "...WlWWWWlL.....",
    "...WWlllWwL.....",
    "...WlWWWlwL.....",
    "....WwwwLL......",
    ".....LLLL.......",
    "................",
])


def stone():
    """Piedra de cinco colores de Nüwa: un canto con cinco vetas (rojo, amarillo, verde azulado, blanco y negro),
    separadas por grietas grises, y un brillo."""
    c = blank()
    cx, cy = 7.5, 8.5
    seeds = [(4.5, 5.5, 'R', 'r'), (10.5, 5.0, 'Y', 'g'), (12.0, 10.0, '3', '4'), (7.5, 12.5, 'W', 'm'), (3.0, 10.5, 'A', 'O')]
    for y in range(16):
        for x in range(16):
            u, v = (x - cx) / 6.6, (y - cy) / 5.6
            if u * u + v * v > 1 or (y < 4 and abs(x - cx) > 4.5):
                continue
            ds = sorted((math.hypot(x - sx, y - sy), i) for i, (sx, sy, _, _) in enumerate(seeds))
            if ds[1][0] - ds[0][0] < 0.8:
                c[y][x] = 'z'
                continue
            _, _, light, dark = seeds[ds[0][1]]
            c[y][x] = dark if (u + v) > 0.55 else light
    c[5][5] = 'M'
    c[4][6] = 'M'
    return outline(c)


ICONS = {"ruyi_jingu_bang": ruyi, "monkey_king_hairs": hairs, "purple_gold_gourd": lambda: outline(GOURD),
         "dragon_pearl": pearl, "hou_yi_bow": bow,
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


def stone_worn():
    return [box([3.6, 2.4, 9.9], [12.4, 3.4, 10.5], "red", "cordón"),
            box([6.8, 1.0, 10.3], [9.2, 3.6, 11.1], "jade", "piedra"),
            box([7.2, 1.4, 11.1], [7.9, 2.2, 11.3], "yellow", "faceta"), box([8.0, 2.4, 11.1], [8.8, 3.2, 11.3], "white", "faceta"),
            box([7.4, 2.6, 11.1], [7.9, 3.2, 11.3], "fire_d", "faceta"), box([8.1, 1.4, 11.1], [8.8, 2.0, 11.3], "black", "faceta")]


WORN = {"five_color_stone": ("body", stone_worn)}


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
