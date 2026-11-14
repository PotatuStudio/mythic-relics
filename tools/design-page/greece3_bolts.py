"""Tres propuestas desde cero para el ícono del rayo de Zeus (16x16, con un píxel de margen para el contorno).
Uso: python greece3_bolts.py <vista.png>"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from china_draft import PAL, blank, outline  # noqa: E402
from greece3_draft import inside_poly  # noqa: E402

# El zigzag clásico (⚡) dibujado por filas: (fila, primera columna, última columna). Tramo de arriba de 4 px que
# baja inclinado, el quiebre ancho hacia la derecha y el tramo de abajo que se afina hasta la punta. Todo dentro de
# las filas y columnas 1..14, para que el contorno no se corte.
SPANS = [(1, 9, 12), (2, 8, 11), (3, 7, 10), (4, 6, 9), (5, 5, 12), (6, 9, 11), (7, 8, 10), (8, 7, 9), (9, 6, 8),
         (10, 5, 7), (11, 4, 6), (12, 4, 5), (13, 3, 4), (14, 3, 3)]


def fill(core_ch, edge_ch, mid_ch):
    """Borde izquierdo claro, núcleo incandescente y borde derecho más oscuro (luz desde arriba a la izquierda)."""
    c = blank()
    for y, x0, x1 in SPANS:
        for x in range(x0, x1 + 1):
            tip = y in (1, 13, 14)
            c[y][x] = mid_ch if x == x0 else edge_ch if x == x1 or tip else core_ch
    return c


SPARKS = [(2, 4), (1, 5), (13, 11), (14, 10), (7, 1)]


def gold():
    """A · Oro: el ⚡ clásico, núcleo blanco y cuerpo dorado, como el rayo de las monedas y estatuas de Zeus."""
    return outline(fill('W', 'g', 'G'))


def electric():
    """B · Eléctrico: el mismo ⚡ blanco azulado con contorno celeste y chispas, más «rayo de tormenta»."""
    c = outline(fill('W', 'L', 'w'))
    for x, y in SPARKS:
        if c[y][x] == '.':
            c[y][x] = '7'
    return c


def gripped():
    """C · Empuñado: el ⚡ dorado con una banda de cuero y oro en el tramo del quiebre, para que se lea como un arma
    que Zeus sostiene."""
    c = fill('W', 'g', 'G')
    for y in (8, 9):
        for x in range(16):
            if c[y][x] != '.':
                c[y][x] = 'u' if (x + y) % 2 else 'p'
    for y in (7, 10):
        for x in range(16):
            if c[y][x] != '.':
                c[y][x] = 'd'
    c = outline(c)
    for x, y in SPARKS:                                     # los rayitos eléctricos de la opción B
        if c[y][x] == '.':
            c[y][x] = '7'
    return c


OPTIONS = [("A", "Oro", gold), ("B", "Eléctrico", electric), ("C", "Empuñado", gripped)]

if __name__ == "__main__":
    from mclib import H, png
    S, bg = 14, H('#8b8b8b')
    imgs = [[[PAL[ch] for ch in row] for row in make()] for _, _, make in OPTIONS]
    prev = [[bg] * (len(imgs) * 18 * S) for _ in range(16 * S)]
    for i, img in enumerate(imgs):
        for y in range(16 * S):
            for x in range(16 * S):
                p = img[y // S][x // S]
                if p[3]:
                    prev[y][i * 18 * S + x] = p
    png(sys.argv[1], prev)
    for k, _, make in OPTIONS:
        print(k)
        print("\n".join("".join(r) for r in make()))
