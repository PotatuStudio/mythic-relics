# Changelog

Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/);
el proyecto usa [SemVer](https://semver.org/lang/es/) con la versión de Minecraft como metadato (`0.1.0+26.3`).

## [Unreleased]
### Added
- Proyecto inicial desde mod-template, con Trinkets Updated (Fabric) y Curios (NeoForge).
- Reliquias de cinco mitologías: Egipto, Grecia, Roma, nórdicos y China (70 reliquias), con sets, aliados, monturas,
  libro (Modonomicon), página de JEI, una pestaña creativa por mitología y modelos 3D puestos con textura pintada.

### Changed
- Espada de Freyr: la de la espalda es la misma que pelea (empuñadura calcada y mismo tamaño, ~1,27 bloques), sin el
  tahalí que la cruzaba; mientras pelea, en la espalda queda la vaina vacía (propiedad `mythicrelics:freyr_drawn`).
- Harpe y cetro de Anubis: el golpe que mata también cuenta como ejecución (antes solo si el mob ya estaba bajo el 25 %).
- Sexta ronda de arreglos del usuario (textos en `tools/model-gen/feedback6_lang.py`):
  - Ruyi Jingu Bang: en la mano es siempre el báculo 3D, chico (nuevo `ruyi_jingu_bang_short`) o estirado; en el
    inventario, el ícono.
  - Dragón de la perla sin la perla llameante delante del hocico.
  - Cabeza de Medusa: 2 s de mirada para petrificar (1 s con el set de Perseo), con Lentitud que sube hasta quedar de
    piedra.
  - Manzana de la Discordia: también contra jefes.
  - Hilo de Ariadna: recarga de 1 min y sin límite de distancia (solo la misma dimensión).
  - Escudo de Aquiles: una sola carga de cólera, +100 % al siguiente golpe y lo manda lejos.
  - Égida de Atenea: ahora es un escudo de mano (modelo calcado del vanilla con el gorgoneion) que aturde al bloquear;
    ya no va en la espalda. En el set de Perseo cuenta en el inventario.
  - Tyrfing: la maldición es medio corazón por cada golpe que no mata (antes, 4 corazones al minuto sin matar).
  - Osiris: mayal de 7 de daño con barrido al 100 % en 3 bloques; el cayado pone de tu lado a un no-muerto hostil 30 s;
    con las dos insignias, Regeneración I y renacer cada 10 min (antes 20).
  - Sandalias de Hermes y alas de Ícaro: tras el doble salto, aleteo o planeo no hay daño de caída hasta tocar el suelo
    (`relic/SafeLanding`).
  - Paleta de Thot: sin límite de experiencia.
  - Hinojo de Prometeo: en la mano alumbra como una antorcha, también bajo el agua (bloque de luz invisible que te sigue,
    `relic/HeldLight`); sigue prendiendo lo que golpeas y fundiendo lo de la otra mano.

### Removed
- Japón entero: sus 19 reliquias (Kusanagi, espejo Yata, magatama Yasakani, tambor de Raijin, kanabō, esfera del
  kitsune, joyas de la marea, mazo de la suerte, kibi dango, caña de Urashima, bolsa de Fūjin, máscara de tengu, sake
  Yashiori, plato del kappa, hoja del tanuki, hacha de Kintarō, muñeca de zashiki-warashi, Ame-no-nuboko y escama de
  Orochi), los sets de los Tres Tesoros y de Fūjin y Raijin, el kitsune, la tortuga de Urashima, la sombra de Orochi,
  su pestaña creativa, su categoría del libro y su página de la wiki.
- Nueve reliquias chinas: lanza de punta de fuego, aro del universo (y su tecla R), ojo y alabarda de Erlang Shen (con
  el set de Erlang y el Perro Celestial), cuerno del qilin (y el qilin), abanico de hoja de plátano, ruedas de viento y
  fuego, pulsera del pixiu y rama del melocotonero. China queda con 9.
- El doble salto vuelve a ser solo de las sandalias de Hermes (ya no avisa que no se acumula con la máscara de tengu).

### Changed
- Tooltips cortos: un resumen propio de cada reliquia (lo importante y la recarga) y el set; con Shift, el detalle
  completo por secciones.
- Rayo de Zeus con ataque fuerte (mantener 2 s: tres rayos con salto, recarga 10 s).
- Cabeza de Medusa sostenida como escudo, Mímir se pregunta, halcón con tecla V, harpe pasiva, saco de xirang pasivo.
- Refactor interno sin cambios de juego: ayudantes comunes en `relic/` (aliados, jefes por tag, brújula, sonidos y
  avisos, números de tooltip), registro de `tooltipArgs`, lista de modificadores de daño, `RelicItem` en `relic/`,
  `MythicRenderers` y `RelicClientTicks`, imports en vez de nombres completos.
- Tooltip de los sets: cuenta solo las piezas que están donde el bono las pide y marca ◐ (con dónde ponerla) las que
  llevas en otro lugar.

- Arte: alas de Ícaro nuevas y más detalle en las 25 reliquias puestas de Egipto, Roma, nórdicos, China y Japón.

- Quinta ronda: dardo de muérdago sin recarga y 16 de daño; Medusa a 8 bloques; lira con el Epitafio de Seikilos y
  frenado leve; yelmo de Hades oculta todo y acelera agachado; Gram pasiva (estocada cada 2 golpes); Gleipnir ata
  15 s con +40 % de daño recibido; talismán fu visible en la frente; aro del universo como brazalete que se lanza
  con su tecla; ojo de Erlang desvía proyectiles; espada de Freyr en la espalda; doble salto que avisa que no se
  acumula; +30 % al arma de cada set; armas reliquia sin reparación ni encantamientos.

### Fixed
- Escudo de Aquiles y Ancile en primera persona: al bloquear ya no desaparecen de la pantalla, y el de Aquiles se
  ve a la misma altura que el escudo vanilla.
- Las recargas de 10 s o más se guardan con el mundo: ya no se pierden al salir y volver a entrar.
- El estado en memoria se limpia al desconectarse un jugador y al cerrar el mundo (vuelo del halcón, fogatas de Vesta…).
- Caché por tick de las reliquias puestas y búsquedas escalonadas (aliados, espada de Freyr, Sibila).
