# Estado del proyecto (para retomar)

**2026-11-15: se borraron Japón entero y 9 reliquias chinas** (ver «Reliquias borradas»). Quedan 70 reliquias en cinco
mitologías: Egipto, Grecia, Roma, nórdicos y China (9).

## Sesión del 2026-11-19: auditoría corregida
Se corrigió toda la auditoría ([AUDITORIA.md](AUDITORIA.md), tabla «Resultado»), también lo que cambia el juego, con
aprobación del usuario. Ayudantes nuevos en `relic/`: `RelicDamage` (daño del jugador que no es de su mano),
`HitEffects` (efectos que solo cuentan si el golpe entra), `TimedMobStatus` (estados sin IA), `Modifiers` (atributos),
`XpMath`. Pruebas: `./gradlew :common:test` (JUnit) y la prueba de cliente, que ahora falla de verdad (fase
`aserciones`). Pendiente que el usuario pruebe en el juego: Freyr con Tyrfing, Gram contra un escudo, Hades con poción,
Medusa + Gleipnir, candados entre dos jugadores, Muninn y la sibila sobre la barra.

## Para retomar (sesión del 2026-11-14 y 15)

**Prueba automática de NeoForge (2026-11-19)**: `RELIC_TEST=1 ./gradlew :neoforge:runClient`
(`neoforge/.../client/NeoForgeClientTest`, la misma que en Misfit). Abre una copia de `run/world` (la crea
`:neoforge:runServer`), arma los tooltips de las 73 reliquias, pone cada accesorio con Curios comprobando `WornRelics` y
saca capturas en `run/screenshots/relic-test`; registro en `run/relic-test/registro.txt`. Pasó entera (24 accesorios) y
las capturas calzan con las de Fabric.
Rama de trabajo: `main`. Todo lo de esta sesión está subido. Hecho, en orden:
- Paquete de refactors completo (`docs/REFACTOR.md`), con los opcionales (geometría por entidad, resúmenes `.summary`).
- **Prueba automática de cliente** (`./gradlew :fabric:runClientGameTest`): abre el juego solo, prueba cada reliquia y deja
  capturas y `registro.txt` en `fabric/build/run/clientGameTest/mythic-test/`. Fases con la variable `MYTHIC_FASES`
  (separadas por coma): `tooltips`, `manos`, `puestas`, `una` (cada reliquia puesta sola, frente/espalda/lado/arriba),
  `entidades`, `escudos`, `sets`, `habilidades`. Sin la variable corre todas (~10 min). No entrar con la terminal a
  `fabric/build/run` mientras corre (no puede borrar la carpeta). Encontró dos errores reales (crash de
  `WornRelics.holding` y escudos invisibles en primera persona), ya arreglados.
- Arte: alas de Ícaro nuevas (giros reales de elemento) y más detalle en las reliquias puestas de Egipto, Roma,
  nórdicos y China; pelo del kappa.
- Quinta ronda de arreglos del usuario (ver CHANGELOG): dardo sin recarga, Medusa a 8, lira con el Epitafio de
  Seikilos, yelmo de Hades que oculta todo y acelera agachado, Gram pasiva, Gleipnir 15 s con +40 % de daño, talismán
  fu visible en la frente, espada de Freyr en la espalda, +30 % al arma de cada set, armas sin reparación ni encantos.
- Revisión de la mitología china con fuentes: https://claude.ai/artifact/Q5Pv7o7DFL3DzuyXknshcM. Tras leerla, el
  usuario **borró Japón entero y 9 reliquias chinas** (ver «Reliquias borradas»).

**Sesión del 2026-11-15 (rondas 6 a 9, ver CHANGELOG)**: arreglos de reliquias (Ra, hinojo, égida como escudo,
Medusa animada, alas de Ícaro con 95 poses suavizadas, Mjölnir pico y pala); los 10 compañeros con modelo nuevo y
animaciones (diseños en `tools/model-gen/proposals/`, comparación en `companions_compare.py`); visor general con vitrina
«En la mano» y animaciones; Sodium, Iris, LambDynamicLights y shaders Complementary en desarrollo (en NeoForge, Sodium
0.9.3-beta.3: la 0.9.2 crashea). Todo subido a GitHub (el usuario prueba en otro PC: push siempre).

**Para retomar:**
1. Que el usuario pruebe en el juego lo de esta sesión: luz del hinojo con LambDynamicLights (Fabric), sonido de Mjölnir
   al clavarse, alas de Ícaro, alas plegadas de Pegaso, cuervos (parpadeo y distancia), espada de Freyr cada 2,6 s, y
   montar el Long y Sleipnir (altura del jinete).
2. Publicar en CurseForge y Modrinth: el usuario crea los proyectos; ofrecer `mod-publish-plugin` (Gradle) con tokens en
   variables de entorno. Dependencias requeridas: Architectury API y Modonomicon; Fabric API y Trinkets Updated
   (Fabric); Curios (NeoForge). Revisar a mano que el nombre esté libre en CurseForge.
3. Evaluar un port a 1.21.1 (NeoForge + Fabric): es donde están los modpacks grandes. Costo alto: no tiene las
   definiciones de ítem (`items/*.json`) ni el render state de entidades.
