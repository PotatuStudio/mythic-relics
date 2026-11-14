"""Página para elegir el ícono del rayo de Zeus entre tres propuestas hechas desde cero.
Uso: python greece3_bolt_page.py  (escribe rayo-opciones.html)"""
import base64
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from china_draft import PAL, png_b64  # noqa: E402
from greece3_bolts import OPTIONS  # noqa: E402

cards = []
for key, name, make in OPTIONS:
    img = [[PAL[ch] for ch in row] for row in make()]
    data = png_b64(img)
    doc = make.__doc__.split(":", 1)[1].strip()
    cards.append(f'''<article class="card">
  <h2><span class="key">{key}</span> {name}</h2>
  <div class="views">
    <figure><img class="big" src="data:image/png;base64,{data}" alt="Rayo {name}, ampliado"><figcaption>Ampliado</figcaption></figure>
    <figure><div class="slot"><img src="data:image/png;base64,{data}" alt=""></div><figcaption>En el inventario</figcaption></figure>
  </div>
  <p>{doc}</p>
</article>''')

html = f'''<title>Rayo de Zeus · opciones</title>
<meta name="claude:viewport-fit-sides" content="cover">
<style>
:root {{ --bg: #0c1118; --panel: #151d28; --line: #2e3c50; --fg: #ece6da; --muted: #9eabbd; --gold: #e9b949;
  --slot: #8b8b8b; --slot-hi: #ffffff; --slot-lo: #373737; color-scheme: dark; }}
body {{ background: var(--bg); color: var(--fg); font: 15px/1.55 system-ui, sans-serif;
  padding-left: max(16px, env(safe-area-inset-left, 0px)); padding-right: max(16px, env(safe-area-inset-right, 0px));
  padding-block: 28px; }}
main {{ max-width: 1040px; margin: 0 auto; display: grid; gap: 20px; }}
h1 {{ margin: 0; font-size: 26px; text-wrap: balance; }}
.lead {{ margin: 0; color: var(--muted); max-width: 70ch; }}
.grid {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 16px; }}
.card {{ background: var(--panel); border: 1px solid var(--line); border-radius: 10px; padding: 18px; display: grid; gap: 12px; min-width: 0; }}
.card h2 {{ margin: 0; font-size: 18px; display: flex; gap: 10px; align-items: center; }}
.key {{ color: var(--bg); background: var(--gold); border-radius: 6px; padding: 0 9px; font-weight: 700; }}
.views {{ display: flex; gap: 16px; align-items: end; flex-wrap: wrap; }}
figure {{ margin: 0; display: grid; gap: 6px; justify-items: center; }}
figcaption {{ color: var(--muted); font-size: 12px; }}
img {{ image-rendering: pixelated; }}
.big {{ width: 160px; height: 160px; background: var(--slot); border-radius: 4px; }}
.slot {{ width: 54px; height: 54px; background: var(--slot); display: grid; place-items: center;
  border: 3px solid; border-color: var(--slot-lo) var(--slot-hi) var(--slot-hi) var(--slot-lo); }}
.slot img {{ width: 48px; height: 48px; }}
.card p {{ margin: 0; color: var(--muted); }}
</style>
<main>
  <h1>Rayo de Zeus · tres opciones</h1>
  <p class="lead">Hechas desde cero: el zigzag clásico, con su quiebre bien marcado y un píxel de margen para que el contorno no se corte. Elige una en el chat; después la pulo y la pongo en la mano del muñeco.</p>
  <section class="grid">
{chr(10).join(cards)}
  </section>
</main>
'''
open(os.path.join(HERE, "rayo-opciones.html"), "w", encoding="utf-8").write(html)
print("ok")
