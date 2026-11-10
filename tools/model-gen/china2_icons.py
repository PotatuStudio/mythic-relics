"""Íconos 16x16 de la tanda 2 china (aprobados el 2026-11-10: los del primer borrador) y sus modelos de ítem.

Los dibujos son los de la página de diseño (tools/design-page/china2_draft.py). La rama del melocotonero tiene dos
dibujos: con el melocotón maduro y sin él (mientras crece); la definición del ítem elige con el componente
mythicrelics:peach_growing. Las reliquias que se llevan puestas tienen su definición en worn_models.py.
Uso: python china2_icons.py
"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "..", "design-page"))
import china2_draft as CD  # noqa: E402
from greece_icons import outline, save  # noqa: E402
from moddata import ASSETS  # noqa: E402

NS = "mythicrelics"
HANDHELD = {"fire_tipped_spear", "erlang_halberd"}
WORN = {"erlang_eye", "pixiu_bracelet"}
PEACH = "immortal_peach_branch"


def empty_branch():
    """La rama sin fruto: se quita el melocotón (piel rosada y su sombra) y quedan la rama y las hojas."""
    rows = [[ch if ch not in "KCc1" else "." for ch in row] for row in CD.PEACH]
    return outline(rows)


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=1, ensure_ascii=False)
        f.write("\n")


if __name__ == "__main__":
    textures = os.path.join(ASSETS, "textures", "item")
    for name, make in CD.ICONS.items():
        save(textures, name, make())
        parent = "minecraft:item/handheld" if name in HANDHELD else "minecraft:item/generated"
        write(os.path.join(ASSETS, "models", "item", name + ".json"), {"parent": parent, "textures": {"layer0": f"{NS}:item/{name}"}})
        if name in WORN:
            continue
        model = {"type": "minecraft:model", "model": f"{NS}:item/{name}"}
        if name == PEACH:
            model = {"type": "minecraft:condition", "property": "minecraft:has_component", "component": f"{NS}:peach_growing",
                     "on_true": {"type": "minecraft:model", "model": f"{NS}:item/{name}_empty"}, "on_false": model}
        write(os.path.join(ASSETS, "items", name + ".json"), {"model": model})
    save(textures, PEACH + "_empty", empty_branch())
    write(os.path.join(ASSETS, "models", "item", PEACH + "_empty.json"),
          {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{PEACH}_empty"}})
    print("ok:", ", ".join(CD.ICONS))
