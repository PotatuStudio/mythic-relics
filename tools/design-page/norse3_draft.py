"""Borrador de la tanda 3 nórdica: íconos 16x16 y modelos puestos (la espada de Freyr envainada al costado, el manto de
plumas de halcón en la espalda). No toca el mod. Uso: python norse3_draft.py <salida.json> [<vista.png>]

Todas las armas van en diagonal (empuñadura abajo a la izquierda, punta arriba a la derecha) y dejan un píxel libre en
cada borde para que el contorno no se corte."""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import china_draft as CD  # noqa: E402
from china_draft import PAL, SHINY, blank, box, glint, outline, png_b64  # noqa: E402,F401

# Colores de los modelos puestos (se agregan a la paleta del borrador).
for _name, _hex in (("feather_b", "#7a4f2c"), ("feather_l", "#c4955e"), ("feather_w", "#efe6d6"), ("leather", "#5e3a1c"),
                    ("steel", "#d6dbe2"), ("steel_d", "#8d949e")):
    CD.COLORS[_name] = _hex
    CD.NAMES.append(_name)


def sword(blade_l, blade_d, guard, grip, pommel, length=8):
    """Espada en diagonal: hoja de dos tonos desde la guarda hasta la punta, guarda en cruz, puño y pomo."""
    c = blank()
    for i in range(length + 1):
        x, y = 5 + i, 10 - i
        c[y][x] = blade_l
        if i < length:
            c[y][x + 1] = blade_d
    for x, y in [(3, 8), (4, 9), (5, 10), (6, 11), (7, 12)]:      # guarda
        c[y][x] = guard
    c[11][4], c[12][3] = grip, grip                             # puño
    c[13][2], c[13][1], c[14][2], c[12][2] = pommel, pommel, pommel, pommel   # pomo lobulado
    return c


def gram():
    """Gram: espada vikinga de acero, la que partió el yunque de Regin: hoja con acanaladura, guarda y pomo de oro
    lobulado (tipo Petersen) y puño de cuero."""
    c = sword('M', 'a', 'g', 'u', 'G')
    for i in range(1, 7):                                       # acanaladura
        c[10 - i][5 + i] = 'm'
    return outline(c)


def tyrfing():
    """Tyrfing: la espada maldita que brilla como el fuego: hoja incandescente (blanca al centro, borde de fuego) y
    empuñadura de oro."""
    c = sword('G', 'C', 'g', 'u', 'G')
    c[3][12] = c[5][10] = c[7][8] = 'W'                         # brasas blancas en el filo
    c = outline(c)
    for x, y in [(14, 4), (11, 1), (9, 3)]:                     # chispas
        if c[y][x] == '.':
            c[y][x] = 'Y'
    return c


def dainsleif():
    """Dáinsleif: hoja de hierro oscuro con la sangre que nunca se seca goteando de la punta; empuñadura negra."""
    c = sword('I', 'o', 'O', 'x', 'i')
    for x, y in [(13, 2), (12, 3), (11, 4)]:                    # sangre en la punta
        c[y][x] = 'r'
    c = outline(c)
    for x, y in [(14, 5), (14, 6)]:                             # gota
        if c[y][x] == '.':
            c[y][x] = 'x'
    return c


def freyr_sword():
    """Espada de Freyr: acero claro bañado de luz dorada (Freyr es el señor del sol de verano), empuñadura de oro y
    destellos alrededor: pelea sola."""
    c = sword('M', 'G', 'g', 'd', 'G')
    c = outline(c)
    for x, y in [(1, 4), (2, 3), (3, 4), (2, 5), (12, 13), (13, 12), (14, 13), (13, 14)]:   # destellos en cruz
        if c[y][x] == '.':
            c[y][x] = '8'
    return c


def falcon_cloak():
    """Manto de plumas de halcón visto de espaldas: se abre hacia abajo como unas alas plegadas, con las barras en V del
    plumaje del halcón, los bordes más oscuros, las puntas de las plumas abajo, cuello claro y broche de oro."""
    c = blank()
    for y in range(2, 15):
        half = 2.5 + (y - 2) * 0.42
        for x in range(1, 15):
            d = abs(x + 0.5 - 8)
            if d > half:
                continue
            if y <= 3:
                c[y][x] = 'w'                                    # cuello claro
            elif d > half - 1:
                c[y][x] = 'u'                                    # borde oscuro
            elif int(y - d * 0.7) % 3 == 0:
                c[y][x] = 'P'                                    # barras en V
            else:
                c[y][x] = 'p'
    for x in range(1, 15):                                       # puntas de las plumas
        if c[14][x] != '.' and x % 2 == 0:
            c[14][x] = '.'
    c[3][7], c[3][8] = 'g', 'G'                                  # broche
    return outline(c)


