"""Textura 64x32 del escarabajo (layout de ScarabModel). Uso: python scarab_texture.py <salida.png>"""
import random
import sys

from PIL import Image

random.seed(7)
GOLD_L = (255, 228, 120)
GOLD = (232, 182, 52)
GOLD_M = (198, 146, 34)
GOLD_D = (146, 98, 22)
OUTL = (86, 56, 14)
TURQ_L = (110, 228, 208)
TURQ = (40, 172, 160)
TURQ_D = (22, 112, 112)
LAPIS_L = (80, 120, 220)
LAPIS = (42, 72, 172)
LAPIS_D = (26, 44, 118)

img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
px = img.load()


def faces(u, v, w, h, d):
    """Rectángulos (x, y, ancho, alto) de cada cara de una caja, como los arma Minecraft."""
    return {
        "top": (u + d, v, w, d),
        "bottom": (u + d + w, v, w, d),
        "east": (u, v + d, d, h),
        "front": (u + d, v + d, w, h),
        "west": (u + d + w, v + d, d, h),
        "back": (u + 2 * d + w, v + d, w, h),
    }


def fill(rect, color_fn):
    x0, y0, w, h = rect
    for y in range(h):
        for x in range(w):
            px[x0 + x, y0 + y] = (*color_fn(x, y, w, h), 255)


def jitter(c, amount=10):
    k = random.randint(-amount, amount)
    return tuple(max(0, min(255, ch + k)) for ch in c)


def side_bands(x, y, w, h):
    """Costados: borde dorado arriba, banda turquesa al medio, sombra abajo."""
    if h == 1:
        return jitter(GOLD_M)
    if y == 0:
        return jitter(GOLD)
    if y == h - 1:
        return jitter(GOLD_D, 6)
    return jitter(TURQ if x % 3 else TURQ_D, 6)


# Élitros (0,0) 6x3x7
f = faces(0, 0, 6, 3, 7)


def elytra_top(x, y, w, h):
    if y in (0, h - 1) or x in (0, w - 1):
        return jitter(GOLD_D, 6)
    if x in (1, w - 2):
        return jitter(TURQ_L if y == 1 else TURQ, 6)
    # Centro dorado con la línea entre los dos élitros y un brillo.
    if y == 1:
        return GOLD_L
    return jitter(GOLD if x == 2 else GOLD_M, 8)


fill(f["top"], elytra_top)
fill(f["bottom"], lambda x, y, w, h: jitter(OUTL, 6))
for side in ("east", "west", "front", "back"):
    fill(f[side], side_bands)

# Cúpula (0,10) 4x1x5: lapislázuli con borde turquesa.
f = faces(0, 10, 4, 1, 5)


def dome_top(x, y, w, h):
    if x in (0, w - 1) or y in (0, h - 1):
        return jitter(TURQ, 6)
    if (x, y) == (1, 1):
        return LAPIS_L
    return jitter(LAPIS if (x + y) % 2 else LAPIS_D, 6)


fill(f["top"], dome_top)
fill(f["bottom"], lambda x, y, w, h: jitter(LAPIS_D, 4))
for side in ("east", "west", "front", "back"):
    fill(f[side], lambda x, y, w, h: jitter(TURQ_D, 6))

# Pronoto (26,0) 5x2x3: dorado con un punto turquesa al centro.
f = faces(26, 0, 5, 2, 3)


def pronotum_top(x, y, w, h):
    if (x, y) == (2, 1):
        return TURQ_L
    if x in (0, w - 1):
        return jitter(GOLD_D, 6)
    return GOLD_L if y == 0 and x in (1, 3) else jitter(GOLD, 8)


fill(f["top"], pronotum_top)
fill(f["bottom"], lambda x, y, w, h: jitter(OUTL, 6))
for side in ("east", "west", "front", "back"):
    fill(f[side], lambda x, y, w, h: jitter(GOLD_M if y == 0 else GOLD_D, 6))

# Cabeza en pala (26,5) 4x1x2: dorado oscuro con los "dientes" del borde más claros.
f = faces(26, 5, 4, 1, 2)
fill(f["top"], lambda x, y, w, h: jitter(GOLD if (y == 0 and x % 2 == 0) else GOLD_D, 6))
fill(f["bottom"], lambda x, y, w, h: jitter(OUTL, 4))
for side in ("east", "west", "front", "back"):
    fill(f[side], lambda x, y, w, h: jitter(GOLD_D, 6))

# Patas (42,0) 4x1x1: bronce oscuro.
f = faces(42, 0, 4, 1, 1)
for rect in f.values():
    fill(rect, lambda x, y, w, h: jitter(OUTL if x == w - 1 else GOLD_D, 6))

img.save(sys.argv[1])
print("ok", sys.argv[1])
