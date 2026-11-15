"""Visor HTML (three.js) para revisar Mythic Relics sin abrir el juego.

Lee los mismos datos que usa el mod (íconos, traducciones, números de EgyptRelics.java, botín de ModLoot.java, rareza
de ModItems.java y la textura del escarabajo) y arma una sola página con:
  - la pestaña creativa con los tooltips como se ven en el juego (colores, líneas, rareza);
  - la página de JEI de "dónde encontrarlo";
  - el escarabajo y el chacal en 3D con sus modelos y su marcha, y un jugador con las reliquias puestas.

No usa texturas de Mojang. Los números de los tooltips replican EgyptRelics#tooltipArgs: si cambia esa fórmula,
actualizar TOOLTIP_ARGS aquí.

Uso: python make_viewer.py <salida.html>
"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from moddata import (ARCHAEOLOGY, ASSETS, CULTURES, FABRIC_ASSETS, ORDER, SETS, SLOTS, LOOT_TABLE_KEYS, b64, constants, icons, lang, loot,  # noqa: E402
                     rarity, read, set_args, tooltip_args)

NS = "mythicrelics"


def _held_model(node):
    """Recorre la definición del ítem como el juego para la mano en tercera persona, sin usar el ítem ni datos extra."""
    t = node.get("type", "minecraft:model")
    if t == "minecraft:model":
        return node["model"]
    if t == "minecraft:select":
        for case in node.get("cases", []):
            when = case["when"] if isinstance(case["when"], list) else [case["when"]]
            if "thirdperson_righthand" in when:
                return _held_model(case["model"])
        return _held_model(node["fallback"])
    if t == "minecraft:condition":
        return _held_model(node["on_false"])
    if t == "minecraft:range_dispatch":
        return _held_model(node.get("fallback") or node["entries"][0]["model"])
    raise ValueError(t)


def held(name):
    """{"kind": "model", "elements", "tex"} si en la mano es un modelo 3D; si no, {"kind": "icon", "diagonal"}."""
    item = json.loads(read(os.path.join(ASSETS, "items", name + ".json")))
    model_id = _held_model(item["model"])
    while True:
        path = os.path.join(ASSETS, "models", *model_id.split(":", 1)[1].split("/")) + ".json"
        if not model_id.startswith(NS + ":") or not os.path.exists(path):
            return {"kind": "icon", "diagonal": model_id.endswith("handheld")}
        m = json.loads(read(path))
        if "elements" in m:
            tex = next(v for k, v in m["textures"].items() if k != "particle")
            png_path = os.path.join(ASSETS, "textures", *tex.split(":", 1)[1].split("/")) + ".png"
            return {"kind": "model", "elements": m["elements"], "tex": b64(png_path)}
        if "parent" not in m:
            return {"kind": "icon", "diagonal": False}
        model_id = m["parent"] if ":" in m["parent"] else "minecraft:" + m["parent"]


def glow_b64(name):
    """La capa que brilla de una entidad; el bote no tiene (usa el renderer vanilla): una imagen vacía de 1x1."""
    path = os.path.join(ASSETS, "textures", "entity", name + "_glow.png")
    if os.path.exists(path):
        return b64(path)
    return "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="


if __name__ == "__main__":
    out = sys.argv[1]
    data = {
        "order": ORDER,
        "cultures": CULTURES,
        "sets": SETS,
        "setArgs": set_args(constants()),
        "archaeology": sorted(ARCHAEOLOGY),
        "slots": SLOTS,
        "args": tooltip_args(constants()),
        "loot": loot(),
        "lootKeys": LOOT_TABLE_KEYS,
        "rarity": rarity(),
        "icons": icons(),
        "lang": lang(),
        "modIcon": b64(os.path.join(ASSETS, "icon.png")),
        "scarabTexture": b64(os.path.join(ASSETS, "textures", "entity", "scarab.png")),
        "jackalTexture": b64(os.path.join(ASSETS, "textures", "entity", "jackal.png")),
        "cerberusTexture": b64(os.path.join(ASSETS, "textures", "entity", "cerberus.png")),
        "sleipnirTexture": b64(os.path.join(ASSETS, "textures", "entity", "sleipnir.png")),
        "sleipnirGlow": b64(os.path.join(ASSETS, "textures", "entity", "sleipnir_glow.png")),
        "trinkets": {name: json.loads(read(os.path.join(FABRIC_ASSETS, "trinkets", name + ".json")))
                     for name in ORDER if os.path.exists(os.path.join(FABRIC_ASSETS, "trinkets", name + ".json"))},
        "wornModels": {name: json.loads(read(os.path.join(ASSETS, "models", "item", name + "_worn.json")))
                       for name in ORDER if os.path.exists(os.path.join(ASSETS, "models", "item", name + "_worn.json"))},
        "wornPalette": b64(os.path.join(ASSETS, "textures", "item", "worn_palette.png")),
        "wornTextures": {name: b64(os.path.join(ASSETS, "textures", "item", "worn", name + ".png"))
                         for name in ORDER if os.path.exists(os.path.join(ASSETS, "textures", "item", "worn", name + ".png"))},
    }
    # Entidades chinas: la geometría sale del mismo diseño que ChinaModelLayers.java (china_entities.py) y las texturas,
    # de los archivos del mod.
    import china_entities as CE  # noqa: E402
    data["china"] = {}
    for key, make, width in (("monkey", CE.monkey, 64), ("dragon", CE.dragon, 256)):
        parts, _img, _glow, tex_w, tex_h = CE.bake(make(), width)
        name = CE.NAMES[key][1]
        data["china"][key] = {"parts": parts, "texW": tex_w, "texH": tex_h, "rootOffset": list(CE.ROOT_OFFSET[key]),
                              "tex": b64(os.path.join(ASSETS, "textures", "entity", name + ".png")),
                              "glow": b64(os.path.join(ASSETS, "textures", "entity", name + "_glow.png"))}
    # Cuervos de Odín y Skíðblaðnir (norse_entities.py, el mismo diseño que client/entity/geometry).
    import norse_entities as NE  # noqa: E402
    for key in NE.MODELS:
        parts, _img, _glow, tex_w, tex_h = NE.bake_one(key)
        name = CE.NAMES[key][1]
        data["china"][key] = {"parts": parts, "texW": tex_w, "texH": tex_h, "rootOffset": list(CE.ROOT_OFFSET[key]),
                              "scale": NE.SCALE[key],
                              "tex": b64(os.path.join(ASSETS, "textures", "entity", name + ".png")),
                              "glow": glow_b64(name)}
    # Pegaso y Talos (greece_entities.py, Grecia 3).
    import greece_entities as GE  # noqa: E402
    for key in GE.MODELS:
        parts, _img, _glow, tex_w, tex_h = GE.bake_one(key)
        name = CE.NAMES[key][1]
        data["china"][key] = {"parts": parts, "texW": tex_w, "texH": tex_h, "rootOffset": list(CE.ROOT_OFFSET[key]),
                              "scale": GE.SCALE[key],
                              "tex": b64(os.path.join(ASSETS, "textures", "entity", name + ".png")),
                              "glow": b64(os.path.join(ASSETS, "textures", "entity", name + "_glow.png"))}
    # Compañeros con la propuesta aprobada (china_entities.APPROVED): partes, textura y escala de su propuesta, como el mod.
    import importlib.util  # noqa: E402
    for key in CE.APPROVED:
        parts, img, glow, tex_w, tex_h = CE.proposal(key)
        spec = importlib.util.spec_from_file_location("p_" + key, os.path.join(os.path.dirname(os.path.abspath(__file__)),
                                                                                "proposals", key + ".py"))
        mod = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(mod)
        data["china"][key] = {"parts": parts, "texW": tex_w, "texH": tex_h, "rootOffset": list(CE.ROOT_OFFSET[key]),
                              "scale": mod.SCALE, "tex": CE.b64(img), "glow": CE.b64(glow)}
    # Armas que vuelan (weapon_entities.py): el modelo 3D que se ve en el aire en lugar del ícono.
    import weapon_entities as WE  # noqa: E402
    data["weapons"] = {}
    for key in WE.MODELS:
        parts, _img, _glow, tex_w, tex_h = WE.bake_one(key)
        data["weapons"][key] = {"parts": parts, "texW": tex_w, "texH": tex_h, "scale": WE.SCALE[key],
                                "tex": b64(os.path.join(ASSETS, "textures", "entity", "weapon", key + ".png")),
                                "glow": b64(os.path.join(ASSETS, "textures", "entity", "weapon", key + "_glow.png"))}
    # Cabezas de Medusa y de Mímir como objetos 3D (head_items.py).
    data["heads"] = {}
    for key in ["medusa_head_3d", "mimir_head_3d"] + [f"medusa_head_gaze_{i}" for i in range(8)]:
        data["heads"][key] = {"model": json.loads(read(os.path.join(ASSETS, "models", "item", key + ".json"))),
                              "tex": b64(os.path.join(ASSETS, "textures", "item", "head", key + ".png"))}
    # Cómo se ve cada reliquia en la mano (la misma definición del ítem que usa el juego): modelo 3D o ícono.
    data["held"] = {name: held(name) for name in ORDER}
    # Poses de las alas de Ícaro: el juego elige entre 95 de una grilla (worn_models.ICARUS_POSES, client/IcarusPose); el
    # visor carga siete representativas (plegadas, cuatro del aleteo, planeo y picada) con la textura única de las alas.
    import worn_models  # noqa: E402
    for pose, (lift, back) in {"folded": (-62, 22), "flap_0": (46, 12), "flap_1": (22, 12), "flap_2": (-8, 12),
                               "flap_3": (-26, 12), "glide": (4, 6), "dive": (34, 58)}.items():
        name = worn_models.icarus_pose_name(lift, back)
        data["wornModels"]["icarus_wings_" + pose] = json.loads(read(os.path.join(ASSETS, "models", "item", name + "_worn.json")))
        data["wornTextures"]["icarus_wings_" + pose] = b64(os.path.join(ASSETS, "textures", "item", "worn", "icarus_wings.png"))
    template = read(os.path.join(os.path.dirname(os.path.abspath(__file__)), "viewer_template.html"))
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        f.write(template.replace("__DATA__", json.dumps(data, ensure_ascii=False)))
    print(out)
