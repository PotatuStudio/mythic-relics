"""Piedra de lo petrificado por la cabeza de Medusa, en cuatro grados: lisa, grietas finas, grietas grandes y a punto de
romperse (textures/entity/petrified/stone_0..3.png, 64x64; el modelo de cada mob la recorre entera con sus UV).

Piedra gris con ruido por texel y vetas; las grietas son caminos oscuros con borde claro que crecen de un grado al
siguiente (cada grado agrega grietas nuevas y alarga las anteriores); en el último faltan trozos (huecos oscuros).
Uso: python petrified_textures.py
"""
import os
import random

from PIL import Image

from moddata import ASSETS

SIZE = 64
OUT = os.path.join(ASSETS, "textures", "entity", "petrified")


def base(rng):
    img = Image.new("RGBA", (SIZE, SIZE))
    px = img.load()
    for y in range(SIZE):
        for x in range(SIZE):
            v = 128 + rng.randint(-14, 14)
            if (x * 7 + y * 3) % 23 == 0:
                v -= 18          # vetas
            px[x, y] = (v, v, v + 2, 255)
    return img


def crack_paths(rng, count, length):
    paths = []
    for _ in range(count):
        x, y = rng.randrange(SIZE), rng.randrange(SIZE)
        path = [(x, y)]
        dx, dy = rng.choice([(1, 0), (0, 1), (1, 1), (1, -1)])
        for _ in range(length):
            if rng.random() < 0.35:
                dx, dy = rng.choice([(1, 0), (0, 1), (1, 1), (1, -1), (-1, 1)])
            x, y = (x + dx) % SIZE, (y + dy) % SIZE
            path.append((x, y))
        paths.append(path)
    return paths


def draw(img, paths, upto):
    px = img.load()
    for path in paths:
        for (x, y) in path[:upto]:
            px[x, y] = (52, 50, 54, 255)
            lx, ly = (x + 1) % SIZE, y
            r, g, b, a = px[lx, ly]
            if r > 70:
                px[lx, ly] = (min(255, r + 26), min(255, g + 26), min(255, b + 26), 255)


if __name__ == "__main__":
    os.makedirs(OUT, exist_ok=True)
    rng = random.Random(7)
    stone = base(rng)
    paths = crack_paths(rng, 22, 26)
    holes = [(rng.randrange(SIZE), rng.randrange(SIZE)) for _ in range(14)]
    # Grado 0: lisa. 1: pocas grietas cortas. 2: más y más largas. 3: todas, largas, y huecos donde falta piedra.
    for stage, (n, upto) in enumerate([(0, 0), (8, 10), (16, 18), (22, 27)]):
        img = stone.copy()
        draw(img, paths[:n], upto)
        if stage == 3:
            px = img.load()
            for hx, hy in holes:
                for dy in range(2):
                    for dx in range(3):
                        px[(hx + dx) % SIZE, (hy + dy) % SIZE] = (34, 32, 36, 255)
        img.save(os.path.join(OUT, f"stone_{stage}.png"))
    print("ok")
