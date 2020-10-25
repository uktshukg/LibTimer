package com.dexter.baseproject.frag_one



import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.dexter.baseproject.R
import kotlinx.coroutines.delay

class ForegroundWorker(val appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    private val notificationManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        appContext.getSystemService(NotificationManager::class.java)
    } else {
        appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }


    override suspend fun doWork(): Result {

        Log.d(TAG, "Start job")

        createNotificationChannel()
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Important background job")
            .build()

        val foregroundInfo = ForegroundInfo(NOTIFICATION_ID, notification)
        setForeground(foregroundInfo)

        for (i in 0..Long.MAX_VALUE) {
            showProgress(i)
            delay(delayDuration)
        }

        Log.d(TAG, "Finish job")
        return Result.success()
    }

    private fun showProgress(progress: Long) {
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Important background job")
            .setContentText(progress.toString())
            .build()
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel =
                notificationManager?.getNotificationChannel(channelId)
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel(
                    channelId, TAG, NotificationManager.IMPORTANCE_LOW
                )
                notificationManager?.createNotificationChannel(notificationChannel)
            }
        }
    }

    companion object {

        const val NOTIFICATION_ID = 42
        const val TAG = "ForegroundWorker"
        const val channelId = "Job progress"
        const val Progress = "Progress"
        private const val delayDuration = 100L
    }
}