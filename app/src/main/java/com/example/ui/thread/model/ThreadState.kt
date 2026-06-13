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
    val captchaState: CaptchaState = CaptchaState.Hidden
)
