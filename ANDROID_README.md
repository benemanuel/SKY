# SKY — Android

A native port of the SKY web app (`index.html` / `sky.js`) that reads the sky at a
glance. The same celestial math drives three surfaces — a **phone app**, a
**home-screen widget**, and a **Wear OS watch face** — all sharing one visual
language: four concentric **notched cycle rings**.

The four cycles, from outer to inner:

| Ring | Segments | Meaning |
|------|----------|---------|
| Season | 3 | the current season (tekufah), split into thirds |
| Lunar | 29 | the day within the ~29.5-day lunar month |
| Week | 7 | the day within the week |
| Hour | 12 | the temporal (halachic) day/night hour |

Each ring fills clockwise from the top by its current progress and is colored from a
12-hue wheel (see `colors.md`): the season's triad, each lunar day's wheel color, a
per-day color for the week, and the 00:00–11:00 wheel for the hour.

## Surfaces

- **Phone app** — the whole screen is the instrument: the four notched rings on a
  midnight ground, no text. Uses the device location when granted, otherwise a
  Jerusalem default.
- **Home-screen widget** (Jetpack Glance) — 2×2 shows the rings; 4×2 adds a moon
  dial + lunar day. Simple 30-minute auto-refresh.
- **Wear OS watch face** (Galaxy Watch / Wear OS 4–5) — a code-based Canvas watch
  face of the same four rings, with a dim monochrome ambient mode. A **"Center"**
  option fills the empty middle: *None* (default), *Time* (digital `H:MM`, 12/24h
  per system), *Mini rings* (concentric 12-segment hour + minute rings), or *Two
  rings* (the same two rings side by side). Because Wear OS 5 does not show the
  system "Customize" editor for code-based watch faces, the option is changed from
  a small companion app on the watch — **"SKY Center"** in the app list — which
  writes a preference the watch face reads live.

## Modules

```
android/
├── build.gradle.kts            # phone app (com.sky.app) + widget
├── settings.gradle.kts         # includes :core and :wear
├── core/                       # :core — shared, pure-Kotlin logic (no Android UI)
│   └── src/main/kotlin/com/sky/app/domain/
│       ├── CelestialCalculations.kt   # lunar / season / sun / temporal hours / tides
│       ├── HebrewStrings.kt           # Hebrew phase + season names
│       └── CyclePalette.kt            # colors.md wheel + the four-ring model
├── src/main/                   # phone app module
│   ├── kotlin/com/sky/app/
│   │   ├── MainActivity.kt
│   │   ├── data/               # LocationRepository, PreferencesRepository
│   │   ├── viewmodel/SkyViewModel.kt
│   │   ├── ui/                 # SkyApp (rings), theme/ (Instrument palette)
│   │   └── widget/             # SkyGlanceWidget, CycleRingsBitmap, MoonBitmap
│   └── res/
└── wear/                       # :wear — Wear OS watch face
    └── src/main/kotlin/com/sky/app/wear/
        ├── SkyWatchFaceService.kt
        ├── SkyStyle.kt              # "Center" options + preference helper
        ├── SkySettingsActivity.kt   # launchable "SKY Center" settings app
        └── SkyRenderer.kt
```

`:core` is an Android library shared by both the phone app and the watch face, so the
calculations and the ring/color model live in exactly one place.

## Technical stack

- **Language**: Kotlin 1.9.25
- **Phone UI**: Jetpack Compose (Material3); the rings are drawn with `Canvas`
- **Widget**: Jetpack Glance (`androidx.glance:glance-appwidget`) — rendered to a
  bitmap since widgets can't host a Compose canvas
- **Watch face**: Jetpack Watch Face (`androidx.wear.watchface`), `CanvasRenderer2`
- **Time/astronomy**: `java.time`; no third-party astronomy library
- **Build**: Gradle (Kotlin DSL), AGP 8.13.2, Compose Compiler 1.5.15

## Requirements

- Android SDK 34 (compileSdk/targetSdk 34)
- minSdk 26 (phone/core); minSdk 30 for the Wear module
- JDK 11+ to compile (JDK 21 works; set `JAVA_HOME`)

## Building

```bash
cd android

# Phone app
./gradlew assembleDebug            # debug APK
./gradlew assembleRelease          # signed release APK (see Signing)
./gradlew installDebug             # install on a connected phone

# Watch face
./gradlew :wear:assembleDebug
./gradlew :wear:assembleRelease
```

Outputs:
- Phone: `build/outputs/apk/{debug,release}/Sky-{debug,release}.apk`
- Watch: `wear/build/outputs/apk/{debug,release}/wear-{debug,release}.apk`

### Installing the watch face

The Galaxy Watch has no USB, so connect over Wi-Fi:

1. On the watch: enable Developer options, then **Wireless debugging** (pair if
   prompted).
2. `adb connect <watch-ip>:<port>` (the port shown on the Wireless debugging screen,
   not 5555).
3. `adb -s <watch> install wear/build/outputs/apk/debug/wear-debug.apk`
4. On the watch, long-press the face and select **SKY**.

## Signing

Release builds (phone and watch) are signed from a git-ignored `keystore.properties`
in the project root:

```
storeFile=sky-release.keystore
storePassword=…
keyAlias=…
keyPassword=…
```

Both `sky-release.keystore` and `keystore.properties` are git-ignored — back them up;
they are required for every future update to the same app. The release certificate
SHA-256 identifies the app to Play App Signing and API consoles.

## Calculations (ported 1:1 from `sky.js`)

- **Lunar** — reference new moon `2025-03-01T08:24Z`, 29.53-day cycle; day, phase
  name, and cycle position.
- **Season** — real equinox/solstice dates (northern hemisphere), with correct day
  counts across the year boundary; elapsed / total / remaining days.
- **Sun times** — solar-position algorithm with the local timezone offset, giving
  local sunrise/sunset and day/night length.
- **Temporal hours** — the day or night split into 12 seasonal (halachic) hours.
- **Tides** — approximate high/low from the moon's transit time.
- **Week** — day of week with Sunday = 0 (matching the web's `Date.getDay()`).

## Wear OS watch face — battery usage

This face is deliberately on the efficient end; the dominant factor is whether
**Always-on Display (AOD)** is enabled, not the face itself.

- **Updates once per minute** (`interactiveDrawModeUpdateDelayMillis = 60_000`) with
  no seconds hand and no animation — no continuous redraw. (Animated faces that
  redraw at ~60 fps are the usual drain; this one is static between minute ticks.)
- **Trivial compute** per redraw: a little trig plus a handful of `drawArc` calls,
  once a minute — negligible CPU/GC.
- **No extra drains**: no complications, no sensors (heart rate/GPS), no network, and
  no on-watch location polling (it uses the Jerusalem default).
- **AMOLED-friendly ambient**: the ambient frame is mostly black with a few thin
  dim-gray arcs, so very few pixels are lit.

Net effect:

- **AOD off** (raise-to-wake): effectively free — renders only the few seconds you
  look at it.
- **AOD on**: incurs the always-on cost inherent to any watch (screen lit 24/7), but
  this face minimizes it via the mostly-black, once-per-minute ambient render.

## Releases

Tagged releases (`v1.x.y`) are published on GitHub with the signed APKs attached to
the latest release. See `android/RELEASE_NOTES.md` for per-version notes.

## License

This project is part of the SKY project suite.
