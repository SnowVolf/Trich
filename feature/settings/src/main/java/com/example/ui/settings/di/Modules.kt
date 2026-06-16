package ru.svolf.trich.ui.settings.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.svolf.trich.ui.settings.SettingsViewModel

val settingsModule = module {
    viewModel { SettingsViewModel(get()) }
}
