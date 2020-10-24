package com.dexter.baseproject.frag_one

import io.reactivex.Observable
import io.reactivex.Observable.mergeArray
import javax.inject.Inject

class FragOnePresenter @Inject constructor(private val initialState: FragOneModel.State):
    BasePresenter<FragOneModel.State, FragOneModel.PartialState, FragOneModel.ViewEvent>(initialState) {
    override fun handle(): Observable<out UiState.Partial<FragOneModel.State>> {
        return mergeArray(
            intent<FragOneModel.Intent.Load>()
                .map {
                   FragOneModel.PartialState.NoChange
                },
            intent<FragOneModel.Intent.ScanData>()
                .map {
                    FragOneModel.PartialState.NoChange
                })
    }

    override fun reduce(
        currentState: FragOneModel.State,
        partialState: FragOneModel.PartialState
    ): FragOneModel.State {
      return FragOneModel.State()
    }
}