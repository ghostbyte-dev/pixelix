package com.daniebeler.pfpixelix.domain.service.account

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.repository.PixelfedApi
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.session.AuthService
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.encodeToPngBytes
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
@AppSingleton
class AccountService(
    private val authService: AuthService,
    private val api: PixelfedApi,
) {
    private val refreshSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getOwnAccount(): Flow<Resource<Account>> {
        val current =
            authService.getCurrentSession() ?: return flowOf(Resource.Error("No account found"))

        return refreshSignal
            .onStart { emit(Unit) }
            .flatMapLatest {
                getAccount(current.accountId).onEach { resource ->
                    if (resource is Resource.Success) {
                        authService.updateSessionAvatar(resource.data.id, resource.data.avatar)
                    }
                }
            }
    }

    fun updateAccount(
        displayName: String,
        note: String,
        website: String,
        privateProfile: Boolean,
        avatar: ImageBitmap?
    ) = loadResource {
        val bytes = withContext(Dispatchers.Default) {
            avatar?.encodeToPngBytes()
        }
        val body = MultiPartFormDataContent(formData {
            if (bytes != null) {
                try {
                    val fileName = "filename=avatar"
                    val fileType = "image/png"
                    append("avatar", bytes, Headers.build {
                        append(HttpHeaders.ContentType, fileType)
                        append(HttpHeaders.ContentDisposition, fileName)
                    })
                } catch (e: Exception) {
                    Logger.e("AccountService.updateAccount error", e)
                }
            }

            append("display_name", displayName)
            append("note", note)
            append("website", website)
            append("locked", privateProfile.toString())
        })
        val result = api.updateAccount(body)
        refreshSignal.emit(Unit)
        result
    }

    fun getAccount(accountId: String) = loadResource { api.getAccount(accountId) }
    fun getAccountByUsername(username: String) = loadResource { api.getAccountByUsername(username) }
    fun getMutualFollowers(userId: String) = loadListResources { api.getMutalFollowers(userId) }
    fun getAccountSettings() = loadResource { api.getSettings() }
    fun followAccount(accountId: String) = loadResource { api.followAccount(accountId) }
    fun unfollowAccount(accountId: String) = loadResource { api.unfollowAccount(accountId) }
    fun muteAccount(accountId: String) = loadResource { api.muteAccount(accountId) }
    fun unMuteAccount(accountId: String) = loadResource { api.unmuteAccount(accountId) }
    fun blockAccount(accountId: String) = loadResource { api.blockAccount(accountId) }
    fun unblockAccount(accountId: String) = loadResource { api.unblockAccount(accountId) }
    fun getMutedAccounts() = loadListResources { api.getMutedAccounts() }
    fun getBlockedAccounts() = loadListResources { api.getBlockedAccounts() }
    fun getLikedBy(postId: String) = loadListResources { api.getAccountsWhoLikedPost(postId) }

    fun getAccountsFollowers(accountId: String, maxId: String? = null) = loadListResources {
        api.getAccountsFollowers(accountId, maxId)
    }

    fun getAccountsFollowing(accountId: String, maxId: String? = null) = loadListResources {
        api.getAccountsFollowing(accountId, maxId)
    }
}
