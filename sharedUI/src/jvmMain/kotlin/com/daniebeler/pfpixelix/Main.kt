package com.daniebeler.pfpixelix

import androidx.compose.runtime.LaunchedEffect
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
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

private val pendingUrls = ConcurrentLinkedQueue<String>()

@Volatile
private var redirectHandler: ((String) -> Unit)? = null

private fun dispatchUrl(url: String) {
    val handler = redirectHandler
    if (handler != null) handler(url) else pendingUrls.add(url)
}

fun desktopApp(args: Array<String>) {
    val protocolUrl = args.firstOrNull { it.startsWith("dev.ghostbyte.pixelix://") }

    if (isAppAlreadyRunning(protocolUrl)) {
        // If it's already running, the function sends the URL to the main app and exits
        System.exit(0)
    }

    startLinkListener { newUrl ->
        dispatchUrl(newUrl)
    }

    protocolUrl?.let { dispatchUrl(it) }

    application {
        FileKit.init("com.daniebeler.pfpixelix")
        configureJavaLogger(true)

        val appComponent = AppComponent.create(
            object : KmpContext() {}, DesktopAppIconManager()
        )

        SingletonImageLoader.setSafe {
            appComponent.provideImageLoader()
        }

        LaunchedEffect(Unit) {
            val handler: (String) -> Unit = { appComponent.systemUrlHandler.onRedirect(it) }
            redirectHandler = handler
            // flush anything that arrived before the app was ready
            while (true) handler(pendingUrls.poll() ?: break)
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
                width = 400.dp, height = 800.dp, position = WindowPosition.Aligned(Alignment.Center)
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
        Socket(InetAddress.getLoopbackAddress(), 49152).use { socket ->
            url?.let { socket.getOutputStream().write((it + "\n").toByteArray()) }
        }
        true
    } catch (_: Throwable) {
        false
    }
}

private fun startLinkListener(onNewLink: (String) -> Unit) {
    thread(isDaemon = true) {
        val serverSocket = ServerSocket(49152, 50, InetAddress.getLoopbackAddress())
        while (true) {
            runCatching {
                serverSocket.accept().use { client ->
                    client.getInputStream().bufferedReader().readLine()
                        ?.takeIf { it.startsWith("dev.ghostbyte.pixelix://") }?.let(onNewLink)
                }
            }
        }
    }
}