package com.daniebeler.pfpixelix.domain.service.general

import androidx.compose.ui.graphics.ImageBitmap
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Relationship
import com.daniebeler.pfpixelix.domain.model.Settings
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedAccountService
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedAuthService
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

interface AccountService {
    val refreshSignal: MutableSharedFlow<Unit>
    fun getOwnAccount(): Flow<Resource<Account>>

    fun updateAccount(
        displayName: String,
        note: String,
        website: String,
        privateProfile: Boolean,
        avatar: ImageBitmap?
    ): Flow<Resource<Account>>

    fun getAccount(accountId: String): Flow<Resource<Account>>
    fun getAccountByUsername(username: String): Flow<Resource<Account>>
    fun getMutualFollowers(userId: String): Flow<Resource<List<Account>>>
    fun getAccountSettings(): Flow<Resource<Settings>>
    fun followAccount(accountId: String): Flow<Resource<Relationship>>
    fun unfollowAccount(accountId: String): Flow<Resource<Relationship>>
    fun muteAccount(accountId: String): Flow<Resource<Relationship>>
    fun unMuteAccount(accountId: String): Flow<Resource<Relationship>>
    fun blockAccount(accountId: String): Flow<Resource<Relationship>>
    fun unblockAccount(accountId: String): Flow<Resource<Relationship>>
    fun getMutedAccounts(): Flow<Resource<List<Account>>>
    fun getBlockedAccounts(): Flow<Resource<List<Account>>>
    fun getLikedBy(postId: String): Flow<Resource<List<Account>>>

    fun getAccountsFollowers(
        accountId: String, cursor: String? = null
    ): Flow<Resource<PaginatedResponse<List<Account>>>>

    fun getAccountsFollowing(
        accountId: String, cursor: String? = null
    ): Flow<Resource<PaginatedResponse<List<Account>>>>
}

@Inject
@AppSingleton
class AccountServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedAccountService,
    //private val vernissage: VernissageTimelineService
) : AccountService {

    private val current: AccountService
        get() = when (session.backendType) {
            // BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }
    override val refreshSignal: MutableSharedFlow<Unit> = current.refreshSignal

    override fun getOwnAccount(): Flow<Resource<Account>> = current.getOwnAccount()

    override fun updateAccount(
        displayName: String,
        note: String,
        website: String,
        privateProfile: Boolean,
        avatar: ImageBitmap?
    ): Flow<Resource<Account>> = current.updateAccount(displayName, note, website, privateProfile, avatar)

    override fun getAccount(accountId: String): Flow<Resource<Account>> = current.getAccount(accountId)

    override fun getAccountByUsername(username: String): Flow<Resource<Account>> = current.getAccountByUsername(username)

    override fun getMutualFollowers(userId: String): Flow<Resource<List<Account>>> = current.getMutualFollowers(userId)

    override fun getAccountSettings(): Flow<Resource<Settings>> = current.getAccountSettings()

    override fun followAccount(accountId: String): Flow<Resource<Relationship>> = current.followAccount(accountId)

    override fun unfollowAccount(accountId: String): Flow<Resource<Relationship>> = current.unfollowAccount(accountId)

    override fun muteAccount(accountId: String): Flow<Resource<Relationship>> = current.muteAccount(accountId)

    override fun unMuteAccount(accountId: String): Flow<Resource<Relationship>> = current.unMuteAccount(accountId)

    override fun blockAccount(accountId: String): Flow<Resource<Relationship>> = current.blockAccount(accountId)

    override fun unblockAccount(accountId: String): Flow<Resource<Relationship>> = current.unblockAccount(accountId)

    override fun getMutedAccounts(): Flow<Resource<List<Account>>> = current.getMutedAccounts()

    override fun getBlockedAccounts(): Flow<Resource<List<Account>>> = current.getBlockedAccounts()

    override fun getLikedBy(postId: String): Flow<Resource<List<Account>>> = current.getLikedBy(postId)

    override fun getAccountsFollowers(
        accountId: String,
        cursor: String?
    ): Flow<Resource<PaginatedResponse<List<Account>>>> = current.getAccountsFollowers(accountId, cursor)

    override fun getAccountsFollowing(
        accountId: String,
        cursor: String?
    ): Flow<Resource<PaginatedResponse<List<Account>>>> = current.getAccountsFollowing(accountId, cursor)
}