package com.example.ui.newthread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.DvachRepository
import com.example.ui.newthread.model.NewThreadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class NewThreadViewModel(
    private val repository: DvachRepository,
    private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(NewThreadState())
    val state: StateFlow<NewThreadState> = _state.asStateFlow()

    private var currentBoard: String = ""

    fun init(board: String) {
        currentBoard = board
    }

    fun onSubjectChanged(subject: String) {
        _state.value = _state.value.copy(subject = subject)
    }

    fun onCommentChanged(comment: String) {
        _state.value = _state.value.copy(comment = comment)
    }

    fun sendPostPressed() {
        requestCaptcha()
    }

    private fun requestCaptcha() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                showCaptcha = true,
                captchaLoading = true,
                captchaError = null,
                postingError = null,
                captchaImage = null,
                captchaKeyboard = null
            )
            try {
                val captchaInfo = repository.getCaptchaId(currentBoard, 0)
                val id = captchaInfo.id
                if (id.isEmpty()) {
                    // No captcha required or error
                    submitPost("")
                    return@launch
                }

                _state.value = _state.value.copy(captchaId = id)
                val showResp = repository.showEmojiCaptcha(id)
                _state.value = _state.value.copy(
                    captchaLoading = false,
                    captchaImage = showResp.image,
                    captchaKeyboard = showResp.keyboard
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(
                    captchaLoading = false,
                    captchaError = e.localizedMessage ?: "Ошибка капчи",
                    showCaptcha = false
                )
            }
        }
    }

    fun onCaptchaEmojiSelected(index: Int) {
        val id = _state.value.captchaId ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(captchaLoading = true, captchaError = null)
            try {
                val response = repository.clickEmojiCaptcha(id, index)
                if (response.success != null) {
                    // Captcha solved
                    _state.value = _state.value.copy(showCaptcha = false, captchaLoading = false)
                    submitPost(id)
                } else if (response.image != null) {
                    // Go to next step
                    _state.value = _state.value.copy(
                        captchaLoading = false,
                        captchaImage = response.image,
                        captchaKeyboard = response.keyboard ?: _state.value.captchaKeyboard
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    captchaLoading = false,
                    captchaError = e.localizedMessage ?: "Ошибка капчи",
                    showCaptcha = false
                )
            }
        }
    }

    fun dismissCaptcha() {
        _state.value = _state.value.copy(showCaptcha = false, captchaLoading = false)
    }

    fun onImagesAttached(uris: List<Uri>) {
        val currentImages = _state.value.attachedImages
        val newImages = (currentImages + uris).take(4) // Максимум 4 картинки
        _state.value = _state.value.copy(attachedImages = newImages)
    }

    fun removeImage(uri: Uri) {
        val currentImages = _state.value.attachedImages
        _state.value = _state.value.copy(attachedImages = currentImages - uri)
    }

    private fun submitPost(captchaId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isPosting = true, postingError = null)
            try {
                val files = _state.value.attachedImages.map { uri ->
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val tempFile =
                        File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                    val outputStream = FileOutputStream(tempFile)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()
                    val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file[]", tempFile.name, requestBody)
                }

                repository.createThread(
                    currentBoard,
                    _state.value.subject,
                    _state.value.comment,
                    captchaId,
                    files
                )
                _state.value = _state.value.copy(
                    isPosting = false,
                    postSuccess = true,
                    subject = "",
                    comment = "",
                    attachedImages = emptyList()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isPosting = false,
                    postingError = e.localizedMessage ?: "Ошибка постинга"
                )
            }
        }
    }
}
