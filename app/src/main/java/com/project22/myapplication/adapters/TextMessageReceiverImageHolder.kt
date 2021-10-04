package com.project22.myapplication.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project22.myapplication.R

class TextMessageReceiverImageHolder(view: View) : RecyclerView.ViewHolder(view) {



    var text_view_message: ImageView
    var text_view_time: TextView


    init {


        text_view_message = view.findViewById(R.id.textMessageImageReceiver)
        text_view_time = view.findViewById(R.id.textMessageTimeReceiverImage)

    }
}