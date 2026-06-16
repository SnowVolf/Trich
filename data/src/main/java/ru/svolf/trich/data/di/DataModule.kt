package ru.svolf.trich.data.di

import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import ru.svolf.trich.api.ApiUrlProvider
import ru.svolf.trich.api.DvachApi
import ru.svolf.trich.db.AppDatabase
import ru.svolf.trich.repository.DvachRepository
import ru.svolf.trich.ui.settings.model.SettingsRepository

val dataModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "dvach_db"
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<AppDatabase>().visitedThreadDao() }
    single { get<AppDatabase>().draftDao() }
    single { get<AppDatabase>().favoriteThreadDao() }

    // Network
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }
    single {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(ChuckerInterceptor(androidContext()))
            .build()
    }
    single { ApiUrlProvider() }

    single {
        val json = get<Json>()
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(get<ApiUrlProvider>().baseUrl + "/")
            .client(get())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(DvachApi::class.java)
    }

    // Repository
    single {
        DvachRepository(
            api = get(),
            visitedThreadDao = get(),
            draftDao = get(),
            favoriteThreadDao = get(),
            apiUrlProvider = get()
        )
    }

    single { SettingsRepository(get()) }
}
