package com.dexter.baseproject.frag_one

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import com.jakewharton.rxrelay2.PublishRelay
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject



abstract class BaseFragment<S : UiState, E : BaseViewEvent, I : UserIntent>(
    @LayoutRes contentLayoutId: Int = 0
) : Fragment(contentLayoutId), UserInterfaceWithViewEvents<E> , UserInterface<S>, LifecycleObserver {
    private val viewEventsRelay: PublishRelay<E> = PublishRelay.create()

    protected val intentRelay by lazy { PublishRelay.create<UserIntent>() }
     var schedulerProvider =  AndroidSchedulers.mainThread()
    private var disposable: Disposable? = null
    @Inject
    lateinit var presenter: Lazy<Presenter<S, E>>
    private lateinit var currentState: S
    protected fun getCurrentState(): S {
        return currentState
    }


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()

        // observe state
        addSubscription(
            presenter.get().state()
                .observeOn(schedulerProvider)
                .subscribe({
                    this.currentState = it
                    render(it)
                }, {

                })
        )

        // attach intents
        addSubscription(
            presenter.get().attachIntents(
                Observable.merge(
                    userIntents(),
                            intentRelay
                )
            )
        )
    }
    private val subscriptions: CompositeDisposable by lazy { CompositeDisposable() }

    protected fun addSubscription(disposable: Disposable) {
        if (!disposable.isDisposed) {
            subscriptions.add(disposable)
        }
    }
    override fun onPause() {
        subscriptions.clear()
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            disposable = presenter.get().viewEvent()
                .observeOn(schedulerProvider)
                .subscribe(
                    {
                        val viewEvent =  it as E
                        handleViewEvent(viewEvent)
                    }, {
                    })
        }


    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

}
