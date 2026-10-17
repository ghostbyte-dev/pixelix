package com.daniebeler.pfpixelix.ui.composables.places

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator

@Composable
fun PlaceComposable(
    navController: AppNavigator,
    placeId: String?,
    slug: String?,
    viewModel: PlaceViewModel = injectViewModel(key = "place-viewmodel-key$placeId$slug") { placeViewModel }
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 32.dp, start = 8.dp, end = 8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(text = "Place")
        }
    }

}