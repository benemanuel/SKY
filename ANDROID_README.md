# SKY Android App

A modern Android application that displays celestial and temporal information including lunar cycles, seasons, and sunrise/sunset times based on the user's location.

## Features

- **Current Date & Time**: Displays the current day of the week in Hebrew, time, and date with animated updates
- **Week Progress**: Visual representation of the week's progress with a segmented progress indicator
- **Lunar Cycle Tracking**: Shows current lunar phase, day in the lunar cycle, and illumination percentage
- **Seasonal Information**: Displays current season with days elapsed and remaining in the season
- **Dynamic Day/Night Cycle**: Calculates and displays sunrise/sunset times and total daylight duration based on location
- **Geolocation Support**: Uses device location or allows manual location input via coordinates
- **Dark Mode Support**: Toggle between light and dark themes with persistent preferences

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material3)
- **Architecture**: MVVM with ViewModel and StateFlow
- **Data Persistence**: DataStore Preferences
- **Location Services**: Google Play Services Location API
- **Build System**: Gradle with Kotlin DSL

## Project Structure

```
android/
├── src/main/
│   ├── kotlin/com/sky/app/
│   │   ├── MainActivity.kt
│   │   ├── domain/
│   │   │   └── CelestialCalculations.kt
│   │   ├── data/
│   │   │   ├── LocationRepository.kt
│   │   │   └── PreferencesRepository.kt
│   │   ├── viewmodel/
│   │   │   └── SkyViewModel.kt
│   │   └── ui/
│   │       ├── SkyApp.kt
│   │       ├── theme/
│   │       │   ├── Theme.kt
│   │       │   ├── Color.kt
│   │       │   └── Typography.kt
│   │       └── components/
│   │           ├── DateTimeCard.kt
│   │           ├── WeekProgressCard.kt
│   │           ├── LunarCard.kt
│   │           ├── SeasonCard.kt
│   │           ├── SunTimesCard.kt
│   │           └── LocationInputCard.kt
│   └── res/
│       ├── values/
│       │   ├── strings.xml
│       │   └── styles.xml
├── AndroidManifest.xml
├── build.gradle.kts
└── settings.gradle.kts
```

## Requirements

- Android SDK 26 or higher (minimum API level 26)
- Android Studio Arctic Fox or later
- Kotlin 1.9.22 or later
- Gradle 8.2.0 or later

## Building the App

1. **Clone or navigate to the repository:**
   ```bash
   cd android
   ```

2. **Build the debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Build the release APK:**
   ```bash
   ./gradlew assembleRelease
   ```

4. **Install on a device/emulator:**
   ```bash
   ./gradlew installDebug
   ```

## Running the App

1. **Open in Android Studio:**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `android` directory
   - Click "Open"

2. **Run on emulator or device:**
   - Connect an Android device or start an emulator
   - Click the "Run" button (green play icon) in Android Studio
   - Select the target device

3. **Permissions:**
   - The app requests fine location permission on first run
   - Users can grant permission or provide custom coordinates manually

## Key Algorithms

### Lunar Phase Calculation
- Based on a reference new moon date (January 6, 2000)
- Uses a 29.53 day lunar cycle
- Calculates illumination percentage and phase name

### Solar Times Calculation
- Implements Julian Day conversion
- Uses solar position algorithm
- Accounts for user's latitude and longitude
- Calculates sunrise, sunset, and day length

### Season Calculation
- Determines current season based on date
- Tracks days elapsed and remaining in season
- Provides visual progress indicator

## Dependencies

### Core Android
- `androidx.activity:activity-compose` - Activity integration with Compose
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle aware coroutines
- `androidx.datastore:datastore-preferences` - Preferences data store

### Compose & Material
- `androidx.compose.ui:ui` - Core Compose UI
- `androidx.compose.material3:material3` - Material3 components
- `androidx.compose.ui:ui-tooling-preview` - Compose preview

### Google Play Services
- `com.google.android.gms:play-services-location` - Location services

## License

This project is part of the SKY project suite.

## Notes

- The app uses the device's system location for timezone calculations
- Solar calculations are approximations suitable for most use cases
- Lunar phase calculations use a simplified model
- For production use, consider more precise astronomical libraries if needed
