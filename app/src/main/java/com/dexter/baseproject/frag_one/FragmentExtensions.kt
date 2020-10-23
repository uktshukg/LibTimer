package com.dexter.baseproject.frag_one

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import javax.inject.Provider

inline fun <reified T : ViewModel> ViewModelStoreOwner.createPresenter(
    provider: Provider<T>
) = ViewModelProvider(
    this,
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = provider.get() as T
    }
)[T::class.java]