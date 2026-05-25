package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CustomPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    animatedBox: Boolean = false,
    threshold: Dp = PullToRefreshDefaults.PositionalThreshold,
    content: @Composable BoxScope.() -> Unit,
) {
    if (animatedBox) {
        return CustomPullToRefreshBoxAnimated(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = modifier,
            enabled = enabled,
            threshold = threshold,
            content = content
        )
    }
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        enabled = enabled,
        modifier = modifier,
        threshold = threshold,
        indicator = {
            PullToRefreshDefaults.IndicatorBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                ContainedLoadingIndicator()
            }
        }
    ) {
        content()
    }
}

@Composable
private fun CustomPullToRefreshBoxAnimated(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    threshold: Dp = PullToRefreshDefaults.PositionalThreshold,
    content: @Composable BoxScope.() -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val density = LocalDensity.current
    var isDismissing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            isDismissing = true
        }
        if (!isRefreshing) {
            snapshotFlow { pullToRefreshState.distanceFraction }.collectLatest { fraction ->
                if (fraction == 0f) {
                    isDismissing = false
                }
            }
        }
    }

    val targetTranslation by remember(isRefreshing, isDismissing) {
        derivedStateOf {
            if (isRefreshing || isDismissing) {
                0f
            } else {
                val thresholdPx = with(density) { PullToRefreshDefaults.PositionalThreshold.toPx() }
                pullToRefreshState.distanceFraction * thresholdPx * 0.5f
            }
        }
    }

    val animatedTranslation by animateFloatAsState(
        targetValue = targetTranslation,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = 0.65f),
        label = "ContentTranslation"
    )
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        enabled = enabled,
        threshold = threshold,
        modifier = modifier,
        indicator = {
            PullToRefreshDefaults.IndicatorBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                ContainedLoadingIndicator()
            }
        }
    ) {
        Box(
            Modifier.fillMaxSize()
                .graphicsLayer {
                    translationY = animatedTranslation
                }) {
            content()
        }
    }
}