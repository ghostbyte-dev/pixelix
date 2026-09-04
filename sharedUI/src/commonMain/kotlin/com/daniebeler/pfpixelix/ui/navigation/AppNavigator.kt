package com.daniebeler.pfpixelix.ui.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AppNavigator internal constructor(
    private val state: AppNavigationState,
    private val exitApp: () -> Unit,
) {
    private val _reselectEvents = MutableSharedFlow<Destination>(extraBufferCapacity = 1)
    val reselectEvents = _reselectEvents.asSharedFlow()
    val graph = NavigationGraph()

    fun navigate(
        destination: Destination,
        options: NavigationOptions.() -> Unit = {},
    ) {
        val navigationOptions = NavigationOptions().apply(options)
        if (destination in state.backStacks) {
            state.currentTopLevel = destination
            if (!navigationOptions.restoreState) {
                state.currentBackStack.apply {
                    clear()
                    add(destination)
                }
            }
        } else if (!navigationOptions.launchSingleTop || state.currentDestination != destination) {
            state.currentBackStack.add(destination)
        }
    }

    fun navigateTopLevel(destination: Destination) {
        require(destination in state.backStacks) { "$destination is not a top-level destination" }
        if (destination == state.currentTopLevel) {
            _reselectEvents.tryEmit(destination)
        } else {
            state.currentTopLevel = destination
        }
    }

    fun replaceTop(destination: Destination) {
        state.currentBackStack.removeLastOrNull()
        state.currentBackStack.add(destination)
    }

    fun popBackStack(): Boolean {
        if (state.currentBackStack.size > 1) {
            state.currentBackStack.removeLastOrNull()
            return true
        }
        if (state.currentTopLevel != state.startDestination &&
            state.startDestination in state.backStacks
        ) {
            state.currentTopLevel = state.startDestination
            return true
        }
        exitApp()
        return false
    }

    fun clearAndNavigate(destination: Destination) {
        if (destination in state.backStacks) {
            state.currentTopLevel = destination
            state.currentBackStack.apply {
                clear()
                add(destination)
            }
        } else {
            state.currentBackStack.apply {
                clear()
                add(destination)
            }
        }
    }

    fun navigateUp(): Boolean = popBackStack()

    fun <T : Destination> clearBackStack(destination: T): Boolean {
        val stack = state.backStacks[destination] ?: return false
        stack.apply {
            clear()
            add(destination)
        }
        return true
    }

    fun resetForSession(startDestination: Destination) {
        state.backStacks.forEach { (root, stack) ->
            stack.clear()
            stack.add(root)
        }
        if (startDestination in state.backStacks) {
            state.currentTopLevel = startDestination
        }
    }
}

class NavigationGraph {
    val id: Int = 0
    val startDestinationId: Int = 0
    fun findStartDestination(): NavigationGraph = this
}

class NavigationOptions {
    var launchSingleTop: Boolean = false
    var restoreState: Boolean = false

    fun popUpTo(@Suppress("UNUSED_PARAMETER") route: Any, options: PopUpToOptions.() -> Unit = {}) {
        PopUpToOptions().apply(options)
    }
}

class PopUpToOptions {
    var inclusive: Boolean = false
    var saveState: Boolean = false
}
