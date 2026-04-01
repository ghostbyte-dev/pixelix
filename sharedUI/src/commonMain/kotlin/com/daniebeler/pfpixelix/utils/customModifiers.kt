package com.daniebeler.pfpixelix.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.imeAwareInsets(substractHeight: Dp): Modifier {
    val imeInsets = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val systemNavigationBarHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomPadding = if (imeInsets > (substractHeight + systemNavigationBarHeight)) {
        imeInsets.minus(substractHeight + systemNavigationBarHeight)
    } else {
        0.dp
    }
    return this then Modifier.padding(bottom = bottomPadding)
}