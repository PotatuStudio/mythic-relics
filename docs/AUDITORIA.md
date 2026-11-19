# Auditoría (2026-11-19)

Revisión del código después del paquete de refactors (`REFACTOR.md`, terminado y en uso). Compila sin avisos de
`-Xlint:deprecation,removal` salvo los de abajo. **[JUEGO]** = el arreglo cambia el comportamiento en el juego: se aprueba
antes de tocarlo. *(sospecha)* = sin comprobar en el juego.

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
