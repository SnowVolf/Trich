package com.example.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.DvachRepository
import com.example.ui.favorites.model.FavoritesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: DvachRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val list = repository.getFavorites()
            _state.value = _state.value.copy(favorites = list, isLoading = false, selectedIds = emptySet(), isSelectionMode = false)
        }
    }

    fun toggleSelection(id: String) {
        val currentSelected = _state.value.selectedIds.toMutableSet()
        if (currentSelected.contains(id)) {
            currentSelected.remove(id)
        } else {
            currentSelected.add(id)
        }
        _state.value = _state.value.copy(
            selectedIds = currentSelected,
            isSelectionMode = currentSelected.isNotEmpty()
        )
    }

    fun clearAllFavorites() {
        viewModelScope.launch {
            repository.clearAllFavorites()
            loadFavorites()
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val idsToDelete = _state.value.selectedIds.toList()
            repository.deleteFavorites(idsToDelete)
            loadFavorites()
        }
    }
}
