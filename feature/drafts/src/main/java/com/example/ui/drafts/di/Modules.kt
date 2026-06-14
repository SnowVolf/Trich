package com.example.ui.drafts.di

import com.example.ui.drafts.DraftsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val draftsModule = module {
    viewModel { DraftsViewModel(get()) }
}
