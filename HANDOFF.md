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
- `viewmodel/GameViewModel` — owns a `Game`. **The level is loaded from a data file, not hardcoded**:
  `loadLevel(String levelText)` parses the text of `assets/level1.txt` and builds the `Game`
  (`addLevel` → `addSquare` per cell → `addEyeball` → `addGoal`), caching the text so `reset()`
  rebuilds the same level. The no-arg constructor builds *nothing* — the caller must `loadLevel(...)` first.
  Exposes read-only board accessors for rendering (`getRows/getCols`, `getColorAt`, `getShapeAt`,
  `isBlankAt`, `hasGoalAt`, `isEyeballAt`, `getEyeballDirection`, `getCompletedGoals`,
  `getTotalGoals`, `isWon`, `getLevelName` ← the file's `name`, `getStatusText`) plus `tryMove(Direction)` and
  `reset()`. The viewmodel does *no* rule logic; parsing is pure data→model-call translation, and
  `tryMove` just bounds-checks the grid, asks `messageIfMovingTo`, and calls `moveTo` if the result is
  `Message.OK`. It remembers the original goal count (`totalGoals`) because the model removes goals as
  they're completed.
- `ui/MainActivity` — on `onCreate` reads `assets/level1.txt` via a small `readAsset(...)` helper
  and calls `viewModel.loadLevel(...)` (on `IOException`: shows an error in the status text and
  returns early). Then binds 4 direction buttons + a Reset button, calls `viewModel.tryMove(...)`,
  shows a human-readable Snackbar on a failed move, redraws the board grid and status text after
  every move, and on a win pops an `AlertDialog` ("Play again?") + disables the move buttons.
  The board is rendered programmatically as a `GridLayout` of `TextView` cells (background =
  square colour, label = shape initial D/C/S/F/L, `*` = goal, arrow ▲▼◀▶ = eyeball).

## Repo layout (the parts that matter)

```
MyApplication/                         <- Android project root, git root
  app/
    build.gradle                       <- Java 17; JUnit 5 (Jupiter) wired for unit tests
    src/main/
      AndroidManifest.xml              <- launcher = ui.MainActivity
      assets/level1.txt                <- level/setup data file: name, height, width, eyeball=r,c,DIR, goals=r,c;r,c, then a `squares=` block (1 row/line, cells = BLANK or COLOR-SHAPE)
      java/nz/ac/ara/bcde223/minimala3skeleton/
        model/                         <- A2 Eyeball Maze model (15 files), package nz.ac.ara.bcde223.minimala3skeleton.model
        ui/MainActivity.java
        viewmodel/GameViewModel.java
      res/layout/activity_main.xml     <- ScrollView > vertical LinearLayout, white bg: level-name text, empty GridLayout (filled in code), status text, Up/Left/Right/Down + Reset buttons
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

The level is read from `app/src/main/assets/level1.txt` at startup (`MainActivity.readAsset` →
`GameViewModel.loadLevel`). `level1.txt` defines the 4×4 level:
- top row: blanks at (0,0),(0,1),(0,3); a PURPLE/LIGHTNING square at (0,2)
- rows 1–3: mixed colours/shapes (see `level1.txt` for the exact layout)
- eyeball at (3,0) facing `UP`
- two goals: (1,2) and (3,3)

The four single-step buttons drive a *forced* path that reaches both goals — the only legal
sequence is UP, UP, RIGHT, RIGHT, RIGHT, DOWN, DOWN: (3,0)→(2,0)→(1,0)→(1,1)→(1,2 ✓ goal)→
(1,3)→(2,3)→(3,3 ✓ goal). Any other press gives an invalid-move Snackbar. Completing the second
goal triggers the win dialog. There are no soft-locks on this path. **To add a level, add another
`.txt` to `assets/`** (the parser handles arbitrary sizes/layouts) — and eventually a level picker.
If `level1.txt` is missing/unreadable the app shows "Error loading level file (level1.txt)." in the
status text and the buttons do nothing (no crash).

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

1. Render shapes as real icons/drawables instead of the letters D/C/S/F/L; consider drawing
   the eyeball as a bitmap rather than an arrow glyph.
2. Support multi-square moves (the real game slides the eyeball along a row/col to a chosen
   square). Probably tap-a-cell — wire `boardGrid` cells' `OnClickListener` to a
   `viewModel.tryMoveTo(row, col)` that delegates to `messageIfMovingTo` / `moveTo`. The
   model already supports arbitrary destinations, so this is a UI/ViewModel change only.
3. More levels (`assets/level2.txt`, …) + a level picker. The level *name* now comes from the
   data file's `name=` line; what's missing is multiple files and UI to choose between them.
   Optionally validate the parsed level (declared size vs. actual row count, eyeball/goals in
   bounds) and report a clearer error than a raw parse exception.
4. Add a lose / "no legal moves" state — right now you can get stuck but aren't told.
5. Tidy: consider making `Position` a record (optional, cosmetic).

## Git state

Latest commits (newest first):
- `Add CHANGELOG; switch skeleton layout to LinearLayout`
- `Bridge A2 Eyeball Maze model to Android UI; add model unit tests`
- (earlier: `test file migration`, `commit 2`, `Initial commit`)

`.idea/encodings.xml` is tracked and pins the project to UTF-8 (Android Studio keeps
appending per-file entries to it — harmless, just commit them). `.idea/deviceManager.xml`
and similar local IDE state are untracked and should stay that way.
