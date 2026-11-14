"""Conceptos para el nuevo ícono del mod y el emblema de la pestaña creativa: un símbolo propio de cada mitología (no
ítems del juego), dibujado en pixel art nítido, sin suavizado.

- Egipto: la pirámide con el disco solar de Ra.     - Grecia: el templo de columnas con su frontón.
- Roma: el águila de la legión con las alas abiertas. - Nórdicos: el barco vikingo con proa de dragón y vela a rayas.
- China: el long, el dragón serpentino, con la perla. - Japón: el torii rojo con el sol naciente.

Composiciones del ícono del mod (128x128): A, un escudo de seis cuarteles; B, un medallón con los seis símbolos en
anillo alrededor de una gema. Emblema de la pestaña (16x16): tres variantes simples.
Uso: python emblem_concepts.py <carpeta_salida>   (escribe PNG sueltos que arma emblem_page.py)
"""
import math
import os
import sys

from PIL import Image, ImageDraw

S = 28   # tamaño de cada símbolo


def hexc(h, a=255):
    return tuple(int(h[i:i + 2], 16) for i in (1, 3, 5)) + (a,)


GOLD, GOLD_L, GOLD_D, INK = hexc("#e8b830"), hexc("#ffe27a"), hexc("#a8781a"), hexc("#1a1208")


def canvas():
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    return im, ImageDraw.Draw(im)


def outline(im, color=INK):
    """Contorno de 1 px por fuera de lo dibujado."""
    px = im.load()
    out = im.copy()
    po = out.load()
    for y in range(S):
        for x in range(S):
            if px[x, y][3]:
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < S and 0 <= ny < S and px[nx, ny][3]:
                    po[x, y] = color
                    break
    return out


def egypt():
    im, d = canvas()
    # Disco solar rojo con rayos dorados.
    d.ellipse((10, 1, 17, 8), fill=hexc("#e0502a"))
    d.point([(13, 3), (12, 2)], fill=hexc("#ff9a5a"))
    for x0, y0, x1, y1 in ((8, 4, 6, 4), (19, 4, 21, 4), (13, 0, 13, 0)):
        d.line((x0, y0, x1, y1), fill=GOLD)
    # Pirámide: cara iluminada y cara en sombra, con hiladas.
    d.polygon([(13, 10), (2, 26), (13, 26)], fill=hexc("#f0d080"))
    d.polygon([(14, 10), (14, 26), (25, 26)], fill=hexc("#c89a48"))
    for y in (14, 18, 22):
        half = (y - 10) * 11 / 16
        d.line((13 - half, y, 13 + half, y), fill=hexc("#a8783a"))
    d.line((13, 10, 13, 10), fill=GOLD_L)
    return outline(im)


def greece():
    im, d = canvas()
    marble, shadow = hexc("#f2efe6"), hexc("#bdb6a6")
    d.polygon([(14, 3), (2, 10), (26, 10)], fill=marble)            # frontón
    d.polygon([(14, 5), (6, 9), (22, 9)], fill=hexc("#7fa6d8"))       # tímpano azul (pintado como en los templos)
    d.rectangle((2, 10, 26, 12), fill=shadow)                          # entablamento
    for x in (4, 9, 14, 19, 24):                                       # columnas con acanaladura
        d.rectangle((x - 1, 13, x + 1, 23), fill=marble)
        d.line((x + 1, 13, x + 1, 23), fill=shadow)
    d.rectangle((1, 24, 27, 26), fill=shadow)                           # escalinata
    d.line((2, 24, 26, 24), fill=marble)
    return outline(im)


