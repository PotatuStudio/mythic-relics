"""Arma la página de diseño de la tanda 3 nórdica a partir de template.html.
Uso: python norse3_draft.py norse3_draft.json && python norse3_build.py"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
s = open(os.path.join(HERE, "template.html"), encoding="utf-8").read()


def rep(a, b):
    global s
    assert a in s, a[:80]
    s = s.replace(a, b)


rep("<title>Egipto · Tanda 2</title>", "<title>Nórdicos · Tanda 3</title>")
# Paleta: noche de invierno, hierro y oro.
rep("""  --night: #15120f; --stone: #221d18; --stone-2: #2b241d; --line: #3a3128;
  --sand: #e8d3a2; --muted: #a8977a; --gold: #e9b949; --lapis: #8aa6f0; --sun: #f08a5a; --green: #8ad874;""",
    """  --night: #0d1014; --stone: #171c22; --stone-2: #1e252d; --line: #333d48;
  --sand: #e6e9ee; --muted: #9aa5b2; --gold: #e9b949; --lapis: #8ab4f0; --sun: #f08a5a; --green: #8ad874;""")
rep("scene.background = new THREE.Color(0x0c0a08);", "scene.background = new THREE.Color(0x0b0e12);")
rep("background: #0c0a08; }", "background: #0b0e12; }")
rep('''    <h1>Egipto · Tanda 2</h1>
    <p class="lead">Propuesta para revisar antes de programar. Seis reliquias con el mismo criterio que la tanda 1: el efecto nace del significado real del mito, con licencias de Minecraft que no lo contradigan. Los íconos y modelos son borradores.</p>
    <ul>
      <li><b>Arqueología:</b> la mayoría se encuentra <b>cepillando arena sospechosa</b> en pirámides y pozos del desierto. Así los cofres de las pirámides no se llenan más (ya traen alguna reliquia en el 57 % de los casos).</li>
      <li><b>Competencia por espacios:</b> Bastet compite con el Anj (collar) y Sekhmet con las vendas (guantes). Elegir es parte del juego.</li>
      <li><b>Probabilidades:</b> la arqueología se expresa por bloque cepillado; los cofres, por cofre y por pirámide (4 cofres).</li>
    </ul>''',
    '''    <h1>Nórdicos · Tanda 3</h1>
    <p class="lead">Seis reliquias más de los nórdicos con los efectos que revisamos en el chat: tres espadas famosas de las sagas, la espada de Freyr que pelea sola, el manto de halcón de Freyja y el dardo que mató a Baldr. Los íconos son borradores; la animación de la espada de Freyr va en una página aparte.</p>
    <ul>
      <li><b>Fuentes:</b> la Edda prosaica (Gylfaginning, Skáldskaparmál), la Edda poética (Skírnismál, Þrymskviða), la Saga de los Volsungos y la Saga de Hervör.</li>
      <li><b>Lo pop que se evita:</b> Stormbreaker y «solo el digno» (Marvel), el Hacha Leviatán y Draupnir como lanza (God of War), los cascos con cuernos.</li>
      <li><b>Maldición solo en Tyrfing:</b> en el mito Dáinsleif también debía matar al desenvainarse; aquí se diferencia por las heridas que no sanan.</li>
      <li><b>Avisos:</b> Gram avisa cuando apuntas a un mob grande con la estocada lista.</li>
      <li><b>Espacios:</b> la espada de Freyr va en el cinturón (compite con el escarabajo y Megingjörð); el manto, en la espalda (con la égida y las alas de Ícaro).</li>
    </ul>''')
rep('''    <p class="note">Mismo muñeco y misma regla de ubicación que el visor de la tanda 1. El cayado y el mayal se llevan en las manos.</p>''',
    '''    <p class="note">Mismo muñeco que los otros visores. La espada de Freyr cuelga envainada del costado izquierdo y el manto de halcón cae por la espalda; en la mano derecha, Gram, Tyrfing o Dáinsleif (una a la vez), y en la izquierda el dardo, de pie como un tridente y tomado por el medio.</p>''')
start = s.index('  <section class="decisions">')
end = s.index('  </section>', start) + len('  </section>\n')
s = s[:start] + '''  <section class="decisions">
    <h2>Decisiones</h2>
    <p class="note">Te las pregunto en el chat, con opciones y mi recomendación.</p>
  </section>
''' + s[end:]

