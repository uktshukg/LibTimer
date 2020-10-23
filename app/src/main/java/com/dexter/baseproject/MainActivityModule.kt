package com.dexter.baseproject

import androidx.appcompat.app.AppCompatActivity
import com.dexter.baseproject.frag_one.FragOne
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

       /****************************************************************
         * Fragments
         ****************************************************************/
        @FragmentScope
        @ContributesAndroidInjector(modules = [FragOneModule::class])
        abstract fun fragOne(): FragOne


    }


