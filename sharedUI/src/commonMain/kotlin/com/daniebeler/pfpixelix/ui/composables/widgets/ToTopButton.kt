package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.chevron_up_outline

@Composable
fun ToTopButton(listState: LazyListState, refresh: () -> Unit) {
    val visible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    ToTopButtonContent(visible = visible, onScrollToTop = {
        listState.animateScrollToItem(0, 0)
    }, refresh = refresh)
}

@Composable
fun ToTopButton(staggeredGridState: LazyStaggeredGridState, refresh: () -> Unit) {
    val visible by remember {
        derivedStateOf {
            staggeredGridState.firstVisibleItemIndex > 0
        }
    }

    ToTopButtonContent(visible = visible, onScrollToTop = {
        staggeredGridState.animateScrollToItem(0, 0)
    }, refresh = refresh)
}

@Composable
private fun ToTopButtonContent(visible: Boolean, onScrollToTop: suspend () -> Unit, refresh: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(Modifier.fillMaxSize().padding(12.dp).padding(bottom = 60.dp), contentAlignment = Alignment.BottomEnd) {
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    onScrollToTop()
                }.invokeOnCompletion {
                    refresh()
                }
            },
                containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                Icon(vectorResource(Res.drawable.chevron_up_outline), contentDescription = null)
            }
        }
    }
}
