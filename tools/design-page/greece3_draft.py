"""Borrador de la tanda 3 griega: íconos 16x16 y la piel del león de Nemea puesta (sobre la espalda, con las patas
anudadas en el pecho). No toca el mod. Uso: python greece3_draft.py <salida.json> [<vista.png>]"""
import json
import math
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import china_draft as CD  # noqa: E402
from china_draft import PAL, SHINY, blank, box, glint, grid, outline, png_b64  # noqa: E402,F401
from china2_draft import line  # noqa: E402


def inside_poly(px, py, pts):
    n, hit = len(pts), False
    for i in range(n):
        (x0, y0), (x1, y1) = pts[i], pts[(i + 1) % n]
        if (y0 > py) != (y1 > py) and px < x0 + (py - y0) * (x1 - x0) / (y1 - y0):
            hit = not hit
    return hit


def keraunos():
    """Rayo de Zeus como en las monedas griegas: empuñadura de oro al centro y un rayo en zigzag (un ⚡, con su quiebre
    hacia afuera bien marcado) en cada punta; en diagonal como un arma en la mano. La mitad de abajo es la de arriba
    girada 180°. Deja un píxel libre en cada borde para que el contorno no se corte."""
    upper = {1: {11: 'G', 12: 'W'}, 2: {10: 'G', 11: 'W'}, 3: {9: 'G', 10: 'W'},
             4: {8: 'G', 9: 'W', 10: 'W', 11: 'W', 12: 'G'},          # quiebre hacia afuera
             5: {10: 'G', 11: 'W'}, 6: {9: 'G', 10: 'W'}, 7: {7: 'd', 8: 'g'}}
    c = blank()
    for y, row in upper.items():
        for x, ch in row.items():
            c[y][x] = ch
            c[15 - y][15 - x] = ch
    c[8][7], c[8][8] = 'g', 'G'                              # empuñadura de oro (2x2)
    c = outline(c)
    for x, y in [(13, 1), (13, 5), (2, 14), (2, 10)]:       # chispas celestes
        if c[y][x] == '.':
            c[y][x] = '7'
    return c


def nemea():
    """Piel del león de Nemea: la cabeza del león de frente, como la llevaba Heracles de capucha. Melena desgreñada de
    terracota oscura con luz arriba a la izquierda, cara dorada, ojos, hocico claro, nariz y colmillos."""
    import random
    rnd = random.Random(7)
    c = blank()
    cx, cy = 7.5, 7.2
    for y in range(16):
        for x in range(16):
            dx, dy = x + 0.5 - 8, y + 0.5 - cy - 0.5
            ang = math.atan2(dy, dx)
            r = 7.6 + 0.7 * math.sin(ang * 9) + rnd.uniform(-0.2, 0.2)
            d = math.hypot(dx, dy * 1.05)
            if d <= r:
                light = -(dx + dy) / 10
                c[y][x] = 'c' if light > 0.3 else 'h' if light > -0.35 else 'n'
    for y in range(16):
        for x in range(16):
            dx, dy = (x + 0.5 - 8) / 4.0, (y + 0.5 - 8.6) / 4.5
            if dx * dx + dy * dy <= 1:
                c[y][x] = 'G' if dx + dy < -0.5 else 'g' if dx + dy < 0.5 else 'd'
    for x, y in [(4, 4), (11, 4)]:          # orejas
        c[y][x] = 'g'
    for x, y in [(5, 7), (10, 7)]:          # ojos con ceja
        c[y][x] = 'k'
        c[y - 1][x] = 'd'
    for x in (6, 7, 8, 9):                  # hocico claro
        c[10][x] = '8'
        c[11][x] = '8'
    c[9][7] = c[9][8] = 'u'                 # nariz
    c[10][7] = c[10][8] = 'u'
    c[12][7] = c[12][8] = 'h'               # boca
    c[12][6] = c[12][9] = 'W'               # colmillos
    return outline(c)


