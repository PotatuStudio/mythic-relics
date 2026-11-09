"""Íconos 16x16 de la primera tanda de Grecia (estilo vanilla: contorno, luz desde arriba a la izquierda, brillo
animado en el oro, el bronce, la plata, las alas y el hilo).

Uso: python greece_icons.py <carpeta_texturas_item> [<carpeta_vista_previa>]
"""
import json
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from glint import glint  # noqa: E402
from mclib import H, T, png  # noqa: E402

PAL = {
    '.': T, 'k': H('#241a10'),
    'G': H('#fff0a0'), 'g': H('#f2c445'), 'd': H('#c8901e'), 'D': H('#8a5a10'),          # oro
    'B': H('#f0b070'), 'b': H('#c87a3a'), 'n': H('#8a4a1e'), 'N': H('#5a2e12'),          # bronce
    'M': H('#ffffff'), 'm': H('#e8e6e0'), 'a': H('#bcb8ae'), 'A': H('#8a867c'),          # mármol / plata
    'L': H('#cfe6ff'), 'l': H('#7fb0e0'), 'w': H('#f4f8ff'),                              # alas, celeste
    'o': H('#1c1719'), 'O': H('#4a4446'),                                                 # negro (yelmo)
    'V': H('#9ad27a'), 'v': H('#4f8f3a'), 'u': H('#2f5a24'),                              # serpientes de Medusa
    'S': H('#c9cfc4'), 's': H('#8f978a'),                                                 # piel de piedra
    'R': H('#ff6a5a'), 'r': H('#c8302a'), 'x': H('#7a1a14'),                              # hilo rojo
    'P': H('#a07848'), 'p': H('#7a5430'),                                                 # cuero / caparazón
}
SHINY = set('GgdBbMmLlRr')


def blank():
    return [['.'] * 16 for _ in range(16)]


def grid(rows):
    assert len(rows) == 16 and all(len(r) == 16 for r in rows), [len(r) for r in rows]
    return [list(r) for r in rows]


def outline(c):
    out = [row[:] for row in c]
    for y in range(16):
        for x in range(16):
            if c[y][x] != '.':
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < 16 and 0 <= ny < 16 and c[ny][nx] not in '.k':
                    out[y][x] = 'k'
                    break
    return out


# Sandalia alada vista de costado: suela, tiras cruzadas y el ala en el talón.
HERMES = grid([
    "................",
    ".l..............",
    ".wl.l...........",
    ".wwlwl.l........",
    "..wwwlwl........",
    "...wwwwll.......",
    "....wwwwlbbb....",
    "......bPbPbPb...",
    ".....bPbPbPbPb..",
    ".....PbPbPbPbPb.",
    ".....bbbbbbbbbbb",
    ".....NNNNNNNNNNN",
    "................",
    "................",
    "................",
    "................",
])

# Yelmo corintio negro con cimera de crin y aberturas de los ojos.
HADES = grid([
    "......rrrr......",
    ".....rRrrrr.....",
    "....rrrrrrrr....",
    "....oooooooo....",
    "...oOooooooOo...",
    "...oOooooooOo...",
    "...ooooooooooo..",
    "...oo.oooo.oo...",
    "...oo..oo..oo...",
    "...oooooooooo...",
    "...ooooo.ooooo..",
    "....oooo.oooo...",
    "....oooo.oooo...",
    ".....ooo.ooo....",
    "................",
    "................",
])

# Cabeza de Medusa de frente: rostro de piedra y cabellera de serpientes.
MEDUSA = grid([
    "..v..V....V..v..",
    ".vV.vVv..vVv.Vv.",
    "..VvvVVvvVVvvV..",
    ".vVvSSSSSSSSvVv.",
    "..vSSSSSSSSSSv..",
    ".vVSsSSSSSSsSVv.",
    "..vSRrSSSSrRSv..",
    ".vVSSSSSSSSSSVv.",
    "..vSSSSssSSSSv..",
    "..vSSSSssSSSSv..",
    "...vSSrrrrSSv...",
    "...vSSSSSSSSv...",
    "....vSSSSSSv....",
    ".....vvSSvv.....",
    "......v..v......",
    "................",
])


