package com.daniebeler.pfpixelix

import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeUIViewController
import coil3.SingletonImageLoader
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.domain.service.icon.IosAppIconManager
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.configureLogger
import platform.UIKit.UIViewController

class IosUrlCallback {
    var onRedirect: (String) -> Unit = {}
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
fun AppViewController(urlCallback: IosUrlCallback): UIViewController {
    //https://youtrack.jetbrains.com/issue/CMP-7623 iOS - Gesture handling is incorrect in 1.8.0-alpha03
    ComposeFoundationFlags.DragGesturePickUpEnabled = false

    var viewController: UIViewController? = null
    val appComponent = AppComponent.Companion.create(
        object : KmpContext() {
            override val viewController get() = viewController!!
        },
        IosAppIconManager()
    )

    configureLogger()

    SingletonImageLoader.setSafe {
        appComponent.provideImageLoader()
    }

    urlCallback.onRedirect = {
        appComponent.systemUrlHandler.onRedirect(it)
    }

    val finishApp = {}
    viewController = ComposeUIViewController(
        configure = {
            parallelRendering = true
        }
    ) {
        App(appComponent, finishApp)
    }

    return viewController
}