# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project follows to [Ragnarök Versioning Convention](https://shor.cz/ragnarok_versioning_convention).

## Wither Config Version 1.1 Changelog - 2024-01-09

### Added

- Boat Jail Fix (Makes the Wither an area damage for boats, to prevent it from being trapped in a boat jail)
- Configuration for:
  - Boat Jail Fix
  - Boat Jail Fix Tick Delay
  - Boat Jail Fix Range
  - Break Fluids

### Fixed

- Fixed Wither not breaking blocks under itself when `breakBlocksWhenTargetingPlayer` is on & player is under it making the player safe

### Internal

- Changed the injection point of `newTargetFollowingLogic`
- General Cleanup
- Switched to [CurseUpdate](https://forge.curseupdate.com/) for update checking
- Switched from [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle) tags to [gmazzo](https://github.com/gmazzo) [gradle-buildconfig-plugin](https://github.com/gmazzo/gradle-buildconfig-plugin)
- Switched to Gradle Kotlin DSL
- Updated to Gradle 8.5

## Wither Config Version 1.0 Changelog - 2023-09-06

### Added

- Configuration for:
  - The Wither health
  - The Wither movement speed
  - The Wither follow range
  - The Wither armor
  - The Wither follow distance
  - The Wither unarmored fly height
  - The Wither summoning sequence length
  - The Wither summoning sequence end explosion strength
  - The Wither skulls damage
  - The Wither skulls magic damage
  - The Wither skulls heal towards the Wither when getting a kill
  - The Wither skulls explosion strength
  - The Wither skulls potion effects
  - Whether the Wither should attempt to break blocks around it when targeting a player
- Fix for the Vanilla broken Wither target following behavior 
