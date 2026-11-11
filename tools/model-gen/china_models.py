"""Modelo 3D del Ruyi Jingu Bang estirado (clic derecho): un báculo de hierro negro mucho más largo, con aros de oro en las
puntas y el centro. Solo se usa mientras dura el estirón (marca 0 de custom_model_data) y fuera del inventario; en el
inventario sigue el ícono. Usa la paleta nórdica (norse_palette.png, de norse_models.py).
Uso: python china_models.py"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import ASSETS  # noqa: E402
from norse_models import box, write  # noqa: E402

NS = "mythicrelics"


def ruyi_extended():
    e = [box([7.4, -15, 7.4], [8.6, 31, 8.6], "dark", "báculo")]
    for y0 in (-15, 29):
        e.append(box([7.1, y0, 7.1], [8.9, y0 + 2, 8.9], "gold", "aro de la punta"))
    for y0 in (-12.5, 26.5):
        e.append(box([7.25, y0, 7.25], [8.75, y0 + 0.6, 8.75], "gold_d", "filete"))
    e.append(box([7.2, 7.2, 7.2], [8.8, 8.8, 8.8], "gold", "aro del centro"))
    return e


DISPLAY = {
    "thirdperson_righthand": {"rotation": [0, 90, 35], "translation": [0, 3, 1], "scale": [0.85, 0.85, 0.85]},
    "thirdperson_lefthand": {"rotation": [0, -90, -35], "translation": [0, 3, 1], "scale": [0.85, 0.85, 0.85]},
    "firstperson_righthand": {"rotation": [0, 90, 30], "translation": [1, 2, 0], "scale": [0.68, 0.68, 0.68]},
    "firstperson_lefthand": {"rotation": [0, -90, -30], "translation": [1, 2, 0], "scale": [0.68, 0.68, 0.68]},
}

if __name__ == "__main__":
    write(os.path.join(ASSETS, "models", "item", "ruyi_jingu_bang_extended.json"), {
        "gui_light": "front", "textures": {"p": f"{NS}:item/norse_palette", "particle": f"{NS}:item/norse_palette"},
        "elements": ruyi_extended(), "display": DISPLAY})
    icon = {"type": "minecraft:model", "model": f"{NS}:item/ruyi_jingu_bang"}
    write(os.path.join(ASSETS, "items", "ruyi_jingu_bang.json"), {"model": {
        "type": "minecraft:condition", "property": "minecraft:custom_model_data", "index": 0,
        "on_false": icon,
        "on_true": {"type": "minecraft:select", "property": "minecraft:display_context",
                    "cases": [{"when": ["gui", "ground", "fixed", "on_shelf"], "model": icon}],
                    "fallback": {"type": "minecraft:model", "model": f"{NS}:item/ruyi_jingu_bang_extended"}}}})
    print("ok: ruyi_jingu_bang_extended")
