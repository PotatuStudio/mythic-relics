"""Arma la página de diseño de la tanda 2 japonesa a partir de template.html.
Uso: python japan2_draft.py japan2_draft.json && python japan2_build.py"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
s = open(os.path.join(HERE, "template.html"), encoding="utf-8").read()


def rep(a, b):
    global s
    assert a in s, a[:80]
    s = s.replace(a, b)


rep("<title>Egipto · Tanda 2</title>", "<title>Japón · Tanda 2</title>")
# Paleta: laca roja oscura, jade y oro.
rep("""  --night: #15120f; --stone: #221d18; --stone-2: #2b241d; --line: #3a3128;
  --sand: #e8d3a2; --muted: #a8977a; --gold: #e9b949; --lapis: #8aa6f0; --sun: #f08a5a; --green: #8ad874;""",
    """  --night: #0d0f1a; --stone: #171a2b; --stone-2: #1e2236; --line: #333a5a;
  --sand: #f4ece0; --muted: #a3a8c2; --gold: #f0c04a; --lapis: #9fb8f0; --sun: #e8452a; --green: #8ad874;""")
rep("scene.background = new THREE.Color(0x0c0a08);", "scene.background = new THREE.Color(0x0b0d16);")
rep("background: #0c0a08; }", "background: #0b0d16; }")
rep('''    <h1>Egipto · Tanda 2</h1>
    <p class="lead">Propuesta para revisar antes de programar. Seis reliquias con el mismo criterio que la tanda 1: el efecto nace del significado real del mito, con licencias de Minecraft que no lo contradigan. Los íconos y modelos son borradores.</p>
    <ul>
      <li><b>Arqueología:</b> la mayoría se encuentra <b>cepillando arena sospechosa</b> en pirámides y pozos del desierto. Así los cofres de las pirámides no se llenan más (ya traen alguna reliquia en el 57 % de los casos).</li>
      <li><b>Competencia por espacios:</b> Bastet compite con el Anj (collar) y Sekhmet con las vendas (guantes). Elegir es parte del juego.</li>
      <li><b>Probabilidades:</b> la arqueología se expresa por bloque cepillado; los cofres, por cofre y por pirámide (4 cofres).</li>
    </ul>''',
    '''    <h1>Japón · Tanda 2</h1>
    <p class="lead">Propuesta para revisar antes de programar. Nueve reliquias más de Japón, con el criterio «fiel al mito, con licencias». Los íconos y modelos son borradores.</p>
    <ul>
      <li><b>De dónde salen:</b> el Kojiki (Susanoo y Orochi, la lanza que creó las islas), Fūjin, y el folclore: los tengu, los kappa, los tanuki, Kintarō y el zashiki-warashi.</li>
      <li><b>Set de Fūjin y Raijin:</b> la bolsa de vientos en el inventario y el tambor de Raijin puesto: el rayo cae más seguido y salta a los mobs cercanos.</li>
      <li><b>Orochi es una licencia grande:</b> en el mito es el monstruo que vence Susanoo; aquí su escama invoca una sombra que pelea a tu lado. El aspecto sí es el del mito. Su modelo 3D se arma después.</li>
      <li><b>Tipos:</b> equipadas (máscara y plato), un arma-herramienta, objetos de uso con recarga, un bloque (la muñeca) y una invocadora.</li>
      <li><b>Espacios:</b> la máscara compite con la cara (Ojo de Horus, máscara de chacal, Ojo de Erlang); el plato, con los sombreros.</li>
    </ul>''')
rep('''    <p class="note">Mismo muñeco y misma regla de ubicación que el visor de la tanda 1. El cayado y el mayal se llevan en las manos.</p>''',
    '''    <p class="note">Mismo muñeco y misma regla de ubicación que los otros visores. El hacha de Kintarō va en la mano derecha y la lanza en la izquierda; la máscara de tengu en la cara y el plato del kappa en la coronilla.</p>''')
start = s.index('  <section class="decisions">')
end = s.index('  </section>', start) + len('  </section>\n')
s = s[:start] + '''  <section class="decisions">
    <h2>Decisiones</h2>
    <p class="note">Te las pregunto en el chat, con opciones y mi recomendación.</p>
  </section>
''' + s[end:]

sys.path.insert(0, HERE)
from japan2_relics import RELICS  # noqa: E402
a = s.index("const RELICS = [")
b = s.index("];\nconst RARITY", a) + 2
s = s[:a] + "const RELICS = " + json.dumps(RELICS, ensure_ascii=False, indent=1) + ";" + s[b:]
rep('''  groups[id] = w.part === "arms" ? [wornGroup("right_arm", w.elements), wornGroup("left_arm", w.elements)] : [wornGroup(w.part, w.elements)];''',
    '''  groups[id] = w.part === "arms" ? [wornGroup("right_arm", w.elements), wornGroup("left_arm", w.elements)]
    : w.part === "legs" ? [wornGroup("right_leg", w.elements), wornGroup("left_leg", w.elements)] : [wornGroup(w.part, w.elements)];''')
rep('''groups.osiris_flail = heldIcon("osiris_flail", "right_arm");
groups.osiris_crook = heldIcon("osiris_crook", "left_arm");
const ORDER = ["ra_disk", "bastet_amulet", "sekhmet_claws", "osiris_flail", "osiris_crook"];''',
    '''groups.kintaro_axe = heldIcon("kintaro_axe", "right_arm", "handheld");
groups.ame_no_nuboko = heldIcon("ame_no_nuboko", "left_arm", "handheld");
const ORDER = ["tengu_mask", "kappa_dish", "kintaro_axe", "ame_no_nuboko"];''')
rep("camera.position.set(1.5, 2.1, 3.3);", "camera.position.set(1.6, 1.8, 2.8);")
rep("controls.target.set(0, 1.4, 0);", "controls.target.set(0, 1.3, 0);")
# Suelo de piedra roja.
rep('''  const tones = ["#dbcfa3", "#e3d8af", "#d4c597", "#e9dfb8", "#cdbd8e"]; let seed = 11;''',
    '''  const tones = ["#6f7480", "#7a7f8b", "#666b77", "#82878f", "#5f646f"]; let seed = 11;''')
draft = open(os.path.join(HERE, "japan2_draft.json"), encoding="utf-8").read()
out = s.replace("__DRAFT__", draft)
open(os.path.join(HERE, "japon-tanda-2.html"), "w", encoding="utf-8").write(out)
open(os.path.join(HERE, "check.js"), "w", encoding="utf-8").write(out[out.rindex("<script>") + 8:out.rindex("</script>")])
print("ok", len(out) // 1024, "KB")
