package com.daniebeler.pfpixelix.ui.composables.places

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.PostsForPlace
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


data class Coordinates(val latitude: Double, val longitude: Double)
data class PostsForPlaceState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val data: PostsForPlace? = null,
    val error: String = "",
) {
    val place get() = data?.place

    val coordinates: Coordinates?
        get() {
            val lat = place?.latitude ?: return null
            val lon = place?.longitude ?: return null
            return Coordinates(lat, lon)
        }}

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class PlaceViewModel(
    private val exploreService: ExploreService,
    private val prefs: UserPreferences,
) : ViewModel() {

    var view by mutableStateOf(ViewEnum.Grid)

    init {
        viewModelScope.launch {
            prefs.showUserGridTimelineFlow.collect { res ->
                view = ViewEnum.getView(res)
            }
        }
    }

    var postsForPlaceState by mutableStateOf(PostsForPlaceState())

    fun loadData(
        placeId: String, placeSlug: String, refreshing: Boolean
    ) {
        getPostsForPlace(placeId, placeSlug)
    }

    fun getPostsForPlace(id: String, slug: String) {
        exploreService.getPostsForPlace(id = id, slug = slug).onEach { result ->
            postsForPlaceState = when (result) {
                is Resource.Success -> {
                    PostsForPlaceState(
                        data = result.data
                    )
                }

                is Resource.Error -> {
                    PostsForPlaceState(error = result.message)
                }

                is Resource.Loading -> {
                    PostsForPlaceState(
                        isLoading = true,
                        data = postsForPlaceState.data
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun changeView(newView: ViewEnum) {
        view = newView
        prefs.showUserGridTimeline = newView.ordinal
    }
}