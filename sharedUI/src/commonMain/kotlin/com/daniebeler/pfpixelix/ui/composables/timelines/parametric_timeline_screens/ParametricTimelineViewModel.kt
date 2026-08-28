package com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.PostService
import com.daniebeler.pfpixelix.domain.service.general.TimelineService
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.widgets.PaginatedPostsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class ParametricTimelineViewModel @Inject constructor(
    private val postService: PostService,
    private val timelineService: TimelineService,
    private val exploreService: ExploreService,
    private val userPreferences: UserPreferences
) : PaginatedPostsViewModel(userPreferences) {

    private var param: String = ""
    private var fetchType: FetchType = FetchType.CAMERA
    var showHelp by mutableStateOf(false)

    enum class FetchType {
        CAMERA, CATEGORY, LENS, FILM, LIKED_POSTS, BOOKMARKED_POSTS, EDITORS_CHOICE_POSTS
    }

    fun init(fetchType: FetchType, param: String = "") {
        if (this.param != param || this.fetchType != fetchType) {
            if (this.fetchType != fetchType || this.param != param) {
                this.fetchType = fetchType
                this.param = param
                loadItems(refreshing = false)
            }
        }
        if (FetchType.EDITORS_CHOICE_POSTS == fetchType) {
            viewModelScope.launch {
                userPreferences.showEditorsChoicePostsHelpFlow.collect {
                    showHelp = it
                }
            }
        }
    }

    override fun fetchPage(maxId: String?): Flow<Resource<PaginatedResponse<Post>>> {
        return when (fetchType) {
            FetchType.CAMERA -> timelineService.getCameraTimeline(param, maxId)
            FetchType.CATEGORY -> timelineService.getCategoryTimeline(param, maxId)
            FetchType.LENS -> timelineService.getLensTimeline(param, maxId)
            FetchType.FILM -> timelineService.getFilmTimeline(param, maxId)
            FetchType.LIKED_POSTS -> postService.getLikedPosts(maxId)
            FetchType.BOOKMARKED_POSTS -> postService.getBookmarkedPosts(maxId)
            FetchType.EDITORS_CHOICE_POSTS -> exploreService.getEditorsChoicePosts(maxId)
        }
    }

    fun discardHelp() {
        if (fetchType == FetchType.EDITORS_CHOICE_POSTS) {
            viewModelScope.launch {
                userPreferences.showEditorsChoicePostsHelp = false
            }
        }
    }
}