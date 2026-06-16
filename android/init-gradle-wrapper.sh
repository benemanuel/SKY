#!/bin/bash
# Initialize Gradle wrapper for the SKY Android project
# This script requires Gradle to be installed

echo "Initializing Gradle wrapper..."
echo ""

# Check if gradle is installed
if ! command -v gradle &> /dev/null; then
    echo "Error: Gradle is not installed or not in PATH"
    echo ""
    echo "Please install Gradle from: https://gradle.org/releases/"
    echo "Or install Android Studio from: https://developer.android.com/studio"
    echo ""
    echo "After installation, re-run this script."
    exit 1
fi

echo "Gradle found. Creating wrapper..."
echo ""

# Create gradle wrapper with Gradle 8.2.0
gradle wrapper --gradle-version=8.2.0

if [ $? -ne 0 ]; then
    echo ""
    echo "Error: Failed to create gradle wrapper"
    exit 1
fi

echo ""
echo "Gradle wrapper initialized successfully!"
echo ""
echo "You can now build the app with:"
echo "  ./gradlew assembleDebug"
echo ""
