"""Arma el visor de los aliados de la tanda 2 china (Perro Celestial y qilin) a partir del visor de la tanda 1.
Uso: python ../model-gen/china2_entities.py china2_entities.json && python china2_entities_build.py"""
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


rep("<title>China · Aliados</title>", "<title>China · Aliados 2</title>")
rep("<h1>China · Aliados</h1>", "<h1>China · Aliados 2</h1>")
rep("Los dos aliados de la tanda 1, con la geometría", "Las dos criaturas de la tanda 2, con la geometría")
rep("Las partes que brillan (ojos y perla) se ven más en la escena nocturna.",
    "Las partes que brillan (ojos y llamas) se ven más en la escena nocturna.")
rep('''      <button class="btn" data-s="monkey" aria-pressed="true">Mono guerrero</button>
      <button class="btn" data-s="monkeys" aria-pressed="false">Los cuatro monos</button>
      <button class="btn" data-s="dragon" aria-pressed="false">Long</button>
      <button class="btn" data-s="rider" aria-pressed="false">Long con jinete</button>''',
    '''      <button class="btn" data-s="dog" aria-pressed="true">Perro Celestial</button>
      <button class="btn" data-s="qilin" aria-pressed="false">Qilin</button>
      <button class="btn" data-s="rider" aria-pressed="false">Qilin con jinete</button>''')
cut('  <div class="cards">', '</div>\n\n<script', '''  <div class="cards">
    <article class="card">
      <h2>Perro Celestial (Xiaotian Quan)</h2>
      <p class="note">Set de Erlang Shen (Ojo + alabarda): aparece cuando un mob te hiere y lo persigue 45 s.</p>
      <div class="fid">
        <h3>Aspecto</h3>
        <ul>
          <li>Lebrel negro, esbelto y veloz: pecho hondo, cintura fina y patas largas.</li>
          <li>Hocico largo, orejas en punta y cola larga que se curva.</li>
          <li>Collar de oro con una campanita.</li>
          <li>Ojos ámbar que brillan en la oscuridad.</li>
        </ul>
        <h3>Fidelidad</h3>
        <p><b>Fiel:</b> el perro de Erlang Shen en <i>Viaje al Oeste</i> y <i>La investidura de los dioses</i>, que mordió la pierna del Rey Mono para que Erlang lo atrapara. Su nombre significa «perro que aúlla al cielo». Se lo describe negro y veloz, como un lebrel de caza.</p>
        <p><b>Licencia:</b> el collar de oro con la campanita y los ojos que brillan.</p>
        <p><b>Pop:</b> nada.</p>
      </div>
    </article>
    <article class="card">
      <h2>Qilin</h2>
      <p class="note">Lo llama el Cuerno del qilin: montura que corre por tierra; si muere, vuelve en 5 min.</p>
      <div class="fid">
        <h3>Aspecto</h3>
        <ul>
          <li>Cabeza de dragón con barba y un solo cuerno dorado con anillos.</li>
          <li>Cuerpo de ciervo cubierto de escamas verde jade con el borde de oro, y vientre crema.</li>
          <li>Cascos hendidos y cola de buey con un mechón.</li>
          <li>Llamas que no queman en la melena, el lomo, las patas y la cola, que brillan.</li>
          <li>Silla roja con borde de oro para el jinete.</li>
        </ul>
        <h3>Fidelidad</h3>
        <p><b>Fiel:</b> la descripción clásica (cuerpo de ciervo, cola de buey, cascos, escamas, un cuerno, cabeza de dragón) y las llamas que lo envuelven en el arte de las dinastías Ming y Qing.</p>
        <p><b>Licencia:</b> la silla y que se monte.</p>
        <p><b>Pop:</b> nada. No es el «unicornio chino» blanco de los juegos ni la jirafa con que se lo confunde en Japón (<i>kirin</i>).</p>
      </div>
    </article>
  </div>
''')
cut("const monkeyMat = material(DATA.monkey)", "/* Jinete:", '''const dogMat = material(DATA.dog), qilinMat = material(DATA.qilin);
const dog = build(DATA.dog, dogMat);
const qilin = build(DATA.qilin, qilinMat);
qilin.entity.scale.setScalar(DATA.qilin.scale);

''')
rep("dragon.parts.seg1.add(rider);\nrider.scale.set(-16, -16, 16); rider.rotation.set(0, 0, 0); rider.position.set(0, -2.5, 6);",
    "qilin.parts.body.add(rider);\nrider.scale.set(-16 / DATA.qilin.scale, -16 / DATA.qilin.scale, 16 / DATA.qilin.scale); rider.rotation.set(0, 0, 0); rider.position.set(0, -4.5, -1.5);")
