# Changelog

## 2026-05-12 — Week 10 Android UI (board grid + win state)

### Done
- Replaced the toy 3×3 all-RED-DIAMOND starter level in `GameViewModel` with a real 4×4 Eyeball Maze level: blanks in the top row, mixed colours/shapes, **two** goals (at (1,2) and (3,3)). The single-step direction buttons follow a forced path that reaches both goals — i.e. a win is actually achievable, no soft-locks.
- `GameViewModel` now exposes read-only board accessors for rendering (`getRows/getCols`, `getColorAt`, `getShapeAt`, `isBlankAt`, `hasGoalAt`, `isEyeballAt`, `getEyeballDirection`, `getCompletedGoals`, `getTotalGoals`, `isWon`, `getLevelName`) plus a `reset()` that rebuilds the level. It still does **no** rule logic — `tryMove` delegates to `messageIfMovingTo` / `moveTo`.
- Fixed the status-text goal denominator: the model removes goals as they're completed, so the view-model remembers the original `totalGoals` and reports `completed/totalGoals` instead of the model's shrinking `getGoalCount()`.
- `MainActivity` now renders the board as a `GridLayout` of coloured cells (one `TextView` per square, rebuilt after every move): background = square colour, label = shape initial (D/C/S/F/L), `*` suffix marks a goal, the eyeball cell shows a direction arrow (▲▼◀▶). Blank squares are dark grey with no label.
- Added a win flow: landing on the last goal pops an `AlertDialog` ("You win! — Play again?"), disables the move buttons, and offers Reset/Play-again to rebuild the level. A standalone **Reset** button is always available.
- Move-failure feedback is now human-readable (e.g. "Can't move backwards", "the next square must share this colour or shape") instead of the raw `Message` enum name; success no longer shows a Snackbar.
- Layout wrapped in a `ScrollView` so the board + controls fit on small screens.

### Build/run
- Tests: `./gradlew test` (model tests still green)
- Build APK: `./gradlew assembleDebug`
- Run: Android Studio Run button (Shift+F10) on the Pixel 8 Pro API 37 emulator.

### Next steps (post-class)
- Render shapes as actual icons/drawables instead of letters.
- Support multi-square moves (the real game slides the eyeball along a row/col to a chosen square) — likely tap-a-cell instead of (or alongside) the four buttons. `Game.moveTo` already takes an arbitrary destination, so this is a UI/ViewModel change.
- Add more levels and a level picker; give `Level` a real name.
- Show a "no moves left" lose state (currently you can only get stuck, never told).

## 2026-05-12 — Week 09 Android Core (A2 → A3 bridge)

### Done
- Copied the Assessment 2 Eyeball Maze model into `app/src/main/java/nz/ac/ara/bcde223/minimala3skeleton/model/` and renamed the package to `nz.ac.ara.bcde223.minimala3skeleton.model`.
- Migrated the 10 A2 JUnit 5 model tests into `app/src/test/java/.../model/` (same package). All 95 tests pass via `./gradlew test`.
- Bumped Java source/target to 17 in `app/build.gradle` (needed by `Position.java`'s pattern-matching `instanceof`).
- Pinned project encoding to UTF-8 (`.idea/encodings.xml`) — Android Studio was mis-detecting the migrated test files.
- Rewrote `GameViewModel` to wrap the real `Game`: builds a 3×3 starter level (all `PlayableSquare(RED, DIAMOND)`), eyeball at (1,1) facing RIGHT, goal at (1,2). Exposes `getStatusText()` and `tryMove(Direction)`.
- Rewrote `MainActivity` + `activity_main.xml`: Up/Left/Right/Down buttons → `tryMove` → Snackbar shows the resulting `Message`; status TextView shows eyeball position + goal count. Layout is a plain vertical `LinearLayout` with an explicit white background (a ConstraintLayout version existed first but the sibling-chain was fragile).

### Notes / gotchas
- The "black screen" seen during testing was just the emulator being asleep — not an app bug. Wake the emulator screen before assuming a render problem.
- `PackageManager: Error occurred while checking alignment of package` in logcat is a benign zipalign/16KB-page warning, unrelated to anything here.
- PowerShell 5.1's `Set-Content -Encoding UTF8` writes a BOM that `javac` rejects — used `[System.IO.File]::WriteAllText` with `UTF8Encoding($false)` instead when copying the test files.

### Next steps (post-class)
- Replace the toy 3×3 all-RED-DIAMOND starter level with a real Eyeball Maze level: blank squares, mixed colours/shapes, multiple goals.
- Render the actual board grid in the UI instead of just the status text.
- Optionally: show a "You win!" state when `getCompletedGoalCount() == getGoalCount()`.
- Consider whether `Position` should become a Java `record` (IDE suggests it; purely cosmetic).

### Build/run
- Tests: `./gradlew test`
- Build APK: `./gradlew assembleDebug`
- Run: Android Studio Run button (Shift+F10) on the Pixel 8 Pro API 37 emulator.
