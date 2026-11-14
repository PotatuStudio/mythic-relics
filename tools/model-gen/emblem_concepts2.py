"""Segunda ronda del emblema (2026-11-14): un solo símbolo con personalidad propia que evoque la mitología en general,
sin repartir un espacio por cultura (ver la memoria «identidad del mod»).

1. Ouroboros: la serpiente de oro que se muerde la cola (Egipto, Grecia, Jörmungandr nórdico) rodeando una gema de
   reliquia. 2. Árbol del mundo: el eje del cosmos de casi todas las mitologías (Yggdrasil, el Fusang chino, el árbol
   de las Hespérides) con frutos de oro bajo las estrellas. 3. La reliquia en su altar: un pedestal antiguo con una
   reliquia que flota y brilla.

Cada uno a 64x64 (pixel art, se muestra x2 = 128 como ícono del mod) y a 16x16 (pestaña creativa).
Uso: python emblem_concepts2.py <carpeta_salida>
"""
import math
import os
import sys

from PIL import Image, ImageDraw


def hexc(h, a=255):
    return tuple(int(h[i:i + 2], 16) for i in (1, 3, 5)) + (a,)


GOLD_L, GOLD, GOLD_D, GOLD_DD = hexc("#fff0a0"), hexc("#f0c040"), hexc("#c08a20"), hexc("#7a5410")
INK = hexc("#1a1208")
TEAL_L, TEAL, TEAL_D = hexc("#b8fff4"), hexc("#3cc8bc"), hexc("#1a7a80")
NIGHT, NIGHT_D = hexc("#1e2440"), hexc("#141830")


def outline(im):
    px = im.load()
    w, h = im.size
    out = im.copy()
    po = out.load()
    for y in range(h):
        for x in range(w):
            if px[x, y][3]:
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < w and 0 <= ny < h and px[nx, ny][3]:
                    po[x, y] = INK
                    break
    return out


def gem(d, cx, cy, r):
    """Gema de reliquia tallada (un hexágono con facetas y un brillo)."""
    pts = [(cx + math.cos(a) * r, cy + math.sin(a) * r) for a in [math.pi / 2 + i * math.pi / 3 for i in range(6)]]
    d.polygon(pts, fill=TEAL_D)
    inner = [(cx + math.cos(a) * r * 0.62, cy + math.sin(a) * r * 0.62) for a in [math.pi / 2 + i * math.pi / 3 for i in range(6)]]
    d.polygon(inner, fill=TEAL)
    d.polygon([pts[3], pts[4], inner[4], inner[3]], fill=TEAL_L)        # faceta de arriba a la izquierda, con luz
    d.polygon([pts[4], pts[5], inner[5], inner[4]], fill=hexc("#7fe8de"))
    d.point((round(cx - r * 0.3), round(cy - r * 0.35)), fill=(255, 255, 255, 255))


