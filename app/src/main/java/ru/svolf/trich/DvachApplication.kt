package ru.svolf.trich

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.svolf.trich.data.di.dataModule
import ru.svolf.trich.ui.boards.di.boardsModule
import ru.svolf.trich.ui.drafts.di.draftsModule
import ru.svolf.trich.ui.favorites.di.favoritesModule
import ru.svolf.trich.ui.history.di.historyModule
import ru.svolf.trich.ui.newthread.di.newThreadModule
import ru.svolf.trich.ui.settings.di.settingsModule
import ru.svolf.trich.ui.thread.di.threadModule
import ru.svolf.trich.ui.threadlist.di.threadListModule

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
