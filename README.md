# PotatuStudio · Mod Template

Plantilla base para los mods de **PotatuStudio**: Minecraft Java **26.3**, **Fabric**, Java 25.
Ya compila, trae CI, licencia MIT, plantilla de issues y Mod Menu para desarrollo.

## Preparar un PC
JDK, IntelliJ, Git, identidad de Friedpotatu, runClient y logs: [docs/entorno-de-desarrollo.md](docs/entorno-de-desarrollo.md).

## Crear un mod nuevo

1. **Use this template** → *Create a new repository* → owner `PotatuStudio`, nombre = slug del mod (ej. `burrow-lanterns`).
2. Clona el repo en `C:\dev\mods\<slug>`.
3. Reemplaza los identificadores. **El mod id no se cambia nunca después de publicar.**

   | Qué | En la plantilla | En tu mod (ejemplo) |
   |---|---|---|
   | Mod id (`a-z0-9_`, empieza con letra) | `modtemplate` | `burrowlanterns` |
   | Nombre visible | `Mod Template` | `Burrow Lanterns` |
   | Paquete Java | `io.github.potatustudio.modtemplate` | `io.github.potatustudio.burrowlanterns` |
   | Clases | `ModTemplate`, `ModTemplateClient` | `BurrowLanterns`, `BurrowLanternsClient` |
   | Repo | `PotatuStudio/mod-template` | `PotatuStudio/burrow-lanterns` |

   Archivos que tocar:
   - `settings.gradle` (`rootProject.name`)
   - `build.gradle` (bloque `loom.mods`)
   - `src/main/resources/fabric.mod.json` (id, nombre, descripción, entrypoints, mixins, contact)
   - `*.mixins.json` (nombre del archivo y `package`)
   - carpetas `src/*/java/io/github/potatustudio/modtemplate` y `assets/modtemplate`
   - `MOD_ID` en la clase principal
   - este README y `CHANGELOG.md`

   Búsqueda rápida de restos: `git grep -i -e modtemplate -e "mod template"`
4. Cambia `icon.png` (512×512) por el ícono del mod.
5. `./gradlew runClient` para verificar que carga (busca `<Nombre> cargado` en el log y el mod en el botón **Mods**).
6. Commit inicial `chore: proyecto inicial desde mod-template` y push.

## Desarrollo
```sh
./gradlew runClient   # juego con el mod
./gradlew runServer   # servidor dedicado (detecta código de cliente en main)
./gradlew build       # jar en build/libs/
```

## Convenciones
- Versión: SemVer + versión de MC → `0.1.0+26.3` (en `gradle.properties`).
- Commits: Conventional Commits (`feat:`, `fix:`, `chore:`, `port:`).
- Changelog: [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/).
- Soporte: desarrollo activo solo en la última versión de Minecraft.

## Licencia
[MIT](LICENSE)
