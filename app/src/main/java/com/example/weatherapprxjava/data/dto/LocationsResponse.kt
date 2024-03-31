package com.example.weatherapprxjava.data.dto

import com.google.gson.annotations.SerializedName

class LocationsResponse(
    @SerializedName("locations")
    val locations: List<ForecastLocation>
)