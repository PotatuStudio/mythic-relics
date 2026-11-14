"""Textura 64x64 de Cerbero (layout de CerberusModel): pelaje de sombra con cresta más oscura, ojos de brasa, fauces
rojas con colmillos de marfil, garras, y la cola de serpiente de escamas verde oscuro que se afina en punta.
Uso: python cerberus_texture.py <salida.png>  (requiere Pillow)"""
import random
import sys

from PIL import Image

random.seed(7)
FUR = [(30, 26, 36), (36, 31, 43), (25, 21, 30), (43, 37, 51)]
FUR_HI = (58, 51, 68)
FUR_SH = (14, 12, 17)
RIDGE = (10, 8, 12)
EMBER = (255, 110, 40)
EMBER_CORE = (255, 214, 96)
MOUTH = (110, 18, 22)
MOUTH_D = (60, 8, 12)
FANG = (240, 232, 208)
CLAW = (214, 204, 180)
SCALE = [(40, 58, 34), (48, 70, 40), (34, 48, 28)]
SCALE_BELLY = (104, 120, 70)
SNAKE_EYE = (255, 210, 58)
TONGUE = (200, 40, 40)

img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
px = img.load()


def faces(u, v, w, h, d):
    return {"top": (u + d, v, w, d), "bottom": (u + d + w, v, w, d), "west": (u, v + d, d, h),
            "front": (u + d, v + d, w, h), "east": (u + d + w, v + d, d, h), "back": (u + 2 * d + w, v + d, w, h)}


def fill(rect, fn):
    x0, y0, w, h = rect
    for y in range(h):
        for x in range(w):
            px[x0 + x, y0 + y] = (*fn(x, y, w, h), 255)


def fur(x, y, w, h):
    return random.choice(FUR)


def fur_top(x, y, w, h):
    return FUR_HI if random.random() < 0.2 else random.choice(FUR)


def box(u, v, w, h, d, side=fur, top=fur_top, bottom=lambda *a: FUR_SH, front=None, back=None):
    f = faces(u, v, w, h, d)
    fill(f["top"], top)
    fill(f["bottom"], bottom)
    fill(f["west"], side)
    fill(f["east"], side)
    fill(f["back"], back or side)
    fill(f["front"], front or side)


# Pecho (0,0) 10x9x8 y cadera (0,17) 8x7x8: cresta oscura a lo largo del lomo y el pelaje erizado en el pecho.
ridge = lambda x, y, w, h: RIDGE if x in (w // 2 - 1, w // 2) else fur_top(x, y, w, h)
box(0, 0, 10, 9, 8, top=ridge, front=lambda x, y, w, h: FUR_HI if (x + y) % 4 == 0 else random.choice(FUR))
box(0, 17, 8, 7, 8, top=ridge)


# Cráneo (36,0) 6x6x5: ceño marcado y ojos de brasa.
def skull_front(x, y, w, h):
    if y == 1 and x in (0, 1, 4, 5):
        return RIDGE
    if y == 2 and x in (1, 4):
        return EMBER_CORE
    if y == 2 and x in (0, 5):
        return EMBER
    return random.choice(FUR)


box(36, 0, 6, 6, 5, front=skull_front)


# Hocico (36,11) 4x2x5: nariz arriba; por debajo, el paladar rojo con colmillos en los bordes.
def palate(x, y, w, h):
    return FANG if x in (0, w - 1) and y % 2 == 0 else MOUTH


def snout_front(x, y, w, h):
    if y == 0:
        return (8, 6, 10) if x in (1, 2) else FUR_SH
    return FANG if x in (0, w - 1) else MOUTH_D


box(36, 11, 4, 2, 5, front=snout_front, bottom=palate)


# Mandíbula (36,18) 4x1x5: arriba la lengua y los colmillos que suben; el resto, pelaje.
def jaw_top(x, y, w, h):
    return FANG if x in (0, w - 1) and y % 2 == 1 else MOUTH


box(36, 18, 4, 1, 5, top=jaw_top, front=lambda x, y, w, h: FANG if x in (0, w - 1) else FUR_SH)
# Oreja (54,11) 2x3x1: en punta, el interior encendido.
box(54, 11, 2, 3, 1, front=lambda x, y, w, h: EMBER if y >= 1 and x == 1 else RIDGE)
# Pata (0,32) 3x10x3 y zarpa (12,32) 4x2x4 con garras de marfil al frente.
box(0, 32, 3, 10, 3, side=lambda x, y, w, h: FUR_SH if y >= h - 2 else random.choice(FUR))
box(12, 32, 4, 2, 4, front=lambda x, y, w, h: CLAW if y == h - 1 and x % 2 == 0 else FUR_SH,
    top=lambda x, y, w, h: FUR_SH, side=lambda x, y, w, h: FUR_SH)


# Serpientes: escamas verde oscuro, vientre más claro abajo.
def scales(x, y, w, h):
    return random.choice(SCALE)


snake = dict(side=scales, top=scales, bottom=lambda *a: SCALE_BELLY)
# Cola de serpiente en tres tramos que se afinan (cadera 3x3x6, medio 2x2x5, punta 1x1x5): solo escamas, sin cabeza.
box(0, 46, 3, 3, 6, **snake)
box(32, 24, 2, 2, 5, **snake)
box(28, 32, 1, 1, 5, **snake)

img.save(sys.argv[1])
print("ok", sys.argv[1])