def rome():
    """El aquila de las legiones: alas alzadas en V con las plumas de las puntas separadas, cabeza de perfil con el pico
    ganchudo, cola en abanico y las garras sobre la barra del estandarte."""
    im, d = canvas()
    gold, gold_d, gold_l = GOLD, GOLD_D, GOLD_L
    for side in (-1, 1):
        # Ala alzada: del hombro hacia arriba y afuera, con cuatro plumas largas en la punta.
        base = [(14 + side * 2, 13), (14 + side * 4, 9), (14 + side * 7, 5), (14 + side * 10, 2)]
        d.polygon([(14 + side * 2, 15), (14 + side * 2, 11), (14 + side * 6, 6), (14 + side * 11, 1), (14 + side * 12, 4),
                   (14 + side * 11, 9), (14 + side * 8, 13), (14 + side * 5, 16)], fill=gold)
        for k in range(4):                                     # plumas separadas en el borde de afuera
            x0, y0 = 14 + side * (7 + k), 13 - k * 3
            d.line((x0, y0, x0 + side * 3, y0 + 1), fill=gold)
            d.point((x0 + side * 3, y0 + 1), fill=gold_d)
        for x, y in base:                                      # hueso del ala, más claro
            d.point((x, y), fill=gold_l)
    d.ellipse((11, 11, 17, 21), fill=gold)                      # cuerpo
    d.line((13, 13, 13, 19), fill=gold_l)
    d.ellipse((9, 6, 15, 12), fill=gold)                        # cabeza mirando a la izquierda
    d.point((11, 8), fill=INK)                                  # ojo
    d.polygon([(9, 8), (6, 9), (7, 11), (9, 10)], fill=hexc("#f8f0c0"))   # pico ganchudo
    d.polygon([(11, 20), (14, 24), (17, 20)], fill=gold_d)       # cola en abanico
    d.line((12, 21, 14, 23), fill=gold)
    d.line((11, 22, 12, 24), fill=gold_d)                        # garras
    d.line((17, 22, 16, 24), fill=gold_d)
    d.rectangle((6, 25, 22, 26), fill=hexc("#8a5a30"))           # barra del estandarte
    d.point([(6, 25), (22, 25)], fill=gold)
    return outline(im)


def norse():
    im, d = canvas()
    wood, wood_d = hexc("#a8743a"), hexc("#6e4a22")
    # Vela a rayas rojas y blancas, mástil.
    d.line((14, 1, 14, 18), fill=wood_d)
    d.rectangle((7, 3, 21, 14), fill=hexc("#f2efe6"))
    for x in (7, 11, 15, 19):
        d.rectangle((x, 3, x + 1, 14), fill=hexc("#c8302a"))
    # Casco: curvo, con la proa de dragón a la izquierda y la popa enroscada a la derecha.
    d.polygon([(3, 17), (25, 17), (22, 22), (6, 22)], fill=wood)
    d.line((4, 18, 24, 18), fill=hexc("#d8a868"))
    d.polygon([(3, 17), (1, 12), (2, 9), (5, 8), (6, 10), (4, 11), (5, 16)], fill=wood)   # cuello del dragón
    d.point((3, 9), fill=GOLD_L)
    d.polygon([(25, 17), (27, 13), (26, 10), (24, 11), (25, 14), (23, 17)], fill=wood)  # popa
    for x in (8, 12, 16, 20):                                                            # escudos en la borda
        d.ellipse((x - 1, 17, x + 1, 19), fill=hexc("#e8b830") if x % 8 == 0 else hexc("#2f52b4"))
    # Olas.
    for x in range(1, 27, 4):
        d.arc((x, 22, x + 4, 26), 180, 360, fill=hexc("#5f86e6"))
    return outline(im)


def china():
    """El long: cuerpo grueso en S de escamas jade con aletas rojas en el lomo, dos patas con garras, cabeza con cuernos,
    melena y bigotes, la boca abierta hacia la perla llameante."""
    im, d = canvas()
    jade, jade_d, belly, red = hexc("#3fa07a"), hexc("#23704f"), hexc("#f0d080"), hexc("#d8392b")
    pts = []
    for i in range(48):
        t = i / 47
        x = 2 + t * 17
        y = 24 - 13 * t - math.sin(t * math.pi * 2) * 5     # ondula subiendo hasta la cabeza
        pts.append((x, y))
    for i, (x, y) in enumerate(pts):                              # aletas del lomo primero, debajo del cuerpo
        if i % 6 == 3:
            r = 1.6 + 1.6 * (i / 47)
            d.polygon([(x - 1, y - r + 0.5), (x + 1, y - r - 2.5), (x + 2, y - r + 0.5)], fill=red)
    for i, (x, y) in enumerate(pts):
        r = 1.6 + 1.6 * (i / 47)
        d.ellipse((x - r, y - r, x + r, y + r), fill=jade)
    for i, (x, y) in enumerate(pts):
        if i % 3 == 0:
            r = 1.6 + 1.6 * (i / 47)
            d.point((round(x), round(y + r * 0.6)), fill=belly)   # vientre claro
            d.point((round(x), round(y - r * 0.3)), fill=jade_d)   # escamas
    for i in (14, 34):                                              # patas con garras
        x, y = pts[i]
        d.line((x, y + 2, x - 1, y + 5), fill=jade_d)
        d.line((x - 2, y + 5, x + 1, y + 5), fill=GOLD_L)
    # Cabeza grande con boca abierta, cuernos, melena roja y bigotes; la perla delante.
    d.ellipse((17, 4, 25, 12), fill=jade)
    d.polygon([(24, 6), (27, 6), (26, 8)], fill=jade)              # mandíbula de arriba
    d.polygon([(24, 10), (27, 11), (24, 12)], fill=jade_d)         # mandíbula de abajo (boca abierta)
    d.point((22, 7), fill=hexc("#ff4a2a"))                          # ojo
    d.line((19, 4, 17, 0), fill=GOLD)                               # cuernos
    d.line((21, 4, 21, 0), fill=GOLD)
    d.line((18, 9, 15, 12), fill=red)                               # melena
    d.line((18, 7, 15, 8), fill=red)
    d.line((27, 8, 27, 3), fill=GOLD_L)                             # bigote
    d.ellipse((24, 14, 27, 17), fill=hexc("#f4f4ff"))               # perla
    d.point((25, 14), fill=hexc("#8af0dc"))
    d.point((26, 13), fill=hexc("#ffb04a"))                         # llama de la perla
    return outline(im)


