package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.chevron_up
import pixelix.app.generated.resources.jump_to_top

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
private fun LazyStaggeredGridState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0) {
                false
            } else {
                if (previousIndex != firstVisibleItemIndex) {
                    previousIndex > firstVisibleItemIndex
                } else {
                    previousScrollOffset >= firstVisibleItemScrollOffset
                }.also {
                    previousIndex = firstVisibleItemIndex
                    previousScrollOffset = firstVisibleItemScrollOffset
                }
            }
        }
    }.value
}

@Composable
fun ToTopButton(staggeredGridState: LazyStaggeredGridState, refresh: () -> Unit) {
    ToTopButtonContent(visible = staggeredGridState.isScrollingUp(), onScrollToTop = {
        staggeredGridState.animateScrollToItem(0, 0)
    }, refresh = refresh)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ToTopButtonContent(
    visible: Boolean, onScrollToTop: suspend () -> Unit, refresh: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = visible, enter = slideInVertically(), exit = slideOutVertically()
    ) {
        Box(
            Modifier.fillMaxSize().padding(12.dp).padding(top = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            Button(
                onClick = {
                    coroutineScope.launch {
                        onScrollToTop()
                    }.invokeOnCompletion {
                        refresh()
                    }
                },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(vectorResource(Res.drawable.chevron_up), contentDescription = null)
                    Text(stringResource(Res.string.jump_to_top))
                }
            }
        }
    }
}
