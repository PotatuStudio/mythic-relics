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


# Rayo de Zeus: el rayo en zigzag de las monedas griegas, con empuñadura de oro al centro y un rayo en cada punta.
KERAUNOS = grid([
    ".............G..",
    "............GW..",
    "...........GWG..",
    "..........GWG...",
    ".........GWWGGG.",
    "..........GGWG..",
    ".........GWG....",
    "........gGd.....",
    ".......gGd......",
    "......gGd.......",
    "....GGWG........",
    "...GWWGGG.......",
    "....GWG.........",
    "...GWG..........",
    "..GW............",
    "..G.............",
])


# Piel del león de Nemea: la piel extendida, con la cabeza del león arriba (melena oscura, ojos) y las patas a los lados.
NEMEA = grid([
    "................",
    ".....nnnnnn.....",
    "....nnBBBBnn....",
    "...nnBkBBkBnn...",
    "...nnBBbbBBnn...",
    "....nBbkkbBn....",
    "..b..nBBBBn..b..",
    ".bBb.bBBBBBb.bBb",
    ".bBBbBBBBBBBbBBb",
    "..bBBBBbbBBBBBb.",
    "...bBBBBBBBBBb..",
    "...bBBBbbBBBBb..",
    "..bBBBBBBBBBBBb.",
    ".bBbbBBBBBBbbBb.",
    ".bb..bBBBBb..bb.",
    "......bbbb......",
])

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


ICONS = {"zeus_thunderbolt": lambda: outline(KERAUNOS), "nemean_lion_skin": lambda: outline(NEMEA), "achilles_shield": shield,
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
