package ru.svolf.trich.ui.threadlist.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.svolf.trich.ui.threadlist.ThreadListViewModel

val threadListModule = module {
    viewModel {
        ThreadListViewModel(
            repository = get(),
            settingsRepository = get()
        )
    }
}
