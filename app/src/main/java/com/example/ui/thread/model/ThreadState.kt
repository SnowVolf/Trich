package com.example.ui.thread.model

import com.example.domain.model.Post

sealed class CaptchaState {
    object Hidden : CaptchaState()
    object Loading : CaptchaState()
    data class Showing(val id: String, val image: String, val keyboard: List<String>) : CaptchaState()
    data class Success(val successId: String) : CaptchaState()
}

data class ThreadState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val draftText: String = "",
    val threadTitle: String = "",
    val isFavorite: Boolean = false,
    val bumpLimit: Int = 500,
    val captchaState: CaptchaState = CaptchaState.Hidden,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val hasNewPosts: Boolean = false,
    val newPostsCount: Int = 0,
    val pendingPosts: List<Post>? = null
) {
    val filteredPosts: List<Post>
        get() = if (!isSearchActive || searchQuery.isBlank()) posts else posts.filter {
            it.comment.contains(searchQuery, ignoreCase = true) || it.num.toString().contains(searchQuery)
        }
}
