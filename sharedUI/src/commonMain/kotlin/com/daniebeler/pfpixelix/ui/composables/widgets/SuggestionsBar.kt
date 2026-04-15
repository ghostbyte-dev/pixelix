package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.composables.post.SuggestionsState

@Composable
fun SuggestionsBar(
    state: SuggestionsState,
    onSelected: (String) -> Unit,
    bottomBarPadding: Boolean,
    modifier: Modifier = Modifier
) {
    if (state.suggestions.isNotEmpty()) {
        LazyRow(
            modifier = modifier
                .padding(bottom = if (bottomBarPadding) 60.dp else 0.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHighest).padding(horizontal = 4.dp, vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(state.suggestions) { suggestion ->
                TextButton(
                    onClick = { onSelected(suggestion) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp).border(
                        1.dp, MaterialTheme.colorScheme.onSurface,
                        RoundedCornerShape(12.dp)
                    ).padding()
                ) {
                    Text(
                        text = suggestion,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
