"""Página de comparación de los compañeros (aliados, monturas e invocaciones): el modelo actual al lado de la propuesta
mejorada, los dos en 3D con las mismas partes de ModelPart que usa el juego (UV de caja; la y crece hacia abajo, el
frente es -z y el suelo está en y = 24).

- Antes: los modelos del mod tal como están (china/greece/norse/weapon_entities.py y, para los cuatro hechos a mano en
  Java, su transcripción en BEFORE_MANUAL, igual a la del visor general).
- Después: tools/model-gen/proposals/<clave>.py, cada uno con build() -> Model (china_entities.Model), TEX_W, SCALE,
  ROOT_OFFSET, SEED y NOTES (texto en español con los cambios y su relación con el mito). Mientras no exista, se ve
  solo el actual.

Uso: python companions_compare.py <salida.html> [--only clave]
Vista rápida sin servidor (para revisar una propuesta):
  chrome-headless-shell --headless --enable-unsafe-swiftshader --use-angle=swiftshader --window-size=1400,900 \
      --virtual-time-budget=6000 --screenshot=<png> "file:///<salida.html>#<clave>/<vista>"
  vistas: front, side, back, top, three (tres cuartos; la de por defecto)."""
import base64
import importlib.util
import io
import json
import os
import random
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import china_entities as CE  # noqa: E402
import greece_entities as GE  # noqa: E402
import norse_entities as NE  # noqa: E402
import weapon_entities as WE  # noqa: E402
from moddata import ASSETS  # noqa: E402

PROPOSALS = os.path.join(HERE, "proposals")

# Orden y nombre de cada compañero, con la reliquia que lo trae.
ENTITIES = [
    ("dragon", "Long (dragón)", "Perla del dragón"),
    ("sleipnir", "Sleipnir", "Herradura de Sleipnir"),
    ("pegasus", "Pegaso", "Brida de oro"),
    ("raven", "Huginn y Muninn", "Cuervos de Odín"),
    ("cerberus", "Cerbero", "Collar de Cerbero"),
    ("talos", "Talos", "Clavo de bronce de Talos"),
    ("jackal", "Chacal", "Anillo de los chacales"),
    ("scarab", "Escarabajo", "Escarabajo dorado"),
    ("monkey", "Mono guerrero", "Pelos del Rey Mono"),
    ("skidbladnir", "Skíðblaðnir", "Skíðblaðnir"),
    ("freyr_sword", "Espada de Freyr", "Espada de Freyr"),
    ("falcon", "Halcón de Freyja", "Manto de plumas de halcón"),
]

