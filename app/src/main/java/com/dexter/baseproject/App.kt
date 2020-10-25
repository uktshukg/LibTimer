package com.dexter.baseproject

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.multidex.MultiDex
import androidx.work.*
import com.dexter.baseproject.frag_one.ForegroundWorker
import com.dexter.baseproject.frag_one.NotificationService
import com.dexter.baseproject.frag_one.TimeWorker
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class App : Application(), HasAndroidInjector, Application.ActivityLifecycleCallbacks,
    Configuration.Provider {

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.VERBOSE)
            .build()
    companion object{
        var instance: App = App()
    }
    var isWorkManagerStarted: Boolean = false

    @Inject
    lateinit var worker: TimeWorker

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>


    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder().applicationBind(this).build().inject(this)
        registerActivityLifecycleCallbacks(this)

    }
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
    override fun onActivityPaused(p0: Activity) {

    }

    var activityCount=0;
    override fun onActivityStarted(p0: Activity) {
        activityCount++
        if(isWorkManagerStarted) {
            isWorkManagerStarted = false
            Log.e("utkarsh","inside manager stop ")
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("abc"));
                WorkManager.getInstance(context).cancelAllWorkByTag("timer")

        }
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStopped(p0: Activity) {

        activityCount--
        Log.e("utkarsh","inside activityCount "+activityCount +" time "+SharedPref.getLong(this,context.getString(R.string.start_time))+" work manager "+isWorkManagerStarted)
        if(activityCount==0 && SharedPref.getLong(this,context.getString(R.string.start_time))!=0L
            && isWorkManagerStarted.not()){
            isWorkManagerStarted = true
            startService(Intent(this,NotificationService::class.java))
        }

    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityResumed(p0: Activity) {
    }

    fun cancelWorker() {
//        WorkManager.getInstance(this).cancelAllWork()
        LocalBroadcastManager.getInstance(instance).sendBroadcast(Intent("abc"));
    }


}