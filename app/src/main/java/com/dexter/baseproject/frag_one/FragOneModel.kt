package com.dexter.baseproject.frag_one

interface FragOneModel {
    data class State(val loading:Boolean= false): UiState

    sealed class ViewEvent : BaseViewEvent {
        object ServerErrorToast : ViewEvent()
    }
    sealed class Intent : UserIntent {
        object Load : Intent()
    }

     class PartialState : UiState.Partial<State> {
    }
}