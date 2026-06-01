---
type: code-map
project: bcde223-a3-eyeball-maze
updated: 2026-06-02
tags: [context, code-map, model]
---

# Code Map

Where each piece of logic lives.

```
asset (level1.txt) → ViewModel.loadLevel → Game (model)
                                              ↓
button press → MainActivity.handleMove → ViewModel.tryMove → Game.messageIfMovingTo / moveTo
                       ↓                                              ↓
                  renderBoard ← ViewModel read-only accessors ← Game state
```

All game rules live in the model. The ViewModel is glue (file → model calls; button → model query+command). The Activity is render + event wiring.

## Model (`app/src/main/java/nz/ac/ara/bcde223/minimala3skeleton/model/`)

| File | What lives here |
|---|---|
| `Game.java` | Aggregate root. Owns levels, current square layout, eyeball, goals. **All rule decisions live here:** `messageIfMovingTo(row,col)` returns the move-attempt verdict (`Message`); `moveTo(row,col)` applies it; `canMoveTo`, `isDirectionOK`, `hasBlankFreePathTo`, `checkMessageForBlankOnPathTo` are the building blocks. |
| `Level.java` | A single level's grid (height, width, squares) + per-level goal list. `Game` holds multiple `Level`s via `addLevel(h,w)` + `setCurrentLevel(n)` (only level 1 is used today). |
| `Square.java` | Abstract base — `getColor()`, `getShape()`. |
| `PlayableSquare.java` | `PlayableSquare(Color, Shape)` — a normal cell the eyeball can land on. |
| `BlankSquare.java` | Impassable cell; blocks paths and can't be landed on. Color is `BLANK`. |
| `Position.java` | Immutable `(row, col)` pair. Uses pattern-matching `instanceof` (Java 17). IDE suggests record-conversion — cosmetic. |
| `Color.java` | Enum: `BLUE, RED, YELLOW, GREEN, BLANK, PURPLE` (note: `BLANK` is in the middle, not last). |
| `Shape.java` | Enum: `DIAMOND, CROSS, STAR, FLOWER, BLANK, LIGHTNING`. |
| `Direction.java` | Enum: `UP, DOWN, LEFT, RIGHT`. |
| `Message.java` | Enum returned by `Game.messageIfMovingTo(...)`: `OK`, `BACKWARDS_MOVE`, `MOVING_OVER_BLANK`, `MOVING_DIAGONALLY`, `DIFFERENT_SHAPE_OR_COLOR`. Human-readable text is built in `MainActivity.describe(Message)` — keep that in sync. |
| `ILevelHolder.java` | `addLevel`, `setCurrentLevel`, `getLevelWidth`, `getLevelHeight`, `getLevelCount`. |
| `ISquareHolder.java` | `addSquare(Square, row, col)`, `getColorAt`, `getShapeAt`. |
| `IGoalHolder.java` | `addGoal(row,col)`, `getGoalCount`, `hasGoalAt`, `getCompletedGoalCount`. |
| `IEyeballHolder.java` | `addEyeball(row,col,Direction)`, `getEyeballRow/Column/Direction`. |
| `IMoving.java` | `canMoveTo`, `moveTo`, `messageIfMovingTo`. |

`Game` implements **all five** holder interfaces — there's no separate `Board` class.

## ViewModel (`app/src/main/java/.../viewmodel/`)