PI6 = 0.5235987755982988
# Los cuatro modelos hechos a mano en Java (ScarabModel, JackalModel, CerberusModel, SleipnirModel), como en el visor:
# partes [nombre, padre, offset, rot, [[u, v, x, y, z, w, h, d, espejo], ...]].
BEFORE_MANUAL = {
    "scarab": {"tex": "scarab.png", "texW": 64, "texH": 32, "scale": 1.0, "parts": [
        ["body", None, [0, 23, 1.5], [0, 0, 0], [[0, 0, -3, -3, -3.5, 6, 3, 7, False], [0, 10, -2, -4, -2.5, 4, 1, 5, False]]],
        ["pronotum", None, [0, 23, -3.5], [0, 0, 0], [[26, 0, -2.5, -2, -1.5, 5, 2, 3, False]]],
        ["head", None, [0, 23, -5], [0, 0, 0], [[26, 5, -2, -1, -2, 4, 1, 2, False]]],
    ] + [[f"leg_l{i}", None, [2.5, 22.2, z], [0, s, 0.5], [[42, 0, 0, -0.5, -0.5, 4, 1, 1, False]]]
         for i, (z, s) in enumerate(zip([-3.5, -1.0, 1.5], [0.5, 0.0, -0.5]))]
      + [[f"leg_r{i}", None, [-2.5, 22.2, z], [0, -s, -0.5], [[42, 0, -4, -0.5, -0.5, 4, 1, 1, True]]]
         for i, (z, s) in enumerate(zip([-3.5, -1.0, 1.5], [0.5, 0.0, -0.5]))]},
    "jackal": {"tex": "jackal.png", "texW": 64, "texH": 32, "scale": 1.0, "parts": [
        ["body", None, [0, 14.5, 0], [0, 0, 0], [[0, 0, -2.5, -2.5, -6, 5, 5, 12, False], [34, 0, -3, -3, -6.5, 6, 6, 1, False]]],
        ["head", None, [0, 12.5, -6.5], [0, 0, 0], [[34, 7, -2.5, -3, -4, 5, 5, 4, False], [48, 0, -1.5, -0.5, -8, 3, 2, 4, False],
                                                   [52, 7, -2.5, -7, -2, 2, 4, 1, False], [52, 7, 0.5, -7, -2, 2, 4, 1, True]]],
        ["tail", None, [0, 12.5, 6], [0.6283185307, 0, 0], [[8, 17, -1, 0, -1, 2, 7, 2, False]]],
    ] + [[f"leg{i}", None, [x, 17, z], [0, 0, 0], [[0, 17, -1, 0, -1, 2, 7, 2, m]]]
         for i, (x, z, m) in enumerate([(1.5, -4.5, False), (-1.5, -4.5, True), (1.5, 4.5, False), (-1.5, 4.5, True)])]},
    "cerberus": {"tex": "cerberus.png", "texW": 64, "texH": 64, "scale": 1.7, "parts": [
        ["body", None, [0, 10, 0], [0, 0, 0], [[0, 0, -5, -5, -8, 10, 9, 8, False], [0, 17, -4, -3, 0, 8, 7, 8, False]]],
    ] + sum([[[f"head{i}", None, off, [0, turn, 0], [[36, 0, -3, -3, -5, 6, 6, 5, False], [36, 11, -2, -1, -10, 4, 2, 5, False],
                                                    [54, 11, -3, -6, -2, 2, 3, 1, False], [54, 11, 1, -6, -2, 2, 3, 1, True]]],
              [f"jaw{i}", f"head{i}", [0, 1, -5], [0, 0, 0], [[36, 18, -2, 0, -5, 4, 1, 5, False]]]]
             for i, (off, turn) in enumerate([([0, 7, -8], 0), ([5, 9, -6.5], -0.5), ([-5, 9, -6.5], 0.5)])], [])
      + [["tail", None, [0, 9, 8], [0.7, 0, 0], [[0, 46, -1.5, -1.5, 0, 3, 3, 6, False]]],
         ["tail_mid", "tail", [0, 0, 5.5], [-0.35, 0, 0], [[32, 24, -1, -1, 0, 2, 2, 5, False]]],
         ["tail_head", "tail_mid", [0, 0, 4.5], [-0.45, 0, 0], [[28, 32, -0.5, -0.5, 0, 1, 1, 5, False]]]]
      + [[f"leg{i}", None, [x, 14, z], [0, 0, 0], [[0, 32, -1.5, 0, -1.5, 3, 10, 3, m], [12, 32, -2, 8, -2.5, 4, 2, 4, m]]]
         for i, (x, z, m) in enumerate([(3.5, -4, False), (-3.5, -4, True), (3, 5, False), (-3, 5, True)])]},
    "sleipnir": {"tex": "sleipnir.png", "glow": "sleipnir_glow.png", "texW": 128, "texH": 64, "scale": 1.0, "parts": [
        ["body", None, [0, 11, 5], [0, 0, 0], [[0, 0, -5, -8, -17, 10, 10, 22, False], [32, 32, -5.5, -8.6, -11, 11, 1, 8, False],
                                              [118, 22, -1, -10.6, -11, 2, 2, 1, False], [70, 32, -5.5, -6, -17.6, 11, 9, 1, False],
                                              [96, 22, 5, -7, -12, 1, 6, 10, False], [96, 22, -6, -7, -12, 1, 6, 10, True]]],
        ["tail", "body", [0, -5, 2], [PI6, 0, 0], [[16, 32, -1.5, 0, 0, 3, 8, 4, False]]],
        ["tail_tip", "tail", [0, 7.5, 0.5], [0, 0, 0], [[30, 42, -1.5, 0, 0, 3, 10, 3, False]]],
        ["head", None, [0, 4, -12], [PI6, 0, 0], [[64, 0, -2.05, -6, -2, 4, 12, 7, False], [112, 0, -1.5, -12, 5.01, 3, 18, 2, False],
                                                 [86, 12, -2, -11, -7, 4, 5, 5, False]]],
        ["head_gear", "head", [0, 0, 0], [0, 0, 0], [[86, 0, -3, -11, -2, 6, 5, 7, False], [70, 22, -2.5, -11.6, -7.5, 5, 1, 8, False],
                                                    [118, 25, 0.55, -13, 4, 2, 3, 1, False], [118, 25, -2.55, -13, 4, 2, 3, 1, False]]],
    ] + [[f"leg{i}", None, [x, 14, z], [0, 0, 0], [[0, 32, -3 if left else -1, -1.01, bz, 4, 11, 4, left]]]
         for i, (x, z, bz, left) in enumerate([(4, -10, -1.9, True), (-4, -10, -1.9, False), (4, -5, -1.9, True), (-4, -5, -1.9, False),
                                               (4, 2, -1, True), (-4, 2, -1, False), (4, 7, -1, True), (-4, 7, -1, False)])]},
}

