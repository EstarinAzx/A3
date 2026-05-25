---
type: stack
project: bcde223-a3-eyeball-maze
updated: 2026-05-26
tags: [context, stack, android, java, gradle]
---

# Stack

## Languages & runtime

- **Java:** source/target **17** (`compileOptions` in `app/build.gradle`). Bumped from 11 because `model/Position.java` uses pattern-matching `instanceof`.
- **Android:** `minSdk 28`, `targetSdk 36`, `compileSdk 36`.
- **Gradle:** **9.1.0** (wrapper `gradle/wrapper/gradle-wrapper.properties`).
- **AGP:** version pinned via `libs.plugins.android.application` in `gradle/libs.versions.toml`.

## App identity

- `applicationId` / `namespace`: `nz.ac.ara.bcde223.minimala3skeleton`
- Root project name (in `settings.gradle`): `Minimal A3 Skeleton`
- `versionCode 1`, `versionName "1.0"`
- Theme: `Theme.MaterialComponents.DayNight.DarkActionBar` (via `Theme.MinimalA3Skeleton` in `res/values/themes.xml`)

## Frameworks / libraries

- **AndroidX appcompat** — `AppCompatActivity`.
- **Material Components** — `Snackbar`, `AlertDialog.Builder`, theme.
- **AndroidX activity** + **constraintlayout** — declared deps; layout itself uses `LinearLayout` inside `ScrollView` (a ConstraintLayout version existed first; LinearLayout was simpler — see [[decisions]]).
- No Jetpack Compose, no LiveData/ViewModel-from-arch-components — the `GameViewModel` here is a plain Java class, not `androidx.lifecycle.ViewModel`.

## Testing

- **JUnit 5 (Jupiter):** `org.junit.jupiter:junit-jupiter-api:5.10.2`, engine `:5.10.2`, platform launcher `1.10.2`. Wired by `testOptions.unitTests.all { useJUnitPlatform() }`.
- JUnit 4 also on the classpath (`junit:junit:4.13.2`) — unused by current tests, kept as a fallback.
- Instrumented tests: `androidx.test` runner + Espresso declared but no instrumented tests written.
- Test count: 10 model test classes, 95 tests total, all green.

## Env vars

None at runtime. Build needs:
- A working JDK 17 on `PATH` / Android Studio's bundled JBR.
- `local.properties` with `sdk.dir=<android sdk path>` (tracked locally, gitignored).

## Build commands (Windows, PowerShell)

- `./gradlew.bat test` — unit tests
- `./gradlew.bat assembleDebug` — debug APK at `app/build/outputs/apk/debug/`
- `./gradlew.bat :app:compileDebugJavaWithJavac --rerun-tasks` — force a clean Java recompile

## Related

- [[overview]] — project shape and architecture rule
- [[gotchas]] — PowerShell BOM trap, emulator-asleep "black screen" false alarm
