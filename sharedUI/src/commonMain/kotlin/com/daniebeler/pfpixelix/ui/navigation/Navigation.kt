package com.daniebeler.pfpixelix.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
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
import com.daniebeler.pfpixelix.ui.composables.post_editor.NewPostComposable
import com.daniebeler.pfpixelix.ui.composables.notifications.NotificationsComposable
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
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.toKmpUri
import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable data class Hashtag(val hashtag: String) : Destination
    @Serializable data class HashtagTimeline(val hashtag: String) : Destination
    @Serializable data class Post(
        val id: String,
        val refresh: Boolean = false,
        val openReplies: Boolean = false
    ) : Destination
    @Serializable data class EditPost(val id: String) : Destination
    @Serializable data class Collection(val id: String) : Destination
    @Serializable data class Followers(val userId: String, val username: String, val isFollowers: Boolean) : Destination
    @Serializable data object Conversations : Destination
    @Serializable data class Chat(val id: String) : Destination
    @Serializable data class Mention(val id: String) : Destination
    @Serializable data object EditProfile : Destination
    @Serializable data object IconSelection : Destination
    @Serializable data object MutedAccounts : Destination
    @Serializable data object BlockedAccounts : Destination
    @Serializable data object LikedPosts : Destination
    @Serializable data object BookmarkedPosts : Destination
    @Serializable data object FollowedHashtags : Destination
    @Serializable data object AboutInstance : Destination
    @Serializable data object AboutPixelix : Destination
    @Serializable data class Profile(val userId: String?, val username: String?) : Destination
    @Serializable data class ProfileByUsername(val userName: String) : Destination
    @Serializable data object FirstLogin : Destination
    @Serializable data object NewLogin : Destination
    @Serializable data class Search(val page: Int = 0) : Destination
    @Serializable data object OwnProfile : Destination
    @Serializable data object Feeds : Destination
    @Serializable data class NewPost(val uris: List<String> = emptyList()) : Destination
    @Serializable data object Notifications : Destination

    @Serializable data object HomeTabFeeds : Destination
    @Serializable data object HomeTabSearch : Destination
    @Serializable data object HomeTabNewPost : Destination
    @Serializable data object HomeTabNotifications : Destination
    @Serializable data object HomeTabOwnProfile : Destination
}

@OptIn(ExperimentalMaterial3Api::class)
internal fun NavGraphBuilder.appGraph(
    navController: NavHostController,
    openPreferencesDrawer: () -> Unit,
    exitApp: () -> Unit,
) {
    composable<Destination.FirstLogin>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        Dialog(
            onDismissRequest = exitApp,
            properties = EdgeToEdgeDialogProperties()
        ) {
            LoginComposable(navController = navController)
        }
    }

    //home tabs (with no transition animations)
    //more info: https://issuetracker.google.com/408010634
    navigation<Destination.HomeTabFeeds>(
        startDestination = Destination.Feeds,
        enterTransition = { tabEnterTransition<Destination.HomeTabFeeds>() },
        exitTransition = { tabExitTransition<Destination.HomeTabFeeds>() }
    ) {
        tabGraph(navController, openPreferencesDrawer)
    }

    navigation<Destination.HomeTabSearch>(
        startDestination = Destination.Search(),
        enterTransition = { tabEnterTransition<Destination.HomeTabSearch>() },
        exitTransition = { tabExitTransition<Destination.HomeTabSearch>() }
    ) {
        tabGraph(navController, openPreferencesDrawer)
    }

    navigation<Destination.HomeTabNewPost>(
        startDestination = Destination.NewPost(),
        enterTransition = { tabEnterTransition<Destination.HomeTabNewPost>() },
        exitTransition = { tabExitTransition<Destination.HomeTabNewPost>() }
    ) {

        composable<Destination.NewPost> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<Destination.NewPost>()
            val imageUris: List<KmpUri>? = args.uris.map { it.toKmpUri() }
            NewPostComposable(navController, imageUris)
        }

        tabGraph(navController, openPreferencesDrawer)
    }

    navigation<Destination.HomeTabNotifications>(
        startDestination = Destination.Notifications,
        enterTransition = { tabEnterTransition<Destination.HomeTabNotifications>() },
        exitTransition = { tabExitTransition<Destination.HomeTabNotifications>() }
    ) {
        tabGraph(navController, openPreferencesDrawer)
    }

    navigation<Destination.HomeTabOwnProfile>(
        startDestination = Destination.OwnProfile,
        enterTransition = { tabEnterTransition<Destination.HomeTabOwnProfile>() },
        exitTransition = { tabExitTransition<Destination.HomeTabOwnProfile>() }
    ) {
        tabGraph(navController, openPreferencesDrawer)
    }
}

