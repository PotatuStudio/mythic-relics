# Publicar en Modrinth

Guía para [modrinth.com](https://modrinth.com) (botón **+** → *Create a project*). Mismo criterio que
[CURSEFORGE.md](CURSEFORGE.md): los textos públicos van en inglés y se reutilizan los mismos jars, descripción e imágenes.
Publicar en los dos sitios está permitido (la licencia es MIT y los dos jars son nuestros).

## 1. Crear el proyecto
| Campo | Qué poner |
|---|---|
| Project type | **Mod** |
| Name | `Mythic Relics` |
| URL | `mythic-relics` (queda `modrinth.com/mod/mythic-relics`) |
| Summary | `Relics from Egyptian, Greek, Roman, Norse and Chinese myth as RPG accessories, found in fitting structures.` |
| Visibility | **Public** (no se ve hasta que la moderación lo aprueba) |

## 2. Ajustes del proyecto (Settings)
| Pestaña | Campo | Qué poner |
|---|---|---|
| General | Icon | `media/icon-512.png` |
| General | Environment | **Client: Required · Server: Required** |
| Tags | Categorías | **Adventure** · **Equipment** · **Magic** · **Mobs** (Modrinth deja hasta 3 destacadas: Adventure, Equipment, Magic) |
| License | License | **MIT** |
| Links | Issues | `https://github.com/PotatuStudio/mythic-relics/issues` |
| Links | Source | `https://github.com/PotatuStudio/mythic-relics` |
| Links | Wiki | `https://github.com/PotatuStudio/mythic-relics/tree/main/docs/wiki` |
| Description | Texto | La misma *Description* de [CURSEFORGE.md](CURSEFORGE.md#description-markdown) (Modrinth también usa Markdown) |

## 3. Galería
Subir las imágenes de `media/gallery/` en orden. La primera (`01_all_relics.png`) marcarla como **Featured** para que
salga de portada en la búsqueda. Título sugerido para cada una:

| Archivo | Title |
|---|---|
| `01_all_relics.png` | All relics |
| `02_held_egypt.png` … `06_held_china.png` | Egyptian / Greek / Roman / Norse / Chinese relics in hand |
| `07_mount_sleipnir.png` | Sleipnir |
| `08_mount_pegasus.png` | Pegasus |
| `09_mount_long_dragon.png` | Long, the Chinese dragon |
| `10_mount_skidbladnir.jpg` | Skíðblaðnir |

El video de YouTube se puede agregar después en la descripción (Modrinth no tiene campo de video; se incrusta con un
link de YouTube en el Markdown).

## 4. Versiones (Versions → Create a version)
Una versión **por loader**. Antes: `./gradlew build` y tomar los jars **sin** `-raw` ni `-sources`. A diferencia de
CurseForge, en Modrinth cada versión necesita un número distinto, por eso llevan el loader al final.

| Campo | Fabric | NeoForge |
|---|---|---|
| Archivo | `fabric/build/libs/mythicrelics-fabric-0.1.1+26.3.jar` | `neoforge/build/libs/mythicrelics-neoforge-0.1.1+26.3.jar` |
| Version name | `Mythic Relics 0.1.1 (Fabric 26.3)` | `Mythic Relics 0.1.1 (NeoForge 26.3)` |
| Version number | `0.1.1+26.3-fabric` | `0.1.1+26.3-neoforge` |
| Release channel | **Beta** | **Beta** |
| Loaders | Fabric | NeoForge |
| Game versions | 26.3 | 26.3 |
| Changelog | El mismo de [CURSEFORGE.md](CURSEFORGE.md#changelog) | igual |

**Dependencias** (botón *Add dependency*, se buscan por nombre):

| Proyecto | Fabric | NeoForge |
|---|---|---|
| Architectury API | Required | Required |
| Fabric API | Required | — |
| Trinkets Updated | Required | — |
| Curios API | — | Required |
| Modonomicon | Optional | Optional |
| JEI | Optional | Optional |
| Jade | Optional | Optional |

Si alguna no aparece en Modrinth (puede pasar con Trinkets Updated), se deja fuera de la lista y se menciona en la
descripción, que ya trae la tabla de requisitos.

## 5. Enviar a revisión
Con el proyecto completo aparece **Submit for review**. La moderación de Modrinth revisa a mano (normalmente 1 a 3 días)
y suele pedir: descripción clara de qué hace el mod, licencia, links que funcionen y que el jar no traiga cosas ajenas.
Si piden cambios, llegan como mensaje en el propio proyecto (pestaña *Moderation*).

## 6. Versiones siguientes
Igual que en CurseForge: subir `mod_version` en `gradle.properties`, agregar la sección al `CHANGELOG.md` y subir los dos
jars como versiones nuevas. Si se vuelve tedioso, el plugin **Minotaur** de Gradle sube a Modrinth con
`./gradlew modrinth` usando un token personal (se puede agregar más adelante).
