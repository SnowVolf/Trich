package ru.svolf.trich.ui.thread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.svolf.trich.repository.DvachRepository
import ru.svolf.trich.ui.settings.model.SettingsRepository
import ru.svolf.trich.ui.thread.model.CaptchaState
import ru.svolf.trich.ui.thread.model.ThreadState

/**
 * ViewModel для управления экраном конкретного треда (просмотр постов, ответы, капча).
 * @property repository Зависимость от DvachRepository для загрузки и отправки постов.
 * @property settingsRepository Зависимость для чтения глобальных настроек (размер шрифта).
 */
class ThreadViewModel(
    private val repository: DvachRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ThreadState())

    /**
     * Поток состояния для UI экрана треда.
     */
    val state: StateFlow<ThreadState> = _state.asStateFlow()

    init {
        settingsRepository.settingsFlow.onEach { settings ->
            _state.value = _state.value.copy(fontSize = settings.fontSize)
        }.launchIn(viewModelScope)
    }

    private var initialDraftLoaded = false
    private var currentBoard: String = ""
    private var currentThreadNum: Int = 0
    private var pollingJob: Job? = null

    /**
     * Инициализирует загрузку данных параметров треда и черновиков.
     * @param board Код доски.
     * @param threadNum Номер треда.
     */
    fun load(board: String, threadNum: Int) {
        if (currentBoard == board && currentThreadNum == threadNum && _state.value.posts.isNotEmpty()) return
        currentBoard = board
        currentThreadNum = threadNum
        loadThread()
        loadDraft()
        checkFavorite()
    }

    /**
     * Проверяет, находится ли текущий загружаемый тред в избранном, и обновляет состояние.
     */
    private fun checkFavorite() {
        viewModelScope.launch {
            val isFavorite = repository.isFavorite(currentBoard, currentThreadNum)
            _state.value = _state.value.copy(isFavorite = isFavorite)
        }
    }

    /**
     * Переключает статус избранного для текущего треда.
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            val title = if (_state.value.posts.isNotEmpty()) {
                val op = _state.value.posts.first()
                op.subject.ifBlank { op.comment.take(50) }
            } else "Тред $currentThreadNum"
            val isFav = repository.toggleFavorite(
                currentBoard,
                currentThreadNum,
                title,
                _state.value.posts.size
            )
            _state.value = _state.value.copy(isFavorite = isFav)
        }
    }

    /**
     * Выполняет загрузку постов треда из репозитория и запускает фоллинг новых сообщений.
     */
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
                _state.value = _state.value.copy(
                    isLoading = false,
                    posts = posts,
                    threadTitle = title,
                    bumpLimit = bumpLimit
                )
                repository.updateFavoritePostsCount(currentBoard, currentThreadNum, posts.size)
                startPolling()
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    /**
     * Проверяет наличие черновика для данного треда в БД и загружает его текст в строку ввода.
     */
    private fun loadDraft() {
        viewModelScope.launch {
            val draft = repository.getDraft(currentBoard, currentThreadNum)
            if (draft != null) {
                _state.value = _state.value.copy(draftText = draft.text)
            }
            initialDraftLoaded = true
        }
    }

    /**
     * Обновляет текстовое содержимое текущего черновика.
     * @param text Новый текст сообщения.
     */
    fun updateDraftText(text: String) {
        _state.value = _state.value.copy(draftText = text)
    }

    /**
     * Обновляет поисковый запрос внутри треда.
     * @param query Текст поиска.
     */
    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    /**
     * Переключает видимость строки поиска в приложении.
     */
    fun toggleSearch() {
        _state.value = _state.value.copy(
            isSearchActive = !_state.value.isSearchActive,
            searchQuery = if (_state.value.isSearchActive) "" else _state.value.searchQuery
        )
    }

    /**
     * Запускает периодический цикл фоллинга на проверку новых сообщений в треде (каждые 60 секунд).
     */
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

    /**
     * Подтверждает применение загруженных при фоллинге новых постов для отображения в ленте треда.
     */
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

    /**
     * Сохраняет текущее незавершенное сообщение как черновик в БД при выходе из треда.
     */
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

    /**
     * Инициализирует старт процесса прохождения капчи.
     */
    fun startCaptchaFlow() {
        viewModelScope.launch {
            _state.value = _state.value.copy(captchaState = CaptchaState.Loading)
            try {
                val idResponse = repository.getCaptchaId(currentBoard, currentThreadNum)
                if (idResponse.id.isNotEmpty()) {
                    val showResponse = repository.showEmojiCaptcha(idResponse.id)
                    val success = showResponse.success
                    val image = showResponse.image
                    val keyboard = showResponse.keyboard
                    if (success != null) {
                        _state.value =
                            _state.value.copy(captchaState = CaptchaState.Success(success))
                    } else if (image != null && keyboard != null) {
                        _state.value = _state.value.copy(
                            captchaState = CaptchaState.Showing(idResponse.id, image, keyboard)
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(captchaState = CaptchaState.Hidden, error = "Ошибка капчи")
            }
        }
    }

    /**
     * Обрабатывает клик пользователя по элементу emoji-капчи.
     * @param id ID текущей капчи.
     * @param index Индекс выбранного элемента.
     */
    fun clickCaptcha(id: String, index: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(captchaState = CaptchaState.Loading)
            try {
                val response = repository.clickEmojiCaptcha(id, index)
                val success = response.success
                val image = response.image
                val keyboard = response.keyboard
                if (success != null) {
                    _state.value = _state.value.copy(captchaState = CaptchaState.Success(success))
                    sendPost(success)
                } else if (image != null && keyboard != null) {
                    _state.value = _state.value.copy(
                        captchaState = CaptchaState.Showing(id, image, keyboard)
                    )
                }
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(captchaState = CaptchaState.Hidden, error = "Ошибка капчи")
            }
        }
    }

    /**
     * Скрывает диалог с капчей.
     */
    fun hideCaptcha() {
        _state.value = _state.value.copy(captchaState = CaptchaState.Hidden)
    }

    /**
     * Инициирует алгоритм отправки сообщения (запускает капчу при необходимости).
     */
    fun startPosting() {
        val currentState = _state.value.captchaState
        if (currentState is CaptchaState.Success) {
            sendPost(currentState.successId)
        } else {
            startCaptchaFlow()
        }
    }

    /**
     * Непосредственно отправляет сообщение на сервер Двачей.
     * Очищает поле ввода и удаляет черновик при успешном завершении.
     * @param captchaId Успешный ID пройденной капчи.
     */
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
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Ошибка отправки: ${e.localizedMessage}"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
