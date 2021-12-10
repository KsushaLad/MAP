package com.example.map.interfaces

import com.example.map.models.GooglePlaceModel

interface NearLocationInterface {

    fun onSaveClick(googlePlaceModel: GooglePlaceModel)

    fun onDirectionClick(googlePlaceModel: GooglePlaceModel)
}