# Changelog

Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/);
el proyecto usa [SemVer](https://semver.org/lang/es/) con la versión de Minecraft como metadato (`0.1.0+26.3`).

## [Unreleased]
### Added
- Proyecto inicial desde mod-template, con Trinkets Updated (Fabric) y Curios (NeoForge).
- Reliquias de seis mitologías: Egipto, Grecia, Roma, nórdicos, China y Japón (101 ítems), con sets, aliados, monturas,
  libro (Modonomicon), página de JEI, una pestaña creativa por mitología y modelos 3D puestos con textura pintada.

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
