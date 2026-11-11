"""Arma la página de revisión de los modelos nuevos pedidos tras la prueba en NeoForge (2026-11-10): cuervos de Odín,
Skíðblaðnir, Mjölnir y Gungnir en 3D, el Paladio rehecho y el candado de Jano. Lee los mismos datos que el mod.
Uso: python revision2_build.py  (escribe revision-modelos.html)"""
import base64
import io
import json
import os
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
MG = os.path.join(HERE, "..", "model-gen")
sys.path.insert(0, MG)
import norse_entities as NE  # noqa: E402
import norse_models as NM  # noqa: E402
import rome_models as RM  # noqa: E402
import china_entities as CE  # noqa: E402
from mclib import png  # noqa: E402


def png_b64(rows):
    path = os.path.join(HERE, "_tmp_palette.png")
    png(path, rows)
    data = base64.b64encode(open(path, "rb").read()).decode()
    os.remove(path)
    return data


def old_palladium():
    src = subprocess.run(["git", "show", "HEAD:tools/model-gen/rome_models.py"], cwd=HERE, capture_output=True,
                         text=True, encoding="utf-8").stdout
    ns = {"__file__": os.path.join(MG, "rome_models.py"), "__name__": "old"}
    exec(compile(src, "old_rome_models", "exec"), ns)
    return ns["palladium_elements"]()


data = {"entities": {}}
for key in NE.MODELS:
    parts, img, glow, w, h = NE.bake_one(key)
    data["entities"][key] = {"parts": parts, "texW": w, "texH": h, "tex": CE.b64(img), "glow": CE.b64(glow)}
data["norsePalette"] = png_b64(NM.palette())
data["romePalette"] = png_b64(RM.palette())
data["mjolnir"] = NM.mjolnir()
data["gungnir"] = NM.gungnir()
data["palladium"] = RM.palladium_elements()
data["palladiumOld"] = old_palladium()
assets = os.path.join(MG, "..", "..", "common", "src", "main", "resources", "assets", "mythicrelics", "textures", "item")
data["padlock"] = base64.b64encode(open(os.path.join(assets, "padlock.png"), "rb").read()).decode()

tpl = io.open(os.path.join(HERE, "revision2_template.html"), encoding="utf-8").read()
out = os.path.join(HERE, "revision-modelos.html")
io.open(out, "w", encoding="utf-8", newline="\n").write(tpl.replace("__DATA__", json.dumps(data, ensure_ascii=False)))
print(out)
