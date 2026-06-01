---
type: active-work
project: bcde223-a3-eyeball-maze
updated: 2026-06-02
tags: [context, active-work]
---

# Active Work

_Last updated: 2026-06-02 by Opus 4.8 (1M) (auto)_
_At commit: `814d24c` + this session's board-visuals/level-redesign commit._

## Current focus

Replaced the board's letter labels with white shape icons, fixed the top-row
clipping, and reworked `level1.txt` (added a start row, moved a goal). All
verified on the emulator by driving a full solve to the win dialog.

## State

- **In flight:** Nothing.
- **Done this session:**
  - **Letters → shape icons.** Added 5 white vector drawables
    (`shape_diamond/cross/star/flower/lightning.xml`). `MainActivity.renderBoard`
    now builds each cell as a `FrameLayout` (coloured bg) holding a centred
    `ImageView` of the shape, tinted via `setColorFilter(inkColor)` (white, or
    black on yellow cells for contrast). `shapeLabel(Shape)` → `shapeDrawable(Shape)`.
    Shape is **always** drawn, even under the eyeball.
  - **Eyeball is a token, not a bare glyph.** New `eyeball_token.xml` (dark oval).
    The direction arrow ▲▼◀▶ now sits in a dark circle on top of the square's
    shape, so the eyeball's cell shows both its shape and the facing.
  - **Goal marker** = small `*` in the cell's top-end corner (was appended to the label).
  - **Top-row clipping fixed.** Cause: `targetSdk 36` enforces edge-to-edge, so
    content drew behind the (redundant) `DarkActionBar`. Switched both
    `themes.xml` → `…DayNight.NoActionBar` and added a `ViewCompat`
    window-insets listener in `onCreate` that pads `rootView` below the system
    bars (on top of the 16dp design padding). See [[decisions]], [[gotchas]].
  - **Level redesigned** (`level1.txt`, data-only): board is now **5×4** (added a
    bottom row). Eyeball starts at **(4,0)** facing UP (the new start cell,
    `RED-DIAMOND`). Row 0 filled in (was 3 blanks): `BLUE-DIAMOND, BLUE-FLOWER,
    PURPLE-LIGHTNING, YELLOW-CROSS`. Goals are now **(1,2)** and **(4,3)**
    (the finish moved from (3,3) to (4,3) `GREEN-FLOWER`). Verified solve path:
    `UP×3, RIGHT×3, DOWN×3` → "You win!".
- **Blocked:** None.

## Pick up here

Still the most useful next code slice: **multi-square moves** (tap a cell instead
of one-step buttons) — the model already supports arbitrary destinations via
`Game.moveTo(row,col)`, so it's a UI/ViewModel change only. Cells are now
`FrameLayout`s in `renderBoard`; wire each cell's `OnClickListener` to a new
`viewModel.tryMoveTo(row,col)` that delegates to `messageIfMovingTo` / `moveTo`.
Don't touch the model.

Alternative data slice: add `assets/level2.txt` + a level-picker (`AlertDialog`
listing assets) calling `GameViewModel.loadLevel(...)`.

Other pending (non-code):
- Submit the Week 11 check-in to the course site.

## How to run (quick ref)

```powershell
.\run.ps1                 # boot emulator, build, install, launch
.\run.ps1 -Avd OtherName  # different AVD
```
Drive moves headless: `adb shell input tap <x> <y>` (UP ≈ `672 1647`, RIGHT ≈
`828 1817`, DOWN ≈ `672 1985`, RESET ≈ `672 1872` on the Pixel 8 Pro AVD).
Capture screen (Git-bash path-mangling-safe): `MSYS_NO_PATHCONV=1 adb exec-out screencap -p > out.png`.

## Gotcha hit this session

`gradlew installDebug` failed with **"No connected devices!"** even though the
emulator was up — gradle's adb had dropped it, then the AVD died. `run.ps1`
recovers (boots if none attached). If a bare `installDebug` says no devices,
re-run `run.ps1`.

## Skills for next session

- None obviously matching. `/run` or `/verify` to launch + check the app again.

## Related

- [[overview]] — architecture, model-is-truth, How to run
- [[code-map]] — where the next slice goes (board render is now FrameLayout cells)
- [[decisions]] — A2/A3 boundary, data format, NoActionBar/edge-to-edge, shape-icon choice
- [[gotchas]] — edge-to-edge clipping; "dead buttons" = rule rejection; emulator-asleep black screen
