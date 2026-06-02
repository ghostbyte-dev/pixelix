package com.daniebeler.pfpixelix.ui.composables.direct_messages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.widgets.CustomPullToRefreshBox
import com.daniebeler.pfpixelix.ui.composables.widgets.InfiniteListHandler
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.utils.imeAwareInsets
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.arrow_left
import pixelix.app.generated.resources.beginning_of_chat_note
import pixelix.app.generated.resources.character_count
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.message
import pixelix.app.generated.resources.send

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ChatComposable(
    navController: NavController,
    accountId: String,
    viewModel: ChatViewModel = injectViewModel(key = "chat$accountId") { chatViewModel }
) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(Unit) {
        viewModel.getChat(accountId)
    }

    LaunchedEffect(viewModel.newMessageState) {
        if (viewModel.newMessageState.message != null) {
            lazyListState.animateScrollToItem(0)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        Box(
            modifier = Modifier.padding(top = TopAppBarDefaults.TopAppBarExpandedHeight + statusBarPadding - 24.dp)
                .fillMaxSize()
        ) {
            CustomPullToRefreshBox(
                isRefreshing = viewModel.chatState.isRefreshing,
                onRefresh = { viewModel.getChat(accountId, true) },
                modifier = Modifier
                    .imeAwareInsets(60.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 76.dp + navigationBarPadding, start = 8.dp, end = 8.dp)
                ) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.weight(1f),
                        reverseLayout = true,
                        contentPadding = PaddingValues(top = 24.dp),
                        content = {
                            if (viewModel.chatState.chat != null && viewModel.chatState.chat?.messages!!.isNotEmpty()) {

                                items(viewModel.chatState.chat!!.messages, key = {
                                    it.id
                                }) {
                                    ConversationElementComposable(
                                        message = it,
                                        { viewModel.deleteMessage(it.reportId) },
                                        navController = navController
                                    )
                                }

                                if (viewModel.chatState.isLoading) {
                                    item {
                                        LoadingComposable()
                                    }
                                }

                                if (viewModel.chatState.endReached) {
                                    item {
                                        EndOfListComposable()
                                    }
                                }
                            }

                            if (viewModel.chatState.chat != null && viewModel.chatState.chat?.messages?.isEmpty() == true) {
                                item {
                                    Spacer(modifier = Modifier.height(56.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(
                                                RoundedCornerShape(8.dp)
                                            )
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = stringResource(Res.string.beginning_of_chat_note),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        })
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            OutlinedTextField(
                                value = viewModel.newMessage,
                                onValueChange = { viewModel.newMessage = it },
                                label = { Text(stringResource(Res.string.message)) },
                                singleLine = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.background
                                ),
                                modifier = Modifier.weight(1f).heightIn(max = 200.dp),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                            )


                            Spacer(Modifier.width(12.dp))
                            if (viewModel.newMessageState.isLoading) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .height(56.dp)
                                        .width(56.dp)
                                        .padding(0.dp, 0.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                ) {
                                    LoadingComposable(size = 48.dp, color = MaterialTheme.colorScheme.onPrimary)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.sendMessage(accountId)
                                    },
                                    enabled = viewModel.newMessage.length <= 500,
                                    modifier =
                                        Modifier
                                            .height(56.dp)
                                            .width(56.dp)
                                            .padding(0.dp, 0.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(12.dp)
                                ) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.send),
                                        contentDescription = "send",
                                        Modifier
                                            .fillMaxSize()
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                        if (viewModel.newMessage.length > 470) {
                            Text(
                                text = stringResource(
                                    Res.string.character_count,
                                    viewModel.newMessage.length,
                                    500
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (viewModel.newMessage.length > 500)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    top = 4.dp,
                                    start = 4.dp,
                                    bottom = 4.dp
                                )
                            )
                        }
                    }
                    ErrorComposable(message = viewModel.chatState.error)
                }
            }
        }

        TopAppBar(
            modifier = Modifier.clip(
                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ),
            title = {
                if (viewModel.chatState.chat != null) {
                    Row(
                        modifier = Modifier.clickable {
                            navController.navigate(Destination.Profile(accountId))
                        }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = viewModel.chatState.chat!!.avatar,
                            error = painterResource(Res.drawable.default_avatar),
                            contentDescription = "",
                            modifier = Modifier
                                .height(46.dp)
                                .width(46.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        Column {

                            Text(text = viewModel.chatState.chat!!.name)
                            Text(
                                text = viewModel.chatState.chat!!.url.substringAfter("https://")
                                    .substringBefore("/"),
                                fontSize = 12.sp,
                                lineHeight = 6.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_left), contentDescription = ""
                    )
                }
            }, colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )


        InfiniteListHandler(lazyListState = lazyListState) {
            viewModel.getChatPaginated(accountId)
        }
    }
}
