package com.example.map.constant

import com.example.map.PlaceModel
import com.example.map.R

class AppConstant {
    companion object {
        @JvmStatic
        val STORAGE_REQUEST_CODE = 1000

        @JvmStatic
        val PROFILE_PATH = "/Profile/image_profile.jpg"

        const val LOCATION_REQUEST_CODE = 2000


        @JvmStatic
        val placesName =
            listOf<PlaceModel>(
                PlaceModel(1, R.drawable.ic_restaurant, "Рестораны", "restaurant"),
                PlaceModel(2, R.drawable.ic_atm, "Банкоматы", "atm"),
                PlaceModel(3, R.drawable.ic_gas_station, "АЗС", "gas_station"),
                PlaceModel(4, R.drawable.ic_shopping_cart, "Продукты", "supermarket"),
                PlaceModel(5, R.drawable.ic_hotel, "Отели", "hotel"),
                PlaceModel(6, R.drawable.ic_pharmacy, "Аптетки", "pharmacy"),
                PlaceModel(7, R.drawable.ic_hospital, "Больницы/клиники", "hospital"),
                PlaceModel(8, R.drawable.ic_car_wash, "Автомойки", "car_wash"),
                PlaceModel(9, R.drawable.ic_saloon, "Салоны красоты", "beauty_salon")
            )
    }
}