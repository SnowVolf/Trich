package ru.svolf.trich.ui.favorites.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.svolf.trich.ui.favorites.FavoritesViewModel

val favoritesModule = module {
    viewModel { FavoritesViewModel(get()) }
}
