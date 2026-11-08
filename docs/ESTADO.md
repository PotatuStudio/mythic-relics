# Estado del proyecto (para retomar)

Última sesión: 2026-11-08. Rama de trabajo: `feat/egipto-tanda-1` (el build multiloader ya está en `main`).

## Decisiones tomadas
- **Mythic Relics**: reliquias mitológicas (Egipto, Grecia, nórdicos, celtas, folclore japonés, etc.) como equipo RPG.
  Es un mod aparte de Misfit Relics (que trata de lo "olvidado" de Minecraft); nada se mueve entre ellos.
- **Fabric + NeoForge** en la última versión de Minecraft (26.3), con **Architectury** (módulos `common`, `fabric`,
  `neoforge` + Architectury API). Misfit Relics se portará igual después.
- **Accesorios: Trinkets Updated** 4.2.1+26.3 (un mismo jar sirve en Fabric y NeoForge; no hace falta Curios).
- **Las reliquias no se fabrican**: se encuentran en estructuras que calzan con su cultura (como Artifacts).
  Mientras más fuertes, más difícil la estructura.
- **Biblioteca en el juego**: libro de reliquias con **Modonomicon** (26.3-2.20.1, Fabric y NeoForge), una página por
  reliquia: efecto, números, dónde encontrarla e ícono.
- **Wiki en el repo** (`docs/wiki/`), porque las wikis de GitHub en repos privados requieren plan de pago.
  El libro y la wiki se generan desde los mismos datos que el botín y los tooltips (una sola fuente).
- Se trabaja **por tandas de cultura**. Los nombres los elige el usuario; los propuestos abajo ya están aprobados.
- Estilo de arte: íconos 2D pixel art 16x16 estilo vanilla (contorno, sombreado), brillo animado solo en metales y gemas;
  ver `tools/model-gen` de Misfit Relics (`ring_icons.py`, `relic_art.py`, `glint.py`).

## Estado de la rama `feat/multiloader`
- Reestructurado como proyecto Architectury siguiendo la configuración oficial de Architectury API para 26.3
  (`dev.architectury.loom-no-remap` 1.17-SNAPSHOT, `architectury-plugin` 3.5-SNAPSHOT, Shadow 9.4.3).
- **`./gradlew build` compila en ambos loaders** (BUILD SUCCESSFUL, 2026-11-08): genera
  `fabric/build/libs/mythicrelics-fabric-0.1.0+26.3.jar` y `neoforge/build/libs/mythicrelics-neoforge-0.1.0+26.3.jar`,
  cada uno con el código común incluido (`MythicRelics.class`, `icon.png`) y su metadata (`fabric.mod.json` /
  `META-INF/neoforge.mods.toml`). Compilar con `JAVA_HOME` apuntando al JDK 25.
- **Probado en el juego** (2026-11-08): `runClient` de Fabric y de NeoForge llegan al menú principal y escriben
  "Mythic Relics cargado" en el log, sin errores del mod. En NeoForge, `FMLEnvironment.getDist()` existe en 26.3.

Versiones (en `gradle.properties`): Minecraft 26.3, Architectury API 22.0.5, Fabric Loader 0.20.1,
Fabric API 0.165.2+26.3, NeoForge 26.3.0.101, Trinkets 4.2.1+26.3.

## Primera tanda de Egipto (rama `feat/egipto-tanda-1`)
Diseño revisado con el usuario el 2026-11-08. Criterio: **fiel al mito, con licencias** (el efecto nace del significado
real del símbolo; se aceptan guiños a Minecraft que no lo contradigan). Rareza: **raras** (~57 % de las pirámides trae
al menos una reliquia). Los números están en constantes de `egypt/EgyptRelics.java`; el botín en `registry/ModLoot.java`.

| Reliquia | Espacio | Mito | Efecto | Pirámide (por cofre) |
|---|---|---|---|---|
| **Escarabajo dorado** | cinturón | Jepri, el sol que renace | Al bajar de 3 corazones: absorción II 15 s y 2 escarabajos aliados 15 s, que salen de la tierra y se entierran al irse. Recarga 60 s. | 3,5 % |
| **Ojo de Horus** | cara | El ojo que Thot sanó: protección y sanación | Inmune a Ceguera y Oscuridad. Tras un golpe de 4 corazones o más, recupera el 50 % en 5 s. Recarga 45 s. | 2,5 % |
| **Anj** | collar | La vida | Cura medio corazón cada 2,5 s; los no-muertos te hacen 25 % menos daño. | 3 % (+3 % casas de aldea del desierto) |
| **Vendas de momia** | guantes | Guiño al husk de Minecraft | Tus golpes dan Hambre II 5 s y Lentitud 3 s; los husks no te atacan. | 4 % |
| **Cetro *was* de Anubis** | arma (mano) | El juicio (pesado del corazón) | Golpear a un no-muerto con menos del 25 % de vida lo destruye y suelta el doble de experiencia. | 2 % |
| **Máscara de chacal** | cara | Anubis guía las almas | Los no-muertos que matas sueltan un alma que te cura 1 corazón al tocarla (dura 10 s). | 3 % |
| **Anillo de los chacales** | anillo | Los chacales, guardianes de las tumbas | De noche o bajo tierra, al recibir un golpe aparecen 2 chacales aliados (lobos) 20 s. Recarga 90 s. | 2,5 % |
| **Set de Anubis** (máscara + anillo + cetro en el inventario) | — | Protector de los muertos | Los no-muertos no te atacan salvo que los hayas golpeado en los últimos 10 s. | — |

Descartado: el báculo de Anubis que alzaba no-muertos como aliados (Anubis no es nigromante; es cultura pop) y el Ojo de
Horus que revelaba criaturas y cofres con una tecla ("el ojo que todo lo ve" es una lectura moderna).

Estado: portado a Architectury en `common/` (eventos `EntityEvent`/`TickEvent`/`LootEvent`, `DeferredRegister`, mixin
común en `LivingEntity` para el Anj y la inmunidad del Ojo). `./gradlew build` compila y ambos `runClient` llegan al
menú. **Falta probar los efectos en un mundo** y los íconos (hoy se ven con la textura faltante).

Escarabajo aliado: entidad propia `mythicrelics:scarab` (`entity/Scarab.java`, `PathfinderMob`, no se guarda con el
mundo) con modelo de cajas propio (`client/entity/ScarabModel.java`: caparazón con cúpula, pronoto, cabeza en pala,
6 patas con marcha en trípode) y textura dorada con turquesa y lapislázuli, generada con
`python tools/model-gen/scarab_texture.py common/src/main/resources/assets/mythicrelics/textures/entity/scarab.png`
(requiere Pillow; el layout de la textura sigue los `texOffs` del modelo). Pendiente: el chacal con el mismo trato (lobo más delgado, orejas grandes,
negro como Anubis).

Segunda tanda de Egipto: disco solar de Ra, amuleto de Bastet, tablilla de Thot, cayado y mayal de Osiris,
garras de Sekhmet. Después: Grecia, Roma, nórdicos, celtas, folclore japonés, Mesoamérica, eslavos, China,
Mesopotamia (las ideas con sus efectos y estructuras están en la conversación del 2026-11-08; resumen en
[ideas.md](ideas.md)).

## Después
1. Libro de reliquias con Modonomicon y generador de `docs/wiki/` desde los datos de las reliquias.
2. Íconos de la tanda (estilo de Misfit Relics) e ícono del mod.
3. Portar Misfit Relics a Architectury (mismo esquema de módulos).
