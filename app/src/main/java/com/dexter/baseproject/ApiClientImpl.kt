package com.dexter.baseproject

import io.reactivex.Completable
import javax.inject.Inject

class ApiClientImpl @Inject constructor(private val apiClient: ApiClient){

    fun sendData( body:StayDetails): Completable {
        return apiClient.submitDurationDetails(body)
    }

}