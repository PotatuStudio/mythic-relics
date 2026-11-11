"""Arma el visor de las criaturas de la tanda 1 japonesa (kitsune y tortuga de Urashima) a partir del visor de la tanda 1.
Uso: python ../model-gen/japan_entities.py japan_entities.json && python japan_entities_build.py"""
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


rep("<title>China · Aliados</title>", "<title>Japón · Criaturas</title>")
rep("<h1>China · Aliados</h1>", "<h1>Japón · Criaturas</h1>")
rep("Los dos aliados de la tanda 1, con la geometría", "Las dos criaturas de la tanda 1 japonesa, con la geometría")
rep("Las partes que brillan (ojos y perla) se ven más en la escena nocturna.",
    "Las partes que brillan (ojos y llamas) se ven más en la escena nocturna.")
rep('''      <button class="btn" data-s="monkey" aria-pressed="true">Mono guerrero</button>
      <button class="btn" data-s="monkeys" aria-pressed="false">Los cuatro monos</button>
      <button class="btn" data-s="dragon" aria-pressed="false">Long</button>
      <button class="btn" data-s="rider" aria-pressed="false">Long con jinete</button>''',
    '''      <button class="btn" data-s="dog" aria-pressed="true">Kitsune</button>
      <button class="btn" data-s="qilin" aria-pressed="false">Tortuga de Urashima</button>
      <button class="btn" data-s="rider" aria-pressed="false">Tortuga con jinete</button>''')
cut('  <div class="cards">', '</div>\n\n<script', '''  <div class="cards">
    <article class="card">
      <h2>Kitsune de nueve colas</h2>
      <p class="note">Lo llama la Esfera estelar del kitsune: pelea a tu lado 60 s y lanza kitsunebi.</p>
      <div class="fid">
        <h3>Aspecto</h3>
        <ul>
          <li>Zorro blanco con ojos dorados que brillan y las marcas rojas de los zorros de Inari en la cara y las orejas.</li>
          <li>Babero rojo, como el de las estatuas de zorro en los santuarios de Inari.</li>
          <li>Nueve colas en abanico, con la punta convertida en llama celeste que brilla.</li>
          <li>Dos kitsunebi, fuegos fatuos azules, que giran a su alrededor.</li>
        </ul>
        <h3>Fidelidad</h3>
        <p><b>Fiel:</b> el kitsune de nueve colas (kyūbi), el blanco de los zorros mensajeros de Inari, sus marcas rojas, el babero de las estatuas y los kitsunebi.</p>
        <p><b>Licencia:</b> que la esfera lo llame como aliado.</p>
        <p><b>Pop:</b> nada. No es el Kurama de Naruto ni el Ninetales de Pokémon.</p>
      </div>
    </article>
    <article class="card">
      <h2>Tortuga de Urashima</h2>
      <p class="note">La llama la caña de Urashima: montura submarina; si muere, vuelve en 5 min.</p>
      <div class="fid">
        <h3>Aspecto</h3>
        <ul>
          <li>Tortuga marina gigante, de unos dos bloques de largo.</li>
          <li>Caparazón abombado verde oscuro con escudos de borde ámbar y plastrón crema.</li>
          <li>Aletas grandes que reman y piel oliva con manchas claras.</li>
          <li>Algas que cuelgan del caparazón como una cola: la minogame, la tortuga de mil años.</li>
        </ul>
        <h3>Fidelidad</h3>
        <p><b>Fiel:</b> la tortuga que lleva a Urashima al Ryūgū-jō y la minogame de las pinturas, que simboliza la larga vida.</p>
        <p><b>Licencia:</b> que la llame una caña y que se monte cuando quieras.</p>
        <p><b>Pop:</b> nada.</p>
      </div>
    </article>
  </div>
''')
cut("const monkeyMat = material(DATA.monkey)", "/* Jinete:", '''const dogMat = material(DATA.kitsune), qilinMat = material(DATA.turtle);
const dog = build(DATA.kitsune, dogMat);
dog.entity.scale.setScalar(DATA.kitsune.scale);
const qilin = build(DATA.turtle, qilinMat);
qilin.entity.scale.setScalar(DATA.turtle.scale);

''')
rep("dragon.parts.seg1.add(rider);\nrider.scale.set(-16, -16, 16); rider.rotation.set(0, 0, 0); rider.position.set(0, -2.5, 6);",
    "qilin.parts.body.add(rider);\nrider.scale.set(-16 / DATA.turtle.scale, -16 / DATA.turtle.scale, 16 / DATA.turtle.scale); rider.rotation.set(0, 0, 0); rider.position.set(0, -8, 0);")