4. Pendientes de antes: mejoras de las 9 reliquias chinas y la tanda Chile/mapuche.

**Siguiente:**
1. El usuario prueba en NeoForge (Curios): las reliquias puestas con el arte nuevo, la espada de Freyr en la espalda, el
   yelmo de Hades (que no se vea nada al estar invisible), la lira y los cambios de la quinta ronda. El papel del
   talismán fu en la frente no quedó visto en la prueba automática (el clic no le dio al husk): confirmarlo.
2. Mejoras de las 9 reliquias chinas que quedan, según la revisión (el usuario eligió dejarlas para después):
   calabaza que llama por el nombre y atrapa al que responde; xirang con nombre «Puñado de xirang», lore corregido
   (tapó manantiales), rescate que desaparece a los segundos y uso contra el agua; elixir de Chang'e sin el daño en el
   aire y con vuelo lento de noche; piedra de cinco colores con un escudo de «remendar el cielo». Mostrarlas antes de
   programar.
3. Luego, Chile/mapuche (punto 2 de «Después»).

## Decisiones tomadas
- **Mythic Relics**: reliquias mitológicas (Egipto, Grecia, Roma, nórdicos, China, etc.) como equipo RPG. Japón se
  programó y luego se quitó entero (2026-11-15), junto con 9 reliquias chinas.
  Es un mod aparte de Misfit Relics (que trata de lo "olvidado" de Minecraft); nada se mueve entre ellos.
- **Fabric + NeoForge** en la última versión de Minecraft (26.3), con **Architectury** (módulos `common`, `fabric`,
  `neoforge` + Architectury API). Misfit Relics se portará igual después.
- **Accesorios: el estándar de cada loader** (cambio del 2026-11-08): **Trinkets Updated** 4.2.1+26.3 en Fabric y
  **Curios** 17.0.0+26.3 en NeoForge, para no chocar con los mods de NeoForge que usan Curios. El código común pregunta
  por `relic/WornRelics` y cada loader instala su implementación. Los espacios: Trinkets en `fabric/.../data/trinkets`
  (cara, collar, cinturón, guantes y anillo en cada mano); Curios en `neoforge/.../data/curios/tags/item` (head,
  necklace, belt, hands, ring; Curios no tiene "cara", así que el Ojo y la máscara van en head). Las reliquias puestas se
  ven igual en los dos: Trinkets con `fabric/.../trinkets/*.json` y Curios con `CuriosRelicRenderer`, ambos con los
  modelos `*_worn` (la definición del ítem los usa en el contexto de display "none"). Accessories (Wispforest) quedó
  descartado: no tiene versión para 26.3.
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
Fabric API 0.165.2+26.3, NeoForge 26.3.0.101, Trinkets 4.2.1+26.3 (Fabric), Curios 17.0.0+26.3 (NeoForge).

## Primera tanda de Egipto
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
(requiere Pillow; el layout de la textura sigue los `texOffs` del modelo). El chacal del anillo también es entidad propia (`mythicrelics:jackal`, `JackalModel`: más delgado que el lobo,
orejas altas, negro con collar de oro). Las reliquias puestas son modelos 3D propios
(`models/item/*_worn.json`, generados por `tools/model-gen/worn_models.py`): collar con cadena y Anj en relieve,
faja con hebilla de escarabajo, diadema con el wedjat, máscara de chacal, vendaje en mano y antebrazo, y aro con ónix.
Se ubican con la regla de Trinkets 4.2.1 (ver el docstring del generador). Libro de reliquias con Modonomicon (opcional) y wiki en `docs/wiki/`.

Segunda tanda de Egipto: disco solar de Ra, amuleto de Bastet, tablilla de Thot, cayado y mayal de Osiris,
garras de Sekhmet. Después: Grecia, Roma, nórdicos, celtas, folclore japonés, Mesoamérica, eslavos, China,
Mesopotamia (las ideas con sus efectos y estructuras están en la conversación del 2026-11-08; resumen en
[ideas.md](ideas.md)).

## Segunda tanda de Egipto (programada el 2026-11-09)
Diseño aprobado en `docs/ideas.md` (con su página de diseño). Código en `egypt/EgyptGods.java` (constantes y lógica),
`egypt/ThothPaletteItem.java` (experiencia escrita en el componente `mythicrelics:written_levels`) y
`egypt/OsirisCrookItem.java`. Mixins nuevos: `PlayerMixin` (carga del ataque para el golpe giratorio del mayal y +20 %
de experiencia de Thot), `BrushableBlockEntityMixin` (al cepillar, la reliquia sale primero; Minecraft solo da un ítem)
y en `LivingEntityMixin` el daño de Ra/Sekhmet/Bastet y el renacer de Osiris en `checkTotemDeathProtection`.
Recargas guardadas con el mundo por jugador (`relic/RelicCooldowns`, SavedData `mythicrelics:cooldowns`): no se
reinician al relogear ni al reiniciar el servidor; al entrar se restaura la recarga visible en el ícono. La paleta de
Thot guarda puntos de experiencia (componente `mythicrelics:written_points`), no niveles, para que no se pueda duplicar.
Arqueología: `archaeology/desert_pyramid` (Thot 3 %, Ra 1,5 %, Sekhmet 1,5 %) y `archaeology/desert_well` (Bastet 5 %),
por bloque cepillado. NeoForge suma 1 espacio de cabeza en Curios (`data/mythicrelics/curios/slots/head.json`) para que
Ra conviva con el Ojo o la máscara. **Sin probar en el juego**: en especial los mixins nuevos (si un punto de inyección
falla, el juego se cae al iniciar), la arqueología, el renacer y que los creepers eviten a Bastet.

