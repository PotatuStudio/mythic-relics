"""Repinta los modelos 3D hechos con una paleta de colores planos (escudos, Mjölnir y Gungnir en la mano, Ruyi estirado,
Paladio) con textura propia: luz, sombras, ruido y patrón del material (worn_texture.retexture).

Cada generador lo llama al final con los modelos que acaba de escribir (así nunca vuelven a quedar planos); también se
puede correr solo: python paint_models.py
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import worn_texture  # noqa: E402
from moddata import ASSETS  # noqa: E402

NS = "mythicrelics"
TEX = os.path.join(ASSETS, "textures")
# modelo (relativo a models/) -> (paleta, textura nueva)
MODELS = {
    "item/achilles_shield_3d": ("item/greece3_palette", "item/painted/achilles_shield_3d"),
    "item/ancile_shield": ("item/rome_palette", "item/painted/ancile_shield"),
    "block/palladium": ("block/palladium", "block/painted/palladium"),
    "item/mjolnir_3d": ("item/norse_palette", "item/painted/mjolnir_3d"),
    "item/gungnir_3d": ("item/norse_palette", "item/painted/gungnir_3d"),
    "item/ruyi_jingu_bang_extended": ("item/norse_palette", "item/painted/ruyi_jingu_bang_extended"),
    "item/ruyi_jingu_bang_short": ("item/norse_palette", "item/painted/ruyi_jingu_bang_short"),
}


def run(names=None):
    for name, (palette, texture) in MODELS.items():
        if names is not None and name not in names:
            continue
        path = os.path.join(ASSETS, "models", *name.split("/")) + ".json"
        worn_texture.retexture_file(path, os.path.join(TEX, *palette.split("/")) + ".png", f"{NS}:{texture}", ASSETS)
        print("pintado:", name)


if __name__ == "__main__":
    run()
