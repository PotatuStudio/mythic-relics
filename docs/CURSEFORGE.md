# Publicar en CurseForge

Guía para el formulario de [authors.curseforge.com](https://authors.curseforge.com). Los textos que ve el público van en
inglés porque es la audiencia de CurseForge; el mod trae `en_us` y `es_es`.

## 1. Crear el proyecto (Create Project)
| Campo | Qué poner |
|---|---|
| Game / Class | Minecraft → **Mods** |
| Name | `Mythic Relics` (ya existe un «Mythic Relic» pequeño; si lo rechazan por parecido: `Mythic Relics: Legends Reforged`) |
| Summary | `Relics from Egyptian, Greek, Roman, Norse and Chinese myth as RPG accessories, found in fitting structures.` |
| Main category | **Adventure and RPG** |
| Additional categories | Armor, Tools, and Weapons · Magic · Mobs |
| License | **MIT License** |
| Logo | `media/icon-512.png` (512×512, CurseForge lo reduce a 400×400) |
| Source | `https://github.com/PotatuStudio/mythic-relics` (repo público desde el 2026-11-20) |
| Issues | `https://github.com/PotatuStudio/mythic-relics/issues` (se configura en legacy.curseforge.com → Settings → Issues → External) |
| Wiki | `https://github.com/PotatuStudio/mythic-relics/tree/main/docs/wiki` |
| Allow comments | Sí |
| Distribution (modpacks/apps de terceros) | Sí |

### Description (Markdown)
```markdown
# Mythic Relics

**70 relics from five mythologies** — Egypt, Greece, Rome, the Norse and China — turned into RPG equipment.
Relics are never crafted: you **find them in structures that fit their culture** (Egyptian relics in desert pyramids), and the more powerful a relic is, the rarer it gets.

## Features
- **Accessories and weapons** with passive effects and active abilities (configurable keybinds).
- **Sets** with bonuses: Anubis, Osiris' Insignia, Perseus and Thor.
- **Allies and mounts** summoned by relics, each with its own behaviour.
- **3D models** when worn or held, painted to look like the myth.
- **Relic Codex** (with Modonomicon): a book with every relic, its myth and where to find it.
- **JEI page** for each relic showing where it drops and how likely it is.
- One **creative tab per mythology**.
- English and Spanish translations.

## Requirements
| | Fabric | NeoForge |
|---|---|---|
| Minecraft | 26.3 | 26.3 |
| Loader | Fabric Loader 0.20.1+ and Fabric API | NeoForge 26.3.0.101+ |
| Accessory slots | Trinkets Updated 4.2.1+ | Curios API 17.0.0+ |
| Library | Architectury API 22.0.5+ | Architectury API 22.0.5+ |

Java 25. Optional: **Modonomicon** (Relic Codex), **JEI** (where to find each relic), **Jade**.

Install it on both the client and the server.
```

## 2. Subir los archivos (Upload File)
Un archivo por loader, en el mismo proyecto. Antes: `./gradlew build` y tomar los jars **sin** `-raw` ni `-sources`.

| Campo | Fabric | NeoForge |
|---|---|---|
| Archivo | `fabric/build/libs/mythicrelics-fabric-0.1.1+26.3.jar` | `neoforge/build/libs/mythicrelics-neoforge-0.1.1+26.3.jar` |
| Display name | `Mythic Relics 0.1.1 (Fabric 26.3)` | `Mythic Relics 0.1.1 (NeoForge 26.3)` |
| Release type | **Beta** | **Beta** |
| Game version | 26.3 | 26.3 |
| Mod loader | Fabric | NeoForge |
| Java | Java 25 | Java 25 |
| Environment | Client + Server | Client + Server |
| Required dependencies | Architectury API, Fabric API, Trinkets Updated | Architectury API, Curios API |
| Optional dependencies | Modonomicon, JEI, Jade | Modonomicon, JEI, Jade |

### Changelog
```markdown
First public beta.
- 70 relics from Egyptian, Greek, Roman, Norse and Chinese mythology.
- Sets, allies, mounts, Relic Codex (Modonomicon) and JEI integration.
- Fabric (Trinkets Updated) and NeoForge (Curios API).
```

## 3. Después
- La moderación revisa el proyecto y cada archivo (horas a pocos días). Si rechazan, el correo dice por qué.
- Para cada versión nueva: subir `mod_version` en `gradle.properties`, agregar la sección al `CHANGELOG.md` y subir
  los dos jars otra vez.