## Primera tanda de Grecia (programada el 2026-11-09)
Diseño aprobado en `docs/ideas.md`. Código en `greece/`: `GreeceRelics` (constantes y lógica: Hermes, Hades, égida,
petrificar, set de Perseo), `MedusaHeadItem`, `AriadneThreadItem` (punto de inicio en el componente
`mythicrelics:ariadne_anchor`), `OrpheusLyreItem` y `CharonStash` (inventario y experiencia guardados con el mundo al
morir con el óbolo; se devuelven al reaparecer). Cliente: `client/RelicClientTicks` (doble salto de Hermes con el paquete
`mythicrelics:hermes_jump` e hilo rojo de Ariadna). Mixin nuevo: `blockUsingItem` en `LivingEntityMixin` para la égida.
Los mobs petrificados llevan la etiqueta `mythicrelics.petrified` y se liberan solos aunque el servidor se reinicie.
Botín: ruinas de los senderos y ruinas oceánicas cálidas (arqueología), fortaleza del Nether, fortaleza (pasillos y
biblioteca), recompensas de cámaras de desafío y ciudades antiguas. Espacios: Trinkets `feet/shoes`, `head/hat` y
`chest/back`; Curios `feet`, `head` y `back`. **Sin probar en el juego**: el doble salto, la égida al bloquear, el
óbolo al morir (también desconectándose en la pantalla de muerte) y la lira.

## Segunda tanda de Grecia (programada el 2026-11-09)
Diseño aprobado en `docs/ideas.md`. Código en `greece/GreeceHeroes` (constantes y lógica: Ícaro en el servidor,
vellocino, Cerbero y el daño del Estigia), `PandoraJarItem`, `StyxWaterItem` (da el efecto `mythicrelics:styx_bath`,
registrado en `registry/ModEffects`) y `PrometheusFennelItem` (cocina con las recetas de horno). Entidad nueva
`entity/Cerberus` (aliado de `Minions`, tres mordidas por ataque) con `client/entity/CerberusModel` (tres cabezas y cola de serpiente, como en Apolodoro) y su
textura de `tools/model-gen/cerberus_texture.py`. Ícaro: el planeo y los aleteos los decide el cliente
(`client/RelicClientTicks`) y los avisa con el paquete `mythicrelics:icarus`; la cera derretida es la recarga del ítem de las
alas (el cliente la ve). Si llevas las sandalias de Hermes, el primer salto en el aire es el de Hermes y los siguientes,
aleteos. Vellocino: Héroe de la aldea renovado cada 2 s; la pepita al esquilar sale de `InteractionEvent.INTERACT_ENTITY`.
Botín: ruinas oceánicas frías (arqueología), casas de pastor, tesoros de naufragios, bastiones (cuatro tablas), mansiones,
ciudades antiguas y portales en ruinas. Espacios: Trinkets `chest/back`, `chest/cape` (nuevo en la lista de la entidad)
y `chest/necklace`; Curios `back`, `body` (nuevo) y `necklace`. **Sin probar en el juego**: el planeo (sensación y daño
de caída), el derretido, las mordidas seguidas del Cerbero, los vexes de Pandora y la dirección del talón del Estigia.

## Primera tanda de Roma (programada el 2026-11-09)
Diseño aprobado en `docs/ideas.md` (solo mito y leyenda propios de Roma). Código en `rome/`: `RomeRelics` (constantes y
lógica: bulla y lobos, Ancile, aparición de monstruos, candados en uso y rotura, Vesta, las dos caras de Jano),
`Palladiums` y `JanusLocks` (datos guardados con el mundo), `PalladiumBlock` + `PalladiumItem` (primer bloque del mod,
`registry/ModBlocks`), `JanusKeyItem`, `SibyllineBooksItem` y `AugurLituusItem`. El Ancile es un ítem con el componente
`blocks_attacks` sin durabilidad (bloquea como el escudo y nunca se rompe); su modelo 3D (`tools/model-gen/rome_models.py`)
usa las coordenadas y los `display` del escudo vanilla, y en el inventario se ve el ícono. Mixins nuevos:
`applyItemBlocking` (devolver proyectiles) y `canFreeze` en `LivingEntityMixin`, `causeFoodExhaustion` en `PlayerMixin`,
y `BreakDoorGoalMixin` + `DoorInteractGoalAccessor` (los zombis no rompen puertas con llave). Cambio sobre el diseño: la
llave cierra y abre con **agachado + clic derecho** (sin agacharse, el clic abre la puerta antes de que actúe el ítem).
**Sin probar en el juego**: cómo se ve el Ancile en la mano y al bloquear, el rebote de las flechas, el Paladio (modelo y
que no aparezcan monstruos), los candados (también cofres dobles y puertas) y la búsqueda de estructuras de la sibila.