def japan():
    im, d = canvas()
    red, red_d = hexc("#d8392b"), hexc("#8c1d16")
    d.ellipse((7, 4, 21, 18), fill=hexc("#ff7a3a"))                    # sol naciente detrás
    d.ellipse((9, 6, 19, 16), fill=hexc("#ffb04a"))
    d.polygon([(1, 4), (27, 4), (26, 7), (2, 7)], fill=red)             # kasagi (dintel curvo)
    d.line((1, 3, 27, 3), fill=INK)
    d.rectangle((4, 10, 24, 11), fill=red)                              # nuki
    d.rectangle((13, 7, 15, 10), fill=red_d)                            # gakuzuka
    d.rectangle((6, 7, 8, 26), fill=red)                                # pilares
    d.rectangle((20, 7, 22, 26), fill=red)
    d.line((8, 7, 8, 26), fill=red_d)
    d.line((22, 7, 22, 26), fill=red_d)
    d.rectangle((5, 25, 9, 26), fill=INK)                               # basas negras
    d.rectangle((19, 25, 23, 26), fill=INK)
    return outline(im)


SYMBOLS = [("egypt", egypt, "#2c4f9e"), ("greece", greece, "#1f6fa8"), ("rome", rome, "#a3282a"),
           ("norse", norse, "#3c4a5a"), ("china", china, "#a83228"), ("japan", japan, "#2b2656")]


def shield_mask(size):
    """Escudo: recto arriba y con la punta en arco abajo."""
    w = size
    top, taper, tip = int(w * 0.04), int(w * 0.62), int(w * 0.98)
    x0, x1 = int(w * 0.08), int(w * 0.92)
    cx = (x0 + x1) / 2
    full = (x1 - x0) / 2

    def inside(x, y):
        if y < top or y > tip:
            return False
        half = full if y < taper else full * math.sqrt(max(0.0, 1 - ((y - taper) / (tip - taper)) ** 2))
        return abs(x + 0.5 - cx) <= half
    return inside


