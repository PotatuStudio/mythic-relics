"""Íconos 16x16 de la tanda 2 china (aprobados el 2026-11-10: los del primer borrador) y sus modelos de ítem: los
talismanes fu y el saco de xirang. Los dibujos son los de la página de diseño (tools/design-page/china2_draft.py).
Uso: python china2_icons.py
"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "..", "design-page"))
import china2_draft as CD  # noqa: E402
from greece_icons import save  # noqa: E402
from moddata import ASSETS  # noqa: E402

NS = "mythicrelics"


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


if __name__ == "__main__":
    textures = os.path.join(ASSETS, "textures", "item")
    for name, make in CD.ICONS.items():
        save(textures, name, make())
        write(os.path.join(ASSETS, "models", "item", name + ".json"),
              {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{name}"}})
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": {"type": "minecraft:model", "model": f"{NS}:item/{name}"}})
    print("ok:", ", ".join(CD.ICONS))
