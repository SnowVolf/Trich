package ru.svolf.trich.ui.thread.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.svolf.trich.ui.thread.ThreadViewModel

val threadModule = module {
    viewModel {
        ThreadViewModel(
            repository = get(),
            settingsRepository = get()
        )
    }
}
