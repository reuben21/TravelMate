package com.project22.myapplication.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project22.myapplication.R
import com.project22.myapplication.model.Destination


class PlaceOfDestination(private val destinationList: ArrayList<Destination>) :
    RecyclerView.Adapter<PlaceOfDestination.MyViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceOfDestination.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_home_destination_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaceOfDestination.MyViewHolder, position: Int) {

        val currentItem = destinationList[position]
        holder.placeName.text = currentItem.placeName
        holder.travellers.text = currentItem.travellers.toString()
        holder.placeImageUrl.text = currentItem.placeImageUrl

    }

    override fun getItemCount(): Int {

       return destinationList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val placeName: TextView = itemView.findViewById(R.id.placeName)
        val travellers: TextView = itemView.findViewById(R.id.travellers)
        val placeImageUrl: TextView = itemView.findViewById(R.id.placeImageUrl)


    }

}