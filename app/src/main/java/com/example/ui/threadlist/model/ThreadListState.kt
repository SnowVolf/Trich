package com.example.ui.threadlist.model

import com.example.domain.model.ThreadSummary

data class ThreadListState(
    val isLoading: Boolean = false,
    val threads: List<ThreadSummary> = emptyList(),
    val error: String? = null
)
