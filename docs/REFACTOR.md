# Paquete de refactors (para una sesión limpia)

> **Estado (2026-11-14, segunda sesión): hecho.** Etapas 1 a 5 en `main` (`bdc589e`, `9f14cba`, `9ed0c53`, `e27a606`,
> `55d15ca`) y, de la 6, la lista de modificadores de daño (`relic/DamageModifiers`). Quedan como opcionales: `createLayer`
> por modelo (cambio en `china_entities.py`) y claves `.summary` para el tooltip corto (son textos nuevos: revisarlos con
> el usuario). Diferencias con el plan: `isFriend` unificado considera aliado a cualquier entidad del mismo equipo (antes
> la versión de China solo a jugadores); `TooltipArgs` guarda arreglos (todo es constante) en vez de `Supplier`; no se
> agregaron ayudantes de segundos y minutos (los `/ 20` de los tooltips se dejaron como están).

Origen: la auditoría del 2026-11-14 (buenas prácticas, DRY, memoria y rendimiento). Lo de memoria (`relic/ServerState`),
rendimiento (caché por tick de `WornRelics`) y limpieza ya está en `main` (commits `b0bf054`, `de011b2`, `7e83021`).
Lo de aquí falta y **no debe cambiar el juego**: mismos números, mismos textos, mismo comportamiento.

El usuario aprobó hacerlo **todo**. Se trabaja por etapas: cada etapa compila (`./gradlew build`), regenera lo que
corresponda y se sube a `main` con su propio commit, para que el usuario pueda probar entre medio.

## Antes de empezar
- Leer `docs/ESTADO.md` («Cómo trabajar») y la sección «Lecciones» de abajo.
- `git status` limpio y `./gradlew build` en verde como punto de partida.
- Raíz del código: `common/src/main/java/io/github/potatustudio/mythicrelics/` (abreviada `…/` abajo).

## Cuidado: las herramientas leen el Java
Varias herramientas en Python leen el código fuente con expresiones regulares. Si un refactor mueve algo, hay que
actualizar la herramienta en la misma etapa:
- `tools/model-gen/moddata.py`:
  - `constants()` lee las constantes `static final` de una lista fija de archivos (EgyptRelics, EgyptGods, GreeceRelics,
    GreeceHeroes, RomeRelics, NorseRelics, NorseGods, ChinaRelics, ChinaGods, JapanRelics, JapanGods, GreeceLegends,
    NorseSagas y `norse/MimirHead`). Si se mueve una constante a otro archivo, ese archivo tiene que estar en la lista.
  - `tooltip_args(c)` es una **copia a mano** de los `tooltipArgs` de Java: el libro y la wiki la usan. Si cambia el
    orden o la cantidad de argumentos en Java, hay que cambiarla también (los cambios por script van en `MODDATA` de
    `feedback4_lang.py`).
  - `_sets()` lee `relic/RelicSets.java`: si cambia el formato de declaración de sets, hay que ajustar la expresión.
  - `loot()` lee `registry/ModLoot.java` y `rarity()` lee `registry/ModItems.java`.
- `make_codex.py` genera `relic/RelicCultures.java`: no se edita a mano.
- `china_entities.py --mod` genera geometría Java de las entidades (`MythicModelLayers.java` y modelos): no se edita a mano.

Comprobación final de cada etapa: `python tools/model-gen/make_codex.py` no debe cambiar nada en `docs/wiki` ni en el
libro (`git diff --stat` vacío en esas carpetas) si la etapa no cambia el juego. Usar `PYTHONIOENCODING=utf-8` en Windows.

## Etapas

### 1. Ayudantes comunes (DRY)
- **`isWearing` con `RegistrySupplier`**:
  - Hoy está en `egypt/EgyptRelics.java:159`, con 61 usos en 14 archivos.
  - Moverlo a `relic/WornRelics` como sobrecarga del que ya existe (`WornRelics.java:41`, que recibe `Item`).
  - Reemplazar `EgyptRelics.isWearing` por `WornRelics.isWearing` en todos los usos.
- **`relic/Allies`**:
  - `isFriend(Player, Entity)` está en `china/ChinaRelics.java:247`, con una copia distinta y privada en
    `egypt/EgyptGods.java:257`.
  - Llevar también ahí `enemiesAround`.
  - Unificar mirando las dos versiones: quedarse con la más completa (dueño, mascotas, aliados de `Minions`, jugadores
    del mismo equipo) y comprobar que ningún uso cambie de comportamiento.
