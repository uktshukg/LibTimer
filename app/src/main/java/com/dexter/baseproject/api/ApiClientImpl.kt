package com.dexter.baseproject.api

import com.dexter.baseproject.fragments.main_frag.models.StayDetails
import io.reactivex.Completable
import javax.inject.Inject

class ApiClientImpl @Inject constructor(private val apiClient: ApiClient): IApiClient{

    override fun sendData(body: StayDetails): Completable {
        return apiClient.submitDurationDetails(body)
    }

}