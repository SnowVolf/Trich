package com.example.ui.favorites.model

import com.example.db.FavoriteThread

data class FavoritesState(
    val favorites: List<FavoriteThread> = emptyList(),
    val isLoading: Boolean = true,
    val selectedIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
)
