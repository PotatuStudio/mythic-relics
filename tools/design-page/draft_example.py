"""Borrador de la tanda 2 nórdica: íconos 16x16 y modelos puestos (herradura, collar, anillo). No toca el mod.
Uso: python draft.py <salida.json> [<vista.png>]"""
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

ICONS = {"sleipnir_horseshoe": horseshoe, "odin_ravens": lambda: outline(RAVENS), "brisingamen": brisingamen,
         "skidbladnir": lambda: outline(SHIP), "mimir_head": lambda: outline(MIMIR), "andvaranaut": andvaranaut,
         "odrerir": lambda: outline(ODRERIR)}

COLORS = {"iron_l": "#cfd3dc", "iron": "#8a8f9c", "iron_d": "#4a4e58", "gold_l": "#fff3a8", "gold": "#f5c842",
          "gold_d": "#c98f1c", "amber": "#ffb030", "ruby": "#d8392b"}
NAMES = list(COLORS)
SHADE = {"iron": ("iron_l", "iron_d"), "gold": ("gold_l", "gold_d")}


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


def ring(x0, x1, y0, y1, z0, z1, t, color, name):
    return [box([x0, y0, z1 - t], [x1, y1, z1], color, name), box([x0, y0, z0], [x1, y1, z0 + t], color, name),
            box([x0, y0, z0 + t], [x0 + t, y1, z1 - t], color, name), box([x1 - t, y0, z0 + t], [x1, y1, z1 - t], color, name)]


def horseshoe_worn():
    return [box([5.6, 1.2, 5.4], [10.4, 1.6, 11.0], "iron", "herradura"),
            box([5.4, 1.6, 9.4], [10.6, 2.6, 11.2], "iron", "talón"),
            box([6.0, 1.0, 10.2], [6.8, 1.2, 10.8], "gold", "clavo"), box([9.2, 1.0, 10.2], [10.0, 1.2, 10.8], "gold", "clavo")]


def brisingamen_worn():
    e = [box([3.5, 13.2, 5.45], [12.5, 13.9, 6.05], "gold", "cadena atrás"),
         box([3.5, 13.2, 6.05], [4.05, 13.9, 10.5], "gold", "cadena der."),
         box([11.95, 13.2, 6.05], [12.5, 13.9, 10.5], "gold", "cadena izq.")]
    for i, (x, y) in enumerate([(4.4, 12.4), (5.6, 11.4), (7.2, 10.8), (8.8, 11.4), (10.0, 12.4)]):
        e.append(box([x, y, 10.5], [x + 1.4, y + 1.2, 11.2], "amber" if i % 2 == 0 else "gold", "gema"))
    return e


def andvaranaut_worn():
    return ring(5.15, 10.85, 8.6, 9.6, 5.15, 10.85, 0.5, "gold", "aro") + [box([7.3, 8.4, 10.85], [8.7, 9.8, 11.5], "ruby", "gema")]


WORN = {"brisingamen": ("body", brisingamen_worn),
        "andvaranaut": ("right_arm", andvaranaut_worn)}


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