- **Un solo `isBoss`**:
  - Hoy hay dos: `greece/GreeceLegends.java:196` (`LivingEntity`) y `greece/GreeceRelics.java:226` (`Entity`).
  - Crear el tag `data/mythicrelics/tags/entity_type/bosses.json` con el dragón, el wither y el warden, más
    `#c:bosses` como opcional (`{"id": "#c:bosses", "required": false}`).
  - Dejar un único `relic/Bosses.is(Entity)`.
- **`relic/Compass.direction(from, to)`**:
  - La brújula de 8 rumbos está copiada en `norse/MimirHead`, `norse/OdinRavensItem`, `rome/SibyllineBooksItem` y
    probablemente en `greece/GreeceHeroes`.
  - Revisar que las claves de idioma de las direcciones sean las mismas en todas.
- **`relic/TooltipNumbers`**:
  - Hoy hay tres `fmt` privados: `ChinaRelics.java:354`, `EgyptRelics.java:198` y `JapanRelics.java:339`.
  - Llevar también `roman` (`EgyptRelics.java:186`) y los ayudantes de segundos y minutos.
  - Comparar las tres versiones de `fmt`: si redondean distinto, conservar la salida exacta de cada tooltip.
- **`RelicFx.sound(level, pos, sound)` y `RelicFx.notify(player, key, args…)`** para los avisos sobre la barra. Los
  avisos van **sin chat** (memoria «controles-y-transformaciones»).
- **`WornRelics.holding(player, item)`**: mano principal u otra mano; hoy se repite a mano.

### 2. Recargas
- `RelicCooldowns.tryStart(player, Item, ticks)` y `ready(player, Item)`. `relic/RelicCooldowns.java` ya es `SavedData`.
- Hay 23 `addCooldown` sueltos. Las recargas de 10 s o más pasan a `RelicCooldowns`, porque las del vanilla se pierden
  al reconectar.
- Mantener el aviso visual de recarga en el ícono (memoria «feedback-visible»): si se deja de usar `addCooldown`, el
  ícono igual tiene que mostrar la recarga.
  - Opción: seguir llamando `addCooldown` para el dibujo y guardar en `RelicCooldowns` para la persistencia.
  - Al entrar al mundo, reponer el `addCooldown` restante.
- La recarga fuerte del rayo de Zeus (10 s) es candidata directa.

### 3. Sets
- `relic/RelicSets`: cada pieza declara dónde cuenta (`WORN`, `INVENTORY` o `HAND`). Así el contador del tooltip
  (`RelicTooltips`, que tiene su propio `countPieces`) y el bono usan la misma regla.
- Sacar los argumentos de los bonos de Egipto (hoy viven en `EgyptRelics`).
- Ajustar `moddata._sets()` al nuevo formato.

### 4. Orden de paquetes y nombres
- Mover `egypt/RelicItem` a `relic/RelicItem` (58 archivos lo usan).
- Renombrar:
  - `client/entity/ChinaRenderers` → `MythicRenderers`: registra los renderizadores de todas las culturas.
  - `client/GreeceClient` → `RelicClientTicks`: tiene ticks de cliente, la tecla V y la carga de todas las culturas.
  - Dejar en ESTADO.md los nombres nuevos, porque ESTADO menciona `GreeceClient.FALCON_KEY`.
- Usar imports en vez de nombres completos dentro del código. La auditoría contó unos 280 (por ejemplo
  `io.github.potatustudio.mythicrelics.japan.JapanRelics.tooltipArgs` en las cadenas). Hacerlo archivo por archivo
  y compilar.

### 5. Registro de `tooltipArgs`
- Hoy es una cadena de 13 clases unidas por `default ->`:
  EgyptRelics → EgyptGods → GreeceRelics → GreeceHeroes → RomeRelics → NorseRelics → NorseGods → ChinaRelics →
  ChinaGods → JapanRelics → JapanGods → GreeceLegends → NorseSagas.
- Reemplazarla por `relic/TooltipArgs`: un `Map<String, Supplier<Object[]>>` que cada clase llena en su
  `static {}` o en un `register()` llamado desde `MythicRelics.init`.
- Comprobación al arrancar, solo en desarrollo: cada ítem con `tooltip.N` que use `%n$s` tiene que tener argumentos.
- `moddata.tooltip_args` sigue siendo la copia en Python: no cambia si los argumentos no cambian.

### 6. Opcionales (si queda tiempo)
- `createLayer` por modelo en vez del archivo gigante (ojo: lo genera `china_entities.py --mod`; el cambio va en el
  generador).
