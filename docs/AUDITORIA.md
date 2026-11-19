# Auditoría (2026-11-19)

Revisión del código después del paquete de refactors (`REFACTOR.md`, terminado y en uso). Compila sin avisos de
`-Xlint:deprecation,removal` salvo los de abajo. **[JUEGO]** = el arreglo cambia el comportamiento en el juego: se aprueba
antes de tocarlo. *(sospecha)* = sin comprobar en el juego.

## Resultado (2026-11-19)
El usuario aprobó corregir todo, también lo que cambia el juego. Estado de cada punto:

| Punto | Estado |
|---|---|
| B1 Dáinsleif | Hecho: se decide quién sangra y se daña fuera del recorrido. Lo cubre la prueba de cliente. |
| B2 Freyr como golpe de la mano | Hecho: `relic/RelicDamage.byPlayer` (la espada es la entidad directa). También barridos del mayal y del Ruyi, rayos de Zeus y la calabaza. El barrido del mayal conserva el bono del set de Osiris; ya no suma el +daño de Megingjörð (no es un golpe de la mano). Lo cubre la prueba de cliente. |
| B3 Efectos antes de que el golpe entre | Hecho: `relic/HitEffects` (Gram, cólera, harpe y barrido del Ruyi se aplican en `LIVING_DAMAGE_POST`). |
| B4 Hades y las pociones | Hecho: el yelmo solo pone y quita su propia invisibilidad (corta y sin partículas). |
| B5 / D2 Estados sin IA | Hecho: `relic/TimedMobStatus` une Gleipnir, talismán y Medusa; la IA vuelve al valor original solo sin otro estado. |
| B6 Papel del talismán | Hecho: la entrada se borra siempre al vencer; un papel sin sello se rechaza al cargarse (`interruptFalse`, D7). |
| B7 Recargas al morir | Hecho: se reponen en `PLAYER_RESPAWN`; las vencidas se olvidan al cerrar el mundo. |
| B8 Skíðblaðnir | Hecho: un bote con la etiqueta vuelve a `SHIPS` al cargarse. |
| B9 Vesta | Hecho: romper el fuego del hogar lo olvida y no suelta carbón. |
| B10 Mímir | Hecho: la guía recuerda su dimensión. |
| B11 Petrificados | Hecho: se manda el estado al empezar a seguir la entidad (`START_TRACKING`). |
| B12 Candados | Hecho: el ítem del candado lleva el dueño y el cliente no predice abrir lo ajeno. El halcón ya lo resolvía en los dos lados. Los candados puestos antes de este cambio no llevan dueño: para esos sigue decidiendo solo el servidor. |
| B13 Calabaza | Hecho: el mob sale con un UUID nuevo. |
| B14 DataFixTypes | **No se cambia**: en 26.3 `SavedDataStorage.readTagFromDisk` usa el tipo sin revisar nulo (pasar `null` cae con NPE). Con la misma versión de datos no corre ninguna regla de vanilla; queda documentado en `RelicCooldowns`. |
| Sospecha `healBlocked` | Confirmada y hecha: en el cliente responde false. |
| Sospecha cayado y Brain | Confirmada, sin daño: a los animales con cerebro (cabra, camello, sniffer…) el `TemptGoal` no les hace nada. No se cambia (sería otra mecánica). |
| Sospecha veto y Brain | Confirmada y hecha: `TargetVeto.init` borra la memoria `ATTACK_TARGET` negada cada 2 ticks cerca de quien tiene un veto. |
| §2 Fugas | Hecho: `WornRelics` por jugador, `RelicCooldowns` vencidas, `FalconForm` al salir, `TALISMAN_PAPER` (B6). |
| §3 Rendimiento | Hecho: `FreyrDrawn` sin búsqueda por cuadro, búsquedas muertas de `EgyptRelics` borradas, pila de Ícaro reutilizada, tooltip de la calabaza sin copiar el NBT. Los recorridos de `BOUND`/`SEALED`/`PETRIFIED` quedan (ahora en `TimedMobStatus`): son pocos y no valía la pena indexarlos. |
| §4 `appendHoverText` | **No se migra**: Mojang lo marca `@Deprecated` como «sobrescribir, no llamar»; en 26.3 sigue siendo el gancho del tooltip (ItemStack lo llama) y `TooltipProvider` solo sirve para componentes que vanilla ya conoce (los propios necesitan API de cada loader). Documentado en los 5 lugares. |
| §5 Código muerto | Hecho: `fmtTenths`, `pointsUpToLevel`, comentario huérfano, imports sin uso. Las 4 paletas pasaron a `tools/model-gen/palettes` (las herramientas se ajustaron). Los borradores locales fuera de git en `tools/` quedan para que el usuario decida. |
| §6 Pruebas | Hecho: JUnit en `common` (`./gradlew :common:test`: números de tooltip, rosa de los vientos, experiencia de Thot, idiomas con las mismas claves y argumentos, `.summary` de cada reliquia). La prueba de cliente ahora falla de verdad (habilidades, `TooltipArgs.problems()`, piezas de set únicas, B1 y B2). Los códecs de `RelicCooldowns`/`CharonStash`/`JanusLocks` no se prueban solos: sus clases cargan el registro del mod. |
| D1 | Hecho: `relic/Modifiers.set`. |
| D3 | Hecho: mapas solo del servidor sin sincronización; `Minions.ownerOf` responde null en el cliente. |
| D4 | Hecho: Muninn y la sibila avisan sobre la barra (la cama de Muninn sale 3 s después). |
| D5 | Queda para después (decisión del usuario). |

