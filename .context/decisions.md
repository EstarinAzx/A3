---
type: decisions
project: bcde223-a3-eyeball-maze
updated: 2026-05-26
tags: [context, decisions]
---

# Decisions

Settled questions. Append-only. Each entry dated.

No `docs/adr/` in this project.

---

## 2026-05-12 — A2 model is the source of truth; Android never duplicates rules

The Eyeball Maze rules, state transitions, win/lose detection, and move-validity messages all live in the `model/` package (copied verbatim from Assessment 2 with only the package renamed). The Android layer (`ui/` + `viewmodel/`) may *call* the model and *display* its results but must not reimplement any of its logic — A2 is graded as model-only and A3 is graded as the Android layer on top of it. Bypassing the model would invalidate the assessment boundary.

The viewmodel's `tryMove(Direction)` is allowed to translate a button press into a destination cell and to bounds-check the grid — those are UI concerns, not game rules. Everything past that delegates to `Game.messageIfMovingTo` / `Game.moveTo`.

---

## 2026-05-12 — Layout is `LinearLayout` inside `ScrollView`, not `ConstraintLayout`

A `ConstraintLayout` version of `activity_main.xml` was tried first but the sibling-constraint chain was fragile when adding/removing controls. Switched to a vertical `LinearLayout` wrapped in a `ScrollView` so the board + controls fit on small screens. Stay with this unless a real layout-perf or rotation problem appears.

---

## 2026-05-12 — Board is rendered programmatically, not via XML cell views

`renderBoard()` in `MainActivity` builds the `GridLayout` cells in code (one `TextView` per cell, sized in dp), because the board is dynamic per level and would otherwise require N×M static XML views. The XML only declares an empty `GridLayout` container.

---

## 2026-05-12 — Java source/target raised to 17

Required because `model/Position.java` uses pattern-matching `instanceof`. Don't downgrade `compileOptions` without rewriting `Position` (or converting it to a record, which the IDE keeps suggesting).

---

## 2026-05-12 — Level data lives in `assets/<name>.txt`, not in code

`GameViewModel.setupStarterLevel()` (hardcoded Java) was removed in the Week 11 data-management slice. Levels are now plain-text files parsed by `GameViewModel.loadLevel(text)`. The format:

```
name=<string>
height=<int>
width=<int>
eyeball=<row>,<col>,<UP|DOWN|LEFT|RIGHT>
goals=<r>,<c>;<r>,<c>;...
squares=
<cell>,<cell>,...    # one row per line; cell = BLANK or COLOR-SHAPE (e.g. PURPLE-LIGHTNING)
<cell>,<cell>,...
...
```

Rationale: lets us add levels without recompiling, sets up a future level-picker, and keeps the ViewModel as pure data→model-call translation (no rule logic moved in). Parser is line-oriented, `\r\n`-tolerant, blank lines ignored. See [[code-map]] for the parser location.

If the data format needs to grow (e.g. per-level move limits, themes, multi-level files), keep it parseable line-by-line — don't reach for JSON/YAML unless the complexity actually demands it.

---

## Related

- [[overview]]
- [[code-map]] — where decisions show up in code
- [[gotchas]] — consequences of the model-is-truth rule
