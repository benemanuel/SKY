plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.sky.app.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
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
    implementation(kotlin("stdlib"))
}
