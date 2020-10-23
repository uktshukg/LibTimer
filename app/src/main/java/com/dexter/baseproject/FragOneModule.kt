package com.dexter.baseproject

import android.util.Log
import com.dexter.baseproject.frag_one.*
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module
abstract class FragOneModule {
    @Module
    companion object {
        @Provides
        @JvmStatic
        fun initialState(): FragOneModel.State {
            Log.e("utkarsh","inside")
           return FragOneModel.State()
        }



        @Provides
        @JvmStatic
        fun presenter(
            fragment: FragOne,
            presenterProvider: Provider<FragOnePresenter>
        ): Presenter<FragOneModel.State, FragOneModel.ViewEvent> = fragment.createPresenter(presenterProvider)
    }
}
