package com.dexter.baseproject.frag_one

import io.reactivex.Observable
import javax.inject.Inject

class FragOnePresenter @Inject constructor(val initialState: FragOneModel.State):
    BasePresenter<FragOneModel.State, FragOneModel.PartialState, FragOneModel.ViewEvent>(initialState) {
    override fun handle(): Observable<out UiState.Partial<FragOneModel.State>> {
      return Observable.just(FragOneModel.PartialState())
    }

    override fun reduce(
        currentState: FragOneModel.State,
        partialState: FragOneModel.PartialState
    ): FragOneModel.State {
      return FragOneModel.State()
    }
}