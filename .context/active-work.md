---
type: active-work
project: bcde223-a3-eyeball-maze
updated: 2026-06-02
tags: [context, active-work]
---

# Active Work

_Last updated: 2026-06-02 by Opus 4.8 (1M) (auto)_
_At commit: `5bb1838` (+ untracked `run.ps1`, committed this session)_

## Current focus

Verified the Week 11 build actually runs and plays correctly on the emulator, and added a terminal-only run workflow (`run.ps1`) so the app can be launched without Android Studio.

## State

- **In flight:** Nothing in code.
- **Done this session:**
  - **Investigated a "buttons don't move the player" report → not a bug.** Drove the emulator via `adb` and confirmed every legal move works: the forced win path `UP,UP,RIGHT,RIGHT,RIGHT,DOWN,DOWN` walks the eyeball (3,0)→(3,3), completes both goals, fires the win dialog. The apparent "dead buttons" were the model correctly rejecting *illegal* moves (backwards / off-board / no shared colour-or-shape), each surfaced via Snackbar. From the start square (3,0 facing UP) only **Up** is legal. See [[gotchas]].
  - Added `run.ps1` at project root — one-click: boot emulator if none attached → wait for boot → `gradlew installDebug` → wake+unlock screen → launch `MainActivity`. See [[overview]] (How to run).
  - Diagnostic `Log.d` calls were added to `MainActivity.handleMove` / temporary accessors on `GameViewModel` during the investigation, then **fully reverted** — model/viewmodel/activity are byte-identical to `5bb1838`.
- **Blocked:** None.

## Pick up here

Still the most useful next code slice: **multi-square moves** (tap a cell instead of one-step buttons) — the model already supports arbitrary destinations via `Game.moveTo(row,col)`, so it's a UI/ViewModel change only. Wire `boardGrid` cells' `OnClickListener` to a new `viewModel.tryMoveTo(row,col)` that delegates to `messageIfMovingTo` / `moveTo`. Don't touch the model.

Alternative data-management slice: add `assets/level2.txt` + a level-picker (`AlertDialog` listing assets) that calls `GameViewModel.loadLevel(...)`.

Other pending (non-code):
- Submit the Week 11 check-in to the course site (screenshot + one-sentence "supports A3 because…" / "next A3 step is…").

## How to run (quick ref)

```powershell
.\run.ps1                 # boot emulator, build, install, launch
.\run.ps1 -Avd OtherName  # different AVD
```
Black screen after launch = emulator asleep; `run.ps1` now wakes+unlocks, but a still-booting cold emulator can take ~30s — tap the screen if needed. See [[gotchas]].

## Skills for next session

- None obviously matching. `/run` or `/verify` if you want the harness to launch + check the app again.

## Open questions

- None pending the user.

## Related

- [[overview]] — architecture, the model-is-truth rule, How to run
- [[code-map]] — where the next slice goes
- [[decisions]] — A2/A3 boundary, data-file format, terminal run workflow
- [[gotchas]] — "dead buttons" = rule rejection; emulator-asleep black screen
