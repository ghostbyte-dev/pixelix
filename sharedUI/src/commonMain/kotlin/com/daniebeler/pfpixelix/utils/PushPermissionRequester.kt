package com.daniebeler.pfpixelix.utils

import androidx.compose.runtime.Composable

@Composable
expect fun PushPermissionRequester(onRequested: () -> Unit)