package com.daniebeler.pfpixelix.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.serialization.modules.subclass

internal val navigationSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destination::class, Destination.serializer())
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
