package com.daniebeler.pfpixelix.ui.composables.custom_account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.account.AccountService
import com.daniebeler.pfpixelix.ui.composables.profile.RelationshipState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class CustomAccountViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    var relationshipState by mutableStateOf(RelationshipState())
    var gotUpdatedRelationship by mutableStateOf(false)

    fun followAccount(userId: String) {
        accountService.followAccount(userId).onEach { result ->
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

    fun unfollowAccount(userId: String) {
        accountService.unfollowAccount(userId).onEach { result ->
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