"""Página para elegir el nuevo ícono del mod y el emblema de la pestaña creativa (segunda ronda: un solo símbolo).
Uso: python ../model-gen/emblem_concepts2.py <carpeta> && python emblem_page.py <carpeta>  (escribe emblema-opciones.html)"""
import base64
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
D = sys.argv[1]


def b64(name):
    return base64.b64encode(open(os.path.join(D, name), "rb").read()).decode()


CONCEPTS = [
    ("ouroboros", "1", "Ouroboros", "La serpiente de oro que se muerde la cola rodeando una gema de reliquia. Está en Egipto (el Libro del Mundo Inferior), le da nombre el griego y es Jörmungandr en el mito nórdico: el ciclo eterno de los mitos. Se reconoce incluso a 16 px."),
    ("tree", "2", "Árbol del mundo", "El eje del cosmos con frutos de oro bajo el cielo estrellado: Yggdrasil, el Fusang chino donde descansan los soles, el árbol de las Hespérides. El más cálido y mitológico de los tres."),
    ("altar", "3", "La reliquia en su altar", "Un pedestal antiguo de piedra con una reliquia que flota y brilla: lo que el mod te hace buscar. El más directo sobre «reliquias», el menos sobre «mitos»."),
]


def card(key, n, title, desc):
    big, tab = b64(f"c2_{key}.png"), b64(f"c2_{key}_tab.png")
    return f'''<article class="card">
  <h3><span class="key">{n}</span> {title}</h3>
  <div class="row">
    <img class="i128" src="data:image/png;base64,{big}" alt="{title}, ícono del mod">
    <div class="small">
      <figure><img class="i64" src="data:image/png;base64,{big}" alt=""><figcaption>64 px</figcaption></figure>
      <figure><img class="i32" src="data:image/png;base64,{big}" alt=""><figcaption>32 px (lista de mods)</figcaption></figure>
      <figure><div class="tab"><img src="data:image/png;base64,{tab}" alt=""></div><figcaption>Pestaña (16 px)</figcaption></figure>
    </div>
  </div>
  <p>{desc}</p>
</article>'''


cards = "\n".join(card(*c) for c in CONCEPTS)
html = f'''<title>Emblema de Mythic Relics</title>
<meta name="claude:viewport-fit-sides" content="cover">
<style>
:root {{ --bg: #11100e; --panel: #1c1a16; --line: #3a342a; --fg: #efe8da; --muted: #a89f8c; --gold: #e9b949;
  --slot: #c6c6c6; --slot-hi: #ffffff; --slot-lo: #555555; color-scheme: dark; }}
body {{ background: var(--bg); color: var(--fg); font: 15px/1.55 system-ui, sans-serif; margin: 0;
  padding-left: max(16px, env(safe-area-inset-left, 0px)); padding-right: max(16px, env(safe-area-inset-right, 0px)); padding-block: 28px 48px; }}
main {{ max-width: 1040px; margin: 0 auto; display: grid; gap: 22px; }}
h1 {{ margin: 0; font-size: 26px; text-wrap: balance; }}
h2 {{ margin: 0; font-size: 18px; color: var(--gold); }}
h3 {{ margin: 0; font-size: 16px; display: flex; gap: 10px; align-items: center; }}
.lead, .card p, figcaption {{ color: var(--muted); margin: 0; }}
.lead {{ max-width: 72ch; }}
img {{ image-rendering: pixelated; display: block; }}
.syms {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 12px; }}
.sym {{ margin: 0; display: grid; gap: 6px; justify-items: center; text-align: center; background: var(--panel);
  border: 1px solid var(--line); border-radius: 8px; padding: 12px; }}
.sym img {{ width: 112px; height: 112px; }}
figcaption {{ font-size: 13px; }}
.grid {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 16px; }}
.card {{ background: var(--panel); border: 1px solid var(--line); border-radius: 10px; padding: 16px; display: grid; gap: 12px; min-width: 0; }}
.key {{ background: var(--gold); color: #1a1208; border-radius: 6px; padding: 0 8px; font-weight: 700; }}
.row {{ display: flex; gap: 16px; align-items: end; flex-wrap: wrap; }}
.small {{ display: grid; gap: 8px; }}
figure {{ margin: 0; display: grid; gap: 4px; justify-items: center; }}
.i128 {{ width: 256px; height: 256px; max-width: 100%; background: #2a2620; border-radius: 8px; }}
.i64 {{ width: 64px; height: 64px; }}
.i32 {{ width: 32px; height: 32px; }}
.t160 {{ width: 160px; height: 160px; background: #2a2620; border-radius: 6px; }}
.tab {{ width: 52px; height: 52px; display: grid; place-items: center; background: var(--slot);
  border: 3px solid; border-color: var(--slot-hi) var(--slot-lo) var(--slot) var(--slot-hi); border-radius: 4px 4px 0 0; }}
.tab img {{ width: 32px; height: 32px; }}
</style>
<main>
  <h1>Emblema de Mythic Relics</h1>
  <p class="lead">Un solo símbolo con personalidad propia que evoque la mitología en general, sin repartir un espacio por cultura. Cada uno se muestra como ícono del mod y, simplificado, en la pestaña creativa. Elige en el chat.</p>
  <section class="grid">{cards}</section>
</main>
'''
open(os.path.join(HERE, "emblema-opciones.html"), "w", encoding="utf-8").write(html)
print("ok")
