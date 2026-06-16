package ru.svolf.trich.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    private const val FAVORITES_WORK_NAME = "check_favorites_work"

    /**
     * Планирует или отменяет выполнение фоновой задачи проверки избранных тредов.
     * Задача выполняется каждые 15 минут в периодическом режиме при наличии интернета.
     * @param context Текущий контекст приложения.
     * @param enabled Включить или выключить фоновую задачу.
     */
    fun scheduleFavoritesWorker(context: Context, enabled: Boolean) {
        val workManager = WorkManager.getInstance(context)
        if (enabled) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<FavoritesWorker>(2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                FAVORITES_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        } else {
            workManager.cancelUniqueWork(FAVORITES_WORK_NAME)
        }
    }
}
