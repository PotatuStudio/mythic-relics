# Mythic Relics

Reliquias de la mitología (Egipto, Grecia, nórdicos y más) convertidas en equipo RPG. Un mod de **PotatuStudio** para
Minecraft Java **26.3** (Fabric y NeoForge).

Como en *Artifacts*, las reliquias no se fabrican: se encuentran en estructuras que calzan con su cultura (las
egipcias, en las pirámides del desierto).

## Requisitos
Minecraft 26.3, Java 25 y [Architectury API](https://modrinth.com/mod/architectury-api) 22.0.5 o superior. Además,
según el loader:
- **Fabric:** Fabric Loader 0.20.1 o superior, Fabric API y
  [Trinkets Updated](https://modrinth.com/mod/trinkets-updated) 4.2.1 o superior (espacios de accesorios).
- **NeoForge:** NeoForge 26.3.0.101 o superior y [Curios API](https://modrinth.com/mod/curios) 17.0.0 o superior
  (espacios de accesorios).

Opcionales: [Modonomicon](https://modrinth.com/mod/modonomicon) 2.20.1 o superior agrega el **Códice de reliquias**
(un libro con cada reliquia, su mito y dónde encontrarla) y [JEI](https://modrinth.com/mod/jei) muestra dónde se
encuentra cada una.

## Wiki
[docs/wiki](docs/wiki/README.md): todas las reliquias con sus efectos, su mito y dónde encontrarlas.

## Desarrollo
```sh
./gradlew :fabric:runClient     # o :neoforge:runClient
./gradlew :fabric:runServer     # o :neoforge:runServer
./gradlew build                 # jars en fabric/build/libs y neoforge/build/libs
```

## Licencia
[MIT](LICENSE)
