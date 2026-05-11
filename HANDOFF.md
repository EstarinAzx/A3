# Project Handoff — BCDE223 Assessment 3 (Eyeball Maze, Android)

> Purpose of this file: give a new agent (or future me) enough context to continue
> without re-deriving everything. Read `CHANGELOG.md` for the chronological log of
> what's been done; this file is the steady-state picture.

## What this is

A university assignment (course **BCDE223**, student **Sean Tined**). **Assessment 3**
is an Android app built on top of the **Eyeball Maze** game model written for
**Assessment 2** (a pure-Java domain model). This repo is the Android Studio project
(`minimala3skeleton`) where the A2 model has been ported in and a minimal Android UI
built around it.

GitHub remote: `https://github.com/EstarinAzx/A3.git` (branch `main`).

## The architecture rule (do not violate)

**Model = the truth.** The Eyeball Maze rules, state, and behaviour live in the
`model` package (copied from A2). The Android layer only *displays* state and *triggers*
actions. Do **not** move game logic (move validity, win/lose, feedback messages, state
transitions) into the UI or ViewModel — call into `Game`/`Level`/etc. instead.

Layering as built:
- `model/` — A2 Eyeball Maze model. `Game` implements `ILevelHolder, IGoalHolder,
  ISquareHolder, IEyeballHolder, IMoving`. Key methods: `addLevel`, `addSquare`,
  `addEyeball`, `addGoal`, `moveTo(row,col)`, `messageIfMovingTo(row,col)` → `Message`,
  `canMoveTo`, `getEyeballRow/Column/Direction`, `getGoalCount`, `getCompletedGoalCount`.
- `viewmodel/GameViewModel` — owns a `Game`, sets up the starter level, exposes
  `getStatusText()` and `tryMove(Direction)`. The viewmodel does *no* rule logic;
  `tryMove` just bounds-checks the grid, asks `messageIfMovingTo`, and calls `moveTo`
  if the result is `Message.OK`.
- `ui/MainActivity` — binds 4 direction buttons, calls `viewModel.tryMove(...)`,
  shows the returned `Message` in a Snackbar, refreshes the status TextView.

## Repo layout (the parts that matter)

```
MyApplication/                         <- Android project root, git root
  app/
    build.gradle                       <- Java 17; JUnit 5 (Jupiter) wired for unit tests
    src/main/
      AndroidManifest.xml              <- launcher = ui.MainActivity
      java/nz/ac/ara/bcde223/minimala3skeleton/
        model/                         <- A2 Eyeball Maze model (15 files), package nz.ac.ara.bcde223.minimala3skeleton.model
        ui/MainActivity.java
        viewmodel/GameViewModel.java
      res/layout/activity_main.xml     <- plain vertical LinearLayout, white bg, Up/Left/Right/Down buttons + status TextView
      res/values/themes.xml            <- Theme.MaterialComponents.DayNight.DarkActionBar
    src/test/
      java/nz/ac/ara/bcde223/minimala3skeleton/model/   <- 10 migrated A2 JUnit 5 tests (95 tests, all green)
  CHANGELOG.md
  HANDOFF.md                           <- this file
```

Original A2 project (source of the model + tests, **not** in this repo):
`C:\Users\sbt0056\Downloads\MyApplication\BCDE223 Assessment 2 - Sean Tined - Eyeball Maze\`
Its package was `nz.ac.ara.sbt.eyeballmaze`; everything was renamed to
`nz.ac.ara.bcde223.minimala3skeleton.model` on copy-in.

## Current behaviour

`GameViewModel` builds a deliberately trivial starter level:
- 3×3 grid, every cell a `PlayableSquare(Color.RED, Shape.DIAMOND)` (so any move passes
  the colour/shape check)
- eyeball at row 1, col 1, facing `Direction.RIGHT`
- one goal at row 1, col 2

So from the start: **Right** or **Down** → `OK`; **Left** → `BACKWARDS_MOVE` (can't go
opposite your facing direction); landing on (1,2) bumps `getCompletedGoalCount()` to 1.
This is a placeholder — see "Next steps".

## How to build / test / run

- Unit tests: `./gradlew test`  (compiles main + test, runs the 10 model test classes)
- Build debug APK: `./gradlew assembleDebug`
- Run: Android Studio Run button (Shift+F10), target = Pixel 8 Pro API 37 emulator
  (or any API 28+ device — `minSdk 28`, `targetSdk 36`).

Environment: Windows 11, PowerShell. Note: PowerShell 5.1's `Set-Content -Encoding UTF8`
adds a BOM that `javac` rejects — if you script file copies, use
`[System.IO.File]::WriteAllText(path, text, (New-Object System.Text.UTF8Encoding $false))`.

## Known non-issues (don't chase these)

- **"black screen" on the emulator** = the emulator screen was asleep/locked. Tap it /
  press the emulator power button. The app renders fine; `logcat` shows
  `Displayed ...MainActivity` when it does.
- **`PackageManager: Error occurred while checking alignment of package`** in logcat — a
  benign zipalign / 16KB-page warning. Install completes right after it.
- **Red test filenames in Android Studio's project tree** — those files are untracked in
  git (added after the initial commit), not compile errors. Sync Gradle if the IDE looks
  stale after `build.gradle` changes.
- **`Position.java` "Class can be record class"** inspection — cosmetic suggestion only.

## Next steps (post-class TODO, in rough priority order)

1. Replace the toy 3×3 all-RED-DIAMOND starter level in `GameViewModel.setupStarterLevel()`
   with a real Eyeball Maze level: blank squares, mixed colours/shapes, multiple goals.
   (The A2 test files — e.g. `TestCompletingGoals.java` — contain example level layouts
   you can crib.)
2. Render the actual board grid in the UI (a `GridLayout` of cells, or custom View)
   instead of just the status text.
3. Show a win state when `getCompletedGoalCount() == getGoalCount()` (e.g. message in the
   status text, disable buttons, offer reset).
4. Optionally support multi-square moves (the real game slides the eyeball along a row/col)
   — currently buttons move exactly one cell. `Game.moveTo` already takes an arbitrary
   destination, so this is a UI/ViewModel change, not a model change.
5. Tidy: consider making `Position` a record (optional).

## Git state

Latest commits (newest first):
- `Add CHANGELOG; switch skeleton layout to LinearLayout`
- `Bridge A2 Eyeball Maze model to Android UI; add model unit tests`
- (earlier: `test file migration`, `commit 2`, `Initial commit`)

`.idea/encodings.xml` is tracked and pins the project to UTF-8 (Android Studio keeps
appending per-file entries to it — harmless, just commit them). `.idea/deviceManager.xml`
and similar local IDE state are untracked and should stay that way.
