import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    kotlin("android")
}

// Reuse the app's release signing credentials (keystore.properties, git-ignored).
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        load(FileInputStream(keystorePropertiesFile))
    }
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

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                fun prop(key: String): String =
                    keystoreProperties[key] as? String
                        ?: error("keystore.properties is missing '$key'")
                storeFile = rootProject.file(prop("storeFile"))
                storePassword = prop("storePassword")
                keyAlias = prop("keyAlias")
                keyPassword = prop("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
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
    // Wearable Data Layer: receive the Center choice pushed from the phone.
    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation(kotlin("stdlib"))
}
