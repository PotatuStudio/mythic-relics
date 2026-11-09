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
    # Tanda 2
    'R': H('#ff8a4a'), 'r': H('#e0502a'), 'X': H('#a8301a'),          # disco solar, cornalina, tinta roja
    'V': H('#7fd06a'), 'v': H('#3f9a3a'),                             # cobra (ureo)
    'Y': H('#f0c070'), 'y': H('#c8892e'), 'z': H('#8a5a20'),          # pelaje de leona, cálamos
    'i': H('#f4ead0'),                                                # garras de marfil
    'P': H('#d6a764'), 'p': H('#a8743a'), 'u': H('#6e4a22'),          # madera de la paleta de escriba
}
SHINY = set('Ggdqt' + 'Ll' + 'RrVv')

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


# --- Tanda 2 ----------------------------------------------------------------------------------------------------------
def empty_chars():
    return [['.'] * 16 for _ in range(16)]


def finish(chars):
    """Convierte la grilla de letras a imagen y le agrega el contorno."""
    img = [[PAL[c] for c in row] for row in chars]
    return outline(img, chars)


def ra_disk():
    """Disco solar con aro de oro y el ureo (cobra) alzándose al frente de su parte baja."""
    c = empty_chars()
    cx, cy = 7.5, 7.0
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d <= 6.2:
                light = -(x - cx + y - cy) / 8
                c[y][x] = 'g' if d > 5.2 else ('R' if light > 0.25 else 'r' if light > -0.35 else 'X')
    # Ureo: cuerpo enroscado en la base del disco y la capucha alzada al frente.
    for x, y, ch in [(5, 12, 'v'), (6, 12, 'V'), (7, 12, 'V'), (8, 12, 'V'), (9, 12, 'v'), (10, 12, 'v'),
                     (7, 11, 'V'), (8, 11, 'v'), (7, 10, 'V'), (8, 10, 'v'),
                     (6, 9, 'V'), (7, 9, 'V'), (8, 9, 'v'), (9, 9, 'v'), (6, 8, 'V'), (7, 8, 'g'), (8, 8, 'g'), (9, 8, 'v'),
                     (7, 7, 'V'), (8, 7, 'v')]:
        c[y][x] = ch
    return finish(c)


BASTET = [
    "................",
    ".....o....o.....",
    ".....oo..oo.....",
    ".....oooooo.....",
    ".....oGooGo.....",
    ".....oooooo.....",
    "......oOOo......",
    ".....gggggg.....",
    "....oooooooo....",
    "....oOoooooo....",
    "...oOooooooo....",
    "...ooooooooo..o.",
    "...oooooooooooo.",
    "...oooooooooo...",
    "....oo....oo....",
    "................",
]


def bastet():
    c = [list(r) for r in BASTET]
    c[7][4] = 'd'
    c[7][11] = 'd'
    c[3][4] = 'g'   # arete de oro
    return finish(c)


def thoth_palette():
    """Paleta de escriba con el aspecto de la tablilla del borrador: superficie clara con jeroglíficos y, arriba, los dos
    pocillos de tinta (roja y negra) de las paletas egipcias reales."""
    c = empty_chars()
    for y in range(1, 15):
        for x in range(3, 13):
            c[y][x] = 'w' if (x == 3 or y == 1) else ('s' if (x == 12 or y == 14) else 'W')
    for x, y, ch in [(5, 2, 'r'), (6, 2, 'X'), (5, 3, 'X'), (6, 3, 'X'),    # tinta roja
                     (9, 2, 'O'), (10, 2, 'o'), (9, 3, 'o'), (10, 3, 'o')]:  # tinta negra
        c[y][x] = ch
    for x in range(4, 12):
        c[5][x] = 's'                                                     # borde del área de escritura
    glyphs = [(5, 7, 'l'), (6, 7, 'l'), (8, 7, 'c'), (10, 7, 'c'), (10, 8, 'c'),
              (5, 9, 'c'), (5, 10, 'c'), (7, 9, 'g'), (8, 9, 'g'), (7, 10, 'g'), (10, 9, 'l'), (10, 10, 'l'),
              (5, 12, 'l'), (6, 12, 'l'), (8, 12, 'c'), (9, 12, 'c'), (9, 11, 'c'), (11, 12, 'c')]
    for x, y, ch in glyphs:
        c[y][x] = ch
    return finish(c)


def crook():
    c = empty_chars()
    for i in range(9):
        x, y = 3 + i, 13 - i
        stripe = 'l' if (i // 2) % 2 == 0 else 'g'
        c[y][x] = stripe
        c[y][x + 1] = 'b' if stripe == 'l' else 'd'
    for x, y in [(12, 4), (12, 3), (13, 2), (14, 2), (14, 3), (14, 4)]:
        c[y][x] = 'g' if y < 4 else 'l'
    c[5][14] = 'l'
    return finish(c)


def flail():
    """Mango a rayas y tres hebras de cuentas que cuelgan desde la punta."""
    c = empty_chars()
    for i in range(7):
        x, y = 2 + i, 13 - i
        stripe = 'l' if (i // 2) % 2 == 0 else 'g'
        c[y][x] = stripe
        c[y][x + 1] = 'b' if stripe == 'l' else 'd'
    for x, y in [(9, 6), (10, 5), (10, 4), (11, 4), (12, 4)]:
        c[y][x] = 'g'
    beads = ['g', 't', 'r', 'g', 't', 'r']
    for strand, x in enumerate((11, 12, 13)):
        for k in range(5 + strand % 2):
            c[5 + k][x] = beads[(k + strand) % len(beads)]
    return finish(c)


SEKHMET = [
    "..i..i..i..i....",
    "..yy.yy.yy.yy...",
    ".kYykYykYykYyk..",
    ".kYykYykYykYyk..",
    ".kYyyYyyYyyYyk..",
    ".kYyyyyyyyyyyk..",
    ".kYyyyyyyyyyykk.",
    ".kYyyyyyyyyyyiyk",
    ".kYyyyyyyyyyyyyk",
    "..kYyyyyyyyyzyk.",
    "..kYyyyyyyyzk...",
    "..kggggggggggdk.",
    "..kdddddddddDk..",
    "..kkkkkkkkkkkk..",
    "................",
    "................",
]


def sekhmet():
    c = [list(r) for r in SEKHMET]
    for x in range(3, 13):
        if c[11][x] == 'g' and x % 3 == 0:
            c[11][x] = 'r'   # cuentas de cornalina en la muñequera
    return finish(c)



ICONS = {
    "golden_scarab": lambda: build(GOLDEN_SCARAB),
    "eye_of_horus": lambda: build(EYE_OF_HORUS),
    "ankh": lambda: build(ANKH),
    "mummy_wraps": lambda: wrapped(MUMMY_WRAPS),
    "anubis_scepter": anubis_scepter,
    "jackal_mask": jackal_mask,
    "jackal_ring": jackal_ring,
    "ra_disk": ra_disk,
    "bastet_amulet": bastet,
    "thoth_palette": thoth_palette,
    "osiris_crook": crook,
    "osiris_flail": flail,
    "sekhmet_claws": sekhmet,
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
