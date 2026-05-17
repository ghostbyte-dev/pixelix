package com.daniebeler.pfpixelix.ui.composables.hashtagMentionText

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.Emoji
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
    emojis: List<Emoji> = emptyList(),
    viewModel: TextWithClickableHashtagsAndMentionsViewModel = injectViewModel(key = "hashtags-mentions-tv$text") { textWithClickableHashtagsAndMentionsViewModel }
) {
    var expanded by remember { mutableStateOf(false) }
    val maxLines = if (expanded) Int.MAX_VALUE else maximumLines
    var showReadMoreButtonState by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme
    val textStyle = SpanStyle(color = colorScheme.onBackground)
    val primaryStyle = SpanStyle(color = colorScheme.primary)

    var regexString =
        "(?=[^\\w!])[@#][\\u4e00-\\u9fa5\\w']+(?:@[\\w']+)?(?:\\.\\w+)*(?:\\/\\w+)*|https?:\\/\\/\\S+"

    if (emojis.isNotEmpty()) {
        regexString += "|:[\\w-]+:"
    }
    val hashtags =
        Regex(regexString)


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
            } else if (string.startsWith(":")) {
                if (emojis.find { it.shortcode == string.drop(1).dropLast(1) } != null) {
                    annotatedStringList.add(
                        AnnotatedString.Range(string, start, end, "emoji")
                    )
                } else {
                    annotatedStringList.add(
                        AnnotatedString.Range(
                            text.substring(lastIndex, start), lastIndex, start, "text"
                        )
                    )
                }
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
    val scope = rememberCoroutineScope()

    val annotatedString = buildAnnotatedString {
        annotatedStringList.forEach { element ->
            when (element.tag) {
                "tag", "account", "link" -> {
                    val link = LinkAnnotation.Clickable(
                        tag = element.tag,
                        styles = TextLinkStyles(style = primaryStyle),
                        linkInteractionListener = {
                            val value = element.item.drop(1)
                            when (element.tag) {
                                "tag" -> navController.navigate(Destination.HashtagTimeline(value))
                                "account" -> {
                                    if (mentions == null) {
                                        navController.navigate(Destination.ProfileByUsername(value))
                                    } else {
                                        val account = mentions.find { it.acct == value }
                                            ?: mentions.find { it.username == value }

                                        if (account != null) {
                                            scope.launch {
                                                val myAccountId = viewModel.getMyAccountId()
                                                if (account.id == myAccountId) {
                                                    navController.navigate(Destination.OwnProfile)
                                                } else {
                                                    navController.navigate(
                                                        Destination.Profile(
                                                            account.id
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                "link" -> {
                                    openUrl(element.item)
                                }
                            }
                        }
                    )

                    withLink(link) {
                        append(element.item)
                    }
                }

                "emoji" -> {
                    appendInlineContent(element.item, element.item)
                }

                else -> {
                    withStyle(style = textStyle) {
                        append(element.item)
                    }
                }
            }
        }
    }

    val inlineContentMap = annotatedStringList
        .filter { it.tag == "emoji" }
        .associate { element ->
            val emoji: Emoji? = emojis.find { it.shortcode == element.item.drop(1).dropLast(1) }

            element.item to InlineTextContent(
                Placeholder(
                    width = textSize ?: MaterialTheme.typography.bodyMedium.fontSize,
                    height = textSize ?: MaterialTheme.typography.bodyMedium.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                if (emoji != null) {
                    AsyncImage(
                        model = emoji.staticUrl,
                        contentDescription = "emoji",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

    Column(modifier = Modifier.animateContentSize()) {
        SelectionContainer {
            Text(
                text = annotatedString,
                style = if (textSize != null) {
                    MaterialTheme.typography.bodyMedium.copy(fontSize = textSize)
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLines,
                onTextLayout = { textLayoutResult: TextLayoutResult ->
                    if (textLayoutResult.lineCount > maxLines - 1) {
                        if (textLayoutResult.isLineEllipsized(maxLines - 1)) showReadMoreButtonState =
                            true
                    }
                },
                modifier = modifier,
                inlineContent = inlineContentMap
            )
        }
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