package com.example.ui.threadlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.threadlist.model.ThreadListState
import com.example.repository.DvachRepository
import com.example.ui.settings.model.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel для управления экраном каталога тредов выбранной доски.
 * @property repository Зависимость от DvachRepository для загрузки списка тредов.
 * @property settingsRepository Зависимость от SettingsRepository для чтения глобальных настроек (размер шрифта).
 */
class ThreadListViewModel(
    private val repository: DvachRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ThreadListState())

    /**
     * Поток состояния для UI экрана каталога тредов.
     */
    val state: StateFlow<ThreadListState> = _state.asStateFlow()

    init {
        settingsRepository.settingsFlow.onEach { settings ->
            _state.value = _state.value.copy(fontSize = settings.fontSize)
        }.launchIn(viewModelScope)
    }

    private var currentBoard: String = ""

    /**
     * Загружает каталог потоков ("тредов") для выбранной доски.
     * @param board строка, обозначающая доску (например "b", "vg").
     * @param force флаг форсированной загрузки (принудительное обновление).
     */
    fun loadThreads(board: String, force: Boolean = false) {
        if (!force && currentBoard == board && _state.value.threads.isNotEmpty() && _state.value.error == null && !_state.value.isLoading) return
        currentBoard = board
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val boardsInfo = repository.getBoards()
                val canPost = boardsInfo.find { it.id == board }?.enablePosting ?: false
                val threads = repository.getThreads(board)
                _state.value = _state.value.copy(
                    isLoading = false,
                    threads = threads,
                    canCreateThread = canPost
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Unknown Error"
                )
            }
        }
    }
}
