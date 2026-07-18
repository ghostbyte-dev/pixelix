package com.daniebeler.pfpixelix.ui.composables.states

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox

@Composable
fun EmptyStateComposable(emptyState: EmptyState, modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        InnerEmptyState(emptyState)
    }
}

@Composable
fun EmptyStateComposableWithoutRefresh(
    emptyState: EmptyState,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        InnerEmptyState(emptyState)
    }
}

@Composable
fun EmptyStateComposable(
    emptyState: EmptyState,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    CustomPullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item { InnerEmptyState(emptyState) }
        }
    }
}

@Composable
private fun InnerEmptyState(emptyState: EmptyState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (emptyState.icon != null) {
            Icon(
                imageVector = emptyState.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
        }

        if (emptyState.heading.isNotBlank()) {
            Text(
                text = emptyState.heading,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        if (emptyState.message.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = emptyState.message,
                modifier = Modifier.wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (emptyState.buttonText.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { emptyState.onClick() }) {
                Text(text = emptyState.buttonText)
            }
        }
    }
}
