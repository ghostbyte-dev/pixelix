package com.daniebeler.pfpixelix.domain.service.general

import androidx.compose.ui.graphics.ImageBitmap
import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.MutedAccount
import com.daniebeler.pfpixelix.domain.model.PaginatedResponse
import com.daniebeler.pfpixelix.domain.model.Relationship
import com.daniebeler.pfpixelix.domain.model.Settings
import com.daniebeler.pfpixelix.domain.model.request.UpdateUserRequest
import com.daniebeler.pfpixelix.domain.model.request.UserBlockRequest
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedAccountService
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissageAccountService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import me.tatarka.inject.annotations.Inject

interface AccountService {
    val refreshSignal: MutableSharedFlow<Unit>
    fun getOwnAccount(): Flow<Resource<Account>>

    fun updateAccount(
        username: String, updateUserRequest: UpdateUserRequest
    ): Flow<Resource<Account>>

    fun updateAvatar(
        username: String, avatar: ImageBitmap?
    ): Flow<Resource<Unit>>

    fun getAccount(accountId: String, username: String): Flow<Resource<Account>>
    fun getAccountByUsername(username: String): Flow<Resource<Account>>
    fun getRelationships(userIds: List<String>): Flow<Resource<List<Relationship>>>
    fun getMutualFollowers(userId: String): Flow<Resource<List<Account>>>
    fun getAccountSettings(): Flow<Resource<Settings>>
    fun followAccount(accountId: String, username: String): Flow<Resource<Relationship>>
    fun unfollowAccount(accountId: String, username: String): Flow<Resource<Relationship>>
    fun muteAccount(
        accountId: String, username: String, userMuteRequest: UserMuteRequest
    ): Flow<Resource<Relationship>>

    fun blockAccount(
        accountId: String, username: String, userBlockRequest: UserBlockRequest
    ): Flow<Resource<Relationship>>

    fun unblockAccount(accountId: String, username: String): Flow<Resource<Relationship>>
    fun getMutedAccounts(): Flow<Resource<List<MutedAccount>>>
    fun getBlockedAccounts(): Flow<Resource<List<Account>>>
    fun getAccountsFollowers(
        accountId: String, username: String, cursor: String? = null
    ): Flow<Resource<PaginatedResponse<List<Account>>>>

    fun getAccountsFollowing(
        accountId: String, username: String, cursor: String? = null
    ): Flow<Resource<PaginatedResponse<List<Account>>>>

    fun acceptFollowRequest(accountId: String): Flow<Resource<Relationship>>
    fun rejectFollowRequest(accountId: String): Flow<Resource<Relationship>>
}

@Inject
@AppSingleton
class AccountServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedAccountService,
    private val vernissage: VernissageAccountService
) : AccountService {

    private val current: AccountService
        get() = when (session.backendType.value) {
            BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }
    override val refreshSignal: MutableSharedFlow<Unit> = current.refreshSignal

    override fun getOwnAccount(): Flow<Resource<Account>> = current.getOwnAccount()

    override fun updateAccount(
        username: String, updateUserRequest: UpdateUserRequest
    ): Flow<Resource<Account>> = current.updateAccount(username, updateUserRequest)

    override fun updateAvatar(username: String, avatar: ImageBitmap?) =
        current.updateAvatar(username, avatar)

    override fun getAccount(accountId: String, username: String): Flow<Resource<Account>> =
        current.getAccount(accountId, username)

    override fun getAccountByUsername(username: String): Flow<Resource<Account>> =
        current.getAccountByUsername(username)

    override fun getRelationships(userIds: List<String>): Flow<Resource<List<Relationship>>> =
        current.getRelationships(userIds)

    override fun getMutualFollowers(userId: String): Flow<Resource<List<Account>>> =
        current.getMutualFollowers(userId)

    override fun getAccountSettings(): Flow<Resource<Settings>> = current.getAccountSettings()

    override fun followAccount(accountId: String, username: String): Flow<Resource<Relationship>> =
        current.followAccount(accountId, username)

    override fun unfollowAccount(
        accountId: String, username: String
    ): Flow<Resource<Relationship>> = current.unfollowAccount(accountId, username)

    override fun muteAccount(
        accountId: String, username: String, userMuteRequest: UserMuteRequest
    ): Flow<Resource<Relationship>> = current.muteAccount(accountId, username, userMuteRequest)

    override fun blockAccount(
        accountId: String, username: String, userBlockRequest: UserBlockRequest
    ): Flow<Resource<Relationship>> = current.blockAccount(accountId, username, userBlockRequest)

    override fun unblockAccount(accountId: String, username: String): Flow<Resource<Relationship>> =
        current.unblockAccount(accountId, username)

    override fun getMutedAccounts(): Flow<Resource<List<MutedAccount>>> = current.getMutedAccounts()

    override fun getBlockedAccounts(): Flow<Resource<List<Account>>> = current.getBlockedAccounts()

    override fun getAccountsFollowers(
        accountId: String, username: String, cursor: String?
    ): Flow<Resource<PaginatedResponse<List<Account>>>> =
        current.getAccountsFollowers(accountId, username, cursor)

    override fun getAccountsFollowing(
        accountId: String, username: String, cursor: String?
    ): Flow<Resource<PaginatedResponse<List<Account>>>> =
        current.getAccountsFollowing(accountId, username, cursor)

    override fun acceptFollowRequest(accountId: String): Flow<Resource<Relationship>> =
        current.acceptFollowRequest(accountId)

    override fun rejectFollowRequest(accountId: String): Flow<Resource<Relationship>> =
        current.rejectFollowRequest(accountId)
}