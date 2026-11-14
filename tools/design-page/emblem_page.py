"""Página para elegir el nuevo ícono del mod y el emblema de la pestaña creativa.
Uso: python ../model-gen/emblem_concepts.py <carpeta> && python emblem_page.py <carpeta>  (escribe emblema-opciones.html)"""
import base64
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
D = sys.argv[1]


def b64(name):
    return base64.b64encode(open(os.path.join(D, name), "rb").read()).decode()


SYMS = [("egypt", "Egipto", "La pirámide con el disco solar de Ra"), ("greece", "Grecia", "El templo de columnas con su frontón"),
        ("rome", "Roma", "El águila de las legiones sobre su estandarte"), ("norse", "Nórdicos", "El barco vikingo con proa de dragón"),
        ("china", "China", "El long con sus aletas rojas y la perla"), ("japan", "Japón", "El torii rojo con el sol naciente")]
syms = "\n".join(f'''<figure class="sym"><img src="data:image/png;base64,{b64(f"sym_{k}.png")}" alt="{name}"><figcaption><b>{name}</b><br>{desc}</figcaption></figure>'''
                 for k, name, desc in SYMS)


def icon_card(key, title, desc):
    data = b64(f"icon_{key}.png")
    return f'''<article class="card">
  <h3><span class="key">{key}</span> {title}</h3>
  <div class="row">
    <img class="i128" src="data:image/png;base64,{data}" alt="{title} a 128 px">
    <div class="small">
      <figure><img class="i64" src="data:image/png;base64,{data}" alt=""><figcaption>64 px</figcaption></figure>
      <figure><img class="i32" src="data:image/png;base64,{data}" alt=""><figcaption>32 px (lista de mods)</figcaption></figure>
    </div>
  </div>
  <p>{desc}</p>
</article>'''


def tab_card(key, title, desc):
    data = b64(f"tab_{key}.png")
    return f'''<article class="card">
  <h3><span class="key">T{key}</span> {title}</h3>
  <div class="row">
    <img class="t160" src="data:image/png;base64,{data}" alt="{title} ampliado">
    <figure><div class="tab"><img src="data:image/png;base64,{data}" alt=""></div><figcaption>En la pestaña</figcaption></figure>
  </div>
  <p>{desc}</p>
</article>'''


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
.grid {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 16px; }}
.card {{ background: var(--panel); border: 1px solid var(--line); border-radius: 10px; padding: 16px; display: grid; gap: 12px; min-width: 0; }}
.key {{ background: var(--gold); color: #1a1208; border-radius: 6px; padding: 0 8px; font-weight: 700; }}
.row {{ display: flex; gap: 16px; align-items: end; flex-wrap: wrap; }}
.small {{ display: grid; gap: 8px; }}
figure {{ margin: 0; display: grid; gap: 4px; justify-items: center; }}
.i128 {{ width: 256px; height: 256px; max-width: 100%; }}
.i64 {{ width: 64px; height: 64px; }}
.i32 {{ width: 32px; height: 32px; }}
.t160 {{ width: 160px; height: 160px; background: #2a2620; border-radius: 6px; }}
.tab {{ width: 52px; height: 52px; display: grid; place-items: center; background: var(--slot);
  border: 3px solid; border-color: var(--slot-hi) var(--slot-lo) var(--slot) var(--slot-hi); border-radius: 4px 4px 0 0; }}
.tab img {{ width: 32px; height: 32px; }}
</style>
<main>
  <h1>Emblema de Mythic Relics</h1>
  <p class="lead">Un símbolo propio de cada mitología, no los ítems del juego, para que el ícono diga de un vistazo qué culturas trae el mod. Elige un ícono del mod (A o B) y un emblema de pestaña (T1, T2 o T3) en el chat.</p>
  <h2>Los seis símbolos</h2>
  <section class="syms">{syms}</section>
  <h2>Ícono del mod (lista de mods)</h2>
  <section class="grid">
    {icon_card("A", "Escudo de seis cuarteles", "Como el emblema actual, pero con un símbolo propio de cada cultura sobre su color, con borde y cruz de oro. Se lee como un blasón de las mitologías.")}
    {icon_card("B", "Medallón de las seis culturas", "Un medallón de oro con los seis símbolos en anillo, cada uno sobre su color, y una gema de reliquia al centro: las culturas reunidas alrededor de lo que el mod colecciona.")}
  </section>
  <h2>Emblema de la pestaña creativa (16×16)</h2>
  <p class="lead">A 16 píxeles no caben seis dibujos: estas variantes resumen la idea y se ven bien en la pestaña.</p>
  <section class="grid">
    {tab_card("1", "Escudo de colores", "El escudo con los seis colores de las culturas y la cruz de oro. Combina con el ícono A.")}
    {tab_card("2", "Medallón", "El aro de oro con seis puntos de color y la gema al centro. Combina con el ícono B.")}
    {tab_card("3", "Gema de reliquia", "Solo la gema tallada en su engaste de oro: lo que une a todas las reliquias. Sirve con cualquiera de los dos.")}
  </section>
</main>
'''
open(os.path.join(HERE, "emblema-opciones.html"), "w", encoding="utf-8").write(html)
print("ok")
