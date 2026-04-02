package com.daniebeler.pfpixelix.ui.composables.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.ui.composables.followers.FollowerElementComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.and
import pixelix.app.generated.resources.followed_by
import pixelix.app.generated.resources.mutual_followers
import pixelix.app.generated.resources.other
import pixelix.app.generated.resources.others

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MutualFollowersComposable(
    mutualFollowersState: MutualFollowersState, navController: NavController
) {

    val normalStyle = SpanStyle(color = MaterialTheme.colorScheme.onBackground)
    val boldStyle =
        SpanStyle(color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (mutualFollowersState.mutualFollowers.isNotEmpty()) {

        val listSize = mutualFollowersState.mutualFollowers.size

        val annotatedString = buildAnnotatedString {
            withStyle(style = normalStyle) {
                append(stringResource(Res.string.followed_by) + " ")
                withStyle(style = boldStyle) {
                    pushStringAnnotation(
                        tag = "account",
                        annotation = mutualFollowersState.mutualFollowers.first().id
                    )
                    append(mutualFollowersState.mutualFollowers.first().username)
                    pop()
                }

                if (listSize == 2) {
                    append(" " + stringResource(Res.string.and) + " ")
                    withStyle(style = boldStyle) {
                        pushStringAnnotation(
                            tag = "account",
                            annotation = mutualFollowersState.mutualFollowers[1].id
                        )
                        append(mutualFollowersState.mutualFollowers[1].username)
                        pop()
                    }
                }
                if (listSize > 2) {
                    append(", ")
                    withStyle(style = boldStyle) {
                        pushStringAnnotation(
                            tag = "account",
                            annotation = mutualFollowersState.mutualFollowers[1].id
                        )
                        append(mutualFollowersState.mutualFollowers[1].username)
                        pop()
                    }
                }

                if (listSize == 3) {
                    append(" " + stringResource(Res.string.and) + " ")
                    withStyle(style = boldStyle) {
                        pushStringAnnotation(
                            tag = "account",
                            annotation = mutualFollowersState.mutualFollowers[2].id
                        )
                        append(mutualFollowersState.mutualFollowers[2].username)
                        pop()
                    }
                }
                if (listSize > 3) {
                    append(", ")
                    withStyle(style = boldStyle) {
                        pushStringAnnotation(
                            tag = "account",
                            annotation = mutualFollowersState.mutualFollowers[2].id
                        )
                        append(mutualFollowersState.mutualFollowers[2].username)
                        pop()
                    }
                    append(" " + stringResource(Res.string.and) + " ")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        pushStringAnnotation(tag = "others", annotation = "others")
                        append((listSize - 3).toString())
                        if (listSize == 4) {
                            append(" " + stringResource(Res.string.other))
                        } else {
                            append(" " + stringResource(Res.string.others))
                        }

                        pop()
                    }
                }

            }


        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
            Box {
                AsyncImage(
                    model = mutualFollowersState.mutualFollowers.first().avatar,
                    contentDescription = "",
                    modifier = Modifier
                        .height(36.dp)
                        .width(36.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.background
                        )
                )

                if (listSize > 1) {
                    Row {
                        Spacer(modifier = Modifier.width(18.dp))
                        AsyncImage(
                            model = mutualFollowersState.mutualFollowers[1].avatar,
                            contentDescription = "",
                            modifier = Modifier
                                .height(36.dp)
                                .width(36.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.background
                                )
                        )
                    }
                }
                if (listSize > 2) {
                    Row {
                        Spacer(modifier = Modifier.width(36.dp))
                        AsyncImage(
                            model = mutualFollowersState.mutualFollowers[2].avatar,
                            contentDescription = "",
                            modifier = Modifier
                                .height(36.dp)
                                .width(36.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.background
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            ClickableText(text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                onClick = {
                    annotatedString.getStringAnnotations("others", it, it).firstOrNull()?.let {
                        showBottomSheet = true
                    }

                    annotatedString.getStringAnnotations("account", it, it)
                        .firstOrNull()?.let { annotation ->
                            navController.navigate(Destination.Profile(annotation.item))
                        }
                })
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                }, sheetState = sheetState
            ) {
                val lazyListState = rememberLazyListState()

                LazyColumn(state = lazyListState, content = {

                    item {
                        Text(
                            text = stringResource(Res.string.mutual_followers),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        HorizontalDivider(Modifier.padding(12.dp))
                    }

                    items(mutualFollowersState.mutualFollowers, key = {
                        it.id
                    }) {
                        FollowerElementComposable(account = it, navController)
                    }

                })
            }
        }
    }
}