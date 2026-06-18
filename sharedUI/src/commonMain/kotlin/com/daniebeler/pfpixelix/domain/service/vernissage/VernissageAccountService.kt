package com.daniebeler.pfpixelix.domain.service.vernissage

import androidx.compose.ui.graphics.ImageBitmap
import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.Relationship
import com.daniebeler.pfpixelix.domain.model.Settings
import com.daniebeler.pfpixelix.domain.model.request.UserBlockRequest
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.model.request.toVernissage
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.utils.loadVernissagePaginatedListResources
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import me.tatarka.inject.annotations.Inject
import kotlin.collections.emptyList

@Inject
@AppSingleton
class VernissageAccountService(
    private val authService: AuthService,
    private val api: VernissageApi,
) : AccountService {
    override val refreshSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val emptyAccount = Account()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getOwnAccount(): Flow<Resource<Account>> {
        val current =
            authService.getCurrentSession() ?: return flowOf(Resource.Error("No account found"))

        return refreshSignal.onStart { emit(Unit) }.flatMapLatest {
                getAccount(current.accountId,current.username).onEach { resource ->
                    if (resource is Resource.Success) {
                        authService.updateSessionAvatar(resource.data.id, resource.data.avatar)
                    }
                }
            }
    }

    override fun updateAccount(
        displayName: String,
        note: String,
        website: String,
        privateProfile: Boolean,
        avatar: ImageBitmap?
    ) = loadResource {

        /* val bytes = withContext(Dispatchers.Default) {
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

             append("display_name", displayName)
             append("note", note)
             append("website", website)
             append("locked", privateProfile.toString())
         })
         val result = api.updateAccount(body).toDomain()
         refreshSignal.emit(Unit)
         result*/
        emptyAccount
    }

    override fun getAccount(accountId: String, username: String) = loadResource {
        api.getUser(username).toDomain()
    }

    override fun getAccountByUsername(username: String) = loadResource {
        api.getUser(username).toDomain()
    }

    override fun getRelationships(userIds: List<String>) = loadListResources {
        api.getRelationships(userIds).map { it.toDomain() }
    }

    override fun getMutualFollowers(userId: String) = loadListResources {
        //    api.getMutalFollowers(userId).map { it.toDomain() }
        emptyList<Account>()
    }

    override fun getAccountSettings() = loadResource {
        //    api.getSettings().toDomain()
        Settings(
            enableReblogs = false, hideCollections = false, hideGroups = false, hideStories = false
        )
    }

    override fun followAccount(accountId: String, username: String) = loadResource {
        api.followUser(username).toDomain()
    }

    override fun unfollowAccount(accountId: String, username: String) = loadResource {
        api.unfollowUser(username).toDomain()
    }

    override fun muteAccount(accountId: String, username: String, userMuteRequest: UserMuteRequest) = loadResource {
        api.muteUser(username, userMuteRequest.toVernissage()).toDomain()
    }

    override fun unMuteAccount(accountId: String, username: String) = loadResource {
        api.unmuteUser(username).toDomain()
    }

    override fun blockAccount(accountId: String, username: String, userBlockRequest: UserBlockRequest) = loadResource {
        api.blockUser(username, userBlockRequest.toVernissage()).toDomain()
    }

    override fun unblockAccount(accountId: String, username: String) = loadResource {
        api.unblockUser(username).toDomain()
    }

    override fun getMutedAccounts() = loadListResources {
        //    api.getMutedAccounts().map { it.toDomain() }
        //TODO: check if there is endpoint, maybe possible to first fetch relationships and then get those accounts which are muted, but expensive
        emptyList<Account>()
    }

    override fun getBlockedAccounts() = loadListResources {
        //TODO: same as getMutedAccounts()
        //    api.getBlockedAccounts().map { it.toDomain() }
        emptyList<Account>()
    }

    override fun getAccountsFollowers(accountId: String, username: String, cursor: String?) =
        loadVernissagePaginatedListResources {
            api.getAccountsFollowers(username, cursor)
        }

    override fun getAccountsFollowing(accountId: String, username: String, cursor: String?) =
        loadVernissagePaginatedListResources {
            api.getAccountsFollowing(username, cursor)
        }
}