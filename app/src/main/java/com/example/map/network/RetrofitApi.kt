package com.example.map.network

import retrofit2.http.GET
import retrofit2.http.Url
import com.example.map.models.GoogleResponseModel
import com.example.map.models.directionPlaceModel.DirectionResponseModel
import retrofit2.Response

interface RetrofitApi {

    @GET
    suspend fun getNearByPlaces(@Url url: String): Response<GoogleResponseModel>

    @GET
    suspend fun getDirection(@Url url: String): Response<DirectionResponseModel>
}