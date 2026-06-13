package com.example.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    private const val FAVORITES_WORK_NAME = "check_favorites_work"

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
