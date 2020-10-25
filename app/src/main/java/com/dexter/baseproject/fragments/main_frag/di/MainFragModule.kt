package com.dexter.baseproject.fragments.main_frag.di

import android.util.Log
import com.dexter.baseproject.api.ApiClient
import com.dexter.baseproject.api.ApiClientImpl
import com.dexter.baseproject.api.IApiClient
import com.dexter.baseproject.base.Presenter
import com.dexter.baseproject.fragments.main_frag.MainFrag
import com.dexter.baseproject.fragments.main_frag.MainFragContract
import com.dexter.baseproject.fragments.main_frag.MainFragPresenter
import com.dexter.baseproject.utilities.createPresenter
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module
abstract class MainFragModule {

    @Binds
    abstract fun server(server: ApiClientImpl): IApiClient

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun initialState(): MainFragContract.State {
           return MainFragContract.State()
        }



        @Provides
        @JvmStatic
        fun presenter(
                fragment: MainFrag,
                presenterProviderMain: Provider<MainFragPresenter>
        ): Presenter<MainFragContract.State, MainFragContract.ViewEvent> = fragment.createPresenter(presenterProviderMain)
    }
}
