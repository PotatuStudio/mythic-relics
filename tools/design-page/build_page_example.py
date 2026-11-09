"""Ejemplo: arma la página de diseño de la tanda 2 nórdica a partir de template.html (reemplaza textos de la plantilla,
mete las fichas de relics_example.py y el borrador de draft_example.py). Uso: python draft_example.py draft.json &&
python build_page_example.py"""
import json
import os

HERE = os.path.dirname(os.path.abspath(__file__))
SRC = os.path.join(HERE, "template.html")
s = open(SRC, encoding="utf-8").read()


def rep(a, b):
    global s
    assert a in s, a[:80]
    s = s.replace(a, b)


rep("<title>Egipto · Tanda 2</title>", "<title>Nórdicos · Tanda 2</title>")
# Paleta egea: noche azul, mármol y bronce.
rep("""  --night: #15120f; --stone: #221d18; --stone-2: #2b241d; --line: #3a3128;
  --sand: #e8d3a2; --muted: #a8977a; --gold: #e9b949; --lapis: #8aa6f0; --sun: #f08a5a; --green: #8ad874;""",
    """  --night: #0b1014; --stone: #141c22; --stone-2: #1a252d; --line: #2b3a45;
  --sand: #e6eef2; --muted: #93a8b6; --gold: #e8b64c; --lapis: #9fd0f0; --sun: #f08a5a; --green: #8ad874;""")
rep("scene.background = new THREE.Color(0x0c0a08);", "scene.background = new THREE.Color(0x0a0f13);")
rep("background: #0c0a08; }", "background: #0a0f13; }")
# Encabezado.
rep('''    <h1>Egipto · Tanda 2</h1>
    <p class="lead">Propuesta para revisar antes de programar. Seis reliquias con el mismo criterio que la tanda 1: el efecto nace del significado real del mito, con licencias de Minecraft que no lo contradigan. Los íconos y modelos son borradores.</p>
    <ul>
      <li><b>Arqueología:</b> la mayoría se encuentra <b>cepillando arena sospechosa</b> en pirámides y pozos del desierto. Así los cofres de las pirámides no se llenan más (ya traen alguna reliquia en el 57 % de los casos).</li>
      <li><b>Competencia por espacios:</b> Bastet compite con el Anj (collar) y Sekhmet con las vendas (guantes). Elegir es parte del juego.</li>
      <li><b>Probabilidades:</b> la arqueología se expresa por bloque cepillado; los cofres, por cofre y por pirámide (4 cofres).</li>
    </ul>''',
    '''    <h1>Nórdicos · Tanda 2</h1>
    <p class="lead">Propuesta para revisar antes de programar. Siete reliquias más de los dioses nórdicos, con el criterio «fiel al mito, con licencias». Los íconos y modelos son borradores.</p>
    <ul>
      <li><b>Las cinco anotadas:</b> Sleipnir, Huginn y Muninn, Brísingamen, Skíðblaðnir y la cabeza de Mímir.</li>
      <li><b>Dos más del mito:</b> Andvaranaut (el anillo maldito del oro, que arrastra la saga de los Volsungos) y Óðrerir (el hidromiel de la poesía).</li>
      <li><b>Sleipnir:</b> la herradura no se lleva puesta: llama a tu propio Sleipnir, un caballo de ocho patas que aparece, te lleva y vuelve a Asgard al bajarte.</li>
      <li><b>Espacios:</b> Brísingamen compite con los collares; Andvaranaut, con Draupnir y el anillo de los chacales.</li>
      <li><b>Riesgo:</b> Andvaranaut es la primera reliquia con una contra: da mucho oro pero está maldito.</li>
    </ul>''')
rep('''    <p class="note">Mismo muñeco y misma regla de ubicación que el visor de la tanda 1. El cayado y el mayal se llevan en las manos.</p>''',
    '''    <p class="note">Mismo muñeco y misma regla de ubicación que el visor de Egipto. Huginn y Muninn van en la mano derecha y la cabeza de Mímir en la izquierda; Brísingamen al cuello y Andvaranaut en la mano derecha.</p>''')
# Sin lista de decisiones en la página: se preguntan en el chat.
start = s.index('  <section class="decisions">')
end = s.index('  </section>', start) + len('  </section>\n')
s = s[:start] + '''  <section class="decisions">
    <h2>Decisiones</h2>
    <p class="note">Te las pregunto en el chat, con opciones y mi recomendación.</p>
  </section>
''' + s[end:]

import sys
sys.path.insert(0, HERE)
from relics_example import RELICS
a = s.index("const RELICS = [")
b = s.index("];\nconst RARITY", a) + 2
s = s[:a] + "const RELICS = " + json.dumps(RELICS, ensure_ascii=False, indent=1) + ";" + s[b:]
# 3D: sandalias en las dos piernas; cabeza de Medusa y lira en las manos.
rep('''  groups[id] = w.part === "arms" ? [wornGroup("right_arm", w.elements), wornGroup("left_arm", w.elements)] : [wornGroup(w.part, w.elements)];''',
    '''  groups[id] = w.part === "arms" ? [wornGroup("right_arm", w.elements), wornGroup("left_arm", w.elements)]
    : w.part === "legs" ? [wornGroup("right_leg", w.elements), wornGroup("left_leg", w.elements)] : [wornGroup(w.part, w.elements)];''')
rep('''groups.osiris_flail = heldIcon("osiris_flail", "right_arm");
groups.osiris_crook = heldIcon("osiris_crook", "left_arm");
const ORDER = ["ra_disk", "bastet_amulet", "sekhmet_claws", "osiris_flail", "osiris_crook"];''',
    '''groups.odin_ravens = heldIcon("odin_ravens", "right_arm");
groups.mimir_head = heldIcon("mimir_head", "left_arm");
const ORDER = ["brisingamen", "andvaranaut", "odin_ravens", "mimir_head"];''')
rep("camera.position.set(1.5, 2.1, 3.3);", "camera.position.set(1.6, 1.8, 2.8);")
rep("controls.target.set(0, 1.4, 0);", "controls.target.set(0, 1.2, 0);")
# Ladrillo de piedra en vez de arena.
rep('''  const tones = ["#dbcfa3", "#e3d8af", "#d4c597", "#e9dfb8", "#cdbd8e"]; let seed = 11;''',
    '''  const tones = ["#eef4f7", "#e2eaee", "#f7fbfc", "#d6e0e6", "#e9f0f3"]; let seed = 11;''')
draft = open(os.path.join(HERE, "draft.json"), encoding="utf-8").read()
out = s.replace("__DRAFT__", draft)
open(os.path.join(HERE, "nordicos-tanda-2.html"), "w", encoding="utf-8").write(out)
open(os.path.join(HERE, "check.js"), "w", encoding="utf-8").write(out[out.rindex("<script>") + 8:out.rindex("</script>")])
print("ok", len(out) // 1024, "KB")
