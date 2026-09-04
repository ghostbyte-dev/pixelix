@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ktorfit)
}

ktorfit {
    compilerPluginVersion.set("2.3.5")
}

kotlin {
    jvmToolchain(21)
    android {
        namespace = "com.daniebeler.pfpixelix"
        compileSdk = 37
        minSdk = 26

        androidResources { enable = true }
        compilerOptions { jvmTarget = JvmTarget.JVM_17 }

        withHostTest {}
    }

    jvm()

    listOf(
        iosArm64(), iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    wasmJs {
        browser()

        binaries.executable()
    }

    sourceSets {
        applyDefaultHierarchyTemplate()

        val skikoMain by creating {
            dependsOn(commonMain.get())
            iosMain.get().dependsOn(this)
            jvmMain.get().dependsOn(this)
            webMain.get().dependsOn(this)
        }

        val nonWebMain by creating {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            iosMain.get().dependsOn(this)
            jvmMain.get().dependsOn(this)
        }

        commonMain.dependencies {
            //compose
            api(libs.runtime)
            implementation(libs.ui)
            implementation(libs.jetbrains.material)
            implementation(libs.material3)
            implementation(libs.components.resources)
            implementation(libs.compose.ui.graphics)
            implementation(libs.compose.navigationevent)
            implementation(libs.compose.preview)
            //logger
            implementation(libs.kermit)

            //html parser
            implementation(libs.ksoup)

            //kotlinx
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections.immutable)

            //ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)

            //ktorfit
            implementation(libs.ktorfit)
            implementation(libs.ktorfit.call)

            //DI
            implementation(libs.kotlin.inject.runtime)

            //datastore
            implementation(libs.androidx.datastore.preferences)

            //file picker
            implementation(libs.filekit.compose)

            //lifecycle
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.savedstate)

            //navigation
            implementation(libs.androidx.navigation.compose)

            //annotation
            implementation(libs.androidx.annotation)

            //disk io
            implementation(libs.okio)

            //image loader
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
            implementation(libs.coil.svg)

            //image crop
            implementation(libs.krop)

            //video player
            implementation(libs.composemediaplayer)
        }

        androidMain.dependencies {
            implementation(libs.androidx.exifinterface)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)

            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.browser)

            implementation(libs.material)

            //media
            implementation(libs.coil.gif)
            implementation(libs.coil.video)

            // widget
            implementation(libs.androidx.glance.appwidget)
            implementation(libs.androidx.glance.material3)
            // work Manager
            implementation(libs.androidx.work.runtime.ktx)

            //blurhash
            implementation(libs.vanniktech.blurhash)
            implementation(libs.unifiedpush.connector)
            implementation(libs.embedded.fcm.distributor)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)

            //blurhash
            implementation(libs.vanniktech.blurhash)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.slf4j.simple)

            //blurhash
            implementation(libs.vanniktech.blurhash)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
    }
}

compose.resources {
    packageOfResClass = "pixelix.app.generated.resources"
}

dependencies {
    listOf(
        "kspAndroid", "kspJvm", "kspIosArm64", "kspIosSimulatorArm64", "kspWasmJs"
    ).forEach {
        add(it, libs.kotlin.inject.compiler.ksp)
    }
    androidRuntimeClasspath(libs.ui.tooling)
}

tasks.configureEach {
    if (this is KspAATask && name != "kspCommonMainKotlinMetadata") dependsOn("kspCommonMainKotlinMetadata")
}
