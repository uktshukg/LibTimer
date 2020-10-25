package com.dexter.baseproject

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.multidex.MultiDex
import com.dexter.baseproject.services.NotificationService
import com.dexter.baseproject.utilities.SharedPref
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App : Application(), HasAndroidInjector, Application.ActivityLifecycleCallbacks{


    companion object{
        var instance: App = App()
    }
    var isWorkManagerStarted: Boolean = false

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
        }
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStopped(p0: Activity) {

        activityCount--
        Log.e("utkarsh","inside activityCount "+activityCount +" time "+ SharedPref.getLong(this,context.getString(R.string.start_time))+" work manager "+isWorkManagerStarted)
        if(activityCount==0 && SharedPref.getLong(this,context.getString(R.string.start_time))!=0L
            && isWorkManagerStarted.not()){
            isWorkManagerStarted = true
            startService(Intent(this, NotificationService::class.java))
        }

    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityResumed(p0: Activity) {
    }

}