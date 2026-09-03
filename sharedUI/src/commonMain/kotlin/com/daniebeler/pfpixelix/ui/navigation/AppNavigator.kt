package com.daniebeler.pfpixelix.ui.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class AppNavigator(
    private val state: AppNavigationState,
    private val exitApp: () -> Unit,
) {
    private val _reselectEvents = MutableSharedFlow<Destination>(extraBufferCapacity = 1)
    val reselectEvents = _reselectEvents.asSharedFlow()

    fun navigate(destination: Destination) {
        if (destination in state.backStacks) {
            state.currentTopLevel = destination
        } else {
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
