package com.daniebeler.pfpixelix

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import coil3.SingletonImageLoader
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.domain.service.icon.WebAppIconManager
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.configureLogger

@OptIn(ExperimentalComposeUiApi::class)
fun webApp() {
    val appComponent = AppComponent.create(
        object : KmpContext() {},
        WebAppIconManager()
    )

    configureLogger(false)

    SingletonImageLoader.setSafe {
        appComponent.provideImageLoader()
    }

    setOAuthRedirectCallback {
        appComponent.systemUrlHandler.onRedirect(it)
    }

    ComposeViewport {
        App(
            appComponent = appComponent,
            exitApp = { /* browser Back leaves the app from its root */ },
        )
    }
}


private fun setOAuthRedirectCallback(cb: (String) -> Unit): Unit =
    js("{ window.pixelixOAuthCallback = cb; }")