# ---- 1. Ouroboros -------------------------------------------------------------------------------------------------
def ouroboros(N=64):
    im = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    c, R, w = N / 2, N * 0.36, N * 0.085
    head_a = -math.pi * 0.62                                           # la cabeza arriba a la izquierda
    # Cuerpo: círculos a lo largo del anillo, más fino hacia la cola (que entra en la boca).
    steps = 220
    for i in range(steps):
        t = i / steps
        a = head_a + 0.32 + t * (2 * math.pi - 0.42)
        r = w * (1.0 - 0.55 * t)
        x, y = c + math.cos(a) * R, c + math.sin(a) * R
        d.ellipse((x - r, y - r, x + r, y + r), fill=GOLD)
    # Escamas: un punto oscuro y uno claro alternados a lo largo del lomo.
    for i in range(0, steps, 6):
        t = i / steps
        a = head_a + 0.32 + t * (2 * math.pi - 0.42)
        r = w * (1.0 - 0.55 * t)
        ox, oy = math.cos(a) * (R + r * 0.45), math.sin(a) * (R + r * 0.45)
        ix, iy = math.cos(a) * (R - r * 0.45), math.sin(a) * (R - r * 0.45)
        d.point((round(c + ox), round(c + oy)), fill=GOLD_D)
        d.point((round(c + ix), round(c + iy)), fill=GOLD_L)
    # Cabeza: una cuña grande que sale del anillo, con la mandíbula abierta mordiendo la punta de la cola.
    ca, sa = math.cos(head_a), math.sin(head_a)
    hx, hy = c + ca * R, c + sa * R
    f = (sa, -ca)          # hacia adelante: hacia donde termina la cola
    n = (ca, sa)           # hacia afuera del anillo

    def at(fw, nw):
        return (hx + f[0] * w * fw + n[0] * w * nw, hy + f[1] * w * fw + n[1] * w * nw)
    d.polygon([at(-1.6, 1.4), at(0.6, 1.6), at(2.2, 0.9), at(1.0, 0.2), at(2.0, -0.5), at(0.4, -1.3), at(-1.6, -1.3)], fill=GOLD)
    d.polygon([at(-1.2, 1.2), at(0.6, 1.4), at(1.6, 0.9), at(-0.8, 0.6)], fill=GOLD_L)      # luz sobre la cabeza
    ex, ey = at(0.1, 0.75)
    d.rectangle((round(ex) - 1, round(ey) - 1, round(ex), round(ey)), fill=hexc("#d8392b"))   # ojo
    d.line((at(1.0, 0.2), at(-0.2, 0.0)), fill=GOLD_DD)                                       # boca
    gem(d, c, c, N * 0.16)
    # Destellos alrededor de la gema.
    for a in range(4):
        ang = a * math.pi / 2 + math.pi / 4
        x, y = c + math.cos(ang) * N * 0.22, c + math.sin(ang) * N * 0.22
        d.point((round(x), round(y)), fill=TEAL_L)
    return outline(im)


def ouroboros_tab():
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.ellipse((1, 1, 14, 14), outline=GOLD, width=2)
    d.arc((1, 1, 14, 14), 200, 330, fill=GOLD_L, width=1)
    d.rectangle((2, 3, 5, 6), fill=GOLD)                                # cabeza
    d.point((3, 4), fill=hexc("#d8392b"))
    d.polygon([(7.5, 5), (10, 7.5), (7.5, 10), (5, 7.5)], fill=TEAL)
    d.point((7, 6), fill=TEAL_L)
    return outline(im)


# ---- 2. Árbol del mundo -------------------------------------------------------------------------------------------
def world_tree(N=64):
    im = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    c = N / 2
    d.ellipse((2, 2, N - 3, N - 3), fill=NIGHT)                           # el cielo de fondo, redondo
    for i, (x, y) in enumerate([(12, 14), (50, 12), (8, 32), (56, 30), (20, 6), (44, 6), (30, 4)]):
        d.point((x, y), fill=(255, 255, 255, 255) if i % 2 else hexc("#ffe27a"))
    # Raíces que se abren abajo (los mundos de abajo) y tronco.
    bark, bark_d = hexc("#8a5a30"), hexc("#5a3818")
    for dx in (-14, -8, 8, 14):
        d.line((c, 48, c + dx, 58), fill=bark, width=3)
    d.polygon([(c - 4, 50), (c + 4, 50), (c + 3, 30), (c - 3, 30)], fill=bark)
    d.line((c + 2, 32, c + 2, 50), fill=bark_d)
    # Ramas y copa en tres masas.
    leaf, leaf_l, leaf_d = hexc("#3f9a3a"), hexc("#8ad874"), hexc("#2a6a28")
    for bx, by, r in ((c, 18, 15), (c - 13, 26, 10), (c + 13, 26, 10)):
        d.ellipse((bx - r, by - r * 0.85, bx + r, by + r * 0.85), fill=leaf_d)
        d.ellipse((bx - r + 2, by - r * 0.85 + 1, bx + r - 3, by + r * 0.85 - 3), fill=leaf)
        d.ellipse((bx - r + 4, by - r * 0.85 + 2, bx + 2, by), fill=leaf_l)
    # Frutos de oro (las manzanas de las Hespérides y de Iðunn).
    for x, y in ((c - 6, 14), (c + 7, 20), (c - 14, 27), (c + 12, 29), (c + 1, 9), (c - 3, 24)):
        d.ellipse((x - 2, y - 2, x + 2, y + 2), fill=GOLD)
        d.point((x - 1, y - 1), fill=GOLD_L)
    # Aro de oro alrededor del cielo.
    d.ellipse((2, 2, N - 3, N - 3), outline=GOLD, width=2)
    return outline(im)


