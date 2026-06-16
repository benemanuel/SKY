@echo off
REM Initialize Gradle wrapper for the SKY Android project
REM This script requires Gradle to be installed and in PATH

echo Initializing Gradle wrapper...
echo.

REM Check if gradle is installed
gradle --version >nul 2>&1
if errorlevel 1 (
    echo Error: Gradle is not installed or not in PATH
    echo.
    echo Please install Gradle from: https://gradle.org/releases/
    echo Or install Android Studio from: https://developer.android.com/studio
    echo.
    echo After installation, re-run this script.
    pause
    exit /b 1
)

echo Gradle found. Creating wrapper...
echo.

REM Create gradle wrapper with Gradle 8.2.0
gradle wrapper --gradle-version=8.2.0

if errorlevel 1 (
    echo.
    echo Error: Failed to create gradle wrapper
    pause
    exit /b 1
)

echo.
echo Gradle wrapper initialized successfully!
echo.
echo You can now build the app with:
echo   .\gradlew assembleDebug
echo.
pause
