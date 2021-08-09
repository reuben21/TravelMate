package com.project22.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project22.myapplication.R
import com.project22.myapplication.model.Destination
import kotlinx.android.synthetic.main.fragment_home_destination_card.*
import kotlinx.android.synthetic.main.fragment_home_destination_card.view.*

//class DestinationAdapter(val context:Context, val listOfDestinaton: ArrayList<Destination>) :
//RecyclerView.Adapter<DestinationAdapter.ViewHolder>()
//{
//    class ViewHolder(view:View): RecyclerView.ViewHolder(view) {
//        var placeName: TextView = view.card_placeName
//        var placeImageUrl: ImageView = view.card_placeImageUrl
//        var travellers: TextView = view.card_travellers
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(
//            LayoutInflater.from(context).inflate(
//            R.layout.fragment_home_destination_card,
//                parent,
//                false)
//        )
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//       val item = listOfDestinaton.get(position)
//
//        holder.placeName.text =  item.placeName
//        holder.travellers.text =  item.travellers.toString()
//        Glide.with(context)
//            .load(item.placeImageUrl)
//            .placeholder(R.drawable.travel)
//            .into(holder.placeImageUrl)
//
//
//    }
//
//    override fun getItemCount(): Int {
//        return listOfDestinaton.size
//    }
//}

class DestinationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var placeNameHolder: TextView
    var travellersHolder: TextView
    var placeImageUrlHolder: ImageView


    init {
        placeNameHolder = view.findViewById(R.id.card_placeName)
        travellersHolder = view.findViewById(R.id.card_travellers)
        placeImageUrlHolder = view.findViewById(R.id.card_placeImageUrl)

    }
}