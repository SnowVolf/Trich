package com.example.ui.newthread.model

data class NewThreadState(
    val subject: String = "",
    val comment: String = "",
    val attachedImages: List<android.net.Uri> = emptyList(),
    val isPosting: Boolean = false,
    val postingError: String? = null,
    val postSuccess: Boolean = false,

    // Captcha
    val showCaptcha: Boolean = false,
    val captchaId: String? = null,
    val captchaImage: String? = null,
    val captchaKeyboard: List<String>? = null,
    val captchaLoading: Boolean = false,
    val captchaError: String? = null,
)