def aegis():
    """Escudo redondo de bronce con el gorgoneion (rostro de Medusa) al centro."""
    c = blank()
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d <= 7.0:
                light = -(x - cx + y - cy) / 9
                c[y][x] = 'g' if d > 6.0 else ('B' if light > 0.3 else 'b' if light > -0.4 else 'n')
    for x, y, ch in [(6, 5, 'V'), (9, 5, 'V'), (5, 6, 'v'), (10, 6, 'v'),
                     (6, 6, 'S'), (7, 6, 'S'), (8, 6, 'S'), (9, 6, 'S'),
                     (6, 7, 'r'), (7, 7, 'S'), (8, 7, 'S'), (9, 7, 'r'),
                     (6, 8, 'S'), (7, 8, 's'), (8, 8, 's'), (9, 8, 'S'),
                     (7, 9, 'r'), (8, 9, 'r'), (7, 10, 'S'), (8, 10, 'S')]:
        c[y][x] = ch
    return outline(c)


def ariadne():
    """Ovillo de hilo rojo con la hebra suelta."""
    c = blank()
    cx, cy = 7.0, 8.0
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d <= 5.3:
                band = (x + 2 * y) % 4
                c[y][x] = 'R' if band == 0 else ('r' if band in (1, 2) else 'x')
    for x, y in [(12, 11), (13, 12), (13, 13), (14, 14), (15, 14)]:
        c[y][x] = 'r'
    return outline(c)


def obol():
    """Óbolo de plata ateniense: la lechuza de Atenea."""
    c = blank()
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d <= 6.0:
                light = -(x - cx + y - cy) / 8
                c[y][x] = 'a' if d > 5.0 else ('M' if light > 0.3 else 'm' if light > -0.3 else 'a')
    for x, y, ch in [(6, 5, 'A'), (9, 5, 'A'), (6, 6, 'A'), (7, 6, 'A'), (8, 6, 'A'), (9, 6, 'A'),
                     (6, 7, 'k'), (9, 7, 'k'), (7, 7, 'A'), (8, 7, 'A'),
                     (6, 8, 'A'), (7, 8, 'A'), (8, 8, 'A'), (9, 8, 'A'), (7, 9, 'A'), (8, 9, 'A'),
                     (7, 10, 'A'), (8, 10, 'A'), (10, 9, 'v'), (11, 8, 'v')]:
        c[y][x] = ch
    return outline(c)


LYRE = grid([
    "..g..........g..",
    "..gg........gg..",
    "...gGgggggggg...",
    "...g.m.m.m.mg...",
    "...g.m.m.m.mg...",
    "...g.m.m.m.mg...",
    "...g.m.m.m.mg...",
    "...g.m.m.m.mg...",
    "...gd.m.m.mdg...",
    "....PpPpPpPp....",
    "...PpPPpPPpPP...",
    "...pPpPpPpPpP...",
    "....PpPpPpPp....",
    ".....pppppp.....",
    "................",
    "................",
])

ICONS = {"hermes_sandals": lambda: outline(HERMES), "hades_helm": lambda: outline(HADES),
         "medusa_head": lambda: outline(MEDUSA), "athena_aegis": aegis, "ariadne_thread": ariadne,
         "charon_obol": obol, "orpheus_lyre": lambda: outline(LYRE)}



def save(folder, name, chars):
    img = [[PAL[ch] for ch in row] for row in chars]
    strip, meta = glint(img, lambda x, y: chars[y][x] in SHINY)
    png(os.path.join(folder, name + ".png"), strip)
    mcmeta = os.path.join(folder, name + ".png.mcmeta")
    if meta:
        with open(mcmeta, "w", encoding="utf-8", newline="\n") as fh:
            json.dump(meta, fh)
    elif os.path.exists(mcmeta):
        os.remove(mcmeta)
    return img


if __name__ == "__main__":
    out = sys.argv[1]
    preview = sys.argv[2] if len(sys.argv) > 2 else None
    images = [save(out, name, make()) for name, make in ICONS.items()]
    if preview:
        os.makedirs(preview, exist_ok=True)
        w = len(images) * 136
        canvas = [[H('#3a3a3a')] * w for _ in range(128)]
        for k, img in enumerate(images):
            for y in range(128):
                for x in range(128):
                    p = img[y // 8][x // 8]
                    if p[3]:
                        canvas[y][k * 136 + x] = p
        png(os.path.join(preview, "greece_icons.png"), canvas)
    print("ok:", ", ".join(ICONS))
