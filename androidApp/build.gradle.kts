import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.daniebeler.pfpixelix"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.daniebeler.pfpixelix"
        minSdk = 26
        targetSdk = 37
        versionCode = 47
        versionName = "5.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }

        create("demo") {
            initWith(getByName("debug"))
            isMinifyEnabled = true
            isDebuggable = false
            isProfileable = false
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    packaging.resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

dependencies {
    implementation(project(":sharedUI"))
}

tasks.configureEach {
    if (name.contains("ArtProfile")) {
        enabled = false
    }
}