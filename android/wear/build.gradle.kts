plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.sky.app.wear"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sky.app"
        minSdk = 30          // Wear OS 3+
        targetSdk = 34       // Wear OS 4/5 (Galaxy Watch 6)
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core"))
    implementation("androidx.core:core-ktx:1.13.1")
    // Code-based watch face (Canvas renderer).
    implementation("androidx.wear.watchface:watchface:1.2.1")
    implementation(kotlin("stdlib"))
}
