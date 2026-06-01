---
type: decisions
project: bcde223-a3-eyeball-maze
updated: 2026-06-02
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

## 2026-06-02 — App is run from the terminal via `run.ps1`, not Android Studio

**Decision:** Launch/iterate on the app with a project-root PowerShell script (`run.ps1`): boot the emulator if none is attached, `gradlew installDebug`, wake+unlock the screen, `am start` MainActivity. The Android Studio Run button stays documented as the alternative but is not the primary workflow.
**Why:** User explicitly does not want to use Android Studio for running. The SDK tools (`emulator.exe`, `adb.exe`) live under `%LOCALAPPDATA%\Android\Sdk` and aren't on PATH, so the script calls them by full path. Wake+unlock is baked in because a cold-booted emulator sleeps and shows a black screen (see [[gotchas]]).
**Reversibility:** easy (delete the script; Gradle + adb still work directly).

---

## 2026-06-02 — Board shapes are white vector drawables on coloured cells, not the supplied GIF tiles

**Decision:** Render each square's shape as a white `VectorDrawable`
(`res/drawable/shape_*.xml`) centred on the colour-blocked cell and tinted at
runtime (`ImageView.setColorFilter`, white — black on yellow for contrast).
Letters (`shapeLabel`) are gone; `shapeDrawable(Shape)` maps the enum to a
drawable id.
**Why:** The classic Eyeball Maze GIFs in `../images/` are colour-baked shapes on
an **opaque white box** (can't overlay cleanly, can't tint), and the set is
incomplete for this puzzle — lightning exists only in purple, other shapes never
in purple, so `PURPLE-CROSS` / `YELLOW-LIGHTNING` have no tile. Vectors give one
clean glyph per shape that works on any cell colour and scales to any cell size.
The colour rule stays conveyed by the cell background, the shape by the icon.
**Reversibility:** easy (swap `shapeDrawable` back to text, or to `ImageView`
GIFs if a complete tinted set is ever produced).

---

## 2026-06-02 — Theme is `NoActionBar`; content insets handled in code (edge-to-edge)

**Decision:** Both `themes.xml` use `Theme.MaterialComponents.DayNight.NoActionBar`,
and `MainActivity.onCreate` installs a `ViewCompat.setOnApplyWindowInsetsListener`
that pads `rootView` by the system-bar insets (plus the existing 16dp).
**Why:** `targetSdk 36` forces edge-to-edge; with the old `DarkActionBar` theme the
ScrollView content drew **behind** the action bar and the board's top row was
clipped. The "Minimal A3 Skeleton" action bar was also redundant —
`levelNameText` already shows the level name. NoActionBar + manual insets removes
the bar and keeps the level name clear of the status-bar notch.
**Reversibility:** easy (restore `DarkActionBar` parent, drop the insets listener),
but then re-solve the clipping.

---

## Related

- [[overview]] — How to run (both paths)
- [[code-map]] — where decisions show up in code
- [[gotchas]] — edge-to-edge clipping; emulator-asleep black screen; "dead buttons" = rule rejection
