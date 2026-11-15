"""Borrador de la tanda 2 china: íconos 16x16 de los talismanes fu y del saco de xirang. No toca el mod. Uso: python china2_draft.py <salida.json> [<vista.png>]"""
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


ICONS = {"fu_talismans": lambda: outline(TALISMAN), "xirang_sack": xirang_sack}
WORN = {}

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
