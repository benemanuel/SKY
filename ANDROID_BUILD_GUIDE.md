# SKY Android App - Build Guide

## Prerequisites

To build and run the SKY Android app, you need:

1. **Android Studio** (recommended - easiest setup)
   - Download from: https://developer.android.com/studio
   - Includes Android SDK, Gradle, and JDK

2. **OR** Manual setup:
   - **Java Development Kit (JDK)** 11 or higher
   - **Android SDK** (API level 26+)
   - **Gradle** 8.2.0 or higher

## Option 1: Build with Android Studio (Recommended)

### Setup
1. **Install Android Studio**
   - Download from https://developer.android.com/studio
   - Run the installer

2. **Open the Project**
   - Launch Android Studio
   - Click "Open an Existing Project"
   - Navigate to: `C:\Users\avi\GitHub\SKY\android`
   - Click "Open"

3. **Build the App**
   - Wait for Gradle to sync (this may take 5-10 minutes on first build)
   - Click **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
   - Or use keyboard shortcut: **Ctrl + F9**

4. **Run on Device/Emulator**
   - Connect an Android device via USB, or start an emulator
   - Click **Run** (green play icon) or press **Shift + F10**
   - Select your device/emulator
   - The app will install and launch

### Build Output
- Debug APK: `android/app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `android/app/build/outputs/apk/release/app-release.apk`

## Option 2: Command Line Build

### Setup (Windows)

1. **Install JDK 11+**
   ```powershell
   # Using Chocolatey
   choco install openjdk11
   
   # Or download from: https://adoptopenjdk.net/
   ```

2. **Install Android SDK**
   - Download SDK tools from: https://developer.android.com/studio
   - Extract to: `C:\Android\sdk`
   - Add to PATH: `C:\Android\sdk\tools`

3. **Install Gradle**
   ```powershell
   choco install gradle
   # Or download from: https://gradle.org/releases/
   ```

4. **Create Gradle Wrapper** (in the android directory)
   ```powershell
   cd android
   gradle wrapper --gradle-version=8.2.0
   ```

### Build Commands

```powershell
# Navigate to android directory
cd C:\Users\avi\GitHub\SKY\android

# Build debug APK
.\gradlew assembleDebug

# Build release APK (requires signing key)
.\gradlew assembleRelease

# Build and install on connected device
.\gradlew installDebug

# Run on emulator/device (requires Android Studio or emulator running)
.\gradlew run
```

### Build Output
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `app/build/outputs/apk/release/app-release.apk`

## Option 3: Gradle Wrapper Setup

If you have Java installed but not Gradle, create the wrapper:

```powershell
cd C:\Users\avi\GitHub\SKY\android

# Download Gradle 8.2.0 and create wrapper scripts
gradle wrapper --gradle-version=8.2.0

# Then use the wrapper for builds
.\gradlew assembleDebug
```

## Running on Emulator

If you don't have a physical device:

### Using Android Studio
1. Tools → Device Manager → Create Virtual Device
2. Select a device profile (e.g., Pixel 6)
3. Select API level (26 or higher)
4. Click "Finish"
5. Once created, click the play button to start the emulator
6. Run the app (Shift + F10)

### From Command Line
```powershell
# List available emulators
.\gradlew listAvds

# Start an emulator
emulator -avd <emulator_name>

# Install and run
.\gradlew installDebug
.\gradlew run
```

## Running on Physical Device

1. **Enable Developer Mode**
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Developer options will appear in Settings

2. **Enable USB Debugging**
   - Settings → Developer Options → USB Debugging
   - Toggle it ON

3. **Connect via USB**
   - Plug phone into computer with USB cable
   - Tap "Allow" on the phone to authorize

4. **Install and Run**
   ```powershell
   .\gradlew installDebug
   .\gradlew run
   ```

Or from Android Studio:
- Click Run (Shift + F10)
- Your device will appear in the device list
- Select it and click OK

## Troubleshooting

### "Android SDK not found"
- Install Android Studio, or
- Set `ANDROID_SDK_ROOT` environment variable
- Example: `$env:ANDROID_SDK_ROOT = "C:\Android\sdk"`

### "Gradle not found"
- Install Gradle, or
- Create gradle wrapper: `gradle wrapper --gradle-version=8.2.0`

### Build fails with "Could not find com.android.tools.build:gradle"
- Ensure you have internet connection (first build downloads dependencies)
- Wait for initial build to complete (can take 10+ minutes)

### Emulator slow / not starting
- Android Studio's emulator is faster than Google's
- Alternative: Use Genymotion emulator
- Or use a physical device (often faster anyway)

## Next Steps

Once built successfully:
1. **Test the app**
   - Grant location permission when prompted
   - Check that celestial data displays correctly
   - Test dark mode toggle
   - Try setting custom location

2. **Customize**
   - Edit colors in `android/src/main/kotlin/com/sky/app/ui/theme/Color.kt`
   - Modify layout in `android/src/main/kotlin/com/sky/app/ui/SkyApp.kt`
   - Update calculations in `android/src/main/kotlin/com/sky/app/domain/CelestialCalculations.kt`

3. **Sign and Release**
   - Generate signing key: `keytool -genkey -v -keystore release.keystore -keyalg RSA -keysize 2048 -validity 10000`
   - Update `build.gradle.kts` with signing config
   - Build release APK: `.\gradlew assembleRelease`

## Resources

- **Android Developer Docs**: https://developer.android.com/docs
- **Gradle Documentation**: https://docs.gradle.org/
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Android SDK Setup**: https://developer.android.com/studio/install