## Primera tanda nórdica (programada el 2026-11-09)
Diseño aprobado en `docs/ideas.md`. Código en `norse/`: `NorseRelics` (constantes y lógica: set de Thor, Járngreipr y
Megingjörð por atributos y daño, Gleipnir con la etiqueta `mythicrelics.bound` que se suelta sola, el tick de Draupnir),
`DraupnirNights` (cuenta de noches guardada con el mundo), `ThrownRelicItem` (Mjölnir y Gungnir: se cargan como un
tridente), `GjallarhornItem`, `GleipnirItem` e `IdunnApplesItem` (componentes `idunn_apples` e `idunn_day`). Entidad nueva
`entity/ThrownRelic` (vuelve como un tridente con Lealtad; Mjölnir llega a 24 bloques y llama un rayo en tormenta o con el
set; Gungnir persigue al mob al que se apuntó), dibujada con `ThrownItemRenderer` (el ícono de cara a la cámara; un
renderizador que lo gire en el aire queda pendiente). Mjölnir y Gungnir no se rompen (`unbreakable`) y usan
`item/handheld`. **Sin probar en el juego**: el lanzamiento y la vuelta, el rayo, la puntería de Gungnir, el aumento de
Draupnir al amanecer y la cesta de Iðunn al pasar los días.

## Segunda tanda nórdica (programada el 2026-11-09)
Diseño aprobado en `docs/ideas.md`. Código en `norse/NorseGods` (constantes y lógica: Brísingamen, Mímir, la maldición y el
oro de Andvaranaut, los botes de Skíðblaðnir que se pliegan solos) y los ítems `SleipnirHorseshoeItem`, `OdinRavensItem`,
`SkidbladnirItem` y `OdrerirItem`. **Sleipnir** es una entidad propia (`entity/Sleipnir`, subclase de `AbstractHorse`):
domado, ensillado y solo de su dueño, no se guarda con el mundo, vuelve a Asgard a los 3 s sin jinete, sin daño de caída
y camina sobre el agua (`canStandOnFluid`). Su modelo (`client/entity/SleipnirModel`) usa la geometría del caballo
vanilla con dos pares de patas más, bardas de hierro rúnico (testera, peto, faldones, silla con perilla), crin trenzada y
cola en dos tramos que se mece; texturas de 128x64 (`tools/model-gen/sleipnir_texture.py`: la normal y `sleipnir_glow`,
que dibuja una `EyesLayer` para que runas y ojos brillen). Al galopar suelta escarcha y chispas (salpicaduras en el agua). El viento a
favor de Skíðblañir lo aplica el cliente (`RelicClientTicks`). Mixin nuevo: `PiglinAiMixin` (trueques dobles con
Andvaranaut). **Sin probar en el juego**: montar a Sleipnir y su velocidad sobre el agua, que se vaya al bajarte, el bote
que se pliega, el doble de oro y de trueques, los avisos de Mímir y el crecimiento con Brísingamen.

## Primera tanda china (programada el 2026-11-10)
Diseño aprobado en `docs/ideas.md` (páginas en `tools/design-page/china_*.py`). Código en `china/ChinaRelics` (constantes,
daño, piedra y ascenso lunar) y los ítems `RuyiJinguBangItem`,
`MonkeyHairsItem`, `PurpleGoldGourdItem` (el mob va en `custom_data` del ítem), `DragonPearlItem`, `HouYiBowItem` (subclase de `BowItem`, con los tres cuadros tensados)
y `ChangeElixirItem` (fase de la luna con `EnvironmentAttributes.MOON_PHASE`). Entidades nuevas: `entity/MonkeyWarrior`
(aliado de `Minions`) y `entity/LongDragon` (montura voladora que se maneja como el ghast feliz). Su geometría y texturas
salen de `tools/model-gen/china_entities.py --mod` (escribe `client/entity/ChinaModelLayers.java`, generado, y las
texturas con la capa que brilla), el mismo diseño que el visor de aliados. Íconos: `tools/model-gen/china_icons.py`.
El abanico de hoja de plátano y las ruedas de viento y fuego se borraron el 2026-11-15.
**Sin probar en el juego**: montar y manejar al long (subir con salto, bajar mirando abajo, nadar), que el jinete no
quede mal ubicado sobre el lomo ni el cuerpo se vea cortado; los monos (aparición, pelea, que se vayan); capturar,
soltar y disolver con la calabaza (y que se conserve al salir y volver); el arco (daño x3, doble botín, tensado); el
elixir según la luna; el bastón estirado; reparación de la piedra; la piedra puesta en los dos loaders.

## Segunda tanda china (programada el 2026-11-10)
Diseño aprobado en `docs/ideas.md` (páginas en `tools/design-page/china2_*.py`; los íconos son los del primer borrador,
que el usuario prefirió). Quedan los talismanes fu (`FuTalismansItem`; el papel se ve pegado en la frente) y el saco de
xirang (solo pasiva: te salva de la lava o el vacío, recarga 5 min). Código en `china/ChinaGods`. Íconos:
`china2_icons.py`. El resto de la tanda se borró el 2026-11-15 (ver «Reliquias borradas»).
**Sin probar en el juego**: los talismanes (que el no-muerto quede quieto y el papel se despegue al vencer) y el rescate
del xirang.

