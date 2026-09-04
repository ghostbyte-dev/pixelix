package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniebeler.pfpixelix.ui.navigation.AppNavigator
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPost
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.StringFormat
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.pluralStringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.posts

@Composable
fun ExploreGridElement(
    keyId: String,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    fetcher: (String) -> Flow<Resource<PaginatedResponse<Post>>>,
    navController: AppNavigator,
    viewModel: TrendingHashtagElementViewModel = injectViewModel(key = "explore_$keyId") { trendingHashtagElementViewModel }
) {

    LaunchedEffect(keyId) {
        viewModel.loadItems(keyId, fetcher)
    }

    Column(
        Modifier.clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow).padding(vertical = 8.dp)
            .fillMaxWidth().clickable {
                onClick()
            }) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp).fillMaxWidth()
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (subtitle != null) {
                Text(
                    text = "  • $subtitle",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        Box(
            modifier = Modifier.padding(horizontal = 8.dp).clip(
                RoundedCornerShape(12.dp)
            )
        ) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.height(428.dp)
            ) {

                itemsIndexed(viewModel.postsState.posts) { index, post ->

                    val postsCount = viewModel.postsState.posts.size
                    val cornerRadius = 12.dp

                    // Calculate row position (0 = top, 1 = middle, 2 = bottom)
                    val row = index % 3
                    // Calculate column position
                    val col = index / 3
                    val totalCols = (postsCount + 2) / 3

                    val isFirstColumn = col == 0
                    val isLastColumn = col == totalCols - 1

                    val isTopRow = row == 0
                    val isBottomRow = row == 2 || index == postsCount - 1

                    // Determine individual corner radii based on boundary position
                    val topStart = if (isFirstColumn && isTopRow) cornerRadius else 0.dp
                    val bottomStart = if (isFirstColumn && isBottomRow) cornerRadius else 0.dp
                    val topEnd = if (isLastColumn && isTopRow) cornerRadius else 0.dp
                    val bottomEnd = if (isLastColumn && isBottomRow) cornerRadius else 0.dp

                    val itemShape = RoundedCornerShape(
                        topStart = topStart,
                        topEnd = topEnd,
                        bottomStart = bottomStart,
                        bottomEnd = bottomEnd
                    )

                    Box(
                        modifier = Modifier.width(140.dp).height(140.dp)
                    ) {
                        CustomPost(
                            post = post,
                            navController = navController,
                            roundedCornerShape = itemShape,
                        )
                    }
                }
            }
        }
    }
}