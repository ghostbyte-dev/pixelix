package com.daniebeler.pfpixelix.utils

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import co.touchlab.kermit.Logger

@androidx.compose.runtime.Composable
actual fun PushPermissionRequester(onRequested: () -> Unit) {

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Logger.d("PushNotifications") {
                "granted"
            }
        } else {
            Logger.d("PushNotifications") {
                "not granted"
            }
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

}
