# SKY — Release Notes

## v1.2.0

### New: Wear OS watch face (Galaxy Watch 6 / Wear OS 4)
- A code-based (Jetpack Watch Face / Canvas) watch face in a new `:wear` module.
- Four concentric notched rings, no text, each divided into its cycle and filled
  clockwise from the top by progress:
  - **Season** — 3 notches (thirds)
  - **Lunar** — 4 notches (quarters of the ~29.5-day cycle)
  - **Week** — 7 notches (days)
  - **Hour** — 12 notches (temporal day/night hours)
- Colors follow `colors.txt` (12-hue wheel):
  - Season = the current season's triad (e.g. Spring: Violet, Red-Violet, Red).
  - Hour = the 00:00–11:00 wheel (Blue-Violet → Blue).
  - Lunar = the current lunar day's wheel color (Day N table).
  - Week keeps per-day colors (no week mapping in `colors.txt`).
- Per-minute updates; dimmed ambient mode; Jerusalem default for sun/hour math.

### Refactor: shared `:core` module
- Extracted `CelestialCalculations` and `HebrewStrings` into a `:core` Android
  library shared by the phone app and the watch face — one source of truth,
  no more duplicated calculation code.

## v1.1.0 (versionCode 2)

**Artifact:** `Sky-release.apk` · signed (APK Signature Scheme v2)
**Requirements:** Android 8.0+ (minSdk 26, target SDK 34)

### New: home-screen widget
- A Jetpack Glance App Widget showing the three nested cycles as a minimalist
  graphic — concentric progress rings: outer = season, middle = week,
  inner = (temporal) hour, each filling clockwise from the top in its accent
  color (season tint, brass week, gold-by-day / blue-by-night hour).
- **2×2** shows the rings; **4×2** adds the moon dial + lunar day.
- Resizable on both axes (down to a square 2×2); simple 30-minute auto-refresh.
- Uses the Jerusalem default for sun/hour math (a widget can't request the GPS
  permission); lunar, week, and season values are location-independent.
- Shares calculations with the app (`CelestialCalculations`) and the moon/ring
  drawing is baked to a Bitmap since widgets can't host a Compose canvas.

## v1.0.0 (versionCode 1)

**Artifact:** `Sky-release.apk` · ~3.0 MB · signed (APK Signature Scheme v2)
**Requirements:** Android 8.0+ (minSdk 26, target SDK 34)

### What it is
A Hebrew, right-to-left Android app that reads the sky at a glance — a single-screen
astronomical instrument showing where you are in the natural cycles right now. It is a
native port of the original SKY web page (`index.html` / `sky.js`), with all
calculations matched to that source of truth.

### What it shows (one screen, no scroll)
- **Psalm 19:1** title — הַשָּׁמַיִם מְסַפְּרִים כְּבוֹד־אֵל
- **Moon (hero):** disc with the true current phase, lunar day (יום N), Hebrew phase
  name, and tide high/low times
- **Three nested cycles** as matching etched scales:
  - **Hour** — the temporal (halachic) hour within the day, day/night, 12-segment progress
  - **Week** — the day within the week, weekday name + `N / 7`
  - **Season** — the tekufah with a 3-part progress, day `X / Y`, days remaining
- **Location:** defaults to Jerusalem; the location button requests GPS and recomputes
  sun, tides, and temporal hours for the device's position

### Design — "The Night Instrument"
Brass engraving on a midnight ground, parchment text, moonlight-blue night accent.
Serif display with monospace readouts so data reads like an etched scale. Hairline
sections instead of cards; the moon is the single bold element. Committed dark theme, RTL.

### Technical
- Jetpack Compose UI; Kotlin 1.9.25 / AGP 8.13.2 / Compose Compiler 1.5.15
- Calculations ported 1:1 from `sky.js` (lunar; season via real equinox/solstice dates;
  sun times with timezone offset; temporal hours; moon-transit tides)
- R8 minification + baseline profiles

### Fixes included (code review)
- Winter season day counts are correct across the year boundary (Dec 21–31 now use the
  following year's spring equinox as the season end).
- Numeric readouts (`6 / 12`, `5 / 7`, `91 / 93`) are wrapped in an LTR isolate so they
  read correctly inside the right-to-left layout instead of being reversed.
- Celestial values recompute once per minute (not every second).
- Single-screen layout scrolls as a fallback on short screens / large accessibility fonts
  instead of clipping.
- Robust release signing config (clear error on a missing keystore property).

### Signing
- Keystore: `sky-release.keystore`, alias `sky`, RSA 2048, 10,000-day validity
- Certificate DN: `CN=Avi BenEmanuel, OU=ShirHashirim Institute, O=Tanach, L=Jerusalem, ST=Jerusalem, C=IL`
- Certificate SHA-256: `6803c847e3308ca621de07988a5c724b2a227d894ad00100872cd7b8df8675ad`
- Keystore and `keystore.properties` are git-ignored — back them up; they are required for
  every future update to the same app.

### Build commands
```sh
# signed release APK
./gradlew assembleRelease
# install it
./gradlew installRelease
# (for Play Store) signed App Bundle
./gradlew bundleRelease
```

### Known notes
- Hebrew display uses the device's Noto Serif Hebrew; devices lacking it fall back to a
  default face.
- For Play Store distribution, a signed `.aab` (`bundleRelease`) is preferred over the APK.
