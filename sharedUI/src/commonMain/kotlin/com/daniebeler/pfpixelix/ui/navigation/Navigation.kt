package com.daniebeler.pfpixelix.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Dialog
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import com.daniebeler.pfpixelix.EdgeToEdgeDialogProperties
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.HomeComposable
import com.daniebeler.pfpixelix.ui.composables.collection.CollectionComposable
import com.daniebeler.pfpixelix.ui.composables.direct_messages.chat.ChatComposable
import com.daniebeler.pfpixelix.ui.composables.direct_messages.conversations.ConversationsComposable
import com.daniebeler.pfpixelix.ui.composables.edit_profile.EditProfileComposable
import com.daniebeler.pfpixelix.ui.composables.explore.ExploreComposable
import com.daniebeler.pfpixelix.ui.composables.followers.FollowersMainComposable
import com.daniebeler.pfpixelix.ui.composables.mention.MentionComposable
import com.daniebeler.pfpixelix.ui.composables.notifications.NotificationsComposable
import com.daniebeler.pfpixelix.ui.composables.post_editor.PostEditorComposable
import com.daniebeler.pfpixelix.ui.composables.post_editor.PostEditorViewModel
import com.daniebeler.pfpixelix.ui.composables.profile.other_profile.OtherProfileComposable
import com.daniebeler.pfpixelix.ui.composables.profile.own_profile.OwnProfileComposable
import com.daniebeler.pfpixelix.ui.composables.session.LoginComposable
import com.daniebeler.pfpixelix.ui.composables.settings.about_instance.AboutInstanceComposable
import com.daniebeler.pfpixelix.ui.composables.settings.about_pixelix.AboutPixelixComposable
import com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts.BlockedAccountsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts.BookmarkedPostsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.followed_hashtags.FollowedHashtagsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.icon_selection.IconSelectionComposable
import com.daniebeler.pfpixelix.ui.composables.settings.liked_posts.LikedPostsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts.MutedAccountsComposable
import com.daniebeler.pfpixelix.ui.composables.single_post.SinglePostComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline.HashtagTimelineComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.CameraTimelineComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.CategoryTimelineComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.FilmTimelineComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.LensTimelineComposable
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.toKmpUri
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data class Hashtag(val hashtag: String) : Destination

    @Serializable
    data class HashtagTimeline(val hashtag: String) : Destination

    @Serializable
    data class CameraTimeline(val camera: String) : Destination

    @Serializable
    data class CategoryTimeline(val category: String) : Destination

    @Serializable
    data class LensTimeline(val lens: String) : Destination

    @Serializable
    data class FilmTimeline(val film: String) : Destination




    @Serializable
    data class Post(
        val id: String, val refresh: Boolean = false, val openReplies: Boolean = false
    ) : Destination

    @Serializable
    data class EditPost(val id: String) : Destination

    @Serializable
    data class Collection(val id: String) : Destination

    @Serializable
    data class Followers(val userId: String, val username: String, val isFollowers: Boolean) :
        Destination

    @Serializable
    data object Conversations : Destination

    @Serializable
    data class Chat(val id: String) : Destination

    @Serializable
    data class Mention(val id: String) : Destination

    @Serializable
    data object EditProfile : Destination

    @Serializable
    data object IconSelection : Destination

    @Serializable
    data object MutedAccounts : Destination

    @Serializable
    data object BlockedAccounts : Destination

    @Serializable
    data object LikedPosts : Destination

    @Serializable
    data object BookmarkedPosts : Destination

    @Serializable
    data object FollowedHashtags : Destination

    @Serializable
    data object AboutInstance : Destination

    @Serializable
    data object AboutPixelix : Destination

    @Serializable
    data class Profile(val userId: String?, val username: String?) : Destination

    @Serializable
    data class ProfileByUsername(val userName: String) : Destination

    @Serializable
    data object FirstLogin : Destination

    @Serializable
    data object NewLogin : Destination

    @Serializable
    data class Search(val page: Int = 0) : Destination

    @Serializable
    data object OwnProfile : Destination

    @Serializable
    data object Feeds : Destination

    @Serializable
    data class NewPost(val uris: List<String> = emptyList()) : Destination

    @Serializable
    data object Notifications : Destination

    @Serializable
    data object HomeTabFeeds : Destination

    @Serializable
    data object HomeTabSearch : Destination

    @Serializable
    data object HomeTabNewPost : Destination

    @Serializable
    data object HomeTabNotifications : Destination

    @Serializable
    data object HomeTabOwnProfile : Destination
}

