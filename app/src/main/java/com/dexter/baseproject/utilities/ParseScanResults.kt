package com.dexter.baseproject.utilities

import com.dexter.base.base.Result
import com.dexter.base.base.UseCase
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import javax.inject.Inject


class ParseScanResults @Inject constructor() :
    UseCase<String, ParseScanResults.Response> {


    data class Response(
        @SerializedName("location_id") val locationId: String? = null,
        @SerializedName("location_details") val locationDetails: String? = null,
        @SerializedName("price_per_min") val pricePerMin: Float? = null
    )

    override fun execute(req: String): Observable<Result<Response>> {
        val gson = Gson()
        val response = gson.fromJson(removeQuotesAndUnescape(req), Response::class.java)
        return UseCase.wrapObservable(Observable.just(response))
    }
}
