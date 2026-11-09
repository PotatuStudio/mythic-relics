"""Textura 128x64 de Sleipnir (layout de SleipnirModel): pelaje gris tormenta moteado, crin y cola blancas, cascos
oscuros, ojos azul claro y la silla de cuero con adornos de oro. Uso: python sleipnir_texture.py <salida.png>"""
import random
import sys

from PIL import Image

random.seed(11)
COAT = [(150, 156, 168), (138, 144, 156), (162, 168, 178), (126, 132, 146)]
COAT_HI = (186, 192, 202)
COAT_SH = (98, 104, 118)
MANE = [(236, 240, 246), (216, 222, 232), (246, 248, 252)]
HOOF = (46, 44, 50)
EYE = (120, 200, 255)
MUZZLE = (88, 92, 104)
LEATHER = (120, 70, 36)
LEATHER_D = (82, 46, 22)
GOLD = (245, 200, 66)

img = Image.new("RGBA", (128, 64), (0, 0, 0, 0))
px = img.load()


def faces(u, v, w, h, d):
    return {"top": (u + d, v, w, d), "bottom": (u + d + w, v, w, d), "west": (u, v + d, d, h),
            "front": (u + d, v + d, w, h), "east": (u + d + w, v + d, d, h), "back": (u + 2 * d + w, v + d, w, h)}


def fill(rect, fn):
    x0, y0, w, h = rect
    for y in range(h):
        for x in range(w):
            px[x0 + x, y0 + y] = (*fn(x, y, w, h), 255)


def coat(x, y, w, h):
    r = random.random()
    return COAT_HI if r < 0.08 else random.choice(COAT)


def box(u, v, w, h, d, side=coat, top=coat, bottom=lambda *a: COAT_SH, front=None, back=None):
    f = faces(u, v, w, h, d)
    fill(f["top"], top)
    fill(f["bottom"], bottom)
    fill(f["west"], side)
    fill(f["east"], side)
    fill(f["front"], front or side)
    fill(f["back"], back or side)


mane = lambda x, y, w, h: random.choice(MANE)
box(0, 0, 10, 10, 22)                                   # cuerpo
box(64, 0, 4, 12, 7)                                    # cuello


def head_side(x, y, w, h):
    return EYE if (y == 2 and x in (1, w - 2)) else coat(x, y, w, h)


box(86, 0, 6, 5, 7, side=head_side)                     # cabeza: ojos a los costados
box(86, 12, 4, 5, 5, front=lambda x, y, w, h: MUZZLE if y >= 2 else coat(x, y, w, h))   # hocico
box(112, 0, 2, 16, 2, side=mane, top=mane, front=mane, back=mane)                       # crin
box(120, 0, 2, 3, 1)                                    # oreja
box(0, 32, 4, 11, 4, side=lambda x, y, w, h: HOOF if y >= h - 2 else coat(x, y, w, h),
    front=lambda x, y, w, h: HOOF if y >= h - 2 else coat(x, y, w, h), bottom=lambda *a: HOOF)   # pata
box(16, 32, 3, 14, 4, side=mane, top=mane, front=mane, back=mane, bottom=mane)                 # cola


def saddle(x, y, w, h):
    return GOLD if x in (0, w - 1) or y in (0, h - 1) else (LEATHER if (x + y) % 3 else LEATHER_D)


box(32, 32, 11, 1, 8, side=lambda x, y, w, h: LEATHER_D, top=saddle, bottom=lambda *a: LEATHER_D)   # silla
img.save(sys.argv[1])
print("ok", sys.argv[1])