## Reliquias borradas (2026-11-15)
Decisión del usuario: se quitaron del todo (código, recursos, libro, wiki, botín, textos y herramientas).
- **China** (9): lanza de punta de fuego, aro del universo, ojo y alabarda de Erlang Shen (con el set de Erlang y el
  Perro Celestial), cuerno del qilin (y la montura qilin con su `FarmlandBlockMixin`), abanico de hoja de plátano,
  ruedas de viento y fuego, pulsera del pixiu y rama del melocotonero (componente `peach_growing`).
- **Japón entero** (19): Kusanagi, espejo Yata, magatama Yasakani, tambor de Raijin, kanabō, esfera del kitsune, joyas
  de la marea, mazo de la suerte, kibi dango, caña de Urashima, bolsa de Fūjin, máscara de tengu, sake Yashiori, plato
  del kappa, hoja del tanuki, hacha de Kintarō, muñeca de zashiki-warashi (bloque), Ame-no-nuboko y escama de Orochi;
  los sets de los Tres Tesoros y de Fūjin y Raijin; el kitsune, la tortuga de Urashima y la sombra de Orochi; la
  pestaña creativa, la categoría del libro y la página de la wiki.
- Con ellas se fueron `RingPayload` y la tecla R, `FoxAccessor`, `relic/RelicMotion`, `RomeRelics#reflectLater`,
  `CharonStash#keepTreasure`, los componentes `kibi_dango` y `kibi_day`, y las herramientas `china2_entities.py`,
  `japan_entities.py`, `japan_icons.py`, `japan2_icons.py` y los borradores `japan*_draft.py`.
- El doble salto vuelve a ser solo de las sandalias de Hermes; el rayo con bono de set, solo el de Thor.
- Las tablas de botín de herrerías de herramientas y mapas de naufragios quedaron sin reliquias.

## Arreglos tras la primera prueba en NeoForge (2026-11-10)
- Objetivos negados en el origen (`relic/TargetVeto` + `mixin/MobMixin` en `setTarget`/`getTarget`): set de Anubis,
  vendas de momia, lira de Orfeo y hoja del tanuki ya no dejan que el mob apunte y dispare entre limpiezas.
- Doble salto (Hermes y tengu): pide soltar y volver a pulsar en el aire (antes se disparaba al mantener el primer salto).
- Recargas: las pasivas (escarabajo, anillo de chacal, uraeus, bula) también se muestran en el ícono; el tooltip dice
  cuánto falta (`client/RelicTooltips`).
- Tooltip: etiqueta de mitología bajo el nombre (`relic/RelicCultures`, generado por `make_codex.py`) y si el set está
  activo; los sets avisan al activarse y al romperse (`relic/RelicSets`).
- Escarabajo dorado: también salva de un golpe mortal (mismo gancho que el tótem y Osiris).
- Cabeza de Medusa rehecha: cono de 100° a los hostiles, se ven de piedra (`PetrifyPayload` + `mixin/client`), si mueren
  así se derrumban con doble botín y 15 % de soltar su estatua (`entity/StoneStatue`, `StoneStatueItem`).
- Óbolo de Caronte: también guarda los accesorios puestos (Curios `DropRulesEvent`, Trinkets `TrinketDropCallback`).
- Cerbero a 1,7 veces; sorbos al beber el agua del Estigia; el hinojo funde el stack entero (1 s de recarga por objeto).
- Para ver la vida de los mobs se recomienda Jade (cliente; hay versión 26.3 para NeoForge).

## Arreglos tras la segunda y tercera prueba (2026-11-10)
- Monturas: en 26.3 el servidor no deja montar entidades `noSave`; Sleipnir, qilin, long, tortuga y Skíðblaðnir se
  guardan como tipo pero se descartan con `shouldBeSaved() = false`.
- Impulsos al jugador desde el servidor (`relic/RelicMotion`): hay que mandarle el paquete de movimiento.
- Ayudantes comunes en `relic/` (refactor del 2026-11-14): `WornRelics.isWearing` (también con `RegistrySupplier`) y
  `holding`; `Allies.isFriend`/`enemiesAround` (dueño, mascotas, aliados de `Minions` y mismo equipo); `Bosses.is` (tag
  `mythicrelics:bosses`, con `#c:bosses` opcional); `Compass.direction`; `TooltipNumbers` (`fmt`, `fmtTenths`, `roman`);
  `RelicFx.sound`/`notify` (avisos sobre la barra); `RelicCooldowns.start(jugador, ítem, ticks)` guarda con el mundo las
  recargas de 10 s o más; `RelicSets` declara por pieza dónde cuenta (`worn`, `inventory`, `hand`, `mainHand`,
  `bearer`) y el tooltip marca ✔ / ◐ (la llevas en otro lugar) / ✘; `TooltipArgs` (cada cultura registra sus números
  en `registerTooltipArgs()`); `DamageModifiers` (el daño recibido, en orden); `RelicItem`.
- Libros sibilinos con mapa de la estructura (sin repetir) y rastro de letras; Mímir con contornos (Display de bloque
  que brilla) de minerales y cofres, se le pregunta con clic derecho y guía 30 s con una estela; candado de Jano (Display de ítem `padlock`); fuego del hogar de Vesta.
