package com.daniebeler.pfpixelix.ui.composables.edit_profile

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.account.AccountService
import com.daniebeler.pfpixelix.domain.service.suggestions.HashtagMentionsSuggestionsManager
import com.daniebeler.pfpixelix.utils.EmptyKmpUri
import com.daniebeler.pfpixelix.utils.toKmpUri
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class EditProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    val hashtagMentionsSuggestionsManager: HashtagMentionsSuggestionsManager
) : ViewModel() {

    var accountState by mutableStateOf(EditProfileState())

    var firstLoaded by mutableStateOf(false)
    var displayName by mutableStateOf(TextFieldValue())
    var note by mutableStateOf(TextFieldValue())
    var website by mutableStateOf("")
    var avatarUri by mutableStateOf(EmptyKmpUri)
    var newAvatar by mutableStateOf<ImageBitmap?>(null)
    var privateProfile by mutableStateOf(false)

    val isEdited: Boolean by derivedStateOf {
        if (accountState.account == null) return@derivedStateOf false
        !(displayName.text == (accountState.account?.displayname
            ?: "") && note.text == (accountState.account?.note
            ?: "") && "https://$website" == (accountState.account?.website
            ?: "") && newAvatar == null && privateProfile == accountState.account?.locked)
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
                    website = accountState.account?.website?.replace("https://", "") ?: ""
                    avatarUri = accountState.account?.avatar!!.toKmpUri()
                    privateProfile = accountState.account?.locked ?: false
                    firstLoaded = true
                }

                is Resource.Error -> {
                    accountState =
                        EditProfileState(error = result.message)
                }

                is Resource.Loading -> {
                    accountState =
                        EditProfileState(isLoading = true, account = accountState.account)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun save() {
        accountService.updateAccount(
            displayName.text, note.text, "https://$website", privateProfile, newAvatar
        ).onEach { result ->
            accountState = when (result) {
                is Resource.Success -> {
                    EditProfileState(account = result.data)
                }

                is Resource.Error -> {
                    EditProfileState(error = result.message)
                }

                is Resource.Loading -> {
                    EditProfileState(isLoading = true, account = accountState.account)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateNote(newNote: TextFieldValue) {
        note = newNote
        hashtagMentionsSuggestionsManager.changeText(newNote, viewModelScope)
    }
}