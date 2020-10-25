package com.dexter.baseproject.fragments.main_frag.use_cases

import android.content.Context
import com.dexter.base.base.Result
import com.dexter.base.base.UseCase
import com.dexter.baseproject.api.IApiClient
import com.dexter.baseproject.fragments.main_frag.models.StayDetails
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject


class SendData @Inject constructor(
        private val apiClientImpl: IApiClient,
        private val context: Context
) :
        UseCase<SendData.Request, Pair<Float, Long>> {

    override fun execute(req: Request): Observable<Result<Pair<Float, Long>>> {
        val startTime = req.startTime
        val endTime = System.currentTimeMillis()
        val duration = (endTime - startTime) / 60000
        val amount = req.price * duration
        return UseCase.wrapSingle(
                apiClientImpl.sendData(
                        StayDetails(
                                req.locationID,
                                duration.toInt(),
                                endTime
                        )
                ).andThen(Single.just(amount to duration))
        )
    }

    data class Request(val locationID: String, val price: Float, val startTime: Long)

}