# Escudo de Aquiles: redondo, de bronce, con anillos concéntricos (el mundo grabado) y el océano en el borde.
def shield():
    c = blank()
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d <= 7.2:
                light = -((x - cx) + (y - cy)) / 10
                if d > 6.2:
                    ch = '4' if (x + y) % 2 else '3'          # Océano rodeando el borde
                elif d > 5.2:
                    ch = 'B' if light > 0 else 'b'
                elif d > 4.2:
                    ch = 'g' if (x * 3 + y) % 4 else 'G'      # Ciudades, cosechas: banda de oro grabada
                elif d > 2.8:
                    ch = 'b' if light > 0 else 'n'
                elif d > 1.6:
                    ch = 'M' if light > 0 else 'a'            # Plata: cielo, sol, luna y estrellas
                else:
                    ch = 'g'
                c[y][x] = ch
    for x, y in [(7, 7), (8, 8)]:
        c[y][x] = 'G'
    return outline(c)


# Harpe: espada de adamante con un garfio curvo de hoz antes de la punta, empuñadura de cuero abajo a la izquierda.
HARPE = grid([
    "............mM..",
    "...........mMa..",
    "..........mMa...",
    ".........mMa....",
    "........mMammm..",
    ".......mMa...Mm.",
    "......mMa.....m.",
    ".....mMa.....a..",
    "....gMa.........",
    "...gGd..........",
    "..gupg..........",
    "...up...........",
    "..up............",
    ".up.............",
    "gd..............",
    "................",
])


# Brida de oro de Atenea: cabezada de cuero con hebillas, frontalera y el bocado de oro.
BRIDLE = grid([
    "................",
    "......gGGg......",
    ".....g....g.....",
    "....p......p....",
    "...Pp......pP...",
    "...p...gg...p...",
    "..Pp..gGGg..pP..",
    "..p....gg....p..",
    "..p..........p..",
    "..pP........Pp..",
    "...p........p...",
    "..gGg......gGg..",
    "..g.gGGGGGGg.g..",
    "..gGg......gGg..",
    "................",
    "................",
])

# Manzana de la Discordia: manzana de oro con hoja verde e inscripción (una raya oscura: «para la más bella»).
APPLE = grid([
    "................",
    ".......u..vV....",
    ".......u.vVv....",
    "......uuvv......",
    "....gGGgGGgg....",
    "...gGGGGGgggd...",
    "..gGGGGgggggdd..",
    "..gGGgggggggdd..",
    "..gGgggggggddd..",
    "..ggDDDDDDDgdd..",
    "..gggggggggddd..",
    "...gggggggddd...",
    "...dgggggdddd...",
    "....dddgdddd....",
    "......dd.d......",
    "................",
])


def nail():
    """Clavo de bronce de Talos: clavo grueso en diagonal, con la cabeza ancha arriba a la derecha y una gota de ícor
    dorado en la punta (la sangre de los dioses que le corría por la única vena)."""
    c = blank()
    for i in range(5):
        c[1 + i][10 + i] = 'B' if i < 2 else 'b'
        if i < 4:
            c[0 + i][11 + i] = 'B'
    line(c, 11, 5, 3, 13, 'b')
    line(c, 12, 5, 4, 13, 'n', only_empty=True)
    c[14][2], c[13][2] = 'n', 'b'
    c = outline(c)
    for x, y, ch in [(0, 15, 'Y'), (1, 15, 'g'), (0, 14, 'G')]:
        c[y][x] = ch
    return c


ICONS = {"zeus_thunderbolt": keraunos, "nemean_lion_skin": nemea, "achilles_shield": shield,
         "harpe": lambda: outline(HARPE), "golden_bridle": lambda: outline(BRIDLE), "apple_of_discord": lambda: outline(APPLE),
         "talos_nail": nail}


