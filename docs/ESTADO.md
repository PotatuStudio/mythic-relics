# Estado del proyecto (para retomar)

Última sesión: 2026-11-08. Rama de trabajo: `main` (el build multiloader ya se fusionó).

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

## Siguiente: primera tanda de Egipto
Código ya escrito pero con eventos de Fabric API, guardado fuera de las fuentes en `wip/egypt/`
(`EgyptRelics.java`, `Minions.java`). Hay que portarlo a `common/` con Architectury:

| Fabric API | Architectury API |
|---|---|
| `ServerLivingEntityEvents.AFTER_DAMAGE / ALLOW_DAMAGE / AFTER_DEATH` | `EntityEvent.LIVING_HURT` (permite cancelar) y `EntityEvent.LIVING_DEATH` |
| `ServerTickEvents.END_SERVER_TICK` | `TickEvent.SERVER_POST` |
| `ServerEntityEvents.ENTITY_LOAD` | `EntityEvent.ADD` |
| `PayloadTypeRegistry` + `ServerPlayNetworking` | `NetworkManager.registerReceiver` / `NetworkManager.sendToPlayer` |
| `KeyMappingHelper` | `KeyMappingRegistry.register` + `ClientTickEvent.CLIENT_POST` |
| Registro de ítems / pestaña creativa | `DeferredRegister` + `CreativeTabRegistry` |
| `LootTableEvents.MODIFY` | `LootEvent.MODIFY_LOOT_TABLE` |
| Attachments de Fabric (dueño de los aliados) | `Entity.addTag(...)` o datos persistentes propios (no hay equivalente en Architectury) |
| Mixin cliente para el contorno solo para un jugador | mismo mixin en `common` (ver `MinecraftMixin` de Misfit Relics) |

| Reliquia | Espacio | Efecto | Dónde |
|---|---|---|---|
| **Escarabajo dorado** | cinturón | Al bajar de 3 corazones: absorción II 15 s y 2 escarabajos aliados (silverfish) 15 s. Recarga 60 s. | Pirámides del desierto (10 %) |
| **Ojo de Horus** | cara | Tecla (H): contorno de criaturas en 32 bloques (solo para ti, 8 s) y columnas doradas sobre los cofres cercanos (2 chunks). Recarga 30 s. | Pirámides (6 %) |
| **Anj** | collar | Cura medio corazón cada 2,5 s; los no-muertos te hacen 25 % menos daño. | Pirámides (8 %), casas de aldeas del desierto (6 %) |
| **Báculo de Anubis** | arma (mano) | 35 % de que los no-muertos que matas con él vuelvan como aliados 20 s (máx. 3). | Pirámides (4 %) |
| **Vendas de momia** | guantes | Tus golpes dan Hambre II 5 s y Lentitud 3 s; los husks no te atacan. | Pirámides (12 %) |

Segunda tanda de Egipto: disco solar de Ra, amuleto de Bastet, tablilla de Thot, cayado y mayal de Osiris,
garras de Sekhmet. Después: Grecia, Roma, nórdicos, celtas, folclore japonés, Mesoamérica, eslavos, China,
Mesopotamia (las ideas con sus efectos y estructuras están en la conversación del 2026-11-08; resumen en
[ideas.md](ideas.md)).

## Después
1. Libro de reliquias con Modonomicon y generador de `docs/wiki/` desde los datos de las reliquias.
2. Íconos de la tanda (estilo de Misfit Relics) e ícono del mod.
3. Portar Misfit Relics a Architectury (mismo esquema de módulos).