def mistletoe_dart():
    """Dardo de muérdago: una ramita verde afilada en diagonal, con hojitas y bayas blancas, la punta arriba a la
    derecha."""
    c = blank()
    for i in range(10):
        x, y = 3 + i, 12 - i
        c[y][x] = 'v' if i % 3 else 'V'
    c[2][13], c[1][13] = 'y', 'y'                                # punta afilada
    for x, y, ch in [(6, 8, 'V'), (7, 8, 'v'), (5, 7, 'V'), (9, 6, 'V'), (10, 6, 'v'), (10, 7, 'V'),
                     (4, 10, 'V'), (3, 10, 'v')]:                # hojas en pares
        c[y][x] = ch
    for x, y in [(8, 9), (6, 11), (11, 8)]:                      # bayas blancas
        c[y][x] = 'W'
    c[13][2], c[14][1] = 'p', 'p'                                # el extremo cortado
    return outline(c)


ICONS = {"gram": gram, "tyrfing": tyrfing, "dainsleif": dainsleif, "freyr_sword": freyr_sword,
         "falcon_cloak": falcon_cloak, "mistletoe_dart": mistletoe_dart}


def freyr_worn():
    """Cuerpo (espacio 0..16 centrado en el torso: torso en x 4..12, y 2..14, z 6..10 con el frente en z 10; el brazo
    izquierdo ocupa x 12..16). Cinturón de cuero y la espada envainada al frente de la cadera izquierda, por fuera del
    brazo, con la guarda y el pomo de oro arriba y la vaina bajando por delante de la pierna."""
    return [box([3.6, 2.2, 5.6], [12.4, 3.2, 6.2], "leather", "cinturón atrás"),
            box([3.6, 2.2, 9.8], [12.4, 3.2, 10.4], "leather", "cinturón adelante"),
            box([10.4, -6.0, 10.4], [11.6, 3.0, 11.4], "leather", "vaina"),
            box([10.3, -6.6, 10.3], [11.7, -6.0, 11.5], "gold", "contera"),
            box([9.8, 3.0, 10.2], [12.2, 3.6, 11.6], "gold", "guarda"),
            box([10.6, 3.6, 10.6], [11.4, 5.6, 11.2], "leather", "puño"),
            box([10.4, 5.6, 10.4], [11.6, 6.4, 11.4], "gold", "pomo")]


def falcon_worn():
    """Cuerpo: el manto de plumas cae por la espalda y se abre abajo, con barras claras del plumaje y un borde de puntas
    de plumas sueltas; una esclavina de plumas cubre los hombros, con el broche de oro al frente."""
    e = [box([3.2, 3.0, 5.0], [12.8, 13.6, 5.6], "feather_b", "manto arriba"),
         box([2.6, 0.4, 4.6], [13.4, 3.0, 5.4], "feather_b", "manto abierto abajo")]
    for i, y in enumerate((4.2, 6.6, 9.0, 11.4)):              # barras claras del plumaje
        e.append(box([3.1, y, 4.9], [12.9, y + 0.6, 5.0], "feather_l", f"barra {i}"))
    e.append(box([2.5, 1.4, 4.5], [13.5, 2.0, 4.6], "feather_l", "barra abajo"))
    x = 2.6
    while x < 13.2:                                              # puntas de las plumas
        e.append(box([x, -1.0, 4.7], [x + 0.9, 0.4, 5.3], "feather_b", "punta"))
        x += 1.6
    e += [box([2.8, 11.4, 4.6], [13.2, 14.4, 10.4], "feather_b", "esclavina"),
          box([2.7, 13.6, 4.5], [13.3, 14.6, 10.5], "feather_w", "cuello"),
          box([7.0, 12.4, 10.4], [9.0, 13.4, 10.8], "gold", "broche")]
    return e


WORN = {"freyr_sword": ("body", freyr_worn), "falcon_cloak": ("body", falcon_worn)}

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
