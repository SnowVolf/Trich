package ru.svolf.trich.ui.threadlist.model

import  ru.svolf.trich.domain.model.ThreadSummary

data class ThreadListState(
    val isLoading: Boolean = false,
    val threads: List<ThreadSummary> = emptyList(),
    val error: String? = null,
    val fontSize: Int = 16,
    val canCreateThread: Boolean = false,
)
