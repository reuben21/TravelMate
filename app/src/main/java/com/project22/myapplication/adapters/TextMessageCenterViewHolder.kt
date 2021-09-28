package com.project22.myapplication.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project22.myapplication.R

class TextMessageCenterViewHolder(view: View) : RecyclerView.ViewHolder(view) {



    var text_view_message: TextView


    init {


        text_view_message = view.findViewById(R.id.textMessageCenterView)


    }
}