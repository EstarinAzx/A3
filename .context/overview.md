---
type: overview
project: bcde223-a3-eyeball-maze
updated: 2026-05-26
tags: [context, overview, android, assignment]
---

# Overview

**Project:** BCDE223 Assessment 3 — Eyeball Maze (Android)
**One-liner:** Android app (Android Studio project `minimala3skeleton`) that wraps the pure-Java Eyeball Maze domain model from Assessment 2 with a board UI, four direction buttons, and a Reset button.

## The architecture rule (do not violate)

**Model is the truth.** The Eyeball Maze rules, state, and behaviour live in the `model/` package (copied from A2). The Android layer only *displays* state and *triggers* actions. Do **not** move game logic (move validity, win/lose, feedback messages, state transitions) into the UI or ViewModel — call into `Game`/`Level`/etc. instead.

Layering as built:
- **`model/`** — A2 Eyeball Maze model (15 files). `Game` implements `ILevelHolder, IGoalHolder, ISquareHolder, IEyeballHolder, IMoving`. See [[code-map]].
- **`viewmodel/GameViewModel`** — owns a `Game`. **Level is loaded from a data file**, not hardcoded: `loadLevel(text)` parses `assets/level1.txt` into model calls (`addLevel` → `addSquare` per cell → `addEyeball` → `addGoal`) and caches the text so `reset()` rebuilds the same level. Exposes read-only board accessors plus `tryMove(Direction)`. No rule logic.
- **`ui/MainActivity`** — reads the asset, calls `viewModel.loadLevel(...)`, binds 4 direction buttons + Reset, renders the board as a programmatic `GridLayout` of `TextView` cells (background = colour, label = shape initial D/C/S/F/L, `*` = goal, arrow ▲▼◀▶ = eyeball), shows Snackbar on invalid move, AlertDialog on win.

## Layout

- `app/src/main/assets/level1.txt` — level/setup data (see [[code-map]] for format)
- `app/src/main/java/nz/ac/ara/bcde223/minimala3skeleton/`
  - `model/` — 15 files, A2 Eyeball Maze domain
  - `viewmodel/GameViewModel.java`
  - `ui/MainActivity.java`
- `app/src/main/res/layout/activity_main.xml` — ScrollView > vertical LinearLayout (white bg): level-name text, empty `GridLayout` (filled in code), status text, Up/Left/Right/Down + Reset buttons
- `app/src/test/java/.../model/` — 10 migrated A2 JUnit 5 tests (95 tests, all green)
- `CHANGELOG.md` — chronological per-week log (newest first)

## How to run

- **Test:** `./gradlew.bat test` (Windows) — compiles main + test, runs the 10 model test classes
- **Build APK:** `./gradlew.bat assembleDebug`
- **Run:** Android Studio Run button (Shift+F10), target = Pixel 8 Pro API 37 emulator (or any API 28+ device)

## User-facing surface

Single launcher activity, no other screens. Controls:
- **Up / Down / Left / Right** — one-square move in that direction; Snackbar on invalid move.
- **Reset** — rebuild the level from `level1.txt`.
- **Win dialog** — pops on last goal completion; "Play again" / "Close".

## Where to look first

- Entry point: `app/src/main/java/nz/ac/ara/bcde223/minimala3skeleton/ui/MainActivity.java`
- Level data: `app/src/main/assets/level1.txt`
- Model surface: `app/src/main/java/.../model/Game.java`
- See [[code-map]] for who-owns-what.

## Conventions

- Package on copy-in: A2 was `nz.ac.ara.sbt.eyeballmaze`; everything renamed to `nz.ac.ara.bcde223.minimala3skeleton.model` here.
- File encoding: UTF-8 **without BOM** (see [[gotchas]] — PowerShell 5.1 can write a BOM that `javac` rejects).
- Per-week workflow: at end of each weekly class, add a `CHANGELOG.md` entry; check-in submission goes to the course site.

## Domain vocabulary (Eyeball Maze)

- **Square** — board cell. `PlayableSquare(Color, Shape)` or `BlankSquare` (impassable / blocks paths).
- **Eyeball** — the moving piece; has a `Direction` (UP/DOWN/LEFT/RIGHT). Can't move backwards relative to facing.
- **Goal** — square the eyeball must land on; removed from the goal set when completed. Win = all goals completed.
- **Move rules** — destination must share `Color` *or* `Shape` with the current square; no diagonals; no path through blank squares (multi-square moves slide along a row/col).
- **Message** — enum the model returns for an attempted move: `OK`, `BACKWARDS_MOVE`, `MOVING_OVER_BLANK`, `MOVING_DIAGONALLY`, `DIFFERENT_SHAPE_OR_COLOR`.

## Map

- [[stack]] — Java/Gradle/Android versions, key libs
- [[active-work]] — current handoff state
- [[decisions]] — settled questions (incl. A2/A3 boundary, data-file format)
- [[code-map]] — per-file logic ownership in `model/`
- [[gotchas]] — non-obvious traps (BOM, emulator sleep, asset-load failure, etc.)