- Cuervos de Odín (`entity/OdinRaven`): mascotas que se posan cerca y miran al dueño; hostiles brillan mientras están.
- Skíðblaðnir: bote propio (`entity/SkidbladnirBoat`, capa `boat/skidbladnir` con vela y proa de dragón).
- Mjölnir y Gungnir con modelo 3D en la mano y en vuelo (`norse_models.py`, `ThrownRelicRenderer`); Paladio rehecho.
- Ruyi estirado 10 s (marca de `custom_model_data`, modelo `ruyi_jingu_bang_extended`); abanico como arma de viento;
  kanju en burbuja; mazo con fuerza, salto y paso; xirang rescata de la lava o el vacío.
- Activas al menos al doble del golpe básico; aliados atacan al hostil más cercano; una pestaña creativa por mitología;
  bloque de set igual en todas las piezas (`set.mythicrelics.<set>.bonus.N`).
- Revisión de modelos: el visor global (`make_viewer.py`).

## Cuarta ronda de arreglos (2026-11-11)
- Estatua de piedra: en 26.3 `move()` lanza `IllegalStateException` en esta entidad; ya no se mueve nunca y cae como
  ítem si le quitan el suelo. Regla general: no llamar `move()` en entidades propias que no sean `LivingEntity`.
- Jade 26.3.5 (Modrinth) como `localRuntime` en Fabric y NeoForge y declarado opcional (ver vida de los mobs).
- Ícono del mod: copia `mythicrelics_icon.png` en la raíz de recursos de cada loader (en desarrollo no veía el de common).
- Yelmo de Hades rehecho (corintio, abertura en T, cimera de crin); sin planos coplanares (`coplanar_check.py`).
- Sleipnir y qilin: `isSaddled() = true` y `getControllingPassenger()` devuelve al jinete (no respondían ni saltaban).
- Cuervos: `setNoAi(true)` (el `travel` de `Mob` los movía además de nuestro `setPos` y giraban sobre sí mismos).
- Gungnir con la geometría y los `display` del tridente vanilla (`trident_in_hand` y `trident_throwing`, con scale 2:
  ver `tri()` en `norse_models.py`); en vuelo `ThrownRelicRenderer` lo centra y lo dobla igual que el tridente.
- Paladio sin caras coplanares del mismo lado; candado de Jano en el punto exacto del clic; mástil de Skíðblaðnir a popa.

## Pestaña creativa
Una pestaña por mitología, cada una con una de sus reliquias como ícono (`ModItems`, `itemGroup.mythicrelics.<cultura>`).
El ícono del mod y el emblema (`mythicrelics:emblem`, la reliquia en su altar) salen de `tools/model-gen/mod_icon.py` con
`emblem_relic.py`; el ítem del emblema no está en ninguna pestaña ni tiene botín, y JEI no le pone página.

## Ítems en la mano
Las armas y bastones usan `minecraft:item/handheld` y se dibujan en diagonal (mango abajo a la izquierda, punta arriba a
la derecha), como las herramientas vanilla: así quedan bien orientados en la mano. Las páginas de diseño muestran el ítem
en la mano con la misma cadena de transformaciones del juego (`tools/design-page/template.html`). Los ítems 3D que se
sostienen calcan la geometría de referencia del vanilla (escudos centrados en el origen con la placa en z 1..2; cabezas
como `template_skull`).

## Herramientas y pruebas
- **El juego no se abre en el PC de desarrollo**: aquí solo se compila (`./gradlew build`). El usuario prueba en su PC
  después de subir a `main`.
- `tools/model-gen/egypt_icons.py`: genera los íconos 16x16 de Egipto (con brillo animado).
- `tools/model-gen/mod_icon.py`: ícono del mod (`icon.png` y `mythicrelics_icon.png` de los dos loaders, 128 px) y
  emblema de la pestaña creativa (`textures/item/emblem.png`, 16 px): la reliquia en su altar sobre un disco de cielo
  con borde de oro (dibujo en `emblem_relic.py`, elegido el 2026-11-14). Es un símbolo propio del mod, no uno por
  cultura: no hay que tocarlo al sumar culturas.
- `tools/model-gen/scarab_texture.py`: textura del escarabajo aliado (requiere Pillow).
- `tools/model-gen/make_codex.py`: genera el libro de Modonomicon (`data/mythicrelics/modonomicon/books/relics/`),
  sus textos con los números ya puestos (claves `book.mythicrelics.relics.entry.*` en los lang) y la wiki
  (`docs/wiki/`). El mito de cada reliquia se escribe a mano en los lang (`lore.mythicrelics.*`). **Correrlo cada vez que
  cambien números, botín, tooltips o mito.**
- `tools/model-gen/greece_icons.py`: íconos de Grecia (como `egypt_icons.py`).
- `tools/model-gen/worn_models.py`: modelos 3D de las reliquias puestas, su paleta y `trinkets/*.json`.
- `tools/model-gen/jackal_texture.py` y `cerberus_texture.py`: texturas del chacal y del Cerbero (requieren Pillow).
- `tools/model-gen/make_viewer.py <salida.html>`: visor three.js con la pestaña creativa, los tooltips, la página de
  JEI y el escarabajo en 3D, armado con los mismos archivos del mod. Se publica como Artifact para revisar sin el juego.
