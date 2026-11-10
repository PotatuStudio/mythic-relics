"""Arma la página de diseño de la tanda 1 china a partir de template.html.
Uso: python china_draft.py china_draft.json && python china_build.py"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
s = open(os.path.join(HERE, "template.html"), encoding="utf-8").read()


def rep(a, b):
    global s
    assert a in s, a[:80]
    s = s.replace(a, b)


rep("<title>Egipto · Tanda 2</title>", "<title>China · Tanda 1</title>")
# Paleta: laca roja oscura, jade y oro.
rep("""  --night: #15120f; --stone: #221d18; --stone-2: #2b241d; --line: #3a3128;
  --sand: #e8d3a2; --muted: #a8977a; --gold: #e9b949; --lapis: #8aa6f0; --sun: #f08a5a; --green: #8ad874;""",
    """  --night: #140b0a; --stone: #22110f; --stone-2: #2c1714; --line: #4a2620;
  --sand: #f3e2c8; --muted: #b8998a; --gold: #f0c04a; --lapis: #7fd6c2; --sun: #f0705a; --green: #8ad874;""")
rep("scene.background = new THREE.Color(0x0c0a08);", "scene.background = new THREE.Color(0x120a09);")
rep("background: #0c0a08; }", "background: #120a09; }")
rep('''    <h1>Egipto · Tanda 2</h1>
    <p class="lead">Propuesta para revisar antes de programar. Seis reliquias con el mismo criterio que la tanda 1: el efecto nace del significado real del mito, con licencias de Minecraft que no lo contradigan. Los íconos y modelos son borradores.</p>
    <ul>
      <li><b>Arqueología:</b> la mayoría se encuentra <b>cepillando arena sospechosa</b> en pirámides y pozos del desierto. Así los cofres de las pirámides no se llenan más (ya traen alguna reliquia en el 57 % de los casos).</li>
      <li><b>Competencia por espacios:</b> Bastet compite con el Anj (collar) y Sekhmet con las vendas (guantes). Elegir es parte del juego.</li>
      <li><b>Probabilidades:</b> la arqueología se expresa por bloque cepillado; los cofres, por cofre y por pirámide (4 cofres).</li>
    </ul>''',
    '''    <h1>China · Tanda 1</h1>
    <p class="lead">Propuesta para revisar antes de programar. Nueve reliquias de los mitos chinos, con el criterio «fiel al mito, con licencias». Los íconos y modelos son borradores.</p>
    <ul>
      <li><b>De dónde salen:</b> los mitos antiguos (Nüwa, Hou Yi y Chang'e), la novela <i>Viaje al Oeste</i> (el Rey Mono, la calabaza, el abanico) y <i>La investidura de los dioses</i> (Nezha). Los dragones son los <i>long</i> chinos: sin alas, de la lluvia y del agua.</li>
      <li><b>Dos entidades nuevas:</b> los monos guerreros del Rey Mono y el dragón que se monta. Sus modelos 3D se arman en el paso siguiente, con su propio visor.</li>
      <li><b>Ya decidido:</b> el elixir es el ascenso lunar (según la fase de la luna) y el abanico es un arma de viento con tres aleteos.</li>
      <li><b>Espacios:</b> las ruedas compiten con las sandalias de Hermes (pies) y la piedra con el escarabajo y Megingjörð (cinturón).</li>
    </ul>''')
rep('''    <p class="note">Mismo muñeco y misma regla de ubicación que el visor de la tanda 1. El cayado y el mayal se llevan en las manos.</p>''',
    '''    <p class="note">Mismo muñeco y misma regla de ubicación que los otros visores. El bastón va en la mano derecha y el abanico en la izquierda; las ruedas en los dos tobillos y la piedra en el cinturón.</p>''')
start = s.index('  <section class="decisions">')
end = s.index('  </section>', start) + len('  </section>\n')
s = s[:start] + '''  <section class="decisions">
    <h2>Decisiones</h2>
    <p class="note">Te las pregunto en el chat, con opciones y mi recomendación.</p>
  </section>
''' + s[end:]

sys.path.insert(0, HERE)
from china_relics import RELICS  # noqa: E402
a = s.index("const RELICS = [")
b = s.index("];\nconst RARITY", a) + 2
s = s[:a] + "const RELICS = " + json.dumps(RELICS, ensure_ascii=False, indent=1) + ";" + s[b:]
rep('''  groups[id] = w.part === "arms" ? [wornGroup("right_arm", w.elements), wornGroup("left_arm", w.elements)] : [wornGroup(w.part, w.elements)];''',
    '''  groups[id] = w.part === "arms" ? [wornGroup("right_arm", w.elements), wornGroup("left_arm", w.elements)]
    : w.part === "legs" ? [wornGroup("right_leg", w.elements), wornGroup("left_leg", w.elements)] : [wornGroup(w.part, w.elements)];''')
rep('''groups.osiris_flail = heldIcon("osiris_flail", "right_arm");
groups.osiris_crook = heldIcon("osiris_crook", "left_arm");
const ORDER = ["ra_disk", "bastet_amulet", "sekhmet_claws", "osiris_flail", "osiris_crook"];''',
    '''groups.ruyi_jingu_bang = heldIcon("ruyi_jingu_bang", "right_arm", "handheld");
groups.banana_leaf_fan = heldIcon("banana_leaf_fan", "left_arm", "handheld");
const ORDER = ["ruyi_jingu_bang", "banana_leaf_fan", "wind_fire_wheels", "five_color_stone"];''')
rep("camera.position.set(1.5, 2.1, 3.3);", "camera.position.set(1.6, 1.6, 2.9);")
rep("controls.target.set(0, 1.4, 0);", "controls.target.set(0, 1.0, 0);")
# Suelo de piedra roja.
rep('''  const tones = ["#dbcfa3", "#e3d8af", "#d4c597", "#e9dfb8", "#cdbd8e"]; let seed = 11;''',
    '''  const tones = ["#8c3a2e", "#9a4434", "#7d3228", "#a34c3a", "#86362b"]; let seed = 11;''')
draft = open(os.path.join(HERE, "china_draft.json"), encoding="utf-8").read()
out = s.replace("__DRAFT__", draft)
open(os.path.join(HERE, "china-tanda-1.html"), "w", encoding="utf-8").write(out)
open(os.path.join(HERE, "check.js"), "w", encoding="utf-8").write(out[out.rindex("<script>") + 8:out.rindex("</script>")])
print("ok", len(out) // 1024, "KB")
