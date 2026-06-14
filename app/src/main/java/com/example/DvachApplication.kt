package com.example

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.decode.VideoFrameDecoder
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import android.os.Build
import com.example.data.di.dataModule
import com.example.di.appModule
import com.example.ui.boards.di.boardsModule
import com.example.ui.drafts.di.draftsModule
import com.example.ui.favorites.di.favoritesModule
import com.example.ui.history.di.historyModule
import com.example.ui.settings.di.settingsModule
import com.example.ui.thread.di.threadModule
import com.example.ui.threadlist.di.threadListModule
import com.example.ui.newthread.di.newThreadModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Главный класс приложения. Инициализирует DI (Koin) и настраивает кеширование Coil.
 */
class DvachApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DvachApplication)
            modules(
                dataModule,
                appModule,
                settingsModule,
                boardsModule,
                historyModule,
                draftsModule,
                favoritesModule,
                threadModule,
                threadListModule,
                newThreadModule
            )
        }
    }

    /**
     * Создает и конфигурирует загрузчик картинок/gif/видео для Coil с кешем.
     * @return Инстанс ImageLoader.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024) // 100 MB
                    .build()
            }
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(VideoFrameDecoder.Factory())
            }
            .respectCacheHeaders(false)
            .build()
    }
}
