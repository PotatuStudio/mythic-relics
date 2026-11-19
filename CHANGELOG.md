# Changelog

Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/);
el proyecto usa [SemVer](https://semver.org/lang/es/) con la versión de Minecraft como metadato (`0.1.0+26.3`).

## [Unreleased]
### Added
- Proyecto inicial desde mod-template, con Trinkets Updated (Fabric) y Curios (NeoForge).
- Reliquias de cinco mitologías: Egipto, Grecia, Roma, nórdicos y China (70 reliquias), con sets, aliados, monturas,
  libro (Modonomicon), página de JEI, una pestaña creativa por mitología y modelos 3D puestos con textura pintada.

### Fixed
- Auditoría del 2026-11-19 (ver `docs/AUDITORIA.md`):
  - El sangrado de Dáinsleif ya no tumba el servidor cuando muere uno de varios heridos.
  - Los tajos de la espada de Freyr, los barridos del mayal y del Ruyi, los rayos de Zeus y la calabaza ya no cuentan
    como golpe de la mano: no cobran la maldición de Tyrfing ni gastan la cólera, Gram o la harpe.
  - Gram, la cólera de Aquiles, la harpe y el barrido del Ruyi solo cuentan si el golpe entra (no con frames de
    invulnerabilidad ni contra un escudo).
  - El yelmo de Hades ya no borra la invisibilidad de las pociones.
  - Un mob petrificado y atado a la vez no camina al soltarse uno; un mob sin IA de un mapa sigue sin IA.
  - El papel del talismán fu ya no queda flotando si el chunk se descargó.
  - Las recargas vuelven a verse (y a contar) al reaparecer tras morir.
  - Skíðblaðnir se pliega aunque se haya guardado con el jugador montado.
  - Romper el fuego de Vesta lo olvida y no da carbón; Mímir no dice «llegaste» al cambiar de dimensión.
  - El mob petrificado se ve de piedra para quien llega después; los candados ajenos no hacen la animación de abrir.
  - La calabaza no pierde al mob si se duplicó; el set de Anubis también frena a los zoglin.
  - Muninn y los libros sibilinos avisan sobre la barra, no en el chat.

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
  - Disco solar de Ra rediseñado: Ra renace (cada amanecer te cura del todo y te quita los efectos negativos) y Ojo de
    Ra (con 6 corazones o menos estalla: 12 de daño, fuego y ceguera a los hostiles a 8 bloques; recarga 90 s). Se quitan
    el +30 % a no-muertos bajo el sol y el ureo.
- Séptima ronda (2026-11-15):
  - Armas 3D en la mano con la referencia de la espada vanilla: el Ruyi (chico y estirado) y Mjölnir van en la misma
    diagonal, con el agarre en el mango y las posiciones de mano de `item/handheld` (`tools/model-gen/handheld3d.py`).
  - Hinojo de Prometeo: la luz se mueve cada tick, la nueva se pone antes de quitar la anterior (sin parpadeo) y, si a
    la altura de los ojos hay algas o pasto marino, prueba los bloques de al lado (no se apaga).
  - Égida de Atenea: el gorgoneion es la misma cabeza de Medusa de la reliquia, en relieve sobre la placa.
  - Cabeza de Medusa: al alzarla, las serpientes se retuercen y sacan la lengua (8 cuadros con `use_cycle`).
  - Alas de Ícaro: plegadas en el suelo, aletean al subir, se abren al planear y se echan atrás en picada (propiedad
    de cliente `mythicrelics:icarus_pose`).
  - Mjölnir: la carga suena con un chasquido eléctrico suave (antes, trueno) y la vuelta a la mano suena más bajo.
  - Visor: arreglado el error que lo dejaba sin 3D (la perla del dragón) y nueva vitrina «En la mano» por cultura.
- Compañeros con modelo nuevo (aprobados por el usuario en la página de comparación, `tools/model-gen/companions_compare.py`;
  diseños en `tools/model-gen/proposals/`): Long (escala 1,3, cuerpo grueso en S), Sleipnir (x1,25, ocho patas
  articuladas y arreos vikingos), Pegaso (alas de tres tramos), Huginn y Muninn, Cerbero (escala 2,0, cola de serpiente),
  Talos (escala 2,2), chacal, escarabajo, mono guerrero y el halcón de Freyja (x1,3). El escarabajo, el chacal, Cerbero y
  Sleipnir dejan de ser modelos hechos a mano: su geometría también la genera `china_entities.py --mod`, y los tres
  primeros suman una capa que brilla. Cajas de choque al tamaño nuevo. La espada de Freyr y Skíðblaðnir no cambian
  (el usuario prefirió quedarse con el barco actual).
- Animaciones de los compañeros nuevos: Cerbero (quijadas que gruñen a destiempo, orejas, cola de serpiente en cuatro
  tramos, patas con codo y corvejón, furia), chacal (trote articulado, orejas, cola, mordida), escarabajo (trípode con
  fémur y tibia, antenas en abanico, élitros que se abren, disco solar que late), Long (melena, barba y bigotes en cadena,
  patas que reman y se apoyan al aterrizar), mono guerrero (cola y plumas, bastón que gira al golpear), cuervos (latigazo
  del ala y plegado en dos tiempos al posarse), halcón de Freyja (aleteo, planeo, picada y suelo), Sleipnir (cañas que
  se doblan, ocho patas en onda, crin y cola trenzada), Pegaso (alas de tres tramos que se pliegan en Z en el suelo,
  patas recogidas en vuelo) y Talos (paso pesado, lanzamiento en tres tiempos y abrazo al rojo vivo).
- Octava ronda (2026-11-15): cuervos sin parpadeo (plumas planas con descarte de caras), más lejos del dueño (8 a 14
  bloques), alcanzan volando rápido y reaparecen cerca si se pierden; cabeza de Medusa alzada mirando al frente en
  tercera persona; luz del hinojo del lado del cliente (`client/HeldLightClient`, se quita `relic/HeldLight`); égida con
  la Gorgona más plana; Pegaso aletea más lento, no abre las alas en tierra al bajar escalones y al tocar el agua te
  desmonta y se va; espada de Freyr pega cada ~2,6 s (antes ~1,3 s).
- Novena ronda (2026-11-15): Sodium 0.9.2, Iris 1.11.7 y LambDynamicLights 4.13.0 (este solo en Fabric) como
  dependencias de desarrollo, y los shaders Complementary Unbound y Reimagined que baja `downloadShaderpacks` a
  run/shaderpacks antes de runClient (para grabar y probar estabilidad; la prueba completa de cliente pasa con ellos).
  Con LambDynamicLights instalado, el hinojo usa su luz dinámica (`assets/mythicrelics/dynamiclights/item`) y se
  desactiva la de respaldo. Mjölnir también es pico y pala, y al clavarse en un bloque suena como maza (el sonido se
  fijaba antes de saber qué arma era). Pegaso pliega las alas hacia atrás pegadas a los flancos. Las alas de Ícaro pasan
  de 7 poses a una grilla de 95 que el cliente recorre con un suavizado en tiempo real (aleteo de 2,2 por segundo).

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
