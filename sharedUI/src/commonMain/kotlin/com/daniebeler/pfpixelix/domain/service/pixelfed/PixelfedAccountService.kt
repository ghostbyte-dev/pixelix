package com.daniebeler.pfpixelix.domain.service.pixelfed

import androidx.compose.ui.graphics.ImageBitmap
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.Relationship
import com.daniebeler.pfpixelix.domain.model.request.UpdateUserRequest
import com.daniebeler.pfpixelix.domain.model.request.UserBlockRequest
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.model.request.toPixelfed
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedAccountDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.utils.encodeToPngBytes
import com.daniebeler.pfpixelix.utils.executeAndParsePagination
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
@AppSingleton
class PixelfedAccountService(
    private val authService: AuthService,
    private val api: PixelfedApi,
) : AccountService {
    override val refreshSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getOwnAccount(): Flow<Resource<Account>> {
        val current =
            authService.getCurrentSession() ?: return flowOf(Resource.Error("No account found"))

        return refreshSignal
            .onStart { emit(Unit) }
            .flatMapLatest {
                getAccount(current.accountId, current.username).onEach { resource ->
                    if (resource is Resource.Success) {
                        authService.updateSessionAvatar(resource.data.id, resource.data.avatar)
                    }
                }
            }
    }

    override fun updateAccount(
        username: String,
        updateUserRequest: UpdateUserRequest
    ) = loadResource {

        val result = api.updateAccount(updateUserRequest.toPixelfed()).toDomain()
        //refreshSignal.emit(Unit)
        result
    }

    override fun updateAvatar(username: String, avatar: ImageBitmap?): Flow<Resource<Unit>> = loadResource {
        val bytes = withContext(Dispatchers.Default) {
            avatar?.encodeToPngBytes()
        }
        val body = MultiPartFormDataContent(formData {
            if (bytes != null) {
                try {
                    val fileName = "filename=avatar"
                    val fileType = "image/png"
                    append("avatar", bytes, Headers.Companion.build {
                        append(HttpHeaders.ContentType, fileType)
                        append(HttpHeaders.ContentDisposition, fileName)
                    })
                } catch (e: Exception) {
                    Logger.Companion.e("AccountService.updateAccount error", e)
                }
            }
        })
        api.updateAvatar(body).toDomain()
        refreshSignal.emit(Unit)
    }

    override fun updateHeader(
        username: String,
        header: ImageBitmap?
    ): Flow<Resource<Unit>> = flow {
       emit(Resource.Loading())
    }


    override fun getAccount(accountId: String, username: String) =
        loadResource { api.getAccount(accountId).toDomain() }

    override fun getAccountByUsername(username: String) =
        loadResource { api.getAccountByUsername(username).toDomain() }

    override fun getRelationships(userIds: List<String>) = loadListResources {
        api.getRelationships(userIds).map { it.toDomain() }
    }

    override fun getMutualFollowers(userId: String) =
        loadListResources { api.getMutalFollowers(userId).map { it.toDomain() } }

    override fun getAccountSettings() = loadResource { api.getSettings().toDomain() }
    override fun followAccount(accountId: String, username: String) =
        loadResource { api.followAccount(accountId).toDomain() }

    override fun unfollowAccount(accountId: String, username: String) =
        loadResource { api.unfollowAccount(accountId).toDomain() }

    override fun muteAccount(accountId: String, username: String, userMuteRequest: UserMuteRequest) =
        if (userMuteRequest.mute == true) {
            loadResource { api.muteAccount(accountId).toDomain() }
        } else {
            loadResource { api.unmuteAccount(accountId).toDomain() }
        }

    override fun blockAccount(accountId: String, username: String, userBlockRequest: UserBlockRequest) =
        loadResource { api.blockAccount(accountId).toDomain() }

    override fun unblockAccount(accountId: String, username: String) =
        loadResource { api.unblockAccount(accountId).toDomain() }

    override fun getMutedAccounts() =
        loadListResources { api.getMutedAccounts().map { it.toMutedAccount() } }

    override fun getBlockedAccounts() =
        loadListResources { api.getBlockedAccounts().map { it.toDomain() } }

    override fun getAccountsFollowers(accountId: String, username: String, cursor: String?) = flow {
        emit(Resource.Loading())

        try {
            val response = api.getAccountsFollowers(accountId, cursor)
                .executeAndParsePagination(
                    directionNext = false,
                    paginationName = "cursor",
                    transform = { dtoList -> dtoList.map { it.toDomain() } }
                )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun getAccountsFollowing(accountId: String, username: String, cursor: String?) = flow {
        emit(Resource.Loading())

        try {
            val response = api.getAccountsFollowing(accountId, cursor)
                .executeAndParsePagination(
                    directionNext = false,
                    paginationName = "cursor",
                    transform = { dtoList -> dtoList.map { it.toDomain() } }
                )

            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override fun acceptFollowRequest(accountId: String) = loadResource {
        api.approveFollowRequest(accountId).toDomain()
    }

    override fun rejectFollowRequest(accountId: String) = loadResource {
        api.denyFollowRequest(accountId).toDomain()
    }
}