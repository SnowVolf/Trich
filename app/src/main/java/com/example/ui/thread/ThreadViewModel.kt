package com.example.ui.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.PostInfo
import com.example.repository.DvachRepository
import com.example.ui.thread.model.CaptchaState
import com.example.ui.thread.model.ThreadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Job

class ThreadViewModel(
    private val repository: DvachRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ThreadState())
    val state: StateFlow<ThreadState> = _state.asStateFlow()

    private var initialDraftLoaded = false
    private var currentBoard: String = ""
    private var currentThreadNum: Int = 0
    private var pollingJob: Job? = null

    fun load(board: String, threadNum: Int) {
        if (currentBoard == board && currentThreadNum == threadNum && _state.value.posts.isNotEmpty()) return
        currentBoard = board
        currentThreadNum = threadNum
        loadThread()
        loadDraft()
        checkFavorite()
    }

    private fun checkFavorite() {
        viewModelScope.launch {
            val isFavorite = repository.isFavorite(currentBoard, currentThreadNum)
            _state.value = _state.value.copy(isFavorite = isFavorite)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val title = if (_state.value.posts.isNotEmpty()) {
                val op = _state.value.posts.first()
                op.subject.ifBlank { op.comment.take(50) }
            } else "Тред $currentThreadNum"
            val isFav = repository.toggleFavorite(currentBoard, currentThreadNum, title, _state.value.posts.size)
            _state.value = _state.value.copy(isFavorite = isFav)
        }
    }

    private fun loadThread() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val boards = repository.getBoards()
                val currentBoardSettings = boards.find { it.id == currentBoard }
                val bumpLimit = currentBoardSettings?.bumpLimit ?: 500

                val posts = repository.getThreadPosts(currentBoard, currentThreadNum)
                val title = if (posts.isNotEmpty()) {
                    val op = posts.first()
                    op.subject.ifBlank { op.comment.take(50) }
                } else "Тред $currentThreadNum"
                _state.value = _state.value.copy(isLoading = false, posts = posts, threadTitle = title, bumpLimit = bumpLimit)
                repository.updateFavoritePostsCount(currentBoard, currentThreadNum, posts.size)
                startPolling()
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    private fun loadDraft() {
        viewModelScope.launch {
            val draft = repository.getDraft(currentBoard, currentThreadNum)
            if (draft != null) {
                _state.value = _state.value.copy(draftText = draft.text)
            }
            initialDraftLoaded = true
        }
    }

    fun updateDraftText(text: String) {
        _state.value = _state.value.copy(draftText = text)
    }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun toggleSearch() {
        _state.value = _state.value.copy(
            isSearchActive = !_state.value.isSearchActive,
            searchQuery = if (_state.value.isSearchActive) "" else _state.value.searchQuery
        )
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(60_000)
                try {
                    val fetchedPosts = repository.getThreadPosts(currentBoard, currentThreadNum)
                    val currentPostsCount = _state.value.posts.size
                    if (fetchedPosts.size > currentPostsCount) {
                        _state.value = _state.value.copy(
                            hasNewPosts = true,
                            newPostsCount = fetchedPosts.size - currentPostsCount,
                            pendingPosts = fetchedPosts
                        )
                    }
                } catch (e: Exception) {
                    // Ignore polling errors
                }
            }
        }
    }

    fun loadNewPosts() {
        _state.value.pendingPosts?.let { pending ->
            _state.value = _state.value.copy(
                posts = pending,
                hasNewPosts = false,
                newPostsCount = 0,
                pendingPosts = null
            )
            viewModelScope.launch {
                repository.updateFavoritePostsCount(currentBoard, currentThreadNum, pending.size)
            }
        }
    }

    fun saveDraftOnExit() {
        if (!initialDraftLoaded || currentBoard.isEmpty()) return
        viewModelScope.launch {
            val text = _state.value.draftText
            if (text.isNotBlank()) {
                val title = _state.value.posts.firstOrNull()?.let {
                    it.subject.ifBlank { it.comment.take(50) }
                } ?: "Тред $currentThreadNum"
                repository.saveDraft(currentBoard, currentThreadNum, text, title)
            }
        }
    }

    fun startCaptchaFlow() {
        viewModelScope.launch {
            _state.value = _state.value.copy(captchaState = CaptchaState.Loading)
            try {
                val idResponse = repository.getCaptchaId(currentBoard, currentThreadNum)
                if (idResponse.id.isNotEmpty()) {
                    val showResponse = repository.showEmojiCaptcha(idResponse.id)
                    if (showResponse.success != null) {
                        _state.value = _state.value.copy(captchaState = CaptchaState.Success(showResponse.success))
                    } else if (showResponse.image != null && showResponse.keyboard != null) {
                        _state.value = _state.value.copy(
                            captchaState = CaptchaState.Showing(idResponse.id, showResponse.image, showResponse.keyboard)
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(captchaState = CaptchaState.Hidden, error = "Ошибка капчи")
            }
        }
    }

    fun clickCaptcha(id: String, index: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(captchaState = CaptchaState.Loading)
            try {
                val response = repository.clickEmojiCaptcha(id, index)
                if (response.success != null) {
                    _state.value = _state.value.copy(captchaState = CaptchaState.Success(response.success))
                    sendPost(response.success)
                } else if (response.image != null && response.keyboard != null) {
                    _state.value = _state.value.copy(
                        captchaState = CaptchaState.Showing(id, response.image, response.keyboard)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(captchaState = CaptchaState.Hidden, error = "Ошибка капчи")
            }
        }
    }

    fun hideCaptcha() {
        _state.value = _state.value.copy(captchaState = CaptchaState.Hidden)
    }

    fun startPosting() {
        val currentState = _state.value.captchaState
        if (currentState is CaptchaState.Success) {
            sendPost(currentState.successId)
        } else {
            startCaptchaFlow()
        }
    }

    private fun sendPost(captchaId: String) {
        val text = _state.value.draftText
        if (text.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, captchaState = CaptchaState.Hidden)
            try {
                repository.postMessage(currentBoard, currentThreadNum, text, captchaId)
                _state.value = _state.value.copy(draftText = "")
                loadThread()
                repository.getDraft(currentBoard, currentThreadNum)?.let {
                    repository.deleteDrafts(listOf(it.id))
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Ошибка отправки: ${e.localizedMessage}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
