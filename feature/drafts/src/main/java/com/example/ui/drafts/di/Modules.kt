package ru.svolf.trich.ui.drafts.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.svolf.trich.ui.drafts.DraftsViewModel

val draftsModule = module {
    viewModel { DraftsViewModel(get()) }
}
