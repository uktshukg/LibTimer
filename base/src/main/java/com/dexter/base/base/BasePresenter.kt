package com.dexter.base.base

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class BasePresenter<S : UiState, P : UiState.Partial<S>, E: BaseViewEvent> protected constructor(
    // initial (or default) state of the state interface
    private val initialState: S,

    // scheduler on which state is reduced using partial states and pushed to stateRelay
    private val stateThread: Scheduler = Schedulers.newThread(),

    // scheduler on which intents coming from the state interface is consumed and pushed to intentRelay
    private val intentThread: Scheduler = Schedulers.newThread()
) : Presenter<S, E>, ViewModel() {

    private val viewEventsRelay: PublishRelay<E> = PublishRelay.create()

     override fun viewEvent(): Observable<E> = viewEventsRelay

    protected fun emitViewEvent(event: E) = viewEventsRelay.accept(event)

    // intent relay is used so that the `presenter processing stream` doesn't break even when the state interface is detached
    private val intentRelay by lazy { PublishRelay.create<UserIntent>() }

    // state relay is used to preserve last known state, which is used by the state interface when it (re)attaches
    private val stateRelay: BehaviorRelay<S> by lazy { BehaviorRelay.createDefault(initialState) }

    // isStateSetup is used to enforce "only once" subscription of state
    private var isStateSetup: Boolean = false

    // subscriptions is used to save state subscription, and any other subscriptions added from the presenter implementation
    private val subscriptions: CompositeDisposable by lazy { CompositeDisposable() }



    final override fun state(): Observable<S> = stateRelay

    fun intents(): Observable<UserIntent> = intentRelay

    final override fun attachIntents(intents: Observable<UserIntent>): Disposable {
        setupState()
        return intents
            .observeOn(intentThread)
            .subscribe(intentRelay::accept)
    }

    override fun onCleared() {
        subscriptions.clear()
    }

    // handle takes care of all processing related to the given state interface (usually initiated because of an intent)
    // to produce partial states, which ultimately, is used to update state
    protected abstract fun handle(): Observable<out UiState.Partial<S>>

    // reduce creates updated state interface state using the previous state and a partial state
    protected abstract fun reduce(currentState: S, partialState: P): S

    protected fun addSubscription(disposable: Disposable) {
        if (!disposable.isDisposed) {
            subscriptions.add(disposable)
        }
    }

    protected inline fun <reified I : UserIntent> intent(): Observable<I> {
        val intentClass = I::class.java
        return intents()
            .filter { intentClass.isAssignableFrom(it.javaClass) }
            .cast(intentClass)
    }




    private fun setupState() {
        synchronized(this) {
            if (isStateSetup) return

            // state handling
            addSubscription(
                this.handle()
                    .observeOn(stateThread)
                    .scan(initialState) { currentState, partialState ->
                        try {
                            val newState = reduce(currentState, partialState as P)
                            newState
                        } catch (e: Exception) {
                            Log.d("base fragment ", " presenter scan error " + e.printStackTrace())
                            currentState
                        }
                    }
                    .distinctUntilChanged()
                    .subscribe({
                        synchronized(this) {
                            stateRelay.accept(it)
                        }
                    }, {
                        Log.d("base fragment ", " presenter handle error " + it.printStackTrace())
                    })
            )
            isStateSetup = true
        }
    }

}
