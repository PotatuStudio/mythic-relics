"""Íconos 16x16 de la tanda 1 japonesa (aprobados el 2026-11-10) y sus modelos de ítem. Los dibujos son los de la
página de diseño (tools/design-page/japan_draft.py). La caña de Urashima se sostiene como la caña de pescar vanilla.
Las reliquias que se llevan puestas tienen su definición en worn_models.py. Uso: python japan_icons.py
"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "..", "design-page"))
import japan_draft as CD  # noqa: E402
from greece_icons import save  # noqa: E402
from moddata import ASSETS  # noqa: E402

NS = "mythicrelics"
HANDHELD = {"kusanagi", "oni_kanabo", "lucky_mallet"}
ROD = {"urashima_rod"}
WORN = {"yasakani_magatama", "raijin_drum"}
def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


if __name__ == "__main__":
    textures = os.path.join(ASSETS, "textures", "item")
    for name, make in CD.ICONS.items():
        save(textures, name, make())
        parent = "minecraft:item/handheld_rod" if name in ROD else "minecraft:item/handheld" if name in HANDHELD else "minecraft:item/generated"
        write(os.path.join(ASSETS, "models", "item", name + ".json"), {"parent": parent, "textures": {"layer0": f"{NS}:item/{name}"}})
        if name in WORN:
            continue
        model = {"type": "minecraft:model", "model": f"{NS}:item/{name}"}
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": model})
    print("ok:", ", ".join(CD.ICONS))