rep("/* Jinete: muñeco del jugador sentado sobre el segundo segmento. */", "/* Jinete: muñeco del jugador sentado en la silla. */")
cut('let subject = "monkey", animate = true;', 'const clock = new THREE.Clock();', '''let subject = "dog", animate = true;
const CAMS = {
  dog: { cam: [1.6, 1.0, 1.9], target: [0, 0.55, 0] },
  qilin: { cam: [3.4, 2.2, 3.8], target: [0, 1.2, 0] },
  rider: { cam: [3.6, 2.6, 4.0], target: [0, 1.6, 0] },
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
cut("function animateMonkey(m, t, phase) {", "function resize()", '''/* Marcha de cuadrúpedo: patas en diagonal (como el lobo y el caballo de vanilla). */
function animateDog(t) {
  const P = dog.parts, w = animate ? 0.9 : 0, a = t * 9;
  setRot(P.leg_fr, Math.cos(a) * w); setRot(P.leg_hl, Math.cos(a) * w);
  setRot(P.leg_fl, -Math.cos(a) * w); setRot(P.leg_hr, -Math.cos(a) * w);
  setRot(P.tail, Math.sin(t * 2) * 0.1, Math.sin(t * 6) * 0.35);
  setRot(P.tail_tip, Math.sin(t * 2 - 0.5) * 0.15);
  setRot(P.head, Math.sin(t * 9) * 0.04 * (animate ? 1 : 0), Math.sin(t * 0.7) * 0.25);
  setRot(P.ear_r, Math.sin(t * 3) * 0.05); setRot(P.ear_l, Math.sin(t * 3 + 1) * 0.05);
  dog.entity.position.y = animate ? Math.abs(Math.sin(a)) * 0.03 : 0;
}
function animateQilin(t) {
  const P = qilin.parts, w = animate ? 0.7 : 0, a = t * 7;
  setRot(P.leg_fr, Math.cos(a) * w); setRot(P.leg_hl, Math.cos(a) * w);
  setRot(P.leg_fl, -Math.cos(a) * w); setRot(P.leg_hr, -Math.cos(a) * w);
  setRot(P.neck, Math.sin(a) * 0.05 * (animate ? 1 : 0));
  setRot(P.head, Math.sin(t * 0.8) * 0.06, Math.sin(t * 0.5) * 0.2);
  setRot(P.jaw, Math.max(0, Math.sin(t * 0.9)) * 0.15);
  setRot(P.tail, Math.sin(t * 1.6) * 0.15, Math.sin(t * 1.1) * 0.3);
  setRot(P.tail_tuft, 0, Math.sin(t * 1.6 - 0.6) * 0.3);
  setRot(P.mane, 0, 0, Math.sin(t * 4) * 0.04);
  qilin.entity.position.y = animate ? Math.abs(Math.sin(a)) * 0.04 : 0;
}
''')
rep('''  monkeys.forEach((m, i) => animateMonkey(m, t, i * 1.3));
  animateDragon(t);''', '''  animateDog(t);
  animateQilin(t);''')
data = open(os.path.join(HERE, "china2_entities.json"), encoding="utf-8").read()
open(os.path.join(HERE, "china-aliados-2.html"), "w", encoding="utf-8").write(s.replace("__DATA__", data))
print("ok")
