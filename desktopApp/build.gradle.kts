import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":sharedUI"))
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
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
}