## 1. Bugs
### Alta
- **B1. Crash del servidor con el sangrado de Dáinsleif** (`norse/NorseSagas.java:431-444` y `:208-210`): `bleed()`
  recorre `WOUNDS` con `removeIf` y llama a `hurtServer`; si el herido muere, `onDeath` hace `WOUNDS.remove` en medio del
  recorrido → `ConcurrentModificationException` en el tick. Con dos o más heridos pasa fácil. Arreglo: dañar fuera del
  recorrido. No cambia el juego.

### Media
- **B2. [JUEGO] La espada de Freyr cuenta como golpe del jugador** (`entity/FreyrSword.java:262`, `playerAttack`): los
  tajos automáticos activan la maldición de Tyrfing (medio corazón por tajo), el crítico de Tyrfing, los golpes de Gram,
  la herida de Dáinsleif, la harpe (y su recarga), la cólera de Aquiles, Nemea y el bono de set. Arreglo: fuente con la
  espada como entidad directa (como el Ojo de Ra). Lo mismo, menos grave: barrido del mayal, del Ruyi, rayos de Zeus,
  disolución de la calabaza.
- **B3. [JUEGO] Efectos secundarios en el modificador de daño** (`mixin/LivingEntityMixin.java:40`, antes de invulnerabilidad
  y escudo): Gram cuenta golpes, la cólera se consume y el Ruyi barre aunque el golpe no entre. Arreglo: contar y
  consumir en `LIVING_DAMAGE_POST` con `damageTaken > 0`.
- **B4. [JUEGO] El yelmo de Hades borra la invisibilidad de las pociones** (`greece/GreeceRelics.java:216-221`).
- **B5. [JUEGO] Estados con NoAI que chocan** (Gleipnir `NorseRelics:115-123`, talismán `ChinaGods:79-116`, Medusa
  `GreeceRelics:290-303`): al terminar uno se reactiva la IA aunque siga otro (petrificado + atado camina), y un mob con
  `NoAI:1b` de mapas queda con IA. Arreglo: un ayudante `relic/TimedMobStatus` que cuente los estados y recuerde el NoAI
  original (une tres copias del mismo patrón).
- **B6. Papel del talismán huérfano** (`china/ChinaGods.java:180-205`, `:118-124`): si el chunk se descarga antes de que
  expire el sello, la entrada queda en `TALISMAN_PAPER` (fuga) y el papel flota para siempre. No cambia el juego.
- **B7. [JUEGO en parte] Recargas al morir** (`relic/RelicCooldowns.java:66-77`): el ícono se repone solo al entrar, no
  al reaparecer; las de ≥ 10 s que se revisan con la recarga vanilla se reinician al morir. Arreglo: también en
  `PLAYER_RESPAWN`.
- **B8. Skíðblaðnir puede quedar en el mundo** *(sospecha)* (`norse/NorseGods.java:82,101-103,167-188`): si sales montado,
  el bote se guarda con el jugador y al volver no se pliega; `SKIDBLADNIR_TAG` se escribe y nunca se lee.

