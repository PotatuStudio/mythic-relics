"""Arma el visor de Pegaso y Talos (tanda 3 griega) a partir del visor de aliados chinos.
Uso: python ../model-gen/greece_entities.py greece3_entities.json && python greece3_entities_build.py"""
import os

HERE = os.path.dirname(os.path.abspath(__file__))
s = open(os.path.join(HERE, "china_entities_template.html"), encoding="utf-8").read()


def rep(a, b):
    global s
    assert a in s, a[:80]
    s = s.replace(a, b)


def cut(start, end, new):
    global s
    a = s.index(start)
    b = s.index(end, a)
    s = s[:a] + new + s[b:]


rep("<title>China · Aliados</title>", "<title>Grecia · Pegaso y Talos</title>")
rep("<h1>China · Aliados</h1>", "<h1>Grecia · Pegaso y Talos</h1>")
rep("""/* Visor de las dos entidades de la tanda 1 china: el mono guerrero y el long. La geometría es la del juego (partes y
   cubos de ModelPart, UV de caja) y las texturas son las que se usarían. */""",
    """/* Visor de las dos entidades de la tanda 3 griega: Pegaso y Talos. La geometría es la del juego (partes y cubos de
   ModelPart, UV de caja) y las texturas son las que se usarían. */""")
# Paleta: noche egea, mármol y oro.
rep("""  --night: #140b0a; --stone: #22110f; --stone-2: #2c1714; --line: #4a2620;
  --sand: #f3e2c8; --muted: #b8998a; --gold: #f0c04a; --jade: #7fd6c2;""",
    """  --night: #0c1118; --stone: #151d28; --stone-2: #1c2633; --line: #2e3c50;
  --sand: #ece6da; --muted: #9eabbd; --gold: #e9b949; --jade: #8ab4f0;""")
rep("background: #120a09; }", "background: #0a0f16; }")
rep("Los dos aliados de la tanda 1, con la geometría", "Pegaso y Talos, con la geometría")
rep("Las partes que brillan (ojos y perla) se ven más en la escena nocturna.",
    "Las partes que brillan (la brida de oro, los ojos de Talos y su vena de ícor) se ven más de noche. «Al rojo» muestra a Talos cuando abraza.")
rep('''      <button class="btn" data-s="monkey" aria-pressed="true">Mono guerrero</button>
      <button class="btn" data-s="monkeys" aria-pressed="false">Los cuatro monos</button>
      <button class="btn" data-s="dragon" aria-pressed="false">Long</button>
      <button class="btn" data-s="rider" aria-pressed="false">Long con jinete</button>''',
    '''      <button class="btn" data-s="pegasus" aria-pressed="true">Pegaso</button>
      <button class="btn" data-s="flying" aria-pressed="false">Pegaso volando</button>
      <button class="btn" data-s="rider" aria-pressed="false">Pegaso con jinete</button>
      <button class="btn" data-s="talos" aria-pressed="false">Talos</button>
      <button class="btn" data-s="guard" aria-pressed="false">Talos junto al jugador</button>''')
rep('<button class="btn" id="puff" aria-pressed="false">Aparecer</button>',
    '<button class="btn" id="hot" aria-pressed="false">Al rojo</button>\n      <button class="btn" id="puff" aria-pressed="false">Aparecer</button>')
