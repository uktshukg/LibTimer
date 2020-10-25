package com.dexter.baseproject.api

import com.dexter.baseproject.fragments.main_frag.models.StayDetails
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiClient {
    @POST("submit-session")
    fun submitDurationDetails(@Body body: StayDetails): Completable
}