package com.example.map.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.map.R
import com.example.map.databinding.PlaceItemLayoutBinding
import com.example.map.interfaces.NearLocationInterface
import com.example.map.models.GooglePlaceModel

class GooglePlaceAdapter(private val nearLocationInterface: NearLocationInterface) :
    RecyclerView.Adapter<GooglePlaceAdapter.ViewHolder>() {

    private var googlePlaceModels: List<GooglePlaceModel>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: PlaceItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.place_item_layout, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (googlePlaceModels != null) {
            val placeModel = googlePlaceModels!![position]
            holder.binding.googlePlaceModel = placeModel
            holder.binding.listener = nearLocationInterface
        }
    }

    override fun getItemCount(): Int {
        return if (googlePlaceModels != null) googlePlaceModels!!.size else 0
    }

    fun setGooglePlaces(googlePlaceModel: List<GooglePlaceModel>) {
        googlePlaceModels = googlePlaceModel
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: PlaceItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}