cut('  <div class="cards">', '</div>\n\n<script', '''  <div class="cards">
    <article class="card">
      <h2>Pegaso</h2>
      <p class="note">Lo llama la Brida de oro de Atenea: montura propia que vuela; en picada embiste a 12 de daño. Si subes sobre la altura 200, el tábano de Zeus lo pica.</p>
      <div class="fid">
        <h3>Aspecto</h3>
        <ul>
          <li>Caballo blanco con grandes alas de plumas que nacen en la cruz, levantadas como en las monedas de Corinto.</li>
          <li>Crin y cola plateadas, cascos grises.</li>
          <li>La brida de oro de Atenea en la cabeza: frontalera, muserola y anillas del bocado. Brilla un poco, porque es un regalo divino.</li>
          <li>Manta roja con greca de oro en vez de silla.</li>
        </ul>
        <h3>Fidelidad</h3>
        <p><b>Fiel:</b> el caballo alado blanco nacido del cuello de Medusa y la brida de oro que Atenea le dio a Belerofonte (Píndaro, Olímpica 13).</p>
        <p><b>Licencia:</b> la manta (Belerofonte lo montaba a pelo; es para que se vea montable) y que la brida lo invoque.</p>
        <p><b>Pop:</b> nada. No es el Pegaso de Disney.</p>
      </div>
    </article>
    <article class="card">
      <h2>Talos</h2>
      <p class="note">Lo llama el Clavo de bronce: 90 s a tu lado, da vueltas a tu alrededor, lanza rocas (10 de daño) y abraza al rojo (8 de daño y fuego). Por la espalda recibe el doble.</p>
      <div class="fid">
        <h3>Aspecto</h3>
        <ul>
          <li>Hombre de bronce de unos 3 bloques, con pelo y barba de rizos como un bronce griego.</li>
          <li>Ojos de horno que brillan y pátina verde en las partes bajas.</li>
          <li>La única vena de ícor, dorada, baja del cuello al tobillo derecho (brilla).</li>
          <li>El clavo de bronce en el tobillo derecho: su punto débil.</li>
          <li>Una roca en la mano derecha, lista para lanzar.</li>
        </ul>
        <h3>Fidelidad</h3>
        <p><b>Fiel:</b> el guardián de bronce de Creta que hizo Hefesto: tira rocas, se pone al rojo para abrazar y tiene una sola vena cerrada con un clavo en el tobillo (Apolonio de Rodas, Apolodoro; la vasija de Ruvo, c. 400 a. C.).</p>
        <p><b>Licencia:</b> el faldellín de bronce (en la vasija está desnudo) y que lo invoque un clavo.</p>
        <p><b>Pop:</b> nada. No es el coloso gigante de Jasón y los argonautas (1963).</p>
      </div>
    </article>
  </div>
''')
rep("scene.background = new THREE.Color(0x120a09);", "scene.background = new THREE.Color(0x0a0f16);")
# Luz fría del cielo egeo (la de la plantilla china era rojiza y teñía de marrón el blanco de las alas).
rep("new THREE.HemisphereLight(0xfff2e0, 0x40201a, 0.85)", "new THREE.HemisphereLight(0xf6f8ff, 0x6a7488, 0.85)")
rep('const tones = ["#8c3a2e", "#9a4434", "#7d3228", "#a34c3a", "#86362b"]; let seed = 11;',
    'const tones = ["#e8e4dc", "#dedad1", "#f0ece4", "#d6d1c6", "#e2ddd3"]; let seed = 11;')
rep("/* Suelo de piedra roja procedural. */", "/* Suelo de mármol procedural. */")
cut("const monkeyMat = material(DATA.monkey)", "/* Jinete:", '''const pegasusMat = material(DATA.pegasus), talosMat = material(DATA.talos);
const pegasus = build(DATA.pegasus, pegasusMat);
const talos = build(DATA.talos, talosMat);
talos.entity.scale.setScalar(DATA.talos.scale);
const talosColor = talosMat.color.clone();

''')
rep("/* Jinete: muñeco del jugador sentado sobre el segundo segmento. */",
    "/* Jinete: muñeco del jugador sentado sobre la manta; el mismo muñeco hace de jugador junto a Talos. */")
rep("dragon.parts.seg1.add(rider);\nrider.scale.set(-16, -16, 16); rider.rotation.set(0, 0, 0); rider.position.set(0, -2.5, 6);",
    "function seatRider(on) {\n  if (on) { pegasus.parts.body.add(rider); rider.scale.set(-16, -16, 16); rider.rotation.set(0, Math.PI, 0); rider.position.set(0, -3, 1);"
    "\n    legR.rotation.set(1.25, 0, 0); legL.rotation.set(1.25, 0, 0); legR.position.set(-6.5 / 16, 1 / 16, -1 / 16); legL.position.set(6.5 / 16, 1 / 16, -1 / 16); legR.scale.set(1, 1, 1); legL.scale.set(1, 1, 1); }"
    "\n  else { scene.add(rider); rider.scale.set(1, 1, 1); rider.rotation.set(0, -0.6, 0); rider.position.set(1.6, 0, 1.2);"
    "\n    legR.rotation.set(0, 0, 0); legL.rotation.set(0, 0, 0); legR.position.set(-2 / 16, 6 / 16, 0); legL.position.set(2 / 16, 6 / 16, 0); legR.scale.set(1, 3, 0.34); legL.scale.set(1, 3, 0.34); }\n}")
