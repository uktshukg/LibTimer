package com.dexter.baseproject.frag_one

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.dexter.baseproject.ConvertMILLISToStandard
import com.dexter.baseproject.R
import com.dexter.baseproject.SharedPref
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class Worker (val context: Context,
              workerParameters: WorkerParameters
) : RxWorker(context, workerParameters) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    override fun createWork(): Single<Result> {
        val startTime =
            SharedPref.getLong(context,context.getString(R.string.start_time))
        createNotificationChannel()
//        val notification = NotificationCompat.Builder(applicationContext, channelId)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setContentTitle("Important background job")
//            .setOngoing(true)
//            .build()
//
//        val foregroundInfo = ForegroundInfo(NOTIFICATION_ID, notification)
//        setForegroundAsync(foregroundInfo)
        val latch = CountDownLatch(1)
        Observable.interval(1, TimeUnit.SECONDS).subscribe {
            Log.e("utkarsh","inside intervalRange "+it)
//            showProgress(System.currentTimeMillis()- startTime)
          }
        Log.e("utkarsh","inside creatework")
        latch.await()
        return Single.just(Result.success())
    }

    private fun showProgress(progress: Long) {
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Important background job")
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setContentText(ConvertMILLISToStandard(progress.toLong()))
            .build()
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel =
                notificationManager?.getNotificationChannel(channelId)
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel(
                    channelId, TAG, NotificationManager.IMPORTANCE_HIGH
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