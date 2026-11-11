"""Arma el visor de la sombra de Orochi (tanda 2 japonesa) a partir del visor de la tanda 1.
Uso: python ../model-gen/japan_entities.py japan_entities.json && python japan2_entities_build.py"""
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


rep("<title>China · Aliados</title>", "<title>Japón · Orochi</title>")
rep("<h1>China · Aliados</h1>", "<h1>Japón · Orochi</h1>")
rep("Los dos aliados de la tanda 1, con la geometría", "La sombra de Yamata no Orochi, con la geometría")
rep("Las partes que brillan (ojos y perla) se ven más en la escena nocturna.",
    "Las partes que brillan (ojos y llamas) se ven más en la escena nocturna.")
rep('''      <button class="btn" data-s="monkey" aria-pressed="true">Mono guerrero</button>
      <button class="btn" data-s="monkeys" aria-pressed="false">Los cuatro monos</button>
      <button class="btn" data-s="dragon" aria-pressed="false">Long</button>
      <button class="btn" data-s="rider" aria-pressed="false">Long con jinete</button>''',
    '''      <button class="btn" data-s="dog" aria-pressed="true">Orochi</button>
      <button class="btn" data-s="rider" aria-pressed="false">Orochi junto al jugador</button>''')
cut('  <div class="cards">', '</div>\n\n<script', '''  <div class="cards">
    <article class="card">
      <h2>Sombra de Yamata no Orochi</h2>
      <p class="note">La llama la Escama de Orochi: se queda quieta a tu lado 45 s y sus ocho cabezas muerden a los hostiles a 6 bloques.</p>
      <div class="fid">
        <h3>Aspecto</h3>
        <ul>
          <li>Ocho cabezas en anillo sobre cuellos largos, cada una con su mandíbula que muerde.</li>
          <li>Ojos rojos como la akakagachi (la cereza de invierno), que brillan.</li>
          <li>Cuerpo de escamas oscuras cubierto de musgo, con cipreses y cedros que le crecen en el lomo.</li>
          <li>Vientre rojo e inflamado, y ocho colas que se extienden por el suelo.</li>
          <li>En una de las colas, el brillo de la espada Kusanagi.</li>
        </ul>
        <h3>Fidelidad</h3>
        <p><b>Fiel:</b> la descripción del Kojiki: ocho cabezas y ocho colas, ojos como cerezas de invierno, musgo y árboles en el lomo, el vientre sangrante, y Kusanagi en la cola.</p>
        <p><b>Licencia grande:</b> que pelee de tu lado. En el mito es el monstruo que vence Susanoo; aquí es su sombra, invocada con una escama.</p>
        <p><b>Pop:</b> nada. No es el Orochi de Naruto ni el de Ōkami.</p>
      </div>
    </article>
  </div>
''')
cut("const monkeyMat = material(DATA.monkey)", "/* Jinete:", '''const dogMat = material(DATA.orochi);
const dog = build(DATA.orochi, dogMat);
dog.entity.scale.setScalar(DATA.orochi.scale);
const qilin = { entity: new THREE.Group(), parts: {} };
scene.add(qilin.entity);

''')
rep("dragon.parts.seg1.add(rider);\nrider.scale.set(-16, -16, 16); rider.rotation.set(0, 0, 0); rider.position.set(0, -2.5, 6);",
    "scene.add(rider);\nrider.position.set(4.0, 0, 0); rider.rotation.set(0, -Math.PI / 2, 0);\nlegR.rotation.set(0, 0, 0); legL.rotation.set(0, 0, 0); legR.position.set(-2 / 16, 6 / 16, 0); legL.position.set(2 / 16, 6 / 16, 0);\nlegR.scale.set(1, 3, 0.34); legL.scale.set(1, 3, 0.34);")
rep("/* Jinete: muñeco del jugador sentado sobre el segundo segmento. */", "/* Jinete: muñeco del jugador sentado en la silla. */")
cut('let subject = "monkey", animate = true;', 'const clock = new THREE.Clock();', '''let subject = "dog", animate = true;
const CAMS = {
  dog: { cam: [7.0, 4.6, 7.6], target: [0, 2.2, 0] },
  qilin: { cam: [7.0, 4.6, 7.6], target: [0, 2.2, 0] },
  rider: { cam: [8.6, 4.4, 8.0], target: [1.6, 1.8, 0] },
};
function show(s) {
  subject = s;
  document.querySelectorAll("#subjects .btn").forEach(b => b.setAttribute("aria-pressed", String(b.dataset.s === s)));
  dog.entity.visible = true;
  qilin.entity.visible = false;
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
  const P = dog.parts, k = animate ? 1 : 0.2;
  for (let i = 0; i < 8; i++) {
    setRot(P["neck" + i], Math.sin(t * 1.1 + i * 0.8) * 0.12 * k);
    setRot(P["neck" + i + "b"], Math.sin(t * 1.3 + i * 0.8 - 0.5) * 0.18 * k, Math.sin(t * 0.9 + i) * 0.15 * k);
    setRot(P["head" + i], Math.sin(t * 1.3 + i * 0.8 - 1.0) * 0.1 * k);
    setRot(P["jaw" + i], Math.max(0, Math.sin(t * 2.4 + i * 1.7)) * 0.6 * k);
    setRot(P["tail" + i], 0, Math.sin(t * 0.8 + i * 0.9) * 0.2 * k);
  }
}
function animateQilin(t) {}
''')
rep('''  monkeys.forEach((m, i) => animateMonkey(m, t, i * 1.3));
  animateDragon(t);''', '''  animateDog(t);
  animateQilin(t);''')
data = open(os.path.join(HERE, "japan_entities.json"), encoding="utf-8").read()
open(os.path.join(HERE, "japon-orochi.html"), "w", encoding="utf-8").write(s.replace("__DATA__", data))
print("ok")
