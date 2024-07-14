@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("dev.rikka.tools.refine") version "4.3.0"
}

android {
    namespace = "io.github.duzhaokun123.yayamf"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.duzhaokun123.yayamf"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        languageVersion = "2.0"
        jvmTarget = "17"
    }

    androidResources.additionalParameters("--allow-reserved-package-id", "--package-id", "0x64")
}

dependencies {
    compileOnly(libs.xposed.api)

    compileOnly("dev.rikka.hidden:stub:4.2.0")
    implementation("dev.rikka.hidden:compat:4.2.0")
    compileOnly(project(":android-stub"))

    implementation("io.github.duzhaokun123:YAXH:main-SNAPSHOT")

    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")

    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.wear:wear:1.3.0") // FIXME: use other RoundedDrawable

    val libsuVersion = "6.0.0"
    // The core module that provides APIs to a shell
    implementation("com.github.topjohnwu.libsu:core:${libsuVersion}")

}