| File | What lives here |
|---|---|
| `GameViewModel.java` | `loadLevel(text)` parses the asset and issues `addLevel`/`addSquare`/`addEyeball`/`addGoal` calls. `reset()` re-parses the cached text. `tryMove(Direction)` translates direction → destination, bounds-checks, calls `messageIfMovingTo` then `moveTo` if `OK`. Read-only accessors mirror `Game` plus: `getTotalGoals()` (cached original count — the model removes goals as they're completed), `isWon()`, `getLevelName()` (from the file). **No rule logic.** |

## UI (`app/src/main/java/.../ui/`)

| File | What lives here |
|---|---|
| `MainActivity.java` | `onCreate` reads `assets/level1.txt` via `readAsset(...)`, calls `viewModel.loadLevel(...)` (on `IOException`: shows error in status text and returns — buttons inert, no crash), and installs a `ViewCompat` window-insets listener padding `rootView` below the system bars (edge-to-edge; theme is NoActionBar). Wires 4 direction buttons + Reset. `handleMove` calls `viewModel.tryMove`, shows Snackbar on non-`OK` (`describe(Message)` maps enum → text), `renderBoard()` rebuilds the `GridLayout`, win triggers `showWinDialog()` + disables move buttons. **`renderBoard` builds one `FrameLayout` per cell** (coloured bg); `addCellContent(...)` adds a centred `ImageView` of the shape (tinted via `setColorFilter`), an eyeball token (`eyeball_token.xml` dark oval + arrow `TextView`) when the eyeball is here, and a corner `*` for goals. Static helpers `backgroundFor(Color)`, `textColorFor`, `arrowFor(Direction)`, `shapeDrawable(Shape)` own the colour/glyph mapping. |

## Board visuals (`app/src/main/res/drawable/`)

| File | What lives here |
|---|---|
| `shape_diamond/cross/star/flower/lightning.xml` | White `VectorDrawable` per `Shape`, mapped by `MainActivity.shapeDrawable(Shape)`, tinted per cell at render time. |
| `eyeball_token.xml` | Dark oval (`#CC000000`) used as the background behind the eyeball's direction arrow so it reads on top of the square's shape. |

## Level data (`app/src/main/assets/`)

| File | What lives here |
|---|---|
| `level1.txt` | Plain-text key=value + `squares=` block. Keys: `name`, `height`, `width`, `eyeball=row,col,DIR`, `goals=r,c;r,c`, `squares=` (then one comma-separated row per line; each cell is `BLANK` or `COLOR-SHAPE`, e.g. `PURPLE-LIGHTNING`). Parser is in `GameViewModel.buildLevelFrom(...)` / `squareFromToken(...)`. |

## Tests (`app/src/test/java/.../model/`)

10 JUnit 5 classes mirroring A2: `TestGameHoldsLevels/Squares/Eyeball`, `TestLevelHoldsGoals`, `TestUp/Down/Left/Right/Diagonal/CompletingGoals/...Moves`. Cover model behaviour only — no Android, no ViewModel, no Activity.

## Quick "where do I look for…?"

| Question | Start at |
|---|---|
| "Can the eyeball move from A to B?" | `model/Game.java` — `messageIfMovingTo` / `canMoveTo` |
| "Why is the move blocked?" | `model/Game.java` — `checkDirectionMessage`, `checkMessageForBlankOnPathTo` |
| "Who decides win?" | `model/Game.java` — `getCompletedGoalCount`; `GameViewModel.isWon()` compares to cached `totalGoals` |
| "Where's the level data?" | `app/src/main/assets/level1.txt`; parser at `viewmodel/GameViewModel.java` (`buildLevelFrom`) |
| "How are buttons wired to the model?" | `ui/MainActivity.java` `onCreate` → `handleMove` → `viewModel.tryMove(Direction)` |
| "How does the board get drawn?" | `ui/MainActivity.java` `renderBoard()` + `addCellContent()` (programmatic `GridLayout`, one `FrameLayout` per cell, shape `ImageView` from `res/drawable/shape_*`) |
| "Where's the colour palette?" | `ui/MainActivity.java` `backgroundFor(Color)` / `textColorFor(Color)` |
| "How do I add a level?" | Add `assets/levelN.txt` (parser already handles arbitrary sizes); UI to choose levels doesn't exist yet — see [[active-work]] |

## Related

- [[overview]] — architecture + domain vocab
- [[decisions]] — A2/A3 boundary, asset-data format
- [[gotchas]] — model removes goals on completion (load-bearing for the cached `totalGoals`)
