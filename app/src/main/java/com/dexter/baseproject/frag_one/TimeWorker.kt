package com.dexter.baseproject.frag_one

import android.content.Context
import androidx.work.*
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.ThreadUtils
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TimeWorker @Inject constructor(private val context: Context) {

    fun schedule(){
        Completable
            .fromAction {
                val workCategory = "timer"
                val workName = "timer"
                val workRequest = OneTimeWorkRequest.Builder(Worker::class.java)
                    .addTag(workCategory)
                    .addTag(workName)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
                    .build()
                        WorkManager.getInstance(context)
                    .beginUniqueWork(workName, ExistingWorkPolicy.KEEP, workRequest)
                    .enqueue()
            }
            .subscribeOn(Schedulers.newThread()).subscribe()
    }
}