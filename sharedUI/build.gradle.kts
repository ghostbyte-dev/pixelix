@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.metro)
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
        compileSdk {
            version = release(37) {
                minorApiLevel = 1
            }
        }
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
            implementation(libs.metro.viewmodel.compose)

            //datastore
            implementation(libs.androidx.datastore.preferences)

            //file picker
            implementation(libs.filekit.compose)

            //lifecycle
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.savedstate)

            //navigation
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodel.navigation3)

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

        webMain.dependencies {
            implementation(libs.navigation3.browser)
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

            //map
            implementation(libs.maplibre.compose)
            implementation(libs.maplibre.compose.material3)
            runtimeOnly("org.maplibre.compose:maplibre-compose-runtime-vulkan-android:0.20.0")
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)

            //blurhash
            implementation(libs.vanniktech.blurhash)

            //map
            implementation(libs.maplibre.compose)
            implementation(libs.maplibre.compose.material3)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.slf4j.simple)
            implementation(libs.metadata.extractor)
            //blurhash
            implementation(libs.vanniktech.blurhash)

            //map
            implementation(libs.maplibre.compose)
            implementation(libs.maplibre.compose.material3)
            runtimeOnly("org.maplibre.compose:maplibre-compose-runtime-vulkan-linux-x64:0.20.0")
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
    }
}

val generateVersionResources = tasks.register("generateVersionResources") {
    val commitHash = providers.exec {
        workingDir(rootProject.layout.projectDirectory)
        commandLine("git", "rev-parse", "HEAD")
    }.standardOutput.asText.map { it.trim() }

    inputs.property("versionName", providers.gradleProperty("app.versionName"))
    inputs.property("versionCode", providers.gradleProperty("app.versionCode"))
    inputs.property("commitHash", commitHash)
    outputs.file(layout.projectDirectory.file("src/commonMain/composeResources/values/version.xml"))

    doLast {
        fun xmlEscape(value: String) = value.replace("&", "&amp;")
            .replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&apos;")

        outputs.files.singleFile.apply {
            parentFile.mkdirs()
            writeText(
                """
                <resources>
                    <string name="version_name">${xmlEscape(inputs.properties.getValue("versionName").toString())}</string>
                    <string name="version_code">${xmlEscape(inputs.properties.getValue("versionCode").toString())}</string>
                    <string name="version_commit_hash">${xmlEscape(inputs.properties.getValue("commitHash").toString().take(8))}</string>
                </resources>
                """.trimIndent() + "\n"
            )
        }
    }
}

// Xcode consumes this generated plist after its Gradle build phase.
tasks.register("generateIosInfoPlist") {
    inputs.file(rootProject.layout.projectDirectory.file("iosApp/iosApp/Info.plist"))
    inputs.property("versionName", providers.gradleProperty("app.versionName"))
    inputs.property("versionCode", providers.gradleProperty("app.versionCode"))
    outputs.file(rootProject.layout.projectDirectory.file("iosApp/build/generated/Info.plist"))

    doLast {
        val template = inputs.files.singleFile.readText()
        outputs.files.singleFile.apply {
            parentFile.mkdirs()
            writeText(
                template.replace("@APP_VERSION_NAME@", inputs.properties.getValue("versionName").toString())
                    .replace("@APP_VERSION_CODE@", inputs.properties.getValue("versionCode").toString())
            )
        }
    }
}

compose.resources {
    packageOfResClass = "pixelix.app.generated.resources"
}

dependencies {
    androidRuntimeClasspath(libs.ui.tooling)
}

tasks.configureEach {
    if (name == "convertXmlValueResourcesForCommonMain") dependsOn(generateVersionResources)
}