- `tools/design-page/`: plantilla y ejemplo de las páginas de diseño de cada tanda (ver su README).
- `tools/model-gen/coplanar_check.py`: busca caras coplanares entre reliquias del cuerpo que se pueden llevar juntas
  (parpadeo). Correrlo después de `worn_models.py` y agregar las reliquias nuevas a sus grupos.
- `tools/model-gen/rome_icons.py`, `norse_icons.py`, `rome_models.py` (Ancile y
  Paladio), `cerberus_texture.py`, `sleipnir_texture.py`, `china_icons.py`, `china_entities.py`, `china2_icons.py`
  (visor de aliados y, con `--mod`, la geometría en Java y las texturas del mono y del long).
- `worn_texture.py`: textura pintada por cara (luz, sombras, ruido y patrón del material) para todas las reliquias
  puestas, y `paint_models.py` para los modelos 3D hechos con paleta (escudos, Mjölnir, Gungnir, Ruyi, Paladio,
  Zashiki-warashi); los generadores lo llaman al final. `head_items.py`: cabezas de Medusa y Mímir en 3D.
  `weapon_entities.py`: armas en vuelo. `petrified_textures.py`: piedra agrietada de lo petrificado.
- Textos: el único script vigente es `feedback4_lang.py` (los `*_lang.py` de tandas viejas se borraron: revertían textos).
- JEI es dependencia opcional (maven de BlameJared, API común); en desarrollo corre en ambos loaders.

## Grecia 3 y nórdicos 3 (programadas el 2026-11-14)
Diseño aprobado en las páginas de diseño de la tanda (no van a git; fichas en `docs/ideas.md`). Lógica en `greece/GreeceLegends`
(constantes, daño, cólera de Aquiles, aviso de la harpe, manzana, Talos) y `norse/NorseSagas` (Gram, Tyrfing,
Dáinsleif, espada de Freyr, halcón, dardo); los dos encadenan `tooltipArgs` (JapanGods → GreeceLegends → NorseSagas) y
`modifyIncomingDamage` en `LivingEntityMixin`, que además cancela `heal` mientras dura la herida de Dáinsleif.
- Ítems: `ZeusThunderboltItem` (carga 0,5 s, rayo visual del vanilla + daño aparte, salta a 3/5 hostiles; ataque
  fuerte: mantener 2 s, con mini rayos alrededor, suelta 3 rayos con su daño y su salto y recarga 10 s), `HarpeItem`
  (pasiva: decapita al golpear bajo el 25 %, 1 s de recarga, cabeza 75 %; en `GreeceLegends.onDeath`), `GoldenBridleItem`,
  `AppleOfDiscordItem`, `TalosNailItem`, `GramItem` (estocada cargada 1 s); Tyrfing, Dáinsleif, la piel, el escudo
  (`BLOCKS_ATTACKS` como el Ancile), la espada de Freyr y el manto son `relic(...)`; el dardo es un `ThrownRelicItem`
  con su rama en `ThrownRelic` y el tipo de daño `mythicrelics:mistletoe` (data + tags `bypasses_armor/resistance/
  enchantments`).
- Entidades: `Pegasus` (vuela con salto, planea, picada de 12, tábano sobre Y 200), `Talos` (aliado de `Minions`, rocas
  = bola de nieve con la etiqueta `mythicrelics.talos_rock`, abrazo con fuego, da vueltas al dueño), `FreyrSword` (Mob
  sin IA movido con `setPos` y seguimiento amortiguado; posición cada tick). El halcón ya no es entidad: `client/FalconForm` dibuja su modelo en lugar del jugador. Geometría en `greece_entities.py` y `norse_entities.py` (halcón), exportada por `china_entities.py --mod`.
- Halcón: la tecla V (`RelicClientTicks.FALCON_KEY`, configurable) manda `FalconPayload`; el servidor da vuelo 20 s y encoge
  al jugador con el atributo `scale` (modificador `falcon_form`, que también marca al cliente para dibujar el ave).
- Set de Perseo: ahora 4 piezas (con la harpe en la mano o el inventario), se activa con 3.
- Íconos: `greece3_icons.py` (incluye el escudo 3D, como el Ancile) y `norse3_icons.py`; puestos en `worn_models.py`
  (piel con la cabeza del león colgando detrás de la nuca, espada envainada al frente de la cadera, manto).
- Botín nuevo: armeros de aldea (`VILLAGE_WEAPONSMITH`) para Gram.

