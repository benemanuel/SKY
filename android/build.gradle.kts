plugins {
    id("com.android.application") version "8.2.0"
    kotlin("android") version "1.9.22"
}

android {
    namespace = "com.sky.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sky.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
