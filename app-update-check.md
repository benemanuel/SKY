# In-App Update Check

**Status:** Implemented — 2026-06-28.
**Scope:** On launch, the phone app checks `sky.geulah.org.il` for a newer version and shows a dismissible banner if one is available. Wear is not wired up (deferred).

## Decisions

| Decision | Choice |
|---|---|
| **Version source** | Static `app-version.json` at the site root — no Node server needed for SKY. |
| **Tracking** | None. The file is read-only; no install-id, no analytics. |
| **HTTP client** | Plain `HttpURLConnection` + `org.json.JSONObject` — no new dependencies. |
| **Release-cut workflow** | Manually bump `versionCode`/`versionName` in both `android/build.gradle.kts` and `app-version.json`, then deploy. |

## Server (static file)

### `app-version.json` (repo root, served as `https://sky.geulah.org.il/app-version.json`)

```json
{
  "versionCode": 3,
  "versionName": "1.3.0",
  "releaseUrl": "https://github.com/benemanuel/SKY/releases/latest",
  "notes": ""
}
```

nginx serves the repo root as the web root (`/usr/share/nginx/html`), so this file is available immediately after deploy. `notes` is an optional short "what's new" string shown below the banner headline.

## Android client

### Files changed

| File | Change |
|---|---|
| `android/build.gradle.kts` | Added `buildConfig = true` to `buildFeatures` |
| `android/src/main/kotlin/com/sky/app/data/UpdateChecker.kt` | New — `UpdateInfo` data class + `checkForUpdate()` suspend fun |
| `android/src/main/kotlin/com/sky/app/viewmodel/SkyViewModel.kt` | Added `updateInfo: StateFlow<UpdateInfo?>`, `dismissUpdate()`, launch in `init` |
| `android/src/main/kotlin/com/sky/app/ui/SkyApp.kt` | Added `UpdateBanner` composable + `safeOpenUrl` helper |

`INTERNET` permission was already present in `AndroidManifest.xml`.

### `UpdateChecker.kt`

- Fetches `https://sky.geulah.org.il/app-version.json` on `Dispatchers.IO`, 5 s timeout.
- Compares `versionCode` from the response against `BuildConfig.VERSION_CODE`.
- Any network/parse error → returns `null` (fail quiet; never blocks UI).

### Banner behaviour

- Shown at top-center of `SkyApp` when `updateAvailable == true`.
- Tapping the text opens `releaseUrl` via `Intent.ACTION_VIEW` through `safeOpenUrl`, which enforces `https://` or `http://` scheme and catches `ActivityNotFoundException` (correct pattern for API 30+ where `resolveActivity` requires a `<queries>` manifest entry).
- Dismiss button sets `updateInfo` to `null` in the VM (in-memory only; re-appears on next cold launch if still outdated).

## Release flow

1. Bump `versionCode` / `versionName` in `android/build.gradle.kts`.
2. Update the same values in `app-version.json` at the repo root.
3. Build and sign the APK (`.\gradlew assembleRelease`).
4. Publish a GitHub Release (tag `sky-v<versionName>`, APK attached).
5. Deploy the server (`docker compose up -d` or equivalent) — nginx serves the updated `app-version.json` immediately.

## Future phases (not in this change)

- **Wear:** wire the same check into the watch settings app once the phone path is proven.
- **Forced update:** add `minSupportedVersionCode` to the JSON and enforce a minimum floor.
- **Install tracking:** add an anonymous install-id ping for "installs per version" counts (needs a storage backend + privacy note).
