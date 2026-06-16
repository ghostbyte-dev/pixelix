package com.daniebeler.pfpixelix.domain.service.vernissage

import androidx.compose.ui.graphics.ImageBitmap
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Relationship
import com.daniebeler.pfpixelix.domain.model.Settings
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.PixelfedAccountDto
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadPaginatedListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.utils.loadVernissagePaginatedListResources
import com.daniebeler.pfpixelix.domain.service.vernissage.model.toDomain
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
import kotlin.collections.emptyList

@Inject
@AppSingleton
class VernissageAccountService(
    private val authService: AuthService,
    private val api: VernissageApi,
) : AccountService {
    override val refreshSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val emptyAccount = Account()
    val emptyRelationship = Relationship(
        id = "", following = false, followedBy = false, muting = false, blocking = false
    )

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
        //api.getAccountByUsername(username).toDomain()
        emptyAccount
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

    override fun followAccount(accountId: String) = loadResource {
        //api.followAccount(accountId).toDomain()
        emptyRelationship
    }

    override fun unfollowAccount(accountId: String) = loadResource {
        //api.unfollowAccount(accountId).toDomain()
        emptyRelationship
    }

    override fun muteAccount(accountId: String) = loadResource {
        //    api.muteAccount(accountId).toDomain()
        emptyRelationship
    }

    override fun unMuteAccount(accountId: String) = loadResource {
        //    api.unmuteAccount(accountId).toDomain()
        emptyRelationship
    }

    override fun blockAccount(accountId: String) = loadResource {
        //api.blockAccount(accountId).toDomain()
        emptyRelationship
    }

    override fun unblockAccount(accountId: String) = loadResource {
        //api.unblockAccount(accountId).toDomain()
        emptyRelationship
    }

    override fun getMutedAccounts() = loadListResources {
        //    api.getMutedAccounts().map { it.toDomain() }
        emptyList<Account>()
    }

    override fun getBlockedAccounts() = loadListResources {
        //    api.getBlockedAccounts().map { it.toDomain() }
        emptyList<Account>()
    }

    override fun getLikedBy(postId: String) = loadListResources {
        //    api.getAccountsWhoLikedPost(postId).map { it.toDomain() }
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