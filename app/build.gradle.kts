@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("dev.rikka.tools.refine") version "4.3.0"
}

android {
    namespace = "io.github.duzhaokun123.yayamf"
    compileSdk = 33

    defaultConfig {
        applicationId = "io.github.duzhaokun123.yayamf"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation(libs.ezXHelper)
    compileOnly(libs.xposed.api)

    compileOnly("dev.rikka.hidden:stub:4.2.0")
    implementation("dev.rikka.hidden:compat:4.2.0")
    compileOnly(project(":android-stub"))
}