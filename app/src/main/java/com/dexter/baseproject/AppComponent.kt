package com.dexter.baseproject

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule

@Component(modules = [ActivityBindingModule::class,
    AndroidInjectionModule::class,
    NetworkModule::class,
    AppModule::class,
    AndroidSupportInjectionModule::class])
interface AppComponent {

    fun inject(application: App)


    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        @BindsInstance
        fun applicationBind(application: App): Builder



    }
}