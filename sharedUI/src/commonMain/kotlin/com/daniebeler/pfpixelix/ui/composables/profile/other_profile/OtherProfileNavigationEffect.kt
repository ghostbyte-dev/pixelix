package com.daniebeler.pfpixelix.ui.composables.profile.other_profile

sealed interface OtherProfileNavigationEffect {
    data object OpenOwnProfile : OtherProfileNavigationEffect
}
