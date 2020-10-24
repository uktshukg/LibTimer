package com.dexter.baseproject.frag_one

interface FragOneModel {
    data class State(val loading:Boolean= false,
                     val locationId: String?= null, val locationDetails: String?= null, val pricePerMin: Float?= null
    ): UiState

    sealed class ViewEvent : BaseViewEvent {
        object ServerErrorToast : ViewEvent()
    }
    sealed class Intent : UserIntent {
        data class ScanData(val scanResults: String) : Intent()

        object Load : Intent()
    }

     sealed class PartialState : UiState.Partial<State> {
         object NoChange : PartialState()
         data class SetScanData(val locationId: String?= null, val locationDetails: String?= null, val pricePerMin: Float?= null):PartialState()
         object ErrorState: PartialState()
    }

    data class QRInfo(val locationId: String?= null, val locationDetails: String?= null, val pricePerMin: Float?= null)


}