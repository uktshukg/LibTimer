package com.dexter.base.base

import io.reactivex.Observable

interface UserInterface<in S : UiState> {
    fun userIntents(): Observable<UserIntent>

    fun render(state: S)
}

interface UserInterfaceWithViewEvents<E: BaseViewEvent> {
    fun handleViewEvent(event: E)
}

