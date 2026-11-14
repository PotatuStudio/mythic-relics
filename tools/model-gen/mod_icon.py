"""Ícono del mod y emblema de la pestaña creativa: la reliquia en su altar (elegida el 2026-11-14; dibujo en
emblem_relic.py). Escribe icon.png (visor) y mythicrelics_icon.png de Fabric y NeoForge (128x128), y el emblema de la
pestaña textures/item/emblem.png (16x16, sin animación).
Uso: python mod_icon.py   (requiere Pillow)
"""
import os
import sys

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import emblem_relic  # noqa: E402

ROOT = os.path.join(HERE, "..", "..")
ASSETS = os.path.join(ROOT, "common", "src", "main", "resources", "assets", "mythicrelics")
OUTPUTS = [os.path.join(ASSETS, "icon.png"),
           os.path.join(ROOT, "fabric", "src", "main", "resources", "mythicrelics_icon.png"),
           os.path.join(ROOT, "neoforge", "src", "main", "resources", "mythicrelics_icon.png")]

if __name__ == "__main__":
    icon = emblem_relic.relic_big().resize((128, 128), Image.NEAREST)
    for out in OUTPUTS:
        icon.save(out)
    emblem = os.path.join(ASSETS, "textures", "item", "emblem.png")
    emblem_relic.relic_tab().save(emblem)
    if os.path.exists(emblem + ".mcmeta"):
        os.remove(emblem + ".mcmeta")
    print("ok")
