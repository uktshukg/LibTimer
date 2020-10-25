package com.dexter.baseproject.fragments.main_frag

import android.content.Context
import com.dexter.base.base.BasePresenter
import com.dexter.base.base.Result
import com.dexter.base.base.UiState
import com.dexter.baseproject.R
import com.dexter.baseproject.fragments.main_frag.use_cases.SendData
import com.dexter.baseproject.utilities.ParseScanResults
import com.dexter.baseproject.utilities.SharedPref
import io.reactivex.Observable
import io.reactivex.Observable.mergeArray
import javax.inject.Inject

class MainFragPresenter @Inject constructor(
    private val initialState: MainFragContract.State,
    private val parseScanResults: ParseScanResults,
    private val sendData: SendData,
    private val context: Context
) :
    BasePresenter<MainFragContract.State, MainFragContract.PartialState, MainFragContract.ViewEvent>(
        initialState
    ) {
    override fun handle(): Observable<out UiState.Partial<MainFragContract.State>> {
        return mergeArray(
            intent<MainFragContract.Intent.Load>()
                .map {
                    val locationId = SharedPref.getString("locationId", context)
                    val price = SharedPref.getFloat("price", context)
                    val locationDetails = SharedPref.getString("details", context)
                    if (locationId.isNotBlank()) {
                        emitViewEvent(MainFragContract.ViewEvent.ResumeTimer)
                        MainFragContract.PartialState.SetScanData(
                            locationDetails,
                            locationId,
                            price,
                            MainFragContract.Session.ONGOING
                        )
                    } else {
                        MainFragContract.PartialState.NoChange
                    }
                },
            intent<MainFragContract.Intent.ScanData>()
                .switchMap { parseScanResults.execute(it.scanResults) }
                .map {
                    when (it) {
                        is Result.Progress -> MainFragContract.PartialState.NoChange
                        is Result.Success -> {
                            if (it.value.locationId != null && it.value.locationDetails != null && it.value.pricePerMin != null) {
                                SharedPref.setLong(
                                    string = context.getString(R.string.start_time),
                                    passedtime = System.currentTimeMillis(),
                                    context = context
                                )
                                SharedPref.setFloat(
                                    name = "price",
                                    value = it.value.pricePerMin!!,
                                    context = context
                                )
                                SharedPref.setString(
                                    name = "locationId",
                                    value = it.value.locationId!!,
                                    context = context
                                )
                                SharedPref.setString(
                                    name = "details",
                                    value = it.value.locationDetails!!,
                                    context = context
                                )
                                emitViewEvent(MainFragContract.ViewEvent.ResumeTimer)
                                MainFragContract.PartialState.SetScanData(
                                    it.value.locationId,
                                    it.value.locationDetails,
                                    it.value.pricePerMin,
                                    MainFragContract.Session.ONGOING
                                )
                            } else {
                                emitViewEvent(MainFragContract.ViewEvent.ServerErrorToast)
                                MainFragContract.PartialState.NoChange
                            }
                        }

                        is Result.Failure -> {
                            MainFragContract.PartialState.ErrorState

                        }

                    }
                },
            intent<MainFragContract.Intent.CompleteSession>()
                .switchMap {
                    sendData.execute(
                        SendData.Request(
                            locationID = it.pair.first,
                            price = it.pair.second,
                            startTime = SharedPref.getLong(
                                context,
                                context.getString(R.string.start_time)
                            )
                        )
                    )
                }
                .map {
                    when (it) {
                        is Result.Progress -> MainFragContract.PartialState.NoChange
                        is Result.Success -> {
                            SharedPref.clearAll(context)
                            MainFragContract.PartialState.SetResult(
                                it.value.first,
                                it.value.second,
                                MainFragContract.Session.ENDED
                            )
                        }

                        is Result.Failure -> {
                            MainFragContract.PartialState.ErrorState

                        }

                    }
                })
    }

    override fun reduce(
        currentState: MainFragContract.State,
        partialState: MainFragContract.PartialState
    ): MainFragContract.State {
        return when (partialState) {
            MainFragContract.PartialState.NoChange -> currentState
            is MainFragContract.PartialState.SetScanData -> currentState.copy(
                locationDetails = partialState.locationDetails,
                locationId = partialState.locationId,
                pricePerMin = partialState.pricePerMin,
                canShowError = false,
                session = partialState.session
            )
            MainFragContract.PartialState.ErrorState -> currentState.copy(canShowError = true)
            is MainFragContract.PartialState.SetResult -> currentState.copy(
                amount = partialState.amount,
                duration = partialState.duration,
                session = partialState.session,
                canShowError = false
            )
        }
    }
}