package com.example.ui.boards.model

import com.example.domain.model.Board

data class BoardsState(
    val isLoading: Boolean = false,
    val boards: List<Board> = emptyList(),
    val error: String? = null
)
