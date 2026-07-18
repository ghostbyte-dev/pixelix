package com.daniebeler.pfpixelix.ui.composables.edit_profile

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.request.UpdateUserRequest
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.suggestions.HashtagMentionsSuggestionsManager
import com.daniebeler.pfpixelix.utils.EmptyKmpUri
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.toKmpUri
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class EditProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    val hashtagMentionsSuggestionsManager: HashtagMentionsSuggestionsManager,
    session: Session
) : ViewModel() {
    val capabilities = session.capabilities
    var accountState by mutableStateOf(EditProfileState())
    var avatarState by mutableStateOf(EditImageState())

    var headerState by mutableStateOf(EditImageState())
    var firstLoaded by mutableStateOf(false)
    var displayName by mutableStateOf(TextFieldValue())
    var note by mutableStateOf(TextFieldValue())
    var website by mutableStateOf(TextFieldValue())
    var avatarUri by mutableStateOf<KmpUri?>(null)
    var headerUri by mutableStateOf<KmpUri?>(null)
    var privateProfile by mutableStateOf(false)
    var manuallyAcceptNewFollowers by mutableStateOf<Boolean?>(null)
    var includePublicPostsInSearchEngine by mutableStateOf<Boolean?>(null)
    var includeProfileInSearchEngine by mutableStateOf<Boolean?>(null)


    val isEdited: Boolean by derivedStateOf {
        if (accountState.account == null) return@derivedStateOf false
        !(displayName.text == (accountState.account?.displayname
            ?: "") && note.text == (accountState.account?.note
            ?: "") && ("https://${website.text}" == (accountState.account?.website
            ?: "") || (accountState.account?.website.isNullOrEmpty() && website.text.isEmpty())) && avatarState.newImage == null && headerState.newImage == null && privateProfile == accountState.account?.locked && manuallyAcceptNewFollowers == accountState.account?.manuallyApprovesFollowers && includeProfileInSearchEngine == accountState.account?.includeProfilePageInSearchEngines && includePublicPostsInSearchEngine == accountState.account?.includePublicPostsInSearchEngines)
    }

    init {
        getAccount()
    }

    fun getAccount() {
        accountState = EditProfileState(isLoading = true)
        accountService.getOwnAccount().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    accountState = EditProfileState(account = result.data)
                    displayName = TextFieldValue(accountState.account?.displayname ?: "")
                    note = TextFieldValue(accountState.account?.note ?: "")
                    website =
                        TextFieldValue(accountState.account?.website?.replace("https://", "") ?: "")
                    avatarUri = accountState.account?.avatar?.toKmpUri()
                    headerUri = accountState.account?.headerUrl?.toKmpUri()
                    privateProfile = accountState.account?.locked ?: false
                    manuallyAcceptNewFollowers =
                        accountState.account?.manuallyApprovesFollowers
                    includeProfileInSearchEngine =
                        accountState.account?.includeProfilePageInSearchEngines
                    includePublicPostsInSearchEngine =
                        accountState.account?.includePublicPostsInSearchEngines
                    firstLoaded = true
                }

                is Resource.Error -> {
                    accountState = EditProfileState(error = result.message)
                }

                is Resource.Loading -> {
                    accountState =
                        EditProfileState(isLoading = true, account = accountState.account)
                }
            }
        }.launchIn(viewModelScope)
    }


    fun updateHeader(header: ImageBitmap) {
        accountService.updateHeader(
            username = accountState.account!!.username, header
        ).onEach { result ->
            headerState = when (result) {
                is Resource.Success -> {
                    headerState.copy(
                        isLoading = false, newImage = null, newUploadedImage = header
                    )
                }

                is Resource.Error -> {
                    headerState.copy(isLoading = false, error = result.message)
                }

                is Resource.Loading -> {
                    headerState.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateAvatar(avatar: ImageBitmap) {
        accountService.updateAvatar(
            username = accountState.account!!.username, avatar
        ).onEach { result ->
            avatarState = when (result) {
                is Resource.Success -> {
                    avatarState.copy(
                        isLoading = false, newImage = null, newUploadedImage = avatar
                    )
                }

                is Resource.Error -> {
                    avatarState.copy(isLoading = false, error = result.message)
                }

                is Resource.Loading -> {
                    avatarState.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun save() {
        if (accountState.account == null) {
            accountState = EditProfileState(error = "No user found")
            return
        }
        if (avatarState.newImage != null) {
            updateAvatar(avatarState.newImage!!)
        }
        if (headerState.newImage != null) {
            updateHeader(headerState.newImage!!)
        }
        val updateUserRequest = UpdateUserRequest(
            displayName = displayName.text,
            note = note.text,
            website = "https://${website.text}",
            locked = privateProfile,
            manuallyAcceptNewFollowers = manuallyAcceptNewFollowers,
            includeProfilePageInSearchEngines = includeProfileInSearchEngine,
            includePublicPostsInSearchEngines = includePublicPostsInSearchEngine
        )

        accountService.updateAccount(
            username = accountState.account!!.username, updateUserRequest = updateUserRequest
        ).onEach { result ->
            accountState = when (result) {
                is Resource.Success -> {
                    accountState.copy(account = result.data, isLoading = false)
                }

                is Resource.Error -> {
                    accountState.copy(error = result.message, isLoading = false)
                }

                is Resource.Loading -> {
                    accountState.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateNote(newNote: TextFieldValue) {
        note = newNote
        hashtagMentionsSuggestionsManager.changeText(newNote, viewModelScope)
    }
}