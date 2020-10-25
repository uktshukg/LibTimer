package com.dexter.baseproject

import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiClient {
    @POST("submit-session")
    fun submitDurationDetails(@Body body: StayDetails): Completable
}