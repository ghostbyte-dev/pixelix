package com.daniebeler.pfpixelix

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.SingletonImageLoader
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.domain.service.icon.DesktopAppIconManager
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.configureJavaLogger
import io.github.vinceglb.filekit.FileKit
import java.awt.Desktop
import java.awt.Dimension

fun main() {
    application {
        FileKit.init("com.daniebeler.pfpixelix")
        configureJavaLogger()

        val appComponent = AppComponent.Companion.create(
            object : KmpContext() {},
            DesktopAppIconManager()
        )

        SingletonImageLoader.setSafe {
            appComponent.provideImageLoader()
        }

        Desktop.getDesktop().setOpenURIHandler { url ->
            appComponent.systemUrlHandler.onRedirect(
                url.uri.toString()
            )
        }

        Window(
            title = "Pixelix",
            state = rememberWindowState(
                width = 400.dp,
                height = 800.dp,
                position = WindowPosition.Aligned(Alignment.Center)
            ),
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(400, 600)
            App(appComponent) { exitApplication() }
        }
    }
}