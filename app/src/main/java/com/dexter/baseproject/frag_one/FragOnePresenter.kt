package com.dexter.baseproject.frag_one

import android.content.Context
import android.util.Log
import com.dexter.baseproject.ParseScanResults
import com.dexter.baseproject.R
import com.dexter.baseproject.SharedPref
import io.reactivex.Observable
import io.reactivex.Observable.mergeArray
import javax.inject.Inject

class FragOnePresenter @Inject constructor(private val initialState: FragOneModel.State, private val parseScanResults: ParseScanResults, private val sendData: SendData, private val context: Context):
    BasePresenter<FragOneModel.State, FragOneModel.PartialState, FragOneModel.ViewEvent>(initialState) {
    override fun handle(): Observable<out UiState.Partial<FragOneModel.State>> {
        return mergeArray(
            intent<FragOneModel.Intent.Load>()
                .map {
                    val locationId = SharedPref.getString("locationId", context)
                    val price = SharedPref.getFloat("price", context)
                    val locationDetails = SharedPref.getString("details", context)
                    if(locationId.isNotBlank()){
                        FragOneModel.PartialState.SetScanData(
                            locationDetails,
                            locationId,
                            price,
                            FragOneModel.Session.ONGOING
                        )
                    }else {
                        FragOneModel.PartialState.NoChange
                    }
                },
            intent<FragOneModel.Intent.ScanData>()
                .doOnNext {
                    Log.e("utkarsh","inside scan "+it.scanResults)
                }
                .switchMap { parseScanResults.execute(it.scanResults)  }
                .map {
                    when (it) {
                        is com.dexter.baseproject.Result.Progress -> FragOneModel.PartialState.NoChange
                        is com.dexter.baseproject.Result.Success -> {
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

                            FragOneModel.PartialState.SetScanData(
                                it.value.locationId,
                                it.value.locationDetails,
                                it.value.pricePerMin,
                                FragOneModel.Session.STARTED
                            )
                        }

                        is com.dexter.baseproject.Result.Failure -> {
                            FragOneModel.PartialState.ErrorState

                        }

                    }
                },
        intent<FragOneModel.Intent.CompleteSession>()
            .switchMap { sendData.execute(it.pair)  }
            .map {
                when (it) {
                    is com.dexter.baseproject.Result.Progress -> FragOneModel.PartialState.NoChange
                    is com.dexter.baseproject.Result.Success -> {
                        SharedPref.clearAll(context)
                        FragOneModel.PartialState.SetResult(
                            it.value.first,
                            it.value.second,
                            FragOneModel.Session.ENDED
                        )
                    }

                    is com.dexter.baseproject.Result.Failure -> {
                        FragOneModel.PartialState.ErrorState

                    }

                }
            })
    }

    override fun reduce(
        currentState: FragOneModel.State,
        partialState: FragOneModel.PartialState
    ): FragOneModel.State {
      return when(partialState){
          FragOneModel.PartialState.NoChange -> currentState
          is FragOneModel.PartialState.SetScanData -> currentState.copy(locationDetails = partialState.locationDetails, locationId = partialState.locationId, pricePerMin = partialState.pricePerMin)
          FragOneModel.PartialState.ErrorState -> currentState
          is FragOneModel.PartialState.SetResult -> currentState.copy(amount= partialState.amount, duration= partialState.duration, session= partialState.session)
      }
    }
}