def concept_shield():
    """A: escudo de seis cuarteles (2x3) con el color de cada cultura, borde y cruz de oro, y su símbolo."""
    N = 128
    im = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    px = im.load()
    inside = shield_mask(N)
    mid = N // 2
    rows = [(5, 44), (44, 83), (83, 124)]
    for y in range(N):
        for x in range(N):
            if not inside(x, y):
                continue
            r = next((i for i, (a, b) in enumerate(rows) if a <= y < b), 2)
            c = 0 if x < mid else 1
            col = hexc(SYMBOLS[r * 2 + c][2])
            if ((x // 3) + (y // 3)) % 2:
                col = tuple(max(0, v - 14) for v in col[:3]) + (255,)
            px[x, y] = col
    for y in range(N):
        for x in range(N):
            if not inside(x, y):
                continue
            edge = not (inside(x - 4, y) and inside(x + 4, y) and inside(x, y - 4) and inside(x, y + 4))
            div = abs(x - mid + 0.5) < 2 or any(abs(y - b + 0.5) < 2 for _, b in rows[:2])
            if edge or div:
                px[x, y] = GOLD_L if (x + y) % 5 == 0 else GOLD if (x + y) % 3 else GOLD_D
    for y in range(N):
        for x in range(N):
            if not inside(x, y) and any(inside(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                px[x, y] = INK
    for i, (_, make, _) in enumerate(SYMBOLS):
        r, c = divmod(i, 2)
        a, b = rows[r]
        cx = (mid // 2 + 3) if c == 0 else (mid + mid // 2 - 3)
        cy = (a + b) // 2 - (4 if r == 2 else 0)
        sym = make()
        im.alpha_composite(sym, (cx - S // 2, cy - S // 2))
    return im


def concept_medallion():
    """B: medallón de oro con los seis símbolos en anillo sobre sus colores y una gema de reliquia al centro."""
    N = 128
    im = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    c = N / 2
    d.ellipse((2, 2, N - 3, N - 3), fill=INK)
    d.ellipse((4, 4, N - 5, N - 5), fill=GOLD_D)
    d.ellipse((7, 7, N - 8, N - 8), fill=GOLD)
    d.ellipse((11, 11, N - 12, N - 12), fill=hexc("#2a1d10"))
    for i, (_, make, color) in enumerate(SYMBOLS):
        a = -math.pi / 2 + i * math.pi / 3
        x, y = c + math.cos(a) * 38, c + math.sin(a) * 38
        d.ellipse((x - 18, y - 18, x + 18, y + 18), fill=GOLD_D)
        d.ellipse((x - 16, y - 16, x + 16, y + 16), fill=hexc(color))
        im.alpha_composite(make(), (int(x - S / 2), int(y - S / 2)))
    # Gema central (la reliquia), tallada.
    d.ellipse((c - 15, c - 15, c + 15, c + 15), fill=GOLD_D)
    d.polygon([(c, c - 12), (c + 11, c - 3), (c + 7, c + 11), (c - 7, c + 11), (c - 11, c - 3)], fill=hexc("#2f9ba0"))
    d.polygon([(c, c - 12), (c + 11, c - 3), (c, c)], fill=hexc("#8fe6dc"))
    d.polygon([(c, c - 12), (c - 11, c - 3), (c, c)], fill=hexc("#5fc8c0"))
    d.point([(int(c - 3), int(c - 6)), (int(c - 2), int(c - 7))], fill=(255, 255, 255, 255))
    return im


# ---- Emblemas de la pestaña (16x16) -----------------------------------------------------------------------------
def tab_shield():
    """T1: escudo con los seis colores de las culturas y la cruz de oro (sin dibujos: no caben)."""
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = im.load()
    inside = shield_mask(16)
    for y in range(16):
        for x in range(16):
            if not inside(x, y):
                continue
            r = 0 if y < 6 else 1 if y < 11 else 2
            c = 0 if x < 8 else 1
            px[x, y] = hexc(SYMBOLS[r * 2 + c][2])
    for y in range(16):
        for x in range(16):
            if inside(x, y) and (not (inside(x - 1, y) and inside(x + 1, y) and inside(x, y - 1) and inside(x, y + 1))
                                 or x in (7, 8) and y > 1 or y in (5, 10)):
                px[x, y] = GOLD if (x + y) % 2 else GOLD_D
    return im


def tab_medallion():
    """T2: el medallón: aro de oro con seis puntos de color y la gema al centro."""
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.ellipse((0, 0, 15, 15), fill=GOLD_D)
    d.ellipse((1, 1, 14, 14), fill=GOLD)
    d.ellipse((3, 3, 12, 12), fill=hexc("#2a1d10"))
    for i, (_, _, color) in enumerate(SYMBOLS):
        a = -math.pi / 2 + i * math.pi / 3
        x, y = 7.5 + math.cos(a) * 5.6, 7.5 + math.sin(a) * 5.6
        d.point((round(x), round(y)), fill=hexc(color))
    d.polygon([(7, 5), (10, 7), (8, 10), (5, 7)], fill=hexc("#2f9ba0"))
    d.point([(7, 6), (8, 6)], fill=hexc("#8fe6dc"))
    return im


def tab_gem():
    """T3: solo la gema de reliquia tallada, en su engaste de oro (lo que une a todas: son reliquias)."""
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.polygon([(8, 1), (14, 6), (12, 13), (4, 13), (2, 6)], fill=GOLD_D)
    d.polygon([(8, 3), (12, 6), (10.5, 11), (5.5, 11), (4, 6)], fill=hexc("#2f9ba0"))
    d.polygon([(8, 3), (12, 6), (8, 7)], fill=hexc("#8fe6dc"))
    d.polygon([(8, 3), (4, 6), (8, 7)], fill=hexc("#5fc8c0"))
    d.point([(6, 5)], fill=(255, 255, 255, 255))
    return im


if __name__ == "__main__":
    out = sys.argv[1]
    os.makedirs(out, exist_ok=True)
    for name, make, _ in SYMBOLS:
        make().save(os.path.join(out, f"sym_{name}.png"))
    concept_shield().save(os.path.join(out, "icon_A.png"))
    concept_medallion().save(os.path.join(out, "icon_B.png"))
    tab_shield().save(os.path.join(out, "tab_1.png"))
    tab_medallion().save(os.path.join(out, "tab_2.png"))
    tab_gem().save(os.path.join(out, "tab_3.png"))
    print("ok")