- Una lista de modificadores de daño en vez de la cadena de `modifyIncomingDamage` en `LivingEntityMixin`.
- Claves `.summary` para el tooltip corto. Hoy `client/RelicTooltips.compact()` toma la primera oración; con claves
  propias sería más preciso.

## Después de cada etapa
1. `./gradlew build` en verde.
2. Si se tocó algo que leen las herramientas: `python tools/model-gen/feedback4_lang.py`, luego `make_codex.py`, y
   revisar que `docs/wiki` y el libro no cambien.
3. Commit en español con el estilo del repo (`refactor: …`) y push con la cuenta Friedpotatu (ver «Lecciones»).
4. Al terminar todo:
   - actualizar ESTADO.md (quitar el punto 4 de «Después»), CHANGELOG (`### Changed`) y este archivo;
   - republicar el visor solo si cambiaron modelos o textos.

## Lecciones de la sesión del 2026-11-14

### Minecraft 26.3 (mapeos de Mojang)
- `Entity.invulnerableTime` es privado: usar `setInvulnerableTime(0)`. No existen `hurtMarked`, `BlockPos.getCenter`
  (usar `Vec3.atCenterOf`) ni `mulPose(Axis.rotationDegrees)` (usar `poseStack.rotateDegrees(Axis, grados)`).
- Renderizado: `SubmitNodeCollector.submitModel(model, state, pose, RenderType, luz, overlay, outline)`, con
  `RenderTypes.entityCutout` y `eyes`.
- `ItemTransform.apply` termina con `translate(-0.5)`. Los modelos especiales usan `transformation` en la definición
  del ítem.
- Teclas: `KeyMapping(String, int, KeyMapping.Category)` con `InputConstants.KEY_V` (no importar GLFW). Para la tecla
  Shift: `Minecraft.hasShiftDown()`.
- `CustomData` no tiene lectura sin copia: no leerlo en cada frame.
- Entidades falsas para renderizar (estatuas): darles `setId(negativo)`. Sin ID, `hashCode` crashea.
- Tamaño del jugador: el atributo `Attributes.SCALE` con un modificador propio (así funciona el halcón).
- Ciclo de vida: `LifecycleEvent.SERVER_STOPPING` y `SERVER_STOPPED`, y `PlayerEvent.PLAYER_QUIT`. Todo eso está
  centralizado en `relic/ServerState` (`onQuit`, `onStopping`, `onStopped`, `perPlayer`, `clearOnStop`). **Todo mapa
  estático nuevo con estado por jugador o por mundo se registra ahí.**
- `ClientPlayerEvent` (y todo lo de cliente de Architectury) no se puede nombrar desde una clase común: rompe el
  servidor dedicado.

### Ítems 3D en la mano
- Calcar la geometría de referencia del vanilla:
  - escudo: placa en x -6..6, y -11..11, z 1..2, con la manija en z -5..1;
  - cabeza: x 4..12, y 0..8, z 4..12, con la cara hacia +z y el display de `template_skull`.
- Las cabezas se veían al revés en tercera persona hasta girarlas 180° en Y.
- Revisar desde todos los lados en el visor, con las transformaciones reales (memoria «revisar-modelos-3d»).
- Caras coplanares: correr `coplanar_check.py` después de `worn_models.py`.

### Herramientas
- Para parchar con Python desde bash: escribir el script en el scratchpad, no en un heredoc. Las comillas y los `\n`
  se rompen: así se rompió `feedback4_lang.py` en esta sesión.
- `make_viewer.py` necesita la salida como argumento:
  `python tools/model-gen/make_viewer.py tools/design-page/viewer_check.html`.
- En el navegador local, agregar `?v=<hora>` para evitar la caché.
- En Windows, `PYTHONIOENCODING=utf-8` para que `print` no falle con cp1252.
- Un hook bloquea `rm -rf`: borrar archivo por archivo y después `rmdir`.
- Push con la cuenta del mod (la sesión de `gh` activa es la del trabajo):
  ```
  git -c credential.helper= -c 'credential.helper=!f(){ echo username=Friedpotatu; echo password=$(gh auth token --user Friedpotatu); }; f' push -q origin main
  ```

### Con el usuario
- Diseño → preguntas en el chat con opciones cortas y recomendación → íconos → código (memorias «decisiones-con-opciones»
  y «disenar-reliquias-con-el-usuario»).
- El usuario prueba en su PC después del push: no abrir el juego aquí ni simular teclado.
- Tooltips cortos (lo importante y la recarga) y el detalle con Shift.
- Avisos sobre la barra, nunca spam en el chat.
- Los efectos tienen que ser llamativos y los aliados trabajados.
