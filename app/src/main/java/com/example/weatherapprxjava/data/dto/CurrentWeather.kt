package com.example.weatherapprxjava.data.dto

import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("temperature")
    val temperature: Float,
    @SerializedName("feelsLikeTemp")
    val feelsLikeTemp: Float
)