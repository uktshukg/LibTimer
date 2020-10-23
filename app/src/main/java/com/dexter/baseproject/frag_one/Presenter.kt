package com.dexter.baseproject.frag_one

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface Presenter<S : UiState,  E : BaseViewEvent> {
    fun state(): Observable<S>

    fun attachIntents(intents: Observable<UserIntent>): Disposable

    fun viewEvent(): Observable<E>
}
