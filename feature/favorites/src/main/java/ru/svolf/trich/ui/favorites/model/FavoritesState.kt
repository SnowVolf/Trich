package ru.svolf.trich.ui.favorites.model

import  ru.svolf.trich.db.FavoriteThread

data class FavoritesState(
    val favorites: List<FavoriteThread> = emptyList(),
    val isLoading: Boolean = true,
    val selectedIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
)
