package com.daniebeler.pfpixelix.ui.composables.settings.notification_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.mediumTopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.PushDistributorPref
import com.daniebeler.pfpixelix.ui.composables.widgets.CardButton
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.lock
import pixelix.app.generated.resources.notification_settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsComposable(
    navController: AppNavigator,
    viewModel: NotificationSettingsViewModel = injectViewModel(key = "notificationSettings") { notificationSettingsViewModel }
) {
    LifecycleResumeEffect(Unit) {
        viewModel.refreshPermissionState()
        onPauseOrDispose { }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
            TopAppBar(
                modifier = Modifier.clip(
                    RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ), title = {
                    Text(
                        stringResource(Res.string.notification_settings),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }, navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_left),
                            contentDescription = ""
                        )
                    }
                }, colors = mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )

        }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier.padding(top = 24.dp).padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh).padding(16.dp)
                ) {
                    Text(
                        text = "Unified Push",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "UnifiedPush is a decentralized push notification system that lets you choose the service you want to use. It’s designed to be privacy-friendly, flexible, and open — making it perfect if you want control over your push notifications.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                viewModel.hasPushNotificationPermission.let { hasPermission ->
                    val bgColor = if (hasPermission) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                    val textColor = if (hasPermission) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(bgColor).padding(16.dp)
                    ) {
                        Text(
                            text = if (hasPermission) {
                                "Permission is set"
                            } else {
                                "Permission is missing"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (hasPermission) {
                                "Permission is set, you are good to go for notifications"
                            } else {
                                "Permission is not set yet, you have to set the permissions in order for your push notifications to work"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (!hasPermission) {
                            CardButton(
                                leadingIcon = Res.drawable.lock,
                                title = "Allow notifications",
                                onClick = {
                                    viewModel.openAppSettings()
                                },
                                cardColor = MaterialTheme.colorScheme.error,
                                textColor = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }

                PushDistributorPref()
            }
        }
    }
}
