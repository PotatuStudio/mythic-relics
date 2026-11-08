"""Textura 64x32 del chacal aliado (layout de JackalModel): pelaje negro como Anubis, ojos y orejas por dentro dorados,
collar de oro con bandas de lapislázuli. Uso: python jackal_texture.py <salida.png>  (requiere Pillow)"""
import random
import sys

from PIL import Image

random.seed(3)
FUR = [(26, 22, 28), (32, 27, 34), (22, 19, 24), (38, 32, 40)]
FUR_HI = (54, 47, 58)
FUR_SH = (14, 12, 16)
NOSE = (8, 6, 8)
GOLD_L = (255, 228, 120)
GOLD = (232, 182, 52)
GOLD_D = (160, 108, 24)
LAPIS = (42, 72, 172)
LAPIS_L = (80, 120, 220)

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
    return FUR_HI if random.random() < 0.18 else random.choice(FUR)


def fur_bottom(x, y, w, h):
    return FUR_SH


def box(u, v, w, h, d, side=fur, top=fur_top, bottom=fur_bottom, front=None):
    f = faces(u, v, w, h, d)
    fill(f["top"], top)
    fill(f["bottom"], bottom)
    for k in ("west", "east", "back"):
        fill(f[k], side)
    fill(f["front"], front or side)
    return f


# Cuerpo (0,0) 5x5x12
box(0, 0, 5, 5, 12)


# Collar (34,0) 6x6x1: oro con dos bandas de lapislázuli.
def collar(x, y, w, h):
    if y in (1, 4):
        return LAPIS_L if x % 2 == 0 else LAPIS
    return GOLD_L if y == 0 else GOLD if y < 5 else GOLD_D


box(34, 0, 6, 6, 1, side=collar, top=lambda x, y, w, h: GOLD_L, bottom=lambda x, y, w, h: GOLD_D, front=collar)


# Cráneo (34,7) 5x5x4: ojos dorados en la cara de adelante.
def skull_front(x, y, w, h):
    if y == 1 and x in (1, 3):
        return GOLD_L
    return random.choice(FUR)


box(34, 7, 5, 5, 4, front=skull_front)


# Hocico (48,0) 3x2x4: punta (frente) con la nariz.
def snout_front(x, y, w, h):
    return NOSE if y == 0 and x == 1 else FUR_SH


box(48, 0, 3, 2, 4, front=snout_front)


# Oreja (52,7) 2x4x1: por delante dorada (dentro de la oreja), borde negro.
def ear_front(x, y, w, h):
    return GOLD if y >= 1 and x == 1 else random.choice(FUR)


box(52, 7, 2, 4, 1, front=ear_front)


# Pata (0,17) 2x7x2: la última fila más oscura (la pata).
def leg(x, y, w, h):
    return FUR_SH if y == h - 1 else random.choice(FUR)


box(0, 17, 2, 7, 2, side=leg, front=leg)
# Cola (8,17) 2x7x2: la punta un poco más clara.
box(8, 17, 2, 7, 2, side=lambda x, y, w, h: FUR_HI if y == h - 1 else random.choice(FUR))

img.save(sys.argv[1])
print("ok", sys.argv[1])
