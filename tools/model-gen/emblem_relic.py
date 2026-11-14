"""Emblema de Mythic Relics (elegido el 2026-11-14): la reliquia en su altar, trabajada en color y detalle.

Ícono del mod (64x64 dibujado, x2 = 128): un disco de cielo nocturno con borde de oro y remaches (contraste en
cualquier fondo), un halo que se aclara hacia la reliquia, rayos alternados de oro y turquesa, la reliquia (una gema
tallada en un engaste de oro con forma de corona solar) flotando sobre un pedestal de mármol cálido con bandas de oro y
tres runas que brillan, y chispas sueltas.

Emblema de la pestaña (16x16): la misma idea dibujada píxel a píxel (no se achica la grande): disco oscuro con borde de
oro, la gema turquesa con su engaste y el pedestal claro con la banda de oro; contorno oscuro para que no se pierda en
el gris de la pestaña.
Uso: python emblem_relic.py <carpeta_salida>
"""
import math
import os
import sys

from PIL import Image, ImageDraw


def hexc(h, a=255):
    return tuple(int(h[i:i + 2], 16) for i in (1, 3, 5)) + (a,)


def mix(a, b, t):
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3)) + (255,)


INK = hexc("#140e1e")
GOLD_L, GOLD, GOLD_D, GOLD_DD = hexc("#fff3b0"), hexc("#f2c23e"), hexc("#c48a1c"), hexc("#7a5212")
TEAL_W, TEAL_L, TEAL, TEAL_D, TEAL_DD = hexc("#eafffb"), hexc("#9ef5ea"), hexc("#2fc6c0"), hexc("#16808a"), hexc("#0c4a58")
NIGHT_L, NIGHT, NIGHT_D = hexc("#3a3480"), hexc("#241e58"), hexc("#140f36")
MARBLE_L, MARBLE, MARBLE_D, MARBLE_DD = hexc("#fbf3e2"), hexc("#e6d7b8"), hexc("#b9a682"), hexc("#7e6c4c")
RUNE = hexc("#7ff7ff")


def outline(im, color=INK):
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
                if 0 <= nx < w and 0 <= ny < h and px[nx, ny][3] > 200:
                    po[x, y] = color
                    break
    return out


