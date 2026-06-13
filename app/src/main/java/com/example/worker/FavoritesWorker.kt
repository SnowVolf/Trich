package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import com.example.repository.DvachRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FavoritesWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val repository: DvachRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            val favorites = repository.getFavorites()
            for (fav in favorites) {
                try {
                    val posts = repository.getThreadPosts(fav.board, fav.threadNum)
                    val newCount = posts.size - fav.lastKnownPostsCount
                    if (newCount > 0 && fav.lastKnownPostsCount > 0) {
                        showNotification(
                            board = fav.board,
                            threadNum = fav.threadNum,
                            title = fav.title,
                            newCount = newCount,
                            targetPost = fav.lastKnownPostsCount + 1
                        )
                    }
                    repository.updateFavoritePostsCount(fav.board, fav.threadNum, posts.size)
                } catch (e: Exception) {
                    // Ignore errors for individual threads
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(board: String, threadNum: Int, title: String, newCount: Int, targetPost: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channelId = "favorites_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Избранные треды",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val url = "https://2ch.hk/$board/res/$threadNum.html?scrollToPost=$targetPost"
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            threadNum, // Unique request code per thread
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = if (newCount == 1) {
            "Новый пост в треде"
        } else {
            "Новые посты ($newCount) в треде"
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            // Use standard Android icon since we might not have a specific one handy
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(threadNum, notification)
    }
}
