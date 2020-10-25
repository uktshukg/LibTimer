package com.dexter.baseproject.fragments.main_frag

import android.content.Context
import android.util.Log
import com.dexter.baseproject.utilities.ParseScanResults
import com.dexter.baseproject.R
import com.dexter.baseproject.utilities.SharedPref
import com.dexter.baseproject.base.BasePresenter
import com.dexter.baseproject.base.Result
import com.dexter.baseproject.base.UiState
import com.dexter.baseproject.fragments.main_frag.use_cases.SendData
import io.reactivex.Observable
import io.reactivex.Observable.mergeArray
import javax.inject.Inject

class MainFragPresenter @Inject constructor(private val initialState: MainFragContract.State, private val parseScanResults: ParseScanResults, private val sendData: SendData, private val context: Context):
    BasePresenter<MainFragContract.State, MainFragContract.PartialState, MainFragContract.ViewEvent>(initialState) {
    override fun handle(): Observable<out UiState.Partial<MainFragContract.State>> {
        return mergeArray(
            intent<MainFragContract.Intent.Load>()
                .map {
                    val locationId = SharedPref.getString("locationId", context)
                    val price = SharedPref.getFloat("price", context)
                    val locationDetails = SharedPref.getString("details", context)
                    if(locationId.isNotBlank()){
                        MainFragContract.PartialState.SetScanData(
                                locationDetails,
                                locationId,
                                price,
                                MainFragContract.Session.ONGOING
                        )
                    }else {
                        MainFragContract.PartialState.NoChange
                    }
                },
            intent<MainFragContract.Intent.ScanData>()
                .doOnNext {
                    Log.e("utkarsh","inside scan "+it.scanResults)
                }
                .switchMap { parseScanResults.execute(it.scanResults)  }
                .map {
                    when (it) {
                        is Result.Progress -> MainFragContract.PartialState.NoChange
                        is Result.Success -> {
                            SharedPref.setLong(
                                string = context.getString(R.string.start_time),
                                passedtime = System.currentTimeMillis(),
                                context = context
                            )
                            SharedPref.setFloat(
                                name = "price",
                                value =  it.value.pricePerMin!!,
                                context = context
                            )
                            SharedPref.setString(
                                name ="locationId",
                                value =   it.value.locationId!!,
                                context = context
                            )
                            SharedPref.setString(
                                name ="details",
                                value =   it.value.locationDetails!!,
                                context = context
                            )

                            MainFragContract.PartialState.SetScanData(
                                    it.value.locationId,
                                    it.value.locationDetails,
                                    it.value.pricePerMin,
                                    MainFragContract.Session.STARTED
                            )
                        }

                        is Result.Failure -> {
                            MainFragContract.PartialState.ErrorState

                        }

                    }
                },
        intent<MainFragContract.Intent.CompleteSession>()
            .switchMap { sendData.execute(it.pair)  }
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
      return when(partialState){
          MainFragContract.PartialState.NoChange -> currentState
          is MainFragContract.PartialState.SetScanData -> currentState.copy(locationDetails = partialState.locationDetails, locationId = partialState.locationId, pricePerMin = partialState.pricePerMin)
          MainFragContract.PartialState.ErrorState -> currentState
          is MainFragContract.PartialState.SetResult -> currentState.copy(amount= partialState.amount, duration= partialState.duration, session= partialState.session)
      }
    }
}