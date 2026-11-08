# Entorno de desarrollo (Windows)

Pasos para dejar un PC listo para desarrollar y probar los mods de PotatuStudio:
compilar, ejecutar `runClient` y tener los logs a mano.

## 1. Herramientas

En PowerShell:

```powershell
winget install EclipseAdoptium.Temurin.25.JDK   # Java 25 (lo exige Minecraft 26.x)
winget install JetBrains.IntelliJIDEA           # IntelliJ IDEA (la versión gratis basta)
winget install Git.Git
winget install GitHub.cli
winget install Anthropic.ClaudeCode             # opcional: Claude Code para leer logs y corregir en este PC
```

Gradle no se instala: cada repo trae su wrapper (`gradlew`).

## 2. GitHub e identidad de Git

```powershell
gh auth login        # entra con la cuenta Friedpotatu (los repos son privados)
gh auth setup-git    # git usa la sesión de gh para push y pull
```

Para que los commits salgan como **Friedpotatu**:

- **PC solo personal:** configura la identidad global.
  ```powershell
  git config --global user.name "Friedpotatu"
  git config --global user.email "82620851+Friedpotatu@users.noreply.github.com"
  ```
- **PC compartido con otra cuenta (por ejemplo, la del trabajo):** usa la identidad de Friedpotatu
  solo dentro de `C:\dev\mods\`. Agrega esto a `~/.gitconfig`:
  ```ini
  [includeIf "gitdir/i:C:/dev/mods/"]
      path = ~/.gitconfig-friedpotatu
  ```
  y crea `~/.gitconfig-friedpotatu`:
  ```ini
  [user]
      name = Friedpotatu
      email = 82620851+Friedpotatu@users.noreply.github.com
  ```

Verifica dentro de un repo con `git config user.email`.

## 3. Clonar un mod

```powershell
mkdir C:\dev\mods -Force; cd C:\dev\mods
gh repo clone PotatuStudio/<repo>          # ej. PotatuStudio/misfit-relics
cd <repo>
git checkout <rama>                        # si vas a probar una rama en desarrollo
```

## 4. IntelliJ IDEA

1. Si al abrirlo ofrece la prueba de la versión Ultimate, puedes rechazarla: no se necesita.
2. *Settings → Plugins → Marketplace* → instala **Minecraft Development** y reinicia.
3. *File → Open* → la carpeta del repo.
4. *Settings → Build, Execution, Deployment → Build Tools → Gradle*:
   - *Distribution*: **Wrapper**
   - *Gradle JVM*: **Temurin 25**
   - *Build and run using*: **Gradle**
5. Espera a que termine el sync de Gradle. La primera vez descarga Minecraft y tarda varios minutos.
6. Panel **Gradle** (elefante, a la derecha) → *Tasks → fabric* → **runClient**.

En desarrollo juegas con un usuario offline ("Player"). Los errores de Realms y de
`user properties` (401) en el log son normales.

## 5. Logs

| Qué | Dónde (dentro del repo) |
|---|---|
| Log de la última partida | `run\logs\latest.log` |
| Logs anteriores | `run\logs\*.log.gz` |
| Crash reports | `run\crash-reports\` |
| Log del servidor dedicado (`runServer`) | `run\logs\latest.log` (misma carpeta) |

Para compartir un log: súbelo a https://mclo.gs y comparte el link, indicando la rama y el commit
(`git log -1 --oneline`). Si usas Claude Code en ese PC, puede leer el archivo directamente.

## 6. Mantenerlo al día

```powershell
git pull                                   # trae los últimos cambios
winget upgrade --all                       # actualiza JDK, IntelliJ, Git, gh
```

Cuando cambien las versiones en `gradle.properties` (Minecraft, Loader, Fabric API), recarga
Gradle en IntelliJ: panel Gradle → 🔄 *Reload All Gradle Projects*.
