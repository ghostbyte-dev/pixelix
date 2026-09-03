package com.daniebeler.pfpixelix

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.github.terrakok.navigation3.browser.HierarchicalBrowserNavigation
import com.github.terrakok.navigation3.browser.buildBrowserHistoryFragment

@Composable
internal fun PixelixBrowserNavigation(destination: Destination) {
    HierarchicalBrowserNavigation {
        destination.toBrowserHistoryFragment()
    }
}

private fun Destination.toBrowserHistoryFragment(): String = when (this) {
    is Destination.Post -> route("post", "id" to id)
    is Destination.EditPost -> route("edit-post", "id" to id)
    is Destination.Collection -> route("collection", "id" to id)
    is Destination.Followers -> route(
        "followers",
        "userId" to userId,
        "username" to username,
        "followers" to isFollowers.toString(),
    )
    is Destination.Chat -> route("chat", "id" to id)
    is Destination.Mention -> route("mention", "id" to id)
    is Destination.Profile -> route("profile", "id" to userId, "username" to username)
    is Destination.ProfileByUsername -> route("profile", "username" to userName)
    is Destination.Hashtag -> route("hashtag", "tag" to hashtag)
    is Destination.HashtagTimeline -> route("hashtag", "tag" to hashtag)
    is Destination.CameraTimeline -> route("camera", "camera" to camera)
    is Destination.CategoryTimeline -> route("category", "category" to category)
    is Destination.LensTimeline -> route("lens", "lens" to lens)
    is Destination.FilmTimeline -> route("film", "film" to film)
    is Destination.Search -> route("search", "page" to page.toString())
    is Destination.NewPost -> route("new-post")
    Destination.FirstLogin -> route("login")
    Destination.NewLogin -> route("new-login")
    Destination.Feeds, Destination.HomeTabFeeds -> route("feeds")
    Destination.Notifications, Destination.HomeTabNotifications -> route("notifications")
    Destination.OwnProfile, Destination.HomeTabOwnProfile -> route("profile")
    Destination.HomeTabSearch -> route("search")
    Destination.HomeTabNewPost -> route("new-post")
    Destination.Conversations -> route("messages")
    Destination.EditProfile -> route("edit-profile")
    Destination.IconSelection -> route("app-icon")
    Destination.MutedAccounts -> route("muted-accounts")
    Destination.BlockedAccounts -> route("blocked-accounts")
    Destination.LikedPosts -> route("liked-posts")
    Destination.BookmarkedPosts -> route("bookmarked-posts")
    Destination.FollowedHashtags -> route("followed-hashtags")
    Destination.AboutInstance -> route("about-instance")
    Destination.AboutPixelix -> route("about-pixelix")
}

private fun route(name: String, vararg parameters: Pair<String, String?>): String =
    buildBrowserHistoryFragment(name, parameters.toMap())
