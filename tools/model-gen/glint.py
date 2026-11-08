"""Brillo animado para los íconos 2D (como los anillos de Rings of Ascension): un destello diagonal recorre las
partes metálicas o las gemas y luego descansa un rato.

glint(img, shiny) -> (tira de cuadros 16 x 16N, dict para el .png.mcmeta)
  img:   imagen 16x16 (filas de tuplas RGBA)
  shiny: función (x, y) -> bool que dice qué píxeles pueden brillar
"""

REST_TICKS = 50        # pausa entre destellos (2,5 s)
FRAME_TICKS = 1        # cada paso del destello dura un tick


def _lighten(c, f):
    return (c[0] + int((255 - c[0]) * f), c[1] + int((255 - c[1]) * f), c[2] + int((255 - c[2]) * f), c[3])


def glint(img, shiny):
    cells = [(x, y) for y in range(16) for x in range(16) if img[y][x][3] and shiny(x, y)]
    if not cells:
        return img, None
    lo = min(x + y for x, y in cells)
    hi = max(x + y for x, y in cells)
    frames = [img]
    for t in range(lo, hi + 2):
        f = [row[:] for row in img]
        for x, y in cells:
            d = t - (x + y)
            if d == 0:
                f[y][x] = _lighten(img[y][x], 0.65)
            elif d == 1:
                f[y][x] = _lighten(img[y][x], 0.3)
        frames.append(f)
    strip = [row for f in frames for row in f]
    order = [{"index": 0, "time": REST_TICKS}] + [{"index": i, "time": FRAME_TICKS} for i in range(1, len(frames))]
    return strip, {"animation": {"frames": order}}