def png_b64(path):
    if not os.path.exists(path):
        return None
    with open(path, "rb") as f:
        return base64.b64encode(f.read()).decode()


def img_b64(img):
    buf = io.BytesIO()
    img.save(buf, "PNG")
    return base64.b64encode(buf.getvalue()).decode()


def from_baked(parts, tex_w, tex_h, tex, glow, scale, root_offset):
    out = []
    for p in parts:
        off = p["offset"] if p["parent"] else [v + root_offset[i] for i, v in enumerate(p["offset"])]
        out.append([p["name"], p["parent"], off, p["rot"], [[*c, False] for c in p["cubes"]]])
    return {"parts": out, "texW": tex_w, "texH": tex_h, "tex": tex, "glow": glow, "scale": scale}


def before(key):
    tex_dir = os.path.join(ASSETS, "textures", "entity")
    if key in BEFORE_MANUAL:
        b = BEFORE_MANUAL[key]
        return {"parts": b["parts"], "texW": b["texW"], "texH": b["texH"], "tex": png_b64(os.path.join(tex_dir, b["tex"])),
                "glow": png_b64(os.path.join(tex_dir, b.get("glow", "-"))), "scale": b["scale"]}
    if key in ("monkey", "dragon"):
        parts, img, glow, w, h = CE.bake({"monkey": CE.monkey, "dragon": CE.dragon}[key](), {"monkey": 64, "dragon": 256}[key])
        scale = 1.0
    elif key in NE.MODELS:
        parts, img, glow, w, h = NE.bake_one(key)
        scale = NE.SCALE[key]
    elif key in GE.MODELS:
        parts, img, glow, w, h = GE.bake_one(key)
        scale = GE.SCALE[key]
    else:
        parts, img, glow, w, h = WE.bake_one(key)
        scale = 0.7    # FreyrSwordRenderer.SCALE
    return from_baked(parts, w, h, img_b64(img), img_b64(glow), scale, CE.ROOT_OFFSET.get(key, (0, 0, 0)))


def after(key):
    path = os.path.join(PROPOSALS, key + ".py")
    if not os.path.exists(path):
        return None
    spec = importlib.util.spec_from_file_location("proposal_" + key, path)
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)
    random.seed(getattr(mod, "SEED", 7))
    parts, img, glow, w, h = CE.bake(mod.build(), mod.TEX_W)
    d = from_baked(parts, w, h, img_b64(img), img_b64(glow), mod.SCALE, getattr(mod, "ROOT_OFFSET", (0, 0, 0)))
    d["notes"] = mod.NOTES
    d["cubes"] = sum(len(p[4]) for p in d["parts"])
    return d


def main(out, only=None):
    data = []
    for key, name, relic in ENTITIES:
        if only and key != only:
            continue
        b = before(key)
        b["cubes"] = sum(len(p[4]) for p in b["parts"])
        data.append({"key": key, "name": name, "relic": relic, "before": b, "after": after(key)})
    with open(os.path.join(HERE, "companions_template.html"), encoding="utf-8") as f:
        template = f.read()
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        f.write(template.replace("__DATA__", json.dumps(data, ensure_ascii=False)))
    print(out, [(d["key"], d["before"]["cubes"], d["after"] and d["after"]["cubes"]) for d in data])


if __name__ == "__main__":
    args = sys.argv[1:]
    only = None
    if "--only" in args:
        only = args[args.index("--only") + 1]
    main(args[0], only)
