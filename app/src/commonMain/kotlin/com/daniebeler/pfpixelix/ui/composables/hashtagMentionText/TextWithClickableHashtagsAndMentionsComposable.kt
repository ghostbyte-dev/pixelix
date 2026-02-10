package com.daniebeler.pfpixelix.ui.composables.hashtagMentionText

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.ui.navigation.Destination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.read_less
import pixelix.app.generated.resources.read_more

@Composable
fun HashtagsMentionsTextView(
    text: String,
    modifier: Modifier = Modifier,
    mentions: List<Account>?,
    navController: NavController,
    openUrl: (url: String) -> Unit,
    textSize: TextUnit? = null,
    maximumLines: Int = Int.MAX_VALUE,
    viewModel: TextWithClickableHashtagsAndMentionsViewModel = injectViewModel(key = "hashtags-mentions-tv$text") { textWithClickableHashtagsAndMentionsViewModel }
) {
    var expanded by remember { mutableStateOf(false) }
    val maxLines = if (expanded) Int.MAX_VALUE else maximumLines
    var showReadMoreButtonState by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme
    val textStyle = SpanStyle(color = colorScheme.onBackground)
    val primaryStyle = SpanStyle(color = colorScheme.primary)

    val hashtags =
        Regex("(?=[^\\w!])[@#][\\u4e00-\\u9fa5\\w']+(?:@[\\w']+)?(?:\\.\\w+)*(?:\\/\\w+)*|https?:\\/\\/\\S+")

    val annotatedStringList = remember(text) {

        var lastIndex = 0
        val annotatedStringList = mutableStateListOf<AnnotatedString.Range<String>>()

        // Add a text range for hashtags
        for (match in hashtags.findAll(text)) {

            val start = match.range.first
            val end = match.range.last + 1

            val string = text.substring(start, end)

            if (start > lastIndex) {
                annotatedStringList.add(
                    AnnotatedString.Range(
                        text.substring(lastIndex, start), lastIndex, start, "text"
                    )
                )
            }
            if (string.startsWith("#")) {
                annotatedStringList.add(
                    AnnotatedString.Range(string, start, end, "tag")
                )
            } else if (string.startsWith("@")) {
                annotatedStringList.add(
                    AnnotatedString.Range(string, start, end, "account")
                )
            } else {
                annotatedStringList.add(
                    AnnotatedString.Range(string, start, end, "link")
                )
            }

            lastIndex = end
        }

        // Add remaining text
        if (lastIndex < text.length) {
            annotatedStringList.add(
                AnnotatedString.Range(
                    text.substring(lastIndex, text.length), lastIndex, text.length, "text"
                )
            )
        }
        annotatedStringList
    }

    // Build an annotated string
    val annotatedString = buildAnnotatedString {
        annotatedStringList.forEach {
            if (it.tag == "tag" || it.tag == "account" || it.tag == "link") {
                pushStringAnnotation(tag = it.tag, annotation = it.item)
                withStyle(style = primaryStyle) { append(it.item) }
                pop()
            } else {
                withStyle(style = textStyle) { append(it.item) }
            }
        }
    }

    Column(modifier = Modifier.animateContentSize()) {
        ClickableText(text = annotatedString, style = if (textSize != null) {
            MaterialTheme.typography.bodyMedium.copy(fontSize = textSize)
        } else {
            MaterialTheme.typography.bodyMedium
        },
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                if (textLayoutResult.lineCount > maxLines -1) {           //Adding this check to avoid ArrayIndexOutOfBounds Exception
                    if (textLayoutResult.isLineEllipsized(maxLines-1)) showReadMoreButtonState = true
                }
            },
            modifier = modifier, onClick = { position ->
                CoroutineScope(Dispatchers.Main).launch {
                    val annotatedStringRange =
                        annotatedStringList.firstOrNull { it.start <= position && position < it.end }
                    if (annotatedStringRange != null) {
                        if (annotatedStringRange.tag == "tag" || annotatedStringRange.tag == "account") {
                            val newItem = annotatedStringRange.item.drop(1)
                            if (annotatedStringRange.tag == "tag") {
                                navController.navigate(Destination.HashtagTimeline(newItem))
                            } else {
                                if (mentions == null) {
                                    navController.navigate(Destination.ProfileByUsername(annotatedStringRange.item.drop(1)))
                                } else {
                                    var account = mentions.find { account: Account -> account.acct == newItem }
                                    if (account == null) {
                                        account = mentions.find { account: Account -> account.username == newItem }
                                    }
                                    if (account != null) {
                                        //get my account id and check if it is mine account
                                        val myAccountId = viewModel.getMyAccountId()
                                        if (account.id == myAccountId) {
                                            navController.navigate(Destination.OwnProfile)
                                        } else {
                                            navController.navigate(Destination.Profile(account.id))
                                        }
                                    }
                                }
                            }
                        } else if (annotatedStringRange.tag == "link") {
                            openUrl(annotatedStringRange.item)
                        }
                    }
                }
            })
        if (showReadMoreButtonState) {
            Text(
                text = if (expanded) stringResource(Res.string.read_less) else stringResource(Res.string.read_more),
                color = Color.Gray,
                modifier = Modifier.clickable {
                    expanded = !expanded
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

}