package com.project22.myapplication.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.project22.myapplication.R

class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var singleChatNameHolder: TextView
    var singleChatImageHolder: ImageView
    var cardOfChatView: MaterialCardView

    init {
        singleChatNameHolder = view.findViewById(R.id.singleChatNameHolder)
        singleChatImageHolder = view.findViewById(R.id.singleChatImageHolder)
        cardOfChatView = view.findViewById(R.id.cardOfChatView)

    }
}