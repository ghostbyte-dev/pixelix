package com.daniebeler.pfpixelix.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

internal val navigationSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destination.Hashtag::class, Destination.Hashtag.serializer())
            subclass(Destination.HashtagTimeline::class, Destination.HashtagTimeline.serializer())
            subclass(Destination.CameraTimeline::class, Destination.CameraTimeline.serializer())
            subclass(Destination.CategoryTimeline::class, Destination.CategoryTimeline.serializer())
            subclass(Destination.LensTimeline::class, Destination.LensTimeline.serializer())
            subclass(Destination.FilmTimeline::class, Destination.FilmTimeline.serializer())
            subclass(Destination.Post::class, Destination.Post.serializer())
            subclass(Destination.EditPost::class, Destination.EditPost.serializer())
            subclass(Destination.Collection::class, Destination.Collection.serializer())
            subclass(Destination.Followers::class, Destination.Followers.serializer())
            subclass(Destination.Conversations::class, Destination.Conversations.serializer())
            subclass(Destination.Chat::class, Destination.Chat.serializer())
            subclass(Destination.Mention::class, Destination.Mention.serializer())
            subclass(Destination.EditProfile::class, Destination.EditProfile.serializer())
            subclass(Destination.IconSelection::class, Destination.IconSelection.serializer())
            subclass(Destination.MutedAccounts::class, Destination.MutedAccounts.serializer())
            subclass(Destination.BlockedAccounts::class, Destination.BlockedAccounts.serializer())
            subclass(Destination.LikedPosts::class, Destination.LikedPosts.serializer())
            subclass(Destination.BookmarkedPosts::class, Destination.BookmarkedPosts.serializer())
            subclass(Destination.FollowedHashtags::class, Destination.FollowedHashtags.serializer())
            subclass(Destination.AboutInstance::class, Destination.AboutInstance.serializer())
            subclass(Destination.AboutPixelix::class, Destination.AboutPixelix.serializer())
            subclass(Destination.Profile::class, Destination.Profile.serializer())
            subclass(Destination.ProfileByUsername::class, Destination.ProfileByUsername.serializer())
            subclass(Destination.FirstLogin::class, Destination.FirstLogin.serializer())
            subclass(Destination.NewLogin::class, Destination.NewLogin.serializer())
            subclass(Destination.Search::class, Destination.Search.serializer())
            subclass(Destination.OwnProfile::class, Destination.OwnProfile.serializer())
            subclass(Destination.Feeds::class, Destination.Feeds.serializer())
            subclass(Destination.NewPost::class, Destination.NewPost.serializer())
            subclass(Destination.Notifications::class, Destination.Notifications.serializer())
            subclass(Destination.HomeTabFeeds::class, Destination.HomeTabFeeds.serializer())
            subclass(Destination.HomeTabSearch::class, Destination.HomeTabSearch.serializer())
            subclass(Destination.HomeTabNewPost::class, Destination.HomeTabNewPost.serializer())
            subclass(Destination.HomeTabNotifications::class, Destination.HomeTabNotifications.serializer())
            subclass(Destination.HomeTabOwnProfile::class, Destination.HomeTabOwnProfile.serializer())
            subclass(Destination.NotificationSettings::class, Destination.NotificationSettings.serializer())
        }
    }
}

internal val topLevelDestinations: Set<Destination> = setOf(
    Destination.HomeTabFeeds,
    Destination.HomeTabSearch,
    Destination.HomeTabNewPost,
    Destination.HomeTabNotifications,
    Destination.HomeTabOwnProfile,
)

@Composable
internal fun rememberAppNavigationState(
    startDestination: Destination,
): AppNavigationState {
    val currentTopLevel = remember(startDestination) {
        mutableStateOf(startDestination)
    }

    val stacks = topLevelDestinations.associateWith { destination ->
        rememberNavBackStack(navigationSavedStateConfiguration, destination)
    }.toMutableMap()

    if (startDestination !in stacks) {
        stacks[startDestination] = rememberNavBackStack(
            navigationSavedStateConfiguration,
            startDestination,
        )
    }

    return remember(startDestination) {
        AppNavigationState(
            startDestination = startDestination,
            currentTopLevelState = currentTopLevel,
            backStacks = stacks,
        )
    }
}

internal class AppNavigationState(
    val startDestination: Destination,
    private val currentTopLevelState: MutableState<Destination>,
    val backStacks: Map<Destination, NavBackStack<NavKey>>,
) {
    var currentTopLevel: Destination
        get() = currentTopLevelState.value
        set(value) {
            currentTopLevelState.value = value
        }

    val currentBackStack: NavBackStack<NavKey>
        get() = checkNotNull(backStacks[currentTopLevel]) {
            "No back stack registered for $currentTopLevel"
        }

    val currentDestination: Destination
        get() = currentBackStack.last() as Destination

    @Composable
    fun decoratedEntries(
        entryProvider: (NavKey) -> NavEntry<NavKey>,
    ): List<NavEntry<NavKey>> {
        val entriesByStack = backStacks.mapValues { (_, stack) ->
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
                    rememberViewModelStoreNavEntryDecorator<NavKey>(),
                ),
                entryProvider = entryProvider,
            )
        }

        val stacksInUse = if (
            currentTopLevel == startDestination || startDestination !in topLevelDestinations
        ) {
            listOf(currentTopLevel)
        } else {
            listOf(startDestination, currentTopLevel)
        }
        return stacksInUse.flatMap { entriesByStack[it].orEmpty() }
    }
}