rep("/* Jinete: muñeco del jugador sentado sobre el segundo segmento. */", "/* Jinete: muñeco del jugador sentado en la silla. */")
cut('let subject = "monkey", animate = true;', 'const clock = new THREE.Clock();', '''let subject = "dog", animate = true;
const CAMS = {
  dog: { cam: [2.4, 1.6, 3.0], target: [0, 0.9, 0] },
  qilin: { cam: [3.6, 2.2, 4.0], target: [0, 0.7, 0] },
  rider: { cam: [4.0, 2.8, 4.4], target: [0, 1.4, 0] },
};
function show(s) {
  subject = s;
  document.querySelectorAll("#subjects .btn").forEach(b => b.setAttribute("aria-pressed", String(b.dataset.s === s)));
  dog.entity.visible = s === "dog";
  qilin.entity.visible = s === "qilin" || s === "rider";
  rider.visible = s === "rider";
  const c = CAMS[s]; camera.position.set(...c.cam); controls.target.set(...c.target);
}
document.querySelectorAll("#subjects .btn").forEach(b => b.addEventListener("click", () => show(b.dataset.s)));
document.getElementById("anim").addEventListener("click", e => { animate = !animate; e.target.setAttribute("aria-pressed", String(animate)); });
document.getElementById("night").addEventListener("click", e => {
  const night = e.target.getAttribute("aria-pressed") !== "true"; e.target.setAttribute("aria-pressed", String(night));
  hemi.intensity = night ? 0.18 : 0.85; sun.intensity = night ? 0.1 : 0.75;
  scene.background = new THREE.Color(night ? 0x05070d : 0x120a09);
});
document.getElementById("puff").addEventListener("click", () => puff(new THREE.Vector3(0, 0.3, 0), subject === "dog" ? 16 : 30, 1.5));
show("dog");

''')
cut("function animateMonkey(m, t, phase) {", "function resize()", '''function animateDog(t) {
  const P = dog.parts, w = animate ? 0.9 : 0, a = t * 8;
  setRot(P.leg_fr, Math.cos(a) * w); setRot(P.leg_hl, Math.cos(a) * w);
  setRot(P.leg_fl, -Math.cos(a) * w); setRot(P.leg_hr, -Math.cos(a) * w);
  for (let i = 0; i < 9; i++) {
    setRot(P["tail" + i], Math.sin(t * 1.5 + i * 0.7) * 0.08, Math.sin(t * 1.2 + i * 0.5) * 0.12);
    setRot(P["tail" + i + "_tip"], Math.sin(t * 1.5 + i * 0.7 - 0.6) * 0.15);
  }
  setRot(P.head, Math.sin(t * 0.8) * 0.06, Math.sin(t * 0.5) * 0.25);
  P.foxfire.rotation.y = t * 1.6;
  P.foxfire_a.position.y = Math.sin(t * 2.0) * 1.5; P.foxfire_b.position.y = -2 + Math.cos(t * 2.0) * 1.5;
  dog.entity.position.y = animate ? Math.abs(Math.sin(a)) * 0.04 : 0;
}
function animateQilin(t) {
  const P = qilin.parts, k = animate ? 1 : 0.3, a = t * 2.2;
  setRot(P.flipper_fr, 0, Math.sin(a) * 0.45 * k, Math.cos(a) * 0.35 * k);
  setRot(P.flipper_fl, 0, -Math.sin(a) * 0.45 * k, -Math.cos(a) * 0.35 * k);
  setRot(P.flipper_hr, 0, Math.sin(a + 1.6) * 0.3 * k);
  setRot(P.flipper_hl, 0, -Math.sin(a + 1.6) * 0.3 * k);
  setRot(P.neck, Math.sin(t * 0.9) * 0.08, Math.sin(t * 0.5) * 0.15);
  for (let k = 0; k < 3; k++) setRot(P["seaweed" + k], Math.sin(t * 1.3 + k) * 0.15, Math.sin(t * 0.9 + k) * 0.2);
  qilin.entity.position.y = animate ? 0.1 + Math.sin(t * 1.1) * 0.06 : 0;
}
''')
rep('''  monkeys.forEach((m, i) => animateMonkey(m, t, i * 1.3));
  animateDragon(t);''', '''  animateDog(t);
  animateQilin(t);''')
data = open(os.path.join(HERE, "japan_entities.json"), encoding="utf-8").read()
open(os.path.join(HERE, "japon-criaturas.html"), "w", encoding="utf-8").write(s.replace("__DATA__", data))
print("ok")