def relic_big(N=64):
    im = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    c = N / 2
    gx, gy = c, 23            # centro de la reliquia
    # Disco de cielo: degradado radial que se aclara hacia la reliquia.
    for r in range(29, 0, -1):
        t = r / 29
        col = mix(NIGHT_L, NIGHT_D, min(1.0, t * 1.15))
        ox = (c - gx) * (1 - t)
        oy = (c - gy) * (1 - t) * 0.0
        d.ellipse((c - r + ox * 0, c - r, c + r, c + r), fill=col)
    # Halo: resplandor dorado que se funde con el cielo (colores mezclados, sin transparencias que ensucien).
    glow = hexc("#ffe7a0")
    for r in range(17, 7, -1):
        t = (r - 8) / 9
        d.ellipse((gx - r, gy - r, gx + r, gy + r), fill=mix(glow, NIGHT_L, t ** 0.7))
    # Estrellas.
    for i, (x, y) in enumerate([(12, 16), (50, 14), (9, 30), (55, 31), (18, 8), (46, 7), (14, 44), (51, 45), (27, 6), (38, 5)]):
        d.point((x, y), fill=(255, 255, 255, 255) if i % 3 else GOLD_L)
    # Rayos alternados de oro y turquesa.
    for i in range(16):
        a = i * math.pi / 8 + math.pi / 16
        long = i % 2 == 0
        r0, r1 = 11, (24 if long else 18)
        col = GOLD_L if long else TEAL_L
        for k in range(r0, r1):
            x, y = gx + math.cos(a) * k, gy + math.sin(a) * k
            if math.hypot(x - c, y - c) < 27:
                d.point((round(x), round(y)), fill=col if k < r1 - 2 else mix(col, NIGHT, 0.5))
    # Pedestal de mármol: base de dos escalones, fuste con bandas de oro y runas, y capitel.
    d.rectangle((c - 15, 53, c + 15, 56), fill=MARBLE_D)
    d.line((c - 15, 53, c + 15, 53), fill=MARBLE)
    d.rectangle((c - 12, 49, c + 12, 52), fill=MARBLE)
    d.line((c - 12, 49, c + 12, 49), fill=MARBLE_L)
    d.rectangle((c - 9, 38, c + 9, 48), fill=MARBLE)
    d.rectangle((c - 9, 38, c - 7, 48), fill=MARBLE_L)                  # luz a la izquierda
    d.rectangle((c + 7, 38, c + 9, 48), fill=MARBLE_D)                  # sombra a la derecha
    d.line((c - 9, 40, c + 9, 40), fill=GOLD)                            # bandas de oro
    d.line((c - 9, 47, c + 9, 47), fill=GOLD_D)
    for rx, glyph in ((c - 5, ((0, 0), (0, 1), (0, 2), (1, 1))), (c - 1, ((0, 0), (1, 1), (0, 2), (1, 0))),
                      (c + 3, ((0, 0), (1, 0), (1, 1), (1, 2)))):       # tres runas que brillan
        for dx, dy in glyph:
            d.point((rx + dx, 42 + dy), fill=RUNE)
    d.rectangle((c - 12, 35, c + 12, 37), fill=MARBLE_L)                 # capitel
    d.line((c - 12, 37, c + 12, 37), fill=GOLD)
    d.line((c - 12, 35, c + 12, 35), fill=hexc("#fffaf0"))
    d.ellipse((c - 8, 33, c + 8, 36), fill=hexc("#bff7f2"))              # luz de la reliquia sobre el capitel
    # La reliquia: corona solar de oro (ocho puntas), aro, gema tallada.
    for i in range(8):
        a = i * math.pi / 4
        tip = (gx + math.cos(a) * 11.5, gy + math.sin(a) * 11.5)
        l = (gx + math.cos(a - 0.28) * 8, gy + math.sin(a - 0.28) * 8)
        r = (gx + math.cos(a + 0.28) * 8, gy + math.sin(a + 0.28) * 8)
        d.polygon([tip, l, r], fill=GOLD if i % 2 else GOLD_L)
    d.ellipse((gx - 9, gy - 9, gx + 9, gy + 9), fill=GOLD_DD)
    d.ellipse((gx - 8, gy - 8, gx + 8, gy + 8), fill=GOLD)
    d.ellipse((gx - 8, gy - 8, gx + 5, gy + 5), outline=GOLD_L)
    hexa = [(gx + math.cos(a) * 6.5, gy + math.sin(a) * 6.5) for a in [math.pi / 2 + i * math.pi / 3 for i in range(6)]]
    inner = [(gx + math.cos(a) * 3.6, gy + math.sin(a) * 3.6) for a in [math.pi / 2 + i * math.pi / 3 for i in range(6)]]
    d.polygon(hexa, fill=TEAL_DD)
    d.polygon(inner, fill=TEAL)
    d.polygon([hexa[3], hexa[4], inner[4], inner[3]], fill=TEAL_L)
    d.polygon([hexa[4], hexa[5], inner[5], inner[4]], fill=hexc("#5fe0d8"))
    d.polygon([hexa[2], hexa[3], inner[3], inner[2]], fill=TEAL)
    d.polygon([hexa[0], hexa[1], inner[1], inner[0]], fill=TEAL_D)
    d.point([(round(gx - 2), round(gy - 3)), (round(gx - 1), round(gy - 3))], fill=TEAL_W)
    # Chispas alrededor.
    for x, y in ((gx - 15, gy - 8), (gx + 14, gy - 11), (gx - 12, gy + 10), (gx + 15, gy + 7)):
        d.point((round(x), round(y)), fill=(255, 255, 255, 255))
        d.point((round(x) + 1, round(y)), fill=GOLD_L)
    # Borde de oro del disco con remaches, y contorno.
    ring = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    rd = ImageDraw.Draw(ring)
    rd.ellipse((2, 2, N - 3, N - 3), outline=GOLD_D, width=3)
    rd.ellipse((3, 3, N - 4, N - 4), outline=GOLD, width=1)
    rd.arc((3, 3, N - 4, N - 4), 160, 290, fill=GOLD_L, width=1)
    for i in range(8):
        a = i * math.pi / 4 + math.pi / 8
        x, y = c + math.cos(a) * 28.5, c + math.sin(a) * 28.5
        rd.rectangle((round(x) - 1, round(y) - 1, round(x), round(y)), fill=GOLD_L)
    im.alpha_composite(ring)
    # Lo que sale del disco (el pedestal) se recorta al disco, salvo la base, que se apoya sobre el borde.
    mask = Image.new("L", (N, N), 0)
    ImageDraw.Draw(mask).ellipse((2, 2, N - 3, N - 3), fill=255)
    clipped = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    clipped.paste(im, (0, 0), mask)
    return outline(clipped)


# Pestaña: dibujada a mano, píxel por píxel.
TAB = [
    "....kkkkkkkk....",
    "..kkdggggggdkk..",
    ".kdnnnnGGnnnndk.",
    ".kgnnnGggGnnngk.",
    "kdnnnGgTtgGnnndk",
    "kgnnngtWTtgnnngk",
    "kgnnnGgtDgGnnngk",
    "kgnnnnGggGnnnngk",
    "kdnnnnnGGnnnnndk",
    "kdnnnnSSSSnnnndk",
    ".kdnnnsggsnnndk.",
    ".kdnnnsrrsnnndk.",
    "..kdnSSSSSSndk..",
    "..kkdzzzzzzdkk..",
    "....kkkkkkkk....",
    "................",
]
TAB_COLORS = {".": (0, 0, 0, 0), "k": INK, "d": GOLD_D, "g": GOLD, "G": GOLD_L, "n": NIGHT, "t": TEAL, "T": TEAL_L,
              "W": TEAL_W, "D": TEAL_D, "S": MARBLE_L, "s": MARBLE, "z": MARBLE_D, "r": RUNE}


def relic_tab():
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = im.load()
    for y, row in enumerate(TAB):
        row = (row + "................")[:16]
        for x, ch in enumerate(row):
            px[x, y] = TAB_COLORS[ch]
    return im


if __name__ == "__main__":
    out = sys.argv[1]
    os.makedirs(out, exist_ok=True)
    relic_big().resize((128, 128), Image.NEAREST).save(os.path.join(out, "relic_icon.png"))
    relic_tab().save(os.path.join(out, "relic_tab.png"))
    print("ok")