private inline fun <reified T: Any> AnimatedContentTransitionScope<NavBackStackEntry>.tabEnterTransition(): EnterTransition? {
    val initialHierarchy = initialState.destination.hierarchy
    return if (initialHierarchy.none { it.hasRoute<T>() }) EnterTransition.None else null
}

private inline fun <reified T: Any> AnimatedContentTransitionScope<NavBackStackEntry>.tabExitTransition(): ExitTransition? {
    val targetHierarchy = targetState.destination.hierarchy
    return if (targetHierarchy.none { it.hasRoute<T>() }) ExitTransition.None else null
}

private fun NavGraphBuilder.tabGraph(
    navController: NavHostController,
    openPreferencesDrawer: () -> Unit
) {
    dialog<Destination.NewLogin>(
        dialogProperties = EdgeToEdgeDialogProperties()
    ) {
        LoginComposable(true, navController)
    }

    composable<Destination.Feeds> {
        HomeComposable(navController, openPreferencesDrawer)
    }

    composable<Destination.Notifications> {
        NotificationsComposable(navController)
    }

    composable<Destination.HashtagTimeline> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.HashtagTimeline>()
        HashtagTimelineComposable(navController, args.hashtag)
    }

    composable<Destination.Profile> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Profile>()
        OtherProfileComposable(navController, userId = args.userId, username = args.username)
    }

    composable<Destination.ProfileByUsername> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.ProfileByUsername>()
        OtherProfileComposable(navController, userId = null, username = args.userName)
    }

    composable<Destination.Hashtag> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Hashtag>()
        HashtagTimelineComposable(navController, args.hashtag)
    }

    composable<Destination.EditProfile> {
        EditProfileComposable(navController)
    }

    composable<Destination.IconSelection> {
        IconSelectionComposable(navController)
    }

    composable<Destination.EditPost> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.EditPost>()

        // Inject a unique ViewModel instance just for editing this specific post
        val viewModel: PostEditorViewModel = injectViewModel(key = "edit-post-${args.id}") { newPostViewModel }

        // Initialize the ViewModel for edit mode
        LaunchedEffect(args.id) {
            viewModel.initForEdit(args.id)
        }

        // Reuse your NewPostComposable!
        NewPostComposable(
            navController = navController,
            uris = null,
            viewModel = viewModel
        )
    }

    composable<Destination.MutedAccounts> {
        MutedAccountsComposable(navController)
    }

    composable<Destination.BlockedAccounts> {
        BlockedAccountsComposable(navController)
    }

    composable<Destination.LikedPosts> {
        LikedPostsComposable(navController)
    }

    composable<Destination.BookmarkedPosts> {
        BookmarkedPostsComposable(navController)
    }

    composable<Destination.FollowedHashtags> {
        FollowedHashtagsComposable(navController)
    }

    composable<Destination.AboutInstance> {
        AboutInstanceComposable(navController)
    }

    composable<Destination.AboutPixelix> {
        AboutPixelixComposable(navController)
    }

    composable<Destination.OwnProfile> {
        OwnProfileComposable(navController, openPreferencesDrawer)
    }

    composable<Destination.Followers> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Followers>()
        FollowersMainComposable(
            navController,
            accountId = args.userId,
            username = args.username,
            isFollowers = args.isFollowers
        )
    }

    composable<Destination.Post> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Post>()
        SinglePostComposable(navController, postId = args.id, args.refresh, args.openReplies)
    }

    composable<Destination.Collection> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Collection>()
        CollectionComposable(navController, collectionId = args.id)
    }

    composable<Destination.Search> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Search>()
        ExploreComposable(navController, args.page)
    }

    composable<Destination.Conversations> {
        ConversationsComposable(navController = navController)
    }

    composable<Destination.Chat> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Chat>()
        ChatComposable(navController = navController, accountId = args.id)
    }

    composable<Destination.Mention> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Mention>()
        MentionComposable(navController = navController, mentionId = args.id)
    }
}