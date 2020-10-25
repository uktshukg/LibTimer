package com.dexter.baseproject

import com.dexter.baseproject.activities.main.MainActivity
import com.dexter.baseproject.activities.main.di.MainActivityModule
import com.dexter.baseproject.scopes.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun mainActivity(): MainActivity
}