package com.dexter.baseproject.services

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dexter.baseproject.utilities.convertMILLISToStandard
import com.dexter.baseproject.R
import com.dexter.baseproject.utilities.SharedPref
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class NotificationService : IntentService("MyService") {

    private var disposable: Disposable?= null
    private lateinit var countDownLatch: CountDownLatch



    override fun onHandleIntent(p0: Intent?) {
        val locationId = SharedPref.getString("locationId", applicationContext)
        val price = SharedPref.getFloat("price", applicationContext)
        val locationDetails = SharedPref.getString("details", applicationContext)
         val notificationManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             applicationContext.getSystemService(NotificationManager::class.java)
        } else {
             applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        val context =applicationContext
        val startTime =
            SharedPref.getLong(context,context.getString(R.string.start_time))
        createNotificationChannel(notificationManager)
        val notification = NotificationCompat.Builder(applicationContext,
                channelId
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Important background job")
            .build()
        startForeground(NOTIFICATION_ID, notification)
         countDownLatch = CountDownLatch(1)
        disposable=  Observable.interval(1, TimeUnit.SECONDS).subscribe {
            Log.e("utkarsh","inside intervalRange "+it)
           showProgress(System.currentTimeMillis()- startTime, notificationManager, locationDetails, locationId, price)
        }
        Log.e("utkarsh","inside")
        countDownLatch.await()

    }
    private fun showProgress(
        progress: Long,
        notificationManager: NotificationManager,
        locationDetails: String,
        locationId: String,
        price: Float
    ) {
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Important background job")
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setStyle(NotificationCompat.BigTextStyle().setSummaryText(convertMILLISToStandard(progress).toString()))
            .setContentText(convertMILLISToStandard(progress).toString()+"\n location deatils "+locationDetails+"\n price "+price+" \n location id"+locationId+"\n")
            .build()
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
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

    private val receiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.e("utkarsh","inside recieve ")
            countDownLatch.countDown()
            disposable?.dispose()
             stopForeground(true)
            stopSelf()
        }
    }
    override fun onCreate() {
        super.onCreate()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(receiver, IntentFilter("abc"))
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(receiver)
        super.onDestroy()
    }

    companion object {

        const val NOTIFICATION_ID = 42
        const val TAG = "ForegroundWorker"
        const val channelId = "Job progress"
        const val Progress = "Progress"
        private const val delayDuration = 100L
    }
}