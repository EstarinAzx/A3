---
type: active-work
project: bcde223-a3-eyeball-maze
updated: 2026-05-26
tags: [context, active-work]
---

# Active Work

_Last updated: 2026-05-26 by Opus 4.7 (auto)_
_At commit: `8eee44b`_

## Current focus

Just finished the **Week 11 (Android data management)** check-in slice: moved the hardcoded 4×4 level out of `GameViewModel` into `assets/level1.txt` and added a parser. Code is committed; the in-class check-in submission is still owed.

## State

- **In flight:** Nothing in code. Open loops are non-coding: (1) submit Week 11 check-in to the course site, (2) smoke-test on the emulator.
- **Done this session:**
  - New `app/src/main/assets/level1.txt` encoding the existing 4×4 level.
  - `GameViewModel.loadLevel(text)` parses it into `addLevel`/`addSquare`/`addEyeball`/`addGoal`; `reset()` re-parses cached text; `getLevelName()` now returns the file's `name`. No-arg constructor builds nothing.
  - `MainActivity.readAsset(...)` reads the asset at startup; `IOException` → status text error + early return (no crash).
  - `CHANGELOG.md` + `HANDOFF.md` updated. Committed as `8eee44b`. **Not pushed.**
- **Blocked:** None.

## Pick up here

If continuing A3 work, the most useful next step is **multi-square moves** (tap a cell instead of one-step buttons) — the model already supports arbitrary destinations via `Game.moveTo(row,col)`, so it's a UI/ViewModel change only. Wire `boardGrid` cells' `OnClickListener` to a new `viewModel.tryMoveTo(row, col)` that delegates to `messageIfMovingTo` / `moveTo`. Don't touch the model.

If the user wants to keep building the data-management story instead: add `assets/level2.txt`, add a level-picker (an `AlertDialog` listing assets, or a separate Activity), and have `GameViewModel.loadLevel(...)` get called from the picker selection.

Other pending items:
- Smoke-test the current build on the Pixel 8 Pro API 37 emulator (Shift+F10 in Android Studio). Verify the board renders and the forced win path `UP,UP,RIGHT,RIGHT,RIGHT,DOWN,DOWN` still completes.
- Optionally: `git push` the `8eee44b` commit to `origin/main` (GitHub remote `https://github.com/EstarinAzx/A3.git`).
- Submit Week 11 check-in (screenshot + one-sentence "supports A3 because…" + "next A3 step is…").

## Skills for next session

- None obviously matching. (`/loop` or `/schedule` only if you want recurring builds; `/review` only if a PR's involved.)

## Open questions

- None pending the user.

## Recent context

- The level-data file format was chosen to extend the Week 11 `SetupDataFromAssetsDemo` shape with a `squares=` block (the demo file only had `name/height/width/eyeball/goals`); the A2 model requires square data so we couldn't skip it. Each cell is `BLANK` or `COLOR-SHAPE`. Decided not to reach for JSON/YAML — line-oriented `key=value` is enough.
- `Color`/`Shape` enums have `BLANK` in the middle, not last — parsing uses `valueOf`, so order doesn't matter, but switch-on-ordinal would bite.
- Asset-load errors currently surface as a status-text message rather than a Snackbar/dialog so they're visible even when buttons are inert.

## Related

- [[overview]] — architecture and the model-is-truth rule
- [[code-map]] — where to put the next slice
- [[decisions]] — level-data format is now codified
- [[gotchas]] — `Game.getGoalCount()` shrinks; the cached `totalGoals` matters
