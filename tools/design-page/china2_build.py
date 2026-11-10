"""Arma la página de diseño de la tanda 2 china a partir de template.html.
Uso: python china2_draft.py china2_draft.json && python china2_build.py"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
s = open(os.path.join(HERE, "template.html"), encoding="utf-8").read()


def rep(a, b):
    global s
    assert a in s, a[:80]
    s = s.replace(a, b)


rep("<title>Egipto · Tanda 2</title>", "<title>China · Tanda 2</title>")
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
    '''    <h1>China · Tanda 2</h1>
    <p class="lead">Propuesta para revisar antes de programar. Nueve reliquias más de los mitos chinos, con el criterio «fiel al mito, con licencias». Los íconos y modelos son borradores.</p>
    <ul>
      <li><b>De dónde salen:</b> La investidura de los dioses (Nezha y Erlang Shen), el Clásico de los montes y los mares (el qilin, el pixiu), el mito del Diluvio (Yu el Grande y el xirang), Viaje al Oeste (los melocotones) y el folclore taoísta (los talismanes).</li>
      <li><b>No todo se equipa:</b> hay armas, objetos que se usan con recarga, una montura que se llama con un objeto (el qilin, como Sleipnir) y una rama cuyo fruto se come y vuelve a crecer (como las manzanas de Iðunn, pero un solo fruto más fuerte).</li>
      <li><b>Set de Erlang Shen:</b> con el Ojo puesto y la alabarda en la mano, el Perro Celestial aparece cuando te atacan.</li>
      <li><b>Dos entidades nuevas:</b> el Perro Celestial y el qilin. Sus modelos 3D se arman después, con su propio visor.</li>
      <li><b>Espacios:</b> el Ojo compite con la máscara y el Ojo de Horus (cara); la pulsera, con los otros anillos.</li>
    </ul>''')
rep('''    <p class="note">Mismo muñeco y misma regla de ubicación que el visor de la tanda 1. El cayado y el mayal se llevan en las manos.</p>''',
    '''    <p class="note">Mismo muñeco y misma regla de ubicación que los otros visores. La alabarda va en la mano derecha y la lanza en la izquierda; el Ojo de Erlang en la frente y la pulsera en la muñeca derecha.</p>''')
start = s.index('  <section class="decisions">')
end = s.index('  </section>', start) + len('  </section>\n')
s = s[:start] + '''  <section class="decisions">
    <h2>Decisiones</h2>
    <p class="note">Te las pregunto en el chat, con opciones y mi recomendación.</p>
  </section>
''' + s[end:]

sys.path.insert(0, HERE)
from china2_relics import RELICS  # noqa: E402
a = s.index("const RELICS = [")
b = s.index("];\nconst RARITY", a) + 2
s = s[:a] + "const RELICS = " + json.dumps(RELICS, ensure_ascii=False, indent=1) + ";" + s[b:]
rep('''  groups[id] = w.part === "arms" ? [wornGroup("right_arm", w.elements), wornGroup("left_arm", w.elements)] : [wornGroup(w.part, w.elements)];''',
    '''  groups[id] = w.part === "arms" ? [wornGroup("right_arm", w.elements), wornGroup("left_arm", w.elements)]
    : w.part === "legs" ? [wornGroup("right_leg", w.elements), wornGroup("left_leg", w.elements)] : [wornGroup(w.part, w.elements)];''')
rep('''groups.osiris_flail = heldIcon("osiris_flail", "right_arm");
groups.osiris_crook = heldIcon("osiris_crook", "left_arm");
const ORDER = ["ra_disk", "bastet_amulet", "sekhmet_claws", "osiris_flail", "osiris_crook"];''',
    '''groups.erlang_halberd = heldIcon("erlang_halberd", "right_arm", "handheld");
groups.fire_tipped_spear = heldIcon("fire_tipped_spear", "left_arm", "handheld");
const ORDER = ["erlang_eye", "pixiu_bracelet", "erlang_halberd", "fire_tipped_spear"];''')
rep("camera.position.set(1.5, 2.1, 3.3);", "camera.position.set(1.6, 1.8, 2.8);")
rep("controls.target.set(0, 1.4, 0);", "controls.target.set(0, 1.3, 0);")
# Suelo de piedra roja.
rep('''  const tones = ["#dbcfa3", "#e3d8af", "#d4c597", "#e9dfb8", "#cdbd8e"]; let seed = 11;''',
    '''  const tones = ["#8c3a2e", "#9a4434", "#7d3228", "#a34c3a", "#86362b"]; let seed = 11;''')
draft = open(os.path.join(HERE, "china2_draft.json"), encoding="utf-8").read()
out = s.replace("__DRAFT__", draft)
open(os.path.join(HERE, "china-tanda-2.html"), "w", encoding="utf-8").write(out)
open(os.path.join(HERE, "check.js"), "w", encoding="utf-8").write(out[out.rindex("<script>") + 8:out.rindex("</script>")])
print("ok", len(out) // 1024, "KB")
