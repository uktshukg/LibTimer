package com.dexter.baseproject.fragments.main_frag.use_cases

import android.content.Context
import com.dexter.baseproject.*
import com.dexter.baseproject.api.ApiClientImpl
import com.dexter.baseproject.base.Result
import com.dexter.baseproject.base.UseCase
import com.dexter.baseproject.fragments.main_frag.models.StayDetails
import com.dexter.baseproject.utilities.SharedPref
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject


class SendData @Inject constructor(private val apiClientImpl: ApiClientImpl, private val context: Context) :
        UseCase<Pair<String, Float>, Pair<Float, Long>> {



    override fun execute(req: Pair<String, Float>): Observable<Result<Pair<Float, Long>>> {
         val startTime= SharedPref.getLong(context, context.getString(R.string.start_time))
        val endTime = System.currentTimeMillis()
        val duration = (endTime-startTime)/60000
        val amount  =  req.second*duration
        return UseCase.wrapSingle(apiClientImpl.sendData(StayDetails(req.first, duration.toInt(), endTime)).andThen(Single.just(amount to duration)))
    }




}