"""Íconos 16x16 de la primera tanda de Egipto y el ícono del mod (estilo vanilla: contorno, luz desde arriba a la
izquierda, brillo animado solo en el oro y las gemas).

Uso: python egypt_icons.py <carpeta_texturas_item> <ícono_del_mod.png> [<carpeta_vista_previa>]
  p. ej. python egypt_icons.py ../../common/src/main/resources/assets/mythicrelics/textures/item \
             ../../common/src/main/resources/assets/mythicrelics/icon.png build
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
    'k': H('#2e1d06'),                                   # contorno
    'G': H('#fff0a0'), 'g': H('#f2c445'), 'd': H('#c8901e'), 'D': H('#8a5a10'),   # oro
    'L': H('#6a92ee'), 'l': H('#2f52b4'), 'b': H('#1b2c70'),                     # lapislázuli
    'q': H('#9af4e2'), 't': H('#34bfae'),                                         # turquesa
    'O': H('#4a4446'), 'o': H('#1c1719'),                                         # ónix / pelaje negro
    'w': H('#fbf5e2'), 'W': H('#e2d6b4'), 's': H('#b8a882'), 'c': H('#9c8a66'), 'x': H('#6e5e40'),  # lino
}
SHINY = set('Ggdqt' + 'Ll')

GOLDEN_SCARAB = [
    "................",
    "......kkkk......",
    ".....kdggdk.....",
    ".k...kkddkk...k.",
    "..k.kgGggggk.k..",
    "...kgGgggggdk...",
    "...kdgggggddk...",
    ".kkkkkkkkkkkkkk.",
    "..kgGtgkkgtgdk..",
    "..kgGtgkkgtgdk..",
    "kkkggtgkkgtgddkk",
    "..kdgtgkkgtgdk..",
    "...kdtgkkgtdk...",
    "....kddkkddk....",
    "..k..kkkkkk..k..",
    "................",
]

EYE_OF_HORUS = [
    "................",
    "................",
    "...kkkkkkkkkk...",
    "..kGgggggggggk..",
    "...kkkkkkkkkkk..",
    "................",
    "....kkkkkkk.....",
    "...kwwwkkkwwk...",
    "..kwwwkLLlkwwkkk",
    "...kwwkllbkwkgdk",
    "....kkkkkkkkkkk.",
    ".....kgk..kgk...",
    ".....kgk...kgk..",
    ".....kgk...kgk..",
    "....kgdk..kgk...",
    ".....kk....kk...",
]

ANKH = [
    "................",
    "......kkkk......",
    ".....kgGGgk.....",
    "....kgk..kdk....",
    "....kgk..kdk....",
    "....kgk..kdk....",
    ".....kgkkdk.....",
    ".kkkkkkgdkkkkkk.",
    ".kgGGggtqgddddk.",
    ".kdddddttddddDk.",
    ".kkkkkkgdkkkkkk.",
    "......kgdk......",
    "......kgdk......",
    "......kgdk......",
    "......kgdk......",
    "......kkkk......",
]

# Mano de cuatro dedos con pulgar (misma silueta que los guantes de Misfit Relics), vendada.
MUMMY_WRAPS = [
    "................",
    "..kk.kk.kk.kk...",
    ".kwWkwWkwWkwWk..",
    ".kwWkwWkwWkwWk..",
    ".kwWkwWkwWkwWk..",
    ".kwWsWsWsWWWsk..",
    ".kwWWWWWWWWWskk.",
    ".kwWWWWWWWWWswWk",
    ".kwWWWWWWWWWWWWk",
    "..kwWWWWWWWWsWk.",
    "..kwWWWWWWWsk...",
    "..kccccccccccxk.",
    "..kxxxxxxxxxxk..",
    "..kkkkkkkkkkkk..",
    "................",
    "................",
]

# Sin contorno: se agrega después con outline().
JACKAL_MASK = [
    "...o........o...",
    "...oo......oo...",
    "...ogo....ogo...",
    "...ogo....ogo...",
    "...oggo..oggo...",
    "...oooooooooo...",
    "..ooOOoooooooo..",
    "..ooGgooooGgoo..",
    "..oooooooooooo..",
    "...oooooooooo...",
    "....oooooooo....",
    ".....oooooo.....",
    ".....odggdo.....",
    "......oooo......",
    "......oOOo......",
    "................",
]

JACKAL_RING_BEZEL = [
    "......o..o......",
    "......oooo......",
    ".....oOoooo.....",
    ".....oGooGo.....",
    "....g.oooo.g....",
    ".......oo.......",
]


def build(shape):
    assert len(shape) == 16 and all(len(r) == 16 for r in shape), [len(r) for r in shape]
    return [[PAL[ch] for ch in row] for row in shape], [list(row) for row in shape]


def wrapped(shape):
    """Vendaje: franjas diagonales más oscuras sobre el lino."""
    rows = [list(r) for r in shape]
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch == 'W' and (x + 2 * y) % 5 == 0:
                row[x] = 's'
    return build(["".join(r) for r in rows])


def outline(img, chars):
    """Contorno de 1 px alrededor de todo lo opaco (4 vecinos)."""
    out = [row[:] for row in img]
    out_chars = [row[:] for row in chars]
    for y in range(16):
        for x in range(16):
            if img[y][x][3]:
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < 16 and 0 <= ny < 16 and img[ny][nx][3]:
                    out[y][x] = PAL['k']
                    out_chars[y][x] = 'k'
                    break
    return out, out_chars


def anubis_scepter():
    """Cetro was: cabeza de chacal arriba a la derecha, asta diagonal con bandas de lapislázuli y horquilla abajo."""
    chars = [['.'] * 16 for _ in range(16)]
    for x, y, ch in [(10, 0, 'o'), (9, 1, 'o'), (10, 1, 'O'), (11, 1, 'o'), (12, 1, 'o'), (9, 2, 'o'), (10, 2, 'g'),
                     (11, 2, 'o'), (12, 2, 'o'), (13, 2, 'o'), (14, 2, 'o'), (9, 3, 'o'), (10, 3, 'o'), (11, 3, 'o'),
                     (12, 3, 'o'), (13, 3, 'O')]:
        chars[y][x] = ch
    for i in range(8):
        x, y = 10 - i, 4 + i
        band = i in (2, 5)
        chars[y][x] = 'L' if band else ('G' if i == 0 else 'g')
        chars[y][x + 1] = 'l' if band else 'd'
    for x, y, ch in [(3, 12, 'g'), (2, 13, 'g'), (4, 13, 'd'), (2, 14, 'd'), (4, 14, 'D')]:
        chars[y][x] = ch
    img = [[PAL[c] for c in row] for row in chars]
    return outline(img, chars)


def jackal_ring():
    chars = [['.'] * 16 for _ in range(16)]
    cx, cy = 7.5, 10.0
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if 2.9 <= d <= 4.7 and y >= 5:
                ang = math.atan2(y - cy, x - cx)  # -pi..pi; arriba-izquierda = luz
                light = -math.cos(ang + math.pi / 4)
                chars[y][x] = 'G' if light > 0.75 else 'g' if light > 0.0 else 'd' if light > -0.7 else 'D'
    for dy, row in enumerate(JACKAL_RING_BEZEL):
        for x, ch in enumerate(row):
            if ch != '.':
                chars[dy + 1][x] = ch
    img = [[PAL[c] for c in row] for row in chars]
    return outline(img, chars)


def jackal_mask():
    img, chars = build(JACKAL_MASK)
    return outline(img, chars)


def save(folder, name, img, chars):
    strip, meta = glint(img, lambda x, y: chars[y][x] in SHINY)
    png(os.path.join(folder, name + ".png"), strip)
    mcmeta = os.path.join(folder, name + ".png.mcmeta")
    if meta:
        with open(mcmeta, "w", encoding="utf-8", newline="\n") as fh:
            json.dump(meta, fh)
    elif os.path.exists(mcmeta):
        os.remove(mcmeta)


# --- Ícono del mod: panel de inventario vanilla con cuatro reliquias (como el de Misfit Relics) ------------------------
GUI = {'bg': H('#c6c6c6'), 'hi': H('#ffffff'), 'sh': H('#555555'), 'edge': H('#000000'),
       'slot': H('#8b8b8b'), 'slot_hi': H('#ffffff'), 'slot_sh': H('#373737')}


def panel(items):
    s = 64
    img = [[T] * s for _ in range(s)]
    for y in range(2, 62):
        for x in range(2, 62):
            img[y][x] = GUI['bg']
    for i in range(2, 62):
        img[2][i] = img[i][2] = GUI['edge']
        img[61][i] = img[i][61] = GUI['edge']
    for i in range(3, 60):
        img[3][i] = img[i][3] = GUI['hi']
        img[60][i] = img[i][60] = GUI['sh']
    for (ox, oy), item in zip([(12, 12), (34, 12), (12, 34), (34, 34)], items):
        for y in range(18):
            for x in range(18):
                col = GUI['slot']
                if y == 0 or x == 0:
                    col = GUI['slot_sh']
                if y == 17 or x == 17:
                    col = GUI['slot_hi']
                img[oy + y][ox + x] = col
        for y in range(16):
            for x in range(16):
                if item[y][x][3]:
                    img[oy + 1 + y][ox + 1 + x] = item[y][x]
    return img


def upscale(img, k):
    return [[img[y // k][x // k] for x in range(len(img[0]) * k)] for y in range(len(img) * k)]


def sheet(images, path, scale=8):
    w = len(images) * (16 * scale + 8)
    canvas = [[H('#3a3a3a')] * w for _ in range(16 * scale)]
    for k, img in enumerate(images):
        for y in range(16 * scale):
            for x in range(16 * scale):
                p = img[y // scale][x // scale]
                if p[3]:
                    canvas[y][k * (16 * scale + 8) + x] = p
    png(path, canvas)


ICONS = {
    "golden_scarab": lambda: build(GOLDEN_SCARAB),
    "eye_of_horus": lambda: build(EYE_OF_HORUS),
    "ankh": lambda: build(ANKH),
    "mummy_wraps": lambda: wrapped(MUMMY_WRAPS),
    "anubis_scepter": anubis_scepter,
    "jackal_mask": jackal_mask,
    "jackal_ring": jackal_ring,
}

if __name__ == "__main__":
    out, mod_icon = sys.argv[1], sys.argv[2]
    preview = sys.argv[3] if len(sys.argv) > 3 else None
    os.makedirs(out, exist_ok=True)
    made = {}
    for name, make in ICONS.items():
        img, chars = make()
        save(out, name, img, chars)
        made[name] = img
    icon = panel([made["ankh"], made["eye_of_horus"], made["golden_scarab"], made["anubis_scepter"]])
    png(mod_icon, upscale(icon, 2))
    if preview:
        os.makedirs(preview, exist_ok=True)
        sheet(list(made.values()), os.path.join(preview, "egypt_icons.png"))
        png(os.path.join(preview, "mod_icon.png"), upscale(icon, 2))
    print("ok:", ", ".join(made))
