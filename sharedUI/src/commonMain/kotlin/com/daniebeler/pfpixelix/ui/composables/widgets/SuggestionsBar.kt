package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.ui.composables.post.SuggestionsState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SuggestionsBar(
    state: SuggestionsState,
    onSelected: (String) -> Unit,
    bottomBarPadding: Boolean,
    modifier: Modifier = Modifier
) {
    val navigationBarPadding =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    val keyboardHeight = with(density) { imeInsets.getBottom(this).toDp() }

    if (state.suggestions.isNotEmpty() && keyboardHeight > 10.dp) {
        LazyRow(
            modifier = modifier
                .padding(bottom = if (bottomBarPadding) 60.dp + navigationBarPadding else 0.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(state.suggestions) { suggestion ->
                OutlinedButton(
                    onClick = { onSelected(suggestion.first) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    suggestion.second?.let {
                        AsyncImage(
                            model = suggestion.second,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp).clip(CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(
                        text = suggestion.first,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