# Colores de la piel del león (se agregan a la paleta del borrador).
for _name, _hex in (("hide", "#c8913f"), ("hide_l", "#e3b469"), ("mane", "#7a4a1c"), ("mane_d", "#4e2c0e")):
    CD.COLORS[_name] = _hex
    CD.NAMES.append(_name)
CD.SHADE["hide"] = ("hide_l", "mane")


def nemea_worn():
    """Cuerpo (espacio 0..16 centrado en el torso: torso en x 4..12, y 2..14, z 6..10 con el frente en z 10; la cabeza ocupa y 14..22 y z 4..12). Como Heracles
    en la cerámica griega: la cabeza del león de capucha, con las fauces sobre la frente, la melena por los hombros, la
    piel cayendo por la espalda y las patas delanteras anudadas en el pecho."""
    e = [box([3.4, 2.5, 5.0], [12.6, 14.2, 5.6], "hide", "piel en la espalda"),
         box([2.8, 11.6, 4.4], [13.2, 14.6, 6.0], "mane", "melena en los hombros"),
         box([3.6, 10.6, 10.0], [7.2, 11.6, 10.6], "hide", "pata der."),
         box([8.8, 10.6, 10.0], [12.4, 11.6, 10.6], "hide", "pata izq."),
         box([7.0, 10.2, 10.4], [9.0, 12.0, 11.0], "mane", "nudo"),
         box([4.4, 1.0, 4.6], [6.0, 3.0, 5.2], "hide", "pata trasera der."),
         box([10.0, 1.0, 4.6], [11.6, 3.0, 5.2], "hide", "pata trasera izq.")]
    # Capucha: cráneo del león sobre la cabeza, melena alrededor y fauces sobre la frente.
    e += [box([3.5, 21.8, 3.5], [12.5, 23.0, 12.5], "hide", "cráneo"),
          box([3.0, 17.0, 3.0], [3.6, 22.6, 12.0], "mane", "melena der."),
          box([12.4, 17.0, 3.0], [13.0, 22.6, 12.0], "mane", "melena izq."),
          box([3.0, 15.0, 3.0], [13.0, 22.6, 3.6], "mane", "melena atrás"),
          box([4.6, 20.6, 12.0], [11.4, 22.4, 13.6], "hide", "hocico"),
          box([5.2, 20.0, 12.6], [10.8, 20.6, 13.6], "white", "colmillos"),
          box([4.4, 22.8, 6.0], [5.8, 23.8, 7.4], "mane", "oreja der."),
          box([10.2, 22.8, 6.0], [11.6, 23.8, 7.4], "mane", "oreja izq."),
          box([5.4, 22.4, 11.6], [6.6, 22.8, 12.4], "black", "ojo der."),
          box([9.4, 22.4, 11.6], [10.6, 22.8, 12.4], "black", "ojo izq.")]
    return e


WORN = {"nemean_lion_skin": ("body", nemea_worn)}

if __name__ == "__main__":
    out = {"icons": {}, "worn": {}, "palette": CD.palette_b64()}
    sheet = []
    for name, make in ICONS.items():
        chars = make()
        img = [[PAL[ch] for ch in row] for row in chars]
        sheet.append(img)
        strip, meta = glint(img, lambda x, y: chars[y][x] in SHINY)
        out["icons"][name] = {"png": png_b64(strip), "frames": meta["animation"]["frames"] if meta else None}
    for name, (part, make) in WORN.items():
        out["worn"][name] = {"part": part, "elements": make()}
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8"))
    if len(sys.argv) > 2:
        from mclib import H, png
        S, bg = 12, H('#8b8b8b')
        prev = [[bg] * (len(sheet) * 17 * S) for _ in range(16 * S)]
        for i, img in enumerate(sheet):
            for y in range(16 * S):
                for x in range(16 * S):
                    p = img[y // S][x // S]
                    if p[3]:
                        prev[y][i * 17 * S + x] = p
        png(sys.argv[2], prev)
    print("ok")
