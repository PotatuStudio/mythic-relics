"""Ícono del mod (lista de mods de los dos loaders y visor): el escudo del emblema de la pestaña creativa, en grande y
con una cultura por cuartel. En cada cuartel va el ícono de la pestaña de esa cultura (el mismo PNG del ítem, primer
cuadro de la animación) sobre su color: anj sobre lapislázuli, cabeza de Medusa sobre mármol, Ancile sobre rojo,
Mjölnir sobre hierro, Ruyi Jingu Bang sobre jade y Kusanagi sobre añil, con borde y cruz de oro como el emblema.

Se dibuja a 64x64 y se escala x2 (128x128), así cada píxel del ícono del ítem queda nítido.
Uso: python mod_icon.py   (escribe icon.png y mythicrelics_icon.png de Fabric y NeoForge; requiere Pillow)
"""
import math
import os

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.join(HERE, "..", "..")
ITEMS = os.path.join(ROOT, "common", "src", "main", "resources", "assets", "mythicrelics", "textures", "item")
OUTPUTS = [os.path.join(ROOT, "common", "src", "main", "resources", "assets", "mythicrelics", "icon.png"),
           os.path.join(ROOT, "fabric", "src", "main", "resources", "mythicrelics_icon.png"),
           os.path.join(ROOT, "neoforge", "src", "main", "resources", "mythicrelics_icon.png")]


def hexc(h):
    return tuple(int(h[i:i + 2], 16) for i in (1, 3, 5)) + (255,)


# (ícono de la pestaña, campo claro, campo oscuro) por cuartel: fila por fila, izquierda y derecha.
CELLS = [("ankh", "#2c4f9e", "#1f3a7a"), ("medusa_head", "#e4ddcc", "#cfc6b0"),
         ("ancile", "#a3282a", "#7f1c1f"), ("mjolnir", "#6b727c", "#555b64"),
         ("ruyi_jingu_bang", "#2f7a57", "#235f43"), ("kusanagi", "#3a3470", "#2b2656")]
GOLD, GOLD_D, GOLD_L, INK = hexc("#e8b830"), hexc("#a8781a"), hexc("#ffe27a"), hexc("#1a1208")

S = 64
X0, X1 = 5, 58            # bordes del escudo
TOP, TAPER, TIP = 2, 42, 62   # arriba, donde empieza a cerrarse y la punta
ROWS = [(TOP, 22), (22, 42), (42, 62)]   # límites de cada fila (la línea de oro va en el límite)
MID = (X0 + X1) // 2 + 1


def half_width(y):
    """Media anchura del escudo en la fila y: recto hasta TAPER y luego un arco hasta la punta."""
    full = (X1 - X0) / 2
    if y < TAPER:
        return full
    t = (y - TAPER) / (TIP - TAPER)
    return max(0.0, full * math.sqrt(max(0.0, 1 - t * t)))


def inside(x, y):
    if y < TOP or y > TIP:
        return False
    cx = (X0 + X1 + 1) / 2
    return abs(x + 0.5 - cx) <= half_width(y)


def item_frame(name):
    img = Image.open(os.path.join(ITEMS, name + ".png")).convert("RGBA")
    return img.crop((0, 0, 16, 16))


def build():
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    px = img.load()
    # Campo de cada cuartel con un ajedrezado suave (como el lapislázuli y el mármol del emblema).
    for y in range(S):
        for x in range(S):
            if not inside(x, y):
                continue
            row = next((i for i, (a, b) in enumerate(ROWS) if a <= y < b), 2)
            col = 0 if x < MID else 1
            _, light, dark = CELLS[row * 2 + col]
            px[x, y] = hexc(light) if ((x // 2) + (y // 2)) % 3 else hexc(dark)
    # Borde y cruz de oro (dos píxeles, con brillo arriba a la izquierda y sombra abajo a la derecha).
    for y in range(S):
        for x in range(S):
            if not inside(x, y):
                continue
            edge = not (inside(x - 2, y) and inside(x + 2, y) and inside(x, y - 2) and inside(x, y + 2))
            divider = x in (MID - 1, MID) or any(y in (b - 1, b) for _, b in ROWS[:2])
            if edge or divider:
                light_side = (not inside(x - 2, y) or not inside(x, y - 2)) if edge else x == MID - 1 or y in (ROWS[0][1] - 1, ROWS[1][1] - 1)
                px[x, y] = GOLD_L if light_side and (x + y) % 2 else GOLD if (x + y) % 2 else GOLD_D
    # Contorno oscuro de un píxel por fuera.
    for y in range(S):
        for x in range(S):
            if not inside(x, y) and any(inside(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                px[x, y] = INK
    # Íconos de las pestañas, centrados en cada cuartel (sin salirse del escudo).
    for i, (name, _, _) in enumerate(CELLS):
        row, col = divmod(i, 2)
        a, b = ROWS[row]
        cx0, cx1 = (X0 + 2, MID - 1) if col == 0 else (MID + 1, X1 - 1)
        ox = (cx0 + cx1) // 2 - 8 + (1 if col == 0 and row == 2 else -1 if row == 2 else 0)
        oy = (a + b) // 2 - 8 + (1 if row == 0 else -3 if row == 2 else 0)
        icon = item_frame(name)
        ipx = icon.load()
        for y in range(16):
            for x in range(16):
                p = ipx[x, y]
                tx, ty = ox + x, oy + y
                # solo dentro del cuartel: sin pisar el borde ni la cruz de oro
                in_cell = cx0 <= tx <= cx1 - 1 and a + 1 <= ty <= b - 2 and inside(tx - 2, ty) and inside(tx + 2, ty)                     and inside(tx, ty + 2)
                if p[3] and in_cell:
                    px[tx, ty] = p
    return img.resize((S * 2, S * 2), Image.NEAREST)


if __name__ == "__main__":
    icon = build()
    for out in OUTPUTS:
        icon.save(out)
        print(out)
