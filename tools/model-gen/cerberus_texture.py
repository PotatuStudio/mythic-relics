"""Textura 64x32 del Cerbero cachorro (layout de CerberusModel): pelaje de sombra, ojos de brasa, orejas con el interior
encendido y collar de cuero negro con púas de plata. Uso: python cerberus_texture.py <salida.png>  (requiere Pillow)"""
import random
import sys

from PIL import Image

random.seed(7)
FUR = [(30, 26, 36), (36, 31, 43), (25, 21, 30), (43, 37, 51)]
FUR_HI = (58, 51, 68)
FUR_SH = (14, 12, 17)
NOSE = (8, 6, 10)
EMBER = (255, 138, 58)
EMBER_CORE = (255, 208, 96)
LEATHER = (42, 38, 48)
LEATHER_D = (24, 21, 28)
SILVER = (232, 232, 236)

img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
px = img.load()


def faces(u, v, w, h, d):
    return {
        "top": (u + d, v, w, d),
        "bottom": (u + d + w, v, w, d),
        "west": (u, v + d, d, h),
        "front": (u + d, v + d, w, h),
        "east": (u + d + w, v + d, d, h),
        "back": (u + 2 * d + w, v + d, w, h),
    }


def fill(rect, fn):
    x0, y0, w, h = rect
    for y in range(h):
        for x in range(w):
            px[x0 + x, y0 + y] = (*fn(x, y, w, h), 255)


def fur(x, y, w, h):
    return random.choice(FUR)


def fur_top(x, y, w, h):
    return FUR_HI if random.random() < 0.2 else random.choice(FUR)


def fur_bottom(x, y, w, h):
    return FUR_SH


def box(u, v, w, h, d, side=fur, top=fur_top, bottom=fur_bottom, front=None):
    f = faces(u, v, w, h, d)
    fill(f["top"], top)
    fill(f["bottom"], bottom)
    for k in ("west", "east", "back"):
        fill(f[k], side)
    fill(f["front"], front or side)


# Cuerpo (0,0) 6x6x12: el lomo con una franja más oscura.
box(0, 0, 6, 6, 12, top=lambda x, y, w, h: FUR_SH if x in (2, 3) else fur_top(x, y, w, h))


# Collar (36,0) 7x7x1: cuero negro con púas de plata en el borde.
def collar(x, y, w, h):
    if (x + y) % 3 == 0 and (x in (0, w - 1) or y in (0, h - 1)):
        return SILVER
    return LEATHER if y < h - 1 else LEATHER_D


box(36, 0, 7, 7, 1, side=collar, top=lambda x, y, w, h: LEATHER, bottom=lambda x, y, w, h: LEATHER_D, front=collar)


# Cabeza (36,8) 5x5x4: ojos de brasa con el centro amarillo.
def skull_front(x, y, w, h):
    if y == 1 and x in (1, 3):
        return EMBER_CORE
    if y == 2 and x in (1, 3):
        return EMBER
    return random.choice(FUR)


box(36, 8, 5, 5, 4, front=skull_front)


# Hocico (52,17) 3x2x3: la nariz arriba y la boca con un resplandor de brasa.
def snout_front(x, y, w, h):
    if y == 0 and x == 1:
        return NOSE
    return EMBER if y == h - 1 and x == 1 else FUR_SH


box(52, 17, 3, 2, 3, front=snout_front, bottom=lambda x, y, w, h: EMBER if x == 1 else FUR_SH)
# Oreja (56,8) 2x2x1: por dentro, brasa.
box(56, 8, 2, 2, 1, front=lambda x, y, w, h: EMBER if y == 1 and x == 1 else random.choice(FUR))


# Pata (0,18) 2x7x2 y cola (8,18) 2x7x2: patas más oscuras abajo; la punta de la cola encendida.
def leg(x, y, w, h):
    return FUR_SH if y == h - 1 else random.choice(FUR)


box(0, 18, 2, 7, 2, side=leg, front=leg)
box(8, 18, 2, 7, 2, side=lambda x, y, w, h: EMBER if y == h - 1 else random.choice(FUR))

img.save(sys.argv[1])
print("ok", sys.argv[1])
