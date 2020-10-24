package com.dexter.baseproject.frag_one

import android.util.Log
import com.dexter.baseproject.ParseScanResults
import io.reactivex.Observable
import io.reactivex.Observable.mergeArray
import javax.inject.Inject

class FragOnePresenter @Inject constructor(private val initialState: FragOneModel.State, private val parseScanResults: ParseScanResults):
    BasePresenter<FragOneModel.State, FragOneModel.PartialState, FragOneModel.ViewEvent>(initialState) {
    override fun handle(): Observable<out UiState.Partial<FragOneModel.State>> {
        return mergeArray(
            intent<FragOneModel.Intent.Load>()
                .map {
                   FragOneModel.PartialState.NoChange
                },
            intent<FragOneModel.Intent.ScanData>()
                .doOnNext {
                    Log.e("utkarsh","inside scan "+it.scanResults)
                }
                .switchMap { parseScanResults.execute(it.scanResults)  }
                .map {
                    when (it) {
                        is com.dexter.baseproject.Result.Progress -> FragOneModel.PartialState.NoChange
                        is com.dexter.baseproject.Result.Success ->

                            FragOneModel.PartialState.SetScanData(it.value.locationDetails,it.value.locationId, it.value.pricePerMin )

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
      }
    }
}