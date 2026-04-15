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
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

fun desktopApp(args: Array<String>) {

    val protocolUrl = args.firstOrNull { it.startsWith("dev.ghostbyte.pixelix://") }

    if (isAppAlreadyRunning(protocolUrl)) {
        // If it's already running, the function sends the URL to the main app and exits
        System.exit(0)
    }

    startLinkListener { newUrl ->
        println("Received new URL while running: $newUrl")
    }

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

        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.APP_OPEN_URI)) {
                desktop.setOpenURIHandler { url ->
                    appComponent.systemUrlHandler.onRedirect(
                        url.uri.toString()
                    )
                }
            } else {
                println("APP_OPEN_URI is not supported on this platform")
            }
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

private fun isAppAlreadyRunning(url: String?): Boolean {
    return try {
        val socket = Socket("localhost", 49152)
        url?.let { socket.getOutputStream().write(it.toByteArray()) }
        socket.close()
        true
    } catch (e: Exception) {
        false
    }
}

private fun startLinkListener(onNewLink: (String) -> Unit) {
    thread(isDaemon = true) {
        val serverSocket = ServerSocket(49152)
        while (true) {
            val client = serverSocket.accept()
            val url = client.getInputStream().bufferedReader().readLine()
            if (url != null) onNewLink(url)
            client.close()
        }
    }
}