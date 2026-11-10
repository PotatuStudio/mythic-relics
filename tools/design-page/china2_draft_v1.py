"""Borrador de la tanda 2 china: íconos 16x16 y modelos puestos (Ojo de Erlang en la cabeza, pulsera del pixiu en la
muñeca). No toca el mod. Uso: python china2_draft.py <salida.json> [<vista.png>]"""
import json
import math
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import china_draft as CD  # noqa: E402
from china_draft import PAL, SHINY, blank, box, glint, grid, outline, png_b64  # noqa: E402,F401


def line(c, x0, y0, x1, y1, ch, only_empty=False):
    steps = max(abs(x1 - x0), abs(y1 - y0)) * 2 + 1
    for i in range(steps + 1):
        t = i / steps
        x, y = round(x0 + (x1 - x0) * t), round(y0 + (y1 - y0) * t)
        if 0 <= x < 16 and 0 <= y < 16 and (not only_empty or c[y][x] == '.'):
            c[y][x] = ch


def fire_spear():
    """Lanza de punta de fuego (huojian qiang) de Nezha: asta roja en diagonal, borla roja bajo la punta de acero y la
    punta envuelta en llamas."""
    c = blank()
    line(c, 1, 14, 10, 5, 'r')
    line(c, 2, 14, 10, 6, 'x', only_empty=True)
    for x, y in [(1, 14), (2, 13)]:
        c[y][x] = 'g'
    # borla roja
    for x, y, ch in [(9, 7, 'R'), (8, 7, 'r'), (9, 8, 'r'), (8, 8, 'R'), (7, 8, 'r'), (10, 7, 'g'), (9, 6, 'g')]:
        c[y][x] = ch
    # punta de acero
    for x, y, ch in [(11, 4, 'W'), (12, 3, 'W'), (11, 5, 'w'), (12, 4, 'w'), (13, 2, 'M'), (10, 5, 'a')]:
        c[y][x] = ch
    c = outline(c)
    # llamas alrededor de la punta (sin contorno)
    for x, y, ch in [(13, 1, 'Y'), (14, 1, 'R'), (14, 2, 'Y'), (15, 2, 'r'), (12, 1, 'R'), (13, 0, 'r'), (14, 3, 'R'),
                     (15, 0, 'Y'), (11, 2, 'Y')]:
        if c[y][x] == '.':
            c[y][x] = ch
    return c


def universe_ring():
    """Aro del Universo (qiankun quan): aro de oro macizo, con estelas de movimiento (vuelve a la mano)."""
    c = blank()
    cx, cy = 8.5, 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if 3.6 <= d <= 6.0:
                light = -((x - cx) + (y - cy)) / 7
                c[y][x] = 'G' if light > 0.35 else 'g' if light > -0.35 else 'd'
    c = outline(c)
    for x, y, ch in [(0, 9, 'w'), (1, 10, 'W'), (0, 11, 'w'), (1, 12, 'w'), (0, 13, 'w')]:
        if c[y][x] == '.':
            c[y][x] = ch
    return c


# Ojo de Erlang Shen: diadema de oro con el tercer ojo vertical en el centro (abierto, con iris dorado).
ERLANG_EYE = grid([
    "................",
    "................",
    "................",
    "................",
    ".......WW.......",
    "......WwwW......",
    ".....dWYYWd.....",
    "gggddgWYkYWgddgg",
    "GGgggGWYkYWGgggG",
    "dddggdWYYWdggddd",
    "......WwwW......",
    ".......WW.......",
    "................",
    "................",
    "................",
    "................",
])


def halberd():
    """Alabarda de tres puntas y doble filo (sanjian liangren dao): asta oscura en diagonal, collar de oro y una hoja de
    acero con tres puntas."""
    c = blank()
    line(c, 1, 14, 9, 6, 'i')
    line(c, 2, 14, 9, 7, 'o', only_empty=True)
    c[6][9] = 'g'
    c[7][9] = 'd'
    c[6][10] = 'g'
    # hoja: base ancha y tres puntas (centro más larga)
    for x, y, ch in [(10, 5, 'W'), (11, 4, 'W'), (12, 3, 'W'), (13, 2, 'M'), (14, 1, 'W'),
                     (11, 5, 'w'), (12, 4, 'w'), (10, 4, 'w'),
                     (9, 3, 'W'), (8, 2, 'W'), (10, 3, 'w'),
                     (12, 6, 'W'), (13, 7, 'W'), (12, 5, 'w')]:
        c[y][x] = ch
    return outline(c)


def qilin_horn():
    """Cuerno del qilin: el cuerno único, curvo, de hueso dorado con anillos, atado con un cordón rojo."""
    c = blank()
    pts = []
    for i in range(40):
        t = i / 39
        x = 3 + 9 * t + 2.2 * math.sin(t * math.pi)
        y = 13 - 11 * t
        pts.append((x, y, t))
    for x, y, t in pts:
        w = 1.8 * (1 - t) + 0.3
        for yy in range(16):
            for xx in range(16):
                if math.hypot(xx - x, yy - y) <= w:
                    ring = int(t * 7) % 2 == 0
                    c[yy][xx] = ('8' if ring else '9') if xx - x < 0 else ('9' if ring else 'T')
    for x, y, ch in [(2, 12, 'r'), (3, 12, 'R'), (4, 12, 'r'), (2, 13, 'x'), (1, 14, 'r'), (1, 15, 'x')]:
        c[y][x] = ch
    return outline(c)


