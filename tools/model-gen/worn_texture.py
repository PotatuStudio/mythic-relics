"""Texturas pintadas para las reliquias puestas (en lugar de los colores planos de la paleta).

Cada cara de cada elemento recibe su propio trozo de textura (2 texeles por píxel del modelo), pintado a partir del
color del elemento con:
  - luz y volumen: la cara de arriba más clara, la de abajo más oscura, los costados un poco más oscuros; en las caras
    laterales, un degradé de arriba (más claro) hacia abajo;
  - borde que define la pieza: fila de arriba con luz, fila de abajo y costados en sombra;
  - ruido fino por texel (nada queda liso);
  - un patrón según el material: hebras en el pelo y la crin, brillo en diagonal en los metales, grano en el cuero,
    raquis y barbas en las plumas, escamas en las serpientes, grietas en la piedra y un brillo húmedo en la cera.
Devuelve los elementos con las UV nuevas y la imagen (se guarda en textures/item/worn/<reliquia>.png).
"""
import math
import random

from PIL import Image

DENSITY = 2   # texeles por píxel del modelo

FUR = {"fur", "fur_l", "fur_d", "mane", "crest", "crest_l", "crest_d"}
METAL = {"gold", "gold_l", "gold_d", "gold_dd", "bronze", "bronze_d", "iron", "iron_l", "iron_d", "silver", "silver_d"}
LEATHER = {"leather", "leather_l", "leather_d", "black", "black_l", "black_d"}
FEATHER = {"white", "wing", "wing_l", "wing_d", "feather_w", "falcon", "falcon_l", "falcon_d"}
SCALES = {"cobra", "cobra_l", "cobra_d", "snake"}
STONE = {"stone", "stone_d"}
WAX = {"wax", "wax_l", "ivory", "ivory_d", "amber", "ember", "ruby"}


def rgb(hexcode):
    return tuple(int(hexcode[i:i + 2], 16) for i in (1, 3, 5))


def face_size(face, frm, to):
    dx, dy, dz = (to[0] - frm[0]), (to[1] - frm[1]), (to[2] - frm[2])
    w, h = {"north": (dx, dy), "south": (dx, dy), "east": (dz, dy), "west": (dz, dy), "up": (dx, dz), "down": (dx, dz)}[face]
    return max(1, int(round(w * DENSITY))), max(1, int(round(h * DENSITY)))


def paint(px, ox, oy, w, h, face, color, base, rng):
    shade = {"up": 1.14, "down": 0.68, "north": 1.0, "south": 1.0, "east": 0.88, "west": 0.88}[face]
    side = face not in ("up", "down")
    phase = [rng.randrange(4) for _ in range(w)]
    column = [1.0 + rng.uniform(-0.06, 0.06) for _ in range(w)]
    sheen = rng.uniform(0.2, 0.45)
    for y in range(h):
        for x in range(w):
            f = shade * (1.0 + rng.uniform(-0.05, 0.05))
            if side and h > 1:
                f *= 1.07 - 0.18 * (y / (h - 1))
            if w >= 3 and h >= 3:
                if y == 0:
                    f *= 1.1
                elif y == h - 1:
                    f *= 0.8
                if x == 0 or x == w - 1:
                    f *= 0.88
            if color in FUR:
                f *= column[x]
                if (y + phase[x]) % 4 == 0:
                    f *= 0.84
            elif color in METAL:
                d = (x + (h - 1 - y)) / max(1, w + h - 2)
                if abs(d - sheen) < 0.12:
                    f *= 1.28
                f *= 1.03 if y % 2 == 0 else 0.97
            elif color in LEATHER:
                if rng.random() < 0.18:
                    f *= 0.85
            elif color in FEATHER:
                if side and w >= 3 and x == w // 2:
                    f *= 1.12
                elif (x + y) % 3 == 0:
                    f *= 0.9
            elif color in SCALES:
                if (x // 2 + y // 2) % 2 == 0:
                    f *= 0.86
            elif color in STONE:
                if rng.random() < 0.08:
                    f *= 0.72
            elif color in WAX:
                if x <= 1 and y <= 1:
                    f *= 1.25
            r, g, b = (max(0, min(255, int(c * f))) for c in base)
            px[ox + x, oy + y] = (r, g, b, 255)


def texture(name, elements, colors, namespace):
    """Elementos con caras de un solo color (clave '__color') -> elementos con UV propias y la imagen pintada."""
    rng = random.Random(name)
    faces = []
    for el in elements:
        for face in el["faces"]:
            faces.append((el, face, *face_size(face, el["from"], el["to"])))
    faces.sort(key=lambda f: -f[3])
    width = 128
    while True:
        x = y = shelf = 0
        spots = []
        for el, face, w, h in faces:
            if x + w > width:
                x, y, shelf = 0, y + shelf, 0
            spots.append((x, y))
            x += w
            shelf = max(shelf, h)
        height = 1
        while height < y + shelf:
            height *= 2
        if height <= width * 2:
            break
        width *= 2
    img = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    px = img.load()
    tex = f"{namespace}:item/worn/{name}"
    for (el, face, w, h), (ox, oy) in zip(faces, spots):
        color = el["__color"]
        paint(px, ox, oy, w, h, face, color, rgb(colors[color]), rng)
        u0, v0 = ox * 16 / width, oy * 16 / height
        u1, v1 = (ox + w) * 16 / width, (oy + h) * 16 / height
        el["faces"][face] = {"uv": [round(u0, 4), round(v0, 4), round(u1, 4), round(v1, 4)], "texture": "#t"}
    return {"textures": {"t": tex, "particle": tex}, "elements": elements}, img
