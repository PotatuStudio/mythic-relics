"""Arma la página de diseño de la tanda 3 griega a partir de template.html.
Uso: python greece3_draft.py greece3_draft.json && python greece3_build.py"""
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
s = open(os.path.join(HERE, "template.html"), encoding="utf-8").read()


def rep(a, b):
    global s
    assert a in s, a[:80]
    s = s.replace(a, b)


rep("<title>Egipto · Tanda 2</title>", "<title>Grecia · Tanda 3</title>")
# Paleta: mármol de noche, azul egeo y oro.
rep("""  --night: #15120f; --stone: #221d18; --stone-2: #2b241d; --line: #3a3128;
  --sand: #e8d3a2; --muted: #a8977a; --gold: #e9b949; --lapis: #8aa6f0; --sun: #f08a5a; --green: #8ad874;""",
    """  --night: #0c1118; --stone: #151d28; --stone-2: #1c2633; --line: #2e3c50;
  --sand: #ece6da; --muted: #9eabbd; --gold: #e9b949; --lapis: #8ab4f0; --sun: #f08a5a; --green: #8ad874;""")
rep("scene.background = new THREE.Color(0x0c0a08);", "scene.background = new THREE.Color(0x0a0f16);")
rep("background: #0c0a08; }", "background: #0a0f16; }")
rep('''    <h1>Egipto · Tanda 2</h1>
    <p class="lead">Propuesta para revisar antes de programar. Seis reliquias con el mismo criterio que la tanda 1: el efecto nace del significado real del mito, con licencias de Minecraft que no lo contradigan. Los íconos y modelos son borradores.</p>
    <ul>
      <li><b>Arqueología:</b> la mayoría se encuentra <b>cepillando arena sospechosa</b> en pirámides y pozos del desierto. Así los cofres de las pirámides no se llenan más (ya traen alguna reliquia en el 57 % de los casos).</li>
      <li><b>Competencia por espacios:</b> Bastet compite con el Anj (collar) y Sekhmet con las vendas (guantes). Elegir es parte del juego.</li>
      <li><b>Probabilidades:</b> la arqueología se expresa por bloque cepillado; los cofres, por cofre y por pirámide (4 cofres).</li>
    </ul>''',
    '''    <h1>Grecia · Tanda 3</h1>
    <p class="lead">Lo más conocido de Grecia que sí es mito: siete reliquias con los efectos que revisamos en el chat. Los íconos son borradores; los modelos 3D de Pegaso y Talos van en una página aparte.</p>
    <ul>
      <li><b>Fuentes:</b> Hesíodo (Teogonía), Homero (Ilíada XVIII), Apolodoro, Píndaro (Olímpica 13), Apolonio de Rodas y los Cantos ciprios.</li>
      <li><b>Lo pop que se evita:</b> las Espadas del Caos y el Cesto de Nemea (God of War), el Kraken griego (Furia de titanes), el bronce celestial (Percy Jackson) y el Pegaso de Disney.</li>
      <li><b>Armas con recarga corta:</b> el rayo recarga en 5 s porque es un arma a distancia de uso continuo.</li>
      <li><b>Avisos:</b> la harpe avisa cuando un mob ya se puede decapitar; tú decides si la usas.</li>
      <li><b>Set de Perseo:</b> suma la harpe (4 piezas) y se activa con 3.</li>
      <li><b>Espacios:</b> la piel de Nemea compite con el vellocino (capa); el escudo, con la Ancile y los otros de la mano secundaria.</li>
    </ul>''')
rep('''    <p class="note">Mismo muñeco y misma regla de ubicación que el visor de la tanda 1. El cayado y el mayal se llevan en las manos.</p>''',
    '''    <p class="note">Mismo muñeco que los otros visores. La piel de Nemea cae por la espalda con las patas anudadas en el pecho; el rayo (o la harpe) en la mano derecha y el escudo embrazado en el brazo izquierdo, de pie y mirando al frente.</p>''')
start = s.index('  <section class="decisions">')
end = s.index('  </section>', start) + len('  </section>\n')
s = s[:start] + '''  <section class="decisions">
    <h2>Decisiones</h2>
    <p class="note">Te las pregunto en el chat, con opciones y mi recomendación.</p>
  </section>
''' + s[end:]

sys.path.insert(0, HERE)
from greece3_relics import RELICS  # noqa: E402
a = s.index("const RELICS = [")
b = s.index("];\nconst RARITY", a) + 2
s = s[:a] + "const RELICS = " + json.dumps(RELICS, ensure_ascii=False, indent=1) + ";" + s[b:]
rep('''groups.osiris_flail = heldIcon("osiris_flail", "right_arm");
groups.osiris_crook = heldIcon("osiris_crook", "left_arm");
const ORDER = ["ra_disk", "bastet_amulet", "sekhmet_claws", "osiris_flail", "osiris_crook"];
const on = new Set(ORDER);''',
    '''groups.zeus_thunderbolt = heldIcon("zeus_thunderbolt", "right_arm", "handheld");
groups.harpe = heldIcon("harpe", "right_arm", "handheld");
/* El escudo no se lleva como un ítem plano: se embraza delante del antebrazo izquierdo, de pie y mirando al frente
   (un poco hacia afuera), como el escudo de vanilla al bloquear. Es el ícono extruido, de 14 px de diámetro y 2 px de
   grosor. */
function shieldHeld(id) {
  const holder = new THREE.Group(); holder.matrixAutoUpdate = false;
  holder.matrix.copy(new THREE.Matrix4().makeTranslation(7.5 / 16, 15 / 16, 3.6 / 16)
    .multiply(new THREE.Matrix4().makeRotationY(0.35))
    .multiply(new THREE.Matrix4().makeScale(14 / 16, 14 / 16, 2))
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
      m.makeTranslation((x + 0.5) / 16, (15 - y + 0.5) / 16, 0.5);
      mesh.setMatrixAt(i, m);
      const k = (y * 16 + x) * 4;
      col.setRGB(data[k] / 255, data[k + 1] / 255, data[k + 2] / 255).convertSRGBToLinear();
      mesh.setColorAt(i, col);
    });
    holder.add(mesh);
  };
  if (img.complete && img.naturalWidth) build(); else img.addEventListener("load", build);
  return [holder];
}
groups.achilles_shield = shieldHeld("achilles_shield");
const ORDER = ["nemean_lion_skin", "zeus_thunderbolt", "harpe", "achilles_shield"];
const on = new Set(ORDER.filter(id => id !== "harpe"));''')
rep("camera.position.set(1.5, 2.1, 3.3);", "camera.position.set(1.6, 1.8, 2.8);")
rep("controls.target.set(0, 1.4, 0);", "controls.target.set(0, 1.2, 0);")
# Suelo de mármol.
rep('''  const tones = ["#dbcfa3", "#e3d8af", "#d4c597", "#e9dfb8", "#cdbd8e"]; let seed = 11;''',
    '''  const tones = ["#e8e4dc", "#dedad1", "#f0ece4", "#d6d1c6", "#e2ddd3"]; let seed = 11;''')
draft = open(os.path.join(HERE, "greece3_draft.json"), encoding="utf-8").read()
out = s.replace("__DRAFT__", draft)
open(os.path.join(HERE, "grecia-tanda-3.html"), "w", encoding="utf-8").write(out)
open(os.path.join(HERE, "check.js"), "w", encoding="utf-8").write(out[out.rindex("<script>") + 8:out.rindex("</script>")])
print("ok", len(out) // 1024, "KB")
