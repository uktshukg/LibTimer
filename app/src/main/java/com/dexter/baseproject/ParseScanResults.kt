package com.dexter.baseproject

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import javax.inject.Inject


class ParseScanResults @Inject constructor() :
    UseCase<String, ParseScanResults.Response> {


    data class Response(@SerializedName("location_id")val locationId: String?= null, @SerializedName("location_details")val locationDetails: String?= null, @SerializedName("price_per_min")val pricePerMin: Float?= null )

    override fun execute(req: String): Observable<com.dexter.baseproject.Result<Response>> {
        Log.e("utkarsh","called inside "+req)
        val gson = Gson()
        val response = gson.fromJson(removeQuotesAndUnescape(req), Response::class.java)
        Log.e("utkarsh","repsonse "+response.toString())
        return UseCase.wrapObservable(Observable.just(response))
    }
    private fun removeQuotesAndUnescape(uncleanJson: String): String? {
        val noQuotes = uncleanJson.replace("^\"|\"$".toRegex(), "")
        return org.apache.commons.text.StringEscapeUtils.unescapeJava(noQuotes)
    }
}
