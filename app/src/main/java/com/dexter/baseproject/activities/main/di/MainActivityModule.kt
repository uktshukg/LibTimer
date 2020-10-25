package com.dexter.baseproject.activities.main.di

import com.dexter.baseproject.fragments.main_frag.MainFrag
import com.dexter.baseproject.fragments.main_frag.di.MainFragModule
import com.dexter.baseproject.scopes.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

       /****************************************************************
         * Fragments
         ****************************************************************/
        @FragmentScope
        @ContributesAndroidInjector(modules = [MainFragModule::class])
        abstract fun fragOne(): MainFrag


    }


