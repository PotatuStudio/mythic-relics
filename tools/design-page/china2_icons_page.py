"""Página de comparación de íconos de la tanda 1 china: antes (primer borrador) / ahora y cómo se ven en el inventario
junto a reliquias de otras culturas. Uso: python china2_draft_v1.py china2_draft_v1.json && python china2_draft.py china2_draft.json && python china2_icons_page.py"""
import base64
import io
import json
import os
import sys

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from china2_relics import RELICS  # noqa: E402

ITEMS = os.path.join(HERE, "..", "..", "common", "src", "main", "resources", "assets", "mythicrelics", "textures", "item")
OTHERS = ["gungnir", "mjolnir", "hermes_sandals", "pandora_jar", "golden_fleece", "ra_disk"]


def first_frame(b64png):
    im = Image.open(io.BytesIO(base64.b64decode(b64png))).convert("RGBA").crop((0, 0, 16, 16))
    buf = io.BytesIO()
    im.save(buf, "PNG")
    return base64.b64encode(buf.getvalue()).decode()


def file_frame(path):
    im = Image.open(path).convert("RGBA").crop((0, 0, 16, 16))
    buf = io.BytesIO()
    im.save(buf, "PNG")
    return base64.b64encode(buf.getvalue()).decode()


old = json.load(open(os.path.join(HERE, "china2_draft_v1.json"), encoding="utf-8"))["icons"]
new = json.load(open(os.path.join(HERE, "china2_draft.json"), encoding="utf-8"))["icons"]
names = {r["id"]: r["name"] for r in RELICS}
CHANGED = {}

rows = []
for rid in new:
    o, n = first_frame(old[rid]["png"]), first_frame(new[rid]["png"])
    note = CHANGED.get(rid, "Se queda el primer borrador.")
    rows.append(f'''<article class="row"><h2>{names[rid]}</h2>
<div class="pair"><figure><img src="data:image/png;base64,{o}" alt="Antes: {names[rid]}"><figcaption>Antes</figcaption></figure>
<figure><img src="data:image/png;base64,{n}" alt="Ahora: {names[rid]}"><figcaption>Ahora</figcaption></figure></div>
<p class="note">{note}</p></article>''')
china_slots = "".join(f'<div class="slot" title="{names[r]}"><img src="data:image/png;base64,{first_frame(new[r]["png"])}" alt="{names[r]}"></div>' for r in new)
other_slots = "".join(f'<div class="slot" title="{o}"><img src="data:image/png;base64,{file_frame(os.path.join(ITEMS, o + ".png"))}" alt="{o}"></div>' for o in OTHERS)
mixed = "".join(f'<div class="slot"><img src="data:image/png;base64,{first_frame(new[r]["png"])}" alt="{names[r]}"></div>' for r in list(new)[:3]) + other_slots

html = f'''<title>China · Íconos 2</title>
<meta name="claude:viewport-fit-sides" content="cover">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Marcellus&family=Figtree:wght@400;600&display=swap">
<style>
:root {{ --night: #140b0a; --stone: #22110f; --line: #4a2620; --sand: #f3e2c8; --muted: #b8998a; --gold: #f0c04a;
  --font-display: "Marcellus", Georgia, serif; --font-body: "Figtree", "Segoe UI", system-ui, sans-serif; color-scheme: dark; }}
* {{ box-sizing: border-box; }}
body {{ margin: 0; background: var(--night); color: var(--sand); font-family: var(--font-body); font-size: 15px; line-height: 1.5;
  padding: 28px max(16px, env(safe-area-inset-left, 0px)) 56px; }}
.wrap {{ max-width: 1080px; margin: 0 auto; display: grid; gap: 24px; }}
h1 {{ font-family: var(--font-display); font-weight: 400; font-size: 2.1rem; margin: 0; }}
h2 {{ font-family: var(--font-display); font-weight: 400; font-size: 1.15rem; margin: 0; color: var(--gold); }}
p {{ margin: 0; }}
.lead, .note {{ color: var(--muted); }}
.note {{ font-size: 14px; }}
.grid {{ display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 14px; }}
.row {{ background: var(--stone); border: 1px solid var(--line); border-radius: 10px; padding: 14px; display: grid; gap: 10px; align-content: start; }}
.pair {{ display: flex; gap: 14px; }}
figure {{ margin: 0; display: grid; gap: 4px; justify-items: center; }}
figure img {{ width: 96px; height: 96px; image-rendering: pixelated; background: #8b8b8b; border-radius: 4px; }}
figcaption {{ font-size: 12px; color: var(--muted); text-transform: uppercase; letter-spacing: .08em; }}
.inv {{ background: var(--stone); border: 1px solid var(--line); border-radius: 10px; padding: 18px; display: grid; gap: 12px; }}
.bar {{ display: flex; flex-wrap: wrap; gap: 0; background: #c6c6c6; padding: 6px; width: fit-content; max-width: 100%;
  border: 3px solid; border-color: #ffffff #555555 #555555 #ffffff; }}
.slot {{ width: 54px; height: 54px; background: #8b8b8b; border: 3px solid; border-color: #373737 #ffffff #ffffff #373737;
  display: grid; place-items: center; }}
.slot img {{ width: 48px; height: 48px; image-rendering: pixelated; }}
</style>
<div class="wrap">
  <header style="display:grid;gap:8px"><h1>China · Íconos 2</h1>
  <p class="lead">Íconos pulidos de la tanda 2. En el juego, las partes de oro, joyas y fuego llevan el brillo animado de las otras reliquias; acá se muestra el primer cuadro.</p></header>
  <section class="inv"><h2>En el inventario</h2>
    <p class="note">La tanda 2 completa, a tamaño de inventario (x3):</p><div class="bar">{china_slots}</div>
    <p class="note">Junto a reliquias de otras culturas, para comparar el estilo:</p><div class="bar">{mixed}</div>
  </section>
  <div class="grid">{"".join(rows)}</div>
</div>
'''
open(os.path.join(HERE, "china-iconos-2.html"), "w", encoding="utf-8").write(html)
print("ok", len(html) // 1024, "KB")