## Después
1. **Seguir probando en el juego** (NeoForge en el PC del usuario, con Jade para ver la vida). Lo pendiente de confirmar
   tras la cuarta ronda:
   - Montar y controlar Sleipnir y el long (correr, saltar, nadar); Skíðblaðnir (vista, vela, plegado).
   - Cuervos de Odín: que se posen y se queden quietos mirando; graznido ocasional; hostiles brillando. Las alas
     plegadas se rehicieron el 2026-11-12 (antes colgaban hasta el suelo y parecían patas): confirmar que se ven bien.
   - Gungnir en la mano, al cargar y en vuelo; Mjölnir en la mano y en vuelo (sus `display` son estimados); Ruyi estirado.
   - Estatuas de Medusa: colocar, romper, que no crasheen; textura de piedra sobre los mobs petrificados.
   - Paladio (modelo y parpadeo), candado de Jano en puertas, trampillas, vallas y cofres, yelmo de Hades puesto.
   - Set de Anubis y lira (que los mobs no disparen), doble salto de Hermes, recarga y bloque de set en los
     tooltips, pestañas por mitología, ícono del mod en la lista.
   - Lo de las rondas de arreglos.
   - Grecia 3 y nórdicos 3 (ver su sección): rayo (salto entre enemigos, sin fuego), piel de Nemea, cólera del escudo
     y que bloquee bien en la mano, harpe (aviso, decapitar, cabezas, set de Perseo con 3 de 4), Pegaso (despegar,
     planear, picada, tábano), manzana, Talos (rocas, abrazo, vueltas, tobillo); Gram (estocada y aviso), Tyrfing
     (críticos, chispas, maldición), Dáinsleif (sin curación y sangrado), espada de Freyr (sale, pelea, vuelve; que se
     vea suave y bien orientada en el renderizador), halcón (activar en el aire, volar, volver al ser golpeado) y dardo
     (orientación en vuelo, daño que ignora armadura). Recarga nueva del Ruyi.
   - Armas en vuelo con modelo 3D (`WeaponModel`, geometría de `weapon_entities.py`): espada de Freyr, Mjölnir,
     Gungnir y dardo ya no se ven como el ícono en el aire. Revisar que la punta vaya hacia adelante, el tamaño, que
     Mjölnir gire de punta a punta y que brillen el canal de la espada y las runas de Gungnir.
   - Cuarta ronda de arreglos (2026-11-14): escudos de Aquiles y Ancile en la mano y al bloquear (posición del vanilla);
     Sleipnir galopa sobre el agua; la espada de Freyr sale y vuelve por la vaina izquierda; Mímir marca minerales y
     guía con estela y aviso sobre la barra (sin chat); cuervos: el brillo de los hostiles sigue aunque se alejen;
     saco de xirang solo pasiva (5 min); harpe pasiva (bajo 25 %, 1 s, cabeza 75 %); cetro de Anubis avisa a quién
     puede juzgar; estatua de Medusa sin crash; halcón con la tecla V (se encoge, se ve como el ave, sin capa ni
     manos; la misma tecla vuelve); carga visible del rayo, Gram, Mjölnir, Gungnir y el dardo; piel de Nemea nueva.
   - Rayo de Zeus fuerte: mantener 2 s (mini rayos a tu alrededor y trueno al estar listo), soltar 3 rayos con su daño
     y salto, recarga 10 s; el débil sigue igual.
   - Arte del 2026-11-14: alas de Ícaro nuevas (en abanico, echadas hacia atrás) y las 25 reliquias puestas no griegas
     con más detalle (Egipto, Roma, nórdicos y China): revisarlas en NeoForge (Curios), que la prueba automática
     solo cubre Fabric.
   - Tras el refactor del 2026-11-14 (no debería cambiar nada del juego): tooltip de sets con ✔ / ◐ / ✘ (por ejemplo el
     anillo del chacal en el inventario sale ◐ «póntela»); recargas largas (Ruyi, monos, elixir…) que siguen en el ícono al salir y volver a entrar; un vistazo general a avisos y sonidos.
   - Tooltip corto con el resumen nuevo de cada reliquia (lo importante y la recarga); con Shift, el detalle.
   Después, probar también en Fabric (Trinkets).
2. **Cuando el usuario apruebe las pruebas: Chile/mapuche** como tanda especial (los celtas se descartaron el
   2026-11-10). Flujo: enseñar el mito, proponer reliquias (fiel / licencia / pop), página de diseño, preguntas con
   opciones, entidades 3D, íconos, programar, commit y push, visor global, lista de pruebas.
3. Pendientes posibles: las Cuatro Bestias Celestiales chinas como set; luego India, aztecas, eslavos, Mesopotamia
   (`docs/ideas.md`).
4. Portar Misfit Relics a Architectury (mismo esquema de módulos).

## Cómo trabajar (recordatorio)
- No abrir el juego en este PC: solo `./gradlew build` (con conexión la primera vez que cambia una dependencia).
- Push con la cuenta Friedpotatu (ver memoria «push-friedpotatu»).
- Modelos y texturas se generan con `tools/model-gen/*.py` (no editar los JSON ni `client/entity/geometry/*Geometry.java` a mano);
  textos con `feedback4_lang.py` (los resúmenes del tooltip corto, `item.mythicrelics.<id>.summary`, con
  `summaries_lang.py`: una reliquia nueva necesita el suyo); libro y wiki con `make_codex.py` (también genera `relic/RelicCultures.java`).
- Visor global: `python tools/model-gen/make_viewer.py tools/design-page/viewer_check.html` y publicarlo en
  https://claude.ai/artifact/KmBKXReYGwHWo2DGQRyHgh al cerrar cada tanda.
- Revisiones visuales: Chrome sin ventana sobre una copia del HTML con la escena a pantalla completa (ver memoria
  «revisar-modelos-3d»).