cut('let subject = "monkey", animate = true;', 'const clock = new THREE.Clock();', '''let subject = "pegasus", animate = true, hot = false;
const CAMS = {
  pegasus: { cam: [3.4, 2.2, 3.6], target: [0, 1.0, 0] },
  flying: { cam: [4.2, 3.0, 4.4], target: [0, 2.6, 0] },
  rider: { cam: [3.6, 2.6, 3.8], target: [0, 1.3, 0] },
  talos: { cam: [4.0, 2.8, 4.6], target: [0, 1.5, 0] },
  guard: { cam: [6.0, 3.6, 6.4], target: [0, 1.4, 0] },
};
function show(s) {
  subject = s;
  document.querySelectorAll("#subjects .btn").forEach(b => b.setAttribute("aria-pressed", String(b.dataset.s === s)));
  pegasus.entity.visible = s === "pegasus" || s === "flying" || s === "rider";
  talos.entity.visible = s === "talos" || s === "guard";
  rider.visible = s === "rider" || s === "guard";
  seatRider(s === "rider");
  pegasus.entity.position.set(0, 0, 0); talos.entity.position.set(0, 0, 0); talos.entity.rotation.y = Math.PI;
  const c = CAMS[s]; camera.position.set(...c.cam); controls.target.set(...c.target);
}
document.querySelectorAll("#subjects .btn").forEach(b => b.addEventListener("click", () => show(b.dataset.s)));
document.getElementById("anim").addEventListener("click", e => { animate = !animate; e.target.setAttribute("aria-pressed", String(animate)); });
document.getElementById("night").addEventListener("click", e => {
  const night = e.target.getAttribute("aria-pressed") !== "true"; e.target.setAttribute("aria-pressed", String(night));
  hemi.intensity = night ? 0.18 : 0.85; sun.intensity = night ? 0.1 : 0.75;
  scene.background = new THREE.Color(night ? 0x05070d : 0x0a0f16);
});
document.getElementById("hot").addEventListener("click", e => {
  hot = !hot; e.target.setAttribute("aria-pressed", String(hot));
  talosMat.color.copy(hot ? new THREE.Color(1.0, 0.45, 0.25) : talosColor);
  talosMat.emissive.copy(hot ? new THREE.Color(1.0, 0.55, 0.3) : new THREE.Color(0xffffff));
  talosMat.emissiveIntensity = 1;
});
document.getElementById("puff").addEventListener("click", () => {
  const at = (subject === "talos" || subject === "guard") ? talos.entity.position : pegasus.entity.position;
  puff(new THREE.Vector3(at.x, 0.4, at.z), 30, 2);
});
show("pegasus");

''')
cut("function animateMonkey(m, t, phase) {", "function resize()", '''function animatePegasus(t) {
  const P = pegasus.parts, k = animate ? 1 : 0, fly = subject === "flying";
  // Alas: en el suelo se mecen; volando aletean.
  const flap = fly ? Math.sin(t * 5) * 0.7 * k : Math.sin(t * 1.2) * 0.08 * k;
  setRot(P.wing_r, 0, 0, flap); setRot(P.wing_l, 0, 0, -flap);
  setRot(P.wing_r2, 0, 0, fly ? Math.sin(t * 5 - 0.6) * 0.4 * k : 0); setRot(P.wing_l2, 0, 0, fly ? -Math.sin(t * 5 - 0.6) * 0.4 * k : 0);
  // Patas: quieto respira; volando las recoge.
  for (const [n, ph] of [["leg_fr", 0], ["leg_fl", Math.PI], ["leg_br", Math.PI], ["leg_bl", 0]]) {
    setRot(P[n], fly ? (n.startsWith("leg_f") ? -0.9 : 0.8) : Math.sin(t * 1.5 + ph) * 0.04 * k);
  }
  setRot(P.neck, Math.sin(t * 1.4) * 0.05 * k);
  setRot(P.head, Math.sin(t * 1.4 - 0.5) * 0.06 * k);
  setRot(P.tail, Math.sin(t * 1.1) * 0.08 * k, Math.sin(t * 0.9) * 0.2 * k);
  setRot(P.tail2, Math.sin(t * 1.1 - 0.6) * 0.15 * k);
  pegasus.entity.position.y = fly ? 2.2 + Math.sin(t * 5) * 0.12 * k : 0;
}
function animateTalos(t) {
  const P = talos.parts, k = animate ? 1 : 0, guard = subject === "guard";
  // Junto al jugador: da vueltas a su alrededor, como alrededor de Creta.
  if (guard && animate) {
    const a = t * 0.35, r = 3.4;
    talos.entity.position.set(1.6 + Math.cos(a) * r, 0, 1.2 + Math.sin(a) * r);
    talos.entity.rotation.y = -a;
  }
  const walk = guard ? Math.sin(t * 3) * 0.45 * k : 0;
  setRot(P.leg_r, walk); setRot(P.leg_l, -walk);
  // Brazo derecho: cada 4 s levanta la roca y la lanza.
  const c = (t % 4) / 4, throwing = c > 0.7;
  setRot(P.arm_r, throwing ? -2.6 + (c - 0.7) / 0.3 * 2.2 : -walk * 0.8 - 0.15 * Math.sin(t) * k);
  P.rock.visible = !(c > 0.9);
  setRot(P.arm_l, walk * 0.8);
  setRot(P.head, 0, Math.sin(t * 0.7) * 0.25 * k);
}
''')
rep('''  monkeys.forEach((m, i) => animateMonkey(m, t, i * 1.3));
  animateDragon(t);''', '''  animatePegasus(t);
  animateTalos(t);''')
data = open(os.path.join(HERE, "greece3_entities.json"), encoding="utf-8").read()
out = s.replace("__DATA__", data)
open(os.path.join(HERE, "grecia-criaturas.html"), "w", encoding="utf-8").write(out)
open(os.path.join(HERE, "check.js"), "w", encoding="utf-8").write(out[out.rindex("<script>") + 8:out.rindex("</script>")])
print("ok", len(out) // 1024, "KB")
