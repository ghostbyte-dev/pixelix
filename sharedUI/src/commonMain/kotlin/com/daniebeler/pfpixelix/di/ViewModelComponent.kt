package com.daniebeler.pfpixelix.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daniebeler.pfpixelix.ui.composables.HomeViewModel
import com.daniebeler.pfpixelix.ui.composables.collection.CollectionViewModel
import com.daniebeler.pfpixelix.ui.composables.custom_account.CustomAccountViewModel
import com.daniebeler.pfpixelix.ui.composables.direct_messages.chat.ChatViewModel
import com.daniebeler.pfpixelix.ui.composables.direct_messages.conversations.ConversationsViewModel
import com.daniebeler.pfpixelix.ui.composables.edit_profile.EditProfileViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.ExploreViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.cameras.CamerasViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.categories.CategoriesViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.films.FilmsViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.lenses.LensesViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts.TrendingAccountElementViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts.TrendingAccountsViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.TrendingHashtagElementViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.TrendingHashtagsViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts.TrendingPostsViewModel
import com.daniebeler.pfpixelix.ui.composables.followers.FollowersViewModel
import com.daniebeler.pfpixelix.ui.composables.hashtagMentionText.TextWithClickableHashtagsAndMentionsViewModel
import com.daniebeler.pfpixelix.ui.composables.mention.MentionViewModel
import com.daniebeler.pfpixelix.ui.composables.notifications.CustomNotificationViewModel
import com.daniebeler.pfpixelix.ui.composables.notifications.NotificationsViewModel
import com.daniebeler.pfpixelix.ui.composables.post.PostViewModel
import com.daniebeler.pfpixelix.ui.composables.post_editor.PostEditorViewModel
import com.daniebeler.pfpixelix.ui.composables.profile.other_profile.OtherProfileViewModel
import com.daniebeler.pfpixelix.ui.composables.profile.own_profile.AccountSwitchViewModel
import com.daniebeler.pfpixelix.ui.composables.profile.own_profile.OwnProfileViewModel
import com.daniebeler.pfpixelix.ui.composables.profile.server_stats.ServerStatsViewModel
import com.daniebeler.pfpixelix.ui.composables.session.LoginViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.about_instance.AboutInstanceViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.about_pixelix.AboutPixelixViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts.BlockedAccountsViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.followed_hashtags.FollowedHashtagsViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.icon_selection.IconSelectionViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts.MutedAccountsViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.PreferencesViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.ClearCacheViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs.DefaultLicenseViewModel
import com.daniebeler.pfpixelix.ui.composables.single_post.SinglePostViewModel
import com.daniebeler.pfpixelix.ui.composables.textfield_location.TextFieldLocationsViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.parametric_timeline_screens.ParametricTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.global_timeline.GlobalTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline.HashtagTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline.HomeTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline.LocalTimelineViewModel
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate

@Component
abstract class ViewModelComponent(
    @Component val appComponent: AppComponent
) {
    abstract val loginViewModel: LoginViewModel
    abstract val collectionViewModel: CollectionViewModel
    abstract val customAccountViewModel: CustomAccountViewModel
    abstract val chatViewModel: ChatViewModel
    abstract val aboutInstanceViewModel: AboutInstanceViewModel
    abstract val aboutPixelixViewModel: AboutPixelixViewModel
    abstract val accountSwitchViewModel: AccountSwitchViewModel
    abstract val blockedAccountsViewModel: BlockedAccountsViewModel
    abstract val customNotificationViewModel: CustomNotificationViewModel
    abstract val editProfileViewModel: EditProfileViewModel
    abstract val exploreViewModel: ExploreViewModel
    abstract val followedHashtagsViewModel: FollowedHashtagsViewModel
    abstract val followersViewModel: FollowersViewModel
    abstract val globalTimelineViewModel: GlobalTimelineViewModel
    abstract val hashtagTimelineViewModel: HashtagTimelineViewModel
    abstract val parametricTimelineViewModel: ParametricTimelineViewModel
    abstract val homeTimelineViewModel: HomeTimelineViewModel
    abstract val iconSelectionViewModel: IconSelectionViewModel
    abstract val localTimelineViewModel: LocalTimelineViewModel
    abstract val mentionViewModel: MentionViewModel
    abstract val mutedAccountsViewModel: MutedAccountsViewModel
    abstract val newPostViewModel: PostEditorViewModel
    abstract val notificationsViewModel: NotificationsViewModel
    abstract val otherProfileViewModel: OtherProfileViewModel
    abstract val ownProfileViewModel: OwnProfileViewModel
    abstract val postViewModel: PostViewModel
    abstract val preferencesViewModel: PreferencesViewModel
    abstract val serverStatsViewModel: ServerStatsViewModel
    abstract val singlePostViewModel: SinglePostViewModel
    abstract val textWithClickableHashtagsAndMentionsViewModel: TextWithClickableHashtagsAndMentionsViewModel
    abstract val trendingAccountElementViewModel: TrendingAccountElementViewModel
    abstract val trendingAccountsViewModel: TrendingAccountsViewModel
    abstract val trendingHashtagElementViewModel: TrendingHashtagElementViewModel
    abstract val trendingHashtagsViewModel: TrendingHashtagsViewModel
    abstract val camerasViewModel: CamerasViewModel
    abstract val trendingPostsViewModel: TrendingPostsViewModel
    abstract val categoriesViewModel: CategoriesViewModel
    abstract val lensesViewModel: LensesViewModel
    abstract val filmsViewModel: FilmsViewModel
    abstract val conversationsViewModel: ConversationsViewModel
    abstract val textFieldLocationsViewModel: TextFieldLocationsViewModel
    abstract val clearCacheViewModel: ClearCacheViewModel
    abstract val defaultLicenseViewModel: DefaultLicenseViewModel
    abstract val homeViewModel: HomeViewModel

    companion object
}

@KmpComponentCreate
expect fun ViewModelComponent.Companion.create(app: AppComponent): ViewModelComponent

val LocalAppComponent = staticCompositionLocalOf<AppComponent> { error("no AppComponent") }

@Composable
inline fun <reified VM: ViewModel> injectViewModel(key: String, crossinline factory: ViewModelComponent.() -> VM): VM {
    val app = LocalAppComponent.current
    return viewModel(key = key) { ViewModelComponent.create(app).factory() }
}