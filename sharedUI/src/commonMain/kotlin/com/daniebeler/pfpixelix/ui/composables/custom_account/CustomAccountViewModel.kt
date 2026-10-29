package com.daniebeler.pfpixelix.ui.composables.custom_account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.profile.RelationshipState
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class CustomAccountViewModel(
    private val accountService: AccountService
) : ViewModel() {

    var relationshipState by mutableStateOf(RelationshipState())
    var gotUpdatedRelationship by mutableStateOf(false)

    fun followAccount(userId: String, username: String) {
        accountService.followAccount(userId, username).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    gotUpdatedRelationship = true
                    RelationshipState(accountRelationship = result.data)
                }

                is Resource.Error -> {
                    gotUpdatedRelationship = true
                    RelationshipState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    RelationshipState(
                        isLoading = true,
                        accountRelationship = relationshipState.accountRelationship
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun unfollowAccount(userId: String, username: String) {
        accountService.unfollowAccount(userId, username).onEach { result ->
            relationshipState = when (result) {
                is Resource.Success -> {
                    gotUpdatedRelationship = true
                    RelationshipState(accountRelationship = result.data)
                }

                is Resource.Error -> {
                    gotUpdatedRelationship = true
                    RelationshipState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    RelationshipState(
                        isLoading = true,
                        accountRelationship = relationshipState.accountRelationship
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}