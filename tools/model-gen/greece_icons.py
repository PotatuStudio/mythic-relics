"""Íconos 16x16 de la primera tanda de Grecia, estilo vanilla: silueta legible, 3 o 4 tonos por material con la luz
arriba a la izquierda, contorno del color oscuro de cada material (no negro plano) y brillo animado en el oro, el
bronce, la plata, las alas y el hilo.

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
    '.': T,
    # oro
    'G': H('#fff3a8'), 'g': H('#f5c842'), 'd': H('#c98f1c'), 'D': H('#7d520c'),
    # bronce
    'B': H('#ffd29a'), 'b': H('#e0954a'), 'n': H('#a85f26'), 'N': H('#5e3214'),
    # hierro oscuro (yelmo)
    'I': H('#8a8796'), 'i': H('#55525f'), 'o': H('#34313c'), 'O': H('#1b1920'),
    # crin roja
    'R': H('#ff8a72'), 'r': H('#d8392b'), 'x': H('#8c1d16'), 'X': H('#4d0d0a'),
    # plumas
    'W': H('#ffffff'), 'w': H('#e4eefa'), 'L': H('#b3cdea'), 'l': H('#6f93bd'),
    # cuero
    'P': H('#d39a5c'), 'p': H('#a2662e'), 'u': H('#6e3f17'), 'U': H('#3f220b'),
    # piedra de la gorgona
    'S': H('#dfe6d6'), 's': H('#a9b5a0'), 'z': H('#6f7d68'), 'Z': H('#3e4a3a'),
    # serpientes
    'V': H('#b6e27a'), 'v': H('#5fa13a'), 'y': H('#2f6420'), 'Y': H('#ffe14a'),
    # plata
    'M': H('#ffffff'), 'm': H('#dcdcdc'), 'a': H('#a8a8b0'), 'A': H('#62626c'),
    # caparazón de tortuga
    'T': H('#c8a060'), 't': H('#8a6230'), 'q': H('#4e3416'),
    'k': H('#1a0f08'), 'e': H('#000000'),
}
SHINY = set('GgdBbIRrWwLMmY')
# Color del contorno según el material del vecino (más oscuro que su tono más oscuro).
OUTLINE_OF = {}
for group, edge in (('Ggd', 'D'), ('Bbn', 'N'), ('Iio', 'O'), ('Rrx', 'X'), ('WwLl', 'l'), ('Ppu', 'U'),
                    ('Ssz', 'Z'), ('VvY', 'y'), ('Mma', 'A'), ('Ttq', 'q')):
    for ch in group:
        OUTLINE_OF[ch] = edge


def grid(rows):
    assert len(rows) == 16 and all(len(r) == 16 for r in rows), [len(r) for r in rows]
    return [list(r) for r in rows]


def outline(c):
    """Contorno de 1 px del color oscuro del material vecino (ignora los píxeles que ya son oscuros)."""
    out = [row[:] for row in c]
    for y in range(16):
        for x in range(16):
            if c[y][x] != '.':
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < 16 and 0 <= ny < 16 and c[ny][nx] in OUTLINE_OF:
                    out[y][x] = OUTLINE_OF[c[ny][nx]]
                    break
    return out


# Sandalia alada de Hermes, de costado (punta a la derecha): ala de tres hileras de plumas en el tobillo,
# tiras cruzadas con hebilla de oro y suela gruesa.
HERMES = grid([
    "................",
    ".W..............",
    ".wW.W...........",
    "..wLwW.W........",
    ".wwLLwLwW.......",
    "..wwLLLlw.......",
    "...wwLLlPp......",
    "....wlLPgPp.....",
    ".......pPpP.....",
    ".......PpPpp....",
    "......pPPPPPPp..",
    "......PPpppppPPp",
    "......uuuuuuuuuu",
    "................",
    "................",
    "................",
])

# Yelmo corintio de Hades, de frente: cimera de crin, cúpula de hierro oscuro, banda de oro, ojos almendrados,
# protector nasal y carrilleras.
HADES = grid([
    "......rRRr......",
    ".....rRrrrr.....",
    ".....rrrrrx.....",
    "....iIIiiiio....",
    "...iIIiiiiiio...",
    "...iIiiiiiiio...",
    "...ggggggggggd..",
    "...io..ii..oi...",
    "...ii..io..ii...",
    "...iiiiooiiio...",
    "...iii.oo.iio...",
    "...iii....iio...",
    "...iio....iio...",
    "....io....io....",
    "................",
    "................",
])

# Cabeza de Medusa: rostro de piedra, ojos amarillos y cabellera de serpientes con sus cabezas.
MEDUSA = grid([
    ".V..V......V..V.",
    "vVv.vVv..vVv.vVv",
    ".vvVvVvvvvVvVvv.",
    "..vvSSSSSSSSvv..",
    ".vVSSSSSSSSSSVv.",
    "vVvSsSSSSSSsSvVv",
    "v.vSYzSSSSzYSv.v",
    "..vSSSSSSSSSSv..",
    "...sSSSssSSSs...",
    "...sSSSssSSSs...",
    "...zsSxxxxSsz...",
    "....zsSSSSsz....",
    ".....zssssz.....",
    "................",
    "................",
    "................",
])

# Lira de Orfeo: caja de caparazón de tortuga, brazos de oro curvados, travesaño y cuerdas.
LYRE = grid([
    "..G..........G..",
    "..gG........Gg..",
    "..dgGGGGGGGGgd..",
    "...dg.W.W.W.gd..",
    "...dg.W.W.W.gd..",
    "...dg.W.W.W.gd..",
    "...dg.W.W.W.gd..",
    "...dg.W.W.W.gd..",
    "...dgTTTTTTTgd..",
    "...TTtTTtTTtTT..",
    "...TtqtTqtTtqT..",
    "...tTTtTTtTTtt..",
    "....ttqttqttq...",
    ".....tttttt.....",
    "................",
    "................",
])


def aegis():
    """Escudo redondo de bronce con aro de oro, reflejo arriba a la izquierda y el gorgoneion al centro."""
    c = [['.'] * 16 for _ in range(16)]
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d > 7.2:
                continue
            light = -((x - cx) + (y - cy)) / 10.0
            if d > 6.2:
                c[y][x] = 'G' if light > 0.35 else 'g' if light > -0.2 else 'd'
            else:
                c[y][x] = 'B' if light > 0.45 else 'b' if light > -0.1 else 'n'
    for x, y, ch in [(5, 5, 'v'), (6, 4, 'V'), (9, 4, 'V'), (10, 5, 'v'), (4, 7, 'v'), (11, 7, 'v'),
                     (6, 5, 'S'), (7, 5, 'S'), (8, 5, 'S'), (9, 5, 'S'),
                     (5, 6, 'S'), (6, 6, 'Y'), (7, 6, 'S'), (8, 6, 'S'), (9, 6, 'Y'), (10, 6, 's'),
                     (5, 7, 'S'), (6, 7, 'S'), (7, 7, 's'), (8, 7, 's'), (9, 7, 'S'), (10, 7, 's'),
                     (6, 8, 's'), (7, 8, 'x'), (8, 8, 'x'), (9, 8, 's'), (7, 9, 's'), (8, 9, 'z')]:
        c[y][x] = ch
    return outline(c)


def ariadne():
    """Ovillo de hilo rojo con las vueltas en arcos, brillo arriba a la izquierda y la hebra suelta."""
    c = [['.'] * 16 for _ in range(16)]
    cx, cy, r = 7.0, 7.5, 5.6
    for y in range(16):
        for x in range(16):
            dx, dy = x - cx, y - cy
            d = math.hypot(dx, dy)
            if d > r:
                continue
            # Vueltas cruzadas: dos familias de arcos inclinados.
            band = (math.sin((dx * 0.8 + dy) * 1.25) + math.sin((dx - dy * 0.6) * 1.1)) / 2
            light = -(dx + dy) / (2 * r)
            if band > 0.55:
                c[y][x] = 'R'
            elif band > -0.1:
                c[y][x] = 'r' if light > -0.25 else 'x'
            else:
                c[y][x] = 'x' if light > -0.1 else 'X'
    for x, y in [(4, 4), (5, 3), (4, 5)]:
        c[y][x] = 'R'
    for x, y in [(12, 11), (13, 12), (13, 13), (14, 14), (15, 14)]:
        c[y][x] = 'r'
    return outline(c)


def obol():
    """Óbolo de plata ateniense: borde gastado, la lechuza de Atenea (ojos grandes, orejas) y una ramita de olivo."""
    c = [['.'] * 16 for _ in range(16)]
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d > 6.6:
                continue
            light = -((x - cx) + (y - cy)) / 9.0
            if d > 5.6:
                c[y][x] = 'm' if light > 0.3 else 'a'
            else:
                c[y][x] = 'M' if light > 0.55 else 'm' if light > -0.3 else 'a'
    owl = [(6, 4, 'A'), (9, 4, 'A'),                                   # orejas
           (6, 5, 'a'), (7, 5, 'a'), (8, 5, 'a'), (9, 5, 'a'),
           (5, 6, 'a'), (6, 6, 'M'), (7, 6, 'a'), (8, 6, 'a'), (9, 6, 'M'), (10, 6, 'a'),   # ojos
           (5, 7, 'a'), (6, 7, 'e'), (7, 7, 'A'), (8, 7, 'A'), (9, 7, 'e'), (10, 7, 'a'),
           (6, 8, 'a'), (7, 8, 'A'), (8, 8, 'a'), (9, 8, 'a'),        # pico y pecho
           (6, 9, 'a'), (7, 9, 'A'), (8, 9, 'a'), (9, 9, 'A'),
           (6, 10, 'A'), (7, 10, 'a'), (8, 10, 'A'), (9, 10, 'a'),
           (7, 11, 'A'), (8, 11, 'A'),
           (11, 8, 'v'), (11, 9, 'y'), (12, 9, 'v'), (11, 10, 'v')]    # olivo
    for x, y, ch in owl:
        c[y][x] = ch
    return outline(c)


ICONS = {
    "hermes_sandals": lambda: outline(HERMES),
    "hades_helm": lambda: outline(HADES),
    "medusa_head": lambda: outline(MEDUSA),
    "athena_aegis": aegis,
    "ariadne_thread": ariadne,
    "charon_obol": obol,
    "orpheus_lyre": lambda: outline(LYRE),
}


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