def pixiu_bracelet():
    """Pulsera del pixiu: cuentas de jade en círculo con el pixiu de oro (león alado que come oro) al frente."""
    c = blank()
    cx, cy = 7.5, 8.5
    for k in range(14):
        a = 2 * math.pi * k / 14
        x, y = round(cx + 5.4 * math.cos(a)), round(cy + 5.0 * math.sin(a))
        c[y][x] = '3' if k % 2 else '4'
        if 0 <= x + 1 < 16 and math.cos(a) < 0:
            c[y][x + 1] = '4'
    # pixiu de oro abajo al frente
    for x, y, ch in [(6, 12, 'g'), (7, 12, 'G'), (8, 12, 'g'), (9, 12, 'd'), (6, 13, 'd'), (7, 13, 'g'), (8, 13, 'g'),
                     (9, 13, 'd'), (5, 12, 'G'), (10, 11, 'G'), (6, 11, 'Y'), (9, 11, 'g')]:
        c[y][x] = ch
    return outline(c)


# Talismanes (fu): tira de papel amarillo con trazos rojos de bermellón y una borla.
TALISMAN = grid([
    "....YYYYYYY.....",
    "....8YYYYY8.....",
    "....8r.rrY8.....",
    "....8YrYYY8.....",
    "....8rrrrY8.....",
    "....8YYrYY8.....",
    "....8YrYrY8.....",
    "....8rYYYr8.....",
    "....8YrrrY8.....",
    "....8YYrYY8.....",
    "....8YrrrY8.....",
    "....8rYrYr8.....",
    "....8YYYYY8.....",
    "....99999999....",
    ".......R........",
    "......rRr.......",
])


def xirang_sack():
    """Saco de Xirang: saco de tela atado, con la tierra que crece sola desbordándose y un brote verde."""
    c = blank()
    cx, cy = 7.5, 10.0
    for y in range(16):
        for x in range(16):
            u, v = (x - cx) / 5.5, (y - cy) / 4.6
            if u * u + v * v <= 1 and y >= 6:
                light = -(u + v) / 2
                c[y][x] = 'P' if light > 0.15 else 'p' if light > -0.3 else 'u'
    for x in range(5, 11):
        c[5][x] = 'n'
        c[6][x] = 'N'
    for x, y in [(6, 4), (7, 4), (8, 4), (9, 4), (7, 3), (8, 3), (5, 4), (10, 4)]:
        c[y][x] = 'U' if (x + y) % 2 else 'q'
    for x, y, ch in [(8, 2, 'v'), (8, 1, 'V'), (9, 1, 'V'), (7, 1, 'v')]:
        c[y][x] = ch
    return outline(c)


# Rama del melocotonero del Huerto Celestial: rama con dos hojas y un melocotón rosado.
PEACH = grid([
    "................",
    "..........yv....",
    "....uu...yVv....",
    "...u..u.yVv.....",
    "........u.......",
    ".......u........",
    "......u.........",
    ".....u.vVy......",
    "....uKKvVy......",
    "...1KKKCvy......",
    "..1KKKKCCc......",
    "..KKKKCCCc......",
    "..1KKCCCCc......",
    "...1CCCCc.......",
    "....cccc........",
    "................",
])


ICONS = {"fire_tipped_spear": fire_spear, "universe_ring": universe_ring, "erlang_eye": lambda: outline(ERLANG_EYE),
         "erlang_halberd": halberd, "qilin_horn": qilin_horn, "pixiu_bracelet": pixiu_bracelet,
         "fu_talismans": lambda: outline(TALISMAN), "xirang_sack": xirang_sack, "immortal_peach_branch": lambda: outline(PEACH)}


def eye_worn():
    """Cabeza (4..12): diadema de oro alrededor de la frente y el tercer ojo vertical al frente."""
    e = [box([3.25, 9.6, 3.25], [12.75, 10.6, 3.85], "gold", "diadema atrás"),
         box([3.25, 9.6, 12.15], [12.75, 10.6, 12.75], "gold", "diadema frente"),
         box([3.25, 9.6, 3.85], [3.85, 10.6, 12.15], "gold", "diadema der."),
         box([12.15, 9.6, 3.85], [12.75, 10.6, 12.15], "gold", "diadema izq.")]
    e += [box([7.3, 9.0, 12.75], [8.7, 11.8, 13.05], "white", "ojo"),
          box([7.6, 9.6, 13.05], [8.4, 11.2, 13.2], "yellow", "iris"),
          box([7.85, 10.0, 13.2], [8.15, 10.8, 13.3], "black", "pupila")]
    return e


def bracelet_worn():
    """Brazo (mano en y 2): cuentas de jade alrededor de la muñeca y el pixiu de oro arriba."""
    e = []
    y0, y1 = 3.6, 4.6
    e += [box([5.45, y0, 5.45], [10.55, y1, 6.05], "jade", "cuentas"), box([5.45, y0, 9.95], [10.55, y1, 10.55], "jade", "cuentas"),
          box([5.45, y0, 6.05], [6.05, y1, 9.95], "jade", "cuentas"), box([9.95, y0, 6.05], [10.55, y1, 9.95], "jade", "cuentas")]
    e.append(box([7.0, y0 - 0.2, 10.55], [9.0, y1 + 0.4, 11.25], "gold", "pixiu"))
    return e


WORN = {"erlang_eye": ("head", eye_worn), "pixiu_bracelet": ("right_arm", bracelet_worn)}

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
