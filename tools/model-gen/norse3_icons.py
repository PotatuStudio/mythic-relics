"""Íconos 16x16 de la tanda 3 nórdica (aprobados el 2026-11-14) y sus modelos de ítem. Los dibujos son los de la
página de diseño (tools/design-page/norse3_draft.py). Las espadas y el dardo se sostienen como herramientas. La espada de
Freyr (envainada al costado) y el manto de halcón tienen su modelo puesto en worn_models.py.
Uso: python norse3_icons.py
"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "..", "design-page"))
import norse3_draft as CD  # noqa: E402
from greece_icons import save  # noqa: E402
from moddata import ASSETS  # noqa: E402

NS = "mythicrelics"
HANDHELD = {"gram", "tyrfing", "dainsleif", "mistletoe_dart"}
WORN = {"freyr_sword", "falcon_cloak"}


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1)
        f.write("\n")


if __name__ == "__main__":
    textures = os.path.join(ASSETS, "textures", "item")
    for name, make in CD.ICONS.items():
        save(textures, name, make())
        parent = "minecraft:item/handheld" if name in HANDHELD else "minecraft:item/generated"
        write(os.path.join(ASSETS, "models", "item", name + ".json"), {"parent": parent, "textures": {"layer0": f"{NS}:item/{name}"}})
        if name in WORN:
            continue
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": {"type": "minecraft:model", "model": f"{NS}:item/{name}"}})
    print("ok:", ", ".join(CD.ICONS))
