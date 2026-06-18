import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    kotlin("android")
}

// Load release signing credentials from keystore.properties (not committed).
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        load(FileInputStream(keystorePropertiesFile))
    }
}

android {
    namespace = "com.sky.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sky.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                fun prop(key: String): String =
                    keystoreProperties[key] as? String
                        ?: error("keystore.properties is missing '$key'")
                storeFile = file(prop("storeFile"))
                storePassword = prop("storePassword")
                keyAlias = prop("keyAlias")
                keyPassword = prop("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Home-screen widget (Compose-style App Widget)
    implementation("androidx.glance:glance-appwidget:1.1.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
