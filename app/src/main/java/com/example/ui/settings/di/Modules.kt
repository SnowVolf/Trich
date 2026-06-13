package com.example.ui.settings.di

import com.example.ui.settings.SettingsViewModel
import com.example.ui.settings.model.SettingsRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single { SettingsRepository(get()) }
    viewModel { SettingsViewModel(get()) }
}
