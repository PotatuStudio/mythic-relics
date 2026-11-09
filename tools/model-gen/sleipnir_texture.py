"""Texturas 128x64 de Sleipnir (layout de SleipnirModel): la normal (pelaje gris tormenta moteado, patas que oscurecen
hacia los cascos, crin y cola plateadas trenzadas, bardas de hierro con bordes de oro y runas, silla de cuero) y la que
brilla (solo los ojos y las runas, azul hielo; la dibuja una capa como la de los ojos de la araña).
Uso: python sleipnir_texture.py <carpeta_textures_entity>"""
import math
import os
import random
import sys

from PIL import Image

random.seed(11)
COAT = [(150, 156, 168), (138, 144, 156), (160, 166, 176), (128, 134, 148)]
DAPPLE = (196, 202, 212)
COAT_SH = (92, 98, 112)
MANE = [(236, 240, 248), (214, 222, 234), (248, 250, 255)]
BRAID = (176, 186, 204)
HOOF = (40, 38, 46)
MUZZLE = (78, 82, 96)
IRON = [(70, 74, 86), (82, 86, 98), (62, 66, 78)]
IRON_HI = (118, 124, 138)
GOLD = (245, 200, 66)
GOLD_D = (190, 140, 40)
RUNE = (130, 220, 255)
LEATHER = (120, 70, 36)
LEATHER_D = (82, 46, 22)
EYE = (150, 230, 255)

# Runas en 3x5 (trazos simples tipo futhark): ᚠ, ᚱ, ᛉ, ᛏ.
RUNES = [["X..", "XX.", "X.X", "X..", "X.."], ["XX.", "X.X", "XX.", "X.X", "X.X"],
         ["X.X", "X.X", ".X.", ".X.", ".X."], ["XXX", ".X.", ".X.", ".X.", ".X."]]

img = Image.new("RGBA", (128, 64), (0, 0, 0, 0))
glow = Image.new("RGBA", (128, 64), (0, 0, 0, 0))
px, gx = img.load(), glow.load()


def faces(u, v, w, h, d):
    return {"top": (u + d, v, w, d), "bottom": (u + d + w, v, w, d), "west": (u, v + d, d, h),
            "front": (u + d, v + d, w, h), "east": (u + d + w, v + d, d, h), "back": (u + 2 * d + w, v + d, w, h)}


def fill(rect, fn):
    x0, y0, w, h = rect
    for y in range(h):
        for x in range(w):
            px[x0 + x, y0 + y] = (*fn(x, y, w, h), 255)


def coat(x, y, w, h):
    # Moteado: manchas claras redondas sobre el gris.
    if math.hypot((x % 6) - 2.5, (y % 6) - 2.5) < 1.6 and (x // 6 + y // 6) % 2 == 0:
        return DAPPLE
    return random.choice(COAT)


def box(u, v, w, h, d, side=coat, top=coat, bottom=lambda *a: COAT_SH, front=None, back=None):
    f = faces(u, v, w, h, d)
    fill(f["top"], top)
    fill(f["bottom"], bottom)
    fill(f["west"], side)
    fill(f["east"], side)
    fill(f["front"], front or side)
    fill(f["back"], back or side)
    return f


def iron(x, y, w, h):
    if x in (0, w - 1) or y in (0, h - 1):
        return GOLD if (x + y) % 2 else GOLD_D
    return IRON_HI if random.random() < 0.1 else random.choice(IRON)


def runes(rect, step=4):
    """Escribe runas en una cara (en ambas texturas): en la normal un poco más oscuras, en la que brilla, azul hielo."""
    x0, y0, w, h = rect
    k = 0
    for rx in range(1, w - 3, step):
        rune = RUNES[k % len(RUNES)]
        k += 1
        ry = max(1, (h - 5) // 2)
        for dy, row in enumerate(rune):
            for dx, c in enumerate(row):
                if c == "X" and rx + dx < w - 1 and ry + dy < h - 1:
                    px[x0 + rx + dx, y0 + ry + dy] = (*RUNE, 255)
                    gx[x0 + rx + dx, y0 + ry + dy] = (*RUNE, 255)


mane = lambda x, y, w, h: BRAID if (y + x) % 4 == 0 else random.choice(MANE)
box(0, 0, 10, 10, 22)                                                                 # cuerpo
box(64, 0, 4, 12, 7)                                                                  # cuello
f = box(86, 0, 6, 5, 7)                                                               # cabeza
for side in ("west", "east"):                                                         # ojos a los costados
    x0, y0, w, h = f[side]
    for ex in (1, w - 2):
        px[x0 + ex, y0 + 1] = (*EYE, 255)
        gx[x0 + ex, y0 + 1] = (*EYE, 255)
box(86, 12, 4, 5, 5, front=lambda x, y, w, h: MUZZLE if y >= 2 else coat(x, y, w, h), bottom=lambda *a: MUZZLE)
box(112, 0, 3, 18, 2, side=mane, top=mane, front=mane, back=mane, bottom=mane)       # crin trenzada
box(118, 25, 2, 3, 1)                                                                 # oreja
box(0, 32, 4, 11, 4, side=lambda x, y, w, h: HOOF if y >= h - 2 else (COAT_SH if y >= h - 5 else coat(x, y, w, h)),
    front=lambda x, y, w, h: HOOF if y >= h - 2 else (COAT_SH if y >= h - 5 else coat(x, y, w, h)),
    bottom=lambda *a: HOOF)                                                           # pata
box(16, 32, 3, 8, 4, side=mane, top=mane, front=mane, back=mane, bottom=mane)       # cola
box(30, 42, 3, 10, 3, side=mane, top=mane, front=mane, back=mane, bottom=mane)       # punta de la cola


def saddle(x, y, w, h):
    return GOLD if x in (0, w - 1) or y in (0, h - 1) else (LEATHER if (x + y) % 3 else LEATHER_D)


box(32, 32, 11, 1, 8, side=lambda *a: LEATHER_D, top=saddle, bottom=lambda *a: LEATHER_D)   # silla
box(118, 22, 2, 2, 1, side=lambda *a: GOLD, top=lambda *a: GOLD, front=lambda *a: GOLD)      # perilla
f = box(70, 22, 5, 1, 8, side=iron, top=iron, bottom=iron, front=iron)                      # testera
runes(f["top"], step=5)
f = box(70, 32, 11, 9, 1, side=iron, top=iron, bottom=iron, front=iron, back=iron)          # peto
runes(f["front"])
f = box(96, 22, 1, 6, 10, side=iron, top=iron, bottom=iron, front=iron, back=iron)          # faldones
runes(f["west"])
runes(f["east"])

out = sys.argv[1]
img.save(os.path.join(out, "sleipnir.png"))
glow.save(os.path.join(out, "sleipnir_glow.png"))
print("ok", out)
