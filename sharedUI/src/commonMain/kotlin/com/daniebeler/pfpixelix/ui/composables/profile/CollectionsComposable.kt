package com.daniebeler.pfpixelix.ui.composables.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.ui.composables.InfiniteListHandler
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.add_outline
import pixelix.app.generated.resources.collection_create_not_supported_explanation
import pixelix.app.generated.resources.collections
import pixelix.app.generated.resources.new_
import pixelix.app.generated.resources.new_collection

@Composable
fun CollectionsComposable(
    collectionsState: CollectionsState,
    getMoreCollections: () -> Unit,
    navController: NavController,
    addNewButton: Boolean = false,
    instanceDomain: String,
    openUrl: (url: String) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val showAddCollectionDialog = remember {
        mutableStateOf(false)
    }

    if (addNewButton || collectionsState.collections.isNotEmpty()) {
        Column {
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.collections),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )

            LazyRow(state = lazyListState) {
                items(collectionsState.collections) {
                    Column(
                        Modifier
                            .padding(12.dp)
                            .clickable {
                                navController.navigate(Destination.Collection(it.id))
                            }, horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = it.thumbnail,
                            contentDescription = "",
                            modifier = Modifier
                                .height(84.dp)
                                .width(84.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it.title, fontSize = 14.sp)
                    }
                }
                if (collectionsState.isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxHeight()
                                .height(96.dp)
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                }
                if (addNewButton) {
                    item {
                        Column(
                            Modifier
                                .padding(12.dp)
                                .clickable {
                                    showAddCollectionDialog.value = true
                                }, horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(84.dp)
                                    .width(84.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.add_outline),
                                    contentDescription = "add collection",
                                    Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = stringResource(Res.string.new_), fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        InfiniteListHandler(lazyListState) {
            getMoreCollections()
        }

        if (showAddCollectionDialog.value) {
            AlertDialog(title = {
                Text(text = stringResource(Res.string.new_collection))
            }, text = {
                Column {
                    Text(text = stringResource(Res.string.collection_create_not_supported_explanation))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$instanceDomain/i/collections/create",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = { openUrl("${instanceDomain}i/collections/create") })
                    )
                }
            }, onDismissRequest = {
                showAddCollectionDialog.value = false
            }, confirmButton = {
                TextButton(onClick = {
                    showAddCollectionDialog.value = false
                }) {
                    Text("Ok")
                }
            })
        }
    }


}