internal fun appEntryProvider(
    navigator: AppNavigator,
    openPreferencesDrawer: () -> Unit,
    exitApp: () -> Unit,
) = entryProvider<NavKey> {
    entry<Destination.FirstLogin> {
        Dialog(onDismissRequest = exitApp, properties = EdgeToEdgeDialogProperties()) {
            LoginComposable(navController = navigator)
        }
    }

    entry<Destination.NewPost> { args ->
        val imageUris: List<KmpUri>? = args.uris.map { it.toKmpUri() }
        PostEditorComposable(navigator, imageUris)
    }

    entry<Destination.NewLogin>(
        metadata = DialogSceneStrategy.dialog(EdgeToEdgeDialogProperties())
    ) {
        LoginComposable(true, navigator)
    }

    entry<Destination.HomeTabFeeds> {
        HomeComposable(navigator, openPreferencesDrawer)
    }

    entry<Destination.HomeTabSearch> {
        ExploreComposable(navigator, 0)
    }

    entry<Destination.HomeTabNewPost> {
        PostEditorComposable(navigator, emptyList())
    }

    entry<Destination.HomeTabNotifications>() {
        NotificationsComposable(navigator)
    }

    entry<Destination.HomeTabOwnProfile> {
        OwnProfileComposable(navigator, openPreferencesDrawer)
    }

    entry<Destination.Feeds> {
        HomeComposable(navigator, openPreferencesDrawer)
    }

    entry<Destination.Notifications> {
        NotificationsComposable(navigator)
    }

    entry<Destination.HashtagTimeline> { args ->
        HashtagTimelineComposable(navigator, args.hashtag)
    }

    entry<Destination.CameraTimeline> { args ->
        CameraTimelineComposable(navigator, args.camera)
    }

    entry<Destination.CategoryTimeline> { args ->
        CategoryTimelineComposable(navigator, args.category)
    }

    entry<Destination.FilmTimeline> { args ->
        FilmTimelineComposable(navigator, args.film)
    }

    entry<Destination.LensTimeline> { args ->
        LensTimelineComposable(navigator, args.lens)
    }

    entry<Destination.Profile> { args ->
        OtherProfileComposable(navigator, userId = args.userId, username = args.username)
    }

    entry<Destination.ProfileByUsername> { args ->
        OtherProfileComposable(navigator, userId = null, username = args.userName)
    }

    entry<Destination.Hashtag> { args ->
        HashtagTimelineComposable(navigator, args.hashtag)
    }

    entry<Destination.EditProfile> {
        EditProfileComposable(navigator)
    }

    entry<Destination.IconSelection> {
        IconSelectionComposable(navigator)
    }

    entry<Destination.EditPost> { args ->
        val viewModel: PostEditorViewModel =
            injectViewModel(key = "edit-post-${args.id}") { newPostViewModel }

        LaunchedEffect(args.id) {
            viewModel.initForEdit(args.id)
        }

        PostEditorComposable(
            navController = navigator, uris = null, viewModel = viewModel
        )
    }

    entry<Destination.MutedAccounts> {
        MutedAccountsComposable(navigator)
    }

    entry<Destination.BlockedAccounts> {
        BlockedAccountsComposable(navigator)
    }

    entry<Destination.LikedPosts> {
        LikedPostsComposable(navigator)
    }

    entry<Destination.BookmarkedPosts> {
        BookmarkedPostsComposable(navigator)
    }

    entry<Destination.FollowedHashtags> {
        FollowedHashtagsComposable(navigator)
    }

    entry<Destination.AboutInstance> {
        AboutInstanceComposable(navigator)
    }

    entry<Destination.AboutPixelix> {
        AboutPixelixComposable(navigator)
    }

    entry<Destination.OwnProfile> {
        OwnProfileComposable(navigator, openPreferencesDrawer)
    }

    entry<Destination.Followers> { args ->
        FollowersMainComposable(
            navigator,
            accountId = args.userId,
            username = args.username,
            isFollowers = args.isFollowers
        )
    }

    entry<Destination.Post> { args ->
        SinglePostComposable(navigator, postId = args.id, args.refresh, args.openReplies)
    }

    entry<Destination.Collection> { args ->
        CollectionComposable(navigator, collectionId = args.id)
    }

    entry<Destination.Search> { args ->
        ExploreComposable(navigator, args.page)
    }

    entry<Destination.Conversations> {
        ConversationsComposable(navController = navigator)
    }

    entry<Destination.Chat> { args ->
        ChatComposable(navController = navigator, accountId = args.id)
    }

    entry<Destination.Mention> { args ->
        MentionComposable(navController = navigator, mentionId = args.id)
    }
}
