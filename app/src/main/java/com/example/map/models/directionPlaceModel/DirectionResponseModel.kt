package com.example.map.models.directionPlaceModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json


data class DirectionResponseModel(
    @field:Json(name = "routes")
    var directionRouteModels: List<DirectionRouteModel>? = null,

    @field:Json(name = "error_message")
    val error: String? = null
)