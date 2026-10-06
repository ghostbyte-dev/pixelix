plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ktorfit) apply false
}

flatpakSources {
    mustRunAfterTasks.set(listOf(":desktopApp:createDistributable"))

    targetPlatforms.set(setOf("linux-x64", "linux-arm64"))
    platformDependencies.set(setOf(
        "org.jetbrains.compose.desktop:desktop-jvm-{platform}:1.12.0",
    ))
}