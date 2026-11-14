# Changelog

Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/);
el proyecto usa [SemVer](https://semver.org/lang/es/) con la versión de Minecraft como metadato (`0.1.0+26.3`).

## [Unreleased]
### Added
- Proyecto inicial desde mod-template, con Trinkets Updated (Fabric) y Curios (NeoForge).
- Reliquias de seis mitologías: Egipto, Grecia, Roma, nórdicos, China y Japón (101 ítems), con sets, aliados, monturas,
  libro (Modonomicon), página de JEI, una pestaña creativa por mitología y modelos 3D puestos con textura pintada.

### Changed
- Tooltips cortos: el efecto principal, la recarga y el set; con Shift, el detalle completo por secciones.
- Cabeza de Medusa sostenida como escudo, Mímir se pregunta, halcón con tecla V, harpe pasiva, saco de xirang pasivo.

### Fixed
- El estado en memoria se limpia al desconectarse un jugador y al cerrar el mundo (vuelo del halcón, fogatas de Vesta…).
- Caché por tick de las reliquias puestas y búsquedas escalonadas (aliados, espada de Freyr, Sibila).
