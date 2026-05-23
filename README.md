# OnlyCoords

The Minecraft Fabric mod that shows just your coordinates. Nothing else.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.8%20%7C%2026.1.2-62B47A)
![Mod Loader](https://img.shields.io/badge/Mod%20Loader-Fabric-DBD0B4)
![License](https://img.shields.io/badge/License-MIT-blue)

## Why OnlyCoords?

The F3 debug screen does show your coordinates — buried under dozens of technical lines you don't need. Existing alternatives are often either too heavy or not compact and configurable enough. OnlyCoords does one thing and does it well: a clean, lightweight, fully customizable coordinates HUD.

## Supported versions

| Minecraft | Loader |
|-----------|--------|
| 1.21.8    | Fabric |
| 26.1.2    | Fabric |

A single codebase builds both versions, managed across versions with [Stonecutter](https://stonecutter.kikugie.dev/).

## Features

- **Coordinates HUD** showing X / Y / Z in real time
- **Fully configurable**:
  - 9 anchor positions on screen (corners, edges, center)
  - Adjustable scale (size)
  - Optional decimals (0 to 3 places), forced decimal point (`.`)
  - Text color and drop shadow
  - Optional semi-transparent background (configurable color)
  - Optional cardinal direction indicator (`Facing: N / NE / E / ...`)
  - Auto-hide while the F3 debug screen is open
- **JSON persistence** of your configuration
- **Toggle keybinding** to show/hide the HUD (assignable, unbound by default)
- **In-game configuration screen** via ModMenu + YACL

**100% client-side** mod: works in singleplayer and on any server.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for your Minecraft version.
2. Drop the following into your `mods/` folder:
   - **Fabric API** (required)
   - **OnlyCoords** (the `.jar` matching your Minecraft version)
   - *(recommended)* **ModMenu** + **YetAnotherConfigLib (YACL)** for the in-game configuration screen
3. Launch the game: your coordinates appear. Configure everything via ModMenu → OnlyCoords.

## Build from source

The project uses [Stonecutter](https://stonecutter.kikugie.dev/) to manage both Minecraft versions from a single source.

Requirements: **JDK 21** and **JDK 25** (Gradle selects the right toolchain per version automatically).

```bash
# Build all versions at once:
./gradlew build

# (optional) Switch the active source view to a specific version:
./gradlew "Set active project to 1.21.8"
./gradlew "Set active project to 26.1.2"
```

Built `.jar` files are located in:

```
versions/1.21.8/build/libs/onlycoords-<version>+1.21.8.jar
versions/26.1.2/build/libs/onlycoords-<version>+26.1.2.jar
```

## License

[MIT](LICENSE)

---

Made by **Kuzalo**.
