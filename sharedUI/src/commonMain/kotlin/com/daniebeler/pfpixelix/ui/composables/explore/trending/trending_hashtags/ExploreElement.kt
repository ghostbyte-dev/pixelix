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
import androidx.navigation.NavController
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
    fetcher: (String) -> Flow<Resource<PaginatedResponse<List<Post>>>>,
    navController: NavController,
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
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            if (subtitle != null) {
                Text(
                    text = "  • $subtitle", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary
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

                    val baseModifier = Modifier

                    val customModifier = when {
                        // Case for a single row
                        postsCount <= 3 -> {
                            when (index) {
                                0 -> baseModifier.clip(
                                    RoundedCornerShape(
                                        topStart = 12.dp, topEnd = 12.dp
                                    )
                                ) // Top-left corner
                                2 -> baseModifier.clip(
                                    RoundedCornerShape(
                                        bottomStart = 12.dp, bottomEnd = 12.dp
                                    )
                                ) // Bottom-left corner
                                else -> baseModifier // Fallback for safety
                            }
                        }
                        // Cases for multiple rows
                        index == 0 -> baseModifier.clip(RoundedCornerShape(topStart = 12.dp)) // Top-left corner
                        index == 2 -> baseModifier.clip(RoundedCornerShape(bottomStart = 12.dp)) // Bottom-start corner
                        index == postsCount - 1 && postsCount % 3 == 0 -> baseModifier.clip(
                            RoundedCornerShape(bottomEnd = 12.dp)
                        ) // Bottom-right corner
                        index >= postsCount - 3 && index % 3 == 0 -> baseModifier.clip(
                            RoundedCornerShape(topEnd = 12.dp)
                        ) // Top-right corner
                        else -> baseModifier
                    }

                    Box(
                        modifier = Modifier.width(140.dp).height(140.dp)
                    ) {
                        CustomPost(
                            post = post,
                            navController = navController,
                            roundedCornerShape = RoundedCornerShape(8.dp),
                            modifier = customModifier
                        )
                    }
                }
            }
        }
    }
}