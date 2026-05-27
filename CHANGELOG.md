# Changelog

## [1.2.0] - 2026-05-27

### Added

- Mod icon now displayed in ModMenu and other mod listings

## [1.1.0] - 2026-05-26

### Added

- Support for Minecraft 1.21.6 (covers 1.21.6 and 1.21.7 via version range)
- Support for Minecraft 1.21.9 (covers 1.21.9 and 1.21.10 via version range)
- Support for Minecraft 1.21.11
- 26.1.2 build now covers 26.1, 26.1.1, and 26.1.2 via version range

### Changed

- Improved Stonecutter conditionals to handle Mojang's API renames across versions (`KeyMapping.Category` from 1.21.9, `Identifier` rename at 1.21.11)

## [1.0.0] - 2026-05-23

### Added

- Initial release supporting Minecraft 1.21.8 and 26.1.2 (Fabric)
- Configurable HUD: 9 anchor positions, scale, decimal precision, color, shadow, background
- Cardinal direction indicator (optional)
- In-game configuration via ModMenu + YACL
- JSON persistence
- Customizable toggle keybinding
