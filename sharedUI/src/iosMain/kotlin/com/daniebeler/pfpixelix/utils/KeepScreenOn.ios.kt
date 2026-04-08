package com.daniebeler.pfpixelix.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import platform.UIKit.UIApplication

@Composable
actual fun KeepScreenOn() {
    DisposableEffect(Unit) {
        val sharedApp = UIApplication.sharedApplication
        sharedApp.idleTimerDisabled = true

        onDispose {
            sharedApp.idleTimerDisabled = false
        }
    }
}