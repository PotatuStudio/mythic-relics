"""Íconos 16x16 de la tanda 1 china (aprobados el 2026-11-10) con su brillo animado, y los modelos de ítem.

Los dibujos son los de la página de diseño (tools/design-page/china_draft.py); aquí se guardan en el mod:
  - textures/item/<reliquia>.png (+ .png.mcmeta con el destello) y los tres cuadros del arco tensado;
  - models/item/<reliquia>.json (handheld para el bastón y el abanico; el arco hereda la pose del arco vanilla);
  - items/<reliquia>.json de las que no se llevan puestas (las puestas las escribe worn_models.py), con el arco que se
    tensa como el vanilla.
Uso: python china_icons.py
"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "..", "design-page"))
import china_draft as CD  # noqa: E402
from greece_icons import save  # noqa: E402
from moddata import ASSETS  # noqa: E402

NS = "mythicrelics"
HANDHELD = {"ruyi_jingu_bang", "banana_leaf_fan"}
WORN = {"wind_fire_wheels", "five_color_stone"}


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


if __name__ == "__main__":
    textures = os.path.join(ASSETS, "textures", "item")
    for name, make in CD.ICONS.items():
        save(textures, name, make())
        if name == "hou_yi_bow":
            parent = "minecraft:item/bow"
        else:
            parent = "minecraft:item/handheld" if name in HANDHELD else "minecraft:item/generated"
        write(os.path.join(ASSETS, "models", "item", name + ".json"), {"parent": parent, "textures": {"layer0": f"{NS}:item/{name}"}})
        if name in WORN:
            continue
        model = {"type": "minecraft:model", "model": f"{NS}:item/{name}"}
        if name == "hou_yi_bow":
            model = {
                "type": "minecraft:condition", "property": "minecraft:using_item", "on_false": model,
                "on_true": {
                    "type": "minecraft:range_dispatch", "property": "minecraft:use_duration", "scale": 0.05,
                    "entries": [{"model": {"type": "minecraft:model", "model": f"{NS}:item/{name}_pulling_1"}, "threshold": 0.65},
                                {"model": {"type": "minecraft:model", "model": f"{NS}:item/{name}_pulling_2"}, "threshold": 0.9}],
                    "fallback": {"type": "minecraft:model", "model": f"{NS}:item/{name}_pulling_0"},
                },
            }
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": model})
    for k in range(3):
        name = f"hou_yi_bow_pulling_{k}"
        save(textures, name, CD.bow(k + 1))
        write(os.path.join(ASSETS, "models", "item", name + ".json"),
              {"parent": "minecraft:item/bow", "textures": {"layer0": f"{NS}:item/{name}"}})
    print("ok:", ", ".join(CD.ICONS))
