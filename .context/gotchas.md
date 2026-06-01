---
type: gotchas
project: bcde223-a3-eyeball-maze
updated: 2026-06-02
tags: [context, gotchas, android, windows]
---

# Gotchas

Non-obvious traps. Group by area.

## Build / tooling

### PowerShell 5.1 `Set-Content -Encoding UTF8` writes a BOM that `javac` rejects

If you script Java file copies on Windows, do **not** use `Set-Content -Encoding UTF8` — it prepends a BOM (`EF BB BF`) and the Java compiler errors out. Use:

```powershell
[System.IO.File]::WriteAllText($path, $text, (New-Object System.Text.UTF8Encoding $false))
```

The Write tool here writes BOM-free UTF-8 — safe.

### Asset files are read at runtime, not compile time

`assets/level1.txt` is bundled into the APK but never validated by the build. A typo in the file (bad colour name, wrong row count, malformed `eyeball=`) is a runtime `NumberFormatException` / `IllegalArgumentException` from `GameViewModel.buildLevelFrom`, not a compile error. `MainActivity.onCreate` only catches `IOException`, so a parse failure currently crashes the app — see [[active-work]] (Next steps) for the optional validation/clearer-error follow-up.

## Emulator / runtime

### "Black screen" on emulator = screen asleep, not an app bug

Tap the emulator screen or press its power button. `logcat` will show `Displayed ...MainActivity` when the app actually rendered. Don't go hunting for a render bug.

### `PackageManager: Error occurred while checking alignment of package` in logcat

Benign zipalign / 16KB-page warning. Install completes right after it. Ignore.

## Gameplay (looks like a bug, isn't)

### "The direction buttons don't move the eyeball" = the move is illegal, not a dead button

Verified 2026-06-02: buttons, `tryMove`, render, and win all work. When a press seems to do nothing, the model rejected an *illegal* move and `MainActivity` showed a Snackbar explaining why (it may be missed at the bottom of the screen). Eyeball Maze rules: no moving backwards relative to facing, no diagonals, no path through blank squares, and the destination must share **colour or shape** with the current square. From the **starting** square (row 3, col 0, facing UP) only **Up** is legal — Down is backwards, Left is off-board, Right (RED-DIAMOND → YELLOW-LIGHTNING) shares neither. Don't go hunting for a wiring bug; press Up first and watch the Snackbar.

## Layout / rendering

### `targetSdk 36` forces edge-to-edge — content draws behind the system bars / action bar

On API 35+ (`targetSdk 36` here) the window goes edge-to-edge by default. With the
old `DarkActionBar` theme this clipped the board's **top row** behind the action
bar. Fix in place: theme is now `…DayNight.NoActionBar` and `MainActivity.onCreate`
pads `rootView` via `ViewCompat.setOnApplyWindowInsetsListener` (system-bar insets
+ 16dp). If you add a new top-level screen or restore an action bar, expect the
same clipping and apply insets the same way. See [[decisions]].

### Tinting a `VectorDrawable` flattens its internal colours

The board shape icons are tinted with `ImageView.setColorFilter(int)` (SRC_ATOP),
which paints **every** non-transparent pixel one colour — any multi-colour detail
inside the vector is lost. That's why `shape_flower.xml` is a solid white
silhouette (its centre dot is white, not a contrasting hole): a contrasting inner
colour would just be overwritten by the filter.

## Model

### `Game.getGoalCount()` *shrinks* as goals are completed — don't use it for the "total" denominator

The model removes goals from its set when the eyeball lands on them, so `getGoalCount()` returns *remaining* goals, not the original total. `GameViewModel` caches the original count in `totalGoals` at `loadLevel(...)` time and uses that for the `completed/total` status text and the win check (`getCompletedGoalCount() == totalGoals`). If you reintroduce a "% complete" or "X of Y" anywhere, route through `viewModel.getTotalGoals()`, not `game.getGoalCount()`.

### `Color` and `Shape` enums have `BLANK` in the **middle**, not last

`Color { BLUE, RED, YELLOW, GREEN, BLANK, PURPLE }` and `Shape { DIAMOND, CROSS, STAR, FLOWER, BLANK, LIGHTNING }`. If you ever switch on ordinals or rely on enum order, this will bite — match by name. Parsing in `GameViewModel.squareFromToken` uses `Color.valueOf` / `Shape.valueOf`, so names are what matter.

### Don't reimplement game rules in the Activity or ViewModel

This is the load-bearing rule of the assessment, not just style preference. **A2 is model-only** (no Android, no UI, no disk I/O); A3 builds the Android layer on top. If you duplicate `canMoveTo` / `messageIfMovingTo` / win detection in Android code, the assignment grading boundary is violated. The ViewModel's `tryMove` is allowed to translate `Direction` → destination coords and bounds-check the grid — those are UI concerns, not rules. See [[decisions]].

## Android Studio

### Red test filenames in the project tree ≠ compile error

Just means the file is untracked in git (added after the initial commit). Open it; if it compiles, it's fine. Sync Gradle if the IDE looks stale after `build.gradle` edits.

### `Position.java` "Class can be record class" inspection

Cosmetic suggestion from the IDE. Ignore unless a refactor is the actual task.

## Related

- [[code-map]] — where the load-bearing functions live
- [[decisions]] — the A2/A3 boundary rule
- [[stack]] — Gradle/Java versions