sys.path.insert(0, HERE)
from norse3_relics import RELICS  # noqa: E402
a = s.index("const RELICS = [")
b = s.index("];\nconst RARITY", a) + 2
s = s[:a] + "const RELICS = " + json.dumps(RELICS, ensure_ascii=False, indent=1) + ";" + s[b:]
rep('''groups.osiris_flail = heldIcon("osiris_flail", "right_arm");
groups.osiris_crook = heldIcon("osiris_crook", "left_arm");
const ORDER = ["ra_disk", "bastet_amulet", "sekhmet_claws", "osiris_flail", "osiris_crook"];
const on = new Set(ORDER);''',
    '''groups.gram = heldIcon("gram", "right_arm", "handheld");
groups.tyrfing = heldIcon("tyrfing", "right_arm", "handheld");
groups.dainsleif = heldIcon("dainsleif", "right_arm", "handheld");
/* El dardo se lleva como el tridente de vanilla: de pie en la mano izquierda, tomado por el medio y con la punta hacia
   arriba (el ícono va en diagonal, así que se gira 45°). */
function dartHeld(id) {
  const holder = new THREE.Group(); holder.matrixAutoUpdate = false;
  holder.matrix.copy(new THREE.Matrix4().makeTranslation(6 / 16, 13 / 16, 2.6 / 16)
    .multiply(new THREE.Matrix4().makeRotationX(-0.15))
    .multiply(new THREE.Matrix4().makeRotationZ(Math.PI / 4))
    .multiply(new THREE.Matrix4().makeScale(0.85, 0.85, 1))
    .multiply(new THREE.Matrix4().makeTranslation(-0.5, -0.5, -0.5)));
  player.add(holder);
  const img = sprites[id].img;
  const build = () => {
    const c = document.createElement("canvas"); c.width = 16; c.height = 16;
    const g = c.getContext("2d"); g.drawImage(img, 0, 0, 16, 16, 0, 0, 16, 16);
    const data = g.getImageData(0, 0, 16, 16).data;
    const cells = [];
    for (let y = 0; y < 16; y++) for (let x = 0; x < 16; x++) if (data[(y * 16 + x) * 4 + 3] > 25) cells.push([x, y]);
    const mesh = new THREE.InstancedMesh(new THREE.BoxGeometry(1 / 16, 1 / 16, 1 / 16), new THREE.MeshLambertMaterial(), cells.length);
    const m = new THREE.Matrix4(), col = new THREE.Color();
    cells.forEach(([x, y], i) => {
      m.makeTranslation((x + 0.5) / 16, (15 - y + 0.5) / 16, 0.5); mesh.setMatrixAt(i, m);
      const k = (y * 16 + x) * 4;
      col.setRGB(data[k] / 255, data[k + 1] / 255, data[k + 2] / 255).convertSRGBToLinear(); mesh.setColorAt(i, col);
    });
    holder.add(mesh);
  };
  if (img.complete && img.naturalWidth) build(); else img.addEventListener("load", build);
  return [holder];
}
groups.mistletoe_dart = dartHeld("mistletoe_dart");
const ORDER = ["falcon_cloak", "freyr_sword", "gram", "tyrfing", "dainsleif", "mistletoe_dart"];
const on = new Set(["falcon_cloak", "freyr_sword", "gram"]);''')
rep("camera.position.set(1.5, 2.1, 3.3);", "camera.position.set(1.9, 1.8, 2.6);")
rep("controls.target.set(0, 1.4, 0);", "controls.target.set(0, 1.1, 0);")
# Suelo de nieve y piedra.
rep('''  const tones = ["#dbcfa3", "#e3d8af", "#d4c597", "#e9dfb8", "#cdbd8e"]; let seed = 11;''',
    '''  const tones = ["#eef2f6", "#e2e8ee", "#f6f8fb", "#d8dfe7", "#e8edf2"]; let seed = 11;''')
draft = open(os.path.join(HERE, "norse3_draft.json"), encoding="utf-8").read()
out = s.replace("__DRAFT__", draft)
open(os.path.join(HERE, "nordicos-tanda-3.html"), "w", encoding="utf-8").write(out)
open(os.path.join(HERE, "check.js"), "w", encoding="utf-8").write(out[out.rindex("<script>") + 8:out.rindex("</script>")])
print("ok", len(out) // 1024, "KB")
