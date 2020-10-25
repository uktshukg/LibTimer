package com.dexter.baseproject.fragments.main_frag.models

import com.google.gson.annotations.SerializedName

data class StayDetails(@SerializedName("location_id" ) val locationId: String,
                       @SerializedName("time_spent" ) val timeSpent: Int,
                       @SerializedName("end_time" ) val endTime: Long
)

