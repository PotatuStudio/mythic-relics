"""Modelo 3D del Ruyi Jingu Bang estirado (clic derecho): un báculo de hierro negro mucho más largo, con aros de oro en las
puntas y el centro. Se usa mientras dura el estirón (marca 0 de custom_model_data) y fuera del inventario; el resto del
tiempo, en la mano, va el mismo báculo en chico (ruyi_jingu_bang_short); en el inventario sigue el ícono. Usa la paleta nórdica (norse_palette.png, de norse_models.py).
Los dos van en la diagonal de la espada vanilla con sus mismas posiciones de mano (handheld3d.py).
Uso: python china_models.py"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402
from handheld3d import GRIP_Y, diagonal, handheld_display  # noqa: E402
from norse_models import box, write  # noqa: E402

NS = "mythicrelics"


def ruyi(bottom, top):
    """El báculo entre las alturas bottom y top: aros de oro en las puntas, filetes y aro del centro. El chico y el
    estirado son el mismo dibujo con distinto largo, para que al crecer y encogerse se vea el mismo objeto."""
    mid = (bottom + top) / 2
    e = [box([7.4, bottom, 7.4], [8.6, top, 8.6], "dark", "báculo")]
    for y0 in (bottom, top - 2):
        e.append(box([7.1, y0, 7.1], [8.9, y0 + 2, 8.9], "gold", "aro de la punta"))
    for y0 in (bottom + 2.5, top - 4.5):
        e.append(box([7.25, y0, 7.25], [8.75, y0 + 0.6, 8.75], "gold_d", "filete"))
    e.append(box([7.2, mid - 0.8, 7.2], [8.8, mid + 0.8, 8.8], "gold", "aro del centro"))
    return e


def ruyi_extended():
    """Estirado: 56 px, con el agarre donde va el mango de una espada y casi todo el báculo hacia adelante. Se dibuja a la
    mitad y el display lo dobla (no cabe en -16..32)."""
    return diagonal(ruyi(GRIP_Y - 8, GRIP_Y + 48), half=True)


def ruyi_short():
    """Chico: 28 px, agarrado a 5 px de la punta de abajo, como un bastón."""
    return diagonal(ruyi(GRIP_Y - 5, GRIP_Y + 23))


# Las dos versiones se sostienen como una espada vanilla (handheld3d.py).
DISPLAY = handheld_display(2)
SHORT_DISPLAY = handheld_display()

if __name__ == "__main__":
    write(os.path.join(ASSETS, "models", "item", "ruyi_jingu_bang_extended.json"), {
        "gui_light": "front", "textures": {"p": f"{NS}:item/norse_palette", "particle": f"{NS}:item/norse_palette"},
        "elements": ruyi_extended(), "display": DISPLAY})
    write(os.path.join(ASSETS, "models", "item", "ruyi_jingu_bang_short.json"), {
        "gui_light": "front", "textures": {"p": f"{NS}:item/norse_palette", "particle": f"{NS}:item/norse_palette"},
        "elements": ruyi_short(), "display": SHORT_DISPLAY})
    # En el inventario, el ícono; en la mano, siempre el báculo 3D, chico o estirado.
    icon = {"type": "minecraft:model", "model": f"{NS}:item/ruyi_jingu_bang"}
    write(os.path.join(ASSETS, "items", "ruyi_jingu_bang.json"), {"model": {
        "type": "minecraft:select", "property": "minecraft:display_context",
        "cases": [{"when": ["gui", "ground", "fixed", "on_shelf"], "model": icon}],
        "fallback": {"type": "minecraft:condition", "property": "minecraft:custom_model_data", "index": 0,
                     "on_false": {"type": "minecraft:model", "model": f"{NS}:item/ruyi_jingu_bang_short"},
                     "on_true": {"type": "minecraft:model", "model": f"{NS}:item/ruyi_jingu_bang_extended"}}}})
    print("ok: ruyi_jingu_bang_extended")
    import paint_models  # noqa: E402  (textura pintada en lugar de la paleta plana)
    paint_models.run(['item/ruyi_jingu_bang_extended', 'item/ruyi_jingu_bang_short'])