def world_tree_tab():
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.ellipse((0, 0, 15, 15), fill=NIGHT)
    d.line((8, 8, 8, 13), fill=hexc("#8a5a30"), width=2)
    d.line((8, 12, 5, 14), fill=hexc("#8a5a30"))
    d.line((8, 12, 11, 14), fill=hexc("#8a5a30"))
    d.ellipse((3, 2, 12, 9), fill=hexc("#3f9a3a"))
    d.ellipse((4, 3, 8, 6), fill=hexc("#8ad874"))
    d.point([(6, 5), (10, 6), (8, 3)], fill=GOLD)
    d.ellipse((0, 0, 15, 15), outline=GOLD, width=1)
    return im


# ---- 3. La reliquia en su altar -----------------------------------------------------------------------------------
def altar(N=64):
    im = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    c = N / 2
    # Rayos de luz detrás de la reliquia (capa aparte, sin contorno).
    rays = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    rd = ImageDraw.Draw(rays)
    for i in range(12):
        a = i * math.pi / 6 + math.pi / 12
        length = 27 if i % 2 else 21
        x, y = c + math.cos(a) * length, 22 + math.sin(a) * length
        rd.line((c, 22, x, y), fill=hexc("#ffe27a", 200 if i % 2 else 140), width=2)
    # Pedestal de piedra con banda grabada.
    stone, stone_l, stone_d = hexc("#bdb6a8"), hexc("#e2dccf"), hexc("#7f786c")
    d.rectangle((c - 16, 54, c + 16, 60), fill=stone_d)
    d.rectangle((c - 14, 50, c + 14, 54), fill=stone)
    d.rectangle((c - 9, 38, c + 9, 50), fill=stone)
    d.line((c - 9, 38, c - 9, 50), fill=stone_l)
    d.line((c + 9, 38, c + 9, 50), fill=stone_d)
    for x in range(int(c - 7), int(c + 8), 3):                         # greca grabada
        d.point((x, 44), fill=stone_d)
        d.point((x + 1, 43), fill=stone_d)
    d.rectangle((c - 12, 35, c + 12, 38), fill=stone_l)
    # La reliquia: una gema engastada en oro que flota, con su halo.
    d.ellipse((c - 13, 9, c + 13, 35), fill=hexc("#ffe27a", 90))
    d.ellipse((c - 10, 12, c + 10, 32), fill=GOLD_D)
    d.ellipse((c - 9, 13, c + 9, 31), fill=GOLD)
    gem(d, c, 22, 7.5)
    for x, y in ((c - 15, 10), (c + 15, 14), (c - 12, 31), (c + 13, 30)):  # destellos
        d.point((x, y), fill=(255, 255, 255, 255))
    body = outline(im)
    rays.alpha_composite(body)
    return rays


def altar_tab():
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.rectangle((4, 13, 11, 14), fill=hexc("#7f786c"))
    d.rectangle((5, 10, 10, 12), fill=hexc("#bdb6a8"))
    d.ellipse((4, 1, 11, 8), fill=GOLD)
    d.polygon([(7.5, 2.5), (10, 4.5), (7.5, 7), (5, 4.5)], fill=TEAL)
    d.point((7, 3), fill=TEAL_L)
    return outline(im)


CONCEPTS = [("ouroboros", ouroboros, ouroboros_tab), ("tree", world_tree, world_tree_tab), ("altar", altar, altar_tab)]

if __name__ == "__main__":
    out = sys.argv[1]
    os.makedirs(out, exist_ok=True)
    for key, big, tab in CONCEPTS:
        big().resize((128, 128), Image.NEAREST).save(os.path.join(out, f"c2_{key}.png"))
        tab().save(os.path.join(out, f"c2_{key}_tab.png"))
    print("ok")
