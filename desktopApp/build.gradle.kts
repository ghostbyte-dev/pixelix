
import io.github.kdroidfilter.nucleus.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinJvm)
    id("io.github.kdroidfilter.nucleus") version "1.15.5"
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21)) // or 21
        vendor.set(JvmVendorSpec.AZUL)
    }
}

dependencies {
    implementation(project(":sharedUI"))
    implementation("io.github.kdroidfilter:nucleus.core-runtime:1.15.5")
}

nucleus.application {
    mainClass = "MainKt"

    nativeDistributions {
        targetFormats(TargetFormat.Dmg, TargetFormat.Nsis, TargetFormat.Deb, TargetFormat.Rpm)
        packageName = "Pixelix"
        packageVersion = "1.0.0"

        vendor = "Ghostbyte"
        description = "A powerful pixel art document manager."
        homepage = "https://github.com/ghostbyte/pixelix"
        copyright = "Copyright 2025 My Company"

        modules("jdk.unsupported")
        modules("jdk.unsupported.desktop")
        protocol("dev.ghostbyte.pixelix", "dev.ghostbyte.pixelix")
        fileAssociation(
            mimeType = "application/x-pixelix",
            extension = "pixelix",
            description = "Pixelix Document",
            linuxIconFile = project.file("desktopAppIcons/LinuxIcon.png"),
            windowsIconFile = project.file("desktopAppIcons/WindowsIcon.ico"),
            macOSIconFile = project.file("desktopAppIcons/MacosIcon.icns"),
        )
        linux {
            debMaintainer = "Emanuel Hiebeler <emanuel.hiebeler@gmail.com>"
            rpmLicenseType = "MIT"
            shortcut = true
            packageName = "pixelix"
            appRelease = "1"
            appCategory = "Utility"
            menuGroup = "Development"
        }
    }
}


/*
compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(Dmg, Msi, Rpm)
            packageName = "Pixelix"
            packageVersion = "1.0.0"

            //data store https://issuetracker.google.com/280205600
            modules("jdk.unsupported")
            modules("jdk.unsupported.desktop")

            linux {
                iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
            }
            windows {
                iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
            }
            macOS {
                iconFile.set(project.file("desktopAppIcons/MacosIcon.icns"))
                bundleID = "com.daniebeler.pfpixelix"
                infoPlist {
                    extraKeysRawXml = """
                      <key>CFBundleURLTypes</key>
                      <array>
                        <dict>
                          <key>CFBundleURLName</key>
                          <string>Pixelix auth redirect</string>
                          <key>CFBundleURLSchemes</key>
                          <array>
                            <string>dev.ghostbyte.pixelix</string>
                          </array>
                        </dict>
                      </array>
                    """.trimIndent()
                }
            }
        }
    }
}*/