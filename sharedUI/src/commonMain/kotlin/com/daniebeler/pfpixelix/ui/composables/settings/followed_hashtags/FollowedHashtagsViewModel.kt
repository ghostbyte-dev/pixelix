package com.daniebeler.pfpixelix.ui.composables.settings.followed_hashtags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.Tag
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class FollowedHashtagsViewModel(
    private val exploreService: ExploreService
) : ViewModel() {

    var followedHashtagsState by mutableStateOf(FollowedHashtagsState())

    init {
        getFollowedHashtags()
    }

    fun getFollowedHashtags(refreshing: Boolean = false) {
        exploreService.getFollowedHashtags().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    followedHashtagsState = FollowedHashtagsState(followedHashtags = result.data)
                }

                is Resource.Error -> {
                    followedHashtagsState = FollowedHashtagsState(
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    followedHashtagsState = FollowedHashtagsState(
                        isLoading = true,
                        isRefreshing = refreshing,
                        followedHashtags = followedHashtagsState.followedHashtags
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}