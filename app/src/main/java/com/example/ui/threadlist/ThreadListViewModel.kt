package com.example.ui.threadlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.threadlist.model.ThreadListState
import com.example.repository.DvachRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThreadListViewModel(
    private val repository: DvachRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ThreadListState())
    val state: StateFlow<ThreadListState> = _state.asStateFlow()

    fun loadThreads(board: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val threads = repository.getThreads(board)
                _state.value = _state.value.copy(isLoading = false, threads = threads)
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
