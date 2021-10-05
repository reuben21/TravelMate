package com.project22.myapplication.adapters


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.project22.myapplication.R



class DestinationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var placeNameHolder: TextView
    var travellersHolder: TextView
    var startDate: TextView
    var placeImageUrlHolder: ImageView
    var cardOfDestination: MaterialCardView


    init {
        placeNameHolder = view.findViewById(R.id.card_placeName)
        travellersHolder = view.findViewById(R.id.card_travellers)
        startDate = view.findViewById(R.id.startDateTravelDestination)
        placeImageUrlHolder = view.findViewById(R.id.card_placeImageUrl)
        cardOfDestination = view.findViewById(R.id.cardOfDestination)

    }
}