### Baja
- B9. La lámpara de Vesta borra fogatas ajenas en la misma posición y romperla da carbón (`rome/VestaLampItem.java:58-73`).
- B10. Mímir dice «llegaste» al cambiar de dimensión (`norse/MimirHead.java:258`, la comparación de mundos siempre es falsa).
- B11. Petrificados que se ven normales para quien llega después o al recargar (`PetrifyPayload` solo a quien está cerca).
- B12. Parpadeo cliente/servidor con candados de Jano y el halcón (se bloquea solo en el servidor).
- B13. La calabaza suelta al mob con el mismo UUID: si la calabaza se duplicó, el segundo se pierde.
- B14. `DataFixTypes.SAVED_DATA_COMMAND_STORAGE` aplicado a datos propios (`RelicCooldowns`, `CharonStash`).
- Sospechas: `NorseSagas.healBlocked` leído desde el cliente; `TemptGoal` del cayado en animales con Brain; el veto de
  objetivos no afecta a mobs con Brain (un zoglin sigue atacando con el set de Anubis).

## 2. Fugas de memoria
Nada grave (todo está en `ServerState`). `TALISMAN_PAPER` en el caso B6; `WornRelics.SERVER_CACHE` no se limpia al
desconectarse (tiene tope); `RelicCooldowns` guarda entradas vencidas de jugadores que no vuelven; `FalconForm.EASED`.

## 3. Rendimiento
- `client/FreyrDrawn.java:31`: busca en 48³ por cada portador en cada cuadro (doble con sombras de Iris). Cachear.
- `egypt/EgyptRelics.java:307-321`: búsquedas de husks y no-muertos cada 5–10 ticks que nunca encuentran nada
  (`TargetVeto` ya lo resuelve). Borrar, junto con `ANUBIS_SET_PROVOKED_TICKS` duplicada.
- Menores: `BOUND`/`SEALED`/`PETRIFIED` recorren todos los niveles por entrada y tick; `waxMelted` crea un `ItemStack`
  por llamada; el tooltip de la calabaza copia el NBT del mob en cada cuadro.

## 4. APIs deprecadas
`Item.appendHoverText` (deprecado en 26.3) en `RelicItem`, `HouYiBowItem`, `StoneStatueItem`, `RelicShieldItem` y
`PalladiumItem`: el reemplazo son los componentes `TooltipProvider`. Gradle avisa funciones incompatibles con Gradle 10.

## 5. Código muerto
Sin restos de Japón ni de las 9 chinas en git. Sin uso: `TooltipNumbers.fmtTenths`, `EgyptGods.pointsUpToLevel`,
`SKIDBLADNIR_TAG`, comentario huérfano en `GreeceLegends.java:116`. Se empaquetan 4 texturas solo de herramientas
(`textures/item/{worn,rome,norse,greece3}_palette.png`). Fuera de git quedan borradores locales de Japón y China 2 en
`tools/`.

## 6. Pruebas
La prueba de cliente es de humo: casi sin aserciones y la fase de habilidades anota los errores sin fallar. No cubre
NeoForge, servidor dedicado, reconectar, morir y reaparecer, ni combates con varios mobs (B1 se habría visto).
Unitarias posibles en `common`: niveles de la paleta de Thot, `lunarSeconds`, `TooltipNumbers`, `Compass`, `RelicSets`,
códecs de `RelicCooldowns`/`CharonStash`/`JanusLocks`, paridad de idiomas, `TooltipArgs.check` como prueba.

## 7. Diseño
- D1. `setModifier` copiado 3 veces y modificadores a mano en otros 4 lugares: un `relic/Modifiers.set(...)`.
- D2. Mismo patrón «mob con etiqueta + mapa hasta cuándo» en tres reliquias (se resuelve con B5).
- D3. Concurrencia inconsistente (`ConcurrentHashMap`/`synchronized` en código solo del servidor).
- D4. **[JUEGO]** Avisos en el chat en los cuervos de Odín y los libros sibilinos (choca con «avisos sin chat»).
- D5. Clases de cultura grandes (`NorseSagas` 471, `EgyptGods` 429, `GreeceRelics` 425): a futuro, una clase por reliquia.
- D7. `ChinaGods` hace `discard()` + `pass` en `ADD`; el resto usa `interruptFalse`.
